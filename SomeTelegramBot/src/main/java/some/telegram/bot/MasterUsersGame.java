package some.telegram.bot;

import some.telegram.bot.manager.ManagerUsersGame;

public class MasterUsersGame extends ManagerUsersGame {

	private boolean acceptNewMaster;

	public MasterUsersGame(boolean acceptNewMaster) {
		super();
		this.acceptNewMaster = acceptNewMaster;
	}

	public boolean isAcceptNewMaster() {
		return acceptNewMaster;
	}

	public void setAcceptNewMaster(boolean acceptNewMaster) {
		this.acceptNewMaster = acceptNewMaster;
	}

}
