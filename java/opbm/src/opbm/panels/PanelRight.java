/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for all primary input panels, the large
 * right panel on the OPBM application.  Most of the calls to child objects
 * are routed through this object, and this is done for future tracknig and
 * intercepting, which may become necessary with plugins or other expansion
 * "devices".
 *
 * Last Updated:  Aug 01, 2011
 *
 * by Van Smith, Rick C. Hodgin
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @author Rick C. Hodgin
 * @version 1.0.2
 *
 */

package opbm.panels;

import opbm.common.Tupel;
import opbm.common.Utils;
import java.awt.Color;
import java.awt.Label;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import opbm.common.Commands;
import opbm.common.Macros;
import opbm.Opbm;
import opbm.common.Xml;

/**
 * Handles the creation and display of right-side edit panel objects.
 */
public class PanelRight {
	/** Constructor creates <code>ArrayList</code> objects for items.
	 * Creates new <code>JPanel</code> object, and adds it to the parent
	 * <code>JFrame</code>.
	 *
	 * @param parent Parent object referenced for global method calls
	 * @param name Name associated with this panel (from panels.prlook)
	 */
	public PanelRight(JFrame		parent,
					  Macros		macroMaster,
					  Commands		commandMaster,
					  JLabel		header,
					  Label			statusBar,
					  String		name,
					  Opbm			opbm,
					  Xml			xmlEdit)
	{
		m_items				= new ArrayList<PanelRightItem>(0);
		m_panel				= new JPanel();
		m_header			= header;
		m_statusBar			= statusBar;
		m_parent			= parent;
		m_macroMaster		= macroMaster;
		m_commandMaster		= commandMaster;
		m_xmlEdit			= xmlEdit;
		m_x					= 0;
		m_y					= 0;
		m_width				= 0;
		m_height			= 0;
		m_name				= name;
		m_opbm				= opbm;
		m_backcolor			= Color.WHITE;
		m_forecolor			= Color.BLACK;
		m_visible			= false;
		m_panel.setLayout(null);
		parent.add(m_panel);
		m_filename			= "";
		m_tooltip			= "";
		m_preCommand		= "";
		m_preP1				= "";
		m_preP2				= "";
		m_preP3				= "";
		m_preP4				= "";
		m_preP5				= "";
		m_preP6				= "";
		m_preP7				= "";
		m_preP8				= "";
		m_preP9				= "";
		m_preP10			= "";
		m_postCommand		= "";
		m_postP1			= "";
		m_postP2			= "";
		m_postP3			= "";
		m_postP4			= "";
		m_postP5			= "";
		m_postP6			= "";
		m_postP7			= "";
		m_postP8			= "";
		m_postP9			= "";
		m_postP10			= "";
	}

	/** Refreshes the <code>JLabel</code> object by resizing and repainting it.
	 *
	 */
	public void refresh() {
		if (m_panel != null) {
			resize();
			if (m_panel.isVisible())
				m_panel.repaint();
		}
	}

	/**
	 * Called when the user resizes a window.  Changes the panel height
	 * @param newWidth
	 * @param newHeight
	 */
	public void afterWindowResize(int newWidth, int newHeight)
	{
		int i;

		// Resize the controls
		for (i = 0; i < m_items.size(); i++) {
			m_items.get(i).afterWindowResize(newWidth, newHeight);
		}

		// Resize the panel afterwards (because some of the child controls
		// reference the parent's old size to determine how their position
		// might change).
		m_width		= newWidth;
		m_height	= newHeight;
		resize();
	}

	/** Private method resizes the <code>JLabel</code> object
	 *
	 */
	private void resize() {
		if (m_panel != null) {
			m_panel.setBounds(m_x, m_y, m_width, m_height);
		}
	}

	/** Adds the specified PanelRightItem.
	 *
	 * @param pi PanelRightItem object to add
	 */
	public void addItem(PanelRightItem pi) {
		m_items.add(pi);
	}

	/** Not currently used but deletes the specified PanelRightItem object.  Future
	 * use will be to delete items that were dynamically added based on context,
	 * use history, etc.
	 *
	 * @param pi PanelRightItem object to delete
	 * @return true of false if object was found and deleted
	 */
	public boolean deleteItem(PanelRightItem pi) {
		for (int i = 0; i < m_items.size(); i++) {
			if (m_items.get(i).equals(pi)) {
				m_items.remove(i);
				return(true);
			}
		}
		// If we get here, it wasn't found
		return(false);
	}

