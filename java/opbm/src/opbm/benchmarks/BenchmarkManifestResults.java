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
 *		<resultsdata>
 *			<rawresults>
 *				<result manifestworkletuuid="whatever" start="Aug 22, 2011 at 11:56am" end="Aug 22, 2011 at 11:59am" tested="yes" status="success" score="99" uuid="whatever"/>
 *				<annotation manifestworkletuuid="whatever" rebootBegan="Tue Aug 16 16:39:51 CDT 2011 1313530812950" rebootEnded="Tue Aug 16 16:39:51 CDT 2011 1313530812950" delta="" uuid="whatever"/>
 *			</rawresults>
 *			<results>
 *				<result datetime="Aug 25, 2011 at 10:56AM" name="OPBM Benchmark" shortname="unnamed" tags="" tested="yes" status="success" score="0">
 *					<suite name="Suite Name" shortname="sun" tags="" tested="yes" status="success" score="0">
 *						<scenario name="Scenario Name" shortname="scn" tags="" tested="yes" status="success" score="0">
 *							<molecule name="Molecule Name" shortname="mon" tags="" tested="yes" status="success" score="0">
 *								<atom shortname="Atom Name" shortnamej="atn" tags="" tested="yes" score="0" name="Un-install Acrobat Reader" status="success">
 *									<worklet name="Launch Adobe Acrobat 10.1 Installer" instances="1" score="74.9624054858326" time="11.135136809047" shortname="LAA101I" minTime="11.135136809047" maxTime="11.135136809047" avgTime="11.135136809047" geoTime="11.135136809047" cvTime="0.0" minScore="74.9624054858326" maxScore="74.9624054858326" avgScore="74.9624054858326" geoScore="74.9624054858326" cvScore="0.0" tags="" tested="yes" status="success">
 *										<run1 time="11.1351368010471" score="74.9624054858326"></run1>
 *										<run2 time="10.8131363098733" score="78.2190387358326"></run2>
 *										<run3 time="12.3981368380873" score="70.3987194858326"></run3>
 *									</worklet>
 *									<worklet name="Install Adobe Acrobat 10.1" instances="1" score="51.2759799444718" time="56.5024453956312" shortname="IAA101" minTime="56.5024453956312" maxTime="56.5024453956312" avgTime="56.5024453956312" geoTime="56.5024453956312" cvTime="0.0" minScore="51.2759799444718" maxScore="51.2759799444718" avgScore="51.2759799444718" geoScore="51.2759799444718" cvScore="0.0" tags="" tested="yes" status="success">
 *										<run1 time="61.1351368010475" score="54.9624054858326"></run1>
 *										<run2 time="50.8131368090479" score="58.2190387358326"></run2>
 *										<run3 time="56.5024453956312" score="51.2759799444718"></run3>
 *									</worklet>
 *								</atom>
 *							</molecule>
 *						</scenario>
 *					</suite>
 *				</result>
 *			</results>
 *			<details>
 *				<detail manifestworkletuuid="whatever" retry="1" attempts="5">
 *					<output>
 *					</output>
 *				</detail>
 *			</details>
 *			<aggregate>
 *				<byAtom>
 *					<atomuuid count="1" timeAverage="01:01" timeMean="01:01" timeMin="01:01" timeMax="01:01" scoreAverage="0" scoreMean="0" scoreMin="0" scoreMax="0" scoreCV="0">whatever</atomuuid>
 *				</byAtom>
 *			</aggregate>
 *		</resultsdata>
 *
 * Last Updated:  Nov 01, 2011
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
import java.util.Date;
import java.util.List;
import opbm.Opbm;
import opbm.common.Tuple;
import opbm.common.Utils;
import opbm.common.Xml;
import opbm.dialogs.OpbmDialog;

public class BenchmarkManifestResults
{
	/**
	 * Constructor, this class is used for storing and querying results data
	 * by BenchmarkManifest and the Results Viewer
	 */
	public BenchmarkManifestResults(Opbm				opbm,
									BenchmarkManifest	bm)
	{
		m_bm		= bm;
		m_opbm		= opbm;
		m_isLoaded	= false;
	}

	/**
	 * Adds an additional piece of information to the last stored result tag
	 * @param attribute name of the attribute to append to the last result
	 * @param data data to store for that attribute
	 * @param manifestWorkletUuid
	 */
	public void appendToLastResult(String	attribute,
								   String	data,
								   String	manifestWorkletUuid)
	{
		Xml candidate, lastResult;

		if (m_isLoaded)
		{
			candidate	= m_rawResults.getFirstChild();
			lastResult	= null;
			while (candidate != null)
			{
				if (candidate.getName().equalsIgnoreCase("result"))
					lastResult = candidate;

				// Move to next entry
				candidate = candidate.getNext();
			}
			if (lastResult == null)
			{	// Add a new result line
				appendResultsResult(attribute, data, manifestWorkletUuid);

			} else {
				if (lastResult.getAttribute("manifestworkletuuid").equalsIgnoreCase(manifestWorkletUuid))
				{	// Add the attribute and data to this entry
					lastResult.appendAttribute(new Xml(attribute, data));
				} else {
					// The last entry does not match the specified uuid, so add a new entry
					appendResultsResult(attribute, data, manifestWorkletUuid);
				}
			}
		}
	}

	/**
	 * Appends data to the existing result element
	 * @param attribute
	 * @param data
	 * @param manifestWorkletUuid
	 */
	public void appendResultsResult(String		attribute,
									String		data,
									String		manifestWorkletUuid)
	{
		Xml result;

		if (m_isLoaded)
		{	// Add the new result line
			result = m_rawResults.appendChild(new Xml("result"));

			// Add the attribute and data to this entry
			result.appendAttribute(new Xml("manifestworkletuuid", manifestWorkletUuid));
			result.appendAttribute(new Xml(attribute, data));
		}
	}

	/**
	 * Appends to the last annotation in the log, does not create a new
	 * annotation entry, just another attribute on the existing one.  If not
	 * prior annotation entry exists, then it creates one
	 * @param attribute attribute tag name
	 * @param data data to store for attribute tag name
	 */
	public void appendToLastResultAnnotation(String		attribute,
											 String		data,
											 String		manifestWorkletUuid)
	{
		Xml candidate, lastAnnotation;

		if (m_isLoaded)
		{
			candidate		= m_rawResults.getFirstChild();
			lastAnnotation	= null;
			while (candidate != null)
			{
				if (candidate.getName().equalsIgnoreCase("annotation"))
					lastAnnotation = candidate;

				// Move to next entry
				candidate = candidate.getNext();
			}
			if (lastAnnotation == null)
			{	// Add a new annotation line
				// This should never occur, and would likely only occur if there is an error in the manifest.xml opbm.resultsdata section
				System.out.println("Warning:  Unable to add to requested previous opbm.resultsdata.results.annotation entry. Creating new.");
				appendResultsAnnotation(attribute, data, manifestWorkletUuid);

			} else {
				if (lastAnnotation.getAttribute("manifestworkletuuid").equalsIgnoreCase(manifestWorkletUuid))
				{	// Add the attribute and data to this entry
					lastAnnotation.appendAttribute(new Xml(attribute, data));
				} else {
					// The last entry does not match the specified uuid, so add a new entry
					appendResultsAnnotation(attribute, data, manifestWorkletUuid);
				}

			}
		}
	}

	/**
	 * Appends data to the existing annotation element
	 * @param attribute
	 * @param data
	 * @param manifestWorkletUuid
	 */
	public void appendResultsAnnotation(String		attribute,
										String		data,
										String		manifestWorkletUuid)
	{
		Xml annotation;

		if (m_isLoaded)
		{	// Add the new annotation line
			annotation = m_rawResults.appendChild(new Xml("annotation"));

			// Add the attribute and data to this entry
			annotation.appendAttribute(new Xml(attribute, data));

			// Add the related worklet uuid (if not empty)
			if (!manifestWorkletUuid.isEmpty())
				annotation.appendAttribute(new Xml("manifestworkletuuid", manifestWorkletUuid));
		}
	}

	/**
	 * Appends a result to the manifest based on the successful or failed
	 * completion of a worklet
	 * @param manifestWorkletUuid unique ID to the run worklet which spawned it
	 * @param manifestAtomUuid unique ID to the atom being worked
	 * @param startTime start time of the worklet (just before launch)
	 * @param endTime end time of the worklet (just after process termination)
	 * @param status "success" or "failure"
	 * @param score assigned if success, based on script-returned scoring
	 * @param timingData (optional) will record the raw timing data lines
	 *
	 * Note:  timingData must be in the format:
	 *			first	- (String)description
	 *			second	- (Double)time
	 *			third	- (Double)percentOfBaseline
	 */
	public void appendResult(String		manifestWorkletUuid,
							 String		manifestAtomUuid,
							 String		startTime,
							 String		endTime,
							 String		status,
							 String		score,
							 Tuple		timingData)
	{
		int i;
		Xml result, timing;

		result = new Xml("result");
		result.appendAttribute(new Xml("manifestworkletuuid",	manifestWorkletUuid));
		result.appendAttribute(new Xml("atomuuid",				manifestAtomUuid));
		result.appendAttribute(new Xml("start",					startTime));
		result.appendAttribute(new Xml("end",					endTime));
		result.appendAttribute(new Xml("status",				status));
		result.appendAttribute(new Xml("score",					score));
		result.appendAttribute(new Xml("uuid",					Utils.getUUID()));

		// If they provided raw timing data, append it here
		if (timingData != null)
		{	// Add the individual timing element items
			for (i = 0; i < timingData.size(); i++)
			{	// Add a line like:  <timing name="whatever" time="whatever" score="whatever" timing="whatever" uuid="whatever"></timing>
				timing = new Xml("timing");
				timing.appendAttribute(new Xml("name",			timingData.getFirst(i)));
				timing.appendAttribute(new Xml("time",			Double.toString((Double)timingData.getSecond(i))));
				timing.appendAttribute(new Xml("score",			Double.toString((Double)timingData.getThird(i))));
				timing.appendAttribute(new Xml("millisecond",	(String)timingData.getFourth(i)));
				timing.appendAttribute(new Xml("uuid",			Utils.getUUID()));
				result.appendChild(timing);
			}
			// Once the data is recorded, remove it for the next pass
			timingData.clear();
		}
		m_rawResults.appendChild(result);
	}


