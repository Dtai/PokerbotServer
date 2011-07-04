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

import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.INode;
import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.InnerNode;

import com.google.common.collect.ImmutableList;

public abstract class MaxFunctionSelector implements SelectionStrategy {

	@Override
	public INode select(InnerNode innerNode) {
		ImmutableList<INode> children = innerNode.getChildren();
		INode maxNode = null;
		double maxValue = Double.NEGATIVE_INFINITY;
		for (INode node : children) {
			double value = evaluate(node);
			if (value > maxValue) {
				maxValue = value;
				maxNode = node;
			}
		}
		if (maxNode == null) {
			//fall back on max value selector which can't fail;
			return (new MaxValueSelector()).select(innerNode);
		}
		return maxNode;
	}

	protected abstract double evaluate(INode node);

}
