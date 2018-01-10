package some.telegram.bot.core;

public class Question {

	private int question;
	private String rightAnswer;
	private int points;

	public Question(int question, String rightQuestion, int points) {
		this.question = question;
		this.rightAnswer = rightQuestion;
		this.points = points;
	}

	public int getQuestion() {
		return question;
	}

	public void setQuestion(int question) {
		this.question = question;
	}

	public String getRightAnswer() {
		return rightAnswer;
	}

	public void setRightAnswer(String rightAnswer) {
		this.rightAnswer = rightAnswer;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	@Override
	public String toString() {
		return "Domanda = " + question + ", Risposta giusta = " + rightAnswer + ", Punti = " + points;
	}

	public String limitedToString() {
		return question + " -> " + rightAnswer + " -> " + points;
	}
}
