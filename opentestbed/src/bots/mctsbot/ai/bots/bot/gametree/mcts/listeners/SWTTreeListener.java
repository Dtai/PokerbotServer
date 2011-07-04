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
package bots.mctsbot.ai.bots.bot.gametree.mcts.listeners;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import bots.mctsbot.ai.bots.bot.gametree.action.ProbabilityAction;
import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.INode;
import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.InnerNode;
import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.MCTSShowdownRollOutNode;
import bots.mctsbot.ai.bots.bot.gametree.mcts.nodes.RootNode;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.common.elements.player.PlayerId;
import bots.mctsbot.common.elements.table.Round;
import bots.mctsbot.common.util.Util;

import com.google.common.collect.ImmutableList;

public class SWTTreeListener implements MCTSListener {

	private final static Logger logger = Logger.getLogger(SWTTreeListener.class);
	private final Display display;
	private final Tree tree;
	private final int relStackSize;
	private final PlayerId botId;
	private final Round startRound;

	public SWTTreeListener(Display display, Shell shell, final Tree tree, final GameState gameState, PlayerId botId) {
		this.display = display;
		this.tree = tree;
		this.relStackSize = gameState.getPlayer(botId).getStack();
		this.botId = botId;
		this.startRound = gameState.getRound();
	}

	@Override
	public void onMCTS(RootNode node) {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				tree.removeAll();
			}
		});
		ImmutableList<INode> children = node.getChildren();
		for (INode child : children) {
			visitNode(child, null);
		}
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				tree.redraw();
			}
		});
	}

	private void visitNode(INode node, final TreeItem previous) {
		InnerNode parent = node.getParent();
		final Round round = parent == null ? startRound : parent.gameState.getRound();
		final ProbabilityAction action = node.getLastAction();
		final String actor = action.getAction().actor.equals(botId) ? "Bot" : "Player " + action.getAction().actor;
		final TreeItemHolder holder = new TreeItemHolder();
		final int nbSamples = node.getNbSamples();
		final double average = node.getEV();
		String stddev;
		try {
			stddev = "" + Util.parseDollars(node.getStdDev());
		} catch (UnsupportedOperationException e) {
			stddev = "?";
		}
		final String stddevf = stddev;
		String nbSamplesInMean;
		try {
			nbSamplesInMean = node.getNbSamplesInMean() + "";
		} catch (UnsupportedOperationException e) {
			nbSamplesInMean = "?";
		}
		final String nbSamplesInMeanf = nbSamplesInMean;
		final double evStadDev = node.getEVStdDev();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				TreeItem newItem = previous == null ? new TreeItem(tree, SWT.NONE) : new TreeItem(previous, SWT.NONE);
				holder.item = newItem;
				newItem.setText(new String[] { actor, action.getAction().toString(), round.getName(), Math.round(100 * action.getProbability()) + "%",
						"" + nbSamples, "" + Util.parseDollars(average - relStackSize), stddevf, nbSamplesInMeanf, "" + Util.parseDollars(evStadDev), });
				if (round == Round.FINAL) {
					newItem.setBackground(2, new Color(display, 30, 30, 255));
				} else if (round == Round.TURN) {
					newItem.setBackground(2, new Color(display, 100, 100, 255));
					;
				} else if (round == Round.FLOP) {
					newItem.setBackground(2, new Color(display, 170, 170, 255));
				} else if (round == Round.PREFLOP) {
					newItem.setBackground(2, new Color(display, 240, 240, 255));
				}
			}
		});
		if (node instanceof InnerNode) {
			ImmutableList<INode> children = ((InnerNode) node).getChildren();
			if (children != null) {
				for (INode node2 : children) {
					visitNode(node2, holder.item);
				}
			}
		} else if (node instanceof MCTSShowdownRollOutNode && node.getNbSamples() > 0) {
			MCTSShowdownRollOutNode snode = (MCTSShowdownRollOutNode) node;
			final int min = snode.stackSize;
			int potsize = snode.rollout.gamePotSize;
			final int max = min + potsize;
			final double winPercentage = (snode.getEV() - min) / potsize;
			final double losePercentage = 1 - winPercentage;
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					TreeItem newItem = new TreeItem(holder.item, SWT.NONE);
					newItem.setText(new String[] { "", "Win", "", Math.round(100 * winPercentage) + "%", "", "" + Util.parseDollars(max - relStackSize), "",
							"", "" });
					newItem.setBackground(1, new Color(display, 0, 255, 0));
					newItem = new TreeItem(holder.item, SWT.NONE);
					newItem.setText(new String[] { "", "Lose", "", Math.round(100 * losePercentage) + "%", "", "" + Util.parseDollars(min - relStackSize), "",
							"", "" });
					newItem.setBackground(1, new Color(display, 255, 0, 0));
				}
			});
		}
	}

	private static class TreeItemHolder {

		public volatile TreeItem item;

	}

	public static class Factory implements MCTSListener.Factory {

		private final Display display;
		private Shell shell;
		private Tree tree;

		public Factory(final Display display) {
			this.display = display;
			display.syncExec(new Runnable() {

				public void run() {
					shell = new Shell(display);
					shell.addShellListener(new ShellAdapter() {
						@Override
						public void shellClosed(ShellEvent e) {
							e.doit = false;
						}
					});
					shell.setSize(600, 400);
					shell.setMinimumSize(500, 400);
					shell.setLayout(new FillLayout());
					shell.setText("Game Tree Browser");
					tree = new Tree(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
					tree.setHeaderVisible(true);

					TreeColumn column = new TreeColumn(tree, SWT.LEFT);
					column.setText("Actor");
					column.setWidth(210);

					column = new TreeColumn(tree, SWT.CENTER);
					column.setText("Action");
					column.setWidth(140);

					column = new TreeColumn(tree, SWT.CENTER);
					column.setText("Round");
					column.setWidth(70);

					column = new TreeColumn(tree, SWT.CENTER);
					column.setText("P(Action)");
					column.setWidth(70);

					column = new TreeColumn(tree, SWT.CENTER);
					column.setText("Samples");
					column.setWidth(70);

					column = new TreeColumn(tree, SWT.CENTER);
					column.setText("Value");
					column.setWidth(80);

					column = new TreeColumn(tree, SWT.CENTER);
					column.setText("StdDev");
					column.setWidth(80);

					column = new TreeColumn(tree, SWT.CENTER);
					column.setText("NbSamplesInMean");
					column.setWidth(100);

					column = new TreeColumn(tree, SWT.CENTER);
					column.setText("MeanStdDev");
					column.setWidth(80);

					shell.pack();
					shell.open();
				}

			});
		}

		@Override
		public SWTTreeListener create(GameState gameState, PlayerId actor) {
			SWTTreeListener visitor = new SWTTreeListener(display, shell, tree, gameState, actor);
			return visitor;
		}

	}

}