	/**
	 * Appends the result of an atom run (pre-run atoms, post-run atoms, on-
	 * spinup atoms, on-failure atoms) to the results details.
	 * @param result what to add
	 */
	public void appendAtomRunResult(Xml result)
	{
		m_details.appendChild(result);
	}

	/**
	 * Appends detailed information about the
	 * @param success
	 * @param failures
	 * @param manifestWorkletUuid
	 */
	public void appendResultsDetail(Xml			success,
									Xml			failures,
									String		manifestWorkletUuid)
	{
		Xml detail;

		detail = new Xml("detail");
		detail.appendAttribute(new Xml("manifestworkletuuid", manifestWorkletUuid));
		if (failures.getFirstChild() != null)
		{	// There was at least one failure
			if (success.getFirstChild() != null)
				detail.appendChild(success);	// And there was a success
			detail.appendChild(failures);

		} else {
			// Only blue skies!
			detail.appendChild(success);
		}
		// The detail information has been added
		m_details.appendChild(detail);
	}

	/**
	 * Creates the resultsdata framework within the manifest's top-level opbm
	 * tag.  See full resulstdata structure at the top of this source file.
	 *
	 *	<opbm>
	 *		<resultsdata>
	 *			...
	 *		</resultsdata>
	 *	</opbm>
	 */
	public void createResultsdataFramework()
	{
		Xml opbmXml;

		opbmXml = m_bm.getRootOpbm();
		m_resultsdataRoot			= opbmXml.appendChild(new Xml("resultsdata"));				// opbm.resultsdata
		m_rawResults				= m_resultsdataRoot.appendChild(new Xml("rawResults"));		// opbm.resultsdata.rawResults
		m_results					= m_resultsdataRoot.appendChild(new Xml("results"));		// opbm.resultsdata.results
		m_details					= m_resultsdataRoot.appendChild(new Xml("details"));		// opbm.resultsdata.details
		m_aggregate					= m_resultsdataRoot.appendChild(new Xml("aggregate"));		// opbm.resultsdata.aggregate
		m_aggregateByAtom			= m_aggregate.appendChild(new Xml("byAtom"));				// opbm.resultsdata.aggregate.byAtom
		m_aggregateByIteration		= m_aggregate.appendChild(new Xml("byIteration"));			// opbm.resultsdata.aggregate.byIteration
		m_isLoaded = true;
	}

	/**
	 * Reloads the resultsdata section after reload-from-disk, validates the
	 * contents to make sure everything is in a valid form.  See the structure
	 * above, and at the top of this source file.
	 * @return true or false indicating every tag was found
	 */
	public boolean reloadResultsdata()
	{
		Xml opbmXml;
		boolean isValid;

		// See if everything is present and accounted for
		isValid = false;
		while (true)
		{
			opbmXml = m_bm.getRootOpbm();
			m_resultsdataRoot			= opbmXml.getAttributeOrChildNode("resultsdata");
			m_rawResults				= opbmXml.getAttributeOrChildNode("resultsdata.rawResults");
			m_results					= opbmXml.getAttributeOrChildNode("resultsdata.results");
			m_details					= opbmXml.getAttributeOrChildNode("resultsdata.details");
			m_aggregate					= opbmXml.getAttributeOrChildNode("resultsdata.aggregate");
			m_aggregateByAtom			= opbmXml.getAttributeOrChildNode("resultsdata.aggregate.byAtom");

			// Check everything
			if (m_resultsdataRoot == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata tag");
				break;
			}

			// Check everything
			if (m_rawResults == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.rawResults tag");
				break;
			}

			// Check everything
			if (m_results == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.results tag");
				break;
			}

			// Check everything
			if (m_details == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.details tag");
				break;
			}

			// Check everything
			if (m_aggregate == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.aggregate tag");
				break;
			}

			// Check everything
			if (m_aggregateByAtom == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.aggregate.byatom tag");
				break;
			}

			// We're good, set it as loaded and valid
			m_isLoaded	= true;
			isValid		= true;
			break;
		}
		return(isValid);
	}

