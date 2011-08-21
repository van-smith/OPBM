/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for benchmarking.  It executes scripts,
 * shows the heads-up display, displays the single-step debugger, etc.
 *
 * Last Updated:  Aug 01, 2011
 *
 * by Van Smith, Rick C. Hodgin
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @author Rick C. Hodgin
 * @version 1.0.2
 *
 */

package opbm.benchmarks.hud;

import opbm.common.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class StreamGobbler extends Thread
{
	public StreamGobbler(InputStream	stream,
						 List<String>	output,
						 String			id,
						 String			hudPrefixText,
						 HUD			hud)
    {
		m_stream			= stream;
        m_output			= output;
		m_id				= id;
		m_hudPrefixText		= hudPrefixText;
		m_lineKnownCount	= 0;
		m_lineTotalCount	= 0;
		m_hud				= hud;
    }

	@Override
    public void run()
    {
		String name;
		String line = null;

		try
		{
			InputStreamReader	isr	= new InputStreamReader(m_stream);
			BufferedReader		br	= new BufferedReader(isr);

			while ((line = br.readLine()) != null)
			{
				++m_lineTotalCount;

				if (line.toLowerCase().startsWith("status,"))
				{
					++m_lineKnownCount;
					if (m_hud != null)
						m_hud.updateStatus(line.substring(7), line);	// Update the status display with this information
				}

				if (line.toLowerCase().startsWith("timing,"))
				{
					++m_lineKnownCount;
					if (m_hud != null)
						m_hud.updateTiming(line.substring(7), line);	// Update the status display with this information
				}

				if (line.toLowerCase().startsWith("error,"))
				{
					++m_lineKnownCount;
					if (m_hud != null)
						m_hud.updateError(line.substring(6), line);		// Update the status display with this information
				}

				if (line.toLowerCase().startsWith("debug,"))
				{
					++m_lineKnownCount;
					if (m_hud != null)
						m_hud.updateDebug(line.substring(6), line);		// Update the status display with this information
				}

				name = "Running " + m_hudPrefixText + " (" + Integer.toString(m_lineKnownCount) + ((m_lineKnownCount != m_lineTotalCount) ? " + " + Integer.toString(m_lineTotalCount - m_lineKnownCount) : "") + " events logged)";
				if (m_hud != null)
					m_hud.updateName(name, name);

				m_output.add(Utils.getTimestamp() + ": " + line);
			}

		} catch (IOException ioe) {
			m_output.add(Utils.getTimestamp() + ": Exception encountered reading " + m_id + ": " + ioe.getMessage());

		}
    }

    private	InputStream		m_stream;
    private	List<String>	m_output;
	private	String			m_id;
	private String			m_hudPrefixText;
	private static int		m_lineKnownCount;
	private static int		m_lineTotalCount;
	private HUD				m_hud;
}