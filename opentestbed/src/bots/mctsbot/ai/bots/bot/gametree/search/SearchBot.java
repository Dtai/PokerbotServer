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

import org.apache.log4j.Logger;

import bots.mctsbot.ai.bots.bot.AbstractBot;
import bots.mctsbot.ai.bots.bot.gametree.search.nodevisitor.NodeVisitor;
import bots.mctsbot.ai.bots.bot.gametree.search.nodevisitor.StatisticsVisitor;
import bots.mctsbot.client.common.GameStateContainer;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.api.lobby.holdemtable.holdemplayer.context.RemoteHoldemPlayerContext;
import bots.mctsbot.common.elements.player.PlayerId;

public class SearchBot extends AbstractBot {

	private final static Logger logger = Logger.getLogger(SearchBot.class);

	private int searchId = 0;
	private final SearchConfiguration config;

	private final NodeVisitor.Factory[] nodeVisitorFactories;

	public SearchBot(PlayerId playerId, GameStateContainer gameStateContainer, RemoteHoldemPlayerContext playerContext, SearchConfiguration config,
			NodeVisitor.Factory[] nodeVisitorFactories) {
		super(playerId, gameStateContainer, playerContext);
		this.config = config;
		this.nodeVisitorFactories = nodeVisitorFactories;
	}

	@Override
	public void doNextAction() {
		GameState gameState = gameStateContainer.getGameState();
		NodeVisitor[] visitors = createVisitors(gameState);
		BotActionNode actionNode;
		// essential to do this with a clean game state from the
		// context, no wrappers
		config.getOpponentModel().assumePermanently(gameState);
		switch (gameState.getRound()) {
		case PREFLOP:
			logger.debug("Searching preflop round game tree:");
			//			SearchConfiguration config2 = new SearchConfiguration(config.getOpponentModel(), 
			//					config.getShowdownNodeFactory(),
			//					config.getBotNodeExpanderFactory(), 20000, 40000, 80000, 160000, 0.25, false, false);
			//			BotActionNode temp = new BotActionNode(botId, gameState,
			//					config2, config.getPreflopTokens(), searchId++,
			//					visitors);
			//			temp.getValueDistribution(0);
			//
			//			logger.info("Without pruning:");
			//			logger.info("NbNodes="+((StatisticsVisitor)visitors[visitors.length-1]).getNbNodes());
			//			logger.info("NbPrunedSubTrees="+((StatisticsVisitor)visitors[visitors.length-1]).getNbPrunedSubtrees());
			//			logger.info("NbPrunedTokens="+((StatisticsVisitor)visitors[visitors.length-1]).getNbPrunedTokens());
			//			logger.info("NbOpponentModelCalls="+((StatisticsVisitor)visitors[visitors.length-1]).getNbOpponentModelCalls());
			//			visitors = createVisitors(gameState);

			actionNode = new BotActionNode(botId, gameState, config, config.getSampler(), config.getPreflopTokens(), searchId++, visitors);
			actionNode.performbestAction(playerContext);

			logger.info("With pruning:");
			logger.info("NbNodes=" + ((StatisticsVisitor) visitors[visitors.length - 1]).getNbNodes());
			logger.info("NbPrunedSubTrees=" + ((StatisticsVisitor) visitors[visitors.length - 1]).getNbPrunedSubtrees());
			logger.info("NbPrunedTokens=" + ((StatisticsVisitor) visitors[visitors.length - 1]).getNbPrunedTokens());
			logger.info("NbOpponentModelCalls=" + ((StatisticsVisitor) visitors[visitors.length - 1]).getNbOpponentModelCalls());

			break;
		case FLOP:
			logger.debug("Searching flop round game tree:");
			actionNode = new BotActionNode(botId, gameState, config, config.getSampler(), config.getFlopTokens(), searchId++, visitors);
			actionNode.performbestAction(playerContext);
			break;
		case TURN:
			logger.debug("Searching turn round game tree:");
			actionNode = new BotActionNode(botId, gameState, config, config.getSampler(), config.getTurnTokens(), searchId++, visitors);
			actionNode.performbestAction(playerContext);
			break;
		case FINAL:
			logger.debug("Searching final round game tree:");
			actionNode = new BotActionNode(botId, gameState, config, config.getSampler(), config.getFinalTokens(), searchId++, visitors);
			actionNode.performbestAction(playerContext);
			break;
		default:
			throw new IllegalStateException("What round are we in?");
		}
	}

	private NodeVisitor[] createVisitors(GameState gameState) {
		NodeVisitor[] visitors = new NodeVisitor[nodeVisitorFactories.length + 1];
		StatisticsVisitor stats = new StatisticsVisitor();
		for (int i = 0; i < nodeVisitorFactories.length; i++) {
			visitors[i] = nodeVisitorFactories[i].create(gameState, botId);
		}
		visitors[nodeVisitorFactories.length] = stats;
		return visitors;
	}

}
