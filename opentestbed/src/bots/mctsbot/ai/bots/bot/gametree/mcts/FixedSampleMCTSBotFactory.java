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
package bots.mctsbot.ai.bots.bot.gametree.mcts;

import bots.mctsbot.ai.bots.bot.Bot;
import bots.mctsbot.ai.bots.bot.BotFactory;
import bots.mctsbot.ai.bots.bot.gametree.mcts.listeners.MCTSListener;
import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.Config;
import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.ShowdownNode;
import bots.mctsbot.ai.bots.bot.gametree.mcts.strategies.backpropagation.BackPropagationStrategy;
import bots.mctsbot.ai.bots.bot.gametree.mcts.strategies.selection.SelectionStrategy;
import bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling.Sampler;
import bots.mctsbot.ai.opponentmodels.OpponentModel;
import bots.mctsbot.client.common.GameStateContainer;
import bots.mctsbot.common.api.lobby.holdemtable.holdemplayer.context.RemoteHoldemPlayerContext;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.handeval.spears2p2.StateTableEvaluator;

public class FixedSampleMCTSBotFactory implements BotFactory {

	private static int copies = 0;

	private final MCTSListener.Factory[] listeners;
	private final OpponentModel.Factory opponentModelFactory;
	private final SelectionStrategy decisionNodeSelectionStrategy;
	private final SelectionStrategy opponentNodeSelectionStrategy;
	private final SelectionStrategy moveSelectionStrategy;
	private final ShowdownNode.Factory showdownNodeFactory;
	private final Sampler sampler;
	private final String name;
	private final BackPropagationStrategy.Factory backPropStratFactory;
	private final int samplesPreFlop;
	private final int samplesFlop;
	private final int samplesTurn;
	private final int samplesRiver;

	public FixedSampleMCTSBotFactory(String name, OpponentModel.Factory opponentModelFactory, SelectionStrategy decisionNodeSelectionStrategy,
			SelectionStrategy opponentNodeSelectionStrategy, SelectionStrategy moveSelectionStrategy, ShowdownNode.Factory showdownNodeFactory,
			BackPropagationStrategy.Factory backPropStratFactory, Sampler sampler, int samplesPreFlop, int samplesFlop, int samplesTurn, int samplesRiver,
			MCTSListener.Factory... listeners) {
		this.name = name;
		this.listeners = listeners;
		this.opponentModelFactory = opponentModelFactory;
		this.decisionNodeSelectionStrategy = decisionNodeSelectionStrategy;
		this.opponentNodeSelectionStrategy = opponentNodeSelectionStrategy;
		this.moveSelectionStrategy = moveSelectionStrategy;
		this.showdownNodeFactory = showdownNodeFactory;
		this.backPropStratFactory = backPropStratFactory;
		this.sampler = sampler;
		this.samplesPreFlop = samplesPreFlop;
		this.samplesFlop = samplesFlop;
		this.samplesTurn = samplesTurn;
		this.samplesRiver = samplesRiver;
		StateTableEvaluator.getInstance();
	}

	public Bot createBot(final PlayerId botId, GameStateContainer gameStateContainer, RemoteHoldemPlayerContext playerContext) {
		copies++;
		OpponentModel opponentModel = opponentModelFactory.create();

		Config config = new Config(opponentModel, showdownNodeFactory, decisionNodeSelectionStrategy, opponentNodeSelectionStrategy, moveSelectionStrategy,
				backPropStratFactory, sampler);
		return new FixedSampleMCTSBot(botId, gameStateContainer, playerContext, config, samplesPreFlop, samplesFlop, samplesTurn, samplesRiver, listeners);
	}

	@Override
	public String toString() {
		return name;
	}
}
