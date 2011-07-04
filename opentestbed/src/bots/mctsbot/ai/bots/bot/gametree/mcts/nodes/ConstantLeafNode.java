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
import bots.mctsbot.client.common.gamestate.GameState;

public class ConstantLeafNode extends LeafNode {

	public final int value;
	private int nbSamples = 0;

	public ConstantLeafNode(InnerNode parent, ProbabilityAction lastAction, int value) {
		super(parent, lastAction);
		this.value = value;
	}

	@Override
	public double getEV() {
		return value;
	}

	@Override
	public double getStdDev() {
		return 0;
	}

	@Override
	public double getEVStdDev() {
		return 0;
	}

	@Override
	public double getEVVar() {
		return 0;
	}

	@Override
	public double getVariance() {
		return 0;
	}

	@Override
	public int getNbSamples() {
		return nbSamples;
	}

	@Override
	public int getNbSamplesInMean() {
		return nbSamples;
	}

	@Override
	public double simulate() {
		return value;
	}

	@Override
	public void backPropagate(double value) {
		++nbSamples;
		parent.backPropagate(value);
	}

	@Override
	public GameState getGameState() {
		return parent.gameState;
	}
}
