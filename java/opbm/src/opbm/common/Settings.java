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

import opbm.Opbm;

public final class Settings
{
	public Settings()
	{
		String translucency;
		double value;

		m_settingsFilename	= Opbm.locateFile("settings.xml");
		m_settings			= Opbm.loadXml(m_settingsFilename);
		if (m_settings != null)
		{	// Use xml-based settings
			System.out.println("Using " + m_settingsFilename);
			m_debugMode			= m_settings.getChild("opbm.settings.benchmarks.debugger").equalsIgnoreCase("yes");
			m_singleStepping	= m_settings.getChild("opbm.settings.benchmarks.singlestep").equalsIgnoreCase("yes");
			m_hudVisible		= m_settings.getChild("opbm.settings.benchmarks.hud.visible").equalsIgnoreCase("yes");
			translucency		= m_settings.getChild("opbm.settings.benchmarks.hud.translucency");
			value				= Double.valueOf(translucency);
			if (value > 1.0f || value < 0.0f)
				value = 0.67;
			m_hudTranslucency	= value;
			m_skin				= m_settings.getChild("opbm.settings.skin");

		} else {
			// Use defaults
			System.out.println("Unable to locate settings.xml, using internal defaults.");
			m_debugMode			= true;
			m_singleStepping	= true;
			m_hudVisible		= true;
			m_hudTranslucency	= 0.67f;

		}
		validateSkin();
	}

	public boolean isInDebugMode()
	{
		return(m_debugMode);
	}

	public void setDebugMode(boolean b)
	{
		m_debugMode = b;
		saveSettings();
	}

	public boolean isSingleStepping()
	{
		return(m_singleStepping);
	}

	public void setSingleStepping(boolean b)
	{
		m_singleStepping = b;
		saveSettings();
	}

	public boolean isHUDVisible()
	{
		return(m_hudVisible);
	}

	public void setHUDVisible(boolean b)
	{
		m_hudVisible = b;
		saveSettings();
	}

	public void saveSettings()
	{
		m_settings.saveNode(m_settingsFilename);
	}

	public void validateSkin()
	{
		if (m_skin != null)
		{	// See if what was specified is valid
			if (m_skin.equalsIgnoreCase("simple"))
			{	// We're good
				return;

			} else if (m_skin.equalsIgnoreCase("developer")) {
				// We're good
				return;
			}
		}
		// If we get here, it wasn't found, so we default to simple
		m_skin = "simple";
	}

	public boolean isSimpleSkin()
	{
		if (m_skin != null && m_skin.equalsIgnoreCase("simple"))
		{	// Yuppers
			return(true);
		}
		// If we get here, it's something else
		return(false);
	}

	public boolean isDeveloperSkin()
	{
		if (m_skin != null && m_skin.equalsIgnoreCase("developer"))
		{	// Yuppers
			return(true);
		}
		// If we get here, it's something else
		return(false);
	}

	public float	getHUDTranslucency()			{	return((float)m_hudTranslucency);		}

	private Xml			m_settings;
	private String		m_settingsFilename;
	private boolean		m_debugMode;
	private boolean		m_singleStepping;
	private boolean		m_hudVisible;
	private double		m_hudTranslucency;
	private String		m_skin;
}