	/**
	 * Called to navigate to a panel, which causes every item on it that's been
	 * set visible = true to be displayed.
	 */
	public void navigateTo()
	{
		int i;

		resize();
		m_panel.setOpaque(true);
		m_panel.setVisible(m_visible);
		for (i = 0; i < m_items.size(); i++)
			m_items.get(i).navigateTo();

		updateListBox();
	}

	/**
	 * Called to navigate away from the specified panel, which causes each
	 * item on it to be hidden.
	 */
	public void navigateAway()
	{
		int i;

		m_panel.setVisible(false);
		for (i = 0; i < m_items.size(); i++)
			m_items.get(i).navigateAway();

		m_parent.remove(m_panel);
		m_panel = null;
	}

	/**
	 * Removes the specified entry from the m_items array, added to support
	 * variable options which appear contextually, removing and adding as the
	 * user navigates through data.
	 * @param pri <code>PanelRightItem</code>
	 */
	public void removePanelRightItem(PanelRightItem pri)
	{
		int i;

		for (i = m_items.size() - 1; i >= 0; i--)
		{
			if (m_items.get(i).equals(pri))
			{
				// Found it, remove it
				m_items.get(i).navigateAway();
				m_items.remove(i);
				return;
			}
		}

	}

	public void listBoxAddCommand()
	{
		int i;

		for (i = 0; i < m_items.size(); i++) {
			m_items.get(i).listBoxAddCommand();
		}
	}

	public void listBoxDeleteCommand()
	{
		int i;

		for (i = 0; i < m_items.size(); i++) {
			m_items.get(i).listBoxDeleteCommand();
		}
	}

	public void listBoxCloneCommand()
	{
		int i;

		for (i = 0; i < m_items.size(); i++)
			m_items.get(i).listBoxCloneCommand();
	}

	public void listboxCommand(String				command,
							   PanelRightListbox	source)
	{
		int i;

		for (i = 0; i < m_items.size(); i++)
			m_items.get(i).listboxCommand(command, source);
	}

	/**
	 * Called when the user clicks on the add button on a
	 * <code>_TYPE_LOOKUPBOX</code>
	 *
	 * @param whereFrom name of the lookupbox control being added from
	 * @param whereTo name of the listbox or lookupbox being added to
	 */
	public void lookupboxAddCommand(PanelRightLookupbox		source,
									String					whereTo,
									String					after,
									String					whereFrom,
									boolean					allowCustoms)
	{
		int i;

		for (i = 0; i < m_items.size(); i++)
			m_items.get(i).lookupboxAddCommand(source, whereTo, after, whereFrom, allowCustoms);
	}

	public void lookupboxCommand(String					command,
								 PanelRightLookupbox	source)
	{
		int i;

		for (i = 0; i < m_items.size(); i++)
			m_items.get(i).lookupboxCommand(command, source);
	}

	public void lookupboxZoomCommand(PanelRightLookupbox	source,
									 String					editName,
									 String					zoomFields,
									 String					dataSource)
	{
		int i;

		for (i = 0; i < m_items.size(); i++)
			m_items.get(i).lookupboxZoomCommand(source, editName, zoomFields, dataSource);
	}

	public PanelRightLookupbox getLookupboxByName(String source)
	{
		int i;

		for (i = 0; i < m_items.size(); i++)
		{
			if (m_items.get(i).getIdentity() == PanelRightItem._TYPE_LOOKUPBOX && m_items.get(i).getName().equalsIgnoreCase(source))
				return(m_items.get(i).getLookupbox());
		}
		return(null);
	}

	public Xml getLookupboxSelectedItemByObject(PanelRightLookupbox source)
	{
		Xml xml = null;
		int i;

		for (i = 0; i < m_items.size(); i++)
		{
			xml = m_items.get(i).getLookupboxSelectedItemByObject(source);
			if (xml != null)
				break;
		}
		return(xml);
	}


	public void lookupboxUpdateCommand(String name)
	{
		int i;

		for (i = 0; i < m_items.size(); i++)
		{
			m_items.get(i).lookupboxUpdateCommand(name);
		}
	}

	/**
	 * Called to update the contents of the listbox of any child properties (if any)
	 */
	public void updateListBox() {
		int i;

		for (i = 0; i < m_items.size(); i++) {
			m_items.get(i).renderListBox();
		}
	}

