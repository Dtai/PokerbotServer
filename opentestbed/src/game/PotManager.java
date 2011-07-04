package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.Utils;

import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.PlayerInfo;

public class PotManager {
	private List<Pot> pots = new ArrayList<Pot>();
	private GameInfo gameInfo;

	public PotManager(GameInfo gameInfo) {
		this.gameInfo = gameInfo;
		this.pots.add(new Pot());
	}

	public Pot getPot(int pot) {
		return pots.get(pot);
	}

	public int getNumPots() {
		return pots.size();
	}

	public double getTotalPotSize() {
		double potSize = 0.0D;
		for (Iterator<Pot> i = this.pots.iterator(); i.hasNext();)
			potSize += i.next().getValue();
		return potSize;
	}

	public void addToPot(int seat, double amount) {
		PlayerInfo player = gameInfo.getPlayer(seat);
		double playerAmountInPot = player.getAmountInPot();
		boolean playerAllIn = Utils.roundToCents(player.getBankRoll() - amount) == 0;
		int lastPotPaidIndex = 0;
		while (!pots.get(lastPotPaidIndex).isCanGrow() && pots.get(lastPotPaidIndex).getUpperBound() < playerAmountInPot) {
			lastPotPaidIndex++;
		}

		double amountToDistribute = amount;
		while (amountToDistribute > 0) {
			Pot lastPotPaid = pots.get(lastPotPaidIndex);

			double amountMissingInThisPot = Utils.roundToCents(lastPotPaid.getUpperBound() - playerAmountInPot);
			if (amountToDistribute >= amountMissingInThisPot) {
				if (lastPotPaid.isCanGrow() || amountToDistribute == amountMissingInThisPot) {
					lastPotPaid.addToPot(amountToDistribute, seat);
					lastPotPaid.setUpperBound(Utils.roundToCents(playerAmountInPot + amountToDistribute));
					if (playerAllIn) {
						// player is All-In - mark this pot as not growable anymore
						lastPotPaid.setCanGrow(false);
					}
					amountToDistribute = 0;
					playerAmountInPot = Utils.roundToCents(playerAmountInPot + amountToDistribute);
				} else {
					amountToDistribute = Utils.roundToCents(amountToDistribute - amountMissingInThisPot);
					playerAmountInPot = Utils.roundToCents(playerAmountInPot + amountMissingInThisPot);
					lastPotPaid.addToPot(amountMissingInThisPot, seat);
					lastPotPaidIndex++;
					if (lastPotPaidIndex == pots.size()) {
						pots.add(new Pot());
					}
				}
			} else {
				lastPotPaid.addToPot(amountToDistribute, seat);
				double potLowerBound = 0;
				if (lastPotPaidIndex > 0) {
					potLowerBound = pots.get(lastPotPaidIndex - 1).getUpperBound();
				}

				splitPot(lastPotPaidIndex, seat, Utils.roundToCents(playerAmountInPot - potLowerBound + amountToDistribute));
				amountToDistribute = 0;
			}
		}
	}

	private void splitPot(int splitPotIndex, int seat, double splitAmount) {
		Pot newPot = new Pot();
		Pot splitPot = pots.get(splitPotIndex);

		double potLowerBound = 0;
		if (splitPotIndex > 0) {
			potLowerBound = pots.get(splitPotIndex - 1).getUpperBound();
		}
		for (Integer potPlayerSeat : splitPot.getEligiblePlayers()) {
			PlayerInfo potPlayer = gameInfo.getPlayer(potPlayerSeat);
			double playerAmountInThisPot = Math.min(Utils.roundToCents(potPlayer.getAmountInPot() - potLowerBound), splitPot.getUpperBound());
			if (playerAmountInThisPot > splitAmount) {
				double toMove = Utils.roundToCents(playerAmountInThisPot - splitAmount);
				splitPot.moveToPot(newPot, toMove, potPlayerSeat);
				double nextPotUpperBound = Utils.roundToCents(potLowerBound + splitAmount + toMove);
				if (newPot.getUpperBound() < nextPotUpperBound) {
					newPot.setUpperBound(nextPotUpperBound);
				}
			}
		}
		pots.add(splitPotIndex + 1, newPot);
		splitPot.setCanGrow(false);
		splitPot.setUpperBound(Utils.roundToCents(potLowerBound + splitAmount));

	}

	/**
	 * little hacked method for JUnit-Tests.<br>
	 * Manipulates and sets the pots
	 * @param potIndex
	 * @param amount
	 * @param seats
	 */
	protected void setPot(int potIndex, double amount, int[] seats) {
		if (pots.size() <= potIndex) {
			pots.add(new Pot());
		}
		Pot pot = pots.get(potIndex);
		pot.addToPot(amount, seats[0]);
		for (int i = 1; i < seats.length; i++) {
			pot.addToPot(0, seats[i]);
		}
	}

	public void removeFromPot(int seat, double amount) {
		Pot lastPot = pots.get(pots.size() - 1);
		if (!lastPot.getEligiblePlayers().contains(Integer.valueOf(seat))) {
			throw new IllegalStateException("can only return money from player in lastPot");
		}
		lastPot.addToPot(-amount, seat);
		if (lastPot.getValue() == 0) {
			pots.remove(pots.get(pots.size() - 1));
		}

	}

}
