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
package bots.mctsbot.ai.opponentmodels.weka.instances;

import weka.core.Instance;
import bots.mctsbot.ai.opponentmodels.weka.PlayerData;
import bots.mctsbot.ai.opponentmodels.weka.Propositionalizer;

public class ShowdownInstances extends InstancesBuilder {

	private final static String attributes = "@attribute gameCount integer" + nl + "@attribute potSize real" + nl + "@attribute logPotSize real" + nl
			+ "@attribute stackSize real" + nl + "@attribute log1pStackSize real" + nl + "@attribute nbSeatedPlayers integer" + nl
			+ "@attribute nbActivePlayers integer" + nl + "@attribute activePlayerRatio real" + nl + "@attribute betFrequency real" + nl
			+ "@attribute foldFrequency real" + nl + "@attribute callFrequency real" + nl + "@attribute raiseFrequency real" + nl
			+ "@attribute nbAllPlayerBetRaise integer" + nl + "@attribute nbPlayerBetRaise integer" + nl + "@attribute nbPlayerPreFlopBetRaise integer" + nl
			+ "@attribute nbPlayerPostFlopBetRaise integer" + nl + "@attribute nbPlayerFlopBetRaise integer" + nl + "@attribute nbPlayerTurnBetRaise integer"
			+ nl + "@attribute nbPlayerRiverBetRaise integer" + nl + "@attribute allPlayerBetRaiseAmount integer" + nl
			+ "@attribute playerBetRaiseAmount integer" + nl + "@attribute playerPreFlopBetRaiseAmount integer" + nl
			+ "@attribute playerPostFlopBetRaiseAmount integer" + nl + "@attribute playerFlopBetRaiseAmount integer" + nl
			+ "@attribute playerTurnBetRaiseAmount integer" + nl + "@attribute playerRiverBetRaiseAmount integer" + nl
			+ "@attribute allPlayerBetsAmount integer" + nl + "@attribute playerBetsAmount integer" + nl + "@attribute playerPreFlopBetsAmount integer" + nl
			+ "@attribute playerPostFlopBetsAmount integer" + nl + "@attribute playerFlopBetsAmount integer" + nl + "@attribute playerTuretsAmount integer"
			+ nl + "@attribute playerRiverBetsAmount integer" + nl + "@attribute allPlayerRaisesAmount integer" + nl + "@attribute playerRaisesAmount integer"
			+ nl + "@attribute playerPreFlopRaisesAmount integer" + nl + "@attribute playerPostFlopRaisesAmount integer" + nl
			+ "@attribute playerFlopRaisesAmount integer" + nl + "@attribute playerTurnRaisesAmount integer" + nl
			+ "@attribute playerRiverRaisesAmount integer" + nl + "@attribute relNbPlayerBetRaise integer" + nl
			+ "@attribute relNbPlayerPreFlopBetRaise integer" + nl + "@attribute relNbPlayerPostFlopBetRaise integer" + nl
			+ "@attribute relNbPlayerFlopBetRaise integer" + nl + "@attribute relNbPlayerTurnBetRaise integer" + nl
			+ "@attribute relNbPlayerRiverBetRaise integer" + nl + "@attribute relPlayerBetRaiseAmount integer" + nl
			+ "@attribute relPlayerPreFlopBetRaiseAmount integer" + nl + "@attribute relPlayerPostFlopBetRaiseAmount integer" + nl
			+ "@attribute relPlayerFlopBetRaiseAmount integer" + nl + "@attribute relPlayerTurnBetRaiseAmount integer" + nl
			+ "@attribute relPlayerRiverBetRaiseAmount integer" + nl + "@attribute nbPlayerPreFlopBetRaiseTable integer" + nl
			+ "@attribute nbPlayerPostFlopBetRaiseTable integer" + nl + "@attribute nbPlayerFlopBetRaiseTable integer" + nl
			+ "@attribute nbPlayerTurnBetRaiseTable integer" + nl + "@attribute nbPlayerRiverBetRaiseTable integer" + nl
			+ "@attribute playerPreFlopBetRaiseAmountTable integer" + nl + "@attribute playerPostFlopBetRaiseAmountTable integer" + nl
			+ "@attribute playerFlopBetRaiseAmountTable integer" + nl + "@attribute playerTurnBetRaiseAmountTable integer" + nl
			+ "@attribute playerRiverBetRaiseAmountTable integer" + nl + "@attribute lastActionWasBetRaise {false,true}" + nl + "@attribute VPIP real" + nl
			+ "@attribute PFR real" + nl + "@attribute AF real" + nl + "@attribute AFq real" + nl + "@attribute logAFAmount real" + nl + "@attribute WtSD real"
			+ nl + "@attribute opponentVPIP real" + nl + "@attribute opponentPFR real" + nl + "@attribute opponentAF real" + nl + "@attribute opponentAFq real"
			+ nl + "@attribute opponentLogAFAmount real" + nl + "@attribute opponentWtSD real" + nl;

