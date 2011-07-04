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

import bots.mctsbot.ai.bots.bot.gametree.search.expander.TokenExpander;
import bots.mctsbot.ai.bots.bot.gametree.search.expander.sampling.Sampler;
import bots.mctsbot.ai.opponentmodels.OpponentModel;

public class SearchConfiguration {

	private final OpponentModel opponentModeler;
	private final ShowdownNode.Factory showdownNodeFactory;
	private final Sampler sampler;
	private final int flopTokens;
	private final int turnTokens;
	private final int finalTokens;
	private final TokenExpander.Factory botNodeExpanderFactory;
	private final int preflopTokens;
	private final double evDiscount;
	private final boolean uniformBotActionTokens;
	private final boolean useAlphaBetaPruning;

	public SearchConfiguration(OpponentModel opponentModel, ShowdownNode.Factory showdownNodeFactory, TokenExpander.Factory botNodeExpanderFactory,
			Sampler sampler, int preflopTokens, int flopTokens, int turnTokens, int finalTokens, double evDiscount, boolean uniformBotActionTokens,
			boolean useAlphaBetaPruning) {
		opponentModeler = opponentModel;
		this.showdownNodeFactory = showdownNodeFactory;
		this.sampler = sampler;
		this.preflopTokens = preflopTokens;
		this.flopTokens = flopTokens;
		this.turnTokens = turnTokens;
		this.finalTokens = finalTokens;
		this.botNodeExpanderFactory = botNodeExpanderFactory;
		this.evDiscount = evDiscount;
		this.uniformBotActionTokens = uniformBotActionTokens;
		this.useAlphaBetaPruning = useAlphaBetaPruning;
	}

	public OpponentModel getOpponentModel() {
		return opponentModeler;
	}

	public ShowdownNode.Factory getShowdownNodeFactory() {
		return showdownNodeFactory;
	}

	public int getFinalTokens() {
		return finalTokens;
	}

	public int getFlopTokens() {
		return flopTokens;
	}

	public int getPreflopTokens() {
		return preflopTokens;
	}

	public int getTurnTokens() {
		return turnTokens;
	}

	public TokenExpander.Factory getBotNodeExpanderFactory() {
		return botNodeExpanderFactory;
	}

	public double getEVDiscount() {
		return evDiscount;
	}

	public boolean isUniformBotActionTokens() {
		return uniformBotActionTokens;
	}

	public boolean isUseAlphaBetaPruning() {
		return useAlphaBetaPruning;
	}

	public Sampler getSampler() {
		return sampler;
	}

}
