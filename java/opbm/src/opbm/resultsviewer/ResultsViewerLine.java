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

import opbm.graphics.AnimateImageTask;
import opbm.graphics.AlphaImage;
import opbm.common.Tupel;
import opbm.common.Utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import opbm.Opbm;
import opbm.common.Xml;

/**
 * Refer to the raw_resources directory for a simple example of how each line
 * should appear.
 *
 * Each line consists of several parts:
 *		1) collapse
 *		2) namebox
 *		3) scorebox
 *		4) tabs
 *
 */
public final class ResultsViewerLine implements MouseListener, MouseWheelListener
{
	public ResultsViewerLine(ResultsViewer	parent,
							 JLayeredPane	pan,
							 JLabel			lblBottom,
							 AlphaImage		imgBackground,
							 JLabel			lblGraphInternal,
							 AlphaImage		imgGraphInternal,
							 AlphaImage		imgGraphMask,
							 int			level,
							 boolean		visible,
							 boolean		expanded,
							 boolean		selected,
							 Xml			node)
	{
		String score;

		m_parent				= parent;
		m_pan					= pan;
		m_lblBottom				= lblBottom;
		m_imgBackground			= imgBackground;
		m_lblGraphInternal		= lblGraphInternal;
		m_imgGraphInternal		= imgGraphInternal;
		m_imgGraphMask			= imgGraphMask;
		m_next					= null;
		m_child					= null;
		m_level					= level;
		m_lineExpanded			= expanded;
		setSelectedForGraph(selected);
		setSelectedForNavigation(selected);
		m_node					= node;
		m_filterTags			= new ArrayList<String>(0);
		m_leftmostTab			= 0;
		m_selectedTab			= -1;		// -1 indicates the row itself is selected, not something else
		m_aitGrapher			= new AnimateImageTask();
		m_ignored				= false;

		m_mousePoint			= new Point();
		m_rectCollapse			= new Rectangle();
		m_rectNamebox			= new Rectangle();
		m_rectScorebox			= new Rectangle();
		m_rectTabSpace			= new Rectangle();

		m_color					= getColor(level);
		m_selectedColor			= AlphaImage.makeARGB(255, 204, 229, 255);
		m_subcomponentScoreRect	= new Rectangle();

		m_score					= 0.0;
		if (node != null)
		{
			Utils.extractCommaItems(m_filterTags, node.getAttributeOrChild("tags"));
			score = node.getAttributeOrChild("score");
			if (!score.isEmpty())
			{	// There is a score at this level / on this line
				m_score		= Double.valueOf(score);
			}

			m_tested	= Utils.interpretBooleanAsYesNo(node.getAttributeOrChild("tested"), true).equalsIgnoreCase("Yes");
			m_success	= node.getAttributeOrChild("status").equals("success");
			m_name		= node.getAttributeOrChild("name");
			m_shortname	= node.getAttributeOrChild("shortname");

		} else {
			m_tested	= false;
			m_success	= false;
			m_name		= "unspecified";
			m_shortname	= "unspec";

		}

		if (m_renderList == null)
			m_renderList = new ArrayList<ResultsViewerLine>(0);

		if (m_subcomponentRenderList == null)
			m_subcomponentRenderList = new ArrayList<ResultsViewerLine>(0);

		m_nameboxFont			= new Font("Calibri", Font.BOLD, 24);
		m_tabTopFont			= new Font("Calibri", Font.PLAIN, 12);
		m_tabBottomFont			= new Font("Calibri", Font.BOLD, 16);
		m_tabTopTextColor		= new Color( 53, 42, 27);
		m_tabBottomTextColor	= new Color(106, 97, 84);
		m_nameboxTextColor		= new Color(106, 97, 84);

		if (m_imgCollapse == null)
			m_imgCollapse = new AlphaImage(Opbm.locateFile("collapse.png"));
		if (m_imgNamebox0 == null)
			m_imgNamebox0 = new AlphaImage(Opbm.locateFile("namebox0.png"));
		if (m_imgNamebox1 == null)
			m_imgNamebox1 = new AlphaImage(Opbm.locateFile("namebox1.png"));
		if (m_imgNamebox2 == null)
			m_imgNamebox2 = new AlphaImage(Opbm.locateFile("namebox2.png"));
		if (m_imgNamebox3 == null)
			m_imgNamebox3 = new AlphaImage(Opbm.locateFile("namebox3.png"));
		if (m_imgNamebox4 == null)
			m_imgNamebox4 = new AlphaImage(Opbm.locateFile("namebox4.png"));
		if (m_imgNamebox5 == null)
			m_imgNamebox5 = new AlphaImage(Opbm.locateFile("namebox5.png"));
		if (m_imgScorebox == null)
			m_imgScorebox = new AlphaImage(Opbm.locateFile("scorebox.png"));
		if (m_imgTab == null)
			m_imgTab = new AlphaImage(Opbm.locateFile("tab.png"));
		if (m_imgTabTopBackground == null)
			m_imgTabTopBackground = new AlphaImage(Opbm.locateFile("tab_top_background.png"));
		if (m_imgTabBottomBackground == null)
			m_imgTabBottomBackground = new AlphaImage(Opbm.locateFile("tab_bottom_background.png"));
		if (m_imgTabIgnored == null)
			m_imgTabIgnored = new AlphaImage(Opbm.locateFile("tab_ignored.png"));
		if (m_imgCollapseSelected == null)
			m_imgCollapseSelected = new AlphaImage(Opbm.locateFile("collapse_selected.png"));
		if (m_imgCollapseAnchored == null)
			m_imgCollapseAnchored = new AlphaImage(Opbm.locateFile("collapse_anchored.png"));
		if (m_imgNamebox0Selected == null)
			m_imgNamebox0Selected = new AlphaImage(Opbm.locateFile("namebox0_selected.png"));
		if (m_imgNamebox1Selected == null)
			m_imgNamebox1Selected = new AlphaImage(Opbm.locateFile("namebox1_selected.png"));
		if (m_imgNamebox2Selected == null)
			m_imgNamebox2Selected = new AlphaImage(Opbm.locateFile("namebox2_selected.png"));
		if (m_imgNamebox3Selected == null)
			m_imgNamebox3Selected = new AlphaImage(Opbm.locateFile("namebox3_selected.png"));
		if (m_imgNamebox4Selected == null)
			m_imgNamebox4Selected = new AlphaImage(Opbm.locateFile("namebox4_selected.png"));
		if (m_imgNamebox5Selected == null)
			m_imgNamebox5Selected = new AlphaImage(Opbm.locateFile("namebox5_selected.png"));
		if (m_imgScoreboxSelected == null)
			m_imgScoreboxSelected = new AlphaImage(Opbm.locateFile("scorebox_selected.png"));
		if (m_imgTabSelected == null)
			m_imgTabSelected = new AlphaImage(Opbm.locateFile("tab_selected.png"));

		m_navEntryLastTime	= 0;
	}

	public int getColor(int level)
	{
		switch (level)
		{
			case 0:		// Summary level, white
			default:	// not specified, so use white
				return(AlphaImage.makeARGB(255, 255, 255, 255));
			case 1:		// Suite level, yellow
				return(AlphaImage.makeARGB(255, 255, 255, 215));
			case 2:		// Scenario level, red
				return(AlphaImage.makeARGB(255, 255, 215, 215));
			case 3:		// Molecule level, green
				return(AlphaImage.makeARGB(255, 215, 255, 215));
			case 4:		// Atom level, blue
				return(AlphaImage.makeARGB(255, 215, 215, 255));
			case 5:		// Worklet level, cyan
				return(AlphaImage.makeARGB(255, 215, 255, 255));
		}
	}

