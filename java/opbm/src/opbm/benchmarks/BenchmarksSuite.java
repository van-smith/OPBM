/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for benchmarking.  It executes scripts,
 * shows the heads-up display, displays the single-step debugger, etc.
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

package opbm.benchmarks;

import opbm.common.Xml;

public class BenchmarksSuite
{
	public BenchmarksSuite(BenchmarksParams bp)
	{
		// Store our benchmark parameters
		m_bp		= bp;
	}

	public Xml processCommand(Xml	thisCommand,
							  Xml	suite,
							  Xml	xmlRunAppendTo)
	{
//////////
// FLOW
		if (thisCommand.getName().equalsIgnoreCase("flow")) {
			// It's a flow-control directive
			return(m_bp.m_bpAtom.processFlow_Atom(thisCommand, suite, xmlRunAppendTo));


//////////
// CUSTOM
//		} else if (thisCommand.getName().equalsIgnoreCase("custom")) {
//			// It's a custom bit of code
//			return(processCustom_Atom(thisCommand, atom));


//////////
// ABSTRACT
		} else if (thisCommand.getName().equalsIgnoreCase("abstract")) {
			// It's an abstract command
			return(m_bp.m_bpAtom.processAbstract_Atom(thisCommand, suite, xmlRunAppendTo));


//////////
// ATOM
		} else if (thisCommand.getName().equalsIgnoreCase("atom")) {
			// It's an atom
			return(processSuite_Atom(thisCommand, suite, xmlRunAppendTo));


//////////
// MOLECULE
		} else if (thisCommand.getName().equalsIgnoreCase("molecule")) {
			// It's a molecule
			return(processSuite_Molecule(thisCommand, suite, xmlRunAppendTo));


//////////
// SCENARIO
		} else if (thisCommand.getName().equalsIgnoreCase("scenario")) {
			// It's a scenario
			return(processSuite_Scenario(thisCommand, suite, xmlRunAppendTo));

		}
		// If we get here, we found something invalid.
		// Ignore it and its invalidity, but log the entry for future reference
		xmlRunAppendTo.appendChild(new Xml("error", thisCommand.getName(), "type", "Unrecognized keyword for molecule, expected flow, custom, abstract or atom"));
		return(thisCommand.getNext());
	}

	/**
	 * Processes the atoms within this molecule
	 * @param thisCommand command to process
	 * @param molecule parent molecule source of thisCommand
	 * @param xmlRunAppendTo xml to append tags to for this portion of the run
	 * @return
	 */
	public Xml processSuite_Atom(Xml		thisCommand,
								 Xml		atom,
								 Xml		xmlRunAppendTo)
	{
		return(null);
	}

	/**
	 * Processes the atoms within this molecule
	 * @param thisCommand command to process
	 * @param molecule parent molecule source of thisCommand
	 * @param xmlRunAppendTo xml to append tags to for this portion of the run
	 * @return
	 */
	public Xml processSuite_Molecule(Xml		thisCommand,
									 Xml		molecule,
									 Xml		xmlRunAppendTo)
	{
		return(null);
	}

	/**
	 * Processes the atoms within this molecule
	 * @param thisCommand command to process
	 * @param scenario parent scenario source of thisCommand
	 * @param xmlRunAppendTo xml to append tags to for this portion of the run
	 * @return
	 */
	public Xml processSuite_Scenario(Xml		thisCommand,
									 Xml		scenario,
									 Xml		xmlRunAppendTo)
	{
		return(null);
	}

	private BenchmarksParams	m_bp;
}
