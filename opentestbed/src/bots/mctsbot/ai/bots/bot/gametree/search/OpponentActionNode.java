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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bots.mctsbot.ai.bots.bot.gametree.action.ActionWrapper;
import bots.mctsbot.ai.bots.bot.gametree.search.expander.SamplingExpander;
import bots.mctsbot.ai.bots.bot.gametree.search.expander.WeightedNode;
import bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling.Sampler;
import bots.mctsbot.ai.bots.bot.gametree.search.nodevisitor.NodeVisitor;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.Pair;

public class OpponentActionNode extends ActionNode {

	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(OpponentActionNode.class);

	private final SamplingExpander expander;
	private Distribution valueDistribution = null;

	public OpponentActionNode(PlayerId opponentId, PlayerId botId, GameState gameState, SearchConfiguration config, Sampler sampler, int tokens, int searchId,
			NodeVisitor... visitors) {
		super(opponentId, botId, gameState, config, searchId, visitors);
		expander = new SamplingExpander(this, tokens, sampler);
	}

	@Override
	public Distribution getValueDistribution(double lowerBound) {
		if (valueDistribution == null) {
			config.getOpponentModel().assumeTemporarily(gameState);
			List<Pair<ActionWrapper, WeightedNode>> children = getExpander().getWeightedChildren(config.isUniformBotActionTokens());
			double percentageDone = 0;
			double valueDone = 0;
			double maxToDo = 0;
			List<Distribution> valueDistributions = new ArrayList<Distribution>(children.size());
			for (Pair<ActionWrapper, WeightedNode> pair : children) {
				WeightedNode child = pair.getRight();
				double prob = child.getWeight();
				double upperWinBound = child.getNode().getUpperWinBound();
				maxToDo += prob * upperWinBound;
			}
			for (int i = 0; i < children.size(); i++) {
				Pair<ActionWrapper, WeightedNode> pair = children.get(i);
				WeightedNode child = pair.getRight();
				double upperWinBound = child.getNode().getUpperWinBound();
				double prob = child.getWeight();
				maxToDo -= prob * upperWinBound;
				int requiredFromSubtree = (int) ((lowerBound - valueDone - maxToDo) / prob);
				if (config.isUseAlphaBetaPruning() && requiredFromSubtree > upperWinBound) {
					//prune
					for (int j = i; j < children.size(); j++) {
						for (NodeVisitor visitor : visitors) {
							Pair<ActionWrapper, WeightedNode> skipped = children.get(j);
							Pair<ActionWrapper, GameTreeNode> node = new Pair<ActionWrapper, GameTreeNode>(skipped.getLeft(), skipped.getRight().getNode());
							visitor.pruneSubTree(node, new Distribution(node.getRight().getUpperWinBound(), 0, true), requiredFromSubtree);
						}
					}
					valueDistribution = new Distribution(valueDone + prob * upperWinBound + maxToDo, 0.0, true);
					//forget last game state!
					config.getOpponentModel().forgetLastAssumption();
					return valueDistribution;
				}
				percentageDone += prob;
				for (NodeVisitor visitor : visitors) {
					visitor.enterNode(new Pair<ActionWrapper, GameTreeNode>(pair.getLeft(), pair.getRight().getNode()), requiredFromSubtree);
				}
				Distribution valueDistribution = child.getNode().getValueDistribution(requiredFromSubtree);
				for (NodeVisitor visitor : visitors) {
					visitor.leaveNode(new Pair<ActionWrapper, GameTreeNode>(pair.getLeft(), pair.getRight().getNode()), valueDistribution);
				}
				valueDone += prob * valueDistribution.getMean();
				valueDistributions.add(valueDistribution);
			}
			config.getOpponentModel().forgetLastAssumption();
			// see Variance Estimation and Ranking of Gaussian Mixture Distributions
			// in Target Tracking Applications
			// Lidija Trailovi and Lucy Y. Pao
			double varEV = 0;
			for (int i = 0; i < valueDistributions.size(); i++) {
				Distribution valueDistribution = valueDistributions.get(i);
				double m = valueDistribution.getMean();
				double var = valueDistribution.getVariance();
				double w = children.get(i).getRight().getWeight();
				varEV += w * (var + m * m);
			}
			valueDistribution = new Distribution(valueDone, Math.max(0, varEV - valueDone));
		}
		return valueDistribution;
	}

	public SamplingExpander getExpander() {
		return expander;
	}

	public int getNbTokens() {
		return expander.tokens;
	}

	@Override
	public String toString() {
		return "Opponent " + playerId + " Action Node";
	}

}
