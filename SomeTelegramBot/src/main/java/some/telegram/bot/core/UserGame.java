package some.telegram.bot.core;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;

public class UserGame {

	private User user;
	private Chat chat;
	private int points;
	private boolean flagQuestion;
	private boolean flagAnswer;
	private boolean flagPoints;
	private int settingQuestion;
	private String settingRightQuestion;
	private boolean wantBan;

	public UserGame(User user, Chat chat) {
		this.user = user;
		this.chat = chat;
		points = 0;
		flagQuestion = false;
		flagAnswer = false;
		flagPoints = false;
		wantBan = false;
		settingQuestion = 0;
		settingRightQuestion = "";
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

	public int getSettingQuestion() {
		return settingQuestion;
	}

	public void setSettingQuestion(int settingQuestion) {
		this.settingQuestion = settingQuestion;
	}

	public String getSettingRightQuestion() {
		return settingRightQuestion;
	}

	public void setSettingRightQuestion(String receivedMessage) {
		this.settingRightQuestion = receivedMessage;
	}

	public boolean isFlagAnswer() {
		return flagAnswer;
	}

	public void setFlagAnswer(boolean flagAnswer) {
		this.flagAnswer = flagAnswer;
	}

	public boolean isFlagPoints() {
		return flagPoints;
	}

	public void setFlagPoints(boolean flagPoints) {
		this.flagPoints = flagPoints;
	}

	public void setWantBan(boolean wantBan) {
		this.wantBan = wantBan;
	}

	public boolean isWantBan() {
		return wantBan;
	}

}
