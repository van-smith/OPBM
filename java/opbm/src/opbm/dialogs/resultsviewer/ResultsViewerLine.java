/*
 * OPBM - Office Productivity Benchmark
 *
 * This class supports displaying results viewer lines (the bottom section of
 * the results viewer window).  It parses the results.xml file input, stores
 * all related variables, is used to determine if it meets the filter critiera,
 * etc.
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

import opbm.common.Tuple;
import opbm.common.Utils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import opbm.Opbm;
import opbm.common.Xml;

/**
 * Refer to the raw_resources directory for a simple example of how each line
 * should appear.
 *
 * Each line consists of several parts:
 *		1) namebox
 *		2) scorebox
 *		3) tabs (one for each child relative to its level)
 *
 */
public final class ResultsViewerLine
{
	public ResultsViewerLine(Opbm				opbm,
							 ResultsViewer		parent,
							 JLayeredPane		pan,
							 int				level,
							 Xml				node)
	{
		int height;
		String score;

		m_opbm					= opbm;
		m_parent				= parent;
		m_parentPane			= pan;
		m_next					= null;
		m_child					= null;
		m_level					= level;
		m_node					= node;
		m_filterTags			= new ArrayList<String>(0);
		m_leftmostTab			= 0;
		m_selectedTab			= -1;		// -1 indicates the row itself is selected, not something else
		m_ignored				= false;

		m_rectNamebox			= new Rectangle();
		m_rectScorebox			= new Rectangle();
		m_rectTabspace			= new Rectangle();

		Utils.extractCommaItems(m_filterTags, node.getAttributeOrChild("tags"));
		m_score	= Utils.doubleValueOf(node.getAttributeOrChild("score"), 0.0);
		m_tested	= Utils.interpretBooleanAsYesNo(node.getAttributeOrChild("tested"), true).equalsIgnoreCase("Yes");
		m_success	= node.getAttributeOrChild("status").equals("success");
		m_name		= m_opbm.getMacroMaster().parseMacros(node.getAttributeOrChild("name"));
		m_shortname	= m_opbm.getMacroMaster().parseMacros(node.getAttributeOrChild("shortname"));
		m_time		= Utils.doubleValueOf(node.getAttributeOrChild("time"), -1.0);
		m_instances	= Utils.integerValueOf(node.getAttributeOrChild("instances"), 1);

		// Grab min/max/avg/geo/cv scores and times
		m_minScore	= Utils.doubleValueOf(node.getAttributeOrChild("minScore"), -1.0);
		m_maxScore	= Utils.doubleValueOf(node.getAttributeOrChild("maxScore"), -1.0);
		m_avgScore	= Utils.doubleValueOf(node.getAttributeOrChild("avgScore"), -1.0);
		m_geoScore	= Utils.doubleValueOf(node.getAttributeOrChild("geoScore"), -1.0);
		m_cvScore	= Utils.doubleValueOf(node.getAttributeOrChild("cvScore"), -1.0);
		m_minTime	= Utils.doubleValueOf(node.getAttributeOrChild("minTime"), -1.0);
		m_maxTime	= Utils.doubleValueOf(node.getAttributeOrChild("maxTime"), -1.0);
		m_avgTime	= Utils.doubleValueOf(node.getAttributeOrChild("avgTime"), -1.0);
		m_geoTime	= Utils.doubleValueOf(node.getAttributeOrChild("geoTime"), -1.0);
		m_cvTime	= Utils.doubleValueOf(node.getAttributeOrChild("cvTime"), -1.0);

		// The tabs are comprised of two portions, the upper and lower
		height = _BOX_HEIGHT;

		// Add the constant elements to this pane
		// Namebox
		m_nameboxLabel = new JLabel(" " + Utils.replicate(level, " ") + (level == _WORKLET ? "   " : "") + m_name);
		m_nameboxLabel.setBounds(195, 0, getNameboxWidth(), height);
		m_nameboxLabel.setBackground(getBackcolorForBoxes(level));
		m_nameboxLabel.setForeground(getForecolorForBoxes(level));
		m_nameboxLabel.setOpaque(true);
		m_nameboxLabel.setFont(new Font("Calibri", Font.PLAIN, Math.min(22, 24 - (level * 2))));
		m_nameboxLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		m_nameboxLabel.addMouseListener(parent);
		m_nameboxLabel.addMouseWheelListener(parent);
		m_nameboxLabel.addKeyListener(parent);
		m_nameboxLabel.setVisible(false);
		m_parentPane.add(m_nameboxLabel);
		m_parentPane.moveToFront(m_nameboxLabel);
	}

