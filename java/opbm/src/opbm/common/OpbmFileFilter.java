/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is used by the OPBM Office Productivity Benchmark.
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

package opbm.common;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class OpbmFileFilter extends FileFilter
{
	public OpbmFileFilter(String	extension,
						  String	description)
	{
		m_extension		= extension;
		m_description	= description;
	}


	@Override
	public boolean accept(File f)
	{
		String extension;

		// Accept all directories
		if (f.isDirectory())
			return true;

		// And XML files
		extension = Utils.getExtension(f.getName());
		if (m_extension.equalsIgnoreCase(extension))
			return true;

		// Everything else, not so much
		return false;
    }


    // The description of this filter
	@Override
    public String getDescription()
	{
        return m_description;
    }

	private String		m_extension;
	private String		m_description;
}
