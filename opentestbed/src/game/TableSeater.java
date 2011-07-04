package game;

import bots.BotRepository;

import com.biotools.meerkat.Player;

/**
 * A tableseater puts bots on a table. As this is the same for 
 * tournaments and cashgames it has been extraced to this class.<br>
 * Furthermore this class (in the future) will be able to create
 * permutations of the seats to reduce variance in the simulations.
 *  
 *
 */
public class TableSeater {
	private BotRepository botRepository;
	/**
	 * if seats should be permuted. Currently only works for 2,3,4 and 6 seats 
	 * to be unbiased. 
	 */
	private boolean permuteSeats;

	/**
	 * 
	 * @param botRepository
	 * @param permuteSeats only works for even seat-counts
	 */
	public TableSeater(BotRepository botRepository, boolean permuteSeats) {
		super();
		this.botRepository = botRepository;
		this.permuteSeats = permuteSeats;
	}

	/**
	 * creates Tables (a.k.a {@link PublicGameInfo}s with the players/bots according to
	 * a gameDescription. <br>
	 * {@link PublicPlayerInfo}s will get their initial bankroll, name and bot assigned
	 * @param gameDescription
	 * @param permuteSeats
	 * @return an array of PublicGameInfos with the bots seated around the table.
	 * Currently just one entry is returned, but in the future we will generate
	 * permutation of the seating order
	 */
	public PublicGameInfo[] createTables(AbstractGameDescription gameDescription) {
		int[][] seatPermutations = createSeatPermutations(gameDescription);
		PublicGameInfo[] createdGameInfos = new PublicGameInfo[seatPermutations.length];

		for (int gamePermutation = 0; gamePermutation < seatPermutations.length; gamePermutation++) {
			PublicGameInfo publicGameInfo = new PublicGameInfo();
			createdGameInfos[gamePermutation] = publicGameInfo;

			publicGameInfo.setNumSeats(gameDescription.getNumSeats());

			for (int seat = 0; seat < gameDescription.getNumSeats(); seat++) {
				if (gameDescription.getBotNames()[seat] != null) {
					int targetBot = seatPermutations[gamePermutation][seat];

					Player bot = botRepository.createBot(gameDescription.getBotNames()[targetBot]);
					if (bot instanceof NamedPlayer) {
						((NamedPlayer) bot).setIngameName(gameDescription.getInGameNames()[targetBot]);
					}
					PublicPlayerInfo playerInfo = new PublicPlayerInfo();
					playerInfo.setBankroll(gameDescription.getInitialBankRoll());
					playerInfo.setBot(bot);
					playerInfo.setName(gameDescription.getInGameNames()[targetBot]);
					publicGameInfo.setPlayer(seat, playerInfo);
				}
			}

		}

		return createdGameInfos;
	}

	private int[][] createSeatPermutations(AbstractGameDescription gameDescription) {
		int numSeats = gameDescription.getBotNames().length;

		if (!permuteSeats) {
			int[][] seatPermutations = new int[1][numSeats];
			for (int seat = 0; seat < numSeats; seat++) {
				seatPermutations[0][seat] = seat;
			}
			return seatPermutations;

		}

		// now for all the permutations
		if (numSeats == 2) {
			return new int[][] { { 0, 1 }, { 1, 0 } };
		}

		if (numSeats == 3) {
			return new int[][] { { 0, 1, 2 }, { 1, 2, 0 }, { 2, 0, 1 }, { 2, 1, 0 }, { 0, 2, 1 }, { 1, 0, 2 } };
		}

		//		if (numSeats == 4) {
		//			return new int[][] { { 0, 1, 2, 3 }, { 1, 3, 0, 2 }, { 2, 0, 3, 1 }, { 3, 2, 1, 0 }, { 1, 2, 3, 0 }, { 3, 0, 2, 1 }, { 0, 3, 1, 2 },
		//					{ 2, 1, 0, 3 }, { 2, 3, 0, 1 }, { 0, 2, 1, 3 }, { 3, 1, 2, 0 }, { 1, 0, 3, 2 }, { 3, 0, 1, 2 }, { 2, 1, 3, 0 }, { 1, 2, 0, 3 },
		//					{ 0, 3, 2, 1 } };
		//		}

		//		if (numSeats == 4) {
		//			return new int[][] { { 0, 1, 2, 3 }, { 1, 3, 0, 2 }, { 2, 0, 3, 1 }, { 3, 2, 1, 0 },
		//			{ 0, 3, 1, 2 }, { 3, 2, 0, 1 }, { 1, 0, 2, 3 }, { 2, 1, 3, 0 },
		//			{ 0, 2, 3, 1 }, { 2, 1, 0, 3 }, { 3, 0, 1, 2 }, { 1, 3, 2, 0 } };
		//		}

		if (numSeats == 4 || numSeats == 6) {
			// would work for 10 seats as well, but currently we support only up to 
			// 9 seats

			// see http://pokerai.org/pf3/viewtopic.php?f=3&t=3272 for algorithm
			int permutationCount = permuteSeats ? numSeats : 1;

			int[][] seatPermutations = new int[permutationCount][numSeats];
			for (int permutation = 0; permutation < permutationCount; permutation++) {
				for (int seat = 0; seat < numSeats; seat++) {
					seatPermutations[permutation][seat] = ((permutation + 1) * (seat + 1) % (numSeats + 1)) - 1;
				}
			}
			return seatPermutations;
		}

		throw new IllegalArgumentException("permutation currently only works with 2,3,4 or 6 seats");
	}

}
