package game;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.biotools.meerkat.Action;
import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;

public class HandHistoryWriterTest {
	/**
	 * Tests against file 'full-tilt-hh1.txt'. This is a pretty standard file
	 */
	@Test
	public void testFTSampleHistory1() throws Exception {
		String exampleFile = IOUtils.toString(this.getClass().getResourceAsStream("./full-tilt-hh1.txt")).replace("\r", "");

		// setup games and players
		PublicGameInfo gameInfo = createSimpleGameInfo();

		gameInfo.setPlayer(0, PublicPlayerInfo.create("player1", 0.33, null));
		gameInfo.setPlayer(2, PublicPlayerInfo.create("player3", 0.42, null));
		gameInfo.setPlayer(4, PublicPlayerInfo.create("player5", 0.42, null));
		gameInfo.setPlayer(5, PublicPlayerInfo.create("player6", 0.85, null));
		gameInfo.setPlayer(6, PublicPlayerInfo.create("player7", 0.76, null));
		gameInfo.setPlayer(7, PublicPlayerInfo.create("player8", 0.40, null));
		gameInfo.setPlayer(8, PublicPlayerInfo.create("player9", 1.57, null));
		gameInfo.newHand(0, 2, 4);

		StringWriter handHistory = new StringWriter();
		HandHistoryWriter handHistoryWriter = new MockHandHistoryWriter();
		handHistoryWriter.setWriter(handHistory);

		// simulate events of a game - actually after each line a 
		// gamestatechange-event is also submitted, but the history writer
		// ignores this, so we make the code easier
		handHistoryWriter.gameStartEvent(gameInfo);
		handHistoryWriter.stageEvent(0); // preflop
		handHistoryWriter.actionEvent(2, Action.smallBlindAction(0.01));
		handHistoryWriter.actionEvent(4, Action.bigBlindAction(0.02));
		handHistoryWriter.dealHoleCardsEvent();
		handHistoryWriter.actionEvent(5, Action.foldAction(0.02));
		handHistoryWriter.actionEvent(6, Action.foldAction(0.02));
		handHistoryWriter.actionEvent(7, Action.foldAction(0.02));
		handHistoryWriter.actionEvent(8, Action.foldAction(0.02));
		handHistoryWriter.actionEvent(0, Action.callAction(0.02));
		handHistoryWriter.actionEvent(2, Action.foldAction(0.02));
		handHistoryWriter.actionEvent(4, Action.checkAction());
		gameInfo.nextStage(new Hand("Kd Kh 8h"));
		handHistoryWriter.stageEvent(1); // Kd Kh 8h
		handHistoryWriter.actionEvent(4, Action.checkAction());
		handHistoryWriter.actionEvent(0, Action.checkAction());
		gameInfo.nextStage(new Hand("7d"));
		handHistoryWriter.stageEvent(2); // Kd Kh 8h 7d
		handHistoryWriter.actionEvent(4, Action.checkAction());
		handHistoryWriter.actionEvent(0, Action.checkAction());
		gameInfo.nextStage(new Hand("7s"));
		handHistoryWriter.stageEvent(3); // Kd Kh 8h 7d 7s
		handHistoryWriter.actionEvent(4, Action.checkAction());
		handHistoryWriter.actionEvent(0, Action.checkAction());
		handHistoryWriter.showdownEvent(4, new Card("As"), new Card("8c"));
		handHistoryWriter.actionEvent(0, Action.muckAction());
		gameInfo.getPotManager().setPot(0, 0.05, new int[] { 0, 2, 4 });
		handHistoryWriter.winEvent(4, 0.05, "not important for test");
		handHistoryWriter.gameOverEvent();

		assertEquals(exampleFile, handHistory.toString());
	}

