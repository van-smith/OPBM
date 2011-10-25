/*
 * OPBM - Office Productivity Benchmark
 *
 * This class handles all static and non-static utility functions used by
 * Opbm.  It uses a largely static model and can be used by other applications
 * as well.
 *
 * Last Updated:  Sep 12, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.2.0
 *
 */

package opbm.common;

import opbm.dialogs.OpbmFileFilter;
import opbm.graphics.AlphaImage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
import java.nio.channels.FileChannel;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import opbm.Opbm;

/**
 * Utility class handling static utility functions.
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
		if (input == null || input.isEmpty())
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

	public static String convertMillisecondDifferenceToHHMMSSff(String		timestampBegan,
																String		timestampEnded)
	{
		String msBegan, msEnded;
		long began, ended;

		msBegan = timestampBegan.substring(29);
		msEnded = timestampEnded.substring(29);

		began	= Long.valueOf(msBegan);
		ended	= Long.valueOf(msEnded);

		return(convertMillisecondDifferenceToHHMMSSff(began, ended));
	}

	/**
	 * Converts the two milliseconds to HH:MM:SS.ff as the time between
	 */
	public static String convertMillisecondDifferenceToHHMMSSff(long	began,
																long	ended)
	{
		long diff;

		diff = (ended - began) / 1000;
		return(convertMillisecondsToHHMMSSff(diff));
	}

	/**
	 * Converts the specified milliseconds into HH:MM:SS.ff
	 */
	public static String convertMillisecondsToHHMMSSff(long diff)
	{
		String hhmmssf;
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
	public static long getMillisecondsBetweenTimestamps(String		timestampBegan,
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

	/**
	 * Verifies that the before timestamp is before the after timestamp
	 * @param timestampBefore
	 * @param timestampAfter
	 * @return true if the timestampBefore is less than or equal to timestampAfter
	 */
	public static boolean ensureTimestampsAreInOrder(String		timestampBefore,
													 String		timestampAfter)
	{
		long before, after;
		String msBefore, msAfter;

		msBefore	= timestampBefore.substring(29);
		msAfter		= timestampAfter.substring(29);

		before		= Long.valueOf(msBefore);
		after		= Long.valueOf(msAfter);

		return(before <= after);
	}

	/**
	 * Converts the specified fractional seconds into HH:MM:SS.ff
	 * @param sourceSeconds number of seconds to convert, as in 1.292837
	 */
	public static String convertSecondsToHHMMSSff(double sourceSeconds)
	{
		String hhmmssf;
		double hours, minutes, seconds, fraction;
		NumberFormat nfi1 = NumberFormat.getIntegerInstance();
		NumberFormat nfi2 = NumberFormat.getNumberInstance();

		hours		= (int) (sourceSeconds / 3600.0);
		minutes		= (int)((sourceSeconds - (hours * 3600.0)) / 60.0);
		seconds		= (int) (sourceSeconds - (hours * 3600.0) - (minutes * 60.0));
		fraction	=       (sourceSeconds - (hours * 3600.0) - (minutes * 60.0) - seconds);

		// We want a format like "00:00:00.0"
		nfi1.setMaximumIntegerDigits(2);
		nfi1.setMinimumIntegerDigits(2);
		nfi2.setMaximumIntegerDigits(1);
		nfi2.setMinimumIntegerDigits(1);
		nfi2.setMinimumFractionDigits(2);
		nfi2.setMaximumFractionDigits(2);

		// Create the format
		hhmmssf = nfi1.format(hours) + ":" + nfi1.format(minutes) + ":" + nfi1.format(seconds) + "." + nfi2.format(fraction).substring(2);
		if (hhmmssf.contains(".9"))
			fraction = 0.0;

		// Return the value
		return(hhmmssf);
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
	 * Verifies the path string ends in a backslash (c:\some\dir becomes c:\some\dir\)
	 * @param path input path to test
	 * @return path always ending in backslash
	 */
	public static String verifyStringEndsInBackslash(String path)
	{
		if (path.endsWith("\\"))
		{	// We're good

		} else {
			// Needs a little help
			path += "\\";
		}
		return(path);
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
	 * @param filename fullpath, as in "c:\some\dir\file.exe" and not just "file.exe"
	 * @return previous directory
	 */
	public static String makeTheCurrentDirectoryThatOfThisFilename(String filename)
	{
		String curDir, newDir, newDir2;

		curDir	= getCurrentDirectory();
		newDir	= new File(filename).getParent();
		if (newDir.startsWith(".."))
		{	// It's a relative path
			newDir2 = verifyStringEndsInBackslash(curDir) + newDir;
			setCurrentDirectory(newDir2);

		} else {
			// It's an explicit path
			setCurrentDirectory(newDir);
		}

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
	 * Ask the user for a filename beginning in the current directory
	 * @param extension filename extension, such as "xml" for name.xml
	 * @param description selection description
	 * @param title title to display in the window
	 * @param opbm reference to find the main GUI frame to display relative to
	 * @param directory directory chooser should launch in/from
	 */
	public static String promptForFilename(String	extension,
										   String	description,
										   String	title,
										   Opbm		opbm)
	{
		return(promptForFilename(extension, description, title, opbm, getCurrentDirectory()));
	}

	/**
	 * Ask the user for a filename beginning at the specified directory
	 * @param extension filename extension, such as "xml" for name.xml
	 * @param description selection description
	 * @param title title to display in the window
	 * @param opbm reference to find the main GUI frame to display relative to
	 * @param directory directory chooser should launch in/from
	 */
	public static String promptForFilename(String	extension,
										   String	description,
										   String	title,
										   Opbm		opbm,
										   String	directory)
	{
		int returnVal;
		File f;

		JFileChooser fc = new JFileChooser(directory);
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

	/**
	 * Forces a value to be between the specified min/max range
	 * @param value value to (potentially) adjust
	 * @param min minimum value it should ever be
	 * @param max maximum value it should ever be
	 * @return
	 */
	public static double ensureBetween(double		value,
								 double		min,
								 double		max)
	{
		if (value < min)
			value = min;
		if (value > max)
			value = max;
		return(value);
	}

	/**
	 * Returns the value of the specified string as a double
	 * @param inputValue the string input, as in "100.393"
	 * @param valueIfEmpty a default value to use if the inputValue is empty
	 * @return converted value of inputValue, or valueIfEmpty
	 */
	public static double doubleValueOf(String	inputValue,
									   double	valueIfEmpty)
	{
		double value;

		if (!inputValue.isEmpty())
		{	// There is a value, convert it
			try {
				value	= Double.valueOf(inputValue);
			} catch (Throwable t) {
				value	= valueIfEmpty;
			}
		} else {
			value	= valueIfEmpty;
		}
		return(value);
	}

	/**
	 * Returns the value of the specified string as a float
	 * @param inputValue the string input, as in "100.393"
	 * @param valueIfEmpty a default value to use if the inputValue is empty
	 * @return converted value of inputValue, or valueIfEmpty
	 */
	public static float floatValueOf(String	inputValue,
									 float	valueIfEmpty)
	{
		float value;

		if (!inputValue.isEmpty())
		{	// There is a value, convert it
			try {
				value	= Float.valueOf(inputValue);
			} catch (Throwable t) {
				value	= valueIfEmpty;
			}
		} else {
			value	= valueIfEmpty;
		}
		return(value);
	}

	/**
	 * Returns the value of the specified string as an integer
	 * @param inputValue the string input, as in "103"
	 * @param valueIfEmpty a default value to use if the inputValue is empty
	 * @return converted value of inputValue, or valueIfEmpty
	 */
	public static int integerValueOf(String		inputValue,
									 int		valueIfEmpty)
	{
		int value;

		if (!inputValue.isEmpty())
		{	// There is a value, convert it
			try {
				value	= Integer.valueOf(inputValue);
			} catch (Throwable t) {
				value	= valueIfEmpty;
			}
		} else {
			value	= valueIfEmpty;
		}
		return(value);
	}

	/**
	 * Converts the double value to a string of the specified integer and
	 * decimal precision
	 * @param value the input value
	 * @param integers number of integers, 3 for "100.x"
	 * @param decimals number of decimals, 5 for "x.12345"
	 * @return formated number string in the form "100.12345"
	 */
	public static String doubleToString(double	value,
										int		integers,
										int		decimals)
	{
		String s;
		NumberFormat nf = NumberFormat.getNumberInstance();

		nf.setMaximumIntegerDigits(integers);
		nf.setMinimumIntegerDigits(integers);
		nf.setMaximumFractionDigits(decimals);
		nf.setMinimumFractionDigits(decimals);

		s = nf.format(value);
		return(s.trim());
	}

	/**
	 * Removes the leading zeros from a string like "001" and returns "1"
	 * @param number input, like "001"
	 * @return "1"
	 */
	public static String removeLeadingZeros(String number)
	{
		int i;

		// Skip forward until we find the first non-zero character
		for (i = 0; i < number.length(); i++)
		{
			if (number.charAt(i) != '0' && number.charAt(i) != ' ')
			{	// We've found our last entry
				if (i == 0 && number.charAt(i) == '.')
					return("0" + number);	// For entries that don't have a leading zero of any kind, we add one

				if (number.charAt(i) == '.' && i > 0)
					--i;	// Back off for the leading zero before the decimal point
				return(number.substring(i));
			}
		}
		// If we get here, it's all zeros
		return("0");
	}

	/**
	 * Removes the leading zeros from a string like "00:00:05" and returns "5"
	 * @param number input, like "00:00:05"
	 * @return "5"
	 */
	public static String removeLeadingZeroTimes(String number)
	{
		int i;

		// Skip forward until we find the first non-zero/non-colon character
		for (i = 0; i < number.length(); i++)
		{
			if (number.charAt(i) != '0' && number.charAt(i) != ':' && number.charAt(i) != ' ')
			{	// We've found our last entry
				if (i ==0 && (number.charAt(i) == ':' || number.charAt(i) == '.'))
					return("0" + number);

				if ((number.charAt(i) == ':' || number.charAt(i) == '.') && i > 0)
					--i;	// Back off for the leading zero before the decimal point or colon
				return(number.substring(i));
			}
		}
		// If we get here, it's all zeros
		return("0");
	}

	/**
	 * Repeats the specified text count times
	 * @param count number to replicate, as in 3
	 * @param text text to replicate, as in "foo"
	 * @return as in "foofoofoo"
	 */
	public static String replicate(int		count,
								   String	text)
	{
		int i;
		String s;

		s = "";
		for (i = 0; i < count; i++)
			s += text;
		return(s);
	}

	/**
	 * Called to delete the contents of the specified directory, but
	 * @param path
	 * @param deleteTopLevelToo
	 */
	public static void deleteDirectoryContents(String	path,
											   boolean	deleteTopLevelToo)
	{
		deleteDir(new File(path), 0, deleteTopLevelToo);
	}

	/**
	 * Deletes all files and subdirectories under the directory.
	 */
	private static boolean deleteDir(File		directory,
									 int		level,
									 boolean	deleteTopLevelToo)
	{
		int i;

		if (directory.isDirectory())
		{	// It's a directory, delete all of its sub-directories (if any)
			String[] files = directory.list();
			for (i = 0; i < files.length; i++)
			{
				if (!deleteDir(new File(directory, files[i]), level + 1, deleteTopLevelToo))
					return false;
			}
		}
		// The directory is empty now, so delete it
		// Except, we don't delete the top-level directory unless they specified it
		if (deleteTopLevelToo || level != 0)
			return directory.delete();
		else
			return(true);
	}

	/**
	 * Monitors the specified id for a change from "unanswered" to something
	 * else within the timeout period
	 * @param opbm parent to call back
	 * @param id dialog id
	 * @param timeoutSeconds number of seconds before returning if no response
	 */
	public static void monitorDialogWithTimeout(Opbm	opbm,
												String	id,
												int		timeoutSeconds)
	{
		int count;

		if (timeoutSeconds == 0)
			timeoutSeconds = 1000000;	// Wait for a million seconds

		count = 0;
		try {
			while (count < timeoutSeconds * 5)
			{	// We keep the dialog up for up to N seconds
				Thread.sleep(200);
				// Check to see if they've clicked something
				if (!opbm.getDialogResponse(id).equalsIgnoreCase("unanswered"))
					return;	// They have
				// Nope, still going
				++count;
			}
			// When we get here, it's a timeout
		} catch (InterruptedException ex) {
		}
	}

	/**
	 * Returns names for single-digit numbers, computed number values for larger
	 * @param number any integer
	 * @return names for 0 to 9, computed number otherwise
	 */
	public static String getNumberName(int number)
	{
		switch (number)
		{
			case 0:		return("zero");
			case 1:		return("one");
			case 2:		return("two");
			case 3:		return("three");
			case 4:		return("four");
			case 5:		return("five");
			case 6:		return("six");
			case 7:		return("seven");
			case 8:		return("eight");
			case 9:		return("nine");
			default:
				return(Integer.toString(number));
		}
	}

	/**
	 * Add properties taken from:
	 * http://download.oracle.com/javase/1.4.2/docs/api/java/lang/System.html#getProperties%28%29
	 * @param tag Xml to add items to
	 */
	public static void appendJavaInfo(Xml tag)
	{
		if (tag != null)
		{	// VM platform, 32-bit or 64-bit
			Xml tagNew;

			// Append some system properties
			tagNew = tag.appendChild(new Xml("property", "sun.arch.data.model"));
			tagNew.appendAttribute(new Xml("value", System.getProperty("sun.arch.data.model")));
			tagNew.appendAttribute(new Xml("desc", "Java Virtual Machine is 32-bit or 64-bit?"));

			tagNew = tag.appendChild(new Xml("property", "Opbm.m_jvmHome"));
			tagNew.appendAttribute(new Xml("value", Opbm.m_jvmHome));
			tagNew.appendAttribute(new Xml("desc", "Home directory the harness is using"));

			tagNew = tag.appendChild(new Xml("property", "autoLogon"));
			tagNew.appendAttribute(new Xml("value", (Opbm.isAutoLogonEnabled() ? "yes" : "no")));
			tagNew.appendAttribute(new Xml("desc", "Is autoLogon enabled?"));

			tagNew = tag.appendChild(new Xml("property", "UAC"));
			tagNew.appendAttribute(new Xml("value", (Opbm.isUACEnabled() ? "yes" : "no")));
			tagNew.appendAttribute(new Xml("desc", "Is UAC enabled?"));

			// System properties
			appendJavaSystemGetPropertyItem(tag, "java.version",					"Java Runtime Environment version");
			appendJavaSystemGetPropertyItem(tag, "java.vendor",						"Java Runtime Environment vendor");
			appendJavaSystemGetPropertyItem(tag, "java.vendor.url",					"Java vendor url");
			appendJavaSystemGetPropertyItem(tag, "java.home",						"Java installation directory");
			appendJavaSystemGetPropertyItem(tag, "java.vm.specification.version",	"Java Virtual Machine specification version");
			appendJavaSystemGetPropertyItem(tag, "java.vm.specification.vendor",	"Java Virtual Machine specification vendor");
			appendJavaSystemGetPropertyItem(tag, "java.vm.specification.name",		"Java Virtual Machine specification name");
			appendJavaSystemGetPropertyItem(tag, "java.specification.version",		"Java Runtime Environment specification version");
			appendJavaSystemGetPropertyItem(tag, "java.specification.vendor",		"Java Runtime Environment specification vendor");
			appendJavaSystemGetPropertyItem(tag, "java.specification.name",			"Java Runtime Environment specification name");
			appendJavaSystemGetPropertyItem(tag, "java.vm.version",					"Java Virtual Machine implementation version");
			appendJavaSystemGetPropertyItem(tag, "java.vm.vendor",					"Java Virtual Machine implementation vendor");
			appendJavaSystemGetPropertyItem(tag, "java.vm.name",					"Java Virtual Machine implementation name");
			appendJavaSystemGetPropertyItem(tag, "java.class.version",				"Java class format version number");
			appendJavaSystemGetPropertyItem(tag, "java.class.path",					"Java class path");
			appendJavaSystemGetPropertyItem(tag, "java.library.path",				"List of paths to search when loading libraries");
			appendJavaSystemGetPropertyItem(tag, "java.io.tmpdir",					"Default temp file path");
			appendJavaSystemGetPropertyItem(tag, "java.compiler",					"Name of JIT compiler to use");
			appendJavaSystemGetPropertyItem(tag, "java.ext.dirs",					"Path of extension directory or directories");
			appendJavaSystemGetPropertyItem(tag, "os.name",							"Operating system name");
			appendJavaSystemGetPropertyItem(tag, "os.arch",							"Operating system architecture");
			appendJavaSystemGetPropertyItem(tag, "os.version",						"Operating system version");
			appendJavaSystemGetPropertyItem(tag, "file.separator",					"File separator (slash or backslash)");
			appendJavaSystemGetPropertyItem(tag, "path.separator",					"Path separator (colon)");
			appendJavaSystemGetPropertyItem(tag, "line.separator",					"Line separator (as in backslash n)");
			appendJavaSystemGetPropertyItem(tag, "user.name",						"User's account name");
			appendJavaSystemGetPropertyItem(tag, "user.home",						"User's home directory");
			appendJavaSystemGetPropertyItem(tag, "user.dir",						"User's current working directory");

			// Grab all properties the system has, and include them
			Properties props = System.getProperties();
			Enumeration e = props.propertyNames();
			while (e.hasMoreElements())
			  appendJavaSystemGetPropertyItem(tag, (String)e.nextElement(), "Enumerated property");
		}
	}

	/**
	 * Appends the specified system property to the specified tag, along with
	 * the specified readable description
	 * @param tag xml to add to
	 * @param systemProperty one of the items listed on http://download.oracle.com/javase/1.4.2/docs/api/java/lang/System.html#getProperties%28%29
	 * @param description human readable description of the property
	 */
	public static void appendJavaSystemGetPropertyItem(Xml		tag,
													   String	systemProperty,
													   String	description)
	{
		if (tag != null)
		{
			Xml tagNew = tag.appendChild(new Xml("property", systemProperty));
			if (tagNew != null)
			{
				try {
					tagNew.appendAttribute("value", System.getProperty(systemProperty));
				} catch (Throwable t) {
					tagNew.appendAttribute("value", "Unreadable, threw an exception");
				}
				tagNew.appendAttribute("desc", description);
			}
		}
	}

	/**
	 * Certain tags have more easily readable meanings.  This method translates
	 * the tag name into its readable meaning.
	 */
	public static String translateScriptsAbstractOptionsTagName(String tag)
	{
		String translation;

		if (tag.equalsIgnoreCase("atomOnSpinup")) {
			translation = "Spinup atom";
		} else if (tag.equalsIgnoreCase("atomAfterRuns")) {
			translation = "After Run atom";
		} else if (tag.equalsIgnoreCase("atomBeforeRuns")) {
			translation = "Before Run atom";
		} else if (tag.equalsIgnoreCase("atomOnFailure")) {
			translation = "Failure atom";
		} else {
			return(tag);
		}
		return(translation);
	}

	/**
	 * Creates a display for use with the OpbmColumns class, of the form:
	 *		<display width="640" height="480" caption="whatever">
	 *			<column header="Conflict" element="conflict" width="50%"/>
	 *			<column header="Resolution" element="resolution" width="50%"/>
	 *		</display>
	 *
	 * @param width how wide should the window be (without decoration)
	 * @param height how high should the window be (without titlebar or decoration)
	 * @param column1Header name of the column header
	 * @param column1Element name of the element to display there
	 * @param column1Width width for that column
	 * @param column2Header name of the column header
	 * @param column2Element name of the element to display there
	 * @param column2Width width for that column
	 * @return
	 */
	public static Xml createSimpleTwoColumnDisplay(int		width,
												   int		height,
												   String	caption,
												   String	column1Header,
												   String	column1Element,
												   String	column1Width,
												   String	column2Header,
												   String	column2Element,
												   String	column2Width)
	{
		Xml display, column1, column2;

		display = new Xml("display");
		column1 = new Xml("column");
		column2 = new Xml("column");
		display.appendChild(column1);
		display.appendChild(column2);

		display.appendAttribute("width",	Integer.toString(width));
		display.appendAttribute("height",	Integer.toString(height));
		display.appendAttribute("caption",	caption);

		column1.appendAttribute("header",	column1Header);
		column1.appendAttribute("element",	column1Element);
		column1.appendAttribute("width",	column1Width);

		column2.appendAttribute("header",	column2Header);
		column2.appendAttribute("element",	column2Element);
		column2.appendAttribute("width",	column2Width);

		return(display);
	}

	/**
	 * Converts the RRGGBB format (from HTML syntax) into the specified Color
	 * class value
	 * @param rrggbb
	 * @param defaultIfNotValid
	 * @return Color specified, or the default value if not a valid format
	 */
	public static Color extractColorFromRRGGBBformat(String		rrggbb,
													 Color		defaultIfNotValid)
	{
		int r, g, b;

		if (verifyIsHexadecimal(rrggbb, 6))
		{	// It's a valid hexadecimal format
			r	= getTwoDigitHexValue(rrggbb);
			g	= getTwoDigitHexValue(rrggbb.substring(2));
			b	= getTwoDigitHexValue(rrggbb.substring(4));
			return(new Color(r, g, b));

		} else {
			// Invalid, return the default
			return(defaultIfNotValid);
		}
	}

	/**'
	 * Checks every character of the input to determine if it is a hexadecimal
	 * character or not.  If any are not hex characters, then return false
	 * @param input the string to evaluate
	 * @param length the length of the characters there at that string
	 */
	public static boolean verifyIsHexadecimal(String	input,
											  int		length)
	{
		int i;

		if (input.length() >= length)
		{	// The string is at least long enough
			for (i = 0; i < length; i++)
			{	// Try each character repeatedly until we're done with the string
				if (!"0123456789abcdefABCDEF".contains(input.substring(i, i+1)))
				{	// Invalid
					return(false);
				}
			}
			// We're good
			return(true);
		}
		// input is too short
		return(false);
	}

	/**
	 * Returns the value of the characters specified by the two-digit
	 * hexadecimal value.
	 * @param xx
	 * @return
	 */
	private static int getTwoDigitHexValue(String xx)
	{
		int msn, lsn;	// most-significant and least-significant nibbles

		if (verifyIsHexadecimal(xx, 2))
		{	// It's valid
			msn		= getOneDigitHexValue(xx);
			lsn		= getOneDigitHexValue(xx.substring(1));
			return((msn << 4) + lsn);

		} else {
			// It's not a valid hexadecimal value
			return(0);
		}
	}

	/**
	 * Returns the value of the nibble character specified by the one-digit
	 * hexadecimal value
	 * @param x a single hexadecimal digit, which must be valid
	 * @return the value
	 */
	private static int getOneDigitHexValue(String x)
	{
		char c;

		if (verifyIsHexadecimal(x, 1))
		{	// It's valid
			c = x.charAt(0);
			if (c >= '0' && c <= '9')
			{	// It's 0-9
				return(c - '0');

			} else if (c >= 'A' && c <= 'F') {
				// It's A-F
				return(c - 'A' + 10);

			} else if (c >= 'a' && c <= 'f') {
				// It's a-f
				return(c - 'a' + 10);

			} else {
				// We should never get here
				return(0);
			}


		} else {
			// It's not a valid hexadecimal value
			return(0);
		}
	}

	/**
	 * Evaluates a string and returns true if it's Yes, True or 1
	 * @param test
	 * @return
	 */
	public static boolean isYes(String test)
	{
		boolean result = true;
		if (test.toLowerCase().startsWith("y"))
		{	// It's yes
		} else if (test.toLowerCase().startsWith("t")) {
			// It's true
		} else if (test.toLowerCase().startsWith("1")) {
			// It's one

		} else {
			// Nope
			result = false;
		}
		return(result);
	}

	/**
	 * Evaluates a string and returns if it's not Yes, True or 1
	 * @param test
	 * @return
	 */
	public static boolean isNo(String test)
	{
		return(!isYes(test));
	}

	/**
	 * For multiple-monitor systems, queries the maximum size available
	 */
	public static Dimension getDesktopMaxScreen(int		minWidth,
												int		minHeight)
	{
		int i;
		Dimension maxSize;

		// So we check the size of the host video display to see if it can be bigger, and if so, then we make it bigger
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		// Get size of each screen
		maxSize = new Dimension(minWidth, minHeight);
		for (i = 0; i < gs.length; i++)
		{
			DisplayMode dm = gs[i].getDisplayMode();
			if (dm.getWidth() > maxSize.getWidth() && dm.getHeight() > maxSize.getHeight())
			{	// Update the max
				maxSize.setSize(dm.getWidth(), dm.getHeight());
			}
		}
		return(maxSize);
	}

	/**
	 * Returns the string that would be used in the HKCU\Software\Microsoft\Windows\CurrentVersion\RunOnce\opbm
	 * registry key for restarting the application on an official run after
	 * rebooting.
	 * @return the full path required to execute the resetarter in RunOnce\opbm
	 */
	public static String getRestarterString()
	{
		return("\"" + Utils.getCurrentDirectory() + "\\restarter.exe\" \"" + Utils.getCurrentDirectory() + "\" \"" + Opbm.m_jvmHome + "\" opbm.jar");
	}

	/**
	 * Returns the string that would be used in the HKCU\Software\Microsoft\Windows\CurrentVersion\RunOnce\opbm
	 * registry key for restarting the application on an official run after
	 * rebooting, but without an automatic restart of the benchmark in progress.
	 * @return the full path required to execute the resetarter in RunOnce\opbm
	 */
	public static String getRestarterStringNoRestart()
	{
		return("\"" + Utils.getCurrentDirectory() + "\\restarter.exe\" \"" + Utils.getCurrentDirectory() + "\" \"" + Opbm.m_jvmHome + "\" opbm.jar -noRestart");
	}

	/**
	 * Returns the string that would be used in the HKCU\Software\Microsoft\Windows\CurrentVersion\RunOnce\opbmpostboot
	 * registry key for noting the reboot time after rebooting.
	 * @return the full path required to execute the postboot.exe in RunOnce\opbmpostboot
	 */
	public static String getPostbootString()
	{
		String fileName;

		fileName = Opbm.getRunningDirectory() + "postboot.xml";
		if (fileName.contains(" "))
			fileName = "\"" + fileName + "\"";

		return("\"" + Utils.getCurrentDirectory() + "\\..\\autoIt\\common\\opbm\\exe\\postboot.exe\" " + fileName);
	}

	public static void copyManifestDotXmlToManifestDateTimeDotXml()
	{
		String newName;

		newName = Opbm.getRunningDirectory() + "manifest_" + convertFilenameToLettersAndNumbersOnly(getDateTimeAs_Mmm_DD_YYYY_at_HH_MM_SS()) + ".xml";
		copyFile(Opbm.getRunningDirectory() + "manifest.xml", newName);
	}

	public static void copyFile(String		sourceFilename,
								String		destFilename)
	{
		File srcFile, dstFile;
		FileChannel srcChannel, dstChannel;

		try {
			srcFile	= new File(sourceFilename);
			dstFile	= new File(destFilename);
			if (!dstFile.exists())
			{	// Overwrite existing file if need be
				dstFile.createNewFile();
			}

			srcChannel	= null;
			dstChannel	= null;
			try
			{	// Grab our streams, and transfer
				srcChannel			= new FileInputStream(srcFile).getChannel();
				dstChannel			= new FileOutputStream(dstFile).getChannel();
				dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
			}
			finally
			{	// Clean house
				if (srcChannel != null)
					srcChannel.close();
				if (dstChannel	 != null)
					dstChannel	.close();
			}

		} catch (IOException ex) {
			System.out.println("Unable to copy file " + sourceFilename + " to " + destFilename);
		}
	}


//////////
// Class constants
/////
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
