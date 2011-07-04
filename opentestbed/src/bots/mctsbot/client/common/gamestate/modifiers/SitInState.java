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

import org.apache.log4j.Logger;

import bots.mctsbot.client.common.gamestate.ForwardingGameState;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.GameStateVisitor;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.client.common.playerstate.SeatedPlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.HoldemTableEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.SitInEvent;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.SeatId;

import com.google.common.collect.ImmutableBiMap;

public class SitInState extends ForwardingGameState {

	private final static Logger logger = Logger.getLogger(SitInState.class);

	private final SitInEvent event;

	private final PlayerState playerState;

	private final ImmutableBiMap<SeatId, PlayerId> seatMap;

	public SitInState(GameState gameState, SitInEvent event) {
		super(gameState);
		this.event = event;
		playerState = new SeatedPlayerState(event.getPlayer());
		seatMap = (new ImmutableBiMap.Builder<SeatId, PlayerId>()).putAll(super.getSeatMap()).put(playerState.getSeatId(), playerState.getPlayerId()).build();
	}

	@Override
	public PlayerState getPlayer(PlayerId playerId) {
		if (playerState.getPlayerId().equals(playerId))
			return playerState;
		else
			return super.getPlayer(playerId);
	}

	@Override
	public ImmutableBiMap<SeatId, PlayerId> getSeatMap() {
		return seatMap;
	}

	public HoldemTableEvent getLastEvent() {
		return event;
	}

	public SitInEvent getEvent() {
		return event;
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitSitInState(this);
	}

}
