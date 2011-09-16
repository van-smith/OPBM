/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for all Command processing.  The
 * main OPBM class creates an m_commandMaster member variale, which is
 * propagated throughout the system and currently is used as the sole
 * entity for processing, but in the future additional Commands class
 * items could be created which dynamically load commands from XML files
 * for unique forms of procesing, etc.
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

package opbm.common;

import opbm.panels.right.PanelRightLookupbox;
import opbm.panels.right.PanelRightListbox;
import opbm.panels.right.PanelRightItem;
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

	// Called when the parameters cannot be strings, and assigns them to class variables
	public void processCommand(Object	source,
							   String	commandOriginal,
							   Object	p1,
							   Object	p2,
							   Object	p3,
							   Object	p4,
							   Object	p5,
							   Object	p6,
							   Object	p7,
							   Object	p8,
							   Object	p9,
							   Object	p10)
	{
		m_op1 = p1;
		m_op2 = p2;
		m_op3 = p3;
		m_op4 = p4;
		m_op5 = p5;
		m_op6 = p6;
		m_op7 = p7;
		m_op8 = p8;
		m_op9 = p9;
		m_op10 = p10;
		processCommand(source, commandOriginal, "", "", "", "", "", "", "", "", "", "");
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
		} else if (command.equals("save_custom")) {
			// Editing via zoom-windows, which send the command "save_custom" to save the user's changes when "Save" is clicked
			m_opbm.saveCustom(p1);

		} else if (command.equals("cancel_custom")) {
			// Editing via zoom-windows, which send the command "cancel_custom" to cancel the edit when "Cancel" is clicked
			m_opbm.cancelCustom(p1);

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
			// Saving the current contents of the active edit, and returning to the previous panel
			m_opbm.editSave();
			m_opbm.editClose();
			m_opbm.navigateBack();

		} else if (command.equals("edit_home")) {
			// Navigating back through chain
			m_opbm.editSave();
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
			// User clicked on the "add" lookupbox button on the flow control input
			// p1 = whereTo
			// p2 = after
			// p3 = whereFrom
			m_opbm.lookupboxAddCommand((PanelRightLookupbox)source, p1, p2, p3);

		} else if (command.equals("lookupbox_subtract")) {
			// User clicked on the "subtract" lookupbox button
			m_opbm.lookupboxCommand("subtract", (PanelRightLookupbox)source);

		} else if (command.equals("lookupbox_clone")) {
			// User clicked on the "clone" lookupbox button on the flow control input
			m_opbm.lookupboxCloneCommand((PanelRightLookupbox)source);

		} else if (command.equals("lookupbox_move_up")) {
			// User clicked on the "Up" lookupbox button
			m_opbm.lookupboxCommand("up", (PanelRightLookupbox)source);

		} else if (command.equals("lookupbox_move_down")) {
			// User clicked on the "Down" lookupbox button
			m_opbm.lookupboxCommand("down", (PanelRightLookupbox)source);

		} else if (command.equals("lookupbox_zoom")) {
			// User clicked on the "zoom" lookupbox button
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
		} else if (command.equals("run_atom_sequence")) {
			m_opbm.benchmarkRunAtom(null, 1, true, (PanelRightItem)source, m_opbm, m_macroMaster, m_settingsMaster, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		} else if (command.equals("run_molecule_sequence")) {
			m_opbm.benchmarkRunMolecule(null, 1, true, (PanelRightItem)source, m_opbm, m_macroMaster, m_settingsMaster, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		} else if (command.equals("run_scenario_sequence")) {
			m_opbm.benchmarkRunScenario(null, 1, true, (PanelRightItem)source, m_opbm, m_macroMaster, m_settingsMaster, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		} else if (command.equals("run_suite_sequence")) {
			m_opbm.benchmarkRunSuite(null, 1, true, (PanelRightItem)source, m_opbm, m_macroMaster, m_settingsMaster, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		} else if (command.equals("launch_trial_run")) {
			m_opbm.benchmarkLaunchTrialRun(/*not automated*/false);
		} else if (command.equals("launch_official_run")) {
			m_opbm.benchmarkLaunchOfficialRun(/*not automated*/false);
		} else if (command.equals("launch_trial_run_automated")) {
			m_opbm.benchmarkLaunchTrialRun(/*automated*/true);
		} else if (command.equals("launch_official_run_automated")) {
			m_opbm.benchmarkLaunchOfficialRun(/*automated*/true);
		} else if (command.equals("translate_manifest_to_results")) {
			m_opbm.translateManifestToResults(m_macroMaster.parseMacros(p1));

//////////
// RESULTS VIEWER
		} else if (command.equals("run_results_viewer_sample")) {
			m_opbm.createAndShowResultsViewer("resultsample.xml");

		} else if (command.equals("run_results_viewer")) {
			m_opbm.createAndShowResultsViewer(Opbm.getHarnessXMLDirectory() + "results.xml");

		} else if (command.equals("prompt_run_results_viewer")) {
			fileName = Utils.promptForFilename(".xml", "Results XML Files", "Please Select A Results File", m_opbm, Opbm.getHarnessXMLDirectory());
			if (!fileName.isEmpty())
				m_opbm.createAndShowResultsViewer(fileName);

		} else if (command.equals("set_results_viewer_cv")) {
			m_opbm.setResultsViewerCV();

		} else if (command.equals("save_results_viewer_cv")) {
			String value = m_opbm.getDialogResponseData("results_viewer_cv");
			if (!value.isEmpty())
			{	// Save the value and refresh
				m_opbm.getSettingsMaster().setCVInRed(Utils.doubleValueOf(value, 3.0) / 100.0);
				m_opbm.refreshLeftPanelsAfterMacroUpdate();
			}


//////////
// SIMPLE WINDOW and DEVELOPER WINDOW
		} else if (command.equals("simple")) {
			m_opbm.showSimpleWindow();
		} else if (command.equals("developer")) {
			m_opbm.showDeveloperWindow();

//////////
// TRIAL RUN and OFFICIAL RUN
		} else if (command.equals("trial_run")) {
			m_opbm.trialRun(false);
		} else if (command.equals("official_run")) {
			m_opbm.officialRun(false);

//////////
// TOGGLE MENU ITEMS
		} else if (command.equals("toggle_debug_info")) {
			m_settingsMaster.toggleHUDDebugInfo();
			m_opbm.refreshLeftPanelsAfterMacroUpdate();
		} else if (command.equals("toggle_hud")) {
			m_settingsMaster.toggleHUDTranslucency();
			m_opbm.refreshLeftPanelsAfterMacroUpdate();
		} else if (command.equals("toggle_retry_attempts")) {
			m_settingsMaster.toggleRetryAttempts();
			m_opbm.refreshLeftPanelsAfterMacroUpdate();
		} else if (command.equals("toggle_halt_on_error")) {
			m_settingsMaster.toggleBenchmarkStopsIfRetriesFail();
			m_opbm.refreshLeftPanelsAfterMacroUpdate();
		} else if (command.equals("toggle_uninstall_after_failure")) {
			m_settingsMaster.toggleUninstallAfterFailure();
			m_opbm.refreshLeftPanelsAfterMacroUpdate();
		} else if (command.equals("toggle_run_spinups")) {
			m_settingsMaster.toggleRunSpinups();
			m_opbm.refreshLeftPanelsAfterMacroUpdate();



//////////
// MISCELLANEOUS COMMANDS
		} else if (command.equals("compute_results_averages")) {
			m_opbm.computeResultsXmlAverages();
		} else if (command.equals("gather_debug_info")) {
			m_opbm.gatherDebugInfo();

//////////
// CLOSING BRACE
		}


	}

	public Opbm			m_opbm;
	public Macros		m_macroMaster;
	public Settings		m_settingsMaster;

	// Used to hold the parameters passed as objects
	public Object		m_op1;
	public Object		m_op2;
	public Object		m_op3;
	public Object		m_op4;
	public Object		m_op5;
	public Object		m_op6;
	public Object		m_op7;
	public Object		m_op8;
	public Object		m_op9;
	public Object		m_op10;
}
