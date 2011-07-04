package game.deck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;

/**
 * A SerializedDeck just replays Cards which were recorded in
 * advance.<br>
 * {@link #main(String[])} creates a simple 100.000 Games Deck (with communitcards and 10players)<br>
 * with {@link #serializeDeck(OutputStream, int, Deck)} one can create arbitrary new decks<br>
 * <br>
 * The format is quite easy:<br>
 * Each game starts with a marker byte (=0), followed by 5 community cards, followed by 10*2 player cards.
 * Cards are saved by their index.
 * 
 */
public class SerializedDeck implements Deck {

	private InputStream deckStream;
	// 5 community-cards + 10 playercards for each game
	private byte[] currentHand = new byte[5 + 2 * 10];
	private int numGames = 0;

	/**
	 * @return a factory for SerializedDecks, all starting with the same file
	 */
	public static DeckFactory createFactory(final String deckFilename) {
		return new DeckFactory() {
			@Override
			public Deck createDeck() {
				try {
					return new SerializedDeck(new FileInputStream(new File(deckFilename)));
				} catch (FileNotFoundException e) {
					throw new RuntimeException("Error creating SerializedDeck", e);
				}
			}
		};
	}

	/**
	 * @param deckStream a stream with cards created by {@link #serializeDeck(OutputStream, int, Deck)}
	 */
	public SerializedDeck(InputStream deckStream) {
		this.deckStream = deckStream;
	}

	@Override
	public Card getCommunityCard(int communityCardNumber) {
		return new Card(currentHand[communityCardNumber]);
	}

	@Override
	public Hand getPlayerCards(int seat) {
		Card c1 = new Card(currentHand[5 + seat * 2 + 0]);
		Card c2 = new Card(currentHand[5 + seat * 2 + 1]);
		Hand hand = new Hand();
		hand.addCard(c1);
		hand.addCard(c2);
		return hand;
	}

	@Override
	public void nextGame() {
		numGames++;
		try {
			int marker = deckStream.read();
			if (marker != 0) {
				throw new IllegalStateException("Error reading from deck-file after " + numGames + " games: marker is not '0'");
			}
			deckStream.read(currentHand);
		} catch (IOException e) {
			throw new IllegalStateException("Error reading from deck-file after " + numGames + " games", e);
		}
	}

	/**
	 * create a serialized Deck with the given number of cards.<br>
	 * @param outStream
	 * @param numCards
	 * @param deckGenerator another Deck to create the actual cards. Typically would be {@link RandomDeck}
	 * @throws IOException
	 */
	public static void serializeDeck(OutputStream outStream, int numCards, Deck deckGenerator) throws IOException {
		for (int i = 0; i < numCards; i++) {
			deckGenerator.nextGame();
			byte[] outCards = new byte[5 + 2 * 10];
			outCards[0] = (byte) deckGenerator.getCommunityCard(0).getIndex();
			outCards[1] = (byte) deckGenerator.getCommunityCard(1).getIndex();
			outCards[2] = (byte) deckGenerator.getCommunityCard(2).getIndex();
			outCards[3] = (byte) deckGenerator.getCommunityCard(3).getIndex();
			outCards[4] = (byte) deckGenerator.getCommunityCard(4).getIndex();
			for (int player = 0; player < 10; player++) {
				Hand playerHand = deckGenerator.getPlayerCards(player);
				outCards[5 + player * 2 + 0] = (byte) playerHand.getFirstCard().getIndex();
				outCards[5 + player * 2 + 1] = (byte) playerHand.getSecondCard().getIndex();
			}
			outStream.write(0); // marker
			outStream.write(outCards);
		}
	}

	/**
	 * writes a serialized deck with 100.000 games to ./data/decks
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		OutputStream out = new FileOutputStream("./data/decks/deck-100000.deck");
		serializeDeck(out, 100000, new RandomDeck());
		out.close();
	}
}
