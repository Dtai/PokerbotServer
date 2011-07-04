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
package bots.mctsbot.ai.bots.bot.gametree.action;

import java.util.Set;

import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.modifiers.AllInState;
import bots.mctsbot.client.common.gamestate.modifiers.CallState;
import bots.mctsbot.client.common.gamestate.modifiers.NextPlayerState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.AllInEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.CallEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.NextPlayerEvent;
import bots.mctsbot.common.api.lobby.holdemtable.holdemplayer.context.RemoteHoldemPlayerContext;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.Round;

public class CallAction extends SearchBotAction {

	public CallAction(GameState gameState, PlayerId actor) {
		super(gameState, actor);
	}

	@Override
	public void perform(RemoteHoldemPlayerContext context) {
		context.checkOrCall();
	}

	@Override
	public GameState getStateAfterAction() throws GameEndedException {
		boolean roundEnds = true;
		Set<PlayerState> players = gameState.getAllSeatedPlayers();
		forloop: for (PlayerState player : players) {
			if (player.isActivelyPlaying() && !player.getPlayerId().equals(actor) && gameState.getDeficit(player.getPlayerId()) > 0) {
				roundEnds = false;
				break forloop;
			}
		}

		PlayerState actorState = gameState.getPlayer(actor);
		int largestBet = gameState.getLargestBet();
		int stack = actorState.getStack();
		int bet = actorState.getBet();

		// what if small or big blind all-in?
		if (roundEnds && gameState.getRound().equals(Round.PREFLOP) && actor.equals(gameState.getSmallBlind())
				&& largestBet <= gameState.getTableConfiguration().getBigBlind()) {
			roundEnds = false;
		}

		GameState state;
		if (stack <= largestBet - bet) {
			state = new AllInState(gameState, new AllInEvent(actor, stack));
		} else {
			state = new CallState(gameState, new CallEvent(actor, largestBet - bet));
		}
		if (roundEnds) {
			return getNewRoundState(state);
		} else {
			PlayerState nextActivePlayerAfter = state.getNextActivePlayerAfter(actor);
			if (nextActivePlayerAfter == null) {
				// BigBlind is all-in
				return getNewRoundState(state);
			}
			return new NextPlayerState(state, new NextPlayerEvent(nextActivePlayerAfter.getPlayerId()));
		}
	}

	@Override
	public String toString() {
		return "Call";
	}

}
