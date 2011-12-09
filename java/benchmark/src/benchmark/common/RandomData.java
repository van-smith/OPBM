/*
 * OPBM's Java Benchmark -- Random number functions
 *
 * This class is a wrapper providing random number support as needed throughout
 * the app.  It instantiates all random number generates, using 12/19/2011
 * (the final official release date of OPBM) as its seed value.
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

package benchmark.common;

import java.util.Random;

public final class RandomData
{
	/**
	 * Create the random engines needed by the various classes within this app.
	 * They are instantiated here, but used in the specified classes
	 */
	public static void initialize()
	{
		// Note: Always give them the same seed, so all machines will test
		// using the same pseudo-random data set
		m_rdIntegerSort			= new Random(12192011);
		m_rdStringBuildBaseline		= new Random(12192011);
		m_rdStringCharInBaseline	= new Random(12192011);
		m_rdStringCharToInsert		= new Random(12192011);
		m_rdStringInsertAt		= new Random(12192011);
	}

	// Create our pseudo-random starting points for each class
	public	static	Random			m_rdIntegerSort;						// IntegerSort
	public	static	Random			m_rdStringBuildBaseline;					// StringTest
	public	static	Random			m_rdStringCharInBaseline;					// StringTest
	public	static	Random			m_rdStringCharToInsert;						// StringTest
	public	static	Random			m_rdStringInsertAt;						// StringTest
}
