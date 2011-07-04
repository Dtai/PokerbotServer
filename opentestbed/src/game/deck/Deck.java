package game.deck;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;

/**
 * Interface representing a Deck of cards.<br>
 * A deck can be prepared for the next game (a.k.a. as shuffled) and the cards for each seat
 * can be retrieved.<br>
 * This interface is written so that we can also have serialized Decks which give always the
 * same cards so that games can be replayed with changed seats but same cards to reduce variance
 *
 */
public interface Deck {
	/**
	 * prepares the Deck for the next game. Community Cards and Player-Cards get shuffled
	 */
	public void nextGame();

	/**
	 * retrieves the community cards. As long as {@link #nextGame()} hasn't been called these are
	 * always the same cards.
	 * @param communityCardNumber 0-4
	 * @return
	 */
	public Card getCommunityCard(int communityCardNumber);

	/**
	 * retrieves the cards for a certain seat. As long as {@link #nextGame()} hasn't been called these are
	 * always the same cards.<br>
	 * It's important that in a serialized Deck the cards for each seat remain constant. In a replayed tourney
	 * players might bust at different times - nevertheless should the other remaining players always get
	 * the same cards for a certain seats.
	 * 
	 * @param seat typically in the range of 0-8
	 */
	public Hand getPlayerCards(int seat);
}
