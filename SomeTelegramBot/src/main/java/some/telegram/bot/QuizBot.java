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
			User user = update.getMessage().getFrom();
			String receivedMessage = update.getMessage().getText();
			if (managerUsersGame.containUserGame(user)) {

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
				KeyboardRow row;
				switch (receivedMessage) {
				case "Si!":
					managerUsersGame.addUserGame(userGame);
					unknownUserGame.removeUserGame(userGame);
					row = new KeyboardRow();
					row.add("A");
					row.add("B");
					row.add("C");
					row.add("D");
					SendTextMessageWithKeyboard(userGame.getChat().getId(),
							"Sei stato aggiunto alla lista dei partecipanti! Ora dovrai solo aspettare l'inizio del gioco!",
							extractKeyboardMarkup(row));
					break;
				case "No!":
					unknownUserGame.removeUserGame(userGame);
					row = new KeyboardRow();
					row.add("/start");
					SendTextMessageWithKeyboard(userGame.getChat().getId(), "Se cambi idea clica sul pulsante start!",
							extractKeyboardMarkup(row));
					break;
				default:
					SendTextMessage(userGame.getChat().getId(), "Devi rispondere o Si! o No!");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
