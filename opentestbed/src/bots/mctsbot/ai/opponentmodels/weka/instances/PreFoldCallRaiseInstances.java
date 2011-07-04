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

public class PreFoldCallRaiseInstances extends InstancesBuilder {

	private final static String attributes = "@attribute roundCompletion real" + nl + "@attribute playersActed integer" + nl
			+ "@attribute playersToAct integer" + nl + "@attribute gameCount integer" + nl + "@attribute somebodyActedThisRound {false,true}" + nl
			+ "@attribute nbActionsThisRound integer" + nl + "@attribute potSize real" + nl + "@attribute stackSize real" + nl + "@attribute deficit real" + nl
			+ "@attribute potOdds real" + nl + "@attribute maxbet real" + nl + "@attribute nbSeatedPlayers integer" + nl + "@attribute nbActivePlayers integer"
			+ nl + "@attribute activePlayerRatio real" + nl + "@attribute foldFrequency real" + nl + "@attribute callFrequency real" + nl
			+ "@attribute raiseFrequency real" + nl + "@attribute foldFrequencyRound real" + nl + "@attribute callFrequencyRound real" + nl
			+ "@attribute raiseFrequencyRound real" + nl + "@attribute isComitted {false,true}" + nl + "@attribute nbAllPlayerRaises integer" + nl
			+ "@attribute nbPlayerRaises integer" + nl + "@attribute gameRaisePercentage real" + nl + "@attribute gameRaiseAmount real" + nl
			+ "@attribute gameRaiseAmountRatio real" + nl + "@attribute lastActionWasRaise {false,true}" + nl + "@attribute VPIP real" + nl
			+ "@attribute PFR real" + nl + "@attribute AF real" + nl + "@attribute AFq real" + nl + "@attribute AFAmount real" + nl + "@attribute WtSD real"
			+ nl + "@attribute opponentVPIP real" + nl + "@attribute opponentPFR real" + nl + "@attribute opponentAF real" + nl + "@attribute opponentAFq real"
			+ nl + "@attribute opponentLogAFAmount real" + nl + "@attribute opponentWtSD real" + nl;

	public PreFoldCallRaiseInstances(String name, String targets) {
		super(name, attributes, targets);
	}

	public Instance getUnclassifiedInstance(Propositionalizer prop, Object actor) {
		PlayerData p = prop.getPlayers().get(actor);
		Instance instance = new Instance(length);

		instance.setDataset(dataset);

		instance.setValue(0, prop.getRoundCompletion());
		instance.setValue(1, prop.getPlayersActed());
		instance.setValue(2, prop.getPlayersToAct());
		instance.setValue(3, p.getGameCount());
		instance.setValue(4, prop.isSomebodyActedThisRound() + "");
		instance.setValue(5, prop.getTableGameStats().getNbRoundActions(prop));
		// Amounts
		instance.setValue(6, prop.getRelativePotSize());
		instance.setValue(7, p.getRelativeStack());
		instance.setValue(8, (float) Math.log(p.getRelativeDeficit(prop)));
		instance.setValue(9, p.getPotOdds(prop));
		instance.setValue(10, (float) Math.log(prop.getRelativeMaxBet()));
		// Player count
		instance.setValue(11, prop.getNbSeatedPlayers());
		instance.setValue(12, prop.getNbActivePlayers());
		instance.setValue(13, prop.getActivePlayerRatio());
		// Global player frequencies
		instance.setValue(14, p.getGlobalStats().getFoldFrequency(4));
		instance.setValue(15, p.getGlobalStats().getCallFrequency(4));
		instance.setValue(16, p.getGlobalStats().getRaiseFrequency(4));
		// Per-round player frequencies
		instance.setValue(17, p.getGlobalStats().getRoundFoldFrequency(prop, 4));
		instance.setValue(18, p.getGlobalStats().getRoundCallFrequency(prop, 4));
		instance.setValue(19, p.getGlobalStats().getRoundRaiseFrequency(prop, 4));
		// Game betting behaviour
		instance.setValue(20, p.isComitted() + "");
		instance.setValue(21, prop.getTableGameStats().getNbBetsRaises());
		instance.setValue(22, p.getGameStats().getNbBetsRaises());
		instance.setValue(23, prop.rel(p.getGameStats().getNbBetsRaises(), prop.getTableGameStats().getNbBetsRaises()));
		instance.setValue(24, (float) Math.log1p(p.getGameStats().getTotalBetRaiseAmount()));
		instance.setValue(25, prop.rel(p.getGameStats().getTotalBetRaiseAmount(), prop.getTableGameStats().getTotalBetRaiseAmount()));
		instance.setValue(26, p.isLastActionWasRaise() + "");
		// PT Stats
		instance.setValue(27, p.getVPIP(4));
		instance.setValue(28, p.getPFR(4));
		instance.setValue(29, p.getGlobalStats().getAF(5));
		instance.setValue(30, p.getGlobalStats().getAFq(5));
		instance.setValue(31, (float) Math.log(p.getGlobalStats().getAFAmount(5)));
		instance.setValue(32, p.getWtSD(4));
		// Table PT stat averages
		instance.setValue(33, prop.getAverageVPIP(p, 4));
		instance.setValue(34, prop.getAveragePFR(p, 4));
		instance.setValue(35, prop.getAverageAF(p, 5));
		instance.setValue(36, prop.getAverageAFq(p, 5));
		instance.setValue(37, prop.getAverageAFAmount(p, 5));
		instance.setValue(38, prop.getAverageWtSD(p, 4));
		return instance;
	}

}
