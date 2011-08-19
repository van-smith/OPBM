/*
 * OPBM - Office Productivity Benchmark
 *
 * This class holds all data relative to the WaitUntilIdle() functionality
 * present in Opbm's static JNI interface (opbm64.dll or opbm32.dll depending
 * on which JVM is running).
 *
 * Last Updated:  Aug 18, 2011
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

import java.text.NumberFormat;
import opbm.Opbm;
import opbm.benchmarks.BenchmarkParams;
import opbm.common.Utils;

/**
 * WaitUntilIdle interface with Windows DLL for monitoring total system CPU
 * activity, allows the system to be "calmed down" between benchmark runs.
 * Called from the benchmark engine in <code>Benchmarks</code>
 * @author Rick C. Hodgin
 */
public class WaitUntilIdle
{
	public WaitUntilIdle(BenchmarkParams bp)
	{
		m_bp = bp;
	}

	/**
	 * Called after a script is executed. Uses standard parameters to wait until
	 * the system drops below X% activity for an Y millisecond contiguous span, and
	 * will timeout after Z milliseconds if such a threshold isn't reached.
	 */
	public void pauseAfterScriptExecution()
	{
		long before, after;
		float utilization, seconds;
		NumberFormat nf = NumberFormat.getNumberInstance();

		// Want a number like 1.23 or 12.34
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumIntegerDigits(1);
		nf.setMaximumIntegerDigits(2);

		// Update the HUD (if it's in use)
		if (m_bp != null & m_bp.m_hud != null)
		{
			m_bp.m_hud.updateStatus("Waiting until idle...");
		}

		// Capture starting time
		before		= Utils.getMillisecondTimer();

		// Wait for up to 20 seconds for a 2-second interval of 10% or less CPU usage system-wide
		utilization	= Opbm.waitUntilSystemIdle(10, 2000, 20000);

		// Capture ending time
		after		= Utils.getMillisecondTimer();
		seconds		= ((float)(after - before)) / (float)1000.0;

		// See where we are
		if (m_bp != null & m_bp.m_hud != null)
		{	// We only display the pause information if the hud is up
			if (utilization <= 10)
			{	// It was able to settle down
				m_bp.m_hud.updateStatus("Settled down to " + nf.format((double)utilization) + "% in " + nf.format((double)seconds) + " seconds");

			} else {
				// Was not able to settle down
				m_bp.m_hud.updateError("Failed.");
				m_bp.m_hud.updateError("Did not settle down after 20 seconds");
				m_bp.m_hud.updateError("Continuing with benchmark.");

			}
			try {
				// Wait an extra 2 seconds (For the hud to display it so people can see it)
				Thread.sleep(2000);

			} catch (InterruptedException ex) {
			}
		}
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


	private BenchmarkParams		m_bp;
}
