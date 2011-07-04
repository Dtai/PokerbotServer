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
package bots.mctsbot.ai.bots.bot.gametree.mcts.nodes;

import java.util.List;
import java.util.Random;

import bots.mctsbot.ai.bots.bot.gametree.action.DefaultWinnerException;
import bots.mctsbot.ai.bots.bot.gametree.action.GameEndedException;
import bots.mctsbot.ai.bots.bot.gametree.action.ProbabilityAction;
import bots.mctsbot.ai.bots.bot.gametree.action.SearchBotAction;
import bots.mctsbot.ai.bots.bot.gametree.mcts.strategies.backpropagation.BackPropagationStrategy;
import bots.mctsbot.ai.bots.bot.gametree.mcts.strategies.selection.SelectionStrategy;
import bots.mctsbot.ai.bots.bot.gametree.search.expander.Expander;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;

import com.google.common.collect.ImmutableList;

public abstract class InnerNode extends AbstractNode {

	private final static Random random = new Random();

	//config
	protected final Config config;

	//parent
	public final GameState gameState;
	public final PlayerId bot;

	//children
	private double[] probabilities = null;
	private double[] cumulativeProbability = null;
	private ImmutableList<INode> children = null;

	//protected boolean inTree = false;

	protected final BackPropagationStrategy backPropStrategy;

	public InnerNode(InnerNode parent, ProbabilityAction probAction, GameState gameState, PlayerId bot, Config config) {
		super(parent, probAction);
		this.bot = bot;
		this.gameState = gameState;
		this.config = config;
		this.backPropStrategy = createBackPropStrategy();
	}

	protected abstract BackPropagationStrategy createBackPropStrategy();

	public INode selectRecursively() {
		//if(!inTree) return this;
		//          long size = config.getModel().getVisitorSize();
		boolean needsChildExpansion = (children == null);
		if (needsChildExpansion) {
			config.getModel().assumeTemporarily(gameState);
			expandChildren();
		}
		INode selectedChild = selectChild().selectRecursively();
		if (needsChildExpansion) {
			config.getModel().forgetLastAssumption();
		}
		//          if (size != config.getModel().getVisitorSize()) throw new IllegalStateException("Model didn't forget last assumption");
		return selectedChild;
	}

	public abstract INode selectChild();

	public INode selectChild(SelectionStrategy selectionStrategy) {
		return selectionStrategy.select(this);
	}

	@Override
	public void expand() {
		//inTree = true;
	}

	public double simulate() {
		throw new IllegalStateException("Selected node must ne leaf.");
	}

	public INode getRandomChild() {
		double randomNumber = random.nextDouble();
		ImmutableList<INode> children = getChildren();
		for (int i = 0; i < cumulativeProbability.length - 1; i++) {
			if (randomNumber < cumulativeProbability[i]) {
				return children.get(i);
			}
		}
		return children.get(cumulativeProbability.length - 1);
	}

	public void backPropagate(double value) {
		backPropStrategy.onBackPropagate(value);
		parent.backPropagate(value);
	}

	@Override
	public double getEV() {
		return backPropStrategy.getEV();
	}

	@Override
	public int getNbSamples() {
		return backPropStrategy.getNbSamples();
	}

	@Override
	public double getStdDev() {
		return backPropStrategy.getStdDev();
	}

	@Override
	public double getEVVar() {
		return backPropStrategy.getEVVar();
	}

	@Override
	public double getEVStdDev() {
		return backPropStrategy.getEVStdDev();
	}

	@Override
	public double getVariance() {
		return backPropStrategy.getVariance();
	}

	@Override
	public int getNbSamplesInMean() {
		return backPropStrategy.getNbSamplesInMean();
	}

	public ImmutableList<INode> getChildren() {
		return children;
	}

	@Override
	public GameState getGameState() {
		return gameState;
	}

	public Config getConfig() {
		return config;
	}

	protected void expandChildren() {
		if (children == null) {
			Expander expander = new Expander(gameState, config.getModel(), gameState.getNextToAct(), bot, config.getSampler());
			List<ProbabilityAction> actions = expander.getProbabilityActions();
			ImmutableList.Builder<INode> childrenBuilder = ImmutableList.builder();
			probabilities = new double[actions.size()];
			cumulativeProbability = new double[actions.size()];
			double cumul = 0;
			for (int i = 0; i < actions.size(); i++) {
				ProbabilityAction action = actions.get(i);
				double probability = action.getProbability();
				childrenBuilder.add(getChildAfter(action));
				cumul += probability;
				cumulativeProbability[i] = cumul;
				probabilities[i] = probability;
			}
			children = childrenBuilder.build();
		}
	}

	public INode getChildAfter(ProbabilityAction probAction) {
		SearchBotAction action = probAction.getAction();
		if (action.endsInvolvementOf(bot)) {
			// bot folded
			return new ConstantLeafNode(this, probAction, gameState.getPlayer(bot).getStack());
		} else {
			try {
				GameState nextState = action.getStateAfterAction();
				// expand further
				if (nextState.getNextToAct().equals(bot)) {
					return new DecisionNode(this, probAction, nextState, bot, config);
				} else {
					return new OpponentNode(this, probAction, nextState, bot, config);
				}
			} catch (GameEndedException e) {
				// no active players left
				// go to showdown
				return config.getShowdownNodeFactory().create(e.lastState, this, probAction);
			} catch (DefaultWinnerException e) {
				assert e.winner.getPlayerId().equals(bot) : "Bot should have folded earlier, winner can't be " + e.winner;
				// bot wins
				return new ConstantLeafNode(this, probAction, gameState.getPlayer(bot).getStack()
						+ (int) (e.foldState.getGamePotSize() * (1 - gameState.getTableConfiguration().getRake())));
			}
		}
	}

	public double[] getCumulativeProbability() {
		return cumulativeProbability;
	}

	public double[] getProbabilities() {
		return probabilities;
	}

}
