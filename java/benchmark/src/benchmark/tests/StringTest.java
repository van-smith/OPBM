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
 * Last Updated:  December 8, 2011
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
import benchmark.common.RandomData;
import java.util.Random;

public class StringTest
{

	// Class variables
	private JbmGui					m_jbm;
	private NanoTimer				m_nano;
	private	long[]					m_times;
	private	byte[]					m_baseline;

    final static String CHAR_POOL    
     = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ .,?;:!-";
    private Random  fRNG;
    private int     fRNGSeed = 1;
    private boolean fDebug = false;

	// Constants
	private static final int		_PASS1_WARMUP				= 5;				// Build it 150x for warmup
	private static final int		_PASS2_SCORING				= 10;				// Build it 450x for scoring
	private static final int		_PASS3_COOLDOWN				= 5;				// Build it 150x for cooldown
	private static final int		_MAX_PASSES					= _PASS1_WARMUP + _PASS2_SCORING + _PASS3_COOLDOWN;
	private static final int		_STRING_LENGTH				= 32768;			// 32KB
	private static final int		_BASELINE_STRING_LENGTH		= 256;				// One for every ANSI+128 character
	private static final double		_STRINGTEST_BASELINE_TIME	= 1.5085 / 4;		// Taken from reference machine, time to produce a score of 100.0
    
	/**
	 * Constructor
	 * @param handle assigned for the app
	 */
	public StringTest(int handle)
	{
		int i, j;

		m_jbm				= new JbmGui(handle, _MAX_PASSES);
		m_nano				= new NanoTimer();
		// Initialize our timing array
		m_times				= new long[_PASS2_SCORING];
		m_nano.initializeTimes(m_times);

// This is strange code.  See if it is necessary. -- Van 12.4.2011        
		// Generate a baseline random sequence of 256 alphanumeric characters
//		m_baseline = new byte[_BASELINE_STRING_LENGTH];
//		for (i = 0; i < _BASELINE_STRING_LENGTH; i++)
//			m_baseline[i] = (byte)(RandomData.m_rdStringBuildBaseline.nextFloat() * (float)(_BASELINE_STRING_LENGTH - 1));
//
//		// Populate the strings needed for AES encoding from the random baseline
//		for (i = 0; i < AesData.m_aesOriginal.length; i++)
//		{	// Populate it with random characters from baseline
//			AesData.m_aesOriginal[i] = new byte[AesData._AES_STRING_LENGTH];
//			for (j = 0; j < AesData._AES_STRING_LENGTH; j++)
//			{	// Grab a character from our pseudo-randomly created list of characters above
//				AesData.m_aesOriginal[i][j] = m_baseline[(int)(RandomData.m_rdStringCharInBaseline.nextFloat() * (float)(_BASELINE_STRING_LENGTH - 1))];
//			}
//		}
		// When we get here, our list is populated with random-length text from _MIN_AES_STRING_LENGTH to _MAX_AES_STRING_LENGTH characters in length
// END This is strange code.  See if it is necessary. -- Van 12.4.2011        
        
        fRNG = new Random( fRNGSeed );
        fRNGSeed++;
	}

	/**
	 * Runs the test
	 */
	public void run()
	{
		runPass(_PASS1_WARMUP, false);											// Warmup
		runPass(_PASS2_SCORING, true);											// Scoring
		runPass(_PASS3_COOLDOWN, false);										// Cooldown

		// Finished
		reportTiming();
	}

    // createRandomString generates a random string having a length of aLength.
    private String createRandomString( long aLength )
    {
        String lRandom = "";
        for( long i = 0; lRandom.length() < aLength; i++ )
        {
            lRandom += CHAR_POOL.charAt( 
                       fRNG.nextInt( CHAR_POOL.length() ) );
        }
        return lRandom;
    }
    
	/**
	 * Runs a pass, a portion of the test as each true test consists of three
	 * phases:  warmup, scoring, and cooldown.
	 * @param max
	 * @param keepScore
	 */
	public void runPass(int			max,
						boolean		keepScore)
	{
		int pass;
        String lCreateMe;

		// Create a 32KB String
		for (pass = 0; pass < max; pass++)
		{	// Each pass, record timing information
            lCreateMe = "";
			m_nano.start();
            lCreateMe = createRandomString( _STRING_LENGTH );
			if (keepScore)	m_times[pass] = m_nano.elapsed();
			// Update the JBM if need be
			m_jbm.increment();
            if( fDebug = true ) System.out.println( lCreateMe );        
		}
	}

	/**
	 * Reports the timing for this test
	 */
	public void reportTiming()
	{
		m_nano.processTimes(m_times, "Build String", m_jbm.getHandle(), _STRINGTEST_BASELINE_TIME);
	}

    public void setDebug( boolean Value ) 
    { 
        fDebug = Value; 
    } 

}
