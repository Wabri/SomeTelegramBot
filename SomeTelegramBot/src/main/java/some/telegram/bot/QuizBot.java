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

import some.telegram.bot.core.Question;
import some.telegram.bot.core.UserGame;
import some.telegram.bot.manager.ManagerUsersGame;
import some.telegram.bot.manager.MasterUsersGame;

public class QuizBot extends TelegramLongPollingBot {

	private static final String INFO = "Info";
	private static final String INVIA_MESSAGGIO = "InviaMessaggio";
	private static final String START = "Start";
	private static final String SELEZIONA_DOMANDA = "SelezionaDomanda";
	private static final String ALTRO = "Altro";
	private static final String BAN_USER = "BanUser";
	private static final String LISTA_MASTER = "ListaMaster";
	private static final String NUOVO_MASTER = "NuovoMaster";
	private static final String LISTA_GIOCATORI = "ListaGiocatori";
	private static final String LISTA_DOMANDE = "ListaDomande";
	private static final String NUOVA_DOMANDA = "NuovaDomanda";
	private static final String INFO_MESSAGE = "I comandi che puoi usare puoi usare sono tutti nella sezione dei pulsanti: \n\r\n\r"
			+ INFO
			+ ": fornisce tutte le informazioni tra cui la spiegazione dei comandi, la lista dei giocatori e tutto ciò di cui puoi avere bisogno \n\r\n\r"
			+ SELEZIONA_DOMANDA
			+ ": questo ti permetterà di selezionare la domanda da fare ai giocatori (ricorda che prima di selezionare la domanda dovrai impostarla usando il comando "
			+ NUOVA_DOMANDA + ") \n\r\n\r" + START
			+ ": permette di dare il via al gioco, saranno infatti abilitate le risposte dei giocatori fino a che non sarà cliccato il pulsante di stop \n\r\n\r"
			+ LISTA_GIOCATORI
			+ ": restituisce la lista di tutti i partecipanti sotto forma di username->punteggio \n\r\n\r"
			+ INVIA_MESSAGGIO + ": permette di inviare un messaggio a tutti i partecipanti del gioco \n\r\n\r"
			+ NUOVA_DOMANDA + ": permette di creare (o sovrascrivere) una nuova domanda \n\r\n\r" + LISTA_DOMANDE
			+ ": restituisce la lista di tutte le domande sotto forma di domanda->risposta->punteggio (anche le domande non impostate) \n\r\n\r"
			+ NUOVO_MASTER + ": abilita la possibilità ad uno user di diventare master \n\r\n\r" + LISTA_MASTER
			+ ": restituisce la lista dei master\n\r\n\r" + BAN_USER
			+ ": cliccato questo pulsante è possibile eliminare uno user (indipendentemente che sia master o normale user)\n\r\n\r"
			+ ALTRO + ": cambia i pulsanti nella tastiera\n\r\n\r";

	private ManagerUsersGame managerUsersGame;
	private ManagerUsersGame unknownUsersGame;
	private MasterUsersGame masterUsersGame;

	private String botUsername;
	private String botToken;
	private String accessPassword;
	private Question questionSelected;
	private int numberOfAnswer;
	private boolean canAnswer;

