package game;

import game.deck.DeckFactory;
import game.stats.BankrollObserver;

import java.util.List;

import com.biotools.meerkat.GameObserver;

/**
 * A Gamerunner will run a full simulation 
 *
 */
public interface GameRunner {
	/**
	 * @param deckFactory factory for a deck. Each (possible) permutation of a game will retrieve a new Deck
	 * @param tableSeater
	 * @param gameIDGenerator
	 * @param gameObservers
	 */
	public void runGame(DeckFactory deckFactory, TableSeater tableSeater, GameIDGenerator gameIDGenerator, List<? extends GameObserver> gameObservers);

	public PublicGameInfo asyncRunGame(DeckFactory deckFactory, TableSeater tableSeater, final GameIDGenerator gameIDGenerator,
			List<? extends GameObserver> gameObservers);

	/**
	 * adds a bankrollObserver that will be informed about bankroll-changes
	 * @param bankrollgraph
	 */
	public void addBankrollObserver(BankrollObserver bankrollgraph);
}