	/**
	 * Completes any incomplete timing entries for an early-aborted run, or
	 * a multi-pass run where there were total failures on one or more of the
	 * passes, yet at least one success.
	 *
	 * Iterate through all opbm.benchmarks.mainfest.run.abstract entries,
	 * loading in all timing data for each atom per run, and comparing the
	 * timing entries.  If there are any missing in any run, then we obtain
	 * the longest unique list from all runs, excepting the "--failure--"
	 * auto-added entries, and add them to each one that has them missing, with
	 * a 0 score and 0 time for each.
	 */
	public void completeAnyIncompleteAbstractTimings(boolean saveManifest)
	{
		int i, j, k, l, thisRun, maxRuns;
		boolean found, anythingChanged;
		String atomuuid, manifestworkletuuid, thisTimingName, uuid;
		Xml thisAbstract, result, timing, candidate;
		List<Xml>		timings				= new ArrayList<Xml>(0);
		List<Xml>		manifestAbstracts	= new ArrayList<Xml>(0);
		List<Xml>		results				= new ArrayList<Xml>(0);
		List<String>	runElementsUniqueList			= new ArrayList<String>(0);
		Tuple abstractData, runNtimingElements, run;

//////////
// manifestAbstracts contains one element for every
// opbm.benchmarks.manifest.run.abstract entry (every
// atom that was run, for all of the runs).
//
// Note:  An abstract is the thing that is run, such as an external script.
//        They are called abstracts because the initial design of OPBM was to
//        create a layer of abstraction between the requested thing in OPBM,
//        and the physical mechanics of completing it externally, such as using
//        AutoIt for the scripting language.  However, AutoIt was not required,
//        but the ability to say "execute this thing" was required, so it became
//        a layer of abstraction, hence the use of "abstract".  And in this
//        case, the abstracts come from manifest.xml, so manifestAbstracts.
/////
		Xml.getNodeListContainingThisAttributeName(manifestAbstracts, m_bm.getManifestRoot(), "atomuuid", true);
		if (!manifestAbstracts.isEmpty())
		{	// Find out how many runs there were
			maxRuns = Math.min(Utils.getValueOf(manifestAbstracts.get(0).getParent().getAttribute("max"), 0), 3);

			// Grab all of the results into an easily navigable listing
			Xml.getNodeList(results, m_rawResults.getFirstChild(), "result", false);
//////////
//
// STEP 1
//
// abstractData sums up manifestAbstracts in such a way that it contains one
// entry for every atomuuid in all of the opbm.benchmarks.manifest.run.abstract
// entries.  By doing this, and since each atom should contain a unique uuid
// that is only run once per benchmark, abstractData can serve as a repository
// for instance data of each run's timing events and result output.
//
// Each abstractData element therefore contains data for (up to) three runs:
//		Runs 1, 2 and 3
//		With each run containing:
//				(a) its recorded/captured timing elements, and
//				(b) the results tag in opbm.resultsdata.rawResults
//
// Note:  This information appears in these slots/positions:
//			(a) elements occupy Tuple slots second,third and fourth
//			(b) elements occupy Tuple slots fifth,sixth and seventh
/////
			abstractData = new Tuple();
			// Iterate through everything that's for each run, loading its run's timing events and result Xml tag into these positions:
			//	first		= ArrayList<String> of 0=atomuuid, 1=manifestworkletuuid
			//	second		= run1 timing data elements
			//	third		= run2 timing data elements
			//	fourth		= run3 timing data elements
			//	fifth		= run1 resultsdata.rawResults.result entry (parent of its timing entries)
			//	sixth		= run2 resultsdata.rawResults.result entry (parent of its timing entries)
			//	seventh		= run3 resultsdata.rawResults.result entry (parent of its timing entries)
			for (i = 0; i < manifestAbstracts.size(); i++)
			{	// For each entry, lookup its timing elements, and add them to the appropriate run slot
				thisAbstract		= manifestAbstracts.get(i);
				thisRun				= Utils.getValueOf(thisAbstract.getParent().getAttribute("this"), 0);
				atomuuid			= thisAbstract.getAttribute("atomuuid");
				if (thisRun >= 1 && thisRun <= maxRuns)
				{
					// See if this atomuuid already exists in the atomData tuple
					found = false;
					for (j = 0; j < abstractData.size(); j++)
					{
						if (abstractData.getFirst(j).equalsIgnoreCase(atomuuid))
						{	// We found a match
							found = true;
							break;
						}
					}

					if (!found)
					{	// Adding a new entry to record this run's data
						j = abstractData.add(atomuuid, /*run1*/		new Tuple(),	/*run2*/	new Tuple(),	/*run3*/	new Tuple(),
													   /*result1*/	null,			/*result2*/	null,			/*result3*/	null);

						// Each new Tuple just added (in second, third and fourth positions,
						// for run1, run2 and run3) each contains these elements within:
						//		first		= timing element name
						//		second		= time (as a String)
						//		third		= score (as a String)
						//		fourth		= uuid
					}
					//else j is set from above where the previous atomuuid entry was found, and it broke out of the for..loop

				} else {
					// We ignore runs beyond 3, or below 1
					runNtimingElements	= null;
					System.out.println("Error: Manifest.xml contains malformed run data relative to atomuuid=" + atomuuid + ", ignoring its data");
					// No results data was found, so we have to create fill-in/false data for it
					// If it does not match the entries from the other runs, it will be supplanted below
				}
			}

//////////
//
// STEP 2
//
// Iterate through the results data, and load all data for the atom entries for
// the run they relate to
//
/////
			for (i = 0; i < results.size(); i++)
			{
				result = results.get(i);
				// See what this result backs into
				// Grab the manifestworkletuuid and find its parent/source
				// entry in the manifestAbstracts, this will tell us what
				// run the data was recorded for, and then we'll use the
				// atomuuid to find the slot/entry in abstractData and
				// populate the appropraite runN entry.
				timings.clear();
				Xml.getNodeList(timings, result.getFirstChild(), "timing", false);
				manifestworkletuuid	= result.getAttribute("manifestworkletuuid");
				atomuuid			= result.getAttribute("atomuuid");
				found				= false;
				for (j = 0; j < manifestAbstracts.size(); j++)
				{
					if (manifestAbstracts.get(j).getAttribute("uuid").equalsIgnoreCase(manifestworkletuuid))
					{	// This is the match
						found = true;
						break;
					}
				}
				if (found)
				{	// It should ALWAYS be found, unless the manifest is corrupted
					thisRun = Utils.getValueOf(manifestAbstracts.get(j).getParent().getAttribute("this"), 0);
					if (thisRun >= 1 && thisRun <= maxRuns)
					{	// Find the associated abstractData entry
						found = false;
						for (k = 0; k < abstractData.size(); k++)
						{
							if (abstractData.getFirst(k).equalsIgnoreCase(atomuuid))
							{	// We found it
								found = true;
								break;
							}
						}
						if (found)
						{	// Load the timing elements to the appropriate runN entry
							runNtimingElements	= (Tuple)abstractData.getNumbered(/*runN*/thisRun+1, k);	// Get second,third or fourth
							abstractData.setNumbered(thisRun+4, k, result);									// Set fifth,sixth or seventh
							for (l = 0; l < timings.size(); l++)
							{
								timing = timings.get(l);
								runNtimingElements.add(timing.getAttribute("name"), timing.getAttribute("time"), timing.getAttribute("score"), timing.getAttribute("uuid"));
							}
							// When we get here, every element is added to this entry's run number
						}
					}
				}
			}
			// When we get here, every result has been applied to its appropriate atomuuid-based run number

//////////
//
// STEP 3
//
// Take the list of populated entries, and fill in anything that doesn't have
// any timing data with a "--failure--" entry, so it can be conglomerated
// together properly in step 4 below.
//
/////
			for (i = 0; i < manifestAbstracts.size(); i++)
			{	// For every manifestworkletuuid, lookup its atomuuid, and make
				// sure it has timing element and result data
				thisAbstract			= manifestAbstracts.get(i);
				manifestworkletuuid		= thisAbstract.getAttribute("uuid");
				atomuuid				= thisAbstract.getAttribute("atomuuid");
				thisRun					= Utils.getValueOf(thisAbstract.getParent().getAttribute("this"), 0);
				if (thisRun >= 1 && thisRun <= maxRuns)
				{	// It's a valid run number
					for (j = 0; j < abstractData.size(); j++)
					{	// Try to find the abstractData run-grouping element
						if (abstractData.getFirst(j).equalsIgnoreCase(atomuuid))
						{	// We found it, now check this run item
							runNtimingElements	= (Tuple)abstractData.getNumbered(/*runN*/   thisRun+1, j);		// Get second,third or fourth
							result				= (Xml)	 abstractData.getNumbered(/*resultN*/thisRun+4, j);		// Get fifth,sixth or seventh
							if (result == null)
							{	// There is no result entry for this one manifestworkletuuid, so we have add it as a placeholder
								// See if there's an entry by manifestworkletuuid
								result = new Xml("resultNew");
								m_rawResults.appendChild(result);
								result.appendAttribute("manifestworkletuuid",	manifestworkletuuid);
								result.appendAttribute("atomuuid",				atomuuid);
								result.appendAttribute("start",					Utils.getTimestamp());
								result.appendAttribute("end",					Utils.getTimestamp());
								result.appendAttribute("status",				"failure");
								result.appendAttribute("score",					"0.0");
								result.appendAttribute("uuid",					Utils.getUUID());
								abstractData.setNumbered(thisRun+4, j, result);	// set the fifth,sixth or seventh for this (j) item
							}
							timings.clear();
							Xml.getNodeList(timings, result.getFirstChild(), "timing", true);
							if (timings.isEmpty())
							{	// There are no timing entries for this record, add one as a placeholder
								runNtimingElements.add("--failure--", "0.0", "0.0", Utils.getUUID());
							}
							// When we get here, we have the result entry guaranteed.
							// We also have at least one timing entry guaranteed.
							break;
						}
					}
				}
			}

//////////
//
// STEP 4
//
// When we get here, we have all recorded timing elements for all runs loaded
// for all atomuuids.  The next step is to take that list of timing elements
// recorded for every atomuuid for each run, and combine them into a single
// conglomerate that has a unique list of every timing item encountered.  This
// will address instances where only run1 ran, and the user stopped the test
// before completing runs 2 and 3, or if the tests simply failed on runs 2 and
// 3, etc.
//
// Once gathered into the single-source conglomeration, each run is then
// iterated back through to ensure that it contains every element.  Doing this
// addresses all instances where not all tests produced all result entries, or
// if there were failures on a specific pass and only a single "--failure--"
// entry was recorded by the harness, it is now bypassed to include the actual
// recorded timing entries for the test(s) that did pass, such that every run
// entry then contains the full set of timing items, as though each had
// received them from their associated script, though in these instances where
// we are "back-filling" with false/filler data, the data comes from a real-
// world examination about what data the instance(s) that did pass produced.
//
// This very complex variable set of variables (which itself contains an
// additional set of variables (the timing events)) is necessary to address the
// flexible nature of the scripting engine and its unknown-in-advance-or-by-
// the-harness method of producing timing events.
//
// This algorithm simply processes whatever it is fed, up to a 3-run pass,
// with "--failure--" events being tagged on completely blank entries (no
// success or failure was recorded by the harness, meaning the script never
// ran).
//
/////
			// For mental reference, these positions are used below in the "for (i.." loop
			//run1		= (Tuple) abstractData.getSecond(i);
			//run2		= (Tuple) abstractData.getThird(i);
			//run3		= (Tuple) abstractData.getFourth(i);
			//result1	= (Xml)   abstractData.getFifth(i);
			//result2	= (Xml)   abstractData.getSixth(i);
			//result3	= (Xml)   abstractData.getSeventh(i);

			// Make sure each abstract entry (each atom that was run) contains the same entries in each run's position
			anythingChanged = false;
			for (i = 0; i < abstractData.size(); i++)
			{	// Load the base "pointers" for each run

				// Create a unique list of every entry in run1, run2 and run3, all conglomerated together in runElements
				runElementsUniqueList.clear();
				for (j = 0; j < maxRuns; j++)
				{	// Iterate through each one building the unique list
					run		= (Tuple)abstractData.getNumbered(j+2, i);		// get the runN		(second,third or fourth) for this (i) item
					result	= (Xml)  abstractData.getNumbered(j+5, i);		// get the resultN	(fifth,sixth or seventh) for this (i) item
					for (k = 0; k < run.size(); k++)
					{	// See if these elements are present in the runElementsUniqueList tuple
						thisTimingName	= run.getFirst(k);
						found			= false;
						for (l = 0; l < runElementsUniqueList.size(); l++)
						{	// See if thisTimingName is already in the unique list
							if (thisTimingName.equalsIgnoreCase(runElementsUniqueList.get(l)))
							{	// It is, we have this one already, so stop looking for this one
								found = true;
								break;
							}
						}
						if (!found)
						{	// It wasn't already in htere, so add this entry
							runElementsUniqueList.add(thisTimingName);
						}
					}
				}
				// When we get here, runElements contains a completely filled
				// and unique list of entries for all three runs.

				if (runElementsUniqueList.size() > 1)
				{	// If there is more than 1 run, and any of the run entries are
					// named "--failure--", then we delete it.
					for (j = 0; j < runElementsUniqueList.size(); j++)
					{
						if (runElementsUniqueList.get(j).equalsIgnoreCase("--failure--"))
						{	// Delete this entry
							runElementsUniqueList.remove(j);
							// It's a unique list, so we can be guaranteed this was the only one
							break;
						}
					}
				}

				// For each run, add any item that is not present within
				for (j = 0; j < maxRuns; j++)
				{	// This time we're looking to see if an entry exists within
					// the run, and if not, then we append it to its opbm.resultsdata.rawResults.result entry
					run		= (Tuple)abstractData.getNumbered(j+2, i);		// get second,third or fourth for this (i) item
					result	= (Xml)  abstractData.getNumbered(j+5, i);		// get fifth,sixth or seventh for this (i) item

					// Now, append all timing elements to it
					for (k = 0; k < runElementsUniqueList.size(); k++)
					{	// See if this full unique set of elements is present in the run's entries
						thisTimingName	= runElementsUniqueList.get(k);
						found			= false;
						for (l = 0; l < run.size(); l++)
						{	// Check this unique list entry against everything in the run
							if (thisTimingName.equalsIgnoreCase(run.getFirst(l)))
							{	// We have this one already, so stop looking for it
								found = true;
								break;
							}
						}
						if (!found || thisTimingName.equalsIgnoreCase("--failure--"))
						{	// Add this entry to the results
							anythingChanged = true;
							timing = new Xml("timing");
							timing.appendAttribute("name",	thisTimingName);
							timing.appendAttribute("score", "0.0");
							timing.appendAttribute("time",	"0.0");
							timing.appendAttribute("uuid",	Utils.getUUID());
							result.appendChild(timing);
						}
					}
				}
				// When we get here, everything for this manifestworkletuuid has been set
				// All of the runs have data which match all of the other runs
			}
			// When we get here, every abstract (atom operation/worklet that was run) has its correct timing data
			if (saveManifest)
				m_bm.saveManifest();	// Save the new results
		}
	}

	/**
	 * Computes the aggregate totals by uuid (each instance of a run, averaged
	 * across multiple passes), along with atomuuid (each instance of an atom,
	 * no matter where it occurs, averaged by the number of instances total)
	 *
	 *		<aggregate>
	 *			<byuuid>
	 *				<uuid count="1" timeAverage="01:01" timeMean="01:01" timeMin="01:01" timeMax="01:01" scoreAverage="0" scoreMean="0" scoreMin="0" scoreMax="0" scoreCV="0">whatever</uuid>
	 *			</byuuid>
	 *			<byatom>
	 *				<atomuuid count="1" timeAverage="01:01" timeMean="01:01" timeMin="01:01" timeMax="01:01" scoreAverage="0" scoreMean="0" scoreMin="0" scoreMax="0" scoreCV="0">whatever</atomuuid>
	 *			</byatom>
	 *		</aggregate>
	 * @param retireOldAggregateData should old data be retired?  If yes, all tags are
	 * appended with an "old" moniker, and the attribute "retired" is added,
	 * set to Utils.getTimestamp().
	 */
	public void computeAggregateTotals(boolean retireOldData)
	{
		List<Xml> manifestAtoms			= new ArrayList<Xml>(0);
		Tuple byAtom					= new Tuple();

		// Retire (or delete) the old data
		if (retireOldData)
		{	// Retiring renames the tag from <aggregate> to <aggregateOld>
			// and adds a "retired" attribute set to Utils.getTimestamp()
			retireOldAggregateData();
			retireOldResultsData();
		} else {
			// Deleting supplants the previous data completely, which is then
			// garbage collected
			deleteOldAggregateData();
			deleteOldResultsData();
		}

		// Verify all of atom data in the manifest has a score
		Xml.getNodeListContainingThisAttributeName(manifestAtoms, m_bm.getManifestRoot(), "atomuuid", true);

		// Iterate through each entry, creating a list of every entry as it exists, along with its count
		populateLists(manifestAtoms, byAtom);

		// Populate the raw souce data into the list
		populateSourceDataIntoByAtomLists(byAtom);
	}

