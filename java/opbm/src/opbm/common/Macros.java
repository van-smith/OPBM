/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for all Macros processing.  It contains
 * static and non-static functions, depending upon what needs to be done.
 * The standard XML-based macros are substituted, such as (equals) for the
 * equal sign =.
 *
 * Last Updated:  Aug 01, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.0.2
 *
 */

package opbm.common;

import opbm.Opbm;
import opbm.benchmarks.BenchmarkParams;

public class Macros
{
	/** Constructor
	 *
	 * @param opbm Parent object referenced for global method calls
	 */
	public Macros(Opbm opbm)
	{
		m_opbm					= opbm;
		m_scriptingEngineSort	= 0;
	}

    /**
	 * Parse the candidate string for known $macros$ and expansion characters,
	 * and replace that portion with their current/real values.
	 *
	 * Each macro is surrounded by dollar signs.  The macro strings are searched
	 * internally with all matches being expanded.
	 *
	 * Note:  Nested macro expansions are possible with this engine.
     *
     * @param candidate a candidate string searched for macros, expanded if found.
	 * @return Candidate string with all macros replaced / expanded.  Any unrecognized macros will be removed.
     */
	public String parseMacros(String candidate)
	{
		int lnFirst, lnSecond;
		String value, macro, macroSubstitution;

		if (candidate != null)
		{
			// Remove common macros
			candidate = Macros.decodeCommonMacrosNoDollarSign(candidate);

			// Remove the macros one by one
			value = candidate;
			while (value.contains("$"))
			{
				lnFirst		= value.indexOf("$");
				lnSecond	= value.indexOf("$", lnFirst + 1);
				if (lnFirst == -1 || lnSecond == -1)
					break;	// We're done
				macro		= value.substring(lnFirst + 1, lnSecond);


	//////////
	// Common HTML tags:
	// br, b, /b, i, /i, u, /u, table, tr, td
				if (macro.equalsIgnoreCase("br"))
					macroSubstitution = "<br>";
				else if (macro.equalsIgnoreCase("b"))
					macroSubstitution = "<b>";
				else if (macro.equalsIgnoreCase("/b"))
					macroSubstitution = "</b>";
				else if (macro.equalsIgnoreCase("u"))
					macroSubstitution = "<u>";
				else if (macro.equalsIgnoreCase("/u"))
					macroSubstitution = "</u>";
				else if (macro.equalsIgnoreCase("i"))
					macroSubstitution = "<i>";
				else if (macro.equalsIgnoreCase("/i"))
					macroSubstitution = "</i>";
				else if (macro.equalsIgnoreCase("table"))
					macroSubstitution = "<table>";
				else if (macro.equalsIgnoreCase("/table"))
					macroSubstitution = "</table>";
				else if (macro.equalsIgnoreCase("tr"))
					macroSubstitution = "<tr>";
				else if (macro.equalsIgnoreCase("trheight100"))
					macroSubstitution = "<tr height='100'>";
				else if (macro.equalsIgnoreCase("/tr"))
					macroSubstitution = "</tr>";
				else if (macro.equalsIgnoreCase("td"))
					macroSubstitution = "<td>";
				else if (macro.equalsIgnoreCase("tdwidth10"))
					macroSubstitution = "<td width='10'>";
				else if (macro.equalsIgnoreCase("tdwidth20"))
					macroSubstitution = "<td width='20'>";
				else if (macro.equalsIgnoreCase("tdwidth45"))
					macroSubstitution = "<td width='45'>";
				else if (macro.equalsIgnoreCase("tdwidth180"))
					macroSubstitution = "<td width='180'>";
				else if (macro.equalsIgnoreCase("/td"))
					macroSubstitution = "</td>";

	//////////
	// HUD DEBUG INFO
	// HUD DEBUG INFO COLOR
				else if (macro.equalsIgnoreCase("debug_info")) {
					m_settings	= m_opbm.getSettingsMaster();
					macroSubstitution = logicalEvaluation(m_settings.getHUDVisible() && m_settings.getHUDDebugInfo()).toUpperCase();
				} else if (macro.equalsIgnoreCase("debug_info_color")) {
					m_settings	= m_opbm.getSettingsMaster();
					macroSubstitution = greenRedEvaluation(m_settings.getHUDVisible() && m_settings.getHUDDebugInfo()).toUpperCase();

	//////////
	// HUD
	// HUD COLOR
				} else if (macro.equalsIgnoreCase("hud")) {
					m_settings	= m_opbm.getSettingsMaster();
					macroSubstitution = logicalEvaluation(m_settings.getHUDVisible()).toUpperCase();
				} else if (macro.equalsIgnoreCase("hud_color")) {
					m_settings	= m_opbm.getSettingsMaster();
					macroSubstitution = greenRedEvaluation(m_settings.getHUDVisible()).toUpperCase();
				} else if (macro.equalsIgnoreCase("hud_translucency")) {
					m_settings	= m_opbm.getSettingsMaster();
					macroSubstitution = logicalTranslucentEvaluation(m_settings.getHUDVisible(), m_settings.getHUDTranslucency());

	//////////
	// RETRY ATTEMPTS
				} else if (macro.equalsIgnoreCase("retry_attempts")) {
					m_settings	= m_opbm.getSettingsMaster();
					macroSubstitution = Integer.toString(m_settings.getRetryAttempts());
				} else if (macro.equalsIgnoreCase("retry_color")) {
					m_settings	= m_opbm.getSettingsMaster();
					macroSubstitution = greenRedEvaluation(m_settings.getRetryAttempts() != 0).toUpperCase();
				} else if (macro.equalsIgnoreCase("halt_on_error")) {
					m_settings	= m_opbm.getSettingsMaster();
					macroSubstitution = logicalEvaluation(m_settings.benchmarkStopsIfRetriesFail()).toUpperCase();
				} else if (macro.equalsIgnoreCase("halt_on_error_color")) {
					m_settings	= m_opbm.getSettingsMaster();
					macroSubstitution = greenRedEvaluation(m_settings.benchmarkStopsIfRetriesFail()).toUpperCase();


	//////////
	// SCRIPTING ENGINE SORT
				} else if (macro.equalsIgnoreCase("scripting_engine_sort")) {
					macroSubstitution = scriptingEngineSortEvaluation();


	//////////
	// PREVIOUS PANEL
				} else if (macro.equalsIgnoreCase("previous_panel")) {
					macroSubstitution = m_opbm.previousPanel();


	//////////
	// HOME PANEL
				} else if (macro.equalsIgnoreCase("home_panel")) {
					macroSubstitution = m_opbm.homePanel();


	//////////
	// edits.xml
	// panels.xml
	// scripts.xml
				} else if (macro.equalsIgnoreCase("edits.xml")) {
					macroSubstitution = Opbm.locateFile("edits.xml");
				} else if (macro.equalsIgnoreCase("panels.xml")) {
					macroSubstitution = Opbm.locateFile("panels.xml");
				} else if (macro.equalsIgnoreCase("scripts.xml")) {
					macroSubstitution = Opbm.locateFile("scripts.xml");


	//////////
	// Unrecognized, remove its invalid contents
				} else {
					macroSubstitution = "";
				}
				// Update our new value
				value		= value.substring(0, lnFirst) +
							  macroSubstitution +
							  value.substring(lnSecond + 1, value.length());
			}
			value = Macros.decodeCommonMacrosDollarSign(value);

		} else {
			value = "";

		}
		return(value);
	}

