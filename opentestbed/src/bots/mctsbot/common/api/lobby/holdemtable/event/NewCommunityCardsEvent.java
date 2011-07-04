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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.biotools.meerkat.Hand;

/**
 * A class to represent new community cards events.
 * 
 */
public class NewCommunityCardsEvent extends HoldemTableEvent {

	private static final long serialVersionUID = -5063239366087788741L;

	//JAXB doesn't like EnumSets
	@XmlElementWrapper
	@XmlElement(name = "card")
	private final Hand communityCards;

	public NewCommunityCardsEvent(Hand commonCards) {
		communityCards = new Hand(commonCards);
	}

	protected NewCommunityCardsEvent() {
		communityCards = null;
	}

	public Hand getCommunityCards() {
		return new Hand(communityCards);
	}

	@Override
	public String toString() {
		String toReturn = "New Community Cards: ";
		toReturn += communityCards;
		return toReturn + ".";
	}

}