	/**
	 * Takes every manifest atom that was found and creates entries indicating
	 * the number of instances by manifestWorkletUuid
	 * @param manifestAtoms
	 * @param byAtom
	 */
	public void populateLists(List<Xml>	manifestAtoms,
							  Tuple		byAtom)
	{
		int i, j;
		Xml atom;
		boolean updated;
		String atomuuid;
		Tuple sourceData, summaryTimeData, summaryScoreData;

		for (i = 0; i < manifestAtoms.size(); i++)
		{
			atom		= (Xml)manifestAtoms.get(i);
			atomuuid	= atom.getAttribute("atomuuid");

			// Add/update it to/in the byAtom tuple
			// This grouping is used to accumulate pass data
			//	first	= atomuuid
			//	second	= count
			//	third	= source data (all rawResults entries for this atomuuid, populated in a later step)
			//	fourth	= summary time data (summed up by description, populated in a later step)
			//	fifth	= summary score data (summed up by description, populated in a later step)
			updated = false;
			for (j = 0; j < byAtom.size(); j++)
			{
				if (byAtom.getFirst(j).equalsIgnoreCase(atomuuid))
				{	// This is a match, update it
					byAtom.addSecondInteger(j, 1);	// byAtom.getSecond(j) += 1;
					updated = true;
					break;
				}
			}
			if (!updated)
			{	// New entry, add it
				sourceData			= new Tuple();
				summaryTimeData		= new Tuple();
				summaryScoreData	= new Tuple();
				byAtom.add(atomuuid, Integer.valueOf(1), sourceData, summaryTimeData, summaryScoreData);
			}
		}
		// When we get here, we have unique lists of uuid and atomuuid entries
	}

	/**
	 * Takes every result in rawResults, and where it matches an entry by
	 * atomuuid, it stores the raw timing elements into the tuple.  These are
	 * then summed in a later step.
	 * @param byAtom
	 */
	public void populateSourceDataIntoByAtomLists(Tuple byAtom)
	{
		int i, count, successCount, failureCount, thisRun, maxRun;
		Xml result, timing, detail, success, failure, source;
		double time, percentOfBaseline;
		String atomuuid, description, manifestworkletuuid;
		Tuple sourceData;

		// Now, iterate through the opbm.resultsdata.rawResults entries and
		// store every raw entry in the source data tuple
		result = m_rawResults.getFirstChild();
		count = 0;
		while (result != null)
		{
			if (result.getName().toLowerCase().startsWith("result"))
			{	// Place this one
				++count;

				// Grab our related-to-uuids
				atomuuid			= result.getAttribute("atomuuid");
				manifestworkletuuid	= result.getAttribute("manifestworkletuuid");
				if (!manifestworkletuuid.isEmpty())
				{	// Runs are only valid if everything is valid
					source = m_bm.getManifestRoot().getNodeByAttributeNameEqualsValue("uuid", manifestworkletuuid, false);
					if (source != null)
					{	// We need to back into the source manifest.run.abstract entry to find its this and max values, to know how many entries their should be
						thisRun		= Utils.getValueOf(source.getParent().getAttribute("this"), 1);
						maxRun		= Utils.getValueOf(source.getParent().getAttribute("max"), 3);

						// Iterate through byAtom (for atomuuid)
						for (i = 0; i < byAtom.size(); i++)
						{	// Find this entry
							if (byAtom.getFirst(i).equalsIgnoreCase(atomuuid))
							{	// We found our match
								// Add the totals lines
								timing		= result.getFirstChild();
								sourceData	= (Tuple)byAtom.getThird(i);
								if (timing != null)
								{	// Store all worklets (points of timing data)
									while (timing != null)
									{	// Add each line in turn as raw data to the tuple
										if (timing.getName().equalsIgnoreCase("timing"))
										{	//
											description			= timing.getAttribute("name");
											time				= Double.valueOf(timing.getAttribute("time"));
											percentOfBaseline	= Double.valueOf(timing.getAttribute("score"));

											// Add the line to the sourceData tuple:
											//		first	= description
											//		second	= time
											//		third	= percent of baseline (score)
											//		fourth	= result xml line
											//		fifth	= timing xml line
											//		sixth	= this run number
											//		seventh	= max runs
											sourceData.add(description, Double.valueOf(time), Double.valueOf(percentOfBaseline), result, timing, Integer.valueOf(thisRun), Integer.valueOf(maxRun));
										}
										// Move to next sibling
										timing = timing.getNext();
									}

								} else {
									// There is no timing data for this atom
									// See if there are any failures noted for it
									manifestworkletuuid = result.getAttribute("manifestworkletuuid");
									detail	= m_details.getNodeByAttributeNameEqualsValue("manifestworkletuuid", manifestworkletuuid, false);
									if (detail != null)
									{	// We have the detail line, see if there is a failure tag
										success = detail.getChildNode("success");
										failure = detail.getChildNode("failure");

										successCount = 0;
										while (success != null)
										{	// Count the number of success entries for our summary line (should always be zero)
											success = success.getNext();
											++successCount;
										}

										failureCount = 0;
										while (failure != null)
										{	// Count the number of failure entries for our summary line (should always be one more more)
											failure = failure.getNext();
											++failureCount;
										}
									}
								}
								// When we get here, we've added everything for this entry
								// There may be more entries for this atomuuid, so continue looking
							}
						}
					}
				}
			}
			result = result.getNext();
		}
		if (count == 0)
		{	// Error
			m_bm.setError("Error:  Unable to find rawResults tag to sum up totals.");
			OpbmDialog od = new OpbmDialog(m_opbm, true, "Unable to create summary data for Results Viewer. No results found.", "Error", OpbmDialog._CANCEL_BUTTON, "populateSourceDataIntoByAtomLists", "");
			od.setTimeout(30);
			Utils.monitorDialogWithTimeout(m_opbm, "populateSourceDataIntoByAtomLists", 30);

		} else {
			// Sum it all up
			sumUpSourceDataByAtom(byAtom);
		}
	}

	/**
	 * Computes the min, max, average, geometric mean and coefficient of variation
	 * for every like item computed in the test.
	 *
	 * @param byAtom
	 */
	public void sumUpSourceDataByAtom(Tuple byAtom)
	{
		int i, j, k;
		Xml resultSource, timingSource, result, timing;
		double time, score, power, value, timeTotal, scoreTotal;
		double minTime, minScore, maxTime, maxScore, avgTime, avgScore, geoTime, geoScore, cvTime, cvScore, cvTimeSum, cvScoreSum;
		String description, atomuuid, timinguuid, timingUuids;
		Tuple sourceData, summaryTimeData, summaryScoreData;
		List<Double>	times		= new ArrayList<Double>(0);
		List<Double>	scores		= new ArrayList<Double>(0);
		List<Xml>		timingXmls	= new ArrayList<Xml>(0);

		// Now, iterate through every sourceData entry in each Tuple, and sum
		// up its data, creating the:
		//
		// sourceData layout:
		//		first	= description
		//		second	= time
		//		third	= percent of baseline (score)
		//		fourth	= result xml line
		//		fifth	= timing xml line
		//		sixth	= this run number
		//		seventh	= max runs
		//
		// summaryData layout:
		//		first	= description
		//		second	= min
		//		third	= max
		//		fourth	= average
		//		fifth	= mean
		//		sixth	= cv
		//
		for (i = 0; i < byAtom.size(); i++)
		{	// Add all common lines for each entry

			// Grab the source data and summary data pointers
			sourceData			= (Tuple)byAtom.getThird(i);
			summaryTimeData		= (Tuple)byAtom.getFourth(i);
			summaryScoreData	= (Tuple)byAtom.getFifth(i);
			if (sourceData != null && summaryTimeData != null && summaryScoreData != null)
			{
				// Iterate through the source data entries, comparing the specified entry to every other one, summing matches
				for (j = 0; j < sourceData.size(); j++)
				{	// For entries that have previously been tagged, we used the word "<!-- Processed -->"
					//
					// Grab the data from the tuple:
					//		first	= description
					//		second	= time
					//		third	= percent of baseline (score)
					//		fourth	= result xml line
					//		fifth	= timing xml line
					//
					description = sourceData.getFirst(j);
					if (!description.equalsIgnoreCase("<!-- Processed -->"))
					{	// This line has not been processed yet
						time	= (Double)sourceData.getSecond(j);		// time
						score	= (Double)sourceData.getThird(j);		// percentOfBaseline

						// Reset our totals
						times.clear();
						scores.clear();
						timingXmls.clear();

						// We have at least one entry to total for this, see if there are any more
						times.add(time);
						scores.add(score);
						timingXmls.add((Xml)sourceData.getFifth(j));					// build chain of uuids which fed into this summary

						// Create our initial totals
						timeTotal	= time;
						scoreTotal	= score;
						timingUuids	= ((Xml)sourceData.getFifth(j)).getAttribute("uuid");
						for (k = 0; k < sourceData.size(); k++)
						{	// We're looking for items that match this one
							if (k != j)
							{	// See if this entry matches
								if (sourceData.getFirst(k).equalsIgnoreCase(description))
								{	// This is a match, include it
									// Add to the array list
									times.add((Double)sourceData.getSecond(k));			// time
									scores.add((Double)sourceData.getThird(k));			// percentOfBaseline
									timingXmls.add((Xml)sourceData.getFifth(k));		// timing xml source line

									// Increase our totals
									timeTotal	+= (Double)sourceData.getSecond(k);		// time
									scoreTotal	+= (Double)sourceData.getThird(k);		// percentOfBaseline
									timingUuids	+= "," + ((Xml)sourceData.getFifth(k)).getAttribute("uuid");

									// Indicate this line's already been included
									sourceData.setFirst(k, "<!-- Processed -->");
								}
							}
						}
						// When we get here, we have all input items for this line
						// Remember, this is just a single line, like "Launch Internet Explorer" or "Type in the URL" or "Run the test", etc.

						// Compute our simple values
						avgTime		= timeTotal / times.size();
						avgScore	= scoreTotal / times.size();

						// Compute the min and max
						minTime		= 100000.0f;
						maxTime		= -100000.0f;
						minScore	= 100000.0f;
						maxScore	= -100000.0f;
						for (k = 0; k < times.size(); k++)
						{
							time	= times.get(k);
							score	= scores.get(k);

							if (time < minTime)		minTime = time;
							if (time > maxTime)		maxTime = time;

							if (score < minScore)	minScore = score;
							if (score > maxScore)	maxScore = score;
						}

						// Compute the geometric mean
						power			= 1.0 / (double)times.size();
						geoTime			= 0.0f;
						geoScore		= 0.0f;
						for (k = 0; k < times.size(); k++)
						{	// Compute the geometric mean
							if (k == 0)
							{	// First time, store the value
								geoTime		= Math.pow(times.get(k), power);
								geoScore	= Math.pow(scores.get(k), power);

							} else {
								// Multiply the value
								geoTime		*= Math.pow(times.get(k), power);
								geoScore	*= Math.pow(scores.get(k), power);
							}
						}
						// When we get here, we have our geometric means

						// Compute the CV
						// Compute the standard deviation
						// sqrt((sum_of_variances^2) / count)
						cvTimeSum	= 0.0f;
						cvScoreSum	= 0.0f;
						for (k = 0; k < times.size(); k++)
						{	// Add up each to get the population's standard deviation
							cvTimeSum	+= Math.pow(times.get(k)  - avgTime, 2.0f);
							cvScoreSum	+= Math.pow(scores.get(k) - avgScore, 2.0f);
						}
						// Compute the standard deviation
						cvTime	= Math.sqrt( cvTimeSum  / (double)Math.max(times.size() - 1, 0.0000001));
						cvScore	= Math.sqrt( cvScoreSum / (double)Math.max(times.size() - 1, 1.0));
						// Now, divide by abs(mean) to get CV
						cvTime	= cvTime  / Math.max(Math.abs(avgTime),  0.0000001);
						cvScore	= cvScore / Math.max(Math.abs(avgScore), 1.0);
						// CV is computed

						// When we get here, we've summed everything for all
						// entries which match this one.
						// These two tuples have a 1:1 ratio, description is in summaryTimeData, and timingUuids which fed into the computation is in summaryScoreData
						summaryTimeData.add(description,						/* first */
											Integer.valueOf(times.size()),		/* second */
											Double.valueOf(minTime),			/* third */
											Double.valueOf(maxTime),			/* fourth */
											Double.valueOf(avgTime),			/* fifth */
											Double.valueOf(geoTime),			/* sixth */
											Double.valueOf(cvTime));			/* seventh */

						summaryScoreData.add(timingUuids,						/* first */
											Integer.valueOf(times.size()),		/* second */
											Double.valueOf(minScore),			/* third */
											Double.valueOf(maxScore),			/* fourth */
											Double.valueOf(avgScore),			/* fifth */
											Double.valueOf(geoScore),			/* sixth */
											Double.valueOf(cvScore));			/* seventh */
					}
				}
			}
		}
		addSummaryDataToAggregateByAtomTag(byAtom);
	}

