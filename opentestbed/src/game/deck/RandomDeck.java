package game.deck;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;

/**
 * a true random deck
 */
public class RandomDeck implements Deck {
	private SecureRandom random = new SecureRandom();
	private List<Card> unshuffledDeck = new ArrayList<Card>();

	/**
	 * @return a factory for RandomDecks
	 */
	public static DeckFactory createFactory() {
		return new DeckFactory() {
			@Override
			public Deck createDeck() {
				return new RandomDeck();
			}
		};
	}

	/**
	 * we take the first 5 cards as community cards, all the following cards are
	 * for the players
	 */
	private List<Card> shuffledDeck = new ArrayList<Card>();

	public RandomDeck() {
		for (int i = 0; i < 52; i++) {
			unshuffledDeck.add(new Card(i));
		}
		unshuffledDeck = Collections.unmodifiableList(unshuffledDeck);
		nextGame();
	}

	@Override
	public Card getCommunityCard(int communityCardNumber) {
		if (communityCardNumber < 0 || communityCardNumber > 4) {
			throw new IllegalStateException("communitycardnumber not in rang 0-4");
		}
		return shuffledDeck.get(communityCardNumber);
	}

	@Override
	public Hand getPlayerCards(int seat) {
		Hand hand = new Hand();
		// skip community-cards
		int skip = 5;
		hand.addCard(shuffledDeck.get(seat * 2 + skip + 0));
		hand.addCard(shuffledDeck.get(seat * 2 + skip + 1));
		return hand;
	}

	@Override
	public void nextGame() {
		shuffledDeck = new ArrayList<Card>(unshuffledDeck);
		Collections.shuffle(shuffledDeck, random);
	}

}
