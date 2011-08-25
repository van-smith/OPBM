/*
 * OPBM - Office Productivity Benchmark
 *
 * This class handles all static and non-static utility functions used by
 * Opbm.  It uses a largely static model and can be used by other applications
 * as well.
 *
 * Last Updated:  Aug 01, 2011
 *
 * by Rick C. Hodgin
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

import opbm.dialogs.OpbmFileFilter;
import opbm.graphics.AlphaImage;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import opbm.Opbm;

/**
 * Utility class handling static utility functions.
 *
 * @author Rick C. Hodgin
 */
public class Utils
{
	/**
	 * Ensures the specified color code is in the format necessary for
	 * processing within the <code>Color.decode()</code> method.
	 *
	 * @param candidate Color text as "ff0000" for red
	 * @return Converted to "0xff0000"
	 */
	public static String verifyColorFormat(String candidate)
	{
		try {
			// See if it's already good
			Color c = Color.decode(candidate);
			return(candidate);

		} catch (NumberFormatException ex) {
			try {
				// See if it needs the "0x" prefix
				Color c = Color.decode("0x" + candidate);
				return("0x" + candidate);

			} catch (NumberFormatException ex2) {
				// It's no good, it will pass through and fail at caller
				return(candidate);
			}
		}
	}

	public static String encodeColorFormat(int	red,
										   int	grn,
										   int	blu)
	{
		return("0x" + Utils.padLeft(Integer.toHexString(red), 2, "0") +
					  Utils.padLeft(Integer.toHexString(grn), 2, "0") +
					  Utils.padLeft(Integer.toHexString(blu), 2, "0"));
	}

	/** Uses the <code>String.format()</code> function to put thousand separators
	 * around the specified integer.
	 *
	 * @param number integer to insert commas
	 * @return converted string with commas in thousands, millions, etc. position
	 */
	public static String putCommas(int number) {
		return(String.format("%, d", number));
	}

	/**
	 * Interprets "yes" "true" and "1" as "yes", and everything else as "no"
	 *
	 * @param input a test string
	 * @param defaultIfEmpty if string is empty, what default value should be used?
	 * @return "yes" or "no"
	 */
	public static String interpretBooleanAsYesNo(String		input,
												 boolean	defaultIfEmpty)
	{
		if (input.isEmpty())
			return(defaultIfEmpty ? "yes" : "no");

		if (input.toLowerCase().contains("yes") || input.toLowerCase().contains("true") ||  input.contains("1"))
			return("yes");

		return("no");
	}

	public static boolean launchWebBrowser(String url)
	{
		try {
			//attempt to use Desktop library from JDK 1.6+
			Class<?> d = Class.forName("java.awt.Desktop");
			d.getDeclaredMethod("browse", new Class[] {java.net.URI.class}).invoke( d.getDeclaredMethod("getDesktop").invoke(null), new Object[] {java.net.URI.create(url)});

			//above code mimicks: java.awt.Desktop.getDesktop().browse()
		} catch (Exception ignore) {
			String osName = System.getProperty("os.name");
			try {
				if (osName.startsWith("Mac OS")) {
					Class.forName("com.apple.eio.FileManager").getDeclaredMethod( "openURL", new Class[] {String.class}).invoke(null, new Object[] {url});

				} else if (osName.startsWith("Windows")) {
					Runtime.getRuntime().exec( "rundll32 url.dll,FileProtocolHandler " + url);

				} else {
					String browser = null;
					for (String b : browsers)
					{
						if (browser == null && Runtime.getRuntime().exec(new String[] {"which", b}).getInputStream().read() != -1) {
							Runtime.getRuntime().exec(new String[] {browser = b, url});
						}
					}

					if (browser == null) {
						throw new Exception(Arrays.toString(browsers));
					}
				}

			} catch (Exception e) {
				return(false);
			}
		}
		return(true);
	}

