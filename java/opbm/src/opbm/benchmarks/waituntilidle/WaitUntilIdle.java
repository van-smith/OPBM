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

package opbm.benchmarks.waituntilidle;

import opbm.Opbm;

/**
 * WaitUntilIdle interface with Windows DLL for monitoring total system CPU
 * activity, allows the system to be "calmed down" between benchmark runs.
 * Called from the benchmark engine in <code>Benchmarks</code>
 * @author Rick C. Hodgin
 */
public class WaitUntilIdle
{
	public WaitUntilIdle()
	{
	}

	/**
	 * Called after a script is executed. Uses standard parameters to wait until
	 * the system drops below 5% activity for a 5 second contiguous span, and
	 * will timeout after 30 seconds if such a threshold isn't reached.
	 */
	public void pauseAfterScriptExecution()
	{	// Wait for up to 20 seconds for a 2-second interval of 10% or less CPU usage system-wide
		Opbm.waitUntilSystemIdle(10, 2000, 20000);
	}

	public void prepareBeforeScriptExecution()
	{
// REMEMBER need to set all registry keys prior to execution
	}

	public void cleanupAfterScriptExecution()
	{
// REMEMBER need to restore all registry keys after execution
// REMEMBER need to implement this to cleanup all temp files in the CSIDL scriptOutput\temp directory(ies)
	}
}
