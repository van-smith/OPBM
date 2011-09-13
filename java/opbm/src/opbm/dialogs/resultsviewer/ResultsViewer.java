/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class of the OPBM.  It creates a GUI, loads
 * necessary files, beings processing based on context, etc.
 *
 * Last Updated:  Sep 12, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.1.0
 *
 */

package opbm.dialogs.resultsviewer;

import java.awt.event.AdjustmentEvent;
import java.awt.event.WindowEvent;
import opbm.graphics.JLabelHotTrack;
import opbm.dialogs.DroppableFrame;
import opbm.graphics.AlphaImage;
import opbm.common.Tuple;
import opbm.common.Utils;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollBar;
import opbm.Opbm;
import opbm.dialogs.OpbmDialog;
import opbm.common.Xml;
import opbm.graphics.AnimateImageTask;

public final class ResultsViewer
							implements KeyListener,
									   MouseListener,
									   MouseWheelListener,
									   ComponentListener,
									   WindowListener,
									   AdjustmentListener

{
	public ResultsViewer(Opbm		opbm,
						 boolean	visible)
	{
		m_opbm					= opbm;
		m_rootRVL				= null;
		m_filterTags			= new Tuple(opbm);
		m_visible				= visible;
		m_rvlList				= new ArrayList<ResultsViewerLine>(0);
		m_renderList			= new ArrayList<ResultsViewerLine>(0);
	}

	/**
	 * Loads the specified filename and creates the physical Results Viewer canvas / screen /image
	 * @param filename
	 * @return
	 */
	public boolean load(String filename)
	{
		// Build the physical viewer
		createResultsViewer(m_visible);

		// Load the specified xml
		Xml results = Opbm.loadXml(filename, m_opbm);
		if (results != null)
		{	// No error, proceed like normal
			populate(results);

			// Setup initial state of everything
			computeInitialTimesAndScores();

			// Begin at the top
			m_renderListTop = 0;

			// Success
			return(true);

		} else {
			// Error, tell the user
			OpbmDialog od = new OpbmDialog(m_opbm, "There was an error processing the file<br>" + filename, "OPBM - Results Viewer Error", OpbmDialog._OKAY_BUTTON, "", "");
			return(false);
		}
	}

	/**
	 * Called once at startup, used to compute all of the initial times, scores,
	 * successes, failures, etc., that occur at each level, so when items are
	 * filtered and lesser results sets are viewed, the totals can be gathered.
	 */
	private void computeInitialTimesAndScores()
	{
		int i, level;

		// Levels are 0=summary, 1=suite, 2=scenario, 3=molecule, 4=atom, 5=worklet
		// Iterate backwards from worklet, to atom, to molecule, to scenario, to
		// suite, to summary, thereby ensuring that the child's computed values
		// are set at each level
		for (level = ResultsViewerLine._WORKLET;
			 level >= ResultsViewerLine._SUMMARY;
			 level--)
		{
			for (i = 0; i < m_rvlList.size(); i++)
			{	// Compute every entry at this level in our list
				if (m_rvlList.get(i).getLevel() == level)
				{	// Compute this one
					m_rvlList.get(i).computedChildScoresTimesEtc(false, null);
				}
			}
		}
	}

	/**
	 * Called to physically render the components.  They were created during
	 * the createResultsViewer() method, but are not physically updated / drawn-
	 * with-datauntil they are rendered.
	 */
	public void render()
	{
		renderScoreboard();
		renderBottom();
		if (m_splashTask != null)
		{	// Turn off splash-screen animation
			m_splashTask.stop();
			m_splashTask = null;
		}
	}

	public void renderScoreboard()
	{
		int switchX;
		double half, diff, height;
		String score, time, successes, failures;
		AlphaImage img;
		Rectangle rect;
		Font font;

		// 1) Render the scoreboard
		// 1a) Render the score at the top
		if (m_lblScoreboard == null)
		{
			m_lblScoreboard = new JLabel();
			m_lblScoreboard.setBounds(26, 74, 126, 159);
			m_lblScoreboard.setVisible(true);
			m_pan.add(m_lblScoreboard);
			m_pan.moveToFront(m_lblScoreboard);
		}
		img = new AlphaImage(m_imgScoreboardInternal);
		img.applyAlphaMask(m_imgScoreboardInternal1Mask);

		// 1b) Render the rainbow gauge
		height	= (double)img.getHeight() - 83.0;
		half = (height / 2.0);
		diff = (Utils.between(m_rootRVL.getComputedScore(), 0.0, 200.0) - 100.0) / 100.0;
		// We make the entire rainbow spectrum only show variations of 33 points
		switchX = (int)Utils.roundAwayFromZero(half + (half * diff));
		img.rainbowRectangle(1, 83, img.getWidth() - 2, img.getHeight() - 1, (int)height - switchX - 6);

		// Score
		font	= new Font("Calibri", Font.BOLD, 48);
		rect	= new Rectangle(0, 0, img.getWidth(), 20);
		score	= Integer.toString((int)m_rootRVL.getComputedScore());
		if (score.length() > 3)
			score = "999";
		img.drawStringInRectangle(rect, score, Color.WHITE, font, 255, true);

		if (m_failureCount != 0)
			img.recolorize(AlphaImage.makeARGB(255, 255, 0, 0));				// Make the background red

		// Mask off the image to fit in its display area, and display it
		img.applyAlphaMask(m_imgScoreboardInternal2Mask);
		m_lblScoreboard.setIcon(new ImageIcon(img.getBufferedImage()));

		// Update the time and score portions
		time		= Utils.removeLeadingZeroTimes(Utils.convertSecondsToHHMMSSff(m_rootRVL.getComputedTime()));
		successes	= Integer.toString(m_rootRVL.getComputedSuccesses());
		failures	= Integer.toString(m_rootRVL.getComputedFailures());

		m_lblScoreboardRunTime.setText(time);
		m_lblScoreboardRunResultsSuccesses.setText(successes);
		m_lblScoreboardRunResultsSuccesses.setToolTipText("There were " + successes + " successful " + Utils.singularOrPlural(m_rootRVL.getComputedSuccesses(), "test", "tests"));
		m_lblScoreboardRunResultsFailures.setText(failures);
		m_lblScoreboardRunResultsFailures.setToolTipText("There " + Utils.singularOrPlural(m_rootRVL.getComputedFailures(), "was", "were") + " " + failures + " " + Utils.singularOrPlural(m_rootRVL.getComputedFailures(), "failure", "failures"));

		m_lblRunName.setText(m_runName);
	}

	public void renderBottom()
	{
		int i;
		ResultsViewerLine rvl;

		// Build a list of everything that should be displayed in the bottom
		buildRenderList();

		// Go through everything
		for (i = 0; i < m_rvlList.size(); i++)
		{
			rvl = m_rvlList.get(i);
			if (rvl.isAboutToBeVisible())
			{	// It's one that's about to be made visible, so make it visible
				rvl.setVisible(true);
				rvl.render(m_displayMode);

			} else {
				if (rvl.isVisible())
				{	// It was previously shown, hide it
					rvl.setVisible(false);
					rvl.render(m_displayMode);
				}
			}
		}
		i = 5;
	}

	/**
	 * Convert the results Xml into a series of ResultsViewer lines and tabs
	 * for the various levels contained within the results run
	 */
	public void populate(Xml results)
	{
		int i;
		Xml item;
		ResultsViewerLine line, last, child, first;
		Xml result;
		List<Xml> list = new ArrayList<Xml>(0);

		// Initialize for processing
		m_rvlList.clear();
		result			= results.getChildNode("resultsdata").getChildNode("result");
		m_rootRVL		= new ResultsViewerLine(m_opbm, this, m_pan, 0, result);
		m_rvlList.add(m_rootRVL);

		// Grab all the top-level data items
		m_runName		= result.getAttributeOrChild("name");

		// Initialize all local variables
		last			= null;
		first			= null;
		m_failureCount	= 0;
		m_testCount		= 0;
		m_untestedCount	= 0;
		Xml.getNodeList(list, results, "opbm.resultsdata.result.suite", false);
		for (i = 0; i < list.size(); i++)
		{
			item = list.get(i);

			// Create the entry for this level
			line = new ResultsViewerLine(m_opbm, this, m_pan, 1, item);
			if (line.wasTested())
			{	// This line was tested
				++m_testCount;
				m_failureCount += line.wasSuccessful() ? 0 : 1;

			} else {
				++m_untestedCount;

			}

			// Append it to our master list
			m_rvlList.add(line);

			if (last != null)
				last.setNext(line);

			if (first == null)
				first = line;

			// Add its children
			child = populateScenarios(item, line);
			line.setChild(child);
			line.calculateRolledUpRunTabs();

			last = line;
		}
		m_rootRVL.setChild(first);
		m_rootRVL.calculateRolledUpRunTabs();
		recomputeScores();
	}

	public void recomputeScores()
	{
		recomputeScores(m_rootRVL);
	}

	public void recomputeScores(ResultsViewerLine rvl)
	{
		ResultsViewerLine thisOne;

		thisOne = rvl;
		while (thisOne != null)
		{
			if (thisOne.getChild() != null)
				recomputeScores(thisOne.getChild());

			// All children at this level are computed, sum at this level
			thisOne.computedChildScoresTimesEtc(areAnyFilterTagsPopulated(), m_filterTags);

			thisOne = thisOne.getNext();
		}
	}

	/**
	 * Load all the scenarios for the specified suite
	 */
	public ResultsViewerLine populateScenarios(Xml					suite,
											   ResultsViewerLine	parent)
	{
		int i;
		Xml item;
		ResultsViewerLine line, last, child, first;
		List<Xml> list = new ArrayList<Xml>(0);

		// Grab all the top-level data items
		first	= null;
		last	= null;
		Xml.getNodeList(list, suite.getChildNode("scenario"), "scenario", false);
		for (i = 0; i < list.size(); i++)
		{
			item = list.get(i);

			// Create the entry for this level
			line = new ResultsViewerLine(m_opbm, this, m_pan, 2, item);
			if (line.wasTested())
			{	// This line was tested
				++m_testCount;
				m_failureCount += line.wasSuccessful() ? 0 : 1;
			} else {
				++m_untestedCount;
			}

			// Append it to our master list
			m_rvlList.add(line);

			if (last != null)
				last.setNext(line);

			if (first == null)
				first = line;

			// Add its children
			child = populateMolecules(item, line);
			line.setChild(child);
			line.calculateRolledUpRunTabs();

			last = line;
		}
		return(first);
	}

	/**
	 * Load all the molecules for the specified scenario
	 */
	public ResultsViewerLine populateMolecules(Xml					scenario,
											   ResultsViewerLine	parent)
	{
		int i;
		Xml item;
		ResultsViewerLine line, last, child, first;
		List<Xml> list = new ArrayList<Xml>(0);

		// Grab all the top-level data items
		first	= null;
		last	= null;
		Xml.getNodeList(list, scenario.getChildNode("molecule"), "molecule", false);
		for (i = 0; i < list.size(); i++)
		{
			item = list.get(i);

			// Create the entry for this level
			line = new ResultsViewerLine(m_opbm, this, m_pan, 3, item);
			if (line.wasTested())
			{	// This line was tested
				++m_testCount;
				m_failureCount += line.wasSuccessful() ? 0 : 1;
			} else {
				++m_untestedCount;
			}

			// Append it to our master list
			m_rvlList.add(line);

			if (last != null)
				last.setNext(line);

			if (first == null)
				first = line;

			// Add its children
			child = populateAtoms(item, line);
			line.setChild(child);
			line.calculateRolledUpRunTabs();

			last = line;
		}
		return(first);
	}

	/**
	 * Load all the atoms for the specified molecule
	 */
	public ResultsViewerLine populateAtoms(Xml					molecule,
										   ResultsViewerLine	parent)
	{
		int i;
		Xml item;
		ResultsViewerLine line, last, child, first;
		List<Xml> list = new ArrayList<Xml>(0);

		// Grab all the top-level data items
		first	= null;
		last	= null;
		Xml.getNodeList(list, molecule.getChildNode("atom"), "atom", false);
		for (i = 0; i < list.size(); i++)
		{
			item = list.get(i);

			// Create the entry for this level
			line = new ResultsViewerLine(m_opbm, this, m_pan, 4, item);
			if (line.wasTested())
			{	// This line was tested
				++m_testCount;
				m_failureCount += line.wasSuccessful() ? 0 : 1;
			} else {
				++m_untestedCount;
			}

			// Append it to our master list
			m_rvlList.add(line);

			if (last != null)
				last.setNext(line);

			if (first == null)
				first = line;

			// Add its children
			child = populateWorklets(item, line);
			line.setChild(child);
			line.calculateRolledUpRunTabs();

			last = line;
		}
		return(first);
	}

	/**
	 * Load all the worklets for the specified atom
	 */
	public ResultsViewerLine populateWorklets(Xml					atom,
											  ResultsViewerLine		parent)
	{
		int i;
		Xml item;
		ResultsViewerLine line, last, first;
		List<Xml> list = new ArrayList<Xml>(0);

		// Grab all the top-level data items
		first	= null;
		last	= null;
		Xml.getNodeList(list, atom.getChildNode("worklet"), "worklet", false);
		for (i = 0; i < list.size(); i++)
		{
			item = list.get(i);

			// Create the entry for this level
			line = new ResultsViewerLine(m_opbm, this, m_pan, 5, item);
			if (line.wasTested())
			{	// This line was tested
				++m_testCount;
				m_failureCount += line.wasSuccessful() ? 0 : 1;
			} else {
				++m_untestedCount;
			}

			// Append it to our master list
			m_rvlList.add(line);

			if (last != null)
				last.setNext(line);

			if (first == null)
				first = line;

			// At the worklet level, we need to load individual run times
			line.loadRuns(item, item.getFirstChild());

			last = line;
		}
		return(first);
	}

	public boolean areAnyFilterTagsPopulated()
	{
		int i;

		for (i = 0; i < m_filterTags.size(); i++)
		{
			if (((String)m_filterTags.getSecond(i)).equalsIgnoreCase("Yes"))
				return(true);
		}
		// If we get here, none of them have been selected
		return(false);
	}

	public void createSystemData()
	{
		DroppableFrame frame;
		Dimension prefSize;
		JLayeredPane pan;
		JLabel lblBackground;
		int actual_width, actual_height, width, height;

		width = 640;
		height = 258;
		frame = new DroppableFrame(m_opbm, false, false);
		frame.setTitle("OPBM - System Data");

		// Compute the actual size we need for our window, so it's properly centered
		frame.pack();
		Insets fi		= frame.getInsets();
		actual_width	= width  + fi.left + fi.right;
		actual_height	= height + fi.top  + fi.bottom;
		frame.setSize(width  + fi.left + fi.right,
					  height + fi.top  + fi.bottom);

		prefSize = new Dimension(width  + fi.left + fi.right,
								 height + fi.top  + fi.bottom);
		frame.setMinimumSize(prefSize);
		frame.setPreferredSize(prefSize);

		prefSize = new Dimension(width  + fi.left + fi.right,
								 height + fi.top  + fi.bottom);
		frame.setMinimumSize(prefSize);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(width, height);
		frame.setLocationRelativeTo(null);  // Center window
		frame.setLayout(null);				// We handle all redraws

		Container c = frame.getContentPane();
		c.setBackground(new Color(120, 120, 120));
		c.setForeground(Color.WHITE);

		pan = new JLayeredPane();
		pan.setLayout(null);
		pan.setBounds(0, 0, width, height);
		pan.setVisible(true);
		pan.setBorder(BorderFactory.createEmptyBorder());
		c.add(pan);

		// Set the background image
		lblBackground = new JLabel();
		lblBackground.setBounds(0, 0, width, height);
		lblBackground.setHorizontalAlignment(JLabel.LEFT);
		lblBackground.setVerticalAlignment(JLabel.TOP);
		lblBackground.setVisible(true);
		AlphaImage img = new AlphaImage(Opbm.locateFile("system_data.png"));
		lblBackground.setIcon(new ImageIcon(img.getBufferedImage()));
		pan.add(lblBackground);
		pan.moveToFront(lblBackground);
		frame.setVisible(true);
	}

	/** Self-explanatory.  Builds the GUI for OPBM using a four-panel design:
	 * 1)  Header
	 * 2)  Left panel for navigation
	 * 3)  Right panel for display and editing of controls
	 * 4)  Status bar for displaying tooltips and general information
	 *
	 */
	public void createResultsViewer(boolean visible)
	{
		Dimension minSize, maxSize;

		m_frame = new DroppableFrame(m_opbm, false, true);
		m_opbm.addResultsViewerToQueue(m_frame);
		m_frame.setTitle("OPBM - Results Viewer");

		// Compute the actual size we need for our window, so it's properly centered
		m_frame.pack();
		m_width			= 1000;
		m_height		= 566;
		Insets fi		= m_frame.getInsets();
		m_actual_width	= m_width  + fi.left + fi.right;
		m_actual_height	= m_height + fi.top  + fi.bottom;
		minSize = new Dimension(m_width  + fi.left + fi.right,
								m_height + fi.top  + fi.bottom);
		maxSize = new Dimension(1000 + fi.left + fi.right,
								1050 + fi.top  + fi.bottom);
		m_frame.setSize(minSize);
		m_frame.setMinimumSize(minSize);
		m_frame.setPreferredSize(minSize);
		m_frame.setMaximumSize(maxSize);
		m_frame.setMinMaxResizeBoundaries(m_actual_width, m_actual_height, m_actual_width, 1050 + fi.top + fi.bottom);
		m_frame.pack();

		m_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		m_frame.setLocationRelativeTo(null);	// Center window
		m_frame.setLayout(null);				// We handle all redraws
		m_frame.addKeyListener(this);
		m_frame.addMouseWheelListener(this);
		m_frame.addComponentListener(this);
		m_frame.addWindowListener(this);
		m_frame.addWindowListener(this);

		Container c = m_frame.getContentPane();
		c.setBackground(new Color(120, 120, 120));
		c.setForeground(Color.WHITE);

		m_pan = new JLayeredPane();
		m_pan.setLayout(null);
		m_pan.setBounds(0, 0, maxSize.width, maxSize.height);
		m_pan.setVisible(true);
		m_pan.setBorder(BorderFactory.createEmptyBorder());
		m_pan.addKeyListener(this);
		c.add(m_pan);

		// Set the background image
		m_lblBackground = new JLabel();
		m_lblBackground.setBounds(0, 0, maxSize.width, maxSize.height);
		m_lblBackground.setHorizontalAlignment(JLabel.LEFT);
		m_lblBackground.setVerticalAlignment(JLabel.TOP);
		m_imgBackground = new AlphaImage(Opbm.locateFile("results_viewer_background.png"));		// Granite
		m_pan.add(m_lblBackground);
		m_pan.moveToFront(m_lblBackground);

		extractScoreboard();
		extractBottom();

		m_lblBackground.setIcon(new ImageIcon(m_imgBackground.getBufferedImage()));
		m_lblBackground.setVisible(true);

		// This process now takes a moment
		// Create a thread to indicate to the user that the rest of the process (the results.xml file) is loading
		m_splashThread	= new Thread("loadResultsXmlSplash")
		{
			@Override
			public void run()
			{	// In the thread we render the bottom section
				m_imgSplash1 = new AlphaImage(Opbm.locateFile("rvsplash1.png"));
				m_imgSplash2 = new AlphaImage(Opbm.locateFile("rvsplash2.png"));
				m_imgSplash3 = new AlphaImage(Opbm.locateFile("rvsplash3.png"));
				m_splashTask = new AnimateImageTask();
				m_splashTask.add(m_imgSplash1);		// "Loading-.."
				m_splashTask.add(m_imgSplash2);		// "Loading.-."
				m_splashTask.add(m_imgSplash3);		// "Loading..-"
				m_splashTask.add(m_imgSplash2);		// "Loading.-."
				m_splashTask.animateAtBounds(28, 129, 122, 28, m_pan, 333);
			}
		};
		m_splashThread.start();
		m_frame.setVisible(visible);
	}

	/**
	 * 13,63	- scoreboard			- 152x183
	 * 28,76	- scoreboard internal	- 122x155
	 */
	private void extractScoreboard()
	{

		m_imgScoreboard			= m_imgBackground.extractImage(13, 63, 13+152, 63+183);
		m_imgScoreboardInternal	= m_imgBackground.extractImage(26, 74, 26+126, 74+159);

		m_imgScoreboard.applyAlphaMask(Opbm.locateFile("scoreboard.png"));
		m_imgBackground.darkenByAlphaMask(Opbm.locateFile("scoreboard_background.png"), 13, 63, 13+152, 63+184);
		m_imgScoreboard.scaleContrast(0.75);
		m_imgScoreboard.scaleBrightness(0.75);
		m_imgScoreboard.recolorize(AlphaImage.makeARGB(255, 255, 227, 174));
		m_imgScoreboard.applyAlphaMask(Opbm.locateFile("scoreboard2.png"));
		JLabel lblScoreboard = new JLabel();
		lblScoreboard.setBounds(13, 63, 152, 183);
		lblScoreboard.setIcon(new ImageIcon(m_imgScoreboard.getBufferedImage()));
		lblScoreboard.setVisible(true);
		m_pan.add(lblScoreboard);
		m_pan.moveToFront(lblScoreboard);

		m_imgScoreboardInternal1Mask = new AlphaImage(Opbm.locateFile("scoreboard_internal1.png"));
		m_imgScoreboardInternal2Mask = new AlphaImage(Opbm.locateFile("scoreboard_internal2.png"));
		m_imgBackground.darkenByAlphaMask(m_imgScoreboardInternal1Mask, 26, 74, 26+126, 74+159);

		// Create the name space text, which isn't really on the scoreboard, but is updated in the same method
		m_lblRunName = new JLabel();
		m_lblRunName.setBounds(790, 0, 210, 48);
		m_lblRunName.setHorizontalAlignment(JLabel.CENTER);
		m_lblRunName.setVerticalAlignment(JLabel.CENTER);
		m_lblRunName.setForeground(Color.white);
		m_lblRunName.setFont(new Font("Calibri", Font.BOLD, 24));
		m_lblRunName.setOpaque(false);
		m_lblRunName.setVisible(true);
		m_pan.add(m_lblRunName);
		m_pan.moveToFront(m_lblRunName);

		// Extract the label behind the middle part of the scoreboard, which is colorized in various areas for the background
		m_imgScoreboardMiddleBackground = m_imgScoreboard.extractImage(15, 66, 15+122, 66+29);
		m_imgScoreboardMiddleBackground.scaleBrightness(0.95, 0, 12, 67+28+27, 28);
//		m_imgMiddleBackground.recolorize(AlphaImage.makeARGB(255, 235, 235, 235),     0,  0,      122, 12);		// top light gray area
		m_imgScoreboardMiddleBackground.recolorize(AlphaImage.makeARGB(255, 255, 255, 255),     0, 12,       67, 28);		// lower-left white area
		m_imgScoreboardMiddleBackground.recolorize(AlphaImage.makeARGB(255,  32, 255,  32),    67, 12,    67+28, 28);		// success green area
		m_imgScoreboardMiddleBackground.recolorize(AlphaImage.makeARGB(255, 255,  32,  32), 67+28, 12, 67+28+27, 28);		// failure red area
		m_imgScoreboardMiddleBackground.darkenByAlphaMask(Opbm.locateFile("scoreboard_middle.png"));
		JLabel m_lblMiddleBackground = new JLabel();
		m_lblMiddleBackground.setBounds(28, 129, 122, 29);
		m_lblMiddleBackground.setIcon(new ImageIcon(m_imgScoreboardMiddleBackground.getBufferedImage()));
		m_pan.add(m_lblMiddleBackground);
		m_pan.moveToFront(m_lblMiddleBackground);

		Font fHeader	= new Font("Calibri", Font.PLAIN, 11);
		Font fData		= new Font("Calibri", Font.BOLD, 15);

		// Create the "Run Time" text
		m_lblScoreboardRunTimeHeader = new JLabel("Time");
		m_lblScoreboardRunTimeHeader.setBounds(28, 130, 68, 13);
		m_lblScoreboardRunTimeHeader.setHorizontalAlignment(JLabel.CENTER);
		m_lblScoreboardRunTimeHeader.setVerticalAlignment(JLabel.CENTER);
		m_lblScoreboardRunTimeHeader.setForeground(Color.black);
		m_lblScoreboardRunTimeHeader.setFont(fHeader);
		m_lblScoreboardRunTimeHeader.setOpaque(false);
		m_lblScoreboardRunTimeHeader.setVisible(true);
		m_pan.add(m_lblScoreboardRunTimeHeader);
		m_pan.moveToFront(m_lblScoreboardRunTimeHeader);

		// Create the area for the actual run time calculation
		m_lblScoreboardRunTime = new JLabel();
		m_lblScoreboardRunTime.setBounds(28, 142, 68, 16);
		m_lblScoreboardRunTime.setHorizontalAlignment(JLabel.CENTER);
		m_lblScoreboardRunTime.setVerticalAlignment(JLabel.CENTER);
		m_lblScoreboardRunTime.setForeground(Color.black);
		m_lblScoreboardRunTime.setFont(fData);
		m_lblScoreboardRunTime.setOpaque(false);
		m_lblScoreboardRunTime.setVisible(true);
		m_pan.add(m_lblScoreboardRunTime);
		m_pan.moveToFront(m_lblScoreboardRunTime);

		// Create the "Run Results" text
		m_lblScoreboardRunResultsHeader = new JLabel("Results");
		m_lblScoreboardRunResultsHeader.setBounds(95, 130, 55, 13);
		m_lblScoreboardRunResultsHeader.setHorizontalAlignment(JLabel.CENTER);
		m_lblScoreboardRunResultsHeader.setVerticalAlignment(JLabel.CENTER);
		m_lblScoreboardRunResultsHeader.setForeground(Color.black);
		m_lblScoreboardRunResultsHeader.setFont(fHeader);
		m_lblScoreboardRunResultsHeader.setOpaque(false);
		m_lblScoreboardRunResultsHeader.setVisible(true);
		m_pan.add(m_lblScoreboardRunResultsHeader);
		m_pan.moveToFront(m_lblScoreboardRunResultsHeader);

		// Create the background image for Success
		m_lblScoreboardRunResultsSuccesses = new JLabel();
		m_lblScoreboardRunResultsSuccesses.setBounds(95, 142, 28, 16);
		m_lblScoreboardRunResultsSuccesses.setHorizontalAlignment(JLabel.CENTER);
		m_lblScoreboardRunResultsSuccesses.setVerticalAlignment(JLabel.CENTER);
		m_lblScoreboardRunResultsSuccesses.setForeground(Color.black);
		m_lblScoreboardRunResultsSuccesses.setFont(fData);
		m_lblScoreboardRunResultsSuccesses.setOpaque(false);
		m_lblScoreboardRunResultsSuccesses.setVisible(true);
		m_pan.add(m_lblScoreboardRunResultsSuccesses);
		m_pan.moveToFront(m_lblScoreboardRunResultsSuccesses);

		// Create the background image for Failure
		m_lblScoreboardRunResultsFailures = new JLabel();
		m_lblScoreboardRunResultsFailures.setBounds(124, 142, 27, 16);
		m_lblScoreboardRunResultsFailures.setHorizontalAlignment(JLabel.CENTER);
		m_lblScoreboardRunResultsFailures.setVerticalAlignment(JLabel.CENTER);
		m_lblScoreboardRunResultsFailures.setForeground(Color.white);
		m_lblScoreboardRunResultsFailures.setFont(fData);
		m_lblScoreboardRunResultsFailures.setOpaque(false);
		m_lblScoreboardRunResultsFailures.setVisible(true);
		m_pan.add(m_lblScoreboardRunResultsFailures);
		m_pan.moveToFront(m_lblScoreboardRunResultsFailures);
	}

	private void extractBottom()
	{
		Font buttonFont, labelFont;
		int foreSelectedNeutral, foreSelectedOver, foreUnselectedNeutral, foreUnselectedOver;
		int backSelectedNeutral, backSelectedOver, backUnselectedNeutral, backUnselectedOver;
		Rectangle rect;

		m_imgBottom = m_imgBackground.extractImage(180, 50, m_imgBackground.getWidth(), m_imgBackground.getHeight());
		m_imgBottom.scaleContrast(0.75);
		m_imgBottom.scaleBrightness(0.75);
		m_imgBottom.recolorize(AlphaImage.makeARGB(255, 255, 255, 255));

		m_lblBottom = new JLabel();
		m_lblBottom.setBounds(180, 50, m_imgBottom.getWidth(), m_imgBottom.getHeight());
		m_lblBottom.setIcon(new ImageIcon(m_imgBottom.getBufferedImage()));
		m_lblBottom.setHorizontalAlignment(JLabel.LEFT);
		m_lblBottom.setVerticalAlignment(JLabel.TOP);
		m_lblBottom.setVisible(true);
		m_pan.add(m_lblBottom);
		m_pan.moveToFront(m_lblBottom);

		m_scrollbarBottom = new JScrollBar(JScrollBar.VERTICAL);
		m_scrollbarBottom.setBounds(m_lblBottom.getX() + m_lblBottom.getWidth() - 20,
									m_lblBottom.getY(),
									20,
									m_height);
		m_scrollbarBottom.setVisible(true);
		m_scrollbarBottom.addAdjustmentListener(this);
		m_scrollbarBottom.addKeyListener(this);
		m_pan.add(m_scrollbarBottom);
		m_pan.moveToFront(m_scrollbarBottom);

		// Add the buttons for showing the times or the scores
		backSelectedNeutral		= AlphaImage.makeARGB(255,  64, 255,  64);		// brighter green
		backSelectedOver		= AlphaImage.makeARGB(255,  32, 215,  32);		// darker green
		backUnselectedOver		= AlphaImage.makeARGB(255,  16,  32,  16);		// darker gray-green
		backUnselectedNeutral	= AlphaImage.makeARGB(255,  32,  48,  32);		// dark gray-green
		foreSelectedNeutral		= AlphaImage.makeARGB(255, 255, 255, 255);		// white
		foreSelectedOver		= AlphaImage.makeARGB(255, 255, 255, 255);		// white
		foreUnselectedNeutral	= AlphaImage.makeARGB(255, 192, 255, 192);		// greenish
		foreUnselectedOver		= AlphaImage.makeARGB(255, 192, 255, 192);		// greenish

		labelFont		= new Font("Calibri", Font.PLAIN, 18);
		JLabel label	= new JLabel("Show by:");
		label.setOpaque(false);
		label.setFont(labelFont);
		label.setForeground(Color.WHITE);
		label.setBounds(53, 257+30, 100, 20);
		label.setVisible(true);
		m_pan.add(label);
		m_pan.moveToFront(label);

		buttonFont	= new Font("Calibri", Font.BOLD, 16);
		rect = new Rectangle();

		rect.setBounds(55, 280+30, AlphaImage.getButtonWidth("Scores", buttonFont), AlphaImage.getButtonHeight("Scores", buttonFont));
		m_scores = new JLabelHotTrack();
		m_scores.setup(this, m_pan, "Scores", buttonFont, rect, backSelectedNeutral, backUnselectedNeutral, backSelectedOver, backUnselectedOver, foreSelectedNeutral, foreUnselectedNeutral, foreSelectedOver, foreUnselectedOver, true);
		m_displayMode = _SCORES;

		rect.setBounds(55, 305+30, AlphaImage.getButtonWidth("Times", buttonFont), AlphaImage.getButtonHeight("Times", buttonFont));
		m_times = new JLabelHotTrack();
		m_times.setup(this, m_pan, "Times", buttonFont, rect, backSelectedNeutral, backUnselectedNeutral, backSelectedOver, backUnselectedOver, foreSelectedNeutral, foreUnselectedNeutral, foreSelectedOver, foreUnselectedOver, false);

		label = new JLabel("External Views:");
		label.setOpaque(false);
		label.setFont(labelFont);
		label.setForeground(Color.WHITE);
		label.setBounds(33, 377+30, 130, 20);
		label.setVisible(true);
		m_pan.add(label);
		m_pan.moveToFront(label);

		// CSV icon
		m_csv = new JLabelHotTrack(this);
		m_csv.setType(JLabelHotTrack.CLICK_ACTION);
		m_csv.setIdentifier("csv");
		JLabel labelUnselectedNeutral = new JLabel();
		labelUnselectedNeutral.setVisible(false);
		labelUnselectedNeutral.setToolTipText("View min, max, average, geometric mean, coefficient of variation by atom");
		m_pan.add(labelUnselectedNeutral);
		m_pan.moveToFront(labelUnselectedNeutral);

		JLabel labelUnselectedOver = new JLabel();
		labelUnselectedOver.setVisible(false);
		labelUnselectedOver.setToolTipText("View min, max, average, geometric mean, coefficient of variation by atom");
		m_pan.add(labelUnselectedOver);
		m_pan.moveToFront(labelUnselectedOver);

		m_csv.setUnselectedNeutral(labelUnselectedNeutral);
		m_csv.setUnselectedOver(labelUnselectedOver);

		// Create all the buttons for the various forms of activity
		AlphaImage img	= new AlphaImage(Opbm.locateFile("csv.png"));
		labelUnselectedNeutral.setIcon(new ImageIcon(img.getBufferedImage()));
		img		= new AlphaImage(img);
		img.scaleBrightness(0.4);
		labelUnselectedOver.setIcon(new ImageIcon(img.getBufferedImage()));
		m_csv.setBounds(60, 410+30, img.getWidth(), img.getHeight());
		m_csv.renderHotTrackChange();

/*
		// Report icon
		m_report = new JLabelHotTrack(this);
		m_report.setType(JLabelHotTrack.CLICK_ACTION);
		m_report.setIdentifier("csv");
		labelUnselectedNeutral = new JLabel();
		labelUnselectedNeutral.setVisible(false);
		labelUnselectedNeutral.setToolTipText("View this report as a report");
		m_pan.add(labelUnselectedNeutral);
		m_pan.moveToFront(labelUnselectedNeutral);

		labelUnselectedOver = new JLabel();
		labelUnselectedOver.setVisible(false);
		labelUnselectedOver.setToolTipText("View this data as a report");
		m_pan.add(labelUnselectedOver);
		m_pan.moveToFront(labelUnselectedOver);

		m_report.setUnselectedNeutral(labelUnselectedNeutral);
		m_report.setUnselectedOver(labelUnselectedOver);

		// Create all the buttons for the various forms of activity
		img	= new AlphaImage(Opbm.locateFile("report.png"));
		labelUnselectedNeutral.setIcon(new ImageIcon(img.getBufferedImage()));
		img	= new AlphaImage(img);
		img.scaleBrightness(0.5);
		labelUnselectedOver.setIcon(new ImageIcon(img.getBufferedImage()));
		m_report.setBounds(65, 480, img.getWidth(), img.getHeight());
		m_report.renderHotTrackChange();
 */
	}

	public void buildRenderList()
	{
		// Rebuild the render list
		populateRenderList(areAnyFilterTagsPopulated(), m_filterTags);

		// Update the scrollbar position
		m_scrollbarBottom.setBlockIncrement(10);
		m_scrollbarBottom.setUnitIncrement(1);
		m_scrollbarBottom.setMinimum(0);
		m_scrollbarBottom.setMaximum(Math.max(m_scrollbarBottom.getVisibleAmount() + m_renderList.size() - 16, m_scrollbarBottom.getVisibleAmount() + 3));
		m_scrollbarBottom.setValue(m_renderListTop);
	}

	/**
	 * Called to populate the render list with every visible entry, based on
	 * filter criteria, whether the user has expanded a node, etc.
	 */
	public void populateRenderList(boolean		byFilter,
								   Tuple		filterTags)
	{
		int i, top, max;
		ResultsViewerLine rvl;

		// Clear out the old render list
		m_renderList.clear();

		// Build the list of things that will be visible
		top = getRunHeaderY() + ResultsViewerLine._BOX_HEIGHT;
		max	= top + m_lblBottom.getHeight() - 5;
		for (i = 0; i < m_rvlList.size(); i++)
		{
			rvl = m_rvlList.get(i);
			if (!rvl.isIgnored() && (!byFilter || rvl.countIfTagMatch(filterTags)))
			{	// This one is not filtered out or ignored
				if (rvl.isVisible())
				{	// This one is visible (it's not hidden by a collapsed node)
					if (i >= m_renderListTop)
					{	// We can position this one
						if (top < max)
						{	// There's room for this one
							rvl.setTop(top);
							top += rvl.getNameboxHeight();
							rvl.setAboutToBeVisible(true);

						} else {
							// No room
							rvl.setAboutToBeVisible(false);

						}
					} else {
						// Not yet one to be displayed
						rvl.setAboutToBeVisible(false);
					}
				} else {
					// Not visible (is hidden by its parent being collapsed)
					rvl.setAboutToBeVisible(false);
				}
			} else {
				// Ignored or filtered out
				rvl.setAboutToBeVisible(false);
			}
			m_renderList.add(rvl);
	}
		// When we get here, every item that can be displayed has been
		// processed, and all of those that will fit in the visible bottom
		// portion are setup to be displayed.
		// When we get here, m_renderList is populated with the items that
		// will be rendered, everything else should/will be hidden.
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			moveUp(1);
			renderBottom();

		} else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
			moveUp(((m_height - m_lblBottom.getY()) / ResultsViewerLine._BOX_HEIGHT) - 2);
			renderBottom();

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			moveDown(1);
			renderBottom();

		} else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			moveDown(((m_height - m_lblBottom.getY()) / ResultsViewerLine._BOX_HEIGHT) - 2);
			renderBottom();

		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			m_frame.dispose();

		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			Opbm.setBreakpointsEnabled(!Opbm.areBreakpointsEnabled());

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		Rectangle rect;
		Insets inset;

		if (m_frame != null && e.getComponent().equals(m_frame))
		{	// The window has resized
			inset		= m_frame.getInsets();
			m_height	= m_frame.getHeight()	- inset.top		- inset.bottom;
			m_width		= m_frame.getWidth()	- inset.left	- inset.right;

			if (m_scrollbarBottom != null)
			{	// Update the scrollbar
				rect = m_scrollbarBottom.getBounds();
				rect.height = m_height - rect.y;
				m_scrollbarBottom.setBounds(rect);
			}
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	public void addFilterTag(String		tag,
							 String		_boolean)
	{
		m_filterTags.add(tag, _boolean);
	}

	public int getFailureCount()
	{
		return(m_failureCount);
	}

	public void toggleStateCallback(JLabelHotTrack jlht)
	{
		if (jlht.getIdentifier().equalsIgnoreCase("scores"))
		{	// They clicked on "scores"
			if (m_displayMode != _SCORES)
			{	// Set it
				m_displayMode = _SCORES;
				renderBottom();
			}

		} else if (jlht.getIdentifier().equalsIgnoreCase("times")) {
			// They clicked on "times"
			if (m_displayMode != _TIMES)
			{	// Set it
				m_displayMode = _TIMES;
				renderBottom();
			}
		}
		// Update the on-screen buttons
		m_scores.setSelected(m_displayMode == _SCORES);
		m_scores.renderHotTrackChange();
		m_times.setSelected(m_displayMode == _TIMES);
		m_times.renderHotTrackChange();
	}

	public void clickActionCallback(JLabelHotTrack jlht)
	{
		Process process;

		if (jlht.getIdentifier().equalsIgnoreCase("csv"))
		{	// The CSV button was clicked
			try
			{	// Let their default/favorite spreadsheet load the results.csv file
				Desktop.getDesktop().open(new File(Opbm.getHarnessCSVDirectory() + "results.csv"));

			} catch (Throwable t) {
			}
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e)
	{	// We're about done with, tell OPBM to remove us from any future going-concerns
		m_opbm.removeResultsViewerFromQueue(m_frame);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		m_frame = null;
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
// Check here for possible use of single-click and double-click
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	/**
	 * Searches the render list to determine which entry is the specified RVL.
	 * @param rvl the RVL entry being searched for
	 * @return the RVL number, or -1 if not found
	 */
	public int getRenderListNumber(ResultsViewerLine rvl)
	{
		int i;

		for (i = 0; i < m_renderList.size(); i++)
		{
			if (m_renderList.get(i).equals(rvl))
				return(i);
		}
		return(-1);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int scroll;
		boolean scrollingUp, result;

		// See which way we're scrolling
// BREAKPOINT for untested code
		scroll		= e.getScrollAmount();
		scrollingUp	= ((e.getWheelRotation() < 0) ? true : false);

		// Scroll as many times as the wheel was moved
		if (scrollingUp)
			result = moveUp(scroll);		// Scrolling up
		else
			result = moveDown(scroll);	// Scrolling down

		if (result)
			renderBottom();
	}

	public boolean moveUp(int count)
	{
		if (m_renderListTop - count >= 0)
		{	// We can move up one
			m_renderListTop -= count;
			return(true);

		} else if (m_renderListTop > 0) {
			// We can move back to the beginning
			m_renderListTop = 0;
			return(true);

		} else {
			// Nope, we're at the top
			return(false);
		}
	}

	public boolean moveDown(int count)
	{
		if (m_renderListTop < m_renderList.size() - count)
		{	// We can move down the specified number
			m_renderListTop += count;
			return(true);

		} else if (m_renderListTop < m_renderList.size() - 1) {
			// We can move down at least a few, until we reach the end
			m_renderListTop = m_renderList.size() - 1;
			return(true);

		} else {
			// Nope, we're at the top
			return(false);
		}
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		if (e.getValue() != m_renderListTop)
		{	// Something has changed
			m_renderListTop = e.getValue();
			renderBottom();
		}
	}

	public int m_renderListTop;

	public DroppableFrame getDroppableFrame()	{	return(m_frame);				}
	public ResultsViewerLine getRootRVL()		{	return(m_rootRVL);				}
	public Tuple getFilterTags()				{	return(m_filterTags);			}
	public int getRunHeaderY()					{	return(m_lblBottom.getY() + 5);	}

	private Opbm						m_opbm;					// The parent
	private DroppableFrame				m_frame;				// The physical window
	private boolean						m_visible;				// Is the window initially visible?
	private JLayeredPane				m_pan;					// The master pane holding all child items
	private List<ResultsViewerLine>		m_rvlList;				// The master list of all ResultsViewerLine entries
	private ResultsViewerLine			m_rootRVL;				// The root results viewer line, the primary entry which holds all children
	private String						m_runName;				// The name of the run, from results.xml
	private JScrollBar					m_scrollbarBottom;		// Scrollbar for the bottom portion

	private int							m_failureCount;			// Determined at load time, the number of failures within
	private int							m_testCount;			// Determined at load time, the number of items tested
	private int							m_untestedCount;		// determined at load time, the number of items untested

	private AlphaImage					m_imgBackground;		// The alpha image used for the background image
	private JLabel						m_lblBackground;		// The background image holder/container used for the entire JFrame

	private AlphaImage					m_imgBottom;			// The alpha image area extracted and colorized for the bottom portion
	private JLabel						m_lblBottom;			// The background image holder/container used for the bottom portion

	private AlphaImage					m_imgScoreboard;		// The alpha image area extracted and colorized for the scoreboard portion
	private JLabel						m_lblScoreboard;		// The background image holder/container used for the scoreboard outer shape

	private AlphaImage					m_imgScoreboardInternal1Mask;	// An alpha image used to mask out part of the scoreboard during extraction, and used subsequently each time the scoreboard is redrawn
	private AlphaImage					m_imgScoreboardInternal2Mask;	// An alpha image used to mask out part of the scoreboard during extraction, and used subsequently each time the scoreboard is redrawn
	private AlphaImage					m_imgScoreboardInternal;
	private	AlphaImage					m_imgScoreboardMiddleBackground;
	private JLabel						m_lblScoreboardRunTimeHeader;
	private JLabel						m_lblScoreboardRunTime;
	private JLabel						m_lblScoreboardRunResultsHeader;
	private JLabel						m_lblScoreboardRunResultsSuccesses;
	private JLabel						m_lblScoreboardRunResultsFailures;

	private AlphaImage					m_imgSplash1;			// Image used for splash screen animation during load
	private AlphaImage					m_imgSplash2;			// Image used for splash screen animation during load
	private AlphaImage					m_imgSplash3;			// Image used for splash screen animation during load
	private AnimateImageTask			m_splashTask;			// Task switching images back and forth

	private JLabelHotTrack				m_times;				// "Times" button
	private JLabelHotTrack				m_scores;				// "Scores" button
	private JLabelHotTrack				m_csv;					// "CSV" button
	private JLabelHotTrack				m_report;				// "Report" button

	// Threads which are used to render the bottom and graph portions, as these sometimes take a few seconds
	private Thread						m_splashThread;			// The initial takes a few seconds, so this thread gives it a "Loading..." message

	// In the upper-right, the name of hte run
	private JLabel						m_lblRunName;			// Holds the run name for the entire set of results

	private List<ResultsViewerLine>		m_renderList;			// List of all rendered items
	private Tuple						m_filterTags;			// Tags discovered at load for filters, and rendered into the lblFilter image

	private int							m_width;
	private int							m_height;
	private int							m_actual_width;
	private int							m_actual_height;

	private int							m_displayMode = _SCORES;	// Default to score mode

	// Constants used to determine what we're displaying
	public final static int				_SCORES		= 1;
	public final static int				_TIMES		= 2;
}
