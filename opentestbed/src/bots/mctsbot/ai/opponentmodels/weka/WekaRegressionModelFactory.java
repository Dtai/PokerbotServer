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
package bots.mctsbot.ai.opponentmodels.weka;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import weka.classifiers.Classifier;
import bots.mctsbot.ai.opponentmodels.OpponentModel;

public class WekaRegressionModelFactory implements OpponentModel.Factory {

	public static WekaRegressionModelFactory createForZip(String zippedModel) throws IOException, ClassNotFoundException {
		ZipInputStream zis = null;
		ClassLoader classLoader = WekaRegressionModelFactory.class.getClassLoader();

		InputStream fis = classLoader.getResourceAsStream(zippedModel);
		zis = new ZipInputStream(fis);

		ZipEntry entry;
		Map<String, Classifier> classifiers = new HashMap<String, Classifier>();

		while ((entry = zis.getNextEntry()) != null) {
			logger.info("Unzipping: " + entry.getName());
			ObjectInputStream in = new ObjectInputStream(zis);
			classifiers.put(entry.getName(), (Classifier) in.readObject());
			zis.closeEntry();
		}

		zis.close();
		fis.close();

		return new WekaRegressionModelFactory(classifiers.get("preBet.model"), classifiers.get("preFold.model"), classifiers.get("preCall.model"),
				classifiers.get("preRaise.model"), classifiers.get("postBet.model"), classifiers.get("postFold.model"), classifiers.get("postCall.model"),
				classifiers.get("postRaise.model"), classifiers.get("showdown0.model"), classifiers.get("showdown1.model"), classifiers.get("showdown2.model"),
				classifiers.get("showdown3.model"), classifiers.get("showdown4.model"), classifiers.get("showdown5.model"));
	}

	private final static Logger logger = Logger.getLogger(WekaRegressionModelFactory.class);

	public static WekaRegressionModelFactory createForDir(String models) throws IOException, ClassNotFoundException {
		Classifier preBetModel, preFoldModel, preCallModel, preRaiseModel, postBetModel, postFoldModel, postCallModel, postRaiseModel, showdown0Model, showdown1Model, showdown2Model, showdown3Model, showdown4Model, showdown5Model;
		ClassLoader classLoader = WekaRegressionModelFactory.class.getClassLoader();
		ObjectInputStream in = new ObjectInputStream(classLoader.getResourceAsStream(models + "preBet.model"));
		preBetModel = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "preFold.model"));
		preFoldModel = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "preCall.model"));
		preCallModel = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "preRaise.model"));
		preRaiseModel = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "postBet.model"));
		postBetModel = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "postFold.model"));
		postFoldModel = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "postCall.model"));
		postCallModel = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "postRaise.model"));
		postRaiseModel = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "showdown0.model"));
		showdown0Model = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "showdown1.model"));
		showdown1Model = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "showdown2.model"));
		showdown2Model = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "showdown3.model"));
		showdown3Model = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "showdown4.model"));
		showdown4Model = (Classifier) in.readObject();
		in.close();
		in = new ObjectInputStream(classLoader.getResourceAsStream(models + "showdown5.model"));
		showdown5Model = (Classifier) in.readObject();
		in.close();
		return new WekaRegressionModelFactory(preBetModel, preFoldModel, preCallModel, preRaiseModel, postBetModel, postFoldModel, postCallModel,
				postRaiseModel, showdown0Model, showdown1Model, showdown2Model, showdown3Model, showdown4Model, showdown5Model);
	}

	public WekaRegressionModelFactory(Classifier preBetModel, Classifier preFoldModel, Classifier preCallModel, Classifier preRaiseModel,
			Classifier postBetModel, Classifier postFoldModel, Classifier postCallModel, Classifier postRaiseModel, Classifier showdown0Model,
			Classifier showdown1Model, Classifier showdown2Model, Classifier showdown3Model, Classifier showdown4Model, Classifier showdown5Model) {
		this.preBetModel = preBetModel;
		this.preFoldModel = preFoldModel;
		this.preCallModel = preCallModel;
		this.preRaiseModel = preRaiseModel;
		this.postBetModel = postBetModel;
		this.postFoldModel = postFoldModel;
		this.postCallModel = postCallModel;
		this.postRaiseModel = postRaiseModel;
		this.showdown0Model = showdown0Model;
		this.showdown1Model = showdown1Model;
		this.showdown2Model = showdown2Model;
		this.showdown3Model = showdown3Model;
		this.showdown4Model = showdown4Model;
		this.showdown5Model = showdown5Model;
	}

	private final Classifier preBetModel, preFoldModel, preCallModel, preRaiseModel, postBetModel, postFoldModel, postCallModel, postRaiseModel,
			showdown0Model, showdown1Model, showdown2Model, showdown3Model, showdown4Model, showdown5Model;

	@Override
	public OpponentModel create() {
		return new WekaLearningOpponentModel(new WekaRegressionModel(preBetModel, preFoldModel, preCallModel, preRaiseModel, postBetModel, postFoldModel,
				postCallModel, postRaiseModel, showdown0Model, showdown1Model, showdown2Model, showdown3Model, showdown4Model, showdown5Model));
	}

	@Override
	public String toString() {
		return "WekaRegressionModel";
	}

}