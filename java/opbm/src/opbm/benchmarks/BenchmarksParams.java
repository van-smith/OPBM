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

import opbm.benchmarks.hud.StreamGobbler;
import opbm.benchmarks.environment.Variables;
import opbm.benchmarks.environment.Stack;
import opbm.benchmarks.hud.HUD;
import opbm.benchmarks.debugger.Debugger;
import java.util.List;
import opbm.common.Macros;
import opbm.Opbm;
import opbm.common.Settings;
import opbm.common.Xml;

public class BenchmarksParams
{

	/**
	 * The line must be in the form "description,timing,ofBaselinePercent", where
	 * description is something like "Launch program" and timing is something
	 * like "1.4839839838393" (indicating seconds), and ofBaselinePercent is
	 * something like "99.73833938262" where 100 is the baseline.
	 * Populates into m_status, m_timing, m_ofBaseline
	 * @param line
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
	public BenchmarksMolecule	m_bpMolecule;
	public BenchmarksScenario	m_bpScenario;
	public BenchmarksSuite		m_bpSuite;

	public List<Xml>			m_benchmarkStack;
	public boolean				m_headsUpActive;
	public HUD					m_hud;

	public boolean				m_debuggerActive;
	public boolean				m_singleStepping;
	public int					m_debugLastAction;
	public Debugger				m_deb;
	public int					m_debuggerAction;
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


	// Constants
	public final static int _NO_ACTION		= 0;
	public final static int _SINGLE_STEP	= 1;
	public final static int _RUN			= 2;
	public final static int _STOP			= 3;
}
