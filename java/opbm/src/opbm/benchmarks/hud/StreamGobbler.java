/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
					m_hud.updateStatus(line.substring(7));	// Update the status display with this information
				}

				if (line.toLowerCase().startsWith("timing,"))
				{
					++m_lineKnownCount;
					m_hud.updateTiming(line.substring(7));	// Update the status display with this information
				}

				if (line.toLowerCase().startsWith("error,"))
				{
					++m_lineKnownCount;
					m_hud.updateError(line.substring(6));	// Update the status display with this information
				}

				if (line.toLowerCase().startsWith("debug,"))
				{
					++m_lineKnownCount;
					m_hud.updateDebug(line.substring(6));	// Update the status display with this information
				}

				m_hud.updateName("Running " + m_hudPrefixText + " (" + Integer.toString(m_lineKnownCount) + ((m_lineKnownCount != m_lineTotalCount) ? " + " + Integer.toString(m_lineTotalCount - m_lineKnownCount) : "") + " events logged)");

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