	/**
	 * Appends the byAtom data to the aggregate totals:
	 *		<aggregate>
	 *			<byAtom>
	 *				<atom name="IE SunSpider" level="Browsers.Compute Browsers.Compute IE.IE SunSpider" instances="3" atomuuid="6f98ea77-7ec4-4096-8886-7551ec23bbed">
	 *					<worklet description="Launch Internet Explorer" instances="3" minTime="0.669650617366751" maxTime="0.866908909489832" avgTime="0.770127095928523" geoTime="0.7658685329502517" cvTime="0.3243522513278651" minScore="70.1556083508141" maxScore="90.8212735906301" avgScore="79.8572942517697" geoScore="79.41117739322316" cvScore="0.32685457570686965" sourcetiminguuids="3202aa3a-9cf7-41b1-af45-0d59e8d541d6,7c29a2d4-fbc3-45b2-ad5c-c629a8b6714a,b5157906-5ac4-4a1c-afe2-040c5a9d3e40"></worklet>
	 *					<worklet description="Type SunSpider URL" instances="3" minTime="2.08194977299573" maxTime="2.08444942054714" avgTime="2.08343290861567" geoTime="2.083432632516769" cvTime="0.02268859258635697" minScore="100.25498639595" maxScore="100.375355357061" avgScore="100.30392763253433" geoScore="100.30391433754097" cvScore="0.022691924298196817" sourcetiminguuids="7d4eca4f-0081-4a2c-a222-7ff9f6c8d4ab,c773a743-a081-4167-9897-41d90ad4e36e,3f58eb65-0717-4b12-88e9-16bd3a2144c4"></worklet>
	 *					<worklet description="Run SunSpider" instances="3" minTime="18.5070766887078" maxTime="19.0243422214253" avgTime="18.679896605954234" geoTime="18.678318385279507" cvTime="0.11419165822472208" minScore="52.6777868562184" maxScore="54.1501103321992" avgScore="53.65817118806863" geoScore="53.653665386162736" cvScore="0.11366884405101425" sourcetiminguuids="02837a84-76b7-4511-a7ba-4b56647f3142,c73a03ff-be9e-4bb4-a99f-0c75d834187d,784ab81a-e70f-46a4-842c-7b9c12da198c"></worklet>
	 *					<worklet description="Close Internet Explorer" instances="3" minTime="0.100912876076337" maxTime="0.203928809490243" avgTime="0.13590805585705698" geoTime="0.12840704948159087" cvTime="0.6120672292027829" minScore="53.528935893299" maxScore="108.17343231544" avgScore="89.26830114157467" geoScore="85.01162680764672" cvScore="0.5453787360748731" sourcetiminguuids="c2808966-60ab-4843-8ced-44973fb46334,f8bdf9ec-8ecf-4e55-b79c-ae086c6f07e9,42b35b4c-730a-467a-8470-99e850730fdc"></worklet>
	 *				</atom>
	 *				<atom name="IE Google V8" level="Browsers.Compute Browsers.Compute IE.IE Google V8" instances="3" atomuuid="72fb3cfc-768b-4372-ab7c-9134ed8be67a">
	 *					<worklet description="Launch Internet Explorer" instances="3" minTime="0.86849903435929" maxTime="0.885279009256737" avgTime="0.8758364665899266" geoTime="0.8758084737954872" cvTime="0.08946688730464966" minScore="68.6998350735346" maxScore="70.0271612562783" avgScore="69.4449338027237" geoScore="69.44271921283315" cvScore="0.08931706530173725" sourcetiminguuids="1854638b-5b35-4a09-8a08-ab94c18d8dfe,f0ecbfa6-cee3-4d2f-94cc-76ff8a1d97d3,8cb112be-bd3b-4e70-9f58-eb4b7ac519c5"></worklet>
	 *					<worklet description="Type URL to Google V8 benchmark" instances="3" minTime="2.03895857257635" maxTime="2.13598767045851" avgTime="2.0806015715785433" geoTime="2.080204161682106" cvTime="0.14002500440172755" minScore="96.3727540505007" maxScore="100.958899895594" avgScore="98.9759133674022" geoScore="98.95712075373557" cvScore="0.1393995063658344" sourcetiminguuids="bbbd9234-0f03-47ef-9b23-a947ffb8d769,657d4c12-25da-4e0e-9b0d-5835f32fd861,9f14a0d1-326e-437c-bbf6-7c7c09089505"></worklet>
	 *					<worklet description="Run Google V8" instances="3" minTime="25.0673871445915" maxTime="25.5156121039744" avgTime="25.36280795908767" geoTime="25.361944002273685" cvTime="0.09076424651561586" minScore="81.7679762510977" maxScore="83.2300531569421" avgScore="82.26622191340817" geoScore="82.2634086867693" cvScore="0.09102929912284301" sourcetiminguuids="a683aa87-b7d0-4eac-90f6-25eb7430ea8e,c77aa796-c760-412e-a1b4-9f804510b08f,742c0f58-751b-4e19-9088-1ab98cfa142d"></worklet>
	 *					<worklet description="Close Internet Explorer" instances="3" minTime="0.200922046583439" maxTime="0.300914792301701" avgTime="0.23456628748216665" geoTime="0.23023707063154142" cvTime="0.45141679138446095" minScore="36.2763561289316" maxScore="54.3299869557459" avgScore="48.22778023869703" geoScore="47.41240035784468" cvScore="0.42220422437398636" sourcetiminguuids="31e7fc6b-42a2-4f06-a148-b6ad8c772dfc,70cc475b-f3d4-4e70-80bd-d8d2294c8fd9,9dbf2437-1895-44f7-ba98-a4fad0035f13"></worklet>
	 *				</atom>
	 *			</byAtom>
	 *		</aggregate>
	 *
	 * @param byAtom
	 */
	public void addSummaryDataToAggregateByAtomTag(Tuple byAtom)
	{
		int i, j, k, l, instances;
		Xml resultSource, timingSource, atom, worklet, abstractXml, run;
		String atomuuid, timinguuid, description, level;
		Tuple sourceData, summaryTimeData, summaryScoreData;
		List<String> uuids = new ArrayList<String>(0);

		for (i = 0; i < byAtom.size(); i++)
		{	// Grab this entry
			atomuuid			= (String)byAtom.getFirst(i);
			instances			= (Integer)byAtom.getSecond(i);
			sourceData			= (Tuple)byAtom.getThird(i);
			summaryTimeData		= (Tuple)byAtom.getFourth(i);
			summaryScoreData	= (Tuple)byAtom.getFifth(i);
			if (sourceData != null && summaryTimeData != null && summaryScoreData != null)
			{
				atom = new Xml("atom");

				// Load the original manifest entry for this atomuuid
				abstractXml = Xml.getNodeByAttributeNameEqualsValue(m_bm.getManifestRoot(), "atomuuid", atomuuid, false);
				if (abstractXml != null)
				{	// Store the description and level (for personal viewer reference)
					description = abstractXml.getAttribute("name");
					level		= abstractXml.getAttribute("level");

					if (description != null)
						atom.appendAttribute(new Xml("name", description));

					if (level != null)
						atom.appendAttribute(new Xml("level", level));
				}

				atom.appendAttribute(new Xml("instances", Integer.toString(instances)));
				atom.appendAttribute(new Xml("atomuuid", atomuuid));
				m_aggregateByAtom.appendChild(atom);

				if (!sourceData.isEmpty())
				{	// Scan through every source line entry, reporting each one, one-by-one
					for (j = 0; j < sourceData.size(); j++)
					{	// Report the total for this source
						//	first	= description
						//	second	= timing
						//	third	= score
						resultSource	= (Xml)sourceData.getFourth(j);				// <result> tag it came from
						timingSource	= (Xml)sourceData.getFifth(j);				// <timing> tag it came from
						timinguuid		= timingSource.getAttribute("uuid");

						// Record the summary data
						for (k = 0; k < summaryTimeData.size(); k++)
						{	// Write the entries in summaryTimeData and summaryScoreData
							// summaryTimeData and summaryScoreData are in sync, so as we migrate through one, we migrate through the other
							// Make sure the descriptions match
							if (summaryTimeData.getFirst(k).equalsIgnoreCase(sourceData.getFirst(j)))
							{	// This is the match
								worklet = new Xml("worklet");

								// Append the attributes for this entry
								worklet.appendAttribute(new Xml("description",			summaryTimeData.getFirst(k)));
								worklet.appendAttribute(new Xml("instances",			Integer.toString((Integer)summaryTimeData.getSecond(k))));
								worklet.appendAttribute(new Xml("minTime",				Double.toString((Double)summaryTimeData.getThird(k))));
								worklet.appendAttribute(new Xml("maxTime",				Double.toString((Double)summaryTimeData.getFourth(k))));
								worklet.appendAttribute(new Xml("avgTime",				Double.toString((Double)summaryTimeData.getFifth(k))));
								worklet.appendAttribute(new Xml("geoTime",				Double.toString((Double)summaryTimeData.getSixth(k))));
								worklet.appendAttribute(new Xml("cvTime",				Double.toString((Double)summaryTimeData.getSeventh(k))));
								worklet.appendAttribute(new Xml("minScore",				Double.toString((Double)summaryScoreData.getThird(k))));
								worklet.appendAttribute(new Xml("maxScore",				Double.toString((Double)summaryScoreData.getFourth(k))));
								worklet.appendAttribute(new Xml("avgScore",				Double.toString((Double)summaryScoreData.getFifth(k))));
								worklet.appendAttribute(new Xml("geoScore",				Double.toString((Double)summaryScoreData.getSixth(k))));
								worklet.appendAttribute(new Xml("cvScore",				Double.toString((Double)summaryScoreData.getSeventh(k))));
								worklet.appendAttribute(new Xml("sourcetiminguuids",	summaryScoreData.getFirst(k)));

								// For each worklet, append all source times and scores for each run
								uuids.clear();
								Utils.extractCommaItems(uuids, summaryScoreData.getFirst(k));
								for (l = 0; l < uuids.size(); l++)
								{	// For every timing item, find its source in resultsdata.rawResults
									timingSource = m_rawResults.getNodeByUUID(uuids.get(l), false);
									if (timingSource != null)
									{	// We have a timing source for this entry
										run = new Xml("run" + Integer.toString(l + 1));
										run.appendAttribute(new Xml("timinguuid",	uuids.get(l)));
										run.appendAttribute(new Xml("time",			timingSource.getAttribute("time")));
										run.appendAttribute(new Xml("score",		timingSource.getAttribute("score")));
										run.appendAttribute(new Xml("millisecond",	timingSource.getAttribute("millisecond")));

										// Add it to the worklet
										worklet.appendChild(run);
									}
								}

								// Add to the result to the atom
								atom.appendChild(worklet);
								break;
							}
						}
					}

				} else {
					// There is no source data because this atom failed
// REMEMBER
				}
			}
		}
	}

