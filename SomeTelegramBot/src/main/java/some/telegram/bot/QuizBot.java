package some.telegram.bot;

import java.util.ArrayList;
import java.util.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import some.telegram.bot.core.Question;
import some.telegram.bot.core.UserGame;
import some.telegram.bot.manager.ManagerUsersGame;
import some.telegram.bot.manager.MasterUsersGame;

public class QuizBot extends TelegramLongPollingBot {

	private static final String RESET_GIOCO = "ResetGioco";
	private static final String RESET_PUNTEGGI = "ResetPunteggi";
	private static final String RESET_DOMANDE = "ResetDomande";
	private static final String START_STOP_PARTECIPANTI = "Start/StopPartecipanti";
	private static final String PARTECIPANTI_CHAT_ID = "PartecipantiChatId";
	private static final String PUNTEGGIO_GIOCATORI = "PunteggioGiocatori";
	private static final String INVIA_MESSAGGIO = "InviaMessaggio";
	private static final String START = "Start";
	private static final String SELEZIONA_DOMANDA = "SelezionaDomanda";
	private static final String ALTRO = "Altro";
	private static final String BAN_USER = "BanUser";
	private static final String LISTA_MASTER = "ListaMaster";
	private static final String NUOVO_MASTER = "NuovoMaster";
	private static final String LISTA_DOMANDE = "ListaDomande";
	private static final String NUOVA_DOMANDA = "NuovaDomanda";

	private ManagerUsersGame managerUsersGame;
	private ManagerUsersGame unknownUsersGame;
	private MasterUsersGame masterUsersGame;

	private String botUsername;
	private String botToken;
	private String accessPassword;
	private Question questionSelected;
	private int numberOfAnswer;
	private boolean canAnswer;
	private boolean alreadyStart;
	private boolean allowNewPlayer;

