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
import bots.mctsbot.common.api.lobby.holdemtable.event.LeaveTableEvent;

public class LeaveTableState extends ForwardingGameState {

	private final LeaveTableEvent event;

	public LeaveTableState(GameState gameState, LeaveTableEvent event) {
		super(gameState);
		this.event = event;
	}

	public LeaveTableEvent getLastEvent() {
		return event;
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitLeaveTableState(this);
	}

}
