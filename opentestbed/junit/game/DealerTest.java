package game;

import static org.junit.Assert.assertEquals;
import game.deck.Deck;
import game.deck.MockDeck;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import bots.BotLoggingDecorator;
import bots.demobots.AlwaysCallBot;

import com.biotools.meerkat.Action;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Player;

/**
 * Tests the workings of the Dealer class
 *
 */
public class DealerTest {

	/**
	 * Tests that dealing a hand produces the right events
	 * to GameObservers/Players<br>
	 * The hand we play here was run in PokerAcademy, events that need
	 * to be generated were 'recorded' with a LoggingBot so we test
	 * for the same events and order
	 * @throws Exception
	 */
	@Test
	public void testGameEventsForPlayer() throws Exception {
		Deck deck = new MockDeck(new String[] { "Jh 9h 2h 3c 5h" }, new String[] { "As Ks|2s 8d|8h Qc" });
		PublicGameInfo gameInfo = new PublicGameInfo();
		gameInfo.setNumSeats(3);
		gameInfo.setPlayer(0, PublicPlayerInfo.create("player0", 200, new FirstRaiseBot()));
		gameInfo.setPlayer(1, PublicPlayerInfo.create("player1", 200, new AlwaysCallBot()));
		BotLoggingDecorator botLog = new BotLoggingDecorator(new AlwaysCallBot(), true);
		gameInfo.setPlayer(2, PublicPlayerInfo.create("player2", 200, botLog));
		gameInfo.setBlinds(0.05, 0.10);

		Dealer dealer = new Dealer(deck, gameInfo);
		dealer.playHand();

		String playerLog = IOUtils.toString(this.getClass().getResourceAsStream("./playerTestLog1.txt")).replace("\r", "");
		assertEquals(playerLog, botLog.getLog());
	}

	/**
	 * tests the win-amount - the last raiser doesn't 'win' his uncontested raise.<br>
	 * all player limp the blind (pot =0.3) but one raises additional $5.
	 * Nevertheless he just wins 0.3, and gets returned his raise<br>
	 * We need to be this exact to not confuse PokerTracker and HoldEm-Manager
	 * @throws Exception
	 */
	@Test
	public void testWinAmount() throws Exception {
		Deck deck = new MockDeck(new String[] { "Jh 9h 2h 3c 5h" }, new String[] { "As Ks|2s 8d|8h Qc" });
		PublicGameInfo gameInfo = new PublicGameInfo();
		gameInfo.setNumSeats(3);
		gameInfo.setBlinds(0.05, 0.10);

		// we use one player at all seats
		Player mockPlayer = new PrerecordedPlayerMock( //
				Action.callAction(0.10), // player2: call BB
				Action.raiseAction(0.05, 5), // player0 (SB): raise BB
				Action.foldAction(5.00), // player1 (BB): fold
				Action.foldAction(5.00) // player2 : fold
		);

		gameInfo.setPlayer(0, PublicPlayerInfo.create("player0 Dealer", 200, mockPlayer));
		gameInfo.setPlayer(1, PublicPlayerInfo.create("player1 SB", 200, mockPlayer));
		BotLoggingDecorator botLog = new BotLoggingDecorator(mockPlayer, false);
		gameInfo.setPlayer(2, PublicPlayerInfo.create("player2 BB", 200, botLog));

		Dealer dealer = new Dealer(deck, gameInfo);
		dealer.playHand();

		String playerLog = IOUtils.toString(this.getClass().getResourceAsStream("./playerTestLog_winAmount.txt")).replace("\r", "");
		assertEquals(playerLog, botLog.getLog());

		assertEquals(199.90, gameInfo.getPlayer(0).getBankRoll(), 0.001);
		assertEquals(200.20, gameInfo.getPlayer(1).getBankRoll(), 0.001);
		assertEquals(199.90, gameInfo.getPlayer(2).getBankRoll(), 0.001);
	}

