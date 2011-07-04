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
package bots.mctsbot.ai.opponentmodels;

import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.Pair;
import bots.mctsbot.common.util.Triple;

/**
 * A collection of opponent models related to the same game.
 * 
 * @author guy
 * 
 */
public interface OpponentModel {

	Pair<Double, Double> getCheckBetProbabilities(GameState gameState, PlayerId actor);

	Triple<Double, Double, Double> getFoldCallRaiseProbabilities(GameState gameState, PlayerId actor);

	double[] getShowdownProbabilities(GameState gameState, PlayerId actor) throws UnsupportedOperationException;

	/**
	 * Assume the given game state permanently.
	 */
	void assumePermanently(GameState gameState);

	/**
	 * Assume the given game state temporarily.
	 */
	void assumeTemporarily(GameState gameState);

	/**
	 * Forget the last assumption.
	 */
	void forgetLastAssumption();

	static interface Factory {

		OpponentModel create();

	}
}