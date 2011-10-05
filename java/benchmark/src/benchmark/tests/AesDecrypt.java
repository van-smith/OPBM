/*
 * OPBM's Java Benchmark -- AES Decrypt test
 *
 * This class is the top-level class of the OPBM Java Benchmark AES decryption
 * test.  It is a single-threaded class that this workload:
 *
 *		o AES decrypt
 *
 * To run the test:
 *		(new AesDecrypt()).run()
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
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class AesDecrypt
{
	/**
	 * Constructor
	 * @param handle assigned for the app
	 */
	public AesDecrypt(int handle)
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
		byte[] decrypted;

		// We're in encryption mode
		AesData.setDecryptMode();

		// Run the test
		completed	= 0.0f;								// Start at the beginning
		increment	= 1.0f / (float)AesData.m_aesEncrypted.length;		// Increase by this much each encryption
		next		= 0.01f;							// Begin reporting at 1%
		for (i = 0; i < AesData.m_aesEncrypted.length; i++)
		{	// Repeat for every input string, decrypting and comparing to the original
			try {
				// Convert it
				decrypted	= AesData.m_cipher.doFinal(AesData.m_aesEncrypted[i]);

				// Compare it
				if (!Arrays.equals(AesData.m_aesOriginal[i], decrypted))
				{	// We have a failure, which means the AES decryption engine did not work properly
					System.out.println("AES Decryption Failure:  Cipher decoded incorrectly on [" + Integer.toString(i) + "]");
					return;
				}

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