	public QuizBot(String botUsername, String botToken) {
		managerUsersGame = new ManagerUsersGame();
		unknownUsersGame = new ManagerUsersGame();
		masterUsersGame = new MasterUsersGame(true);
		this.botUsername = botUsername;
		this.botToken = botToken;
		this.accessPassword = String.valueOf((int) (99991 * Math.random()));
		questionSelected = null;
		canAnswer = false;
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
							SendTextMessageWithKeyboard(gamer.getChat().getId(), "Usa la tastiera!",
									extractAnswerKeyboard());
						}
					}

				}
			} else if (!unknownUsersGame.containUserGame(user)) {
				newUserMenu(update, user, receivedMessage);
			} else if (unknownUsersGame.containUserGame(user)) {
				unknownUserMenu(user, receivedMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void masterUserMenu(User user, String receivedMessage) {
		UserGame master = masterUsersGame.getUserGame(user);
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
								masterNewAnswerRequestQuestio(receivedMessage, master);
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
	}

	private void masterRequestStartGame(String receivedMessage, UserGame master) {
		if (receivedMessage.equals("Stop!")) {
			SendTextMessageWithKeyboard(master.getChat().getId(),
					"Hai stoppato la possibilità di rispondere, il numero delle risposte ricevute sono: "
							+ numberOfAnswer,
					extractMasterKeyboard(master.isOtherMasterMenu()));
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
		} else {
			SendTextMessage(master.getChat().getId(), "Per stoppare devi inviare Stop!");
		}
	}

	private void masterRequestSelectQuestion(String receivedMessage, UserGame master) {
		if ((Integer.parseInt(receivedMessage) <= 30) && (Integer.parseInt(receivedMessage) >= 1)) {
			questionSelected = masterUsersGame.getQuestion((Integer.parseInt(receivedMessage)));
			SendTextMessageWithKeyboard(master.getChat().getId(),
					"Hai selezionato la domanda:\n\r" + questionSelected.toString(),
					extractMasterKeyboard(master.isOtherMasterMenu()));
			master.setGetSelectedQuestion(false);
		}
	}

	private void masterRequestSendMessage(String receivedMessage, UserGame master) {
		if (!receivedMessage.equals("/annulla")) {
			for (UserGame receiver : managerUsersGame.getListOfUsers()) {
				SendTextMessage(receiver.getChat().getId(), receivedMessage);
			}
			SendTextMessageWithKeyboard(master.getChat().getId(), "Il messaggio inviato è: " + receivedMessage,
					extractMasterKeyboard(master.isOtherMasterMenu()));
		} else {
			SendTextMessageWithKeyboard(master.getChat().getId(), "Hai annullato il processo di invio messaggi!",
					extractMasterKeyboard(master.isOtherMasterMenu()));
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
									extractMasterKeyboard(master.isOtherMasterMenu()));
						} else {
							masterUsersGame.removeUserGame(userBan);
							SendTextMessageWithKeyboard(master.getChat().getId(),
									"Il master " + userBan.getUser().getUserName() + " è stato bannato!",
									extractMasterKeyboard(master.isOtherMasterMenu()));
						}
					} else {
						unknownUsersGame.removeUserGame(userBan);
						SendTextMessageWithKeyboard(master.getChat().getId(),
								"Il giocatore " + userBan.getUser().getUserName() + " è stato bannato!",
								extractMasterKeyboard(master.isOtherMasterMenu()));
					}
				} else {
					managerUsersGame.removeUserGame(userBan);
					SendTextMessageWithKeyboard(master.getChat().getId(),
							"Il giocatore " + userBan.getUser().getUserName() + " è stato bannato!",
							extractMasterKeyboard(master.isOtherMasterMenu()));
				}
				if (userBan != null) {
					SendTextMessageWithKeyboard(userBan.getChat().getId(), "Mi dispiace ma sei stato bannato!",
							extractStartKeyboard());
				}
			} else {
				SendTextMessageWithKeyboard(master.getChat().getId(), "Non puoi bannare te stesso!",
						extractMasterKeyboard(master.isOtherMasterMenu()));
			}
		} else {
			SendTextMessageWithKeyboard(master.getChat().getId(), "Hai annullato il processo di ban!",
					extractMasterKeyboard(master.isOtherMasterMenu()));
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
					extractMasterKeyboard(master.isOtherMasterMenu()));
			master.setSettingQuestion(0);
			master.setSettingRightQuestion("");
			master.setFlagPoints(false);
		} else {
			SendTextMessage(master.getChat().getId(), "Devi inserire un punteggio 1 2 o 3!");
		}
	}

	private void masterNewAnswerRequestQuestio(String receivedMessage, UserGame master) {
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
			if (questionSelected != null) {
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
			break;
		case INVIA_MESSAGGIO:
			master.setSendMessage(true);
			SendTextMessageWithKeyboard(master.getChat().getId(), "Inserisci il messaggio da inviare!",
					extractDeleteKeyboard());
			break;
		case INFO:
			SendTextMessage(master.getChat().getId(), INFO_MESSAGE);
			break;
		case ALTRO:
			master.setOtherMasterMenu(!master.isOtherMasterMenu());
			SendTextMessageWithKeyboard(master.getChat().getId(), "Questi sono gli altri comandi!",
					extractMasterKeyboard(master.isOtherMasterMenu()));
			break;
		default:
			SendTextMessageWithKeyboard(master.getChat().getId(), "Usa i pulsanti, non so come aiutarti altrimenti!",
					extractMasterKeyboard(master.isOtherMasterMenu()));
			break;
		}
	}

	private void newUserMenu(Update update, User user, String receivedMessage) {
		UserGame newGamer = new UserGame(user, update.getMessage().getChat());
		KeyboardRow row;
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
	}

	private void unknownUserMenu(User user, String receivedMessage) {
		UserGame userGame = unknownUsersGame.getUserGame(user);
		if (receivedMessage.equals("/master " + accessPassword)) {
			if (masterUsersGame.isAcceptNewMaster()) {
				masterUsersGame.addUserGame(userGame);
				unknownUsersGame.removeUserGame(userGame);
				masterUsersGame.setAcceptNewMaster(false);
				SendTextMessageWithKeyboard(userGame.getChat().getId(), INFO_MESSAGE,
						extractMasterKeyboard(userGame.isOtherMasterMenu()));
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

	private ReplyKeyboardMarkup extractMasterKeyboard(boolean user) {
		KeyboardRow row1 = new KeyboardRow();
		KeyboardRow row2 = new KeyboardRow();
		KeyboardRow row3 = new KeyboardRow();
		if (user) {
			row1.add(NUOVA_DOMANDA);
			row1.add(LISTA_DOMANDE);
			row2.add(NUOVO_MASTER);
			row2.add(LISTA_MASTER);
			row3.add(BAN_USER);
			row3.add(ALTRO);
		} else {
			row1.add(SELEZIONA_DOMANDA);
			row1.add(START);
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
