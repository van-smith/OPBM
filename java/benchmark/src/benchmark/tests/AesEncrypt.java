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
import javax.crypto.*;


public class AesEncrypt
{
	/**
	 * Constructor
	 * @param handle assigned for the app
	 */
	public AesEncrypt(int handle)
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

		// We're in encryption mode
		AesData.setEncryptMode();

		// Repeat the test however many times
		for (pass = 0; pass < _MAX_PASSES; pass++)
		{
			// Run the test
			m_nano.start();
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
		m_nano.processTimes(m_times, "AES Encrypt", m_jbm.getHandle(), _AESENCRYPT_BASELINE_TIME);
	}

	// Class variables
	private JbmGui					m_jbm;
	private NanoTimer				m_nano;
	private	long[]					m_times;

	// Constants
	private static final int		_MAX_PASSES						= 50;				// Build it 50x over
	private static final double		_AESENCRYPT_BASELINE_TIME		= 0.5286754978;		// Taken from reference machine, time to produce a score of 100.0
}
