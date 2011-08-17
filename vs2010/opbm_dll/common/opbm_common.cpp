//////////
//
// opbm_common.cpp
//
// Shared functions between opbm.dll, and opbm64.dll (used by Java JNI interface)
//
/////




void GetHarnessCSVDirectory(char* dirname, int dirnameLength)
{
	USES_CONVERSION;
	// Clear out the dirname contents
	memset(dirname, 0, dirnameLength);

	// Grab the directory from Windows
	SHGetSpecialFolderPathA(0, dirname, CSIDL_MYDOCUMENTS, TRUE);

	// Make sure it ends with a backslash
	if (*(dirname +strlen(dirname) - 1) != '\\')
		*(dirname + strlen(dirname)) = '\\';

	// Append the portion relative to the harness's CSV directory
	memcpy(dirname + strlen(dirname), "opbm\\results\\csv\\", 17);

	// Make sure the directory exists
	SHCreateDirectoryEx(NULL, A2T(dirname), NULL);
}


void GetHarnessXMLDirectory(char* dirname, int dirnameLength)
{
	USES_CONVERSION;
	// Initialize the path
	memset(dirname, 0, dirnameLength);

	// Grab the directory from Windows
	SHGetSpecialFolderPathA(0, dirname, CSIDL_MYDOCUMENTS, TRUE);

	// Make sure it ends with a backslash
	if (*(dirname +strlen(dirname) - 1) != '\\')
		*(dirname + strlen(dirname)) = '\\';

	// Append the portion relative to the harness's XML directory
	memcpy(dirname + strlen(dirname), "opbm\\results\\xml\\", 17);

	// Make sure the directory exists
	SHCreateDirectoryEx(NULL, A2T(dirname), NULL);
}




void GetHarnessTempDirectory(char* dirname, int dirnameLength)
{
	USES_CONVERSION;
	// Clear out the dirname contents
	memset(dirname, 0, dirnameLength);

	// Grab the directory from Windows
	SHGetSpecialFolderPathA(0, dirname, CSIDL_MYDOCUMENTS, TRUE);

	// Make sure it ends with a backslash
	if (*(dirname +strlen(dirname) - 1) != '\\')
		*(dirname + strlen(dirname)) = '\\';

	// Append the portion relative to the harness's temp directory
	memcpy(dirname + strlen(dirname), "opbm\\temp\\", 10);

	// Make sure the directory exists
	SHCreateDirectoryEx(NULL, A2T(dirname), NULL);
}



void GetScriptCSVDirectory(char* dirname, int dirnameLength)
{
	USES_CONVERSION;
	// Initialize the path
	memset(dirname, 0, dirnameLength);

	// Grab the directory from Windows
	SHGetSpecialFolderPathA(0, dirname, CSIDL_MYDOCUMENTS, TRUE);

	// Make sure it ends with a backslash
	if (*(dirname +strlen(dirname) - 1) != '\\')
		*(dirname + strlen(dirname)) = '\\';

	// Append the portion relative to the script's CSV directory
	memcpy(&dirname[strlen(dirname)], "opbm\\scriptOutput\\", 18);

	// Make sure the directory exists
	SHCreateDirectoryEx(NULL, A2T(dirname), NULL);
}



void GetScriptTempDirectory(char* dirname, int dirnameLength)
{
	USES_CONVERSION;
	// Initialize the path
	memset(dirname, 0, dirnameLength);

	// Grab the directory from Windows
	SHGetSpecialFolderPathA(0, dirname, CSIDL_MYDOCUMENTS, TRUE);

	// Make sure it ends with a backslash
	if (*(dirname +strlen(dirname) - 1) != '\\')
		*(dirname + strlen(dirname)) = '\\';

	// Append the portion relative to the script's temp directory
	memcpy(&dirname[strlen(dirname)], "opbm\\scriptOutput\\temp\\", 23);

	// Make sure the directory exists
	SHCreateDirectoryEx(NULL, A2T(dirname), NULL);
}




