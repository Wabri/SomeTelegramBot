package some.telegram.bot.core;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;

public class UserGame {

	private User user;
	private Chat chat;
	private int points;
	private boolean flagQuestion;

	public UserGame(User user, Chat chat) {
		this.user = user;
		this.chat = chat;
		points = 0;
		flagQuestion = false;
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

	public boolean isFlagQuestion() {
		return flagQuestion;
	}

	public void setFlagQuestion(boolean flagQuestion) {
		this.flagQuestion = flagQuestion;
	}

}
