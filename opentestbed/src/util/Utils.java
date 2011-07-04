package util;

public class Utils {

	/**
	 * Math with doubles sucks - simple additions/subtraction can lead
	 * to numbers with big decimals. 
	 * @return
	 */
	public static double roundToCents(double amount) {
		return (Math.round(amount * 100)) / 100D;
	}
}
