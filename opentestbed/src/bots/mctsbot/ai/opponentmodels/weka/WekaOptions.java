package bots.mctsbot.ai.opponentmodels.weka;

public class WekaOptions {

	private static boolean arffPersistency = true;
	private static boolean arffOverwrite = true;
	private static long modelCreationTreshold = 500;
	private static boolean continueAfterCreation = false;
	private static boolean modelPersistency = true;

	public static boolean isArffPersistency() {
		return arffPersistency;
	}

	public static void setArffPersistency(boolean arffPersistency) {
		WekaOptions.arffPersistency = arffPersistency;
	}

	public static boolean isArffOverwrite() {
		return arffOverwrite;
	}

	public static void setArffOverwrite(boolean arffOverwrite) {
		WekaOptions.arffOverwrite = arffOverwrite;
	}

	public static long getModelCreationTreshold() {
		return modelCreationTreshold;
	}

	public static void setModelCreationTreshold(long modelCreationTreshold) {
		WekaOptions.modelCreationTreshold = modelCreationTreshold;
	}

	public static boolean isContinueAfterCreation() {
		return continueAfterCreation;
	}

	public static void setContinueAfterCreation(boolean continueAfterCreation) {
		WekaOptions.continueAfterCreation = continueAfterCreation;
	}

	public static boolean isModelPersistency() {
		return modelPersistency;
	}

	public static void setModelPersistency(boolean modelPersistency) {
		WekaOptions.modelPersistency = modelPersistency;
	}
}
