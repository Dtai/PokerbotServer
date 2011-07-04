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
import java.util.List;
import java.util.Set;

import bots.mctsbot.client.common.gamestate.AbstractGameState;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.GameStateVisitor;
import bots.mctsbot.client.common.playerstate.AbstractPlayerState;
import bots.mctsbot.client.common.playerstate.ForwardingPlayerState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.api.lobby.holdemtable.event.HoldemTableTreeEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.NewDealEvent;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.player.SeatedPlayer;
import bots.mctsbot.common.elements.table.Round;
import bots.mctsbot.common.elements.table.SeatId;
import bots.mctsbot.common.elements.table.TableConfiguration;

import com.biotools.meerkat.Hand;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

public class NewDealState extends AbstractGameState {

	private final TableConfiguration tableConfiguration;
	private final NewDealEvent event;

	//TODO make weak reference? clean up memory?
	private final GameState previousGame;

	private final ImmutableBiMap<SeatId, PlayerId> seatMap;
	private final ImmutableMap<PlayerId, PlayerState> playerStates;

	public NewDealState(NewDealEvent newDealEvent, GameState previousGame) {
		this.previousGame = previousGame;
		this.event = newDealEvent;
		this.tableConfiguration = previousGame.getTableConfiguration();
		this.seatMap = previousGame.getSeatMap();

		ImmutableMap.Builder<PlayerId, PlayerState> playerStateBuilder = ImmutableMap.builder();

		for (final SeatedPlayer player : newDealEvent.getPlayers()) {
			if (player.isSittingIn()) {
				AbstractPlayerState playerState = new AbstractPlayerState() {

					@Override
					public String getName() {
						return player.getName();
					}

					public int getBet() {
						return 0;
					}

					@Override
					public int getTotalInvestment() {
						return 0;
					}

					public Hand getCards() {
						return new Hand();
					}

					public int getStack() {
						return player.getStackValue();
					}

					public boolean hasFolded() {
						return false;
					}

					public PlayerId getPlayerId() {
						return player.getId();
					}

					public SeatId getSeatId() {
						return player.getSeatId();
					}

					@Override
					public boolean hasChecked() {
						return false;
					}

					@Override
					public boolean hasBeenDealt() {
						return true;
					}

					@Override
					public List<Integer> getBetProgression() {
						return new ArrayList<Integer>();
					}

				};
				playerStateBuilder.put(player.getId(), playerState);
			}
		}
		//also add players that are not being dealt a card.
		ImmutableMap<PlayerId, PlayerState> playerStatesInEvent = playerStateBuilder.build();
		for (PlayerState p : previousGame.getAllSeatedPlayers()) {
			if (!playerStatesInEvent.containsKey(p.getPlayerId())) {
				playerStateBuilder.put(p.getPlayerId(), new ForwardingPlayerState(p) {

					@Override
					public boolean hasBeenDealt() {
						return false;
					}

				});
			}
		}
		playerStates = playerStateBuilder.build();
	}

	public TableConfiguration getTableConfiguration() {
		return tableConfiguration;
	}

	public Set<PlayerId> getAllSeatedPlayerIds() {
		return playerStates.keySet();
	}

	public Hand getCommunityCards() {
		return new Hand();
	}

	public PlayerId getDealer() {
		return event.getDealer();
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

	public int getMinNextRaise() {
		return tableConfiguration.getSmallBet();
	}

	public PlayerId getNextToAct() {
		return null;
	}

	public PlayerState getPlayer(PlayerId playerId) {
		try {
			return playerStates.get(playerId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ImmutableBiMap<SeatId, PlayerId> getSeatMap() {
		return seatMap;
	}

	public GameState getPreviousGameState() {
		return previousGame;
	}

	public int getPreviousRoundsPotSize() {
		return 0;
	}

	public Round getRound() {
		return Round.PREFLOP;
	}

	public int getRoundPotSize() {
		return 0;
	}

	@Override
	public int getNbRaises() {
		return 0;
	}

	@Override
	public PlayerId getBigBlind() {
		return null;
	}

	@Override
	public PlayerId getSmallBlind() {
		return null;
	}

	@Override
	public void acceptVisitor(GameStateVisitor visitor) {
		visitor.visitNewDealState(this);
	}

	@Override
	public String toString() {
		return getLastEvent().toString();
	}
}