	public int getColorForTabs(int level)
	{
		switch (level)
		{
			case 0:		// Summary level, white
			default:	// not specified, so use white
				return(AlphaImage.makeARGB(255, 255, 255, 255));
			case 1:		// Suite level, yellow
				return(AlphaImage.makeARGB(255, 255, 255, 164));
			case 2:		// Scenario level, red
				return(AlphaImage.makeARGB(255, 255, 164, 164));
			case 3:		// Molecule level, green
				return(AlphaImage.makeARGB(255, 164, 255, 215));
			case 4:		// Atom level, blue
				return(AlphaImage.makeARGB(255, 164, 164, 255));
			case 5:		// Worklet level, cyan
				return(AlphaImage.makeARGB(255, 164, 255, 255));
		}
	}

	public void buildRenderList()
	{
		int i;

		m_renderList.clear();
		populateRenderList(this);
		setRenderListRenderVisibles(this, true);

		// Remove everything that's not visible
		for (i = m_renderList.size() - 1; i >= 0; i--)
		{
			if (!m_renderList.get(i).isRenderVisible())
				m_renderList.remove(i);		// Remove this item which is not visible
		}
	}

	public void clearOutAllRenderListRenderVisibleSettings()
	{
		clearAllRenderListRenderVisibleSettings(m_parent.getRootRVL());
	}

	public void clearAllRenderListRenderVisibleSettings(ResultsViewerLine rvl)
	{
		while (rvl != null)
		{
			// Clear the flag
			rvl.setRenderVisible(false);

			// Process any children
			removeAllPriorLabels(rvl.getChild());

			// Move to next sibling
			rvl = rvl.getNext();
		}
	}

	public void removePriorRenderListRenderVisibleLabels()
	{
		removeAllPriorLabels(m_parent.getRootRVL());
	}

	public void removeAllPriorLabels(ResultsViewerLine rvl)
	{
		while (rvl != null)
		{
			if (rvl.m_lblLine != null)
				rvl.m_lblLine.setVisible(false);

			// Process any children
			removeAllPriorLabels(rvl.getChild());

			// Move to next sibling
			rvl = rvl.getNext();
		}
	}

	public Dimension render(Dimension pos)
	{
		int i, deletedCount;

		// Create a list of all entries to display, and make sure the navigation entry is visible on-screen
		clearOutAllRenderListRenderVisibleSettings();
		removePriorRenderListRenderVisibleLabels();
		buildRenderList();

		// Make sure the navigation entry is visible
		m_navEntry = locateNavEntryInRenderList();
		if (m_navEntry == -1)
		{	// Nothing to draw (due to filter requirements)
			removePriorRenderListRenderVisibleLabels();
			return(pos);
		}

		// Delete everything before 6 above that entry
		deletedCount = 0;
		if (m_navEntry < m_navEntryLastTime || (m_navEntry - m_navEntryLastTime) >= 5)
		{
			for (i = 0; i < ((m_navEntry >= m_navEntryLastTime) ? m_navEntry - 5 : m_navEntry); i++)
			{
				// Hide the items we're deleting
				if (m_renderList.get(0).m_lblLine != null)
					m_renderList.get(0).m_lblLine.setVisible(false);

				m_renderList.remove(0);
				++deletedCount;
			}
			m_navEntryLastTime = deletedCount;

		} else {
			// the m_navEntryLastTime has not changed, but we need to remove the items above that level
			for (i = 0; i < m_navEntryLastTime; i++)
			{
				// Hide the items we're deleting
				if (m_renderList.get(0).m_lblLine != null)
					m_renderList.get(0).m_lblLine.setVisible(false);

				m_renderList.remove(0);
			}

		}

		// Draw everything until we're out of the screen
		for (i = 0; i < m_renderList.size(); i++)
			pos = renderThisOne(pos, m_renderList.get(i));

		return(pos);
	}

	public Dimension renderThisOne(Dimension			pos,
								   ResultsViewerLine	rvl)
	{
		Rectangle rect;

		if (rvl.m_lblBottom.getY() + pos.height + m_imgScorebox.getHeight() <= m_imgBackground.getHeight())
		{
			// Render this line
			if (rvl.m_lblLine == null)
			{
				rvl.m_lblLine = new JLabel();
				rvl.m_lblLine.addMouseListener(this);
				rvl.m_lblLine.setVisible(false);
				rvl.m_lblLine.addMouseWheelListener(this);
				m_pan.add(rvl.m_lblLine);
				m_pan.moveToFront(rvl.m_lblLine);
			}

			// Position the line where it should go
			rect = new Rectangle(m_lblBottom.getX() + pos.width,
								 m_lblBottom.getY() + pos.height,
								 m_imgBackground.getWidth() - pos.width - 4,
								 m_imgNamebox0.getHeight());
			rvl.m_lblLine.setBounds(rect);

			// Extract the background image for it
			rvl.m_imgLine = m_imgBackground.extractImage(rect.x,
														 rect.y,
														 rect.x + rect.width,
														 rect.y + rect.height);
			rvl.m_imgLine.recolorize(AlphaImage.makeARGB(255, 255, 255, 255));
			rvl.m_imgLine.scaleContrast(0.75);
			rvl.m_imgLine.scaleBrightness(0.75);

			rvl.renderCollapse();
			rvl.renderNamebox();
			rvl.renderScorebox();
			rvl.renderTabs();

			rvl.m_lblLine.setIcon(new ImageIcon(rvl.m_imgLine.getBufferedImage()));
			rvl.m_lblLine.setVisible(true);
			pos.height += m_imgScorebox.getHeight();

		} else {
			// Hide the line (if it was previously displayed)
			if (rvl.m_lblLine != null) {
				rvl.m_lblLine.setVisible(false);
			}
		}
		return(pos);
	}

	public int locateNavEntryInRenderList()
	{
		int entry;

		if (m_renderList.size() > 0)
		{
			for (entry = 0; entry < m_renderList.size(); entry++)
			{
				if (m_renderList.get(entry).isSelectedForNavigation())
					return(entry);
			}
			// If we get here, no navigation entry was found
			// Force it to the beginning
			m_renderList.get(0).setSelectedForNavigation(true);
			return(0);

		} else {
			return(-1);

		}
	}

	/**
	 * Call recursively until all visible lines are displayed
	 * @param rvl root entry to be "at the top logically", though this position
	 * is adjusted as needed so as to ensure visibility of the navigation line
	 */
	public void populateRenderList(ResultsViewerLine rvl)
	{
		while (rvl != null)
		{
			if (!rvl.isIgnored())
				addRenderedLine(rvl);

			populateRenderList(rvl.getChild());
			rvl = rvl.getNext();
		}
	}

	public void populateSubcomponentRenderList(ResultsViewerLine	rvl,
											   int					level)
	{
		ResultsViewerLine targetChild;

		targetChild = m_parent.getNavLineCurrentChild();
		while (rvl != null)
		{
			if (!rvl.isIgnored())
			{
				addSubcomponentRenderedLine(rvl);
				rvl.setRenderLevel(level);
			}

			if (rvl.isExpanded() || rvl.hasThisChild(targetChild))
			{
				populateSubcomponentRenderList(rvl.getChild(), level + 1);
			}

			rvl = rvl.getNext();
		}
	}

	public int adjustSubcomponentRenderListLevels()
	{
		int i, highestLevel, navLevel, numRemoved;
		ResultsViewerLine rvl, navLine;

		// Find the highest level for the nav line's children (the deepest level we'll render)
		navLine			= m_parent.getNavLineCurrentChild();
		highestLevel	= -1;
		navLevel		= -1;
		for (i = 0; i < m_subcomponentRenderList.size(); i++)
		{
			rvl = m_subcomponentRenderList.get(i);

			if (rvl.getRenderLevel() > highestLevel)
				highestLevel = rvl.getRenderLevel();

			if (rvl == navLine)
				navLevel = highestLevel;	// The nav line determines what level we render to, as it's always one child level deeper than the actual nav line (if such a child level exists)
		}

		// Delete everything beyond the level after the navLevel
		numRemoved = 0;
		for (i = m_subcomponentRenderList.size() - 1; i >= 0; i--)
		{
			rvl = m_subcomponentRenderList.get(i);

			if (rvl.getRenderLevel() > navLevel)
			{
				m_subcomponentRenderList.remove(i);
				++numRemoved;
			}
		}
		// Nav levels are converted to one higher than was found (to allow for their children being rendered)
		// But, if no items were removed above, it means the actual nav level is as far down as they can go, as there are no more child levels below
		navLevel += ((numRemoved == 0) ? 0 : 1);	// Only adjust the navLevel if there's something deeper than its level

		// Reverse the values of the level, such that level 0 is the furthest descendant, level 1 is its parent, level 2 is its parent, and so on
		for (i = 0; i < m_subcomponentRenderList.size(); i++)
		{
			rvl = m_subcomponentRenderList.get(i);
			rvl.setRenderLevel(navLevel - rvl.getRenderLevel());
		}
		// When we get here, we have a whittled list, down to the number of
		// elements to render at the level we're at, and their numbers are
		// increasing, render level 0 first, level 1 second, and so on, unto
		// the highest
		return(navLevel);
	}

