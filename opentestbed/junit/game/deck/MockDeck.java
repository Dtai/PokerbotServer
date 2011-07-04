package game.deck;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;

/**
 * A testdeck that can be preconfigured with certain hands and
 * communityCards
 */
public class MockDeck implements Deck {

	private String[] communityCards;
	private String[] playerCards;
	private int index = -1;

	public MockDeck(String communityCards[], String[] playerCards) {
		this.communityCards = communityCards;
		this.playerCards = playerCards;
	}

	@Override
	public Card getCommunityCard(int communityCardNumber) {
		Hand community = new Hand(communityCards[index]);
		return community.getCard(communityCardNumber + 1);
	}

	@Override
	public Hand getPlayerCards(int seat) {
		String[] singleCards = playerCards[index].split("\\|");
		Hand playerHand = new Hand(singleCards[seat]);
		return playerHand;
	}

	@Override
	public void nextGame() {
		index++;
	}

}
