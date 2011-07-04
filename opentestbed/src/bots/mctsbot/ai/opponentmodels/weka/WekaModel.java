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

import org.apache.log4j.Logger;

import weka.core.Instance;
import bots.mctsbot.ai.opponentmodels.weka.instances.InstancesBuilder;
import bots.mctsbot.ai.opponentmodels.weka.instances.PostCheckBetInstances;
import bots.mctsbot.ai.opponentmodels.weka.instances.PostFoldCallRaiseInstances;
import bots.mctsbot.ai.opponentmodels.weka.instances.PreCheckBetInstances;
import bots.mctsbot.ai.opponentmodels.weka.instances.PreFoldCallRaiseInstances;
import bots.mctsbot.ai.opponentmodels.weka.instances.ShowdownInstances;
import bots.mctsbot.common.elements.player.PlayerId;

public abstract class WekaModel {

	protected static final Logger logger = Logger.getLogger(WekaRegressionModel.class);

	private final PostCheckBetInstances postCheckBetInstance;
	private final PreCheckBetInstances preCheckBetInstance;
	private final PreFoldCallRaiseInstances preFoldCallRaiseInstance;
	private final PostFoldCallRaiseInstances postFoldCallRaiseInstance;
	private final ShowdownInstances showdownInstance;

	public WekaModel() {
		this.preCheckBetInstance = new PreCheckBetInstances("PreCheckBet", "@attribute prob real" + InstancesBuilder.nl);
		this.postCheckBetInstance = new PostCheckBetInstances("PostCheckBet", "@attribute prob real" + InstancesBuilder.nl);
		this.preFoldCallRaiseInstance = new PreFoldCallRaiseInstances("PreFoldCallRaise", "@attribute prob real" + InstancesBuilder.nl);
		this.postFoldCallRaiseInstance = new PostFoldCallRaiseInstances("PostFoldCallRaise", "@attribute prob real" + InstancesBuilder.nl);
		this.showdownInstance = new ShowdownInstances("Showdown", "@attribute prob real" + InstancesBuilder.nl);
	}

	//	public long getVisitorSize() {
	//		System.out.print("<" + visitors.size() + ">");
	//		return visitors.size();
	//	}

	protected Instance getPreCheckBetInstance(PlayerId actor, Propositionalizer props) {
		return preCheckBetInstance.getUnclassifiedInstance(props, actor);
	}

	protected Instance getPostCheckBetInstance(PlayerId actor, Propositionalizer props) {
		return postCheckBetInstance.getUnclassifiedInstance(props, actor);
	}

	protected Instance getPostFoldCallRaiseInstance(PlayerId actor, Propositionalizer props) {
		return postFoldCallRaiseInstance.getUnclassifiedInstance(props, actor);
	}

	protected Instance getPreFoldCallRaiseInstance(PlayerId actor, Propositionalizer props) {
		return preFoldCallRaiseInstance.getUnclassifiedInstance(props, actor);
	}

	protected Instance getShowdownInstance(PlayerId actor, Propositionalizer props) {
		return showdownInstance.getUnclassifiedInstance(props, actor);
	}

}