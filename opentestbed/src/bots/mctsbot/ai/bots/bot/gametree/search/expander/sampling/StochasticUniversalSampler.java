package bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling;

import java.util.Arrays;

public class StochasticUniversalSampler extends RouletteWheelSampler {

	public StochasticUniversalSampler() {
		super();
	}

	public StochasticUniversalSampler(int nbBetSizeSamples) {
		super(nbBetSizeSamples);
	}

	protected double[] getStochasticSamples(int n) {
		RelativeBetDistribution distr = new RelativeBetDistribution();
		double[] samples = new double[n];
		double sample = r.nextDouble() / n;
		for (int i = 0; i < n; i++)
			samples[i] = distr.inverseCdf(sample + (double) i / n);
		Arrays.sort(samples);
		return samples;
	}

	public static void main(String[] args) {
		StochasticUniversalSampler s = new StochasticUniversalSampler();
		double[] list = s.getStochasticSamples(8);
		for (int i = 0; i < list.length; i++) {
			System.out.println(" - " + list[i]);
		}
	}

}