	/** Evaluates a boolean and returns yes/no string.
	 *
	 * @param condition Test condition to return yes or no to.
	 * @return Yes or No string (in that case style)
	 */
	public String logicalEvaluation(boolean condition)
	{
		return(condition ? "Yes" : "No");
	}

	/** Evaluates a boolean and returns green or red color for visual cue.
	 *
	 * @param condition Test condition.
	 * @return If condition is true, returns green color, otherwise returns red color.
	 */
	public String greenRedEvaluation(boolean condition) {
		return(condition ? "0x3f7f3f"/* true  = green */ :
						   "0x8f3f3f"/* false = red   */);
	}

	/**
	 * Returns a human-readable string identifying the state of the HUD.
	 * @param hudVisible
	 * @param hudTranslucency
	 * @return
	 */
	public String logicalTranslucentEvaluation(boolean		hudVisible,
											   float		hudTranslucency)
	{
		if (hudVisible)
		{	// Enabled, but at what level?
			if (hudTranslucency <= 0.25f) {
				return("25%");

			} else if (hudTranslucency <= 0.50f) {
				return("50%");

			} else if (hudTranslucency <= 0.75f) {
				return("75%");

			} else {
				return("100%");
			}

		} else {
			// Not enabled
			return("NO");
		}
	}

	/** Sample case statement returning one of multiple possible conditions.
	 *
	 * @return Based on the internal variable's setting, returns text of its condition.
	 * @deprecated Never intended for use as of creation.  Provided only as an example.
	 */
	public String scriptingEngineSortEvaluation() {
		switch (m_scriptingEngineSort) {
			case 0:
				return("Default");
			case 1:
				return("by Name");
			case 2:
				return("by OS");
			default:
				return("");
		}
	}

