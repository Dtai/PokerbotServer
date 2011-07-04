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
import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.OpponentNode;

public class MixedBackPropStrategy implements BackPropagationStrategy {

	private final BackPropagationStrategy first;
	private final BackPropagationStrategy second;
	private final int treshold;

	private MixedBackPropStrategy(int treshold, BackPropagationStrategy first, BackPropagationStrategy second) {
		this.first = first;
		this.second = second;
		this.treshold = treshold;
	}

	public BackPropagationStrategy getCurrent() {
		return (first.getNbSamples() < treshold) ? first : second;
	}

	@Override
	public double getEV() {
		return getCurrent().getEV();
	}

	@Override
	public double getEVStdDev() {
		return getCurrent().getEVStdDev();
	}

	@Override
	public double getEVVar() {
		return getCurrent().getEVVar();
	}

	@Override
	public int getNbSamples() {
		return getCurrent().getNbSamples();
	}

	@Override
	public int getNbSamplesInMean() {
		return getCurrent().getNbSamplesInMean();
	}

	@Override
	public double getStdDev() {
		return getCurrent().getStdDev();
	}

	@Override
	public double getVariance() {
		return getCurrent().getVariance();
	}

	@Override
	public void onBackPropagate(double value) {
		if (first.getNbSamples() < treshold) {
			first.onBackPropagate(value);
		}
		second.onBackPropagate(value);
	}

	public static class Factory implements BackPropagationStrategy.Factory {

		private final BackPropagationStrategy.Factory first;
		private final BackPropagationStrategy.Factory second;
		private final int treshold;

		public Factory(int treshold, BackPropagationStrategy.Factory first, BackPropagationStrategy.Factory second) {
			this.first = first;
			this.second = second;
			this.treshold = treshold;
		}

		@Override
		public BackPropagationStrategy createForDecisionNode(DecisionNode node) {
			return new MixedBackPropStrategy(treshold, first.createForDecisionNode(node), second.createForDecisionNode(node));
		}

		@Override
		public BackPropagationStrategy createForOpponentNode(OpponentNode node) {
			return new MixedBackPropStrategy(treshold, first.createForOpponentNode(node), second.createForOpponentNode(node));
		}

	}

}
