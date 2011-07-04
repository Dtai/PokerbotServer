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

public class MCTSBotFactory implements BotFactory {

	private final MCTSListener.Factory[] listeners;
	private final OpponentModel.Factory opponentModelFactory;
	private final SelectionStrategy decisionNodeSelectionStrategy;
	private final SelectionStrategy opponentNodeSelectionStrategy;
	private final SelectionStrategy moveSelectionStrategy;
	private final ShowdownNode.Factory showdownNodeFactory;
	private final Sampler sampler;
	private final int decisionTime;
	private final String name;
	private final BackPropagationStrategy.Factory backPropStratFactory;

	public MCTSBotFactory(String name, OpponentModel.Factory opponentModelFactory, SelectionStrategy decisionNodeSelectionStrategy,
			SelectionStrategy opponentNodeSelectionStrategy, SelectionStrategy moveSelectionStrategy, ShowdownNode.Factory showdownNodeFactory,
			BackPropagationStrategy.Factory backPropStratFactory, Sampler sampler, int decisionTime, MCTSListener.Factory... listeners) {
		this.name = name;
		this.listeners = listeners;
		this.opponentModelFactory = opponentModelFactory;
		this.decisionNodeSelectionStrategy = decisionNodeSelectionStrategy;
		this.opponentNodeSelectionStrategy = opponentNodeSelectionStrategy;
		this.moveSelectionStrategy = moveSelectionStrategy;
		this.showdownNodeFactory = showdownNodeFactory;
		this.backPropStratFactory = backPropStratFactory;
		this.sampler = sampler;
		this.decisionTime = decisionTime;
		StateTableEvaluator.getInstance();
	}

	public Bot createBot(final PlayerId botId, GameStateContainer gameStateContainer, RemoteHoldemPlayerContext playerContext) {
		OpponentModel opponentModel = opponentModelFactory.create();

		Config config = new Config(opponentModel, showdownNodeFactory, decisionNodeSelectionStrategy, opponentNodeSelectionStrategy, moveSelectionStrategy,
				backPropStratFactory, sampler);
		return new MCTSBot(botId, gameStateContainer, playerContext, config, decisionTime, listeners);
	}

	@Override
	public String toString() {
		return name;
	}
}
