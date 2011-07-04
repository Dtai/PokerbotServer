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
import bots.mctsbot.common.api.lobby.holdemtable.event.BetEvent;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.Round;

public class BetState extends ForwardingGameState {

	private final BetEvent event;

	private final int newPotSize;

	private final PlayerState playerState;

	private final boolean betAfterBlindCase;

	public BetState(final GameState gameState, final BetEvent event) {
		super(gameState);
		this.event = event;

		PlayerState oldPlayerState = super.getPlayer(event.getPlayerId());

		betAfterBlindCase = Round.PREFLOP.equals(gameState.getRound());
		if (betAfterBlindCase && gameState.getDeficit(event.getPlayerId()) > 0)
			throw new IllegalStateException("Can't bet in the preflop round when you have a deficit to pay.");

		final int newStack = oldPlayerState.getStack() - event.getAmount();
		this.newPotSize = super.getRoundPotSize() + event.getAmount();
		this.playerState = new ForwardingPlayerState(oldPlayerState) {

			@Override
			public int getStack() {
				return newStack;
			}

			@Override
			public int getBet() {
				if (betAfterBlindCase) {
					return super.getBet() + BetState.this.event.getAmount();
				}
				return BetState.this.event.getAmount();
			}

			@Override
			public int getTotalInvestment() {
				return super.getTotalInvestment() + BetState.this.event.getAmount();
			}

			@Override
			public PlayerId getPlayerId() {
				return BetState.this.event.getPlayerId();
			}

			@Override
			public boolean hasFolded() {
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
				if (betAfterBlindCase) {
					List<Integer> result = new ArrayList<Integer>();
					PlayerId lastBettorId = gameState.getLastBettor();
					PlayerState lastBettor = gameState.getPlayer(lastBettorId);
					List<Integer> previousBetProgression = lastBettor.getBetProgression();
					result.addAll(previousBetProgression);
					result.add(event.getAmount());
					return Collections.unmodifiableList(result);
				}
				return Collections.singletonList(getBet());
			}

			@Override
			public boolean hasChecked() {
				return false;
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
		if (betAfterBlindCase) {
			return super.getLargestBet() + event.getAmount();
		}
		return event.getAmount();
	}

	@Override
	public int getMinNextRaise() {
		return event.getAmount();
	}

	@Override
	public int getRoundPotSize() {
		return newPotSize;
	}

	@Override
	public BetEvent getLastEvent() {
		return event;
	}

	@Override
	public PlayerId getLastBettor() {
		return event.getPlayerId();
	}

	@Override
	public int getNbRaises() {
		return 1;
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitBetState(this);
	}

	public BetEvent getEvent() {
		return event;
	}
}
