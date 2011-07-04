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
package bots.mctsbot.client.common.gamestate.modifiers;

import bots.mctsbot.client.common.gamestate.ForwardingGameState;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.GameStateVisitor;
import bots.mctsbot.common.api.lobby.holdemtable.event.HoldemTableEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.NewCommunityCardsEvent;

import com.biotools.meerkat.Hand;

public class NewCommunityCardsState extends ForwardingGameState {

	private final NewCommunityCardsEvent event;

	public NewCommunityCardsState(GameState gameState, NewCommunityCardsEvent event) {
		super(gameState);
		this.event = event;
	}

	@Override
	public Hand getCommunityCards() {
		Hand cards = super.getCommunityCards();
		cards.addHand(event.getCommunityCards());
		return cards;
	}

	public HoldemTableEvent getLastEvent() {
		return event;
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitNewCommunityCardsState(this);
	}

	public NewCommunityCardsEvent getEvent() {
		return event;
	}

}