	/**
	 * Converts some "text like this" to "Text Like This"
	 *
	 * @param candidate input string
	 * @return converted to proper string
	 */
	public static String toProper(String candidate)
	{
		int i;
		String s, c;
		boolean doUpperCase = true;

		s = "";
		for (i = 0; i < candidate.length(); i++)
		{
			// Grab the character
			c = candidate.substring(i, i+1);

			// Store it
			if (doUpperCase) {
				s += c.toUpperCase();
			} else {
				s += c.toLowerCase();
			}

			// See if the next one should be upper-case or not
			if (c.equals(" ")) {
				doUpperCase = true;
			} else {
				doUpperCase = false;
			}
		}
		return(s);
	}

	public static boolean areAnyNull(Object	o1,
									 Object	o2)
	{
		if (o1 == null || o2 == null)
			return(true);
		else
			return(false);
	}

	public static boolean areAnyNull(Object	o1,
									 Object	o2,
									 Object	o3)
	{
		if (o1 == null || o2 == null || o3 == null)
			return(true);
		else
			return(false);
	}

	public static boolean areAnyNull(Object	o1,
									 Object	o2,
									 Object	o3,
									 Object	o4)
	{
		if (o1 == null || o2 == null || o3 == null || o4 == null)
			return(true);
		else
			return(false);
	}

	/**
	 * Search the candidate for {:fieldName, Prompt:} and extract the field
	 * names and fieldPrompts for any found.
	 * @param fieldNames 1:1 ratio with fieldPrompts, holds the field name for the input
	 * @param fieldPrompts 1:1 ratio with fieldNames, holds the prompt found (if any)
	 * or a generic "Specify fieldName" prompt
	 * @param candidate string to search for {: :} formatted entries
	 */
	public static void extractCustomInputFields(List<String>		fieldNames,
										   List<String>		fieldPrompts,
										   String			candidate)
	{
		int lnFirst, lnSecond, lnComma;
		String value, customInput, prompt, part1, part2;

		// Remove all () parenthesis portions
		value = candidate;
		while (value.contains("{:") || value.contains(":}"))
		{
			lnFirst		= value.indexOf("{:");
			lnSecond	= value.indexOf(":}");

			if (lnFirst == -1 || lnSecond == -1 || lnSecond < lnFirst)
				break;	// We're done, missing entry or invalid syntax

			customInput = value.substring(lnFirst + 2, lnSecond);
			lnComma	= customInput.indexOf(",");
			if (lnComma != -1)
			{
				// They specified a custom prompt
				part1	= customInput.substring(0, lnComma);
				prompt	= customInput.substring(lnComma + 1, customInput.length()).trim();
				customInput	= part1;

				if (!prompt.endsWith(":") && !prompt.endsWith("?"))
					prompt += ":";

			} else {
				// Just add a generic "Specify field name" prompt
				prompt = "Specify " + Utils.toProper(customInput) + ":";

			}

			fieldNames.add(customInput);
			fieldPrompts.add(prompt);
			value = value.substring(0, lnFirst) + value.substring(lnSecond + 2, value.length());
		}
	}

	/**
	 * Returns time in the format Tue Aug 16 16:39:51 CDT 2011 1313530812950
	 *                            Day Mmm DD HH MM SS TZ- YYYY Millisecond--
	 *                            000000000011111111112222222222333333333344
	 *                            012345678901234567890123456789012345678901
	 * Code to access components:

		String timestamp = Utils.getTimestamp();
		String dow, month, day, time, tz, year, ms;

 		dow		= timestamp.substring(0,3);
 		month	= timestamp.substring(4,7);
 		day		= timestamp.substring(8,10);
 		time	= timestamp.substring(11,19);
 		tz		= timestamp.substring(20,23);
 		year	= timestamp.substring(24,28);
 		ms		= timestamp.substring(29);

		System.out.println("  dow = \"" + dow	+ "\"");
		System.out.println("month = \"" + month	+ "\"");
		System.out.println("  day = \"" + day	+ "\"");
		System.out.println(" time = \"" + time	+ "\"");
		System.out.println("   tz = \"" + tz	+ "\"");
		System.out.println(" year = \"" + year	+ "\"");
		System.out.println("   ms = \"" + ms	+ "\"");
		System.out.println(timestamp);

	 * @return time
	 */
	public static String getTimestamp()
	{
		Calendar cal = Calendar.getInstance();
		return(cal.getTime().toString() + " " + Long.toString(cal.getTimeInMillis()));
	}

