package some.telegram.bot;

import java.util.ArrayList;
import java.util.List;

import some.telegram.bot.core.Question;
import some.telegram.bot.manager.ManagerUsersGame;

public class MasterUsersGame extends ManagerUsersGame {

	private boolean acceptNewMaster;
	private List<Question> listOfQuestion;

	public MasterUsersGame(boolean acceptNewMaster) {
		super();
		this.acceptNewMaster = acceptNewMaster;
		listOfQuestion = new ArrayList<Question>();
		for (int i = 1; i <= 20; i++) {
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
		question.setRightQuestion(settingRightQuestion);
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

}