	/**
	 * Tests against file 'full-tilt-hh2.txt'. A side pot is built which one player gets.
	 */
	@Test
	public void testFTSampleHistory2() throws Exception {
		String exampleFile = IOUtils.toString(this.getClass().getResourceAsStream("./full-tilt-hh2.txt")).replace("\r", "");

		// setup games and players
		PublicGameInfo gameInfo = createSimpleGameInfo();

		gameInfo.setPlayer(3, PublicPlayerInfo.create("player4", 0.80, null));
		gameInfo.setPlayer(4, PublicPlayerInfo.create("player5", 0.80, null));
		gameInfo.setPlayer(6, PublicPlayerInfo.create("player7", 0.76, null));
		gameInfo.setPlayer(7, PublicPlayerInfo.create("player8", 0.94, null));
		gameInfo.newHand(4, 6, 7);

		StringWriter handHistory = new StringWriter();
		HandHistoryWriter handHistoryWriter = new MockHandHistoryWriter();
		handHistoryWriter.setWriter(handHistory);

		// simulate events of a game - actually after each line a 
		// gamestatechange-event is also submitted, but the history writer
		// ignores this, so we make the code easier
		handHistoryWriter.gameStartEvent(gameInfo);
		handHistoryWriter.stageEvent(0); // preflop
		handHistoryWriter.actionEvent(6, Action.smallBlindAction(0.01));
		handHistoryWriter.actionEvent(7, Action.bigBlindAction(0.02));
		handHistoryWriter.dealHoleCardsEvent();
		handHistoryWriter.actionEvent(3, Action.raiseAction(0.02, 0.02));
		handHistoryWriter.actionEvent(4, Action.callAction(0.04));
		handHistoryWriter.actionEvent(6, Action.raiseAction(0.03, 0.06));
		handHistoryWriter.actionEvent(7, Action.callAction(0.08));
		handHistoryWriter.actionEvent(3, Action.raiseAction(0.06, 0.70));
		handHistoryWriter.actionEvent(4, Action.callAction(0.76));
		handHistoryWriter.actionEvent(6, Action.callAction(0.66));
		handHistoryWriter.actionEvent(7, Action.foldAction(0.70));
		handHistoryWriter.showdownEvent(3, new Card("Ad"), new Card("As"));
		handHistoryWriter.showdownEvent(4, new Card("6c"), new Card("5c"));
		handHistoryWriter.showdownEvent(6, new Card("Kh"), new Card("Ac"));
		gameInfo.nextStage(new Hand("8s Qc Jc"));
		handHistoryWriter.stageEvent(1);
		gameInfo.nextStage(new Hand("Kd"));
		handHistoryWriter.stageEvent(2);
		gameInfo.nextStage(new Hand("3h"));
		handHistoryWriter.stageEvent(3);
		gameInfo.getPotManager().setPot(0, 2.23, new int[] { 6, 7, 3, 4 }); // reduced by rake
		gameInfo.getPotManager().setPot(1, 0.07, new int[] { 3, 4 });
		handHistoryWriter.winEvent(3, 0.07, "not important for test");
		handHistoryWriter.winEvent(3, 2.23, "not important for test");
		handHistoryWriter.gameOverEvent();

		assertEquals(exampleFile, handHistory.toString());

	}