	public static String convertMillisecondDifferenceToHHMMSS(String	timestampBegan,
															  String	timestampEnded)
	{
		String msBegan, msEnded;
		long began, ended;

		msBegan = timestampBegan.substring(29);
		msEnded = timestampEnded.substring(29);

		began	= Long.valueOf(msBegan);
		ended	= Long.valueOf(msEnded);

		return(convertMillisecondDifferenceToHHMMSS(began, ended));
	}

	/**
	 * Converts the two milliseconds to HHMMSS as the time between
	 */
	public static String convertMillisecondDifferenceToHHMMSS(long		began,
															  long		ended)
	{
		long diff;

		diff = (ended - began) / 1000;
		return(convertMillisecondsToHHMMSS(diff));
	}

	/**
	 * Converts the specified milliseconds into HHMMSS
	 */
	public static String convertMillisecondsToHHMMSS(long diff)
	{
		String hhmmssf, hh, mm, ss;
		double hours, minutes, seconds, fraction;
		NumberFormat nfi1 = NumberFormat.getIntegerInstance();
		NumberFormat nfi2 = NumberFormat.getNumberInstance();

		hours		= (double)diff / (60.0f * 60.0f * 1000.0f);
		minutes		= (double)diff % (60.0f * 60.0f * 1000.0f);
		seconds		= minutes % (60.0f * 1000.0f);
		fraction	= seconds % 1.0f;
		minutes		-= seconds;
		seconds		-= fraction;

		// We want a format like "00:00:00.0"
		nfi1.setMaximumIntegerDigits(2);
		nfi1.setMinimumIntegerDigits(2);
		nfi2.setMaximumIntegerDigits(1);
		nfi2.setMinimumIntegerDigits(1);
		nfi2.setMinimumFractionDigits(1);
		nfi2.setMaximumFractionDigits(1);

		// Create the format
		hhmmssf = nfi1.format(hours) + ":" + nfi1.format(minutes) + ":" + nfi1.format(seconds) + "." + nfi2.format(fraction).substring(2);

		// Return the value
		return(hhmmssf);
	}

	/**
	 * Computes the milliseconds between two timestamps
	 * @param began start time
	 * @param ended end time
	 * @return milliseconds between
	 */
	public static long millisecondsBetweenTimestamps(String		timestampBegan,
													 String		timestampEnded)
	{
		String msBegan, msEnded;
		long bms, ems;

		msBegan = timestampBegan.substring(29);
		msEnded = timestampEnded.substring(29);

		bms		= Long.valueOf(msBegan);
		ems		= Long.valueOf(msEnded);

		return(ems - bms);
	}

	public static Xml processExecutableLine(String	tag,
											String	source,
											String	attributeName,
											String	attributeContent)
	{
		Xml xml = processExecutableLine(tag, source);
		xml.appendAttribute(new Xml(attributeName, attributeContent));
		return(xml);
	}

	public static Xml processExecutableLine(String	tag,
											String	source,
											String	childName1,
											String	childContent1,
											String	childName2,
											String	childContent2)
	{
		Xml xml = processExecutableLine(tag, source);
		xml.appendChild(new Xml(childName1, childContent1));
		xml.appendChild(new Xml(childName2, childContent2));
		return(xml);
	}

