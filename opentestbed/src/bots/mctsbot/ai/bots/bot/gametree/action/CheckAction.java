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

import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.modifiers.CheckState;
import bots.mctsbot.client.common.gamestate.modifiers.NextPlayerState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.CheckEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.NextPlayerEvent;
import bots.mctsbot.common.api.lobby.holdemtable.holdemplayer.context.RemoteHoldemPlayerContext;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.Round;

public class CheckAction extends SearchBotAction {

	public CheckAction(GameState gameState, PlayerId actor) {
		super(gameState, actor);
	}

	@Override
	public void perform(RemoteHoldemPlayerContext context) {
		context.checkOrCall();
	}

	@Override
	public GameState getStateAfterAction() throws GameEndedException {
		PlayerState nextToAct = gameState.getNextActivePlayerAfter(actor);
		// if bigblind is all-in, he shouldn't check again, so we're safe
		boolean newRound = nextToAct.hasChecked() || gameState.getRound().equals(Round.PREFLOP) && actor.equals(gameState.getBigBlind())
				&& gameState.getLargestBet() <= gameState.getTableConfiguration().getBigBlind();

		CheckState checkState = new CheckState(gameState, new CheckEvent(actor));
		if (!newRound) {
			return new NextPlayerState(checkState, new NextPlayerEvent(nextToAct.getPlayerId()));
		}
		return getNewRoundState(checkState);
	}

	@Override
	public String toString() {
		return "Check";
	}

}
