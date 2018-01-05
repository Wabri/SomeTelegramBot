package some.telegram.bot;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class QuizBot extends TelegramLongPollingBot {

	public QuizBot() {

	}

	@Override
	public void onUpdateReceived(Update update) {

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
