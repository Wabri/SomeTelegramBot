package some.telegram.bot.core;

public class Question {

	private int question;
	private String rightQuestion;
	private int points;

	public Question(int question, String rightQuestion, int points) {
		this.question = question;
		this.rightQuestion = rightQuestion;
		this.points = points;
	}

	public int getQuestion() {
		return question;
	}

	public void setQuestion(int question) {
		this.question = question;
	}

	public String getRightQuestion() {
		return rightQuestion;
	}

	public void setRightQuestion(String rightQuestion) {
		this.rightQuestion = rightQuestion;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	@Override
	public String toString() {
		return "Domanda = " + question + ", Risposta giusta = " + rightQuestion + ", Punti = " + points;
	}

	public String limitedToString() {
		return question + " -> " + rightQuestion + " -> " + points;
	}
}
