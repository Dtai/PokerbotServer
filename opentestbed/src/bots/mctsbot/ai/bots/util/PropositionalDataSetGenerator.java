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

package bots.mctsbot.ai.bots.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import weka.core.Instance;
import bots.mctsbot.ai.opponentmodels.weka.PlayerData;
import bots.mctsbot.ai.opponentmodels.weka.Propositionalizer;
import bots.mctsbot.ai.opponentmodels.weka.instances.EmptyInstance;
import bots.mctsbot.ai.opponentmodels.weka.instances.InstancesBuilder;
import bots.mctsbot.ai.opponentmodels.weka.instances.PostCheckBetInstances;
import bots.mctsbot.ai.opponentmodels.weka.instances.PostFoldCallRaiseInstances;
import bots.mctsbot.ai.opponentmodels.weka.instances.PreCheckBetInstances;
import bots.mctsbot.ai.opponentmodels.weka.instances.PreFoldCallRaiseInstances;
import bots.mctsbot.ai.opponentmodels.weka.instances.ShowdownInstances;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;

public class PropositionalDataSetGenerator extends Propositionalizer {

	private static final String nl = InstancesBuilder.nl;

	protected static final String hole = "*** HOLE";
	protected static final String flop = "*** FLOP";
	protected static final String turn = "*** TURN";
	protected static final String river = "*** RIVER";

	// if you want all files in 'unzipped' then make folder empty string
	protected static final String folder = "FTP_NLH50";

	protected final Writer preCheckBetFile;
	protected final Writer postCheckBetFile;
	protected final Writer preFoldCallRaiseFile;
	protected final Writer postFoldCallRaiseFile;
	protected final Writer showdownFile;

	protected final Writer preBetSizeFile;
	protected final Writer postBetSizeFile;
	protected final Writer betSizeFile;

	private boolean forgetCurrentGame = false;

	private int bb;

	private final PreCheckBetInstances preCheckBetInstance;
	private final PostCheckBetInstances postCheckBetInstance;
	private final PreFoldCallRaiseInstances preFoldCallRaiseInstance;
	private final PostFoldCallRaiseInstances postFoldCallRaiseInstance;
	private final ShowdownInstances showdownInstance;

	private final EmptyInstance preBetSizeInstance;
	private final EmptyInstance postBetSizeInstance;
	private final EmptyInstance betSizeInstance;

	public PropositionalDataSetGenerator() throws IOException {
		String tmpFolder = folder + (folder.equals("") ? "" : "/");
		preCheckBetFile = new OutputStreamWriter(new FileOutputStream("output/" + tmpFolder + "PreCheckBet.arff"));
		postCheckBetFile = new OutputStreamWriter(new FileOutputStream("output/" + tmpFolder + "PostCheckBet.arff"));
		preFoldCallRaiseFile = new OutputStreamWriter(new FileOutputStream("output/" + tmpFolder + "PreFoldCallRaise.arff"));
		postFoldCallRaiseFile = new OutputStreamWriter(new FileOutputStream("output/" + tmpFolder + "PostFoldCallRaise.arff"));
		showdownFile = new OutputStreamWriter(new FileOutputStream("output/" + tmpFolder + "Showdown.arff"));

		preBetSizeFile = new OutputStreamWriter(new FileOutputStream("output/" + tmpFolder + "PreBetSize.arff"));
		postBetSizeFile = new OutputStreamWriter(new FileOutputStream("output/" + tmpFolder + "PostBetSize.arff"));
		betSizeFile = new OutputStreamWriter(new FileOutputStream("output/" + tmpFolder + "BetSize.arff"));

		this.preCheckBetInstance = new PreCheckBetInstances("PreCheckBet", "@attribute betProb real" + nl + "@attribute action {check, bet}" + nl);
		preCheckBetFile.write(preCheckBetInstance.toString());

		this.postCheckBetInstance = new PostCheckBetInstances("PostCheckBet", "@attribute betProb real" + nl + "@attribute action {check, bet}" + nl);
		postCheckBetFile.write(postCheckBetInstance.toString());

		this.preFoldCallRaiseInstance = new PreFoldCallRaiseInstances("PreFoldCallRaise", "@attribute foldProb real" + nl + "@attribute callProb real" + nl
				+ "@attribute raiseProb real" + nl + "@attribute action {fold,call,raise}" + nl);
		preFoldCallRaiseFile.write(preFoldCallRaiseInstance.toString());

		this.postFoldCallRaiseInstance = new PostFoldCallRaiseInstances("PostFoldCallRaise", "@attribute foldProb real" + nl + "@attribute callProb real" + nl
				+ "@attribute raiseProb real" + nl + "@attribute action {fold,call,raise}" + nl);
		postFoldCallRaiseFile.write(postFoldCallRaiseInstance.toString());

		this.showdownInstance = new ShowdownInstances("Showdown", "@attribute part0Prob real" + nl + "@attribute part1Prob real" + nl
				+ "@attribute part2Prob real" + nl + "@attribute part3Prob real" + nl + "@attribute part4Prob real" + nl + "@attribute part5Prob real" + nl
				+ "@attribute avgPartition {0,1,2,3,4,5}" + nl);
		showdownFile.write(showdownInstance.toString());

		this.preBetSizeInstance = new EmptyInstance("PreBetSize", "@attribute minRaise real" + nl + "@attribute maxRaise real" + nl
				+ "@attribute relBetSize real" + nl + "@attribute blindRelBetSize real" + nl);
		preBetSizeFile.write(preBetSizeInstance.toString());

		this.postBetSizeInstance = new EmptyInstance("PostBetSize", "@attribute minRaise real" + nl + "@attribute maxRaise real" + nl
				+ "@attribute relBetSize real" + nl + "@attribute blindRelBetSize real" + nl);
		postBetSizeFile.write(postBetSizeInstance.toString());

		this.betSizeInstance = new EmptyInstance("BetSize",
		//				"@attribute minRaise real"+nl+
		//				"@attribute maxRaise real"+nl+ 
				"@attribute relBetSize real" + nl
		//				"@attribute blindRelBetSize real"+nl
		);
		betSizeFile.write(betSizeInstance.toString());

	}

