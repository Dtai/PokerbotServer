package bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling;

import java.util.List;

import bots.mctsbot.ai.bots.bot.gametree.action.CallAction;
import bots.mctsbot.ai.bots.bot.gametree.action.CheckAction;
import bots.mctsbot.ai.bots.bot.gametree.action.FoldAction;
import bots.mctsbot.ai.bots.bot.gametree.action.ProbabilityAction;
import bots.mctsbot.ai.opponentmodels.OpponentModel;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.Pair;
import bots.mctsbot.common.util.Triple;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class SmartSampler extends Sampler {

	public static final double[] relBetSizeSamples = { 0.00, 0.01, 0.02, 0.04, 0.06, 0.10, 0.20, 0.50, 1.00 };
	public static final double[] relPBetSizeSamples = { 0.19, 0.26, 0.15, 0.12, 0.09, 0.08, 0.06, 0.02, 0.04 };

	@Override
	public ImmutableList<ProbabilityAction> getProbabilityActions(GameState gameState, OpponentModel model, PlayerId actor, PlayerId bot) {
		//          long size;
		List<ProbabilityAction> actions = Lists.newArrayListWithExpectedSize(2 + relBetSizeSamples.length);
		if (gameState.getDeficit(actor) > 0) {
			// call, raise or fold
			//                  size = model.getVisitorSize();
			model.assumeTemporarily(gameState);
			Triple<Double, Double, Double> probabilities = model.getFoldCallRaiseProbabilities(gameState, actor);
			model.forgetLastAssumption();
			//                  if (size != model.getVisitorSize()) throw new IllegalStateException("Model didn't forget last assumption");

			double foldProbability = probabilities.getLeft();
			actions.add(new ProbabilityAction(new FoldAction(gameState, actor), foldProbability));

			double callProbability = probabilities.getMiddle();
			actions.add(new ProbabilityAction(new CallAction(gameState, actor), callProbability));

			if (!gameState.getPlayer(bot).isAllIn() && gameState.isAllowedToRaise(actor)) {
				double raiseProbability = probabilities.getRight();
				addRaiseProbalities(gameState, actor, actions, raiseProbability, true, relBetSizeSamples, relPBetSizeSamples);
			}
		} else {
			// check or bet
			//                  size = model.getVisitorSize();
			model.assumeTemporarily(gameState);
			Pair<Double, Double> probabilities = model.getCheckBetProbabilities(gameState, actor);
			model.forgetLastAssumption();
			//                  if (size != model.getVisitorSize()) throw new IllegalStateException("Model didn't forget last assumption");

			double checkProbability = probabilities.getLeft();
			actions.add(new ProbabilityAction(new CheckAction(gameState, actor), checkProbability));

			if (!gameState.getPlayer(bot).isAllIn() && gameState.isAllowedToRaise(actor)) {
				double betProbability = probabilities.getRight();
				addRaiseProbalities(gameState, actor, actions, betProbability, false, relBetSizeSamples, relPBetSizeSamples);
			}
		}
		ImmutableList.Builder<ProbabilityAction> normalizedActionsBuilder = ImmutableList.builder();
		for (ProbabilityAction action : actions) {
			normalizedActionsBuilder.add(new ProbabilityAction(action.getActionWrapper(), action.getProbability()));
		}
		return normalizedActionsBuilder.build();
	}

}
