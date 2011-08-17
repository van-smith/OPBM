/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is a container class for data items and parameters used during
 * benchmarking.  It has only those methods necessary to process the data
 * consumed or referenced during benchmark execution.
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
import opbm.common.Xml;

public class BenchmarkParams
{

	/**
	 * The line must be in the form "description,timing,ofBaselinePercent", where
	 * description is something like "Launch program" and timing is something
	 * like "1.4839839838393" (indicating seconds), and ofBaselinePercent is
	 * something like "99.73833938262" where 100 is the baseline.
	 * Populates into m_status, m_timing, m_ofBaseline
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

	public Opbm					m_opbm;
	public Macros				m_macroMaster;
	public Settings				m_settingsMaster;

	public Benchmarks			m_parent;
	public BenchmarksAtom		m_bpAtom;
	public WaitUntilIdle		m_wui;

	public boolean				m_retry;
	public int					m_retryAttempts;

	public List<Xml>			m_benchmarkStack;
	public boolean				m_headsUpActive;
	public HUD					m_hud;

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

	// Used to process the timing lines, as in "timing,Launch Program,1.58392837639873,102.38908379387398
	public String				m_line;
	public int					m_firstComma;
	public int					m_secondComma;
	public String				m_timingName;
	public double				m_timingInSeconds;
	public double				m_timingOfBaseline;

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
}
