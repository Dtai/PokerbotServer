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

import bots.mctsbot.common.elements.chips.Pots;
import bots.mctsbot.common.elements.table.Round;

/**
 * A class to represent new round events.
 * 
 */
public class NewRoundEvent extends HoldemTableEvent {

	private static final long serialVersionUID = 5282936949568835084L;

	private final Round round;

	private final Pots pots;

	public NewRoundEvent(Round round, Pots pots) {
		this.round = round;
		this.pots = pots;
	}

	protected NewRoundEvent() {
		round = null;
		pots = null;
	}

	@Override
	public String toString() {
		return round + " " + pots;
	}

	public Round getRound() {
		return round;
	}

	public Pots getPots() {
		return pots;
	}

}
