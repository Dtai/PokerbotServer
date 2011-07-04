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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bots.mctsbot.client.common.gamestate.ForwardingGameState;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.GameStateVisitor;
import bots.mctsbot.client.common.playerstate.ForwardingPlayerState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.AllInEvent;
import bots.mctsbot.common.elements.player.PlayerId;

/**
 * State after somebody went all-in.
 * BEWARE, this can be in stead of a SmallBlindState or BigBlindState!
 * @author guy
 *
 */
public class AllInState extends ForwardingGameState {

	//TODO fix for split pot

	private final AllInEvent event;

	private final int newPotSize;

	private final int raise;

	private final PlayerState playerState;

	private final int newBetSize;

	public AllInState(final GameState gameState, final AllInEvent event) {
		super(gameState);
		this.event = event;

		final PlayerState player = super.getPlayer(event.getPlayerId());
		this.newPotSize = super.getRoundPotSize() + event.getMovedAmount();

		this.newBetSize = player.getBet() + event.getMovedAmount();
		int buildingRaise = newBetSize - super.getLargestBet();
		if (buildingRaise < 0) {
			buildingRaise = 0;
		}
		raise = buildingRaise;
		this.playerState = new ForwardingPlayerState(player) {

			@Override
			public int getBet() {
				return newBetSize;
			}

			@Override
			public int getTotalInvestment() {
				return super.getTotalInvestment() + event.getMovedAmount();
			}

			@Override
			public int getStack() {
				return 0;
			}

			@Override
			public PlayerId getPlayerId() {
				return event.getPlayerId();
			}

			@Override
			public boolean hasFolded() {
				return false;
			}

			@Override
			public boolean hasChecked() {
				return false;
			}

			@Override
			public boolean hasBeenDealt() {
				return true;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public List<Integer> getBetProgression() {
				List<Integer> result = new ArrayList<Integer>();
				if (gameState.getLastBettor() != null) {
					result.addAll(gameState.getPlayer(gameState.getLastBettor()).getBetProgression());
				}
				result.add(event.getMovedAmount());
				return Collections.unmodifiableList(result);
			}

		};
	}

	@Override
	public PlayerState getPlayer(PlayerId playerId) {
		if (event.getPlayerId().equals(playerId)) {
			return playerState;
		}
		return super.getPlayer(playerId);
	}

	@Override
	public int getLargestBet() {
		return raise > 0 ? newBetSize : super.getLargestBet();
	}

	@Override
	public int getMinNextRaise() {
		return Math.max(raise, super.getMinNextRaise());
	}

	@Override
	public int getRoundPotSize() {
		return newPotSize;
	}

	public AllInEvent getLastEvent() {
		return event;
	}

	@Override
	public PlayerId getLastBettor() {
		return raise > 0 ? event.getPlayerId() : super.getLastBettor();
	}

	@Override
	public int getNbRaises() {
		int prevNbRaises = super.getNbRaises();
		if (raise > 0) {
			return prevNbRaises + 1;
		}
		return prevNbRaises;
	}

	public int getRaise() {
		return raise;
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitAllInState(this);
	}

	public AllInEvent getEvent() {
		return event;
	}
}