void GetCSIDLDirectory(char* dirname, int dirnameLength, char* csidl_name)
{
	USES_CONVERSION;
	int csid;
	bool failure;

	// Initialize the path
	memset(dirname, 0, dirnameLength);

	// Find out what directory they're after
	failure = false;
	if (_stricmp(csidl_name, "DESKTOP") == 0)
	{	// <desktop>
		csid = CSIDL_DESKTOP;
	
	} else if (_stricmp(csidl_name, "INTERNET") == 0) {
		// Internet Explorer (icon on desktop)
		csid = CSIDL_INTERNET;
	
	} else if (_stricmp(csidl_name, "PROGRAMS") == 0) {
		// Start Menu\Programs
		csid = CSIDL_PROGRAMS;
	
	} else if (_stricmp(csidl_name, "CONTROLS") == 0) {
		// My Computer\Control Panel
		csid = CSIDL_CONTROLS;
	
	} else if (_stricmp(csidl_name, "PRINTERS") == 0) {
		// My Computer\Printers
		csid = CSIDL_PRINTERS;
	
	} else if (_stricmp(csidl_name, "PERSONAL")		== 0 ||
				_stricmp(csidl_name, "MY_DOCUMENTS")	== 0 ||
				_stricmp(csidl_name, "MY_DOCS")			== 0 ||
				_stricmp(csidl_name, "MYDOCS")			== 0 ||
				_stricmp(csidl_name, "MYDOCUMENTS")		== 0) {
		// My Documents
		csid = CSIDL_PERSONAL;
	
	} else if (_stricmp(csidl_name, "FAVORITES") == 0) {
		// <user name>\Favorites
		csid = CSIDL_FAVORITES;
	
	} else if (_stricmp(csidl_name, "STARTUP") == 0) {
		// Start Menu\Programs\Startup
		csid = CSIDL_STARTUP;
	
	} else if (_stricmp(csidl_name, "RECENT") == 0) {
		// <user name>\Recent
		csid = CSIDL_RECENT;
	
	} else if (_stricmp(csidl_name, "SENDTO") == 0) {
		// <user name>\SendTo
		csid = CSIDL_SENDTO;
	
	} else if (_stricmp(csidl_name, "BITBUCKET") == 0) {
		// <desktop>\Recycle Bin
		csid = CSIDL_BITBUCKET;
	
	} else if (_stricmp(csidl_name, "STARTMENU") == 0) {
		// <user name>\Start Menu
		csid = CSIDL_STARTMENU;
	
	} else if (_stricmp(csidl_name, "MYMUSIC") == 0) {
		// "My Music" folder
		csid = CSIDL_MYMUSIC;
	
	} else if (_stricmp(csidl_name, "MYVIDEO") == 0) {
		// "My Videos" folder
		csid = CSIDL_MYVIDEO;
	
	} else if (_stricmp(csidl_name, "DESKTOPDIRECTORY") == 0) {
		// <user name>\Desktop
		csid = CSIDL_DESKTOPDIRECTORY;
	
	} else if (_stricmp(csidl_name, "DRIVES") == 0) {
		// My Computer
		csid = CSIDL_DRIVES;
	
	} else if (_stricmp(csidl_name, "NETWORK") == 0) {
		// Network Neighborhood (My Network Places)
		csid = CSIDL_NETWORK;
	
	} else if (_stricmp(csidl_name, "NETHOOD") == 0) {
		// <user name>\nethood
		csid = CSIDL_NETHOOD;
	
	} else if (_stricmp(csidl_name, "FONTS") == 0) {
		// windows\fonts
		csid = CSIDL_FONTS;
	
	} else if (_stricmp(csidl_name, "COMMON_STARTMENU") == 0) {
		// All Users\Start Menu
		csid = CSIDL_COMMON_STARTMENU;
	
	} else if (_stricmp(csidl_name, "COMMON_PROGRAMS") == 0) {
		// All Users\Start Menu\Programs
		csid = CSIDL_COMMON_PROGRAMS;
	
	} else if (_stricmp(csidl_name, "COMMON_STARTUP") == 0) {
		// All Users\Startup
		csid = CSIDL_COMMON_STARTUP;
	
	} else if (_stricmp(csidl_name, "COMMON_DESKTOPDIRECTORY") == 0) {
		// All Users\Desktop
		csid = CSIDL_COMMON_DESKTOPDIRECTORY;
	
	} else if (_stricmp(csidl_name, "APPDATA") == 0) {
		// <user name>\Application Data
		csid = CSIDL_APPDATA;
	
	} else if (_stricmp(csidl_name, "PRINTHOOD") == 0) {
		// <user name>\PrintHood
		csid = CSIDL_PRINTHOOD;
	
	} else if (_stricmp(csidl_name, "LOCAL_APPDATA") == 0) {
		// <user name>\Local Settings\Applicaiton Data (non roaming)
		csid = CSIDL_LOCAL_APPDATA;
	
	} else if (_stricmp(csidl_name, "ALTSTARTUP") == 0) {
		// non localized startup
		csid = CSIDL_ALTSTARTUP;
	
	} else if (_stricmp(csidl_name, "COMMON_ALTSTARTUP") == 0) {
		// non localized common startup
		csid = CSIDL_COMMON_ALTSTARTUP;
	
	} else if (_stricmp(csidl_name, "COMMON_FAVORITES") == 0) {
		// favorites folder
		csid = CSIDL_COMMON_FAVORITES;
	
	} else if (_stricmp(csidl_name, "INTERNET_CACHE") == 0) {
		// internet cache folder
		csid = CSIDL_INTERNET_CACHE;
	
	} else if (_stricmp(csidl_name, "COOKIES") == 0) {
		// cookies folder
		csid = CSIDL_COOKIES;
	
	} else if (_stricmp(csidl_name, "HISTORY") == 0) {
		// history folder
		csid = CSIDL_HISTORY;
	
	} else if (_stricmp(csidl_name, "COMMON_APPDATA") == 0) {
		// All Users\Application Data
		csid = CSIDL_COMMON_APPDATA;
	
	} else if (_stricmp(csidl_name, "WINDOWS") == 0) {
		// GetWindowsDirectory(), typically "c:\windows\"
		csid = CSIDL_WINDOWS;
	
	} else if (_stricmp(csidl_name, "SYSTEM") == 0) {
		// GetSystemDirectory(), typically "c:\windows\system32\"
		csid = CSIDL_SYSTEM;
	
	} else if (_stricmp(csidl_name, "PROGRAM_FILES") == 0) {
		// C:\Program Files
		csid = CSIDL_PROGRAM_FILES;
	
	} else if (_stricmp(csidl_name, "MYPICTURES") == 0) {
		// C:\Program Files\My Pictures
		csid = CSIDL_MYPICTURES;
	
	} else if (_stricmp(csidl_name, "PROFILE") == 0) {
		// USERPROFILE
		csid = CSIDL_PROFILE;
	
	} else if (_stricmp(csidl_name, "SYSTEMX86") == 0) {
		// x86 system directory on RISC
		csid = CSIDL_SYSTEMX86;
	
	} else if (_stricmp(csidl_name, "PROGRAM_FILESX86") == 0) {
		// x86 C:\Program Files on RISC
		csid = CSIDL_PROGRAM_FILESX86;
	
	} else if (_stricmp(csidl_name, "PROGRAM_FILES_COMMON") == 0) {
		// C:\Program Files\Common
		csid = CSIDL_PROGRAM_FILES_COMMON;
	
	} else if (_stricmp(csidl_name, "PROGRAM_FILES_COMMONX86") == 0) {
		// x86 Program Files\Common on RISC
		csid = CSIDL_PROGRAM_FILES_COMMONX86;
	
	} else if (_stricmp(csidl_name, "COMMON_TEMPLATES") == 0) {
		// All Users\Templates
		csid = CSIDL_COMMON_TEMPLATES;
	
	} else if (_stricmp(csidl_name, "COMMON_DOCUMENTS") == 0) {
		// All Users\Documents
		csid = CSIDL_COMMON_DOCUMENTS;
	
	} else if (_stricmp(csidl_name, "COMMON_ADMINTOOLS") == 0) {
		// All Users\Start Menu\Programs\Administrative Tools
		csid = CSIDL_COMMON_ADMINTOOLS;
	
	} else if (_stricmp(csidl_name, "ADMINTOOLS") == 0) {
		// <user name>\Start Menu\Programs\Administrative Tools
		csid = CSIDL_ADMINTOOLS;
	
	} else if (_stricmp(csidl_name, "CONNECTIONS") == 0) {
		// Network and Dial-up Connections
		csid = CSIDL_CONNECTIONS;
	
	} else if (_stricmp(csidl_name, "COMMON_MUSIC") == 0) {
		// All Users\My Music
		csid = CSIDL_COMMON_MUSIC;
	
	} else if (_stricmp(csidl_name, "COMMON_PICTURES") == 0) {
		// All Users\My Pictures
		csid = CSIDL_COMMON_PICTURES;
	
	} else if (_stricmp(csidl_name, "COMMON_VIDEO") == 0) {
		// All Users\My Video
		csid = CSIDL_COMMON_VIDEO;
	
	} else if (_stricmp(csidl_name, "RESOURCES") == 0) {
		// Resource Directory
		csid = CSIDL_RESOURCES;
	
	} else if (_stricmp(csidl_name, "RESOURCES_LOCALIZED") == 0) {
		// Localized Resource Direcotry
		csid = CSIDL_RESOURCES_LOCALIZED;
	
	} else if (_stricmp(csidl_name, "COMMON_OEM_LINKS") == 0) {
		// Links to All Users OEM specific apps
		csid = CSIDL_COMMON_OEM_LINKS;
	
	} else if (_stricmp(csidl_name, "CDBURN_AREA") == 0) {
		// USERPROFILE\Local Settings\Application Data\Microsoft\CD Burning
		csid = CSIDL_CDBURN_AREA;
	
	} else if (_stricmp(csidl_name, "COMPUTERSNEARME") == 0) {
		// Computers Near Me (computered from Workgroup membership)
		csid = CSIDL_COMPUTERSNEARME;

	} else {
		failure = true;
		memcpy(dirname, "?unknown?csidl?name?", 20);
	}


	if (!failure)
	{
		// Grab the directory from Windows
		SHGetSpecialFolderPathA(0, dirname, csid, TRUE);

		// Make sure it ends with a backslash
		if (*(dirname +strlen(dirname) - 1) != '\\')
			*(dirname + strlen(dirname)) = '\\';

		// Make sure the directory exists
		SHCreateDirectoryEx(NULL, A2T(dirname), NULL);
	}
}




