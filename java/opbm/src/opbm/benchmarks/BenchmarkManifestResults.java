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
 *			<results>
 *				<result manifestworkletuuid="whatever" start="Aug 22, 2011 at 11:56am" end="Aug 22, 2011 at 11:59am" tested="yes" status="success" score="99" uuid="whatever"/>
 *				<annotation manifestworkletuuid="whatever" rebootBegan="Tue Aug 16 16:39:51 CDT 2011 1313530812950" rebootEnded="Tue Aug 16 16:39:51 CDT 2011 1313530812950" delta="" uuid="whatever"/>
 *			</results>
 *			<details>
 *				<detail manifestworkletuuid="whatever" retry="1" attempts="5">
 *					<output>
 *					</output>
 *				</detail>
 *			</details>
 *			<aggregate>
 *				<timing>
 *					<byuuid>
 *						<uuid atomuuid="whatever" min="00:00" max="00:00" avg="00:00" mean="00:00" cv="00:00"/>
 *					</byuuid>
 *					<total>00:00</total>
 *				</timing>
 *				<scoring >
 *					<byuuid>
 *						<uuid atomuuid="whatever" min="00" max="00" avg="00" mean="00" cv="00"/>
 *					</byuuid>
 *					<total>99</total>
 *					<average>99</average>
 *					<mean>98</mean>
 *					<cv>5</cv>
 *				</scoring>
 *			</aggregate>
 *		</resultsdata>
 *
 * Last Updated:  Aug 24, 2011
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

/**
 *
 * @author rick
 */
