/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for benchmarking.  It executes scripts,
 * shows the heads-up display, displays the single-step debugger, etc.
 *
 * Last Updated:  Aug 17, 2011
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

package opbm.benchmarks;

import opbm.Opbm;
import opbm.benchmarks.environment.Variables;
import opbm.benchmarks.environment.Stack;
import opbm.benchmarks.hud.HUD;
import opbm.benchmarks.debugger.Debugger;
import opbm.common.Macros;
import opbm.common.Utils;
import opbm.common.Settings;
import opbm.common.Xml;
import opbm.panels.PanelRightItem;
import java.util.ArrayList;
import opbm.benchmarks.waituntilidle.WaitUntilIdle;
import opbm.dialogs.OpbmDialog;
import opbm.dialogs.OpbmInput;

/**
 *
 * @author rick
 */
public class Benchmarks
{
	/**
	 * Constructor
	 * @param opbm Opbm master instance creating this class
	 */
	public Benchmarks(Opbm opbm)
	{
		m_opbm = opbm;
	}

	/**
	 * Called to initiate the process of starting a trial run benchmark
	 * @param automated
	 */
	public void benchmarkTrialRun(boolean automated)
	{
		m_opbm.setTrialRun();
		if (!automated)
		{	// User is manually keying the benchmark, so give them the dialog option
			OpbmInput oi = new OpbmInput(m_opbm, "Trial Run", "Assign a name to this Trial Run (optional):", "", OpbmInput._ACCEPT_CANCEL, "trial", "", "", "Go", "", "launch_trial_run");

		} else {
			// For automated processes, we go right to it
			benchmarkLaunchTrialRun(true);

		}
	}

	/**
	 * Called to physically execute the trial run benchmark
	 * @param automated
	 */
	public void benchmarkLaunchTrialRun(boolean automated)
	{
		String result;

		if (!automated)
		{	// We have to check the user response
			result = m_opbm.getDialogResponse("trial");
			if (!result.equalsIgnoreCase("go"))
			{	// User cancelled
				return;
			}
			// If we get here, they're continuing
			m_opbm.setRunName(m_opbm.getDialogResponseData("trial").trim());
			m_opbm.clearDialogResponse("trial");
		}
		// Execute the trial run benchmark
		OpbmDialog od = new OpbmDialog(m_opbm, "Trial Run named '" + m_opbm.getRunName() + "' would begin", "caption", OpbmDialog._OKAY_BUTTON, "", "");
	}

	/**
	 * Called to initiate the process of starting an official run benchmark
	 * @param automated
	 */
	public void benchmarkOfficialRun(boolean automated)
	{
		m_opbm.setOfficialRun();
		if (!automated)
		{	// User is manually keying the benchmark, so give them the dialog option
			OpbmInput oi = new OpbmInput(m_opbm, "Official Run", "Assign a name to this Official Run (optional):", "", OpbmInput._ACCEPT_CANCEL, "official", "", "", "Go", "", "launch_official_run");

		} else {
			// For automated processes, we go right to it
			benchmarkLaunchOfficialRun(true);

		}
	}

	/**
	 * Called to physically execute the official run benchmark
	 * @param automated
	 */
	public void benchmarkLaunchOfficialRun(boolean automated)
	{
		String result;

		if (!automated)
		{	// We have to check the user response
			result = m_opbm.getDialogResponse("official");
			if (!result.equalsIgnoreCase("go"))
			{	// User cancelled
				return;
			}
			// If we get here, they're continuing
			m_opbm.setRunName(m_opbm.getDialogResponseData("official").trim());
			m_opbm.clearDialogResponse("official");
		}
		// Execute the official run benchmark
		OpbmDialog od = new OpbmDialog(m_opbm, "Official Run named '" + m_opbm.getRunName() + "' would begin", "caption", OpbmDialog._OKAY_BUTTON, "", "");
	}

