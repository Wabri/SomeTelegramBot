import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import some.telegram.bot.QuizBot;

public class Main {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			telegramBotsApi.registerBot(new QuizBot(RunMyBot.getBotUsername(), RunMyBot.getBotToken()));
		} catch (TelegramApiRequestException e) {
			e.printStackTrace();
		}
		System.out.println("Bot successfully started!");
	}

}
