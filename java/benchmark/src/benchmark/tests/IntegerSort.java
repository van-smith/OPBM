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
 * Last Updated:  Sep 30, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.2.0
 *
 */

package benchmark.tests;

import benchmark.Benchmark;

public class IntegerSort
{
	/**
	 * Constructor
	 * @param handle assigned for the app
	 */
	public IntegerSort(int handle)
	{
		m_handle = handle;
	}

	/**
	 * Runs the test
	 */
	public void run()
	{
		int i;

		for (i = 0; i < 20; i++)
		{
			Benchmark.reportCompletionN(m_handle, (float)i / 20);
			try {
				Thread.sleep(200);
			} catch (InterruptedException ex) {
			}
		}
	}

	// Class variables
	private int				m_handle;
}
