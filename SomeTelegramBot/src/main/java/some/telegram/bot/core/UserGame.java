package some.telegram.bot.core;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Contact;

public class UserGame {

	private Contact contact;
	private Chat chat;
	private int points;

	public UserGame(Contact contact, Chat chat) {
		this.contact = contact;
		this.chat = chat;
		points = 0;
	}

	public Contact getContact() {
		return contact;
	}

	public int getPoints() {
		return points;
	}

	public Chat getChat() {
		return chat;
	}

}