	public void setRenderListRenderVisibles(ResultsViewerLine	rvl,
											boolean				parentIsExpanded)
	{
		ResultsViewerLine thisOne;

		thisOne = rvl;
		while (thisOne != null)
		{
			// Set this one
			thisOne.setRenderVisible(parentIsExpanded);

			// Set its children appropriately
			if (!parentIsExpanded) {
				thisOne.setRenderListRenderVisibles(thisOne.getChild(), false);
			} else {
				thisOne.setRenderListRenderVisibles(thisOne.getChild(), thisOne.isExpanded());
			}

			// Move to the next sibling at this level
			thisOne = thisOne.getNext();
		}
	}

	public void renderCollapse()
	{
		int startX, startY, endX, endY;

		startX	= m_level * m_imgCollapse.getWidth();
		startY	= 0;
		endX	= startX + m_imgCollapse.getWidth();
		endY	= m_imgCollapse.getHeight();
		m_imgLine.scaleBrightness(0.80, startX, startY, endX, endY);
		m_rectCollapse.setBounds(startX, startY, m_imgCollapse.getWidth(), m_imgCollapse.getHeight());
		if (m_lineSelectedGraph)
		{	// Indicates which entry is being graphed, not navigated to/on
			m_imgLine.recolorize(m_selectedColor, startX, startY, endX, endY);

			if (m_lineSelectedNav)
			{	// This line is selected and the navigation line, draw it normally
				m_imgLine.darkenByAlphaMask(m_imgCollapseSelected, startX, startY, endX, endY);

			} else {
				// The navigation line is away from this graph line, so we show the anchor icon to indicate the graph is anchored here
				m_imgLine.darkenByAlphaMask(m_imgCollapseAnchored, startX, startY, endX, endY);

			}

		} else {
			m_imgLine.recolorize(m_color, startX, startY, endX, endY);
			m_imgLine.darkenByAlphaMask(m_imgCollapse, startX, startY, endX, endY);

		}
	}

