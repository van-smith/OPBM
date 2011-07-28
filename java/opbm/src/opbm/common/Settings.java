/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package opbm.common;

import opbm.Opbm;

/**
 *
 * @author rick
 */
public class Settings
{
	public Settings()
	{
		Xml settings;
		String translucency;
		double value;

		settings = Opbm.loadXml("settings.xml");
		if (settings != null)
		{	// Use xml-based settings
			m_debugMode			= settings.getChild("opbm.settings.benchmarks.debugger").equalsIgnoreCase("yes");
			m_singleStepping	= settings.getChild("opbm.settings.benchmarks.singlestep").equalsIgnoreCase("yes");
			m_hudVisible		= settings.getChild("opbm.settings.benchmarks.hud.visible").equalsIgnoreCase("yes");
			translucency		= settings.getChild("opbm.settings.benchmarks.hud.translucency");
			value				= Double.valueOf(translucency);
			if (value > 1.0f || value < 0.0f)
				value = 0.67;
			m_hudTranslucency	= value;

		} else {
			// Use defaults
			m_debugMode			= true;
			m_singleStepping	= true;
			m_hudVisible		= true;
			m_hudTranslucency	= 0.67f;

		}
	}

	public boolean	isInDebugMode()					{	return(m_debugMode);					}
	public void		setDebugMode(boolean b)			{	m_debugMode = b;						}

	public boolean	isSingleStepping()				{	return(m_singleStepping);				}
	public void		setSingleStepping(boolean b)	{	m_singleStepping = b;					}

	public boolean	isHUDVisible()					{	return(m_hudVisible);					}
	public void		setHUDVisible(boolean b)		{	m_hudVisible = b;						}

	public float	getHUDTranslucency()			{	return((float)m_hudTranslucency);		}

	private boolean		m_debugMode;
	private boolean		m_singleStepping;
	private boolean		m_hudVisible;
	private double		m_hudTranslucency;
}