	private void close() throws IOException {
		preCheckBetFile.close();
		postCheckBetFile.close();
		preFoldCallRaiseFile.close();
		postFoldCallRaiseFile.close();
		showdownFile.close();
		preBetSizeFile.close();
		postBetSizeFile.close();
		betSizeFile.close();
	}

	private void write(Writer writer, Instance instance) {
		try {
			writer.write(instance.toString() + nl);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	protected void logFold(Object actorId) {
		//		if(getRound().equals("preflop")){
		//			write(preFoldCallRaiseFile, 
		//					preFoldCallRaiseInstance.getClassifiedInstance(this, actorId, new Object[]{1,0,0,"fold"}));
		//		}else{
		//			write(postFoldCallRaiseFile, 
		//					postFoldCallRaiseInstance.getClassifiedInstance(this, actorId, new Object[]{1,0,0,"fold"}));
		//		}

	}

	protected void logCall(Object actorId) {
		//		if(getRound().equals("preflop")){
		//			write(preFoldCallRaiseFile, 
		//					preFoldCallRaiseInstance.getClassifiedInstance(this, actorId, new Object[]{0,1,0,"call"}));
		//		}else{
		//			write(postFoldCallRaiseFile, 
		//					postFoldCallRaiseInstance.getClassifiedInstance(this, actorId, new Object[]{0,1,0,"call"}));
		//		}	
	}

	protected void logRaise(Object actorId, double raiseAmount) {
		//		if(getRound().equals("preflop")){
		//			write(preFoldCallRaiseFile, 
		//					preFoldCallRaiseInstance.getClassifiedInstance(this, actorId, new Object[]{0,0,1,"raise"}));
		//		}else{
		//			write(postFoldCallRaiseFile, 
		//					postFoldCallRaiseInstance.getClassifiedInstance(this, actorId, new Object[]{0,0,1,"raise"}));
		//		}
		logRaiseAmount(actorId, raiseAmount);
	}

	protected void logCheck(Object actorId) {
		//		if(getRound().equals("preflop")){
		//			write(preCheckBetFile, 
		//					preCheckBetInstance.getClassifiedInstance(this, actorId, new Object[]{0,"check"}));
		//		}else{
		//			write(postCheckBetFile, 
		//					postCheckBetInstance.getClassifiedInstance(this, actorId, new Object[]{0,"check"}));
		//		}
	}

	protected void logBet(Object actorId, double raiseAmount) {
		//		if(getRound().equals("preflop")){
		//			write(preCheckBetFile, 
		//					preCheckBetInstance.getClassifiedInstance(this, actorId, new Object[]{1,"bet"}));
		//		}else{
		//			write(postCheckBetFile, 
		//					postCheckBetInstance.getClassifiedInstance(this, actorId, new Object[]{1,"bet"}));
		//		}	
		logRaiseAmount(actorId, raiseAmount);
	}

	@Override
	protected void logShowdown(Object actorId, double[] partitionDistr) {
		//		Object[] targets = new Object[partitionDistr.length+1];
		//		double avgBucket = 0;
		//		for(int i=0;i<partitionDistr.length;i++){
		//			targets[i]=partitionDistr[i];
		//			avgBucket += i*partitionDistr[i];
		//		}
		//		targets[partitionDistr.length] = (int)Math.round(avgBucket);
		//		write(showdownFile, 
		//				showdownInstance.getClassifiedInstance(this, actorId, targets));
	}

	private void writeRaise(Object actorId, double minRaise, double maxRaise, double blindRelRaiseAmount, double relRaiseAmount) {
		if (getRound().equals("preflop")) {
			write(preBetSizeFile,
					preBetSizeInstance.getClassifiedInstance(this, actorId, new Object[] { minRaise, maxRaise, relRaiseAmount, blindRelRaiseAmount }));
		} else {
			write(postBetSizeFile,
					postBetSizeInstance.getClassifiedInstance(this, actorId, new Object[] { minRaise, maxRaise, relRaiseAmount, blindRelRaiseAmount }));
		}
		write(betSizeFile, betSizeInstance.getClassifiedInstance(this, actorId,
		//					new Object[]{minRaise, maxRaise, relRaiseAmount, blindRelRaiseAmount}));
				new Object[] { relRaiseAmount }));
	}

	private void logRaiseAmount(Object actorId, double raiseAmount) {
		PlayerData p = this.getPlayers().get(actorId);
		double bb = p.getBB();
		double relAmount = raiseAmount;
		raiseAmount = raiseAmount * bb;
		double minRaise = (double) getMinRaise(p);
		double maxRaise = (double) getMaxRaise(p);
		raiseAmount = Math.min(raiseAmount, maxRaise);
		//		float logBetSize = (float)Math.log(raiseAmount);
		if (Math.abs(minRaise - maxRaise) > 0.6) //only when we have a choice
		{
			if (Math.abs(minRaise - raiseAmount) < 0.6) {
				//				betSizeInstance(p, minRaise, maxRaise, raiseAmount+","+logBetSize+",0,1,0,0,minBet", betSizeClass);
				writeRaise(actorId, minRaise, maxRaise, 0, 0);
			} else if (Math.abs(maxRaise - raiseAmount) < 0.6) {
				//				betSizeInstance(p, minRaise, maxRaise, raiseAmount+","+logBetSize+",1,0,0,1,allin", betSizeClass);
				writeRaise(actorId, minRaise, maxRaise, Double.NaN, 1);
			} else {
				if (raiseAmount < minRaise || minRaise > maxRaise) {
					System.out.println("Skipping illegal bet");
					return;
				}
				double relBetSize = (raiseAmount - minRaise) / (maxRaise - minRaise);
				//				betSizeInstance(p, minRaise, maxRaise, raiseAmount+","+logBetSize+","+relBetSize+",0,1,0,avg", betSizeClass);
				writeRaise(actorId, minRaise, maxRaise, relAmount, relBetSize);
				//				betSizeInstance(p, minRaise, maxRaise, raiseAmount+","+logBetSize+","+relBetSize+","+(float)Math.log(relBetSize), betSize);
			}
		}
	}

	public void run() throws Exception {
		try {
			int nbFiles = 0;
			final int maxNbFiles = 100;
			String line;
			File dir1 = new File("../../../data/unzipped");
			String[] children1 = dir1.list();
			if (children1 == null) {
				// Either dir does not exist or is not a directory
			} else {
				for1: for (String element : children1) {
					File child1 = new File(dir1, element);
					String[] children2 = child1.list();
					if (children2 == null) {
						// Either dir does not exist or is not a directory
					} else {
						for (String element2 : children2) {
							if (element.equals(folder) || folder.equals("")) {
								// Get filename of file or directory
								System.out.println("Starting file #" + (nbFiles + 1) + "/" + maxNbFiles + ": " + element + "/" + element2);
								BufferedReader r = new BufferedReader(new FileReader(new File(child1, element2)));
								while ((line = r.readLine()) != null) {
									try {
										doLine(line);
									} catch (Exception e) {
										System.out.println(line);
										throw e;
									}
								}
								r.close();
								nbFiles++;
								if (nbFiles >= maxNbFiles)
									break for1;
							}
						}
					}
				}
			}
		} finally {
			close();
		}
	}

	private void doLine(String line) throws IOException {
		// inputRaise.write(line+"\n");
		// foldFile.write(line+"\n");
		//		System.out.println(line);
		if (line.startsWith("Full Tilt Poker Game ")) {
			//			if(line.startsWith("Full Tilt Poker Game #7148395139")){
			//				System.out.println("Found needle");
			//			}
			int temp = line.indexOf("/");
			this.bb = parseAmount(line.substring(temp + 2, line.indexOf(" ", temp + 2)));
			forgetCurrentGame = false;
			signalNewGame();
			signalBBAmount(bb);
		} else if (!forgetCurrentGame) {
			if (line.startsWith("Seat ")) {
				if (line.endsWith("(0)")) {
					forgetCurrentGame = true;
				} else {
					int startName = line.indexOf(":") + 2;
					int startDollar = line.indexOf("(", startName);
					int stack = parseAmount(line.substring(startDollar + 2, line.indexOf(")", startDollar)));
					String name = line.substring(startName, startDollar - 1);
					signalSeatedPlayer(stack, name);
				}
			} else if (line.startsWith("*** ")) {
				if (line.startsWith("*** SUMMARY ***")) {
					forgetCurrentGame = true;
					signalShowdown();
				} else {
					if (line.startsWith(hole)) {
						signalCommunityCards(new Hand());
					}
					if (line.startsWith(flop)) {
						signalFlop();
						String[] cardsString = line.substring(line.indexOf("[")).replaceAll("\\[", "").replaceAll("\\]", "").split(" ");
						Hand cardSet = new Hand();
						cardSet.addCard(new Card(cardsString[0]));
						cardSet.addCard(new Card(cardsString[1]));
						cardSet.addCard(new Card(cardsString[2]));
						signalCommunityCards(cardSet);
					} else if (line.startsWith(turn)) {
						signalTurn();
						String[] cardsString = line.substring(line.indexOf("[")).replaceAll("\\[", "").replaceAll("\\]", "").split(" ");
						Hand cardSet = new Hand();
						cardSet.addCard(new Card(cardsString[0]));
						cardSet.addCard(new Card(cardsString[1]));
						cardSet.addCard(new Card(cardsString[2]));
						cardSet.addCard(new Card(cardsString[3]));

						signalCommunityCards(cardSet);
					} else if (line.startsWith(river)) {
						signalRiver();
						String[] cardsString = line.substring(line.indexOf("[")).replaceAll("\\[", "").replaceAll("\\]", "").split(" ");
						Hand cardSet = new Hand();
						cardSet.addCard(new Card(cardsString[0]));
						cardSet.addCard(new Card(cardsString[1]));
						cardSet.addCard(new Card(cardsString[2]));
						cardSet.addCard(new Card(cardsString[3]));
						cardSet.addCard(new Card(cardsString[4]));
						signalCommunityCards(cardSet);
					}
				}
			} else if (line.contains(":")) {
				// ignore chat message
			} else {
				boolean isAllIn = line.contains("all in");
				if (line.contains(" posts the small blind")) {
					String id = line.substring(0, line.indexOf(" posts the small blind"));

					signalBlind(isAllIn, id, bb / 2);
				} else if (line.contains(" posts the big blind")) {
					String id = line.substring(0, line.indexOf(" posts the big blind"));
					signalBlind(isAllIn, id, bb);
				} else if (line.endsWith(" folds")) {
					String id = line.substring(0, line.indexOf(" folds"));
					signalFold(id);
				} else if (line.contains(" calls")) {
					int allinIndex = line.lastIndexOf(", and is all in");
					if (allinIndex <= 0) {
						allinIndex = line.length();
					}
					String id = line.substring(0, line.indexOf(" calls"));
					int movedAmount = parseAmount(line.substring(line.indexOf("$") + 1, allinIndex));
					signalCall(isAllIn, id, movedAmount);
				} else if (line.contains(" raises to")) {
					int allinIndex = line.lastIndexOf(", and is all in");
					if (allinIndex <= 0) {
						allinIndex = line.length();
					}
					int maxBetParsed = parseAmount(line.substring(line.indexOf("$") + 1, allinIndex));
					String id = line.substring(0, line.indexOf(" raises to"));
					signalRaise(isAllIn, id, maxBetParsed);
				} else if (line.endsWith(" checks")) {
					String id = line.substring(0, line.indexOf(" checks"));
					signalCheck(id);
				} else if (line.contains(" bets ") && !line.contains("bo bets all")) {
					String id = line.substring(0, line.indexOf(" bets"));
					int allinIndex = line.lastIndexOf(", and is all in");
					if (allinIndex <= 0) {
						allinIndex = line.length();
					}
					int maxBetParsed = parseAmount(line.substring(line.indexOf("$") + 1, allinIndex));
					//cannot be bet by big blind, is raise in dataset
					signalBet(isAllIn, id, maxBetParsed);
				} else if (line.contains(" shows [")) {
					int showsIndex = line.indexOf(" shows [");
					String id = line.substring(0, showsIndex);

					int start = showsIndex + 8;
					String[] cardStrings = line.substring(start, line.indexOf("]", start)).split(" ");
					signalCardShowdown(id, new Card(cardStrings[0]), new Card(cardStrings[1]));
				}
			}
		}
	}

	private int parseAmount(String stringAmount) {
		return (int) Math.round(100 * Double.parseDouble(stringAmount.replaceAll(",", "")));
	}
}
