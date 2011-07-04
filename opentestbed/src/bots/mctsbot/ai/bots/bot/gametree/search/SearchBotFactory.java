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
package bots.mctsbot.ai.bots.bot.gametree.search;

import java.util.HashMap;

import bots.mctsbot.ai.bots.bot.Bot;
import bots.mctsbot.ai.bots.bot.BotFactory;
import bots.mctsbot.ai.bots.bot.gametree.search.expander.SamplingExpander;
import bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling.Sampler;
import bots.mctsbot.ai.bots.bot.gametree.search.nodevisitor.NodeVisitor;
import bots.mctsbot.ai.opponentmodels.OpponentModel;
import bots.mctsbot.client.common.GameStateContainer;
import bots.mctsbot.common.api.lobby.holdemtable.holdemplayer.context.RemoteHoldemPlayerContext;
import bots.mctsbot.common.elements.player.PlayerId;

public class SearchBotFactory implements BotFactory {

	private static int copies = 0;
	private final int copy;

	private final HashMap<PlayerId, OpponentModel> opponentModels = new HashMap<PlayerId, OpponentModel>();
	private final NodeVisitor.Factory[] nodeVisitorFactories;
	private final OpponentModel.Factory modelFactory;
	private final ShowdownNode.Factory showdownNodeFactory;
	private final int preflopTokens;
	private final int flopTokens;
	private final int turnTokens;
	private final int finalTokens;
	private final double evDiscount;
	private final boolean uniformBotActionTokens;
	private final boolean useAlphaBetaPruning;
	private final Sampler sampler;

	public SearchBotFactory(OpponentModel.Factory modelFactory, ShowdownNode.Factory showdownNodeFactory, Sampler sampler, int preflopTokens, int flopTokens,
			int turnTokens, int finalTokens, double evDiscount, boolean uniformBotActionTokens, boolean useAlphaBetaPruning,
			NodeVisitor.Factory... nodeVisitorFactories) {
		copy = ++copies;
		this.modelFactory = modelFactory;
		this.nodeVisitorFactories = nodeVisitorFactories;
		this.showdownNodeFactory = showdownNodeFactory;
		this.preflopTokens = preflopTokens;
		this.flopTokens = flopTokens;
		this.turnTokens = turnTokens;
		this.finalTokens = finalTokens;
		this.evDiscount = evDiscount;
		this.uniformBotActionTokens = uniformBotActionTokens;
		this.useAlphaBetaPruning = useAlphaBetaPruning;
		this.sampler = sampler;
	}

	/**
	 * @see bots.mctsbot.ai.bots.bot.BotFactory#createBot(bots.mctsbot.common.elements.player.PlayerId,
	 *      bots.mctsbot.common.elements.table.TableId,
	 *      bots.mctsbot.client.common.SmartLobbyContext,
	 *      java.util.concurrent.ExecutorService,
	 *      bots.mctsbot.ai.bots.listener.BotListener[])
	 */
	public synchronized Bot createBot(final PlayerId botId, GameStateContainer gameStateContainer, RemoteHoldemPlayerContext playerContext) {
		copies++;

		OpponentModel opponentModel = opponentModels.get(botId);
		if (opponentModel == null) {
			opponentModel = modelFactory.create();
			opponentModels.put(botId, opponentModel);
		}
		SearchConfiguration config = new SearchConfiguration(opponentModel, showdownNodeFactory, new SamplingExpander.Factory(), sampler, preflopTokens,
				flopTokens, turnTokens, finalTokens, evDiscount, uniformBotActionTokens, useAlphaBetaPruning);
		return new SearchBot(botId, gameStateContainer, playerContext, config, nodeVisitorFactories);
	}

	@Override
	public String toString() {
		return "SearchBot v2-" + copy;
	}
}