	public PanelRightListbox getListboxByName(String source)
	{
		int i;

		for (i = 0; i < m_items.size(); i++)
		{
			if (m_items.get(i).getIdentity() == PanelRightItem._TYPE_LISTBOX && m_items.get(i).getName().equalsIgnoreCase(source))
				return(m_items.get(i).getListbox());
		}
		return(null);
	}

	/** Called when user clicks on a list item.
	 *
	 * @param oldEntry moving away from (for the save-before-move)
	 * @param newEntry moving to (for the load-after-move)
	 */
	public void saveAndLoadListBox(Xml oldEntry,
									   Xml newEntry)
	{
		if (oldEntry != null)
			saveChanges(oldEntry);		// Save any current changes

		if (newEntry != null)
			loadData(newEntry);			// Load records for the new entry
	}

	/**
	 * Repaints the listbox control after an update, ignores all other types.
	 */
	public void repaintListBox()
	{
		int i;

		for (i = 0; i < m_items.size(); i++)
			m_items.get(i).repaintListBox();
	}

	public void updateRelativeToFields()
	{
		int i;

		for (i = 0; i < m_items.size(); i++)
			m_items.get(i).updateRelativeToFields();
	}

	/** Saves the changes to the specified source based on what's been input
	 * on the screen.
	 *
	 * @param source prlook to update
	 */
	public void saveChanges(Xml source)
	{
		int i;

		for (i = 0; i < m_items.size(); i++) {
			m_items.get(i).saveData(source);
		}
	}

	/** Loads the data from the Xml file onto the controls on the form.
	 *
	 * @param source prlook from which to load data
	 */
	public void loadData(Xml source)
	{
		int i;

		for (i = 0; i < m_items.size(); i++) {
			m_items.get(i).loadData(source);
		}
	}

	/**
	 * Called to load the specified rawedit with its current data.  Allows for
	 * future edit expansion, but edits are not currently supported.
	 *
	 * @param isRawEdit true or false if this is a rawedit panel
	 */
	public void load(boolean isRawEdit)
	{
		int length;
		char[] buffer;

		try
		{
			if (isRawEdit)
			{
				// Rawedit saves save the entire contents of the file
				File f = new File(m_filename);
				if (f.exists())
				{
					// Overwrite the contents
					FileReader fr = new FileReader(f);
					length = (int)f.length();
					buffer = new char[length];
					fr.read(buffer, 0, length);
					fr.close();
					m_items.get(0).setText(new String(buffer));
					m_statusBar.setText("Read " + Utils.putCommas(length) + " bytes from " + m_filename);

				} else {
					// Nothing to read, because the file doesn't exist
					m_statusBar.setText("New file will be created: " + m_filename);
				}
			}

		} catch (IOException ex) {
			m_statusBar.setText("Failure on load (data was not read from disk). Please do not save or file contents may be lost.");

		}
	}

	/**
	 * Called to save the specified edit or rawedit
	 *
	 * @param isRawEdit true or false if this is a rawedit panel
	 */
	public void save(boolean isRawEdit)
	{
		int i, length;

		try
		{
			if (isRawEdit)
			{
				// Rawedit saves save the entire contents of the file
				File f = new File(m_filename);
				PanelRightItem pri = m_items.get(0);
				if (f.exists())
				{
					// Overwrite the contents
					FileWriter fw = new FileWriter(f);
					length = pri.getText().length();
					fw.write(pri.getText(), 0, length);
					fw.close();
					m_statusBar.setText("Wrote " + Utils.putCommas(length) + " bytes to " + m_filename);

				} else {
					if (f.createNewFile())
					{
						FileWriter fw = new FileWriter(f);
						length = pri.getText().length();
						fw.write(pri.getText(), 0, length);
						fw.close();
						m_statusBar.setText("Created new file. Wrote " + Utils.putCommas(length) + " bytes to " + m_filename);

					} else {
						m_statusBar.setText("Failure on save (data was not written to disk). Please use Copy-and-Paste as backup measure.");

					}
				}

			} else {
				// Edit saves are handled by parsing the source items one by one and
				// updating their associated Xml node
				for (i = 0; i < m_items.size(); i++)
					m_items.get(i).saveEditData();

				for (i = 0; i < m_items.size(); i++)
					m_items.get(i).saveListBoxData();

			}

		} catch (IOException ex) {
			m_statusBar.setText("Failure on save (data was not written to disk). Please use Copy-and-Paste as backup measure.");
		}
	}