	/**
	 * For worklets, called to load individual run data stored beneath the
	 * worklet entry.  These entries are used to display content in tabs.
	 * @param worklet the worklet being added for
	 * @param run the first run for the worklet
	 */
	public void loadRuns(Xml	worklet,
						 Xml	run)
	{
		int width, height;
		JLabel lower;
		String name, rawName, upper;
		double time, score;

		// Create the fonts and color we'll use
		height		= _BOX_HEIGHT;
		width		= getTabWidth();

		// Create the tuple used for all run data
		//		first	= name
		//		second	= Upper Label (JLabel)
		//		third	= Lower Label (JLabel)
		//		fourth	= time (Double)
		//		fifth	= score (Double)
		//		sixth	= count (Integer)
		m_runList = new Tuple();

		// Process through all of the runs
		while (run != null)
		{
			if (run.getName().toLowerCase().startsWith("run"))
			{	// Load this run entry
				rawName	= run.getName();
				name	= rawName.substring(0, 3) + " " + rawName.substring(3);
				time	= Utils.doubleValueOf(run.getAttributeOrChild("time"), -1.0);
				score	= Utils.doubleValueOf(run.getAttributeOrChild("score"), -1.0);
				upper	= Utils.toProper(name);
				lower	= makeLower(width, height, false);	// text will be set at rendering, either score or time
				// Append the entry to the tuple
				m_runList.add(worklet.getAttributeOrChild("name"), upper, lower, Double.valueOf(time), Double.valueOf(score), Integer.valueOf(1));
			}

			// Move to next sibling
			run = run.getNext();
		}
		// When we get here, all run data items are loaded for the worklet
		// CV scores were computed during the "create results.xml" phase, which occurs in BenchmarkManifestResults.sumUpSourceDataByAtom() method
		// Note:  Additional rolled-up CV values are computed below in calculateRolledUpRunTabs() for higher-than-worklet-level results.
		// Append the CV
		time	= Utils.doubleValueOf(worklet.getAttributeOrChild("cvTime"), -1.0);
		score	= Utils.doubleValueOf(worklet.getAttributeOrChild("cvScore"), -1.0);
		upper	= "CV";
		lower	= makeLower(width, height, false);
		// Append the entry to the tuple
		m_runList.add("Coefficient of Variation", upper, lower, Double.valueOf(time), Double.valueOf(score), Integer.valueOf(1));

		// Append the average entries
		time	= Utils.doubleValueOf(worklet.getAttributeOrChild("avgTime"), -1.0);
		score	= Utils.doubleValueOf(worklet.getAttributeOrChild("avgScore"), -1.0);
		upper	= "Average";
		lower	= makeLower(width, height, true);
		// Append the entry to the tuple
		m_runList.add("Average", upper, lower, Double.valueOf(time), Double.valueOf(score), Integer.valueOf(1));
	}

