import game.CashGameDescription;
import game.GameIDGenerator;
import game.GameRunner;
import game.HandHistoryWriter;
import game.TableSeater;
import game.deck.DeckFactory;
import game.deck.SerializedDeck;
import game.stats.BankrollGraphUI;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import bots.BotRepository;


/**
 * This class starts a simulation on the console and writes a handhistory-file
 * to the ./data directory
 */
public class CashGameConsoleStarter {
	public static void main(String[] args) throws Exception {

		// number of games
		int numGames = 1000;
		// if to permute seats to reduce variance
		boolean permuteSeats = true;
		// four Bots fight against each other
		// valid BotNames can be obtained from the botRepository
		String[] botNames = new String[] { "DemoBot/SimpleBot", "DemoBot/AlwaysCallBot", "MCTSBot/MCTSBot", "DemoBot/SimpleBot" };

		BotRepository botRepository = new BotRepository();
		TableSeater tableSeater = new TableSeater(botRepository, permuteSeats);
		GameIDGenerator gameIDGenerator = new GameIDGenerator(System.nanoTime());
		HandHistoryWriter handHistoryWriter = new HandHistoryWriter();
		String simulationFileName = new SimpleDateFormat("yyMMdd-hhmm").format(new Date());
		handHistoryWriter.setWriter(new FileWriter("./data/" + simulationFileName + "-history.txt"));
		
		// in the future created via GUI, and persisted via XML to the ./data/games dir
		CashGameDescription cashGameDescription = new CashGameDescription();
		cashGameDescription.setSmallBlind(0.01);
		cashGameDescription.setBigBlind(0.02);
		cashGameDescription.setInitialBankRoll(2);
		cashGameDescription.setNumGames(numGames);

		cashGameDescription.setBotNames(botNames);
		//cashGameDescription.setInGameNames(new String[] { "Simply #1", "Simply #2", "Cally #3", "Cally #4" });

		// start the game
		GameRunner runner = cashGameDescription.createGameRunner();
		BankrollGraphUI bankrollgraphUI = new BankrollGraphUI();
		runner.addBankrollObserver(bankrollgraphUI);
		DeckFactory deckFactory = SerializedDeck.createFactory("./data/decks/deck-100000.deck");
		runner.runGame(deckFactory, tableSeater, gameIDGenerator, Arrays.asList(handHistoryWriter));

		bankrollgraphUI.createGraph(simulationFileName);
	}
}
