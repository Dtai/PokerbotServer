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
package bots.mctsbot.ai.bots.bot.gametree.mcts.strategies.backpropagation;

import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.DecisionNode;
import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.INode;
import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.OpponentNode;
import bots.mctsbot.ai.bots.bot.gametree.mcts.strategies.selection.SelectionStrategy;

import com.google.common.collect.ImmutableList;

public abstract class MixtureBackPropStrategy implements BackPropagationStrategy {

	private MixtureBackPropStrategy() {
	}

	public static class Factory implements BackPropagationStrategy.Factory {

		private final SelectionStrategy selector;

		public Factory(SelectionStrategy selector) {
			this.selector = selector;
		}

		@Override
		public DecisionStrategy createForDecisionNode(DecisionNode node) {
			return new DecisionStrategy(node, selector);
		}

		@Override
		public OpponentStrategy createForOpponentNode(OpponentNode node) {
			return new OpponentStrategy(node);
		}

	}

	private static class OpponentStrategy extends MixtureBackPropStrategy {

		private final OpponentNode node;

		private int nbSamples = 0;
		private int nbSamplesInMean = 0;

		private double EV = 0;
		private double variance = 0;

		public OpponentStrategy(OpponentNode node) {
			this.node = node;
		}

		@Override
		public double getEV() {
			return EV;
		}

		@Override
		public int getNbSamples() {
			return nbSamples;
		}

		@Override
		public double getStdDev() {
			return Math.sqrt(getVariance());
		}

		@Override
		public double getVariance() {
			return variance;
		}

		@Override
		public double getEVStdDev() {
			return Math.sqrt(variance / nbSamplesInMean);
		}

		@Override
		public double getEVVar() {
			return variance / nbSamplesInMean;
		}

		@Override
		public int getNbSamplesInMean() {
			return nbSamplesInMean;
		}

		@Override
		public void onBackPropagate(double value) {
			++this.nbSamples;

			ImmutableList<INode> children = node.getChildren();
			double[] probabilities = node.getProbabilities();
			EV = 0;
			variance = 0;
			nbSamplesInMean = 0;
			for (int i = 0; i < probabilities.length; i++) {
				INode child = children.get(i);
				int childN = child.getNbSamples();
				if (childN > 0) {
					nbSamplesInMean += child.getNbSamplesInMean();
					double childEV = child.getEV();
					EV += childN * childEV;
					double childVariance = child.getVariance();
					variance += childN * (childVariance + childEV * childEV);
				}
			}
			EV /= nbSamples;
			variance /= nbSamples;
			variance -= EV * EV;
			if (variance < 0) {
				if (variance > -0.001) {
					variance = 0;
				} else {
					throw new IllegalStateException();
				}
			}
			if (Double.isNaN(variance) || Double.isInfinite(variance)) {
				throw new IllegalStateException();
			}
		}

	}

	private static class DecisionStrategy extends MixtureBackPropStrategy {

		private final DecisionNode node;
		private final SelectionStrategy selectionStrategy;

		private int nbSamples = 0;
		private int nbSamplesInMean = 0;
		private double EV = 0;
		private double variance = 0;

		public DecisionStrategy(DecisionNode node, SelectionStrategy selectionStrategy) {
			this.node = node;
			this.selectionStrategy = selectionStrategy;
		}

		@Override
		public double getEV() {
			return EV;
		}

		@Override
		public int getNbSamples() {
			return nbSamples;
		}

		@Override
		public double getStdDev() {
			return Math.sqrt(getVariance());
		}

		@Override
		public double getVariance() {
			return variance;
		}

		@Override
		public double getEVStdDev() {
			return Math.sqrt(variance / nbSamplesInMean);
		}

		@Override
		public double getEVVar() {
			return variance / nbSamplesInMean;
		}

		@Override
		public int getNbSamplesInMean() {
			return nbSamplesInMean;
		}

		@Override
		public void onBackPropagate(double value) {
			INode selection = selectionStrategy.select(node);

			//TODO decide
			//this.nbSamples = selection.getNbSamples();
			++this.nbSamples;

			this.EV = selection.getEV();
			this.variance = selection.getVariance();
			this.nbSamplesInMean = selection.getNbSamplesInMean();
			if (Double.isNaN(variance) || Double.isInfinite(variance) || variance < 0) {
				throw new IllegalStateException();
			}
		}
	}
}
