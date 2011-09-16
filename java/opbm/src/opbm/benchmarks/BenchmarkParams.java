/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is a container class for data items and parameters used during
 * benchmarking.  It has only those methods necessary to process the data
 * consumed or referenced during benchmark execution.
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

import java.text.NumberFormat;
import opbm.benchmarks.hud.StreamGobbler;
import opbm.benchmarks.environment.Variables;
import opbm.benchmarks.environment.Stack;
import opbm.benchmarks.hud.HUD;
import opbm.benchmarks.debugger.Debugger;
import java.util.List;
import opbm.common.Macros;
import opbm.Opbm;
import opbm.benchmarks.waituntilidle.WaitUntilIdle;
import opbm.common.Settings;
import opbm.common.Tuple;
import opbm.common.Utils;
import opbm.common.Xml;

public class BenchmarkParams
{

	/**
	 * The line must be in the form "description,timing,ofBaselinePercent", where
	 * description is something like "Launch program" and timing is something
	 * like "1.4839839838393" (indicating seconds), and ofBaselinePercent is
	 * something like "99.73833938262" where 100 is the baseline.
	 * Populates into m_status, m_timing, m_ofBaseline
	 *
	 * If coming from a "timing,Launch program,1.4839839838393,99.73833938262",
	 * use output.getText().substring(7) to access the "Launch program" part
	 * directly.
	 *
	 * @param line "Launch program,1.4839839838393,99.73833938262"
	 * @sets m_status = "Launch Program"
	 * @sets m_timing = 1.4839839838393
	 * @sets m_ofBaseline = 99.73833938262
	 */
	public void extractTimingLineElements(String line)
	{
		m_line			= line;
		m_firstComma	= line.indexOf(",");
		m_secondComma	= line.indexOf(",", m_firstComma + 1);

		extractTimingStatus();
		extractTimingTiming();
		extractTimingOfBaseline();
	}

	public void extractTimingStatus()
	{
		if (m_firstComma != -1)
			m_timingName = m_line.substring(0, m_firstComma);
		else
			m_timingName = "";
	}

	public void extractTimingTiming()
	{
		String s;

		if (m_secondComma != -1)
		{
			s = m_line.substring(m_firstComma + 1, m_secondComma);
			m_timingInSeconds = Double.valueOf(s);
		} else {
			m_timingInSeconds = 0.0;
		}
	}

	public void extractTimingOfBaseline()
	{
		if (m_secondComma != -1 && m_secondComma < m_line.length())
			m_timingOfBaseline = Double.valueOf(m_line.substring(m_secondComma + 1));
		else
			m_timingOfBaseline = 0.0;
	}

	/**
	 * Returns the atom associated with this BenchmarkParams instance
	 * @return
	 */
	public BenchmarksAtom getBPAtom()
	{
		return(m_bpAtom);
	}

	/**
	 * Each time a worklet within an atom is processed, results are updated and
	 * stored.  This accessor returns "success" or "failure"
	 * @return "success" or "failure"
	 */
	public String getLastWorkletResult()			{	return(m_lastWorkletResult);		}

	/**
	 * Each time a worklet within an atom is processed, the number of retry
	 * attempts are recorded.
	 * @return the number of retries, or 0 if went through on first try
	 */
	public int getLastWorkletRetries()				{	return(m_lastWorkletRetries);		}

	/**
	 * Start time of the worklet
	 * @return
	 */
	public String getLastWorkletStart()				{	return(m_lastWorkletStart);			}

	/**
	 * End time upon worklet process completion
	 * @return
	 */
	public String getLastWorkletEnd()				{	return(m_lastWorkletEnd);			}

	/**
	 * Score, as assigned by the geometric mean from data provided for by the script
	 * @return
	 */
	public String getLastWorkletScore()				{	return(m_lastWorkletScore);			}

	/**
	 * Raw timing data lines as provided for by the script
	 * @return
	 */
	public Tuple getLastWorkletTimingData()			{	return(m_lastWorkletTimingData);	}

	/**
	 * Return the number of milliseconds between the end and start of the last
	 * worklet as recorded in BenchmarksAtom
	 * @return total milliseconds elapsed on last worklet process run
	 */
	public long getMillisecondsRunningLastWorklet()
	{
		return(m_lastWorkletAccumulationTotal);
	}

	/**
	 * Each time a worklet within an atom is processed, results are updated and
	 * stored.  This accessor returns "success" or "failure"
	 * @param r result code "success" or "failuire"
	 */
	public void setLastWorkletResult(String r)		{	m_lastWorkletResult = r;			}

