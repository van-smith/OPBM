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

/**
 * WaitUntilIdle interface with Windows DLL for monitoring total system CPU
 * activity, allows the system to be "calmed down" between benchmark runs.
 * Called from the benchmark engine in <code>Benchmarks</code>
 * @author Rick C. Hodgin
 */
public class WaitUntilIdle
{
//	static {
//		System.loadLibrary("\\windows\\system32\\opbm_WaitUntilIdle.dll");
//	}

	/**
	 * Called after a script is executed. Uses standard parameters to wait until
	 * the system drops below 5% activity for a 5 second contiguous span, and
	 * will timeout after 30 seconds if such a threshold isn't reached.
	 */
	public static void afterScriptExecution()
	{
//		// Wait for 5% or lower system activity over a period of 5 seconds, timeout after 30 seconds
//		WaitUntilIdleParams	info	= new WaitUntilIdleParams( 5, 5000, 30000 );
//		WaitUntilIdle		wui		= new WaitUntilIdle();
//
//		wui.WaitUntilSystemIdle(info);
		try {
			// Sleep for 2 seconds here before continuing
			Thread.sleep(2000);
		} catch (InterruptedException ex) {
		}
	}

//	/**
//	 * Function inside the 32-bit DLL.
//	 * @param info parameter block which passes WaitUntilIdleParams and receives
//	 * return code information
//	 */
//	public native void WaitUntilSystemIdle(WaitUntilIdleParams info);
}