	/**
	 * This hand starts preflop with calls and on the flop with a raise. All players
	 * fold, so the pot goes to the raise uncontested
	 * @throws Exception
	 */
	@Test
	public void testFoldedGameFlop() throws Exception {
		Deck deck = new MockDeck(new String[] { "Jh 9h 2h 3c 5h" }, new String[] { "As Ks|2s 8d|8h Qc" });
		PublicGameInfo gameInfo = new PublicGameInfo();
		gameInfo.setNumSeats(3);
		gameInfo.setBlinds(0.05, 0.10);

		// we use one player at all seats
		Player mockPlayer = new PrerecordedPlayerMock( //
				Action.callAction(0.10), // player2: call BB
				Action.callAction(0.05), // player0 (SB): call BB
				Action.checkAction(), // player1 (BB): check
										// -- flop --
				Action.betAction(0.10), // player0: bet
				Action.foldAction(0.10), // player1: fold
				Action.foldAction(0.10) // player2: fold
		);

		gameInfo.setPlayer(0, PublicPlayerInfo.create("player0", 200, mockPlayer));
		gameInfo.setPlayer(1, PublicPlayerInfo.create("player1", 200, mockPlayer));
		BotLoggingDecorator botLog = new BotLoggingDecorator(mockPlayer, false);
		gameInfo.setPlayer(2, PublicPlayerInfo.create("player2", 200, botLog));

		Dealer dealer = new Dealer(deck, gameInfo);
		dealer.playHand();

		String playerLog = IOUtils.toString(this.getClass().getResourceAsStream("./playerTestLog_FoldedFlopGame.txt")).replace("\r", "");
		assertEquals(playerLog, botLog.getLog());
	}

	/**
	 * This hand starts preflop with folds.<br>
	 * Thus the bigblind doesn't need to act and wins
	 * @throws Exception
	 */
	@Test
	public void testFoldedGamePreFlop() throws Exception {
		Deck deck = new MockDeck(new String[] { "Jh 9h 2h 3c 5h" }, new String[] { "As Ks|2s 8d|8h Qc" });
		PublicGameInfo gameInfo = new PublicGameInfo();
		gameInfo.setNumSeats(3);
		gameInfo.setBlinds(0.05, 0.10);

		// we use one player at all seats
		Player mockPlayer = new PrerecordedPlayerMock( //
				Action.foldAction(0.10), // player2: fold
				Action.foldAction(0.05) // player0 (SB): fold
		);

		gameInfo.setPlayer(0, PublicPlayerInfo.create("player0", 200, mockPlayer));
		gameInfo.setPlayer(1, PublicPlayerInfo.create("player1", 200, mockPlayer));
		BotLoggingDecorator botLog = new BotLoggingDecorator(mockPlayer, false);
		gameInfo.setPlayer(2, PublicPlayerInfo.create("player2", 200, botLog));

		Dealer dealer = new Dealer(deck, gameInfo);
		dealer.playHand();

		String playerLog = IOUtils.toString(this.getClass().getResourceAsStream("./playerTestLog_FoldedPreFlopGame.txt")).replace("\r", "");
		assertEquals(playerLog, botLog.getLog());
	}

	/**
	 * This hand starts preflop with calls and on the flop with an all-in. 
	 * One player calls.<br>
	 * The players must now reveal cards, the game progresses to showdown (
	 * i.e. turn + river cards are revealed).<br>
	 * The hand we play here was run in PokerAcademy, events that need
	 * to be generated were 'recorded' with a LoggingBot so we test
	 * for the same events and order
	 * @throws Exception
	 */
	@Test
	public void testAllInGame1() throws Exception {
		Deck deck = new MockDeck(new String[] { "Jh 9h 2h 3c 5h" }, new String[] { "As Ks|2s 8d|8h Qc" });
		PublicGameInfo gameInfo = new PublicGameInfo();
		gameInfo.setNumSeats(3);
		gameInfo.setBlinds(0.05, 0.10);

		// we use one player at all seats
		Player mockPlayer = new PrerecordedPlayerMock( //
				Action.callAction(0.10), // player2: call BB
				Action.callAction(0.05), // player0 (SB): call BB
				Action.checkAction(), // player1 (BB): check
				// -- flop --
				Action.betAction(0.90), // player0: all-in bet
				Action.callAction(0.90), // player1: all-in call
				Action.foldAction(0.90) // player2: fold
		);

		gameInfo.setPlayer(0, PublicPlayerInfo.create("player0", 1, mockPlayer));
		gameInfo.setPlayer(1, PublicPlayerInfo.create("player1", 1, mockPlayer));
		BotLoggingDecorator botLog = new BotLoggingDecorator(mockPlayer, false);
		gameInfo.setPlayer(2, PublicPlayerInfo.create("player2", 1, botLog));

		Dealer dealer = new Dealer(deck, gameInfo);
		dealer.playHand();

		String playerLog = IOUtils.toString(this.getClass().getResourceAsStream("./playerTestLog_AllInGame1.txt")).replace("\r", "");
		assertEquals(playerLog, botLog.getLog());
	}

