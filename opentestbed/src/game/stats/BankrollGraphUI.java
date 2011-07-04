package game.stats;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import util.Utils;

/**
 * This BankrollObserver opens a window while the game is running, showing the current
 * progress. A bankroll-chart is updated every 10 seconds.<br>
 * <br>
 * The window closes itself on the end of the game.<br>
 * 
 * Register this class on a GameRunner and call {@link #createGraph()} in the end
 * to open a window with the final result
 */
public class BankrollGraphUI implements BankrollObserver {

	// access should be synchronized, as this variable is filled from 
	// from the running game and regularly read by the GUI drawing the Bankroll
	private Map<Integer, List<Map<String, Double>>> stats = new HashMap<Integer, List<Map<String, Double>>>();
	private Set<String> playerNames;
	private int numSeatPermuations;
	private int numGames;
	private long totalGames;

	private volatile int currentGamesPlayed;
	private volatile int currentSeatPermutation;
	private long gameStartedTime;
	private JFrame progressFrame;
	private JProgressBar topProgressBar;
	private Timer guiUpdateTimer;
	private ChartPanel chartPanel = new ChartPanel(null);

	@Override
	public synchronized void gameStarted(int numSeatPermutations, int numGames, Set<String> playerNames) {
		this.numGames = numGames;
		this.numSeatPermuations = numSeatPermutations;
		this.playerNames = playerNames;
		this.totalGames = numGames * numSeatPermutations;

		for (int i = 0; i < numSeatPermutations; i++) {
			stats.put(Integer.valueOf(i), new ArrayList<Map<String, Double>>());
		}

		createProgressFrame();
		setupGuiUpdateTime();
		this.progressFrame.setVisible(true);

	}