	/**
	 * Since only worklets have run data loaded, tabs must be manually created
	 * for each higher-level entry once all of its children have rolled up.  So,
	 * for each atom, molecule, scenario, suite and root, we create a summation
	 * of the child times, and a geomean of the child scores, for run1, run2,
	 * run3, cv and average.
	 */
	public void calculateRolledUpRunTabs()
	{
		int i, j, iteration, count, entry, maxRuns, left, top, width, height;
		ResultsViewerLine child;
		String name, upper;
		double time, score, power, geoTime, geoScore, totTime, totScore, avgTime, avgScore, cvTime, cvScore, cvTimeSum, cvScoreSum;
		Tuple tup;
		JLabel lower;

		// Create this entry
		// See definition in loadRuns()
		//		first	= name
		//		second	= Upper Label (JLabel)
		//		third	= Lower Label (JLabel)
		//		fourth	= time (Double)
		//		fifth	= score (Double)
		//		sixth	= count (Integer)
		// Only used here:
		//		seventh	= all source scores at each run level (Double, used for computing geomean)
		m_runList = new Tuple();

		// Find out how many children there are
		count	= 0;
		maxRuns	= 0;
		child	= m_child;
		while (child != null)
		{	// Count each child, and find out how many run entries it has
			if (child.wasSuccessful())
			{	// We only include successful children in our scores
				++count;
				if (child.m_runList.size() - 2 > maxRuns)	// We back off 2 for the CV and Average entries
					maxRuns = child.m_runList.size() - 2;
			}
			// Move to the next sibling
			child = child.getNext();
		}
		if (maxRuns == 0)
		{	// Nothing was recorded for run data (due to an error in the script)
			return;
		}

		// Create the template tuple entries for summation
		// Create the fonts and color we'll use
		height		= _BOX_HEIGHT;
		width		= getTabWidth();
		for (i = 0; i < maxRuns; i++)
		{
			name = "Run " + Integer.toString(i + 1);	// Human-readable name, base-1

			// Build our tuple of child runs
			tup = new Tuple();
			for (j = 0; j < count; j++)
				tup.add("run" + Integer.toString(i) + ".child" + Integer.toString(j));	// identifier, base-0

			m_runList.add(m_name,										// first, identifier / name
						  name,											// second, upper label
						  makeLower(width, height, false),				// third, lower label
						  Double.valueOf(0.0),							// fourth, time
						  Double.valueOf(0.0),							// fifth, score
						  Integer.valueOf(0),							// sixth, count
						  tup);											// seventh, all scores for this run level (used for computing geomean)
		}

		// Total up each child run entry times, and store the scores for processing
		child	= m_child;
		entry	= 0;
		while (child != null)
		{	// Add the totals for this child
			if (child.wasSuccessful())
			{	// We only include successful children in our scores
				for (i = 0; i < child.m_runList.size() - 2; i++)
				{
					tup = (Tuple)m_runList.getSeventh(i);
					m_runList.addFourthDouble(i, (Double)child.m_runList.getFourth(i));	// time
					tup.setSecond(entry, (Double)child.m_runList.getFifth(i));			// score
					m_runList.addSixthInteger(i, 1);									// count
				}
			}

			// Move to the next sibling
			child = child.getNext();
			++entry;
		}

		// Process the geomean score for each run level
		for (i = 0; i < m_runList.size(); i++)
		{	// i iterates through every run
			tup			= (Tuple)m_runList.getSeventh(i);		// tuple of runN.childN entries

			// Find out how many non-null values there are (entries with valid times)
			count = 0;
			for (j = 0; j < tup.size(); j++)
				count += tup.getSecond(j) == null ? 0 : 1;

			power		= 1.0 / (double)count;
			geoScore	= 0.0;
			iteration	= 0;
			for (j = 0; iteration < count; j++)
			{	// j iterates through every child's entry
				if (tup.getSecond(j) != null)
				{
					if (iteration == 0)
						geoScore	= Math.pow((Double)tup.getSecond(j), power);
					else
						geoScore	*= Math.pow((Double)tup.getSecond(j), power);

					++iteration;
				}
			}
			// When we get here, this run's geoScore is computed
			m_runList.setFifth(i, Double.valueOf(geoScore));
		}
		// We have our geomean

		// Compute the average score and times for the whole thing
		totTime		= 0.0;
		totScore	= 0.0;
		geoTime		= 0.0;
		geoScore	= 0.0;
		power		= 1.0 / (double)m_runList.size();
		count		= m_runList.size();
		for (i = 0; i < count; i++)
		{
			time		= (Double)m_runList.getFourth(i);		// time
			score		= (Double)m_runList.getFifth(i);		// score
			totTime		+= time;
			totScore	+= score;
			if (i == 0)
			{
				geoTime		= Math.pow(time, power);
				geoScore	= Math.pow(score, power);
			} else {
				geoTime		*= Math.pow(time, power);
				geoScore	*= Math.pow(score, power);
			}
		}
		avgTime		= totTime	/ (double)(Math.max(count, 1));
		avgScore	= totScore	/ (double)(Math.max(count, 1));

		// Compute the score and time sum of deviations
		cvTimeSum	= 0.0f;
		cvScoreSum	= 0.0f;
		for (i = 0; i < count; i++)
		{	// Add up each to get the population's standard deviation
			time		= (Double)m_runList.getFourth(i);		// time
			score		= (Double)m_runList.getFifth(i);		// score
			cvTimeSum	+= Math.pow(time	- avgTime,	2.0f);
			cvScoreSum	+= Math.pow(score	- avgScore,	2.0f);
		}
		// Compute the standard deviation
		cvTime	= Math.sqrt(cvTimeSum  / (double)(Math.max(count - 1, 0.0000001)));
		cvScore	= Math.sqrt(cvScoreSum / (double)(Math.max(count - 1, 1.0)));
		// Divide by abs(mean) to get CV
		cvTime	= cvTime	/ Math.max(Math.abs(avgTime),  0.0000001);
		cvScore	= cvScore	/ Math.max(Math.abs(avgScore), 1.0);
		// Right now, these variables are defined:
		//		totTime		totScore
		//		geoTime		geoScore
		//		avgTime		avgScore
		//		cvTime		cvScore

		// Append the CV
		upper	= "CV";
		lower	= makeLower(width, height, false);
		// Append the entry to the tuple
		m_runList.add("Coefficient of Variation", upper, lower, Double.valueOf(cvTime), Double.valueOf(cvScore), Integer.valueOf(1));

		// Append the average entries
		upper	= "Average";
		lower	= makeLower(width, height, true);
		// Append the entry to the tuple
		m_runList.add("Average", upper, lower, Double.valueOf(avgTime), Double.valueOf(avgScore), Integer.valueOf(1));

		if (m_level == _SUMMARY)
		{	// Do the headers
			left	= m_nameboxLabel.getX() + m_nameboxLabel.getWidth();
			top		= m_parent.getRunHeaderY();
			for (i = 0; i < m_runList.size(); i++)
			{
				lower = makeLower(width, height, false);
				lower.setText((String)m_runList.getSecond(i));
				lower.setBounds(left, top, width, height);
				lower.setVisible(true);
				left += width;
			}
		}
	}

