/*
 * OPBM - Office Productivity Benchmark
 *
 * The settings tree looks like this
 * (Note: numbers are used for reference in extractSettingsXmlEntries()):
 *		#1	<opbm>
 *		#2		<settings>
 *		#3			<benchmarks>
 *		#4				<debugger>no</debugger>
 *		#5				<singlestep>no</singlestep>
 *		#6				<hud>
 *		#7					<visible>yes</visible>
 *		#8					<translucency>0.5</translucency>
 *		#9					<debuginfo>yes</debuginfo>
 *						</hud>
 *		#10				<retry>
 *		#11					<enabled>yes</enabled>
 *		#12					<attempts>5</attempts>
 *						</retry>
 *		#13				<stopIfFailure>yes</stopIfFailure>
 *		#15				<rebootBeforeEachPass>yes</rebootBeforeEachPass>
 *					</benchmarks>
 *		#14			<skin>developer</skin>
 *				</settings>
 *			</opbm>
 *
 * Last Updated:  Aug 20, 2011
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
		m_opbm = opbm;

		// See if we can set real values
		m_settingsFilename	= Opbm.locateFile("settings.xml");
		m_settingsRootXml	= Opbm.loadXml(m_settingsFilename);
		if (m_settingsRootXml == null)
		{	// Use defaults
			System.out.println("Unable to locate settings.xml");
			createDefaultSettingsXml();

		} else {
			// We can use the settings.xml they have
			System.out.println("Using " + m_settingsFilename);
			extractSettingsXmlEntries();
		}
	}

	/**
	 * Create the root node, and let the extractSettingsXmlEntries() populate
	 * all the missing defaults
	 */
	public void createDefaultSettingsXml()
	{
		m_settingsRootXml = new Xml();
		extractSettingsXmlEntries();
	}

	public void extractSettingsXmlEntries()
	{
		String translucency, count;
		double value;
		int updateCount = 0;

//////////
// OPBM
		m_opbmXml = m_settingsRootXml;
		if (m_opbmXml == null)
		{	// #1 - Create it
			++updateCount;
			m_opbmXml = new Xml("opbm");
			m_settingsRootXml = m_opbmXml;
			// "opbm" is root node
		}

//////////
// SETTINGS
		m_settingsXml = m_opbmXml.getChildNode("settings");
		if (m_settingsXml == null)
		{	// #2 - Create it
			++updateCount;
			m_settingsXml = new Xml("settings");
			m_opbmXml.appendChild(m_settingsXml);
			// "settings" goes on opbm node
		}

//////////
// BENCHMARKS
		m_benchmarksXml = m_settingsXml.getChildNode("benchmarks");
		if (m_benchmarksXml == null)
		{	// #3 - Create it
			++updateCount;
			m_benchmarksXml = new Xml("benchmarks");
			m_settingsXml.appendChild(m_benchmarksXml);
			// "benchmarks" goes on settings node
		}

//////////
// DEBUGGER
		m_debuggerXml = m_benchmarksXml.getChildNode("debugger");
		if (m_debuggerXml == null)
		{	// #4 - Create it and set default value
			++updateCount;
			m_debuggerXml = new Xml("debugger");
			m_benchmarksXml.appendChild(m_debuggerXml);
			// "debugger" goes on benchmarks node
			m_debuggerXml.setText("no");
			m_debugMode = false;

		} else {
			// #4 - Grab value
			m_debugMode	= m_opbm.getMacroMaster().parseMacros(m_debuggerXml.getText()).equalsIgnoreCase("yes");
		}

//////////
// SINGLE-STEP
		m_singleStepXml = m_benchmarksXml.getChildNode("debugger");
		if (m_singleStepXml == null)
		{	// #5 - Create it and set default value
			++updateCount;
			m_singleStepXml = new Xml("singlestep");
			m_benchmarksXml.appendChild(m_singleStepXml);
			// "singlestep" goes on benchmarks node
			m_singleStepXml.setText("no");
			m_singleStep = false;

		} else {
			// #5 - Grab value
			m_singleStep = m_opbm.getMacroMaster().parseMacros(m_singleStepXml.getText()).equalsIgnoreCase("yes");
		}

//////////
// HUD
		m_hudXml = m_benchmarksXml.getChildNode("hud");
		if (m_hudXml == null)
		{	// #6 - Create it
			++updateCount;
			m_hudXml = new Xml("hud");
			m_benchmarksXml.appendChild(m_hudXml);
			// "hud" goes on benchmarks node
		}

//////////
// HUD VISIBLE
		m_hudVisibleXml = m_hudXml.getChildNode("visible");
		if (m_hudVisibleXml == null)
		{	// #7 - Create it and set default value
			++updateCount;
			m_hudVisibleXml = new Xml("visible");
			m_hudXml.appendChild(m_hudVisibleXml);
			// "visible" goes on hud node
			m_hudVisibleXml.setText("yes");
			m_hudVisible = true;

		} else {
			// #4 - Grab value
			m_hudVisible = m_opbm.getMacroMaster().parseMacros(m_hudVisibleXml.getText()).equalsIgnoreCase("yes");
		}

//////////
// HUD TRANSLUCENCY
		m_hudTranslucencyXml = m_hudXml.getChildNode("translucency");
		if (m_hudTranslucencyXml == null)
		{	// #8 - Create it and set default value
			++updateCount;
			m_hudTranslucencyXml = new Xml("translucency");
			m_hudXml.appendChild(m_hudTranslucencyXml);
			// "translucency" goes on hud node
			m_hudTranslucencyXml.setText("0.5");
			m_hudTranslucency = 0.5;

		} else {
			// #4 - Grab value
			translucency	= m_opbm.getMacroMaster().parseMacros(m_hudTranslucencyXml.getText());
			value			= Double.valueOf(translucency);
			if (value > 1.0f || value < 0.0f)
				value = 0.5;
			m_hudTranslucency	= value;
		}

//////////
// HUD DEBUG INFO
		m_hudDebugInfoXml = m_hudXml.getChildNode("debuginfo");
		if (m_hudDebugInfoXml == null)
		{	// #9 - Create it and set default value
			++updateCount;
			m_hudDebugInfoXml = new Xml("debuginfo");
			m_hudXml.appendChild(m_hudDebugInfoXml);
			// "debugInfo" goes on hud node
			m_hudDebugInfoXml.setText("yes");
			m_hudDebugInfo = true;

		} else {
			// #9 - Grab value
			m_hudDebugInfo = m_opbm.getMacroMaster().parseMacros(m_hudDebugInfoXml.getText()).equalsIgnoreCase("yes");
		}

//////////
// RETRY
		m_retryXml = m_benchmarksXml.getChildNode("retry");
		if (m_retryXml == null)
		{	// #10 - Create it
			++updateCount;
			m_retryXml = new Xml("retry");
			m_benchmarksXml.appendChild(m_retryXml);
			// "retry" goes on benchmarks node
		}

//////////
// RETRY ENABLED
		m_retryEnabledXml = m_retryXml.getChildNode("enabled");
		if (m_retryEnabledXml == null)
		{	// #11 - Create it and set default value
			++updateCount;
			m_retryEnabledXml = new Xml("enabled");
			m_retryXml.appendChild(m_retryEnabledXml);
			// "enabled" goes on retry node
			m_retryEnabledXml.setText("yes");
			m_retryEnabled = true;

		} else {
			// #11 - Grab value
			m_retryEnabled = m_opbm.getMacroMaster().parseMacros(m_retryEnabledXml.getText()).equalsIgnoreCase("yes");
		}

//////////
// RETRY ATTEMPTS
		m_retryAttemptsXml = m_retryXml.getChildNode("attempts");
		if (m_retryAttemptsXml == null)
		{	// #12 - Create it and set default value
			++updateCount;
			m_retryAttemptsXml = new Xml("enabled");
			m_retryXml.appendChild(m_retryAttemptsXml);
			// "attempts" goes on retry node
			m_retryAttemptsXml.setText("yes");
			m_retryAttempts = 5;

		} else {
			// #12 - Grab value
			count = m_opbm.getMacroMaster().parseMacros(m_retryAttemptsXml.getText());
			if (count.isEmpty())
			{	// Nothing was specified, so we use the default
				m_retryAttempts = 5;
			} else {
				m_retryAttempts = Utils.getValueOf(count, 5);
			}
		}
		validateRetryAttempts();

//////////
// STOP IF FAILURE
		m_stopIfFailureXml = m_benchmarksXml.getChildNode("stopIfFailure");
		if (m_stopIfFailureXml == null)
		{	// #11 - Create it and set default value
			++updateCount;
			m_stopIfFailureXml = new Xml("stopIfFailure");
			m_benchmarksXml.appendChild(m_stopIfFailureXml);
			// "stopIfFailure" goes on benchmarks node
			m_stopIfFailureXml.setText("yes");
			m_stopIfFailure = true;

		} else {
			// #11 - Grab value
			m_stopIfFailure = m_opbm.getMacroMaster().parseMacros(m_stopIfFailureXml.getText()).equalsIgnoreCase("yes");
		}

//////////
// REBOOT BEFORE EACH PASS
		m_rebootBeforeEachPassXml = m_benchmarksXml.getChildNode("rebootBeforeEachPass");
		if (m_rebootBeforeEachPassXml == null)
		{	// #15 - Create it and set default value
			++updateCount;
			m_rebootBeforeEachPassXml = new Xml("rebootBeforeEachPass");
			m_benchmarksXml.appendChild(m_rebootBeforeEachPassXml);
			// "stopIfFailure" goes on benchmarks node
			m_rebootBeforeEachPassXml.setText("yes");
			m_rebootBeforeEachPass = true;

		} else {
			// #11 - Grab value
			m_rebootBeforeEachPass = m_opbm.getMacroMaster().parseMacros(m_rebootBeforeEachPassXml.getText()).equalsIgnoreCase("yes");
		}

//////////
// SKIN
		m_skinXml = m_settingsXml.getChildNode("skin");
		if (m_skinXml == null)
		{	// #11 - Create it and set default value
			++updateCount;
			m_skinXml = new Xml("skin");
			m_settingsXml.appendChild(m_skinXml);
			// "skin" goes on settings node
			m_skinXml.setText("simple");
			m_skin = "simple";

		} else {
			// #11 - Grab value
			m_skin = m_opbm.getMacroMaster().parseMacros(m_skinXml.getText());
		}
		validateSkin();


		// All done!
		if (updateCount != 0)
		{	// Tell the user we had to reset some internal defaults
			System.out.println("Warning:  Updated " + Integer.toString(updateCount) + " default " + Utils.singularOrPlural(updateCount, "value", "values") + " in settings.xml");
		}
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
		return(m_singleStep);
	}

	public void setSingleStepping(boolean b)
	{
		m_singleStep = b;
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
		// Update the entries in the Xml
		m_singleStepXml.setText(Utils.evaluateLogicalToYesOrNo(m_singleStep));
		m_hudVisibleXml.setText(Utils.evaluateLogicalToYesOrNo(m_hudVisible));
		m_hudTranslucencyXml.setText(Double.toString(m_hudTranslucency));
		m_hudDebugInfoXml.setText(Utils.evaluateLogicalToYesOrNo(m_hudDebugInfo));
		m_retryEnabledXml.setText(Utils.evaluateLogicalToYesOrNo(m_retryEnabled));
		m_retryAttemptsXml.setText(Integer.toString(m_retryAttempts));
		m_stopIfFailureXml.setText(Utils.evaluateLogicalToYesOrNo(m_stopIfFailure));
		m_rebootBeforeEachPassXml.setText(Utils.evaluateLogicalToYesOrNo(m_rebootBeforeEachPass));
		m_skinXml.setText(m_skin);

		// Save the Xml to the local user's settings directory
		m_settingsRootXml.saveNode(Opbm.getSettingsDirectory() + "settings.xml");
	}

	public void validateSkin()
	{
		if (m_skin != null)
		{	// See if what was specified is valid
			if (m_skin.toLowerCase().contains("simple"))
			{	// We're good
				m_skinXml.setText(m_skin);
				return;

			} else if (m_skin.toLowerCase().contains("developer")) {
				// We're good
				m_skinXml.setText(m_skin);
				return;
			}
		}
		// If we get here, it wasn't found, so we default to simple
		m_skin = "simple";
		m_skinXml.setText(m_skin);
	}

	public void validateRetryAttempts()
	{
		if (m_retryEnabled)
		{	// Make sure the count is valid, between 0 and 10 retries
			m_retryAttempts = Math.min(Math.max(m_retryAttempts, 0), 10);
			m_retryAttemptsXml.setText(Integer.toString(m_retryAttempts));
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
		return(m_retryEnabled);
	}

	public int benchmarkRetryOnErrorCount()
	{
		return(m_retryAttempts);
	}

	public boolean benchmarkStopsIfRetriesFail()
	{
		return(m_stopIfFailure);
	}

	public boolean benchmarkRebootBeforeEachPass()
	{
		return(m_rebootBeforeEachPass);
	}

	public boolean getHUDVisible()
	{
		return(m_hudVisible);
	}

	public float getHUDTranslucency()
	{
		return((float)m_hudTranslucency);
	}

	public boolean getHUDDebugInfo()
	{
		return(m_hudDebugInfo);
	}

	public void toggleHUDDebugInfo()
	{
		m_hudDebugInfo = !m_hudDebugInfo;
		saveSettings();
	}

	public void toggleHUDTranslucency()
	{
		if (!m_hudVisible)
		{	// Turn it on
			m_hudVisible		= true;
			m_hudTranslucency	= 0.25f;

		} else {
			// Cycle through the values
			if (m_hudTranslucency <= 0.25f)
			{	// Make it 50%
				m_hudTranslucency = 0.50f;

			} else if (m_hudTranslucency <= 0.50f) {
				// Make it 75%
				m_hudTranslucency = 0.75f;

			} else if (m_hudTranslucency <= 0.75f) {
				// Make it 100%
				m_hudTranslucency = 1.00f;

			} else {
				// Turn it off
				m_hudVisible = false;
			}
		}
		saveSettings();
	}

	public void toggleRetryAttempts()
	{
		++m_retryAttempts;
		if (m_retryAttempts > 10)
		{	// Reset to 0
			m_retryAttempts = 0;
		}
		saveSettings();
	}

	public int getRetryAttempts()
	{
		return(m_retryAttempts);
	}

	public Xml getSettingsXml()
	{
		return(m_settingsRootXml);
	}

	/**
	 * Returns Xml pointer to the raw data element by name.  Note:  any changes
	 * made here will be overwritten by the Settings class if the user changes
	 * any settings through an accessor.  Should only be used for cloning.
	 * @param element
	 * @return
	 */
	public Xml getSettingsXml(String element)
	{
		if (element.equalsIgnoreCase("opbm"))
		{	// They want offset to the root node
			return(m_opbmXml);
		} else if (element.equalsIgnoreCase("opbm.settings")) {
			// They want offset to this node
			return(m_settingsXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks")) {
			// They want offset to this node
			return(m_benchmarksXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks.debugger")) {
			// They want offset to this node
			return(m_debuggerXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks.debugger.singleStep")) {
			// They want offset to this node
			return(m_singleStepXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks.debugger.hud")) {
			// They want offset to this node
			return(m_hudXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks.debugger.hud.visible")) {
			// They want offset to this node
			return(m_hudVisibleXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks.debugger.hud.translucency")) {
			// They want offset to this node
			return(m_hudTranslucencyXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks.debugger.debugInfo")) {
			// They want offset to this node
			return(m_hudDebugInfoXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks.retry")) {
			// They want offset to this node
			return(m_retryXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks.retryEnabled")) {
			// They want offset to this node
			return(m_retryEnabledXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks.retryAttempts")) {
			// They want offset to this node
			return(m_retryAttemptsXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks.stopIfFailure")) {
			// They want offset to this node
			return(m_stopIfFailureXml);
		} else if (element.equalsIgnoreCase("opbm.settings.benchmarks.rebootBeforeEachPass")) {
			// They want offset to this node
			return(m_rebootBeforeEachPassXml);
		} else if (element.equalsIgnoreCase("opbm.settings.skin")) {
			// They want offset to this node
			return(m_skinXml);
		} else {
			return(null);
		}
	}

	/**
	 * Validates the settings for the settings.xml root Xml that's passed
	 * @param settings pointer to a settings.xml
	 * @param isFullSettings the BenchmarkManifest uses a subset of the full settings.xml structure
	 * @return true or false if the settings there are validated
	 */
	public boolean validateSettings(Xml			settingsXml,
									boolean		isFullSettings)
	{
		boolean isValid;
		String translucency, count;
		double value;
		Xml debuggerXml, singleStepXml, hudXml, hudVisibleXml, hudTranslucencyXml, hudDebugInfoXml, retryXml, retryEnabledXml, retryAttemptsXml, stopIfFailureXml, rebootBeforeEachPassXml;

		isValid = false;
		if (isFullSettings)
		{	// Not yet supported
			System.out.println("Error: Validating full settings.xml file is not yet supported.");

		} else {
			// Validate the BenchmarkManifest subset, which is everything from opbm.settings.benchmarks and deeper
			do
			{
//////////
// DEBUGGER
				debuggerXml = settingsXml.getAttributeOrChildNode("debugger");
				if (debuggerXml == null)
				{	// #4 - fail
					break;
				}

//////////
// SINGLE-STEP
				singleStepXml = settingsXml.getAttributeOrChildNode("singleStep");
				if (singleStepXml == null)
				{	// #5 - fail
					break;
				}

//////////
// HUD
				hudXml = settingsXml.getAttributeOrChildNode("hud");
				if (hudXml == null)
				{	// #6 - fail
					break;
				}

//////////
// HUD VISIBLE
				hudVisibleXml = hudXml.getAttributeOrChildNode("visible");
				if (hudVisibleXml == null)
				{	// #7 - failed
					break;
				}

//////////
// HUD TRANSLUCENCY
				hudTranslucencyXml = hudXml.getAttributeOrChildNode("translucency");
				if (hudTranslucencyXml == null)
				{	// #8 - failed
					break;

				} else {
					// #4 - Grab value
					translucency	= m_opbm.getMacroMaster().parseMacros(m_hudTranslucencyXml.getText());
					value			= Double.valueOf(translucency);
					if (value < 0.0f || value > 1.0f)
					{	// fail
						break;
					}
				}

//////////
// HUD DEBUG INFO
				hudDebugInfoXml = hudXml.getAttributeOrChildNode("debuginfo");
				if (hudDebugInfoXml == null)
				{	// #9 - fail
					break;
				}

//////////
// RETRY
				retryXml = settingsXml.getAttributeOrChildNode("retry");
				if (retryXml == null)
				{	// #10 - Create it
					break;
				}

//////////
// RETRY ENABLED
				retryEnabledXml = retryXml.getAttributeOrChildNode("enabled");
				if (retryEnabledXml == null)
				{	// #11 - fail
					break;
				}

//////////
// RETRY ATTEMPTS
				retryAttemptsXml = retryXml.getAttributeOrChildNode("attempts");
				if (retryAttemptsXml == null)
				{	// #12 - fail
					break;

				} else {
					// #12 - Grab value
					count = m_opbm.getMacroMaster().parseMacros(m_retryAttemptsXml.getText());
					if (count.isEmpty())
					{	// fail
						break;
					} else {
						if (Utils.getValueOf(count, 5) < 0)
						{	// fail
							break;
						}
					}
				}

//////////
// STOP IF FAILURE
				stopIfFailureXml = settingsXml.getAttributeOrChildNode("stopIfFailure");
				if (stopIfFailureXml == null)
				{	// #11 - fail
					break;
				}

//////////
// REBOOT BEFORE EACH PASS
				rebootBeforeEachPassXml = settingsXml.getAttributeOrChildNode("rebootBeforeEachPass");
				if (rebootBeforeEachPassXml == null)
				{	// #15 - fail
					break;
				}

				// If we get here, everything's acceptable
				isValid = true;
				break;
				
			} while (true);
		}
		return(isValid);
	}

	private Opbm		m_opbm;
	private Xml			m_settingsRootXml;
	private String		m_settingsFilename;
	private boolean		m_debugMode;
	private boolean		m_singleStep;
	private boolean		m_hudVisible;
	private double		m_hudTranslucency;
	private boolean		m_hudDebugInfo;
	private boolean		m_retryEnabled;
	private int			m_retryAttempts;
	private boolean		m_stopIfFailure;
	private boolean		m_rebootBeforeEachPass;
	private String		m_skin;

	// Tags to reach the entries in the Xml tree
	private	Xml			m_opbmXml;
	private	Xml			m_settingsXml;
	private Xml			m_benchmarksXml;
	private	Xml			m_debuggerXml;
	private	Xml			m_singleStepXml;
	private	Xml			m_hudXml;
	private	Xml			m_hudVisibleXml;
	private	Xml			m_hudTranslucencyXml;
	private	Xml			m_hudDebugInfoXml;
	private	Xml			m_retryXml;
	private	Xml			m_retryEnabledXml;
	private	Xml			m_retryAttemptsXml;
	private	Xml			m_stopIfFailureXml;
	private	Xml			m_rebootBeforeEachPassXml;
	private	Xml			m_skinXml;
}