	/**
	 * Some XML parsers cannot read various character combinations, so this
	 * code is used to decode common macro expansion expressions into their real
	 * values for user editing and output-file processing.
	 *
	 * When written back to the XML files, they are re-encoded with
	 * <code>encodeCommonMacros()</code>.
	 *
	 * @param candidate string to search for possible common macros
	 * @return converted string if any found, original string otherwise
	 */
	public static String decodeCommonMacrosNoDollarSign(String candidate)
	{
		candidate = candidate.replace("(lessthan)",				"<");
		candidate = candidate.replace("(greaterthan)",			">");
		candidate = candidate.replace("(equals)",				"=");
		candidate = candidate.replace("(ampersand)",			"&");
		candidate = candidate.replace("(caret)",				"^");
		candidate = candidate.replace("(percent)",				"%");
		candidate = candidate.replace("(poundsign)",			"#");
		candidate = candidate.replace("(atsign)",				"@");
		candidate = candidate.replace("(exclamationpoint)",		"!");
		candidate = candidate.replace("(tilde)",				"~");
		candidate = candidate.replace("(singlequote)",			"'");
		candidate = candidate.replace("(doublequote)",			"\"");
		candidate = candidate.replace("(hyphen)",				"-");
		candidate = candidate.replace("(plus)",					"+");
		candidate = candidate.replace("(leftbracket)",			"[");
		candidate = candidate.replace("(rightbracket)",			"]");
		candidate = candidate.replace("(leftbrace)",			"{");
		candidate = candidate.replace("(rightbrace)",			"}");
		candidate = candidate.replace("(asterisk)",				"*");
		candidate = candidate.replace("(backslash)",			"\\");
		candidate = candidate.replace("(slash)",				"/");
		candidate = candidate.replace("(colon)",				":");
		candidate = candidate.replace("(semicolon)",			";");
		return(candidate);
	}

	public static String decodeCommonMacrosDollarSign(String candidate)
	{
		candidate = candidate.replace("(dollarsign)", "$");
		return(candidate);
	}

	/**
	 * Encode the human readable form of various text items into a form that can
	 * be stored in XML for some parsers, which cannot recognize all codes.
	 *
	 * When read back in for user input, the XML files should first be decoded
	 * with <code>decodeCommonMacros()</code>.
	 *
	 * @param candidate string to search for possible common macros
	 * @return converted string if any found, original string otherwise
	 */
	public static String encodeCommonMacros(String candidate)
	{
		candidate = candidate.replace("<",	"(lessthan)");
		candidate = candidate.replace(">",	"(greaterthan)");
		candidate = candidate.replace("=",	"(equals)");
		candidate = candidate.replace("&",	"(ampersand)");
		candidate = candidate.replace("^",	"(caret)");
		candidate = candidate.replace("%",	"(percent)");
		candidate = candidate.replace("$",	"(dollarsign)");
		candidate = candidate.replace("#",	"(poundsign)");
		candidate = candidate.replace("@",	"(atsign)");
		candidate = candidate.replace("!",	"(exclamationpoint)");
		candidate = candidate.replace("~",	"(tilde)");
		candidate = candidate.replace("'",	"(singlequote)");
		candidate = candidate.replace("\"",	"(doublequote)");
		candidate = candidate.replace("+",	"(plus)");
		candidate = candidate.replace("-",	"(hyphen)");
		candidate = candidate.replace("[",	"(leftbracket)");
		candidate = candidate.replace("]",	"(rightbracket)");
		candidate = candidate.replace("{",	"(leftbrace)");
		candidate = candidate.replace("}",	"(rightbrace)");
		candidate = candidate.replace("*",	"(asterisk)");
		candidate = candidate.replace("\\",	"(backslash)");
		candidate = candidate.replace("/",	"(slash)");
		candidate = candidate.replace(":",	"(colon)");
		candidate = candidate.replace(";",	"(semicolon)");
		return(candidate);
	}

	/**
	 * Displays text with macro parsing, so "Un(hyphen)install" shows up as
	 * "Un-install", etc.
	 * @param text data to display to System.out
	 */
	public void SystemOutPrintln(String text)
	{
		System.out.println(parseMacros(text));
	}

	// Public macro variables
	private Opbm				m_opbm;
	private BenchmarkParams		m_bp;
	private Settings			m_settings;
	private int					m_scriptingEngineSort;
}