	/**
	 * Builds the lower textbox.  Originally, the results viewer had an upper
	 * textbox above each lower item, which was its moniker or description.
	 * Later versions added simple headers, and the upper textbox was removed.
	 * @param width how wide
	 * @param height how high
	 * @param isTotalTab if it's a total tab, it has a different border color
	 * @return the new label which was created to these specs
	 */
	public JLabel makeLower(int			width,
							int			height,
							boolean		isTotalTab)
	{
		JLabel lower;

		lower	= new JLabel();
		lower.setBounds(0, 0, width, height);
		lower.setBackground(getBackcolorForBoxes(m_level));
		lower.setForeground(getForecolorForBoxes(m_level));
		lower.setHorizontalAlignment(JLabel.CENTER);
		lower.setOpaque(true);
		lower.setBorder(BorderFactory.createLineBorder(isTotalTab ? Color.RED : Color.BLACK, 1));
		lower.setFont(new Font("Calibri", Font.PLAIN, 16));
		lower.setVisible(false);
		lower.addMouseListener(m_parent);
		lower.addMouseWheelListener(m_parent);
		lower.addKeyListener(m_parent);
		m_parentPane.add(lower);
		m_parentPane.moveToFront(lower);
		return(lower);
	}

	/**
	 * Each level is rendered in a different color.
	 * @param level the level to return
	 * @return the forecolor for that level
	 */
	public Color getBackcolorForBoxes(int level)
	{
		switch (level)
		{
			case 0:		// Summary level, white
			default:	// not specified, so use white
				return(new Color(255, 255, 255));
			case 1:		// Suite level, yellow
				return(new Color(197, 217, 241));
			case 2:		// Scenario level, red
				return(new Color(140, 179, 225));
			case 3:		// Molecule level, green
				return(new Color(82, 140, 212));
			case 4:		// Atom level, blue
				return(new Color(21, 53, 91));
			case 5:		// Worklet level, cyan
				return(new Color(59, 175, 255));
		}
	}

	/**
	 * Each level is rendered in a different color.
	 * @param level the level to return
	 * @return the backcolor for that level
	 */
	public Color getForecolorForBoxes(int level)
	{
		switch (level)
		{
			case 0:		// Summary level, white
			default:	// not specified, so use white
				return(new Color(0,0,0));
			case 1:		// Suite level, yellow
				return(new Color(0,0,0));
			case 2:		// Scenario level, red
				return(new Color(0,0,0));
			case 3:		// Molecule level, green
				return(new Color(0,0,0));
			case 4:		// Atom level, blue
				return(new Color(255,255,255));
			case 5:		// Worklet level, cyan
				return(new Color(0,0,0));
		}
	}

