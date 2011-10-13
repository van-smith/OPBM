/*
 * OPBM's Java Benchmark -- Integer Sort test
 *
 * This class is the top-level class of the OPBM Java Benchmark integer sort
 * test.  It is a single-threaded class that this workload:
 *
 *		o Integer sort of 128KB 64-bit integer values
 *
 * To run the test:
 *		(new IntegerSort()).run()
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
import java.util.Arrays;
import java.util.Collections;

public class IntegerSort
{
	/**
	 * Constructor
	 * @param handle assigned for the app
	 */
	public IntegerSort(int		handle,
					   int		pass1_warmup,
					   int		pass2_scoring,
					   int		pass3_cooldown,
					   int		integers)
	{
		m_max_passes		= pass1_warmup + pass2_scoring + pass3_cooldown;
		m_pass1_warmup		= pass1_warmup;
		m_pass2_scoring		= pass2_scoring;
		m_pass3_cooldown	= pass3_cooldown;
		m_max_integers		= integers;

		m_list1				= new Long[m_max_integers];		// For ascending sort
		m_list2				= new Long[m_max_integers];		// For descending sort

		m_jbm				= new JbmGui(handle, m_max_passes);
		m_nano				= new NanoTimer();

		// Initialize our timing array
		m_times				= new long[pass2_scoring];
		m_nano.initializeTimes(m_times);
	}

	/**
	 * Runs the test multiple times through:
	 *
	 *		1)  Create 128K 64-bit pseudo-random integers
	 *			Java "long" variables are 64-bit, 128K * 8 = 1MB
	 *		2)  Sort the array in ascending order
	 *		3)  Sort the array in descending order
	 */
	public void run()
	{
		runPass(m_pass1_warmup, false);											// Warmup
		runPass(m_pass2_scoring, true);											// Scoring
		runPass(m_pass3_cooldown, false);										// Cooldown

		// Finished
		reportTiming();
	}

	/**
	 * Runs a pass, a portion of the test as each true test consists of three
	 * phases:  warmup, scoring, and cooldown.
	 * @param max
	 * @param keepScore
	 */
	public void runPass(int			max,
						boolean		keepScore)
	{
		int i, pass;

		for (pass = 0; pass < max; pass++)
		{
			// Step 1:  Generate the list
				for (i = 0; i < m_max_integers; i++)
				{	// Two lists are created, one for ascending sort, one for descending
					m_list1[i] = RandomData.m_rdIntegerSort.nextLong();
					m_list2[i] = m_list1[i];
				}

			m_nano.start();
			//////////
			// Test code
			//////
				// Step 2:  Sort the list in ascending
					Arrays.sort(m_list1);
				// Step 3:  Sort the list in descending
					Arrays.sort(m_list2, Collections.reverseOrder());
			//////
			// End
			//////////

			if (keepScore)
				m_times[pass] = m_nano.elapsed();	// Score it

			// Update the JBM after this pass
			m_jbm.increment();
		}
	}

	/**
	 * Reports the timing for this test
	 */
	public void reportTiming()
	{
		m_nano.processTimes(m_times, "Integer Sort", m_jbm.getHandle(), _INTEGERSORT_BASELINE_TIME);
	}


	// Class variables
	private	JbmGui					m_jbm;
	private	NanoTimer				m_nano;
	private	int						m_max_passes;
	private	int						m_pass1_warmup;
	private	int						m_pass2_scoring;
	private	int						m_pass3_cooldown;
	private	int						m_max_integers;
	private	long[]					m_times;
	private	Long[]					m_list1;
	private	Long[]					m_list2;

	private static final double		_INTEGERSORT_BASELINE_TIME	= 0.0095772565;		// Taken from reference machine, time to produce a score of 100.0
}
