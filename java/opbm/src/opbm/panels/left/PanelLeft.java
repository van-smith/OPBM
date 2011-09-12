/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for all left-panel items, which is the
 * menu-pane along the left side used for system navigation.
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

package opbm.panels.left;

import opbm.common.Utils;
import java.awt.Color;
import java.awt.Label;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import opbm.common.Macros;
import opbm.Opbm;

/**
 * Handles the left-side navigation panels.
 */
public class PanelLeft {
	/** Constructor creates <code>ArrayList</code> objects for navigation and
	 * regular items.  Creates new <code>JPanel</code> object, and adds it to
	 * the parent <code>JFrame</code>.
	 *
	 * @param parent Parent object referenced for global method calls
	 * @param name Name associated with this panel (from panels.xml)
	 */
	public PanelLeft(JFrame parent,
					 String name,
					 Opbm	opbm)
	{
		m_navItems			= new ArrayList<PanelLeftItem>(0);
		m_items				= new ArrayList<PanelLeftItem>(0);
		m_panel				= new JPanel();
		m_x					= 0;
		m_y					= 0;
		m_width				= 0;
		m_height			= 0;
		m_name				= name;
		m_opbm				= opbm;
		m_visible			= false;
		m_panel.setLayout(null);
		m_associatedEdit	= "";
		m_associatedRawEdit	= "";
		parent.add(m_panel);
	}

