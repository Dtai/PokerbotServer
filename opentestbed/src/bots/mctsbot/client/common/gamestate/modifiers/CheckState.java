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
package bots.mctsbot.client.common.gamestate.modifiers;

import bots.mctsbot.client.common.gamestate.ForwardingGameState;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.GameStateVisitor;
import bots.mctsbot.client.common.playerstate.ForwardingPlayerState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.CheckEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.HoldemTableEvent;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.Round;

public class CheckState extends ForwardingGameState {

	private final CheckEvent checkEvent;

	private final PlayerState playerState;

	private final boolean checkAfterBlindCase;

	private final int newPotSize;

	public CheckState(final GameState gameState, final CheckEvent checkEvent) {
		super(gameState);
		this.checkEvent = checkEvent;
		final PlayerState player = gameState.getPlayer(checkEvent.getPlayerId());

		//case if big blind checks after all opponents called
		//OR if additional blind checks
		checkAfterBlindCase = Round.PREFLOP.equals(gameState.getRound());
		if (checkAfterBlindCase && gameState.getDeficit(checkEvent.getPlayerId()) > 0)
			throw new IllegalStateException("Can't check in the preflop round when you have a deficit to pay.");
		this.newPotSize = super.getRoundPotSize();

		playerState = new ForwardingPlayerState(player) {

			@Override
			public boolean hasChecked() {
				//don't mind checkAfterBlindCase because hasChecked isn't used in the preflop round?
				return true;
			}

			@Override
			public int getBet() {
				if (checkAfterBlindCase)
					return super.getBet();
				return 0;
			}

			@Override
			public PlayerId getPlayerId() {
				return checkEvent.getPlayerId();
			}

			@Override
			public boolean hasFolded() {
				return false;
			}

			@Override
			public boolean hasBeenDealt() {
				return true;
			}

		};

	}

	@Override
	public PlayerState getPlayer(PlayerId playerId) {
		if (checkEvent.getPlayerId().equals(playerId)) {
			return playerState;
		}
		return super.getPlayer(playerId);
	}

	public HoldemTableEvent getLastEvent() {
		return checkEvent;
	}

	@Override
	public int getLargestBet() {
		if (checkAfterBlindCase) {
			return super.getLargestBet();
		} else {
			return 0;
		}
	}

	@Override
	public PlayerId getLastBettor() {
		if (checkAfterBlindCase)
			return super.getLastBettor();
		return null;
	}

	@Override
	public int getNbRaises() {
		return 0;
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitCheckState(this);
	}

	public CheckEvent getEvent() {
		return checkEvent;
	}

	@Override
	public int getRoundPotSize() {
		return newPotSize;
	}

}
