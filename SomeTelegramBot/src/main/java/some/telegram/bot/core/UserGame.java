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
	private boolean sendMessage;
	private int numberMenu;
	private boolean getSelectedQuestion;
	private boolean startStop;
	private boolean alreadyAnswerToQuestion;
	private boolean wantResetGame;
	private boolean wantResetPoints;
	private boolean wantResetQuestions;

	public UserGame(User user, Chat chat) {
		this.user = user;
		this.chat = chat;
		points = 0;
		flagQuestion = false;
		flagAnswer = false;
		flagPoints = false;
		wantBan = false;
		wantResetGame = false;
		wantResetPoints = false;
		wantResetQuestions = false;
		sendMessage = false;
		numberMenu = 0;
		getSelectedQuestion = false;
		startStop = false;
		alreadyAnswerToQuestion = false;
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

	public void setSendMessage(boolean sendMessage) {
		this.sendMessage = sendMessage;
	}

	public boolean isSendMessage() {
		return sendMessage;
	}

	public boolean isGetSelectedQuestion() {
		return getSelectedQuestion;
	}

	public void setGetSelectedQuestion(boolean getSelectedQuestion) {
		this.getSelectedQuestion = getSelectedQuestion;
	}

	public void setStartStop(boolean startStop) {
		this.startStop = startStop;
	}

	public boolean isStartStop() {
		return startStop;
	}

	public void addPoints(int points) {
		this.points += points;
	}

	public boolean isAlreadyAnswerToQuestion() {
		return alreadyAnswerToQuestion;
	}

	public void setAlreadyAnswerToQuestion(boolean AlreadyAnswerToQuestion) {
		this.alreadyAnswerToQuestion = AlreadyAnswerToQuestion;
	}

	public int getNumberMenu() {
		return numberMenu;
	}

	public void nextNumberMenu() {
		this.numberMenu = (numberMenu + 1) % 3;
	}

	public boolean isWantResetGame() {
		return wantResetGame;
	}

	public void setWantResetGame(boolean wantResetGame) {
		this.wantResetGame = wantResetGame;
	}

	public boolean isWantResetPoints() {
		return wantResetPoints;
	}

	public void setWantResetPoints(boolean wantResetPoints) {
		this.wantResetPoints = wantResetPoints;
	}

	public boolean isWantResetQuestions() {
		return wantResetQuestions;
	}

	public void setWantResetQuestions(boolean wantResetQuestions) {
		this.wantResetQuestions = wantResetQuestions;
	}

}