	public static Xml processExecutableLine(String	tag,
											String	source)
	{
		Xml xmlDate, xmlMillisecond, xmlLine;
		String date, millisecond, line;

		//////////
		// Rigid format of parsed line is this:
		//
		// 000000000011111111112222222222333333333344444+++
		// 012345678901234567890123456789012345678901234
		// Fri Jun 10 12:06:22 CDT 2011 1307725582761: Test stderr
		// ---------------------------- -------------  -----------
		//           date                millisecond      line
		//////////

		// Extract out the portions: date, millisecond, line
		date			= source.substring(0, 28).trim();
		millisecond		= source.substring(29, 42).trim();

		// Some entries do not have any appending content, just the timestamp
		if (source.length() >= 44)
			line		= source.substring(44).trim();
		else
			line		= "";

		// Create the attributes
		xmlDate			= new Xml("date", date);
		xmlMillisecond	= new Xml("millisecond", millisecond);

		// Create the entire tag, and append the attributes
		xmlLine			= new Xml(tag, line);
		xmlLine.appendAttribute(xmlDate);
		xmlLine.appendAttribute(xmlMillisecond);
		return(xmlLine);
	}

	public static String forceExtension(String	pathName,
										String	newExtension)
	{
		int i;

		// Iterate backwards through the pathName to find the period in the ".ext" extension
		for (i = pathName.length() - 1; i >= 0; i--)
		{
			if (pathName.substring(i, i + 1).equals("."))
				return(pathName.substring(0, i) + newExtension);	// We found the extension
		}
		// No extension, just append it
		return(pathName + newExtension);
	}

	public static String getExtension(String pathName)
	{
		int i;

		// Iterate backwards through the pathName to find the period in the ".ext" extension
		for (i = pathName.length() - 1; i >= 0; i--)
		{
			if (pathName.substring(i, i + 1).equals("."))
				return(pathName.substring(i));	// We found the extension
		}
		// No extension was found, return an empty string
		return("");
	}

	/**
	 * Called to verify if the directory exists, and if not, then to create it.
	 * @param pathAndFileName file containing path to verify
	 * @return indicates whether or not it exists
	 */
	public static boolean verifyDirectoryExists(String pathAndFileName)
	{
		File d = new File(new File(pathAndFileName).getParent());

		if (!d.exists())
			return(d.mkdirs());
		else
			return(true);
	}

	/**
	 * Verifies the path ends in a backslash (c:\some\dir becomes c:\some\dir\)
	 * @param path input path to test
	 * @return path always ending in backslash
	 */
	public static String verifyPathEndsInBackslash(String path)
	{
		String backslashPath;
		File d = new File(new File(path).getParent());

		backslashPath = d.getAbsolutePath();
		if (backslashPath.endsWith("\\"))
		{	// We're good

		} else {
			// Needs a little help
			backslashPath += "\\";
		}
		return(backslashPath);
	}

	/**
	 * Makes sure there are no instances where a c:\some\dir\ was joined with
	 * \bin\file.exe to create c:\some\dir\\bin\file.exe, and thereby causing
	 * it to fail.
	 * @param pathName input pathname to test
	 * @return all instances of \\ are replaced with \
	 *
	 * Note:  For paths prefixed with \\whatever\, do not use this function
	 *        unless the leading part is not included!
	 */
	public static String verifyNoDoubleBackslashesInPathName(String pathName)
	{
		// Replace all instances of c:\some\path\\ with c:\some\path\
		return(pathName.replace("\\\\", "\\"));
	}

	public static String rightJustify(String	source,
									  int		length,
									  String	padChar)
	{
		int i;
		String newSource;

		if (source.length() >= length)
			return(source);

		newSource = "";
		for (i = 0; i < length - source.length(); i++)
			newSource += padChar;
		return(newSource + source);
	}

	public static String justFileName(String pathName)
	{
		int i;

		// Iterate backwards through the pathName to find the last \ character
		for (i = pathName.length() - 1; i >= 0; i--)
		{
			if (pathName.substring(i, i + 1).equals("\\"))
				return(pathName.substring(i));
		}
		// Not found, return whole thing
		return(pathName);
	}

