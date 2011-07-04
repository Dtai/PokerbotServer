package game;

import game.deck.DeckFactory;
import game.stats.BankrollObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.Utils;

import com.biotools.meerkat.GameObserver;

/**
 * GameRunner running a CashGame<br>
 * Runs the given count of CashGames. In this implementation 'Doyles Game' is played, 
 * i.e. after each game the players always start again with their initial bankroll
 */
public class CashGameRunner implements GameRunner {

	private CashGameDescription cashGameDescription;
	private List<BankrollObserver> bankrollObservers = new ArrayList<BankrollObserver>();

	public CashGameRunner(CashGameDescription cashGameDescription) {
		super();
		this.cashGameDescription = cashGameDescription;
	}

	@Override
	public void runGame(DeckFactory deckFactory, TableSeater tableSeater, GameIDGenerator gameIDGenerator, List<? extends GameObserver> gameObservers) {
		PublicGameInfo gameInfos[] = tableSeater.createTables(cashGameDescription);
		notifyBankRollObserversGameStarted(gameInfos[0], gameInfos.length, cashGameDescription.getNumGames());
		for (int seatpermutation = 0; seatpermutation < gameInfos.length; seatpermutation++) {
			PublicGameInfo gameInfo = gameInfos[seatpermutation];
			for (GameObserver gameObserver : gameObservers) {
				gameInfo.addGameObserver(gameObserver);
			}

			gameInfo.setBlinds(cashGameDescription.getSmallBlind(), cashGameDescription.getBigBlind());
			gameInfo.setLimit(cashGameDescription.isNolimit() ? PublicGameInfo.NO_LIMIT : PublicGameInfo.FIXED_LIMIT);
			Dealer dealer = new Dealer(deckFactory.createDeck(), gameInfo);

			runGames(gameIDGenerator, gameInfo, seatpermutation, dealer);

		}
	}

	@Override
	public PublicGameInfo asyncRunGame(DeckFactory deckFactory, TableSeater tableSeater, final GameIDGenerator gameIDGenerator,
			List<? extends GameObserver> gameObservers) {
		PublicGameInfo gameInfos[] = tableSeater.createTables(cashGameDescription);
		if (gameInfos.length != 1) {
			throw new IllegalArgumentException("No permutations supported.");
		}
		final PublicGameInfo gameInfo = gameInfos[0];
		notifyBankRollObserversGameStarted(gameInfo, gameInfos.length, cashGameDescription.getNumGames());
		for (GameObserver gameObserver : gameObservers) {
			gameInfo.addGameObserver(gameObserver);
		}

		gameInfo.setBlinds(cashGameDescription.getSmallBlind(), cashGameDescription.getBigBlind());
		gameInfo.setLimit(cashGameDescription.isNolimit() ? PublicGameInfo.NO_LIMIT : PublicGameInfo.FIXED_LIMIT);
		final Dealer dealer = new Dealer(deckFactory.createDeck(), gameInfo);

		Runnable gamesRunner = new Runnable() {
			public void run() {
				runGames(gameIDGenerator, gameInfo, 0, dealer);
			}
		};
		(new Thread(gamesRunner, "AsyncRunGame")).start();
		return gameInfo;
	}

	private void runGames(GameIDGenerator gameIDGenerator, PublicGameInfo gameInfo, int seatpermutation, Dealer dealer) {
		for (int i = 0; i < cashGameDescription.getNumGames(); i++) {
			Map<String, Double> playerBeforeBankroll = rememberPlayerBankrolls(gameInfo);

			gameInfo.setGameID(gameIDGenerator.getNextGameID());
			dealer.playHand();

			notifyBankRollObserversBankrollDelta(gameInfo, seatpermutation, playerBeforeBankroll);

			checkPlayerRebuy(gameInfo);
			dealer.moveButton();
		}
	}

	/**
	 * notifies Bankroll-Observers that the game has started
	 * @param gameInfo
	 * @param numSeatPermutations
	 * @param numGames
	 */
	private void notifyBankRollObserversGameStarted(PublicGameInfo gameInfo, int numSeatPermutations, int numGames) {
		Set<String> playerNames = new HashSet<String>();
		for (int seat = 0; seat < gameInfo.getNumPlayers(); seat++) {
			PublicPlayerInfo player = gameInfo.getPlayer(seat);
			if (player != null) {
				playerNames.add(player.getName());
			}
		}

		for (BankrollObserver bankrollObserver : bankrollObservers) {
			bankrollObserver.gameStarted(numSeatPermutations, numGames, playerNames);

		}

	}

	/**
	 * calculates the delta of all bankrolls and notifies all observers
	 * @param gameInfo
	 * @param seatpermutation
	 * @param playersBeforeBankroll
	 */
	private void notifyBankRollObserversBankrollDelta(PublicGameInfo gameInfo, int seatpermutation, Map<String, Double> playersBeforeBankroll) {
		Map<String, Double> playerBankrollDelta = new HashMap<String, Double>();
		for (Map.Entry<String, Double> playerBeforeBankroll : playersBeforeBankroll.entrySet()) {
			String playerName = playerBeforeBankroll.getKey();
			double beforeBankroll = playerBeforeBankroll.getValue();
			double bankrollNow = gameInfo.getPlayer(playerName).getBankRoll();
			playerBankrollDelta.put(playerName, Utils.roundToCents(bankrollNow - beforeBankroll));
		}

		for (BankrollObserver bankrollObserver : bankrollObservers) {
			bankrollObserver.updateBankroll(seatpermutation, playerBankrollDelta);

		}

	}

	/**
	 * saves the bankrolls of all players to a map
	 * @param gameInfo
	 * @return
	 */
	private Map<String, Double> rememberPlayerBankrolls(PublicGameInfo gameInfo) {
		Map<String, Double> playerToBankRoll = new HashMap<String, Double>();
		for (int seat = 0; seat < gameInfo.getNumPlayers(); seat++) {
			PublicPlayerInfo player = gameInfo.getPlayer(seat);
			if (player != null) {
				playerToBankRoll.put(player.getName(), Double.valueOf(player.getBankRoll()));
			}
		}
		return playerToBankRoll;
	}

	private void checkPlayerRebuy(PublicGameInfo gameInfo) {
		for (int seat = 0; seat < gameInfo.getNumSeats(); seat++) {
			PublicPlayerInfo player = gameInfo.getPlayer(seat);
			if (player != null) {
				player.setBankroll(cashGameDescription.getInitialBankRoll());
			}
			if (player != null && player.isSittingOut()) {
				player.setSittingOut(false);
			}
		}
	}

	@Override
	public void addBankrollObserver(BankrollObserver bankrollgraph) {
		this.bankrollObservers.add(bankrollgraph);
	}
}
