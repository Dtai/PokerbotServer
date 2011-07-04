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
package bots.mctsbot.ai.bots.bot.gametree.mcts.nodes;

import bots.mctsbot.ai.bots.bot.gametree.action.ProbabilityAction;
import bots.mctsbot.ai.bots.util.RunningStats;
import bots.mctsbot.client.common.gamestate.GameState;

public abstract class ShowdownNode extends LeafNode {

	//stats
	protected final RunningStats stats = new RunningStats();

	public ShowdownNode(InnerNode parent, ProbabilityAction probAction) {
		super(parent, probAction);
	}

	@Override
	public double getEV() {
		return stats.getMean();
	}

	@Override
	public double getStdDev() {
		return stats.getStdDev();
	}

	@Override
	public double getVariance() {
		return stats.getVariance();
	}

	@Override
	public int getNbSamples() {
		return stats.getNbSamples();
	}

	@Override
	public double getEVStdDev() {
		return stats.getEVStdDev();
	}

	@Override
	public double getEVVar() {
		return stats.getEVStdDev();
	}

	@Override
	public int getNbSamplesInMean() {
		return stats.getNbSamples();
	}

	@Override
	public void backPropagate(double value) {
		stats.add(value);
		parent.backPropagate(value);
	}

	public static interface Factory {

		LeafNode create(GameState gameState, InnerNode parent, ProbabilityAction probAction);

	}

}
