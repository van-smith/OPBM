/*
 * OPBM's Java Benchmark -- STREAM
 *
 * This class has support for the STREAM bandwidth test, with four functions:
 *
 *		o Copy
 *		o Scale
 *		o Add
 *		o Triad
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

public final class Stream
{
	/**
	 * Constructor. The handle is used for communicating with the JBM.
	 */
	public Stream(int handle)
	{
		m_jbm		= new JbmGui(handle, _MAX_PASSES);
		m_nano		= new NanoTimer();

		// Allocate a,b,c arrays (100MB each)
		m_a = new double[_ARRAY_ELEMENTS];
		m_b = new double[_ARRAY_ELEMENTS];
		m_c = new double[_ARRAY_ELEMENTS];
		// Initialize the array
		for (int i = 0; i < _ARRAY_ELEMENTS; i++)
		{
			m_a[i] = 2.0;	// Per the example at http://www.cs.virginia.edu/stream/FTP/Contrib/Java/STREAM.java
			m_b[i] = 2.0;	// the arrays are initialized to a=1, b=2, c=0, though a is later reinitialized to
			m_c[i] = 0.0;	// 2.0e0 * a, hence its initialization to 2.0 initially here
		}

		// Initialize our timing array
		m_timesCopy		= new long[_MAX_PASSES];
		m_timesScale	= new long[_MAX_PASSES];
		m_timesAdd		= new long[_MAX_PASSES];
		m_timesTriad	= new long[_MAX_PASSES];

		m_nano.initializeTimes(m_timesCopy);
		m_nano.initializeTimes(m_timesScale);
		m_nano.initializeTimes(m_timesAdd);
		m_nano.initializeTimes(m_timesTriad);
	}


	/**
	 * Runs the STREAM test
	 */
	public void run()
	{
		for (m_pass = 0; m_pass < _MAX_PASSES; m_pass++)
		{	// Run the four tests, each handles its own timing
			copy();
			scale();
			add();
			triad();

			// Update the JBM after this pass
			m_jbm.increment();
		}
		// Finished
		reportTimings();
	}


	/**
	 * 1. COPY:  a( i ) = b( i )
	 * 8 bytes read + 8 bytes write per assignment = 16 bytes / assignment
	 *
	 * A Java-specific alternative algorithm to the copy:
	 *		System.arraycopy(m_b, 0, m_a, 0, _ARRAY_ELEMENTS);
	 */
	public void copy()
	{
		m_nano.start();

		for (int i = 0; i < _ARRAY_ELEMENTS; i++)
			m_a[i] = m_b[i];

		m_timesCopy[m_pass] = m_nano.elapsed();
	}

	/**
	 * 2. SCALE:  a( i ) = k * b( i )
	 * 8 bytes read + 8 bytes write per assignment = 16 bytes / assignment
	 */
	public void scale()
	{
		m_nano.start();

		for (int i = 0; i < _ARRAY_ELEMENTS; i++)
			m_a[i] = _SCALAR * m_b[i];

		m_timesScale[m_pass] = m_nano.elapsed();
	}

	/**
	 * 3. ADD:  a( i ) = b( i ) + c( i )
	 * 16 bytes read + 8 bytes write per assignment = 24 bytes / assignment
	 */
	public void add()
	{
		m_nano.start();

		for (int i = 0; i < _ARRAY_ELEMENTS; i++)
			m_a[i] = m_b[i] + m_c[i];

		m_timesAdd[m_pass] = m_nano.elapsed();
	}

	/**
	 * 4. TRIAD:  a( i ) = b( i ) + k * c( i )
	 * 16 bytes read + 8 bytes write per assignment = 24 bytes / assignment
	 */
	public void triad()
	{
		m_nano.start();

		for (int i = 0; i < _ARRAY_ELEMENTS; i++)
			m_a[i] = m_b[i] + _SCALAR * m_c[i];

		m_timesTriad[m_pass] = m_nano.elapsed();
	}

	/**
	 * Reports the timing for this test
	 */
	public void reportTimings()
	{
		m_nano.processTimes(m_timesCopy,	"STREAM Copy",		m_jbm.getHandle());
		m_nano.processTimes(m_timesScale,	"STREAM Scale",		m_jbm.getHandle());
		m_nano.processTimes(m_timesAdd,		"STREAM Add",		m_jbm.getHandle());
		m_nano.processTimes(m_timesTriad,	"STREAM Triad",		m_jbm.getHandle());
	}

	// Class variables
	private JbmGui						m_jbm;
	private NanoTimer					m_nano;
	private int							m_pass;

	// Test variables
	private double[]					m_a;
	private double[]					m_b;
	private double[]					m_c;

	private	long[]						m_timesCopy;
	private	long[]						m_timesScale;
	private	long[]						m_timesAdd;
	private	long[]						m_timesTriad;

	// Constants
	private static final int			_MAX_PASSES			= 10;
	private static final int			_ARRAY_ELEMENTS		= 12800000;			// 12.8 million * 8 bytes = 100 MB
	private static final int			_SCALAR				= 3;
}
