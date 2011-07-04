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

package bots.mctsbot.common.api.lobby.holdemtable.event;

import java.util.Collection;
import java.util.Collections;

import bots.mctsbot.common.elements.player.Player;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.player.SeatedPlayer;
import bots.mctsbot.common.util.Util;

/**
 * A class to represent new deal events.
 * 
 */
public class NewDealEvent extends HoldemTableEvent {

	private static final long serialVersionUID = 8048593844056212117L;

	private final Collection<SeatedPlayer> players;

	private final PlayerId dealerId;

	public NewDealEvent(Collection<SeatedPlayer> players, PlayerId dealerId) {
		this.players = Collections.unmodifiableCollection(players);
		this.dealerId = dealerId;
	}

	protected NewDealEvent() {
		players = null;
		dealerId = null;
	}

	public NewDealEvent(Collection<SeatedPlayer> players, Player dealer) {
		this(players, dealer.getId());
	}

	public PlayerId getDealer() {
		return dealerId;
	}

	public Collection<SeatedPlayer> getPlayers() {
		return players;
	}

	@Override
	public String toString() {
		String toReturn = "A new deal with ";
		for (SeatedPlayer player : players) {
			toReturn += player;
			toReturn += " (";
			toReturn += Util.parseDollars(player.getStackValue());
			toReturn += "), ";
		}
		return toReturn.substring(0, toReturn.length() - 2) + " as initial players of this table. " + "Player " + dealerId + " is dealer.";
	}

}
