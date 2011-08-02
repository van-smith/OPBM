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

public class WaitUntilIdleParams
{
	public WaitUntilIdleParams(int	percentageDesired,
							 int	duration,
							 int	timeout)
	{
		m_percentageDesired		= percentageDesired;
		m_duration				= duration;
		m_timeout				= timeout;
		m_returnCode			= 0;
	}

	public	int		m_percentageDesired;
	public	int		m_duration;
	public	int		m_timeout;
	public	int		m_returnCode;
}
