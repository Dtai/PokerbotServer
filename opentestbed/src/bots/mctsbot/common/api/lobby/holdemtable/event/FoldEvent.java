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

import bots.mctsbot.common.elements.player.Player;
import bots.mctsbot.common.elements.player.PlayerId;

/**
 * A class to represent fold events.
 */
public class FoldEvent extends HoldemTableEvent {

	private static final long serialVersionUID = -7805526864154493974L;

	@XmlAttribute
	private final PlayerId playerId;

	public FoldEvent(PlayerId player) {
		this.playerId = player;
	}

	protected FoldEvent() {
		playerId = null;
	}

	public FoldEvent(Player player) {
		this(player.getId());
	}

	@Override
	public String toString() {
		return getPlayerId() + " folds.";
	}

	public PlayerId getPlayerId() {
		return playerId;
	}

}
