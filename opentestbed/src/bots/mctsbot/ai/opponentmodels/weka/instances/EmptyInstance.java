package bots.mctsbot.ai.opponentmodels.weka.instances;

import weka.core.Instance;
import bots.mctsbot.ai.opponentmodels.weka.Propositionalizer;

public class EmptyInstance extends InstancesBuilder {

	private static final String attributes = "@attribute round {preflop,flop,turn,river}" + nl;

	public EmptyInstance(String name, String targets) {
		super(name, attributes, targets);
	}

	@Override
	public Instance getUnclassifiedInstance(Propositionalizer prop, Object actorId) {
		Instance instance = new Instance(length);
		instance.setDataset(dataset);
		instance.setValue(0, prop.getRound() + "");
		return instance;
	}
}
