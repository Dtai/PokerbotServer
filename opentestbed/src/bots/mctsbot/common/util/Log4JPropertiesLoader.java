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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public final class Log4JPropertiesLoader {

	private static Logger logger = Logger.getLogger(Log4JPropertiesLoader.class);

	private static AtomicBoolean loaded = new AtomicBoolean(false);

	public static void load(String path) {
		if (loaded.compareAndSet(false, true)) {
			Properties properties = new Properties();
			try {
				InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
				properties.load(is);
			} catch (final IOException e) {
				// logger will log to the default logger and write a warning to the
				// error stream?
				e.printStackTrace();
				throw new IllegalStateException(e);
			} catch (final NullPointerException e) {
				// logger will log to the default logger and write a warning to the
				// error stream?
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
			PropertyConfigurator.configure(properties);
			logger.info("Configured Log4J correctly");
		}
	}
}