//////////
//
// Functions related to the above AU3 plugin functions:
//		NoteAllOpenWindows()
//		CloseAllWindowsNotPreviouslyNoted()
//
//////
	// Begin:
	// Callback function, physically appends this HWND to the list of HWNDs already captured
	BOOL CALLBACK EnumWindowsCallbackProc(HWND hwnd, LPARAM lParam)
	{
		++hwndMaxCount;
		if (hwndMaxCount < _MAX_HWND_COUNT)
		{	// We're good, we can store this one
			enumeratedWindows[hwndMaxCount] = hwnd;

		} else {
			// Too many ducks in the barrel!
			// We just have to ignore the count beyond here
		}
		// Keep enumerating
		return TRUE;
	}

	//	Callback function, physically appends this HWND to the list of HWNDs already captured
	BOOL CALLBACK ComparativeWindowsCallbackProc(HWND hwnd, LPARAM lParam)
	{
		wchar_t windowName[2048];
		int i;

		if (GetParent(hwnd) == NULL)
		{	// We only close top-level windows, all subordinate windows should be closed politely when their top-level window closes
			for (i = 0; i < hwndMaxCount; i++)
			{
				if (enumeratedWindows[i] == hwnd)
				{	// We found a match, this one is already known to us, it was there when noted previously
					// Ignore it
					return TRUE;
				}
			}

			// If we get here, this window wasn't found
			GetWindowText(hwnd, &windowName[0], sizeof(windowName));
			if (windowName[0] != 0)
			{	// We only close windows with real names
				SendMessage(hwnd, WM_CLOSE, 0, 0);
				++hwndsClosed;
			}
		}

		// Keep enumerating
		return TRUE;
	}
	// :End




