/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class of the OPBM.  It creates a GUI, loads
 * necessary files, beings processing based on context, etc.
 *
 * Last Updated:  July 18, 2011
 *
 * by Van Smith, Rick C. Hodgin
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @author Rick C. Hodgin
 * @version 1.0.1
 *
 */

package opbm.resultsviewer;

import opbm.graphics.JLabelHotTrack;
import opbm.dialogs.DroppableFrame;
import opbm.graphics.AlphaImage;
import opbm.common.Tuple;
import opbm.common.Utils;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import opbm.Opbm;
import opbm.dialogs.OpbmDialog;
import opbm.common.Xml;

public final class ResultsViewer implements KeyListener,
											MouseWheelListener,
											ComponentListener

{
	public ResultsViewer(Opbm		opbm,
						 int		width,
						 int		height,
						 boolean	visible)
	{
		m_opbm			= opbm;
		m_rootRVL		= null;
		m_width			= width;
		m_height		= height;
		m_actual_width	= width;
		m_actual_height	= height;
		m_filterTags	= new Tuple(opbm);
		m_graphThread	= null;
		m_graphLine		= null;
		createResultsViewer(visible);
	}

	public boolean load(String filename)
	{
		Xml results = Opbm.loadXml(filename);
		if (results != null)
		{	// No error, proceed like normal
			populate(results);
			return(true);

		} else {
			// Error, tell the user
			OpbmDialog od = new OpbmDialog(m_opbm, "There was an error processing the file<br>" + filename, "OPBM - Results Viewer Error", OpbmDialog._OKAY_BUTTON, "ResultsViewer", "");
			return(false);

		}

	}

	public void render()
	{
		renderScoreboard();
		renderFilters();
		renderBottom();
		renderGraph();		// We renderHotTrackChange the graph last because it requires some information obtained from the bottom section
	}

	public void renderScoreboard()
	{
		int switchX;
		double half, diff;
		String score;
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
//		if (m_failureCount == 0)
//		{	// Make the background green
//			img.alphaize(128);
//			img.colorize(AlphaImage.makeARGB(255, 0, 255, 0));
//		}
		img.applyAlphaMask(m_imgScoreboardInternal1Mask);

		// 1b) Render the rainbow gauge
		half = (((double)img.getHeight() - 83.0) / 2.0);
		diff = 100.0 - m_rootRVL.getScore();
		// We make the entire rainbow spectrum only show variations of 33 points
		switchX = (int)Utils.roundAwayFromZero(half * diff / 33.0);
		img.rainbowRectangle(1, 83, img.getWidth() - 2, img.getHeight() - 1, Math.min(Math.max((int)half + switchX - 6, 0), img.getHeight() - 83));

		// Score
		font	= new Font("Calibri", Font.BOLD, 64);
		rect	= new Rectangle(0, 0, img.getWidth(), 20);
		score	= Integer.toString((int)m_rootRVL.getScore());
		if (score.length() > 3)
			score = "999";
		img.drawStringInRectangle(rect, score, Color.WHITE, font, 255, true);

		if (m_failureCount != 0)
		{	// Make the background red
			img.recolorize(AlphaImage.makeARGB(255, 255, 0, 0));
		}

		img.applyAlphaMask(m_imgScoreboardInternal2Mask);
		m_lblScoreboard.setIcon(new ImageIcon(img.getBufferedImage()));
	}

	public void renderBottomButtons()
	{
		Rectangle rect;
		AlphaImage img, mask;
		JLabelHotTrack excel, word, notepad;
		JLabel labelUnselectedNeutral, labelUnselectedOver;

//		// Excel icon
//		excel = new JLabelHotTrack(this);
//		excel.setType(JLabelHotTrack.CLICK_ACTION);
//		excel.setIdentifier("excel");
//
//		labelUnselectedNeutral = new JLabel();
//		labelUnselectedNeutral.setVisible(false);
//		m_pan.add(labelUnselectedNeutral);
//		m_pan.moveToFront(labelUnselectedNeutral);
//
//		labelUnselectedOver = new JLabel();
//		labelUnselectedOver.setVisible(false);
//		m_pan.add(labelUnselectedOver);
//		m_pan.moveToFront(labelUnselectedOver);
//
//		excel.setUnselectedNeutral(labelUnselectedNeutral);
//		excel.setUnselectedOver(labelUnselectedOver);
//
//		// Create all the buttons for the various forms of activity
//		mask	= new AlphaImage(Opbm.locateFile("icon_mask.png"));
//		img		= new AlphaImage(Opbm.locateFile("excel_icon_neutral.png"));
//		img.applyAlphaMask(mask);
//		labelUnselectedNeutral.setIcon(new ImageIcon(img.getBufferedImage()));
//
//		img		= new AlphaImage(Opbm.locateFile("excel_icon_over.png"));
//		img.applyAlphaMask(mask);
//		labelUnselectedOver.setIcon(new ImageIcon(img.getBufferedImage()));
//
//		rect = m_lblBottom.getBounds();
//		excel.setBounds(rect.x + 4, rect.y + 44, img.getWidth(), img.getHeight());
//		excel.renderHotTrackChange();
//
//		// Word icon
//		word = new JLabelHotTrack(this);
//		word.setType(JLabelHotTrack.CLICK_ACTION);
//		word.setIdentifier("word");
//
//		labelUnselectedNeutral = new JLabel();
//		labelUnselectedNeutral.setVisible(false);
//		m_pan.add(labelUnselectedNeutral);
//		m_pan.moveToFront(labelUnselectedNeutral);
//
//		labelUnselectedOver = new JLabel();
//		labelUnselectedOver.setVisible(false);
//		m_pan.add(labelUnselectedOver);
//		m_pan.moveToFront(labelUnselectedOver);
//
//		word.setUnselectedNeutral(labelUnselectedNeutral);
//		word.setUnselectedOver(labelUnselectedOver);
//
//		// Create all the buttons for the various forms of activity
//		mask	= new AlphaImage(Opbm.locateFile("icon_mask.png"));
//		img		= new AlphaImage(Opbm.locateFile("word_icon_neutral.png"));
//		img.applyAlphaMask(mask);
//		labelUnselectedNeutral.setIcon(new ImageIcon(img.getBufferedImage()));
//
//		img		= new AlphaImage(Opbm.locateFile("word_icon_over.png"));
//		img.applyAlphaMask(mask);
//		labelUnselectedOver.setIcon(new ImageIcon(img.getBufferedImage()));
//
//		rect = m_lblBottom.getBounds();
//		word.setBounds(rect.x + 4, rect.y + 88, img.getWidth(), img.getHeight());
//		word.renderHotTrackChange();

		// Notepad icon
		notepad = new JLabelHotTrack(this);
		notepad.setType(JLabelHotTrack.CLICK_ACTION);
		notepad.setIdentifier("notepad");

		labelUnselectedNeutral = new JLabel();
		labelUnselectedNeutral.setToolTipText("Export results to text file");
		labelUnselectedNeutral.setVisible(false);
		m_pan.add(labelUnselectedNeutral);
		m_pan.moveToFront(labelUnselectedNeutral);

		labelUnselectedOver = new JLabel();
		labelUnselectedOver.setToolTipText("Export results to text file");
		labelUnselectedOver.setVisible(false);
		m_pan.add(labelUnselectedOver);
		m_pan.moveToFront(labelUnselectedOver);

		notepad.setUnselectedNeutral(labelUnselectedNeutral);
		notepad.setUnselectedOver(labelUnselectedOver);

		// Create all the buttons for the various forms of activity
		mask	= new AlphaImage(Opbm.locateFile("icon_mask.png"));
		img		= new AlphaImage(Opbm.locateFile("notepad_icon_neutral.png"));
		img.applyAlphaMask(mask);
// REMEMBER need to remove this when the control does something
img.grayscale();
		labelUnselectedNeutral.setIcon(new ImageIcon(img.getBufferedImage()));

		img		= new AlphaImage(Opbm.locateFile("notepad_icon_over.png"));
		img.applyAlphaMask(mask);
// REMEMBER need to remove this when the control does something
img.grayscale();
		labelUnselectedOver.setIcon(new ImageIcon(img.getBufferedImage()));

		rect = m_lblBottom.getBounds();
		notepad.setBounds(rect.x + 4, rect.y + 36 /*132*/, img.getWidth(), img.getHeight());
		notepad.renderHotTrackChange();
	}

	public void renderFilters()
	{
		int i, top, left;
		String tag;
		boolean selected;
		JLabelHotTrack label;
		JLabel labelSelectedNeutral, labelSelectedOver, labelUnselectedNeutral, labelUnselectedOver;
		AlphaImage img, button;
		Rectangle rect, filterRect;
		Font font;

		// Determine the size of the filter area
		filterRect = m_lblFilter.getBounds();

		// 5) Render the filter buttons
		font	= new Font("Calibri", Font.BOLD, 14);
		top		= 10;
		left	= 30;
		for (i = 0; i < m_filterTags.size(); i++)
		{
			tag		= m_filterTags.getFirst(i);
			// Determine where the button will go logically
			rect = new Rectangle(left, top, AlphaImage.getButtonWidth(tag, font), AlphaImage.getButtonHeight(tag, font));
			if (left + rect.width >= filterRect.getWidth() - 15)
			{
				left = 30;
				top += AlphaImage.getButtonHeight(tag, font) + 10;
				rect.setBounds(left, top, AlphaImage.getButtonWidth(tag, font), AlphaImage.getButtonHeight(tag, font));
			}

			// Grab the controls for the button
			selected	= ((String)m_filterTags.getSecond(i)).equalsIgnoreCase("Yes");
			label		= (JLabelHotTrack)m_filterTags.getThird(i);
			if (label == null)
			{
				label = new JLabelHotTrack(this);
				label.setTupleToUpdateByMouseActivity(m_filterTags, i);

				labelSelectedNeutral = new JLabel();
				labelSelectedNeutral.setVisible(false);
				m_pan.add(labelSelectedNeutral);
				m_pan.moveToFront(labelSelectedNeutral);

				labelSelectedOver = new JLabel();
				labelSelectedOver.setVisible(false);
				m_pan.add(labelSelectedOver);
				m_pan.moveToFront(labelSelectedOver);

				labelUnselectedNeutral = new JLabel();
				labelUnselectedNeutral.setVisible(false);
				m_pan.add(labelUnselectedNeutral);
				m_pan.moveToFront(labelUnselectedNeutral);

				labelUnselectedOver = new JLabel();
				labelUnselectedOver.setVisible(false);
				m_pan.add(labelUnselectedOver);
				m_pan.moveToFront(labelUnselectedOver);

				label.setSelectedNeutral(labelSelectedNeutral);
				label.setSelectedOver(labelSelectedOver);
				label.setUnselectedNeutral(labelUnselectedNeutral);
				label.setUnselectedOver(labelUnselectedOver);

				// Create all the buttons for the various forms of activity
				button = AlphaImage.createButton(tag, font, AlphaImage.makeARGB(255, 64, 255, 64), Color.WHITE);
				labelSelectedNeutral.setIcon(new ImageIcon(button.getBufferedImage()));

				button = AlphaImage.createButton(tag, font, AlphaImage.makeARGB(255, 128, 255, 128), Color.WHITE);
				labelSelectedOver.setIcon(new ImageIcon(button.getBufferedImage()));

				button = AlphaImage.createButton(tag, font, AlphaImage.makeARGB(255, 255, 64, 64), Color.WHITE);
				labelUnselectedNeutral.setIcon(new ImageIcon(button.getBufferedImage()));

				button = AlphaImage.createButton(tag, font, AlphaImage.makeARGB(255, 255, 128, 128), Color.WHITE);
				labelUnselectedOver.setIcon(new ImageIcon(button.getBufferedImage()));

				// Add the hot track label
				m_filterTags.setThird(i, label);

			} else {
				labelSelectedNeutral	= label.getSelectedNeutral();
				labelSelectedOver		= label.getSelectedOver();
				labelUnselectedNeutral	= label.getUnselectedNeutral();
				labelUnselectedOver		= label.getUnselectedOver();

			}

			// Position the label (in case its position has changed)
			label.setSelected(selected);
			label.setBounds(filterRect.x + rect.x, filterRect.y + rect.y, rect.width, rect.height);
			label.renderHotTrackChange();

			// Move over for the next button
			left += rect.width + 15;
		}
	}

	public void renderBottomAndGraph()
	{
		renderBottom();
		renderGraph();
		try {
			if (m_bottomThread != null)
				m_bottomThread.join();

			if (m_graphThread != null)
				m_graphThread.join();

		} catch (InterruptedException ex) {
		}
		m_bottomThread	= null;
		m_graphThread	= null;
	}

	public void renderBottom()
	{
		// Create in a separate thread, as this task may take a few seconds
		// as it renders both the bottom and the graph
		m_bottomThread	= new Thread("cell_area_worker")
		{
			@Override
			public void run()
			{
				m_rootRVL.render(new Dimension(53, 6));
				renderBottomButtons();
			}
		};
		m_bottomThread.start();
	}

	/**
	 * Iterate through every entry until we find the m_lineSelectedGraph entry,
	 * and then draw its graph.  If one of its children is selected with the
	 * m_graphLeg entry, then we highlight that line and portion.  Note:  The
	 * graph leg must be its immediate child, not its grand child or great
	 * grand child, etc.
	 */
	public void renderGraph()
	{
		if (m_graphLine != null)
		{
			m_graphThread	= new Thread("cells_worker")
			{
				@Override
				public void run()
				{
					m_graphLine.renderGraph();
				}
			};
			m_graphThread.start();

		} else {
			m_graphLine = null;
		}
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
		List<Xml> list = new ArrayList<Xml>(0);

		// Grab all the top-level data items
		m_rootRVL		= new ResultsViewerLine(this, m_pan, m_lblBottom, m_imgBackground, m_lblGraphInternal, m_imgGraphInternal, m_imgGraphInternalMask, 0, true, true, true, results.getChildNode("resultsdata").getChildNode("result"));
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
			line = new ResultsViewerLine(this, m_pan, m_lblBottom, m_imgBackground, m_lblGraphInternal, m_imgGraphInternal, m_imgGraphInternalMask, 1, true, true, false, item);
			if (line.wasTested())
			{	// This line was tested
				++m_testCount;
				m_failureCount += line.wasSuccessful() ? 0 : 1;
			} else {
				++m_untestedCount;
			}

			if (last != null)
				last.setNext(line);

			if (first == null)
				first = line;

			// Add its children
			child = populateScenarios(item, line);
			line.setChild(child);

			last = line;
		}
		m_rootRVL.setChild(first);
		recomputeScores();
		assignIterativeColorNumbers(m_rootRVL);
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
			thisOne.sumChildren(areAnyFilterTagsPopulated(), m_filterTags);

			thisOne = thisOne.getNext();
		}
	}

	public void assignIterativeColorNumbers(ResultsViewerLine rvl)
	{
		while (rvl != null)
		{
			rvl.assignIterativeColorNumber();
			assignIterativeColorNumbers(rvl.getChild());
			rvl = rvl.getNext();
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
			line = new ResultsViewerLine(this, m_pan, m_lblBottom, m_imgBackground, m_lblGraphInternal, m_imgGraphInternal, m_imgGraphInternalMask, 2, false, true, false, item);
			if (line.wasTested())
			{	// This line was tested
				++m_testCount;
				m_failureCount += line.wasSuccessful() ? 0 : 1;
			} else {
				++m_untestedCount;
			}

			if (last != null)
				last.setNext(line);

			if (first == null)
				first = line;

			// Add its children
			child = populateMolecules(item, line);
			line.setChild(child);

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
			line = new ResultsViewerLine(this, m_pan, m_lblBottom, m_imgBackground, m_lblGraphInternal, m_imgGraphInternal, m_imgGraphInternalMask, 3, false, true, false, item);
			if (line.wasTested())
			{	// This line was tested
				++m_testCount;
				m_failureCount += line.wasSuccessful() ? 0 : 1;
			} else {
				++m_untestedCount;
			}

			if (last != null)
				last.setNext(line);

			if (first == null)
				first = line;

			// Add its children
			child = populateAtoms(item, line);
			line.setChild(child);

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
			line = new ResultsViewerLine(this, m_pan, m_lblBottom, m_imgBackground, m_lblGraphInternal, m_imgGraphInternal, m_imgGraphInternalMask, 4, false, false, false, item);
			if (line.wasTested())
			{	// This line was tested
				++m_testCount;
				m_failureCount += line.wasSuccessful() ? 0 : 1;
			} else {
				++m_untestedCount;
			}

			if (last != null)
				last.setNext(line);

			if (first == null)
				first = line;

			// Add its children
			child = populateWorklets(item, line);
			line.setChild(child);

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
			line = new ResultsViewerLine(this, m_pan, m_lblBottom, m_imgBackground, m_lblGraphInternal, m_imgGraphInternal, m_imgGraphInternalMask, 5, false, false, false, item);
			if (line.wasTested())
			{	// This line was tested
				++m_testCount;
				m_failureCount += line.wasSuccessful() ? 0 : 1;
			} else {
				++m_untestedCount;
			}

			if (last != null)
				last.setNext(line);

			if (first == null)
				first = line;

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
		frame = new DroppableFrame(m_opbm, false);
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
// REMEMBER need to remove this when the control does something
img.grayscale();
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
		Dimension prefSize;

		m_frame = new DroppableFrame(m_opbm, false);
		m_frame.setTitle("OPBM - Results Viewer");

		// Compute the actual size we need for our window, so it's properly centered
		m_frame.pack();
		Insets fi		= m_frame.getInsets();
		m_actual_width	= m_width  + fi.left + fi.right;
		m_actual_height	= m_height + fi.top  + fi.bottom;
		m_frame.setSize(m_width  + fi.left + fi.right,
						m_height + fi.top  + fi.bottom);

		prefSize = new Dimension(m_width  + fi.left + fi.right,
								 m_height + fi.top  + fi.bottom);
		m_frame.setMinimumSize(prefSize);
		m_frame.setPreferredSize(prefSize);

		prefSize = new Dimension(m_width  + fi.left + fi.right,
								 m_height + fi.top  + fi.bottom);
		m_frame.setMinimumSize(prefSize);
		m_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        m_frame.setSize(m_width, m_height);
		m_frame.setLocationRelativeTo(null);  // Center window
		m_frame.setLayout(null);				// We handle all redraws
		m_frame.addKeyListener(this);
		m_frame.addMouseWheelListener(this);
		m_frame.addComponentListener(this);

		Container c = m_frame.getContentPane();
		c.setBackground(new Color(120, 120, 120));
		c.setForeground(Color.WHITE);

		m_pan = new JLayeredPane();
		m_pan.setLayout(null);
		m_pan.setBounds(0, 0, m_width, m_height);
		m_pan.setVisible(true);
		m_pan.setBorder(BorderFactory.createEmptyBorder());
		m_pan.addKeyListener(this);
		c.add(m_pan);

		// Set the background image
		m_lblBackground = new JLabel();
		m_lblBackground.setBounds(0, 0, m_width, m_height);
		m_lblBackground.setHorizontalAlignment(JLabel.LEFT);
		m_lblBackground.setVerticalAlignment(JLabel.TOP);
//		m_imgBackground = new AlphaImage(Opbm.locateFile("results_viewer_background1.png"));		// Original mahogany
//		m_imgBackground = new AlphaImage(Opbm.locateFile("results_viewer_background2.png"));		// Darker mahogany
//		m_imgBackground = new AlphaImage(Opbm.locateFile("results_viewer_background3.png"));		// Slate
		m_imgBackground = new AlphaImage(Opbm.locateFile("results_viewer_background4.png"));		// Granite
//		m_imgBackground = new AlphaImage(Opbm.locateFile("results_viewer_background5.png"));		// Clowns + Granite
//		m_imgBackground = new AlphaImage(Opbm.locateFile("results_viewer_background6.png"));		// Z80 CPU die
		m_pan.add(m_lblBackground);
		m_pan.moveToFront(m_lblBackground);

		extractBottom();
		extractScoreboard();
		extractGraph();
		extractThisSystem();
		extractReferenceSystem();
		extractFilter();

		m_lblBackground.setIcon(new ImageIcon(m_imgBackground.getBufferedImage()));
		m_lblBackground.setVisible(true);
		m_frame.setVisible(visible);
	}

	/**
	 * Create the skinned components of the background image, using the various masks, etc.
	 */
	private void extractBottom()
	{
		m_imgBottom = m_imgBackground.extractImage(0, m_height - 193, m_width, m_height);
		m_imgBottom.applyAlphaMask(Opbm.locateFile("bottom.png"));
		m_imgBackground.darkenByAlphaMask(Opbm.locateFile("bottom_background.png"), 0, m_height - 193, m_width, m_height);
		m_imgBottom.recolorize(AlphaImage.makeARGB(255, 255, 255, 255));
		m_imgBottom.scaleContrast(0.75);
		m_imgBottom.scaleBrightness(0.75);
		m_lblBottom = new JLabel();
		m_lblBottom.setBounds(0, m_height - 193, m_width, 193);
		m_lblBottom.setIcon(new ImageIcon(m_imgBottom.getBufferedImage()));
		m_lblBottom.setVisible(true);
		m_pan.add(m_lblBottom);
		m_pan.moveToFront(m_lblBottom);

		// Add an input field for selected cell movement left/right/up/down, and single-digit commands
		m_txtInput = new JTextField();
		// Position off-screen:
		m_txtInput.setBounds(25, m_height + 100, 16, 24);
		m_txtInput.setVisible(true);
		m_txtInput.addKeyListener(this);
		m_pan.add(m_txtInput);
		m_pan.moveToFront(m_txtInput);
		m_txtInput.requestFocusInWindow();
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
	}

	/**
	 * 175,63	- graph				- 610x183
	 * 190,76	- graph internal	- 580x155
	 */
	private void extractGraph()
	{
		m_imgGraph				= m_imgBackground.extractImage(175, 63, 175+610, 63+183);
		m_imgGraphInternal		= m_imgBackground.extractImage(190, 77, 190+580, 77+155);
		m_imgGraphInternalMask	= new AlphaImage(Opbm.locateFile("graph_internal_mask.png"));
		m_imgGraphInternal.applyAlphaMask(m_imgGraphInternalMask);
		m_imgGraphMask			= new AlphaImage(Opbm.locateFile("graph.png"));
		m_imgBackground.darkenByAlphaMask(Opbm.locateFile("graph_background.png"), 175, 63, 175+610, 63+184);
		m_imgGraph.applyAlphaMask(m_imgGraphMask);
		m_imgGraph.scaleContrast(0.75);
		m_imgGraph.scaleBrightness(0.75);
		m_imgGraph.recolorize(AlphaImage.makeARGB(255, 255, 227, 174));
		m_imgGraph.darkenByAlphaMask(Opbm.locateFile("graph2.png"));

		m_lblGraph = new JLabel();
		m_lblGraph.setBounds(175, 63, 610, 183);
		m_lblGraph.setIcon(new ImageIcon(m_imgGraph.getBufferedImage()));
		m_lblGraph.setVisible(true);
		m_pan.add(m_lblGraph);
		m_pan.moveToFront(m_lblGraph);

		m_lblGraphInternal = new JLabel();
		m_lblGraphInternal.setBounds(190, 76, 580, 154);
		m_lblGraphInternal.setIcon(new ImageIcon(m_imgGraphInternal.getBufferedImage()));
		m_lblGraphInternal.setVisible(true);
		m_pan.add(m_lblGraphInternal);
		m_pan.moveToFront(m_lblGraphInternal);

		m_imgBackground.darkenByAlphaMask(Opbm.locateFile("graph_internal.png"), 188, 74, 188+584, 74+159);
	}

	/**
	 * 13,255	- this system	- 152x40
	 */
	private void extractThisSystem()
	{
		Font font;
		Rectangle rect;

		m_imgThisSystemNeutral		= m_imgBackground.extractImage(13, 255, 13+152, 255+40);
		m_imgThisSystemNeutralMask	= new AlphaImage(Opbm.locateFile("this_system.png"));
		m_imgThisSystemOver			= new AlphaImage(m_imgThisSystemNeutral);
		m_imgThisSystemOverMask		= new AlphaImage(Opbm.locateFile("this_system_over.png"));

		m_imgThisSystemNeutral.applyAlphaMask(m_imgThisSystemNeutralMask);
		m_imgThisSystemNeutral.scaleContrast(0.75);
		m_imgThisSystemNeutral.scaleBrightness(0.75);
		m_imgThisSystemNeutral.recolorize(AlphaImage.makeARGB(255, 255, 227, 174)/*AlphaImage.makeARGB(255, 128, 255, 128)*/);
		font	= new Font("Calibri", Font.BOLD, 20);
		rect	= new Rectangle(20, 0, m_imgThisSystemNeutral.getWidth() - 20, (int)(m_imgThisSystemNeutral.getHeight() * 0.65));
		m_imgThisSystemNeutral.drawStringInRectangle(rect, "This System", new Color(255,255,255), font, 255, true);

		m_imgThisSystemOver.applyAlphaMask(m_imgThisSystemNeutralMask);
		m_imgThisSystemOver.scaleContrast(0.75);
		m_imgThisSystemOver.scaleBrightness(0.75);
		m_imgThisSystemOver.recolorize(AlphaImage.makeARGB(255, 128, 255, 128));
		m_imgThisSystemOver.copyByAlphaMask(m_imgThisSystemNeutral, m_imgThisSystemOverMask);
		m_imgThisSystemOver.drawStringInRectangle(rect, "This System", new Color(255,255,255), font, 255, true);

		m_lblThisSystem	= new JLabelHotTrack(this);
		m_lblThisSystem.setType(JLabelHotTrack.CLICK_ACTION);
		m_lblThisSystem.setBounds(13, 255, 152, 40);
		m_lblThisSystem.setIdentifier("this_system");

		m_lblThisSystemNeutral = new JLabel();
// REMEMBER need to remove this when the control does something
m_imgThisSystemNeutral.grayscale();
		m_lblThisSystemNeutral.setIcon(new ImageIcon(m_imgThisSystemNeutral.getBufferedImage()));
		m_lblThisSystemNeutral.setVisible(true);
		m_pan.add(m_lblThisSystemNeutral);
		m_pan.moveToFront(m_lblThisSystemNeutral);

		m_lblThisSystemOver = new JLabel();
// REMEMBER need to remove this when the control does something
m_imgThisSystemOver.grayscale();
		m_lblThisSystemOver.setIcon(new ImageIcon(m_imgThisSystemOver.getBufferedImage()));
		m_lblThisSystemOver.setVisible(true);
		m_pan.add(m_lblThisSystemOver);
		m_pan.moveToFront(m_lblThisSystemOver);

		m_lblThisSystem.setUnselectedNeutral(m_lblThisSystemNeutral);
		m_lblThisSystem.setUnselectedOver(m_lblThisSystemOver);
		m_lblThisSystem.renderHotTrackChange();

		// Darken the background behind this control
		m_imgBackground.darkenByAlphaMask(Opbm.locateFile("this_system_background.png"), 13, 255, 13+152, 255+40);
	}

	/**
	 * 13,296	- ref system	- 152x40
	 */
	private void extractReferenceSystem()
	{
		Font font;
		Rectangle rect;

		m_imgRefSystemNeutral		= m_imgBackground.extractImage(13, 296, 13+152, 296+40);
		m_imgRefSystemNeutralMask	= new AlphaImage(Opbm.locateFile("ref_system.png"));
		m_imgRefSystemOver			= new AlphaImage(m_imgRefSystemNeutral);
		m_imgRefSystemOverMask		= new AlphaImage(Opbm.locateFile("ref_system_over.png"));

		m_imgRefSystemNeutral.applyAlphaMask(m_imgRefSystemNeutralMask);
		m_imgRefSystemNeutral.scaleContrast(0.75);
		m_imgRefSystemNeutral.scaleBrightness(0.75);
		m_imgRefSystemNeutral.recolorize(AlphaImage.makeARGB(255, 255, 227, 174)/*AlphaImage.makeARGB(255, 128, 128, 255)*/);
		font	= new Font("Calibri", Font.BOLD, 20);
		rect	= new Rectangle(20, 0, m_imgRefSystemNeutral.getWidth() - 20, (int)(m_imgRefSystemNeutral.getHeight() * 0.65));
		m_imgRefSystemNeutral.drawStringInRectangle(rect, "Baseline", new Color(255,255,255), font, 255, true);

		m_imgRefSystemOver.applyAlphaMask(m_imgRefSystemNeutralMask);
		m_imgRefSystemOver.scaleContrast(0.75);
		m_imgRefSystemOver.scaleBrightness(0.75);
		m_imgRefSystemOver.recolorize(AlphaImage.makeARGB(255, 128, 128, 255));
		m_imgRefSystemOver.copyByAlphaMask(m_imgRefSystemNeutral, m_imgRefSystemOverMask);
		m_imgRefSystemOver.drawStringInRectangle(rect, "Baseline", new Color(255,255,255), font, 255, true);

		m_lblRefSystem	= new JLabelHotTrack(this);
		m_lblRefSystem.setType(JLabelHotTrack.CLICK_ACTION);
		m_lblRefSystem.setBounds(13, 296, 152, 40);
		m_lblRefSystem.setIdentifier("ref_system");

		m_lblRefSystemNeutral = new JLabel();
// REMEMBER need to remove this when the control does something
m_imgRefSystemNeutral.grayscale();
		m_lblRefSystemNeutral.setIcon(new ImageIcon(m_imgRefSystemNeutral.getBufferedImage()));
		m_lblRefSystemNeutral.setVisible(true);
		m_pan.add(m_lblRefSystemNeutral);
		m_pan.moveToFront(m_lblRefSystemNeutral);

		m_lblRefSystemOver = new JLabel();
// REMEMBER need to remove this when the control does something
m_imgRefSystemOver.grayscale();
		m_lblRefSystemOver.setIcon(new ImageIcon(m_imgRefSystemOver.getBufferedImage()));
		m_lblRefSystemOver.setVisible(true);
		m_pan.add(m_lblRefSystemOver);
		m_pan.moveToFront(m_lblRefSystemOver);

		m_lblRefSystem.setUnselectedNeutral(m_lblRefSystemNeutral);
		m_lblRefSystem.setUnselectedOver(m_lblRefSystemOver);
		m_lblRefSystem.renderHotTrackChange();

		// Darken the background behind this control
		m_imgBackground.darkenByAlphaMask(Opbm.locateFile("ref_system_background.png"), 13, 296, 13+152, 296+40);
	}

	/**
	 * 167,255	- filter		- 618x81
	 */
	private void extractFilter()
	{
		m_imgFilterBackground	= m_imgBackground.extractImage(167, 255, 167+618, 255+81);
		m_imgFilterMask			= new AlphaImage(Opbm.locateFile("filter.png"));
		m_imgBackground.darkenByAlphaMask(Opbm.locateFile("filter_background.png"), 167, 255, 167+618, 255+81);
		m_imgFilterBackground.applyAlphaMask(m_imgFilterMask);
		m_imgFilterBackground.scaleContrast(0.75);
		m_imgFilterBackground.scaleBrightness(0.75);
		m_imgFilterBackground.recolorize(AlphaImage.makeARGB(255, 255, 255, 128));
		m_lblFilter = new JLabel();
		m_lblFilter.setBounds(167, 255, 618, 81);
		m_lblFilter.setIcon(new ImageIcon(m_imgFilterBackground.getBufferedImage()));
		m_lblFilter.setVisible(true);
		m_pan.add(m_lblFilter);
		m_pan.moveToFront(m_lblFilter);
	}

	public void setAllEntriesUnselectedForNavigation(ResultsViewerLine rvl)
	{
		if (rvl != null)
		{
			// Set this one
			rvl.setJustSelectedForNavigation(false);
			// Set its children (if any)
			if (rvl.getChild() != null)
				setAllEntriesUnselectedForNavigation(rvl.getChild());
			// Set its siblings (if any)
			if (rvl.getNext() != null)
				setAllEntriesUnselectedForNavigation(rvl.getNext());
		}
	}

	public void setAllEntriesUnselectedForGraph(ResultsViewerLine rvl)
	{
		if (rvl != null)
		{
			// Set this one
			rvl.setJustSelectedForGraph(false);
			// Set its children (if any)
			if (rvl.getChild() != null)
				setAllEntriesUnselectedForGraph(rvl.getChild());
			// Set its siblings (if any)
			if (rvl.getNext() != null)
				setAllEntriesUnselectedForGraph(rvl.getNext());
		}
	}

	public void moveUpOneLine()
	{
		if (m_rootRVL.moveUp(m_navLine))
			renderBottomAndGraph();
	}

	public void moveDownOneLine()
	{
		if (m_rootRVL.moveDown(m_navLine))
			renderBottomAndGraph();
	}

	public void moveLeftOneColumn()
	{
		if (m_navLine != null)
		{
			if (m_navLine.moveLeft())
				renderBottomAndGraph();
		} else {
			// Reset to the top
			setAllEntriesUnselectedForGraph(m_rootRVL);
			m_navLine = m_rootRVL;
		}
	}

	public void moveRightOneColumn()
	{
		if (m_navLine != null)
		{
			if (m_navLine.moveRight())
				renderBottomAndGraph();
		} else {
			// Reset to the top
			setAllEntriesUnselectedForNavigation(m_rootRVL);
			m_navLine = m_rootRVL;
		}
	}

	public void expandOrCollapse()
	{
		if (m_navLine != null)
		{
			m_navLine.toggleExpandedState();
			renderBottomAndGraph();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			moveUpOneLine();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			moveDownOneLine();
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			moveLeftOneColumn();
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			moveRightOneColumn();
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			m_frame.dispose();
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			expandOrCollapse();
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			Opbm.setBreakpointsEnabled(!Opbm.areBreakpointsEnabled());
		} else if (e.getKeyCode() == KeyEvent.VK_I) {
			getNavLineCurrentChild().toggleIgnored();
			recomputeScores();
			renderScoreboard();
			renderBottomAndGraph();
		}
		// Reset/hide the input
		m_txtInput.selectAll();
		m_txtInput.cut();
		m_txtInput.setText("");
		m_txtInput.requestFocusInWindow();
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		boolean scrollingUp;

		// See which way we're scrolling
		scrollingUp = ((e.getWheelRotation() < 0) ? true : false);

		// Scroll as many times as the wheel was moved
		if (scrollingUp)
		{	// Scrolling up
			m_rootRVL.moveUp(m_navLine);
		} else {
			m_rootRVL.moveDown(m_navLine);
		}
		renderBottomAndGraph();
	}

	@Override
	public void componentResized(ComponentEvent e) {
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

	public void setGraphLeg(ResultsViewerLine leg)
	{
		m_graphLine = leg;
	}

	public void setNavigationLeg(ResultsViewerLine leg)
	{
		m_navLine = leg;

	}

	public ResultsViewerLine getGraphLine()
	{
		return(m_graphLine);
	}

	public ResultsViewerLine getGraphLineCurrentChild()
	{
		int count;
		ResultsViewerLine child;

		if (m_graphLine.getSelectedTab() == -1)
			return(m_graphLine);

		child	= m_graphLine.getChild();
		count	= 0;
		while (child != null)
		{
			if (count == m_graphLine.getSelectedTab())
				return(child);

			// Move to next sibling
			child = child.getNext();
			++count;
		}
		// If we get here, there's some error, just return the graph line
		return(m_graphLine);
	}

	public ResultsViewerLine getNavLine()
	{
		return(m_navLine);
	}

	public ResultsViewerLine getNavLineCurrentChild()
	{
		int count;
		ResultsViewerLine child;

		if (m_navLine.getSelectedTab() == -1)
			return(m_navLine);

		child	= m_navLine.getChild();
		count	= 0;
		while (child != null)
		{
			if (count == m_navLine.getSelectedTab())
				return(child);

			// Move to next sibling
			child = child.getNext();
			++count;
		}
		// If we get here, there's some error, just return the nav line
		return(m_navLine);
	}

	public int getFailureCount()
	{
		return(m_failureCount);
	}

	/**
	 * Remove any previously generated images from the target lines
	 * @param rvl
	 */
	public void resetAllSubcomponentRenderImagesAndCounts(ResultsViewerLine rvl)
	{
		while (rvl != null)
		{
			rvl.setSubcomponentImage(null);
			rvl.setRenderedLevelCount(0);
			resetAllSubcomponentRenderImagesAndCounts(rvl.getChild());
			rvl = rvl.getNext();
		}
	}

	public void clickActionCallback(JLabelHotTrack jlht)
	{
		if (jlht.getIdentifier().equalsIgnoreCase("word"))
		{	// Word is clicked

		} else if (jlht.getIdentifier().equalsIgnoreCase("excel")) {
			// Excel is clicked

		} else if (jlht.getIdentifier().equalsIgnoreCase("notepad")) {
			// Notepad is clicked
// REMEMBER need to restore this functionality once the output is coded to be generated
//			SystemDataNotepad sdn = new SystemDataNotepad(m_opbm, this);

		} else if (jlht.getIdentifier().equalsIgnoreCase("this_system")) {
			// This System is clicked
// REMEMBER need to restore this functionality once the output is coded to be generated
//			createSystemData();

		} else if (jlht.getIdentifier().equalsIgnoreCase("ref_system")) {
			// Ref System is clicked
// REMEMBER need to restore this functionality once the output is coded to be generated
//			createSystemData();

		}
	}

	public ResultsViewerLine getRootRVL()	{	return(m_rootRVL);		}
	public Tuple getFilterTags()			{	return(m_filterTags);	}

	private Opbm						m_opbm;				// The parent
	private DroppableFrame				m_frame;			// The physical window
	private JLayeredPane				m_pan;
	private int							m_failureCount;
	private int							m_testCount;
	private int							m_untestedCount;
	private JLabel						m_lblBackground;	// The background image
	private AlphaImage					m_imgBackground;
	private AlphaImage					m_imgBottom;
	private AlphaImage					m_imgScoreboard;
	private AlphaImage					m_imgScoreboardInternal1Mask;
	private AlphaImage					m_imgScoreboardInternal2Mask;
	private AlphaImage					m_imgGraph;
	private AlphaImage					m_imgGraphInternal;
	private AlphaImage					m_imgGraphInternalMask;
	private AlphaImage					m_imgThisSystemNeutral;
	private AlphaImage					m_imgThisSystemOver;
	private AlphaImage					m_imgThisSystemNeutralMask;
	private AlphaImage					m_imgThisSystemOverMask;
	private AlphaImage					m_imgRefSystemNeutral;
	private AlphaImage					m_imgRefSystemNeutralMask;
	private AlphaImage					m_imgRefSystemOver;
	private AlphaImage					m_imgRefSystemOverMask;
	private AlphaImage					m_imgFilterBackground;
	private AlphaImage					m_imgFilterMask;
	private AlphaImage					m_imgGraphMask;
	private AlphaImage					m_imgScoreboardInternal;
	private Thread						m_bottomThread;		// Renders the bottom section in the background
	private Thread						m_graphThread;		// Renders graphs in the background

	private JLabel						m_lblScoreboard;
	private JLabel						m_lblGraph;
	private JLabel						m_lblGraphInternal;
	private JLabelHotTrack				m_lblThisSystem;
	private JLabel						m_lblThisSystemNeutral;
	private JLabel						m_lblThisSystemOver;
	private JLabelHotTrack				m_lblRefSystem;
	private JLabel						m_lblRefSystemNeutral;
	private JLabel						m_lblRefSystemOver;
	private JLabel						m_lblFilter;
	private JLabel						m_lblBottom;
	private JTextField					m_txtInput;

	private ResultsViewerLine			m_graphLine;		// The selected graph leg (if any) for rendering the graph
	private ResultsViewerLine			m_navLine;			// The selected navigation leg (if any) for rendering the graph
	private ResultsViewerLine			m_rootRVL;			// The root results viewer line
	private ResultsViewerLine			m_highlighted;		// Current highlight
	private Tuple						m_filterTags;

	private int							m_width;
	private int							m_height;
	private int							m_actual_width;
	private int							m_actual_height;
}
