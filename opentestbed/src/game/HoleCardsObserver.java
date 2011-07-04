package game;

import com.biotools.meerkat.Card;

/**
 * observer that will be informed about all holecards
 *
 */
public interface HoleCardsObserver {
	public void holeCards(Card c1, Card c2, int seat);
}
