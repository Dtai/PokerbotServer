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

import org.apache.log4j.Logger;

import bots.mctsbot.ai.bots.bot.gametree.action.ActionWrapper;
import bots.mctsbot.ai.bots.bot.gametree.search.Distribution;
import bots.mctsbot.ai.bots.bot.gametree.search.GameTreeNode;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.Pair;

public class Log4JOutputVisitor extends TextOutputVisitor {

	private final static Logger logger = Logger.getLogger(Log4JOutputVisitor.class);

	public Log4JOutputVisitor() {
		super();
	}

	public Log4JOutputVisitor(int maxDepth) {
		super(maxDepth);
	}

	@Override
	public void enterNode(Pair<ActionWrapper, GameTreeNode> node, double lowerBound) {
		if (logger.isDebugEnabled()) {
			super.enterNode(node, lowerBound);
		}
	}

	@Override
	public void leaveNode(Pair<ActionWrapper, GameTreeNode> node, Distribution distr) {
		if (logger.isDebugEnabled()) {
			super.leaveNode(node, distr);
		}
	}

	@Override
	public void pruneSubTree(Pair<ActionWrapper, GameTreeNode> node, Distribution distribution, double lowerBound) {
		if (logger.isDebugEnabled()) {
			super.pruneSubTree(node, distribution, lowerBound);
		}
	}

	@Override
	protected void output(String line) {
		logger.debug(line);
	}

	public static class Factory implements NodeVisitor.Factory {

		private final int maxDepth;

		public Factory(int maxDepth) {
			this.maxDepth = maxDepth;
		}

		@Override
		public Log4JOutputVisitor create(GameState gameState, PlayerId actor) {
			return new Log4JOutputVisitor(maxDepth);
		}

	}

}