	/**
	 * Computes the results viewer totals, of this form:
	 *		<result datetime="Aug 25, 2011 at 10:56AM" name="OPBM Benchmark" shortname="unnamed" tags="" tested="yes" status="success" score="0">
	 *			<suite name="Suite Name" shortname="sun" tags="" tested="yes" status="success" score="0">
	 *				<scenario name="Scenario Name" shortname="scn" tags="" tested="yes" status="success" score="0">
	 *					<molecule name="Molecule Name" shortname="mon" tags="" tested="yes" status="success" score="0">
	 *						<atom shortname="Atom Name" shortnamej="atn" tags="" tested="yes" score="0" name="Un-install Acrobat Reader" status="success">
	 *							<worklet name="Launch Adobe Acrobat 10.1 Un-installer" shortname="LAdob" timing="3.29899172327516" score="99.8133294687674" tested="yes" status="success"></worklet>
	 *							<worklet name="Un-install Adobe Acrobat 10.1" shortname="UnAdob" timing="13.6098921028247" score="50.3682532418995" tested="yes" status="success"></worklet>
	 *						</atom>
	 *					</molecule>
	 *				</scenario>
	 *			</suite>
	 *		</result>
	 */
	public void computeResultsViewerTotalsAndGenerateCSVFile()
	{
		String point, sourcename, manifestworkletuuid, atomuuid, datetime, rootFile, xmlFile, csvFile;
		Xml run, child, rootXml, resultsDataXml, resultXml, runN, newRunN;
		Xml lastSuiteXml, lastScenarioXml, lastMoleculeXml, lastAtomXml;
		Xml worklet, result, timing, newWorklet, instanceResult;
		List<String> csvLines = new ArrayList<String>(0);

		rootXml				= new Xml("opbm");									// opbm
		resultsDataXml		= rootXml.appendChild(new Xml("resultsdata"));		// opbm.resultsdata
		resultXml			= resultsDataXml.appendChild(new Xml("result"));	// opbm.resultsdata.results
		lastSuiteXml		= null;												// opbm.resultsdata.results.suite
		lastScenarioXml		= null;												// opbm.resultsdata.results.suite.scenario
		lastMoleculeXml		= null;												// opbm.resultsdata.results.suite.scenario.molecule
		lastAtomXml			= null;												// opbm.resultsdata.results.suite.scenario.molecule.atom
		worklet				= null;												// opbm.resultsdata.results.suite.scenario.molecule.atom.worklet

		datetime = Utils.getDateTimeAs_Mmm_DD__YYYY_at_HH_MMampm();
		resultXml.appendAttribute(new Xml("datetime",	datetime));
		resultXml.appendAttribute(new Xml("name",		"OPBM Benchmark"));
		resultXml.appendAttribute(new Xml("tested",		"yes"));
		resultXml.appendAttribute(new Xml("status",		"success"));
		resultXml.appendAttribute(new Xml("score",		""));

		csvLines.add("OPBM Benchmark");
		csvLines.add("Date: " + datetime);
		csvLines.add("");

		// Iterate through the opbm.benchmarks.manifest.run entries to build the results.xml structure
// working here, remove the leading _
		run = m_bm.getManifestRoot().getFirstChild();
		while (run != null)
		{	// We're looking for "run" entries, ignoring other ones
			if (run.getName().equalsIgnoreCase("run"))
			{	// We have a run entry, begin outlining the results.xml structure
				child = run.getFirstChild();
				while (child != null)
				{	// Looking for the structure of the results.xml file, which will have been dictated by the order in which BenchmarkManifest built the class
					if (child.getName().equalsIgnoreCase("tag"))
					{	// It's control information, tagging the level
						point = child.getAttribute("point");
						if (point.equalsIgnoreCase("beginsuite"))
						{	// The beginning of a results viewer suite-level set of entries
							lastSuiteXml = resultXml.appendChild(new Xml("suite"));
							lastSuiteXml.appendAttribute(new Xml("name",			child.getAttribute("name")));
							lastSuiteXml.appendAttribute(new Xml("shortname",		Utils.getShortName(child.getAttribute("name"), 6)));
							lastSuiteXml.appendAttribute(new Xml("tags",			""));
							lastSuiteXml.appendAttribute(new Xml("tested",			"yes"));
							lastSuiteXml.appendAttribute(new Xml("status",			"success"));
							lastSuiteXml.appendAttribute(new Xml("score",			""));
							lastScenarioXml	= null;
							lastMoleculeXml	= null;
							lastAtomXml		= null;
							csvLines.add("Suite," + child.getAttribute("name"));

						} else if (point.equalsIgnoreCase("beginscenario")) {
							// The beginning of a results viewer scenario-level set of entries
							if (lastSuiteXml == null)
							{	// Add a placeholder scenario
								lastSuiteXml = addPlaceholderSuite(resultXml);
							}
							lastScenarioXml = lastSuiteXml.appendChild(new Xml("scenario"));
							lastScenarioXml.appendAttribute(new Xml("name",			child.getAttribute("name")));
							lastScenarioXml.appendAttribute(new Xml("shortname",	Utils.getShortName(child.getAttribute("name"), 6)));
							lastScenarioXml.appendAttribute(new Xml("tags",			""));
							lastScenarioXml.appendAttribute(new Xml("tested",		"yes"));
							lastScenarioXml.appendAttribute(new Xml("status",		"success"));
							lastScenarioXml.appendAttribute(new Xml("score",		""));
							lastMoleculeXml	= null;
							lastAtomXml		= null;
							csvLines.add("Scenario," + child.getAttribute("name"));

						} else if (point.equalsIgnoreCase("beginmolecule")) {
							// The beginning of a results viewer molecule-level set of entries
							if (lastSuiteXml == null)
							{	// Add a placeholder scenario
								lastSuiteXml = addPlaceholderSuite(resultXml);
							}
							if (lastScenarioXml == null)
							{	// Add a placeholder scenario
								lastScenarioXml = addPlaceholderScenario(lastSuiteXml);
							}
							lastMoleculeXml = lastScenarioXml.appendChild(new Xml("molecule"));
							lastMoleculeXml.appendAttribute(new Xml("name",			child.getAttribute("name")));
							lastMoleculeXml.appendAttribute(new Xml("shortname",	Utils.getShortName(child.getAttribute("name"), 6)));
							lastMoleculeXml.appendAttribute(new Xml("tags",			""));
							lastMoleculeXml.appendAttribute(new Xml("tested",		"yes"));
							lastMoleculeXml.appendAttribute(new Xml("status",		"success"));
							lastMoleculeXml.appendAttribute(new Xml("score",		""));
							lastAtomXml		= null;
							csvLines.add("Molecule," + child.getAttribute("name"));

						} else if (point.equalsIgnoreCase("beginatom")) {
							// The beginning of a results viewer atom-level set of entries
							if (lastSuiteXml == null)
							{	// Add a placeholder scenario
								lastSuiteXml = addPlaceholderSuite(resultXml);
							}
							if (lastScenarioXml == null)
							{	// Add a placeholder scenario
								lastScenarioXml = addPlaceholderScenario(lastSuiteXml);
							}
							if (lastMoleculeXml == null)
							{	// Add a placeholder molecule
								lastMoleculeXml = addPlaceholderMolecule(lastScenarioXml);
							}
							lastAtomXml = lastMoleculeXml.appendChild(new Xml("atom"));
							lastAtomXml.appendAttribute(new Xml("name",				child.getAttribute("name")));
							lastAtomXml.appendAttribute(new Xml("shortname",		Utils.getShortName(child.getAttribute("name"), 6)));
							lastAtomXml.appendAttribute(new Xml("tags",				""));
							lastAtomXml.appendAttribute(new Xml("tested",			"yes"));
							lastAtomXml.appendAttribute(new Xml("status",			"success"));
							lastAtomXml.appendAttribute(new Xml("score",			""));
							csvLines.add("Atom," + child.getAttribute("name"));

						}// else, we ignore it, it's not one of our known entries

					} else if (child.getName().equalsIgnoreCase("abstract")) {
						// It's an abstract command
						sourcename = child.getAttribute("sourcename");
						if (!sourcename.toLowerCase().startsWith("reboot"))
						{	// We ignore reboot commands, but everything else we process
//////////
// REMEMBER The following code can be used if it is desired to record the instance times, rather than the average
// times for multiple passes.  Be sure also to remove the "break" below, which will allow each pass to be included
// in the results (search for "GUIDANCE#1" in this file).  A settings.xml setting should be advised here.
// BEGIN
//							// Lookup the timing data for this instance of the run in rawResults, and load in the "timing" worklets
//							// Grab the uuid, which is also the manifestworkletuuid
//							manifestworkletuuid = child.getAttribute("uuid");
//							result = Xml.getNodeByAttributeNameEqualsValue(m_rawResults, "manifestworkletuuid", manifestworkletuuid, false);
//							if (result != null)
//							{	// Add the timing elements
//								timing = result.getFirstChild();
//								while (timing != null)
//								{	// These are the raw instance results for this test
//									worklet = lastAtomXml.appendChild(new Xml("worklet"));
//									worklet.appendAttribute(new Xml("name",			timing.getAttribute("name")));
//									worklet.appendAttribute(new Xml("score",		timing.getAttribute("score")));
//									worklet.appendAttribute(new Xml("time",			timing.getAttribute("time")));
//									worklet.appendAttribute(new Xml("shortname",	timing.getAttribute("name")));
//									worklet.appendAttribute(new Xml("tags",			""));
//									worklet.appendAttribute(new Xml("tested",		"yes"));
//									worklet.appendAttribute(new Xml("status",		"success"));
//
//									csvLines.add(",,Worklet,"	+ timing.getAttribute("name"));
//									csvLines.add(",,,score,"	+ timing.getAttribute("score"));
//									csvLines.add(",,,time,"		+ timing.getAttribute("time"));
//
//									// Move to next sibling
//									timing = timing.getNext();
//								}
//							}
// END
//////////

//////////
// The following code uses the aggregate totals byAtom, to record the min, max, avg, geomean and cv for timing and scores
// BEGIN
							// Lookup the timing data for this instance of the run in rawResults, and load in the "timing" worklets
							// Grab the atomuuid, which is also the manifestworkletuuid
							atomuuid = child.getAttribute("atomuuid");
							result = Xml.getNodeByAttributeNameEqualsValue(m_aggregateByAtom, "atomuuid", atomuuid, false);
							if (result != null)
							{	// Add the timing elements
								manifestworkletuuid = child.getAttribute("uuid");
								instanceResult = Xml.getNodeByAttributeNameEqualsValue(m_rawResults, "manifestworkletuuid", manifestworkletuuid, false);

								worklet = result.getFirstChild();
								while (worklet != null)
								{	// These are the raw instance results for this test
									newWorklet = lastAtomXml.appendChild(new Xml("worklet"));
									newWorklet.appendAttribute(new Xml("name",			worklet.getAttribute("description")));
									newWorklet.appendAttribute(new Xml("instances",		worklet.getAttribute("instances")));
									newWorklet.appendAttribute(new Xml("score",			worklet.getAttribute("avgScore")));
									newWorklet.appendAttribute(new Xml("time",			worklet.getAttribute("avgTime")));
									newWorklet.appendAttribute(new Xml("shortname",		Utils.getShortName(worklet.getAttribute("description"), 6)));
									newWorklet.appendAttribute(new Xml("minTime",		worklet.getAttribute("minTime")));
									newWorklet.appendAttribute(new Xml("maxTime",		worklet.getAttribute("maxTime")));
									newWorklet.appendAttribute(new Xml("avgTime",		worklet.getAttribute("avgTime")));
									newWorklet.appendAttribute(new Xml("geoTime",		worklet.getAttribute("geoTime")));
									newWorklet.appendAttribute(new Xml("cvTime",		worklet.getAttribute("cvTime")));
									newWorklet.appendAttribute(new Xml("minScore",		worklet.getAttribute("minScore")));
									newWorklet.appendAttribute(new Xml("maxScore",		worklet.getAttribute("maxScore")));
									newWorklet.appendAttribute(new Xml("avgScore",		worklet.getAttribute("avgScore")));
									newWorklet.appendAttribute(new Xml("geoScore",		worklet.getAttribute("geoScore")));
									newWorklet.appendAttribute(new Xml("cvScore",		worklet.getAttribute("cvScore")));
									newWorklet.appendAttribute(new Xml("tags",			""));
									newWorklet.appendAttribute(new Xml("tested",		"yes"));

									if (instanceResult != null)	// This should ALWAYS be valid, NEVER null
										newWorklet.appendAttribute(new Xml("status",	instanceResult.getAttribute("status")));

									// If there was more than one run present, append each one as part of its three-run nature
									// If it was an iterated run, score the main worklet many times.

									// Append the runN data to the worklet (run1, run2, run3...)
									runN = worklet.getFirstChild();
									while (runN != null)
									{
										if (runN.getName().toLowerCase().startsWith("run"))
										{	// Append this one, but we only need the time and score data, not the timinguuid
											newRunN = new Xml(runN.getName());
											newRunN.appendAttribute(new Xml("time",			runN.getAttribute("time")));
											newRunN.appendAttribute(new Xml("score",		runN.getAttribute("score")));
											newRunN.appendAttribute(new Xml("status",		runN.getAttribute("status")));
											newRunN.appendAttribute(new Xml("millisecond",	runN.getAttribute("millisecond")));
											newWorklet.appendChild(newRunN);
										}
										// Continue to next sibling
										runN = runN.getNext();
									}
									// When we get here, all run data items are copied from the byAtom worklet data

									csvLines.add(",Worklet,"		+ worklet.getAttribute("description"));
									csvLines.add(",,,time,"			+ worklet.getAttribute("avgTime"));
									csvLines.add(",,,score,"		+ worklet.getAttribute("avgScore"));
									csvLines.add(",,instances,"		+ worklet.getAttribute("instances"));
									csvLines.add(",,minTime,"		+ worklet.getAttribute("minTime"));
									csvLines.add(",,maxTime,"		+ worklet.getAttribute("maxTime"));
									csvLines.add(",,avgTime,"		+ worklet.getAttribute("avgTime"));
									csvLines.add(",,geoTime,"		+ worklet.getAttribute("geoTime"));
									csvLines.add(",,cvTime,"		+ worklet.getAttribute("cvTime"));
									csvLines.add(",,minScore,"		+ worklet.getAttribute("minScore"));
									csvLines.add(",,maxScore,"		+ worklet.getAttribute("maxScore"));
									csvLines.add(",,avgScore,"		+ worklet.getAttribute("avgScore"));
									csvLines.add(",,geoScore,"		+ worklet.getAttribute("geoScore"));
									csvLines.add(",,cvScore,"		+ worklet.getAttribute("cvScore"));

									if (instanceResult != null)
									{	// This should ALWAYS be valid, NEVER null
										newWorklet.appendAttribute(new Xml("status",	instanceResult.getAttribute("status")));
										csvLines.add(",,,status,"	+ instanceResult.getAttribute("status"));

									} else {
										// This condition should never occur
										newWorklet.appendAttribute(new Xml("status",	"success"));
										csvLines.add(",,,status,success");
									}

									// Move to next sibling
									worklet = worklet.getNext();
								}
							}
// END
//////////
						}
					}

					// Move to next sibling
					child = child.getNext();
				}
				// When we get here, we've processed one of the runs
				// We're done
				break;	// REMOVE THIS BREAK if you are following the GUIDANCE#1 instruction above
			}

			// Move to next sibling
			run = run.getNext();
		}

		// Save results.xml
		rootFile	= "results_" + Utils.convertFilenameToLettersAndNumbersOnly(Utils.getDateTimeAs_YYYY_MM_DD_at_HH_MM_SS() + "_" + m_opbm.getRunName());
		xmlFile		= Opbm.getHarnessXMLDirectory() + rootFile + ".xml";
		csvFile		= Opbm.getHarnessCSVDirectory() + rootFile + ".csv";

		// Save the XML and CSV files
		m_opbm.setResultsViewerFilename(xmlFile);
		rootXml.saveNode(xmlFile);
		Utils.writeTerminatedLinesToFile(csvFile, csvLines);
	}

