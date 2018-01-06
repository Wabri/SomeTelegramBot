package some.telegram.bot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import some.telegram.bot.core.UserGame;
import some.telegram.bot.manager.ManagerUsersGame;

public class QuizBot extends TelegramLongPollingBot {

	ManagerUsersGame managerUsersGame;
	ManagerUsersGame unknownUserGame;
	private String botUsername;
	private String botToken;

	public QuizBot(String botUsername, String botToken) {
		managerUsersGame = new ManagerUsersGame();
		unknownUserGame = new ManagerUsersGame();
		this.botUsername = botUsername;
		this.botToken = botToken;
	}

	@Override
	public void onUpdateReceived(Update update) {
		try {
			Contact contact = update.getMessage().getContact();
			String receivedMessage = update.getMessage().getText();
			if (!managerUsersGame.containUserGameContact(contact) && !unknownUserGame.containUserGameContact(contact)) {
				UserGame newGamer = new UserGame(contact, update.getMessage().getChat());
				unknownUserGame.addUserGame(newGamer);
				if (receivedMessage.equals("/start")) {
					ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
					List<KeyboardRow> keyboard = new ArrayList<>();
					KeyboardRow row = new KeyboardRow();
					row.add("Si!");
					row.add("No!");
					keyboard.add(row);
					keyboardMarkup.setKeyboard(keyboard);
					SendTextMessageWithKeyboard(newGamer.getChat().getId(), "Vuoi giocare?", keyboardMarkup);
				} else {
					SendTextMessage(newGamer.getChat().getId(),
							"Per avere informazioni devi inserire il comando /start");
				}
			} else if (unknownUserGame.containUserGameContact(contact)) {
				UserGame userGame = unknownUserGame.getUserGame(contact);
				if (receivedMessage.equals("Si!")) {
					managerUsersGame.addUserGame(userGame);
					unknownUserGame.removeUserGame(userGame);
					ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
					List<KeyboardRow> keyboard = new ArrayList<>();
					KeyboardRow row = new KeyboardRow();
					row.add("A");
					row.add("B");
					row.add("C");
					row.add("D");
					keyboard.add(row);
					keyboardMarkup.setKeyboard(keyboard);
					SendTextMessageWithKeyboard(userGame.getChat().getId(),
							"Sei stato aggiunto alla lista dei partecipanti! Ora dovrai solo aspettare l'inizio del gioco!",
							keyboardMarkup);
				} else if (receivedMessage.equals("No!")) {
					unknownUserGame.removeUserGame(userGame);
					ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
					List<KeyboardRow> keyboard = new ArrayList<>();
					KeyboardRow row = new KeyboardRow();
					row.add("/start");
					keyboard.add(row);
					keyboardMarkup.setKeyboard(keyboard);
					SendTextMessageWithKeyboard(userGame.getChat().getId(), "Se cambi idea clica sul pulsante start!",
							keyboardMarkup);
				} else {
					SendTextMessage(userGame.getChat().getId(), "Devi rispondere o Si! o No!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