//////////
//
// The key is of the form:  ROOT\name\name\name\name\key,
//
// Where ROOT is one of the following:
//		HKCR	- HKEY_CLASSES_ROOT
//		HKCU	- HKEY_CURRENT_USER
//		HKLM	- HKEY_LOCAL_MACHINE
//		HKU		- HKEY_USERS
//
// This function attempts to read the key level, obtaining its value
//
/////
	char* GetRegistryKeyValue(char* key)
	{
		int skip;
		HKEY hk, hkout;
		DWORD type, length;
		BYTE* intermediate;
		char* keyName;
		char* converted;

		// Break out our HKEY root, and keyname components
		keyName = breakoutHkeyComponents(key, hk, skip);
		if (RegOpenKeyExA( hk, key + skip, 0, KEY_READ, &hkout ) == ERROR_SUCCESS)
		{	// Success, we can read the key value
			if (RegQueryValueExA( hkout, keyName, 0, &type, NULL, &length ) == ERROR_SUCCESS)
			{
				// Allocate a buffer to read the value
				intermediate = (BYTE*)malloc(length);
				RegQueryValueExA( hkout, keyName, 0, &type, intermediate, &length );
				RegCloseKey(hkout);
				switch (type)
				{
					case REG_BINARY:
					case REG_EXPAND_SZ:
					case REG_MULTI_SZ:
					case REG_SZ:
						// The intermediate buffer is the wholeness of the string
						return((char*)intermediate);

					case REG_DWORD:
						// Convert the dword value to its string form
						converted = (char*)malloc(length);
						if (converted != NULL)
						{
							sprintf_s(converted, length, "%u", *(DWORD*)intermediate);
						}
						free(intermediate);
						return(converted);

					case REG_QWORD:
						// Convert the qword value to its string form
						converted = (char*)malloc(length);
						if (converted != NULL)
						{
							sprintf_s(converted, length, "%I64u", *(__int64*)intermediate);
						}
						free(intermediate);
						return(converted);

					default:
						// Unhandled type
						free(intermediate);
						return(NULL);
				}

			} else {
				// Error, return NULL
				return(NULL);
			}

		} else {
			// Failure, return a NULL
			return(NULL);

		}
	}




