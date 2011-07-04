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
package bots.mctsbot.ai.bots.util;

import org.apache.log4j.Logger;

/**
 * Class to calculate running mean and variance from a set of samples 
 * without keeping them all in memory.
 */
public final class RunningStats {

	private final static Logger logger = Logger.getLogger(RunningStats.class);

	private static final double default_spread = 0.0;

	private int n = 0;
	private double oldM, newM, oldS, newS;

	public void add(double value) {
		if (Double.isInfinite(value) || Double.isNaN(value)) {
			logger.error("Bad value: " + value);
			//			throw new IllegalArgumentException("Bad mean: "+mean);
		}
		n++;

		// See Knuth TAOCP vol 2, 3rd edition, page 232
		if (n == 1) {
			oldM = newM = value;
			oldS = 0.0;
		} else {
			newM = oldM + (value - oldM) / n;
			newS = oldS + (value - oldM) * (value - newM);

			// set up for next iteration
			oldM = newM;
			oldS = newS;
		}
	}

	public int getNbSamples() {
		return n;
	}

	public double getMean() {
		return (n > 0) ? newM : 0.0;
	}

	/**
	 * Variance is positive infinity when n<2.
	 */
	public double getVariance() {
		return ((n > 1) ? newS / (n - 1) : default_spread);
	}

	public double getStdDev() {
		return Math.sqrt(getVariance());
	}

	public double getEVStdDev() {
		if (n == 0) {
			return default_spread;
		}
		return Math.sqrt(getVariance() / getNbSamples());
	}

	public double getEVVar() {
		if (n == 0) {
			return default_spread;
		}
		return getVariance() / getNbSamples();
	}

	public void reset() {
		n = 0;
	}

}
