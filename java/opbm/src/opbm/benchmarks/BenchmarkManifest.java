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
 *			BenchmarkManifest bm = new BenchmarkManifest(m_opbm, "trial");
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
 * Last Updated:  Aug 23, 2011
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

import java.util.ArrayList;
import java.util.List;
import opbm.Opbm;
import opbm.common.Tuple;
import opbm.common.Xml;
import opbm.dialogs.OpbmDialog;

/**
 *
 * @author rick
 */
public final class BenchmarkManifest
{
	/**
	 * Constructor assigns the run-specific information, identifies the type
	 * of run, either "trial", "official" or "compilation", gives it a name, and
	 * specifies (if "compilation") the specific trial, official, suite, scenario,
	 * molecule or atom to run, otherwise runs the trial or official benchmark.
	 * @param type "trial", "official" or "compilation"
	 * @param name (optional) name given to the run
	 * @param suiteToRun suite(s) to run if manual (separated by commas), empty otherwise
	 * @param scenarioToRun scenario(s) to run if manual (separated by commas), empty otherwise
	 * @param moleculeToRun molecule(s) to run if manual (separated by commas), empty otherwise
	 * @param atomToRun atom(s) to run if manual (separated by commas), empty otherwise
	 */
	public BenchmarkManifest(Opbm		opbm,
							 String		type)
	{
		m_isManifestInError		= false;
		m_opbm					= opbm;
		m_type					= type;
		m_name					= m_opbm.getRunName();

		m_suiteName				= "";
		m_scenarioName			= "";
		m_moleculeName			= "";
		m_atomName				= "";

		m_passThis				= 0;
		m_passMax				= 0;

		m_compilation			= new Tuple(opbm);

		m_root					= null;			// of opbm in manifest.xml
		m_benchmarks			= null;			// of opbm.benchmarks in manifest.xml
		m_manifest				= null;			// of opbm.benchmarks.manifest in manifest.xml
		m_control				= null;			// of opbm.benchmarks.control in manifest.xml
		m_statistics			= null;			// of opbm.benchmarks.statistics in manifest.xml
		m_settings				= null;			// of opbm.benchmarks.settings in manifest.xml
		m_discovery				= null;			// of opbm.benchmarks.discovery in manifest.xml

		m_runPasses				= new Tuple(opbm);

		// Create the blank manifest template
		createManifest();
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
			m_isManifestInError = true;
			m_error				= "Run named '" + m_name + "' was not specified to be trial, official or compilation.  Cannot build.";
			result = false;
		}

		// If we're good, finalize the build settings
		if (result)
			buildFinalize();

