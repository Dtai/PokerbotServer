package bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling;

import java.util.List;

import bots.mctsbot.ai.bots.bot.gametree.action.BetAction;
import bots.mctsbot.ai.bots.bot.gametree.action.ProbabilityAction;
import bots.mctsbot.ai.bots.bot.gametree.action.RaiseAction;
import bots.mctsbot.ai.opponentmodels.OpponentModel;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;

import com.google.common.collect.ImmutableList;

public abstract class Sampler {

	public abstract ImmutableList<ProbabilityAction> getProbabilityActions(GameState gameState, OpponentModel model, PlayerId actor, PlayerId bot);

	protected void addRaiseProbalities(GameState gameState, PlayerId actor, List<ProbabilityAction> actions, double raiseProbability, boolean raise,
			double[] relBetSizeSamples, double[] relPBetSizeSamples) {
		int lowerRaiseBound = gameState.getLowerRaiseBound(actor);
		int upperRaiseBound = gameState.getUpperRaiseBound(actor);
		if (lowerRaiseBound < upperRaiseBound) {
			int lastBetAmount = 0;
			double skippedProbabilities = 0;
			for (int i = 0; i < relBetSizeSamples.length; i++) {
				double probability = raiseProbability * relPBetSizeSamples[i];
				double amount = lowerRaiseBound + (upperRaiseBound - lowerRaiseBound) * relBetSizeSamples[i];
				int smallBlind = gameState.getTableConfiguration().getSmallBlind();
				int blindAdjustedAmount = Math.min((int) (smallBlind * Math.round(amount / smallBlind)), upperRaiseBound);

				if (Math.abs(lastBetAmount - blindAdjustedAmount) < (2 * smallBlind)) {
					// we skip some actions, if the new amount is not at least
					// two smallblinds greater than the last
					skippedProbabilities += probability;
				} else {
					if (blindAdjustedAmount < lowerRaiseBound)
						blindAdjustedAmount += smallBlind;
					if (blindAdjustedAmount > upperRaiseBound)
						blindAdjustedAmount = upperRaiseBound;
					if (raise) {
						actions.add(new ProbabilityAction(new RaiseAction(gameState, actor, blindAdjustedAmount), skippedProbabilities));
					} else
						actions.add(new ProbabilityAction(new BetAction(gameState, actor, blindAdjustedAmount), skippedProbabilities));
					lastBetAmount = blindAdjustedAmount;
					skippedProbabilities = 0.0;
				}

			}
		} else {
			if (raise)
				actions.add(new ProbabilityAction(new RaiseAction(gameState, actor, lowerRaiseBound), raiseProbability));
			else
				actions.add(new ProbabilityAction(new BetAction(gameState, actor, lowerRaiseBound), raiseProbability));
		}
	}
}
