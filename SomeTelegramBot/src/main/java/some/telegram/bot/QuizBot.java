package some.telegram.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import some.telegram.bot.core.UserGame;
import some.telegram.bot.manager.ManagerUsersGame;

public class QuizBot extends TelegramLongPollingBot {

	ManagerUsersGame managerUsersGame;

	public QuizBot() {
		managerUsersGame = new ManagerUsersGame();
	}

	@Override
	public void onUpdateReceived(Update update) {
		try {
			Contact contact = update.getMessage().getContact();
			if (managerUsersGame.containUserGameContact(contact)) {
				UserGame userGame = managerUsersGame.getUserGame(contact);
				SendTextMessage(userGame.getChat().getId(), "Vuoi giocare?");
			} else {

			}
		} catch (Exception e) {
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
