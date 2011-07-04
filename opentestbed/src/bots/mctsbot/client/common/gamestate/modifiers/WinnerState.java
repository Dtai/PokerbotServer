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
import bots.mctsbot.common.api.lobby.holdemtable.event.WinnerEvent;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.player.Winner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class WinnerState extends ForwardingGameState {

	private final WinnerEvent event;

	private final ImmutableMap<PlayerId, Integer> gained;

	public WinnerState(GameState gameState, WinnerEvent event) {
		super(gameState);
		this.event = event;
		Builder<PlayerId, Integer> gainedBuilder = ImmutableMap.builder();
		for (Winner winner : event.getWinners()) {
			gainedBuilder.put(winner.getPlayerId(), winner.getGainedAmount());
		}
		gained = gainedBuilder.build();
	}

	@Override
	public PlayerState getPlayer(final PlayerId playerId) {
		return new ForwardingPlayerState(super.getPlayer(playerId)) {
			@Override
			public int getBet() {
				return 0;
			}

			@Override
			public boolean hasBeenDealt() {
				return true;
			}

			@Override
			public int getStack() {
				Integer gainedValue;
				if ((gainedValue = gained.get(playerId)) != null) {
					return super.getStack() + gainedValue;
				}
				return super.getStack();
			}

			@Override
			public PlayerId getPlayerId() {
				return playerId;
			}

		};
	}

	@Override
	public int getPreviousRoundsPotSize() {
		return 0;
	}

	@Override
	public int getRoundPotSize() {
		return 0;
	}

	public WinnerEvent getLastEvent() {
		return event;
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitWinnerState(this);
	}

}
