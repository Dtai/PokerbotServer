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

import bots.mctsbot.ai.bots.bot.gametree.rollout.rankdistribution.ShowdownRankPredictor1of4;
import bots.mctsbot.ai.bots.bot.gametree.rollout.rankdistribution.ShowdownRankPredictor2of4;
import bots.mctsbot.ai.bots.bot.gametree.rollout.rankdistribution.ShowdownRankPredictor3of4;
import bots.mctsbot.ai.bots.bot.gametree.rollout.rankdistribution.ShowdownRankPredictor4of4;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;

public class DistributionRollout4 extends AbstractDistributionRollout {

	public DistributionRollout4(GameState gameState, PlayerId botId) {
		super(gameState, botId);
	}

	@Override
	protected float getRelativeProbability(int rank, int relativePotSize) {
		if (relativePotSize <= 4) {
			return ShowdownRankPredictor1of4.getRelativeProbability(rank);
		} else if (relativePotSize <= 15) {
			return ShowdownRankPredictor2of4.getRelativeProbability(rank);
		} else if (relativePotSize <= 30) {
			return ShowdownRankPredictor3of4.getRelativeProbability(rank);
		} else {
			return ShowdownRankPredictor4of4.getRelativeProbability(rank);
		}

	}

	@Override
	public String toString() {
		return "4 Part Distribution Rollout";
	}

	public static class Factory implements AbstractDistributionRollout.Factory {

		@Override
		public DistributionRollout4 create(GameState gameState, PlayerId botId) {
			return new DistributionRollout4(gameState, botId);
		}

	}

}
