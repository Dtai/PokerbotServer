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
package bots.mctsbot.ai.bots.bot.gametree.mcts.strategies.selection;

import java.util.Random;

import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.INode;
import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.InnerNode;

public class MixedSelectionStrategy implements SelectionStrategy {

	private final static Random r = new Random();
	private final SelectionStrategy strat1;
	private final SelectionStrategy strat2;
	private final double prob1;

	public MixedSelectionStrategy(SelectionStrategy strat1, SelectionStrategy strat2, double prob1) {
		this.strat1 = strat1;
		this.strat2 = strat2;
		this.prob1 = prob1;
	}

	@Override
	public INode select(InnerNode innerNode) {
		if (r.nextDouble() < prob1)
			return strat1.select(innerNode);
		else
			return strat2.select(innerNode);
	}

}