//////////
//
// Does a double-compare, one with case, one without case.
//
// Returns:
//		0	- no match
//		1	- exact match
//		2	- case-insensitive match
//
/////
	int caseNocaseCompare(char* left, char* right, int length)
	{
		int i;
		char l, r;
		bool matchNoCase = true;
		bool matchCase = true;

		for (i = 0; i < length && (matchNoCase || matchCase); i++)
		{
			l = tolower(left[i]);
			r = tolower(right[i]);

			if (matchNoCase && left[i] != right[i])
				matchNoCase = false;

			if (matchCase && l != r)
				matchCase = false;
		}
		if (matchNoCase)
			return(1);	// Exact match
		else if (matchCase)
			return(2);	// Case-insensitive match
		else
			return(0);	// No match
	}




//////////
//
// Does a double-compare, one with case, one without case, looking throughout
// the entire haystack to see if the needle matches.
//
// Returns:
//		0	- no match
//		1	- exact match
//		2	- case-insensitive match
//
/////
	int caseNocaseContains(char* needle, char* haystack)
	{
		int i, nlength, hlength, result;
		bool matchNoCase = false;

		nlength	= (int)strlen(needle);
		hlength	= (int)strlen(haystack);
		for (i = 0; i + nlength <= hlength; i++)
		{
			result = caseNocaseCompare(needle, haystack + i, nlength);
			if (result == 1)
			{	// We found an exact match, we're done
				return(1);
			} else if (result == 2) {
				matchNoCase = true;
			}
		}
		if (matchNoCase)
			return(2);	// We found at least one instance where it matched with no case
		else
			return(0);	// We did not find one instance
	}