	/**
	 * Refreshes the contents of the text on all menu items in case any
	 * content on the entry changed, and the new macro state will be
	 * processed.
	 */
	public void refreshAfterMacroUpdate()
	{
		int i;

		for (i = 0; i < m_navItems.size(); i++)
		{	// Tell every item within to refresh itself
			m_navItems.get(i).refreshAfterMacroUpdate();
		}

		for (i = 0; i < m_items.size(); i++)
		{	// Tell every item within to refresh itself
			m_items.get(i).refreshAfterMacroUpdate();
		}
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

	/** Resizes the <code>JLabel</code> object
	 *
	 */
	private void resize() {
		if (m_panel != null) {
			m_panel.setBounds(m_x, m_y, m_width, m_height);
		}
	}

	/** Adds the specified PanelLeftItem as a regular item (not a navigation item).
	 *
	 * @param pi PanelLeftItem object to add
	 */
	public void addItem(PanelLeftItem pi) {
		m_items.add(pi);
	}

	/** Not currently used.  Adds an empty PanelLeftItem object.  Future use will be
	 * items that are dynamically added based on context, use history, etc.
	 *
	 * @param macroMaster Macro class
	 * @param statusBar status bar object
	 * @param parent parent <code>JPanel</code> object the PanelLeftItem's
	 * <code>JLabel</code> will be added to.
	 * @return the new PanelLeftItem object created
	 */
	public PanelLeftItem addEmptyItem(Macros	macroMaster,
									  Label		statusBar,
									  JPanel	parent)
	{
		PanelLeftItem pi = new PanelLeftItem(m_opbm, macroMaster, statusBar, parent);
		m_items.add(pi);
		return(pi);
	}

	/** Not currently used.  Deletes the specified PanelLeftItem object.  Future use
	 * will be to delete items that were dynamically added based on context, use
	 * history, etc.
	 *
	 * @param pi PanelLeftItem object to delete
	 * @return true of false if object was found and deleted
	 */
	public boolean deleteItem(PanelLeftItem pi) {
		for (int i = 0; i < m_items.size(); i++) {
			if (m_items.get(i).equals(pi)) {
				m_items.remove(i);
				return(true);
			}
		}
		// If we get here, it wasn't found
		return(false);
	}

	/** Adds the PanelLeftItem object as a navigation item (not as a regular item).
	 *
	 * @param pn PanelLeftItem object to add
	 */
	public void addNavigation(PanelLeftItem pn) {
		m_navItems.add(pn);
	}

	/** Called when the user is navigating away from this panel.  Calls
	 * <code>navigateAway()</code> on all PanelLeftItem navigation and regular items.
	 *
	 */
	public void navigateAway() {
		int i;

		m_panel.setVisible(false);
		if (!m_associatedEdit.isEmpty()) {
			// There's an associated edit rightpanel with this leftpanel
			m_opbm.navigateAwayEdit(m_associatedEdit, true);
		}
		if (!m_associatedRawEdit.isEmpty()) {
			// There's an associated rawedit rightpanel with this leftpanel
			m_opbm.navigateAwayRawEdit(m_associatedRawEdit, true);
		}

		// Make all navigation and item components visible
		for (i = 0; i < m_navItems.size(); i++) {
			m_navItems.get(i).navigateAway();
		}

		for (i = 0; i < m_items.size(); i++) {
			m_items.get(i).navigateAway();
		}
	}

	/** Called when the user is navigating to this panel.  Calls
	 * <code>navigateTo()</code> on all PanelLeftItem navigation and regular items.
	 *
	 */
	public void navigateTo(Macros macroKeyMaster) {
		int i;

		// Make this panel appropriately visible
		resize();
		m_panel.setOpaque(true);
		m_panel.setVisible(m_visible);

		// Make all navigation and item components visible
		for (i = 0; i < m_navItems.size(); i++) {
			m_navItems.get(i).navigateTo();
		}

		for (i = 0; i < m_items.size(); i++) {
			m_items.get(i).navigateTo();
		}
	}

	/**
	 * Resizes the control after the user resizes the window.  Note, the width
	 * on left panels should not change, as these are fixed-sized areas.
	 *
	 * @param newWidth
	 * @param newHeight
	 */
	public void afterWindowResize(int newWidth, int newHeight)
	{
		m_width		= newWidth;
		m_height	= newHeight;
		resize();
	}

	/** Setter sets the X coordinate of this PanelLeft object.  After set, calls
	 * <code>resize()</code>.
	 *
	 * @param x X coordinate.
	 */
	public void setX(int x)
	{
		m_x = x;
		resize();
	}


	/** Setter sets the Y coordinate of this PanelLeft object.  After set, calls
	 * <code>resize()</code>.
	 *
	 * @param y Y coordinate.
	 */
	public void setY(int y)
	{
		m_y = y;
		resize();
	}

	/**Setter sets the width of this PanelLeft object.  After set, calls
	 * <code>resize()</code>.
	 *
	 * @param width
	 */
	public void setWidth(int width)
	{
		m_width = width;
		resize();
	}

	/**Setter sets the height of this PanelLeft object.  After set, calls
	 * <code>resize()</code>.
	 *
	 * @param height
	 */
	public void setHeight(int height)
	{
		m_height = height;
		resize();
	}

	/** Setter sets the name of the PanelLeft object.  Used to identify the panel
	 * for navigation.
	 *
	 * @param name Name of the panel
	 */
	public void setName(String name)
	{
		m_name = name;
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
		if (m_panel != null) {
			m_panel.setForeground(new Color(r, g, b));
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
		if (m_panel != null) {
			m_panel.setBackground(new Color(r, g, b));
			m_panel.repaint();
		}
	}

	/** Setter sets the background color by red, green and blue integer
	 * components.
	 *
	 * @param rrggbb red, green and blue component in rrggbb format
	 */
	public void setBackColor(String rrggbb)
	{
		if (m_panel != null) {
			m_panel.setBackground(Color.decode(Utils.verifyColorFormat(rrggbb)));
			m_panel.repaint();
		}
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

	/** Getter returns the name associated with this PanelLeft.
	 *
	 * @return PanelLeft name
	 */
	public String getName()
	{
		return(m_name);
	}

	/** Getter returns the logical visible state of the PanelLeft object, not of
	 * its underlying <code>JPanel</code> object.
	 *
	 * @deprecated No longer needed with <code>navigateTo()</code> and
	 * <code>navigateAway()</code>, which handle panel visibility.
	 *
	 * @return logical visible state
	 */
	public boolean getVisible()
	{
		return(m_visible);
	}

	/** Getter returns the PanelLeft class's <code>JPanel</code> object.
	 *
	 * @return <code>JPanel</code> object of this class
	 */
	public JPanel getJPanel()
	{
		return(m_panel);
	}

	private List<PanelLeftItem>		m_items;
	private List<PanelLeftItem>		m_navItems;
	private JPanel					m_panel;
	private Opbm					m_opbm;
	private int						m_x;
	private int						m_y;
	private int						m_width;
	private int						m_height;
	private String					m_name;
	private boolean					m_visible;
	private String					m_associatedEdit;
	private String					m_associatedRawEdit;
}
