package bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RouletteWheelSampler extends StochasticSampler {

	public RouletteWheelSampler() {
		super();
	}

	public RouletteWheelSampler(int nbBetSizeSamples) {
		super(nbBetSizeSamples);
	}

	class Bucket {
		private double start;
		private double end;
		private double prob;
		private double totalProb;
		private double averageProb;

		public Bucket(double start, double prob, double totalProb) {
			this.start = start;
			this.setProb(prob);
			this.setTotalProb(totalProb);
		}

		public double getStart() {
			return start;
		}

		public double getEnd() {
			return end;
		}

		public void setEnd(double end) {
			this.end = end;
		}

		public double getProb() {
			return prob;
		}

		private void setProb(double prob) {
			this.prob = prob;
		}

		public double getTotalProb() {
			return totalProb;
		}

		public void setTotalProb(double totalProb) {
			this.totalProb = totalProb;
		}

		public String toString() {
			return "Start: " + start + ",  \t end: " + end + ",\t prob: " + prob + ", averageProb: " + averageProb + ", totalProb: " + totalProb;
		}

		public void setAverageProb(int length) {
			if (end == start)
				this.averageProb = totalProb;
			else
				this.averageProb = totalProb / ((end - start) * (length - 1));
		}

		public double getAverageProb() {
			return averageProb;
		}
	}

	@Override
	protected double[] getStochasticSamples(int n) {
		RelativeBetDistribution distr = new RelativeBetDistribution();
		double[] samples = new double[n];
		for (int i = 0; i < samples.length; i++)
			samples[i] = distr.inverseCdf(r.nextDouble());
		Arrays.sort(samples);
		return samples;
	}

	protected List<Bucket> getStochasticSamples(double threshold) {
		RelativeBetDistribution distr = new RelativeBetDistribution();
		List<Bucket> buckets = new ArrayList<Bucket>();
		// start of range bucket, prob of start, totalProb
		Bucket bucket = new Bucket(0, distr.pdf(0), 0);
		for (int i = 0; i < distr.length(); i++) {
			double relBet = (double) i / (distr.length() - 1);
			if (Math.abs(bucket.getProb() - distr.pdf(relBet)) > threshold) {
				bucket.setEnd(relBet);
				bucket.setAverageProb(distr.length());
				buckets.add(bucket);
				bucket = new Bucket(relBet, distr.pdf(relBet), 0);
			}
			bucket.setTotalProb(bucket.getTotalProb() + distr.pdf(relBet));
		}
		bucket.setEnd(1);
		bucket.setAverageProb(distr.length());
		buckets.add(bucket);
		return buckets;
	}

	public static void main(String[] args) {
		RouletteWheelSampler s = new RouletteWheelSampler();
		//          double[] list = s.getStochasticSamples(8);
		//          for (int i = 0; i < list.length; i++) {
		//                  System.out.println(" - " + list[i]);
		//          }
		List<Bucket> list = s.getStochasticSamples(0.01);
		double totalProb = 0;
		for (int i = 0; i < list.size(); i++) {
			totalProb += list.get(i).getTotalProb();
			System.out.println(" - " + list.get(i));
		}
		System.out.println("\n => TotalProb: " + totalProb);
	}
}
