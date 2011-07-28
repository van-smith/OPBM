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
	public Opbm					m_opbm;
	public Macros				m_macroMaster;
	public Settings				m_settingsMaster;

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
	public List<Stack>			m_atomStack;

	public List<String>			m_errorArray;
	public StreamGobbler		m_errorGobbler;

	public List<String>			m_outputArray;
	public StreamGobbler		m_outputGobbler;

	// Constants
	public final static int _NO_ACTION		= 0;
	public final static int _SINGLE_STEP	= 1;
	public final static int _RUN			= 2;
	public final static int _STOP			= 3;
}
