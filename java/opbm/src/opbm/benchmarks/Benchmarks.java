/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for benchmarking.  It executes scripts,
 * shows the heads-up display, displays the single-step debugger, etc.
 *
 * Last Updated:  Jun 24, 2011
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

package opbm.benchmarks;

import opbm.benchmarks.environment.Variables;
import opbm.benchmarks.environment.Stack;
import opbm.benchmarks.hud.HUD;
import opbm.benchmarks.debugger.Debugger;
import java.util.ArrayList;
import opbm.common.Macros;
import opbm.Opbm;
import opbm.common.Utils;
import opbm.panels.PanelRightItem;
import opbm.common.Settings;
import opbm.common.Xml;

public class Benchmarks
{

	public void benchmarkInitialize(Opbm			opbm,
								    Macros			macroMaster,
								    Settings		settingsMaster)

	{
		if (m_bp == null)
			m_bp = new BenchmarksParams();

		if (m_bpAtom == null)
			m_bpAtom = new BenchmarksAtom(this, m_bp);

		// Store our passed parameters
		m_bp.m_opbm				= opbm;
		m_bp.m_macroMaster		= macroMaster;
		m_bp.m_settingsMaster	= settingsMaster;

		// Begin the process
		benchmarkInitializeExecutionEnvironment(m_bp, m_bpAtom);

		// See if they are using the HUD
		if (m_bp.m_headsUpActive)
		{
			// Update the heads-up display
			if (!m_bp.m_hud.isVisible())
			{
				m_bp.m_opbm.hideMainPanel();
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

	public void benchmarkShutdown()
	{
		String fileName;


		// All finished
		if (m_bp.m_headsUpActive)
		{
			m_bp.m_hud.dispose();
			m_bp.m_opbm.showMainPanel();
			m_bp.m_hud = null;
		}

		if (m_bp.m_debuggerActive)
		{
			m_bp.m_deb.dispose();
			m_bp.m_deb = null;
		}

		// Create a unique filename for this operation, based on the date and time
		fileName = "results_" + Utils.convertToLettersAndNumbersOnly(Utils.getDateTimeAs_Mmm_DD_YYYY_at_HH_MM_SS()) + ".xml";

		// Save the output
		m_bp.m_xmlRoot.saveNode(fileName);

		// Launch the results viewer
		m_bp.m_opbm.createAndShowResultsViewer(fileName);
	}

	public void benchmarkInitializeExecutionEnvironment(BenchmarksParams	bp,
														BenchmarksAtom		bpAtom)
	{
		// Initialize our relative items
		bpAtom.m_executeCounter			= 0;
		bp.m_atomVariables				= new ArrayList<Variables>(0);
		bp.m_atomStack					= new ArrayList<Stack>(0);
		bpAtom.m_timingEvents			= new ArrayList<Xml>(0);
		bpAtom.m_returnValue			= 0;

		bp.m_benchmarkStack				= new ArrayList<Xml>(0);

		bp.m_debuggerActive				= bp.m_settingsMaster.isInDebugMode();
		bp.m_singleStepping				= bp.m_settingsMaster.isSingleStepping();
		if (bp.m_debuggerActive)
		{	// The debugger is displayed, but that doesn't mean that the user is single-stepping
			bp.m_deb					= new Debugger(bp);
		} else {
			// Not displayted (typical condition)
			bp.m_deb					= null;
		}

		bp.m_headsUpActive				= bp.m_settingsMaster.isHUDVisible();
		if (bp.m_headsUpActive)
		{	// The HUD is displayed
			bp.m_hud					= new HUD(bp.m_opbm, false);
		} else {
			// Not displayed (typical condition)
			bp.m_hud					= null;
		}

		// Initialize our captures and gobblers
		bp.m_errorArray					= new ArrayList<String>(0);
		bp.m_errorGobbler				= null;

		bp.m_outputArray				= new ArrayList<String>(0);
		bp.m_outputGobbler				= null;

		// Begin the logging
		// Run data goes to opbm.rawdata.run
		Xml root						= new Xml("opbm");
		Xml rawdata						= new Xml("rawdata");
		Xml run							= new Xml("run");
		root.appendChild(rawdata);							// Create opbm.rawdata
		rawdata.appendChild(run);							// Create opbm.rawdata.run

		// Post-processed results goes to opbm.resultdata.result
		Xml resultsdata					= new Xml("resultsdata");		// Create opbm.resultdata
		Xml result						= new Xml("result");			// Create opbm.resultsdata.result
		addStandardResultAttributes(result);

		root.appendChild(resultsdata);
		resultsdata.appendChild(result);

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
		result.appendAttribute("tested",	"yes");
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
	public Xml getNextResultAtomForNextAppend(BenchmarksParams bp)
	{
		Xml xmlSuite, xmlScenario, xmlMolecule, xmlAtom;

		if (bp.m_xmlResultsLastAtom != null)
		{	// Append a new atom sibling
			xmlAtom = new Xml("atom");
			addStandardEmptyTags(xmlAtom, "Atom");
			bp.m_xmlResultsLastAtom.setNext(xmlAtom);
			bp.m_xmlResultsLastAtom = xmlAtom;

		} else {
			// No last atom, see what level we were at:
			if (bp.m_xmlResultsLastMolecule != null)
			{	// We can add to this molecule the first atom
				xmlAtom = new Xml("atom");
				addStandardEmptyTags(xmlAtom, "Atom");
				bp.m_xmlResultsLastMolecule.appendChild(xmlAtom);
				bp.m_xmlResultsLastAtom = xmlAtom;

			} else if (bp.m_xmlResultsLastScenario != null) {
				// We can add to this scenario an empty molecule for the new atom
				xmlMolecule = new Xml("molecule");
				addStandardEmptyTags(xmlMolecule, "Molecule");
				bp.m_xmlResultsLastMolecule = xmlMolecule;

				xmlAtom = new Xml("atom");
				addStandardEmptyTags(xmlAtom, "Atom");
				bp.m_xmlResultsLastAtom = xmlAtom;

				bp.m_xmlResultsLastScenario.appendChild(xmlMolecule);
				bp.m_xmlResultsLastMolecule.appendChild(xmlAtom);

			} else if (bp.m_xmlResultsLastSuite != null) {
				// We can add to this suite an empty scenario, and an empty molecule for the new atom
				xmlScenario = new Xml("scenario");
				addStandardEmptyTags(xmlScenario, "Scenario");
				bp.m_xmlResultsLastScenario = xmlScenario;

				xmlMolecule = new Xml("molecule");
				addStandardEmptyTags(xmlMolecule, "Molecule");
				bp.m_xmlResultsLastMolecule = xmlMolecule;

				xmlAtom = new Xml("atom");
				addStandardEmptyTags(xmlAtom, "Atom");
				bp.m_xmlResultsLastAtom = xmlAtom;

				bp.m_xmlResultsLastSuite.appendChild(xmlScenario);
				bp.m_xmlResultsLastScenario.appendChild(xmlMolecule);
				bp.m_xmlResultsLastMolecule.appendChild(xmlAtom);

			} else {
				// We have to add everything, an empty suite, an empty scenario and an empty molecule for the new atom
				xmlSuite = new Xml("suite", "Suite");
				addStandardEmptyTags(xmlSuite, "Suite");
				bp.m_xmlResultsLastSuite = xmlSuite;

				xmlScenario = new Xml("scenario", "Scenario");
				addStandardEmptyTags(xmlScenario, "Scenario");
				bp.m_xmlResultsLastScenario = xmlScenario;

				xmlMolecule = new Xml("molecule", "Molecule");
				addStandardEmptyTags(xmlMolecule, "Atom");
				bp.m_xmlResultsLastMolecule = xmlMolecule;

				xmlAtom = new Xml("atom");
				addStandardEmptyTags(xmlAtom, "Atom");
				bp.m_xmlResultsLastAtom = xmlAtom;

				bp.m_xmlResults.appendChild(xmlSuite);
				bp.m_xmlResultsLastSuite.appendChild(xmlScenario);
				bp.m_xmlResultsLastScenario.appendChild(xmlMolecule);
				bp.m_xmlResultsLastMolecule.appendChild(xmlAtom);

			}
		}
		return(xmlAtom);
	}

	public void addStandardEmptyTags(Xml		xml,
									 String		additionalInfo)
	{
		xml.appendAttribute("name",			"Empty" + ((additionalInfo.isEmpty()) ? "" : " " + additionalInfo));
		xml.appendAttribute("shortname",	"empty");
		xml.appendAttribute("tags",			"");
		xml.appendAttribute("tested",		"yes");
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

	public void benchmarkRunAtom(Xml	atom,
								 int	iterations)
	{
		int i;
		Xml child, xml, xmlIteration, xmlRunAppendTo, xmlRunStart;

		// Indicate the atom we're going into for the stack
		m_bp.m_benchmarkStack.add(atom);

		// Append the
		xmlRunAppendTo	= new Xml("atom");
		xmlRunStart		= xmlRunAppendTo;
		xmlRunAppendTo.appendAttribute(new Xml("name", atom.getAttribute("name")));
		m_bp.m_xmlRun.appendChild(xmlRunAppendTo);

		// REMEMBER In the future, will store data here containing run profile (information, notes, optimization settings, etc.)
		xml = null;
		m_bp.m_maxIterations = iterations;
		for (i = 0; i < iterations; i++)
		{
			// Process through the entire atom, based on its logic
			child = atom.getFirstChild();
			Stack.enterNewBlock(Stack._STACK_ROOT, child, m_bp.m_atomStack);

			if (iterations != 1)
			{
				xml = xmlRunAppendTo;
				xmlIteration = new Xml("iteration");
				xmlIteration.appendAttribute("this", Integer.toString(i+1));
				xmlIteration.appendAttribute("max", Integer.toString(iterations));
				xmlRunAppendTo = xmlRunAppendTo.appendChild(xmlIteration);
				m_bp.m_thisIteration		= i+1;
				m_bpAtom.m_executeCounter	= 0;

			} else {
				m_bp.m_thisIteration = 0;

			}

			// Process all atom commands one-by-one
			while (child != null)
			{
				if (m_bp.m_debuggerActive)
				{
					// Update the debugger display
					m_bp.m_debugLastAction	= m_bp.m_debuggerAction;
					m_bp.m_debuggerAction	= BenchmarksParams._NO_ACTION;
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
								Thread.sleep(10);

								// Did they select something while they were running?
								if (m_bp.m_debuggerAction != BenchmarksParams._NO_ACTION)
									break;

							} while (true);

						} catch (InterruptedException ex) {
						}
						m_bp.m_deb.setVisible(false);

						// See what option they chose
						if (m_bp.m_debuggerAction == BenchmarksParams._RUN) {
							// Lower the single-stepping flag
							m_bp.m_singleStepping = false;

						} else if (m_bp.m_debuggerAction == BenchmarksParams._SINGLE_STEP) {
							// Do nothing, except continue on

						} else if (m_bp.m_debuggerAction == BenchmarksParams._STOP) {
							// We're finished
							break;
						}
					}
				}

				// Process the next command
				child = m_bpAtom.processCommand(child, atom, xmlRunAppendTo);
			}

			if (iterations != 1 && xml != null)
				xmlRunAppendTo = xml;

			if (m_bp.m_debuggerAction == BenchmarksParams._STOP)
				break;
		}
		// When we get here, we are finished with the run, now generate the outputs
		m_bpAtom.generateSummaryCSVs(xmlRunAppendTo);
		m_bpAtom.appendResultsDataForResultsViewer(xmlRunAppendTo, m_bp);

		// Indicate the atom is done
		m_bp.m_benchmarkStack.remove(m_bp.m_benchmarkStack.size() - 1);		// Remove the last item
	}

	private BenchmarksParams	m_bp;
	private BenchmarksAtom		m_bpAtom;
}