	/**
	 * like {@link #testAllInGame1()} but other order of action.<br>
	 * Here the first aggressor raises more than the second players bankroll.<br>
	 * Second player just calls and a splitpot is created - this needs to cleared
	 * instantly (uncalled bet returned to aggressor) and both players have to show
	 * their cards before river. 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllInGame2() throws Exception {
		Deck deck = new MockDeck(new String[] { "2d 3d Td 6h Ac" }, new String[] { "As Ks|2s 8d|8h Qc|2c 7c" });
		PublicGameInfo gameInfo = new PublicGameInfo();
		gameInfo.setNumSeats(4);
		gameInfo.setBlinds(0.01, 0.02);

		// we use one player at all seats
		Player mockPlayer = new PrerecordedPlayerMock( //
				Action.callAction(0.02), // player2: call BB
				Action.foldAction(0.02), // player3: fold
				Action.callAction(0.01), // player0 (SB): call BB
				Action.checkAction(), // player1 (BB): check
				// -- flop --
				Action.betAction(0.02), // player0: bet
				Action.foldAction(0.02), // player1: fold
				Action.raiseAction(0.02, 0.03), // player2: raise
				Action.raiseAction(0.03, 0.15), // player0: raise 
				Action.raiseAction(0.15, 0.83), // player2: raise
				Action.callAction(0.83), // player0: call
				// -- turn --
				Action.checkAction(), // player0: check
				Action.betAction(0.71), // player2: bet
				Action.callAction(0.19) // player0: call (all-in)
		);

		gameInfo.setPlayer(0, PublicPlayerInfo.create("player0 (SB)", 1.24, mockPlayer));
		gameInfo.setPlayer(1, PublicPlayerInfo.create("player1 (BB)", 1.72, mockPlayer));
		BotLoggingDecorator botLog = new BotLoggingDecorator(mockPlayer, false);
		gameInfo.setPlayer(2, PublicPlayerInfo.create("player2", 4.88, botLog));
		gameInfo.setPlayer(3, PublicPlayerInfo.create("player3", 20.88, mockPlayer));

		Dealer dealer = new Dealer(deck, gameInfo);
		dealer.moveButton();
		dealer.moveButton();
		dealer.moveButton();
		dealer.playHand();

		String playerLog = IOUtils.toString(this.getClass().getResourceAsStream("./playerTestLog_AllInGame2.txt")).replace("\r", "");
		assertEquals(playerLog, botLog.getLog());
	}

	/**
	 * test for tied pots with even money in the end  
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTiedPotEvenMoney() throws Exception {
		Deck deck = new MockDeck(new String[] { "4d 2d 7h 5h Kh" }, new String[] { "2c 7s|As 7d|Ah 7c" });
		PublicGameInfo gameInfo = new PublicGameInfo();
		gameInfo.setNumSeats(4);
		gameInfo.setBlinds(0.01, 0.02);

		// we use one player at all seats
		Player mockPlayer = new PrerecordedPlayerMock( //
				Action.foldAction(0.02), // player0: fold
				Action.callAction(0.01), // player1 (SB): call
				Action.checkAction(), // player2 (BB): check
				// -- flop --
				Action.betAction(0.02), // player1: bet
				Action.raiseAction(0.02, 9.74), // player2: raise (all-in)
				Action.callAction(9.74) // player1: call
		);

		gameInfo.setPlayer(0, PublicPlayerInfo.create("player0", 38.35, mockPlayer));
		gameInfo.setPlayer(1, PublicPlayerInfo.create("player1 (SB)", 10.46, mockPlayer));
		BotLoggingDecorator botLog = new BotLoggingDecorator(mockPlayer, false);
		gameInfo.setPlayer(2, PublicPlayerInfo.create("player2 (BB)", 9.78, botLog));

		Dealer dealer = new Dealer(deck, gameInfo);
		dealer.playHand();

		String playerLog = IOUtils.toString(this.getClass().getResourceAsStream("./playerTestLog_tiedPotEvenMoney.txt")).replace("\r", "");
		assertEquals(playerLog, botLog.getLog());
	}

	/**
	 * test for tied pots with uneven money in the end.<br>
	 * Some players get a cent more than others  
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTiedPotUnevenMoney() throws Exception {
		Deck deck = new MockDeck(new String[] { "4d 2d 7h 5h Kh" }, new String[] { "2c 7s|As 7d|Ah 7c" });
		PublicGameInfo gameInfo = new PublicGameInfo();
		gameInfo.setNumSeats(4);
		gameInfo.setBlinds(0.01, 0.02);

		// we use one player at all seats
		Player mockPlayer = new PrerecordedPlayerMock( //
				Action.raiseAction(0.02, 0.05), // player0: raise
				Action.callAction(0.06), // player1 (SB): call
				Action.callAction(0.05), // player2 (BB): check
				// -- flop --
				Action.betAction(0.02), // player1: bet
				Action.raiseAction(0.02, 9.69), // player2: raise (all-in)
				Action.foldAction(9.71), // player0: fold
				Action.callAction(9.69) // player1: call
		);

		gameInfo.setPlayer(0, PublicPlayerInfo.create("player0 ", 38.35, mockPlayer));
		gameInfo.setPlayer(1, PublicPlayerInfo.create("player1 (SB)", 10.46, mockPlayer));
		BotLoggingDecorator botLog = new BotLoggingDecorator(mockPlayer, false);
		gameInfo.setPlayer(2, PublicPlayerInfo.create("player2 (BB)", 9.78, botLog));

		Dealer dealer = new Dealer(deck, gameInfo);
		dealer.playHand();

		String playerLog = IOUtils.toString(this.getClass().getResourceAsStream("./playerTestLog_tiedPotUnevenMoney.txt")).replace("\r", "");
		assertEquals(playerLog, botLog.getLog());
	}

	/**
	 * tests that the bigblind is able to bet/raise correctly preflop.<br>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBigBlindRaise() throws Exception {
		Deck deck = new MockDeck(new String[] { "4d 2d 7h 5h Kh" }, new String[] { "2c 7s|As 7d|Ah 7c" });
		PublicGameInfo gameInfo = new PublicGameInfo();
		gameInfo.setNumSeats(3);
		gameInfo.setBlinds(0.01, 0.02);

		// we use one player at all seats
		Player mockPlayer = new PrerecordedPlayerMock( //
				Action.foldAction(0.02), // player0: fold
				Action.callAction(0.01), // player1 (SB): call
				Action.betAction(0.05), // player2 (BB): bet/raise
				Action.callAction(0.05), // player1 (SB): call
				// -- flop --
				Action.betAction(0.10), // player1: bet
				Action.foldAction(0.10) // player2: fold
		);

		gameInfo.setPlayer(0, PublicPlayerInfo.create("player0", 38.35, mockPlayer));
		gameInfo.setPlayer(1, PublicPlayerInfo.create("player1 (SB)", 10.46, mockPlayer));
		BotLoggingDecorator botLog = new BotLoggingDecorator(mockPlayer, false);
		gameInfo.setPlayer(2, PublicPlayerInfo.create("player2 (BB)", 9.78, botLog));

		Dealer dealer = new Dealer(deck, gameInfo);
		dealer.playHand();

		String playerLog = IOUtils.toString(this.getClass().getResourceAsStream("./playerTestLog_bigBlindRaise.txt")).replace("\r", "");
		assertEquals(playerLog, botLog.getLog());
	}

	/**
	 * simple Testbot, whose first action is to raise
	 *
	 */
	class FirstRaiseBot extends AlwaysCallBot {
		private int counter = 0;
		private GameInfo gameInfo;

		@Override
		public void gameStartEvent(GameInfo gInfo) {
			this.gameInfo = gInfo;
			super.gameStartEvent(gInfo);
		}

		@Override
		public Action getAction() {
			if (counter++ == 0) {
				return Action.raiseAction(gameInfo, 0.1);
			}
			return super.getAction();
		}
	}

}
