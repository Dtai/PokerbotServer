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
package bots.mctsbot.common.api.lobby.holdemtable.holdemplayer.event;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.biotools.meerkat.Hand;

public class NewPocketCardsEvent extends HoldemPlayerEvent {

	private static final long serialVersionUID = -3328895783353781276L;

	//JAXB doesn't like EnumSets
	@XmlElementWrapper
	@XmlElement(name = "card")
	private final Hand pocketCards;

	public NewPocketCardsEvent(Hand pocketCards) {
		this.pocketCards = new Hand(pocketCards);
	}

	protected NewPocketCardsEvent() {
		pocketCards = null;
	}

	public Hand getPocketCards() {
		return new Hand(pocketCards);
	}

	@Override
	public String toString() {
		String toReturn = "You have received new pocket cards: ";
		toReturn += pocketCards + ".";
		return toReturn;
	}

}