	/**
	 * Physically makes visible or invisible the RVLs, including their tabs.
	 * Updates the text for the most recently computed score.
	 */
	public void render(int mode)
	{
		int i;

		// If we're visibile at this point, render it
		if (m_visible)
		{	// Arrange the child sub-components
			if (!m_success)
			{	// There's an error
				m_nameboxLabel.setBackground(Color.RED);
				m_nameboxLabel.setForeground(Color.YELLOW);
			} else {
				m_nameboxLabel.setBackground(getBackcolorForBoxes(m_level));
				m_nameboxLabel.setForeground(getForecolorForBoxes(m_level));
			}

			if (!m_nameboxLabel.isVisible())
				m_nameboxLabel.setVisible(true);		// Show it

			if (m_success && !m_runList.isEmpty())
				renderTabs(mode);						// Show the tabs appropriately when not in error

		} else {
			if (m_nameboxLabel.isVisible())
				m_nameboxLabel.setVisible(false);

			// Hide all the tabs
			for (i = 0; i < m_runList.size(); i++)
			{	// The tuple is defined in loadRun() or calculateRolledUpRunTabs() above
				((JLabel)m_runList.getThird(i)).setVisible(false);		// Lower
			}
		}
	}

	/**
	 * Renders the tabs based on their current position
	 */
	public void renderTabs(int mode)
	{
		int i, left, top;
		double cvScore, cvTime;
		String text;
		Rectangle rect;
		JLabel lower;

		// Show all the tabs that should be shown
		left	= m_nameboxLabel.getX() + m_nameboxLabel.getWidth();
		top		= m_nameboxLabel.getY();
		for (i = 0; i < m_runList.size(); i++)
		{
			lower	= (JLabel)m_runList.getThird(i);
			if (i == m_runList.size() - 2)
			{	// CV
				// Refer to loadRuns() and calculateRolledUpRunTabs() above for info on the tuple assignments
				cvScore = (Double)m_runList.getFifth(i);
				cvTime	= (Double)m_runList.getFourth(i);
				if (mode == ResultsViewer._SCORES)
					text = Utils.removeLeadingZeros(Utils.doubleToString(cvScore * 100, 3, 1));
				else
					text = Utils.removeLeadingZeros(Utils.doubleToString(cvTime * 100, 3, 1));

				if ((mode == ResultsViewer._SCORES ? cvScore : cvTime) >= 0.03)
				{	// Make it red with yellow text
					lower.setBackground(Color.RED);
					lower.setForeground(Color.YELLOW);

				} else {
					// Normla text
					lower.setBackground(getBackcolorForBoxes(m_level));
					lower.setForeground(getForecolorForBoxes(m_level));
				}

			} else if (i == m_runList.size() - 1) {
				// Average total score
				if (mode == ResultsViewer._SCORES)
					text = Utils.removeLeadingZeros(Utils.doubleToString((Double)m_runList.getFifth(i), 3, 0));
				else
					text = Utils.removeLeadingZeroTimes(Utils.convertSecondsToHHMMSSff((Double)m_runList.getFourth(i)));

				lower.setBackground(getBackcolorForBoxes(m_level));
				lower.setForeground(getForecolorForBoxes(m_level));

			} else {
				// Regular run score
				if (mode == ResultsViewer._SCORES)
					text = Utils.removeLeadingZeros(Utils.doubleToString((Double)m_runList.getFifth(i), 3, 0));
				else
					text = Utils.removeLeadingZeroTimes(Utils.convertSecondsToHHMMSSff((Double)m_runList.getFourth(i)));

				lower.setBackground(getBackcolorForBoxes(m_level));
				lower.setForeground(getForecolorForBoxes(m_level));
			}
			lower.setText(text);

			// Make it visible
			rect = lower.getBounds();
			rect.y = top;
			rect.x = left;
			lower.setBounds(rect);
			lower.setVisible(true);

			// Move over for next item
			left += lower.getWidth();
		}
	}

	/**
	 * Returns the last tab score, which is the average score for all completed runs
	 * @return score
	 */
	public double getAverageTabScore()
	{
		double cvScore;

		// Refer to loadRuns() and calculateRolledUpRunTabs() above for info on the tuple assignments
		if (m_runList != null)
		{
			cvScore		= (Double)m_runList.getFifth(m_runList.size() - 1);
			//cvTime	= (Double)m_runList.getFourth(m_runList.size() - 1);
			return(cvScore);

		} else {
			// Some error
			return(-1.0);
		}
	}

