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
package bots.mctsbot.ai.bots.bot.gametree.action;

public class ProbabilityAction implements ActionWrapper {

	private final ActionWrapper actionWrapper;
	private final double probability;

	public ProbabilityAction(ActionWrapper action, double probability) {
		actionWrapper = action;
		this.probability = probability;
	}

	public ActionWrapper getActionWrapper() {
		return actionWrapper;
	}

	public SearchBotAction getAction() {
		return actionWrapper.getAction();
	}

	public double getProbability() {
		return probability;
	}

	@Override
	public String toString() {
		return actionWrapper.toString() + " (" + Math.round(probability * 100) + "% chance)";
	}

}
