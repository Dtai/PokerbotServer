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
package bots.mctsbot.ai.bots.bot.gametree.search.nodevisitor;

import bots.mctsbot.ai.bots.bot.gametree.action.ActionWrapper;
import bots.mctsbot.ai.bots.bot.gametree.search.Distribution;
import bots.mctsbot.ai.bots.bot.gametree.search.GameTreeNode;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.Pair;

public interface NodeVisitor {

	void enterNode(Pair<ActionWrapper, GameTreeNode> node, double lowerBound);

	void leaveNode(Pair<ActionWrapper, GameTreeNode> node, Distribution distribution);

	void pruneSubTree(Pair<ActionWrapper, GameTreeNode> node, Distribution distribution, double lowerBound);

	void visitLeafNode(int winnings, double probability, int minWinnable, int maxWinnable);

	void callOpponentModel();

	public static interface Factory {

		NodeVisitor create(GameState gameState, PlayerId actor);

	}

}
