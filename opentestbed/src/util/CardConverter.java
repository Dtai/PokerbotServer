package util;

import java.util.HashMap;
import java.util.Map;

import com.biotools.meerkat.Card;

public class CardConverter {
	static Map<String, Integer> spears2p2 = new HashMap<String, Integer>();
	static {
		// Card doesn't implement hashcode, so we put strings in here 

		spears2p2.put("2c", Integer.valueOf(1));
		spears2p2.put("2d", Integer.valueOf(2));
		spears2p2.put("2h", Integer.valueOf(3));
		spears2p2.put("2s", Integer.valueOf(4));
		spears2p2.put("3c", Integer.valueOf(5));
		spears2p2.put("3d", Integer.valueOf(6));
		spears2p2.put("3h", Integer.valueOf(7));
		spears2p2.put("3s", Integer.valueOf(8));
		spears2p2.put("4c", Integer.valueOf(9));
		spears2p2.put("4d", Integer.valueOf(10));
		spears2p2.put("4h", Integer.valueOf(11));
		spears2p2.put("4s", Integer.valueOf(12));
		spears2p2.put("5c", Integer.valueOf(13));
		spears2p2.put("5d", Integer.valueOf(14));
		spears2p2.put("5h", Integer.valueOf(15));
		spears2p2.put("5s", Integer.valueOf(16));
		spears2p2.put("6c", Integer.valueOf(17));
		spears2p2.put("6d", Integer.valueOf(18));
		spears2p2.put("6h", Integer.valueOf(19));
		spears2p2.put("6s", Integer.valueOf(20));
		spears2p2.put("7c", Integer.valueOf(21));
		spears2p2.put("7d", Integer.valueOf(22));
		spears2p2.put("7h", Integer.valueOf(23));
		spears2p2.put("7s", Integer.valueOf(24));
		spears2p2.put("8c", Integer.valueOf(25));
		spears2p2.put("8d", Integer.valueOf(26));
		spears2p2.put("8h", Integer.valueOf(27));
		spears2p2.put("8s", Integer.valueOf(28));
		spears2p2.put("9c", Integer.valueOf(29));
		spears2p2.put("9d", Integer.valueOf(30));
		spears2p2.put("9h", Integer.valueOf(31));
		spears2p2.put("9s", Integer.valueOf(32));
		spears2p2.put("Tc", Integer.valueOf(33));
		spears2p2.put("Td", Integer.valueOf(34));
		spears2p2.put("Th", Integer.valueOf(35));
		spears2p2.put("Ts", Integer.valueOf(36));
		spears2p2.put("Jc", Integer.valueOf(37));
		spears2p2.put("Jd", Integer.valueOf(38));
		spears2p2.put("Jh", Integer.valueOf(39));
		spears2p2.put("Js", Integer.valueOf(40));
		spears2p2.put("Qc", Integer.valueOf(41));
		spears2p2.put("Qd", Integer.valueOf(42));
		spears2p2.put("Qh", Integer.valueOf(43));
		spears2p2.put("Qs", Integer.valueOf(44));
		spears2p2.put("Kc", Integer.valueOf(45));
		spears2p2.put("Kd", Integer.valueOf(46));
		spears2p2.put("Kh", Integer.valueOf(47));
		spears2p2.put("Ks", Integer.valueOf(48));
		spears2p2.put("Ac", Integer.valueOf(49));
		spears2p2.put("Ad", Integer.valueOf(50));
		spears2p2.put("Ah", Integer.valueOf(51));
		spears2p2.put("As", Integer.valueOf(52));

		/*
		 * Card to integer conversions: 2c = 1 2d = 2 2h = 3 2s = 4 3c = 5 3d = 6 3h
		 * = 7 3s = 8 4c = 9 4d = 10 4h = 11 4s = 12 5c = 13 5d = 14 5h = 15 5s = 16
		 * 6c = 17 6d = 18 6h = 19 6s = 20 7c = 21 7d = 22 7h = 23 7s = 24 8c = 25
		 * 8d = 26 8h = 27 8s = 28 9c = 29 9d = 30 9h = 31 9s = 32 Tc = 33 Td = 34
		 * Th = 35 Ts = 36 Jc = 37 Jd = 38 Jh = 39 Js = 40 Qc = 41 Qd = 42 Qh = 43
		 * Qs = 44 Kc = 45 Kd = 46 Kh = 47 Ks = 48 Ac = 49 Ad = 50 Ah = 51 As = 52
		 */
	}

	public static int toSpears2p2Index(Card card) {
		return spears2p2.get(card.toString());

	}
}