	/**
	 * Called to physically execute the specified atom (pointed to by "atom")
	 * @param atom
	 * @param iterations
	 */
	public void benchmarkRunAtom(Xml	atom,
								 int	iterations)
	{
		int i;
		Xml child, xmlSuccess, xmlFailure, xmlIteration, xmlRun_Success, xmlRun_Failure;

		// Indicate the atom we're going into for the stack
		m_bp.m_benchmarkStack.add(atom);

		// Append the atom's entry to the output/results xml file
		xmlRun_Success	= new Xml("atom");
		xmlRun_Success.appendAttribute(new Xml("name", atom.getAttribute("name")));
		m_bp.m_xmlRun.appendChild(xmlRun_Success);

		xmlRun_Failure	= new Xml("failures");
		xmlRun_Failure.appendAttribute(new Xml("name", atom.getAttribute("name")));
		m_bp.m_xmlRun.appendChild(xmlRun_Failure);

// REMEMBER In the future, will store data here containing run profile (information, notes, optimization settings, etc.)

		// Initialize everything
		xmlSuccess	= null;
		xmlFailure	= null;

		// We are processing as many iterations of this atom as were specified by the caller
		// Note:  These iterations are different than retry counts, but are passed parameters
		//        (typically from the command) line which indicate a repeating set of data
		m_bp.m_maxIterations = iterations;
		for (i = 0; i < iterations; i++)
		{
			// Process through the entire atom, based on its logic
			child = atom.getFirstChild();
			Stack.enterNewBlock(Stack._STACK_ROOT, child, m_bp.m_atomStack);

			if (iterations != 1)
			{
				xmlSuccess		= xmlRun_Success;
				xmlFailure		= xmlRun_Failure;
				xmlIteration	= new Xml("iteration");

				// Append success iteration information
				xmlIteration.appendAttribute("this", Integer.toString(i+1));
				xmlIteration.appendAttribute("max", Integer.toString(iterations));
				xmlRun_Success = xmlRun_Success.appendChild(xmlIteration);
				xmlRun_Failure = xmlRun_Failure.appendChild(xmlIteration);

				// Indicate where we are in the overall scheme
				m_bp.m_thisIteration			= i + 1;
				m_bp.m_bpAtom.m_executeCounter	= 0;
				m_bp.m_bpAtom.m_failureCounter	= 0;

			} else {
				m_bp.m_thisIteration = 0;

			}

			// Process all atom commands one-by-one until finished, or until the user presses the stop button
			while (child != null)
			{
				if (m_bp.m_debuggerActive)
				{
					// Update the debugger display
					m_bp.m_debugLastAction	= m_bp.m_debuggerOrHUDAction;
					m_bp.m_debuggerOrHUDAction	= BenchmarkParams._NO_ACTION;
					m_bp.m_debugParent		= atom;
					m_bp.m_debugChild		= child;
					// Put focus in the window
					m_bp.m_deb.forceWindowToHaveFocus();
					m_bp.m_deb.update();

					if (m_bp.m_singleStepping)
					{
						// Position the cursor
						m_bp.m_deb.getCareTextbox().requestFocusInWindow();

						// Wait for user input and respond to the command
						try {
							do {
								// The warning which appears here can be ignored because we're
								// waiting for the debugger's window to set a value here in
								// m_bp.m_debuggerOrHUDAction.  Once populated, the break
								// will be executed below.
								// It may be worth in the future creation some atomic variable
								// that can be set and released by the debugger.  For now,
								// this works.
								Thread.sleep(50);

								// Did they select something while they were running?
								if (m_bp.m_debuggerOrHUDAction != BenchmarkParams._NO_ACTION)
									break;

							} while (true);

						} catch (InterruptedException ex) {
						}
						m_bp.m_deb.setVisible(false);

						// See what option they chose
						if (m_bp.m_debuggerOrHUDAction == BenchmarkParams._RUN) {
							// Lower the single-stepping flag
							m_bp.m_singleStepping = false;

						} else if (m_bp.m_debuggerOrHUDAction == BenchmarkParams._SINGLE_STEP) {
							// Do nothing, except continue on

						} else if (m_bp.m_debuggerOrHUDAction >= BenchmarkParams._STOP) {
							// We're finished
							break;
						}
					}
				}

				// Process the next command
				child = m_bp.m_bpAtom.processCommand(child, atom, xmlRun_Success, xmlRun_Failure);
				if (m_bp.m_debuggerOrHUDAction >= BenchmarkParams._STOP)
					break;
			}

			if (iterations != 1)
			{	// For each iteration we created (above) a child of the actual
				// xmlRun_Success and xmlRun_Failure entries.
				// So now we have to restore them back to their parent values,
				// for the next iteration, or for the completion of the run.
				if (xmlSuccess != null)
					xmlRun_Success = xmlSuccess;

				if (xmlFailure != null)
					xmlRun_Failure = xmlFailure;
			}

			// If the user is telling us to stop, we stop
			if (m_bp.m_debuggerOrHUDAction >= BenchmarkParams._STOP)
				break;
		}

		// When we get here, we are finished with the run, generate the outputs to the success branch
		m_bp.m_bpAtom.generateSummaryCSVs(xmlRun_Success);
		appendResultsDataForResultsViewer(xmlRun_Success);
		// Note:  The failure branch is not post-processed here, but remains in the file for later (manual?) examination

		// Indicate the atom is done
		m_bp.m_benchmarkStack.remove(m_bp.m_benchmarkStack.size() - 1);		// Remove the last item
	}

