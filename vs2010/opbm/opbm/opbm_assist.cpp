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
// August 17, 2011
// Registry key and supporitive functions moved to opbm_common.cpp,
// to allow sharing between opbm.dll (scripts) and opbm64.dll (JNI).
//////////


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