//////////
//
// Sets the registry value to the specified string
//
// Returns:
//
//		0 - failure
//		1 - success
//
/////
	int SetRegistryKeyValueAsString(char* key, char* value)
	{
		int skip;
		HKEY hk, hkout;
		char* keyName;

		// Break out our HKEY root, and keyname components
		keyName = breakoutHkeyComponents(key, hk, skip);
		if (RegCreateKeyExA( hk, key + skip, 0, NULL, REG_OPTION_NON_VOLATILE, KEY_SET_VALUE, NULL, &hkout, NULL ) == ERROR_SUCCESS)
		{	// Success, we can write the key value
			if (RegSetKeyValueA( hkout, 0, keyName, REG_SZ, value, (DWORD)strlen(value) ) == ERROR_SUCCESS)
			{	// Success
				RegCloseKey( hkout );
				return(1);
			}
			// Failure
			RegCloseKey( hkout );
		}
		// Failure, return a zero
		return(0);
	}




//////////
//
// Sets the registry value to the specified dword value
//
// Returns:
//
//		0 - failure
//		1 - success
//
/////
	int SetRegistryKeyValueAsDword(char* key, int value)
	{
		int skip;
		HKEY hk, hkout;
		char* keyName;

		// Break out our HKEY root, and keyname components
		keyName = breakoutHkeyComponents(key, hk, skip);
		if (RegCreateKeyExA( hk, key + skip, 0, NULL, REG_OPTION_NON_VOLATILE, KEY_SET_VALUE, NULL, &hkout, NULL ) == ERROR_SUCCESS)
		{	// Success, we can write the key value
			if (RegSetKeyValueA( hkout, 0, keyName, REG_DWORD, &value, 4 ) == ERROR_SUCCESS)
			{	// Success
				RegCloseKey( hkout );
				return(1);
			}
			// Failure
			RegCloseKey( hkout );
		}
		// Failure, return a zero
		return(0);
	}




//////////
//
// Sets the registry value to the specified binary value
//
// Returns:
//
//		0 - failure
//		1 - success
//
/////
	int SetRegistryKeyValueAsBinary(char* key, char* value, int length)
	{
		int skip;
		HKEY hk, hkout;
		char* keyName;

		// Break out our HKEY root, and keyname components
		keyName = breakoutHkeyComponents(key, hk, skip);
		if (RegCreateKeyExA( hk, key + skip, 0, NULL, REG_OPTION_NON_VOLATILE, KEY_SET_VALUE, NULL, &hkout, NULL ) == ERROR_SUCCESS)
		{	// Success, we can write the key value
			if (RegSetKeyValueA( hkout, 0, keyName, REG_BINARY, &value, length ) == ERROR_SUCCESS)
			{	// Success
				RegCloseKey( hkout );
				return(1);
			}
			// Failure
			RegCloseKey( hkout );
		}
		// Failure, return a zero
		return(0);
	}




//////////
//
// Called as a helper function, to breakout the registry key format:
//		hklm\some\path\to\the\key\and\its\name
//
// returns pointer to "name", and NULL-terminates "hklm\some\path\to\the\key\and\its[NULL]"
// Also sets the skip value, of how many characters to skip past "hklm" or its other ROOT HKEY
//
/////
	char* breakoutHkeyComponents(char* key, HKEY& hk, int& skip)
	{
		char* keyName;

		if (_strnicmp(key, "hkcr\\", 5) == 0) {
			hk = HKEY_CLASSES_ROOT;
			skip = 5;
		} else if (_strnicmp(key, "hkcu\\", 5) == 0) {
			hk = HKEY_CURRENT_USER;
			skip = 5;
		} else if (_strnicmp(key, "hklm\\", 5) == 0) {
			hk = HKEY_LOCAL_MACHINE;
			skip = 5;
		} else if (_strnicmp(key, "hku\\", 4) == 0) {
			hk = HKEY_USERS;
			skip = 4;
		} else {
			// Unknown key root, cannot be accessed
			return(NULL);
		}

		// Backup to the previous level and put a NULL and mark that location for the key name
		keyName = key + strlen(key) - 1;
		while (keyName > key + skip)
		{
			if (*keyName == '\\')
			{	// Found it
				*keyName = 0;
				++keyName;
				break;
			}
			--keyName;
		}

		// If there is no key name, then leave it NULL
		if (keyName == key)
			keyName = NULL;

		return(keyName);
	}
