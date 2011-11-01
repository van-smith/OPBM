//////////
//
// preboot.cpp
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
// This source file is the top-level file used to write time data
// to a preboot.xml file, which is used to determine the last second
// before Windows shuts down all processes and reboots the system.
//
// The file preboot.xml is opened and written to once every second
// until it is terminated through the reboot.
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
//		preboot "c:\\some\\full\\path\\to\\preboot.xml"
//
// The above example will launch preboot and have it write the date + time
// to preboot.xml, once every second.
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
		sprintf_s(file, sizeof(file), lpCmdLine);

		// Verify there's not a double-quote prefixing the pathname
		if (file[0] == '\"')
			memcpy(&file[0], &file[1], strlen(file) + 1);
		// Now remove all other instances of a double-quote, replacing each with a NULL (should only be one at the end if it's a properly formatted syntax line)
		for (i = 0; i < sizeof(file); i++)
		{
			if (file[i] == '\"')
				file[i] = 0;
		}

		// (Re-)Create our files
		fopen_s(&lfh, file, "wb+");
		if (!lfh)
		{	// Error creating file1
			std::cerr << "PREBOOT: Fatal Error: Unable to create file " << file;
			exit(-1);
		}

		// Begin writing time data to our Xml repeatedly until the system shuts down
		while (1)
		{	// Grab the current local time
			GetLocalTime(&st);

			// Create our xml
			sprintf_s(time, sizeof(time), xmlTemplate, st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);

			// Write it
			fseek(lfh, 0, SEEK_SET);
			numwritten = fwrite(time, 1, strlen(time), lfh);
			if (numwritten != strlen(time))
			{	// Error writing
				std::cerr << "PREBOOT: Error: Unable to write content to " << file;
			}

			// Wait one second
			Sleep(1000);
		}
		// This program runs until it is terminated eternally by Windows, the result of a reboot
	}
