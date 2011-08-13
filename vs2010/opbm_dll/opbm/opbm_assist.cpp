//////////
//
// opbm_assist.cpp
// Holds functions related to opbm.cpp
//
/////




//////////
//
// Functions related to the above AU3 plugin functions:
//		FirefoxInstallerAssist()
//		ChromeInstallerAssist()
//		OperaInstallerAssist()
//		SafariInstallerAssist()
//		InternetExplorerInstallerAssist()
//
//////
	// prefixDir must be:   "c:\some\dir"
	// srcFile   must be:  "\path\to\some\file.txt"
	bool iCopyFile(wchar_t* prefixDir, wchar_t* srcFile, char* content, int contentLength)
	{
		int length, numwritten;
		wchar_t filename[2048];
		FILE* lfh;

		// Copy the directory part
		length = wcstrncpy(&filename[0], sizeof(filename), prefixDir);

		// See if it ends in a "/" or "\" character
		if (filename[length-1] == L'\\' || filename[length-1] == L'/')
		{	// It does contain a trailing directory character, remove it
			filename[length-1] = 0;
			--length;
		}
		wcstrncpy(&filename[0] + length, (int)sizeof(filename) - length, srcFile);
		// Right now, filename is the full path to the filename

		// Create the file
		_wfopen_s(&lfh, &filename[0], L"wb+");
		if (lfh == NULL)
		{	// Failed
			return(false);
		}
		// Write the contents
		numwritten = (int)fwrite(content, 1, contentLength, lfh);
		fclose(lfh);
		if (numwritten != contentLength)
		{
			return(false);
		}
		// If we make it here, success
		return(true);
	}




	// prefixDir  must be:  "c:\some\dir"
	// postfixDir must be:  "\relative\path\to\some\dir\"
	bool iMakeDirectory(wchar_t* prefixDir, wchar_t* postfixDir)
	{
		int length, result;
		wchar_t dirname[2048];

		// Copy the directory part
		wcstrncpy(&dirname[0], sizeof(dirname), prefixDir);
		length = (int)wcslen(&dirname[0]);

		// See if it ends in a "/" or "\" character
		if (dirname[length-1] == L'\\' || dirname[length-1] == L'/')
		{	// It does contain a trailing directory character, remove it
			dirname[length-1] = 0;
			--length;
		}
		wcstrncpy(&dirname[0] + length, sizeof(dirname) - length, postfixDir);
		// Right now, filename is the full path to the directory
		result = SHCreateDirectoryEx(NULL, &dirname[0], NULL);
		if (result == ERROR_SUCCESS || result == ERROR_ALREADY_EXISTS)
		{	// We're good, it's where it should be
			return(true);
		}
		// If we get here, a failure
		return(false);
	}




	// Copies the src to the dest for max characters, or until src[n] == null is true
	int wcstrncpy(wchar_t* dest, int max, wchar_t* src)
	{
		int length;

		length = 0;
		while (length < max && src[length] != 0)
		{
			dest[length] = src[length];
			++length;
		}
		if (length < max)
		{	// Terminate with a null
			dest[length] = 0;
		}
		return(length);
	}




	// Compares the left and right for max characters in length, returning
	// 0=equal, 1=left greater than right, -1=left less than right
	int wcstrncmp(wchar_t* left, wchar_t* right, int max)
	{
		int length;

		length = 0;
		while (length < max)
		{
			if (left[length] > right[length])
			{	// Left is greater than right
				return(1);

			} else if (left[length] < right[length]) {
				// Left is less than right
				return(-1);
			}
			++length;
		}
		return(0);
	}




	// Compares the left and right for max characters in length, ignoring case, returning
	// 0=equal, 1=left greater than right, -1=left less than right
	int wcstrnicmp(wchar_t* left, wchar_t* right, int max)
	{
		int length;
		wchar_t l, r;

		length = 0;
		while (length < max)
		{
			l = towlower(left[length]);
			r = towlower(right[length]);

			if (l > r)
			{	// Left is greater than right
				return(1);

			} else if (l < r) {
				// Left is less than right
				return(-1);
			}
			++length;
		}
		return(0);
	}




	char* GetOffsetToResource(int number, LPWSTR type, int* size)
	{
		HRSRC r;
		HGLOBAL h;

		r		= FindResource(ghModule, MAKEINTRESOURCE(number), type);
		h		= LoadResource(ghModule, r);
		*size	= SizeofResource(ghModule, r);
		return((char*)LockResource(h));
	}




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
			if (RegQueryValueExA( hkout, keyName, 0, &type, null, &length ) == ERROR_SUCCESS)
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
						converted = (char*)malloc(length + 32);
						if (converted != NULL)
						{
							sprintf_s(converted, length, "%I64u", *(__int64*)intermediate);
						}
						free(intermediate);
						return(converted);

					default:
						// Unhandled type
						free(intermediate);
						return(null);
				}

			} else {
				// Error, return null
				return(null);
			}

		} else {
			// Failure, return a null
			return(null);

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

		nlength	= strlen(needle);
		hlength	= strlen(haystack);
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
		if (RegCreateKeyExA( hk, key + skip, 0, NULL, REG_OPTION_NON_VOLATILE, KEY_SET_VALUE, null, &hkout, NULL ) == ERROR_SUCCESS)
		{	// Success, we can write the key value
			if (RegSetKeyValueA( hkout, 0, keyName, REG_SZ, value, strlen(value) ) == ERROR_SUCCESS)
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
		if (RegCreateKeyExA( hk, key + skip, 0, NULL, REG_OPTION_NON_VOLATILE, KEY_SET_VALUE, null, &hkout, NULL ) == ERROR_SUCCESS)
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
		if (RegCreateKeyExA( hk, key + skip, 0, NULL, REG_OPTION_NON_VOLATILE, KEY_SET_VALUE, null, &hkout, NULL ) == ERROR_SUCCESS)
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
// returns pointer to "name", and null-terminates "hklm\some\path\to\the\key\and\its[null]"
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
			return(null);
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

		// If there is no key name, then leave it null
		if (keyName == key)
			keyName = null;

		return(keyName);
	}



	// From a running 32-bit DLL, determines if running under WOW64
	// If so, then is 64-bit OS, if not, then is 32-bit OS
	BOOL isRunningUnderWOW64(void)
	{
		typedef BOOL (WINAPI* LPFN_ISWOW64PROCESS)(HANDLE, PBOOL);
		LPFN_ISWOW64PROCESS fnIsWow64Process;
		BOOL bIs64BitOS = FALSE;

		// Check if the OS is 64 Bit
		fnIsWow64Process = (LPFN_ISWOW64PROCESS)GetProcAddress(GetModuleHandle(L"kernel32"), "IsWow64Process");
		if (fnIsWow64Process != NULL)
		{
			if (!fnIsWow64Process(GetCurrentProcess(), &bIs64BitOS))
			{	// error
				bIs64BitOS = false;
			}
		}
		return bIs64BitOS;
	}