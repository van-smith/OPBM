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
		m_jbm				= new JbmGui(handle, _MAX_PASSES);
		m_nano				= new NanoTimer();
		// Initialize our timing array
		m_times				= new long[_MAX_PASSES];
		m_nano.initializeTimes(m_times);
	}

	/**
	 * Runs the test on the specified input strings
	 */
	public void run()
	{
		int i, pass;
		byte[] decrypted;

		// We're in encryption mode
		AesData.setDecryptMode();

		// Repeat the test however many times
		for (pass = 0; pass < _MAX_PASSES; pass++)
		{
			// Run the test
			m_nano.start();
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
				// When we get here, we're good
			}
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
		m_nano.processTimes(m_times, "AES Decrypt", m_jbm.getHandle(), _AESDECRYPT_BASELINE_TIME);
	}

	// Class variables
	private JbmGui					m_jbm;
	private NanoTimer				m_nano;
	private	long[]					m_times;

	// Constants
	private static final int		_MAX_PASSES						= 50;				// Build it 50x over
	private static final double		_AESDECRYPT_BASELINE_TIME		= 0.5761001149;		// Taken from reference machine, time to produce a score of 100.0
}
