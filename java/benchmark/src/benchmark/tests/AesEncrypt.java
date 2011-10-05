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
import benchmark.tests.AesData;
import java.io.UnsupportedEncodingException;
import javax.crypto.*;


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
	 * Runs the test on the specified input strings
	 */
	public void run()
	{
		int i;
		float completed, increment, next;
		String encrypted, original;

		// We're in encryption mode
		AesData.setEncryptMode();

		// Run the test
		completed	= 0.0f;								// Start at the beginning
		increment	= 1.0f / (float)AesData.m_aesOriginal.length;		// Increase by this much each encryption
		next		= 0.01f;							// Begin reporting at 1%
		for (i = 0; i < AesData.m_aesOriginal.length; i++)
		{
			try {
				// Convert it
				AesData.m_aesEncrypted[i] = AesData.m_cipher.doFinal(AesData.m_aesOriginal[i]);

			} catch (IllegalBlockSizeException ex) {
				// A failure on conversion
				System.out.println("AES Encryption Failure:  Cipher reported illegal block size on string [" + Integer.toString(i) + "]");
				return;
			} catch (BadPaddingException ex) {
				// A failure on conversion
				System.out.println("AES Encryption Failure:  Cipher reported bad padding on string [" + Integer.toString(i) + "]");
				return;
			}
			completed += increment;
			if (completed > next)
			{	// Report its progress to the JBM
				Benchmark.reportCompletionN(m_handle, completed);
				next += 0.01f;
			}
		}
		// When we get here, we're good
	}


	// Class variables
	private int				m_handle;
}
