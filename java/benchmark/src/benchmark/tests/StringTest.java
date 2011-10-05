/*
 * OPBM's Java Benchmark -- String Builder test
 *
 * This class is the top-level class of the OPBM Java Benchmark string builder
 * test.  It is a single-threaded class that this workload:
 *
 *		o 32KB String construction
 *
 * To run the test:
 *		(new StringTest()).run()
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

public class StringTest
{
	/**
	 * Constructor
	 * @param handle assigned for the app
	 */
	public StringTest(int handle)
	{
		m_handle = handle;
	}

	/**
	 * Runs the test
	 */
	public void run()
	{
		char c;
		int i, j, length, insertAt;
		float completed, increment, next;
		byte[] baseline;
		StringBuilder sb;
		Random rBuildBaseline, rCharInBaseline, rCharToInsert, rInsertAt;

		// Generate a baseline random sequence of 256 alphanumeric characters
		baseline = new byte[_BASELINE_STRING_LENGTH];
		rBuildBaseline = new Random(12192011);
		for (i = 0; i < _BASELINE_STRING_LENGTH; i++)
			baseline[i] = (byte)(rBuildBaseline.nextFloat() * (float)(_BASELINE_STRING_LENGTH - 1));

		// #1 - Populate the list with random length strings up to 8KB
		Benchmark.reportTestN(m_handle, Benchmark.m_testNumber++, "Build " + Integer.toString(AesData.m_aesOriginal.length) + " Strings");
		// Process through populating the list with
		rCharInBaseline	= new Random(12192011);		// Used to pull in a randomly generated character from baseline
		completed		= 0.0f;
		increment		= 1.0f / (float)AesData.m_aesOriginal.length;
		next			= 0.01f;
		for (i = 0; i < AesData.m_aesOriginal.length; i++)
		{	// Populate it with random characters from baseline
			AesData.m_aesOriginal[i] = new byte[_AES_STRING_LENGTH];
			for (j = 0; j < _AES_STRING_LENGTH; j++)
			{	// Grab a character from our pseudo-randomly created list of characters above
				AesData.m_aesOriginal[i][j] = baseline[(int)(rCharInBaseline.nextFloat() * (float)(_BASELINE_STRING_LENGTH - 1))];
			}

			completed += increment;
			if (completed > next)
			{	// Report our progress
				Benchmark.reportCompletionN(m_handle, completed);
				next += 0.01f;
			}
		}
		// When we get here, our list is populated with random-length text from _MIN_AES_STRING_LENGTH to _MAX_AES_STRING_LENGTH characters in length


		// #2 - Populate a 32KB String using the StringBuilder class
		Benchmark.reportTestN(m_handle, Benchmark.m_testNumber++, "Build 256KB String");
		// Process through populating the list with
		sb				= new StringBuilder(0);
		rCharToInsert	= new Random(12192011);		// Handles the character to insert
		rInsertAt		= new Random(12192011);		// Handles the location within sb to insert it at
		completed		= 0.0f;
		increment		= 1.0f / (float)_STRING_LENGTH;
		next			= 0.01f;
		for (i = 0; i < _STRING_LENGTH; i++)
		{	// Grab our random character from baseline
			c = (char)baseline[(int)(rCharToInsert.nextFloat() * (float)(_BASELINE_STRING_LENGTH - 1))];

			// Find out where we should insert the character
			if (i % 2 == 0 && i != 0)
			{	// Every other character we insert a character
				insertAt = (int)(rInsertAt.nextFloat() * (float)(sb.length() - 1));
				sb.insert(insertAt, c);
			} else {
				// And every alternate character we append a character
				sb.append(c);
			}

			completed += increment;
			if (completed > next)
			{	// Report our progress
				Benchmark.reportCompletionN(m_handle, completed);
				next += 0.01f;
			}
		}
		// When we get here, the StringBuilder string is produced
	}

	// Class variables
	private int					m_handle;
	private static final int	_STRING_LENGTH				= 32768 * 8;		// 256KB
	private static final int	_BASELINE_STRING_LENGTH		= 256;				// One for every ANSI+128 character
	private static final int	_AES_STRING_LENGTH			= 2048;				// 2KB
}
