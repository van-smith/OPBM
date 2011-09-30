/*
 * OPBM's Java Benchmark -- SHA-256 test
 *
 * This class is the top-level class of the OPBM Java Benchmark SHA-256
 * test.  It is a single-threaded class that this workload:
 *
 *		o SHA-256
 *
 * To run the test:
 *		(new SHA256()).run()
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

public class SHA256
{
	/**
	 * Constructor
	 * @param handle assigned for the app
	 */
	public SHA256(int handle)
	{
		m_handle = handle;
	}

	/**
	 * Runs the test
	 */
	public void run()
	{
	}

	// Class variables
	private int				m_handle;
}
