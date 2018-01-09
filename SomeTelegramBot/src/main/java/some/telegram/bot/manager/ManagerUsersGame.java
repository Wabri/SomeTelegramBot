package some.telegram.bot.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.telegram.telegrambots.api.objects.User;

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

	public boolean containUserGame(User user) {
		for (UserGame userGame : listOfUsers) {
			if (userGame.getUser().getId().equals(user.getId())) {
				return true;
			}
		}
		return false;
	}

	public UserGame getUserGame(User user) {
		for (UserGame userGame : listOfUsers) {
			if (userGame.getUser().getId().equals(user.getId())) {
				return userGame;
			}
		}
		return null;
	}

	public String getUsersPointsList() {
		String userPointList = "";
		for (UserGame userGame : listOfUsers) {
			if (!(userGame.getUser().getUserName() == null)) {
				userPointList += "\n\r " + userGame.getUser().getUserName() + " -> " + userGame.getPoints();
			} else if (!(userGame.getUser().getFirstName() == null)) {
				if (!(userGame.getUser().getLastName() == null)) {
					userPointList += "\n\r " + userGame.getUser().getFirstName() + " "
							+ userGame.getUser().getLastName() + " -> " + userGame.getPoints();
				} else {
					userPointList += "\n\r " + userGame.getUser().getFirstName() + " -> " + userGame.getPoints();
				}
			} else if (!(userGame.getUser().getLastName() == null)) {
				userPointList += "\n\r " + userGame.getUser().getLastName() + " -> " + userGame.getPoints();
			} else {
				userPointList += "\n\r NoNamePlayer" + " -> " + userGame.getPoints();
			}
		}
		return userPointList;
	}

	public UserGame getUserGame(String receivedMessage) {
		for (UserGame userGame : listOfUsers) {
			if (userGame.getUser().getUserName().equals(receivedMessage)) {
				return userGame;
			}
		}
		return null;
	}

}
