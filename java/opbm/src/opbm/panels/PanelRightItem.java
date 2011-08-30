/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for items which appear in the PanelRight
 * area.  Several different types of child items (to PanelRight parent) are
 * defined, with some of those having child items themselves.  This assembly
 * of items may seem complicated, but it is very flexible and allows for the
 * source edits.xml file to create all manner of variable edits within the
 * system, all from modifying a few lines within that file.
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

import java.awt.event.KeyEvent;
import opbm.common.Utils;
import java.awt.Font;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Label;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

// The following are used as these types from edits.xml:
import javax.swing.JLabel;		// Label / Link
import javax.swing.JButton;		// Button
import javax.swing.JCheckBox;	// Checkbox
import javax.swing.JTextField;	// Textbox
import javax.swing.JTextArea;	// Editbox
import opbm.common.Commands;
import opbm.common.Macros;
import opbm.Opbm;
import opbm.common.Xml;
								// Listbox comes from the PanelRightListbox class
								// Lookupbox comes from the PanelRightLookupbox class

/**
 * Handles the subordinate objects to every PanelRight object, including everything
 * which appears on the form/screen for display or editing, including listboxes,
 * which back into their own PanelListbox* class.
 */
public class PanelRightItem
					implements MouseListener,
							   KeyListener,
							   FocusListener
{
	/** Constructor creates default size, <code>JLabel</code> object (and
	 * links object to panelParent), stores variables related to panelParent.
	 *
	 * @param opbm Parent object referenced for global method calls
	 * @param macroMaster Macro object referenced directly for expanding macros
	 * @param statusBar Status bar on main <code>JFrame</code> object, updated with tooltip text
	 * @param panelParent Parent <code>JPanel</code> object to which this PanelRightItem's <code>JLabel</code> object will be added.
	 */
	PanelRightItem(Opbm			opbm,
				   Macros		macroMaster,
				   Commands		commandMaster,
				   Label		statusBar,
				   JPanel		panelParent,
				   PanelRight	parent)
	{
		m_opbm				= opbm;
		m_macroMaster		= macroMaster;
		m_commandMaster		= commandMaster;
		m_statusBar			= statusBar;
		m_panelParent		= panelParent;
		m_parentPR			= parent;
		m_autoUpdate		= true;
		m_name				= "";
		m_field				= "";
		m_fieldRelativeTo	= "";
		m_default			= "";
		m_command			= "";
		m_commandP1			= "";
		m_commandP2			= "";
		m_commandP3			= "";
		m_commandP4			= "";
		m_commandP5			= "";
		m_commandP6			= "";
		m_commandP7			= "";
		m_commandP8			= "";
		m_commandP9			= "";
		m_commandP10		= "";
		m_tooltip			= "";
		m_source			= null;
		m_x					= -1;
		m_y					= -1;
		m_width				= -1;
		m_height			= -1;
		m_font				= null;
	}


	/**
	 * Navigating away from a panel (making it invisible).
	 */
	public void navigateAway()
	{
		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
				{
					m_label.setVisible(false);
					m_panelParent.remove(m_label);
					m_label = null;
				}
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
				{
					m_button.setVisible(false);
					m_panelParent.remove(m_button);
					m_button = null;
				}
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
				{
					m_checkbox.setVisible(false);
					m_panelParent.remove(m_checkbox);
					m_checkbox = null;
				}
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
				{
					m_textbox.setVisible(false);
					m_panelParent.remove(m_textbox);
					m_textbox = null;
				}
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
				{
					m_editboxScroller.setVisible(false);
					m_editbox.setVisible(false);
					m_panelParent.remove(m_editboxScroller);
					m_editboxScroller = null;
					m_editbox = null;
				}
				break;

			case _TYPE_LISTBOX:
				if (m_listbox != null)
				{
					m_listbox.setVisible(false);
					m_listbox.remove();
					m_listbox = null;
				}
				break;

			case _TYPE_LOOKUPBOX:
				if (m_lookupbox != null)
				{
					m_lookupbox.setVisible(false);
					m_lookupbox.remove();
					m_lookupbox = null;
				}
				break;

		}
	}

	/**
	 * Explicitly instructs the listbox to write its Xml data to disk.  Called
	 * during the edit_save command.
	 */
	public void saveListBoxData()
	{
		if (m_type == _TYPE_LISTBOX && m_listbox != null)
			m_listbox.saveListBoxData();
	}

	/**
	 * Navigating to a panel (set it up and make it visible).
	 */
	public void navigateTo()
	{
		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
					m_label.setVisible(true);
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
					m_button.setVisible(true);
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					m_checkbox.setVisible(true);
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					m_textbox.setVisible(true);
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					m_editbox.setVisible(true);
				break;

			case _TYPE_LISTBOX:
				if (m_listbox != null)
					m_listbox.setVisible(true);
				break;

			case _TYPE_LOOKUPBOX:
				if (m_lookupbox != null)
					m_lookupbox.setVisible(true);
				break;
		}
	}

	/**
	 * Processes the associated command (if any) for the object when user presses
	 * any mouse button (right-, left- or other-button-click)
	 *
	 * @param e System mouse event
	 */
	@Override
    public void mousePressed(MouseEvent e)
	{
		if (!m_command.isEmpty())
		{
			// There's a command here, and it's being clicked
			m_commandMaster.processCommand(this,
										   m_command,
										   m_commandP1,
										   m_commandP2,
										   m_commandP3,
										   m_commandP4,
										   m_commandP5,
										   m_commandP6,
										   m_commandP7,
										   m_commandP8,
										   m_commandP9,
										   m_commandP10);
		}
    }

	/** Not used but included because required for listener declaration.
	 *
	 * @param e System mouse event
	 */
	@Override
    public void mouseReleased(MouseEvent e)
	{
        //e.getClickCount()
    }

	/** Dynamically updates tooltip text for object being moused over.
	 *
	 * @param e System mouse event
	 */
	@Override
    public void mouseEntered(MouseEvent e)
	{
		if (m_macroMaster != null)
		{
			String tooltip = m_macroMaster.parseMacros(m_tooltip);
			if (m_statusBar != null && !m_statusBar.getText().equals(tooltip))
			{
				m_statusBar.setText(tooltip);
				m_statusBar.repaint();
			}
		}
    }

	/** Not used but included because required for listener declaration.
	 *
	 * @param e System mouse event
	 */
	@Override
    public void mouseExited(MouseEvent e)
	{
    }

	/** Not used but included because required for listener declaration.
	 *
	 * @param e System mouse event
	 */
	@Override
    public void mouseClicked(MouseEvent e)
	{
    }

	/** Setter sets the identify of this control as to its type.
	 *
	 * @param type one of _TYPE_LABEL, _TYPE_BUTTON, _TYPE_CHECKBOX,
	 * _TYPE_TEXTBOX, _TYPE_EDITBOX, _TYPE_LISTBOX, _TYPE_LINK, _TYPE_LOOKUPBOX
	 * @return true or false, if identity was valid
	 */
	public boolean setIdentity(int type)
	{
		switch (type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				m_type = type;
				if (m_label == null)
				{
					m_label = new JLabel();
					m_label.addMouseListener(this);
					m_label.addKeyListener(this);
					m_label.addFocusListener(this);
					m_label.setVisible(false);
					if (type == _TYPE_LINK)
						m_label.setCursor(new Cursor(Cursor.HAND_CURSOR));
					m_panelParent.add(m_label);
				}
				break;

			case _TYPE_BUTTON:
				m_type = type;
				if (m_button == null)
				{
					m_button = new JButton();
					m_button.addMouseListener(this);
					m_button.addKeyListener(this);
					m_button.addFocusListener(this);
					m_button.setVisible(false);
					m_panelParent.add(m_button);
				}
				break;

			case _TYPE_CHECKBOX:
				m_type = type;
				if (m_checkbox == null)
				{
					m_checkbox = new JCheckBox();
					m_checkbox.addMouseListener(this);
					m_checkbox.addKeyListener(this);
					m_checkbox.addFocusListener(this);
					m_checkbox.setVisible(false);
					m_panelParent.add(m_checkbox);
				}
				break;

			case _TYPE_TEXTBOX:
				m_type = type;
				if (m_textbox == null)
				{
					m_textbox = new JTextField();
					m_textbox.addMouseListener(this);
					m_textbox.addKeyListener(this);
					m_textbox.addFocusListener(this);
					m_textbox.setVisible(false);
					m_panelParent.add(m_textbox);
				}
				break;

			case _TYPE_EDITBOX:
				m_type = type;
				if (m_editbox == null)
				{
					m_editbox			= new JTextArea(256, 132);
					m_editboxScroller	= new JScrollPane(m_editbox); /*,
														  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED,
														  ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);*/
					m_editbox.addMouseListener(this);
					m_editbox.addKeyListener(this);
					m_editbox.addFocusListener(this);
					m_editbox.setVisible(false);
					m_editbox.setLineWrap(true);
					m_editbox.setWrapStyleWord(true);
					m_editbox.setTabSize(2);
					// Needs a scrollbar
					m_panelParent.add(m_editboxScroller);
				}
				break;

			case _TYPE_LISTBOX:
				m_type = type;
				if (m_listbox == null)
				{
					m_listbox = new PanelRightListbox(m_opbm, m_panelParent, m_parentPR, this, m_commandMaster, m_macroMaster);
					m_listbox.setVisible(false);
				}
				break;

			case _TYPE_LOOKUPBOX:
				m_type = type;
				if (m_lookupbox == null)
				{
					m_lookupbox = new PanelRightLookupbox(m_opbm, m_panelParent, m_parentPR, this, m_commandMaster, m_macroMaster);
					m_lookupbox.setVisible(false);
				}
				break;

			case _TYPE_OPTIONS:
				m_type = type;
				if (m_options == null)
				{
					m_options = new PanelRightOptions(m_opbm, m_panelParent, m_parentPR, this, m_commandMaster, m_macroMaster);
				}
				break;

			default:
				return(false);
		}

		// Clear out the other ones which may still exist
		if (!(m_type == _TYPE_LABEL || m_type == _TYPE_LINK) && m_label != null)
		{
			m_panelParent.remove(m_label);
			m_label = null;
		}
		if (m_type != _TYPE_BUTTON && m_button != null)
		{
			m_panelParent.remove(m_button);
			m_button = null;
		}
		if (m_type != _TYPE_CHECKBOX && m_checkbox != null)
		{
			m_panelParent.remove(m_checkbox);
			m_checkbox = null;
		}
		if (m_type != _TYPE_TEXTBOX && m_textbox != null)
		{
			m_panelParent.remove(m_textbox);
			m_textbox = null;
		}
		if (m_type != _TYPE_EDITBOX && m_editbox != null)
		{
			m_panelParent.remove(m_editbox);
			m_editbox = null;
		}
		if (m_type != _TYPE_LISTBOX && m_listbox != null)
		{
			m_listbox.remove();
			m_listbox = null;
		}
		if (m_type != _TYPE_LOOKUPBOX && m_lookupbox != null)
		{
			m_lookupbox.remove();
			m_lookupbox = null;
		}
		return(true);
	}

	public int getIdentity()
	{
		return(m_type);
	}

	public PanelRightLookupbox getLookupbox()
	{
		return(m_lookupbox);
	}

	public PanelRightListbox getListbox()
	{
		return(m_listbox);
	}

	/**
	 * Called to resize the associated controls if they are at the pane's
	 * extent, or are beyond the specified width
	 *
	 * @param newWidth
	 * @param newHeight
	 */
	public void afterWindowResize(int newWidth, int newHeight)
	{
		int howCloseToBorder, extent, newExtent;

		switch (m_type)
		{
			case _TYPE_TEXTBOX:
				if (m_textbox != null)
				{
					extent				= m_x + m_width;
					howCloseToBorder	= m_parentPR.getWidth() - extent;
					if (howCloseToBorder <= 5)
					{
						// We only resize controls that are within 5 pixels of the right border
						newExtent = newWidth - howCloseToBorder - m_x;
						m_textbox.setSize(newExtent, m_textbox.getHeight());
						m_width = newExtent;
					}
				}
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
				{
					// Update the width
					extent				= m_x + m_width;
					howCloseToBorder	= m_parentPR.getWidth() - extent;
					if (howCloseToBorder <= 5)
					{
						// We only resize controls that are within 5 pixels of the right border
						newExtent = newWidth - howCloseToBorder - m_x;
						m_editboxScroller.setSize(newExtent, m_editboxScroller.getHeight());
						m_width = newExtent;
					}

					// Update the height
					extent				= m_y + m_height;
					howCloseToBorder	= m_parentPR.getHeight() - extent;
					if (howCloseToBorder <= 5)
					{
						// We only resize controls that are within 5 pixels of the bottom border
						newExtent = newHeight - howCloseToBorder - m_y;
						m_editboxScroller.setSize(m_editboxScroller.getWidth(), newExtent);
						m_height = newExtent;
					}

					// Update the edit control within
					m_editbox.setSize(m_editboxScroller.getSize());
				}
				break;

			case _TYPE_LISTBOX:
				m_listbox.afterWindowResize(newWidth, newHeight);
				break;

			case _TYPE_LOOKUPBOX:
				m_lookupbox.afterWindowResize(newWidth, newHeight);
				break;
		}
	}

	/**
	 * Specifies whether or not this control is auto-updated.
	 *
	 * @param autoUpdate true or false
	 */
	public void setAutoUpdate(boolean autoUpdate)
	{
		m_autoUpdate = autoUpdate;
	}

	/**
	 * Called to update the listbox after its identity and size is setup to its
	 * maximum extent.  The listbox is physically resized to a smaller size, and
	 * buttons added, etc., based upon the settings for <code>setButton*</code>.
	 *
	 */
	public void updateListBox()
	{
		if (m_listbox != null)
			m_listbox.updateListBox();
	}

	/**
	 * Asks the listboxes and lookupboxes to refresh their member lists.
	 */
	public void updateEditListboxesAndLookupboxes()
	{
		if (m_type == _TYPE_LISTBOX && m_listbox != null)
			m_listbox.fillOrRefillListBoxArray();

		else if (m_type == _TYPE_LOOKUPBOX && m_lookupbox != null)
			m_lookupbox.fillOrRefillLookupBoxArray();
	}

	public Xml addFieldToOptions(Xml options)
	{
		String value = "";
		Xml xmlNew = null;

		if (!m_field.isEmpty())
		{
			switch (m_type) {
				case _TYPE_LABEL:
				case _TYPE_LINK:
					if (m_label != null)
						value = Macros.encodeCommonMacros(m_label.getText());
					break;

				case _TYPE_BUTTON:
					if (m_button != null)
						value = Macros.encodeCommonMacros(m_button.getText());
					break;

				case _TYPE_CHECKBOX:
					if (m_checkbox != null)
						value = Macros.encodeCommonMacros(m_checkbox.getText());
					break;

				case _TYPE_TEXTBOX:
					if (m_textbox != null)
						value = Macros.encodeCommonMacros(m_textbox.getText());
					break;

				case _TYPE_EDITBOX:
					if (m_editbox != null)
						value = Macros.encodeCommonMacros(m_editbox.getText());
					break;
			}
			xmlNew = new Xml(m_field, value);
		}
		return(xmlNew);
	}

	/**
	 * Called to update the lookupbox after its identity and size is setup to its
	 * maximum extent.  The lookupbox is physically resized to a smaller size, and
	 * buttons added, etc., based upon the settings for <code>setButton*</code>.
	 *
	 */
	public void updateLookupBox()
	{
		if (m_lookupbox != null)
			m_lookupbox.updateLookupBox();
	}

	/**
	 * Called to update the listbox contents
	 */
	public void renderListBox()
	{
		if (m_type == _TYPE_LISTBOX && m_listbox != null)
		{
			m_listbox.fillOrRefillListBoxArray();
			m_listbox.select(0);

		} else if (m_type == _TYPE_LOOKUPBOX && m_lookupbox != null) {
			m_lookupbox.fillOrRefillLookupBoxArray();
			m_lookupbox.select(0);
		}
	}

	/**
	 * Add button clicked on a listbox
	 */
	public void listBoxAddCommand()
	{
		if (m_type == _TYPE_LISTBOX && m_listbox != null)
			m_listbox.listBoxAddCommand();
	}

	/**
	 * Delete button clicked on a listbox
	 */
	public void listBoxDeleteCommand()
	{
		if (m_type == _TYPE_LISTBOX && m_listbox != null)
			m_listbox.listBoxDeleteCommand();
	}

	/**
	 * Clone button clicked on a listbox
	 */
	public void listBoxCloneCommand()
	{
		if (m_type == _TYPE_LISTBOX && m_listbox != null)
			m_listbox.listBoxCloneCommand();
	}

	/**
	 * A variable button clicked on a listbox
	 */
	public void listboxCommand(String				command,
							   PanelRightListbox	source)
	{
		if (m_type == _TYPE_LISTBOX && m_listbox != null)
			m_listbox.listboxCommand(command, source);
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
									String					whereFrom)
	{
		if (m_type == _TYPE_LOOKUPBOX && m_lookupbox != null)
			m_lookupbox.lookupboxAddCommand(source, whereTo, after, whereFrom);
	}

	public void lookupboxCloneCommand(PanelRightLookupbox source)
	{
		if (m_type == _TYPE_LOOKUPBOX && m_lookupbox != null)
			m_lookupbox.lookupboxCloneCommand(source);
	}

	/**
	 * A variable button clicked on a lookupbox
	 */
	public void lookupboxCommand(String					command,
								 PanelRightLookupbox	source)
	{
		if (m_type == _TYPE_LOOKUPBOX && m_lookupbox != null)
			m_lookupbox.lookupboxCommand(command, source);
	}

	/**
	 * Zoom button clicked on a lookupbox
	 */
	public void lookupboxZoomCommand(PanelRightLookupbox	source,
									 String					editName,
									 String					zoomFields,
									 String					dataSource)
	{
		if (m_type == _TYPE_LOOKUPBOX && m_lookupbox != null)
			m_lookupbox.lookupboxZoomCommand(source, editName, zoomFields, dataSource);
	}

	public Xml getLookupboxSelectedItemByObject(PanelRightLookupbox source)
	{
		if (m_type == _TYPE_LOOKUPBOX && m_lookupbox == source)
			return(m_lookupbox.getLookupboxSelectedItem());
		else
			return(null);
	}

	/**
	 * Positions the listbox on the specified entry (if found)
	 *
	 * @param source <code>Xml</code> from the non-zoom, which will relate
	 * back directly to the entry to display (if found)
	 */
	public void positionListboxTo(Xml source)
	{
		if (m_type == _TYPE_LISTBOX && m_listbox != null)
			m_listbox.positionListboxTo(source);
	}

	public void lookupboxUpdateCommand(String name)
	{
		if (name.equalsIgnoreCase(m_name) && m_type == _TYPE_LOOKUPBOX && m_lookupbox != null )
			m_lookupbox.lookupboxUpdateCommand();
	}

	/**
	 * Repaint the listbox after its related <code>Xml</code> items are updated.
	 */
	public void repaintListBox()
	{
		if (m_type == _TYPE_LISTBOX && m_listbox != null)
			m_listbox.repaintListBox();

		else if (m_type == _TYPE_LOOKUPBOX && m_lookupbox != null)
			m_lookupbox.repaintLookupBox();
	}

	public void updateRelativeToFields()
	{
		if (m_type == _TYPE_OPTIONS && m_options != null)
			m_options.updateRelativeToFields();
		else
			if (!m_fieldRelativeTo.isEmpty())
				loadData(m_source);
	}

	/**
	 * Loads the data from the <code>Xml</code> source onto/into the control.
	 *
	 * @param source xml from which to get the data
	 */
	public void loadData(Xml source)
	{
		String value;

		if (m_autoUpdate)
		{
			// Store the node this item relates to
			m_source = source;

			// If there's an explicitly named relativeTo field, we must use it
			if (!m_fieldRelativeTo.isEmpty())	// Grab the true source of the explicitly named relativeTo entry
				m_source = m_parentPR.getListboxOrLookupboxNodeByName(m_fieldRelativeTo);

			// Load the data onto the control
			if (!m_field.isEmpty())
			{
				switch (m_type)
				{
					case _TYPE_CHECKBOX:
						// See if the tag specified is found relative to the source
						if (m_checkbox != null)
						{
							if (m_source != null)
							{
								value = Utils.interpretBooleanAsYesNo(Xml.getAttributeOrChildExplicit(m_source, m_field, "", m_default, true), true);
								m_checkbox.setEnabled(true);
								if (value.equals("yes"))
									m_checkbox.setSelected(true);
								else
									m_checkbox.setSelected(false);

							}
							else if (m_source != null)
							{
								m_checkbox.setSelected(false);
								m_checkbox.setEnabled(false);
							}
							m_checkbox.repaint();
						}
						break;

					case _TYPE_TEXTBOX:
						if (m_textbox != null)
						{
							if (m_source != null)
							{
								value = m_macroMaster.parseMacros(Xml.getAttributeOrChildExplicit(m_source, m_field, "", m_default, true)).trim();
								m_textbox.setText(value);
								m_textbox.setEnabled(true);

							} else {
								m_textbox.setText("");
								m_textbox.setEnabled(false);

							}
							m_textbox.repaint();
						}
						break;

					case _TYPE_EDITBOX:
						if (m_editbox != null)
						{
							if (m_source != null)
							{
								value = m_macroMaster.parseMacros(Xml.getAttributeOrChildExplicit(m_source, m_field, "", m_default, true)).trim();
								m_editbox.setText(value);
								m_editbox.setEnabled(true);

							} else {
								value = m_macroMaster.parseMacros(Xml.getAttributeOrChildExplicit(m_source, m_field, "", m_default, true)).trim();
								m_editbox.setText("");
								m_editbox.setEnabled(false);

							}
							m_editbox.repaint();
						}
						break;
				}
			}
		}
	}

	/**
	 * Called during the edit_save command to save the current contents of the
	 * edit before then writing any listbox Xml content to disk.
	 */
	public void saveEditData()
	{
		if (m_autoUpdate)
			saveData(m_source);
	}

	/**
	 * Saves the control data to the <code>Xml</code> source.
	 *
	 * @param source xml to update
	 */
	public void saveData(Xml source)
	{
		// We always update the one we loaded data from
		source = m_source;

		// Save the entry if we're in auto-update mode and there's an actual input field
		if (m_autoUpdate && source != null && !m_field.isEmpty())
		{
			switch (m_type)
			{
				case _TYPE_CHECKBOX:
					// See if the tag specified is found relative to the source
					if (m_checkbox != null && m_checkbox.isEnabled())
					{
						if (m_checkbox.isSelected())
							Xml.setAttributeOrChildExplicit(source, m_field, "yes");
						else
							Xml.setAttributeOrChildExplicit(source, m_field, "no");
					}
					break;

				case _TYPE_TEXTBOX:
					if (m_textbox != null && m_textbox.isEnabled())
						Xml.setAttributeOrChildExplicit(source, m_field, Macros.encodeCommonMacros(m_textbox.getText()));
					break;

				case _TYPE_EDITBOX:
					if (m_editbox != null && m_editbox.isEnabled())
						Xml.setAttributeOrChildExplicit(source, m_field, Macros.encodeCommonMacros(m_editbox.getText()));
					break;
			}
		}
	}

	/**
	 * Adds the default value to the specified <code>Xml</code> when the user
	 * clicks the add button on a <code>_TYPE_LISTBOX</code> or
	 * <code>_TYPE_LOOKUPBOX</code> control which has the add button.
	 *
	 * @param whereToAdd xml to add the default field to
	 * @param includeValue true or false if the actual value should be added,
	 * as this method also doubles as a "add blank field structure" type
	 * algorithm.
	 */
	public void addDefaultField(Xml			whereToAdd,
								boolean		includeValue)
	{
		String text;
		Xml newXml;

		if (!m_field.isEmpty())
		{
			// We have a value to add here, it comes from our editable fields:
			switch (m_type)
			{
				case _TYPE_CHECKBOX:
					if (m_checkbox != null)
					{
						if (m_field.startsWith("#"))
						{
							if (whereToAdd.getAttribute(m_field.substring(1)) == null)
							{
								newXml = new Xml(m_field.substring(1), includeValue ? Utils.interpretBooleanAsYesNo(m_checkbox.getText(), false) : "");
								whereToAdd.appendAttribute(newXml);
							}

						} else {
							if (whereToAdd.getChildNode(m_field) == null)
							{
								newXml = new Xml(m_field, includeValue ? Utils.interpretBooleanAsYesNo(m_checkbox.getText(), false) : "");
								whereToAdd.appendChild(newXml);
							}
						}
					}
					break;

				case _TYPE_TEXTBOX:
					if (m_textbox != null)
					{
						text = m_textbox.getText();
						if (m_field.startsWith("#"))
						{
							if (whereToAdd.getAttribute(m_field.substring(1)) == null)
							{
								newXml = new Xml(m_field.substring(1), includeValue ? (text.isEmpty() ? m_default : text) : "");
								whereToAdd.appendAttribute(newXml);
							}

						} else {
							if (whereToAdd.getChildNode(m_field) == null)
							{
								newXml = new Xml(m_field, includeValue ? (text.isEmpty() ? m_default : text) : "");
								whereToAdd.appendChild(newXml);
							}
						}
					}
					break;

				case _TYPE_EDITBOX:
					if (m_editbox != null)
					{
						text = m_editbox.getText();
						if (m_field.startsWith("#"))
						{
							if (whereToAdd.getAttribute(m_field.substring(1)) == null)
							{
								newXml = new Xml(m_field.substring(1), includeValue ? (text.isEmpty() ? m_default : text) : "");
								whereToAdd.appendAttribute(newXml);
							}

						} else {
							if (whereToAdd.getChildNode(m_field) == null)
							{
								newXml = new Xml(m_field, includeValue ? (text.isEmpty() ? m_default : text) : "");
								whereToAdd.appendChild(newXml);
							}
						}
					}
					break;
				//default:
				//If we get here, there's a syntax error in edits.xml, but
				//we'll just ignore it
			}
		}
	}

	/**
	 * Setter sets text to display on labels, buttons and checkboxes
	 *
	 * @param text text to display
	 */
	public void setText(String text)
	{
		switch (m_type)
		{
			case _TYPE_LABEL:
				if (m_label != null)
					m_label.setText("<html>" + m_macroMaster.parseMacros(text));
				break;

			case _TYPE_LINK:
				if (m_label != null)
					m_label.setText("<html><u>" + m_macroMaster.parseMacros(text));
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
					m_button.setText(m_macroMaster.parseMacros(text));
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					m_checkbox.setText(m_macroMaster.parseMacros(text));
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					m_textbox.setText(text);
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					m_editbox.setText(text);
				break;
		}
	}

	/**
	 * Setter sets the field from which to obtain data for this control.  If
	 * explicitly related to another listbox or lookupbox, it is shown as
	 * "parentcontrol.field" or "parentcontrol.#attribute".
	 *
	 * @param field field name relative to the parent <code>Xml</code>, or
	 * to the explicitly named parent listbox or lookupbox entry, and is
	 * prefixed with "#" pound sign if attribute.
	 */
	public void setField(String field)
	{
		if (field.contains("."))
		{
			// It's relative to a specifically named listbox or lookupbox
			// parentcontrol.field or parentcontrol.#attribute
			m_field				= field.substring(field.indexOf(".") + 1);
			m_fieldRelativeTo	= field.substring(0, field.indexOf("."));

		} else {
			m_field				= field;
			m_fieldRelativeTo	= "";

		}
	}

	/**
	 * Setter sets the default value for when a blank record is added.
	 */
	public void setDefault(String d)
	{
		m_default = d;
	}

	/**
	 * Setter sets foreground color of <code>JLabel</code>
	 *
	 * @param color from Color() class
	 */
	public void setForeColor(Color color)
	{
		m_foreground = color;
		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
					m_label.setForeground(color);
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
					m_button.setForeground(color);
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					m_checkbox.setForeground(color);
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					m_textbox.setForeground(color);
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					m_editbox.setForeground(color);
				break;

			case _TYPE_LISTBOX:
				if (m_listbox != null)
					m_listbox.setForeground(color);
				break;

			case _TYPE_LOOKUPBOX:
				if (m_lookupbox != null)
					m_lookupbox.setForeground(color);
				break;
		}
	}

	/**
	 * Setter sets background color of <code>JLabel</code>.  Only used if opaque set to true.
	 *
	 * @param color from Color() class
	 */
	public void setBackColor(Color color)
	{
		m_background = color;
		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
				{
					m_label.setBackground(color);
					m_label.setOpaque(true);
				}
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
				{
					m_button.setBackground(color);
					m_button.setOpaque(true);
				}
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
				{
					m_checkbox.setBackground(color);
					m_checkbox.setOpaque(true);
				}
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
				{
					m_textbox.setBackground(color);
					m_textbox.setOpaque(true);
				}
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					m_editbox.setBackground(color);
				break;

			case _TYPE_LISTBOX:
				if (m_listbox != null)
				{
					m_listbox.setBackground(color);
					m_listbox.setOpaque(true);
				}
				break;

			case _TYPE_LOOKUPBOX:
				if (m_lookupbox != null)
				{
					m_lookupbox.setBackground(color);
					m_lookupbox.setOpaque(true);
				}
				break;
		}
	}

	/**
	 * Setter sets the associated command for this <code>JLabel</code> when user clicks their mouse.
	 * Populated from PanelFactory, or dynamically as system parameters change.
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
	public void setCommand(String command,
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
		m_command		= command;
		m_commandP1		= p1;
		m_commandP2		= p2;
		m_commandP3		= p3;
		m_commandP4		= p4;
		m_commandP5		= p5;
		m_commandP6		= p6;
		m_commandP7		= p7;
		m_commandP8		= p8;
		m_commandP9		= p9;
		m_commandP10	= p10;
	}

	/**
	 * Setter sets the associated <code>_TYPE_LISTBOX</code> and
	 * <code>_TYPE_LOOKUPBOX</code> listing parameters.  Populated from
	 * PanelFactory, or dynamically as list filters change.  The list is
	 * populated dynamically when called to update its list contents.
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
		if (m_type == _TYPE_LISTBOX)
			m_listbox.setListBy(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		else if (m_type == _TYPE_LOOKUPBOX)
			m_lookupbox.setListBy(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
	}

	/**
	 * Setter sets the associated <code>_TYPE_LISTBOX</code> and
	 * <code>_TYPE_LOOKUPBOX</code> listing parameters.  Populated from
	 * PanelFactory, or dynamically as list filters change.  The list is
	 * populated dynamically when called to update its list contents.
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
		if (m_type == _TYPE_LISTBOX)
			m_listbox.setDblClick(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		else if (m_type == _TYPE_LOOKUPBOX)
			m_lookupbox.setDblClick(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
	}

	/**
	 * Setter sets the associated <code>_TYPE_LISTBOX</code> and
	 * <code>_TYPE_LOOKUPBOX</code> listing parameters.  Populated from
	 * PanelFactory, or dynamically as list filters change.  The list is
	 * populated dynamically when called to update its list contents.
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
		if (m_type == _TYPE_LISTBOX)
			m_listbox.setEnter(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		else if (m_type == _TYPE_LOOKUPBOX)
			m_lookupbox.setEnter(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
	}

	/**
	 * Specifies the add button is allowed on the <code>_TYPE_LISTBOX</code>
	 * control, or on the <code>_TYPE_LOOKUPBOX</code>.
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
		if (m_type == _TYPE_LISTBOX)
			m_listbox.setAddButton(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		else if (m_type == _TYPE_LOOKUPBOX)
			m_lookupbox.setAddButton(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
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
		m_listbox.setDeleteButton(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
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
		if (m_type == _TYPE_LISTBOX)
			m_listbox.setCloneButton(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		else if (m_type == _TYPE_LOOKUPBOX)
			m_lookupbox.setCloneButton(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
	}

	/**
	 * Specifies the subtract button is allowed on the <code>_TYPE_LOOKUPBOX</code>
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
	public void setSubtractButton(String command,
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
		m_lookupbox.setSubtractButton(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
	}

	/**
	 * Specifies the zoom button is allowed on the <code>_TYPE_LOOKUPBOX</code>
	 * control.
	 *
	 * @param command command to execute when the button is clicked
	 * @param autoPreview is there an autoPreview option?
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
	public void setZoomButton(String	 command,
							  boolean	autoPreview,
							  String	 p1,
							  String	 p2,
							  String	 p3,
							  String	 p4,
							  String	 p5,
							  String	 p6,
							  String	 p7,
							  String	 p8,
							  String	 p9,
							  String	 p10)
	{
		m_lookupbox.setZoomButton(command, autoPreview, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
	}

	/**
	 * Specifies the up button is allowed on the <code>_TYPE_LOOKUPBOX</code>
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
		if (m_type == _TYPE_LOOKUPBOX && m_lookupbox != null)
			m_lookupbox.setUpButton(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		else if (m_type == _TYPE_LISTBOX && m_listbox != null)
			m_listbox.setUpButton(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
	}

	/**
	 * Specifies the down button is allowed on the <code>_TYPE_LOOKUPBOX</code>
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
		if (m_type == _TYPE_LOOKUPBOX && m_lookupbox != null)
			m_lookupbox.setDownButton(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		else if (m_type == _TYPE_LISTBOX && m_listbox != null)
			m_listbox.setDownButton(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
	}

	/**
	 * Specifies edit options for the <code>_TYPE_LOOKUPBOX</code> control.
	 *
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
	 * @param p11 eleventh parameter
	 * @param p12 twelfth parameter
	 * @param p13 thirteenth parameter
	 * @param p14 fourteenth parameter
	 * @param p15 fifteenth parameter
	 * @param p16 sixteenth parameter
	 * @param p17 seventeenth parameter
	 * @param p18 eighteenth parameter
	 * @param p19 nineteenth parameter
	 * @param p20 twentieth parameter
	 */
	public void setRelativeEdits(String p1,
								 String p2,
								 String p3,
								 String p4,
								 String p5,
								 String p6,
								 String p7,
								 String p8,
								 String p9,
								 String p10,
								 String p11,
								 String p12,
								 String p13,
								 String p14,
								 String p15,
								 String p16,
								 String p17,
								 String p18,
								 String p19,
								 String p20)
	{
		if (m_type == _TYPE_LOOKUPBOX && m_lookupbox != null)
			m_lookupbox.setRelativeEdits(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20);
	}

	/**
	 * Specifies edit options for the <code>_TYPE_LOOKUPBOX</code> control.
	 *
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
	 * @param p11 eleventh parameter
	 * @param p12 twelfth parameter
	 * @param p13 thirteenth parameter
	 * @param p14 fourteenth parameter
	 * @param p15 fifteenth parameter
	 * @param p16 sixteenth parameter
	 * @param p17 seventeenth parameter
	 * @param p18 eighteenth parameter
	 * @param p19 nineteenth parameter
	 * @param p20 twentieth parameter
	 */
	public void setRelativeOptions(String p1,
								   String p2,
								   String p3,
								   String p4,
								   String p5,
								   String p6,
								   String p7,
								   String p8,
								   String p9,
								   String p10,
								   String p11,
								   String p12,
								   String p13,
								   String p14,
								   String p15,
								   String p16,
								   String p17,
								   String p18,
								   String p19,
								   String p20)
	{
		if (m_type == _TYPE_LOOKUPBOX && m_lookupbox != null)
			m_lookupbox.setRelativeOptions(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20);
	}

	/**
	 * Setter sets the on-select command to execute, along with parameters
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
		if (m_type == _TYPE_LISTBOX)
			m_listbox.setOnSelect(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		else if (m_type == _TYPE_LOOKUPBOX)
			m_lookupbox.setOnSelect(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
	}

	public void setName(String name)
	{
		m_name = name;
	}

	public String getName()
	{
		return(m_name);
	}

	/**
	 * Specifies the relative path to the root node of the xml file to access
	 * the data element for the <code>_TYPE_LISTBOX</code> control.
	 *
	 * @param source dot source, as in <code>opbm.scriptdata.flows</code>
	 * @param relativeTo relative source, as when the related item changes, this
	 * entry must be reloaded
	 */
	public void setSource(String	source,
						  String	relativeTo)
	{
		if (m_type == _TYPE_LISTBOX)
			m_listbox.setSource(source, relativeTo);

		else if (m_type == _TYPE_LOOKUPBOX)
			m_lookupbox.setSource(source, relativeTo);
	}

	/**
	 * Specifies the relative path to the root node of the xml file to access
	 * the template data element for the <code>_TYPE_LISTBOX</code> control.
	 *
	 * @param source dot source, as in <code>opbm.scriptdata.templates.flow</code>
	 * @param relativeTo relative source, as when the related item changes, this
	 * entry must be reloaded
	 */
	public void setTemplate(String	source,
							String	relativeTo)
	{
		if (m_type == _TYPE_LISTBOX)
			m_listbox.setTemplate(source, relativeTo);
	}

	/**
	 * Return the currently selected node from the specified
	 * <code>_TYPE_LISTBOX</code> or <code>_TYPE_LOOKUPBOX</code>
	 * @return <code>Xml</code> for the selected item in the listbox or lookupbox
	 */
	public Xml getListboxOrLookupboxFirstChildNode()
	{
		if (m_type == _TYPE_LISTBOX)
			return(m_listbox.getListboxFirstChildNode());

		else if (m_type == _TYPE_LOOKUPBOX)
			return(m_lookupbox.getLookupboxFirstChildNode());

		else
			return(null);
	}

	/**
	 * Return the currently selected node from the specified
	 * <code>_TYPE_LISTBOX</code> or <code>_TYPE_LOOKUPBOX</code>
	 * @return <code>Xml</code> for the selected item in the listbox or lookupbox
	 */
	public Xml getListboxOrLookupboxNode()
	{
		if (m_type == _TYPE_LISTBOX)
			return(m_listbox.getListboxNode());

		else if (m_type == _TYPE_LOOKUPBOX)
			return(m_lookupbox.getLookupboxNode());

		else
			return(null);
	}

	/**
	 * Return the currently selected node's source for the specified
	 * <code>_TYPE_LISTBOX</code> or <code>_TYPE_LOOKUPBOX</code>
	 * @return source in scripts.xml for the selected item in the listbox or lookupbox
	 */
	public String getListboxOrLookupboxSource()
	{
		if (m_type == _TYPE_LISTBOX)
			return(m_listbox.getListboxSource());

		else if (m_type == _TYPE_LOOKUPBOX)
			return(m_lookupbox.getLookupboxSource());

		else
			return(null);
	}

	public String getListboxTemplate()
	{
		if (m_type == _TYPE_LISTBOX)
			return(m_listbox.getListboxTemplate());
		else
			return("");
	}

	/**
	 * Specifies the xml filename to load for the <code>_TYPE_LISTBOX</code> control.
	 *
	 * @param fileName xml file to load for listbox
	 */
	public void setFileName(String fileName)
	{
		if (m_type == _TYPE_LISTBOX)
			m_listbox.setFileName(fileName);

		else if (m_type == _TYPE_LOOKUPBOX)
			m_lookupbox.setFileName(fileName);
	}

	/**
	 * Specifies where source data from which the <code>_TYPE_LISTBOX</code>
	 * control is populated.
	 *
	 * @param location  absolute newXml within the xml file to reach the list of data items
	 */
	public void setLocation(String location)
	{
		if (m_type == _TYPE_LISTBOX)
			m_listbox.setLocation(location);

		else if (m_type == _TYPE_LOOKUPBOX)
			m_lookupbox.setLocation(location);
	}

	/**
	 * Specifies which entries within newXml will be pulled (allows a filter
	 * within a larger data set, to only pull out those named tags) for the
	 * <code>_TYPE_LISTBOX</code> controls.
	 *
	 * @param forEach specifies relative tag within newXml for enumerated items
	 */
	public void setForEach(String forEach)
	{
		if (m_type == _TYPE_LISTBOX)
			m_listbox.setForEach(forEach);

		else if (m_type == _TYPE_LOOKUPBOX)
			m_lookupbox.setForEach(forEach);
	}

	/**
	 * Called to specify what listbox or lookupbox in the current we are to
	 * display options relative to
	 * @param relativeTo listbox or lookupbox name
	 */
	public void setOptionsRelativeTo(String relativeTo)
	{
		if (m_type == _TYPE_OPTIONS)
			m_options.setOptionsRelativeTo(relativeTo);
	}

	/**
	 * Setter sets the tooltip text displayed when user is over this <code>JLabel</code>
	 *
	 * @param tooltip text to display
	 */
	public void setTooltip(String tooltip)
	{
		m_tooltip = tooltip;
	}

	/**
	 * Setter sets the font to display
	 *
	 * @param font Font() class
	 */
	public void setFont(Font font)
	{
		m_font = font;
		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
					m_label.setFont(font);
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
					m_button.setFont(font);
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					m_checkbox.setFont(font);
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					m_textbox.setFont(font);
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					m_editbox.setFont(font);
				break;

			case _TYPE_LISTBOX:
				if (m_listbox != null)
					m_listbox.setFont(font);
				break;

			case _TYPE_LOOKUPBOX:
				if (m_lookupbox != null)
					m_lookupbox.setFont(font);
				break;
		}
	}

	/**
	 * Setter positions the <code>JLabel</code> component within the parent <code>JPanel</code>.
	 * Stored as a <code>Dimension</code>.
	 *
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	public void setPosition(int x,
							int y)
	{
		m_x	= x;
		m_y	= y;

		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
					m_label.setLocation(x, y);
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
					m_button.setLocation(x, y);
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					m_checkbox.setLocation(x, y);
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					m_textbox.setLocation(x, y);
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					m_editboxScroller.setLocation(x, y);
					m_editbox.setLocation(x, y);
				break;

			case _TYPE_LISTBOX:
				if (m_listbox != null)
					m_listbox.setLocation(x, y);
				break;

			case _TYPE_LOOKUPBOX:
				if (m_lookupbox != null)
					m_lookupbox.setLocation(x, y);
				break;
		}
	}

	/**
	 * Setter sets the buttonWidth and height of the <code>JLabel</code>.  Stored as a <code>Dimension</code>.
	 *
	 * @param width Width of the <code>JLabel</code>
	 * @param height Height of the <code>JLabel</code>
	 */
	public void setSize(int width,
						int height)
	{
		Dimension d = new Dimension(width, height);
		m_width		= width;
		m_height	= height;
		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
				{
					m_label.setMinimumSize(d);
					m_label.setMaximumSize(d);
					m_label.setPreferredSize(d);
					m_label.setSize(d);
				}
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
				{
					m_button.setMinimumSize(d);
					m_button.setMaximumSize(d);
					m_button.setPreferredSize(d);
					m_button.setSize(d);
				}
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
				{
					m_checkbox.setMinimumSize(d);
					m_checkbox.setMaximumSize(d);
					m_checkbox.setPreferredSize(d);
					m_checkbox.setSize(d);
				}
				break;

			case _TYPE_TEXTBOX:
				// For textbox items, the label is displayed also, so this setText updates the label, the setData method updates textbox data
				if (m_textbox != null)
				{
					m_textbox.setMinimumSize(d);
					m_textbox.setMaximumSize(d);
					m_textbox.setPreferredSize(d);
					m_textbox.setSize(d);
				}
				break;

			case _TYPE_EDITBOX:
				// For editbox items, the label is displayed also, so this setText updates the label, the setData method updates editbox data
				if (m_editbox != null)
				{
					m_editboxScroller.setMinimumSize(d);
					m_editboxScroller.setMaximumSize(d);
					m_editboxScroller.setPreferredSize(d);
					m_editboxScroller.setSize(d);
					m_editbox.setMinimumSize(d);
					m_editbox.setMaximumSize(d);
					m_editbox.setPreferredSize(d);
					m_editbox.setSize(d);
				}
				break;

			case _TYPE_LISTBOX:
				// For listbox items, an optional label is displayed also, so this setText updates the label if available, the setData method updates listbox data
				if (m_listbox != null)
					m_listbox.setSize(width, height);
				break;

			case _TYPE_LOOKUPBOX:
				// For listbox items, an optional label is displayed also, so this setText updates the label if available, the setData method updates listbox data
				if (m_lookupbox != null)
					m_lookupbox.setSize(width, height);
				break;
		}
	}

	/**
	 * For controls which have readonly options, or the ability to be edited,
	 * manipulated, or not, this setting will update the raw control's property.
	 * For checkbox, it is enabled or not. For textbox and editbox, it is set to
	 * editable or not, which still allows navigation.
	 *
	 * @param readonly
	 */
	public void setReadOnly(boolean readonly)
	{
		switch (m_type)
		{
			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					m_checkbox.setEnabled(!readonly);
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					m_textbox.setEditable(!readonly);
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					m_editbox.setEditable(!readonly);
				break;

			default:
			// For other controls, we don'text adjust anything
				break;
		}
	}

	/**
	 * Getter returns the text originally defined (not what visibly appears on
	 * the label, as it will have macros expanded and possibly be marked up with
	 * HTML codes.
	 *
	 * @return original un-expanded form of the string
	 */
	public String getText()
	{
		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
					return(m_label.getText());
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
					return(m_button.getText());
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					return(m_checkbox.getText());
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					return(m_textbox.getText());
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					return(m_editbox.getText());
				break;
		}
		return("");
	}

	/**
	 * Getter returns the foreground color.
	 *
	 * @return Color() class object
	 */
	public Color getForeColor()
	{
		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
					return(m_label.getForeground());
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
					return(m_button.getForeground());
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					return(m_checkbox.getForeground());
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					return(m_textbox.getForeground());
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					return(m_editbox.getForeground());
				break;

			case _TYPE_LISTBOX:
				if (m_listbox != null)
					return(m_listbox.getForeground());
				break;

			case _TYPE_LOOKUPBOX:
				if (m_lookupbox != null)
					return(m_lookupbox.getForeground());
				break;
		}
		return(Color.BLACK);
	}

	/**
	 * Getter returns the background color.
	 *
	 * @return Color() class object.
	 */
	public Color getBackColor()
	{
		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
					return(m_label.getBackground());
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
					return(m_button.getBackground());
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					return(m_checkbox.getBackground());
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					return(m_textbox.getBackground());
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					return(m_editbox.getBackground());
				break;

			case _TYPE_LISTBOX:
				if (m_listbox != null)
					return(m_listbox.getBackground());
				break;

			case _TYPE_LOOKUPBOX:
				if (m_lookupbox != null)
					return(m_lookupbox.getBackground());
				break;
		}
		return(Color.WHITE);
	}

	/**
	 * Getter returns the un-expanded tooltip text.
	 *
	 * @return tooltip text
	 */
	public String getTooltip()
	{
		return(m_tooltip);
	}

	/**
	 * Getter returns the font used for the <code>JLabel</code>
	 *
	 * @return Font() class object
	 */
	public Font getFont()
	{
		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
					return(m_label.getFont());
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
					return(m_button.getFont());
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					return(m_checkbox.getFont());
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					return(m_textbox.getFont());
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					return(m_editbox.getFont());
				break;

			case _TYPE_LISTBOX:
				if (m_listbox != null)
					return(m_listbox.getFont());
				break;

			case _TYPE_LOOKUPBOX:
				if (m_lookupbox != null)
					return(m_lookupbox.getFont());
				break;
		}
		return(null);
	}

	/**
	 * Getter returns the current position of the object
	 *
	 * @return as Dimension() class object
	 */
	public Dimension getPosition()
	{
		Point pt = new Point(0,0);

		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
					pt = m_label.getLocation();
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
					pt = m_button.getLocation();
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					pt = m_checkbox.getLocation();
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					pt = m_textbox.getLocation();
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					pt = m_editbox.getLocation();
				break;

			case _TYPE_LISTBOX:
				if (m_listbox!= null)
					pt = m_listbox.getLocation();
				break;

			case _TYPE_LOOKUPBOX:
				if (m_lookupbox!= null)
					pt = m_lookupbox.getLocation();
				break;
		}
		return(new Dimension(pt.x, pt.y));
	}

	/**
	 * Getter returns the current size of the object
	 *
	 * @return as Dimension() class object
	 */
	public Dimension getSize()
	{
		switch (m_type)
		{
			case _TYPE_LABEL:
			case _TYPE_LINK:
				if (m_label != null)
					return(m_label.getSize());
				break;

			case _TYPE_BUTTON:
				if (m_button != null)
					return(m_button.getSize());
				break;

			case _TYPE_CHECKBOX:
				if (m_checkbox != null)
					return(m_checkbox.getSize());
				break;

			case _TYPE_TEXTBOX:
				if (m_textbox != null)
					return(m_textbox.getSize());
				break;

			case _TYPE_EDITBOX:
				if (m_editbox != null)
					return(m_editbox.getSize());
				break;

			case _TYPE_LISTBOX:
				if (m_listbox != null)
					return(m_listbox.getSize());
				break;

			case _TYPE_LOOKUPBOX:
				if (m_lookupbox != null)
					return(m_lookupbox.getSize());
				break;
		}
		return(new Dimension(0, 0));
	}

	public int getWidth()
	{
		return(m_width);
	}

	public int getHeight()
	{
		return(m_height);
	}

	public int getX()
	{
		return(m_x);
	}

	public int getY()
	{
		return(m_y);
	}

	public boolean getAutoUpdate()
	{
		return(m_autoUpdate);
	}

	public PanelRight getParentPR()
	{
		return(m_parentPR);
	}

	/**
	 * Not used, but defined as override for listener.
	 *
	 * @param e focus event
	 */
	@Override
	public void focusGained(FocusEvent e) {
	}

	/**
	 * When focus is lost, update the contents of the control, as well as the
	 * listbox in case it contains a field which is used for displaying on
	 * the listbox.
	 *
	 * @param e focus event
	 */
	@Override
	public void focusLost(FocusEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (m_autoUpdate)
		{
			if (m_parentPR != null && m_source != null)
			{
				saveData(m_source);
			}
		}
	}


	// Types supported for this class, each PanelRightItem can take on one of these identities at a time:
	static final int _TYPE_LABEL		= 1;
	static final int _TYPE_BUTTON		= 2;
	static final int _TYPE_CHECKBOX		= 3;
	static final int _TYPE_TEXTBOX		= 4;
	static final int _TYPE_EDITBOX		= 5;
	static final int _TYPE_LISTBOX		= 6;
	static final int _TYPE_LINK			= 7;
	static final int _TYPE_LOOKUPBOX	= 8;
	static final int _TYPE_OPTIONS		= 9;

	private Opbm				m_opbm;
	private Macros				m_macroMaster;
	private Commands			m_commandMaster;
	private PanelRight			m_parentPR;
	private Label				m_statusBar;
	private JPanel				m_panelParent;
	private Color				m_background;
	private Color				m_foreground;
	private Font				m_font;
	private int					m_x;
	private int					m_y;
	private int					m_width;
	private int					m_height;
	private boolean				m_autoUpdate;
	private String				m_name;

	// Variables supporting the various types (not all of them are used all the time)
	private int					m_type;					// Used by:
	private JLabel				m_label;				// Labels, Textbox, Editbox, (optional Listbox)
	private JButton				m_button;				// Button
	private JCheckBox			m_checkbox;				// Checkbox
	private JTextField			m_textbox;				// Textbox
	private JScrollPane			m_editboxScroller;			// For editbox, provides scrolling
	private JTextArea			m_editbox;				// Editbox
	private PanelRightListbox	m_listbox;				// Listbox
	private PanelRightLookupbox	m_lookupbox;			// Lookupbox
	private PanelRightOptions	m_options;				// Options (variable input based on user-defined variables)
	private Xml					m_source;				// The Xml entry for which this item was last updated/used

	private String				m_field;				// Associated field if this is an editable control with a listbox
	private String				m_fieldRelativeTo;		// If field is relative to a named listbox or lookupbox, then it's stored here, otherwise the default
	private String				m_default;				// Default value if nothing is populated in field
	private String				m_command;
	private String				m_commandP1;			// Parameter #1
	private String				m_commandP2;			// Parameter #2
	private String				m_commandP3;			// Parameter #3
	private String				m_commandP4;			// Parameter #4
	private String				m_commandP5;			// Parameter #5
	private String				m_commandP6;			// Parameter #6
	private String				m_commandP7;			// Parameter #7
	private String				m_commandP8;			// Parameter #8
	private String				m_commandP9;			// Parameter #9
	private String				m_commandP10;			// Parameter #10
	private String				m_tooltip;
}
