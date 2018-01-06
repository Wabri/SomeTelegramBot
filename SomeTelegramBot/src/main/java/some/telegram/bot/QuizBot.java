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
	List<UserGame> userGameUnknown;

	public QuizBot() {
		managerUsersGame = new ManagerUsersGame();
		userGameUnknown = new ArrayList<UserGame>();
	}

	@Override
	public void onUpdateReceived(Update update) {
		try {
			Contact contact = update.getMessage().getContact();
			if (!managerUsersGame.containUserGameContact(contact)) {
				if (update.getMessage().getText().equals("/start")) {
					// UserGame userGame = managerUsersGame.getUserGame(contact);
					UserGame newGamer = new UserGame(contact, update.getMessage().getChat());
					userGameUnknown.add(newGamer);
					ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
					List<KeyboardRow> keyboard = new ArrayList<>();
					KeyboardRow row = new KeyboardRow();
					row.add("Si!");
					row.add("No!");
					keyboard.add(row);
					keyboardMarkup.setKeyboard(keyboard);
					SendTextMessageWithKeyboard(newGamer.getChat().getId(), "Vuoi giocare?", keyboardMarkup);
				}
			} else {

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
		return null;
	}

	@Override
	public String getBotToken() {
		return null;
	}

}