	/** For rawedit <code>PanelRight</code> items, sets the filename that is being edited.
	 *
	 * @param filename
	 */
	public void setFilename(String filename) {
		m_filename = filename;
	}

	/** Setter sets the X coordinate of this PanelRight object.  After set,
	 * calls <code>resize()</code>.
	 *
	 * @param x X coordinate.
	 */
	public void setX(int x)
	{
		m_x = x;
		resize();
	}


	/** Setter sets the Y coordinate of this PanelRight object.  After set,
	 * calls <code>resize()</code>.
	 *
	 * @param y Y coordinate.
	 */
	public void setY(int y)
	{
		m_y = y;
		resize();
	}

	/**Setter sets the width of this PanelRight object.  After set, calls
	 * <code>resize()</code>.
	 *
	 * @param width
	 */
	public void setWidth(int width)
	{
		m_width = width;
		resize();
	}

	/**Setter sets the height of this PanelRight object.  After set, calls
	 * <code>resize()</code>.
	 *
	 * @param height
	 */
	public void setHeight(int height)
	{
		m_height = height;
		resize();
	}

	/** Setter sets the pre-command to execute, along with parameters
	 *
	 * @param command
	 */
	public void setPreCommand(String command,
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
		m_preCommand	= command;
		m_preP1			= p1;
		m_preP2			= p2;
		m_preP3			= p3;
		m_preP4			= p4;
		m_preP5			= p5;
		m_preP6			= p6;
		m_preP7			= p7;
		m_preP8			= p8;
		m_preP9			= p9;
		m_preP10		= p10;
	}

	/**
	 * Executes a command before the panel is displayed.
	 */
	public void doPreCommand() {
		m_commandMaster.processCommand(this, m_preCommand, m_preP1, m_preP2, m_preP3, m_preP4, m_preP5, m_preP6, m_preP7, m_preP8, m_preP9, m_preP10);
	}

	/** Setter sets the name of the PanelRight object.  Used to identify the
	 * panel for navigation.
	 *
	 * @param name Name of the panel
	 */
	public void setName(String name)
	{
		m_name = name;
	}

	/** Setter sets text to display in the status bar when control gets focus.
	 *
	 * @param tooltip text to display
	 */
	public void setTooltip(String tooltip) {
		m_tooltip = tooltip;
	}

	/** Setter sets the post-command to execute, along with parameters
	 *
	 * @param command
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param p4
	 * @param p5
	 * @param p6
	 * @param p7
	 * @param p8
	 * @param p9
	 * @param p10
	 */
	public void setPostCommand(String command,
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
		m_postCommand	= command;
		m_postP1		= p1;
		m_postP2		= p2;
		m_postP3		= p3;
		m_postP4		= p4;
		m_postP5		= p5;
		m_postP6		= p6;
		m_postP7		= p7;
		m_postP8		= p8;
		m_postP9		= p9;
		m_postP10		= p10;
	}

	/**
	 * Execute the post-command
	 */
	public void doPostCommand() {
		m_commandMaster.processCommand(this, m_postCommand, m_postP1, m_postP2, m_postP3, m_postP4, m_postP5, m_postP6, m_postP7, m_postP8, m_postP9, m_postP10);
	}


	/** Setter sets the logical visible state of the control, but does not
	 * update its actual visibility.
	 *
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		m_visible = visible;
	}

	/** Setter sets the foreground color by red, green and blue integer
	 * components.
	 *
	 * @param r Red component (0-255)
	 * @param g Green component (0-255)
	 * @param b Blue component (0-255)
	 */
	public void setForeColor(int r,
							 int g,
							 int b)
	{
		Color color = new Color(r, g, b);
		m_forecolor = color;
		if (m_panel != null) {
			m_panel.setForeground(color);
			m_panel.repaint();
		}
	}

	/** Setter sets the background color by red, green and blue integer
	 * components.
	 *
	 * @param r Red component (0-255)
	 * @param g Green component (0-255)
	 * @param b Blue component (0-255)
	 */
	public void setBackColor(int r,
							 int g,
							 int b)
	{
		Color color = new Color(r, g, b);
		m_backcolor = color;
		if (m_panel != null) {
			m_panel.setBackground(color);
			m_panel.repaint();
		}
	}