	/**
	 * Called to initialize the harness state for the execution of a benchmark.
	 * @param macroMaster the Macros class to use for this instance
	 * @param settingsMaster the Settings class to use for this instance
	 */
	public void benchmarkInitialize(Macros			macroMaster,
								    Settings		settingsMaster)

	{
		if (m_bp == null)
			m_bp = new BenchmarkParams();

		m_bp.m_parent = this;
		if (m_bp.m_bpAtom == null)
			m_bp.m_bpAtom = new BenchmarksAtom(m_bp);

		// Store our passed parameters
		m_bp.m_opbm				= m_opbm;
		m_bp.m_macroMaster		= macroMaster;
		m_bp.m_settingsMaster	= settingsMaster;

		// Begin the process
		benchmarkInitializeExecutionEnvironment(m_bp);

		// See if they are using the HUD
		if (m_bp.m_headsUpActive)
		{
			// Update the heads-up display
			if (!m_bp.m_hud.isVisible())
			{
				m_bp.m_opbm.hideDeveloperWindow();
				m_bp.m_opbm.hideSimpleWindow();
				m_bp.m_hud.setVisible(true);
			}
		}

		// See if they are using the debugger
		if (m_bp.m_debuggerActive)
		{
			// Update the debugger display
			if (!m_bp.m_deb.isVisible())
				m_bp.m_deb.setVisible(true);
		}
	}

	/**
	 * When the benchmark is complete, this method is called
	 */
	public void benchmarkShutdown()
	{
		String fileName;


		// All finished
		if (m_bp.m_headsUpActive)
		{
			m_bp.m_hud.dispose();
			m_bp.m_opbm.showUserWindow();
			m_bp.m_hud = null;
		}

		if (m_bp.m_debuggerActive)
		{
			m_bp.m_deb.dispose();
			m_bp.m_deb = null;
		}

		// Create a unique filename for this operation, based on the date and time
		fileName = Opbm.getHarnessXMLDirectory() + "results_" + Utils.convertFilenameToLettersAndNumbersOnly(Utils.getDateTimeAs_Mmm_DD_YYYY_at_HH_MM_SS()) + ".xml";

		// Save the output
		m_bp.m_xmlRoot.saveNode(fileName);

		// Close the benchmark run
		m_bp.m_opbm.setRunFinished();

		// Launch the results viewer
		m_bp.m_opbm.createAndShowResultsViewer(fileName);
	}

