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
					   int		passes,
					   int		integers)
	{
		m_max_passes		= passes;
		m_max_integers		= integers;

		m_list1				= new Long[m_max_integers];		// For ascending sort
		m_list2				= new Long[m_max_integers];		// For descending sort

		m_jbm				= new JbmGui(handle, passes);
		m_nano				= new NanoTimer();

		// Initialize our timing array
		m_times				= new long[passes];
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
		int i, pass;

		for (pass = 0; pass < m_max_passes; pass++)
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
			m_times[pass] = m_nano.elapsed();

			// Update the JBM after this pass
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
		m_nano.processTimes(m_times, "Integer Sort", m_jbm.getHandle());
	}


	// Class variables
	private JbmGui				m_jbm;
	private NanoTimer			m_nano;
	private int					m_max_passes;
	private int					m_max_integers;
	private	long[]				m_times;
	private Long[]				m_list1;
	private Long[]				m_list2;
}
