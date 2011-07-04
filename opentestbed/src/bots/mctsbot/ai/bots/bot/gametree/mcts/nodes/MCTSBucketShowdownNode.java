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
import bots.mctsbot.ai.bots.bot.gametree.rollout.BucketRollOut;
import bots.mctsbot.ai.opponentmodels.OpponentModel;
import bots.mctsbot.client.common.gamestate.GameState;

public class MCTSBucketShowdownNode extends ShowdownNode {

	public final BucketRollOut rollout;

	public final int stackSize;

	public MCTSBucketShowdownNode(GameState gameState, InnerNode parent, ProbabilityAction probAction, OpponentModel model) {
		super(parent, probAction);
		this.rollout = new BucketRollOut(gameState, parent.bot, model);
		this.stackSize = rollout.botState.getStack();
	}

	@Override
	public double simulate() {
		return stackSize + rollout.doRollOut(4);
	}

	@Override
	public GameState getGameState() {
		return rollout.gameState;
	}

	public static class Factory implements ShowdownNode.Factory {

		@Override
		public MCTSBucketShowdownNode create(GameState gameState, InnerNode parent, ProbabilityAction probAction) {
			return new MCTSBucketShowdownNode(gameState, parent, probAction, parent.getConfig().getModel());
		}

	}

}