	/**
	 * Initialize all of the BenchmarkParams variables for this instance.  If
	 * the passed parameter is the same as this one, then it is assumed a
	 * benchmark will be running soon, so we also take a snapshot of the
	 * currently running processes and open windows, so that everything can be
	 * restored to a known state between runs.
	 *
	 * @param bp BenchmarkParams item to update
	 */
	public void benchmarkInitializeExecutionEnvironment(BenchmarkParams bp)
	{
		if (m_bp == bp)
		{	// We are initializing the current entry, so we can take a snapshot
			// of the system as it exists right now before the benchmark is run
			Opbm.snapshotProcesses();
			// Saves all processes currently running, and all windows currently
			// open.  Opbm.stopProcesses() is used during the benchmark to
			// restore everything to its original state (cleaning up in that way
			// after the benchmark terminates)
		}

		// Initialize our relative items
		bp.m_bpAtom.m_executeCounter	= 0;
		bp.m_bpAtom.m_failureCounter	= 0;
		bp.m_atomVariables				= new ArrayList<Variables>(0);
		bp.m_atomStack					= new ArrayList<Stack>(0);
		bp.m_bpAtom.m_timingEvents		= new ArrayList<Xml>(0);
		bp.m_bpAtom.m_returnValue		= 0;
		bp.m_wui						= new WaitUntilIdle();

		bp.m_benchmarkStack				= new ArrayList<Xml>(0);

		bp.m_debuggerActive				= bp.m_settingsMaster.isInDebugMode();
		bp.m_singleStepping				= bp.m_settingsMaster.isSingleStepping();
		if (bp.m_debuggerActive)
		{	// The debugger is displayed, but that doesn't mean the user is single-stepping
			bp.m_deb					= new Debugger(bp);
		} else {
			// Not displayted (typical condition)
			bp.m_deb					= null;
		}
		bp.m_debuggerOrHUDAction		= BenchmarkParams._NO_ACTION;

		bp.m_headsUpActive				= bp.m_settingsMaster.isHUDVisible();
		if (bp.m_headsUpActive)
		{	// The HUD is displayed
			bp.m_hud					= new HUD(bp.m_opbm, bp, false);
		} else {
			// Not displayed (typical condition)
			bp.m_hud					= null;
		}

		bp.m_retry						= bp.m_settingsMaster.isBenchmarkToRetryOnErrors();
		bp.m_retryAttempts				= bp.m_settingsMaster.benchmarkRetryOnErrorCount();

		// Initialize our captures and gobblers
		bp.m_errorArray					= new ArrayList<String>(0);
		bp.m_errorGobbler				= null;

		bp.m_outputArray				= new ArrayList<String>(0);
		bp.m_outputGobbler				= null;

		// Begin the logging
		// Run data goes to opbm.rawdata.run
		Xml root						= new Xml("opbm");				// Creae opbm
		Xml rawdata						= new Xml("rawdata");			// Create opbm.rawdata
		root.appendChild(rawdata);
		Xml run							= new Xml("run");				// Create opbm.rawdata.run
		rawdata.appendChild(run);

		// Post-processed results goes to opbm.resultdata.result
		Xml resultsdata					= new Xml("resultsdata");		// Create opbm.resultdata
		Xml result						= new Xml("result");			// Create opbm.resultsdata.result
		addStandardResultAttributes(result);
		resultsdata.appendChild(result);
		root.appendChild(resultsdata);

		// Information about the run goes to opbm.rundata
		Xml runinfo						= new Xml("runinfo");
		Xml runtype						= new Xml("type", bp.m_opbm.getRunType());
		Xml start						= new Xml("start", Utils.getTimestamp());
		Xml name						= new Xml("name", Utils.convertToLettersAndNumbersOnly(bp.m_opbm.getRunName()));
		runinfo.appendChild(runtype);
		runinfo.appendChild(start);
		runinfo.appendChild(name);
		root.appendChild(runinfo);

// REMEMBER CPUID information will be recorded here

		// Populate the global variables
		bp.m_xmlRoot					= root;
		bp.m_xmlRun						= run;
		bp.m_xmlResults					= result;
		bp.m_xmlResultsLastSuite		= null;
		bp.m_xmlResultsLastScenario		= null;
		bp.m_xmlResultsLastMolecule		= null;
		bp.m_xmlResultsLastAtom			= null;
	}

