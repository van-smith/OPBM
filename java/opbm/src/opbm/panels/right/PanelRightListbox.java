/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for listbox items, which hold the primary
 * data items where things are added.  The listbox differs from the lookupbox
 * in that items are intended to be added in the listbox, and not just used as
 * reference.
 *
 * Typically a PanelRight edit screen will have a listbox, and possibly multiple
 * lookupboxes based on data contained as child "options" items stored beneath
 * the data entry in the XML file.
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

package opbm.panels.right;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import opbm.common.Commands;
import opbm.common.Macros;
import opbm.Opbm;
import opbm.common.Utils;
import opbm.common.Xml;

/**
 * Handles all <code>_TYPE_LISTBOX</code> processing specifically related to
 * the literal JList itself.  Has a parent <code>PanelRightItem</code> which
 * handles some related operations, though the relationship between member
 * functions here and on the parent should be straight-forward, typically with
 * identical names.
 */
public class PanelRightListbox
					implements	ListSelectionListener,
								MouseListener,
								KeyListener
{
	/**
	 * Constructor initializes the class, stores parent items for the panel,
	 * <code>PanelRight</code> and <code>PanelRightItem</code>.
	 *
	 * @param opbm master class parent object
	 * @param parentPanel panel the controls will be added to (for add, delete,
	 * clone buttons)
	 * @param parentPR <code>PanelRight</code> parent object
	 * @param parentPRI <code>PanelRightItem</code> parent object
	 */
	public PanelRightListbox(Opbm			opbm,
							 JPanel			parentPanel,
							 PanelRight		parentPR,
							 PanelRightItem	parentPRI,
							 Commands		commandMaster,
							 Macros			macroMaster)
	{
		m_xmlListboxMaster	= null;
		m_visible			= false;
		m_opaque			= true;
		m_foreground		= Color.BLACK;
		m_background		= Color.WHITE;
		m_font				= null;
		m_lastIndex			= -1;
		m_width				= 0;
		m_height			= 0;
		m_x					= 0;
		m_y					= 0;
		m_opbm				= opbm;
		m_parentPanel		= parentPanel;
		m_parentPR			= parentPR;
		m_parentPRI			= parentPRI;
		m_commandMaster		= commandMaster;
		m_macroMaster		= macroMaster;
		m_lastMillisecond	= 0;
		m_listBox			= null;
		m_listBoxScroller	= null;
		m_listBoxXml		= null;
		m_listBoxName		= "";
		m_listBoxButtons	= "";
		m_listByP1			= "";
		m_listByP2			= "";
		m_listByP3			= "";
		m_listByP4			= "";
		m_listByP5			= "";
		m_listByP6			= "";
		m_listByP7			= "";
		m_listByP8			= "";
		m_listByP9			= "";
		m_listByP10			= "";

		m_dblClickCommand	= "";
		m_dblClickP1		= "";
		m_dblClickP2		= "";
		m_dblClickP3		= "";
		m_dblClickP4		= "";
		m_dblClickP5		= "";
		m_dblClickP6		= "";
		m_dblClickP7		= "";
		m_dblClickP8		= "";
		m_dblClickP9		= "";
		m_dblClickP10		= "";

		m_enterCommand	= "";
		m_enterP1		= "";
		m_enterP2		= "";
		m_enterP3		= "";
		m_enterP4		= "";
		m_enterP5		= "";
		m_enterP6		= "";
		m_enterP7		= "";
		m_enterP8		= "";
		m_enterP9		= "";
		m_enterP10		= "";

		m_onSelectCommand	= "";
		m_onSelectP1		= "";
		m_onSelectP2		= "";
		m_onSelectP3		= "";
		m_onSelectP4		= "";
		m_onSelectP5		= "";
		m_onSelectP6		= "";
		m_onSelectP7		= "";
		m_onSelectP8		= "";
		m_onSelectP9		= "";
		m_onSelectP10		= "";

		m_listBoxUpCommand = "";
		m_listBoxUpCommandP1 = "";
		m_listBoxUpCommandP2 = "";
		m_listBoxUpCommandP3 = "";
		m_listBoxUpCommandP4 = "";
		m_listBoxUpCommandP5 = "";
		m_listBoxUpCommandP6 = "";
		m_listBoxUpCommandP7 = "";
		m_listBoxUpCommandP8 = "";
		m_listBoxUpCommandP9 = "";
		m_listBoxUpCommandP10 = "";

		m_listBoxDownCommand = "";
		m_listBoxDownCommandP1 = "";
		m_listBoxDownCommandP2 = "";
		m_listBoxDownCommandP3 = "";
		m_listBoxDownCommandP4 = "";
		m_listBoxDownCommandP5 = "";
		m_listBoxDownCommandP6 = "";
		m_listBoxDownCommandP7 = "";
		m_listBoxDownCommandP8 = "";
		m_listBoxDownCommandP9 = "";
		m_listBoxDownCommandP10 = "";
	}

	/**
	 * Setter sets the associated <code>_TYPE_LISTBOX</code> listing parameters
	 * Populated from PanelFactory, or dynamically as list filters change.
	 * The list is populated dynamically when called to update its list contents.
	 *
	 * @param p1 first list-by parameter
	 * @param p2 second list-by parameter
	 * @param p3 third list-by parameter
	 * @param p4 fourth list-by parameter
	 * @param p5 fifth list-by parameter
	 * @param p6 sixth list-by parameter
	 * @param p7 seventh list-by parameter
	 * @param p8 eighth list-by parameter
	 * @param p9 ninth list-by parameter
	 * @param p10 tenth list-by parameter
	 */
	public void setListBy(String p1,
						  String p2,
						  String p3,
						  String p4,
						  String p5,
						  String p6,
						  String p7,
						  String p8,
						  String p9,
						  String p10)
	{
		m_listByP1	= p1;
		m_listByP2	= p2;
		m_listByP3	= p3;
		m_listByP4	= p4;
		m_listByP5	= p5;
		m_listByP6	= p6;
		m_listByP7	= p7;
		m_listByP8	= p8;
		m_listByP9	= p9;
		m_listByP10	= p10;
	}

	/**
	 * Setter sets the associated <code>_TYPE_LISTBOX</code> listing parameters
	 * Populated from PanelFactory, or dynamically as list filters change.
	 * The list is populated dynamically when called to update its list contents.
	 *
	 * @param command command to execute
	 * @param p1 first list-by parameter
	 * @param p2 second list-by parameter
	 * @param p3 third list-by parameter
	 * @param p4 fourth list-by parameter
	 * @param p5 fifth list-by parameter
	 * @param p6 sixth list-by parameter
	 * @param p7 seventh list-by parameter
	 * @param p8 eighth list-by parameter
	 * @param p9 ninth list-by parameter
	 * @param p10 tenth list-by parameter
	 */
	public void setDblClick(String command,
							String p1,
							String p2,
							String p3,
							String p4,
							String p5,
							String p6,
							String p7,
							String p8,
							String p9,
							String p10)
	{
		m_dblClickCommand	= command;
		m_dblClickP1		= p1;
		m_dblClickP2		= p2;
		m_dblClickP3		= p3;
		m_dblClickP4		= p4;
		m_dblClickP5		= p5;
		m_dblClickP6		= p6;
		m_dblClickP7		= p7;
		m_dblClickP8		= p8;
		m_dblClickP9		= p9;
		m_dblClickP10		= p10;
	}

	/**
	 * Setter sets the associated <code>_TYPE_LISTBOX</code> listing parameters
	 * Populated from PanelFactory, or dynamically as list filters change.
	 * The list is populated dynamically when called to update its list contents.
	 *
	 * @param command command to execute
	 * @param p1 first list-by parameter
	 * @param p2 second list-by parameter
	 * @param p3 third list-by parameter
	 * @param p4 fourth list-by parameter
	 * @param p5 fifth list-by parameter
	 * @param p6 sixth list-by parameter
	 * @param p7 seventh list-by parameter
	 * @param p8 eighth list-by parameter
	 * @param p9 ninth list-by parameter
	 * @param p10 tenth list-by parameter
	 */
	public void setEnter(String command,
						 String p1,
						 String p2,
						 String p3,
						 String p4,
						 String p5,
						 String p6,
						 String p7,
						 String p8,
						 String p9,
						 String p10)
	{
		m_enterCommand	= command;
		m_enterP1		= p1;
		m_enterP2		= p2;
		m_enterP3		= p3;
		m_enterP4		= p4;
		m_enterP5		= p5;
		m_enterP6		= p6;
		m_enterP7		= p7;
		m_enterP8		= p8;
		m_enterP9		= p9;
		m_enterP10		= p10;
	}

	/**
	 * Setter sets the associated <code>_TYPE_LISTBOX</code> onSelect parameters
	 * Populated from PanelFactory, or dynamically as list filters change.
	 * The list is populated dynamically when called to update its list contents.
	 *
	 * @param command command to execute
	 * @param p1 first parameter
	 * @param p2 second parameter
	 * @param p3 third parameter
	 * @param p4 fourth parameter
	 * @param p5 fifth parameter
	 * @param p6 sixth parameter
	 * @param p7 seventh parameter
	 * @param p8 eighth parameter
	 * @param p9 ninth parameter
	 * @param p10 tenth parameter
	 */
	public void setOnSelect(String command,
							String p1,
							String p2,
							String p3,
							String p4,
							String p5,
							String p6,
							String p7,
							String p8,
							String p9,
							String p10)
	{
		m_onSelectCommand	= command;
		m_onSelectP1		= p1;
		m_onSelectP2		= p2;
		m_onSelectP3		= p3;
		m_onSelectP4		= p4;
		m_onSelectP5		= p5;
		m_onSelectP6		= p6;
		m_onSelectP7		= p7;
		m_onSelectP8		= p8;
		m_onSelectP9		= p9;
		m_onSelectP10		= p10;
	}

	/**
	 * Specifies the add button is allowed on the <code>_TYPE_LISTBOX</code>
	 * control.
	 *
	 * @param command command to execute when the add button is clicked
	 * @param p1 first parameter
	 * @param p2 second parameter
	 * @param p3 third parameter
	 * @param p4 fourth parameter
	 * @param p5 fifth parameter
	 * @param p6 sixth parameter
	 * @param p7 seventh parameter
	 * @param p8 eighth parameter
	 * @param p9 ninth parameter
	 * @param p10 tenth parameter
	 */
	public void setAddButton(String command,
							 String p1,
							 String p2,
							 String p3,
							 String p4,
							 String p5,
							 String p6,
							 String p7,
							 String p8,
							 String p9,
							 String p10)
	{
		// The m_listBoxButtons string is used to identify which buttons are present, currently can be "+", "-" and "c" for "add, delete and clone"
		if (!command.isEmpty()) {
			m_listBoxButtons			= m_listBoxButtons.toLowerCase().replace("+", " ");
			m_listBoxButtons			+= "+";
			m_listBoxAddCommand			= command;
			m_listBoxAddCommandP1		= p1;
			m_listBoxAddCommandP2		= p2;
			m_listBoxAddCommandP3		= p3;
			m_listBoxAddCommandP4		= p4;
			m_listBoxAddCommandP5		= p5;
			m_listBoxAddCommandP6		= p6;
			m_listBoxAddCommandP7		= p7;
			m_listBoxAddCommandP8		= p8;
			m_listBoxAddCommandP9		= p9;
			m_listBoxAddCommandP10		= p10;
		}
	}

	/**
	 * Specifies the delete button is allowed on the <code>_TYPE_LISTBOX</code>
	 * control.
	 *
	 * @param command command to execute when the delete button is clicked
	 * @param p1 first parameter
	 * @param p2 second parameter
	 * @param p3 third parameter
	 * @param p4 fourth parameter
	 * @param p5 fifth parameter
	 * @param p6 sixth parameter
	 * @param p7 seventh parameter
	 * @param p8 eighth parameter
	 * @param p9 ninth parameter
	 * @param p10 tenth parameter
	 */
	public void setDeleteButton(String command,
								String p1,
								String p2,
								String p3,
								String p4,
								String p5,
								String p6,
								String p7,
								String p8,
								String p9,
								String p10)
	{
		// The m_listBoxButtons string is used to identify which buttons are present, currently can be "+", "-" and "c" for "add, delete and clone"
		if (!command.isEmpty()) {
			m_listBoxButtons			= m_listBoxButtons.toLowerCase().replace("-", " ");
			m_listBoxButtons			+= "-";
			m_listBoxDeleteCommand		= command;
			m_listBoxDeleteCommandP1	= p1;
			m_listBoxDeleteCommandP2	= p2;
			m_listBoxDeleteCommandP3	= p3;
			m_listBoxDeleteCommandP4	= p4;
			m_listBoxDeleteCommandP5	= p5;
			m_listBoxDeleteCommandP6	= p6;
			m_listBoxDeleteCommandP7	= p7;
			m_listBoxDeleteCommandP8	= p8;
			m_listBoxDeleteCommandP9	= p9;
			m_listBoxDeleteCommandP10	= p10;
		}
	}

	/**
	 * Specifies the clone button is allowed on the <code>_TYPE_LISTBOX</code>
	 * control.
	 *
	 * @param command command to execute when the clone button is clicked
	 * @param p1 first parameter
	 * @param p2 second parameter
	 * @param p3 third parameter
	 * @param p4 fourth parameter
	 * @param p5 fifth parameter
	 * @param p6 sixth parameter
	 * @param p7 seventh parameter
	 * @param p8 eighth parameter
	 * @param p9 ninth parameter
	 * @param p10 tenth parameter
	 */
	public void setCloneButton(String command,
							   String p1,
							   String p2,
							   String p3,
							   String p4,
							   String p5,
							   String p6,
							   String p7,
							   String p8,
							   String p9,
							   String p10)
	{
		// The m_listBoxButtons string is used to identify which buttons are present, currently can be "+", "-" and "c" for "add, delete and clone"
		if (!command.isEmpty()) {
			m_listBoxButtons			= m_listBoxButtons.toLowerCase().replace("c", " ");
			m_listBoxButtons			+= "c";
			m_listBoxCloneCommand		= command;
			m_listBoxCloneCommandP1		= p1;
			m_listBoxCloneCommandP2		= p2;
			m_listBoxCloneCommandP3		= p3;
			m_listBoxCloneCommandP4		= p4;
			m_listBoxCloneCommandP5		= p5;
			m_listBoxCloneCommandP6		= p6;
			m_listBoxCloneCommandP7		= p7;
			m_listBoxCloneCommandP8		= p8;
			m_listBoxCloneCommandP9		= p9;
			m_listBoxCloneCommandP10	= p10;
		}
	}

	/**
	 * Specifies the up button is allowed on the <code>_TYPE_LISTBOX</code>
	 * control.
	 *
	 * @param command command to execute when the button is clicked
	 * @param p1 first parameter
	 * @param p2 second parameter
	 * @param p3 third parameter
	 * @param p4 fourth parameter
	 * @param p5 fifth parameter
	 * @param p6 sixth parameter
	 * @param p7 seventh parameter
	 * @param p8 eighth parameter
	 * @param p9 ninth parameter
	 * @param p10 tenth parameter
	 */
	public void setUpButton(String command,
							String p1,
							String p2,
							String p3,
							String p4,
							String p5,
							String p6,
							String p7,
							String p8,
							String p9,
							String p10)
	{
		// The m_listBoxButtons string is used to identify which buttons are present, currently can be "+", "-" and "z" for "add, subtract and zoom"
		if (!command.isEmpty()) {
			m_listBoxButtons			= m_listBoxButtons.toLowerCase().replace("u", " ");
			m_listBoxButtons			+= "u";
			m_listBoxUpCommand			= command;
			m_listBoxUpCommandP1		= p1;
			m_listBoxUpCommandP2		= p2;
			m_listBoxUpCommandP3		= p3;
			m_listBoxUpCommandP4		= p4;
			m_listBoxUpCommandP5		= p5;
			m_listBoxUpCommandP6		= p6;
			m_listBoxUpCommandP7		= p7;
			m_listBoxUpCommandP8		= p8;
			m_listBoxUpCommandP9		= p9;
			m_listBoxUpCommandP10		= p10;
		}
	}

	/**
	 * Specifies the down button is allowed on the <code>_TYPE_LISTBOX</code>
	 * control.
	 *
	 * @param command command to execute when the button is clicked
	 * @param p1 first parameter
	 * @param p2 second parameter
	 * @param p3 third parameter
	 * @param p4 fourth parameter
	 * @param p5 fifth parameter
	 * @param p6 sixth parameter
	 * @param p7 seventh parameter
	 * @param p8 eighth parameter
	 * @param p9 ninth parameter
	 * @param p10 tenth parameter
	 */
	public void setDownButton(String command,
							  String p1,
							  String p2,
							  String p3,
							  String p4,
							  String p5,
							  String p6,
							  String p7,
							  String p8,
							  String p9,
							  String p10)
	{
		// The m_listBoxButtons string is used to identify which buttons are present, currently can be "+", "-" and "z" for "add, subtract and zoom"
		if (!command.isEmpty()) {
			m_listBoxButtons			= m_listBoxButtons.toLowerCase().replace("d", " ");
			m_listBoxButtons			+= "d";
			m_listBoxDownCommand		= command;
			m_listBoxDownCommandP1		= p1;
			m_listBoxDownCommandP2		= p2;
			m_listBoxDownCommandP3		= p3;
			m_listBoxDownCommandP4		= p4;
			m_listBoxDownCommandP5		= p5;
			m_listBoxDownCommandP6		= p6;
			m_listBoxDownCommandP7		= p7;
			m_listBoxDownCommandP8		= p8;
			m_listBoxDownCommandP9		= p9;
			m_listBoxDownCommandP10		= p10;
		}
	}

	/**
	 * Specifies the xml filename to load for the <code>_TYPE_LISTBOX</code> control.
	 * @param fileName xml file to load for listbox
	 */
	public void setFileName(String fileName) {
		m_listBoxFileName = fileName;
	}

	/**
	 * Save the changes
	 */
	public void saveListBoxData()
	{
		m_listBoxXml.saveNode(m_macroMaster.parseMacros("$scripts.xml$"));
	}

	/**
	 * Specifies where source data from which the <code>_TYPE_LISTBOX</code>
	 * control is populated.
	 *
	 * @param location absolute location within the xml file to reach the list of data items
	 */
	public void setLocation(String location) {
		m_listBoxLocation = location;
	}

	/**
	 * Specifies which entries within location will be pulled (allows a filter
	 * within a larger data set, to only pull out those named tags) for the
	 * <code>_TYPE_LISTBOX</code> controls.
	 *
	 * @param forEach specifies relative tag within location for enumerated items
	 */
	public void setForEach(String forEach) {
		m_listBoxForEach = forEach;
	}

	/**
	 * Positions the listbox on the specified entry (if found)
	 *
	 * @param source <code>Xml</code> from the non-zoom, which will relate
	 * back directly to the entry to display (if found)
	 */
	public void positionListboxTo(Xml source)
	{
		int i;

		for (i = 0; i < m_xmlListboxMaster.size(); i++)
		{
			if (m_xmlListboxMaster.get(i) == source ||
				m_xmlListboxMaster.get(i).getAttribute("name").equalsIgnoreCase(source.getAttribute("name")))
			{
				// Found our guy
				select(i);
				return;
			}
		}
		// If we get here, it wasn't found
	}

	/**
	 * Updates the ListBox (typically during instantiation) to set the add,
	 * delete and clone buttons. Can be used dynamically to update the buttons
	 * as real-world conditions change.
	 */
	public void updateListBox()
	{
		boolean add, delete, clone, up, down;
		int buttonWidth = 0;
		int count = 0;
		int width, left, top;
		boolean destroyAdd		= true;
		boolean destroyDelete	= true;
		boolean destroyClone	= true;
		boolean destroyUp		= true;
		boolean destroyDown		= true;
		Dimension d;
		Font f;
		Insets inset;

		if (m_opbm.isFontOverride())
			f	= new Font("Arial", Font.BOLD, 12);
		else
			f	= new Font("Calibri", Font.BOLD, 14);

		add		= m_listBoxButtons.contains("+");
		delete	= m_listBoxButtons.contains("-");
		clone	= m_listBoxButtons.toLowerCase().contains("c");
		up		= m_listBoxButtons.toLowerCase().contains("u");
		down	= m_listBoxButtons.toLowerCase().contains("d");
		if (add || delete || clone || up || down)
		{
			// We must add new buttons and readjust
			if (add)
			{
				// The add button exists
				destroyAdd = false;
				if (m_listBoxAdd == null)
				{
					m_listBoxAdd = new JButton("+");
					m_listBoxAdd.setFont(f);
					inset = m_listBoxAdd.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_listBoxAdd.setMargin(inset);
					m_listBoxAdd.addMouseListener(this);
					m_parentPanel.add(m_listBoxAdd);
				}
				buttonWidth += 30;
				++count;
			}

			if (delete)
			{
				// The delte button exists
				destroyDelete = false;
				if (m_listBoxDelete == null)
				{
					m_listBoxDelete = new JButton("-");
					m_listBoxDelete.setFont(f);
					inset = m_listBoxDelete.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_listBoxDelete.setMargin(inset);
					m_listBoxDelete.addMouseListener(this);
					m_parentPanel.add(m_listBoxDelete);
				}
				buttonWidth += 30;
				++count;
			}

			if (clone)
			{
				// The clone button exists
				destroyClone = false;
				if (m_listBoxClone == null)
				{
					m_listBoxClone = new JButton("Clone");
					m_listBoxClone.setFont(f);
					inset = m_listBoxClone.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_listBoxClone.setMargin(inset);
					m_listBoxClone.addMouseListener(this);
					m_parentPanel.add(m_listBoxClone);
				}
				buttonWidth += 55;
				++count;
			}

			if (up)
			{
				// The up button exists
				destroyUp = false;
				if (m_listBoxUp == null)
				{
					m_listBoxUp = new JButton("Up");
					m_listBoxUp.setFont(f);
					inset = m_listBoxUp.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_listBoxUp.setMargin(inset);
					m_listBoxUp.addMouseListener(this);
					m_parentPanel.add(m_listBoxUp);
				}
				buttonWidth += 40;
				++count;
			}

			if (down)
			{
				// The up button exists
				destroyDown = false;
				if (m_listBoxDown == null)
				{
					m_listBoxDown = new JButton("Dn");
					m_listBoxDown.setFont(f);
					inset = m_listBoxDown.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_listBoxDown.setMargin(inset);
					m_listBoxDown.addMouseListener(this);
					m_parentPanel.add(m_listBoxDown);
				}
				buttonWidth += 40;
				++count;
			}

			// Determine the relative center location of the defined buttons
			width		= m_width;
			buttonWidth += ((count - 1) * 5);
			left		= m_x + ((width - buttonWidth) / 2);
			top			= m_y + m_height - 25;
			if (add) {
				d = new Dimension(30,25);
				m_listBoxAdd.setLocation(left, top);
				m_listBoxAdd.setMinimumSize(d);
				m_listBoxAdd.setMaximumSize(d);
				m_listBoxAdd.setPreferredSize(d);
				m_listBoxAdd.setSize(d);
				left += d.getWidth() + 5;
			}

			if (delete) {
				d = new Dimension(30,25);
				m_listBoxDelete.setLocation(left, top);
				m_listBoxDelete.setMinimumSize(d);
				m_listBoxDelete.setMaximumSize(d);
				m_listBoxDelete.setPreferredSize(d);
				m_listBoxDelete.setSize(d);
				left += d.getWidth() + 5;
			}

			if (clone) {
				d = new Dimension(55,25);
				m_listBoxClone.setLocation(left, top);
				m_listBoxClone.setMinimumSize(d);
				m_listBoxClone.setMaximumSize(d);
				m_listBoxClone.setPreferredSize(d);
				m_listBoxClone.setSize(d);
				left += d.getWidth() + 5;
			}

			if (up) {
				d = new Dimension(40,25);
				m_listBoxUp.setLocation(left, top);
				m_listBoxUp.setMinimumSize(d);
				m_listBoxUp.setMaximumSize(d);
				m_listBoxUp.setPreferredSize(d);
				m_listBoxUp.setSize(d);
				left += d.getWidth() + 5;
			}

			if (down) {
				d = new Dimension(40,25);
				m_listBoxDown.setLocation(left, top);
				m_listBoxDown.setMinimumSize(d);
				m_listBoxDown.setMaximumSize(d);
				m_listBoxDown.setPreferredSize(d);
				m_listBoxDown.setSize(d);
				left += d.getWidth() + 5;
			}

			// Adjust the listbox size
			m_height -= 28;
		}
		// If the add button exists, remove it
		if (m_listBoxAdd != null) {
			if (destroyAdd) {
				m_parentPanel.remove(m_listBoxAdd);
				m_listBoxAdd = null;
			} else {
				m_listBoxAdd.setVisible(true);
			}
		}

		// If the delete button exists, remove it
		if (m_listBoxDelete != null) {
			if (destroyDelete) {
				m_parentPanel.remove(m_listBoxDelete);
				m_listBoxDelete = null;
			} else {
				m_listBoxDelete.setVisible(true);
			}
		}

		// If the clone button exists, remove it
		if (m_listBoxClone != null) {
			if (destroyClone) {
				m_parentPanel.remove(m_listBoxClone);
				m_listBoxClone = null;
			} else {
				m_listBoxClone.setVisible(true);
			}
		}

		// If the up button exists, remove it
		if (m_listBoxUp != null) {
			if (destroyUp) {
				m_parentPanel.remove(m_listBoxUp);
				m_listBoxUp = null;
			} else {
				m_listBoxUp.setVisible(true);
			}
		}

		// If the down button exists, remove it
		if (m_listBoxDown != null) {
			if (destroyDown) {
				m_parentPanel.remove(m_listBoxDown);
				m_listBoxDown = null;
			} else {
				m_listBoxDown.setVisible(true);
			}
		}
	}

	/**
	 * Updates the listbox's associated array after something has been added,
	 * deleted or cloned, and then repaints the control.
	 */
	public void updateListBoxArray()
	{
		int saveIndex;

		if (m_listBox != null && m_xmlListboxMaster != null)
		{
			saveIndex = m_listBox.getSelectedIndex();
			m_listBox.setListData(m_xmlListboxMaster.toArray());

			if (saveIndex != -1 && saveIndex < m_xmlListboxMaster.size())
				m_listBox.setSelectedIndex(saveIndex);
			else
				m_listBox.setSelectedIndex(m_xmlListboxMaster.size() - 1);

			m_parentPR.updateRelativeToFields();
			m_listBox.repaint();
		}
	}

	/**
	 * Repaints the listbox control.
	 */
	public void repaintListBox() {
		m_listBox.repaint();
	}

	/**
	 * Renders the physical listbox, which allows it to load the file contents,
	 * create the node list, and populate and format the control.
	 */
	public void fillOrRefillListBoxArray()
	{
		int saveIndex;

		if (m_listBox != null)
			saveIndex = m_listBox.getSelectedIndex();
		else
			saveIndex = -1;

		// Grab the list of elements
		if (m_listBoxFileName.isEmpty())
		{
			// We're editing data directly from the scripts xml file that's already been loaded
			m_listBoxXml = m_opbm.getScriptsXml();

		} else {
			// We must load the specified file
			m_listBoxXml = Opbm.loadXml(m_listBoxFileName, m_opbm);
			if (m_listBoxXml == null)
			{
				m_listBoxXml = failedLoad();
				m_listBoxSource = "root.error";
			}

		}

		// Clear out any list that may already be there
		m_xmlListboxMaster = new ArrayList<Xml>(0);

		// Locate all the nodes within the xml node space
		Xml.getNodeList(m_xmlListboxMaster, m_listBoxXml, m_listBoxSource, false);
		if (m_listBox == null)
		{
			m_listBox = new JList();
			m_listBoxScroller = new JScrollPane(m_listBox);
			m_listBox.setSize(m_width, m_height);
			m_listBox.setFont(m_font);
			m_listBox.setLocation(m_x, m_y);
			m_listBox.setForeground(m_foreground);
			m_listBox.setBackground(m_background);
			m_listBox.setOpaque(m_opaque);
			PanelRightListboxRenderer prlr = new PanelRightListboxRenderer(m_listByP1, m_listByP2, m_listByP3, m_listByP4, m_listByP5, m_listByP6, m_listByP7, m_listByP8, m_listByP9, m_listByP10);
			if (prlr != null)
				m_listBox.setCellRenderer(prlr);
			m_listBox.setVisible(true);
			m_listBoxScroller.setSize(m_width, m_height);
			m_listBoxScroller.setLocation(m_x, m_y);
			m_listBoxScroller.setVisible(true);
			m_listBoxScroller.addKeyListener(this);
			m_listBox.addListSelectionListener(this);
			m_listBox.addMouseListener(this);
			m_listBox.addKeyListener(this);
			m_parentPanel.add(m_listBoxScroller);
		}
		m_listBox.setListData(m_xmlListboxMaster.toArray());
		if (m_xmlListboxMaster != null)
		{
			if (saveIndex != -1)
				m_listBox.setSelectedIndex(saveIndex);
			else
				m_listBox.setSelectedIndex(0);

			m_parentPR.updateRelativeToFields();
		}
	}

	/**
	 * After the user resizes the main window, this function is called to
	 * resize the listbox and its associated buttons (if any).
	 *
	 * @param newWidth
	 * @param newHeight
	 */
	public void afterWindowResize(int newWidth, int newHeight)
	{
		int diff;

		diff = newHeight - m_parentPR.getHeight();

		if (m_listBox != null) {
			m_listBox.setSize(m_listBox.getWidth(), m_listBox.getHeight() + diff);
			m_listBoxScroller.setSize(m_listBoxScroller.getWidth(), m_listBoxScroller.getHeight() + diff);

			if (m_listBoxAdd != null) {
				m_listBoxAdd.setLocation(m_listBoxAdd.getX(), m_listBoxAdd.getY() + diff);
			}

			if (m_listBoxDelete != null) {
				m_listBoxDelete.setLocation(m_listBoxDelete.getX(), m_listBoxDelete.getY() + diff);
			}

			if (m_listBoxClone != null) {
				m_listBoxClone.setLocation(m_listBoxClone.getX(), m_listBoxClone.getY() + diff);
			}

			if (m_listBoxUp != null) {
				m_listBoxUp.setLocation(m_listBoxUp.getX(), m_listBoxUp.getY() + diff);
			}

			if (m_listBoxDown != null) {
				m_listBoxDown.setLocation(m_listBoxDown.getX(), m_listBoxDown.getY() + diff);
			}
		}
	}

	/**
	 * If the specified file doesn't exist, creates an "error placeholder"
	 * to let the user know it wasn't loaded properly.
	 *
	 * @return the newly created error <code>Xml</code>
	 */
	public Xml failedLoad() {
		Xml root = new Xml("root", "");
		root.setFirstChild(new Xml("error", "Unable to load xml file"));
		return(root);
	}

	/** Called to physically remove the listbox, and any add, delete or clone
	 * buttons that are active.
	 */
	public void remove() {
		if (m_listBox != null)
			m_parentPanel.remove(m_listBox);

		if (m_listBoxAdd != null)
			m_parentPanel.remove(m_listBoxAdd);

		if (m_listBoxClone != null)
			m_parentPanel.remove(m_listBoxClone);

		if (m_listBoxDelete != null)
			m_parentPanel.remove(m_listBoxDelete);
	}

	/**
	 * Physically selects the specified index.
	 *
	 * @param index item to update
	 */
	public void select(int index)
	{
		if (m_parentPRI.getAutoUpdate())
		{
			if (m_listBox != null)
			{
				if (index >= 0 && m_listBox.getModel().getSize() > 0)
				{
					m_listBox.setSelectedIndex(index);
					doOnSelect();
				}
			}
		}
	}

	/**
	 * Specifies the relative path to the root node of the xml file to access
	 * the data element for the <code>_TYPE_LISTBOX</code> control.
	 *
	 * @param source dot source, as in <code>opbm.scriptdata.flows</code>
	 * @param relativeTo ignored
	 */
	public void setSource(String	source,
						  String	relativeTo)
	{
		m_listBoxSource = source;
	}

	/**
	 * Specifies the relative path to the root node of the xml file to access
	 * the template data element for the <code>_TYPE_LISTBOX</code> control.
	 *
	 * @param template dot source, as in <code>opbm.scriptdata.templates.flow</code>
	 * @param relativeTo ignored
	 */
	public void setTemplate(String	template,
							String	relativeTo)
	{
		m_listBoxTemplate = template;
	}

	/**
	 * Assigns the name defined in edits.xml to the control
	 * @param name
	 */
	public void setName(String name)
	{
		m_listBoxName = name;
	}

	/**
	 * Searches through <code>PanelRight</code>'s items to see if the specified
	 * <code>_TYPE_LISTBOX</code> is identified by name, and if so, then returns
	 * its currently selected node.
	 * @return <code>Xml</code> for the selected item in the listbox
	 */
	public Xml getListboxFirstChildNode()
	{
		// See if there's an active Xml list and a selected item
		if (m_xmlListboxMaster != null && m_xmlListboxMaster.size() > 0)
		{
			if (m_lastIndex >= 0 && m_lastIndex < m_xmlListboxMaster.size())
			{
				return(m_xmlListboxMaster.get(m_lastIndex).getFirstChild());
			} else {
				return(m_xmlListboxMaster.get(0).getFirstChild());
			}
		}
		return(null);
	}

	/**
	 * Searches through <code>PanelRight</code>'s items to see if the specified
	 * <code>_TYPE_LISTBOX</code> is identified by name, and if so, then returns
	 * its currently selected node.
	 * @return <code>Xml</code> for the selected item in the listbox
	 */
	public Xml getListboxNode()
	{
		// See if there's an active Xml list and a selected item
		if (m_xmlListboxMaster != null && m_lastIndex >= 0 && m_lastIndex < m_xmlListboxMaster.size()) {
			return(m_xmlListboxMaster.get(m_lastIndex));
		}
		return(null);
	}

	/**
	 * Returns the source for this listbox, as in opbm.scriptdata.flows.flow
	 * @return source to access data in scripts.xml
	 */
	public String getListboxSource()
	{
		return(m_listBoxSource);
	}

	/**
	 * Returns the template source for this listbox, as in opbm.scriptdata.templates.flow
	 * @return source to access template pattern data in scripts.xml
	 */
	public String getListboxTemplate()
	{
		return(m_listBoxTemplate);
	}

	/**
	 * Sets the visible parameter.
	 *
	 * @param visible true or false should this control be displayed
	 */
	public void setVisible(boolean visible) {
		m_visible = visible;
	}

	/**
	 * Specifies the size of the listbox
	 *
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		m_width		= width;
		m_height	= height;
	}

	/**
	 * Specifies the position of the listbox
	 *
	 * @param x
	 * @param y
	 */
	public void setLocation(int x, int y) {
		m_x		= x;
		m_y		= y;
	}

	/**
	 * Specifies the foreground color
	 *
	 * @param color
	 */
	public void setForeground(Color color) {
		m_foreground = color;
	}

	/**
	 * Specifies the background color
	 *
	 * @param color
	 */
	public void setBackground(Color color) {
		m_background = color;
	}

	/**
	 * Specifies whether or not the control is opaque
	 *
	 * @param opaque
	 */
	public void setOpaque(boolean opaque) {
		m_opaque = opaque;
	}

	/**
	 * Specifies the font to use for the control
	 *
	 * @param font
	 */
	public void setFont(Font font) {
		m_font = font;
	}

	/**
	 * Returns the control width
	 * @return width
	 */
	public int getWidth() {
		return(m_width);
	}

	/**
	 * Returns the control height
	 * @return height
	 */
	public int getHeight() {
		return(m_height);
	}

	/**
	 * Returns the control's x position
	 * @return x coordinate
	 */
	public int getX() {
		return(m_x);
	}

	/**
	 * Returns the controls' y position
	 * @return y coordinate
	 */
	public int getY() {
		return(m_y);
	}

	/**
	 * Returns the control's foreground color
	 * @return <code>Color</code>
	 */
	public Color getForeground() {
		return(m_foreground);
	}

	/**
	 * Returns the control's background color
	 * @return <code>Color</code>
	 */
	public Color getBackground() {
		return(m_background);
	}

	/**
	 * Returns the controls' font
	 * @return <code>Font</code>
	 */
	public Font getFont() {
		return(m_font);
	}

	/**
	 * Returns the location
	 * @return <code>Point</code>
	 */
	public Point getLocation() {
		Point p = new Point(m_x, m_y);
		return(p);
	}

	/**
	 * Returns the size
	 * @return <code>Dimension</code>
	 */
	public Dimension getSize() {
		Dimension d = new Dimension(m_width, m_height);
		return(d);
	}

	/**
	 * Used when the listbox position changes
	 * @param e event information about the changing of position
	 */
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		int selectedIndex = m_listBox.getSelectedIndex();

		if (!m_isMovingUpOrDown)
		{
			m_parentPR.updateRelativeToFields();
			if (m_lastIndex == -1)
			{
				// Send null so we don't save, as this is our first movement
				m_parentPR.saveAndLoadListBox(null, m_xmlListboxMaster.get(m_listBox.getSelectedIndex()));

			} else if (m_listBox.getSelectedIndex() != m_lastIndex)
			{
				if (m_lastIndex < m_xmlListboxMaster.size())
				{
					// Save previous position, and load new position if one is selected
					m_parentPR.saveAndLoadListBox(m_xmlListboxMaster.get(m_lastIndex),
												  selectedIndex >= 0 ? m_xmlListboxMaster.get(selectedIndex) : null);
				}
			}
			m_lastIndex = m_listBox.getSelectedIndex();
			doOnSelect();
		}
	}

	/**
	 * Execute the selected command when an option is selected
	 */
	public void doOnSelect()
	{
		if (!m_onSelectCommand.isEmpty()) {
			m_commandMaster.processCommand(this, m_onSelectCommand, m_onSelectP1, m_onSelectP2, m_onSelectP3, m_onSelectP4, m_onSelectP5, m_onSelectP6, m_onSelectP7, m_onSelectP8, m_onSelectP9, m_onSelectP10);
		}
	}

	public void listBoxAddCommand()
	{
		Xml xmlEntry;

		try
		{
			m_parentPR.saveChanges(m_xmlListboxMaster.get(m_lastIndex));

			// Do the add
			xmlEntry = m_parentPR.listBoxAddClicked(m_xmlListboxMaster.get(m_lastIndex), m_listBoxForEach);
			m_xmlListboxMaster.add(m_lastIndex, xmlEntry);
			updateListBoxArray();

		} catch (UnsupportedOperationException ex) {
		} catch (ClassCastException ex) {
		} catch (NullPointerException ex) {
		} catch (IllegalArgumentException ex) {
		} catch (IndexOutOfBoundsException ex) {
		}
	}

	public void listBoxDeleteCommand()
	{
		try
		{
			m_parentPR.listBoxDelete(m_xmlListboxMaster.get(m_lastIndex));
			m_xmlListboxMaster.remove(m_lastIndex);
			updateListBoxArray();

		} catch (UnsupportedOperationException ex) {
		} catch (ClassCastException ex) {
		} catch (NullPointerException ex) {
		} catch (IllegalArgumentException ex) {
		} catch (IndexOutOfBoundsException ex) {
		}
	}

	public void listBoxCloneCommand()
	{
		Xml xmlEntry;

		try
		{
			m_parentPR.saveChanges(m_xmlListboxMaster.get(m_lastIndex));

			// Do the clone
			xmlEntry = m_parentPR.listBoxCloneClicked(m_xmlListboxMaster.get(m_lastIndex), m_listBoxForEach);
			m_xmlListboxMaster.add(m_lastIndex, xmlEntry);
			updateListBoxArray();

		} catch (UnsupportedOperationException ex) {
		} catch (ClassCastException ex) {
		} catch (NullPointerException ex) {
		} catch (IllegalArgumentException ex) {
		} catch (IndexOutOfBoundsException ex) {
		}
	}

	/**
	 * Called when user clicks a button on a <code>_TYPE_LOOKUPBOX</code>
	 */
	public void listboxCommand(String				command,
							   PanelRightListbox	source)
	{
		Xml xml;
		int saveIndex;
		boolean didMove = false;

		if (source == this)
		{
			m_isMovingUpOrDown = true;

			xml = getListboxNode();
			if (xml != null)
			{
				saveIndex = m_listBox.getSelectedIndex();
				m_lastIndex = -1;

				if (command.equalsIgnoreCase("up"))
				{
					didMove = xml.moveNodeUp();
					if (didMove)
						--saveIndex;
				}
				else if (command.equalsIgnoreCase("down"))
				{
					didMove = xml.moveNodeDown();
					if (didMove)
						++saveIndex;
				}

				m_listBox.setSelectedIndex(saveIndex);
				m_lastIndex = saveIndex;

				if (didMove)
					fillOrRefillListBoxArray();
			}

			m_isMovingUpOrDown = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getSource().equals(m_listBox) || e.getSource().equals(m_listBoxScroller))
		{
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{	// They pressed enter on this selection, execute its enter command
				m_commandMaster.processCommand(m_parentPRI,
											   m_enterCommand,
											   m_enterP1,
											   m_enterP2,
											   m_enterP3,
											   m_enterP4,
											   m_enterP5,
											   m_enterP6,
											   m_enterP7,
											   m_enterP8,
											   m_enterP9,
											   m_enterP10);
			}
		}
	}

	/**
	 * Not used but required for override
	 *
	 * @param e mouse event
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * When the mouse button is pressed down on an add, delete or clone button.
	 *
	 * @param e mouse event
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		int nowItem;
		long now;

		try {
//////////
// LISTBOX
			if (e.getSource().equals(m_listBox))
			{
				// We don't do anything when the user clicks down on here, except
				// see how long it's been since the last down click.  If it's less
				// than .4 second, we execute the dblClick command
				now		= Utils.getMillisecondTimer();
				nowItem	= m_listBox.getSelectedIndex();
				if (m_lastItem == nowItem && m_lastMillisecond != 0)
				{
					if (now - m_lastMillisecond <= 800)
					{	// It's less than .4 second, so we issue the double click
						m_commandMaster.processCommand(m_parentPRI,
													   m_dblClickCommand,
													   m_dblClickP1,
													   m_dblClickP2,
													   m_dblClickP3,
													   m_dblClickP4,
													   m_dblClickP5,
													   m_dblClickP6,
													   m_dblClickP7,
													   m_dblClickP8,
													   m_dblClickP9,
													   m_dblClickP10);
					}
				}
				m_lastItem			= nowItem;
				m_lastMillisecond	= now;

//////////
// ADD
			} else if (e.getSource().equals(m_listBoxAdd)) {
				// Before the add, we have to save any current changes
				m_commandMaster.processCommand(this,
											   m_listBoxAddCommand,
											   m_listBoxAddCommandP1,
											   m_listBoxAddCommandP2,
											   m_listBoxAddCommandP3,
											   m_listBoxAddCommandP4,
											   m_listBoxAddCommandP5,
											   m_listBoxAddCommandP6,
											   m_listBoxAddCommandP7,
											   m_listBoxAddCommandP8,
											   m_listBoxAddCommandP9,
											   m_listBoxAddCommandP10);

//////////
// DELETE
			} else if (e.getSource().equals(m_listBoxDelete)) {
				// They clicked on the delete button on the specified entry
				m_commandMaster.processCommand(this,
											   m_listBoxDeleteCommand,
											   m_listBoxDeleteCommandP1,
											   m_listBoxDeleteCommandP2,
											   m_listBoxDeleteCommandP3,
											   m_listBoxDeleteCommandP4,
											   m_listBoxDeleteCommandP5,
											   m_listBoxDeleteCommandP6,
											   m_listBoxDeleteCommandP7,
											   m_listBoxDeleteCommandP8,
											   m_listBoxDeleteCommandP9,
											   m_listBoxDeleteCommandP10);


//////////
// CLONE
			} else if (e.getSource().equals(m_listBoxClone)) {
				// Before the clone, we have to save any current changes
				m_commandMaster.processCommand(this,
											   m_listBoxCloneCommand,
											   m_listBoxCloneCommandP1,
											   m_listBoxCloneCommandP2,
											   m_listBoxCloneCommandP3,
											   m_listBoxCloneCommandP4,
											   m_listBoxCloneCommandP5,
											   m_listBoxCloneCommandP6,
											   m_listBoxCloneCommandP7,
											   m_listBoxCloneCommandP8,
											   m_listBoxCloneCommandP9,
											   m_listBoxCloneCommandP10);



//////////
// UP
			} else if (e.getSource().equals(m_listBoxUp)) {
				// Before the clone, we have to save any current changes
				m_commandMaster.processCommand(this,
											   m_listBoxUpCommand,
											   m_listBoxUpCommandP1,
											   m_listBoxUpCommandP2,
											   m_listBoxUpCommandP3,
											   m_listBoxUpCommandP4,
											   m_listBoxUpCommandP5,
											   m_listBoxUpCommandP6,
											   m_listBoxUpCommandP7,
											   m_listBoxUpCommandP8,
											   m_listBoxUpCommandP9,
											   m_listBoxUpCommandP10);


//////////
// DOWN
			} else if (e.getSource().equals(m_listBoxDown)) {
				// Before the clone, we have to save any current changes
				m_commandMaster.processCommand(this,
											   m_listBoxDownCommand,
											   m_listBoxDownCommandP1,
											   m_listBoxDownCommandP2,
											   m_listBoxDownCommandP3,
											   m_listBoxDownCommandP4,
											   m_listBoxDownCommandP5,
											   m_listBoxDownCommandP6,
											   m_listBoxDownCommandP7,
											   m_listBoxDownCommandP8,
											   m_listBoxDownCommandP9,
											   m_listBoxDownCommandP10);
			}

		} catch (UnsupportedOperationException ex) {
		} catch (ClassCastException ex) {
		} catch (NullPointerException ex) {
		} catch (IllegalArgumentException ex) {
		} catch (IndexOutOfBoundsException ex) {
		}
	}

	/**
	 * When the mouse is released on the add, clone or delete button, it sets
	 * focus back on the listbox control.
	 *
	 * @param e
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	/**
	 * Not used but required for override
	 *
	 * @param e mouse event
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Not used but required for override
	 *
	 * @param e mouse event
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	private Opbm			m_opbm;
	private Xml				m_listBoxXml;
	private boolean			m_visible;
	private boolean			m_opaque;
	private Color			m_foreground;
	private Color			m_background;
	private Font			m_font;
	private JPanel			m_parentPanel;
	private PanelRight		m_parentPR;
	private PanelRightItem	m_parentPRI;
	private Commands		m_commandMaster;
	private Macros			m_macroMaster;
	private JList			m_listBox;
	private JScrollPane		m_listBoxScroller;
	private List<Xml>		m_xmlListboxMaster;

	private int				m_lastIndex;
	private int				m_width;
	private int				m_height;
	private int				m_x;
	private int				m_y;
	private boolean			m_isMovingUpOrDown;		// Is moving up or down overrides save operations
	private long			m_lastMillisecond;		// The last millisecond when the mouse button was clicked down within the listbox (used to catch double-clicks)
	private int				m_lastItem;				// The last item they clicked on in the listbox, used with lastMillisecond to determine if the double-click should be recognized

	private String			m_listBoxButtons;		// Contains any combination of "+" "-" and "c" as in "+-c" or "+c-" or "c-+", etc.
	private JButton			m_listBoxAdd;			// Add button for listboxes
	private JButton			m_listBoxDelete;		// Delete button for listboxes
	private JButton			m_listBoxClone;			// Clone button for listboxes
	private JButton			m_listBoxUp;			// Up button for lookupboxes
	private JButton			m_listBoxDown;			// Down button for lookupboxes
	private String			m_listBoxSource;		// Location to access nodes within the xml file
	private String			m_listBoxTemplate;		// Location to access template nodes within the xml file
	private String			m_listBoxName;			// Name given to this control in editx.xml
	private String			m_listBoxFileName;		// Source filename of this control's xml file (file being edited)
	private String			m_listBoxLocation;		// The location within the specified fileName (XML file) where the data is found
	private String			m_listBoxForEach;		// For each of these elements within the listBoxLocation specified, display / edit relative content

	private String			m_listByP1;				// Parameter #1
	private String			m_listByP2;				// Parameter #2
	private String			m_listByP3;				// Parameter #3
	private String			m_listByP4;				// Parameter #4
	private String			m_listByP5;				// Parameter #5
	private String			m_listByP6;				// Parameter #6
	private String			m_listByP7;				// Parameter #7
	private String			m_listByP8;				// Parameter #8
	private String			m_listByP9;				// Parameter #9
	private String			m_listByP10;			// Parameter #10

	private String			m_dblClickCommand;
	private String			m_dblClickP1;
	private String			m_dblClickP2;
	private String			m_dblClickP3;
	private String			m_dblClickP4;
	private String			m_dblClickP5;
	private String			m_dblClickP6;
	private String			m_dblClickP7;
	private String			m_dblClickP8;
	private String			m_dblClickP9;
	private String			m_dblClickP10;

	private String			m_enterCommand;
	private String			m_enterP1;
	private String			m_enterP2;
	private String			m_enterP3;
	private String			m_enterP4;
	private String			m_enterP5;
	private String			m_enterP6;
	private String			m_enterP7;
	private String			m_enterP8;
	private String			m_enterP9;
	private String			m_enterP10;

	private String			m_listBoxAddCommand;
	private String			m_listBoxAddCommandP1;
	private String			m_listBoxAddCommandP2;
	private String			m_listBoxAddCommandP3;
	private String			m_listBoxAddCommandP4;
	private String			m_listBoxAddCommandP5;
	private String			m_listBoxAddCommandP6;
	private String			m_listBoxAddCommandP7;
	private String			m_listBoxAddCommandP8;
	private String			m_listBoxAddCommandP9;
	private String			m_listBoxAddCommandP10;

	private String			m_listBoxDeleteCommand;
	private String			m_listBoxDeleteCommandP1;
	private String			m_listBoxDeleteCommandP2;
	private String			m_listBoxDeleteCommandP3;
	private String			m_listBoxDeleteCommandP4;
	private String			m_listBoxDeleteCommandP5;
	private String			m_listBoxDeleteCommandP6;
	private String			m_listBoxDeleteCommandP7;
	private String			m_listBoxDeleteCommandP8;
	private String			m_listBoxDeleteCommandP9;
	private String			m_listBoxDeleteCommandP10;

	private String			m_listBoxCloneCommand;
	private String			m_listBoxCloneCommandP1;
	private String			m_listBoxCloneCommandP2;
	private String			m_listBoxCloneCommandP3;
	private String			m_listBoxCloneCommandP4;
	private String			m_listBoxCloneCommandP5;
	private String			m_listBoxCloneCommandP6;
	private String			m_listBoxCloneCommandP7;
	private String			m_listBoxCloneCommandP8;
	private String			m_listBoxCloneCommandP9;
	private String			m_listBoxCloneCommandP10;

	private String			m_listBoxUpCommand;
	private String			m_listBoxUpCommandP1;
	private String			m_listBoxUpCommandP2;
	private String			m_listBoxUpCommandP3;
	private String			m_listBoxUpCommandP4;
	private String			m_listBoxUpCommandP5;
	private String			m_listBoxUpCommandP6;
	private String			m_listBoxUpCommandP7;
	private String			m_listBoxUpCommandP8;
	private String			m_listBoxUpCommandP9;
	private String			m_listBoxUpCommandP10;

	private String			m_listBoxDownCommand;
	private String			m_listBoxDownCommandP1;
	private String			m_listBoxDownCommandP2;
	private String			m_listBoxDownCommandP3;
	private String			m_listBoxDownCommandP4;
	private String			m_listBoxDownCommandP5;
	private String			m_listBoxDownCommandP6;
	private String			m_listBoxDownCommandP7;
	private String			m_listBoxDownCommandP8;
	private String			m_listBoxDownCommandP9;
	private String			m_listBoxDownCommandP10;

	private String			m_onSelectCommand;
	private String			m_onSelectP1;
	private String			m_onSelectP2;
	private String			m_onSelectP3;
	private String			m_onSelectP4;
	private String			m_onSelectP5;
	private String			m_onSelectP6;
	private String			m_onSelectP7;
	private String			m_onSelectP8;
	private String			m_onSelectP9;
	private String			m_onSelectP10;
}
