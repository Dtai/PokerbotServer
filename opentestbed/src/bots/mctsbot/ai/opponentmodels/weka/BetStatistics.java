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
package bots.mctsbot.ai.opponentmodels.weka;

/**
 * A number of statistics about betting behavior. 
 * Action types and bet sizes are kept score of.
 * 
 * All amounts are relative to the big blind.
 * 
 * @author guy
 *
 */
public class BetStatistics implements Cloneable {

	private int nbBetsPreFlop = 0;
	private int nbChecksPreFlop = 0;

	private int nbFoldsPreFlop = 0;
	private int nbCallsPreFlop = 0;
	private int nbRaisesPreFlop = 0;

	private int nbBetsFlop = 0;
	private int nbChecksFlop = 0;

	private int nbFoldsFlop = 0;
	private int nbCallsFlop = 0;
	private int nbRaisesFlop = 0;

	private int nbBetsTurn = 0;
	private int nbChecksTurn = 0;

	private int nbFoldsTurn = 0;
	private int nbCallsTurn = 0;
	private int nbRaisesTurn = 0;

	private int nbBetsRiver = 0;
	private int nbChecksRiver = 0;

	private int nbFoldsRiver = 0;
	private int nbCallsRiver = 0;
	private int nbRaisesRiver = 0;

	private double betAmountPreFlop = 0;
	private double callAmountPreFlop = 0;
	private double raiseAmountPreFlop = 0;

	private double betAmountFlop = 0;
	private double callAmountFlop = 0;
	private double raiseAmountFlop = 0;

	private double betAmountTurn = 0;
	private double callAmountTurn = 0;
	private double raiseAmountTurn = 0;

	private double betAmountRiver = 0;
	private double callAmountRiver = 0;
	private double raiseAmountRiver = 0;

