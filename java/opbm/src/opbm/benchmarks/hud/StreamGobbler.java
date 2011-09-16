/*
 * OPBM - Office Productivity Benchmark
 *
 * This class reads all STDIN and STDOUT data from the executing process,
 * gobbling it all up and pre-parsing it into a form used for the HUD during
 * benchmark script execution.
 *
 * Last Updated:  Sep 12, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.1.0
 *
 */

package opbm.benchmarks.hud;

import opbm.common.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import opbm.benchmarks.BenchmarkParams;

public class StreamGobbler extends Thread
{
	public StreamGobbler(InputStream		stream,
						 List<String>		output,
						 String				id,
						 String				hudPrefixText,
						 BenchmarkParams	bp,
						 HUD				hud)
    {
		m_stream			= stream;
        m_output			= output;
		m_id				= id;
		m_hudPrefixText		= hudPrefixText;
		m_lineKnownCount	= 0;
		m_lineTotalCount	= 0;
		m_bp				= bp;
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

			// Clear conflicts and resolutions upon instantiation
			m_bp.clearConflicts();
			m_bp.clearResolutions();

			while ((line = br.readLine()) != null)
			{
				++m_lineTotalCount;

				if (line.toLowerCase().startsWith("status,"))
				{
					++m_lineKnownCount;
					if (m_hud != null)
						m_hud.updateStatus(line.substring(7), line);	// Update the status display with this information

				} else if (line.toLowerCase().startsWith("timing,")) {
					++m_lineKnownCount;
					if (m_hud != null)
						m_hud.updateTiming(line.substring(7), line);	// Update the status display with this information

				} else if (line.toLowerCase().startsWith("error,")) {
					++m_lineKnownCount;
					if (m_hud != null)
						m_hud.updateError(line.substring(6), line);		// Update the status display with this information

				} else if (line.toLowerCase().startsWith("debug,")) {
					++m_lineKnownCount;
					if (m_hud != null)
						m_hud.updateDebug(line.substring(6), line);		// Update the status display with this information

				} else if (line.toLowerCase().startsWith("conflict,")) {
					// It is a tag identifying a conflict.  A resolution for this conflict must follow.
					m_bp.addConflict(line.substring(9).trim());

				} else if (line.toLowerCase().startsWith("resolution,")) {
					// It is a tag identifying a resolution to the previously noted conflict
					m_bp.addResolution(line.substring(11).trim());

// The following are not actually processed in any separate way at this stage,
// though the HUD could be modified to show a list of tests conducted.  But
// rather they are recorded for conveying information into manifest.xml and to
// the results viewer.
				} else if (line.toLowerCase().startsWith("overhead,")) {
					// Includes an overhead timing, not a timing time or score, but something to be totaled into the overhead grouping

				} else if (line.toLowerCase().startsWith("filter,")) {
					// It is a list of filters for this atom

				} else if (line.toLowerCase().startsWith("tags,")) {
					// It is a list of tags for the next timing entry alone (not the entire atom)

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

    private	InputStream			m_stream;
    private	List<String>		m_output;
	private	String				m_id;
	private String				m_hudPrefixText;
	private static int			m_lineKnownCount;
	private static int			m_lineTotalCount;
	private BenchmarkParams		m_bp;
	private HUD					m_hud;
}