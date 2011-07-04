package bots;

import com.biotools.meerkat.util.Preferences;

/**
 * MetaData as defined in a meerkat-pd file
 * 
 */
public class BotMetaData {
	private String botName;
	private String botClassName;
	private boolean noLimit;
	private Preferences botPreferences;

	public BotMetaData(String botName, String botClassName, boolean noLimit, Preferences botPreferences) {
		super();
		this.botName = botName;
		this.botClassName = botClassName;
		this.botPreferences = botPreferences;
		this.noLimit = noLimit;
	}

	public String getBotName() {
		return botName;
	}

	public String getBotClassName() {
		return botClassName;
	}

	public Preferences getBotPreferences() {
		return botPreferences;
	}

	public boolean isNoLimit() {
		return noLimit;
	}

}
