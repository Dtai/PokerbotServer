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

import java.io.StringReader;

import org.apache.log4j.Logger;

import weka.core.Instance;
import weka.core.Instances;
import bots.mctsbot.ai.opponentmodels.weka.Propositionalizer;

public abstract class InstancesBuilder {

	private static final Logger logger = Logger.getLogger(InstancesBuilder.class);

	public final static String nl = "\n";

	public final int length;

	protected final Instances dataset;

	public InstancesBuilder(String name, String attributes, String targets) {
		String header = "@relation " + name + nl + attributes + targets + "@data" + nl;
		this.length = attributes.split(nl).length + targets.split(nl).length;

		try {
			this.dataset = new Instances(new StringReader(header));
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		dataset.setClassIndex((length > 0 ? length - 1 : 0));
	}

	public abstract Instance getUnclassifiedInstance(Propositionalizer prop, Object actorId);

	public void addClassifiedInstance(Propositionalizer prop, Object actorId, Object[] targets) {
		Instance instance = getClassifiedInstance(prop, actorId, targets);
		dataset.add(instance);
	}

	public Instance getClassifiedInstance(Propositionalizer prop, Object actorId, Object[] targets) {
		Instance instance = getUnclassifiedInstance(prop, actorId);
		for (int i = 0; i < targets.length; i++) {
			//dirty, blame weka
			if (targets[i] instanceof Number) {
				instance.setValue(length - targets.length + i, ((Number) targets[i]).doubleValue());
			} else {
				instance.setValue(length - targets.length + i, (String) targets[i]);
			}
		}
		return instance;
	}

	@Override
	public String toString() {
		return dataset.toString();
	}

}