	public void renderNamebox()
	{
		int startX, startY, endX, endY;

		// Determine the rectangle size
		startX	= (m_level + 1) * m_imgCollapse.getWidth();
		startY	= 0;
		endY	= m_imgCollapse.getHeight();
		switch (m_level)
		{
			case 0:
			default:
				endX = startX + m_imgNamebox0.getWidth();
				break;
			case 1:
				endX = startX + m_imgNamebox1.getWidth();
				break;
			case 2:
				endX = startX + m_imgNamebox2.getWidth();
				break;
			case 3:
				endX = startX + m_imgNamebox3.getWidth();
				break;
			case 4:
				endX = startX + m_imgNamebox4.getWidth();
				break;
			case 5:
				endX = startX + m_imgNamebox5.getWidth();
				break;
		}
		m_rectNamebox.setBounds(startX, startY, endX - startX, endY - startY);

		// Colorize the entire section
		m_imgLine.scaleBrightness(0.80, startX, startY, endX, endY);
		if (m_lineSelectedNav && m_selectedTab == -1)
		{	// This entry is the selected navigation entry
			m_imgLine.recolorize(m_selectedColor, startX, startY, endX, endY);
		} else {
			// Just a regular entry
			m_imgLine.recolorize(m_color, startX, startY, endX, endY);
		}

		// Darken the appropriate parts
		if (m_lineSelectedNav && m_selectedTab == -1)
		{
			switch (m_level)
			{
				case 0:
				default:
					m_imgLine.darkenByAlphaMask(m_imgNamebox0Selected, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 19), m_nameboxTextColor, m_nameboxFont, 255);
					break;
				case 1:
					m_imgLine.darkenByAlphaMask(m_imgNamebox1Selected, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 17), m_nameboxTextColor, m_nameboxFont, 255);
					break;
				case 2:
					m_imgLine.darkenByAlphaMask(m_imgNamebox2Selected, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 15), m_nameboxTextColor, m_nameboxFont, 255);
					break;
				case 3:
					m_imgLine.darkenByAlphaMask(m_imgNamebox3Selected, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 13), m_nameboxTextColor, m_nameboxFont, 255);
					break;
				case 4:
					m_imgLine.darkenByAlphaMask(m_imgNamebox4Selected, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 11), m_nameboxTextColor, m_nameboxFont, 255);
					break;
				case 5:
					m_imgLine.darkenByAlphaMask(m_imgNamebox5Selected, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 9), m_nameboxTextColor, m_nameboxFont, 255);
					break;
			}

		} else {
			switch (m_level)
			{
				case 0:
				default:
					m_imgLine.darkenByAlphaMask(m_imgNamebox0, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 19), m_nameboxTextColor, m_nameboxFont, 255);
					break;
				case 1:
					m_imgLine.darkenByAlphaMask(m_imgNamebox1, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 17), m_nameboxTextColor, m_nameboxFont, 255);
					break;
				case 2:
					m_imgLine.darkenByAlphaMask(m_imgNamebox2, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 15), m_nameboxTextColor, m_nameboxFont, 255);
					break;
				case 3:
					m_imgLine.darkenByAlphaMask(m_imgNamebox3, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 13), m_nameboxTextColor, m_nameboxFont, 255);
					break;
				case 4:
					m_imgLine.darkenByAlphaMask(m_imgNamebox4, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 11), m_nameboxTextColor, m_nameboxFont, 255);
					break;
				case 5:
					m_imgLine.darkenByAlphaMask(m_imgNamebox5, startX, startY, endX, endY);
					m_imgLine.drawString(startX + 13, endY - 8, Utils.padLeft(m_name, 9), m_nameboxTextColor, m_nameboxFont, 255);
					break;
			}

		}

		// Make the tooltip have the full name
		m_lblLine.setToolTipText(m_name);

	}

	public void renderScorebox()
	{
		int startX, startY, endX, endY;

		startX	= 336 - m_imgScorebox.getWidth();
		startY	= 0;
		endX	= 336;
		endY	= m_imgScorebox.getHeight();
		m_imgLine.scaleBrightness(0.80, startX, startY, endX, endY);
		m_rectScorebox.setBounds(startX, startY, m_imgScorebox.getWidth(), m_imgScorebox.getHeight());
		if (m_lineSelectedNav && m_selectedTab == -1)
		{
			m_imgLine.recolorize(m_selectedColor, startX, startY, endX, endY);
			m_imgLine.darkenByAlphaMask(m_imgScoreboxSelected, startX, startY, endX, endY);

		} else {
			m_imgLine.recolorize(m_color, startX, startY, endX, endY);
			m_imgLine.darkenByAlphaMask(m_imgScorebox, startX, startY, endX, endY);

		}

		m_imgLine.drawString(startX + 13, endY - 8, Integer.toString((int)getScore()), m_nameboxTextColor, m_nameboxFont, 255);
	}

	public void renderTabs()
	{
		int thisEntry, color, startX, startY, endX, endY;
		String text;
		ResultsViewerLine child;

		// Begin at the left-most tab position
		startX	= 336 + 4;
		startY	= 0;
		endX	= startX + m_imgTab.getWidth();
		endY	= m_imgTab.getHeight();

		// Store the start of the tab area rectangle for this entry
		m_rectTabSpace.setBounds(startX, startY, endX, endY);

		// Determine the color for this level
		color = getColorForTabs(m_level + 1);

		// Draw the tabs
		thisEntry	= 0;
		child		= m_child;
		while (child != null && startX + m_imgTab.getWidth() <= m_imgLine.getWidth())
		{
			if (thisEntry >= m_leftmostTab)
			{	// This one is visible, we can render this one
				m_imgLine.scaleBrightnessByAlphaMask(0.80, m_imgTabBottomBackground, startX, startY, endX, endY);
				m_imgLine.recolorizeByAlphaMask(m_imgTabTopBackground, color, startX, startY, endX, endY);
				if (m_lineSelectedNav && m_selectedTab >= 0)
				{
					if (m_selectedTab == thisEntry)
					{	// Make the background highlighted to indicate the "selected cell"
						m_imgLine.recolorizeByAlphaMask(m_imgTabBottomBackground, m_selectedColor, startX, startY, endX, endY);
						m_imgLine.darkenByAlphaMask(m_imgTabSelected, startX, startY, endX, endY);

					} else {
						m_imgLine.darkenByAlphaMask(m_imgTab, startX, startY, endX, endY);

					}

				} else {
					m_imgLine.darkenByAlphaMask(m_imgTab, startX, startY, endX, endY);

				}
				if (child.isIgnored())
				{	// Put the overlain "X" over the tab
					m_imgLine.darkenByAlphaMask(m_imgTabIgnored, startX, startY, endX, endY);
					if (child.wasTested())
					{
						if (child.wasFilteredOut())
							text = "Filtered";
						else
							text = "Ignored";

					} else {
						text = "No Data";

					}
					// Draw the reason
					m_imgLine.drawString(startX + 5, endY - 5, text, m_tabBottomTextColor, m_tabBottomFont, 255);

				} else {
					// Draw the score
					text = Integer.toString((int)child.getScore());
					m_imgLine.drawString(startX + 21, endY - 5, text, m_tabBottomTextColor, m_tabBottomFont, 255);

				}
				// Draw the top wording for the name of the tab
				m_imgLine.drawString(startX + 7, startY + 10, child.getShortName(), m_tabTopTextColor, m_tabTopFont, 255);


				// Move to the next position
				startX	+= m_imgTab.getWidth();
				endX	+= m_imgTab.getWidth();
			}
			++thisEntry;

			// Move to next child entry
			child = child.getNext();
		}
		// Set the ending boundary
		m_rectTabSpace.setBounds((int)m_rectScorebox.getX(), (int)m_rectScorebox.getY(), endX, endY);
	}

	/**
	 * Only called from parent. Renders the graph for the current row, with
	 * all of its children in tact
	 */
	public void renderGraph()
	{
		int failCount;
		ResultsViewerLine graph, nav;
		Rectangle rect;
		String failureText;
		Font font;

		// Copy the default graph background
		m_imgGraph		= new AlphaImage(m_imgGraphInternal);
		if (m_parent.getFailureCount() != 0)
			m_imgGraph.colorize(AlphaImage.makeARGB(255, 255, 0, 0));
// Used to make the background green if needed:
//		else
//			m_imgGraph.colorize(AlphaImage.makeARGB(255, 0, 255, 0));

		graph			= m_parent.getGraphLine();
		nav				= m_parent.getNavLine();
		if (graph == nav && nav.getSelectedTab() >= 0)
		{	// Render the graph normally, as they're on the same row as the graph
			graph.renderGraphWithSubcomponents();

		} else {
			// On different rows, if they're on a descendant of the graph line, we can draw it
			if (graph.hasThisForADescendant(nav))
			{	// Render a type of detailed graph highlighting the tab the user is on
				graph.renderGraphWithSubcomponents();

			} else {
				// The graph
				graph.renderGraphNormally();

			}
		}

		// Determine the spacing and width for each graph portion
		failCount = m_parent.getFailureCount();
		if (failCount != 0)
		{	// Add text across the top indicating the failures
			font = new Font("Calibri", Font.BOLD, 30);
			failureText	= "Failure on " + Integer.toString(m_parent.getFailureCount()) + Utils.singularOrPlural(failCount, " test!", " tests!");
			rect		= m_imgGraph.getRectangle();
			rect.height /= 8;
			m_imgGraph.drawStringInRectangle(rect, failureText, new Color(192, 0, 0), font, 255, true);
			rect.move(-2, -2);
			m_imgGraph.drawStringInRectangle(rect, failureText, Color.WHITE, font, 255, true);
		}

		m_lblGraphInternal.setIcon(new ImageIcon(m_imgGraph.getBufferedImage()));
	}

	public void renderGraphNormally()
	{
		int i, count, highestScore, lowestScore, delta, top, startX, startY, endX, endY, borderColor, backgroundColor;
		double border, between, lineWidth, position, score, step;
		ResultsViewerLine child;
		Rectangle rect;
		String scoreText;
		Font font;

		// Find out how many children there are (to determine the thickness and spacing of each graph)
		count	= countChildrenExcludeIgnores();
		if (getChild() != null)
		{	// There's at least oen child
			highestScore	= (int)getHighestChildScore(m_parent.getGraphLine(), -999.0);
			lowestScore		= (int)getLowestChildScore(m_parent.getGraphLine(), 999.0);

		} else {
			// No children, just graph the current line and use its score for highest and lowest
			highestScore	= (int)m_score;
			lowestScore		= highestScore;

		}
		delta	= Math.max(highestScore, 100) - Math.min(lowestScore, 100) + 30;
		step	= 0.9 * ((double)m_imgGraph.getHeight() / (double)delta);

		// Draw the grid
		for (i = 0; i < delta; i += 2)
		{
			m_imgGraph.drawLine(0,
								(int)((double)m_imgGraph.getHeight() * (double)i / (double)delta),
								m_imgGraph.getWidth() - 1,
								(int)((double)m_imgGraph.getHeight() * (double)i / (double)delta),
								AlphaImage.makeARGB(64,255,255,255));
		}

		if (m_parent.getFailureCount() != 0)
		{	// Make it all red
			m_imgGraph.alphaize(255);
			m_imgGraph.recolorize(AlphaImage.makeARGB(255, 255, 0, 0));
//		} else {
//			// Make it all green
//			m_imgGraph.alphaize(128);
//			m_imgGraph.colorize(AlphaImage.makeARGB(255, 0, 255, 0));
		}

		// Mask off the portions which should not appear
		m_imgGraph.applyAlphaMask(m_imgGraphMask);

		// Draw the baseline
		m_imgGraph.drawHighlightedHorizontalRectangle(0, (int)((double)m_imgGraph.getHeight() * 0.48), m_imgGraph.getWidth(), (int)((double)m_imgGraph.getHeight() * 0.52), AlphaImage.makeARGB(255, 255,255,192), AlphaImage.makeARGB(255,   0,   0, 255));

		// Determine spacing alotted for each, based off center-of-graph-line
		// Formula is:
		//
		//		For 3 graph legs, for example:
		//			[leftBorder]|graphLine|[between]|graphLine|[between]|graphLine|[righBorder]
		//			[--------------------------------width------------------------------------]
		//
		//		(width - leftBorder - rightBorder - graphLine.width) / (count - 1)
		border		= 25;
		lineWidth	= 16;
		between		= Math.min((m_imgGraph.getWidth() - (2 * border) - lineWidth) / ((double)count - 1.0), 50.0);
		position	= border + (lineWidth / 2);

		// Create the font
		font = new Font("Calibri", Font.BOLD, 14);

		// Set the colors
		borderColor = AlphaImage.makeARGB(255, 255,255,192);
		if (m_parent.getFailureCount() != 0)
		{	// Draw in the error color
			backgroundColor = AlphaImage.makeARGB(255, 255, 64, 64);
		} else {
			// Draw in the normal color
			backgroundColor = AlphaImage.makeARGB(255,  64, 255,  64);
		}

		// Find out what type of graph we're drawing
		child	= getChild();
		if (child != null)
		{
			count	= 0;
			while (child != null)
			{
				if (!child.isIgnored())
				{
					score		= (double)((int)child.getScore());
					scoreText	= Integer.toString((int)score);
					startX		= 5 + (int)((position - (lineWidth / 2.0)));
					startY		= (int)((m_imgGraph.getHeight() * 0.5) - (step * (double)(score - 100)));
					endX		= startX + (int)lineWidth;
					endY		= (int)(m_imgGraph.getHeight() - 2);

					// Draw the graph normally
					if (m_lineSelectedNav && count == m_selectedTab)
						m_imgGraph.drawHighlightedVerticalRectangle(startX, startY, endX, endY, borderColor, m_selectedColor);
					else
						m_imgGraph.drawHighlightedVerticalRectangle(startX, startY, endX, endY, borderColor, backgroundColor);

					// Put the score on it
					rect		= AlphaImage.getStringRectangle(scoreText, font);
					m_imgGraph.drawString(5 + (int)position - (rect.width / 2), startY - 5, scoreText, Color.WHITE, font, 128);

					// Put the test's label on it
					rect		= AlphaImage.getStringRectangle(child.getShortName(), font);
					m_imgGraph.drawStringRotated90DegreesCCW(startX, endY - 2, child.getShortName(), Color.WHITE, font);

					position	+= between;
					++count;
				}

				// Move to next location
				child		= child.getNext();
			}

		} else {
			// We're on something that doesn't have children, so just render its value
			score		= (double)((int)getScore());
			scoreText	= Integer.toString((int)score);
			startX		= 5 + (int)((position - (lineWidth / 2.0)));
			startY		= (int)((m_imgGraph.getHeight() * 0.5) - (step * (double)(score - 100)));
			endX		= startX + (int)lineWidth;
			endY		= (int)(m_imgGraph.getHeight() - 2);

			// Draw the graph normally
			if (m_lineSelectedNav && count == m_selectedTab)
				m_imgGraph.drawHighlightedVerticalRectangle(startX, startY, endX, endY, borderColor, AlphaImage.makeARGB(255, 222, 22, 222));
			else
				m_imgGraph.drawHighlightedVerticalRectangle(startX, startY, endX, endY, borderColor, backgroundColor);

			// Put the score on it
			rect		= AlphaImage.getStringRectangle(scoreText, font);
			m_imgGraph.drawString(5 + (int)position - (rect.width / 2), startY - 5, scoreText, Color.WHITE, font, 128);

			// Put the test's label on it
			rect		= AlphaImage.getStringRectangle(m_shortname, font);
			m_imgGraph.drawStringRotated90DegreesCCW(startX, endY - 2, m_shortname, Color.WHITE, font);

			// Move to next location
			position	+= between;

		}
	}

	public void renderGraphWithSubcomponents()
	{
		int i, level, startX, startY, endX, highestLevel, highestScore, lowestScore, score, delta, count, margin, height;
		double step;
		Font font;
		boolean isValid;
		ResultsViewerLine rvl, child;

		// Clear out the list buffer used for this process
		m_subcomponentRenderList.clear();
		m_parent.resetAllSubcomponentRenderImagesAndCounts(m_parent.getGraphLine());

		// Create our font
		font	= new Font("Calibri", Font.BOLD, 20);
		margin	= 10;
		height	= 20;

		// Generate a list of everything that needs rendering
		populateSubcomponentRenderList(this, 0);
		highestLevel = adjustSubcomponentRenderListLevels();

		// Determine the highest and lowest scores
		highestScore	= (int)getHighestChildScore(m_parent.getRootRVL(), -999.0);
		lowestScore		= (int)getLowestChildScore(m_parent.getRootRVL(), 999.0);

		// Render everything in the list, level by level
		count	= 0;
		for (level = 0; level <= highestLevel; level++)
		{
			for (i = 0; i < m_subcomponentRenderList.size(); i++)
			{
				rvl = m_subcomponentRenderList.get(i);
				if (rvl.getRenderLevel() == level)
				{
					if (level == 0)
						rvl.generateSubcomopnentRenderedImages(font, highestScore, margin, height);
					else
					{
						// See if this item has child entries (it may not, if it wasn't opened during the render
						child	= rvl.getChild();
						isValid	= true;
						while (child != null)
						{
							if (child.getSubcomponentImage() == null)
							{
								isValid = false;
								break;
							}
							child = child.getNext();
						}
						if (isValid)
							rvl.combineSubcomponentRenderedImages(rvl, highestScore, highestLevel, margin, height);
						else
							rvl.generateSubcomopnentRenderedImages(font, highestScore, margin, height);
					}

					if (rvl.getRenderLevel() == highestLevel - 1)
						++count;
				}
			}
		}
		// When we get here, the highestLevel-1 entries are the ones we want to render onto m_imgGraph

		// Determine each item's height, they have to fit in (m_imgGraph.getHeight() - (2 * margin)) pixels
		step	= Math.min((double)(m_imgGraph.getHeight() - (2 * margin)) / (double)count, (double)height);

		// Draw the grid
		for (i = 0; i < highestScore; i += 2)
		{
			startX = margin + (int)(((double)m_imgGraph.getWidth() - (2 * margin)) * (double)i / (double)highestScore);
			m_imgGraph.drawLine(startX,
								0,
								startX,
								m_imgGraph.getHeight(),
								AlphaImage.makeARGB(64,255,255,255));
		}

		if (m_parent.getFailureCount() != 0)
		{	// Make it all red
			m_imgGraph.alphaize(255);
			m_imgGraph.recolorize(AlphaImage.makeARGB(255, 255, 0, 0));
//		} else {
//			// Make it all green
//			m_imgGraph.alphaize(128);
//			m_imgGraph.colorize(AlphaImage.makeARGB(255, 0, 255, 0));
		}

		// Mask off the portions which should not appear
		m_imgGraph.applyAlphaMask(m_imgGraphMask);

		// Draw the baseline
		startX	= margin + (int)(((double)m_imgGraph.getWidth() - (2 * margin)) * (double)100 / (double)highestScore);
		endX	= margin + (int)(((double)m_imgGraph.getWidth() - (2 * margin)) * (double)101 / (double)highestScore);
		m_imgGraph.drawHighlightedVerticalRectangle(startX, 0, endX, m_imgGraph.getHeight() - 1,
													AlphaImage.makeARGB(255, 255,255,192), AlphaImage.makeARGB(255,   0,   0, 255));

		// Render them
		startX	= margin;
		startY	= margin;
		for (i = 0; i < m_subcomponentRenderList.size(); i++)
		{
			rvl = m_subcomponentRenderList.get(i);
			if (rvl.getRenderLevel() == (highestLevel - 1))
			{
				// Scale down the height if necessary
				if (step < height)
					rvl.m_subcomponentImage.scale(1.0, step / (rvl.m_subcomponentImage.getHeight() + 2));

				// Overlay the image
				m_imgGraph.overlayImage(rvl.m_subcomponentImage, startX, startY, 255);

				// Colorize it
				m_imgGraph.alphaize(255, startX, startY, startX + rvl.m_subcomponentImage.getWidth()-1, startY + rvl.m_subcomponentImage.getHeight() - 1);
				startY += (int)step;
			}
		}
	}

	/**
	 * Graphs are simple at this level, a fixed size image with text on it
	 * showing the leg name and score
	 * @param font
	 */
	public void generateSubcomopnentRenderedImages(Font		font,
												   int		highestScore,
												   int		margin,
												   int		height)
	{
		int argb, startY;
		String text;
		Rectangle rect;
		double graphWidth, width;

		argb	= m_iterativeColor;
		graphWidth			= (double)(m_imgGraph.getWidth() - (margin * 2));
		width				= Math.max((int)((m_score / (double)highestScore) * graphWidth), 1);
		m_subcomponentImage	= new AlphaImage((int)width, height);

		if (m_parent.getNavLineCurrentChild() == this)
		{	// This is not only the nav line, but the exact sub-component we're on
			m_subcomponentImage.drawHighlightedHorizontalRectangle(0, 0, m_subcomponentImage.getWidth()-1, m_subcomponentImage.getHeight()-1, AlphaImage.makeARGB(255, 255, 255, 255), AlphaImage.makeARGB(255, 222, 22, 222));
		} else {
			// Not the nav line, just render in a normal iterative color
			m_subcomponentImage.drawHighlightedHorizontalRectangle(0, 0, m_subcomponentImage.getWidth()-1, m_subcomponentImage.getHeight()-1, AlphaImage.makeARGB(255, 255, 255, 255), argb);
		}

		// Add the name
		if (m_parent.getNavLine().hasThisChild(m_parent.getGraphLineCurrentChild()))
		{	// The long name plus the short name on largely expanded items
			text = m_name + " (" + m_shortname + ")";
		} else {
			text = m_shortname;
		}
		startY = (int)((double)m_subcomponentImage.getHeight() * 0.75);
		m_subcomponentImage.drawString(2, startY, text, Color.WHITE, font, 255);

		// Add the score
		text = Integer.toString((int)m_score);
		m_subcomponentScoreRect = AlphaImage.getStringRectangle(text, font);
		m_subcomponentImage.drawString(m_subcomponentImage.getWidth() - 2 - m_subcomponentScoreRect.width, startY, text, Color.WHITE, font, 255);
		m_subcomponentImage.setUserValue(1);
	}

	public void combineSubcomponentRenderedImages(ResultsViewerLine		rvl,
												  int					highestScore,
												  int					highestLevel,
												  int					margin,
												  int					height)
	{
		int width, startX, endX, largestUserValue;
		double totalScore, graphWidth, childWidth;
		ResultsViewerLine child;
		AlphaImage img;

		// We typically build images (m_imgGraph.getWidth() - (2 * margin)) pixels
		// wide, adjusted down by their relative performance compared to the highest score
		graphWidth			= (double)(m_imgGraph.getWidth() - (margin * 2));
		width				= Math.max((int)((m_score / (double)highestScore) * graphWidth), 1);
		m_subcomponentImage	= new AlphaImage(width, height);

		// Sum up the child's total score
		child		= rvl.getChild();
		totalScore	= 0.0;
		while (child != null)
		{
			totalScore += child.getScore();
			child = child.getNext();
		}

		// Affix everything proportionately (to its score) where it should go on the parent's line
		largestUserValue	= 0;
		startX				= 0;	// Always begin at margin over, for spacing around the rounded corners of the graph window
		child				= rvl.getChild();
		while (child != null)
		{
			// Determine how wide it should be on this line
			childWidth	= (child.getScore() / totalScore) * (double)m_subcomponentImage.getWidth();
			endX		= startX + (int)childWidth;

			if (child.m_subcomponentImage.getUserValue() > 2)
			{	// It's a grandchild or greater, so we need to scale its image
				child.m_subcomponentImage.scale((endX - startX) / (double)m_subcomponentImage.getWidth(), 1.0);
				m_subcomponentImage.overlayImage(child.m_subcomponentImage, startX, 0, startX + child.m_subcomponentImage.getWidth(), child.m_subcomponentImage.getHeight(), 255);

			} else {
				// No scaling, just do the overlay
				m_subcomponentImage.overlayImage(child.m_subcomponentImage, startX, 0, endX, child.m_subcomponentImage.getHeight() - 1, 255);

				// Pull in the right-side score and position it correctly
				img = child.m_subcomponentImage.extractImage(child.m_subcomponentImage.getWidth() - child.m_subcomponentScoreRect.width - 5,
															 0,
															 child.m_subcomponentImage.getWidth() - 2,
															 child.m_subcomponentImage.getHeight());
				m_subcomponentImage.overlayImage(img, endX - img.getWidth() - 2, 0, endX, img.getHeight(), 255);

			}
			if (child.m_subcomponentImage.getUserValue() + 1 > largestUserValue)
				largestUserValue = child.m_subcomponentImage.getUserValue() + 1;

			// Move to next position
			startX	+= (int)childWidth;

			// Move to the next sibling at this child level
			child = child.getNext();
		}
		m_subcomponentImage.setUserValue(largestUserValue);
	}

	public int howManyDescendantsToReachThisOne(ResultsViewerLine searchingFor)
	{
		return(tryAnotherDescendant(searchingFor, 0));
	}

	public int tryAnotherDescendant(ResultsViewerLine	searchingFor,
									int					level)
	{
		ResultsViewerLine rvl;
		int oldLevel;

		rvl = this;
		while (rvl != null)
		{
			if (!rvl.isIgnored())
			{
				if (rvl == searchingFor)
					return(level);
			}

			if (rvl.getChild() != null)
			{
				oldLevel	= level;
				level		= rvl.getChild().tryAnotherDescendant(searchingFor, level + 1);
				if (level != oldLevel)
					return(level);
			}

			// Move to next entry
			rvl = rvl.getNext();
		}
		// Not found, return the previous level
		return(level - 1);
	}

	public boolean hasThisForADescendant(ResultsViewerLine nav)
	{
		if (checkThisDescendant(nav, getChild()))
			return(true);
		return(false);
	}

	public boolean checkThisDescendant(ResultsViewerLine	searchingFor,
									   ResultsViewerLine	thisOne)
	{
		while (thisOne != null)
		{
			if (!thisOne.isIgnored())
			{
				if (searchingFor == thisOne)
					return(true);

				// Check the children below this level
				if (thisOne.checkThisDescendant(searchingFor, thisOne.getChild()))
					return(true);
			}

			// Check the siblings at this level
			thisOne = thisOne.getNext();
		}
		return(false);
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

	public boolean isGraphLegChildOf(ResultsViewerLine rvl)
	{
		ResultsViewerLine child, graphLine;

		graphLine	= m_parent.getGraphLine();
		child		= rvl.getChild();
		while (child != null)
		{
			if (!child.isIgnored() && graphLine == child)
				return(true);

			// Try the next sibling
			child = child.getNext();
		}
		// If we get here, it was not found, so it's not a child
		return(false);
	}

	public boolean isGraphLegNonChildDescendantOf(ResultsViewerLine rvl)
	{
		ResultsViewerLine child, graphLine;

		graphLine	= m_parent.getGraphLine();
		child		= rvl.getChild();
		while (child != null)
		{
			if (!child.isIgnored())
			{
				if (graphLine == child)
					return(true);

				if (child.isExpanded())
				{	// Try its children (and grandchildren)
					if (isGraphLegNonChildDescendantOf(rvl.getChild()))
						return(true);
					// If we get here, it wasn't a child, grandchild or later
				}
			}

			// Try the next sibling
			child = child.getNext();
		}
		// If we get here, it was not found, so it's not a child
		return(false);
	}

	/**
	 * Computes the geometric mean of the children's scores, or those scores
	 * which are included if a tag is specified
	 * @param byTags
	 * @param tags
	 */
	public void sumChildren(boolean			byTags,
							Tupel			tags)
	{
		double n, power;
		ResultsViewerLine child;

		// Count the child items
		n		= 0.0;
		child	= m_child;

		// If there is no child, then we're at the furthest extremity and whatever is loaded for the score is the score
		if (child == null)
			return;

		while (child != null)
		{
			if (!child.isIgnored())
				n += ((!byTags || child.countIfTagMatch(tags)) ? 1.0 : 0.0);

			child = child.getNext();
		}
		power = 1.0 / (double)n;

		// Perform the n_root(a1)... * n_root(an) computation
		m_score	= 0.0;
		child	= m_child;
		while (child != null)
		{
			if (!child.isIgnored() && (!byTags || child.countIfTagMatch(tags)))
			{
				if (m_score == 0.0)
					m_score = Math.pow(child.getScore(), power);
				else
					m_score *= Math.pow(child.getScore(), power);
			}

			// Move to the next entry
			child = child.getNext();
		}
// for random
//		if (m_node.getAttributeNode("score") == null)
//			m_node.appendAttribute(new Xml("score", Integer.toString((int)m_score)));
	}

	public boolean countIfTagMatch(Tupel tags)
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

	public boolean moveUp(ResultsViewerLine rvl)
	{
		int i;

		buildRenderList();
		for (i = 0; i < m_renderList.size(); i++)
		{
			if (m_renderList.get(i).equals(rvl))
			{	// This is the entry
				if (i > 0)
				{
					m_renderList.get(i-1).setSelectedForNavigation(true);
					if (m_renderList.get(i-1).getSelectedTab() == -1)
					{
						// They're navigating in the namebox and scorebox areas, so we change the graph line also
						m_renderList.get(i-1).setSelectedForGraph(true);
					}
					return(true);
				}
				// Nothing above to go to, we're at the top
				return(false);
			}
		}
		// If we get here, the nav leg wasn't found... so we don't know what to do because this should never really happen
		return(false);
	}

	public boolean moveDown(ResultsViewerLine rvl)
	{
		int i;

		buildRenderList();
		for (i = 0; i < m_renderList.size(); i++)
		{
			if (m_renderList.get(i).equals(rvl))
			{	// This is the entry
				if (i < m_renderList.size() - 1)
				{
					m_renderList.get(i+1).setSelectedForNavigation(true);
					if (m_renderList.get(i+1).getSelectedTab() == -1)
					{
						// They're navigating in the namebox and scorebox areas, so we change the graph line also
						m_renderList.get(i+1).setSelectedForGraph(true);
					}
					return(true);
				}
				// Nothing above to go to, we're at the top
				return(false);
			}
		}
		// If we get here, the nav leg wasn't found... so we don't know what to do because this should never really happen
		return(false);
	}

	public boolean moveLeft()
	{
		if (m_selectedTab > -1)
		{
			--m_selectedTab;

			if (m_selectedTab < m_leftmostTab)
				m_leftmostTab = Math.max(m_selectedTab, 0);

			if (m_selectedTab == -1)
				setSelectedForGraph(true);	// This one is now taking graph control

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
				setSelectedForNavigation(true);

			return(true);
		}
		return(false);
	}

	public void toggleExpandedState()
	{
		m_lineExpanded = !m_lineExpanded;
		if (!m_lineExpanded)
			m_lineExpanded = !(!m_lineExpanded);
	}

	public void addRenderedLine(ResultsViewerLine rvl)
	{
		m_renderList.add(rvl);
	}

	public void addSubcomponentRenderedLine(ResultsViewerLine rvl)
	{
		m_subcomponentRenderList.add(rvl);
	}

	public void setSubcomponentImage(AlphaImage img)
	{
		m_subcomponentImage = img;
	}

	public void setRenderedLevelCount(int count)
	{
		m_renderedLevelCount = count;
	}

	public void assignIterativeColorNumber()
	{
		m_iterativeColor = Utils.getNextIterativeColor();
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

	public boolean wasMouseClickedOnATab()
	{
		int startX, startY, endX, endY, thisEntry;
		Rectangle rect;
		ResultsViewerLine child;

		// Begin at the left-most tab position
		startX	= 336 + 4;
		startY	= 0;
		endX	= startX + m_imgTab.getWidth();
		endY	= m_imgTab.getHeight();

		// Iterate until we find it
		rect		= new Rectangle();
		child		= m_child;
		thisEntry	= 0;
		while (child != null && startX + m_imgTab.getWidth() <= m_imgLine.getWidth())
		{
			if (thisEntry >= m_leftmostTab)
			{	// This one is visible, we can render this one
				rect.setBounds(startX, startY, m_imgTab.getWidth(), m_imgTab.getHeight());
				if (rect.contains(m_mousePoint))
				{	// This is our man
					m_mouseHitOnTabEntry = thisEntry;
					return(true);
				}
				// Move to the next position
				startX	+= m_imgTab.getWidth();
				endX	+= m_imgTab.getWidth();
			}
			++thisEntry;

			// Move to next child entry
			child = child.getNext();
		}
		m_mouseHitOnTabEntry = -1;
		return(false);
	}

	public boolean wasMouseClickedOnAControl()
	{
		ResultsViewerLine rvl;
		int i;

		for (i = 0; i < m_renderList.size(); i++)
		{
			rvl = m_renderList.get(i);
			if (rvl.m_lblLine != null && rvl.m_lblLine == m_mouseLblLine)
			{	// This is the line, see where we are
				if (rvl.m_rectCollapse.contains(m_mousePoint))
				{	// It's happened inside the collapse point
					m_mouseHitOnControl		= rvl;
					m_mouseHitOnCollapse	= true;
					return(true);

				} else if (rvl.m_rectNamebox.contains(m_mousePoint)) {
					// It's happened inside the namebox
					m_mouseHitOnControl		= rvl;
					m_mouseHitOnNamebox		= true;
					return(true);

				} else if (rvl.m_rectScorebox.contains(m_mousePoint)) {
					// It's happened inside the scorebox
					m_mouseHitOnControl		= rvl;
					m_mouseHitOnScorebox	= true;
					return(true);

				} else if (rvl.m_rectTabSpace.contains(m_mousePoint)) {
					// It's happened inside the tab area
					// See if it hit onto a tab, or was on the space between
					if (rvl.wasMouseClickedOnATab())
					{	// The wasMouseClickedOnATab() method sets m_mouseHitOnControl to the appropriate child
						m_mouseHitOnControl		= rvl;
						m_mouseHitOnTabSpace	= true;
						return(true);
					}
				}
			}
		}
		// If we get here, it wasn't found
		return(false);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		m_mouseHitOnCollapse	= false;
		m_mouseHitOnNamebox		= false;
		m_mouseHitOnScorebox	= false;
		m_mouseHitOnTabSpace	= false;
		m_mouseHitOnTabEntry	= -1;
		m_mouseLblLine			= (JLabel)e.getComponent();
		m_mousePoint.setLocation(e.getPoint());

		if (wasMouseClickedOnAControl())
		{	// The mouse was clicked on a control?
			if (m_mouseHitOnCollapse)
			{	// Mouse was clicked on this collapse
				// Toggle the expanded condition
				// Select for graphing and navigation
				m_mouseHitOnControl.toggleExpandedState();
				m_mouseHitOnControl.setSelectedTab(-1);
				m_mouseHitOnControl.setSelectedForGraph(true);
				m_mouseHitOnControl.setSelectedForNavigation(true);
				m_parent.renderBottomAndGraph();

			} else if (m_mouseHitOnNamebox) {
				// Mouse was clicked on this Namebox
				// Select for navigation and graphing
				m_mouseHitOnControl.setSelectedTab(-1);
				m_mouseHitOnControl.setSelectedForGraph(true);
				m_mouseHitOnControl.setSelectedForNavigation(true);
				m_parent.renderBottomAndGraph();

			} else if (m_mouseHitOnScorebox) {
				// Mouse was clicked on this Scorebox
				// Select for navigation and graphing
				m_mouseHitOnControl.setSelectedTab(-1);
				m_mouseHitOnControl.setSelectedForGraph(true);
				m_mouseHitOnControl.setSelectedForNavigation(true);
				m_parent.renderBottomAndGraph();

			} else if (m_mouseHitOnTabSpace) {
				// Mouse was clicked on the tab space area
				// Determine which tab is visible
				// Select for navigation and graphing
				if (m_mouseHitOnTabEntry != -1)
				{	// We found the tab they clicked on
					m_mouseHitOnControl.setSelectedTab(m_mouseHitOnTabEntry);
					m_mouseHitOnControl.setSelectedForGraph(true);
					m_mouseHitOnControl.setSelectedForNavigation(true);
					m_parent.renderBottomAndGraph();
				}
			}
		}
		// We ignore it
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

// REMEMBER need to process this by scrolling appropriately
		scroll = e.getScrollAmount();
	}

	public void setNext(ResultsViewerLine next)					{	m_next	= next;						}
	public void setChild(ResultsViewerLine child)				{	m_child	= child;					}
	public void setExpanded(boolean b)							{	m_lineExpanded = b;					}
	public void setSelectedTab(int value)						{	m_selectedTab = value;				}
	public void setLeftmostTab(int value)						{	m_leftmostTab = value;				}
	public void setJustSelectedForGraph(boolean isGraph)		{	m_lineSelectedGraph = isGraph;		}
	public void setJustSelectedForNavigation(boolean isNav)		{	m_lineSelectedNav = isNav;			}
	public void setRenderVisible(boolean isRenderVisible)		{	m_renderVisible = isRenderVisible;	}
	public void setRenderLevel(int level)						{	m_renderLevel = level;				}

	public void setSelectedForGraph(boolean isGraph)
	{
		if (isGraph)
		{
			m_parent.setAllEntriesUnselectedForGraph(m_parent.getRootRVL());
			m_parent.setGraphLeg(this);
		}
		m_lineSelectedGraph = isGraph;
	}

	public void setSelectedForNavigation(boolean isNav)
	{
		int children;

		if (isNav)
		{
			m_parent.setAllEntriesUnselectedForNavigation(m_parent.getRootRVL());
			m_parent.setNavigationLeg(this);
			children = countChildren();
			if (m_selectedTab > children - 1)
				m_selectedTab = children - 1;
		}
		m_lineSelectedNav = isNav;
	}

	public ResultsViewerLine getNext()				{	return(m_next);						}
	public ResultsViewerLine getChild()				{	return(m_child);					}
	public Xml getNode()							{	return(m_node);						}
	public double getScore()						{	return(m_score);					}
	public String getName()							{	return(m_name);						}
	public String getShortName()					{	return(m_shortname);				}
	public boolean isExpanded()						{	return(m_lineExpanded);				}
	public boolean isSelectedForGraph()				{	return(m_lineSelectedGraph);		}
	public boolean isSelectedForNavigation()		{	return(m_lineSelectedNav);			}
	public boolean wasSuccessful()					{	return(m_success);					}
	public int getLeftmostTab()						{	return(m_leftmostTab);				}
	public int getSelectedTab()						{	return(m_selectedTab);				}
	public boolean isRenderVisible()				{	return(m_renderVisible);			}
	public int getRenderLevel()						{	return(m_renderLevel);				}
	public AlphaImage getSubcomponentImage()		{	return(m_subcomponentImage);		}
	public int getCollpaseHeight()					{	return(m_imgCollapse.getHeight());	}

	private ResultsViewer					m_parent;			// Parent class for the results viewer itself
	private JLayeredPane					m_pan;				// The pane labels are added to
	private JLabel							m_lblBottom;		// The bottom area to extract images from
	private AlphaImage						m_imgBackground;	// The background image to extract from
	private JLabel							m_lblGraphInternal;	// For the coordinates
	private AlphaImage						m_imgGraphInternal;	// Area used for the graph image
	private AlphaImage						m_imgGraphMask;		// Mask to make the graph visualize properly
	private ResultsViewerLine				m_next;				// Next line in the chain at this level
	private ResultsViewerLine				m_child;			// First child from this entry
	private JLabel							m_lblLine;			// The label for the line
	private AlphaImage						m_imgLine;			// The background image
	private int								m_level;			// 0=summary, 1=suite, 2=scenario, 3=molecule, 4=atom, 5=worklet
	private double							m_score;			// Exact score at this level
	private Xml								m_node;				// Root node for this entry
	private List<String>					m_filterTags;		// Tags for the filter for this entry
	private boolean							m_lineSelectedGraph;// Is this line selected for a graph? (is this the last line the user clicked on the collapse, namebox or scorebox portions?)
	private boolean							m_lineSelectedNav;	// Is this line selected for navigation? (is the user moving around on this line?)
	private boolean							m_lineExpanded;		// is this item expanded? (are its children shown)
	private int								m_color;			// Color to highlight the background of the collapse, namebox and scorebox
	private int								m_selectedColor;	// Color to highlight the selected entry (a shade of blue)
	private static int						m_leftmostTab;		// Number of the left-most tab that's visible on this line
	private static int						m_selectedTab;		// Number of the selected tab
	private String							m_name;
	private String							m_shortname;

	private static AlphaImage				m_imgGraph;			// The graph generated for this image
	private static JLabel					m_lblGraph;			// Label covering the graph used for rendering
	private static AnimateImageTask			m_aitGrapher;		// Shows animated portions of the graph legs in a cycle

	// Static images for drawing components
	private static AlphaImage				m_imgCollapse;
	private static AlphaImage				m_imgNamebox0;
	private static AlphaImage				m_imgNamebox1;
	private static AlphaImage				m_imgNamebox2;
	private static AlphaImage				m_imgNamebox3;
	private static AlphaImage				m_imgNamebox4;
	private static AlphaImage				m_imgNamebox5;
	private static AlphaImage				m_imgScorebox;
	private static AlphaImage				m_imgTab;
	private static AlphaImage				m_imgTabTopBackground;
	private static AlphaImage				m_imgTabBottomBackground;
	private static AlphaImage				m_imgTabIgnored;

	private static AlphaImage				m_imgCollapseSelected;
	private static AlphaImage				m_imgCollapseAnchored;
	private static AlphaImage				m_imgNamebox0Selected;
	private static AlphaImage				m_imgNamebox1Selected;
	private static AlphaImage				m_imgNamebox2Selected;
	private static AlphaImage				m_imgNamebox3Selected;
	private static AlphaImage				m_imgNamebox4Selected;
	private static AlphaImage				m_imgNamebox5Selected;
	private static AlphaImage				m_imgScoreboxSelected;
	private static AlphaImage				m_imgTabSelected;

	private static Font						m_nameboxFont;
	private static Color					m_nameboxTextColor;

	private static Font						m_tabTopFont;
	private static Color					m_tabTopTextColor;
	private static Font						m_tabBottomFont;
	private static Color					m_tabBottomTextColor;

	private static List<ResultsViewerLine>	m_renderList;
	private static List<ResultsViewerLine>	m_subcomponentRenderList;
	private AlphaImage						m_subcomponentImage;		// Image rendered for this subcomponent
	private Rectangle						m_subcomponentScoreRect;	// Rectangle size holding the score portion
	private static int						m_navEntry;
	private static int						m_navEntryLastTime;
	private boolean							m_renderVisible;			// Enabled or disabled at each render depending on its relationship to a parent lineExpanded condition
	private boolean							m_ignored;					// Is this score ignored?
	private boolean							m_tested;					// Was this entry even tested?
	private boolean							m_success;					// Was this entry successful in completing the benchmark?
	private int								m_renderLevel;				// Set for descendants of the graph line when the nav line is different, used to determine which lines are to be physically rendered, and which lines are to be rolled up for display.  Value=0 render, value=1 sum up at first level, value=2 at second level, and so on.
	private int								m_renderedLevelCount;		// Each time a level is rendered, the count goes up, used to determine when to scale
	private int								m_iterativeColor;

	// For mouse operations
	private static ResultsViewerLine		m_mouseHitOnControl;		// The control where the mouse was hit on
	private static JLabel					m_mouseLblLine;				// The m_lblLine the mouse was clicked on
	private static Point					m_mousePoint;				// Where the mouse was clicked
	private static boolean					m_mouseHitOnCollapse;
	private static boolean					m_mouseHitOnNamebox;
	private static boolean					m_mouseHitOnScorebox;
	private static boolean					m_mouseHitOnTabSpace;
	private static int						m_mouseHitOnTabEntry;		// The tab entry the mouse was clicked on
	private Rectangle						m_rectCollapse;
	private Rectangle						m_rectNamebox;
	private Rectangle						m_rectScorebox;
	private Rectangle						m_rectTabSpace;
}
