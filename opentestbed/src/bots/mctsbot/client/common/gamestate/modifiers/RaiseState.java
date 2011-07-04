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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bots.mctsbot.client.common.gamestate.ForwardingGameState;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.GameStateVisitor;
import bots.mctsbot.client.common.playerstate.ForwardingPlayerState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.RaiseEvent;
import bots.mctsbot.common.elements.player.PlayerId;

public class RaiseState extends ForwardingGameState {

	private final RaiseEvent event;
	private final int newBetSize;
	private final int newPotSize;
	private final PlayerState playerState;

	public RaiseState(final GameState gameState, final RaiseEvent event) {
		super(gameState);
		this.event = event;
		final PlayerState oldPlayerState = super.getPlayer(event.getPlayerId());
		this.newBetSize = super.getLargestBet() + event.getAmount();
		final int newStack = oldPlayerState.getStack() - event.getMovedAmount();
		this.newPotSize = super.getRoundPotSize() + event.getMovedAmount();
		playerState = new ForwardingPlayerState(oldPlayerState) {

			@Override
			public int getBet() {
				return RaiseState.this.newBetSize;
			}

			@Override
			public int getTotalInvestment() {
				return super.getTotalInvestment() + event.getMovedAmount();
			}

			@Override
			public PlayerId getPlayerId() {
				return RaiseState.this.event.getPlayerId();
			}

			@Override
			public int getStack() {
				return newStack;
			}

			@Override
			public boolean hasFolded() {
				return false;
			}

			@Override
			public boolean hasChecked() {
				return false;
			}

			@Override
			public boolean hasBeenDealt() {
				return true;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public List<Integer> getBetProgression() {
				List<Integer> result = new ArrayList<Integer>();
				result.addAll(gameState.getPlayer(gameState.getLastBettor()).getBetProgression());
				result.add(event.getAmount());
				return Collections.unmodifiableList(result);
			}
		};
	}

	@Override
	public PlayerState getPlayer(PlayerId playerId) {
		if (event.getPlayerId().equals(playerId)) {
			return playerState;
		}
		return super.getPlayer(playerId);
	}

	@Override
	public int getLargestBet() {
		return newBetSize;
	}

	@Override
	public int getMinNextRaise() {
		return Math.max(super.getMinNextRaise(), event.getAmount());
	}

	@Override
	public int getRoundPotSize() {
		return newPotSize;
	}

	public RaiseEvent getLastEvent() {
		return event;
	}

	@Override
	public PlayerId getLastBettor() {
		return event.getPlayerId();
	}

	@Override
	public int getNbRaises() {
		return super.getNbRaises() + 1;
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitRaiseState(this);
	}

	public RaiseEvent getEvent() {
		return event;
	}

}
