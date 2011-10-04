/*
 * OPBM's Java Benchmark -- Integer Sort test
 *
 * This class is the top-level class of the OPBM Java Benchmark AES encryption
 * test.  It is a single-threaded class that this workload:
 *
 *		o AES encrypt
 *
 * To run the test:
 *		(new AesEncrypt()).run()
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

public class AesEncrypt
{
	/**
	 * Constructor
	 * @param handle assigned for the app
	 */
	public AesEncrypt(int handle)
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
				Thread.sleep(100);
			} catch (InterruptedException ex) {
			}
		}
	}

	// Class variables
	private int				m_handle;
}
