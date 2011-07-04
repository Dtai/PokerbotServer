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

import java.util.Collections;
import java.util.List;

import bots.mctsbot.client.common.gamestate.ForwardingGameState;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.GameStateVisitor;
import bots.mctsbot.client.common.playerstate.ForwardingPlayerState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.BlindEvent;
import bots.mctsbot.common.elements.player.PlayerId;

public class BlindState extends ForwardingGameState {

	private final BlindEvent event;
	private final int newPot;
	private final PlayerState playerState;

	public BlindState(GameState gameState, BlindEvent event) {
		super(gameState);
		this.event = event;
		PlayerState oldPlayerState = super.getPlayer(event.getPlayerId());
		final int newStack = oldPlayerState.getStack() - event.getAmount();
		this.newPot = super.getRoundPotSize() + event.getAmount();

		playerState = new ForwardingPlayerState(oldPlayerState) {

			@Override
			public int getBet() {
				return BlindState.this.event.getAmount();
			}

			@Override
			public int getTotalInvestment() {
				return getBet();
			}

			@Override
			public int getStack() {
				return newStack;
			}

			@Override
			public boolean hasBeenDealt() {
				return true;
			}

			@Override
			public boolean hasFolded() {
				return false;
			}

			@Override
			public PlayerId getPlayerId() {
				return BlindState.this.event.getPlayerId();
			}

			@Override
			public List<Integer> getBetProgression() {
				return Collections.singletonList(getBet());
			}

			@Override
			public boolean hasChecked() {
				return false;
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
		return event.getAmount();
	}

	@Override
	public int getMinNextRaise() {
		return getTableConfiguration().getSmallBet();
	}

	@Override
	public int getRoundPotSize() {
		return newPot;
	}

	public BlindEvent getLastEvent() {
		return event;
	}

	@Override
	public PlayerId getLastBettor() {
		return event.getPlayerId();
	}

	@Override
	public int getNbRaises() {
		return 0;
	}

	//BEWARE! Sometime blinds can be posted by other people than the BB or SB! (dead blinds?)

	@Override
	public PlayerId getBigBlind() {
		if (super.getBigBlind() == null && getTableConfiguration().getBigBlind() == BlindState.this.event.getAmount())
			return playerState.getPlayerId();
		else
			return super.getBigBlind();
	}

	@Override
	public PlayerId getSmallBlind() {
		if (super.getBigBlind() == null && getTableConfiguration().getSmallBlind() == BlindState.this.event.getAmount())
			return playerState.getPlayerId();
		else
			return super.getSmallBlind();
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitBlindState(this);
	}

	public BlindEvent getEvent() {
		return event;
	}
}
