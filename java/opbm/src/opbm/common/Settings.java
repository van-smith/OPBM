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
	public Settings(Opbm opbm)
	{
		String translucency, count;
		double value;

		// Initially set defaults
		m_debugMode			= true;
		m_singleStepping	= true;
		m_hudVisible		= true;
		m_hudTranslucency	= 0.67f;
		m_retry				= true;
		m_retryAttempts		= 3;
		m_retryStops		= false;

		// See if we can set real values
		m_settingsFilename	= Opbm.locateFile("settings.xml");
		m_settings			= Opbm.loadXml(m_settingsFilename);
		if (m_settings != null)
		{	// We can, use the xml-based settings
			System.out.println("Using " + m_settingsFilename);
			m_debugMode			= opbm.getMacroMaster().parseMacros(m_settings.getChild("opbm.settings.benchmarks.debugger")).equalsIgnoreCase("yes");
			m_singleStepping	= opbm.getMacroMaster().parseMacros(m_settings.getChild("opbm.settings.benchmarks.singlestep")).equalsIgnoreCase("yes");
			m_hudVisible		= opbm.getMacroMaster().parseMacros(m_settings.getChild("opbm.settings.benchmarks.hud.visible")).equalsIgnoreCase("yes");
			translucency		= opbm.getMacroMaster().parseMacros(m_settings.getChild("opbm.settings.benchmarks.hud.translucency"));
			value				= Double.valueOf(translucency);
			if (value > 1.0f || value < 0.0f)
				value = 0.67;
			m_hudTranslucency	= value;
			m_skin				= opbm.getMacroMaster().parseMacros(m_settings.getChild("opbm.settings.skin"));
			m_retry				= opbm.getMacroMaster().parseMacros(m_settings.getChild("opbm.settings.benchmarks.retry")).equalsIgnoreCase("yes");
			if (m_retry)
			{	// Grab the count
				try
				{
					count = opbm.getMacroMaster().parseMacros(m_settings.getAttributeOrChild("settings.benchmarks.retry.attempts"));
					if (count.isEmpty())
					{	// Nothing was specified, so we use the default
						count = "3";
					}
					m_retryAttempts = Integer.valueOf(count);
				} catch (NumberFormatException ex) {
				} catch (NullPointerException ex) {
				}
			}
			String test = m_settings.getChild("opbm.settings.benchmarks.retry.#stopIfFailureOnRetries");
			m_retryStops		= opbm.getMacroMaster().parseMacros(test).equalsIgnoreCase("yes");

		} else {
			// Use defaults
			System.out.println("Unable to locate settings.xml, using internal defaults.");

		}
		validateSkin();
		validateRetries();
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
			if (m_skin.toLowerCase().contains("simple"))
			{	// We're good
				return;

			} else if (m_skin.toLowerCase().contains("developer")) {
				// We're good
				return;
			}
		}
		// If we get here, it wasn't found, so we default to simple
		m_skin = "simple";
	}

	public void validateRetries()
	{
		if (m_retry)
		{	// Make sure the count is valid, between 0 and 10 retries
			m_retryAttempts = Math.min(Math.max(m_retryAttempts, 0), 10);
		}
	}

	public boolean isSimpleSkin()
	{
		if (m_skin != null && m_skin.toLowerCase().contains("simple"))
		{	// Yuppers
			return(true);
		}
		// If we get here, it's something else
		return(false);
	}

	public boolean isDeveloperSkin()
	{
		if (m_skin != null && m_skin.toLowerCase().contains("developer"))
		{	// Yuppers
			return(true);
		}
		// If we get here, it's something else
		return(false);
	}

	public boolean isBenchmarkToRetryOnErrors()
	{
		return(m_retry);
	}

	public int benchmarkRetryOnErrorCount()
	{
		return(m_retryAttempts);
	}

	public boolean benchmarkStopsIfRetriesFail()
	{
		return(m_retryStops);
	}

	public float getHUDTranslucency()
	{
		return((float)m_hudTranslucency);
	}

	private Xml			m_settings;
	private String		m_settingsFilename;
	private boolean		m_debugMode;
	private boolean		m_singleStepping;
	private boolean		m_hudVisible;
	private double		m_hudTranslucency;
	private boolean		m_retry;
	private int			m_retryAttempts;
	private boolean		m_retryStops;
	private String		m_skin;
}
