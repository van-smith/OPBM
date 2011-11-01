//////////
//
// postboot.cpp
//
// -----
// Last Updated:  October 25, 2011
//
// by Van Smith
// Cossatot Analytics Laboratories, LLC. (Cana Labs)
//
// (c) Copyright Cana Labs.
// Free software licensed under the GNU GPL2.
//
// version 1.0
//
/////
//
// This source file is the top-level file used to write the
// time data to a postboot.xml file, which is used to determine
// the first moment Windows wakes back up after rebooting a system.
//
/////

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <iostream>

char xmlTemplate[] = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<opbm>\n\t<time>\n\t\t<year>%04u</year>\n\t\t<month>%02u</month>\n\t\t<day>%02u</day>\n\t\t<hh>%02u</hh>\n\t\t<mm>%02u</mm>\n\t\t<ss>%02u</ss>\n\t</time>\n</opbm>\n\000";

//////////
//
// One command line parameters MUST be provided:  filenameTemplate
//
// Example usage:
//
//		postboot "c:\\some\\full\\path\\to\\postboot%04u.xml"
//
// The above example will launch postboot and have it write the
// current date and time to the postboot.xml file, and then terminate.
//
/////
	int APIENTRY WinMain(HINSTANCE hInstance,
						 HINSTANCE hPrevInstance,
						 LPSTR    lpCmdLine,
						 int       nCmdShow)
	{
		char		file[_MAX_FNAME];
		char		time[sizeof(xmlTemplate) * 2];
		FILE*		lfh;
		int			i, numwritten;
		SYSTEMTIME	st;

		// Create our output file name
		sprintf_s(file, sizeof(file), lpCmdLine, 1);

		// Verify there's not a double-quote prefixing the pathname
		if (file[0] == '\"')
			memcpy(&file[0], &file[1], strlen(file) + 1);
		// Now remove all other instances of a double-quote, replacing each with a NULL (should only be one at the end if it's a properly formatted syntax line)
		for (i = 0; i < sizeof(file); i++)
		{
			if (file[i] == '\"')
				file[i] = 0;
		}

		// (Re-)Create our file
		fopen_s(&lfh, file, "wb+");
		if (!lfh)
		{	// Error creating file1
			std::cerr << "POSTBOOT: Fatal Error: Unable to create file " << file;
			exit(-1);
		}

		// Grab the current local time
		GetLocalTime(&st);

		// Create our xml
		sprintf_s(time, sizeof(time), xmlTemplate, st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);

		// Write it
		fseek(lfh, 0, SEEK_SET);
		numwritten = fwrite(time, 1, strlen(time), lfh);
		if (numwritten != strlen(time))
		{	// Error writing
			std::cerr << "POSTBOOT: Error: Unable to write content to " << file;
		}

		// Close our file handle and exit properly
		fclose(lfh);

		// All done!
		return(0);
	}
