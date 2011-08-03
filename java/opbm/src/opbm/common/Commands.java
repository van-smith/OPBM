/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for all Command processing.  The
 * main OPBM class creates a m_commandMaster member variale, which is
 * propagated throughout the system and currently is used as the sole
 * entity for processing, but in the future additional Commands class
 * items could be created which dynamically load commands from an XML
 * file, etc.
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

package opbm.common;

import opbm.panels.PanelRightLookupbox;
import opbm.panels.PanelRightListbox;
import opbm.panels.PanelRightItem;
import javax.swing.JOptionPane;
import opbm.Opbm;

public class Commands
{
	/** Constructor
	 *
	 * @param opbm Parent object referenced for global method calls
	 * @param macroMaster Macros to use for processing
	 */
	public Commands(Opbm		opbm,
					Macros		macroMaster,
					Settings	settingsMaster)
	{
		m_opbm				= opbm;
		m_macroMaster		= macroMaster;
		m_settingsMaster	= settingsMaster;
	}

	/** Parses commands from panels.xml or in any other string.
	 * Up to six optional parameters are always included (even if only as
	 * empty strings), and are used for some commands.
	 *
	 * @param commandOriginal Original command before being converted to lower-case for testing.
	 * @param p1 First command parameter
	 * @param p2 Second command parameter
	 * @param p3 Third command parameter
	 * @param p4 Fourth command parameter
	 * @param p5 Fifth command parameter
	 * @param p6 Sixth command parameter
	 * @param p7 Seventh command parameter
	 * @param p8 Eighth command parameter
	 * @param p9 Ninth command parameter
	 * @param p10 Tenth command parameter
	 */
	public void processCommand(Object	source,
							   String	commandOriginal,
							   String	p1,
							   String	p2,
							   String	p3,
							   String	p4,
							   String	p5,
							   String	p6,
							   String	p7,
							   String	p8,
							   String	p9,
							   String	p10)
	{
		Xml xml;
		String command, fileName;


		command = commandOriginal.trim().toLowerCase();
		if (command == null || command.isEmpty())
			return;	// Nothing to do

//////////
// Quit
		if (command.equals("quit")) {
			// Exiting the system
			System.exit(0);


//////////
// Back
		} else if (command.equals("back")) {
			// Navigating back through chain
			m_opbm.navigateBack();
			// Note:  For rawedits and edits, use the rawedit_* and edit_* commands below


//////////
// Home
		} else if (command.equals("home")) {
			// Navigating back through chain
			m_opbm.navigateHome();


//////////
// LeftPanel
		} else if (command.equals("leftpanel")) {
			// Navigating to a leftpanel
			m_opbm.navigateToLeftPanel(m_opbm.expandMacros(p1));


//////////
// Raw Edit related
		} else if (command.equals("rawedit")) {
			// Raw editing (full-page edit box) of whatever file is specified
			m_opbm.rawedit(m_opbm.expandMacros(p1));

		} else if (command.equals("rawedit_save")) {
			// Saving the current contents of the active rawedit
			m_opbm.rawEditSave();

		} else if (command.equals("rawedit_save_and_close")) {
			// Saving the current contents of the active rawedit, and returning to the previous panel
			m_opbm.rawEditSaveAndClose();
			m_opbm.navigateBack();

		} else if (command.equals("rawedit_close") || command.equals("rawedit_back")) {
			// Saving the current contents of the active rawedit, and returning to the previous panel
			m_opbm.rawEditClose();
			m_opbm.navigateBack();

		} else if (command.equals("rawedit_home")) {
			// Navigating back through chain
			m_opbm.rawEditClose();
			m_opbm.navigateHome();


//////////
// Edit related
		} else if (command.equals("edit")) {
			// Raw editing (full-page edit box) of whatever file is specified
			m_opbm.edit(m_opbm.expandMacros(p1));

		} else if (command.equals("edit_save")) {
			// Saving the current contents of the active rawedit
			m_opbm.editSave();

		} else if (command.equals("edit_save_and_close")) {
			// Saving the current contents of the active rawedit, and returning to the previous panel
			m_opbm.editSave();
			m_opbm.editClose();
			m_opbm.navigateBack();

		} else if (command.equals("edit_close") || command.equals("edit_back")) {
			// Saving the current contents of the active rawedit, and returning to the previous panel
			m_opbm.editClose();
			m_opbm.navigateBack();

		} else if (command.equals("edit_home")) {
			// Navigating back through chain
			m_opbm.editClose();
			m_opbm.navigateHome();


//////////
// LISTBOX BUTTONS
		} else if (command.equals("listbox_add")) {
			// User clicked on the "add" listbox button on the flow control input
			m_opbm.listBoxAddCommand();

		} else if (command.equals("listbox_delete")) {
			// User clicked on the "delete" listbox button on the flow control input
			m_opbm.listBoxDeleteCommand();

		} else if (command.equals("listbox_clone")) {
			// User clicked on the "clone" listbox button on the flow control input
			m_opbm.listBoxCloneCommand();

		} else if (command.equals("listbox_move_up")) {
			// User clicked on the "Up" listbox button
			m_opbm.listBoxCommand("up", (PanelRightListbox)source);

		} else if (command.equals("listbox_move_down")) {
			// User clicked on the "Down" listbox button
			m_opbm.listBoxCommand("down", (PanelRightListbox)source);


//////////
// LOOKUPBOX BUTTONS
		} else if (command.equals("lookupbox_add")) {
			// User clicked on the "add" listbox button on the flow control input
			// p1 = whereTo
			// p2 = after
			// p3 = whereFrom
			// p4 = allow customs?
			m_opbm.lookupboxAddCommand((PanelRightLookupbox)source, p1, p2, p3, Utils.interpretBooleanAsYesNo(p4, true).equalsIgnoreCase("yes"));

		} else if (command.equals("lookupbox_subtract")) {
			// User clicked on the "subtract" listbox button
			m_opbm.lookupboxCommand("subtract", (PanelRightLookupbox)source);

		} else if (command.equals("lookupbox_move_up")) {
			// User clicked on the "Up" listbox button
			m_opbm.lookupboxCommand("up", (PanelRightLookupbox)source);

		} else if (command.equals("lookupbox_move_down")) {
			// User clicked on the "Down" listbox button
			m_opbm.lookupboxCommand("down", (PanelRightLookupbox)source);

		} else if (command.equals("lookupbox_zoom")) {
			// User clicked on the "zoom" listbox button
			// p1 = name of edit to use for display
			// p2 = reference in "zoom" field in edit to only show those fields which contain that portion
			// p3 = override for data source (if present)
			p3 = "";
			if (p1.equalsIgnoreCase("relativeto"))
			{
				// The name isn't a hard-coded name, because it is one of a list.
				// We have to find out which name belongs here based on whatever
				// one of the list is currently selected.
				xml = m_opbm.getListboxOrLookupboxSelectedItem((PanelRightLookupbox)source);
				if (xml == null)
				{ // Cannot execut the command because nothing is selected, or there is no data
					return;
				}
				p1 = m_macroMaster.parseMacros(((PanelRightLookupbox)source).getEditForXml(xml.getName()));
				if (p1.contains(":"))
				{
					// We have to separate out the edit from the location where the data source is specified
					p3 = p1.substring(p1.indexOf(":") + 1);
					p1 = p1.substring(0, p1.indexOf(":"));
				}

			} else {
				p1 = m_macroMaster.parseMacros(p1);

			}

			if (!p1.isEmpty()) {
				m_opbm.lookupboxZoomCommand((PanelRightLookupbox)source, p1, p2, p3);

			} else {
				// An error, we need an edit to display content
				if (p2.isEmpty())
					JOptionPane.showMessageDialog(null, "No p1 or edits parameter was found for lookupbox_zoom \"" + ((PanelRightLookupbox)source).getName() + "\"");
				else
					JOptionPane.showMessageDialog(null, "No p1 or edits was found for lookupbox_zoom(" + p2 +") \"" + ((PanelRightLookupbox)source).getName() + "\"");
			}

		} else if (command.equals("save_custom")) {
			// User clicked on the "zoom" listbox button on the flow control input
			// p1 = uuid of tupel containing everything to update
			m_opbm.saveCustomCommand(p1);

		} else if (command.equals("cancel_custom")) {
			// User clicked on the "zoom" listbox button on the flow control input
			// p1 = uuid of tupel containing everything to update
			m_opbm.cancelCustomCommand(p1);


//////////
// LOOKUPBOX UPDATE
		} else if (command.equals("lookupbox_update")) {
			// User clicked on a listbox that's related to a lookupbox that needs
			// to have its information updated after the change in entry
			m_opbm.lookupboxUpdateCommand(p1);


//////////
// WEB_BROWSER
		} else if (command.equals("web_browser")) {
			// Wants to link to the specified web browser address
			m_opbm.webBrowser(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

//////////
// BENCHMARKS
		} else if (command.equals("run_atom_benchmark")) {
			m_opbm.benchmarkRunAtom(null, 1, true, (PanelRightItem)source, m_opbm, m_macroMaster, m_settingsMaster, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

//////////
// RESULTS VIEWER
		} else if (command.equals("run_results_viewer_sample")) {
			m_opbm.createAndShowResultsViewer("resultsample.xml");

		} else if (command.equals("run_results_viewer")) {
			m_opbm.createAndShowResultsViewer("output.xml");

		} else if (command.equals("prompt_run_results_viewer")) {
			fileName = Utils.promptForFilename(".xml", "Results XML Files", "Please Select A Results File", m_opbm);
			if (!fileName.isEmpty())
				m_opbm.createAndShowResultsViewer( fileName );


//////////
// CLOSING BRACE
		}


	}

	public Opbm			m_opbm;
	public Macros		m_macroMaster;
	public Settings		m_settingsMaster;
}
