package some.telegram.bot.core;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;

public class UserGame {

	private User user;
	private Chat chat;
	private int points;

	public UserGame(User user, Chat chat) {
		this.user = user;
		this.chat = chat;
		points = 0;
	}

	public User getUser() {
		return user;
	}

	public int getPoints() {
		return points;
	}

	public Chat getChat() {
		return chat;
	}

}