	/**
	 * Each time a worklet within an atom is processed, the number of retry
	 * attempts are recorded.
	 * @param r number of retries
	 */
	public void setLastWorkletRetries(int r)		{	m_lastWorkletRetries = r;			}

	/**
	 * Start time of the worklet
	 * @param t start time
	 */
	public void setLastWorkletStart(String t)
	{
		m_lastWorkletAccumulationTotal	= 0;
		m_lastWorkletStart				= t;
	}

	/**
	 * End time upon worklet process completion
	 * @param t end time
	 */
	public void setLastWorkletEnd(String t)
	{
		m_lastWorkletEnd = t;
	}

	/**
	 * Computes the time between end and start, and adds it to an accumulation
	 * total for the worklet runtime
	 */
	public void accumulateLastWorkletTime()
	{
		m_lastWorkletAccumulationTotal += Utils.millisecondsBetweenTimestamps(m_lastWorkletStart, m_lastWorkletEnd);
	}

	/**
	 * Score as assigned by the script
	 * @param s score
	 */
	public void setLastWorkletScore(String s)		{	m_lastWorkletScore = s;				}

	/**
	 * When the score is computed, these are/were the timing lines it used
	 * @param t
	 */
	public void setLastWorkletTimingData(Tuple t)	{	m_lastWorkletTimingData = t;		}

	/**
	 * Points to the output data captured, which should hold timing data
	 *		<outputs>
	 *			<output date="Wed Aug 24 16:06:10 CDT 2011" millisecond="1314219970772">timing,Launch Adobe Acrobat 10.1 Installer, 11.5386581878518, 72.3408759520074</output>
	 *			<output date="Wed Aug 24 16:07:18 CDT 2011" millisecond="1314220038918">timing,Install Adobe Acrobat 10.1, 67.9449565959183, 42.640666829038</output>
	 *			<output date="Wed Aug 24 16:07:20 CDT 2011" millisecond="1314220040419">timing,Launch Adobe Acrobat 10.1, 1.50383500529185, 48.3342750994773</output>
	 *		</outputs>
	 * @param outputs pointer to the outputs tag
	 */
	public void computeLastWorkletScore(Xml outputs)
	{
		int i, count;
		double power, score;
		Xml output;
		NumberFormat nf = NumberFormat.getNumberInstance();
		Tuple timingData;


		timingData = new Tuple(m_opbm);
		//	first	= timing item description (as in "Launch Excel")
		//	second	= time
		//	third	= score

		count	= 0;
		output	= outputs.getFirstChild();
		while (output != null)
		{
			if (output.getText().toLowerCase().contains("timing,"))
			{	// This is a timing line
				extractTimingLineElements(output.getText().substring(7));
				timingData.add(m_timingName, Double.valueOf(m_timingInSeconds), Double.valueOf(m_timingOfBaseline));
				++count;
			}

			// Move to next output sibling
			output = output.getNext();
		}

		if (timingData.isEmpty())
		{	// A failure, nothing was recorded, so indicate the same
			setLastWorkletScore("0.0");
			setLastWorkletTimingData(null);
			return;
//			timingData.add("--failure--", Double.valueOf(0.0), Double.valueOf(0.0));
//			count = 1;
		}

		// When we get here, we have all of our timings
		score = 0.0f;
		if (count != 0)
		{	// Compute a geometric mean
			power = 1.0 / (double)count;

			// Perform the n_root(a1)... * n_root(an) computation
			for (i = 0; i < timingData.size(); i++)
			{
				if (score == 0.0)
					score = Math.pow((Double)timingData.getThird(i), power);
				else
					score *= Math.pow((Double)timingData.getThird(i), power);
			}
		}

		// Store the score and continue
		nf.setMinimumIntegerDigits(0);
		nf.setMaximumIntegerDigits(3);
		nf.setMinimumFractionDigits(5);
		nf.setMaximumFractionDigits(5);
		setLastWorkletScore(nf.format(score));
		setLastWorkletTimingData(timingData);
	}

	/**
	 * Returns the current state of the m_debuggerOrHUDAction setting
	 * @return
	 */
	public String getDebuggerOrHUDActionReason()
	{
		switch (m_debuggerOrHUDAction)
		{
			case _NO_ACTION:
				return("normal");

			case _SINGLE_STEP:
				return("single-stepping");

			case _RUN:
				return("running");

			case _STOP:
				return("general stopping, no reason given");

			case _STOPPED_DUE_TO_FAILURE_ON_ALL_RETRIES:
				return("retry attempts failure count exceeded");

			case _STOP_USER_CLICKED_STOP:
				return("user clicked stop");

			case _STOP_FAILURE_ON_REBOOT_WRITE_REGISTRY:
				return("failure on reboot write registry entry");

			default:
				return("unknown code " + Integer.toBinaryString(m_debuggerOrHUDAction));
		}
	}

