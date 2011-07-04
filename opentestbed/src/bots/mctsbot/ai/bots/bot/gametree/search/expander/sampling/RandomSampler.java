package bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling;

import java.util.Arrays;

public class RandomSampler extends StochasticSampler {

	public RandomSampler() {
		super();
	}

	public RandomSampler(int nbBetSizeSamples) {
		super(nbBetSizeSamples);
	}

	//	@Override
	//	protected double[] getStochasticSamples(int n) {
	//		double[] samples = new double[n];
	//		double sample = r.nextDouble() / n;
	//		for (int i = 0; i < n; i++) 
	//			samples[i] = sample + (double) i/n;
	//		Arrays.sort(samples);
	//		return samples;
	//	}

	@Override
	protected double[] getStochasticSamples(int n) {
		double[] samples = new double[n];
		for (int i = 0; i < n; i++)
			samples[i] = r.nextDouble();
		Arrays.sort(samples);
		return samples;
	}

	public static void main(String[] args) {
		RandomSampler s = new RandomSampler();
		double[] list = s.getStochasticSamples(8);
		for (int i = 0; i < list.length; i++) {
			System.out.println(" => " + list[i]);
		}
	}
}
