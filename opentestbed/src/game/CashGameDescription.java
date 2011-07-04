package game;

/**
 * GameDescription for CashGames<br>
 *
 */
public class CashGameDescription extends AbstractGameDescription {
	/** number of games to run in a simulation */
	private int numGames;
	private double smallBlind;
	private double bigBlind;

	public GameRunner createGameRunner() {
		return new CashGameRunner(this);
	}

	public int getNumGames() {
		return numGames;
	}

	public void setNumGames(int numGames) {
		this.numGames = numGames;
	}

	public double getSmallBlind() {
		return smallBlind;
	}

	public void setSmallBlind(double smallBlind) {
		this.smallBlind = smallBlind;
	}

	public double getBigBlind() {
		return bigBlind;
	}

	public void setBigBlind(double bigBlind) {
		this.bigBlind = bigBlind;
	}

}