	public static boolean appendTerminatedLinesToFile(String		fileName,
													  List<String>	items)
	{
		int i;

		try {
			FileWriter fstream = new FileWriter(fileName, true);
			BufferedWriter out = new BufferedWriter(fstream);

			// Write each item in turn
			for (i = 0; i < items.size(); i++)
				out.write(items.get(i) + "\n");

			out.close();
			return(true);

		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
		return(false);
	}

	public static boolean writeTerminatedLinesToFile(String			fileName,
													 List<String>	items)
	{
		int i;

		try {
			File f;
			FileOutputStream fo;

			f  = new File(fileName);
			fo = new FileOutputStream(f);
			DataOutputStream dos = new DataOutputStream(fo);

			// Write each item in turn
			for (i = 0; i < items.size(); i++)
				dos.writeBytes(items.get(i) + "\n");

			dos.close();
			return(true);

		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
		return(false);
	}

	public static void readTerminatedLinesFromFile(String			fileName,
												   List<String>		args)
	{
		File f;
		FileInputStream fi;
		String line;

		try {
			f  = new File(fileName);
			fi = new FileInputStream(f);
			BufferedReader d = new BufferedReader(new InputStreamReader(fi));

			// Read each line in turn
			line = d.readLine();
			while (line != null && !line.isEmpty())
			{
				args.add(line);
				line = d.readLine();
			}
			d.close();

		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
	}

	public static boolean appendStringToFile(String		fileName,
											 String		data)
	{
		int i;

		try {
			File f;
			FileOutputStream fo;

			f  = new File(fileName);
			fo = new FileOutputStream(f, true);
			DataOutputStream dos = new DataOutputStream(fo);

			// Write each item in turn
			dos.writeBytes(data);

			dos.close();
			return(true);

		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
		return(false);
	}

	public static String extractOnlyNumbers(String source)
	{
		int i;
		String numbers = "";

		// Continue concatenating until we stop with the characters
		for (i = 0; i < source.length() && source.charAt(i) >= '0' && source.charAt(i) <= '9'; i++)
			numbers += source.charAt(i);

		return(numbers);
	}

	/**
	 * Scans the entire string, and extracts out only numbers, periods, commas,
	 * and plus or minus signs
	 */
	public static String extractOnlyNumbersWithCommasPeriodsAndSignsWholeString(String source)
	{
		int i;
		char ch;
		String numbers = "";

		// Continue concatenating until we stop with the characters
		for (i = 0; i < source.length(); i++)
		{
			ch =  source.charAt(i);
			if ((ch >= '0' && ch <= '9') || ch == '.' || ch == ',' || ch == '-' || ch == '+')
				numbers += source.charAt(i);
		}
		return(numbers);
	}

	/**
	 * Returns the singular or plural string based on the test value
	 * @param testValue integer that's either 1 or something else
	 * @param singular what to return if 1
	 * @param plural what to return if something other than 1
	 * @return singular or plural string
	 */
	public static String singularOrPlural(int		testValue,
										  String	singular,
										  String	plural)
	{
		if (testValue == 1)
			return(singular);
		return(plural);
	}

	public static int truncateHeight(int	maxHeight,
									 int	top,
									 int	size)
	{
		if (maxHeight - top >= size)
			return(size);
		else
			return(maxHeight - top);
	}

	public static int truncateHeightDown(int	minHeight,
										 int	top,
										 int	size)
	{
		if (top >= minHeight)
			return(size);
		else
			return(size - (minHeight - top));
	}

	public static int extractCommaItems(List<String>	list,
										String			tags)
	{
		int count, offset;
		String tag;

		count = 0;
		while (!tags.isEmpty())
		{
			offset = tags.indexOf(",");
			if (offset >= 0)
			{
				tag = tags.substring(0, offset).trim();
				tags = tags.substring(tag.length() + 1).trim();

			} else {
				tag = tags.trim();
				tags = "";

			}
			++count;
			list.add(tag);
		}
		return(count);
	}

	public static double roundAwayFromZero(double num)
	{
		if (num >= 0.0)
			return(Math.round(num + 0.49999999999999));		// Normal positive rounding
		else
			return(Math.round(num - 0.49999999999999));		// Negative rounding
	}

	public static int getNextUniqueCounter()
	{
		return(m_uniqueCounter++);
	}

	public static int getNextIterativeColor()
	{
		int argb;

		switch (m_iterativeColorIndex)
		{
			case 0:
			default:
				argb = AlphaImage.makeARGB(255, 192, 192, 51);
				break;
			case 1:
				argb = AlphaImage.makeARGB(255, 153, 102, 0);
				break;
			case 2:
				argb = AlphaImage.makeARGB(255, 255, 0, 51);
				break;
			case 3:
				argb = AlphaImage.makeARGB(255, 0, 153, 0);
				break;
			case 4:
				argb = AlphaImage.makeARGB(255, 102, 153, 0);
				break;
			case 5:
				argb = AlphaImage.makeARGB(255, 128, 51, 128);
				break;
			case 6:
				argb = AlphaImage.makeARGB(255, 255, 51, 0);
				break;
			case 7:
				argb = AlphaImage.makeARGB(255, 153, 51, 0);
				break;
			case 8:
				argb = AlphaImage.makeARGB(255, 0, 153, 153);
				break;
			case 9:
				argb = AlphaImage.makeARGB(255, 153, 0, 51);
				break;
		}

		++m_iterativeColorIndex;
		if (m_iterativeColorIndex >= 10)
			m_iterativeColorIndex = 0;

		return(argb);
	}

	public static String convertFilenameToLettersAndNumbersOnly(String fileName)
	{
		int i;
		char thisChar;
		String ch, pathChars, wildcards, outFileName;
		boolean colonFound, spaceFound;

		colonFound	= false;
		spaceFound	= false;
		pathChars	= "\\/";
		wildcards	= "*?";
		outFileName	= "";
		for (i = 0; i < fileName.length(); i++)
		{
			ch = fileName.substring(i, i+1);
			if (pathChars.indexOf(ch) < 0 && wildcards.indexOf(ch) < 0)
			{	// It's not a path character or a wildcard
				if (ch.equals(":"))
				{	// It's a colon
					// We allow the first colon, but none after that
					if (colonFound || spaceFound)
					{	// Replace it
						ch = "_";
					}
					colonFound = true;

				} else {
					// If it's already a letter or number, we leave it, otherwise we replace it
					if (ch.equals(" "))
					{	// We found a space, note it
						spaceFound = true;
					}

					thisChar = ch.charAt(0);
					if ((thisChar >= 'A' && thisChar <= 'Z') || (thisChar >= 'a' && thisChar <= 'z') || (thisChar >= '0' && thisChar <= '9'))
					{	// It's a letter or number
						// Leave it as it is

					} else {
						// Replace it with an underscore
						ch = "_";
					}
				}
			}
			outFileName += ch;
		}
		return(outFileName);
	}

	/**
	 * Converts the text to letters and numbers only
	 * @param text input text
	 * @return converted text
	 */
	public static String convertToLettersAndNumbersOnly(String text)
	{
		return(convertToLettersAndNumbersOnly(text, ""));
	}

	/**
	 * Converts the input string to letters and numbers only, allowing characters
	 * in the optionalCharactersToLeave string to remain as well
	 *
	 * @param text the input text to parse/convert
	 * @param optionalCharactersToLeave a string of characters to leave in the string
	 * @return the parsed/converted string
	 */
	public static String convertToLettersAndNumbersOnly(String	text,
														String	optionalCharactersToLeave)
	{
		int i;
		char thisChar;
		String ch, outText;

		outText = "";
		for (i = 0; i < text.length(); i++)
		{
			ch = text.substring(i, i+1);
			thisChar = ch.charAt(0);
			if ((thisChar >= 'A' && thisChar <= 'Z') || (thisChar >= 'a' && thisChar <= 'z') || (thisChar >= '0' && thisChar <= '9'))
			{	// It's a letter or number
				// Leave it as it is
				// ch = ch;

			} else {
				if (!optionalCharactersToLeave.contains(ch))
				{	// Replace it with an underscore
					ch = "_";
				}
			}
			outText += ch;
		}
		return(outText);
	}

	/**
	 * Jul 04, 2011 at 11:56pm
	 */
	public static String getDateTimeAs_Mmm_DD__YYYY_at_HH_MMampm()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mmaa");
		return(sdf.format(new Date()));
	}

	/**
	 * Military time Jul 04, 2011 at 23:56
	 * @return
	 */
	public static String getDateTimeAs_Mmm_DD_YYYY_at_HH_MM_SS()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy 'at' kk:mm:ss");
		return(sdf.format(new Date()));
	}

	/**
	 * Extract all of the capital letters and first characters after spaces
	 * @param longName source name, as in "This is an Example"
	 * @param maxLength maximum length to construct for the short name
	 * @return "TiaE" for the "This is an Example" entry
	 */
	public static String getShortName(String	longName,
									  int		maxLength)
	{
		int i;
		boolean lastWasSpace, isCapitalLetter, isNumber;
		char character;
		String shortName;

		shortName		= "";
		lastWasSpace	= true;		// Set to true so the first character is always included
		for (i = 0; i < longName.length(); i++)
		{
			character		= longName.charAt(i);

			isCapitalLetter	= (character >= "A".charAt(0) && character <= "Z".charAt(0));
			isNumber		= (character >= "0".charAt(0) && character <= "9".charAt(0));

			if (lastWasSpace || isCapitalLetter || isNumber)
			{	// It's a capital letter, use it
				shortName += character;
				lastWasSpace = false;

			} else if (character == " ".charAt(0)) {
				lastWasSpace = true;

			} else {
				// Ignore it
			}
		}

		// Make sure we have a name
		if (shortName.isEmpty())
		{
			if (longName.length() > maxLength)
				shortName = longName.substring(0, maxLength).toLowerCase();
			else
				shortName = longName.toLowerCase();
		}

		// All done
		return(shortName);
	}

	/**
	 * Set the directory to that of the executable, such that "c:\some\dir\file.exe"
	 * will set to "c:\some\dir\"
	 * @param executable fullpath executable, as in "c:\some\dir\file.exe" and
	 * not just "file.exe"
	 * @return previous directory
	 */
	public static String makeTheCurrentDirectoryThatOfThisExecutable(String executable)
	{
		String curDir, newDir;

		curDir	= getCurrentDirectory();
		newDir	= new File(executable).getParent();
		setCurrentDirectory(newDir);

		return(curDir);
	}

	public static void setCurrentDirectory(String dir)
	{
		System.setProperty("user.dir", dir);
	}

	public static String getCurrentDirectory()
	{
		return(System.getProperty("user.dir"));
	}

	public static String padLeft(String		text,
								 int		length)
	{
		return(padLeft(text, length, " "));
	}

	public static String padLeft(String		text,
								 int		length,
								 String		fillChar)
	{
		int i;

		if (text.length() >= length)
		{	// Truncate the string to the specified length
			return(text.substring(0, length));

		} else {
			// String is not long enough, pad with spaces, and return
			for (i = text.length(); i < length; i++)
				text += fillChar;

			return(text);
		}
	}

	/**
	 * Returns the millisecond as of the call
	 * @return
	 */
	public static long getMillisecondTimer()
	{
		Calendar cal = Calendar.getInstance();
		return(cal.getTimeInMillis());
	}

	/**
	 * Ask the user for a filename
	 * @param extension filename extension, such as "xml" for name.xml
	 * @param title title to display in the window
	 */
	public static String promptForFilename(String	extension,
										   String	description,
										   String	title,
										   Opbm		opbm)
	{
		int returnVal;
		File f;

		JFileChooser fc = new JFileChooser(getCurrentDirectory());
		fc.setApproveButtonText("Select");
		fc.addChoosableFileFilter(new OpbmFileFilter(extension, description));
		fc.setAcceptAllFileFilterUsed(false);
		returnVal = fc.showOpenDialog(opbm.getGUIFrame());
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return("");
		}
		f = fc.getSelectedFile();		// File they chose
		return(f.getAbsolutePath());
	}

	public static void removeAllKeyListeners(JComponent c)
	{
		int i;
		KeyListener[] kl = c.getKeyListeners();

		for (i = 0; i < kl.length; i++)
		{	// Remove this vermin!
			c.removeKeyListener(kl[i]);
		}
	}

	/**
	 * The JDK and JRE return different "home" locations.  The JDK nicely points
	 * to its full path, such as c:\program files\java\jdk1.7.0\ whereas the JRE
	 * points only to "c:\program files\java\" even though it lives in
	 * "c:\program files\java\jre7\" or "c:\program files\java\jre6\", etc.
	 * @return a valid directory relative to the reported java.home location
	 * where java.exe exists
	 */
	public static String getPathToJavaDotExe()
	{
		int i;
		String pathName;
		File f1, f2, f3, f4, f5;

		pathName	= Utils.verifyNoDoubleBackslashesInPathName(Utils.verifyPathEndsInBackslash(System.getProperty("java.home") + "\\"));
		f1			= new File(pathName + "bin\\java.exe");
		f2			= new File(pathName + "jre7\\bin\\java.exe");
		f3			= new File(pathName + "jre6\\bin\\java.exe");
		f4			= new File(pathName + "jre8\\bin\\java.exe");
		f5			= new File(pathName + "jre9\\bin\\java.exe");
		if (f1.exists())
		{	// java.home is reporting correctly!
			return(f1.getAbsolutePath());
		} else if (f2.exists()) {
			return(f2.getAbsolutePath());
		} else if (f3.exists()) {
			return(f3.getAbsolutePath());
		} else if (f4.exists()) {
			return(f4.getAbsolutePath());
		} else if (f5.exists()) {
			return(f5.getAbsolutePath());
		}
		return(f1.getAbsolutePath());
	}

	public static int getValueOf(String number,
								 int	defaultValue,
								 int	minValue,
								 int	maxValue)
	{
		int value = getValueOf(number, defaultValue);
		if (minValue > maxValue)
		{	// Swap them
			int t = maxValue;
			maxValue = minValue;
			minValue = t;
		}

		if (value < minValue)
			value = minValue;

		if (value > maxValue)
			value = maxValue;

		return(value);
	}

	public static int getValueOf(String number,
								 int	defaultValue)
	{
		int value;

		try
		{	// Convert what they have as a number into either its value
			value = Integer.valueOf(number);

		} catch (NumberFormatException ex) {
			value = defaultValue;
		} catch (NullPointerException ex) {
			value = defaultValue;
		} catch (Throwable t) {
			value = defaultValue;
		}

		return(value);
	}

	/**
	 * Returns "Yes" or "No" in first-char-upper-case form, so a toLowerCase()
	 * or ToUpperCase() can address specific other needs
	 * @param test input test condition
	 * @return "Yes" or "No"
	 */
	public static String evaluateLogicalToYesOrNo(boolean test)
	{
		if (test)
			return("Yes");
		else
			return("No");
	}

	/**
	 * The UUID string representation is as described by this BNF:
	 *
	 *	 UUID                   = time_low-time_mid-time_high_and_version-variant_and_sequence-node
	 *	 time_low               = 4*<hexOctet>				// 8 bytes
	 *	 time_mid               = 2*<hexOctet>				// 4 bytes
	 *	 time_high_and_version  = 2*<hexOctet>				// 4 bytes
	 *	 variant_and_sequence   = 2*<hexOctet>				// 4 bytes
	 *	 node                   = 6*<hexOctet>				// 12 bytes
	 *	 hexOctet               = <hexDigit><hexDigit>		// 2 bytes
	 *	 hexDigit               = "0" through "f" or "F"	// 1 byte
	 * @return the aforementioned format
	 */
	public static String getUUID()
	{
		return(UUID.randomUUID().toString());
	}

	private static final String		errMsg = "Error attempting to launch web browser";

	private static final String[]	browsers = { "google-chrome",
												 "firefox",
												 "opera",
												 "epiphany",
												 "konqueror",
												 "conkeror",
												 "midori",
												 "kazehakase",
												 "mozilla" };

	private static int				m_uniqueCounter = 0;
	private static int				m_iterativeColorIndex = 0;
}
