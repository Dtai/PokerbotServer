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

import java.util.List;

import org.apache.log4j.Logger;

import bots.mctsbot.ai.bots.bot.gametree.action.ActionWrapper;
import bots.mctsbot.ai.bots.bot.gametree.search.expander.TokenExpander;
import bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling.Sampler;
import bots.mctsbot.ai.bots.bot.gametree.search.nodevisitor.NodeVisitor;
import bots.mctsbot.client.common.gamestate.CachingNode;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.api.lobby.holdemtable.holdemplayer.context.RemoteHoldemPlayerContext;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.Pair;
import bots.mctsbot.common.util.Triple;

public class BotActionNode extends ActionNode {

	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(BotActionNode.class);

	private final TokenExpander expander;
	private Triple<ActionWrapper, GameTreeNode, Distribution> best = null;

	public BotActionNode(PlayerId botId, GameState gameState, SearchConfiguration config, Sampler sampler, int tokens, int searchId, NodeVisitor... visitors) {
		super(botId, botId, new CachingNode(gameState), config, searchId, visitors);
		expander = config.getBotNodeExpanderFactory().create(this, tokens, sampler);
	}

	@Override
	public Distribution getValueDistribution(double lowerBound) {
		getBestEvaluatedAction(lowerBound);
		return best.getRight();
	}

	public void performbestAction(RemoteHoldemPlayerContext context) {
		getBestEvaluatedAction(0.0);
		//		for (NodeVisitor visitor : visitors) {
		//			visitor.leaveNode(bestEvaluatedAction);
		//		}
		best.getLeft().getAction().perform(context);
	}

	public Triple<ActionWrapper, GameTreeNode, Distribution> getBestEvaluatedAction(double lowerBound) {
		if (best == null) {
			double maxEv = Double.NEGATIVE_INFINITY;
			config.getOpponentModel().assumeTemporarily(gameState);
			List<Pair<ActionWrapper, GameTreeNode>> children = getExpander().getChildren(config.isUniformBotActionTokens());
			for (int i = 0; i < children.size(); i++) {
				Pair<ActionWrapper, GameTreeNode> pair = children.get(i);
				GameTreeNode child = pair.getRight();
				double upperWinBound = child.getUpperWinBound();
				double nextLowerBound = Math.max(lowerBound, maxEv);
				if (config.isUseAlphaBetaPruning() && upperWinBound < nextLowerBound) {
					//prune
					Distribution distribution = new Distribution(upperWinBound, 0, true);
					if (upperWinBound > maxEv) {
						//take the best insufficient child as an upper bound
						maxEv = upperWinBound;
						best = new Triple<ActionWrapper, GameTreeNode, Distribution>(pair.getLeft(), pair.getRight(), distribution);
					}
					for (NodeVisitor visitor : visitors) {
						visitor.pruneSubTree(pair, distribution, nextLowerBound);
					}
					continue;
				}
				for (NodeVisitor visitor : visitors) {
					visitor.enterNode(pair, nextLowerBound);
				}
				Distribution valueDistribution = child.getValueDistribution(nextLowerBound);
				for (NodeVisitor visitor : visitors) {
					visitor.leaveNode(pair, valueDistribution);
				}
				if (valueDistribution.getMean() > maxEv) {
					maxEv = valueDistribution.getMean();
					best = new Triple<ActionWrapper, GameTreeNode, Distribution>(pair.getLeft(), pair.getRight(), valueDistribution);
				}
			}
			if (best == null) {
				throw new IllegalStateException();
			}
			config.getOpponentModel().forgetLastAssumption();
		}
		return best;
	}

	protected TokenExpander getExpander() {
		return expander;
	}

	public int getNbTokens() {
		return expander.tokens;
	}

	@Override
	public String toString() {
		return "Bot Action Node";
	}

}
