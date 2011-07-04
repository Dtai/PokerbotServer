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

import javax.xml.bind.annotation.XmlAttribute;

import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.Util;

/**
 * A class to represent raise events.
 * 
 */
public class RaiseEvent extends HoldemTableEvent {

	private static final long serialVersionUID = -5634645028675762487L;

	@XmlAttribute
	private final PlayerId playerId;

	@XmlAttribute
	private final int amount;

	@XmlAttribute
	private final int movedAmount;

	public RaiseEvent(PlayerId player, int amount, int movedAmount) {
		this.playerId = player;
		this.amount = amount;
		this.movedAmount = movedAmount;
	}

	protected RaiseEvent() {
		playerId = null;
		amount = 0;
		movedAmount = 0;
	}

	@Override
	public String toString() {
		return getPlayerId() + " raises with " + Util.parseDollars(getAmount()) + ".";
	}

	public int getAmount() {
		return amount;
	}

	/**
	 * Returns the number of chips that is moved from the player's main stack to the bet stack to make the raise.
	 * (Call previous bet/raise + raise amount)
	 * 
	 * @return The number of chips that is moved from the player's main stack to the bet stack to make the raise.
	 */
	public int getMovedAmount() {
		return movedAmount;
	}

	public PlayerId getPlayerId() {
		return playerId;
	}

}