	public void addStandardResultAttributes(Xml result)
	{
		result.appendAttribute("datetime",	Utils.getDateTimeAs_Mmm_DD__YYYY_at_HH_MMampm());	// Jul 04, 2011 at 11:56am
		result.appendAttribute("name",		"OPBM Benchmark");
		result.appendAttribute("shortname",	"unnamed");
		result.appendAttribute("tags",		"");
		result.appendAttribute("tested",	"yes");
		result.appendAttribute("status",	"success");
		result.appendAttribute("score",		"0");
	}

	/**
	 * The resultsdata.result tag should be conducted logically throughout,
	 * such that a suite calls a scenario which calls a molecule which calls
	 * an atom, which includes worklets.  If, however, there are stray atoms
	 * that execute outside of that natural suite.scenario.molecule.atom framework,
	 * then we still have to process them properly.  So, in the case where there
	 * are no parents for the atom, we create "empties" all the way down to the
	 * atom level, which serve as "placeholder suites", "placeholder scenarios"
	 * and "placeholder molecules".
	 * @return
	 */
	public Xml getNextResultAtomForNextAppend()
	{
		Xml xmlSuite, xmlScenario, xmlMolecule, xmlAtom;

		if (m_bp.m_xmlResultsLastAtom != null)
		{	// Append a new atom sibling
			xmlAtom = new Xml("atom");
			addStandardEmptyTags(xmlAtom, "Atom");
			m_bp.m_xmlResultsLastAtom.setNext(xmlAtom);
			m_bp.m_xmlResultsLastAtom = xmlAtom;

		} else {
			// No last atom, see what level we were at:
			if (m_bp.m_xmlResultsLastMolecule != null)
			{	// We can add to this molecule the first atom
				xmlAtom = new Xml("atom");
				addStandardEmptyTags(xmlAtom, "Atom");
				m_bp.m_xmlResultsLastMolecule.appendChild(xmlAtom);
				m_bp.m_xmlResultsLastAtom = xmlAtom;

			} else if (m_bp.m_xmlResultsLastScenario != null) {
				// We can add to this scenario an empty molecule for the new atom
				xmlMolecule = new Xml("molecule");
				addStandardEmptyTags(xmlMolecule, "Molecule");
				m_bp.m_xmlResultsLastMolecule = xmlMolecule;

				xmlAtom = new Xml("atom");
				addStandardEmptyTags(xmlAtom, "Atom");
				m_bp.m_xmlResultsLastAtom = xmlAtom;

				m_bp.m_xmlResultsLastScenario.appendChild(xmlMolecule);
				m_bp.m_xmlResultsLastMolecule.appendChild(xmlAtom);

			} else if (m_bp.m_xmlResultsLastSuite != null) {
				// We can add to this suite an empty scenario, and an empty molecule for the new atom
				xmlScenario = new Xml("scenario");
				addStandardEmptyTags(xmlScenario, "Scenario");
				m_bp.m_xmlResultsLastScenario = xmlScenario;

				xmlMolecule = new Xml("molecule");
				addStandardEmptyTags(xmlMolecule, "Molecule");
				m_bp.m_xmlResultsLastMolecule = xmlMolecule;

				xmlAtom = new Xml("atom");
				addStandardEmptyTags(xmlAtom, "Atom");
				m_bp.m_xmlResultsLastAtom = xmlAtom;

				m_bp.m_xmlResultsLastSuite.appendChild(xmlScenario);
				m_bp.m_xmlResultsLastScenario.appendChild(xmlMolecule);
				m_bp.m_xmlResultsLastMolecule.appendChild(xmlAtom);

			} else {
				// We have to add everything, an empty suite, an empty scenario and an empty molecule for the new atom
				xmlSuite = new Xml("suite");
				addStandardEmptyTags(xmlSuite, "Suite");
				m_bp.m_xmlResultsLastSuite = xmlSuite;

				xmlScenario = new Xml("scenario");
				addStandardEmptyTags(xmlScenario, "Scenario");
				m_bp.m_xmlResultsLastScenario = xmlScenario;

				xmlMolecule = new Xml("molecule");
				addStandardEmptyTags(xmlMolecule, "Atom");
				m_bp.m_xmlResultsLastMolecule = xmlMolecule;

				xmlAtom = new Xml("atom");
				addStandardEmptyTags(xmlAtom, "Atom");
				m_bp.m_xmlResultsLastAtom = xmlAtom;

				m_bp.m_xmlResults.appendChild(xmlSuite);
				m_bp.m_xmlResultsLastSuite.appendChild(xmlScenario);
				m_bp.m_xmlResultsLastScenario.appendChild(xmlMolecule);
				m_bp.m_xmlResultsLastMolecule.appendChild(xmlAtom);

			}
		}
		return(xmlAtom);
	}

