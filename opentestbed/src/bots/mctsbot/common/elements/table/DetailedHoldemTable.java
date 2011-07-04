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
package bots.mctsbot.common.elements.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import bots.mctsbot.common.elements.chips.Pots;
import bots.mctsbot.common.elements.player.SeatedPlayer;

import com.biotools.meerkat.Hand;

/**
 * An immutable class to represent a snapshot of the state of a table.
 * 
 */
public class DetailedHoldemTable extends Table {

	private static final long serialVersionUID = 1647960710321459407L;

	@XmlElementWrapper
	@XmlElement(name = "player")
	private final List<SeatedPlayer> players;

	private final boolean playing;

	private final TableConfiguration property;

	private final Pots pots;

	private final SeatedPlayer dealer;

	@XmlElementWrapper
	@XmlElement(name = "card")
	private final Hand communityCards;

	private final Round round;

	public DetailedHoldemTable(TableId id, String name, List<SeatedPlayer> players, boolean playing, TableConfiguration property, Pots pots,
			SeatedPlayer dealer, Hand communityCards, Round round) {
		super(id, name);
		this.players = players == null ? new ArrayList<SeatedPlayer>() : new ArrayList<SeatedPlayer>(players);
		this.playing = playing;
		this.property = property;
		this.pots = pots;
		this.dealer = dealer;
		this.communityCards = communityCards == null ? new Hand() : new Hand(communityCards);
		this.round = round;
	}

	public DetailedHoldemTable(TableId id, String name, List<SeatedPlayer> players, boolean playing, TableConfiguration property) {
		this(id, name, players, playing, property, null, null, null, null);
	}

	protected DetailedHoldemTable() {
		this.players = null;
		this.playing = false;
		this.property = null;
		this.pots = null;
		this.dealer = null;
		this.communityCards = null;
		this.round = null;
	}

	public DetailedHoldemTable(TableId tableId, TableConfiguration config) {
		this.players = Collections.emptyList();
		;
		this.playing = false;
		this.property = config;
		this.pots = new Pots(0);
		this.dealer = null;
		this.communityCards = new Hand();
		this.round = Round.WAITING;
	}

	/**
	 * Returns the list of players at this table.
	 * 
	 * @return The list of players at this table.
	 */
	public List<SeatedPlayer> getPlayers() {
		return players;
	}

	/**
	 * The number of players seated at this table.
	 * 
	 * @return The number of players seated at this table.
	 */
	public int getNbPlayers() {
		return players.size();
	}

	/**
	 * The playing status of this table.
	 * 
	 * @return True if the players are playing, false otherwise.
	 */
	public boolean isPlaying() {
		return playing;
	}

	/**
	 * Returns the game property of this table.
	 * 
	 * @return The game property of this table.
	 */
	public TableConfiguration getTableConfiguration() {
		return property;
	}

	public Pots getPots() {
		return pots;
	}

	public SeatedPlayer getDealer() {
		return dealer;
	}

	public Hand getCommunityCards() {
		return communityCards;
	}

	public Round getRound() {
		return round;
	}

}
