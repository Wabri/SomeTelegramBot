package some.telegram.bot.manager;

import java.util.ArrayList;
import java.util.List;

import some.telegram.bot.core.Question;
import some.telegram.bot.core.UserGame;

public class MasterUsersGame extends ManagerUsersGame {

	private boolean acceptNewMaster;
	private List<Question> listOfQuestion;

	public MasterUsersGame(boolean acceptNewMaster) {
		super();
		this.acceptNewMaster = acceptNewMaster;
		resetDefaultQuestion();
	}

	public void resetDefaultQuestion() {
		listOfQuestion = new ArrayList<Question>();
		for (int i = 1; i <= 30; i++) {
			listOfQuestion.add(new Question(i, "", 0));
		}
	}

	public boolean isAcceptNewMaster() {
		return acceptNewMaster;
	}

	public void setAcceptNewMaster(boolean acceptNewMaster) {
		this.acceptNewMaster = acceptNewMaster;
	}

	public void addNewQuestion(int settingQuestion, String settingRightQuestion, int points) {
		Question question = listOfQuestion.get(settingQuestion - 1);
		question.setRightAnswer(settingRightQuestion);
		question.setPoints(points);
	}

	public List<Question> getListOfQuestion() {
		return listOfQuestion;
	}

	public void setListOfQuestion(List<Question> listOfQuestion) {
		this.listOfQuestion = listOfQuestion;
	}

	public String getStringListOfQuestion() {
		String questionList = "";
		for (Question question : listOfQuestion) {
			questionList += question.limitedToString() + "\n\r";
		}
		return questionList;
	}

	public String getMasterInfoList() {
		String userPointList = "";
		for (UserGame userGame : getListOfUsers()) {
			userPointList += "\n\r " + userGame.getChat().getId() + " ";
			if (!(userGame.getUser().getUserName() == null)) {
				userPointList += userGame.getUser().getUserName();
			} else if (!(userGame.getUser().getFirstName() == null)) {
				if (!(userGame.getUser().getLastName() == null)) {
					userPointList += userGame.getUser().getFirstName() + " " + userGame.getUser().getLastName();
				} else {
					userPointList += userGame.getUser().getFirstName();
				}
			} else if (!(userGame.getUser().getLastName() == null)) {
				userPointList += userGame.getUser().getLastName();
			} else {
				userPointList += "NoNamePlayer";
			}
		}
		return userPointList;
	}

	public Question getQuestion(int i) {
		if (i >= 1 || i <= 30) {
			for (Question question : listOfQuestion) {
				if (question.getQuestion() == i) {
					return question;
				}
			}
		}
		return null;
	}

}
