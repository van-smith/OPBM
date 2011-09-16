/*
 * OPBM - Office Productivity Benchmark
 *
 * This class handles all variable options for the right-side panel items,
 * which are the edit screens.  Variable items come in the form of an array
 * of field names and their labels, and display them as they are relative to
 * the the specified Xml tag.  They allow non-hard-coded variables to be
 * created and added via changes in the UI, which write changes to scripts.xml,
 * which are then used to augment data in edits.xml.
 *
 * To see how this works today, look at the abstracts and their fields which
 * look like this:
 *		{:tagName, description to display to user:}
 *
 * and then compare that to the output written to each
 * opbm.scriptdata.atoms.atom.abstract entry in scripts.xml, and see how those
 * prompts result in data being populated into scripts.xml.
 *
 * Last Updated:  Sep 12, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.2.0
 *
 */

package opbm.panels.right;

import opbm.common.Utils;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import opbm.common.Commands;
import opbm.common.Macros;
import opbm.Opbm;
import opbm.common.Xml;
import opbm.panels.PanelFactory;

public class PanelRightOptions
				implements MouseListener
{
	public PanelRightOptions(Opbm				opbm,
							 JPanel				parentPanel,
							 PanelRight			parentPR,
							 PanelRightItem		parentPRI,
							 Commands			commandMaster,
							 Macros				macroMaster)
	{
		m_opbm				= opbm;
		m_parentPanel		= parentPanel;
		m_parentPR			= parentPR;
		m_parentPRI			= parentPRI;
		m_commandMaster		= commandMaster;
		m_macroMaster		= macroMaster;

		m_xmlOptionsMaster	= new ArrayList<Xml>(0);
		m_priOptionsMaster	= new ArrayList<PanelRightItem>(0);
		m_fieldNames		= new ArrayList<String>(0);
		m_fieldPrompts		= new ArrayList<String>(0);
		m_relativeTo		= "";
	}

	/**
	 * Called to specify what listbox or lookupbox in the current we are to
	 * display options relative to
	 * @param relativeTo listbox or lookupbox edit
	 */
	public void setOptionsRelativeTo(String relativeTo)
	{
		m_relativeTo = relativeTo;
	}

	/**
	 * Called to update the relative to fields based on the current edit context
	 */
	public void updateRelativeToFields()
	{
		int i;
		PanelRightLookupbox lookupbox;
		Xml xml, child, options;
		String group, sourceName, candidate;
		List<Xml> editsXml = new ArrayList<Xml>(0);

		// Remove all existing fields
		for (i = 0; i < m_priOptionsMaster.size(); i++)
			m_parentPR.removePanelRightItem(m_priOptionsMaster.get(i));

		m_priOptionsMaster.clear();
		m_xmlOptionsMaster.clear();

		// Find our group lookupbox (options cannot appear next to listboxes,
		// as they are designed to be explicit single-group data items, which
		// means the developer will know what fields to store within, explicitly)
		lookupbox = m_parentPR.getLookupboxByName(m_relativeTo);
		if (lookupbox == null)
			return;	// Nothing to do, because the named control wasn't found
		// It's relative to this lookupbox

		// Source Xml highlighted in the lookupbox
		xml = lookupbox.getLookupboxNode();
		if (xml == null)
			return;	// Nothing to do, because no item is selected in this list

		// Based on the type of entry we're on, grab its associated edit
		group = lookupbox.getOptionsForXml(xml.getName());
		if (group.isEmpty())
			return;	// Nothing to do, because the named edit wasn't found

		// Grab its sourcename attribute, to lookup the original entry in scripts.xml,
		// to parse its content for options fields
		sourceName = xml.getAttributeOrChild("sourcename");
		if (sourceName.isEmpty())
		{
// REMEMBER, this dialog needs to be added to something the user can review, a log or something
//			JOptionPane.showMessageDialog(m_parentPR.getParent(),
//
//										  "The SCRIPTS.XML file contains a missing 'sourcename' attribute on the " +
//												edit +
//												" entry named " +
//												xml.getAttributeOrChild("name") +
//												".\nIt should contain both [name=\"" +
//												xml.getAttributeOrChild("name") +
//												"\"] and [sourcename=\"x\"], where sourcename relates back to the original " +
//												group +
//												" entry and will contain all of the optional fields specified by data input.\n\nThis data error must be manually corrected in the Xml file.",
//
//										  "Data Error",
//										  JOptionPane.WARNING_MESSAGE);
			return;	// Nothing to do, because the scripts.xml file is not formatted properly
		}

		// Grab a list of the original edit entries, to locate the one we're searching for
		Xml.getNodeList(editsXml, m_opbm.getScriptsXml(), group, false);
		if (editsXml.isEmpty())
		{
// REMEMBER, this dialog needs to be added to something the user can review, a log or something
//			JOptionPane.showMessageDialog(m_parentPR.getParent(),
//
//										  "The SCRIPTS.XML file is missing the " +
//												search +
//												" entry named " +
//												sourceName +
//												".\n\nThis missing entry will have to be added to display this " + edit + ".",
//
//										  "Data Error",
//										  JOptionPane.WARNING_MESSAGE);
			return;	// Nothing to do, because the orginal group edit for this entry wasn't found
		}

		// Iterate through until we find our entry
		m_fieldNames.clear();
		m_fieldPrompts.clear();
		for (i = 0; i < editsXml.size(); i++)
		{
			// Looking for this sourceName we have against the name of the original entry
			if (editsXml.get(i).getAttribute("name").equalsIgnoreCase(sourceName))
			{
				// This is our matching entry, parse its children to extract all option fields (if any)
				child = editsXml.get(i).getFirstChild();
				while (child != null)
				{
					// Extract this field's entries (if any)
					candidate = m_macroMaster.parseMacros(child.getText());
					if (!candidate.isEmpty())
						Utils.extractCustomInputFields(m_fieldNames, m_fieldPrompts, candidate);

					// Move to next entry
					child = child.getNext();
				}

				// When we get here, we have everything
				if (m_fieldNames.isEmpty())
					return;	// Nothing to do for this entry, no options to display
				// If we get here, there are entries to add

				// Add those variable entries to the form (if any)
				PanelFactory.createRightPanelContextOptionsFromCustomInputsArrays(m_parentPR,
																				  m_parentPanel,
																				  m_opbm,
																				  m_macroMaster,
																				  m_commandMaster,
																				  m_priOptionsMaster,
																				  m_xmlOptionsMaster,
																				  m_fieldNames,
																				  m_fieldPrompts,
																				  xml.getChildNode("options"),
																				  m_parentPRI.getX(),
																				  m_parentPRI.getY());
				// All done!
				return;
			}
		}
		// If we get here, it wasn't found
// REMEMBER, this dialog needs to be added to something the user can review, a log or something
//		JOptionPane.showMessageDialog(m_parentPR.getParent(),
//
//									  "The SCRIPTS.XML file is missing the " +
//											search +
//											" entry named " +
//											sourceName +
//											".\n\nThis missing entry will have to be added to display this " + edit + ".",
//
//									  "Data Error",
//									  JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Not used but required for override
	 * @param e mouse event
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * Not used but required for override
	 * @param e mouse event
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * Not used but required for override
	 * @param e
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Not used but required for override
	 * @param e mouse event
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Not used but required for override
	 * @param e mouse event
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	private Opbm					m_opbm;
	private JPanel					m_parentPanel;
	private PanelRight				m_parentPR;
	private PanelRightItem			m_parentPRI;
	private Commands				m_commandMaster;
	private Macros					m_macroMaster;

	private List<Xml>				m_xmlOptionsMaster;
	private List<PanelRightItem>	m_priOptionsMaster;
	private List<String>			m_fieldNames;
	private List<String>			m_fieldPrompts;
	private String					m_relativeTo;
}
