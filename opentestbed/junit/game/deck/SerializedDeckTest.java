package game.deck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class SerializedDeckTest {

	@Test
	public void testSerializeAndDeserialize() throws Exception {

		MockDeck mockDeck = new MockDeck(new String[] { "2c 3c 4c 5c 6c", "2s 3s 4s 5s 6s" }, new String[] {
				"2h 2h|3h 3h|4h 4h|5h 5h|6h 6h|7h 7h|8h 8h|9h 9h|Th Th|Jh Jh", "2d 2d|3d 3d|4d 4d|5d 5d|6d 6d|7d 7d|8d 8d|9d 9d|Td Td|Jd Jd" });

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		SerializedDeck.serializeDeck(byteArrayOutputStream, 2, mockDeck);

		SerializedDeck testDeck = new SerializedDeck(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
		testDeck.nextGame();
		assertEquals("3c", testDeck.getCommunityCard(1).toString());
		assertEquals("3h 3h", testDeck.getPlayerCards(1).toString());
		testDeck.nextGame();
		assertEquals("4s", testDeck.getCommunityCard(2).toString());
		assertEquals("4d 4d", testDeck.getPlayerCards(2).toString());

		try {
			testDeck.nextGame();
			fail("expected Exception not thrown");
		} catch (IllegalStateException e) {
			assertEquals("Error reading from deck-file after 3 games: marker is not '0'", e.getMessage());
		}
	}
}