	@Override
	protected BetStatistics clone() {
		try {
			return (BetStatistics) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	//inspectors

	public int getNbBetsRaisesPreFlop() {
		return nbBetsPreFlop + nbRaisesPreFlop;
	}

	public int getNbBetsRaisesPostFlop() {
		return getNbBetsRaisesFlop() + getNbBetsRaisesTurn() + getNbBetsRaisesRiver();
	}

	public int getNbBetsRaisesFlop() {
		return nbBetsFlop + nbRaisesFlop;
	}

	public int getNbBetsRaisesTurn() {
		return nbBetsTurn + nbRaisesTurn;
	}

	public int getNbBetsRaisesRiver() {
		return nbBetsRiver + nbRaisesRiver;
	}

	public int getNbRaisesPreFlop() {
		return nbRaisesPreFlop;
	}

	public int getNbRaisesPostFlop() {
		return getNbRaisesFlop() + getNbRaisesTurn() + getNbRaisesRiver();
	}

	public int getNbRaisesFlop() {
		return nbRaisesFlop;
	}

	public int getNbRaisesTurn() {
		return nbRaisesTurn;
	}

	public int getNbRaisesRiver() {
		return nbRaisesRiver;
	}

	public int getNbBetsPreFlop() {
		return nbBetsPreFlop;
	}

	public int getNbBetsPostFlop() {
		return getNbBetsFlop() + getNbBetsTurn() + getNbBetsRiver();
	}

	public int getNbBetsFlop() {
		return nbBetsFlop;
	}

	public int getNbBetsTurn() {
		return nbBetsTurn;
	}

	public int getNbBetsRiver() {
		return nbBetsRiver;
	}

	public int getNbCalls() {
		return nbCallsPreFlop + nbCallsFlop + nbCallsTurn + nbCallsRiver;
	}

	public int getNbFolds() {
		return nbFoldsPreFlop + nbFoldsFlop + nbFoldsTurn + nbFoldsRiver;
	}

	public int getNbRaises() {
		return nbRaisesPreFlop + nbRaisesFlop + nbRaisesTurn + nbRaisesRiver;
	}

	public int getNbBets() {
		return nbBetsPreFlop + nbBetsFlop + nbBetsTurn + nbBetsRiver;
	}

	public int getNbChecks() {
		return nbChecksPreFlop + nbChecksFlop + nbChecksTurn + nbChecksRiver;
	}

	public int getNbBetsRaises() {
		return getNbBets() + getNbRaises();
	}

	public int getNbRoundRaises(Propositionalizer p) {
		if (p.inPreFlopRound()) {
			return nbRaisesPreFlop;
		} else if (p.inFlopRound()) {
			return nbRaisesFlop;
		} else if (p.inTurnRound()) {
			return nbRaisesTurn;
		} else if (p.inRiverRound()) {
			return nbRaisesRiver;
		} else {
			throw new IllegalStateException();
		}
	}

	public int getNbRoundBets(Propositionalizer p) {
		if (p.inPreFlopRound()) {
			return nbBetsPreFlop;
		} else if (p.inFlopRound()) {
			return nbBetsFlop;
		} else if (p.inTurnRound()) {
			return nbBetsTurn;
		} else if (p.inRiverRound()) {
			return nbBetsRiver;
		} else {
			throw new IllegalStateException();
		}
	}

	public int getNbRoundBetsRaises(Propositionalizer p) {
		if (p.inPreFlopRound()) {
			return nbBetsPreFlop + nbRaisesPreFlop;
		} else if (p.inFlopRound()) {
			return nbBetsFlop + nbRaisesFlop;
		} else if (p.inTurnRound()) {
			return nbBetsTurn + nbRaisesTurn;
		} else if (p.inRiverRound()) {
			return nbBetsRiver + nbRaisesRiver;
		} else {
			throw new IllegalStateException();
		}
	}

	public float getRaiseFrequency(int memory) {
		float nbRaises = getNbRaises();
		float nbFolds = getNbFolds();
		float nbCalls = getNbCalls();
		return (0.16F * memory + nbRaises) / (memory + nbRaises + nbFolds + nbCalls);
	}

	public float getCallFrequency(int memory) {
		float nbRaises = getNbRaises();
		float nbFolds = getNbFolds();
		float nbCalls = getNbCalls();
		return (0.16F * memory + nbCalls) / (memory + nbRaises + nbFolds + nbCalls);
	}

	public float getFoldFrequency(int memory) {
		float nbRaises = getNbRaises();
		float nbFolds = getNbFolds();
		float nbCalls = getNbCalls();
		return (0.71F * memory + nbFolds) / (memory + nbRaises + nbFolds + nbCalls);
	}

	public float getCheckFrequency(int memory) {
		return 1 - getBetFrequency(memory);
	}

	public float getBetFrequency(int memory) {
		float nbChecks = getNbChecks();
		float nbBets = getNbBets();
		return (0.34F * memory + nbBets) / (memory + nbChecks + nbBets);
	}

	public float getRoundRaiseFrequency(Propositionalizer p, int memory) {
		if (p.inPreFlopRound()) {
			return (0.15F * memory + nbRaisesPreFlop) / (memory + nbFoldsPreFlop + nbCallsPreFlop + nbRaisesPreFlop);
		} else if (p.inFlopRound()) {
			return (0.12F * memory + nbRaisesFlop) / (memory + nbFoldsFlop + nbCallsFlop + nbRaisesFlop);
		} else if (p.inTurnRound()) {
			return (0.1F * memory + nbRaisesTurn) / (memory + nbFoldsTurn + nbCallsTurn + nbRaisesTurn);
		} else if (p.inRiverRound()) {
			return (0.09F * memory + nbRaisesRiver) / (memory + nbFoldsRiver + nbCallsRiver + nbRaisesRiver);
		} else {
			throw new IllegalStateException();
		}
	}

	public float getRoundCallFrequency(Propositionalizer p, int memory) {
		if (p.inPreFlopRound()) {
			return (0.14F * memory + nbCallsPreFlop) / (memory + nbFoldsPreFlop + nbCallsPreFlop + nbRaisesPreFlop);
		} else if (p.inFlopRound()) {
			return (0.29F * memory + nbCallsFlop) / (memory + nbFoldsFlop + nbCallsFlop + nbRaisesFlop);
		} else if (p.inTurnRound()) {
			return (0.37F * memory + nbCallsTurn) / (memory + nbFoldsTurn + nbCallsTurn + nbRaisesTurn);
		} else if (p.inRiverRound()) {
			return (0.35F * memory + nbCallsRiver) / (memory + nbFoldsRiver + nbCallsRiver + nbRaisesRiver);
		} else {
			throw new IllegalStateException();
		}
	}

	public float getRoundFoldFrequency(Propositionalizer p, int memory) {
		if (p.inPreFlopRound()) {
			return (0.71F * memory + nbFoldsPreFlop) / (memory + nbFoldsPreFlop + nbCallsPreFlop + nbRaisesPreFlop);
		} else if (p.inFlopRound()) {
			return (0.59F * memory + nbFoldsFlop) / (memory + nbFoldsFlop + nbCallsFlop + nbRaisesFlop);
		} else if (p.inTurnRound()) {
			return (0.53F * memory + nbFoldsTurn) / (memory + nbFoldsTurn + nbCallsTurn + nbRaisesTurn);
		} else if (p.inRiverRound()) {
			return (0.56F * memory + nbFoldsRiver) / (memory + nbFoldsRiver + nbCallsRiver + nbRaisesRiver);
		} else {
			throw new IllegalStateException();
		}
	}

	public float getRoundBetFrequency(Propositionalizer p, int memory) {
		if (p.inPreFlopRound()) {
			return (0.15F * memory + nbBetsPreFlop) / (memory + nbBetsPreFlop + nbChecksPreFlop);
		} else if (p.inFlopRound()) {
			return (0.38F * memory + nbBetsFlop) / (memory + nbBetsFlop + nbChecksFlop);
		} else if (p.inTurnRound()) {
			return (0.36F * memory + nbBetsTurn) / (memory + nbBetsTurn + nbChecksTurn);
		} else if (p.inRiverRound()) {
			return (0.35F * memory + nbBetsRiver) / (memory + nbBetsRiver + nbChecksRiver);
		} else {
			throw new IllegalStateException();
		}
	}

	public float getRoundCheckFrequency(Propositionalizer p, int memory) {
		return 1 - getRoundBetFrequency(p, memory);
	}

	public float getAF(int memory) {
		float nbRaises = getNbRaises();
		float nbCalls = getNbCalls();
		float nbBets = getNbBets();
		return (2.4F * memory + nbRaises + nbBets) / (memory + nbCalls);
	}

	public float getAFq(int memory) {
		float nbRaises = getNbRaises();
		float nbCalls = getNbCalls();
		float nbBets = getNbBets();
		float nbFolds = getNbFolds();
		return (0.24F * memory + nbRaises + nbBets) / (memory + nbCalls + nbFolds + nbRaises + nbBets);
	}

	public float getAFAmount(int memory) {
		double betAmount = getTotalBetAmount();
		double raiseAmount = getTotalRaiseAmount();
		double callAmount = getTotalCallAmount();
		return (float) ((1.2 * memory + betAmount + raiseAmount) / (memory + callAmount));
	}

	public double getTotalCallAmount() {
		return callAmountPreFlop + callAmountFlop + callAmountTurn + callAmountRiver;
	}

	public double getTotalRaiseAmount() {
		return raiseAmountPreFlop + raiseAmountFlop + raiseAmountTurn + raiseAmountRiver;
	}

	public double getTotalBetAmount() {
		return betAmountPreFlop + betAmountFlop + betAmountTurn + betAmountRiver;
	}

	public double getTotalBetRaiseAmount() {
		return getTotalBetAmount() + getTotalRaiseAmount();
	}

	public double getBetAmountFlop() {
		return betAmountFlop;
	}

	public double getBetAmountPreFlop() {
		return betAmountPreFlop;
	}

	public double getBetAmountPostFlop() {
		return getBetAmountFlop() + getBetAmountTurn() + getBetAmountRiver();
	}

	public double getBetAmountRiver() {
		return betAmountRiver;
	}

	public double getBetAmountTurn() {
		return betAmountTurn;
	}

	public double getRaiseAmountFlop() {
		return raiseAmountFlop;
	}

	public double getRaiseAmountPostFlop() {
		return getRaiseAmountFlop() + getRaiseAmountTurn() + getRaiseAmountRiver();
	}

	public double getRaiseAmountPreFlop() {
		return raiseAmountPreFlop;
	}

	public double getRaiseAmountRiver() {
		return raiseAmountRiver;
	}

	public double getRaiseAmountTurn() {
		return raiseAmountTurn;
	}

	public double getBetRaiseAmountFlop() {
		return betAmountFlop + raiseAmountFlop;
	}

	public double getBetRaiseAmountPreFlop() {
		return betAmountPreFlop + raiseAmountPreFlop;
	}

	public double getBetRaiseAmountTurn() {
		return betAmountTurn + raiseAmountTurn;
	}

	public double getBetRaiseAmountPostFlop() {
		return getBetRaiseAmountFlop() + getBetRaiseAmountTurn() + getBetRaiseAmountRiver();
	}

	public double getBetRaiseAmountRiver() {
		return betAmountRiver + raiseAmountRiver;
	}

	//mutators

	protected void addRaise(Propositionalizer p, double called, double raised) {
		if (p.inPreFlopRound()) {
			++nbRaisesPreFlop;
			callAmountPreFlop += called;
			raiseAmountPreFlop += raised;
		} else if (p.inFlopRound()) {
			++nbRaisesFlop;
			callAmountFlop += called;
			raiseAmountFlop += raised;
		} else if (p.inTurnRound()) {
			++nbRaisesTurn;
			callAmountTurn += called;
			raiseAmountTurn += raised;
		} else if (p.inRiverRound()) {
			++nbRaisesRiver;
			callAmountRiver += called;
			raiseAmountRiver += raised;
		} else {
			throw new IllegalStateException();
		}
	}

	protected void addCall(Propositionalizer p, double called) {
		if (p.inPreFlopRound()) {
			++nbCallsPreFlop;
			callAmountPreFlop += called;
		} else if (p.inFlopRound()) {
			++nbCallsFlop;
			callAmountFlop += called;
		} else if (p.inTurnRound()) {
			++nbCallsTurn;
			callAmountTurn += called;
		} else if (p.inRiverRound()) {
			++nbCallsRiver;
			callAmountRiver += called;
		} else {
			throw new IllegalStateException();
		}
	}

	protected void addFold(Propositionalizer p) {
		if (p.inPreFlopRound()) {
			++nbFoldsPreFlop;
		} else if (p.inFlopRound()) {
			++nbFoldsFlop;
		} else if (p.inTurnRound()) {
			++nbFoldsTurn;
		} else if (p.inRiverRound()) {
			++nbFoldsRiver;
		} else {
			throw new IllegalStateException();
		}
	}

	protected void addBet(Propositionalizer p, double betAmount) {
		if (p.inPreFlopRound()) {
			++nbBetsPreFlop;
			betAmountPreFlop += betAmount;
		} else if (p.inFlopRound()) {
			++nbBetsFlop;
			betAmountFlop += betAmount;
		} else if (p.inTurnRound()) {
			++nbBetsTurn;
			betAmountTurn += betAmount;
		} else if (p.inRiverRound()) {
			++nbBetsRiver;
			betAmountRiver += betAmount;
		} else {
			throw new IllegalStateException();
		}
	}

	protected void addCheck(Propositionalizer p) {
		if (p.inPreFlopRound()) {
			++nbChecksPreFlop;
		} else if (p.inFlopRound()) {
			++nbChecksFlop;
		} else if (p.inTurnRound()) {
			++nbChecksTurn;
		} else if (p.inRiverRound()) {
			++nbChecksRiver;
		} else {
			throw new IllegalStateException();
		}
	}

	public int getNbRoundActions(Propositionalizer p) {
		if (p.inPreFlopRound()) {
			return nbFoldsPreFlop + nbCallsPreFlop + nbRaisesPreFlop + nbChecksPreFlop + nbBetsPreFlop;
		} else if (p.inFlopRound()) {
			return nbFoldsFlop + nbCallsFlop + nbRaisesFlop + nbChecksFlop + nbBetsFlop;
		} else if (p.inTurnRound()) {
			return nbFoldsTurn + nbCallsTurn + nbRaisesTurn + nbChecksTurn + nbBetsTurn;
		} else if (p.inRiverRound()) {
			return nbFoldsRiver + nbCallsRiver + nbRaisesRiver + nbChecksRiver + nbBetsRiver;
		} else {
			throw new IllegalStateException();
		}
	}
}
