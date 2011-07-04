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

//import org.apache.log4j.Logger;
import bots.mctsbot.ai.bots.bot.gametree.rollout.BucketRollOut;
import bots.mctsbot.ai.bots.bot.gametree.search.nodevisitor.NodeVisitor;
import bots.mctsbot.ai.opponentmodels.OpponentModel;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;

public class ShowdownBucketRolloutNode implements ShowdownNode {

	//	private final static Logger logger = Logger
	//	.getLogger(ShowdownBucketRolloutNode.class);

	private Distribution valueDistribution = null;

	private final GameState gameState;

	//	private final NodeVisitor[] nodeVisitors;

	private final int nbTokens;

	private final BucketRollOut rollout;

	ShowdownBucketRolloutNode(PlayerId botId, GameState gameState, OpponentModel model, int tokens, NodeVisitor... nodeVisitors) {
		this.gameState = gameState;
		//		this.nodeVisitors = nodeVisitors;
		this.nbTokens = tokens;
		this.rollout = new BucketRollOut(gameState, botId, model);
	}

	public Distribution getValueDistribution(double lowerBound) {
		if (valueDistribution == null) {
			int nbSamplesEst = Math.min(30, Math.max(5, nbTokens));
			double result = rollout.doRollOut(nbSamplesEst);

			int stackSize = rollout.botState.getStack();
			valueDistribution = new Distribution(stackSize + result, 0);
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

	@Override
	public String toString() {
		return "Showdown Rollout Node";
	}

	public static class Factory implements ShowdownNode.Factory {

		public ShowdownBucketRolloutNode create(PlayerId botId, GameState gameState, int tokens, SearchConfiguration config, int searchId,
				NodeVisitor... nodeVisitors) {
			return new ShowdownBucketRolloutNode(botId, gameState, config.getOpponentModel(), tokens, nodeVisitors);
		}

		@Override
		public String toString() {
			return "Showdown Bucket Rollout Node";
		}
	}

}
