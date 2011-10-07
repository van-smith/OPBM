/*
 * OPBM's Java Benchmark -- Low-level Timing Functions
 *
 * This class records individual events.
 *
 * -----
 * Last Updated:  Oct 6, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.0
 *
 */

package benchmark.common;

import benchmark.Benchmark;

public class NanoTimer
{
	public NanoTimer()
	{
		m_didStart	= false;
		m_didStop	= false;
		m_start		= System.nanoTime();
		m_stop		= m_start;
	}

	public void start()
	{
		m_start		= System.nanoTime();
		m_didStart	= true;
	}

	public void stop()
	{
		m_stop		= System.nanoTime();
		m_didStop	= true;
	}

	public long diff()
	{
		if (m_didStart && m_didStop)
			return(m_stop - m_start);
		else
			return(0);
	}

	public long elapsed()
	{
		return(System.nanoTime() - m_start);
	}

	public long elapsedAndRestart()
	{
		long time	= System.nanoTime() - m_start;
		m_start		= System.nanoTime();
		m_didStart	= true;
		return(time);
	}

	public double secondsElapsed()
	{
		return((double)elapsed() / _ONE_BILLION);
	}

	public double secondsElapsedAndRestart()
	{
		return((double)elapsedAndRestart() / _ONE_BILLION);
	}

	public static void sleep(int duration)
	{
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
		}
	}

//////////
// Methods below are in support of summary timing data in bulk, and not
// in capturing individual nanotimes.
/////
	/**
	 * Initialize every entry to -1, which sets it up to be ignored if it is
	 * not recorded during the run.
	 * @param times
	 */
	public void initializeTimes(long[] times)
	{
		for (int i = 0; i < times.length; i++)
			times[i] = -1;
	}

	/**
	 * Process the long[] times array, and derive min/max/avg values
	 */
	public void processTimes(long[]		times,
							 String		description,
							 int		handle)
	{
		double min, max, avg, geo, cv;
		int i, count;

		min	= Double.MAX_VALUE;
		max	= Double.MIN_VALUE;
		avg	= 0.0;
		geo = 0.0;
		cv	= 0.0;
		count	= 0;
		for (i = 0; i < times.length; i++)
		{	// We ignore -1 entries, as they were never populated with valid times
			if (times[i] != -1)
			{
				if (times[i] < min)
					min = times[i];

				if (times[i] > max)
					max = times[i];

				avg		+= times[i];
				++count;
			}
		}

		if (count != 0)
		{	// Compute avg, geo and cv
			avg /= count;
// REMEMBER
		}

		// Convert to seconds
		min	/= _ONE_BILLION;
		max	/= _ONE_BILLION;
		avg /= _ONE_BILLION;
		geo /= _ONE_BILLION;
		cv	/= _ONE_BILLION;

		// Report the times to JBM
		Benchmark.reportTestScoreAndTimeN(handle, description, /*score*/ min, max, avg, geo, cv,
															   /* time*/ min, max, avg, geo, cv);
	}

	private boolean		m_didStart;
	private boolean		m_didStop;
	private long		m_start;
	private long		m_stop;

	private static final double		_ONE_BILLION = 1000000000;
}
