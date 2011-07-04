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

import java.util.HashSet;
import java.util.Set;

import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.SeatId;

import com.google.common.collect.ImmutableBiMap;

/**
 * Abstract GameState partial implementation. Only methods that are a simple
 * combination of other methods should be implemented here. This is the only
 * place where you can safely call other methods in the same state.
 * 
 * @author guy
 */
public abstract class AbstractGameState implements GameState {

	public final int getDeficit(PlayerId playerId) {
		return getLargestBet() - getPlayer(playerId).getBet();
	}

	public final int getCallValue(PlayerId playerId) {
		PlayerState player = getPlayer(playerId);
		return Math.min(getLargestBet() - player.getBet(), player.getStack());
	}

	public final boolean isAllowedToRaise(PlayerId playerId) {
		PlayerState player = getPlayer(playerId);
		if (getLargestBet() - player.getBet() >= player.getStack()) {
			return false;
		}
		Set<PlayerState> otherPlayers = getAllSeatedPlayers();
		for (PlayerState otherPlayer : otherPlayers) {
			// check whether we are the only active player left in the game.
			if (!otherPlayer.getPlayerId().equals(playerId) && otherPlayer.isActivelyPlaying()) {
				return true;
			}
		}
		return false;
	}

	public final int getLowerRaiseBound(PlayerId playerId) {
		PlayerState player = getPlayer(playerId);
		return Math.max(0, Math.min(getMinNextRaise(), player.getStack() - (getLargestBet() - player.getBet())));
	}

	public final int getUpperRaiseBound(PlayerId playerId) {
		PlayerState player = getPlayer(playerId);
		PlayerState tempPlayer;
		PlayerId tempId = playerId;
		int maxOtherBettableChips = 0;
		loop: do {
			//TODO fix infinite loop on double BB
			tempPlayer = getNextActivePlayerAfter(tempId);
			if (tempPlayer == null) {
				break loop;
			}
			tempId = tempPlayer.getPlayerId();
			if (!tempPlayer.getPlayerId().equals(playerId)) {
				maxOtherBettableChips = Math.max(maxOtherBettableChips, tempPlayer.getBet() + tempPlayer.getStack());
			} else {
				break loop;
			}
		} while (true);
		int betableChips = Math.min(player.getStack() + player.getBet(), maxOtherBettableChips);
		return Math.max(0, betableChips - getLargestBet());
	}

	public final int getGamePotSize() {
		return getPreviousRoundsPotSize() + getRoundPotSize();
	}

	public final boolean hasBet() {
		return getLargestBet() > 0;
	}

	public final Set<PlayerState> getAllSeatedPlayers() {
		Set<PlayerId> ids = getSeatMap().values();
		HashSet<PlayerState> states = new HashSet<PlayerState>();
		for (PlayerId id : ids) {
			states.add(getPlayer(id));
		}
		return states;
	}

	@Override
	public final PlayerState getDefaultWinner() {
		Set<PlayerId> ids = getSeatMap().values();
		PlayerState first = null;
		for (PlayerId id : ids) {
			PlayerState state = getPlayer(id);
			if (!state.hasFolded()) {
				if (first != null) {
					return null;
				} else {
					first = state;
				}
			}
		}
		return first;
	}

	public final PlayerState getNextSeatedPlayerAfter(PlayerId startPlayer) {
		ImmutableBiMap<SeatId, PlayerId> seatMap = getSeatMap();
		int maxNbPlayers = getTableConfiguration().getMaxNbPlayers();
		SeatId currentSeat = seatMap.inverse().get(startPlayer);
		PlayerId currentPlayer;
		do {
			currentSeat = new SeatId((currentSeat.getId() + 1) % maxNbPlayers);
			currentPlayer = seatMap.get(currentSeat);
		} while (currentPlayer == null);
		if (currentPlayer.equals(startPlayer)) {
			return null;
		}
		return getPlayer(currentPlayer);
	}

	public final PlayerState getNextActivePlayerAfter(PlayerId startPlayerId) {
		PlayerState currentPlayer;
		PlayerId currentPlayerId = startPlayerId;
		do {
			currentPlayer = getNextSeatedPlayerAfter(currentPlayerId);
			if (currentPlayer == null) {
				return null;
			}
			currentPlayerId = currentPlayer.getPlayerId();
			if (currentPlayerId.equals(startPlayerId)) {
				return null;
			}
		} while (!currentPlayer.isActivelyPlaying());
		return currentPlayer;
	}

	@Override
	public void acceptHistoryVisitor(GameStateVisitor visitor, GameState start) {
		if (this != start) {
			getPreviousGameState().acceptHistoryVisitor(visitor, start);
			acceptVisitor(visitor);
		}
	}

	@Override
	public String toString() {
		return getLastEvent() + "\n" + getPreviousGameState();
	}

}
