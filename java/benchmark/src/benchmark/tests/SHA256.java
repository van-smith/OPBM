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
		m_handle = handle;
	}

	/**
	 * Runs the test
	 */
	public void run()
	{
		int i;
		float completed, increment, next;
		MessageDigest md;
		byte[] hash;

		// Allocate our SHA-256 message digest
		try {
			md = MessageDigest.getInstance("SHA-256");

		} catch (NoSuchAlgorithmException ex) {
			System.out.println("SHA-256 Failure:  Unable to get SHA-256 instance.");
			return;
		}
		// If we get here, we're god

		// Perform the test
		completed	= 0.0f;
		increment	= 1.0f / (float)AesData.m_aesOriginal.length / 2.0f;
		next		= 0.01f;

		// Compute on the original data
		for (i = 0; i < AesData.m_aesOriginal.length; i++)
		{	// Compute the has for this item
			md.reset();
			hash = md.digest(AesData.m_aesOriginal[i]);

			completed += increment;
			if (completed > next)
			{	// Report our progress
				Benchmark.reportCompletionN(m_handle, completed);
				next += 0.01f;
			}
		}

		// Compute on the encrypted data
		for (i = 0; i < AesData.m_aesEncrypted.length; i++)
		{	// Compute the has for this item
			md.reset();
			hash = md.digest(AesData.m_aesEncrypted[i]);

			completed += increment;
			if (completed > next)
			{	// Report our progress
				Benchmark.reportCompletionN(m_handle, completed);
				next += 0.01f;
			}
		}
		// All done
	}

	// Class variables
	private int				m_handle;
}
