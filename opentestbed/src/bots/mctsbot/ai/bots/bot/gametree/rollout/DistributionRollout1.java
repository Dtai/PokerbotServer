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
package bots.mctsbot.ai.bots.bot.gametree.rollout;

import bots.mctsbot.ai.bots.bot.gametree.rollout.rankdistribution.ShowdownRankPredictor1of1;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;

public class DistributionRollout1 extends AbstractDistributionRollout {

	DistributionRollout1(GameState gameState, PlayerId botId) {
		super(gameState, botId);
	}

	@Override
	protected float getRelativeProbability(int rank, int relativePotSize) {
		return ShowdownRankPredictor1of1.getRelativeProbability(rank);
	}

	@Override
	public String toString() {
		return "1 Part Distribution Rollout";
	}

	public static class Factory implements AbstractDistributionRollout.Factory {

		@Override
		public DistributionRollout1 create(GameState gameState, PlayerId botId) {
			return new DistributionRollout1(gameState, botId);
		}

	}

}
