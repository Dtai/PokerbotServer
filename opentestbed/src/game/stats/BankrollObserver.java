package game.stats;

import java.util.Map;
import java.util.Set;

/**
 * a bankroll observer watches for changes in the (money-) bankroll
 * of the player.<br>
 * For a cash-game it is called after each hand, for a tournament only
 * when the game is over (as only then the money-bankroll is touched)
 *
 */
public interface BankrollObserver {

	/**
	 * called, when a game is started. Important infos, like 
	 * games to player, playerNames and seatPermutations are given
	 * @param numSeatPermutations
	 * @param numGames (without permutations)
	 * @param playerNames
	 */
	public void gameStarted(int numSeatPermutations, int numGames, Set<String> playerNames);

	/**
	 * @param seatpermutation if game are replayed with permuted seats, 
	 * this is the number of the permutation. Observers thus can 
	 * group bankrollevents of the same hands, that were just played
	 * with permuted seats
	 * @param playerDelta delta to the bankroll Map<PlayerName/BankrollDelta>
	 */
	public void updateBankroll(int seatpermutation, Map<String, Double> playerDelta);

}
