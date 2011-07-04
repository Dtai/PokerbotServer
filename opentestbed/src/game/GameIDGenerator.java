package game;

/**
 * This class generates (threadsafe) GameIDs <br>
 * There should be just one instance per VM
 *
 */
public class GameIDGenerator {
	private long currentGameID;

	/**
	 * @param startGameID id to start with - System.getNanoTime()
	 */
	public GameIDGenerator(long startGameID) {
		super();
		this.currentGameID = startGameID;
	}

	public synchronized long getNextGameID() {
		return currentGameID++;

	}
}
