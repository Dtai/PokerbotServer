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

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.log4j.Logger;

public class Gaussian {

	private final static Logger logger = Logger.getLogger(Gaussian.class);

	public final double mean;

	public final double variance;

	public Gaussian(double mean, double variance) {
		if (Double.isInfinite(mean) || Double.isNaN(mean)) {
			logger.error("Bad mean: " + mean);
			throw new IllegalArgumentException("Bad mean: " + mean);
		}
		if (Double.isInfinite(variance) || Double.isNaN(variance) || variance < 0) {
			logger.error("Bad variance: " + variance);
			throw new IllegalArgumentException("Bad variance: " + variance);
		}
		this.mean = mean;
		this.variance = variance;
	}

	public Gaussian() {
		this(0, 1);
	}

	public final static Gaussian maxOf(Gaussian... gaussians) {
		Gaussian max = gaussians[0];
		for (int i = 1; i < gaussians.length; i++) {
			max = maxOf(max, gaussians[i]);
		}
		return max;
	}

	public final static Gaussian maxOf(Gaussian g1, Gaussian g2) {
		double a = Math.sqrt(g1.variance + g2.variance);
		if (a == 0) {
			return new Gaussian(Math.max(g1.mean, g2.mean), 0);
		}
		double alpha = (g1.mean - g2.mean) / a;
		double bigPhiAlpha = bigPhi(alpha);
		double bigPhiMinAlpha = 1 - bigPhiAlpha;
		double smallPhiAlpha = smallPhi(alpha);
		double aSmallPhiAlpha = a * smallPhiAlpha;
		double mean = g1.mean * bigPhiAlpha + g2.mean * bigPhiMinAlpha + aSmallPhiAlpha;
		double stddev = (g1.mean * g1.mean + g1.variance) * bigPhiAlpha + (g2.mean * g2.mean + g2.variance) * bigPhiMinAlpha + (g1.mean + g2.mean)
				* aSmallPhiAlpha - mean * mean;
		return new Gaussian(mean, Math.max(0, stddev));
	}

	public final static double smallPhi(double x) {
		return 1.0 / Math.sqrt(2 * Math.PI) * Math.exp(-x * x / 2.0);
	}

	private final static NormalDistributionImpl defaultNormal = new NormalDistributionImpl();

	public final static double bigPhi(double x) {
		//TODO tabulate
		if (x < -4.5)
			return 0;
		if (x > 4.5)
			return 1;
		try {
			//must check for negative numbers, approximation might fail.
			return Math.min(1, Math.max(0, defaultNormal.cumulativeProbability(x)));
		} catch (MathException e) {
			throw new IllegalStateException();
		}
	}

	@Override
	public String toString() {
		return "N(" + mean + "," + getStdDev() + ")";
	}

	public double getStdDev() {
		return Math.sqrt(variance);
	}

}
