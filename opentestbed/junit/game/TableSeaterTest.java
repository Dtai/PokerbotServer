package game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bots.BotRepository;

public class TableSeaterTest {
	@Test
	public void testSeatPermutations() {
		AbstractGameDescription testDescription = new MockGameDescription();
		testDescription.setBotNames(new String[] { "DemoBot/AlwaysCallBot", "DemoBot/AlwaysCallBot", "DemoBot/AlwaysCallBot", "DemoBot/AlwaysCallBot" });
		testDescription.setInGameNames(new String[] { "1", "2", "3", "4" });
		TableSeater seater = new TableSeater(new BotRepository(), true);
		PublicGameInfo[] gameInfos = seater.createTables(testDescription);

		// 4 permutations for 4 players
		assertEquals(4, gameInfos.length);
		testPermutation(gameInfos, 0, "1", "2", "3", "4");
		testPermutation(gameInfos, 1, "2", "4", "1", "3");
		testPermutation(gameInfos, 2, "3", "1", "4", "2");
		testPermutation(gameInfos, 3, "4", "3", "2", "1");
	}

	private void testPermutation(PublicGameInfo[] publicGameInfos, int permutation, String... botNames) {
		for (int seat = 0; seat < botNames.length; seat++) {
			assertEquals("testing seat #" + seat + " in permutation #" + permutation, botNames[seat], publicGameInfos[permutation].getPlayerName(seat));
		}
	}

	class MockGameDescription extends AbstractGameDescription {
		@Override
		public GameRunner createGameRunner() {
			return null;
		}
	}

}
