package bots.mctsbot.ai.opponentmodels.weka;

import java.io.IOException;

import org.apache.log4j.Logger;

import weka.core.Instance;

/**
 * This {@link ARFFPlayer} manages and writes ARFFFiles for each player.<br />
 * After a certain treshold is reached (see {@link WekaOptions} a new weka-model
 * is created with the data and the default model saved in a given
 * {@link WekaRegressionModel} replaced by the new data.
 */
public class ARFFPlayer {

	private final static Logger logger = Logger.getLogger(ARFFPlayer.class);

	private final Object player;

	private ARFFFile preCheckBetFile;
	private ARFFFile postCheckBetFile;
	private ARFFFile preFoldCallRaiseFile;
	private ARFFFile postFoldCallRaiseFile;
	private ARFFFile showdownFile;

	private boolean preCheckBetCreated = false;
	private boolean postCheckBetCreated = false;
	private boolean preFoldCallRaiseCreated = false;
	private boolean postFoldCallRaiseCreated = false;
	private boolean showdownCreated = false;

	private boolean modelCreated = false;

	private WekaRegressionModel model = null;

	public ARFFPlayer(Object player, WekaRegressionModel baseModel) {
		this.player = player;

		if (!modelCreated || (modelCreated && WekaOptions.isContinueAfterCreation())) {
			try {
				String path = "./data/mctsbot/";
				this.model = new WekaRegressionModel(baseModel);

				boolean overwrite = WekaOptions.isArffOverwrite();
				preCheckBetFile = new ARFFFile(path, player, "PreCheckBet.arff", ARFFPropositionalizer.getPreCheckBetInstance().toString(), overwrite);
				postCheckBetFile = new ARFFFile(path, player, "PostCheckBet.arff", ARFFPropositionalizer.getPostCheckBetInstance().toString(), overwrite);
				preFoldCallRaiseFile = new ARFFFile(path, player, "PreFoldCallRaise.arff", ARFFPropositionalizer.getPreFoldCallRaiseInstance().toString(),
						overwrite);
				postFoldCallRaiseFile = new ARFFFile(path, player, "PostFoldCallRaise.arff", ARFFPropositionalizer.getPostFoldCallRaiseInstance().toString(),
						overwrite);
				showdownFile = new ARFFFile(path, player, "Showdown.arff", ARFFPropositionalizer.getShowdownInstance().toString(), overwrite);
			} catch (IOException io) {
				throw new RuntimeException(io);
			}
		}
	}

	public void close() throws IOException {
		if (model != null) {
			preCheckBetFile.close();
			postCheckBetFile.close();
			preFoldCallRaiseFile.close();
			postFoldCallRaiseFile.close();
			showdownFile.close();
		}
	}

	public void writePreCheckBet(Instance instance) {
		if (preCheckBetCreated && !WekaOptions.isContinueAfterCreation())
			return;
		preCheckBetFile.write(instance);
		if (!preCheckBetCreated && preCheckBetFile.isModelReady()) {
			try {
				logger.info("Learning preBetModel for player " + player);
				model.setPreBetModel(preCheckBetFile.createModel("preBet", "betProb", new String[] { "action" }));
				preCheckBetCreated = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void writePostCheckBet(Instance instance) {
		if (postCheckBetCreated && !WekaOptions.isContinueAfterCreation())
			return;
		postCheckBetFile.write(instance);
		if (!postCheckBetCreated && postCheckBetFile.isModelReady()) {
			try {
				logger.info("Learning postBetModel for player " + player);
				model.setPostBetModel(postCheckBetFile.createModel("postBet", "betProb", new String[] { "action" }));
				postCheckBetCreated = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void writePreFoldCallRaise(Instance instance) {
		if (preFoldCallRaiseCreated && !WekaOptions.isContinueAfterCreation())
			return;
		preFoldCallRaiseFile.write(instance);
		if (!preFoldCallRaiseCreated && preFoldCallRaiseFile.isModelReady()) {
			try {
				logger.info("Learning preFoldModel for player " + player);
				model.setPreFoldModel(preFoldCallRaiseFile.createModel("preFold", "foldProb", new String[] { "callProb", "raiseProb", "action" }));
				logger.info("Learning preCallModel for player " + player);
				model.setPreCallModel(preFoldCallRaiseFile.createModel("preCall", "callProb", new String[] { "foldProb", "raiseProb", "action" }));
				logger.info("Learning preRaiseModel for player " + player);
				model.setPreRaiseModel(preFoldCallRaiseFile.createModel("preRaise", "raiseProb", new String[] { "callProb", "foldProb", "action" }));
				preFoldCallRaiseCreated = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void writePostFoldCallRaise(Instance instance) {
		if (postFoldCallRaiseCreated && !WekaOptions.isContinueAfterCreation())
			return;
		postFoldCallRaiseFile.write(instance);
		if (!postFoldCallRaiseCreated && postFoldCallRaiseFile.isModelReady()) {
			try {
				logger.info("Learning postFoldModel for player " + player);
				model.setPostFoldModel(postFoldCallRaiseFile.createModel("postFold", "foldProb", new String[] { "callProb", "raiseProb", "action" }));
				logger.info("Learning postCallModel for player " + player);
				model.setPostCallModel(postFoldCallRaiseFile.createModel("postCall", "callProb", new String[] { "foldProb", "raiseProb", "action" }));
				logger.info("Learning postRaiseModel for player " + player);
				model.setPostRaiseModel(postFoldCallRaiseFile.createModel("postRaise", "raiseProb", new String[] { "callProb", "foldProb", "action" }));
				postFoldCallRaiseCreated = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void writeShowdown(Instance instance) {
		if (showdownCreated && !WekaOptions.isContinueAfterCreation())
			return;
		showdownFile.write(instance);
		if (!showdownCreated && showdownFile.isModelReady()) {
			try {
				logger.info("Learning showdown0Model for player " + player);
				model.setShowdown0Model(showdownFile.createModel("showdown0", "part0Prob", new String[] { "part1Prob", "part2Prob", "part3Prob", "part4Prob",
						"part5Prob", "avgPartition" }));
				logger.info("Learning showdown1Model for player " + player);
				model.setShowdown1Model(showdownFile.createModel("showdown1", "part1Prob", new String[] { "part0Prob", "part2Prob", "part3Prob", "part4Prob",
						"part5Prob", "avgPartition" }));
				logger.info("Learning showdown2Model for player " + player);
				model.setShowdown2Model(showdownFile.createModel("showdown5", "part2Prob", new String[] { "part0Prob", "part1Prob", "part3Prob", "part4Prob",
						"part5Prob", "avgPartition" }));
				logger.info("Learning showdown3Model for player " + player);
				model.setShowdown3Model(showdownFile.createModel("showdown3", "part3Prob", new String[] { "part0Prob", "part1Prob", "part2Prob", "part4Prob",
						"part5Prob", "avgPartition" }));
				logger.info("Learning showdown4Model for player " + player);
				model.setShowdown4Model(showdownFile.createModel("showdown4", "part4Prob", new String[] { "part0Prob", "part1Prob", "part2Prob", "part3Prob",
						"part5Prob", "avgPartition" }));
				logger.info("Learning showdown5Model for player " + player);
				model.setShowdown5Model(showdownFile.createModel("showdown5", "part5Prob", new String[] { "part0Prob", "part1Prob", "part2Prob", "part3Prob",
						"part4Prob", "avgPartition" }));
				// add it to pool
				//OpponentModelPool.getInstance().addModel(player, model);
				showdownCreated = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