	/**
	 * Clear the conflicts list (if any) to remove old, stale data
	 */
	public void clearConflicts()
	{
		m_conflicts.clear();
	}

	/**
	 * Clear the resolutions list (if any) to remove old, stale data
	 */
	public void clearResolutions()
	{
		m_resolutions.clear();
	}

	/**
	 * Add a conflict to the conflict array list.  They should be added
	 * with a 1:1 ratio with resolutions.
	 * @param conflict text describing the conflict
	 */
	public void addConflict(String conflict)
	{
		m_conflicts.add(conflict);
	}

	/**
	 * Add a resolution to the resolution array list.  They should be added
	 * with a 1:1 ratio with conflicts.
	 * @param resolution text describing the resolution
	 */
	public void addResolution(String resolution)
	{
		m_resolutions.add(resolution);
	}

	public Opbm					m_opbm;
	public Macros				m_macroMaster;
	public Settings				m_settingsMaster;
	public Benchmarks			m_parent;
	public BenchmarksAtom		m_bpAtom;
	public BenchmarkManifest	m_bm;
	public WaitUntilIdle		m_wui;

	public boolean				m_retry;
	public int					m_retryAttempts;

	public List<Xml>			m_benchmarkStack;
	public HUD					m_hud;
	public boolean				m_hudActive;
	public boolean				m_hudDebugInfo;

	public boolean				m_debuggerActive;
	public boolean				m_singleStepping;
	public int					m_debugLastAction;
	public Debugger				m_deb;
	public int					m_debuggerOrHUDAction;
	public Xml					m_debugParent;
	public Xml					m_debugChild;

	public int					m_thisIteration;
	public int					m_maxIterations;

	public Xml					m_xmlRoot;
	public Xml					m_xmlRun;
	public Xml					m_xmlResults;
	public Xml					m_xmlResultsLastSuite;
	public Xml					m_xmlResultsLastScenario;
	public Xml					m_xmlResultsLastMolecule;
	public Xml					m_xmlResultsLastAtom;

	public List<Variables>		m_atomVariables;
	public List<Variables>		m_moleculeVariables;
	public List<Variables>		m_scenarioVariables;
	public List<Variables>		m_suiteVariables;

	public List<Stack>			m_atomStack;
	public List<Stack>			m_moleculeStack;
	public List<Stack>			m_scenarioStack;
	public List<Stack>			m_suiteStack;

	public List<String>			m_errorArray;
	public StreamGobbler		m_errorGobbler;

	public List<String>			m_outputArray;
	public StreamGobbler		m_outputGobbler;

	public List<String>			m_conflicts;
	public List<String>			m_resolutions;

// Timing line elements
// Used to process the timing lines
// as in "timing,Launch Program,1.58392837639873,102.38908379387398
	public String				m_line;
	public int					m_firstComma;
	public int					m_secondComma;
	// Values obtained from extractTimingLineElements():
	public String				m_timingName;
	public double				m_timingInSeconds;
	public double				m_timingOfBaseline;

	// Conditions set when an atom's worklet is run:
	private String				m_lastWorkletResult;				// "success" or "failure"
	private int					m_lastWorkletRetries;				// 0 through (m_retryAttempts-1)
	private String				m_lastWorkletStart;					// Tue Aug 16 16:39:51 CDT 2011 1313530812950
	private String				m_lastWorkletEnd;					// Tue Aug 16 16:39:51 CDT 2011 1313530812950
	private String				m_lastWorkletScore;					// Computed at the end of each script executed for the timing points contained within
	private Tuple				m_lastWorkletTimingData;			// Computed at the end of each script executed for the timing points contained within
	private long				m_lastWorkletAccumulationTotal;		// Holds an accumulation during retries of the time between scripts

// REMEMBER Data derived from CPUID library will go here

//////////
// Constants
	public final static int _NO_ACTION		= 0;
	public final static int _SINGLE_STEP	= 1;
	public final static int _RUN			= 2;

	// The following constants are all used to test a stop condition, providing
	// also an explanation as to why execution was stopped
	public final static int _STOP									= 100;
	public final static int _STOPPED_DUE_TO_FAILURE_ON_ALL_RETRIES	= 101;
	public final static int _STOP_USER_CLICKED_STOP					= 102;
	public final static int _STOP_FAILURE_ON_REBOOT_WRITE_REGISTRY	= 103;
}
