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

public class PreCheckBetInstances extends InstancesBuilder {

	private final static String attributes = "@attribute roundCompletion real" + nl + "@attribute playersActed integer" + nl
			+ "@attribute playersToAct integer" + nl + "@attribute gameCount integer" + nl + "@attribute nbActionsThisRound integer" + nl
			+ "@attribute potSize real" + nl + "@attribute stackSize real" + nl + "@attribute nbSeatedPlayers integer" + nl
			+ "@attribute nbActivePlayers integer" + nl + "@attribute activePlayerRatio real" + nl + "@attribute betFrequency real" + nl
			+ "@attribute betFrequencyRound real" + nl + "@attribute VPIP real" + nl + "@attribute PFR real" + nl + "@attribute AF real" + nl
			+ "@attribute AFq real" + nl + "@attribute AFAmount real" + nl + "@attribute WtSD real" + nl + "@attribute opponentVPIP real" + nl
			+ "@attribute opponentPFR real" + nl + "@attribute opponentAF real" + nl + "@attribute opponentAFq real" + nl
			+ "@attribute opponentLogAFAmount real" + nl + "@attribute opponentWtSD real" + nl;

	public PreCheckBetInstances(String name, String targets) {
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
		instance.setValue(4, prop.getTableGameStats().getNbRoundActions(prop));
		// Amounts
		instance.setValue(5, prop.getRelativePotSize());
		instance.setValue(6, p.getRelativeStack());
		// Player count
		instance.setValue(7, prop.getNbSeatedPlayers());
		instance.setValue(8, prop.getNbActivePlayers());
		instance.setValue(9, prop.getActivePlayerRatio());
		// Global player frequencies
		instance.setValue(10, p.getGlobalStats().getBetFrequency(4));
		// Per-round player frequencies
		instance.setValue(11, p.getGlobalStats().getRoundBetFrequency(prop, 4));
		// PT Stats
		instance.setValue(12, p.getVPIP(4));
		instance.setValue(13, p.getPFR(4));
		instance.setValue(14, p.getGlobalStats().getAF(5));
		instance.setValue(15, p.getGlobalStats().getAFq(5));
		instance.setValue(16, (float) Math.log(p.getGlobalStats().getAFAmount(5)));
		instance.setValue(17, p.getWtSD(4));
		// Table PT stat averages
		instance.setValue(18, prop.getAverageVPIP(p, 4));
		instance.setValue(19, prop.getAveragePFR(p, 4));
		instance.setValue(20, prop.getAverageAF(p, 5));
		instance.setValue(21, prop.getAverageAFq(p, 5));
		instance.setValue(22, prop.getAverageAFAmount(p, 5));
		instance.setValue(23, prop.getAverageWtSD(p, 4));
		return instance;
	}

}
