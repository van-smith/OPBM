/*
 * OPBM's Java Benchmark -- SHA-256 test
 *
 * This class is the top-level class of the OPBM Java Benchmark SHA-256
 * test.  It is a single-threaded class that this workload:
 *
 *		o SHA-256
 *
 * Perform SHA-256 upon a byte array using a Java message digest, as in:
 *		fDigestSha256 = MessageDigest.getInstance("SHA-256");
 *
 * Only the hashing operation is timed:
 *		long startTime = System.nanoTime();
 *		fDigestSha256.reset();
 *		lbyteHash = fDigestSha256.digest( fHashMe );
 *		endTime = System.nanoTime();
 *		totalTime = (endTime - startTime);
 *		totalTime /= ONE_BILLION;
 *
 * To run the test:
 *		(new SHA256()).run()
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256
{
	/**
	 * Constructor
	 * @param handle assigned for the app
	 */
	public SHA256(int handle)
	{
		m_jbm				= new JbmGui(handle, _MAX_PASSES);
		m_nano				= new NanoTimer();
		// Initialize our timing array
		m_times				= new long[_MAX_PASSES];
		m_nano.initializeTimes(m_times);
	}

	/**
	 * Runs the test
	 */
	public void run()
	{
		int i, pass;
		MessageDigest md;
		byte[] hash;

		// Allocate our SHA-256 message digest
		try {
			md = MessageDigest.getInstance("SHA-256");

		} catch (NoSuchAlgorithmException ex) {
			System.out.println("SHA-256 Failure:  Unable to get SHA-256 Message Digest instance.");
			return;
		}
		// If we get here, we're god

		// Repeat the test however many times
		for (pass = 0; pass < _MAX_PASSES; pass++)
		{
			// Perform the test
			m_nano.start();
			// Compute on the original data
			for (i = 0; i < AesData.m_aesOriginal.length; i++)
			{	// Compute the has for this item
				md.reset();
				hash = md.digest(AesData.m_aesOriginal[i]);
			}

			// Compute on the encrypted data
			for (i = 0; i < AesData.m_aesEncrypted.length; i++)
			{	// Compute the has for this item
				md.reset();
				hash = md.digest(AesData.m_aesEncrypted[i]);
			}
			// All done
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
		m_nano.processTimes(m_times, "SHA-256", m_jbm.getHandle());
	}

	// Class variables
	private JbmGui				m_jbm;
	private NanoTimer			m_nano;
	private	long[]				m_times;

	// Constants
	private static final int	_MAX_PASSES					= 40;				// Build it 40x over
}
