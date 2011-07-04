package bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling;

import java.util.List;
import java.util.Random;

import bots.mctsbot.ai.bots.bot.gametree.action.BetAction;
import bots.mctsbot.ai.bots.bot.gametree.action.CallAction;
import bots.mctsbot.ai.bots.bot.gametree.action.CheckAction;
import bots.mctsbot.ai.bots.bot.gametree.action.FoldAction;
import bots.mctsbot.ai.bots.bot.gametree.action.ProbabilityAction;
import bots.mctsbot.ai.bots.bot.gametree.action.RaiseAction;
import bots.mctsbot.ai.opponentmodels.OpponentModel;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.Pair;
import bots.mctsbot.common.util.Triple;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public abstract class StochasticSampler extends Sampler {

	protected int nbBetSizeSamples = 5;

	protected final static Random r = new Random();

	public StochasticSampler() {
	}

	public StochasticSampler(int nbBetSizeSamples) {
		this.nbBetSizeSamples = nbBetSizeSamples;
	}

	//  @Override
	//  public ImmutableList<ProbabilityAction> getProbabilityActions(
	//                  GameState gameState, OpponentModel model, PlayerId actor,
	//                  PlayerId bot) {
	//          List<ProbabilityAction> actions = Lists.newArrayListWithExpectedSize(2+nbBetSizeSamples);
	//          RelativeBetDistribution distr = new RelativeBetDistribution();
	//          if (gameState.getDeficit(actor)>0) {
	//                  // call, raise or fold
	//                  model.assumeTemporarily(gameState);
	//                  Triple<Double, Double, Double> probabilities = 
	//                          model.getFoldCallRaiseProbabilities(gameState, actor);
	//                  model.forgetLastAssumption();
	//
	//                  double foldProbability = probabilities.getLeft();
	//                  actions.add(new ProbabilityAction(new FoldAction(gameState, actor), foldProbability));
	//
	//                  double callProbability = probabilities.getMiddle();
	//                  actions.add(new ProbabilityAction(new CallAction(gameState, actor), callProbability));
	//
	//                  if (!gameState.getPlayer(bot).isAllIn()
	//                                  && gameState.isAllowedToRaise(actor)) {
	//                          double raiseProbability = probabilities.getRight();
	//                          double[] betSizeSamples = getStochasticSamples(nbBetSizeSamples);
	//                          double[] pBetSizeSamples = new double[nbBetSizeSamples];                                
	//                          for (int i = 0; i < betSizeSamples.length; i++) 
	//                                  pBetSizeSamples[i] = distr.pdf(betSizeSamples[i]);
	//                          
	//                          addRaiseProbalities(gameState, actor, actions, raiseProbability, true, 
	//                                          betSizeSamples, pBetSizeSamples);
	//                  }
	//          } else {
	//                  // check or bet
	//                  model.assumeTemporarily(gameState);
	//                  Pair<Double, Double> probabilities = model.getCheckBetProbabilities(gameState, actor);
	//                  model.forgetLastAssumption();
	//                  double checkProbability = probabilities.getLeft();
	//                  actions.add(new ProbabilityAction(new CheckAction(gameState, actor), checkProbability));
	//
	//                  if (!gameState.getPlayer(bot).isAllIn()
	//                                  && gameState.isAllowedToRaise(actor)) {
	//                          double betProbability = probabilities.getRight();
	//                          double[] betSizeSamples = getStochasticSamples(nbBetSizeSamples);
	//                          double[] pBetSizeSamples = new double[nbBetSizeSamples];                                
	//                          for (int i = 0; i < betSizeSamples.length; i++) 
	//                                  pBetSizeSamples[i] = distr.pdf(betSizeSamples[i]);
	//                          
	//                          addRaiseProbalities(gameState, actor, actions, betProbability, true, 
	//                                          betSizeSamples, pBetSizeSamples);
	//                  }
	//          }
	//          ImmutableList.Builder<ProbabilityAction> normalizedActionsBuilder = ImmutableList.builder();
	//          for (ProbabilityAction action : actions) {
	//                  normalizedActionsBuilder.add(new ProbabilityAction(action
	//                                  .getActionWrapper(), action.getProbability()));
	//          }
	//          return normalizedActionsBuilder.build();
	//  }

	@Override
	public ImmutableList<ProbabilityAction> getProbabilityActions(GameState gameState, OpponentModel model, PlayerId actor, PlayerId bot) {
		List<ProbabilityAction> actions = Lists.newArrayListWithExpectedSize(2 + nbBetSizeSamples);
		double totalProbability = 0;
		if (gameState.getDeficit(actor) > 0) {
			// call, raise or fold
			//                  model.assumeTemporarily(gameState);
			Triple<Double, Double, Double> probabilities = model.getFoldCallRaiseProbabilities(gameState, actor);
			//                  model.forgetLastAssumption();

			double foldProbability = probabilities.getLeft();
			totalProbability += foldProbability;
			actions.add(new ProbabilityAction(new FoldAction(gameState, actor), foldProbability));

			double callProbability = probabilities.getMiddle();
			totalProbability += callProbability;
			actions.add(new ProbabilityAction(new CallAction(gameState, actor), callProbability));

			if (!gameState.getPlayer(bot).isAllIn() && gameState.isAllowedToRaise(actor)) {
				double raiseProbability = probabilities.getRight();
				int lowerRaiseBound = gameState.getLowerRaiseBound(actor);
				int upperRaiseBound = gameState.getUpperRaiseBound(actor);
				double[] betSizeSamples = getStochasticSamples(nbBetSizeSamples);
				for (double betSizeSample : betSizeSamples) {
					RaiseAction betAction = new RaiseAction(gameState, actor, (int) Math.round(lowerRaiseBound + betSizeSample
							* (upperRaiseBound - lowerRaiseBound)));
					actions.add(new ProbabilityAction(betAction, raiseProbability / nbBetSizeSamples));
					totalProbability += raiseProbability / nbBetSizeSamples;
				}
			}
		} else {
			// check or bet
			//                  model.assumeTemporarily(gameState);
			Pair<Double, Double> probabilities = model.getCheckBetProbabilities(gameState, actor);
			//                  model.forgetLastAssumption();
			double checkProbability = probabilities.getLeft();
			totalProbability += checkProbability;
			actions.add(new ProbabilityAction(new CheckAction(gameState, actor), checkProbability));

			if (!gameState.getPlayer(bot).isAllIn() && gameState.isAllowedToRaise(actor)) {
				double betProbability = probabilities.getRight();
				int lowerRaiseBound = gameState.getLowerRaiseBound(actor);
				int upperRaiseBound = gameState.getUpperRaiseBound(actor);
				double[] betSizeSamples = getStochasticSamples(nbBetSizeSamples);
				for (double betSizeSample : betSizeSamples) {
					BetAction betAction = new BetAction(gameState, actor, (int) Math.round(lowerRaiseBound + betSizeSample
							* (upperRaiseBound - lowerRaiseBound)));
					actions.add(new ProbabilityAction(betAction, betProbability / nbBetSizeSamples));
					totalProbability += betProbability / nbBetSizeSamples;
				}
			}
		}
		ImmutableList.Builder<ProbabilityAction> normalizedActionsBuilder = ImmutableList.builder();
		for (ProbabilityAction action : actions) {
			normalizedActionsBuilder.add(new ProbabilityAction(action.getActionWrapper(), action.getProbability() / totalProbability));
		}
		return normalizedActionsBuilder.build();
	}

	protected abstract double[] getStochasticSamples(int n);

}