	/**
	 * Called to retire the old <aggregate> data, and prepare a new, virgin tag
	 */
	public void retireOldAggregateData()
	{
		Xml newAggregate, newAggregateByAtom;

		if (m_aggregate != null)
		{	// Create the new entries
			newAggregate		= new Xml("aggregate");
			newAggregateByAtom	= new Xml("byAtom");
			newAggregate.appendChild(newAggregateByAtom);

			// Update the old entry
			m_aggregate.setNext(newAggregate);
			m_aggregate.setName(m_aggregate.getName() + "Old");
			m_aggregate.appendAttribute("retired", Utils.getTimestamp());

			// Supplant the old entry
			m_aggregate			= newAggregate;
			m_aggregateByAtom	= newAggregateByAtom;
		}
	}

	/**
	 * Called to delete the old <aggregate> data, and prepare a new, virgin tag
	 */
	public void deleteOldAggregateData()
	{
		Xml newAggregate, newAggregateByAtom;

		if (m_aggregate != null)
		{	// Create the new entries
			newAggregate		= new Xml("aggregate");
			newAggregateByAtom	= new Xml("byAtom");
			newAggregate.appendChild(newAggregateByAtom);

			// Supplant the old entry
			m_aggregate			= newAggregate;
			m_aggregateByAtom	= newAggregateByAtom;
			// Old entries are deleted by garbage collection
		}
	}

