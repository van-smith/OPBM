/*
 * OPBM's Java Benchmark -- JBM interface
 *
 * This class holds variables to indicate when/where to update the JBM
 * status bar.
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

import benchmark.Benchmark;

public final class JbmGui
{
	public JbmGui(int		handle,
				  float		max)
	{
		m_handle = handle;
		reset(max);
	}

	/**
	 * Reset for movement from 0 to the specified max value
	 * @param max
	 */
	public void reset(float max)
	{
		m_completed		= 0.0f;
		m_increment		= 1.0f / (float)max;
		m_next			= _MIN_THRESHOLD;
	}

	/**
	 * Increment to the next item, and update the JBM status bar if we're past
	 * the minimum threshold
	 */
	public void increment()
	{
		m_completed += m_increment;
		if (m_completed > m_next)
		{	// Report its progress to the JBM
			Benchmark.reportCompletionN(m_handle, m_completed);
			m_next = m_completed + _MIN_THRESHOLD;
		}
	}

	public int getHandle()
	{
		return(m_handle);
	}

	private int		m_handle;
	private float	m_completed;
	private float	m_increment;
	private float	m_next;

	// Constant for reporting, every 1%
	private static final float		_MIN_THRESHOLD	= 0.01f;
}
