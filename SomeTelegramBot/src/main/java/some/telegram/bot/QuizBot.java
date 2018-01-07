package some.telegram.bot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import some.telegram.bot.core.UserGame;
import some.telegram.bot.manager.ManagerUsersGame;

public class QuizBot extends TelegramLongPollingBot {

	private ManagerUsersGame managerUsersGame;
	private ManagerUsersGame unknownUserGame;
	private MasterUsersGame masterUsersGame;
	private String botUsername;
	private String botToken;
	private String accessPassword;

	public QuizBot(String botUsername, String botToken) {
		managerUsersGame = new ManagerUsersGame();
		unknownUserGame = new ManagerUsersGame();
		masterUsersGame = new MasterUsersGame(true);
		this.botUsername = botUsername;
		this.botToken = botToken;
		this.accessPassword = String.valueOf((int) (99991 * Math.random()));
		System.out.println("Access Password: " + accessPassword);
	}

	@Override
	public void onUpdateReceived(Update update) {
		try {
			User user = update.getMessage().getFrom();
			String receivedMessage = update.getMessage().getText();
			if (masterUsersGame.containUserGame(user)) {
				UserGame master = masterUsersGame.getUserGame(user);
				if (!master.isFlagPoints()) {
					if (!master.isFlagAnswer()) {
						if (!master.isFlagQuestion()) {
							switch (receivedMessage) {
							case ("nuovadomanda"):
								master.setFlagQuestion(true);
								KeyboardRow row1 = new KeyboardRow();
								for (int i = 1; i <= 10; i++) {
									row1.add(Integer.toString(i));
								}
								KeyboardRow row2 = new KeyboardRow();
								for (int i = 11; i <= 20; i++) {
									row2.add(Integer.toString(i));
								}
								SendTextMessageWithKeyboard(master.getChat().getId(), "Quale domanda vuoi impostare?",
										extractKeyboardMarkup(row1, row2));
								break;
							case ("listadomande"):
								break;
							case ("listagiocatori"):
								managerUsersGame.orderGamersList();
								SendTextMessage(master.getChat().getId(),
										"Il numero dei partecipanti è: " + managerUsersGame.getListOfUsers().size()
												+ " \n\rLa lista dei giocatori è in ordine decrescente:"
												+ managerUsersGame.getUsersPointsList());
								break;
							case ("nuovomaster"):
								masterUsersGame.setAcceptNewMaster(true);
								SendTextMessage(master.getChat().getId(), "Può essere accettato un nuovo master");
								break;
							default:
								break;
							}
						} else {
							if ((Integer.parseInt(receivedMessage) <= 20) && (Integer.parseInt(receivedMessage) >= 1)) {
								master.setFlagQuestion(false);
								master.setSettingQuestion(Integer.parseInt(receivedMessage));
								KeyboardRow row1 = new KeyboardRow();
								row1.add("A");
								row1.add("B");
								KeyboardRow row2 = new KeyboardRow();
								row2.add("C");
								row2.add("D");
								SendTextMessageWithKeyboard(master.getChat().getId(), "Quale e' la risposta giusta?",
										extractKeyboardMarkup(row1, row2));
								master.setFlagAnswer(true);
							}
						}
					} else {
						if ((receivedMessage.equals("A")) || (receivedMessage.equals("B"))
								|| (receivedMessage.equals("C")) || (receivedMessage.equals("D"))) {
							master.setFlagAnswer(false);
							master.setSettingRightQuestion(receivedMessage);
							KeyboardRow row = new KeyboardRow();
							row.add("1");
							row.add("2");
							row.add("3");
							SendTextMessageWithKeyboard(master.getChat().getId(),
									"Quale è il punteggio della risposta?", extractKeyboardMarkup(row));
							master.setFlagPoints(true);
						}
					}
				} else {
					int points = Integer.parseInt(receivedMessage);
					if (points >= 1 || points <= 3) {
						masterUsersGame.addNewQuestion(master.getSettingQuestion(), master.getSettingRightQuestion(),
								points);
						KeyboardRow row1 = new KeyboardRow();
						row1.add("nuovadomanda");
						row1.add("listadomande");
						KeyboardRow row2 = new KeyboardRow();
						row2.add("listagiocatori");
						row2.add("nuovomaster");
						SendTextMessageWithKeyboard(master.getChat().getId(),
								"La domanda creata è: \n\r" + masterUsersGame.getListOfQuestion()
										.get(master.getSettingQuestion()).toString(),
								extractKeyboardMarkup(row1, row2));
						master.setSettingQuestion(0);
						master.setSettingRightQuestion("");
						master.setFlagPoints(false);
					}
				}
			} else if (managerUsersGame.containUserGame(user)) {

			} else if (!unknownUserGame.containUserGame(user)) {
				UserGame newGamer = new UserGame(user, update.getMessage().getChat());
				KeyboardRow row;
				switch (receivedMessage) {
				case "/start":
					unknownUserGame.addUserGame(newGamer);
					row = new KeyboardRow();
					row.add("Si!");
					row.add("No!");
					SendTextMessageWithKeyboard(newGamer.getChat().getId(), "Vuoi giocare?",
							extractKeyboardMarkup(row));
					break;
				default:
					unknownUserGame.removeUserGame(newGamer);
					row = new KeyboardRow();
					row.add("/start");
					SendTextMessageWithKeyboard(newGamer.getChat().getId(),
							"Non so chi sei... Per iniziare a giocare clicca il pulsante start!",
							extractKeyboardMarkup(row));
					break;
				}
			} else if (unknownUserGame.containUserGame(user)) {
				UserGame userGame = unknownUserGame.getUserGame(user);
				if (receivedMessage.equals("/master " + accessPassword)) {
					if (masterUsersGame.isAcceptNewMaster()) {
						masterUsersGame.addUserGame(userGame);
						unknownUserGame.removeUserGame(userGame);
						masterUsersGame.setAcceptNewMaster(false);
						KeyboardRow row1 = new KeyboardRow();
						row1.add("nuovadomanda");
						row1.add("listadomande");
						KeyboardRow row2 = new KeyboardRow();
						row2.add("listagiocatori");
						row2.add("nuovomaster");
						SendTextMessageWithKeyboard(userGame.getChat().getId(), "Sei il nuovo master, cosa vuoi fare?",
								extractKeyboardMarkup(row1, row2));
					} else {
						unknownUserGame.removeUserGame(userGame);
						KeyboardRow row = new KeyboardRow();
						row.add("/start");
						SendTextMessageWithKeyboard(userGame.getChat().getId(),
								"La possibilità di diventare master è stata disabilitata!", extractKeyboardMarkup(row));
					}
				} else {
					switch (receivedMessage) {
					case "Si!":
						managerUsersGame.addUserGame(userGame);
						unknownUserGame.removeUserGame(userGame);
						KeyboardRow row1 = new KeyboardRow();
						row1.add("A");
						row1.add("B");
						KeyboardRow row2 = new KeyboardRow();
						row2.add("C");
						row2.add("D");
						SendTextMessageWithKeyboard(userGame.getChat().getId(),
								"Sei stato aggiunto alla lista dei partecipanti! Ora dovrai solo aspettare l'inizio del gioco!",
								extractKeyboardMarkup(row1, row2));
						break;
					case "No!":
						unknownUserGame.removeUserGame(userGame);
						KeyboardRow row = new KeyboardRow();
						row.add("/start");
						SendTextMessageWithKeyboard(userGame.getChat().getId(),
								"Se cambi idea clica sul pulsante start!", extractKeyboardMarkup(row));
						break;
					default:
						SendTextMessage(userGame.getChat().getId(), "Devi rispondere o Si! o No!");
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ReplyKeyboardMarkup extractKeyboardMarkup(KeyboardRow row1, KeyboardRow row2) {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(row1);
		keyboard.add(row2);
		keyboardMarkup.setKeyboard(keyboard);
		return keyboardMarkup;
	}

	private ReplyKeyboardMarkup extractKeyboardMarkup(KeyboardRow row1) {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(row1);
		keyboardMarkup.setKeyboard(keyboard);
		return keyboardMarkup;
	}

	private void SendTextMessageWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboardMarkup) {
		try {
			execute(new SendMessage().setChatId(chatId).setText(text).setReplyMarkup(keyboardMarkup));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void SendTextMessage(Long chatId, String text) {
		try {
			execute(new SendMessage().setChatId(chatId).setText(text));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getBotUsername() {
		return botUsername;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

}
