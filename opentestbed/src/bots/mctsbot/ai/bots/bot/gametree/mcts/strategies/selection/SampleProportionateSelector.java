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

import com.google.common.collect.ImmutableList;

public class SampleProportionateSelector implements SelectionStrategy {

	private final static Random random = new Random();

	@Override
	public INode select(InnerNode innerNode) {
		ImmutableList<INode> children = innerNode.getChildren();
		int[] cumulSums = new int[children.size()];
		int cumulSum = 0;
		for (int i = 0; i < children.size(); i++) {
			int nbSamples = children.get(i).getNbSamples();
			cumulSum += nbSamples;
			cumulSums[i] = cumulSum;
		}
		int randVar = random.nextInt(cumulSum);
		for (int i = 0; i < cumulSums.length; i++) {
			if (randVar < cumulSums[i]) {
				return children.get(i);
			}
		}
		return children.get(cumulSums.length - 1);
	}

}
