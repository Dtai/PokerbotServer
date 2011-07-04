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

public enum Round {
	WAITING("waiting"), PREFLOP("pre-flop"), FLOP("flop"), TURN("turn"), FINAL("final");

	private final String name;

	private Round(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + " round";
	}

	public Round getNextRound() {
		Round[] rounds = values();
		int nextOrdinal = ordinal() + 1;
		if (nextOrdinal == rounds.length) {
			return null;
		}
		return rounds[nextOrdinal];
	}
}
