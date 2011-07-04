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
import bots.mctsbot.client.common.gamestate.modifiers.AllInState;
import bots.mctsbot.client.common.gamestate.modifiers.NextPlayerState;
import bots.mctsbot.client.common.gamestate.modifiers.RaiseState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.AllInEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.NextPlayerEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.RaiseEvent;
import bots.mctsbot.common.api.lobby.holdemtable.holdemplayer.context.RemoteHoldemPlayerContext;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.Util;

public class RaiseAction extends SearchBotAction {

	public final int amount;

	public RaiseAction(GameState gameState, PlayerId actor, int amount) {
		super(gameState, actor);
		this.amount = amount;
	}

	@Override
	public void perform(RemoteHoldemPlayerContext context) {
		context.betOrRaise(amount);
	}

	@Override
	public GameState getStateAfterAction() {
		PlayerState actorState = gameState.getPlayer(actor);
		int stack = actorState.getStack();
		int oldBet = actorState.getBet();
		int largestBet = gameState.getLargestBet();
		int deficit = largestBet - oldBet;
		int movedAmount = deficit + amount;

		GameState raiseState;
		if (movedAmount >= stack) {
			raiseState = new AllInState(gameState, new AllInEvent(actor, movedAmount));
		} else {
			raiseState = new RaiseState(gameState, new RaiseEvent(actor, amount, movedAmount));
		}
		return new NextPlayerState(raiseState, new NextPlayerEvent(raiseState.getNextActivePlayerAfter(actor).getPlayerId()));
	}

	@Override
	public String toString() {
		PlayerState actorState = gameState.getPlayer(actor);
		int stack = actorState.getStack();
		int oldBet = actorState.getBet();
		int largestBet = gameState.getLargestBet();
		int deficit = largestBet - oldBet;
		int movedAmount = deficit + amount;

		if (movedAmount >= stack) {
			return "Raise " + Util.parseDollars(amount) + " (all-in)";
		} else {
			return "Raise " + Util.parseDollars(amount);
		}
	}

}
