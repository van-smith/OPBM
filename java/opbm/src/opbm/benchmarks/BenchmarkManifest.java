/*
 * OPBM - Office Productivity Benchmark
 *
 * This class allows the creation of a versatile manifest, describing completely
 * the benchmark operations to direct for a single atom up to a full official run,
 * or a customizable compiled list of operations to run in any order, and with
 * multiple passes.
 *
 * The methods within create a framework to simply run the following:
 *
 * ----------
 *		1) Trial run.  Execute this code:
 *			BenchmarkManifest bm = new BenchmarkManifest(m_opbm, "trial");
 *			if (bm.build())
 *				bm.run();
 *			else
 *				// Report error, use bm.getError() to get the error text
 *
 *
 * ----------
 *		2) Official run.  Execute this code:
 *			BenchmarkManifest bm = new BenchmarkManifest(m_opbm, "official");
 *			if (bm.build())
 *				bm.run();
 *			else
 *				// Report error, use bm.getError() to get the error text
 *
 * ----------
 *		3) Compiled run.  Execute this code:
 *			BenchmarkManifest bm = new BenchmarkManifest(m_opbm, "compilation");
 *			// Perform some loop to add the items, perhaps reading from command line
 *			for (i = 0; i < max; i++)
 *			{	// Add in the custom options
 *				if (haveASuiteToAdd)
 *					bm.addToCompiledList("suite",		suiteName,		iterations);	// Add a suite
 *				if (haveAScenarioToAdd)
 *					bm.addToCompiledList("scenario",	scenarioName,	iterations);	// Add a scenario
 *				if (haveAMoleculeToAdd)
 *					bm.addToCompiledList("molecule",	moleculeName,	iterations);	// Add a molecule
 *				if (haveAnAtomToAdd)
 *					bm.addToCompiledList("atom",		atomName,		iterations);	// Add a atom
 *				if (haveATrialRunToAdd)
 *					bm.addToCompiledList("trial",		"some name",	iterations);	// Add a full trial run with the specified name
 *				if (haveAnOfficialRunToAdd)
 *					bm.addToCompiledList("official",	"some name",	iterations);	// Add a full official run (three passes) with the specified name
 *			}
 *			// Note:  Multiple passes can be created by using bm.setPass(n) first, and then adding the item (it will be added to the specified pass)
 *			// Note:  The exact order items are added to the compilation is the order things are executed
 *			if (bm.build())
 *				bm.run();
 *			else
 *				// Report error, use bm.getError() to get the error text
 * ----------
 *
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
package opbm.benchmarks;

import java.util.ArrayList;
import java.util.List;
import opbm.Opbm;
import opbm.common.Macros;
import opbm.common.Tuple;
import opbm.common.Utils;
import opbm.common.Xml;
import opbm.dialogs.OpbmColumns;
import opbm.dialogs.OpbmDialog;

public final class BenchmarkManifest
{
	/**
	 * Constructor assigns the run-specific information, identifies the type
	 * of run, either "trial", "official" or "compilation", gives it a name, and
	 * specifies (if "compilation") the specific trial, official, suite, scenario,
	 * molecule or atom to run, otherwise runs the trial or official benchmark.
	 *
	 * Note:  When this class is created, a valid BenchmarkParams class needs
	 *        to have been instantiated for the Benchmarks class
	 *
	 * @param opbm main class reference
	 * @param type "trial", "official" or "compilation"
	 * @param manifestPathName path to manifest.xml for reload (if not null)
	 * @param automated is the run automated?
	 * @param rebootRequired is a reboot required?
	 */
	public BenchmarkManifest(Opbm		opbm,
							 String		type,
							 String		manifestPathName,
							 boolean	automated,
							 boolean	rebootRequired)
	{
		initialize(opbm, type, manifestPathName, automated, rebootRequired);
	}

	/**
	 * Constructor, creates a generic BenchmarkManifest instance, most likely
	 * used for processing an existing manifest.xml into results.xml
	 * @param opbm
	 */
	public BenchmarkManifest(Opbm opbm)
	{
		initialize(opbm, "", "", false, false);
	}

	/**
	 * Handles the grunt work of initialization, sets up everything based on
	 * user-supplied parameters.  Note:  When initialize() is called, not only
	 * are class items updated, but also external files, such as scripts.xml
	 * losing all of its atomuuid assignments, etc.  While this class does not
	 * need to be a singleton, it should be viewed as one when it comes to
	 * benchmark processing.  For generic manipulation of manifest.xml into
	 * other forms, as many instances as are needed can be created and used.
	 * @param opbm main class reference
	 * @param type "trial", "official" or "compilation"
	 * @param manifestPathName path to manifest.xml for reload (if not null)
	 * @param automated is the run automated?
	 * @param rebootRequired is a reboot required?
	 */
	public void initialize(Opbm			opbm,
						   String		type,
						   String		manifestPathName,
						   boolean		automated,
						   boolean		rebootRequired)
	{
		m_isManifestInError		= false;
		m_opbm					= opbm;
		m_benchmarksMaster		= opbm.getBenchmarkMaster();
		m_macroMaster			= opbm.getMacroMaster();
		m_bp					= null;
		m_bpa					= null;
		m_bmr					= new BenchmarkManifestResults(opbm, this);
		m_type					= type;
		m_name					= m_opbm.getRunName();
		m_conflicts				= new ArrayList<String>(0);
		m_resolutions			= new ArrayList<String>(0);

		m_suiteName				= "";
		m_scenarioName			= "";
		m_moleculeName			= "";
		m_atomName				= "";

		m_passThis				= 1;
		m_passMax				= 1;

		m_compilation			= new Tuple(opbm);

		m_manifestIsLoaded		= false;
		m_automated				= automated;
		m_rebootRequired		= rebootRequired;
												// In manifest.xml, access to these nodes:
		m_root					= null;			//		opbm
		m_benchmarks			= null;			//		opbm.benchmarks
		m_manifest				= null;			//		opbm.benchmarks.manifest
		m_control				= null;			//		opbm.benchmarks.control
		m_statistics			= null;			//		opbm.benchmarks.statistics
		m_settings				= null;			//		opbm.benchmarks.settings
		m_discovery				= null;			//		opbm.benchmarks.discovery

		m_runPasses				= new Tuple(opbm);

		// Remove any previous uuids that may have been leftover from a
		// previously failed build process
		m_opbm.getScriptsXml().stripUUIDsFromAllNodes(true);

		// Create the blank manifest template
		if (manifestPathName.isEmpty())
			createManifest();
		else
			reloadManifest(manifestPathName);
	}

	/**
	 * Adds entries to the compiled list for later building
	 * @param type "trial", "official", "suite", "scenario", "molecule", "atom"
	 * @param name if not trial or official, the name of the thing to include
	 * @param iterations if repeating, some larger value, otherwise must be 1
	 */
	public void addToCompiledList(String	type,
								  String	name,
								  int		iterations)
	{
		m_compilation.add(type,								/* first	= type, one of:  suite, scenario, molecule, atom, trial, official */
						  name,								/* second	= name of the thing, for suite-atom, is name in obpm.scriptdata.*, for trial or official is arbitrarily assigned name */
						  Integer.toString(iterations),		/* third	= number of iterations, typically 1*/
						  Integer.toString(m_passThis));	/* fourth	= the pass this entry should be added to */
	}

	/**
	 * Based on the run condition, builds the manifest for execution
	 */
	public boolean build()
	{
		boolean result;

		result = true;
		if (m_type.equalsIgnoreCase("trial")) {
			// Running a trial run (full benchmark, one pass)
			result = buildTrial();

		} else if (m_type.equalsIgnoreCase("official")) {
			// Running a trial run (full benchmark, three passes)
			result = buildOfficial();

		} else if (m_type.equalsIgnoreCase("compilation")) {
			// Running a compiled list of items added (from command line presumably, but can be compiled from any source)
			result = buildCompilation();

		} else {
			// Unknown
			setError("Run named '" + m_name + "' was not specified to be trial, official or compilation.  Cannot build.");
			result = false;
		}

		// Indicate success or failure to caller
		return(result);
	}

	/**
	 * Finalize everything in the build, set all max values, etc.
	 */
	public void buildFinalize()
	{
		setPassMaxValues();

		if (m_opbm.getSettingsMaster().benchmarkRebootBeforeEachPass())
			insertAutoRebootCommands();

		assignUUIDs();

		m_bmr.createResultsdataFramework();

		saveManifest();

		// Clean up by removing the uuids that were temporarily assigned in the build process
		m_opbm.getScriptsXml().stripUUIDsFromAllNodes(true);
	}

	/**
	 * Writes the manifest to opbm\running\manifest.xml
	 */
	public void saveManifest()
	{
// REMEMBER need to add error checking here
		m_root.saveNode(Opbm.getRunningDirectory() + "manifest.xml");
	}

	/**
	 * Compiled items are sent to the BenchmarkManifest class in succession
	 * while the list is being compiled (from any source).  Once compiled, this
	 * method is called to create a manifest to run the items in that order.
	 * @return
	 */
	public boolean buildCompilation()
	{
		int i, count;
		boolean error;
		String type, name;

		// See if they've build anything in the compilation
		if (m_compilation.isEmpty())
		{	// Nothing to do
			m_macroMaster.SystemOutPrintln("Error: Nothing has been compiled to build manifest");
			return(false);
		}

		// Try to add the things they've build
		count	= 0;
		error	= false;
		for (i = 0; i < m_compilation.size(); i++)
		{	// Extract this item, find out what it is
			type		= m_compilation.getFirst(i);
			name		= (String)m_compilation.getSecond(i);
			count		= Integer.valueOf((String)m_compilation.getThird(i));
			m_passThis	= Integer.valueOf((String)m_compilation.getFourth(i));

			// Now, they can build trial, official, suite, scenario, molecule or atom, in any order
			if (type.equalsIgnoreCase("suite"))
			{	// Add the suite!
				if (addSuiteByName(name, count) == 0)
				{	// The suite wasn't found
					m_isManifestInError = true;
					m_error = "Fatal Error: Unable to add suite named \"" + name + "\": does not exist";
					m_macroMaster.SystemOutPrintln(m_error);
					return(false);
				}

			} else if (type.equalsIgnoreCase("scenario")) {
				if (addScenarioByName(name, count) == 0)
				{	// The scenario wasn't found
					setError("Fatal Error: Unable to add scenario named \"" + name + "\": does not exist");
					return(false);
				}

			} else if (type.equalsIgnoreCase("molecule")) {
				if (addMoleculeByName(name, count) == 0)
				{	// The molecule wasn't found
					setError("Fatal Error: Unable to add molecule named \"" + name + "\": does not exist");
					return(false);
				}

			} else if (type.equalsIgnoreCase("atom")) {
				if (addAtomByName(name, count) == 0)
				{	// The atom wasn't found
					setError("Fatal Error: Unable to add atom named \"" + name + "\": does not exist");
					return(false);
				}
			}
		}
		// All done adding
		if (!error)
			buildFinalize();

		// Indicate success or failure
		return(!error);
	}

	/**
	 * Adds a full trial run to the manifest, which is all suites for one pass
	 * @return
	 */
	public boolean buildTrial()
	{
		boolean error;

		error = addAllSuites(1);	// One pass on a trial run
		// All done
		if (!error)
			buildFinalize();

		// Indicate success or failure
		return(!error);
	}

	/**
	 * Adds a full official run to the manifest, which is all suites, three passes
	 * @return
	 */
	public boolean buildOfficial()
	{
		boolean error;

		error = addAllSuites(3);	// Three passes on an official run
		// All done
		if (!error)
			buildFinalize();

		// Indicate success or failure
		return(!error);
	}

	/**
	 * Adds every suite in scripts.xml opbm.scriptdata.suites
	 * @param passes number of passes to add, 1 for trial, 3 for official, other values are custom
	 * @return true or false if everything added okay, and passed syntax check / validation
	 */
	public boolean addAllSuites(int passes)
	{
		int i, j, count;
		List<Xml> nodes = new ArrayList<Xml>(0);

		// Lower our count so it will report an error if there's nothing to do
		count = 0;

		// Grab every suite by its name
		Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.suites.suite", false);
		// If there are no suites, we move right to scenarios
		if (!nodes.isEmpty())
		{	// For each returned entry, add it to the list for the specified number of passes
			m_passMax = passes;
			count = 0;
			for (i = 0; i < passes; i++)
			{
				setPass(i + 1);
				for (j = 0; j < nodes.size(); j++)
				{	// Add each suite
					count += addSuite(nodes.get(j));
				}
			}

		} else {
			// Try scenarios
			// Grab every scenario by its name
			Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.scenarios.scenario", false);
			// If there are no scenarios, we move right to molecules
			if (!nodes.isEmpty())
			{	// For each returned entry, add it to the list for the specified number of passes
				m_passMax = passes;
				count = 0;
				for (i = 0; i < passes; i++)
				{
					setPass(i + 1);
					for (j = 0; j < nodes.size(); j++)
					{	// Add each scenario
						count += addScenario(nodes.get(j));
					}
				}

			} else {
				// Try molecules
				// Grab every molecule by its name
				Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.molecules.molecule", false);
				// If there are no molecules, we move right to atoms
				if (!nodes.isEmpty())
				{	// For each returned entry, add it to the list for the specified number of passes
					m_passMax = passes;
					count = 0;
					for (i = 0; i < passes; i++)
					{
						setPass(i + 1);
						for (j = 0; j < nodes.size(); j++)
						{	// Add each molecule
							count += addMolecule(nodes.get(j));
						}
					}

				} else {
					// Try atoms
					// Grab every atom by its name
					Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.atoms.atom", false);
					// If there are no atoms, this is an error because there's nothing to do
					if (!nodes.isEmpty())
					{	// For each returned entry, add it to the list for the specified number of passes
						m_passMax = passes;
						count = 0;
						for (i = 0; i < passes; i++)
						{
							setPass(i + 1);
							for (j = 0; j < nodes.size(); j++)
							{	// Add each atom
								count += addAtom(nodes.get(j));
							}
						}

					} else {
						// This is an error
						setError("Error:  Nothing to do.  No suites, scenarios, molecules or atoms were found in scripts.xml");

					}
				}

			}

		}
		return(count == 0);
	}

	/**
	 * Searches for the named suite and adds it to the manifest
	 * @param name name to add to the manifest
	 * @param iterations number of times to repeat this test
	 * @return
	 */
	public int addSuiteByName(String	name,
							  int		iterations)
	{
		int i, count, result;
		List<Xml> nodes = new ArrayList<Xml>(0);
		Xml suite;

		Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.suites.suite", false);
		for (i = 0; i < nodes.size(); i++)
		{	// See if we can find this one
			suite = nodes.get(i);
			if (suite.getAttribute("name").equalsIgnoreCase(name))
			{	// Found it
				count = 0;
				for (i = 0; i < iterations; i++)
				{	// Add this iteration
					result = addSuite(suite);
					if (result == 0)
					{	// Failure
						return(0);
					}
					count += result;
				}
				// When we get here, indicate how many were added
				return(count);
			}
		}
		// If we get here, the named suite wasn't found
		setError("Error: Unable to find suite by name: \"" + name + "\"");
		return(0);
	}

	/**
	 * Searches for the named scenario and adds it to the manifest
	 * @param name name to add to the manifest
	 * @param iterations number of times to repeat this test
	 * @return
	 */
	public int addScenarioByName(String		name,
								 int		iterations)
	{
		int i, count, result;
		List<Xml> nodes = new ArrayList<Xml>(0);
		Xml scenario;

		Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.scenarios.scenario", false);
		for (i = 0; i < nodes.size(); i++)
		{	// See if we can find this one
			scenario = nodes.get(i);
			if (scenario.getAttribute("name").equalsIgnoreCase(name))
			{	// Found it
				count = 0;
				for (i = 0; i < iterations; i++)
				{	// Add this iteration
					result = addScenario(scenario);
					if (result == 0)
					{	// Failure
						return(0);
					}
					count += result;
				}
				// When we get here, indicate how many were added
				return(count);
			}
		}
		// If we get here, the named scenario wasn't found
		setError("Error: Unable to find scenario by name: \"" + name + "\"");
		return(0);
	}

	/**
	 * Searches for the named molecule and adds it to the manifest
	 * @param name name to add to the manifest
	 * @param iterations number of times to repeat this test
	 * @return
	 */
	public int addMoleculeByName(String		name,
								 int		iterations)
	{
		int i, count, result;
		List<Xml> nodes = new ArrayList<Xml>(0);
		Xml molecule;

		Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.molecules.molecule", false);
		for (i = 0; i < nodes.size(); i++)
		{	// See if we can find this one
			molecule = nodes.get(i);
			if (molecule.getAttribute("name").equalsIgnoreCase(name))
			{	// Found it
				count = 0;
				for (i = 0; i < iterations; i++)
				{	// Add this iteration
					result = addMolecule(molecule);
					if (result == 0)
					{	// Failure
						return(0);
					}
					count += result;
				}
				// When we get here, indicate how many were added
				return(count);
			}
		}
		// If we get here, the named molecule wasn't found
		setError("Error: Unable to find molecule by name: \"" + name + "\"");
		return(0);
	}

	/**
	 * Searches for the named atom and adds it to the manifest
	 * @param name name to add to the manifest
	 * @param iterations number of times to repeat this test
	 * @return
	 */
	public int addAtomByName(String		name,
							 int		iterations)
	{
		int i, count, result;
		List<Xml> nodes = new ArrayList<Xml>(0);
		Xml atom;

		Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.atoms.atom", false);
		for (i = 0; i < nodes.size(); i++)
		{	// See if we can find this one
			atom = nodes.get(i);
			if (atom.getAttribute("name").equalsIgnoreCase(name))
			{	// Found it
				count = 0;
				for (i = 0; i < iterations; i++)
				{	// Add this iteration
					result = addAtom(atom);
					if (result == 0)
					{	// Failure
						return(0);
					}
					count += result;
				}
				// When we get here, indicate how many were added
				return(count);
			}
		}
		// If we get here, the named atom wasn't found
		setError("Error: Unable to find atom by name: \"" + name + "\"");
		return(0);
	}

	/**
	 * Adds the suite to the manifest
	 * @param name
	 */
	public int addSuite(Xml suite)
	{
		int i, j, count;
		List<Xml> nodes = new ArrayList<Xml>(0);
		Xml element;
		String type;

		// Grab every suite element
		Xml.getNodeList(nodes, suite.getFirstChild(), "[flow,abstract,scenario,molecule,atom]", false);

		if (nodes.isEmpty())
		{	// Nothing to do, no nodes for this
			m_isManifestInError = true;
			m_error = "Empty node list for " + suite.getAttribute("name");
			return(0);
		}

		// Add a tag for this suite in the manifest
		m_suiteName = suite.getAttribute("name");
		addTag("beginsuite", m_suiteName);

		// Iterate through this suite's entries and
		count = 0;
		for (i = 0; !m_isManifestInError && i < nodes.size(); i++)
		{
			element	= nodes.get(i);
			type	= element.getName();
			if (type.equalsIgnoreCase("flow"))
			{	// It's a flow-control directive, add it
				addElement(element, false);

			} else if (type.equalsIgnoreCase("abstract")) {
				addElement(element, true);
				++count;

			} else if (type.equalsIgnoreCase("scenario")) {
				count += addScenarioByName(element.getAttribute("sourcename"), 1);

			} else if (type.equalsIgnoreCase("molecule")) {
				count += addMoleculeByName(element.getAttribute("sourcename"), 1);

			} else if (type.equalsIgnoreCase("atom")) {
				count += addAtomByName(element.getAttribute("sourcename"), 1);

			}
		}

		// Add a tag for this suite in the manifest
		addTag("endsuite", m_suiteName);
		m_suiteName = "";

		// Return the number of elements added at this level
		return(count);
	}

	/**
	 * Adds the scenario to the manifest
	 * @param name
	 */
	public int addScenario(Xml scenario)
	{
		int i, j, count;
		List<Xml> nodes = new ArrayList<Xml>(0);
		Xml element;
		String type;

		// Grab every suite element
		Xml.getNodeList(nodes, scenario.getFirstChild(), "[flow,abstract,molecule,atom]", false);

		if (nodes.isEmpty())
		{	// Nothing to do, no nodes for this
			setError("Empty node list for " + scenario.getAttribute("name"));
			return(0);
		}

		// Add a tag for this suite in the manifest
		m_scenarioName = scenario.getAttribute("name");
		addTag("beginscenario", m_scenarioName);

		// Iterate through this suite's entries and
		count = 0;
		for (i = 0; !m_isManifestInError && i < nodes.size(); i++)
		{
			element = nodes.get(i);
			type = element.getName();
			if (type.equalsIgnoreCase("flow"))
			{	// It's a flow-control directive, add it
				addElement(element, false);

			} else if (type.equalsIgnoreCase("abstract")) {
				addElement(element, true);
				++count;

			} else if (type.equalsIgnoreCase("molecule")) {
				count += addMoleculeByName(element.getAttribute("sourcename"), 1);

			} else if (type.equalsIgnoreCase("atom")) {
				count += addAtomByName(element.getAttribute("sourcename"), 1);

			}
		}

		// Add a tag for this suite in the manifest
		addTag("endscenario", m_scenarioName);
		m_scenarioName = "";

		// Return the number of elements added at this level
		return(count);
	}

	/**
	 * Adds the molecule to the manifest
	 * @param name
	 */
	public int addMolecule(Xml molecule)
	{
		int i, j, count;
		List<Xml> nodes = new ArrayList<Xml>(0);
		Xml element;
		String type;

		// Grab every suite element
		Xml.getNodeList(nodes, molecule.getFirstChild(), "[flow,abstract,atom]", false);

		if (nodes.isEmpty())
		{	// Nothing to do, no nodes for this
			setError("Empty node list for " + molecule.getAttribute("name"));
			return(0);
		}

		// Add a tag for this suite in the manifest
		m_moleculeName = molecule.getAttribute("name");
		addTag("beginmolecule", m_moleculeName);

		// Iterate through this suite's entries and
		count = 0;
		for (i = 0; !m_isManifestInError && i < nodes.size(); i++)
		{
			element = nodes.get(i);
			type = element.getName();
			if (type.equalsIgnoreCase("flow"))
			{	// It's a flow-control directive, add it
				addElement(element, false);

			} else if (type.equalsIgnoreCase("abstract")) {
				addElement(element, true);
				++count;

			} else if (type.equalsIgnoreCase("atom")) {
				count += addAtomByName(element.getAttribute("sourcename"), 1);

			}
		}

		// Add a tag for this suite in the manifest
		addTag("endmolecule", m_moleculeName);
		m_moleculeName = "";

		// Return the number of elements added at this level
		return(count);
	}

	/**
	 * Adds the atom to the manifest
	 * @param name
	 */
	public int addAtom(Xml atom)
	{
		int i, j, count;
		List<Xml> nodes = new ArrayList<Xml>(0);
		Xml element;
		String type;

		// Grab every suite element
		Xml.getNodeList(nodes, atom.getFirstChild(), "[flow,abstract]", false);

		if (nodes.isEmpty())
		{	// Nothing to do, no nodes for this
			setError("Empty node list for " + atom.getAttribute("name"));
			return(0);
		}

		// Add a tag for this suite in the manifest
		m_atomName = atom.getAttribute("name");
		addTag("beginatom", m_atomName);

		// Iterate through this suite's entries and
		count = 0;
		for (i = 0; !m_isManifestInError && i < nodes.size(); i++)
		{
			element = nodes.get(i);
			type = element.getName();
			if (type.equalsIgnoreCase("flow"))
			{	// It's a flow-control directive, add it
				addElement(element, false);

			} else if (type.equalsIgnoreCase("abstract")) {
				addElement(element, true);
				++count;

			}
		}

		// Add a tag for this suite in the manifest
		addTag("endatom", m_atomName);
		m_atomName = "";

		// Return the number of elements added at this level
		return(count);
	}

	/**
	 * Adds a flow control directive or an atom for the specified element
	 * @param element
	 */
	public void addElement(Xml		element,
						   boolean	isAtom)
	{
		String level, atomuuid;
		Xml flowOrAtom, run, atomUuidXml;

		// Grab the current level
		level = getLevel();

		// If it's an atom, it must have an "atomuuid" field
		if (isAtom)
		{	// Assign an atomuuid to this flowOrAtom entry (allows the same
			// atom to be referenced in the output, even when used by different
			// molecules, scenarios or suites, by creating a single number to
			// reference throughout the entire manifest.
			atomUuidXml = element.getAttributeNode("atomuuid");
			if (atomUuidXml == null)
			{	// Create a UUID for this atom (if this atom is used again, it will be referenced with this single, unique value)
				element.appendAttribute("atomuuid", Utils.getUUID());
			}
		}

		// Duplicate the source flow control directive
		flowOrAtom = element.cloneNode(true);

		// Tag on the level for this entry
		flowOrAtom.appendAttribute("level", level);

		// Grab the current run pass we're appending to
		run = getRunForThisPass();

		// add the flow control directive to this run's pass
		run.appendChild(flowOrAtom);
	}

	/**
	 * Adds a tag to the manifest indicating the level we're at for any atoms
	 * that are executed at this point
	 * @param point
	 * @param name
	 */
	public void addTag(String	point,
					   String	name)
	{
		Xml run, tag;

		run = getRunForThisPass();
		tag = new Xml("tag");
		tag.appendAttribute("point",	point);
		tag.appendAttribute("name",		name);
		run.appendChild(tag);
	}

	/**
	 * Assembles a string which indicates the current level executing,
	 * assembling the suite name, scenario name, molecule name and atom name
	 * into a single suite.scenario.molecule.atom string
	 * @return the current level as suite.scenario.molecule.atom names
	 */
	public String getLevel()
	{
		String level;

		// Used to identify the level we're targeting
		level	= m_suiteName		+ (m_suiteName.isEmpty()		? "" : ".") +
				  m_scenarioName	+ (m_scenarioName.isEmpty()		? "" : ".") +
				  m_moleculeName	+ (m_moleculeName.isEmpty()		? "" : ".") +
				  m_atomName;
		return(level);
	}

	/**
	 * Sets an error condition and records the error message
	 * @param errorMsg message to record for the error
	 */
	public void setError(String errorMsg)
	{
		m_error = errorMsg;
		m_macroMaster.SystemOutPrintln(getLevel());
		m_macroMaster.SystemOutPrintln(m_error);
		m_isManifestInError = true;
	}

	/**
	 * Returns the last recorded error message
	 * @return
	 */
	public String getError()
	{
		return(m_error);
	}

	/**
	 * Manually overrides the pass, used typically for creation compilations,
	 * to specify which pass items should be added to.  Also creates the pass's
	 * run entry in the tuple, so it can be referenced later with
	 * <code>getRunForThisPass()</code>.
	 * @param pass the pass number to make it
	 */
	public void setPass(int pass)
	{
		m_passThis = pass;
		addPass();
	}

	/**
	 * Gets the current pass that's being added to, typically used during the
	 * compilation
	 * @return
	 */
	public int getPass()
	{
		return(m_passThis);
	}

	/**
	 * Gets the maximum pass for this manifest, valid only after buildFinalize()
	 * is called
	 * @return
	 */
	public int getPassMax()
	{
		return(m_passMax);
	}

	/**
	 * Based on m_passThis value, return the run pointer in the m_runPasses
	 * tuple.
	 * @return run pointer for this pass (from m_runPasses tuple)
	 */
	public Xml getRunForThisPass()
	{
		int i;

		// Make sure the run for this pass exists
		addPass();

		// Locate the item for this pass
		for (i = 0; i < m_runPasses.size(); i++)
		{
			if (i + 1 == m_passThis)
				return((Xml)m_runPasses.getSecond(i));
		}

		// We will never get here
		return(null);
	}

	/**
	 * Makes sure the pass is added to the manifest, and can be accessed.
	 * Uses m_passThis to determine which one should be added
	 */
	private void addPass()
	{
		int i;
		Xml runPass;
		String number;

		// Set our maximum if we're going ahead
		if (m_passThis > m_passMax)
			m_passMax = m_passThis;

		// We have to add up to this pass
		for (i = 0; i < m_passThis; i++)
		{	// Each entry may have already been added
			if (m_runPasses.size() < i + 1)
			{	// Create and add this one
				number	= Integer.toString(i + 1);
				runPass = new Xml("run");
				runPass.appendAttribute("this", number);

				// Append to opbm.benchmarks.manifest in manifests.xml
				m_manifest.appendChild(runPass);

				// Add to our tuple list
				m_runPasses.add(number, runPass);
				//              ^       ^
				//				first , second entries in tuple
			}
		}
	}

	/**
	 * Called as part of buildFinalize() to assign the maximum number of passes
	 * reached while processing to each of the run entries, so they have:
	 *		<run this="N" max="m_passMax">
	 */
	private void setPassMaxValues()
	{
		int i;
		Xml runPass;

		// We have to add up to this pass
		for (i = 0; i < m_runPasses.size(); i++)
		{	// Each entry may have already been added
			runPass = (Xml)m_runPasses.getSecond(i);
			runPass.appendAttribute("max", Integer.toString(m_passMax));
			// Will add "uuid" later when build is completed
		}
		// When we get here, they all have their max values set
	}

	/**
	 * Inserts automatic reboot-and-continue commands in-between passes, so the
	 * benchmark will reboot itself automatically, and continue running.
	 */
	private void insertAutoRebootCommands()
	{
		int i;
		Xml reboot, rebootTemplate;

		if (m_passMax > 1)
		{	// We need them
			// Build the master reboot abstract so it can be cloned for each pass
			m_rebootRequired = true;
			rebootTemplate = new Xml("abstract");
			rebootTemplate.appendAttribute(new Xml("name", "Reboot the machine (Auto-inserted at start of each pass)"));
			rebootTemplate.appendAttribute(new Xml("sourcename", "rebootAndContinue"));
			for (i = 0; i < m_runPasses.size(); i++)
			{	// Update the run-level xml for each entry
				reboot = rebootTemplate.cloneNode(true);
				// The "addChild" adds it to the beginning of the children, so it will be the first entry
				// Using "appendChild" or "appendAttribute" appends it to the end
				((Xml)m_runPasses.getSecond(i)).addChild(reboot);
			}

		} else {
			// For single-passes, no rebooting
			// So, we're done
		}
	}

	/**
	 * For every run entry, assign UUIDs to every node
	 */
	private void assignUUIDs()
	{
		int i;
		Xml runPass;

		// We have to add up to this pass
		for (i = 0; i < m_runPasses.size(); i++)
		{	// Each entry may have already been added
			runPass = (Xml)m_runPasses.getSecond(i);
			runPass.addUUIDsToAllNodes(false);
		}
		// When we get here, they all have their uuid values set
	}

	/**
	 * Builds the manifest template, assigns all Xml variables used to access
	 * member components
	 */
	public void createManifest()
	{
		Xml last;
		String time;

		m_root						= new Xml("opbm");
		m_benchmarks				= new Xml("benchmarks");
		m_manifest					= new Xml("manifest");
		m_control					= new Xml("control");
		m_statistics				= new Xml("statistics");
		m_discovery					= new Xml("discovery");

		m_root.appendChild(m_benchmarks);				// opbm.benchmarks

		// Manifest data will be added based on the variables of the specific run
		m_benchmarks.appendChild(m_manifest);			// opbm.benchmarks.manifest

//////////
// Append in the control data:
//
//		<control>
//			<run type="trial" name="whatever"/>
//			<last>
//				<run uuid="3169f211-ed26-2b45-7d99-68bf88a92ad1"/>
//				<tag uuid="938a8a12-387f-38b1-9038-abd38f8as8c8"/>
//				<worklet uuid="a9f26931-62de-45b2-9ed7-bd686156232d"/>
//			</last>
//		</control>
//
/////
		m_benchmarks.appendChild(m_control);			// opbm.benchmarks.control
		m_controlRun					= new Xml("run");
		last							= new Xml("last");
		m_controlLastRun				= new Xml("run");
		m_controlLastTag				= new Xml("tag");
		m_controlLastResult				= new Xml("result");
		m_controlLastAnnotation			= new Xml("annotation");
		m_controlLastWorklet			= new Xml("worklet");
		m_controlLastRunUuid			= new Xml("uuid");
		m_controlLastTagUuid			= new Xml("uuid");
		m_controlLastWorkletUuid		= new Xml("uuid");
		m_controlLastWorkletFinished	= new Xml("finished");
		m_controlLastResultUuid			= new Xml("uuid");
		m_controlLastAnnotationUuid		= new Xml("uuid");

		m_control.appendChild(m_controlRun);
		m_control.appendChild(last);
		last.appendChild(m_controlLastRun);
		last.appendChild(m_controlLastTag);
		last.appendChild(m_controlLastWorklet);

		// Set the initial control values
		m_controlRun.appendAttribute("type", m_type);
		m_controlRun.appendAttribute("name", m_name);
		m_controlRun.appendAttribute("automatedRun", (m_automated ? "Yes" : "No"));
		m_controlLastRun.appendAttribute(m_controlLastRunUuid);
		m_controlLastTag.appendAttribute(m_controlLastTagUuid);
		m_controlLastWorklet.appendAttribute(m_controlLastWorkletUuid);
		m_controlLastWorklet.appendAttribute(m_controlLastWorkletFinished);
		m_controlLastResult.appendAttribute(m_controlLastResultUuid);
		m_controlLastAnnotation.appendAttribute(m_controlLastAnnotationUuid);

//////////
// Append in statistics
//		<statistics>
//			<runtime>
//				<began>Tue Aug 16 16:39:51 CDT 2011 1313530812950</began>
//				<ended>Tue Aug 16 16:39:51 CDT 2011 1313530812950</ended>
//				<harness>00:08:15.000</harness>
//				<scripts>01:21:17.000</scripts>
//			</runtime>
//			<successes>30</successes>
//			<failures>5</failures>
//			<retries>7</retries>
//		</statistics>
/////
		m_benchmarks.appendChild(m_statistics);			// opbm.benchmarks.statistics
		m_statisticsRuntime			= new Xml("runtime");
		m_statisticsRuntimeBegan	= new Xml("began");
		m_statisticsRuntimeEnded	= new Xml("ended");
		m_statisticsRuntimeHarness	= new Xml("harness");
		m_statisticsRuntimeScripts	= new Xml("scripts");
		m_statisticsSuccesses		= new Xml("successes");
		m_statisticsFailures		= new Xml("failures");
		m_statisticsRetries			= new Xml("retries");
		m_statistics.appendChild(m_statisticsRuntime);
		m_statisticsRuntime.appendChild(m_statisticsRuntimeBegan);
		m_statisticsRuntime.appendChild(m_statisticsRuntimeEnded);
		m_statisticsRuntime.appendChild(m_statisticsRuntimeHarness);
		m_statisticsRuntime.appendChild(m_statisticsRuntimeScripts);
		m_statistics.appendChild(m_statisticsSuccesses);
		m_statistics.appendChild(m_statisticsFailures);
		m_statistics.appendChild(m_statisticsRetries);

		// Set initial time values for the time-in-harness statistics computation
		time = Utils.getTimestamp();
		m_statisticsRuntimeBegan.setText(time);
		m_statisticsRuntimeEnded.setText(time);
		m_statisticsSuccesses.setText("0");
		m_statisticsFailures.setText("0");
		m_statisticsRetries.setText("0");

		// Append in user settings at time of manifest creation
		m_settings = m_opbm.getSettingsMaster().getSettingsXml("opbm.settings.benchmarks").cloneNode(true);
		m_settings.setName("settings");
		m_benchmarks.appendChild(m_settings);

		// Append discovery info
		m_benchmarks.appendChild(m_discovery);			// opbm.benchmarks.discovery

// REMEMBER CPU-Z CPUZ stuff will go here

		m_manifestIsLoaded = true;
		// Note:  The manifest is not saved to manifest.xml because it may not be used.
		//        When a build*() method is called and a run is initiated, then it will
		//        be saved as needed for the operation.
	}

	/**
	 * Reload the manifest, and recompute its tallied values into the form
	 * required by results.xml
	 * @param pathName file to load (typically Opbm.getRunningDirectory() + "manifest.xml"
	 */
	public void reloadManifestAndComputeResultsXml(String pathName)
	{
		reloadManifest(pathName);

		if (m_isManifestInError)
			return;		// There was an error opening the manifest

		// If we get here, we're good
		computeForResultsXml(true, true);
	}

	/**
	 * Reloads the specified filename to the manifest.xml file, and processes
	 * it to make sure everything that should be there is there, ignoring
	 * extraneous information
	 * @param pathName
	 */
	public void reloadManifest(String pathName)
	{
		m_isManifestInError = true;
		m_root = Opbm.loadXml(pathName, m_opbm);
		if (m_root == null)
		{	// Did not load successfully
			setError("Error: Unable to reload " + pathName + " manifest file, cannot restart.");
			return;
		}
		// Try to set our tags
		m_benchmarks					= m_root.getAttributeOrChildNode("benchmarks");
		m_manifest						= m_root.getAttributeOrChildNode("benchmarks.manifest");
		m_control						= m_root.getAttributeOrChildNode("benchmarks.control");
		m_statistics					= m_root.getAttributeOrChildNode("benchmarks.statistics");
		m_settings						= m_root.getAttributeOrChildNode("benchmarks.settings");
		m_discovery						= m_root.getAttributeOrChildNode("benchmarks.discovery");
		m_controlRun					= m_root.getAttributeOrChildNode("benchmarks.control.run");
		m_controlLastRun				= m_root.getAttributeOrChildNode("benchmarks.control.last.run");
		m_controlLastTag				= m_root.getAttributeOrChildNode("benchmarks.control.last.tag");
		m_controlLastWorklet			= m_root.getAttributeOrChildNode("benchmarks.control.last.worklet");
		m_controlLastRunUuid			= m_root.getAttributeOrChildNode("benchmarks.control.last.run.uuid");
		m_controlLastTagUuid			= m_root.getAttributeOrChildNode("benchmarks.control.last.tag.uuid");
		m_controlLastWorkletUuid		= m_root.getAttributeOrChildNode("benchmarks.control.last.worklet.uuid");
		m_controlLastWorkletFinished	= m_root.getAttributeOrChildNode("benchmarks.control.last.worklet.finished");
		m_statisticsRuntime				= m_root.getAttributeOrChildNode("benchmarks.statistics.runtime");
		m_statisticsRuntimeBegan		= m_root.getAttributeOrChildNode("benchmarks.statistics.runtime.began");
		m_statisticsRuntimeEnded		= m_root.getAttributeOrChildNode("benchmarks.statistics.runtime.ended");
		m_statisticsRuntimeHarness		= m_root.getAttributeOrChildNode("benchmarks.statistics.runtime.harness");
		m_statisticsRuntimeScripts		= m_root.getAttributeOrChildNode("benchmarks.statistics.runtime.scripts");
		m_statisticsSuccesses			= m_root.getAttributeOrChildNode("benchmarks.statistics.successes");
		m_statisticsFailures			= m_root.getAttributeOrChildNode("benchmarks.statistics.failures");
		m_statisticsRetries				= m_root.getAttributeOrChildNode("benchmarks.statistics.retries");

		if (m_benchmarks == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks not found");
			return;
		}

		if (m_manifest == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.manifest not found");
			return;
		}

		if (m_control == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.control not found");
			return;
		}

		if (m_controlRun == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.control.run not found");
			return;
		}

		if (m_controlLastRun == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.control.last.run not found");
			return;
		}

		if (m_controlLastTag == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.control.last.tag not found");
			return;
		}

		if (m_controlLastWorklet == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.control.last.worklet not found");
			return;
		}

		if (m_controlLastRunUuid == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.control.last.run.#uuid not found");
			return;
		}

		if (m_controlLastTagUuid == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.control.last.tag.#uuid not found");
			return;
		}

		if (m_controlLastWorkletUuid == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.control.last.worklet.#uuid not found");
			return;
		}

		if (m_controlLastWorkletFinished == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.control.last.worklet.#finished not found");
			return;
		}

		if (m_statistics == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.statistics not found");
			return;
		}

		if (m_statisticsRuntime == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.statistics.runtime not found");
			return;
		}

		if (m_statisticsRuntimeBegan == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.statistics.runtime.began not found");
			return;
		}

		if (m_statisticsRuntimeEnded == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.statistics.runtime.ended not found");
			return;
		}

		if (m_statisticsRuntimeHarness == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.statistics.runtime.harness not found");
			return;
		}

		if (m_statisticsRuntimeScripts == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.statistics.runtime.scripts not found");
			return;
		}

		if (m_statisticsSuccesses == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.statistics.successes not found");
			return;
		}

		if (m_statisticsFailures == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.statistics.failures not found");
			return;
		}

		if (m_statisticsRetries == null)
		{	// fail
			System.out.print("Error:  Unable to re-load manifest. opbm.benchmarks.statistics.retries not found");
			return;
		}

		// Verify the settings there are as they should be
		if (!m_opbm.getSettingsMaster().validateSettings(m_settings, false))
		{	// The settings are not right
			System.out.print("Error:  Unable to re-load manifest. Settings are not correct.");
			return;
		}

		if (!m_bmr.reloadResultsdata())
		{	// The resultsdata section is not right
			System.out.print("Error:  Unable to re-load manifest. Resultsdata is not correct.");
			return;
		}

		// Reload the automated run condition
		// Determines whether or not the results viewer is displayed at the end
		Xml automated = m_controlRun.getAttributeNode("automatedRun");
		m_automated = false;
		if (automated != null)
		{	// The attribute is present
			if (Utils.interpretBooleanAsYesNo(automated.getText(), false).equalsIgnoreCase("yes"))
			{	// And it says it is automated
				m_automated = true;
			}
		}

		// If we get here, we found everything, everything looks good, etc.
		m_manifestIsLoaded	= true;
		m_isManifestInError	= false;
	}

	/**
	 * Returns the manifest state.  If everything was created or loaded okay,
	 * it will not be in error.  Used mostly for restarting, to see if the
	 * reload went okay.
	 * @return is the manifest in error?
	 */
	public boolean isManifestInError()
	{
		return(m_isManifestInError);
	}

	/**
	 * Returns the opbm tag in the manifest.xml file
	 * @return opbm tag
	 */
	public Xml getRootOpbm()
	{
		return(m_root);
	}

	/**
	 * Runs the manifest, records all data
	 */
	public void run()
	{
		boolean prevIsRecordingCounts, showResultsViewer;
		Xml result, car;

		if (m_isManifestInError)
		{	// We cannot run this manifest because it's in error
			OpbmDialog od = new OpbmDialog(m_opbm, m_error, "Run Error", OpbmDialog._OKAY_BUTTON, "", "");
			return;
		}
		if (m_controlLastRunUuid.getText().equalsIgnoreCase("finished"))
		{	// The manifest is already finished, there's nothing else to do
			OpbmDialog od = new OpbmDialog(m_opbm, "Benchmark has completed", "Restart Error", OpbmDialog._OKAY_BUTTON, "completed", "");
			return;
		}

		// We're good, run the manifest
		m_processing = true;
		// The benchmark may proceed many times through reboots, each time we re-record the time
		if (m_statisticsRuntimeBegan.getText().isEmpty())
		{	// The begin time has never been recorded, so record it
			m_statisticsRuntimeBegan.setText(Utils.getTimestamp());
		}
		// Mark this time, as it is likely following a reboot
		m_bmr.appendResultsAnnotation("startup", Utils.getTimestamp(), "");

		// Initialize everything
		if (!m_benchmarksMaster.benchmarkInitialize(m_opbm.getMacroMaster(), m_opbm.getSettingsMaster(), m_automated, m_rebootRequired))
			return;	// Something happened, we cannot continue

		// Grab some necessary pointers for benchmark processing
		m_bp		= m_benchmarksMaster.getBP();
		m_bpa		= m_benchmarksMaster.getBPAtom();
		m_bp.m_bm	= this;

//////////
// For debugging mode, uncomment and/or set this variable to true.  It will
// simulate runs with the success.exe script so that full official, trial and
// compilation runs proceed much faster.  It also eliminates the WaitUntilIdle()
// test and turns off the regular saving of the manifest.xml file until the run
// is done.
//////
	//
	//Opbm.m_debugSimulateRunAtomMode = true;
	//Opbm.m_debugSimulateRunAtomModeFailurePercent = 0.6;	// Fail 60% of the time
	//
//////
// End
//////////

		// Save and install the Office 2010 registry keys
		Opbm.Office2010SaveKeys();
		Opbm.Office2010InstallKeys();


		// Begin processing atoms
		showResultsViewer = true;
		if (runHasNotYetStarted())
		{	// Check for any conflicts on this run
			prevIsRecordingCounts = m_bpa.m_isRecordingCounts;
			m_bpa.m_isRecordingCounts = false;
			result = runAtomTags("atomCheckConflicts", true);
			m_bpa.m_isRecordingCounts = prevIsRecordingCounts;
			if (m_bp.m_debuggerOrHUDAction < BenchmarkParams._STOP && !m_conflicts.isEmpty() && !m_resolutions.isEmpty())
			{	// See if any results contain "conflict," responses
				car = createXmlOfConflictsAndResolutions();
				if (car != null && car.getFirstChild() != null)
				{	// Display them to the user, and then terminate
					OpbmColumns oc		= new OpbmColumns(m_opbm, car, Utils.createSimpleTwoColumnDisplay(750, 500, "Conflicts and Resolutions", "Conflict", "conflict", "50%", "Resolution", "resolution", "50%"));
					m_processing		= false;
					showResultsViewer	= false;
				}
			}
		}

		if (m_processing)
		{
			// If this is a trial or official run...
			if (isATrialRun() || isAnOfficialRun())
			{	// Run the appropriate tags based on where we are
				prevIsRecordingCounts = m_bpa.m_isRecordingCounts;
				m_bpa.m_isRecordingCounts = false;
				if (runHasNotYetStarted())
				{	// Run the pre-run atoms
					result = runAtomTags("atomBeforeRuns", false);
					// If is an official run, the next thing it will do is reboot,
					// at which time it will run the spinups below
					// If it's a trial run, it will simply proceed

				} else {
					result = null;
					if (isAnOfficialRun())
					{	// Launch the spinup scripts first, anything beginning with the text "spinup ", as in "spinup something" or "spinup other thing"
						if (m_opbm.getSettingsMaster().benchmarkRunSpinups())
						{	// They want to run spinups
							result = runAtomsStartingWithThisPrefix("spinup ");
							if (result.getFirstChild() != null)
							{	// At least one thing was done, record it
								m_bmr.appendAtomRunResult(result);
								saveManifest();
							}
							// Run the specifically-related-to-atom spinup atoms
							System.out.println("Spinning up");
							result = runAtomTags("atomOnSpinup", false);
						}
					}
				}
				if (result != null && result.getFirstChild() != null)
				{	// At least one thing was done, record it
					m_bmr.appendAtomRunResult(result);
					saveManifest();
				}
				m_bpa.m_isRecordingCounts = prevIsRecordingCounts;

				// If the user clicked stop, we're done processing
				m_processing = !(m_bp.m_debuggerOrHUDAction == BenchmarkParams._STOP_USER_CLICKED_STOP);
			}
		}

		// Continue processing until we're done
		while (m_processing)
		{
			// For commands like reboot-and-continue, there are follow-ups to the
			// last command which must be stored, such as the time between reboot
			// and continuation.
			runPreprocessAnyFollowups();

			if (m_processing)
				runMoveToNext();	// Move to the next thing to do

			if (m_processing)
				runExecute();		// Execute that thing
		}
		// When we get here, we're no longer processing, but it may not be due
		// to the benchmark being over.  If we are rebooting, then we simply
		// shut down and continue.
		// Record the time of this event
		m_bmr.appendResultsAnnotation("shutdown", Utils.getTimestamp(), "");
		saveManifest();

		// When the run is completely over, run the post-run atoms
		if ((m_bp.m_debuggerOrHUDAction < BenchmarkParams._STOP) && (isATrialRun() || isAnOfficialRun()) && runIsNowCompletelyOver())
		{	// Run the post-run atoms
			prevIsRecordingCounts = m_bpa.m_isRecordingCounts;
			m_bpa.m_isRecordingCounts = false;
			m_bmr.appendAtomRunResult(runAtomTags("atomAfterRuns", false));
			m_bpa.m_isRecordingCounts = prevIsRecordingCounts;
		}

		// Restore the user's previous registry keys
		Opbm.Office2010RestoreKeys();

//////////
// We compute totals if one of these conditions is true:
//		1)  the last atom was a failure, and we're done now
//		2)  the user clicked stop, and we're done now
//		3)  the benchmark is completely run finished, and we're done now
/////
		// We are finished, so tally up!
		computeForResultsXml(false, true);

		// If the last thing that happened was a failure, and we are supposed to uninstall stuff after failures, we need to do that
		if ((m_bp.m_debuggerOrHUDAction < BenchmarkParams._STOP) && m_bp.m_bpAtom.m_lastAtomWasFailure && m_opbm.getSettingsMaster().benchmarkUninstallAfterFailures())
		{	// If there were failures, and they want to run the uninstall scripts after processing
			prevIsRecordingCounts = m_bpa.m_isRecordingCounts;
			m_bpa.m_isRecordingCounts = false;
			m_bp.m_debuggerOrHUDAction = BenchmarkParams._NO_ACTION;
			runAutoUninstaller();
			m_bpa.m_isRecordingCounts = prevIsRecordingCounts;
		}

		// Finish up, show the results viewer
		m_benchmarksMaster.benchmarkShutdown(showResultsViewer);
	}

	/**
	 * Called to indicate a reboot is imminent.  If the current abstract is a
	 * reboot command, then we mark it as finished so it will proceed to the
	 * next entry upon OPBM restart.  Otherwise, we do nothing.
	 */
	public void runExecuteSetRebooting()
	{
		if (m_worklet.getName().equalsIgnoreCase("abstract"))
		{	// We're processing an abstract
			if (m_worklet.getAttribute("sourcename").toLowerCase().startsWith("reboot"))
			{	// And it IS a reboot command
				// Indicate it's finished
				setLastWorkletFinished();
				// Save it to disk for the restart
				saveManifest();
				// If the reboot fails, it will report a failed reboot
			}
		}
	}

	/**
	 * Called to indicate a reboot failed, and the watchdog timer (set for 60
	 * seconds) timed out, and canceled the reboot.
	 */
	public void runExecuteSetRebootingFailed()
	{
		if (m_worklet.getName().equalsIgnoreCase("abstract"))
		{	// We're processing an abstract
			if (m_worklet.getAttribute("sourcename").toLowerCase().startsWith("reboot"))
			{	// And it IS a reboot command
				m_controlLastWorkletFinished.setText("no");
				saveManifest();
				setError("Error: Failed to reboot the operating system.");
				m_processing = false;
			}
		}
	}

	/**
	 * Looks at the control portion of the manifest, and determines if there
	 * is anything that needs to be followed-up on before continuing.  This is
	 * typically used only for the reboot-and-continue or reboot-and-restart
	 * commands.
	 */
	public void runPreprocessAnyFollowups()
	{
		String uuid;
		Xml candidate;

		uuid = m_controlLastRun.getAttribute("uuid");
		if (!uuid.isEmpty())
		{	// There is a uuid here, see what it relates to
			candidate = m_manifest.getNodeByUUID(uuid, false);
			if (candidate != null)
			{	// We found the prior entry
				if (candidate.getName().toLowerCase().startsWith("reboot"))
				{	// It's a reboot command
					// Grab the current time and update it in the log
					m_bmr.appendToLastResultAnnotation("rebootEnded", Utils.getTimestamp(), candidate.getAttribute("uuid"));

				} else {
					// We don't have anything else to do for other commands
					// So, we're done with preprocessing anything
					// Return politely and silently
				}
			}
		}
		// When we get here, all finished with preprocessing
	}

	/**
	 * Looks at the control portion of the manifest, and moves to the next flow
	 * control directive, or abstract command, and prepares it for processing
	 * by updating its location in the control portion.
	 */
	public void runMoveToNext()
	{
		Xml candidate;

		// Based on where we are currently in the opbm.benchmarks.manifest.control.last.*
		// entries, move to the next location in opbm.benchmarks.manifest.*
		if (m_controlLastWorkletUuid.getText().isEmpty())
		{	// We haven't started yet, we are just beginning
			// Process through the manifest to reach the first run entry
			runMoveToNext_MoveToNextRunTagAndWorklet();
			if (!m_processing)
			{	// Some failure in moving
				return;
			}
			// When we get here, we have moved to our starting positions

		} else {
			// Continue on from our previous location
			// Load our existing pointers
			m_run		= m_manifest.getNodeByUUID(m_controlLastRunUuid.getText(), false);
			m_tag		= m_manifest.getNodeByUUID(m_controlLastTagUuid.getText(), false);
			m_worklet	= m_manifest.getNodeByUUID(m_controlLastWorkletUuid.getText(), false);
			if (m_run == null)
			{	// Fail
				setError("Error:  Unable to find last run position by uuid.");
				return;
			}
			if (m_tag == null && !m_controlLastTagUuid.getText().isEmpty())
			{	// Fail
				// Note:  For the auto-inserted reboot command, the tag will be empty, so the failure is only valid if the tag is not empty
				setError("Error:  Unable to find last tag position by uuid.");
				return;
			}
			if (m_worklet == null)
			{	// Fail
				setError("Error:  Unable to find last worklet position by uuid.");
				return;
			}

			// We have retrieved our existing pointers
			if (runMoveToNext_DidLastWorkletFinish())
			{	// Move to the next location
				runMoveToNext_MoveToNextTagAndWorklet();
				if (!m_processing)
				{	// Some failure in moving
					return;
				}
				// When we get here, we're good, we have everything
			}
		}

		// Update our uuids for these entries
		m_controlLastRunUuid.setText(m_run == null ? "" : m_run.getAttribute("uuid"));
		m_controlLastTagUuid.setText(m_tag == null ? "" : m_tag.getAttribute("uuid"));
		m_controlLastWorkletUuid.setText(m_worklet == null ? "" : m_worklet.getAttribute("uuid"));
		setLastWorkletNotFinished();
		// Note:  We don't save our new manifest state to disk just yet, because
		//        OPBM leaves everything in memory until such time as it begins
		//        an execute* abstract.  Then, as part of the pre-processing for
		//        the script/worklet execution of an external app, it fully saves
		//        the state so it can resume should there be a power loss or
		//        other catastrophic failure resulting in a system reboot.
	}

	/**
	 * See if the last run finished okay
	 * @return true or false based on control.last.worklet.#finished
	 */
	public boolean runMoveToNext_DidLastWorkletFinish()
	{
		if (m_controlLastWorkletFinished.getText().equalsIgnoreCase("yes"))
		{	// Finished okay
			return(true);

		} else {
			// Did not finish, needs to be continued or tried again
			return(false);
		}
	}

	/**
	 * Moves to the first item in the run/pass
	 */
	public void runMoveToNext_MoveToFirstTagAndWorkletInRun()
	{
		Xml candidate;

		candidate		= m_run.getFirstChild();
		m_tag			= null;
		m_worklet		= null;
		while (candidate != null)
		{
			if (candidate.getName().equalsIgnoreCase("tag"))
			{	// Update the last tag found
				m_tag = candidate;

			} else if (candidate.getName().equalsIgnoreCase("flow")) {
				// It's a flow-control directive, this is our next entry
				m_worklet = candidate;
				break;

			} else if (candidate.getName().equalsIgnoreCase("abstract")) {
				// It's an abstract command, this is our next entry
				m_worklet = candidate;
				break;

			} else {
				// Continue looking
			}

			// Move to next entry
			candidate = candidate.getNext();
		}
		if (candidate == null)
		{	// Nothing found to do in this run, see if there are any more runs
			m_macroMaster.SystemOutPrintln("Warning: An empty opbm.benchmarks.manifest.run was not found. Nothing to do. Looking for next run tag.");
			runMoveToNext_MoveToNextRunTagAndWorklet();
			if (!m_processing)
			{	// No more runs, nothing to do
				setError("Error: No valid opbm.benchmarks.manifest.run entries found. Nothing to do.");
				return;
			}
		}
		// If we get here, we're good
	}

	public void runMoveToNext_MoveToNextTagAndWorklet()
	{
		Xml candidate;

		candidate = m_worklet.getNext();
		while (candidate != null)
		{
			if (candidate.getName().equalsIgnoreCase("tag"))
			{	// Update the last tag found
				m_tag = candidate;

			} else if (candidate.getName().equalsIgnoreCase("flow")) {
				// It's a flow-control directive, this is our next entry
				m_worklet = candidate;
				break;

			} else if (candidate.getName().equalsIgnoreCase("abstract")) {
				// It's an abstract command, this is our next entry
				m_worklet = candidate;
				break;

			} else {
				// Continue looking
			}

			// Move to next entry
			candidate = candidate.getNext();
		}
		if (candidate == null)
		{	// Nothing more was found, we're done with this pass
			// See if there are any more passes
			runMoveToNext_MoveToNextRunTagAndWorklet();
			if (!m_processing)
			{	// No more runs
				return;
			}
		}
	}

	/**
	 * Moves to the next run (if any).  If there aren't any, then the benchmark
	 * is finished.
	 */
	public void runMoveToNext_MoveToNextRunTagAndWorklet()
	{
		Xml candidate;
		boolean wasFirst;

		// If we're doing this the first time through, start from the beginning
		// Otherwise, start from where the last run was, its next sibling
		if (m_run == null)
		{	// First time through, we have to try from the manifest
			candidate	= m_manifest.getFirstChild();
			wasFirst	= true;

		} else {
			candidate	= m_run.getNext();
			wasFirst	= false;
		}

		// Iterate until there are no more entries at the opbm.benchmarks.manifest.* level
		while (candidate != null)
		{
			if (candidate.getName().equalsIgnoreCase("run"))
			{	// We found another pass
				m_run = candidate;
				break;
			}
			// Move to next location
			candidate = candidate.getNext();
		}
		if (candidate == null)
		{	// Nothing (more) was found
			if (wasFirst)
			{	// An error, this is a malformed manifest
				setError("Error: Malformed manifest.xml, could not find a run to initiate.");

			} else {
				// We're done, benchmark is over
				m_controlLastRunUuid.setText("finished");
				m_controlLastTagUuid.setText("finished");
				m_controlLastWorkletUuid.setText("finished");
			}
			m_processing = false;
			return;
		}
		// When we get here, we found our next run, so move to the
		// first thing to do
		runMoveToNext_MoveToFirstTagAndWorkletInRun();
		// When we get here, we're either finished, or we're on the first
		// item in this or another run
	}

	/**
	 * Runs the current command pointed to by the control portion of the manifest
	 */
	public void runExecute()
	{
		Xml success, failures;
		String manifestWorkletUuid, manifestAtomUuid, type;

		if (!Opbm.m_debugSimulateRunAtomMode)
			saveManifest();		// Save this result when not simulating a run in debug moe

		if (!m_run.getAttribute("this").equals("1") || !m_run.getAttribute("this").equals(m_run.getAttribute("max")))
		{	// We display the pass information on runs that have more than one pass
			m_macroMaster.SystemOutPrintln("Pass " + m_run.getAttribute("this") + " of " + m_run.getAttribute("max") + ": " + m_worklet.getAttribute("name"));
		} else {
			m_macroMaster.SystemOutPrintln("Executing: " + m_worklet.getAttribute("name"));
		}

		if (m_worklet.getName().equalsIgnoreCase("abstract"))
		{	// Create the area to store results from our execute atom
			success		= new Xml("success");
			failures	= new Xml("failure");

			// Grab some pointers
			manifestWorkletUuid	= m_controlLastWorkletUuid.getText();
			manifestAtomUuid	= m_worklet.getAttribute("atomuuid");

//////////
// Physically conduct the work of the atom
			m_bpa.processAbstract_Atom(m_worklet, success, failures);
			if (m_bp.m_debuggerOrHUDAction != BenchmarkParams._NO_ACTION && m_bp.m_debuggerOrHUDAction != BenchmarkParams._RUN)
			{	// They're doing something other than "nothing" or running
				// Log it
				type = m_bp.getDebuggerOrHUDActionReason();
				m_bmr.appendResultsAnnotation("note", type, manifestWorkletUuid);

				// And if they're stopping, cease processing
				if (m_bp.m_debuggerOrHUDAction >= BenchmarkParams._STOP)
				{	// They're stopping for whatever reason
					m_processing = false;
				}
			}
			if (m_bp.getLastWorkletResult().equalsIgnoreCase("success"))
			{	// We're good, it was a success
				setLastWorkletFinished();

			} else {
				// If it was a failure, it will have already been set to "yes" or "no"
				// for "finished" based on failure logic in BenchmarksAtom().  And if
				// it is set to "yes", we continue, otherwise we're done here.
				if (!m_controlLastWorkletFinished.getText().equalsIgnoreCase("yes"))
				{	// We're finished
					m_processing = false;
				} else {
					// We're good, that failure did not knock us down
				}
			}
// End
//////////

			// Store the results good or bad
			m_bmr.appendResultsDetail(success, failures, manifestWorkletUuid);

			// Add the result line
			m_bmr.appendResult(manifestWorkletUuid,
							   manifestAtomUuid,
							   m_bp.getLastWorkletStart(),
							   m_bp.getLastWorkletEnd(),
							   m_bp.getLastWorkletResult(),
							   m_bp.getLastWorkletScore(),
							   m_bp.getLastWorkletTimingData());

			// Update our statistics
			runExecuteUpdateStatistics();

		} else {
			// It's a flow-control directive, it will update where we move from here
			setError("Error: Flow control directives not yet been supported in manifest benchmarks");
		}
	}

	/**
	 * Updates the statistics portion of the manifest
	 *		<statistics>
	 *			<runtime>
	 *				<began>Tue Aug 16 16:39:51 CDT 2011 1313530812950</began>
	 *				<ended>Tue Aug 16 16:39:51 CDT 2011 1313530812950</ended>
	 *				<harness>00:00:00.0</harness>
	 *				<scripts>298312950</scripts>	<!-- totla milliseconds running external scripts -->
	 *			</runtime>
	 *			<successes>00</successes>
	 *			<failures>00</failures>
	 *			<retries>00</retries>
	 *		</statistics>
	 */
	public void runExecuteUpdateStatistics()
	{
		String began, ended, count;

//////////
// Update the overall harness runtime
		// Update our last end run time
		setStatisticsRuntimeEnded(m_bp.getLastWorkletEnd());

		// Compute the total runtime
		began = m_statisticsRuntimeBegan.getText();
		ended = m_statisticsRuntimeEnded.getText();
		if (!began.isEmpty() && !ended.isEmpty())
		{	// There is a time here, we can compute the difference
			m_statisticsRuntimeHarness.setText(Utils.convertMillisecondDifferenceToHHMMSSff(began, ended));
		}

		// Successes or failures
		if (m_bp.getLastWorkletResult().equalsIgnoreCase("success"))
		{	// Increases successes
			count = m_statisticsSuccesses.getText();
			if (count.isEmpty())
			{	// First entry
				m_statisticsSuccesses.setText("1");
			} else {
				// Add to the existing count
				m_statisticsSuccesses.setText(Integer.toString(Integer.valueOf(count) + 1));
			}

		} else {
			// Increase failures
			count = m_statisticsFailures.getText();
			if (count.isEmpty())
			{	// First entry
				m_statisticsFailures.setText("1");
			} else {
				// Add to the existing count
				m_statisticsFailures.setText(Integer.toString(Integer.valueOf(count) + 1));
			}
		}

		// Retries
		if (m_bp.getLastWorkletRetries() != 0)
		{	// Increase retries
			count = m_statisticsRetries.getText();
			if (count.isEmpty())
			{	// First entry
				m_statisticsRetries.setText(Integer.toString(m_bp.getLastWorkletRetries()));
			} else {
				// Add to the existing count
				m_statisticsRetries.setText(Integer.toString(Integer.valueOf(count) + m_bp.getLastWorkletRetries()));
			}
		}

//////////
// Update the overall script runtime
		runExecuteUpdateStatisticsScriptRuntime();

//////////
// Note:  To determine the time-in-harness, subtract out the m_statisticsRuntimeScripts
//        value, and compute the resulting difference as HHMMSS.
	}

	public void runExecuteUpdateStatisticsScriptRuntime()
	{
		String count;

		count = m_statisticsRuntimeScripts.getText();
		if (count.isEmpty())
		{	// First entry
			m_statisticsRuntimeScripts.setText(Long.toString(m_bp.getMillisecondsRunningLastWorklet()));
		} else {
			// Add to the existing count
			m_statisticsRuntimeScripts.setText(Long.toString(Integer.valueOf(count) + m_bp.getMillisecondsRunningLastWorklet()));
		}
	}

	/**
	 * Called to run the un-installers for any atoms that were processed, so
	 * the state of the machine is rolled back to its original state
	 */
	public void runAutoUninstaller()
	{
		int i, j, k, failures, previousDebuggerOrHUDAction;
		boolean executed, ignoreThisOne;
		Xml result, atomCandidate, autoUninstall, success, failure, thisAtom, thisAtomResults;
		String manifestworkletuuid, atomsList, atomName, failureNames, successNames, autoUninstalledAlready;
		List<String>	atoms			= new ArrayList<String>(0);		// atom names found in <atomOnFailure> tag
		List<Xml>		scriptsAtoms	= new ArrayList<Xml>(0);		// atoms from scripts.xml
		List<Xml>		results			= new ArrayList<Xml>(0);		// results from manifest.xml

		// Grab the completed atoms, one-by-one
		Xml.getNodeList(results, m_bmr.getResultsDataRawResults(), "rawResults.result", false);

		if (!results.isEmpty())
		{	// Load all of the scripts.xml atoms
			Xml.getNodeList(scriptsAtoms, m_opbm.getScriptsXml(), "opbm.scriptdata.atoms.atom", false);
			// Right now, scriptsAtoms is populated with every atom entry
		}
		// See if there is already an uninstall tag
		autoUninstall = m_bmr.getResultsDataDetails().getChildNode("autoUninstalls");
		if (autoUninstall == null)
		{	// Nope, add it
			autoUninstall = new Xml("autoUninstalls");
			autoUninstall.appendAttribute(new Xml("time", Utils.getTimestamp()));
			autoUninstall.appendAttribute(new Xml("uuid", Utils.getUUID()));
			m_bmr.appendAtomRunResult(autoUninstall);
		}

		// Iterate through each one identifying if any of those executed
		// atoms have a list of uninstall atoms to run on failure
		for (i = 0; i < results.size(); i++)
		{	// Grab this entry
			result = results.get(i);
			manifestworkletuuid = result.getAttribute("manifestworkletuuid");
			if (!manifestworkletuuid.isEmpty())
			{	// Search in the original run data to see what it is
				atomCandidate = m_manifest.getNodeByUUID(manifestworkletuuid, true);
				if (atomCandidate != null)
				{	// We found our worklet
					if (atomCandidate.getName().equalsIgnoreCase("abstract") && atomCandidate.getChildNode("options") != null)
					{	// And it's an atom, see if it has a populated atomOnFailure tag
						atomsList = atomCandidate.getChildNode("options").getAttributeOrChild("atomOnFailure");
						if (!atomsList.isEmpty())
						{	// There are atoms to run, extract them and execute them one at a time
							atoms.clear();
							Utils.extractCommaItems(atoms, atomsList);
							failureNames	= "";
							successNames	= "";
							failures		= 0;
							for (j = 0; j < atoms.size(); j++)
							{	// Grab this atom name
								atomName = atoms.get(j);
								// See if the atom is found in scripts.xml (as it may not have been included in the manifest.xml file or run data, but may exist externally as a "only-on-failure" cleanup state)
								executed		= false;
								ignoreThisOne	= false;
								for (k = 0; k < scriptsAtoms.size(); k++)
								{	// See if this scripts.xml atom matches
									thisAtom = scriptsAtoms.get(k);
									if (thisAtom.getAttribute("name").equalsIgnoreCase(atomName))
									{	// We found it, see if we still need to execute it
										autoUninstalledAlready = atomCandidate.getAttribute("autoUninstall");
										if (autoUninstalledAlready.isEmpty() || !autoUninstalledAlready.toLowerCase().contains(atomName.toLowerCase()))
										{	// It hasn't already been run, or if it has, it failed previously, so we run it (or try again)
											executed	= true;
											success		= new Xml("success");
											failure		= new Xml("failure");
											// If success, executed = true, otherwise executed = false

										//////////
										// Physically conduct the work of the atom
											previousDebuggerOrHUDAction = m_bp.m_debuggerOrHUDAction;	// Save the state of the thing
											m_bp.m_debuggerOrHUDAction = BenchmarkParams._NO_ACTION;
											System.out.println("Auto-uninstall: " + atomName);
											m_bpa.processAbstract_Atom(thisAtom.getChildNode("abstract"), success, failure);
											if (m_bp.m_debuggerOrHUDAction != BenchmarkParams._STOP_USER_CLICKED_STOP)
												m_bp.m_debuggerOrHUDAction = previousDebuggerOrHUDAction;	// Return the list of things we've executed
										// End
										//////////
											thisAtomResults = new Xml("atomUninstall");
											thisAtomResults.appendAttribute(new Xml("name", atomName));
											thisAtomResults.appendAttribute(new Xml("uuid", Utils.getUUID()));

											// Create an entry identifying this atom
											thisAtomResults.appendChild(success);
											thisAtomResults.appendChild(failure);
											if (success.getFirstChild() != null)
											{	// There was a success
												thisAtomResults.appendAttribute("result", "success");
											} else {
												// Only failures
												thisAtomResults.appendAttribute("result", "fail");
											}

											// Store the results good or bad
											autoUninstall.appendChild(thisAtomResults);

											// Save this result
											if (!Opbm.m_debugSimulateRunAtomMode)
												saveManifest();		// Save this result when not simulating a run in debug moe

										} else {
											if (autoUninstalledAlready.toLowerCase().contains(atomName.toLowerCase()))
											{	// It's already been run
												System.out.println("Auto-Uninstall " + atomName + " (indicated by \"" + atomCandidate.getAttribute("name") + "\")already run previously, skipping");
												ignoreThisOne = true;
											}
											//else it
										}
										// Update that it's been executed
										break;
									}
								}
								if (!ignoreThisOne)
								{	// Items are ignored if they've already been run
									if (!executed)
									{	// We encountered an atomOnFailure that does not exist
										++failures;
										failureNames += (failureNames.isEmpty() ? "" : ", ") + atomName;
										setError("Warning:  atomOnFailure \"" + atomName + "\" (indicated by \"" + atomCandidate.getAttribute("name") + "\") was not found in scripts.xml");
									} else {
										// Success
										successNames += (successNames.isEmpty() ? "" : ", ") + atomName;
									}
								}
								// If the user clicked stop, we're done
								if (m_bp.m_debuggerOrHUDAction >= BenchmarkParams._STOP)
									break;
							}
							// When we get here, we've tried to run every atom
							if (!failureNames.isEmpty())
							{	// Append list of failed atoms that could not be run (by names)
								atomCandidate.appendAttribute(new Xml("autoUninstallFailure", failureNames));
							}

							if (!successNames.isEmpty())
							{	// Append list of successful atoms
								atomCandidate.appendAttribute(new Xml("autoUninstalled", successNames));
							}
							// If the user clicked stop, we're done
							if (m_bp.m_debuggerOrHUDAction >= BenchmarkParams._STOP)
							{	// We're done, clean up before leaving
								if (!Opbm.m_debugSimulateRunAtomMode)
									saveManifest();		// Save this result when not simulating a run in debug moe
								return;
							}
						}
					}
				}
			}
		}
		if (!Opbm.m_debugSimulateRunAtomMode)
			saveManifest();		// Save this result when not simulating a run in debug moe
	}

	/**
	 * runs the specified tags for all atom items in scripts.xml
	 * @param tagName name of the tag to execute
	 * @param recordConflictsAndResolutions should conflicts and resolutions
	 * be recorded for all atoms?
	 */
	public Xml runAtomTags(String		tagName,
						   boolean		recordConflictsAndResolutions)
	{
		int i, j, k, previousDebuggerOrHUDAction;
		boolean executed;
		Xml candidate, thisAtom, autoExecute, success, failure, thisAtomResults, options, tag;
		String atomsList, atomName, warning;
		List<String>	atoms				= new ArrayList<String>(0);		// atom names found in <tagName> tag
		List<Xml>		scriptsAtoms		= new ArrayList<Xml>(0);		// atoms from scripts.xml
		List<Xml>		manifestAbstracts	= new ArrayList<Xml>(0);		// abstracts from manifest.xml, those that are actively being run

		// Load all of the atoms present in scripts.xml
		Xml.getNodeList(scriptsAtoms, m_opbm.getScriptsXml(), "opbm.scriptdata.atoms.atom", false);

		// Load all of the abstracts present in the manifest
		loadManifestAtoms(manifestAbstracts);

		autoExecute = new Xml("autoExecute");
		autoExecute.appendAttribute(new Xml("time", Utils.getTimestamp()));
		autoExecute.appendAttribute(new Xml("uuid", Utils.getUUID()));

		// Iterate through each one identifying if any of those executed
		// atoms have a list of uninstall atoms to run on failure
		for (i = 0; i < manifestAbstracts.size(); i++)
		{	// Grab this entry
			candidate	= manifestAbstracts.get(i);
			if (candidate != null)
			{	// The candidate is an abstract
				options = candidate.getChildNode("options");
				if (options != null)
				{	// Grab its tag for this run
					tag = options.getChildNode(tagName);
					if (tag != null)
					{	// We found it, see if there are any options there
						atomsList = tag.getText();
						if (!atomsList.isEmpty())
						{	// There are atoms to run, extract them and execute them one at a time
							atoms.clear();
							Utils.extractCommaItems(atoms, atomsList);
							for (j = 0; j < atoms.size(); j++)
							{	// Grab this atom name
								atomName = atoms.get(j);
								// See if the atom is found in scripts.xml (as it may not have been included in the manifest.xml file or run data, but may exist externally as a "only-on-failure" cleanup state)
								executed = false;
								for (k = 0; k < scriptsAtoms.size(); k++)
								{	// See if this scripts.xml atom matches
									thisAtom = scriptsAtoms.get(k);
									if (thisAtom.getChildNode("abstract") != null && thisAtom.getAttribute("name").equalsIgnoreCase(atomName))
									{	// We found it, see if we still need to execute it
										executed = true;
										success	= new Xml("success");
										failure	= new Xml("failure");
										// If success, executed = true, otherwise executed = false

									//////////
									// Physically conduct the work of the atom
										previousDebuggerOrHUDAction = m_bp.m_debuggerOrHUDAction;	// Save the state of the thing
										m_bp.m_debuggerOrHUDAction = BenchmarkParams._NO_ACTION;
										System.out.println(Utils.translateScriptsAbstractOptionsTagName(tagName) + ": " + atomName);
										m_bpa.processAbstract_Atom(thisAtom.getChildNode("abstract"), success, failure);
										if (m_bp.m_debuggerOrHUDAction != BenchmarkParams._STOP_USER_CLICKED_STOP)
											m_bp.m_debuggerOrHUDAction = previousDebuggerOrHUDAction;	// Return the list of things we've executed
									// End
									//////////

										if (recordConflictsAndResolutions)
										{	// Grab the conflicts and resolutions array
											m_conflicts.addAll(m_bp.m_conflicts);
											m_resolutions.addAll(m_bp.m_resolutions);
										}

										thisAtomResults = new Xml(tagName);
										thisAtomResults.appendAttribute(new Xml("name", atomName));
										thisAtomResults.appendAttribute(new Xml("uuid", Utils.getUUID()));

										// Create an entry identifying this atom
										thisAtomResults.appendChild(success);
										thisAtomResults.appendChild(failure);
										if (success.getFirstChild() != null)
										{	// There was a success
											thisAtomResults.appendAttribute("result", "success");
										} else {
											// Only failures
											thisAtomResults.appendAttribute("result", "fail");
										}

										// Store the results good or bad
										autoExecute.appendChild(thisAtomResults);
										break;
									}
								}
								if (!executed)
								{	// This specified atom wasn't found
									warning = "Warning: The specified atom named \"" + atomName + "\" (indicated by a " + Utils.translateScriptsAbstractOptionsTagName(tagName) + " on \"" + candidate.getAttribute("name") + "\") was not found, ignoring.";
									System.out.println(warning);
									autoExecute.appendChild(new Xml("warning", warning));
								}
								// If the user clicked stop, we're done
								if (m_bp.m_debuggerOrHUDAction >= BenchmarkParams._STOP)
									return(autoExecute);
							}
						}
					}
				}
			}
		}
		return(autoExecute);
	}

	/**
	 * Runs all atoms starting with the prefix text, as in "spinup" to run
	 * all spinup atoms at start.
	 * @param prefix text atom name must begin with
	 * @return
	 */
	public Xml runAtomsStartingWithThisPrefix(String prefix)
	{
		// REMEMBER and then update the above code to use only those atoms specified in the manifest
		int i, previousDebuggerOrHUDAction;
		Xml thisAtom, candidate, autoExecute, success, failure, thisAtomResults, options;
		String name;
		List<Xml> scriptsAtoms = new ArrayList<Xml>(0);	// atoms from scripts.xml

		// Load all of the scripts.xml atoms
		Xml.getNodeList(scriptsAtoms, m_opbm.getScriptsXml(), "opbm.scriptdata.atoms.atom", false);

		autoExecute = new Xml("autoExecute");
		autoExecute.appendAttribute(new Xml("time", Utils.getTimestamp()));
		autoExecute.appendAttribute(new Xml("uuid", Utils.getUUID()));

		// Iterate through each one identifying if any of those executed
		// atoms have a list of uninstall atoms to run on failure
		for (i = 0; i < scriptsAtoms.size(); i++)
		{	// Grab this entry
			thisAtom	= scriptsAtoms.get(i);
			candidate	= thisAtom.getChildNode("abstract");
			if (candidate != null)
			{	// The candidate is an abstract
				name = candidate.getAttribute("name");
				if (name != null && name.toLowerCase().startsWith(prefix))
				{	// And its name begins with our prefix
					success	= new Xml("success");
					failure	= new Xml("failure");
					// If success, executed = true, otherwise executed = false

				//////////
				// Physically conduct the work of the atom
					previousDebuggerOrHUDAction = m_bp.m_debuggerOrHUDAction;	// Save the state of the thing
					m_bp.m_debuggerOrHUDAction = BenchmarkParams._NO_ACTION;
					System.out.println("Running: " + name);
					m_bpa.processAbstract_Atom(candidate, success, failure);
					if (m_bp.m_debuggerOrHUDAction != BenchmarkParams._STOP_USER_CLICKED_STOP)
						m_bp.m_debuggerOrHUDAction = previousDebuggerOrHUDAction;	// Return the list of things we've executed
				// End
				//////////
					thisAtomResults = new Xml(prefix.trim());
					thisAtomResults.appendAttribute(new Xml("name", name));
					thisAtomResults.appendAttribute(new Xml("uuid", Utils.getUUID()));

					// Create an entry identifying this atom
					thisAtomResults.appendChild(success);
					thisAtomResults.appendChild(failure);
					if (success.getFirstChild() != null)
					{	// There was a success
						thisAtomResults.appendAttribute("result", "success");
					} else {
						// Only failures
						thisAtomResults.appendAttribute("result", "fail");
					}

					// Store the results good or bad
					autoExecute.appendChild(thisAtomResults);

					// If the user clicked stop, we're done
					if (m_bp.m_debuggerOrHUDAction >= BenchmarkParams._STOP)
						return(autoExecute);
				}
			}
		}
		// Return the list of things we've executed
		return(autoExecute);
	}

	/**
	 * Process through all m_conflicts and m_resolution entries, creating an
	 * Xml data list for display in OpbmColumns
	 * @return the created Xml used for input to OpbmColumns
	 */
	public Xml createXmlOfConflictsAndResolutions()
	{
		int i;
		Xml car, item, conflict, resolution;

		car = new Xml("data");
		if (!m_conflicts.isEmpty())
		{
			for (i = 0; i < m_conflicts.size(); i++)
			{
				item		= new Xml("item");
				car.appendChild(item);

				conflict	= new Xml("conflict",		m_conflicts.get(i));
				resolution	= new Xml("resolution",		(i <= m_resolutions.size()) ? m_resolutions.get(i) : "Error in conflict/resolution listing");

				item.appendChild(conflict);
				item.appendChild(resolution);
			}
		}
		m_conflicts.clear();
		m_resolutions.clear();
		return(car);
	}

	/**
	 * Populates the specified list with a unique list of all atoms in the
	 * manifest.xml file
	 * @param list list to update
	 */
	public void loadManifestAtoms(List<Xml> list)
	{
		int i;
		boolean found;
		Xml run, candidate;

		run = m_manifest.getFirstChild();
		while (run != null)
		{	// See if this run has any abstract entries
			if (run.getName().equalsIgnoreCase("run"))
			{
				candidate = run.getFirstChild();
				while (candidate != null)
				{
					if (candidate.getName().equalsIgnoreCase("abstract"))
					{	// We found one
						// See if this atom is already in the list
						found = false;
						for (i = 0; i < list.size(); i++)
						{
							if (list.get(i).getAttribute("name").equalsIgnoreCase(candidate.getAttribute("name")))
							{	// We found a match
								found = true;
								break;
							}
						}
						if (!found)
							list.add(candidate);
					}

					// Move to next sibling
					candidate = candidate.getNext();
				}
			}

			// Move to next run
			run = run.getNext();
		}
		// When we get here, we have a unique list of all atoms in this run
	}

	/**
	 * Computes the aggregate totals based on the details and rawResults in
	 * the manifest.  Can be called when the manifest is in a virgin state, or
	 * repeatedly if there are improvements made to the scoring algorithms, or
	 * if it is desirable to see a previously saved manifest.xml's capture again.
	 * @param retireOldAggregateData should any existing data be retired?  If no, then
	 * it is deleted and the new data supplants it
	 * @param saveManifest should manifest.xml be saved when finished?
	 */
	public void computeForResultsXml(boolean	retireOldData,
									 boolean	saveManifest)
	{
		// The following code is called to initially compute, or to recompute
		// the results, as such these algorithms look for previously computed
		// data and migrate it to a retired state before creating new spaces
		// for the newly created data.

		// Go ahead and complete any incomplete entries in the run
		m_bmr.completeAnyIncompleteAbstractTimings(saveManifest);

		// Create the results.xml output from it
		m_bmr.computeAggregateTotals(retireOldData);
		// Compute the ResultsViewer totals and generate a CSV file with all of the data
		m_bmr.computeResultsViewerTotalsAndGenerateCSVFile();

		if (saveManifest)
			saveManifest();	// Save the new results
	}

	/**
	 * Returns the root node of the <manifest> tag
	 * @returns <code>Xml</code> to manifest node
	 */
	public Xml getManifestRoot()
	{
		return(m_manifest);
	}

	/**
	 * Updates the time for the script ending
	 * @param t time to use for the update
	 */
	public void setStatisticsRuntimeEnded(String t)
	{
		m_statisticsRuntimeEnded.setText(t);
	}

	/**
	 * Indicates whether or not the compilation is empty.  Used for testing
	 * whether or not a buildCompilation() should be called after command-line
	 * processing
	 * @returns true if there are entries compiled ready to attempt for running
	 */
	public boolean isCompilationEmpty()
	{
		return(m_compilation.isEmpty());
	}

	/**
	 * Is this an official run that's running?
	 * opbm.benchmarks.control.run.#type = "official run"
	 */
	public boolean isAnOfficialRun()
	{
		String runtype = m_controlRun.getAttribute("type");

		if (runtype != null)
		{
			if (runtype.toLowerCase().startsWith("official"))
			{	// It is an official run
				return(true);
			}
		}
		return(false);
	}

	/**
	 * Is this a trial run that's running?
	 * opbm.benchmarks.control.run.#type = "trial run"
	 */
	public boolean isATrialRun()
	{
		String runtype = m_controlRun.getAttribute("type");

		if (runtype != null)
		{
			if (runtype.toLowerCase().startsWith("trial"))
			{	// It is a trial run
				return(true);
			}
		}
		return(false);
	}

	/**
	 * Has the run started yet?
	 * opbm.benchmarks.control.last.worklet.#uuid is empty
	 */
	public boolean runHasNotYetStarted()
	{
		String uuid = m_controlLastWorkletUuid.getText();

		if (uuid == null || uuid.isEmpty())
		{	// It has not started yet
			return(true);
		}
		return(false);
	}

	/**
	 * Is the run completely over now?
	 * opbm.benchmarks.control.last.worklet.#uuid is "finished"
	 */
	public boolean runIsNowCompletelyOver()
	{
		String uuid = m_controlLastWorkletUuid.getText();

		if (uuid != null && uuid.equalsIgnoreCase("finished"))
		{	// It is over, the benchmark is completed
			return(true);
		}
		return(false);
	}

	/**
	 * Reads the control section on "automatedRun" and returns yes or no based
	 * on its value
	 * @return yes or no based on opbm.benchmarks.control.run.#automated
	 */
	public boolean didOriginallyLaunchFromCommandLine()
	{
		Xml node = m_controlLastRun.getAttributeNode("automatedRun");
		if (node != null)
		{
			if (Utils.interpretBooleanAsYesNo(node.getText(), false).equalsIgnoreCase("yes"))
			{	// It is an automated run
				return(true);
			}
		}
		return(false);
	}

	/**
	 * Sets the currently executing worklet as finished
	 */
	public void setLastWorkletFinished()
	{
		m_controlLastWorkletFinished.setText("yes");
	}

	/**
	 * Sets the currently executing worklet as not finished
	 */
	public void setLastWorkletNotFinished()
	{
		m_controlLastWorkletFinished.setText("no");
	}

	// Error conditions
	private boolean						m_isManifestInError;
	private String						m_error;
	private int							m_passThis;
	private int							m_passMax;

	// When checking for conflicts, grab the conflicts and resolutions reported by the scripts
	private	List<String>				m_conflicts;
	private	List<String>				m_resolutions;

	// Change as the levels are traversed during add operations
	private String						m_suiteName;
	private String						m_scenarioName;
	private String						m_moleculeName;
	private String						m_atomName;

	// Compilation variables
	private Tuple						m_compilation;

	// Class member variables
	private Opbm						m_opbm;
	private Benchmarks					m_benchmarksMaster;
	private Macros						m_macroMaster;
	private BenchmarkParams				m_bp;
	private BenchmarksAtom				m_bpa;
	private String						m_type;
	private String						m_name;
	private BenchmarkManifestResults	m_bmr;
	private boolean						m_processing;			// Used in all of the run*() methods
	private boolean						m_automated;			// Set by the launcher, was this an automated run or not?
	private boolean						m_rebootRequired;		// Set by the builder, is a reboot required during this run?

	// Root-level Xml entries
	private boolean						m_manifestIsLoaded;
	private	Xml							m_root;
	private Xml							m_benchmarks;
	private Xml							m_manifest;
	private Xml							m_control;
	private Xml							m_statistics;
	private Xml							m_settings;
	private Xml							m_discovery;

	// Run passes
	private Tuple						m_runPasses;

	// Control data
	private Xml							m_controlRun;
	private Xml							m_controlLastRun;
	private Xml							m_controlLastTag;
	private Xml							m_controlLastResult;
	private Xml							m_controlLastAnnotation;
	private Xml							m_controlLastWorklet;
	private Xml							m_controlLastRunUuid;
	private Xml							m_controlLastTagUuid;
	private Xml							m_controlLastWorkletUuid;
	private Xml							m_controlLastWorkletFinished;
	private Xml							m_controlLastResultUuid;
	private Xml							m_controlLastAnnotationUuid;
	private	Xml							m_run;		// Updated each pass through runMoveToNext()
	private Xml							m_tag;		// Updated each pass through runMoveToNext()
	private Xml							m_worklet;	// Updated each pass through runMoveToNext()

	// Statistics data
	private Xml							m_statisticsRuntime;
	private Xml							m_statisticsRuntimeBegan;
	private Xml							m_statisticsRuntimeEnded;
	private Xml							m_statisticsRuntimeHarness;
	private Xml							m_statisticsRuntimeScripts;
	private Xml							m_statisticsSuccesses;
	private Xml							m_statisticsFailures;
	private Xml							m_statisticsRetries;
// Note:  The resultsdata portions are handled by BenchmarkManifestResults
//        See createResultsdataFramework().
}