	/**
	 * Returns the last tab time, which is the average time for all completed runs
	 * @return time
	 */
	public double getAverageTabTime()
	{
		double cvTime;

		// Refer to loadRuns() and calculateRolledUpRunTabs() above for info on the tuple assignments
		if (m_runList != null)
		{
			//cvScore	= (Double)m_runList.getFifth(m_runList.size() - 1);
			cvTime		= (Double)m_runList.getFourth(m_runList.size() - 1);
			return(cvTime);

		} else {
			// Some error
			return(-1.0);
		}
	}

	public double getHighestChildScore(ResultsViewerLine	rvl,
									   double				value)
	{
		while (rvl != null)
		{
			if (!rvl.isIgnored())
			{
				if (rvl.getScore() > value)
					value = rvl.getScore();
			}

			value = getHighestChildScore(rvl.getChild(), value);
			rvl = rvl.getNext();
		}
		return(value);
	}

	public double getLowestChildScore(ResultsViewerLine		rvl,
									  double				value)
	{
		while (rvl != null)
		{
			if (!rvl.isIgnored())
			{
				if (rvl.getScore() < value)
					value = rvl.getScore();
			}

			value = getHighestChildScore(rvl.getChild(), value);
			rvl = rvl.getNext();
		}
		return(value);
	}

	public int countChildren()
	{
		int count;
		ResultsViewerLine child;

		count = 0;
		child = getChild();
		while (child != null)
		{
			++count;
			child = child.getNext();
		}
		return(count);
	}

	public int countChildrenExcludeIgnores()
	{
		int count;
		ResultsViewerLine child;

		count = 0;
		child = getChild();
		while (child != null)
		{
			if (!child.isIgnored())
				++count;

			child = child.getNext();
		}
		return(count);
	}

	public boolean hasThisChild(ResultsViewerLine searchingForChild)
	{
		ResultsViewerLine child;

		child = getChild();
		while (child != null)
		{
			if (!child.isIgnored() && child == searchingForChild)
				return(true);

			// Try the next sibling
			child = child.getNext();
		}
		// If we get here, it was not found, so it's not a child
		return(false);
	}

	/**
	 * Computes the geometric mean of the children's scores, times, etc., of
	 * all values, or those values which are included if a filter is specified
	 * summation of time for each child item.
	 * @param byFilter is a filter specified?
	 * @param filterTags tags that must match to be included in the filter
	 */
	public void computedChildScoresTimesEtc(boolean		byFilter,
											Tuple		filterTags)
	{
		double n, power;
		ResultsViewerLine child;

		if (m_level == _WORKLET)
		{	// There is nothing to sum at this level, just store its value
			m_computedScore		= m_score;
			m_computedTime		= m_time;
			m_computedSuccesses	= m_success ? 1 : 0;
			m_computedFailures	= m_success ? 0 : 1;
			return;
		}

		// Count the child items
		n		= 0.0;
		child	= m_child;

		// If there is no child, then we're at the furthest extremity and whatever is loaded for the score is the score
		if (child == null)
			return;

		// Find out how many child items there are which match the filter
		while (child != null)
		{
			if (!child.isIgnored())
				n += ((!byFilter || child.countIfTagMatch(filterTags)) ? 1.0 : 0.0);

			child = child.getNext();
		}
		power = 1.0 / Math.max(n, 1.0);

		// Perform the n_root(a1)... * n_root(an) computation
		// And sum up all times at this level
		m_computedScore		= 0.0;
		m_computedTime		= 0.0;
		m_computedSuccesses	= 0;
		m_computedFailures	= 0;
		child				= m_child;
		while (child != null)
		{
			if (!child.isIgnored() && (!byFilter || child.countIfTagMatch(filterTags)))
			{
				m_computedTime		+= child.getComputedTime();
				m_computedSuccesses	+= child.getComputedSuccesses();
				m_computedFailures	+= child.getComputedFailures();

				if (m_computedScore == 0.0)
					m_computedScore = Math.pow(child.getComputedScore(), power);
				else
					m_computedScore *= Math.pow(child.getComputedScore(), power);
			}

			// Move to the next entry
			child = child.getNext();
		}
	}

	public boolean countIfTagMatch(Tuple tags)
	{
		int i, j;
		boolean foundOne;

		foundOne = false;
		for (i = 0; i < tags.size(); i++)
		{
			if (((String)tags.getSecond(i)).equalsIgnoreCase("Yes"))
			{
				foundOne = true;
				for (j = 0; j < m_filterTags.size(); j++)
				{
					// This filter tag is active
					if (m_filterTags.get(j).equalsIgnoreCase(tags.getFirst(i)))
						return(true);	// This is a match
				}
			}
		}
		// If foundOne and we get here, it didn't match any of the filters, so it fails
		// If !foundOne and we get here, there are no filters, so it passes
		return(!foundOne);
	}

