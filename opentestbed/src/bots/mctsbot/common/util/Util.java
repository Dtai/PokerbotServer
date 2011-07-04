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
package bots.mctsbot.common.util;

public class Util {

	private Util() {

	}

	public static String parseDollars(double amount) {
		if (Double.isNaN(amount) || Double.isInfinite(amount))
			return (new Double(amount)).toString();
		return parseDollars((int) Math.round(amount));
	}

	public static String parseDollars(int amount) {
		if (amount < 0) {
			return "-" + parseDollars(-amount);
		}
		int cents = amount % 100;
		int dollars = amount / 100;
		return amount == 0 ? "$0" : "$" + dollars + "." + (cents >= 10 ? cents : "0" + cents);
	}

}
