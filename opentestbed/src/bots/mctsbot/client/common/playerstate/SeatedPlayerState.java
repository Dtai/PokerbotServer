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
package bots.mctsbot.client.common.playerstate;

import java.util.Collections;
import java.util.List;

import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.player.SeatedPlayer;
import bots.mctsbot.common.elements.table.SeatId;

import com.biotools.meerkat.Hand;

public class SeatedPlayerState extends AbstractPlayerState {

	private final SeatedPlayer player;

	public SeatedPlayerState(SeatedPlayer player) {
		this.player = player;
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public boolean hasFolded() {
		return false;
	}

	@Override
	public int getStack() {
		return player.getStackValue();
	}

	@Override
	public SeatId getSeatId() {
		return player.getSeatId();
	}

	@Override
	public PlayerId getPlayerId() {
		return player.getId();
	}

	@Override
	public Hand getCards() {
		return new Hand();
	}

	@Override
	public List<Integer> getBetProgression() {
		return Collections.singletonList(player.getBetChipsValue());
	}

	@Override
	public int getBet() {
		return player.getBetChipsValue();
	}

	@Override
	public int getTotalInvestment() {
		return player.getBetChipsValue();
	}

	@Override
	public boolean hasBeenDealt() {
		return false;
	}

	@Override
	public boolean hasChecked() {
		return false;
	}

}
