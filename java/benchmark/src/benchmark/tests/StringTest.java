/*
 * OPBM's Java Benchmark -- String Builder test
 *
 * This class is the top-level class of the OPBM Java Benchmark string builder
 * test.  It is a single-threaded class that this workload:
 *
 *		o 32KB String construction
 *
 * To run the test:
 *		(new StringTest()).run()
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

package benchmark.tests;

import benchmark.common.JbmGui;
import benchmark.common.NanoTimer;
import benchmark.common.RandomData;

public class StringTest
{
	/**
	 * Constructor
	 * @param handle assigned for the app
	 */
	public StringTest(int handle)
	{
		int i, j;

		m_jbm				= new JbmGui(handle, _MAX_PASSES);
		m_nano				= new NanoTimer();
		// Initialize our timing array
		m_times				= new long[_MAX_PASSES];
		m_nano.initializeTimes(m_times);

		// Generate a baseline random sequence of 256 alphanumeric characters
		m_baseline = new byte[_BASELINE_STRING_LENGTH];
		for (i = 0; i < _BASELINE_STRING_LENGTH; i++)
			m_baseline[i] = (byte)(RandomData.m_rdStringBuildBaseline.nextFloat() * (float)(_BASELINE_STRING_LENGTH - 1));

		// Populate the strings needed for AES encoding from the random baseline
		for (i = 0; i < AesData.m_aesOriginal.length; i++)
		{	// Populate it with random characters from baseline
			AesData.m_aesOriginal[i] = new byte[AesData._AES_STRING_LENGTH];
			for (j = 0; j < AesData._AES_STRING_LENGTH; j++)
			{	// Grab a character from our pseudo-randomly created list of characters above
				AesData.m_aesOriginal[i][j] = m_baseline[(int)(RandomData.m_rdStringCharInBaseline.nextFloat() * (float)(_BASELINE_STRING_LENGTH - 1))];
			}
		}
		// When we get here, our list is populated with random-length text from _MIN_AES_STRING_LENGTH to _MAX_AES_STRING_LENGTH characters in length
	}

	/**
	 * Runs the test
	 */
	public void run()
	{
		char c;
		int i, pass, insertAt;
		StringBuilder sb;

		// Populate a 32KB String using the StringBuilder class
		for (pass = 0; pass < _MAX_PASSES; pass++)
		{	// Each pass, record timing information
			m_nano.start();

			// Process through populating the list with
			sb = new StringBuilder(0);
			for (i = 0; i < _STRING_LENGTH; i++)
			{	// Grab our random character from baseline
				c = (char)m_baseline[(int)(RandomData.m_rdStringCharToInsert.nextFloat() * (float)(_BASELINE_STRING_LENGTH - 1))];

				// Find out where we should insert the character
				if (i % 2 == 0 && i != 0)
				{	// Every other character we insert a character
					insertAt = (int)(RandomData.m_rdStringInsertAt.nextFloat() * (float)(sb.length() - 1));
					sb.insert(insertAt, c);
				} else {
					// And every alternate character we append a character
					sb.append(c);
				}
			}
			// When we get here, the StringBuilder string is produced
			m_times[pass] = m_nano.elapsed();

			// Update the JBM if need be
			m_jbm.increment();
		}
		// Finished
		reportTiming();
	}

	/**
	 * Reports the timing for this test
	 */
	public void reportTiming()
	{
		m_nano.processTimes(m_times, "Build String", m_jbm.getHandle(), _STRINGTEST_BASELINE_TIME);
	}


	// Class variables
	private JbmGui					m_jbm;
	private NanoTimer				m_nano;
	private	long[]					m_times;
	private	byte[]					m_baseline;

	// Constants
	private static final int		_MAX_PASSES					= 750;				// Build it 750x over
	private static final int		_STRING_LENGTH				= 32768;			// 32KB
	private static final int		_BASELINE_STRING_LENGTH		= 256;				// One for every ANSI+128 character
	private static final double		_STRINGTEST_BASELINE_TIME	= 0.0256620821;		// Taken from reference machine, time to produce a score of 100.0
}
