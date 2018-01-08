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

	private static final String INFO = "Info";
	private static final String INVIA_MESSAGGIO = "InviaMessaggio";
	private static final String START_STOP = "StartStop";
	private static final String SELEZIONA_DOMANDA = "SelezionaDomanda";
	private static final String ALTRO = "Altro";
	private static final String BAN_USER = "BanUser";
	private static final String LISTA_MASTER = "ListaMaster";
	private static final String NUOVO_MASTER = "NuovoMaster";
	private static final String LISTA_GIOCATORI = "ListaGiocatori";
	private static final String LISTA_DOMANDE = "ListaDomande";
	private static final String NUOVA_DOMANDA = "NuovaDomanda";

	private ManagerUsersGame managerUsersGame;
	private ManagerUsersGame unknownUserGame;
	private MasterUsersGame masterUsersGame;

	private String botUsername;
	private String botToken;
	private String accessPassword;
	private boolean otherMasterMenu;

	public QuizBot(String botUsername, String botToken) {
		managerUsersGame = new ManagerUsersGame();
		unknownUserGame = new ManagerUsersGame();
		masterUsersGame = new MasterUsersGame(true);
		otherMasterMenu = true;
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
				masterUserMenu(user, receivedMessage);
			} else if (managerUsersGame.containUserGame(user)) {

			} else if (!unknownUserGame.containUserGame(user)) {
				newUserMenu(update, user, receivedMessage);
			} else if (unknownUserGame.containUserGame(user)) {
				unknownUserMenu(user, receivedMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void masterUserMenu(User user, String receivedMessage) {
		UserGame master = masterUsersGame.getUserGame(user);
		if (!master.isFlagPoints()) {
			if (!master.isFlagAnswer()) {
				if (!master.isFlagQuestion()) {
					switch (receivedMessage) {
					case NUOVA_DOMANDA:
						master.setFlagQuestion(true);
						SendTextMessageWithKeyboard(master.getChat().getId(), "Quale domanda vuoi impostare?",
								extractQuestionKeyboard());
						break;
					case LISTA_DOMANDE:
						SendTextMessage(master.getChat().getId(),
								"Ecco la lista delle domande:\n\r" + masterUsersGame.getStringListOfQuestion());
						break;
					case LISTA_GIOCATORI:
						managerUsersGame.orderGamersList();
						SendTextMessage(master.getChat().getId(),
								"Il numero dei partecipanti è: " + managerUsersGame.getListOfUsers().size()
										+ " \n\rLa lista dei giocatori è in ordine decrescente:"
										+ managerUsersGame.getUsersPointsList());
						break;
					case NUOVO_MASTER:
						masterUsersGame.setAcceptNewMaster(true);
						SendTextMessage(master.getChat().getId(), "Può essere accettato un nuovo master");
						break;
					case LISTA_MASTER:
						break;
					case BAN_USER:
						break;
					case SELEZIONA_DOMANDA:
						break;
					case START_STOP:
						break;
					case INVIA_MESSAGGIO:
						break;
					case INFO:
						break;
					case ALTRO:
						this.otherMasterMenu = !otherMasterMenu;
						SendTextMessageWithKeyboard(master.getChat().getId(), "Questi sono gli altri comandi!",
								extractMasterKeyboard());
						break;
					default:
						break;
					}
				} else {
					if ((Integer.parseInt(receivedMessage) <= 20) && (Integer.parseInt(receivedMessage) >= 1)) {
						master.setFlagQuestion(false);
						master.setSettingQuestion(Integer.parseInt(receivedMessage));
						SendTextMessageWithKeyboard(master.getChat().getId(), "Quale e' la risposta giusta?",
								extractAnswerKeyboard());
						master.setFlagAnswer(true);
					}
				}
			} else {
				if ((receivedMessage.equals("A")) || (receivedMessage.equals("B")) || (receivedMessage.equals("C"))
						|| (receivedMessage.equals("D"))) {
					master.setFlagAnswer(false);
					master.setSettingRightQuestion(receivedMessage);
					SendTextMessageWithKeyboard(master.getChat().getId(), "Quale è il punteggio della risposta?",
							extractPointAnswerKeyboard());
					master.setFlagPoints(true);
				}
			}
		} else {
			int points = Integer.parseInt(receivedMessage);
			if (points >= 1 || points <= 3) {
				masterUsersGame.addNewQuestion(master.getSettingQuestion(), master.getSettingRightQuestion(), points);
				SendTextMessageWithKeyboard(master.getChat().getId(),
						"La domanda creata è: \n\r"
								+ masterUsersGame.getListOfQuestion().get(master.getSettingQuestion() - 1).toString(),
						extractMasterKeyboard());
				master.setSettingQuestion(0);
				master.setSettingRightQuestion("");
				master.setFlagPoints(false);
			}
		}
	}

	private void newUserMenu(Update update, User user, String receivedMessage) {
		UserGame newGamer = new UserGame(user, update.getMessage().getChat());
		KeyboardRow row;
		switch (receivedMessage) {
		case "/start":
			unknownUserGame.addUserGame(newGamer);
			row = new KeyboardRow();
			row.add("Si!");
			row.add("No!");
			SendTextMessageWithKeyboard(newGamer.getChat().getId(), "Vuoi giocare?", extractKeyboardMarkup(row));
			break;
		default:
			unknownUserGame.removeUserGame(newGamer);
			row = new KeyboardRow();
			row.add("/start");
			SendTextMessageWithKeyboard(newGamer.getChat().getId(),
					"Non so chi sei... Per iniziare a giocare clicca il pulsante start!", extractKeyboardMarkup(row));
			break;
		}
	}

	private void unknownUserMenu(User user, String receivedMessage) {
		UserGame userGame = unknownUserGame.getUserGame(user);
		if (receivedMessage.equals("/master " + accessPassword)) {
			if (masterUsersGame.isAcceptNewMaster()) {
				masterUsersGame.addUserGame(userGame);
				unknownUserGame.removeUserGame(userGame);
				masterUsersGame.setAcceptNewMaster(false);
				SendTextMessageWithKeyboard(userGame.getChat().getId(), "Sei il nuovo master, cosa vuoi fare?",
						extractMasterKeyboard());
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
				SendTextMessageWithKeyboard(userGame.getChat().getId(),
						"Sei stato aggiunto alla lista dei partecipanti! Ora dovrai solo aspettare l'inizio del gioco!",
						extractAnswerKeyboard());
				break;
			case "No!":
				unknownUserGame.removeUserGame(userGame);
				KeyboardRow row = new KeyboardRow();
				row.add("/start");
				SendTextMessageWithKeyboard(userGame.getChat().getId(), "Se cambi idea clica sul pulsante start!",
						extractKeyboardMarkup(row));
				break;
			default:
				SendTextMessage(userGame.getChat().getId(), "Devi rispondere o Si! o No!");
				break;
			}
		}
	}

	private ReplyKeyboardMarkup extractPointAnswerKeyboard() {
		KeyboardRow row = new KeyboardRow();
		row.add("1");
		row.add("2");
		row.add("3");
		return extractKeyboardMarkup(row);
	}

	private ReplyKeyboardMarkup extractQuestionKeyboard() {
		KeyboardRow row1 = new KeyboardRow();
		for (int i = 1; i <= 10; i++) {
			row1.add(Integer.toString(i));
		}
		KeyboardRow row2 = new KeyboardRow();
		for (int i = 11; i <= 20; i++) {
			row2.add(Integer.toString(i));
		}
		return extractKeyboardMarkup(row1, row2);
	}

	private ReplyKeyboardMarkup extractAnswerKeyboard() {
		KeyboardRow row1 = new KeyboardRow();
		row1.add("A");
		row1.add("B");
		KeyboardRow row2 = new KeyboardRow();
		row2.add("C");
		row2.add("D");
		return extractKeyboardMarkup(row1, row2);
	}

	private ReplyKeyboardMarkup extractMasterKeyboard() {
		KeyboardRow row1 = new KeyboardRow();
		KeyboardRow row2 = new KeyboardRow();
		KeyboardRow row3 = new KeyboardRow();
		if (otherMasterMenu) {
			row1.add(NUOVA_DOMANDA);
			row1.add(LISTA_DOMANDE);
			row2.add(NUOVO_MASTER);
			row2.add(LISTA_MASTER);
			row3.add(BAN_USER);
			row3.add(ALTRO);
		} else {
			row1.add(SELEZIONA_DOMANDA);
			row1.add(START_STOP);
			row2.add(LISTA_GIOCATORI);
			row2.add(INVIA_MESSAGGIO);
			row3.add(INFO);
			row3.add(ALTRO);
		}
		return extractKeyboardMarkup(row1, row2, row3);
	}

	private ReplyKeyboardMarkup extractKeyboardMarkup(KeyboardRow row1, KeyboardRow row2, KeyboardRow row3) {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(row1);
		keyboard.add(row2);
		keyboard.add(row3);
		keyboardMarkup.setKeyboard(keyboard);
		return keyboardMarkup;
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