	/** Setter sets the foreground color by reference <code>Color</code> value.
	 *
	 * @param color <code>Color</code> to set
	 */
	public void setForeColor(Color color)
	{
		m_forecolor = color;
		if (m_panel != null) {
			m_panel.setForeground(color);
			m_panel.repaint();
		}
	}

	/** Setter sets the background color by reference <code>Color</code> value.
	 *
	 * @param color <code>Color</code> to set
	 */
	public void setBackColor(Color color)
	{
		m_backcolor = color;
		if (m_panel != null) {
			m_panel.setBackground(color);
			m_panel.repaint();
		}
	}

	public Color getForeColor() {
		return(m_forecolor);
	}

	public Color getBackColor() {
		return(m_backcolor);
	}

	/** Getter returns the X coordinate.
	 *
	 * @return X coordinate
	 */
	public int getX()
	{
		return(m_x);
	}

	/** Getter returns the Y coordinate.
	 *
	 * @return Y coordinate
	 */
	public int getY()
	{
		return(m_y);
	}

	/** Getter returns the width.
	 *
	 * @return width
	 */
	public int getWidth()
	{
		return(m_width);
	}

	/** Getter returns the height.
	 *
	 * @return height
	 */
	public int getHeight()
	{
		return(m_height);
	}

	/** Getter returns the name associated with this PanelRight.
	 *
	 * @return PanelRight name
	 */
	public String getName()
	{
		return(m_name);
	}

	/** Getter returns the logical visible state of the PanelRight object, not of
	 * its underlying <code>JPanel</code> object.
	 *
	 * @return logical visible state
	 */
	public boolean getVisible()
	{
		return(m_visible);
	}

	/** Getter returns the PanelRight class's <code>JPanel</code> object.
	 *
	 * @return <code>JPanel</code> object of this class
	 */
	public JPanel getJPanel()
	{
		return(m_panel);
	}

	/** Getter returns the PanelRight class's <code>JPanel</code>'s <code>JFrame</code>.
	 *
	 * @return <code>JFrame</code> parent of the panel object
	 */
	public JFrame getParent()
	{
		return(m_parent);
	}

	/**
	 * Searches through <code>PanelRight</code>'s items to see if the specified
	 * <code>_TYPE_LISTBOX</code> or <code>_TYPE_LOOKUPBOX</code> is identified
	 * by name, and if so, then returns its currently selected node.
	 *
	 * @param name control name to find
	 * @return <code>Xml</code> for the selected item in the listbox or lookupbox
	 */
	public Xml getListboxOrLookupboxFirstChildNodeByName(String name)
	{
		int i;
		Xml x = null;

		for (i = 0; x == null && i < m_items.size(); i++)
		{
			if (m_items.get(i).getName().equalsIgnoreCase(name)) {
				// This control matches the named control
				x = m_items.get(i).getListboxOrLookupboxFirstChildNode();
				break;
			}
		}
		return(x);
	}

	/**
	 * Searches through <code>PanelRight</code>'s items to see if the specified
	 * <code>_TYPE_LISTBOX</code> or <code>_TYPE_LOOKUPBOX</code> is identified
	 * by name, and if so, then returns its currently selected node.
	 *
	 * @param name control name to find
	 * @return <code>Xml</code> for the selected item in the listbox or lookupbox
	 */
	public Xml getListboxOrLookupboxNodeByName(String name)
	{
		int i;
		Xml x = null;

		for (i = 0; x == null && i < m_items.size(); i++)
		{
			if (m_items.get(i).getName().equalsIgnoreCase(name)) {
				// This control matches the named control
				x = m_items.get(i).getListboxOrLookupboxNode();
				break;
			}
		}
		return(x);
	}

	/**
	 * Called when a left-click is made on a Listbox's add button, to add
	 * a new node BEFORE the specified one
	 *
	 * @param actionSource root node to add before
	 * @param rootTag tag name to update
	 * @return actionSource is returned, as a convenience to caller
	 */
	public Xml listBoxAddClicked(Xml actionSource, String rootTag)
	{
		Xml xmlAdd;
		int i;

		// Create our new Xml to add
		xmlAdd = new Xml(rootTag);
		xmlAdd.cloneAttributes(actionSource, false);

		// Instruct every field that has a value to insert its default value,
		// or add an empty tag if there is no default value
		for (i = 0; i < m_items.size(); i++) {
			m_items.get(i).addDefaultField(xmlAdd, false);
		}

		// Insert before the specified entry (left-click)
		actionSource.insertNodeBefore(xmlAdd, false);
		return(xmlAdd);
	}

