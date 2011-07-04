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
import bots.mctsbot.client.common.gamestate.modifiers.FoldState;
import bots.mctsbot.client.common.gamestate.modifiers.NextPlayerState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.FoldEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.NextPlayerEvent;
import bots.mctsbot.common.api.lobby.holdemtable.holdemplayer.context.RemoteHoldemPlayerContext;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.Round;

public class FoldAction extends SearchBotAction {

	public FoldAction(GameState gameState, PlayerId actor) {
		super(gameState, actor);
	}

	@Override
	public void perform(RemoteHoldemPlayerContext context) {
		context.fold();
	}

	@Override
	public GameState getStateAfterAction() throws GameEndedException, DefaultWinnerException {
		boolean roundEnds = true;
		Set<PlayerState> players = gameState.getAllSeatedPlayers();
		PlayerState first = null;
		boolean noDefaultWinner = false;
		// TODO use getNbPlayers()
		forloop: for (PlayerState player : players) {
			if (roundEnds && player.isActivelyPlaying() && !player.getPlayerId().equals(actor) && gameState.getDeficit(player.getPlayerId()) > 0) {
				roundEnds = false;
			}
			if (!noDefaultWinner && !player.getPlayerId().equals(actor) && !player.hasFolded()) {
				if (first != null) {
					noDefaultWinner = true;
				} else {
					first = player;
				}
			}
			if (noDefaultWinner && !roundEnds) {
				break forloop;
			}
		}
		if (!noDefaultWinner) {
			throw new DefaultWinnerException(first, new FoldState(gameState, new FoldEvent(actor)));
		}
		if (roundEnds && gameState.getRound().equals(Round.PREFLOP) && actor.equals(gameState.getSmallBlind())
				&& gameState.getLargestBet() <= gameState.getTableConfiguration().getBigBlind()) {
			roundEnds = false;
		}

		FoldState foldState = new FoldState(gameState, new FoldEvent(actor));

		if (!roundEnds) {
			return new NextPlayerState(foldState, new NextPlayerEvent(foldState.getNextActivePlayerAfter(actor).getPlayerId()));
		}
		return getNewRoundState(foldState);
	}

	@Override
	public boolean endsInvolvementOf(PlayerId botId) {
		return actor.equals(botId);
	}

	@Override
	public String toString() {
		return "Fold";
	}

}
