package bots.demobots;

import javax.swing.JPanel;

import com.biotools.meerkat.Action;
import com.biotools.meerkat.Card;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Player;
import com.biotools.meerkat.util.Preferences;

/** 
 * A simple bot that always calls
 */
public class AlwaysCallBot implements Player {
	private int ourSeat; // our seat for the current hand
	private GameInfo gi; // general game information
	private Preferences prefs;

	public AlwaysCallBot() {
	}

	/**
	 * An event called to tell us our hole cards and seat number
	 * @param c1 your first hole card
	 * @param c2 your second hole card
	 * @param seat your seat number at the table
	 */
	public void holeCards(Card c1, Card c2, int seat) {
		this.ourSeat = seat;
	}

	/**
	 * Requests an Action from the player
	 * Called when it is the Player's turn to act.
	 */
	public Action getAction() {
		double toCall = gi.getAmountToCall(ourSeat);
		if (toCall == 0.0D) {
			return Action.checkAction();
		}
		return Action.callAction(toCall);
	}

	/**
	 * If you implement the getSettingsPanel() method, your bot will display
	 * the panel in the Opponent Settings Dialog.
	 * @return a GUI for configuring your bot (optional)
	 */
	public JPanel getSettingsPanel() {
		return null;
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
		this.prefs = playerPrefs;
	}

	/**
	 * A new betting round has started.
	 */
	public void stageEvent(int stage) {
	}

	/**
	 * A showdown has occurred.
	 * @param pos the position of the player showing
	 * @param c1 the first hole card shown
	 * @param c2 the second hole card shown
	 */
	public void showdownEvent(int seat, Card c1, Card c2) {
	}

	/**
	 * A new game has been started.
	 * @param gi the game stat information
	 */
	public void gameStartEvent(GameInfo gInfo) {
		this.gi = gInfo;
	}

	/**
	 * An event sent when all players are being dealt their hole cards
	 */
	public void dealHoleCardsEvent() {
	}

	/**
	 * An action has been observed. 
	 */
	public void actionEvent(int pos, Action act) {
	}

	/**
	 * The game info state has been updated
	 * Called after an action event has been fully processed
	 */
	public void gameStateChanged() {
	}

	/**
	 * The hand is now over. 
	 */
	public void gameOverEvent() {
	}

	/**
	 * A player at pos has won amount with the hand handName
	 */
	public void winEvent(int pos, double amount, String handName) {
	}

}