		// Indicate success or failure to caller
		return(result);
	}

	/**
	 * Finalize everything in the build, set all max values, etc.
	 */
	public void buildFinalize()
	{
		setPassMaxValues();
		assignUUIDs();
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
			System.out.println("Error: Nothing has been compiled to build manifest");
			return(false);
		}

		// Try to add the things they've build
		count = 0;
		error = false;
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
					System.out.println(m_error);
					return(false);
				}

			} else if (type.equalsIgnoreCase("scenario")) {
				if (addScenarioByName(name, count) != 1)
				{	// The scenario wasn't found
					m_isManifestInError = true;
					m_error = "Fatal Error: Unable to add scenario named \"" + name + "\": does not exist";
					System.out.println(m_error);
					return(false);
				}

			} else if (type.equalsIgnoreCase("molecule")) {
				if (addMoleculeByName(name, count) != 1)
				{	// The molecule wasn't found
					m_isManifestInError = true;
					m_error = "Fatal Error: Unable to add molecule named \"" + name + "\": does not exist";
					System.out.println(m_error);
					return(false);
				}

			} else if (type.equalsIgnoreCase("atom")) {
				if (addAtomByName(name, count) != 1)
				{	// The atom wasn't found
					m_isManifestInError = true;
					m_error = "Fatal Error: Unable to add atom named \"" + name + "\": does not exist";
					System.out.println(m_error);
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

		// Grab every suite by its name
		Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.suites", false);

		// For each returned entry, add it to the list for the specified number of passes
		m_passMax = passes;
		count = 0;
		for (i = 0; i < passes; i++)
		{
			m_passThis = i + 1;
			for (j = 0; j < nodes.size(); j++)
			{	// Add each suite
				count += addSuite(nodes.get(j));
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

		Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.suites", false);
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
					++count;
				}
				// When we get here, indicate how many were added
				return(count);
			}
		}
		// If we get here, the named suite wasn't found
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

		Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.scenarios", false);
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
					++count;
				}
				// When we get here, indicate how many were added
				return(count);
			}
		}
		// If we get here, the named scenario wasn't found
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

		Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.molecules", false);
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
					++count;
				}
				// When we get here, indicate how many were added
				return(count);
			}
		}
		// If we get here, the named suite wasn't found
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

		Xml.getNodeList(nodes, m_opbm.getScriptsXml(), "opbm.scriptdata.atoms", false);
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
					++count;
				}
				// When we get here, indicate how many were added
				return(count);
			}
		}
		// If we get here, the named suite wasn't found
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
		Xml.getNodeList(nodes, suite, "[flow,abstract,scenario,molecule,atom]", true);

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
			element = nodes.get(i);
			type = element.getName();
			if (type.equalsIgnoreCase("flow"))
			{	// It's a flow-control directive, add it
				addElement(element);

			} else if (type.equalsIgnoreCase("abstract")) {
				addElement(element);
				++count;

			} else if (type.equalsIgnoreCase("scenario")) {
				count += addScenario(element);

			} else if (type.equalsIgnoreCase("molecule")) {
				count += addMolecule(element);

			} else if (type.equalsIgnoreCase("atom")) {
				count += addAtom(element);

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
		Xml.getNodeList(nodes, scenario, "[flow,abstract,molecule,atom]", true);

		if (nodes.isEmpty())
		{	// Nothing to do, no nodes for this
			m_isManifestInError = true;
			m_error = "Empty node list for " + scenario.getAttribute("name");
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
				addElement(element);

			} else if (type.equalsIgnoreCase("abstract")) {
				addElement(element);
				++count;

			} else if (type.equalsIgnoreCase("molecule")) {
				count += addMolecule(element);

			} else if (type.equalsIgnoreCase("atom")) {
				count += addAtom(element);

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
		Xml.getNodeList(nodes, molecule, "[flow,abstract,atom]", true);

		if (nodes.isEmpty())
		{	// Nothing to do, no nodes for this
			m_isManifestInError = true;
			m_error = "Empty node list for " + molecule.getAttribute("name");
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
				addElement(element);

			} else if (type.equalsIgnoreCase("abstract")) {
				addElement(element);
				++count;

			} else if (type.equalsIgnoreCase("atom")) {
				count += addAtom(element);

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
		Xml.getNodeList(nodes, atom, "[flow,abstract]", true);

		if (nodes.isEmpty())
		{	// Nothing to do, no nodes for this
			m_isManifestInError = true;
			m_error = "Empty node list for " + atom.getAttribute("name");
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
				addElement(element);

			} else if (type.equalsIgnoreCase("abstract")) {
				addElement(element);
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
	 * Adds a flow control directive for the specified element
	 * @param element
	 */
	public void addElement(Xml element)
	{
		String level;
		Xml flow, run;

		// Grab the current level
		level = getLevel();

		// Duplicate the source flow control directive
		flow = element.cloneNode(true);

		// Tag on the level for this entry
		flow.appendAttribute("level", level);

		// Grab the current run pass we're appending to
		run = getRunForThisPass();

		// add the flow control directive to this run's pass
		run.appendChild(flow);
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
			if (i == m_passThis)
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
			if (m_runPasses.size() < i)
			{	// Create and add this one
				number	= Integer.toString(i);
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
		Xml last, settings;

		m_root						= new Xml("opbm");
		m_benchmarks				= new Xml("benchmarks");
		m_manifest					= new Xml("manifest");
		m_control					= new Xml("control");
		m_statistics				= new Xml("statistics");
		m_settings					= new Xml("settings");
		m_discovery					= new Xml("discovery");

		m_root.appendChild(m_benchmarks);				// opbm.benchmarks
		m_benchmarks.appendChild(m_manifest);			// opbm.benchmarks.manifest
		m_benchmarks.appendChild(m_control);			// opbm.benchmarks.control
		m_benchmarks.appendChild(m_statistics);			// opbm.benchmarks.statistics
		m_benchmarks.appendChild(m_settings);			// opbm.benchmarks.settings
		m_benchmarks.appendChild(m_discovery);			// opbm.benchmarks.discovery

		// Manifest data will be added based on the variables of the specific run

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
		m_controlRun				= new Xml("run");
		last						= new Xml("last");
		m_controlLastRun			= new Xml("run");
		m_controlLastTag			= new Xml("tag");
		m_controlLastWorklet		= new Xml("worklet");
		m_controlLastRunUuid		= new Xml("uuid");
		m_controlLastTagUuid		= new Xml("uuid");
		m_controlLastWorkletUuid	= new Xml("uuid");

		m_control.appendChild(m_controlRun);
		m_control.appendChild(last);
		last.appendChild(m_controlLastRun);
		last.appendChild(m_controlLastTag);
		last.appendChild(m_controlLastWorklet);

		// Set the initial control values
		m_controlRun.appendAttribute("type", m_type);
		m_controlRun.appendAttribute("name", m_name);
		m_controlLastRunUuid.appendAttribute("uuid");
		m_controlLastTagUuid.appendAttribute("uuid");
		m_controlLastWorkletUuid.appendAttribute("uuid");

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

		// Append in user settings at time of manifest creation
		settings = m_opbm.getSettingsMaster().getSettingsXml("opbm.settings.benchmarks").cloneNodeTree(true);
		settings.setName("settings");
		m_benchmarks.appendChild(settings);

		// Append discovery info
// REMEMBER for CPU-Z CPUZ stuff
	}

	/**
	 * Runs the manifest, records all data
	 */
	public void run()
	{
		OpbmDialog od;

		if (m_isManifestInError)
		{	// We cannot run this manifest because it's in error
			od = new OpbmDialog(m_opbm, m_error, "Run Error", OpbmDialog._OKAY_BUTTON, "", "");

		} else {
			// We're good, get ready to run it
			od = new OpbmDialog(m_opbm, "Manifest Run would begin", "Ready", OpbmDialog._OKAY_BUTTON, "", "");
		}
	}

	// Error conditions
	private boolean		m_isManifestInError;
	private String		m_error;
	private int			m_passThis;
	private int			m_passMax;

	// Change as the levels are traversed during add operations
	private String		m_suiteName;
	private String		m_scenarioName;
	private String		m_moleculeName;
	private String		m_atomName;

	// Compilation variables
	private Tuple		m_compilation;

	// Class member variables
	private Opbm		m_opbm;
	private String		m_type;
	private String		m_name;

	private	Xml			m_root;
	private Xml			m_benchmarks;
	private Xml			m_manifest;
	private Xml			m_control;
	private Xml			m_statistics;
	private Xml			m_settings;
	private Xml			m_discovery;

	// Run passes
	private Tuple		m_runPasses;

	// Control data
	private Xml			m_controlRun;
	private Xml			m_controlLastRun;
	private Xml			m_controlLastTag;
	private Xml			m_controlLastWorklet;
	private Xml			m_controlLastRunUuid;
	private Xml			m_controlLastTagUuid;
	private Xml			m_controlLastWorkletUuid;

	// Statistics data
	private Xml			m_statisticsRuntime;
	private Xml			m_statisticsRuntimeBegan;
	private Xml			m_statisticsRuntimeEnded;
	private Xml			m_statisticsRuntimeHarness;
	private Xml			m_statisticsRuntimeScripts;
	private Xml			m_statisticsSuccesses;
	private Xml			m_statisticsFailures;
	private Xml			m_statisticsRetries;
}