	/**
	 * Deletes the specified node.
	 *
	 * @param actionSource prlook to remove
	 */
	public void	listBoxDelete(Xml actionSource)
	{
		actionSource.deleteNode(false);
	}

	/**
	 * Called when a left-click is made on the clone button to insert a
	 * duplicate item BEFORE the selected one.
	 *
	 * @param actionSource prlook to add before
	 * @param rootTag tag name to update
	 * @return actionSource is returned as a convenience
	 */
	public Xml listBoxCloneClicked(Xml actionSource, String rootTag)
	{
		Xml clone = actionSource.cloneNode(true);
//////////
// For debugging:
//		Xml.saveNode(actionSource,	"original.prlook");
//		Xml.saveNode(clone,			"clone.prlook");
//////////
		actionSource.insertNodeBefore(clone, false);
		return(clone);
	}

	/**
	 * Called when a left-click is made on a Listbox's add button
	 *
	 * @param actionSource prlook to add to the specified entry
	 * @param addName name of the listbox control adding to
	 * @param whereFrom name of the lookupbox control being added from
	 * @param after
	 * @param whereTo name of the listbox or lookupbox being added to
	 * @param allowCustoms true or false if custom inputs are allowed at this level?
	 */
	public void lookupboxAddClicked(Xml			actionSource,
									String		addName,
									String		whereTo,
									String		after,
									String		whereFrom,
									boolean		allowCustoms)
	{
		List<Xml>customs = new ArrayList<Xml>(0);
		Xml xmlTo, xmlAfter, xmlFrom, xmlNew;
		Tupel saveObjectList;

		// Grab the references
		xmlTo		= getListboxOrLookupboxNodeByName(whereTo);
		xmlAfter	= getListboxOrLookupboxNodeByName(after);
		xmlFrom		= getListboxOrLookupboxNodeByName(whereFrom);

		if (Utils.areAnyNull(xmlTo, xmlAfter, xmlFrom)) {
			m_statusBar.setText("Unable to add. Please select an entry in " + whereTo + ", " + after + " and " + whereFrom);
			return;
		}

		// If we get here, we have our data
		// Build the edit to see if there are any custom edit variables to populate ahead of time
		saveObjectList = new Tupel(m_opbm);
		saveObjectList.add("xmlTo",		xmlTo);
		saveObjectList.add("xmlAfter",		xmlAfter);
		saveObjectList.add("xmlFrom",		xmlFrom);
		saveObjectList.add("addName",		addName);
		saveObjectList.add("whereTo",		whereTo);
		saveObjectList.add("after",		after);
		saveObjectList.add("whereFrom",	whereFrom);
		PanelFactory.buildCustomInputsFromEdit(m_opbm, customs, whereFrom, xmlFrom, m_macroMaster, saveObjectList.getName());
		if (!customs.isEmpty())
		{
			// There is data that must be input before the record is added
			// Create the edit
			JFrame fr = m_opbm.createZoomWindow(this, "Populate " + Utils.toProper(whereTo) + ", after " + Utils.toProper(after) + ", from " + Utils.toProper(whereFrom) + ":" + Utils.toProper(xmlFrom.getName()));
			PanelRight	pr = PanelFactory.createRightPanelFromXmlCustomsArray(customs, m_opbm, m_macroMaster, m_commandMaster, m_panel, fr);
			saveObjectList.add("frame",		fr);
			saveObjectList.add("PanelRight",	pr);

			// Position and display the content pane
			pr.setX(0);
			pr.setY(0);
			pr.setVisible(true);
			pr.navigateTo();

			// Show the window
			fr.setVisible(true);

			// Add to our list of controlled windows (so they can be termined when the main frame exits as well)
			m_opbm.addZoomWindow(fr);
			// The add won't be conducted here.  But when the user clicks on the
			// Save button, it will call "saveCustomCommand()" below, and it
			// will be saved there with their variable parameters.

		} else {
			// Just add the record with no user-editable options
			xmlNew = new Xml(xmlFrom.getName());
			xmlNew.addAttribute("name", xmlFrom.getAttribute("name"));
			xmlNew.addAttribute("sourcename", xmlFrom.getAttribute("name"));
			xmlAfter.insertNodeAfter(xmlNew);

			// Update the Lookupbox (which now has one more addition)
			m_opbm.updateEditListboxesAndLookupboxes();
			// Note:  This function is not called locally, because the active
			//        edit may not be this instance of PanelRight.
		}
	}

