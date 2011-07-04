package game;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;
import com.biotools.meerkat.HandEval;
import common.handeval.klaatu.PartialStageFastEval;

/**
 * Implementation of the Meerkat HandEval interface.
 * We delegate to Klaatus-FastEval who is the fastest known for random 
 * hands
 *
 */
public class HandEvalImpl implements HandEval {

	private int cardToKlaatuIndex(Card card) {
		return PartialStageFastEval.encode(card.getRank(), card.getSuit());
	}

	@Override
	public int rankHand(Hand hand) {
		switch (hand.size()) {
		case 5:
			return rankHand5(hand);
		case 6:
			return rankHand6(hand);
		case 7:
			return rankHand7(hand);
		default:
			throw new IllegalStateException("hand of size " + hand.size() + " not supported");
		}
	}

	@Override
	public int rankHand5(Hand hand) {
		return PartialStageFastEval.toBrecher5(PartialStageFastEval.eval5(cardToKlaatuIndex(hand.getCard(1)), cardToKlaatuIndex(hand.getCard(2)),
				cardToKlaatuIndex(hand.getCard(3)), cardToKlaatuIndex(hand.getCard(4)), cardToKlaatuIndex(hand.getCard(5))));
	}

	@Override
	public int rankHand6(Hand hand) {
		return PartialStageFastEval
				.toBrecher6(PartialStageFastEval.eval6(cardToKlaatuIndex(hand.getCard(1)), cardToKlaatuIndex(hand.getCard(2)),
						cardToKlaatuIndex(hand.getCard(3)), cardToKlaatuIndex(hand.getCard(4)), cardToKlaatuIndex(hand.getCard(5)),
						cardToKlaatuIndex(hand.getCard(6))));
	}

	@Override
	public int rankHand7(Hand hand) {
		return PartialStageFastEval.toBrecher7(PartialStageFastEval.eval7(cardToKlaatuIndex(hand.getCard(1)), cardToKlaatuIndex(hand.getCard(2)),
				cardToKlaatuIndex(hand.getCard(3)), cardToKlaatuIndex(hand.getCard(4)), cardToKlaatuIndex(hand.getCard(5)), cardToKlaatuIndex(hand.getCard(6)),
				cardToKlaatuIndex(hand.getCard(7))));
	}

}
