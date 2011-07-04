/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package bots.mctsbot.ai.opponentmodels.weka;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import util.CardConverter;
import bots.mctsbot.common.handeval.spears2p2.StateTableEvaluator;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

public class Propositionalizer implements Cloneable {

	private Map<Object, PlayerData> players = new HashMap<Object, PlayerData>();
	private List<PlayerData> activePlayers = new LinkedList<PlayerData>();
	private List<PlayerData> allinPlayers = new LinkedList<PlayerData>();

	private int bb = 0;

	private int maxBet = 0;
	private int lastRaise = 0;
	private int gameRaiseAmount = 0;
	private boolean somebodyActedThisRound = false;

	private BetStatistics gameStats = new BetStatistics();
	private String round = "preflop";
	private int totalPot = 0;

	private int nbPlayersDoneThisRound = 0;
	private int nbSeatedPlayers = 0;
	private Hand cards;

	private int minRank = 0;
	private int maxRank = 0;
	private int averageRank = 0;
	private double sigmaRank = 0;

	public Propositionalizer() {
	}

	@Override
	public Propositionalizer clone() {
		try {
			Propositionalizer clone = (Propositionalizer) super.clone();

			//clone player map (is this needed?)
			Map<Object, PlayerData> playersClone = new HashMap<Object, PlayerData>(clone.getPlayers());

			//clone the active players at this table. they are mutable
			List<PlayerData> activePlayersClone = new LinkedList<PlayerData>();
			for (PlayerData player : activePlayers) {
				PlayerData playerClone = player.clone();
				activePlayersClone.add(playerClone);
				playersClone.put(playerClone.getId(), playerClone);
			}

			//no need to clone contents of allinPlayers, is immutable
			//do need to clone the list itself
			List<PlayerData> allinplayersClone = new LinkedList<PlayerData>(clone.getAllinPlayers());
			clone.players = playersClone;
			clone.activePlayers = activePlayersClone;
			clone.allinPlayers = allinplayersClone;
			clone.gameStats = gameStats.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	public void signalBBAmount(int bb) {
		this.bb = bb;
		for (PlayerData p : activePlayers)
			p.signalBBAmount(bb);
	}

	public void signalAllIn(Object id, int chipsMoved) {
		if (inPreFlopRound() && maxBet < bb && chipsMoved <= bb) {
			signalBlind(true, id, chipsMoved);
		} else {
			PlayerData p = players.get(id);
			//call raise or bet
			if (maxBet == 0) {
				signalBet(true, id, chipsMoved);
			} else if (p.getDeficit(this) < chipsMoved) {
				signalRaise(true, id, p.getBet() + chipsMoved);
			} else {
				signalCall(true, id, chipsMoved);
			}
		}
	}

	public void signalBet(boolean isAllIn, Object id, int movedAmount) {
		PlayerData p = players.get(id);
		logBet(id, movedAmount / (double) bb);
		if (isAllIn) {
			activePlayers.remove(p);
			allinPlayers.add(p);
		}
		// we also consider a raise by big blind here, treat kinda like bet!
		// that's why the +=
		maxBet += movedAmount;
		lastRaise = Math.max(lastRaise, movedAmount);
		gameRaiseAmount += movedAmount;
		totalPot += movedAmount;
		somebodyActedThisRound = true;
		gameStats.addBet(this, movedAmount / (double) bb);
		p.signalBet(this, movedAmount);
	}

	public void signalCheck(Object id) {
		PlayerData p = players.get(id);
		logCheck(id);
		++nbPlayersDoneThisRound;
		gameStats.addCheck(this);
		somebodyActedThisRound = true;
		p.signalCheck(this);
	}

	public void signalRaise(boolean isAllIn, Object id, int maxBetParsed) {
		PlayerData p = players.get(id);
		int raiseAmount = maxBetParsed - maxBet;
		if (p.getDeficit(this) == 0) {
			signalBet(isAllIn, id, raiseAmount);
		} else {
			logRaise(id, raiseAmount / (double) bb);
			if (isAllIn) {
				activePlayers.remove(p);
				allinPlayers.add(p);
			}
			maxBet = maxBetParsed;
			lastRaise = Math.max(lastRaise, raiseAmount);
			int movedAmount = maxBet - p.getBet();
			gameRaiseAmount += raiseAmount;

			totalPot += movedAmount;
			somebodyActedThisRound = true;
			gameStats.addRaise(this, (movedAmount - raiseAmount) / (double) bb, raiseAmount / (double) bb);
			p.signalRaise(this, raiseAmount, movedAmount);
		}
		nbPlayersDoneThisRound = 0;
	}

	public void signalCall(boolean isAllIn, Object id) {
		PlayerData p = players.get(id);
		if (p.getDeficit(this) == 0) {
			signalCheck(id);
		} else {
			signalCall(isAllIn, id, Math.min(p.getStack(), maxBet - p.getBet()));
		}
	}

	public void signalCall(boolean isAllIn, Object id, int movedAmount) {
		PlayerData p = players.get(id);
		logCall(id);

		if (isAllIn) {
			activePlayers.remove(p);
			allinPlayers.add(p);
		}
		totalPot += movedAmount;
		++nbPlayersDoneThisRound;
		somebodyActedThisRound = true;
		gameStats.addCall(this, movedAmount / (double) bb);
		p.signalCall(this, movedAmount);
	}

	public void signalFold(Object id) {
		PlayerData p = players.get(id);
		if (p.getDeficit(this) > 0)
			logFold(id);

		activePlayers.remove(p);
		somebodyActedThisRound = true;
		gameStats.addFold(this);
		p.signalFold(this);
	}

	public void signalBlind(boolean isAllIn, Object playerId, int amount) {
		PlayerData p = players.get(playerId);

		//TODO change amount when allin
		maxBet = amount;
		totalPot += amount;
		if (isAllIn) {
			activePlayers.remove(p);
			allinPlayers.add(p);
		}
		p.signalBlind(amount);
	}

	public void signalRiver() {
		if (!activePlayers.isEmpty()) {
			round = "river";
			startNewRound();
		}
	}

	public void signalTurn() {
		if (!activePlayers.isEmpty()) {
			round = "turn";
			startNewRound();
		}
	}

	public void signalFlop() {
		if (!activePlayers.isEmpty()) {
			round = "flop";
			startNewRound();
			for (PlayerData p : activePlayers) {
				p.signalFlop();
			}
		}
	}

	public void signalCommunityCards(Hand cardsSet) {
		this.cards = cardsSet;
		//updateER();
	}

	public void signalCardShowdown(Object id, Card card1, Card card2) {
		//		PlayerData p = players.get(id);
		if (cards.size() == 5) {
			//showdown after river
			Multiset<Integer> ranks = new TreeMultiset<Integer>();
			//			int minSampleRank = Integer.MAX_VALUE;
			//			int maxSampleRank = Integer.MIN_VALUE;
			//			int sum = 0;

			int startRank = 53;
			for (int i = 0; i < cards.size(); i++) {
				startRank = handRanks[CardConverter.toSpears2p2Index(cards.getCard(i + 1)) + startRank];
			}

			//add real rank
			int realRank = startRank;
			realRank = handRanks[CardConverter.toSpears2p2Index(card1) + realRank];
			realRank = handRanks[CardConverter.toSpears2p2Index(card2) + realRank];
			int realType = (realRank >>> 12) - 1;
			realRank = realRank & 0xFFF;
			realRank = offsets[realType] + realRank - 1;

			//take rank samples
			int nbBuckets = 6;
			int nbSamplesPerBucket = 6;
			int nbSamples = nbBuckets * nbSamplesPerBucket;
			for (int i = 0; i < nbSamples; i++) {

				int rank = startRank;
				Card sampleCard1;
				do {
					sampleCard1 = new Card(random.nextInt(52));
				} while (cards.contains(sampleCard1));
				rank = handRanks[CardConverter.toSpears2p2Index(sampleCard1) + rank];

				Card sampleCard2;
				do {
					sampleCard2 = new Card(random.nextInt(52));
				} while (cards.contains(sampleCard2) || sampleCard2.equals(sampleCard1));
				rank = handRanks[CardConverter.toSpears2p2Index(sampleCard2) + rank];

				int type = (rank >>> 12) - 1;
				rank = rank & 0xFFF;
				rank = offsets[type] + rank - 1;

				ranks.add(rank);

				//				if(rank<minSampleRank){
				//					minSampleRank = rank;
				//				}
				//				if(rank>maxSampleRank){
				//					maxSampleRank = rank;
				//				}
				//				sum += rank;
			}
			//			double var = 0;
			//			double mean = ((double)sum)/nbSamples;
			//			for (Multiset.Entry<Integer> entry : ranks.entrySet()) {
			//				double diff = mean - entry.getElement();
			//				var += diff * diff * entry.getCount();
			//			}
			//			var /= (nbSamples-1);
			//			int averageSampleRank = (int) Math.round(mean);
			//			int sigmaSampleRank = (int) Math.round(Math.sqrt(var));
			int[] bucketCounts = new int[nbBuckets];
			Iterator<Integer> iter = ranks.iterator();
			double realRankCount = ranks.count(realRank);
			//			long avgBucket = -1;
			double[] bucketDistr = new double[nbBuckets];
			if (realRankCount > 0) {
				for (int bucket = 0; bucket < nbBuckets; bucket++) {
					for (int i = 0; i < nbSamplesPerBucket; i++) {
						int rank = iter.next();
						if (rank == realRank) {
							++bucketCounts[bucket];
						}
					}
				}
				int partitionSum = 0;
				for (int i = 0; i < nbBuckets; i++) {
					bucketDistr[i] = bucketCounts[i] / realRankCount;
					partitionSum += bucketCounts[i] * i;
				}
				//			avgBucket = Math.round(partitionSum/realRankCount);
			} else {
				boolean found = false;
				bucketIteration: for (int bucket = 0; bucket < nbBuckets; bucket++) {
					for (int i = 0; i < nbSamplesPerBucket; i++) {
						int rank = iter.next();
						if (rank > realRank) {
							bucketDistr[bucket] = 1;
							//				avgBucket = bucket;
							found = true;
							break bucketIteration;
						}
					}
				}
				if (!found) {
					bucketCounts[nbBuckets - 1] = 1;
					//				avgBucket = nbBuckets-1;
				}
			}
			logShowdown(id, bucketDistr);
		} else {
			//ignore
			//throw new IllegalStateException("everybody went all-in before the river");
		}
	}

	private final static int[] handRanks;
	static {
		handRanks = StateTableEvaluator.getInstance().handRanks;
	}
	private static final int[] offsets = //
	new int[] { 0, 1277, 4137, 4995, 5853, 5863, 7140, 7296, 7452 };
	private Random random = new Random(0);

	//
	//	private void updateExpectedRank() {
	//		Multiset<Integer> ranks = new HashMultiset<Integer>();
	//		minRank = Integer.MAX_VALUE;
	//		maxRank = Integer.MIN_VALUE;
	//		int sum = 0;
	//		int n = 100;
	//
	//		int startRank = 53;
	//		for (Card card:cards) {
	//			startRank = handRanks[card.ordinal() + 1 + startRank];
	//		}
	//		for(int i=0;i<n;i++){
	//			EnumSet<Card> sample = EnumSet.copyOf(cards);
	//			int rank = startRank;
	//			while(sample.size()<7){
	//				Card sampleCard;
	//				do{
	//					sampleCard = Card.values()[random.nextInt(Card.values().length)];
	//				}while(sample.contains(sampleCard));
	//				sample.add(sampleCard);
	//				rank = handRanks[sampleCard.ordinal() + 1 + rank];
	//			}
	//			int type = (rank >>> 12) - 1;
	//			rank = rank & 0xFFF;
	//			rank = offsets[type] + rank - 1;
	//			ranks.add(rank);
	//			if(rank<minRank){
	//				minRank = rank;
	//			}
	//			if(rank>maxRank){
	//				maxRank = rank;
	//			}
	//			sum += rank;
	//		}
	//		double var = 0;
	//		double mean = ((double)sum)/n;
	//		for (Multiset.Entry<Integer> entry : ranks.entrySet()) {
	//			double diff = mean - entry.getElement();
	//			var += diff * diff * entry.getCount();
	//		}
	//		var /= (n-1);
	//		averageRank = (int)Math.round(mean);
	//		sigmaRank = Math.round(Math.sqrt(var));
	//	}

	public void signalShowdown() {
		if (round.equals("flop") || round.equals("turn") || round.equals("river")) {
			for (PlayerData p : activePlayers) {
				p.signalShowdown();
			}
		}
	}

	public void signalSeatedPlayer(int stack, Object id) {
		PlayerData p = players.get(id);
		if (p == null) {
			p = new PlayerData(id);
			players.put(id, p);
		}
		activePlayers.add(p);
		p.signalBBAmount(bb);
		++nbSeatedPlayers;
		p.startNewGame();
		p.resetStack(stack);
	}

	public void signalNewGame() {
		round = "preflop";
		somebodyActedThisRound = false;
		gameStats = new BetStatistics();
		nbPlayersDoneThisRound = 0;
		nbSeatedPlayers = 0;
		activePlayers.clear();
		allinPlayers.clear();
		gameRaiseAmount = 0;
		cards = new Hand();
		startNewRound();
	}

	protected void startNewRound() {
		for (PlayerData player : activePlayers) {
			player.startNewRound();
		}
		nbPlayersDoneThisRound = 0;
		maxBet = 0;
		lastRaise = bb;
		somebodyActedThisRound = false;
	}

	public boolean isSomebodyActedThisRound() {
		return somebodyActedThisRound;
	}

	public List<PlayerData> getActivePlayers() {
		return activePlayers;
	}

	public Map<Object, PlayerData> getPlayers() {
		return players;
	}

	public List<PlayerData> getAllinPlayers() {
		return allinPlayers;
	}

	public int getTotalPot() {
		return totalPot;
	}

	public int getMaxBet() {
		return maxBet;
	}

	public double getRelativeMaxBet() {
		return maxBet / (double) bb;
	}

	public int getAverageRank() {
		return averageRank;
	}

	public int getMaxRank() {
		return maxRank;
	}

	public BetStatistics getTableGameStats() {
		return gameStats;
	}

	public int getMinRank() {
		return minRank;
	}

	public double getSigmaRank() {
		return sigmaRank;
	}

	public int getNbSeatedPlayers() {
		return nbSeatedPlayers;
	}

	public int getNbActivePlayers() {
		return activePlayers.size();
	}

	public double getActivePlayerRatio() {
		return (double) getNbActivePlayers() / (double) getNbSeatedPlayers();
	}

	public int getPotSize() {
		return totalPot;
	}

	public double getRelativePotSize() {
		return totalPot / (double) bb;
	}

	public int getMaxMaxBet() {
		int maxBet1 = 0;
		int maxBet2 = 0;

		for (PlayerData player : activePlayers) {
			int maxMaxPlayerBet = player.getStack() + player.getBet();
			if (maxMaxPlayerBet > maxBet1) {
				maxBet2 = maxBet1;
				maxBet1 = maxMaxPlayerBet;
			} else if (maxMaxPlayerBet > maxBet2) {
				maxBet2 = maxMaxPlayerBet;
			}
		}
		return maxBet2;
	}

	public int getMaxRaise(PlayerData p) {
		int amountLeftToRaiseWith = p.getStack() - p.getDeficit(this);
		int maxRaise = Math.max(0, getMaxMaxBet() - getMaxBet());
		return Math.min(amountLeftToRaiseWith, maxRaise);
	}

	public int getMinRaise(PlayerData p) {
		return Math.min(lastRaise, getMaxRaise(p));
	}

	public int getPlayersToAct() {
		if (somebodyActedThisRound) {
			return getNbActivePlayers() - nbPlayersDoneThisRound - 1;
		}
		return getNbActivePlayers() - nbPlayersDoneThisRound;
	}

	public int getPlayersActed() {
		return nbPlayersDoneThisRound;
	}

	public double getRoundCompletion() {
		if (isSomebodyActedThisRound()) {
			if (getNbActivePlayers() <= 1) {
				return 0;
			}
			return nbPlayersDoneThisRound / (double) (getNbActivePlayers() - 1);
		}
		return nbPlayersDoneThisRound / (double) getNbActivePlayers();
	}

	public double getAverageAF(PlayerData p, int memory) {
		List<PlayerData> opponents = activePlayers.size() > 1 ? activePlayers : allinPlayers;
		double sum = 0;
		int n = 0;
		for (PlayerData player : opponents) {
			if (!p.equals(player)) {
				sum += player.getGlobalStats().getAF(memory);
				++n;
			}
		}
		return (sum / n);
	}

	public double getAverageVPIP(PlayerData p, int memory) {
		List<PlayerData> opponents = activePlayers.size() > 1 ? activePlayers : allinPlayers;
		double sum = 0;
		int n = 0;
		for (PlayerData player : opponents) {
			if (!p.equals(player)) {
				sum += player.getVPIP(memory);
				++n;
			}
		}
		return (sum / n);
	}

	public double getAveragePFR(PlayerData p, int memory) {
		List<PlayerData> opponents = activePlayers.size() > 1 ? activePlayers : allinPlayers;
		double sum = 0;
		int n = 0;
		for (PlayerData player : opponents) {
			if (!p.equals(player)) {
				sum += player.getPFR(memory);
				++n;
			}
		}
		return (sum / n);
	}

	public double getAverageAFq(PlayerData p, int memory) {
		List<PlayerData> opponents = activePlayers.size() > 1 ? activePlayers : allinPlayers;
		double sum = 0;
		int n = 0;
		for (PlayerData player : opponents) {
			if (!p.equals(player)) {
				sum += player.getGlobalStats().getAFq(memory);
				++n;
			}
		}
		return (sum / n);
	}

	public double getAverageAFAmount(PlayerData p, int memory) {
		List<PlayerData> opponents = activePlayers.size() > 1 ? activePlayers : allinPlayers;
		double sum = 0;
		int n = 0;
		for (PlayerData player : opponents) {
			if (!p.equals(player)) {
				sum += Math.log(player.getGlobalStats().getAFAmount(memory));
				++n;
			}
		}
		return (sum / n);
	}

	public double getAverageWtSD(PlayerData p, int memory) {
		List<PlayerData> opponents = activePlayers.size() > 1 ? activePlayers : allinPlayers;
		double sum = 0;
		int n = 0;
		for (PlayerData player : opponents) {
			if (!p.equals(player)) {
				sum += player.getWtSD(memory);
				++n;
			}
		}
		return (sum / n);
	}

	public String getRound() {
		return round;
	}

	public boolean inPreFlopRound() {
		return "preflop".equals(round);
	}

	public boolean inFlopRound() {
		return "flop".equals(round);
	}

	public boolean inTurnRound() {
		return "turn".equals(round);
	}

	public boolean inRiverRound() {
		return "river".equals(round);
	}

	protected void logBet(Object actorId, double raiseAmount) {
		// no op
	}

	protected void logRaise(Object actorId, double raiseAmount) {
		// no op
	}

	protected void logFold(Object actorId) {
		// no op
	}

	protected void logCall(Object actorId) {
		// no op
	}

	protected void logCheck(Object actorId) {
		// no op
	}

	protected void logShowdown(Object actorId, double[] partitionDistr) {
		// no op
	}

	public double rel(double up, double down) {
		if (down == 0)
			return 0;
		return up / down;
	}

}