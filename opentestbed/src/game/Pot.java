/*
Open Meerkat Testbed. An open source implementation of the Meerkat API for running poker games
Copyright (C) 2010  Dan Schatzberg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package game;

import java.util.Set;
import java.util.TreeSet;

import util.Utils;

public class Pot implements Comparable<Pot> {
	private Set<Integer> eligiblePlayers = new TreeSet<Integer>();
	private double value = 0;
	private double upperBound = 0;
	private boolean canGrow = true;

	public Pot() {
	}

	public double getValue() {
		return this.value;
	}

	public boolean isEligible(int seat) {
		return eligiblePlayers.contains(new Integer(seat));
	}

	public Set<Integer> getEligiblePlayers() {
		return eligiblePlayers;
	}

	public void addToPot(double val, int seat) {
		this.value = Utils.roundToCents(this.value + val);
		this.eligiblePlayers.add(Integer.valueOf(seat));
	}

	@Override
	public int compareTo(Pot pot) {
		return Double.compare(getValue(), pot.getValue());
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperValue) {
		this.upperBound = upperValue;
	}

	public void setCanGrow(boolean canGrow) {
		this.canGrow = canGrow;
	}

	public boolean isCanGrow() {
		return canGrow;
	}

	public void moveToPot(Pot newPot, double toMove, int seat) {
		newPot.addToPot(toMove, seat);
		value = Utils.roundToCents(value - toMove);
	}

}