	public void addStandardEmptyTags(Xml		xml,
									 String		additionalInfo)
	{
		xml.appendAttribute("name",			"Placeholder" + ((additionalInfo.isEmpty()) ? "" : " " + additionalInfo));
		xml.appendAttribute("shortname",	"ph" + Utils.getShortName(additionalInfo, 7));
		xml.appendAttribute("tags",			"");
		xml.appendAttribute("tested",		"yes");
		xml.appendAttribute("status",		"success");
		xml.appendAttribute("score",		"0");
	}

	public Xml loadAtomFromPanelRightItem(PanelRightItem	pri,
										  String			p1,
										  String			p2,
										  String			p3,
										  String			p4,
										  String			p5,
										  String			p6,
										  String			p7,
										  String			p8,
										  String			p9,
										  String			p10)
	{
		// Grab the selected Atom node from the listbox
		return(pri.getParentPR().getListboxOrLookupboxNodeByName(p1));
	}

	/**
	 * Processes the specified portion of a run into the resultsdata of the
	 * specified bp.m_xmlResults
	 * @param xmlRun start of the run for this atom
	 * context of m_xmlResults* members
	 */
	public void appendResultsDataForResultsViewer(Xml xmlRun)
	{
		String line, result, name, sourcename;
		Xml atom, child, command, finish, outputs, output, worklet;

		child = xmlRun.getFirstChild();
		while (child != null)
		{
			name		= child.getAttribute("name");
			sourcename	= child.getAttribute("sourcename");
			if (child.getName().equalsIgnoreCase("abstract") && sourcename.equalsIgnoreCase("execute"))
			{	// See if it's an executable
				command = child.getChildNode("command");
				if (command != null)
				{	// Grab the relevant outputs
					finish	= child.getChildNode("finish");
					outputs	= child.getChildNode("outputs");
					result	= finish.getAttribute("result");

					// Append the atom information
					atom = getNextResultAtomForNextAppend();
					atom.appendAttribute(new Xml("name", name));
					atom.appendAttribute(new Xml("status", result));

					// Add the worklets for this atom
					output = outputs.getFirstChild();
					while (output != null)
					{
						line = output.getText();
						if (line.toLowerCase().startsWith("timing,"))
						{	// We extract all timing lines as worklets
							m_bp.extractTimingLineElements(line.substring(7));
							worklet = new Xml("worklet");
							worklet.appendAttribute("name", m_bp.m_timingName);
							worklet.appendAttribute("shortname", Utils.getShortName(m_bp.m_timingName, 7));
							worklet.appendAttribute("timing", Double.toString(m_bp.m_timingInSeconds));
							worklet.appendAttribute("score", Double.toString(m_bp.m_timingOfBaseline));
							worklet.appendAttribute("tested", "yes");
							worklet.appendAttribute("status", result);
							atom.appendChild(worklet);
						}

						// Move to next output line
						output = output.getNext();
					}
				}
			}

			// Move to the next sibling
			child = child.getNext();
		}
	}

	private Opbm				m_opbm;
	private BenchmarkParams		m_bp;
}
