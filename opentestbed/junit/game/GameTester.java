package game;

import game.deck.Deck;
import game.deck.RandomDeck;

import java.io.OutputStreamWriter;

import bots.demobots.AlwaysCallBot;

/**
 * simple class for debugging where we don't have a test yet
 *
 */
public class GameTester {
	public static void main(String[] args) {
		Deck deck = new RandomDeck();
		PublicGameInfo gameInfo = new PublicGameInfo();
		gameInfo.setNumSeats(5);
		gameInfo.setPlayer(0, PublicPlayerInfo.create("player1", 200, new AlwaysCallBot()));
		gameInfo.setPlayer(2, PublicPlayerInfo.create("player2", 200, new AlwaysCallBot()));
		gameInfo.setPlayer(4, PublicPlayerInfo.create("player3", 200, new AlwaysCallBot()));
		gameInfo.setBlinds(1, 2);
		HandHistoryWriter hhWriter = new HandHistoryWriter();
		hhWriter.setWriter(new OutputStreamWriter(System.out));
		gameInfo.addGameObserver(hhWriter);

		Dealer dealer = new Dealer(deck, gameInfo);
		dealer.playHand();
		dealer.playHand();

	}

}
