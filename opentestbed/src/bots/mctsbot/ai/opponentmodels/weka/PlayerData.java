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
 * Class that tracks player statistics when playing games. 
 * 
 * @author guy
 *
 */
public class PlayerData implements Cloneable {

	private final Object id;

	private int bb;
	private int stack;
	private int bet;
	private boolean comitted;

	private boolean lastActionWasRaise;
	private int gameCount = 0;
	private int VPIPCount = 0;
	private int PFRCount = 0;

	private int flopCount = 0;
	private int showdownCount = 0;

	private BetStatistics gameStats = new BetStatistics();
	private BetStatistics globalStats = new BetStatistics();

	public PlayerData(Object id) {
		this.id = id;
	}

	@Override
	protected PlayerData clone() {
		try {
			PlayerData clone = (PlayerData) super.clone();
			clone.gameStats = clone.gameStats.clone();
			clone.globalStats = clone.globalStats.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	public void signalBBAmount(int bb) {
		this.bb = bb;
	}

	public Object getId() {
		return id;
	}

	public int getStack() {
		return stack;
	}

	public double getRelativeStack() {
		return stack / (double) bb;
	}

	public int getBB() {
		return bb;
	}

	public int getBet() {
		return bet;
	}

	public double getRelativeBet() {
		return bet / (double) bb;
	}

	public int getGameCount() {
		return gameCount;
	}

	public BetStatistics getGameStats() {
		return gameStats;
	}

	public BetStatistics getGlobalStats() {
		return globalStats;
	}

	public int getDeficit(Propositionalizer p) {
		return Math.min(stack, (p.getMaxBet() - bet));
	}

	public double getRelativeDeficit(Propositionalizer p) {
		return getDeficit(p) / (double) bb;
	}

	public double getPotOdds(Propositionalizer p) {
		int potSize = p.getPotSize();
		int deficit = getDeficit(p);
		return deficit / (double) (deficit + potSize);
	}

	// "@attribute VPIP real\n"+
	public double getVPIP(int memory) {
		return (0.3F * memory + VPIPCount) / (memory + gameCount);
	}

	// "@attribute PFR real\n"+
	public double getPFR(int memory) {
		return (0.16F * memory + PFRCount) / (memory + gameCount);
	}

	// "@attribute WtSD real\n"+
	public double getWtSD(int memory) {
		if (Math.random() < 0.00001) {
			System.out.println();
			System.out.println(showdownCount + " " + flopCount);
			System.out.println("WtSD for " + id + " = " + ((0.57F * memory + showdownCount) / (memory + flopCount)));
		}
		return (0.57F * memory + showdownCount) / (memory + flopCount);
	}

	public boolean isComitted() {
		return comitted;
	}

	public boolean isLastActionWasRaise() {
		return lastActionWasRaise;
	}

	protected void startNewGame() {
		didVPIP = false;
		comitted = false;
		lastActionWasRaise = false;
		++gameCount;
		gameStats = new BetStatistics();
		startNewRound();
	}

	protected void startNewRound() {
		bet = 0;
		comitted = false;
	}

	protected boolean didVPIP = false;

	protected void updateVPIP(Propositionalizer p) {
		if (!didVPIP && p.inPreFlopRound()) {
			++VPIPCount;
			didVPIP = true;
		}
	}

	protected void updatePFR(Propositionalizer p) {
		if (p.inPreFlopRound() && gameStats.getNbBetsRaisesPreFlop() == 0) {
			++PFRCount;
		}
	}

	public void signalBet(Propositionalizer p, int amount) {
		bet += amount;
		decreaseStack(amount);
		comitted = true;
		lastActionWasRaise = true;
		updateVPIP(p);
		updatePFR(p); //before gameStats
		gameStats.addBet(p, amount / (double) bb);
		globalStats.addBet(p, amount / (double) bb);
	}

	private void decreaseStack(int amount) {
		stack -= amount;
		if (stack < 0) {
			throw new IllegalStateException("Bad stack: " + stack + " when decreasing by " + amount);
		}
	}

	public void signalCheck(Propositionalizer p) {
		lastActionWasRaise = false;
		gameStats.addCheck(p);
		globalStats.addCheck(p);
	}

	public void signalRaise(Propositionalizer p, int raiseAmount, int movedAmount) {
		bet += movedAmount;
		decreaseStack(movedAmount);
		updateVPIP(p);
		updatePFR(p);
		lastActionWasRaise = true;
		comitted = true;
		gameStats.addRaise(p, (movedAmount - raiseAmount) / (double) bb, raiseAmount / (double) bb);
		globalStats.addRaise(p, (movedAmount - raiseAmount) / (double) bb, raiseAmount / (double) bb);
	}

	public void signalCall(Propositionalizer p, int movedAmount) {
		bet += movedAmount;
		decreaseStack(movedAmount);
		lastActionWasRaise = false;
		updateVPIP(p);
		comitted = true;
		gameStats.addCall(p, movedAmount / (double) bb);
		globalStats.addCall(p, movedAmount / (double) bb);
	}

	public void signalFold(Propositionalizer p) {
		lastActionWasRaise = false;
		gameStats.addFold(p);
		globalStats.addFold(p);
	}

	public void signalBlind(int amount) {
		bet = amount;
		decreaseStack(amount);
		comitted = true;
	}

	public void signalFlop() {
		++flopCount;
	}

	public void signalShowdown() {
		++showdownCount;
	}

	public void resetStack(int stack) {
		this.stack = stack;
		if (stack < 0) {
			throw new IllegalStateException("Bad stack: " + stack);
		}
	}

	@Override
	public String toString() {
		return "PlayerData " + Long.toHexString(hashCode()) + " for " + id.toString();
	}

}
