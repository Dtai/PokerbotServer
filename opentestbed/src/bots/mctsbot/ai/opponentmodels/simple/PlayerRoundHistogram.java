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
package bots.mctsbot.ai.opponentmodels.simple;

import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.modifiers.AllInState;
import bots.mctsbot.common.api.lobby.holdemtable.event.AllInEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.BetEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.CallEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.CheckEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.FoldEvent;
import bots.mctsbot.common.api.lobby.holdemtable.event.RaiseEvent;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.util.Pair;
import bots.mctsbot.common.util.Triple;

public class PlayerRoundHistogram {

	// prior derived from data set
	private volatile int nbCheck;
	private volatile int nbBet;
	private volatile int totalNoBet;

	private volatile int nbFold;
	private volatile int nbCall;
	private volatile int nbRaise;
	private volatile int totalBet;

	public PlayerRoundHistogram(int nbCheck, int nbBet, int totalNoBet, int nbFold, int nbCall, int nbRaise, int totalBet) {
		this.nbCheck = nbCheck;
		this.nbBet = nbBet;
		this.totalNoBet = totalNoBet;
		this.nbFold = nbFold;
		this.nbCall = nbCall;
		this.nbRaise = nbRaise;
		this.totalBet = totalBet;
	}

	public Pair<Double, Double> getCheckBetProbabilities(GameState gameState, PlayerId actor) {
		return new Pair<Double, Double>(getCheckProbability(gameState), getBetProbability(gameState));
	}

	public Triple<Double, Double, Double> getFoldCallRaiseProbabilities(GameState gameState, PlayerId actor) {
		return new Triple<Double, Double, Double>(getFoldProbability(gameState), getCallProbability(gameState), getRaiseProbability(gameState));
	}

	public void addAllIn(GameState gameState, AllInEvent allInEvent) {
		AllInState newState = new AllInState(gameState, allInEvent);
		if (gameState.hasBet()) {
			if (newState.getRaise() > 0) {
				++nbRaise;
			} else {
				++nbCall;
			}
			++totalBet;
		} else {
			++nbBet;
			++totalNoBet;
		}
	}

	public void addCheck(GameState gameState, CheckEvent checkEvent) {
		++nbCheck;
		++totalNoBet;
	}

	public void addBet(GameState gameState, BetEvent betEvent) {
		++nbBet;
		++totalNoBet;
	}

	public void addCall(GameState gameState, CallEvent callEvent) {
		++nbCall;
		++totalBet;
	}

	public void addRaise(GameState gameState, RaiseEvent raiseEvent) {
		++nbRaise;
		++totalBet;
	}

	public void addFold(GameState gameState, FoldEvent foldEvent) {
		++nbFold;
		++totalBet;
	}

	public double getCheckProbability(GameState gameState) {
		return nbCheck * 1.0 / totalNoBet;
	}

	public double getBetProbability(GameState gameState) {
		return nbBet * 1.0 / totalNoBet;
	}

	public double getCallProbability(GameState gameState) {
		return nbCall * 1.0 / totalBet;
	}

	public double getFoldProbability(GameState gameState) {
		return nbFold * 1.0 / totalBet;
	}

	public double getRaiseProbability(GameState gameState) {
		return nbRaise * 1.0 / totalBet;
	}

}
