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
package bots.mctsbot.common.elements.player;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.biotools.meerkat.Hand;

public class ShowdownPlayer {

	private static final long serialVersionUID = -1618593137613219527L;

	private String description;

	@XmlElementWrapper
	@XmlElement(name = "card")
	private Hand handCards;

	private PlayerId playerId;

	public ShowdownPlayer(PlayerId playerId, Hand handCards, String description) {
		this.playerId = playerId;
		this.handCards = new Hand(handCards);
		this.description = description;
	}

	protected ShowdownPlayer() {
		// no op
	}

	@Override
	public String toString() {
		return getPlayerId() + " has a " + description + " with " + handCards;
	}

	public PlayerId getPlayerId() {
		return playerId;
	}

	public Hand getHandCards() {
		return new Hand(handCards);
	}

	public String getHandDescription() {
		return description;
	}

}
