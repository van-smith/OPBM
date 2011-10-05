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
import java.util.Random;
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
		m_handle			= handle;
		m_max_pass_count	= passes;
		m_max_integers		= integers;
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
		float completed, passIncrement, next;
		Random r;
		Long[] list1 = new Long[m_max_integers];
		Long[] list2 = new Long[m_max_integers];

		// Create our pseudo-random starting point
		r = new Random(12192011);		// Note: Always give it the same seed, so all machines will test using the same pseudo-random data set

		// Initialize our total variables
		completed		= 0.0f;
		next			= 0.01f;
		passIncrement	= 1.0f / (float)m_max_pass_count;
		for (pass = 0; pass < m_max_pass_count; pass++)
		{	// Step 1:  Generate the list
			if (completed >= next)
				Benchmark.reportCompletionN(m_handle, completed);

			// Build the next part of the list
			for (i = 0; i < m_max_integers; i++)
			{	// Two lists are created, one for ascending sort, one for descending
				list1[i] = r.nextLong();
				list2[i] = list1[i];
			}

			// Step 2:  Sort the list in ascending
			if (completed >= next)
				Benchmark.reportCompletionN(m_handle, completed + (0.33f * passIncrement));
			Arrays.sort(list1);

			// Step 3:  Sort the list in descending
			if (completed >= next)
				Benchmark.reportCompletionN(m_handle, completed + (0.67f * passIncrement));
			Arrays.sort(list2, Collections.reverseOrder());

			// Move to the next display/report value if need be
			if (completed >= next)
				next += 0.01f;

			// Indicate this pass is complete
			completed += passIncrement;
		}
		// Finished
	}

	// Class variables
	private int					m_handle;
	private int					m_max_pass_count;
	private int					m_max_integers;
}
