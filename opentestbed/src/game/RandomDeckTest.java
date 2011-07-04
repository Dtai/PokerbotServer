package game;

import static org.junit.Assert.assertEquals;

import game.deck.RandomDeck;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.biotools.meerkat.Hand;

public class RandomDeckTest {
	@Test
	public void testUniqueCards() {
		Set<String> cards = new TreeSet<String>();
		RandomDeck deck = new RandomDeck();

		for (int i = 0; i < 5; i++) {
			cards.add(deck.getCommunityCard(i).toString());
		}
		for (int i = 0; i < 9; i++) {
			Hand playersCard = deck.getPlayerCards(i);
			assertEquals(2, playersCard.size());
			cards.add(playersCard.getFirstCard().toString());
			cards.add(playersCard.getSecondCard().toString());
		}
		// if there were no duplicates, we still have all community
		// and player cards in the set
		assertEquals(9 * 2 + 5, cards.size());

	}
}
