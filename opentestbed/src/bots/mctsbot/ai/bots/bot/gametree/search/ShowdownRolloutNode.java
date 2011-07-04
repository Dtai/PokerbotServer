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
package bots.mctsbot.ai.bots.bot.gametree.search;

import java.util.SortedMap;
import java.util.Map.Entry;

import bots.mctsbot.ai.bots.bot.gametree.rollout.AbstractDistributionRollout;
import bots.mctsbot.ai.bots.bot.gametree.rollout.RolloutResult;
import bots.mctsbot.ai.bots.bot.gametree.search.nodevisitor.NodeVisitor;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.MutableDouble;

public class ShowdownRolloutNode implements ShowdownNode {

	public static final int MaxNbSamples = 400;

	private Distribution valueDistribution = null;

	private final GameState gameState;

	private final NodeVisitor[] nodeVisitors;

	private final int nbTokens;

	private final AbstractDistributionRollout rollout;

	ShowdownRolloutNode(PlayerId botId, GameState gameState, AbstractDistributionRollout rollout, int tokens, NodeVisitor... nodeVisitors) {
		this.gameState = gameState;
		this.nodeVisitors = nodeVisitors;
		this.nbTokens = tokens;
		this.rollout = rollout;
	}

	public Distribution getValueDistribution(double lowerBound) {
		if (valueDistribution == null) {
			int nbSamplesEst = Math.min(MaxNbSamples, Math.max(25, nbTokens * 5));
			int nbCommunitySamples, nbOpponentSamples;
			if (rollout.nbMissingCommunityCards == 0) {
				nbCommunitySamples = 1;
				nbOpponentSamples = nbSamplesEst;
			} else {
				double root = Math.sqrt(nbSamplesEst);
				nbCommunitySamples = (int) (root * rollout.nbMissingCommunityCards / 2);
				nbOpponentSamples = (int) (root * 2 / rollout.nbMissingCommunityCards);
			}

			RolloutResult result = rollout.doRollOut(nbCommunitySamples, nbOpponentSamples);

			int stackSize = rollout.botState.getStack();
			informListeners(result.getValues(), result.getTotalProb(), rollout.gamePotSize, stackSize);
			double mean = result.getMean();
			double var = result.getVariance(mean, nbOpponentSamples * nbCommunitySamples);
			valueDistribution = new Distribution(stackSize + mean, var);
		}
		return valueDistribution;
	}

	@Override
	public double getUpperWinBound() {
		return rollout.getUpperWinBound();
	}

	@Override
	public int getNbTokens() {
		return nbTokens;
	}

	@Override
	public GameState getGameState() {
		return gameState;
	}

	private void informListeners(SortedMap<Integer, MutableDouble> sortedMap, double totalProb, int gamePotSize, int stackSize) {
		for (Entry<Integer, MutableDouble> value : sortedMap.entrySet()) {
			for (NodeVisitor nodeVisitor : nodeVisitors) {
				nodeVisitor.visitLeafNode(stackSize + value.getKey(), value.getValue().getValue() / totalProb, stackSize, stackSize + gamePotSize);
			}
		}
	}

	@Override
	public String toString() {
		return "Showdown Rollout Node";
	}

	public static class Factory implements ShowdownNode.Factory {

		private final AbstractDistributionRollout.Factory rolloutFactory;

		public Factory(AbstractDistributionRollout.Factory rolloutFactory) {
			this.rolloutFactory = rolloutFactory;
		}

		public ShowdownRolloutNode create(PlayerId botId, GameState gameState, int tokens, SearchConfiguration config, int searchId,
				NodeVisitor... nodeVisitors) {
			return new ShowdownRolloutNode(botId, gameState, rolloutFactory.create(gameState, botId), tokens, nodeVisitors);
		}

		@Override
		public String toString() {
			return "Showdown Rollout Node";
		}
	}

}
