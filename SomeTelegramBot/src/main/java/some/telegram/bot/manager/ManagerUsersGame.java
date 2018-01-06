package some.telegram.bot.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.telegram.telegrambots.api.objects.Contact;

import some.telegram.bot.core.UserGame;

public class ManagerUsersGame {

	List<UserGame> listOfUsers;

	public ManagerUsersGame() {
		listOfUsers = new ArrayList<UserGame>();
	}

	public List<UserGame> getListOfUsers() {
		return listOfUsers;
	}

	public void addUserGame(UserGame newUserGame) {
		listOfUsers.add(newUserGame);
	}

	public void removeUserGame(UserGame removeUserGame) {
		listOfUsers.remove(removeUserGame);
	}

	public void orderGamersList() {
		if (!listOfUsers.isEmpty()) {
			Collections.sort(listOfUsers, (gamerX, gamerY) -> gamerX.getPoints() < gamerY.getPoints() ? -1
					: gamerX.getPoints() >= gamerY.getPoints() ? 0 : 1);
		}
	}

	public boolean containUserGameContact(Contact contact) {
		for (UserGame userGame : listOfUsers) {
			if (userGame.getContact().equals(contact)) {
				return true;
			}
		}
		return false;
	}

	public UserGame getUserGame(Contact contact) {
		return null;
	}

}
