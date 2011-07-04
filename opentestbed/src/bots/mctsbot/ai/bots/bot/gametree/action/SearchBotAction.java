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
import bots.mctsbot.client.common.gamestate.modifiers.NewRoundState;
import bots.mctsbot.client.common.gamestate.modifiers.NextPlayerState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.NewRoundEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.NextPlayerEvent;
import bots.mctsbot.common.api.lobby.holdemtable.holdemplayer.context.RemoteHoldemPlayerContext;
import bots.mctsbot.common.elements.chips.Pots;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.Round;

public abstract class SearchBotAction implements ActionWrapper {

	public final GameState gameState;
	public final PlayerId actor;

	public SearchBotAction(GameState gameState, PlayerId actor) {
		this.gameState = gameState;
		this.actor = actor;
	}

	@Override
	public SearchBotAction getAction() {
		return this;
	}

	public abstract void perform(RemoteHoldemPlayerContext context);

	public abstract GameState getStateAfterAction() throws GameEndedException, DefaultWinnerException;

	protected GameState getNewRoundState(GameState lastState) throws GameEndedException {
		Round nextRound = lastState.getRound().getNextRound();
		if (nextRound == null) {
			throw new GameEndedException(lastState);
		}
		//		List<Pot> pots = Collections.emptyList();
		NewRoundState newRoundState = new NewRoundState(lastState, new NewRoundEvent(nextRound, new Pots(lastState.getGamePotSize())));
		PlayerState firstToAct = newRoundState.getNextActivePlayerAfter(newRoundState.getDealer());
		if (firstToAct == null || newRoundState.getNextActivePlayerAfter(firstToAct.getPlayerId()) == null) {
			// no one/only one left
			return getNewRoundState(newRoundState);
		}
		return new NextPlayerState(newRoundState, new NextPlayerEvent(firstToAct.getPlayerId()));
	}

	public boolean endsInvolvementOf(PlayerId botId) {
		return false;
	}

	// do not define equals or hashcode!
	// it will map all actions without extra fields to the same entity

}
