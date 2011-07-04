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

import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.Round;
import bots.mctsbot.common.elements.table.SeatId;
import bots.mctsbot.common.elements.table.TableConfiguration;

import com.biotools.meerkat.Hand;
import com.google.common.collect.ImmutableBiMap;

public abstract class ForwardingGameState extends AbstractGameState {

	private final GameState gameState;

	public ForwardingGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public int getLargestBet() {
		return gameState.getLargestBet();
	}

	public int getMinNextRaise() {
		return gameState.getMinNextRaise();
	}

	public int getPreviousRoundsPotSize() {
		return gameState.getPreviousRoundsPotSize();
	}

	public int getRoundPotSize() {
		return gameState.getRoundPotSize();
	}

	public Round getRound() {
		return gameState.getRound();
	}

	public Hand getCommunityCards() {
		return gameState.getCommunityCards();
	}

	public PlayerId getDealer() {
		return gameState.getDealer();
	}

	public GameState getPreviousGameState() {
		return gameState; //no delegation!
	}

	public PlayerId getLastBettor() {
		return gameState.getLastBettor();
	}

	public PlayerId getNextToAct() {
		return gameState.getNextToAct();
	}

	public PlayerState getPlayer(PlayerId playerId) {
		return gameState.getPlayer(playerId);
	}

	@Override
	public ImmutableBiMap<SeatId, PlayerId> getSeatMap() {
		return gameState.getSeatMap();
	}

	public TableConfiguration getTableConfiguration() {
		return gameState.getTableConfiguration();
	}

	@Override
	public PlayerId getBigBlind() {
		return gameState.getBigBlind();
	}

	@Override
	public PlayerId getSmallBlind() {
		return gameState.getSmallBlind();
	}

	@Override
	public int getNbRaises() {
		return gameState.getNbRaises();
	}

}