	public QuizBot(String botUsername, String botToken) {
		managerUsersGame = new ManagerUsersGame();
		unknownUsersGame = new ManagerUsersGame();
		masterUsersGame = new MasterUsersGame(true);
		this.botUsername = botUsername;
		this.botToken = botToken;
		this.accessPassword = String.valueOf((int) (99991 * Math.random()));
		questionSelected = null;
		canAnswer = false;
		alreadyStart = false;
		allowNewPlayer = true;
		numberOfAnswer = 0;
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
				playerUserMenu(user, receivedMessage);
			} else if (!unknownUsersGame.containUserGame(user)) {
				unknownUserMenu(update, user, receivedMessage);
			} else if (unknownUsersGame.containUserGame(user)) {
				newUserMenu(user, receivedMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void playerUserMenu(User user, String receivedMessage) {
		if (canAnswer) {
			UserGame gamer = managerUsersGame.getUserGame(user);
			if (!gamer.isAlreadyAnswerToQuestion()) {
				if (receivedMessage.equals("A") || receivedMessage.equals("B") || receivedMessage.equals("C")
						|| receivedMessage.equals("D")) {
					if (receivedMessage.equals(questionSelected.getRightAnswer())) {
						gamer.addPoints(questionSelected.getPoints());
					}
					gamer.setAlreadyAnswerToQuestion(true);
					numberOfAnswer += 1;
				} else {
					SendTextMessageWithKeyboard(gamer.getChat().getId(), "Usa la tastiera!", extractAnswerKeyboard());
				}
			}

		}
	}

	private void masterUserMenu(User user, String receivedMessage) {
		UserGame master = masterUsersGame.getUserGame(user);
		if (!master.isWantResetQuestions()) {
			if (!master.isWantResetPoints()) {
				if (!master.isWantResetGame()) {
					if (!master.isStartStop()) {
						if (!master.isGetSelectedQuestion()) {
							if (!master.isSendMessage()) {
								if (!master.isWantBan()) {
									if (!master.isFlagPoints()) {
										if (!master.isFlagAnswer()) {
											if (!master.isFlagQuestion()) {
												masterParserMessage(receivedMessage, master);
											} else {
												masterNewQuestionRequestQuestion(receivedMessage, master);
											}
										} else {
											masterNewAnswerRequestQuestion(receivedMessage, master);
										}
									} else {
										masterNewPointRequestQuestion(receivedMessage, master);
									}
								} else {
									masterRequestBan(receivedMessage, master);
								}
							} else {
								masterRequestSendMessage(receivedMessage, master);
							}
						} else {
							masterRequestSelectQuestion(receivedMessage, master);
						}
					} else {
						masterRequestStartGame(receivedMessage, master);
					}
				} else {
					masterWantResetGame(receivedMessage, master);
				}
			} else {
				masterWantResetPoints(receivedMessage, master);
			}
		} else {
			masterWantResetQuestions(receivedMessage, master);
		}
	}

	private void masterWantResetQuestions(String receivedMessage, UserGame master) {
		if (receivedMessage.equals(accessPassword)) {
			masterUsersGame.resetDefaultQuestion();
			SendTextMessageWithKeyboard(master.getChat().getId(), "Le domande sono state resettate!",
					extractMasterKeyboard(master.getNumberMenu()));
		} else if (receivedMessage.equals("/annulla")) {
			SendTextMessageWithKeyboard(master.getChat().getId(), "Hai annullato il processo di reset delle domande!",
					extractMasterKeyboard(master.getNumberMenu()));
		} else {
			SendTextMessageWithKeyboard(master.getChat().getId(), "La password inserita è sbagliata!",
					extractMasterKeyboard(master.getNumberMenu()));
		}
		master.setWantResetQuestions(false);
	}

	private void masterWantResetPoints(String receivedMessage, UserGame master) {

	}

	private void masterWantResetGame(String receivedMessage, UserGame master) {

	}

	private void masterRequestStartGame(String receivedMessage, UserGame master) {
		if (receivedMessage.equals("Stop!")) {
			SendTextMessageWithKeyboard(master.getChat().getId(),
					"Hai stoppato la possibilità di rispondere, il numero delle risposte ricevute sono: "
							+ numberOfAnswer,
					extractMasterKeyboard(master.getNumberMenu()));
			for (UserGame userGame : managerUsersGame.getListOfUsers()) {
				SendTextMessage(userGame.getChat().getId(), "Stop alle risposte!");
			}
			numberOfAnswer = 0;
			questionSelected = null;
			canAnswer = false;
			for (UserGame userGame : managerUsersGame.getListOfUsers()) {
				userGame.setAlreadyAnswerToQuestion(false);
			}
			master.setStartStop(false);
			alreadyStart = false;
		} else {
			SendTextMessage(master.getChat().getId(), "Per stoppare devi inviare Stop!");
		}
	}

	private void masterRequestSelectQuestion(String receivedMessage, UserGame master) {
		if ((Integer.parseInt(receivedMessage) <= 30) && (Integer.parseInt(receivedMessage) >= 1)) {
			questionSelected = masterUsersGame.getQuestion((Integer.parseInt(receivedMessage)));
			SendTextMessageWithKeyboard(master.getChat().getId(),
					"Hai selezionato la domanda:\n\r" + questionSelected.toString(),
					extractMasterKeyboard(master.getNumberMenu()));
			master.setGetSelectedQuestion(false);
		}
	}

	private void masterRequestSendMessage(String receivedMessage, UserGame master) {
		if (!receivedMessage.equals("/annulla")) {
			for (UserGame receiver : managerUsersGame.getListOfUsers()) {
				SendTextMessage(receiver.getChat().getId(), receivedMessage);
			}
			SendTextMessageWithKeyboard(master.getChat().getId(), "Il messaggio inviato è: " + receivedMessage,
					extractMasterKeyboard(master.getNumberMenu()));
		} else {
			SendTextMessageWithKeyboard(master.getChat().getId(), "Hai annullato il processo di invio messaggi!",
					extractMasterKeyboard(master.getNumberMenu()));
		}
		master.setSendMessage(false);
	}

	private void masterRequestBan(String receivedMessage, UserGame master) {
		if (!receivedMessage.equals("/annulla")) {
			Long chatIdToBan = Long.parseLong(receivedMessage);
			if (!chatIdToBan.equals(master.getChat().getId())) {
				UserGame userBan = managerUsersGame.getUserGame(chatIdToBan);
				if (userBan == null) {
					userBan = unknownUsersGame.getUserGame(chatIdToBan);
					if (userBan == null) {
						userBan = masterUsersGame.getUserGame(chatIdToBan);
						if (userBan == null) {
							SendTextMessageWithKeyboard(master.getChat().getId(), "Non esiste nessuno con questo nome",
									extractMasterKeyboard(master.getNumberMenu()));
						} else {
							masterUsersGame.removeUserGame(userBan);
							SendTextMessageWithKeyboard(master.getChat().getId(),
									"Il master " + userBan.getUser().getUserName() + " è stato bannato!",
									extractMasterKeyboard(master.getNumberMenu()));
						}
					} else {
						unknownUsersGame.removeUserGame(userBan);
						SendTextMessageWithKeyboard(master.getChat().getId(),
								"Il giocatore " + userBan.getUser().getUserName() + " è stato bannato!",
								extractMasterKeyboard(master.getNumberMenu()));
					}
				} else {
					managerUsersGame.removeUserGame(userBan);
					SendTextMessageWithKeyboard(master.getChat().getId(),
							"Il giocatore " + userBan.getUser().getUserName() + " è stato bannato!",
							extractMasterKeyboard(master.getNumberMenu()));
				}
				if (userBan != null) {
					SendTextMessageWithKeyboard(userBan.getChat().getId(), "Mi dispiace ma sei stato bannato!",
							extractStartKeyboard());
				}
			} else {
				SendTextMessageWithKeyboard(master.getChat().getId(), "Non puoi bannare te stesso!",
						extractMasterKeyboard(master.getNumberMenu()));
			}
		} else {
			SendTextMessageWithKeyboard(master.getChat().getId(), "Hai annullato il processo di ban!",
					extractMasterKeyboard(master.getNumberMenu()));
		}
		master.setWantBan(false);
	}

	private void masterNewPointRequestQuestion(String receivedMessage, UserGame master) {
		int points = Integer.parseInt(receivedMessage);
		if (points >= 1 || points <= 3) {
			masterUsersGame.addNewQuestion(master.getSettingQuestion(), master.getSettingRightQuestion(), points);
			SendTextMessageWithKeyboard(master.getChat().getId(),
					"La domanda creata è: \n\r"
							+ masterUsersGame.getListOfQuestion().get(master.getSettingQuestion() - 1).toString(),
					extractMasterKeyboard(master.getNumberMenu()));
			master.setSettingQuestion(0);
			master.setSettingRightQuestion("");
			master.setFlagPoints(false);
		} else {
			SendTextMessage(master.getChat().getId(), "Devi inserire un punteggio 1 2 o 3!");
		}
	}

	private void masterNewAnswerRequestQuestion(String receivedMessage, UserGame master) {
		if ((receivedMessage.equals("A")) || (receivedMessage.equals("B")) || (receivedMessage.equals("C"))
				|| (receivedMessage.equals("D"))) {
			master.setFlagAnswer(false);
			master.setSettingRightQuestion(receivedMessage);
			SendTextMessageWithKeyboard(master.getChat().getId(), "Quale è il punteggio della risposta?",
					extractPointAnswerKeyboard());
			master.setFlagPoints(true);
		} else {
			SendTextMessage(master.getChat().getId(), "Devi inserire una risposta A B C o D!");
		}
	}

	private void masterNewQuestionRequestQuestion(String receivedMessage, UserGame master) {
		if ((Integer.parseInt(receivedMessage) <= 30) && (Integer.parseInt(receivedMessage) >= 1)) {
			master.setFlagQuestion(false);
			master.setSettingQuestion(Integer.parseInt(receivedMessage));
			SendTextMessageWithKeyboard(master.getChat().getId(), "Quale e' la risposta giusta?",
					extractAnswerKeyboard());
			master.setFlagAnswer(true);
		} else {
			SendTextMessage(master.getChat().getId(), "Devi selezionare una domanda tra la 1 e la 30!");
		}
	}

	private void masterParserMessage(String receivedMessage, UserGame master) {
		switch (receivedMessage) {
		case RESET_GIOCO:
			master.setWantResetGame(true);
			SendTextMessageWithKeyboard(master.getChat().getId(), "Inserisci la access password!",
					extractDeleteKeyboard());
			break;
		case RESET_PUNTEGGI:
			master.setWantResetPoints(true);
			SendTextMessageWithKeyboard(master.getChat().getId(), "Inserisci la access password!",
					extractDeleteKeyboard());
			break;
		case RESET_DOMANDE:
			master.setWantResetQuestions(true);
			SendTextMessageWithKeyboard(master.getChat().getId(), "Inserisci la access password!",
					extractDeleteKeyboard());
			break;
		case START_STOP_PARTECIPANTI:
			if (allowNewPlayer) {
				SendTextMessage(master.getChat().getId(), "Hai disabilitato la possibilità di entrare in gioco!");
			} else {
				SendTextMessage(master.getChat().getId(), "Hai abilitato la possibilità di entrare in gioco!");
			}
			unknownUsersGame.getListOfUsers().clear();
			allowNewPlayer = !allowNewPlayer;
			break;
		case PARTECIPANTI_CHAT_ID:
			SendTextMessage(master.getChat().getId(),
					"Il numero dei partecipanti è: " + managerUsersGame.getListOfUsers().size()
							+ "\n\rLa lista dei giocatori con chat id è: \n\r" + managerUsersGame.getUsersChatIdList()
							+ "\n\rIl numero dei master è: " + masterUsersGame.getListOfUsers().size()
							+ "\n\rLa lista dei master è: \n\r" + masterUsersGame.getUsersChatIdList());
			break;
		case PUNTEGGIO_GIOCATORI:
			managerUsersGame.orderGamersList();
			SendTextMessage(master.getChat().getId(),
					"Il numero dei partecipanti è: " + managerUsersGame.getListOfUsers().size()
							+ " \n\rLa lista dei giocatori è in ordine decrescente: \n\r"
							+ managerUsersGame.getUsersPointsList());
			break;
		case NUOVA_DOMANDA:
			master.setFlagQuestion(true);
			SendTextMessageWithKeyboard(master.getChat().getId(), "Quale domanda vuoi impostare?",
					extractQuestionKeyboard());
			break;
		case LISTA_DOMANDE:
			SendTextMessage(master.getChat().getId(),
					"Ecco la lista delle domande:\n\r" + masterUsersGame.getStringListOfQuestion());
			break;
		case NUOVO_MASTER:
			masterUsersGame.setAcceptNewMaster(true);
			SendTextMessage(master.getChat().getId(), "Può essere accettato un nuovo master");
			break;
		case LISTA_MASTER:
			SendTextMessage(master.getChat().getId(),
					"Il numero dei master è: " + masterUsersGame.getListOfUsers().size() + " \n\rLa lista dei master:"
							+ masterUsersGame.getMasterInfoList());
			break;
		case BAN_USER:
			master.setWantBan(true);
			SendTextMessageWithKeyboard(master.getChat().getId(), "Inserisci la chatId del giocatore da bannare!",
					extractDeleteKeyboard());
			break;
		case SELEZIONA_DOMANDA:
			SendTextMessageWithKeyboard(master.getChat().getId(), "Quale domanda vuoi selezionare?",
					extractQuestionKeyboard());
			master.setGetSelectedQuestion(true);
			break;
		case START:
			if (!alreadyStart) {
				if (questionSelected != null) {
					alreadyStart = true;
					master.setStartStop(true);
					KeyboardRow row = new KeyboardRow();
					row.add("Stop!");
					SendTextMessageWithKeyboard(master.getChat().getId(),
							"I giocatori possono ora rispondere alla domanda selezionata, quando vuoi stoppare questa possibilità clicca su stop!",
							extractKeyboardMarkup(row));
					canAnswer = true;
					for (UserGame userGame : managerUsersGame.getListOfUsers()) {
						SendTextMessage(userGame.getChat().getId(),
								"Ora puoi rispondere... Ricordati che la prima risposta che darai sarà quella definitiva!");
					}
				} else {
					SendTextMessage(master.getChat().getId(), "Devi ancora scegliere la domanda!");
				}
			} else {
				SendTextMessage(master.getChat().getId(), "Già un altro master ha abilitato le risposte!");
			}
			break;
		case INVIA_MESSAGGIO:
			master.setSendMessage(true);
			SendTextMessageWithKeyboard(master.getChat().getId(), "Inserisci il messaggio da inviare!",
					extractDeleteKeyboard());
			break;
		case ALTRO:
			master.nextNumberMenu();
			SendTextMessageWithKeyboard(master.getChat().getId(), "Questi sono gli altri comandi!",
					extractMasterKeyboard(master.getNumberMenu()));
			break;
		default:
			SendTextMessageWithKeyboard(master.getChat().getId(), "Usa i pulsanti, non so come aiutarti altrimenti!",
					extractMasterKeyboard(master.getNumberMenu()));
			break;
		}
	}

	private void unknownUserMenu(Update update, User user, String receivedMessage) {
		UserGame newGamer = new UserGame(user, update.getMessage().getChat());
		KeyboardRow row;
		if (allowNewPlayer) {
			switch (receivedMessage) {
			case "/start":
				unknownUsersGame.addUserGame(newGamer);
				row = new KeyboardRow();
				row.add("Si!");
				row.add("No!");
				SendTextMessageWithKeyboard(newGamer.getChat().getId(), "Vuoi giocare?", extractKeyboardMarkup(row));
				break;
			default:
				unknownUsersGame.removeUserGame(newGamer);
				SendTextMessageWithKeyboard(newGamer.getChat().getId(),
						"Non so chi sei... Per iniziare a giocare clicca il pulsante start!", extractStartKeyboard());
				break;
			}
		} else {
			SendTextMessage(newGamer.getChat().getId(), "Mi dispiace ma non è più possibile entrare in gioco!");
		}
	}

	private void newUserMenu(User user, String receivedMessage) {
		UserGame userGame = unknownUsersGame.getUserGame(user);
		if (receivedMessage.equals("/master " + accessPassword)) {
			if (masterUsersGame.isAcceptNewMaster()) {
				masterUsersGame.addUserGame(userGame);
				unknownUsersGame.removeUserGame(userGame);
				masterUsersGame.setAcceptNewMaster(false);
				SendTextMessageWithKeyboard(userGame.getChat().getId(), "Utilizzando i pulsanti puoi gestire tutto!",
						extractMasterKeyboard(userGame.getNumberMenu()));
				logUser(userGame.getUser().getFirstName(), userGame.getUser().getLastName(),
						userGame.getUser().getUserName(), Long.toString(userGame.getChat().getId()), "Master",
						Integer.toString(masterUsersGame.getListOfUsers().size()));
			} else {
				unknownUsersGame.removeUserGame(userGame);
				SendTextMessageWithKeyboard(userGame.getChat().getId(),
						"La possibilità di diventare master è stata disabilitata!", extractStartKeyboard());
			}
		} else {
			switch (receivedMessage) {
			case "Si!":
				managerUsersGame.addUserGame(userGame);
				unknownUsersGame.removeUserGame(userGame);
				SendTextMessageWithKeyboard(userGame.getChat().getId(),
						"Sei stato aggiunto alla lista dei partecipanti! Ora dovrai solo aspettare l'inizio del gioco!",
						extractAnswerKeyboard());
				logUser(userGame.getUser().getFirstName(), userGame.getUser().getLastName(),
						userGame.getUser().getUserName(), Long.toString(userGame.getChat().getId()), "Player",
						Integer.toString(managerUsersGame.getListOfUsers().size()));
				break;
			case "No!":
				unknownUsersGame.removeUserGame(userGame);
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

	private ReplyKeyboardMarkup extractDeleteKeyboard() {
		KeyboardRow row = new KeyboardRow();
		row.add("/annulla");
		return extractKeyboardMarkup(row);
	}

	private ReplyKeyboardMarkup extractStartKeyboard() {
		KeyboardRow row = new KeyboardRow();
		row.add("/start");
		return extractKeyboardMarkup(row);
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
		KeyboardRow row3 = new KeyboardRow();
		for (int i = 21; i <= 30; i++) {
			row3.add(Integer.toString(i));
		}
		return extractKeyboardMarkup(row1, row2, row3);
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

	private ReplyKeyboardMarkup extractMasterKeyboard(int numberMenu) {
		KeyboardRow row1 = new KeyboardRow();
		KeyboardRow row2 = new KeyboardRow();
		KeyboardRow row3 = new KeyboardRow();
		if (numberMenu == 0) {
			row1.add(SELEZIONA_DOMANDA);
			row1.add(START);
			row2.add(PUNTEGGIO_GIOCATORI);
			row2.add(INVIA_MESSAGGIO);
			row3.add(ALTRO);
		} else if (numberMenu == 1) {
			row1.add(NUOVA_DOMANDA);
			row1.add(LISTA_DOMANDE);
			row2.add(BAN_USER);
			row2.add(PARTECIPANTI_CHAT_ID);
			row3.add(START_STOP_PARTECIPANTI);
			row3.add(ALTRO);
		} else if (numberMenu == 2) {
			row1.add(RESET_DOMANDE);
			row1.add(RESET_PUNTEGGI);
			row2.add(RESET_GIOCO);
			row2.add(NUOVO_MASTER);
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

	private void logUser(String firstName, String lastName, String userName, String chatId, String type,
			String numberOfType) {
		System.out.println("\n\r ----------------------------");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		System.out.println("\n\r Nuovo " + type + " con fistname: " + firstName + ", lastname: " + lastName
				+ ", username: " + userName + ", chatId: " + chatId + " \n\r Il numero di " + type
				+ " registrati sono: " + numberOfType);
	}

}
