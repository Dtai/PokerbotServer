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
package bots.mctsbot.ai.bots.bot.gametree.search;

public class Distribution {

	private final double variance;
	private final double mean;
	private final boolean isUpperBound;

	public Distribution(double mean, double variance, boolean isUpperBound) {
		this.mean = mean;
		this.variance = variance;
		this.isUpperBound = isUpperBound;
	}

	public Distribution(double mean, double variance) {
		this(mean, variance, false);
	}

	public double getMean() {
		return mean;
	}

	public double getVariance() {
		return variance;
	}

	public boolean isUpperBound() {
		return isUpperBound;
	}
}
