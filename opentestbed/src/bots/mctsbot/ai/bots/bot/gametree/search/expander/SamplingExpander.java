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
package bots.mctsbot.ai.bots.bot.gametree.search.expander;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import bots.mctsbot.ai.bots.bot.gametree.action.ActionWrapper;
import bots.mctsbot.ai.bots.bot.gametree.action.BetAction;
import bots.mctsbot.ai.bots.bot.gametree.action.ProbabilityAction;
import bots.mctsbot.ai.bots.bot.gametree.action.RaiseAction;
import bots.mctsbot.ai.bots.bot.gametree.search.BotActionNode;
import bots.mctsbot.ai.bots.bot.gametree.search.GameTreeNode;
import bots.mctsbot.ai.bots.bot.gametree.search.InnerGameTreeNode;
import bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling.Sampler;
import bots.mctsbot.common.util.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.google.common.collect.Multiset.Entry;

public class SamplingExpander extends TokenExpander {

	private static final int Max_Granularity = 300;

	private final static Logger logger = Logger.getLogger(BotActionNode.class);

	private final Random random = new Random();

	public SamplingExpander(InnerGameTreeNode node, int tokens, Sampler sampler) {
		super(node, tokens, sampler);
	}

	@Override
	public List<Pair<ActionWrapper, GameTreeNode>> getChildren(boolean uniformTokens) {
		List<Pair<ActionWrapper, WeightedNode>> weightedChildren = getWeightedChildren(uniformTokens);
		List<Pair<ActionWrapper, GameTreeNode>> children = new ArrayList<Pair<ActionWrapper, GameTreeNode>>(weightedChildren.size());
		for (Pair<ActionWrapper, WeightedNode> wpair : weightedChildren) {
			children.add(new Pair<ActionWrapper, GameTreeNode>(wpair.getLeft(), wpair.getRight().getNode()));
		}
		return children;
	}

	public List<Pair<ActionWrapper, WeightedNode>> getWeightedChildren(boolean uniformTokens) {
		List<ProbabilityAction> probActions = new ArrayList<ProbabilityAction>(getProbabilityActions());
		double[] cumulProb = new double[probActions.size()];

		for (int i = 0; i < probActions.size(); i++) {
			cumulProb[i] = (i > 0 ? cumulProb[i - 1] : 0) + probActions.get(i).getProbability();
		}
		if (logger.isTraceEnabled()) {
			for (int i = 0; i < probActions.size(); i++) {
				logger.trace("cumulProb[" + i + "]=" + cumulProb[i] + " for action " + probActions.get(i));

			}
		}

		// ordening for sexy debugging output
		Multiset<ProbabilityAction> samples = TreeMultiset.create(new Comparator<ProbabilityAction>() {
			@Override
			public int compare(ProbabilityAction o1, ProbabilityAction o2) {
				if (o2.getProbability() < o1.getProbability()) {
					return -1;
				}
				if (o2.getProbability() > o1.getProbability()) {
					return 1;
				}
				if (o1.getAction() instanceof RaiseAction && o2.getAction() instanceof RaiseAction) {
					return ((RaiseAction) o2.getAction()).amount - ((RaiseAction) o1.getAction()).amount;
				}
				if (o1.getAction() instanceof BetAction && o2.getAction() instanceof BetAction) {
					return ((BetAction) o2.getAction()).amount - ((BetAction) o1.getAction()).amount;
				}
				// if probabilities are equal for different classes,
				// objects are NOT equal per se
				// go alphabetically?
				return o1.toString().compareTo(o2.toString());
			}
		});
		// Multiset<ProbabilityAction> samples = new
		// HashMultiset<ProbabilityAction>();
		int nbSamples = Math.min(Max_Granularity, tokens);
		for (int i = 0; i < nbSamples; i++) {
			ProbabilityAction sampledAction = sampleAction(probActions, cumulProb);
			samples.add(sampledAction);
		}

		Set<Entry<ProbabilityAction>> entrySet = samples.entrySet();
		ImmutableList.Builder<Pair<ActionWrapper, WeightedNode>> childrenBuilder = ImmutableList.builder();
		for (Entry<ProbabilityAction> entry : entrySet) {
			int tokensShare = uniformTokens ? tokens / entrySet.size() : tokens * entry.getCount() / nbSamples;
			//			
			childrenBuilder.add(new Pair<ActionWrapper, WeightedNode>(entry.getElement(), new WeightedNode(node.getChildAfter(entry.getElement(), tokensShare),
					entry.getCount() / (double) nbSamples)));
		}
		return childrenBuilder.build();
	}

	private ProbabilityAction sampleAction(List<ProbabilityAction> probActions, double[] cumulProb) {
		double randDouble = random.nextDouble();
		for (int i = 0; i < cumulProb.length; i++) {
			if (randDouble < cumulProb[i]) {
				if (logger.isTraceEnabled()) {
					logger.trace("random " + randDouble + " assigned to " + probActions.get(i));
				}
				return probActions.get(i);
			}
		}
		return probActions.get(probActions.size() - 1);
	}

	public static class Factory implements TokenExpander.Factory {
		public SamplingExpander create(InnerGameTreeNode node, int tokens, Sampler sampler) {
			return new SamplingExpander(node, tokens, sampler);
		}
	}

}
