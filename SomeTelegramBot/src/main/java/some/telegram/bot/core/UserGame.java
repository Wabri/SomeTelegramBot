package some.telegram.bot.core;

import org.telegram.telegrambots.api.objects.Contact;

public class UserGame {

	private Contact contact;

	public UserGame(Contact contact) {
		this.contact = contact;
	}

	public Contact getContact() {
		return contact;
	}

}
