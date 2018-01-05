package some.telegram.bot.core;

import org.telegram.telegrambots.api.objects.Contact;

public class UserGame {

	private Contact contact;
	private int points;

	public UserGame(Contact contact) {
		this.contact = contact;
		points = 0;
	}

	public Contact getContact() {
		return contact;
	}

	public int getPoints() {
		return points;
	}

}
