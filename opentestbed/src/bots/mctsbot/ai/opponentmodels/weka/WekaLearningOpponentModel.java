package bots.mctsbot.ai.opponentmodels.weka;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import bots.mctsbot.ai.opponentmodels.OpponentModel;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.Pair;
import bots.mctsbot.common.util.Triple;

/**
 * This OpponentModel delegates to a provided default {@link WekaModel} for its opponent-model.
 * In addition it observes the game and (configured by {@link WekaOptions}) replaces
 * the opponent-model for each villain after enough data has been collected.
 *
 */
public final class WekaLearningOpponentModel implements OpponentModel {

	protected static final Logger logger = Logger.getLogger(WekaRegressionModel.class);

	private PlayerTrackingVisitor permanentVisitor;
	private ActionTrackingVisitor actionTrackingVisitor;
	private final Deque<PlayerTrackingVisitor> visitors = new ArrayDeque<PlayerTrackingVisitor>();

	Map<PlayerId, WekaRegressionModel> opponentModels = new HashMap<PlayerId, WekaRegressionModel>();
	WekaRegressionModel defaultModel;

	public WekaLearningOpponentModel(WekaRegressionModel defaultModel) {
		this.permanentVisitor = new PlayerTrackingVisitor();
		this.visitors.add(permanentVisitor);
		this.defaultModel = defaultModel;
		if (WekaOptions.isArffPersistency()) {
			this.actionTrackingVisitor = new ActionTrackingVisitor();
		}
	}

	@Override
	public void assumePermanently(GameState gameState) {
		// make sure we have created Models for all players
		Set<PlayerState> seatedPlayers = gameState.getAllSeatedPlayers();
		for (PlayerState playerState : seatedPlayers) {
			getWekaModel(playerState.getPlayerId());
		}
		permanentVisitor.readHistory(gameState);
		if (actionTrackingVisitor != null) {
			actionTrackingVisitor.readHistory(gameState);
		}
	}

	@Override
	public void assumeTemporarily(GameState gameState) {
		PlayerTrackingVisitor root = visitors.peek();
		PlayerTrackingVisitor clonedTopVisitor = root.clone();
		clonedTopVisitor.readHistory(gameState);
		visitors.push(clonedTopVisitor);
	}

	@Override
	public void forgetLastAssumption() {
		visitors.pop();
		// the permanentVisitor should never be popped
		if (visitors.isEmpty()) {
			throw new IllegalStateException("'forgetAssumption' was called more often than 'assumeTemporarily'");
		}
	}

	private Propositionalizer getCurrentGamePropositionalizer() {
		return visitors.peek().getPropz();
	}

	private WekaRegressionModel getWekaModel(PlayerId actor) {
		WekaRegressionModel model = opponentModels.get(actor);
		if (model == null) {
			model = new WekaRegressionModel(defaultModel);
			opponentModels.put(actor, model);
			actionTrackingVisitor.getPropz().addPlayer(actor, new ARFFPlayer(actor, model));
		}
		return model;
	}

	@Override
	public Pair<Double, Double> getCheckBetProbabilities(GameState gameState, PlayerId actor) {
		return getWekaModel(actor).getCheckBetProbabilities(actor, getCurrentGamePropositionalizer());
	}

	@Override
	public Triple<Double, Double, Double> getFoldCallRaiseProbabilities(GameState gameState, PlayerId actor) {
		return getWekaModel(actor).getFoldCallRaiseProbabilities(actor, getCurrentGamePropositionalizer());
	}

	@Override
	public double[] getShowdownProbabilities(GameState gameState, PlayerId actor) throws UnsupportedOperationException {
		return getWekaModel(actor).getShowdownProbabilities(actor, getCurrentGamePropositionalizer());
	}

}
