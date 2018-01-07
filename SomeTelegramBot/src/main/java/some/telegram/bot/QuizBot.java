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
				switch (receivedMessage) {
				case ("nuovadomanda"):
					break;
				case ("listadomande"):
					break;
				case ("listagiocatori"):
					managerUsersGame.orderGamersList();
					SendTextMessage(master.getChat().getId(), managerUsersGame.getUsersPointList());
					break;
				case (""):
					break;
				default:
					break;
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
						row2.add("listadeigiocatori");
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