public class BenchmarkManifestResults
{
	/**
	 * Constructor, this class is used for storing and querying results data
	 * by BenchmarkManifest and the Results Viewer
	 */
	public BenchmarkManifestResults(BenchmarkManifest bm)
	{
		m_bm		= bm;
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
			candidate	= m_results.getFirstChild();
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
			result = m_results.appendChild(new Xml("result"));

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
			candidate		= m_results.getFirstChild();
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
			annotation = m_results.appendChild(new Xml("annotation"));

			// Add the attribute and data to this entry
			annotation.appendAttribute(new Xml(attribute, data));
		}
	}

	/**
	 * Appends a result to the manifest based on the successful or failed
	 * completion of a worklet
	 * @param manifestWorkletUuid unique ID to the run worklet which spawned it
	 * @param startTime start time of the worklet (just before launch)
	 * @param endTime end time of the worklet (just after process termination)
	 * @param status "success" or "failure"
	 * @param score assigned if success, based on script-returned scoring
	 */
	public void appendResult(String		manifestWorkletUuid,
							 String		startTime,
							 String		endTime,
							 String		status,
							 String		score)
	{
		Xml result;

		result = new Xml("result");
		result.appendAttribute(new Xml("manifestworkletuuid",	manifestWorkletUuid));
		result.appendAttribute(new Xml("start",					startTime));
		result.appendAttribute(new Xml("end",					endTime));
		result.appendAttribute(new Xml("status",				status));
		result.appendAttribute(new Xml("score",					score));
		m_results.appendChild(result);
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
			detail.appendChild(success);
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
		m_results					= m_resultsdataRoot.appendChild(new Xml("results"));		// opbm.resultsdata.results
		m_details					= m_resultsdataRoot.appendChild(new Xml("details"));		// opbm.resultsdata.details
		m_aggregate					= m_resultsdataRoot.appendChild(new Xml("aggregate"));		// opbm.resultsdata.aggregate
		m_aggregateTiming			= m_aggregate.appendChild(new Xml("timing"));				// opbm.resultsdata.aggregate.timing
		m_aggregateTimingByUuid		= m_aggregateTiming.appendChild(new Xml("byuuid"));			// opbm.resultsdata.aggregate.timing.byuuid
		m_aggregateTimingTotal		= m_aggregateTiming.appendChild(new Xml("total"));			// opbm.resultsdata.aggregate.timing.total
		m_aggregateScoring			= m_aggregate.appendChild(new Xml("scoring"));				// opbm.resultsdata.aggregate.scoring
		m_aggregateScoringByUuid	= m_aggregateScoring.appendChild(new Xml("byuuid"));		// opbm.resultsdata.aggregate.scoring.byuuid
		m_aggregateScoringTotal		= m_aggregateScoring.appendChild(new Xml("total"));			// opbm.resultsdata.aggregate.scoring.total
		m_aggregateScoringAverage	= m_aggregateScoring.appendChild(new Xml("average"));		// opbm.resultsdata.aggregate.scoring.average
		m_aggregateScoringMean		= m_aggregateScoring.appendChild(new Xml("mean"));			// opbm.resultsdata.aggregate.scoring.mean
		m_aggregateScoringCV		= m_aggregateScoring.appendChild(new Xml("cv"));			// opbm.resultsdata.aggregate.scoring.cv
		m_aggregateTimingTotal.setText("0");
		m_aggregateScoringTotal.setText("0");
		m_aggregateScoringAverage.setText("0");
		m_aggregateScoringMean.setText("0");
		m_aggregateScoringCV.setText("0");
		m_aggregateScoringTotal.setText("0");
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
			m_results					= opbmXml.getAttributeOrChildNode("resultsdata.results");
			m_details					= opbmXml.getAttributeOrChildNode("resultsdata.details");
			m_aggregate					= opbmXml.getAttributeOrChildNode("resultsdata.aggregate");
			m_aggregateTiming			= opbmXml.getAttributeOrChildNode("resultsdata.aggregate.timing");
			m_aggregateTimingByUuid		= opbmXml.getAttributeOrChildNode("resultsdata.aggregate.timing.byuuid");
			m_aggregateTimingTotal		= opbmXml.getAttributeOrChildNode("resultsdata.aggregate.timing.total");
			m_aggregateScoring			= opbmXml.getAttributeOrChildNode("resultsdata.aggregate.scoring");
			m_aggregateScoringByUuid	= opbmXml.getAttributeOrChildNode("resultsdata.aggregate.scoring.byuuid");
			m_aggregateScoringTotal		= opbmXml.getAttributeOrChildNode("resultsdata.aggregate.scoring.total");
			m_aggregateScoringAverage	= opbmXml.getAttributeOrChildNode("resultsdata.aggregate.scoring.average");
			m_aggregateScoringMean		= opbmXml.getAttributeOrChildNode("resultsdata.aggregate.scoring.mean");
			m_aggregateScoringCV		= opbmXml.getAttributeOrChildNode("resultsdata.aggregate.scoring.cv");

			// Check everything
			if (m_resultsdataRoot == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata tag");
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
			if (m_aggregateTiming == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.aggregate.timing tag");
				break;
			}

			// Check everything
			if (m_aggregateTimingByUuid == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.aggregate.timing.byuuid tag");
				break;
			}

			// Check everything
			if (m_aggregateTimingTotal == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.aggregate.timing.total tag");
				break;
			}

			// Check everything
			if (m_aggregateScoring == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.aggregate.scoring tag");
				break;
			}

			// Check everything
			if (m_aggregateScoringByUuid == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.aggregate.scoring.byuuid tag");
				break;
			}

			// Check everything
			if (m_aggregateScoringTotal == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.aggregate.scoring.total tag");
				break;
			}

			// Check everything
			if (m_aggregateScoringAverage == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.aggregate.scoring.average tag");
				break;
			}

			// Check everything
			if (m_aggregateScoringMean == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.aggregate.scoring.mean tag");
				break;
			}

			// Check everything
			if (m_aggregateScoringCV == null)
			{	// fail
				m_bm.setError("Unable to locate opbm.resultsdata.aggregate.scoring.cv tag");
				break;
			}

			// We're good, set it as loaded and valid
			m_isLoaded	= true;
			isValid		= true;
			break;
		}
		return(isValid);
	}

	BenchmarkManifest		m_bm;						// Parent this results processor relates to
	private boolean			m_isLoaded;					// When the results have been created or successfully loaded, this flag is raised high

	private Xml				m_resultsdataRoot;			// opbm.resultsdata
	private Xml				m_results;					// opbm.resultsdata.results
	private Xml				m_details;					// opbm.resultsdata.results.details
	private Xml				m_aggregate;				// opbm.resultsdata.results.aggregate
	private Xml				m_aggregateTiming;			// opbm.resultsdata.results.aggregate.timing
	private Xml				m_aggregateTimingByUuid;	// opbm.resultsdata.results.aggregate.byuuid
	private Xml				m_aggregateTimingTotal;		// opbm.resultsdata.results.aggregate.total
	private Xml				m_aggregateScoring;			// opbm.resultsdata.results.scoring
	private Xml				m_aggregateScoringByUuid;	// opbm.resultsdata.results.scoring.byuuid
	private Xml				m_aggregateScoringTotal;	// opbm.resultsdata.results.scoring.total
	private Xml				m_aggregateScoringAverage;	// opbm.resultsdata.results.scoring.average
	private Xml				m_aggregateScoringMean;		// opbm.resultsdata.results.scoring.mean
	private Xml				m_aggregateScoringCV;		// opbm.resultsdata.results.scoring.cv
}