	/**
	 * Called to retire the old <aggregate> data, and prepare a new, virgin tag
	 */
	public void retireOldResultsData()
	{
		Xml newResults;

		if (m_results != null)
		{	// Create the new entries
			newResults		= new Xml("aggregate");

			// Update the old entry
			m_results.setNext(newResults);
			m_results.setName(m_results.getName() + "Old");
			m_results.appendAttribute("retired", Utils.getTimestamp());

			// Supplant the old entry
			m_results	= newResults;
		}
	}

	/**
	 * Called to delete the old <aggregate> data, and prepare a new, virgin tag
	 */
	public void deleteOldResultsData()
	{
		Xml newResults;

		if (m_results != null)
		{	// Create the new entries
			newResults	= new Xml("results");

			// Supplant the old entry
			m_results	= newResults;
			// Old entries are deleted by garbage collection
		}
	}

	/**
	 * For the results viewer, there is a rigid structure that must be adhered
	 * to which is an xml tree representation of the logical OPBM entity
	 * relationship, which is suites, scenarios, molecules and atoms.
	 *
	 * If something is missing in a particular run, we add a placeholder to
	 * hold its position for the items which were physically present.  This
	 * will occur if a user runs a single atom, molecule or scenario, for
	 * example.
	 *
	 * @param root node in which to add
	 * @return the new xml that was appended
	 */
	public Xml addPlaceholderSuite(Xml root)
	{
		Xml newXml;

		newXml = root.appendChild(new Xml("suite"));
		newXml.appendAttribute(new Xml("name",			"Suite"));
		newXml.appendAttribute(new Xml("shortname",		"Suite"));
		newXml.appendAttribute(new Xml("tags",			""));
		newXml.appendAttribute(new Xml("tested",		"yes"));
		newXml.appendAttribute(new Xml("status",		"success"));
		newXml.appendAttribute(new Xml("score",			""));
		return(newXml);
	}

	/**
	 * For the results viewer, there is a rigid structure that must be adhered
	 * to which is an xml tree representation of the logical OPBM entity
	 * relationship, which is suites, scenarios, molecules and atoms.
	 *
	 * If something is missing in a particular run, we add a placeholder to
	 * hold its position for the items which were physically present.  This
	 * will occur if a user runs a single atom, molecule or scenario, for
	 * example.
	 *
	 * @param root node in which to add
	 * @return the new xml that was appended
	 */
	public Xml addPlaceholderScenario(Xml root)
	{
		Xml newXml;

		newXml = root.appendChild(new Xml("scenario"));
		newXml.appendAttribute(new Xml("name",			"Scenario"));
		newXml.appendAttribute(new Xml("shortname",		"Scenario"));
		newXml.appendAttribute(new Xml("tags",			""));
		newXml.appendAttribute(new Xml("tested",		"yes"));
		newXml.appendAttribute(new Xml("status",		"success"));
		newXml.appendAttribute(new Xml("score",			""));
		return(newXml);
	}

	/**
	 * For the results viewer, there is a rigid structure that must be adhered
	 * to which is an xml tree representation of the logical OPBM entity
	 * relationship, which is suites, scenarios, molecules and atoms.
	 *
	 * If something is missing in a particular run, we add a placeholder to
	 * hold its position for the items which were physically present.  This
	 * will occur if a user runs a single atom, molecule or scenario, for
	 * example.
	 *
	 * @param root node in which to add
	 * @return the new xml that was appended
	 */
	public Xml addPlaceholderMolecule(Xml root)
	{
		Xml newXml;

		newXml = root.appendChild(new Xml("molecule"));
		newXml.appendAttribute(new Xml("name",			"Molecule"));
		newXml.appendAttribute(new Xml("shortname",		"Molecule"));
		newXml.appendAttribute(new Xml("tags",			""));
		newXml.appendAttribute(new Xml("tested",		"yes"));
		newXml.appendAttribute(new Xml("status",		"success"));
		newXml.appendAttribute(new Xml("score",			""));
		return(newXml);
	}

	/**
	 * Called to return the time between the last reboot and restart, by
	 * examining the annotation entries in manifest.xml's
	 * opbm.resultsdata.rawresults
	 * @return the interval between the noted shutdown and restart times
	 */
	public String getLastRebootTime()
	{
		Xml candidate, lastStartup, lastShutdown;
		String result;

		lastStartup		= null;
		lastShutdown	= null;
		candidate		= m_rawResults.getFirstChild();
		while (candidate != null)
		{	// See if this is a startup or shutdown annotation
			if (candidate.getName().equalsIgnoreCase("annotation"))
			{	// It's an annotation, see if it's a startup or shutdown annotation
				if (candidate.getAttributeNode("startup") != null)
				{	// It's a startup
					lastStartup = candidate.getAttributeNode("startup");

				} else if (candidate.getAttributeNode("shutdown") != null) {
					lastShutdown = candidate.getAttributeNode("shutdown");

				}
			}
			// Move to the next sibling
			candidate = candidate.getNext();
		}
		if (lastStartup != null && lastShutdown != null)
		{	// When we get here, we have our set, see if they're in the right order
			if (Utils.ensureTimestampsAreInOrder(lastShutdown.getText(), lastStartup.getText()))
			{	// Startup occurred after shutdown
				result = Double.toString((double)Utils.getMillisecondsBetweenTimestamps(lastShutdown.getText(), lastStartup.getText()) / 1000.0);
			} else {
				// Something's not recorded properly in manifest.xml, there should be a shutdown, and then a startup in that order
				result = "0.00000000000";
			}
		} else {
			// The result
			result = "0.00000000000";
		}
		return(result);
	}

	public String getLastPrebootPostbootRebootTime()
	{
		String prebootXmlFilename, postbootXmlFilename, result;
		Xml preboot, postboot;
		String preYear, preMonth, preDay, preHour, preMinute, preSecond;
		String postYear, postMonth, postDay, postHour, postMinute, postSecond;
		Date preDate, postDate;

		prebootXmlFilename	= Utils.getPrebootXmlFilename();
		postbootXmlFilename	= Utils.getPostbootXmlFilename();

		preboot		= Opbm.loadXml(prebootXmlFilename,	m_opbm);
		postboot	= Opbm.loadXml(postbootXmlFilename,	m_opbm);

		result = "0.00000000001";
		if (preboot != null && postboot != null)
		{
			preYear		= preboot.getChild("opbm.time.year");
			preMonth	= preboot.getChild("opbm.time.month");
			preDay		= preboot.getChild("opbm.time.day");
			preHour		= preboot.getChild("opbm.time.hh");
			preMinute	= preboot.getChild("opbm.time.mm");
			preSecond	= preboot.getChild("opbm.time.ss");

			postYear	= postboot.getChild("opbm.time.year");
			postMonth	= postboot.getChild("opbm.time.month");
			postDay		= postboot.getChild("opbm.time.day");
			postHour	= postboot.getChild("opbm.time.hh");
			postMinute	= postboot.getChild("opbm.time.mm");
			postSecond	= postboot.getChild("opbm.time.ss");

			if (preYear  != null && preMonth  != null && preDay  != null && preHour  != null && preMinute  != null && preSecond  != null &&
				postYear != null && postMonth != null && postDay != null && postHour != null && postMinute != null && postSecond != null)
			{	// We're good
				preDate		= Utils.createDate(Utils.getValueOf(preYear, 0),
											   Utils.getValueOf(preMonth, 0),
											   Utils.getValueOf(preDay, 0),
											   Utils.getValueOf(preHour, 0),
											   Utils.getValueOf(preMinute, 0),
											   Utils.getValueOf(preSecond, 0));

				postDate	= Utils.createDate(Utils.getValueOf(postYear, 0),
											   Utils.getValueOf(postMonth, 0),
											   Utils.getValueOf(postDay, 0),
											   Utils.getValueOf(postHour, 0),
											   Utils.getValueOf(postMinute, 0),
											   Utils.getValueOf(postSecond, 0));

				result = Long.toString((postDate.getTime() - preDate.getTime()) / 1000);
			}
		}
		return(result);
	}

	public Xml getResultsDataRoot()				{	return(m_resultsdataRoot);		}
	public Xml getResultsDataRawResults()		{	return(m_rawResults);			}
	public Xml getResultsDataResults()			{	return(m_results);				}
	public Xml getResultsDataDetails()			{	return(m_details);				}
	public Xml getResultsDataAggregate()		{	return(m_aggregate);			}
	public Xml getResultsDataAggregateByAtom()	{	return(m_aggregateByAtom);		}

	private Opbm			m_opbm;
	BenchmarkManifest		m_bm;						// Parent this results processor relates back to
	private boolean			m_isLoaded;					// When the results have been created or successfully loaded, this flag is raised high

	private Xml				m_resultsdataRoot;			// opbm.resultsdata
	private Xml				m_rawResults;				// opbm.resultsdata.rawResults
	private Xml				m_results;					// opbm.resultsdata.results
	private Xml				m_details;					// opbm.resultsdata.results.details
	private Xml				m_aggregate;				// opbm.resultsdata.results.aggregate
	private Xml				m_aggregateByAtom;			// opbm.resultsdata.results.aggregate.byAtom
	private Xml				m_aggregateByIteration;		// opbm.resultsdata.results.aggregate.byIteration
}
