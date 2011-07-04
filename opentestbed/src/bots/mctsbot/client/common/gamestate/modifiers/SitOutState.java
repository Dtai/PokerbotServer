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

import java.util.Map;

import bots.mctsbot.client.common.gamestate.ForwardingGameState;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.GameStateVisitor;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.HoldemTableEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.SitOutEvent;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.SeatId;

import com.google.common.collect.ImmutableBiMap;

public class SitOutState extends ForwardingGameState {

	private final SitOutEvent event;
	private final PlayerId playerId;
	private final ImmutableBiMap<SeatId, PlayerId> seatMap;

	public SitOutState(GameState gameState, SitOutEvent event) {
		super(gameState);
		this.event = event;
		this.playerId = event.getPlayerId();

		ImmutableBiMap.Builder<SeatId, PlayerId> seatMapBuilder = new ImmutableBiMap.Builder<SeatId, PlayerId>();
		for (Map.Entry<SeatId, PlayerId> entry : super.getSeatMap().entrySet()) {
			if (!entry.getValue().equals(playerId)) {
				seatMapBuilder.put(entry.getKey(), entry.getValue());
			}
		}
		seatMap = seatMapBuilder.build();
	}

	@Override
	public PlayerState getPlayer(PlayerId playerId) {
		if (this.playerId.equals(playerId))
			return null;
		return super.getPlayer(playerId);
	}

	@Override
	public ImmutableBiMap<SeatId, PlayerId> getSeatMap() {
		return seatMap;
	}

	public HoldemTableEvent getLastEvent() {
		return event;
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitSitOutState(this);
	}

}