	public boolean moveLeft()
	{
		if (m_selectedTab > -1)
		{
			--m_selectedTab;

			if (m_selectedTab < m_leftmostTab)
				m_leftmostTab = Math.max(m_selectedTab, 0);

			if (m_selectedTab == -1)
				return(false);

			return(true);
		}
		return(false);
	}

	public boolean moveRight()
	{
		if (m_selectedTab + 1 < countChildren())
		{
			++m_selectedTab;

			if (m_selectedTab - m_leftmostTab >= 6)
				++m_leftmostTab;

			if (m_selectedTab == 0)
				return(false);

			return(true);
		}
		return(false);
	}

	public boolean doesNameboxContainThisPoint(JLabel	labelMousedOn,
											   Point	p)
	{
		if (labelMousedOn == m_nameboxLabel && m_rectNamebox.contains(p))
			return(true);

		return(false);
	}

	public ResultsViewerLine doesTabspaceContainThisPoint(JLabel	labelMousedOn,
														  Point		p)
	{
/*
		int i;
		ResultsViewerLine child;

		// Iterate through every subcomponent and see if it's a match
		for (i = 0; i < m_subcomponentRenderList.size(); i++)
		{
			child = m_subcomponentRenderList.get(i);
			if (child.m_tabLabelLower == labelMousedOn || child.m_tabLabelUpper == labelMousedOn)
			{	// It was a match on this entry
				// Go to this parent
				return(child);
			}
		}

 */
		// If we get here, it wasn't found here
		return(null);
	}

	public void toggleIgnored()
	{
		m_ignored = !m_ignored;
	}

	public boolean wasTested()
	{
		return(m_tested);
	}

	public boolean wasFilteredOut()
	{
		// If there's a filter that matches, then it's considered filtered out
		return(!countIfTagMatch(m_parent.getFilterTags()));
	}

	public boolean isIgnored()
	{
		// If it's ingored or wasn't tested, then it's considered ignored
		if (m_ignored || !m_tested)
			return(true);

		// If there's a filter, and it doesn't match, then it's considered ignored
		if (!countIfTagMatch(m_parent.getFilterTags()))
			return(true);

		// If we get here, it wasn't ignored
		return(false);
	}

	public void setNext(ResultsViewerLine next)					{	m_next	= next;				}
	public void setChild(ResultsViewerLine child)				{	m_child	= child;			}
	public void setSelectedTab(int value)						{	m_selectedTab = value;		}
	public void setLeftmostTab(int value)						{	m_leftmostTab = value;		}
	public void setRenderLevel(int level)						{	m_renderLevel = level;		}
	public void setAboutToBeVisible(boolean b)					{	m_isAboutToBeVisible = b;	}

	/**
	 * When being rendered, sets the top for m_pane so the visible items appear
	 * as they should in the visible portion of the window
	 * @param top coordinate relative to the top of the bottom pane (m_parentPane)
	 */
	public void setTop(int top)
	{
		Rectangle rect = m_nameboxLabel.getBounds();
		rect.y = top;
		m_nameboxLabel.setBounds(rect);
	}