	/**
	 * Saves the custom after the user clicks the save button.
	 * @param tup
	 */
	public void saveCustomCommand(Tupel tup)
	{
		int i;

		// Retrieve variables from the tupel
		Xml			xmlTo		= (Xml)tup.getSecond("xmlTo");
		Xml			xmlAfter	= (Xml)tup.getSecond("xmlAfter");
		Xml			xmlFrom		= (Xml)tup.getSecond("xmlFrom");
		String		addName		= (String)tup.getSecond("addName");
		String		whereTo		= (String)tup.getSecond("whereTo");
		String		after		= (String)tup.getSecond("after");
		String		whereFrom	= (String)tup.getSecond("whereFrom");
		JFrame		fr			= (JFrame)tup.getSecond("frame");
		PanelRight	pr			= (PanelRight)tup.getSecond("PanelRight");

		// Call the associated PanelRight to write its contents
		if (pr != null)
			pr.saveCustomCommand(fr, whereTo, after, addName, whereFrom, xmlTo, xmlAfter, xmlFrom);
	}

	public void saveCustomCommand(JFrame		fr,
								  String		addName,
								  String		whereTo,
								  String		after,
								  String		whereFrom,
								  Xml			xmlTo,
								  Xml			xmlAfter,
								  Xml			xmlFrom)
	{
		int i;
		Xml options, xmlNew, xmlAdd;
		// Build the Xml containing every field in this list
		// Store it at the specified location

		// Create the entry
		xmlNew = new Xml(xmlFrom.getName());
		xmlNew.addAttribute("name", xmlFrom.getAttribute("name"));
		xmlNew.addAttribute("sourcename", xmlFrom.getAttribute("name"));
		options = new Xml("options");
		xmlNew.addChild(options);

		// Add the options (if any)
		for (i = 0; i < m_items.size(); i++)
		{
			xmlAdd = m_items.get(i).addFieldToOptions(options);
			if (xmlAdd != null)
				options.addChild(xmlAdd);
		}

		// Append to the listing
		xmlAfter.insertNodeAfter(xmlNew);
		// When we get here, the entry has been added

		// Update the Lookupbox (which now has one more addition)
		m_opbm.updateEditListboxesAndLookupboxes();
		// Note:  This function is not called locally, because the active
		//        edit may not be this instance of PanelRight.
	}

	/**
	 * Asks each listbox and lookupbox to refresh its list of members.
	 */
	public void updateEditListboxesAndLookupboxes()
	{
		int i;

		for (i = 0; i < m_items.size(); i++)
		{
			m_items.get(i).updateEditListboxesAndLookupboxes();
		}
	}

	/**
	 * Called when a left-click is made on the subtract button
	 *
	 * @param actionSource prlook to subtract
	 */
	public void lookupboxSubtractClicked(Xml actionSource)
	{
// REMEMBER, we need to make sure there is at least one item
		actionSource.deleteNode(false);
		updateEditListboxesAndLookupboxes();
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

		for (i = 0; i < m_items.size(); i++) {
			m_items.get(i).positionListboxTo(source);
		}
	}

	private List<PanelRightItem>	m_items;
	private JPanel					m_panel;
	private JLabel					m_header;
	private Label					m_statusBar;
	private Opbm					m_opbm;
	private JFrame					m_parent;
	private Macros					m_macroMaster;
	private Commands				m_commandMaster;
	private Xml						m_xmlEdit;			// Reference to the edit root for this panel

	private int						m_x;
	private int						m_y;
	private int						m_width;
	private int						m_height;
	private String					m_name;
	private boolean					m_visible;
	private Color					m_backcolor;
	private Color					m_forecolor;
	private String					m_filename;
	private String					m_tooltip;

	private String					m_preCommand;
	private String					m_preP1;
	private String					m_preP2;
	private String					m_preP3;
	private String					m_preP4;
	private String					m_preP5;
	private String					m_preP6;
	private String					m_preP7;
	private String					m_preP8;
	private String					m_preP9;
	private String					m_preP10;

	private String					m_postCommand;
	private String					m_postP1;
	private String					m_postP2;
	private String					m_postP3;
	private String					m_postP4;
	private String					m_postP5;
	private String					m_postP6;
	private String					m_postP7;
	private String					m_postP8;
	private String					m_postP9;
	private String					m_postP10;
}
