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
package bots.mctsbot.client.common.gamestate;

import java.util.concurrent.ConcurrentMap;

import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.HoldemTableTreeEvent;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.Round;
import bots.mctsbot.common.elements.table.SeatId;
import bots.mctsbot.common.elements.table.TableConfiguration;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.MapMaker;

public class CachingNode extends ForwardingGameState {

	private final static MapMaker mapmaker = new MapMaker().initialCapacity(8).concurrencyLevel(3);

	private final Round round = super.getRound();

	private final TableConfiguration tableConfig = super.getTableConfiguration();

	private final ImmutableBiMap<SeatId, PlayerId> seatMap = super.getSeatMap();

	private final PlayerId dealer = super.getDealer();

	private final ConcurrentMap<PlayerId, PlayerState> playerStates = mapmaker.makeComputingMap(new Function<PlayerId, PlayerState>() {
		@Override
		public PlayerState apply(PlayerId playerId) {
			return CachingNode.super.getPlayer(playerId);
		}
	});

	public CachingNode(GameState gameState) {
		super(gameState);
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		//no op
	}

	@Override
	public HoldemTableTreeEvent getLastEvent() {
		return null;
	}

	@Override
	public Round getRound() {
		return round;
	}

	@Override
	public TableConfiguration getTableConfiguration() {
		return tableConfig;
	}

	@Override
	public String toString() {
		return "(Cached) " + getPreviousGameState();
	}

	@Override
	public PlayerState getPlayer(PlayerId playerId) {
		return playerStates.get(playerId);
	}

	@Override
	public ImmutableBiMap<SeatId, PlayerId> getSeatMap() {
		return seatMap;
	}

	@Override
	public PlayerId getDealer() {
		return dealer;
	}

}
