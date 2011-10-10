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
							 int		handle,
							 double		baselineTime)
	{
		double time, minTime, maxTime, avgTime, geoTime, cvTime;
		double score, minScore, maxScore, avgScore, geoScore, cvScore;
		int i, count;

		// Total times,
		minTime		= Double.MAX_VALUE;
		maxTime		= Double.MIN_VALUE;
		avgTime		= 0.0;
		geoTime		= 0.0;
		cvTime		= 0.0;
		// and scores,
		minScore	= Double.MAX_VALUE;
		maxScore	= Double.MIN_VALUE;
		avgScore	= 0.0;
		geoScore	= 0.0;
		cvScore		= 0.0;
		// ror valid entries
		count		= 0;
		for (i = 0; i < times.length; i++)
		{	// We ignore -1 entries, as they were never populated with valid times
			if (times[i] != -1)
			{	// Compute both time and score data
				time		= times[i] / _ONE_BILLION;
				score		= (baselineTime / time) * 100.0;

				avgTime		+= time;
				avgScore	+= score;

				if (time < minTime)
				{
					minTime		= time;
					minScore	= score;
				}

				if (time > maxTime)
				{
					maxTime		= time;
					maxScore	= score;
				}

				// Increase the count for this entry
				++count;
			}
		}

		if (count != 0)
		{	// Compute avg, geo and cv
			avgTime		/= count;
			avgScore	/= count;
// REMEMBER to add geo and cv here
		}

		// Report the times to JBM
		Benchmark.reportTestScoreAndTimeN(handle, description, /*score*/ minScore,	maxScore,	avgScore,	geoScore,	cvScore,
															   /* time*/ minTime,	maxTime,	avgTime,	geoTime,	cvTime);
	}

	private boolean		m_didStart;
	private boolean		m_didStop;
	private long		m_start;
	private long		m_stop;

	private static final double		_ONE_BILLION = 1000000000;
}