	private void setupGuiUpdateTime() {
		guiUpdateTimer = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int snapshotCurrentGamesPlayed = currentGamesPlayed;
				long currentTime = System.currentTimeMillis();
				long progressTime = currentTime - gameStartedTime;
				long msPerGame = snapshotCurrentGamesPlayed > 0 ? (progressTime / snapshotCurrentGamesPlayed) : 0;
				double gamesPerSecond = (1000D / msPerGame);
				Date expectedEnd = new Date(System.currentTimeMillis() + (totalGames - snapshotCurrentGamesPlayed) * msPerGame);
				long progress = snapshotCurrentGamesPlayed * 100 / (BankrollGraphUI.this.numGames * BankrollGraphUI.this.numSeatPermuations);
				topProgressBar.setValue(snapshotCurrentGamesPlayed);
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
				topProgressBar.setString("Completed: " + currentGamesPlayed + "/" + totalGames + " games (" + progress + "%), Speed: "
						+ Utils.roundToCents(gamesPerSecond) + "games/s, Expected Finish: " + dateFormat.format(expectedEnd));
				final JFreeChart chart = calcBankRollsAndCreateJFreeChart(snapshotCurrentGamesPlayed);
				chartPanel.setChart(chart);

			}
		});
		guiUpdateTimer.start();
	}

	/**
	 * setups the GUI.
	 * On the top a progressbar and in the middle a big area for the bankroll chart
	 */
	private void createProgressFrame() {
		this.progressFrame = new JFrame("GameProgress");
		//		this.progressFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		//		Toolkit tk = Toolkit.getDefaultToolkit();
		//		int xSize = ((int) tk.getScreenSize().getWidth());
		//		int ySize = ((int) tk.getScreenSize().getHeight());
		//		this.progressFrame.setSize(xSize, ySize);
		this.progressFrame.setSize(1200, 800);
		this.progressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.topProgressBar = new JProgressBar(0, numGames * numSeatPermuations);
		this.topProgressBar.setStringPainted(true);
		this.progressFrame.add(topProgressBar, BorderLayout.NORTH);
		this.progressFrame.add(chartPanel, BorderLayout.CENTER);
		this.gameStartedTime = System.currentTimeMillis();
	};

	@Override
	public synchronized void updateBankroll(int seatpermutation, Map<String, Double> playerDelta) {
		currentGamesPlayed++;
		currentSeatPermutation = seatpermutation;
		if (currentGamesPlayed == totalGames) {
			guiUpdateTimer.stop();
			progressFrame.dispose();
		}

		List<Map<String, Double>> permutationStats = stats.get(Integer.valueOf(seatpermutation));
		permutationStats.add(playerDelta);
	}

	/**
	 * creates the graph, saves it to the data-directory and popups a frame to show the results.<br>
	 * As JFreeChart is used for rendering, one can zoom and do some other
	 * nice things.
	 */
	public void createGraph(String chartName) {
		final JFreeChart chart = calcBankRollsAndCreateJFreeChart(currentGamesPlayed);

		try {
			ChartUtilities.saveChartAsPNG(new File("./data/" + chartName + "-chart.png"), chart, 1000, 700);
		} catch (IOException e) {
			e.printStackTrace();
		}
		openChartInFrame(chart);
	}

	private JFreeChart calcBankRollsAndCreateJFreeChart(int snapshotCurrentGamesPlayed) {
		Map<String, Double> playerToBankRoll = new HashMap<String, Double>();
		Map<String, XYSeries> playerToXYSeries = new HashMap<String, XYSeries>();

		for (String playerName : playerNames) {
			playerToBankRoll.put(playerName, new Double(0));
			playerToXYSeries.put(playerName, new XYSeries(playerName));
		}

		calculateBankrolls(playerToBankRoll, playerToXYSeries, snapshotCurrentGamesPlayed);

		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		for (XYSeries playerXYSeries : playerToXYSeries.values()) {
			xySeriesCollection.addSeries(playerXYSeries);
		}

		final JFreeChart chart = createJFreeChart(playerToBankRoll, xySeriesCollection, snapshotCurrentGamesPlayed);
		return chart;
	}

	/**
	 * opens the chart in a frame
	 * @param chart
	 */
	private void openChartInFrame(final JFreeChart chart) {
		final ChartPanel chartPanel = new ChartPanel(chart);
		//chartPanel.setPreferredSize(new java.awt.Dimension(1000, 500));

		JFrame frame = new JFrame("SimulationResults");
		//frame.setSize(1000, 700);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(chartPanel);
		frame.setVisible(true);
	}

	/**
	 * creates a JFreeChart from all the data
	 * @param playerToBankRoll
	 * @param xySeriesCollection
	 * @return
	 */
	private JFreeChart createJFreeChart(Map<String, Double> playerToBankRoll, XYSeriesCollection xySeriesCollection, int snapshotCurrentGamesPlayed) {
		final JFreeChart chart = ChartFactory.createXYLineChart("Bankroll after " + (currentSeatPermutation + 1) + " seat permutation(s)", "Games", "Bankroll",
				xySeriesCollection, PlotOrientation.VERTICAL, true, false, false);
		chart.setBackgroundPaint(Color.WHITE);
		chart.getXYPlot().setBackgroundPaint(Color.WHITE);
		XYItemRenderer xyir = chart.getXYPlot().getRenderer();//.get.setOutlineStroke()
		try {
			xyir.setBaseStroke(new BasicStroke(3));
			// bug workaround
			((AbstractRenderer) xyir).setAutoPopulateSeriesStroke(false);

			//			xyir.setSeriesStroke(new BasicStroke(5));
			//			xyir.setSeriesStroke(0, ); //series line style
		} catch (Exception e) {
			System.err.println("Error setting style: " + e);
		}

		// create some Pointers to the final bankrolls
		for (String playerName : playerNames) {
			double finalBankroll = playerToBankRoll.get(playerName);
			DecimalFormat moneyFormat = new DecimalFormat("0.00");
			String resultString = playerName + ": $" + moneyFormat.format(finalBankroll) + " ($"
					+ moneyFormat.format(finalBankroll / (snapshotCurrentGamesPlayed / 100D)) + "/100)";
			final XYPointerAnnotation pointer = new XYPointerAnnotation(resultString, Math.min(snapshotCurrentGamesPlayed, numGames), finalBankroll,
					Math.PI * 5.9 / 6);
			pointer.setBaseRadius(130.0);
			pointer.setTipRadius(1.0);
			pointer.setLabelOffset(10.0);
			pointer.setOutlineVisible(true);
			pointer.setBackgroundPaint(Color.WHITE);
			chart.getXYPlot().addAnnotation(pointer);

		}

		// after the first permutation the next permutations get
		// merges with the existing data. We show a marker, what
		// data is already merged
		final Marker permutationEnd = new ValueMarker(snapshotCurrentGamesPlayed % numGames);
		permutationEnd.setLabel((currentSeatPermutation + 1) + " permutation(s)");
		permutationEnd.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		permutationEnd.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
		chart.getXYPlot().addDomainMarker(permutationEnd);

		return chart;
	}

	/**
	 * calculates the bankroll for each player (aggregating the results of all
	 * seatpermutations)
	 * @param playerToBankRoll filled with the final bankroll for each player
	 * @param playerToXYSeries filled with a XYSeries with the bankroll for each player
	 */
	private synchronized void calculateBankrolls(Map<String, Double> playerToBankRoll, Map<String, XYSeries> playerToXYSeries, int snapshotCurrentGamesPlayed) {
		for (int game = 0; game < Math.min(numGames, snapshotCurrentGamesPlayed); game++) {
			for (String playerName : playerNames) {
				double playerBankRoll = playerToBankRoll.get(playerName);

				for (int seatpermutation = 0; seatpermutation < numSeatPermuations; seatpermutation++) {
					List<Map<String, Double>> permutationStats = stats.get(seatpermutation);
					if (permutationStats.size() > game) {
						playerBankRoll += permutationStats.get(game).get(playerName);
					}
				}
				playerToBankRoll.put(playerName, Double.valueOf(playerBankRoll));
				playerToXYSeries.get(playerName).add(game + 1, playerBankRoll);
			}
		}
	}

}
