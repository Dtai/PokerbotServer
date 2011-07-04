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

import java.util.Set;

import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.HoldemTableTreeEvent;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.Round;
import bots.mctsbot.common.elements.table.SeatId;
import bots.mctsbot.common.elements.table.TableConfiguration;

import com.biotools.meerkat.Hand;
import com.google.common.collect.ImmutableBiMap;

/**
 * States of the game.
 * 
 */
public interface GameState {

	TableConfiguration getTableConfiguration();

	ImmutableBiMap<SeatId, PlayerId> getSeatMap();

	PlayerState getPlayer(PlayerId playerId);

	Set<PlayerState> getAllSeatedPlayers();

	PlayerId getDealer();

	PlayerId getLastBettor();

	/**
	 * Returns the ID of the player that is next to act or null if nobody should act.
	 */
	PlayerId getNextToAct();

	int getPreviousRoundsPotSize();

	int getRoundPotSize();

	/**
	 * A derived state property that is the sum of the pot this round and previous rounds.
	 */
	int getGamePotSize();

	int getLargestBet();

	int getMinNextRaise();

	Round getRound();

	Hand getCommunityCards();

	GameState getPreviousGameState();

	HoldemTableTreeEvent getLastEvent();

	/**
	 * A derived state property that is the difference between the largest bet and the
	 * current bet of a given player.
	 */
	int getDeficit(PlayerId playerId);

	/**
	 * A derived state property that is the minimum of the player deficit and stack.
	 */
	int getCallValue(PlayerId playerId);

	/**
	 * A derived state property that is the minimum of the minimal raise and stack.
	 */
	int getLowerRaiseBound(PlayerId playerId);

	/**
	 * A derived state property that is the minimum of the minimal raise and stack.
	 */
	int getUpperRaiseBound(PlayerId playerId);

	/**
	 * A derived state property whether the given player has enough money to raise.
	 */
	boolean isAllowedToRaise(PlayerId playerId);

	boolean hasBet();

	int getNbRaises();

	PlayerState getNextActivePlayerAfter(PlayerId playerId);

	PlayerState getNextSeatedPlayerAfter(PlayerId playerId);

	void acceptHistoryVisitor(GameStateVisitor visitor, GameState start);

	void acceptVisitor(GameStateVisitor visitor);

	/**
	 * Get the PlayerState of the only player left for the pot, null if there are multiple left.
	 */
	PlayerState getDefaultWinner();

	PlayerId getBigBlind();

	PlayerId getSmallBlind();

}
