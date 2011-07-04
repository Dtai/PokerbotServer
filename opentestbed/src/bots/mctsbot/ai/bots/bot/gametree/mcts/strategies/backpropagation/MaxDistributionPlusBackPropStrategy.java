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
import bots.mctsbot.ai.bots.util.Gaussian;

import com.google.common.collect.ImmutableList;

public abstract class MaxDistributionPlusBackPropStrategy implements BackPropagationStrategy {

	private static final Gaussian startGaussian = new Gaussian(0, 0);

	private MaxDistributionPlusBackPropStrategy() {

	}

	public static class Factory implements BackPropagationStrategy.Factory {

		@Override
		public DecisionStrategy createForDecisionNode(DecisionNode node) {
			return new DecisionStrategy(node);
		}

		@Override
		public OpponentStrategy createForOpponentNode(OpponentNode node) {
			return new OpponentStrategy(node);
		}

	}

	private static class OpponentStrategy extends MaxDistributionPlusBackPropStrategy {

		private final OpponentNode node;

		private int nbSamples = 0;

		private Gaussian EVGaussian = startGaussian;

		public OpponentStrategy(OpponentNode node) {
			this.node = node;
		}

		@Override
		public double getEV() {
			return EVGaussian.mean;
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
			throw new UnsupportedOperationException();
		}

		@Override
		public double getEVStdDev() {
			return EVGaussian.getStdDev();
		}

		@Override
		public double getEVVar() {
			return EVGaussian.variance;
		}

		@Override
		public int getNbSamplesInMean() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void onBackPropagate(double value) {
			++this.nbSamples;

			ImmutableList<INode> children = node.getChildren();
			double[] probabilities = node.getProbabilities();
			double EV = 0;
			double EVVar = 0;
			double totalWeight = 0;
			for (int i = 0; i < probabilities.length; i++) {
				INode child = children.get(i);
				double childWeight = probabilities[i];
				if (childWeight > 0) {
					double childEV = child.getEV();
					EV += childWeight * childEV;
					totalWeight += childWeight;
					double childVariance = child.getEVVar();
					EVVar += childWeight * (childVariance);//+ childEV * childEV);
				}
			}
			EV /= totalWeight;
			EVVar /= totalWeight;
			//EVVar -= EV*EV;

			if (EVVar < 0) {
				if (EVVar < -0.001) {
					throw new IllegalStateException("Rounding error is too big.");
				}
				EVVar = 0;
			}

			this.EVGaussian = new Gaussian(EV, EVVar);
		}

	}

	private static class DecisionStrategy extends MaxDistributionPlusBackPropStrategy {

		private final DecisionNode node;

		private int nbSamples = 0;
		private Gaussian EVGaussian = startGaussian;

		public DecisionStrategy(DecisionNode node) {
			this.node = node;
		}

		@Override
		public double getEV() {
			return EVGaussian.mean;
		}

		@Override
		public int getNbSamples() {
			return nbSamples;
		}

		@Override
		public double getStdDev() {
			throw new UnsupportedOperationException();
		}

		@Override
		public double getVariance() {
			throw new UnsupportedOperationException();
		}

		@Override
		public double getEVStdDev() {
			return EVGaussian.getStdDev();
		}

		@Override
		public double getEVVar() {
			return EVGaussian.variance;
		}

		@Override
		public int getNbSamplesInMean() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void onBackPropagate(double value) {
			++this.nbSamples;

			ImmutableList<INode> children = node.getChildren();
			Gaussian[] gaussians = new Gaussian[children.size()];
			for (int i = 0; i < children.size(); i++) {
				INode child = children.get(i);
				gaussians[i] = new Gaussian(child.getEV(), child.getEVVar());
			}
			EVGaussian = Gaussian.maxOf(gaussians);
		}
	}
}
