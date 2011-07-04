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

public class PostCheckBetInstances extends InstancesBuilder {

	private static final String attributes = "@attribute roundCompletion real" + nl + "@attribute playersActed integer" + nl
			+ "@attribute playersToAct integer" + nl + "@attribute round {flop,turn,river}" + nl + "@attribute gameCount integer" + nl
			+ "@attribute somebodyActedThisRound {false,true}" + nl + "@attribute nbActionsThisRound integer" + nl + "@attribute potSize real" + nl
			+ "@attribute stackSize real" + nl + "@attribute nbSeatedPlayers integer" + nl + "@attribute nbActivePlayers integer" + nl
			+ "@attribute activePlayerRatio real" + nl + "@attribute betFrequency real" + nl + "@attribute betFrequencyRound real" + nl
			+ "@attribute nbAllPlayerRaises integer" + nl + "@attribute nbPlayerRaises integer" + nl + "@attribute gameRaisePercentage real" + nl
			+ "@attribute gameRaiseAmount real" + nl + "@attribute gameRaiseAmountRatio real" + nl + "@attribute lastActionWasRaise {false,true}" + nl
			+ "@attribute VPIP real" + nl + "@attribute PFR real" + nl + "@attribute AF real" + nl + "@attribute AFq real" + nl + "@attribute AFAmount real"
			+ nl + "@attribute WtSD real" + nl + "@attribute opponentVPIP real" + nl + "@attribute opponentPFR real" + nl + "@attribute opponentAF real" + nl
			+ "@attribute opponentAFq real" + nl + "@attribute opponentLogAFAmount real" + nl + "@attribute opponentWtSD real" + nl;

	public PostCheckBetInstances(String name, String targets) {
		super(name, attributes, targets);
	}

	public Instance getUnclassifiedInstance(Propositionalizer prop, Object actor) {
		PlayerData p = prop.getPlayers().get(actor);
		Instance instance = new Instance(length);

		instance.setDataset(dataset);

		instance.setValue(0, prop.getRoundCompletion());
		instance.setValue(1, prop.getPlayersActed());
		instance.setValue(2, prop.getPlayersToAct());
		instance.setValue(3, prop.getRound() + "");
		instance.setValue(4, p.getGameCount());
		instance.setValue(5, prop.isSomebodyActedThisRound() + "");
		instance.setValue(6, prop.getTableGameStats().getNbRoundActions(prop));
		// Amounts
		instance.setValue(7, prop.getRelativePotSize());
		instance.setValue(8, p.getRelativeStack());
		// Player count
		instance.setValue(9, prop.getNbSeatedPlayers());
		instance.setValue(10, prop.getNbActivePlayers());
		instance.setValue(11, prop.getActivePlayerRatio());
		// Global player frequencies
		instance.setValue(12, p.getGlobalStats().getBetFrequency(4));
		// Per-round player frequencies
		instance.setValue(13, p.getGlobalStats().getRoundBetFrequency(prop, 4));
		// Game betting behaviour
		instance.setValue(14, prop.getTableGameStats().getNbBetsRaises());
		instance.setValue(15, p.getGameStats().getNbBetsRaises());
		instance.setValue(16, prop.rel(p.getGameStats().getNbBetsRaises(), prop.getTableGameStats().getNbBetsRaises()));
		instance.setValue(17, (float) Math.log1p(p.getGameStats().getTotalBetRaiseAmount()));
		instance.setValue(18, prop.rel(p.getGameStats().getTotalBetRaiseAmount(), prop.getTableGameStats().getTotalBetRaiseAmount()));
		instance.setValue(19, p.isLastActionWasRaise() + "");
		// PT Stats
		instance.setValue(20, p.getVPIP(4));
		instance.setValue(21, p.getPFR(4));
		instance.setValue(22, p.getGlobalStats().getAF(5));
		instance.setValue(23, p.getGlobalStats().getAFq(5));
		instance.setValue(24, (float) Math.log(p.getGlobalStats().getAFAmount(5)));
		instance.setValue(25, p.getWtSD(4));
		// Table PT stat averages
		instance.setValue(26, prop.getAverageVPIP(p, 4));
		instance.setValue(27, prop.getAveragePFR(p, 4));
		instance.setValue(28, prop.getAverageAF(p, 5));
		instance.setValue(29, prop.getAverageAFq(p, 5));
		instance.setValue(30, prop.getAverageAFAmount(p, 5));
		instance.setValue(31, prop.getAverageWtSD(p, 4));
		return instance;
	}

}