	/**
	 * Sets the visible condition, and hides the pane if not visible.  Note:
	 * it does not show it, but only hides it
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		m_visible = visible;
		// Note:  render() needs to be called to properly make the entry visible or invisible, due to its child/sub-component requirements
	}

	public ResultsViewerLine getNext()							{	return(m_next);							}
	public ResultsViewerLine getChild()							{	return(m_child);						}
	public Xml getNode()										{	return(m_node);							}
	public double getScore()									{	return(m_score);						}
	public String getName()										{	return(m_name);							}
	public String getShortName()								{	return(m_shortname);					}
	public boolean wasSuccessful()								{	return(m_success);						}
	public int getLeftmostTab()									{	return(m_leftmostTab);					}
	public int getSelectedTab()									{	return(m_selectedTab);					}
	public int getRenderLevel()									{	return(m_renderLevel);					}
	public boolean isVisible()									{	return(true/*m_visible*/);				}
	public double getComputedTime()								{	return(m_computedTime);					}
	public double getComputedScore()							{	return(m_computedScore);				}
	public int getComputedSuccesses()							{	return(m_computedSuccesses);			}
	public int getComputedFailures()							{	return(m_computedFailures);				}
	public double getTime()										{	return(m_time);							}
	public int getLevel()										{	return(m_level);						}
	public boolean isAboutToBeVisible()							{	return(m_isAboutToBeVisible);			}
	public int getNameboxHeight()								{	return(m_nameboxLabel.getHeight() - 1);	}
	public static int getNameboxWidth()							{	return(355);							}
	public static int getTabWidth()								{	return(85);								}

	private Opbm							m_opbm;
	private ResultsViewer					m_parent;					// Parent class for the results viewer itself
	private JLayeredPane					m_parentPane;				// The parent pane we're adding to
	private ResultsViewerLine				m_next;						// Next line in the chain at this level
	private ResultsViewerLine				m_child;					// First child from this entry
	private int								m_level;					// 0=summary, 1=suite, 2=scenario, 3=molecule, 4=atom, 5=worklet
	private boolean							m_visible;					// Enabled or disabled at each render, determines whether or not this pane is visible (if not, it's either logically above or below the render list)
	private boolean							m_isAboutToBeVisible;		// Enabled or disabled at each render, determines whether or not this pane will be visible at the end of the current render or not
	private Xml								m_node;						// Root node for this entry
	private List<String>					m_filterTags;				// Filter for this entry/node
	private double							m_computedScore;			// Re-computed score for the (non-)filtered items at this level and below
	private double							m_computedTime;				// Re-computed time for the (non-)filtered items at this level and below
	private int								m_computedSuccesses;		// Re-computed time for the (non-)filtered items at this level and below
	private int								m_computedFailures;			// Re-computed time for the (non-)filtered items at this level and below

	private static int						m_leftmostTab;				// Number of the left-most tab that's visible on this line
	private static int						m_selectedTab;				// Number of the selected tab
	private String							m_name;						// Name specified for this level, as from results.xml
	private String							m_shortname;				// Short name specified for this level, as from results.xml
	private double							m_score;					// Exact score at this level, as from results.xml
	private double							m_time;						// Time of run in seconds
	private int								m_instances;				// Number of atom instances reported in run
	private double							m_minTime;					// maximum time observed for all instances run
	private double							m_maxTime;					// minimum time observed for all instances run
	private double							m_avgTime;					// average time observed for all instances run
	private double							m_geoTime;					// geometric mean time observed for all instances run
	private double							m_cvTime;					// coefficient of variation for time observed for all instances run
	private double							m_minScore;					// minimum score observed for all instances run
	private double							m_maxScore;					// maximum score observed for all instances run
	private double							m_avgScore;					// average score observed for all instances run
	private double							m_geoScore;					// geometric mean score observed for all instances run
	private double							m_cvScore;					// coefficient of variation for score observed for all instances run

	private JLabel							m_nameboxLabel;				// The label used to populate content with visibly within the class's m_pane

	// Sub-components are not referenced by themselves, but by their parent (their owner)
	// In this way, every ResultsViewerLine is rendered both as a primary item (namebox, scorebox), and as a tab (short name, score)
	private Tuple							m_runList;					// For worklets only, holds all runN data for each run (pass), along with the labels to render for each run, etc.
	private JLabel							m_tabLabelUpper;			// The label used to populate content with visibly within the class's m_pane
	private JLabel							m_tabLabelLower;			// The label used to populate content with visibly within the class's m_pane

	private boolean							m_ignored;					// Is this score ignored?
	private boolean							m_tested;					// Was this entry even tested?
	private boolean							m_success;					// Was this entry successful in completing the benchmark?
	private int								m_renderLevel;				// Set for descendants of the graph line when the nav line is different, used to determine which lines are to be physically rendered, and which lines are to be rolled up for display.  Value=0 render, value=1 sum up at first level, value=2 at second level, and so on.

	// Initially populated at instantiation, used for mouse operations, compares mouse click location to event
	private Rectangle						m_rectNamebox;
	private Rectangle						m_rectScorebox;
	private Rectangle						m_rectTabspace;

	// Constants to identify the supported levels by the results viewer
	public static final int					_SUMMARY	= 0;
	public static final int					_SUITE		= 1;
	public static final int					_SCENARIO	= 2;
	public static final int					_MOLECULE	= 3;
	public static final int					_ATOM		= 4;
	public static final int					_WORKLET	= 5;

	public static final int					_BOX_HEIGHT	= 22;
}