	/**
	 * Tests against file 'full-tilt-hh3.txt'. A side pot is built and side+main pot are split among three players.
	 */
	@Test
	public void testFTSampleHistory3() throws Exception {
		String exampleFile = IOUtils.toString(this.getClass().getResourceAsStream("./full-tilt-hh3.txt")).replace("\r", "");

		// setup games and players
		PublicGameInfo gameInfo = createSimpleGameInfo();

		gameInfo.setPlayer(0, PublicPlayerInfo.create("player1", 2.49, null));
		gameInfo.setPlayer(1, PublicPlayerInfo.create("player2", 0.53, null));
		gameInfo.setPlayer(3, PublicPlayerInfo.create("player4", 3.23, null));
		gameInfo.setPlayer(4, PublicPlayerInfo.create("player5", 0.40, null));
		gameInfo.setPlayer(5, PublicPlayerInfo.create("player6", 0.11, null));
		gameInfo.newHand(3, 4, 5);

		StringWriter handHistory = new StringWriter();
		HandHistoryWriter handHistoryWriter = new MockHandHistoryWriter();
		handHistoryWriter.setWriter(handHistory);

		// simulate events of a game - actually after each line a 
		// gamestatechange-event is also submitted, but the history writer
		// ignores this, so we make the code easier
		handHistoryWriter.gameStartEvent(gameInfo);
		handHistoryWriter.stageEvent(0); // preflop
		handHistoryWriter.actionEvent(4, Action.smallBlindAction(0.01));
		handHistoryWriter.actionEvent(5, Action.bigBlindAction(0.02));
		handHistoryWriter.dealHoleCardsEvent();
		handHistoryWriter.actionEvent(0, Action.callAction(0.02));
		handHistoryWriter.actionEvent(1, Action.callAction(0.02));
		handHistoryWriter.actionEvent(3, Action.callAction(0.02));
		handHistoryWriter.actionEvent(4, Action.callAction(0.01));
		handHistoryWriter.actionEvent(5, Action.checkAction());
		gameInfo.nextStage(new Hand("8h 6s 5c"));
		handHistoryWriter.stageEvent(1);
		handHistoryWriter.actionEvent(4, Action.checkAction());
		handHistoryWriter.actionEvent(5, Action.checkAction());
		handHistoryWriter.actionEvent(0, Action.betAction(0.02));
		handHistoryWriter.actionEvent(1, Action.callAction(0.02));
		handHistoryWriter.actionEvent(3, Action.callAction(0.02));
		handHistoryWriter.actionEvent(4, Action.foldAction(0.02));
		handHistoryWriter.actionEvent(5, Action.callAction(0.02));

		gameInfo.nextStage(new Hand("9s"));
		handHistoryWriter.stageEvent(2);

		handHistoryWriter.actionEvent(5, Action.checkAction());
		handHistoryWriter.actionEvent(0, Action.betAction(0.18));
		handHistoryWriter.actionEvent(1, Action.foldAction(0.18));
		handHistoryWriter.actionEvent(3, Action.callAction(0.18));
		handHistoryWriter.actionEvent(5, Action.callAction(0.07));

		gameInfo.nextStage(new Hand("8d"));
		handHistoryWriter.stageEvent(3);

		handHistoryWriter.actionEvent(0, Action.betAction(0.61));
		handHistoryWriter.actionEvent(3, Action.callAction(0.61));

		gameInfo.getPotManager().setPot(0, 0.38, new int[] { 4, 5, 0, 1, 3 }); // reduced by rake
		gameInfo.getPotManager().setPot(0, 1.34, new int[] { 0, 3 });

		handHistoryWriter.showdownEvent(0, new Card("7c"), new Card("Ac"));
		handHistoryWriter.showdownEvent(3, new Card("7d"), new Card("8c"));
		handHistoryWriter.winEvent(0, 0.67, "not important for test");
		handHistoryWriter.winEvent(3, 0.67, "not important for test");
		handHistoryWriter.showdownEvent(5, new Card("Qh"), new Card("7s"));
		handHistoryWriter.winEvent(5, 0.13, "not important for test");
		handHistoryWriter.winEvent(0, 0.12, "not important for test");
		handHistoryWriter.winEvent(3, 0.12, "not important for test");

		handHistoryWriter.gameOverEvent();

		assertEquals(exampleFile, handHistory.toString());
	}

	private PublicGameInfo createSimpleGameInfo() {
		PublicGameInfo gameInfo = new PublicGameInfo();
		gameInfo.setGameID(1111);
		gameInfo.setBlinds(0.01, 0.02);
		gameInfo.setLimit(PublicGameInfo.NO_LIMIT);
		gameInfo.setNumSeats(9);
		return gameInfo;
	}

	/**
	 * overrides 'getGameTime()', otherwise the generated histories always have different times
	 */
	static class MockHandHistoryWriter extends HandHistoryWriter {
		@Override
		protected Date getGameTime() {
			return new GregorianCalendar(2000, 0, 1).getTime();
		}
	}
}
