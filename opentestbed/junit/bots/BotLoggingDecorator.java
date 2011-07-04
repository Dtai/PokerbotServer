package bots;

import com.biotools.meerkat.Action;
import com.biotools.meerkat.Card;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Player;
import com.biotools.meerkat.util.Preferences;

/**
 * A simple decorator, that writes all events into a log.<br>
 * Used for testing, if our API sends all events correctly
 * 
 */
public class BotLoggingDecorator implements Player {

	private Player delegate;
	private int ourSeat; // our seat for the current hand
	private GameInfo gi; // general game information
	private Preferences prefs; // the configuration options for this bot
	private StringBuffer log = new StringBuffer();
	private boolean logPlayerEvents;

	/**
	 * @param delegate
	 * @param logPlayerEvents true, if all player events ({@link #getAction(), #holeCards(Card, Card, int)} should be logged.
	 * if false only events that a normal observer would see are logged
	 */
	public BotLoggingDecorator(Player delegate, boolean logPlayerEvents) {
		this.delegate = delegate;
		this.logPlayerEvents = logPlayerEvents;
	}

	public String getLog() {
		return log.toString();

	}

	private void log(String msg) {
		log.append(msg).append("\n");
	}

	/**
	 * An event called to tell us our hole cards and seat number
	 * 
	 * @param c1
	 *            your first hole card
	 * @param c2
	 *            your second hole card
	 * @param seat
	 *            your seat number at the table
	 */
	public void holeCards(Card c1, Card c2, int seat) {
		delegate.holeCards(c1, c2, seat);
		if (logPlayerEvents) {
			log("#holeCards: " + c1 + c2 + " seat:" + seat);
		}
		this.ourSeat = seat;
	}

	/**
	 * Requests an Action from the player Called when it is the Player's turn to act.
	 */
	public Action getAction() {
		if (logPlayerEvents) {
			log("#getAction, amountToCall" + gi.getAmountToCall(ourSeat));
		}
		return delegate.getAction();
	}

	/**
	 * Get the current settings for this bot.
	 */
	public Preferences getPreferences() {
		return prefs;
	}

	/**
	 * Load the current settings for this bot.
	 */
	public void init(Preferences playerPrefs) {
		if (logPlayerEvents) {
			log("#init");
		}
		this.prefs = playerPrefs;
		delegate.init(playerPrefs);
	}

	/**
	 * A new betting round has started.
	 */
	public void stageEvent(int stage) {
		log("#stageEvent " + stage + ", board " + gi.getBoard());
		delegate.stageEvent(stage);
	}

	/**
	 * A showdown has occurred.
	 * 
	 * @param pos
	 *            the position of the player showing
	 * @param c1
	 *            the first hole card shown
	 * @param c2
	 *            the second hole card shown
	 */
	public void showdownEvent(int seat, Card c1, Card c2) {
		log("#showdownEvent: " + c1 + c2 + " seat:" + seat);
		delegate.showdownEvent(seat, c1, c2);
	}

	/**
	 * A new game has been started.
	 * 
	 * @param gi
	 *            the game stat information
	 */
	public void gameStartEvent(GameInfo gInfo) {
		log("#gameStartEvent");
		this.gi = gInfo;
		delegate.gameStartEvent(gInfo);
	}

	/**
	 * An event sent when all players are being dealt their hole cards
	 */
	public void dealHoleCardsEvent() {
		log("#dealHoleCardsEvent");
		delegate.dealHoleCardsEvent();
	}

	/**
	 * An action has been observed.
	 */
	public void actionEvent(int pos, Action act) {
		log("#actionEvent " + pos + " action:" + act.getType() + " (toCall: " + act.getToCall() + ", amount:" + act.getAmount() + ")");
		delegate.actionEvent(pos, act);
	}

	/**
	 * The game info state has been updated Called after an action event has been fully processed
	 */
	public void gameStateChanged() {
		log("#gameStateChanged");
		delegate.gameStateChanged();
	}

	/**
	 * The hand is now over.
	 */
	public void gameOverEvent() {
		log("#gameOverEvent");
		delegate.gameOverEvent();
	}

	/**
	 * A player at pos has won amount with the hand handName
	 */
	public void winEvent(int pos, double amount, String handName) {
		log("#winEvent " + pos + ",  amount " + amount + ", hand " + handName);
		delegate.winEvent(pos, amount, handName);
	}
}