	public ShowdownInstances(String name, String targets) {
		super(name, attributes, targets);
	}

	public Instance getUnclassifiedInstance(Propositionalizer prop, Object actor) {
		PlayerData p = prop.getPlayers().get(actor);
		Instance instance = new Instance(length);

		instance.setDataset(dataset);

		instance.setValue(0, p.getGameCount());
		// Amounts
		instance.setValue(1, prop.getRelativePotSize());
		instance.setValue(2, (float) Math.log(prop.getRelativePotSize()));
		instance.setValue(3, p.getRelativeStack());
		instance.setValue(4, (float) Math.log1p(p.getRelativeStack()));
		// Player count
		instance.setValue(5, prop.getNbSeatedPlayers());
		instance.setValue(6, prop.getNbActivePlayers());
		instance.setValue(7, prop.getActivePlayerRatio());
		// Global player frequencies
		instance.setValue(8, p.getGlobalStats().getBetFrequency(4));
		instance.setValue(9, p.getGlobalStats().getFoldFrequency(4));
		instance.setValue(10, p.getGlobalStats().getCallFrequency(4));
		instance.setValue(11, p.getGlobalStats().getRaiseFrequency(4));
		//BetRaise counts
		instance.setValue(12, prop.getTableGameStats().getNbBetsRaises());
		instance.setValue(13, p.getGameStats().getNbBetsRaises());
		instance.setValue(14, p.getGameStats().getNbBetsRaisesPreFlop());
		instance.setValue(15, p.getGameStats().getNbBetsRaisesPostFlop());
		instance.setValue(16, p.getGameStats().getNbBetsRaisesFlop());
		instance.setValue(17, p.getGameStats().getNbBetsRaisesTurn());
		instance.setValue(18, p.getGameStats().getNbBetsRaisesRiver());
		//BetRaise amount
		instance.setValue(19, (float) prop.getTableGameStats().getTotalBetRaiseAmount());
		instance.setValue(20, (float) p.getGameStats().getTotalBetRaiseAmount());
		instance.setValue(21, (float) p.getGameStats().getBetRaiseAmountPreFlop());
		instance.setValue(22, (float) p.getGameStats().getBetRaiseAmountPostFlop());
		instance.setValue(23, (float) p.getGameStats().getBetRaiseAmountFlop());
		instance.setValue(24, (float) p.getGameStats().getBetRaiseAmountTurn());
		instance.setValue(25, (float) p.getGameStats().getBetRaiseAmountRiver());
		//Bet amount
		instance.setValue(26, (float) prop.getTableGameStats().getTotalBetAmount());
		instance.setValue(27, (float) p.getGameStats().getTotalBetAmount());
		instance.setValue(28, (float) p.getGameStats().getBetAmountPreFlop());
		instance.setValue(29, (float) p.getGameStats().getBetAmountPostFlop());
		instance.setValue(30, (float) p.getGameStats().getBetAmountFlop());
		instance.setValue(31, (float) p.getGameStats().getBetAmountTurn());
		instance.setValue(32, (float) p.getGameStats().getBetAmountRiver());
		//Raise amount
		instance.setValue(33, (float) prop.getTableGameStats().getTotalRaiseAmount());
		instance.setValue(34, (float) p.getGameStats().getTotalRaiseAmount());
		instance.setValue(35, (float) p.getGameStats().getRaiseAmountPreFlop());
		instance.setValue(36, (float) p.getGameStats().getRaiseAmountPostFlop());
		instance.setValue(37, (float) p.getGameStats().getRaiseAmountFlop());
		instance.setValue(38, (float) p.getGameStats().getRaiseAmountTurn());
		instance.setValue(39, (float) p.getGameStats().getRaiseAmountRiver());
		// Relative BetRaise counts
		instance.setValue(40, prop.rel(p.getGameStats().getNbBetsRaises(), prop.getTableGameStats().getNbBetsRaises()));
		instance.setValue(41, prop.rel(p.getGameStats().getNbBetsRaisesPreFlop(), prop.getTableGameStats().getNbBetsRaisesPreFlop()));
		instance.setValue(42, prop.rel(p.getGameStats().getNbBetsRaisesPostFlop(), prop.getTableGameStats().getNbBetsRaisesPostFlop()));
		instance.setValue(43, prop.rel(p.getGameStats().getNbBetsRaisesFlop(), prop.getTableGameStats().getNbBetsRaisesFlop()));
		instance.setValue(44, prop.rel(p.getGameStats().getNbBetsRaisesTurn(), prop.getTableGameStats().getNbBetsRaisesTurn()));
		instance.setValue(45, prop.rel(p.getGameStats().getNbBetsRaisesRiver(), prop.getTableGameStats().getNbBetsRaisesRiver()));
		// Relative BetRaise amounts
		instance.setValue(46, prop.rel(p.getGameStats().getTotalBetRaiseAmount(), prop.getTableGameStats().getTotalBetRaiseAmount()));
		instance.setValue(47, prop.rel(p.getGameStats().getBetRaiseAmountPreFlop(), prop.getTableGameStats().getBetRaiseAmountPreFlop()));
		instance.setValue(48, prop.rel(p.getGameStats().getBetRaiseAmountPostFlop(), prop.getTableGameStats().getBetRaiseAmountPostFlop()));
		instance.setValue(49, prop.rel(p.getGameStats().getBetRaiseAmountFlop(), prop.getTableGameStats().getBetRaiseAmountFlop()));
		instance.setValue(50, prop.rel(p.getGameStats().getBetRaiseAmountTurn(), prop.getTableGameStats().getBetRaiseAmountTurn()));
		instance.setValue(51, prop.rel(p.getGameStats().getBetRaiseAmountRiver(), prop.getTableGameStats().getBetRaiseAmountRiver()));
		//Table BetRaise counts
		instance.setValue(52, prop.getTableGameStats().getNbBetsRaisesPreFlop());
		instance.setValue(53, prop.getTableGameStats().getNbBetsRaisesPostFlop());
		instance.setValue(54, prop.getTableGameStats().getNbBetsRaisesFlop());
		instance.setValue(55, prop.getTableGameStats().getNbBetsRaisesTurn());
		instance.setValue(56, prop.getTableGameStats().getNbBetsRaisesRiver());
		//Table BetRaise amount
		instance.setValue(57, (float) prop.getTableGameStats().getBetRaiseAmountPreFlop());
		instance.setValue(58, (float) prop.getTableGameStats().getBetRaiseAmountPostFlop());
		instance.setValue(59, (float) prop.getTableGameStats().getBetRaiseAmountFlop());
		instance.setValue(60, (float) prop.getTableGameStats().getBetRaiseAmountTurn());
		instance.setValue(61, (float) prop.getTableGameStats().getBetRaiseAmountRiver());
		// Other
		instance.setValue(62, p.isLastActionWasRaise() + "");
		// PT Stats
		instance.setValue(63, p.getVPIP(4));
		instance.setValue(64, p.getPFR(4));
		instance.setValue(65, p.getGlobalStats().getAF(5));
		instance.setValue(66, p.getGlobalStats().getAFq(5));
		instance.setValue(67, (float) Math.log(p.getGlobalStats().getAFAmount(5)));
		instance.setValue(68, p.getWtSD(4));
		// Table PT stat averages
		instance.setValue(69, prop.getAverageVPIP(p, 4));
		instance.setValue(70, prop.getAveragePFR(p, 4));
		instance.setValue(71, prop.getAverageAF(p, 5));
		instance.setValue(72, prop.getAverageAFq(p, 5));
		instance.setValue(73, prop.getAverageAFAmount(p, 5));
		instance.setValue(74, prop.getAverageWtSD(p, 4));
		return instance;
	}

}
