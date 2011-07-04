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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import bots.mctsbot.client.common.gamestate.AbstractGameState;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.GameStateVisitor;
import bots.mctsbot.client.common.playerstate.AbstractPlayerState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.HoldemTableTreeEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.NewRoundEvent;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.Round;
import bots.mctsbot.common.elements.table.SeatId;
import bots.mctsbot.common.elements.table.TableConfiguration;

import com.biotools.meerkat.Hand;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class NewRoundState extends AbstractGameState {

	private final NewRoundEvent event;

	private final ImmutableBiMap<SeatId, PlayerId> seatMap;
	private final ImmutableMap<PlayerId, PlayerState> playerStates;

	private final PlayerId dealer;
	private final Hand communityCards;

	private final TableConfiguration tableConfiguration;

	private final GameState previousRoundState;

	private final PlayerId sb;
	private final PlayerId bb;

	public NewRoundState(GameState gameState, NewRoundEvent event) {
		this.event = event;
		this.previousRoundState = gameState;
		this.tableConfiguration = gameState.getTableConfiguration();
		this.dealer = gameState.getDealer();
		this.communityCards = gameState.getCommunityCards();
		this.sb = gameState.getSmallBlind();
		this.bb = gameState.getBigBlind();

		Builder<PlayerId, PlayerState> playerStateBuilder = ImmutableMap.builder();

		Set<PlayerId> playersIds = gameState.getSeatMap().values();

		for (final PlayerId playerId : playersIds) {
			final PlayerState oldPlayerState = gameState.getPlayer(playerId);

			final Hand cards = oldPlayerState.getCards();
			final int stack = oldPlayerState.getStack();
			final boolean hasFolded = oldPlayerState.hasFolded();
			final boolean hasBeenDealt = oldPlayerState.hasBeenDealt();
			final SeatId seat = oldPlayerState.getSeatId();
			final int investment = oldPlayerState.getTotalInvestment();

			PlayerState playerState = new AbstractPlayerState() {

				@Override
				public String getName() {
					return oldPlayerState.getName();
				}

				public int getBet() {
					return 0;
				}

				@Override
				public int getTotalInvestment() {
					return investment;
				}

				public Hand getCards() {
					return new Hand(cards);
				}

				public int getStack() {
					return stack;
				}

				public boolean hasFolded() {
					return hasFolded;
				}

				@Override
				public boolean hasBeenDealt() {
					return hasBeenDealt;
				}

				public PlayerId getPlayerId() {
					return playerId;
				}

				public SeatId getSeatId() {
					return seat;
				}

				@Override
				public boolean hasChecked() {
					return false;
				}

				/**
				 * {@inheritDoc}
				 */
				@Override
				public List<Integer> getBetProgression() {
					return Collections.emptyList();
				}

			};
			playerStateBuilder.put(playerId, playerState);
		}
		seatMap = previousRoundState.getSeatMap();
		playerStates = playerStateBuilder.build();
	}

	public TableConfiguration getTableConfiguration() {
		return tableConfiguration;
	}

	public Set<PlayerId> getAllSeatedPlayerIds() {
		return playerStates.keySet();
	}

	public Hand getCommunityCards() {
		return new Hand(communityCards);
	}

	public PlayerId getDealer() {
		return dealer;
	}

	public int getLargestBet() {
		return 0;
	}

	public PlayerId getLastBettor() {
		return null;
	}

	public HoldemTableTreeEvent getLastEvent() {
		return event;
	}

	public PlayerId getNextToAct() {
		return null;
	}

	@Override
	public int getMinNextRaise() {
		return tableConfiguration.getSmallBet();
	}

	public PlayerState getPlayer(PlayerId playerId) {
		return playerStates.get(playerId);
	}

	@Override
	public ImmutableBiMap<SeatId, PlayerId> getSeatMap() {
		return seatMap;
	}

	public GameState getPreviousGameState() {
		return previousRoundState;
	}

	public int getPreviousRoundsPotSize() {
		return event.getPots().getTotalValue();
	}

	public Round getRound() {
		return event.getRound();
	}

	public int getRoundPotSize() {
		return 0;
	}

	public int getNbSeats() {
		return tableConfiguration.getMaxNbPlayers();
	}

	@Override
	public int getNbRaises() {
		return 0;
	}

	@Override
	public PlayerId getBigBlind() {
		return bb;
	}

	@Override
	public PlayerId getSmallBlind() {
		return sb;
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitNewRoundState(this);
	}
}
