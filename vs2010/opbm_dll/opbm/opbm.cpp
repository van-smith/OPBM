/*
 * Au3_WaitUntilIdle.cpp
 *
 * AutoIt Plugin SDK -	WaitUntilIdle()
 *						WaitUntilSystemIdle()
 *
 * Copyright(c)2011 Cossatot Analytics Laboratories, LLC.
 * Free Software. Released under GPLv2.
 *
 * See:
 *	Free Software Foundation at www.fsf.org, and www.gnu.org.
 *
 */

#include "stdafx.h"
#include "windows.h"
#include "winbase.h"
//#include "shfolder.h"
#include "shlobj.h"
#include "resource2.h"
#include "AtlBase.h"
#include "AtlConv.h"
#include "wctype.h"

#include "..\CPU\cpu.h"
#include "au3\au3plugin.h"
// Note: AU3_GetString() allocates some memory that must be manually freed later using AU3_FreeString()

extern HMODULE ghModule;

//////////
//
// Used for NoteAllOpenWindows() and CloseAllWindowsNotPreviouslyNoted()
// Allocate enough space for 16K windows, which should be more than plenty.
// Note:  Windows doesn't just have windows 
//
/////
	#define _MAX_HWND_COUNT	16384
	HWND		enumeratedWindows[_MAX_HWND_COUNT];
	int			hwndMaxCount	= 0;
	int			hwndsClosed		= 0;
	char		converted[2048];


	// opbm_assist.cpp functions:
	BOOL CALLBACK	EnumWindowsCallbackProc			(HWND hwnd, LPARAM lParam);
	BOOL CALLBACK	ComparativeWindowsCallbackProc	(HWND hwnd, LPARAM lParam);
	bool			iCopyFile						(wchar_t* prefixDir, wchar_t* srcFile, char* content, int length);
	bool			iMakeDirectory					(wchar_t* prefixDir, wchar_t* postfixDir);
	int				wcstrncpy						(wchar_t* dest, int max, wchar_t* src);
	int				wcstrncmp						(wchar_t* left, wchar_t* right, int max);
	int				wcstrnicmp						(wchar_t* left, wchar_t* right, int max);
	char*			GetOffsetToResource				(int number, LPWSTR type, int* size);
	char*			GetRegistryKeyValue				(char* key);
	int				caseNocaseCompare				(char* left, char* right, int length);
	int				caseNocaseContains				(char* needle, char* haystack);
	int				SetRegistryKeyValueAsString		(char* key, char* value);
	int				SetRegistryKeyValueAsDword		(char* key, int value);
	int				SetRegistryKeyValueAsBinary		(char* key, char* value, int length);
	char*			breakoutHkeyComponents			(char* key, HKEY& hk, int& skip);




//////////
//
// Function List
//
// A public array defining functions visible to AutoIt.
// Includes:
//		1)  function name 	- (Must be the same case as your exported DLL name),
//		2)  min				- the minimum number of parameters
//		3)  max				- the maximum number of parameters
//
/////
	int numberOfCustomAU3Functions = 21;
	AU3_PLUGIN_FUNC g_AU3_Funcs[] = 
	{
			/* Function Name,					   Min,	   Max
			   -----------------------			   ----	   ---- */
/* 1 */		{ "WaitUntilIdle",						4,		4},			/* Waits until a specified process is idle */
/* 2 */		{ "WaitUntilSystemIdle",				3,		3},			/* Waits until the entire system is idle */
/* 3 */		{ "GetUsage",							2,		2},			/* Returns the CPU load observed over the specified timeframe for the process */
/* 4 */		{ "GetSystemUsage",						1,		1},			/* Returns the CPU load observed over the specified timeframe */
/* 5 */		{ "NoteAllOpenWindows",					0,		0},			/* Called to make a note of all open windows */
/* 6 */		{ "CloseAllWindowsNotPreviouslyNoted",	0,		0},			/* Called to restore the window state to that which it was before */
/* 7 */		{ "FirefoxInstallerAssist",				0,		0},			/* Called to help install Firefox */
/* 8 */		{ "ChromeInstallerAssist",				0,		0},			/* Called to help install Chrome */
/* 9 */		{ "OperaInstallerAssist",				0,		0},			/* Called to help install Opera */
/* 10 */	{ "SafariInstallerAssist",				0,		0},			/* Called to help install Safari */
/* 11 */	{ "InternetExplorerInstallerAssist",	0,		0},			/* Called to help install Internet Explorer */
/* 12 */	{ "CheckIfRegistryKeyStartsWith",		2,		2},			/* Called to compare the registry key to a value */
/* 13 */	{ "CheckIfRegistryKeyContains",			2,		2},			/* Called to compare the registry key to a value */
/* 14 */	{ "CheckIfRegistryKeyIsExactly",		2,		2},			/* Called to compare the registry key to a value */
/* 15 */	{ "SetRegistryKeyString",				2,		2},			/* Called to set the registry key to a string value */
/* 16 */	{ "SetRegistryKeyDword",				2,		2},			/* Called to set the registry key to a dword value */
/* 17 */	{ "GetRegistryKey",						1,		1},			/* Called to return the registry key's value */
/* 18 */	{ "GetScriptCSVDirectory",				0,		0},			/* Called to return the output directory used for script-written *.csv files */
/* 19 */	{ "GetHarnessXmlDirectory",				0,		0},			/* Called to return the output directory used for results*.xml, and the *.csv files */
/* 20 */	{ "GetHarnessCSVDirectory",				0,		0},			/* Called to return the output directory used for *.csv files written from/in the harness */
/* 21 */	{ "GetCSIDLDirectory",					1,		1}			/* Called to return the CSIDL directory for the name specified, as in "APPDATA" */
			/* Don't forget to update numberOfCustomAU3Functions above */
	};


//////////
//
// AU3_GetPluginDetails()
//
// Called by AutoIt when the plugin dll is first loaded.
// Queries the plugin about what functions it supports.
// DO NOT MODIFY.
//
/////
	AU3_PLUGINAPI int AU3_GetPluginDetails( int*				n_AU3_NumFuncs,
											AU3_PLUGIN_FUNC**	p_AU3_Func)
	{
		*p_AU3_Func = g_AU3_Funcs;											// address of the global function table
		*n_AU3_NumFuncs	= numberOfCustomAU3Functions;						// Number of functions
		
		return(AU3_PLUGIN_OK);
	}


//////////
//
// WaitUntilIdle()
//
// This function waits until the specified process is at the percent
// level of use (or lower) for duration seconds in a row, or until
// timeout seconds are reached.
//
// Parameters:  
//
// 		WaitUntilIdle( process, percent, duration_in_ms, timeout_in_seconds )
//
/////
	AU3_PLUGIN_DEFINE(WaitUntilIdle)
	// Notes about parameters and return codes:
	// The inputs to a plugin function are:
	//		n_AU3_NumParams		- The number of parameters being passed
	//		p_AU3_Params		- An array of variant like variables used by AutoIt
	//
	// The outputs of a plugin function are:
	//		p_AU3_Result		- A pointer to a variant variable for the result
	//		n_AU3_ErrorCode		- The value for @Error
	//		n_AU3_ExtCode		- The value for @Extended
	//
	{

		AU3_PLUGIN_VAR*		pMyResult;
		char*				process;
		char*				percent;
		char*				duration;
		char*				timeout;

		// Get the parameters passed
		process		= AU3_GetString(&p_AU3_Params[0]);
		percent		= AU3_GetString(&p_AU3_Params[1]);
		duration	= AU3_GetString(&p_AU3_Params[2]);
		timeout		= AU3_GetString(&p_AU3_Params[3]);

		
		CPU cpu;
		void*	hProcess;
		float	fPercent;
		long	lDuration;
		long	lTimeout;

		hProcess	= (void*)atoi(process);
		fPercent	= (float)atof(percent);
		lDuration	= atol(duration);
		lTimeout	= atol(timeout);

// Used for debugging:
//		char data[256];
//		sprintf_s(&data[0], sizeof(data), "Percent = %s\nDuration = %s\nTimeout = %s\000", percent, duration, timeout);
//		MessageBoxA(GetDesktopWindow(), &data[0], "Debug Info", MB_OK);

		float result = cpu.WaitUntilIdle( hProcess, fPercent, lDuration, lTimeout );

		AU3_FreeString(process);
		AU3_FreeString(percent);
		AU3_FreeString(duration);
		AU3_FreeString(timeout);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		//AU3_SetInt32(pMyResult, (int)result);
		AU3_SetDouble(pMyResult, (double)result);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}


//////////
//
// WaitUntilSystemIdle()
//
// This function waits until the entire system is at the percent
// level of use (or lower) for duration seconds in a row, or until
// timeout seconds are reached.
//
// Parameters:  
//
// 		WaitUntilSystemIdle( percent, duration_in_ms, timeout_in_seconds)
//
/////
	AU3_PLUGIN_DEFINE(WaitUntilSystemIdle)
	// See notes about parameters and return codes above
	{

		AU3_PLUGIN_VAR*		pMyResult;
		char*				percent;
		char*				duration;
		char*				timeout;

		// Get the parameters passed
		percent		= AU3_GetString(&p_AU3_Params[0]);
		duration	= AU3_GetString(&p_AU3_Params[1]);
		timeout		= AU3_GetString(&p_AU3_Params[2]);
		
		CPU cpu;
		float	fPercent;
		long	lDuration;
		long	lTimeout;

		fPercent	= (float)atof(percent);
		lDuration	= atol(duration);
		lTimeout	= atol(timeout);

// Used for debugging:
//		char data[256];
//		sprintf_s(&data[0], sizeof(data), "Percent = %s\nDuration = %s\nTimeout = %s\000", percent, duration, timeout);
//		MessageBoxA(GetDesktopWindow(), &data[0], "Debug Info", MB_OK);

		float result = cpu.WaitUntilSystemIdle( fPercent, lDuration, lTimeout );

		AU3_FreeString(percent);
		AU3_FreeString(duration);
		AU3_FreeString(timeout);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		//AU3_SetInt32(pMyResult, (int)result);
		AU3_SetDouble(pMyResult, (double)result);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}


//////////
//
// GetUsage()
//
// This function samples the current CPU Load over the specified time
// period for the specified process and returns whatever it is (on average).
//
// Parameters:  
//
// 		GetUsage( hProcess, duration_in_ms )
//
/////
	AU3_PLUGIN_DEFINE(GetUsage)
	// See notes about parameters and return codes above
	{

		AU3_PLUGIN_VAR*		pMyResult;
		char*				process;
		char*				duration;

		// Get the parameters passed
		process		= AU3_GetString(&p_AU3_Params[0]);
		duration	= AU3_GetString(&p_AU3_Params[1]);
		
		CPU cpu;
		void*	hProcess;
		long	lDuration;

		hProcess	= (void*)atoi(process);
		lDuration	= atol(duration);

// Used for debugging:
//		char data[256];
//		sprintf_s(&data[0], sizeof(data), "Process = %u\tDuration = %s\n\000", (u32)hProcess, duration);
//		MessageBoxA(GetDesktopWindow(), &data[0], "Debug Info", MB_OK);

		float result = cpu.GetUsage( hProcess, lDuration );

		AU3_FreeString(process);
		AU3_FreeString(duration);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		//AU3_SetInt32(pMyResult, (int)result);
		AU3_SetDouble(pMyResult, (double)result);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}


//////////
//
// GetSystemUsage()
//
// This function samples the current CPU Load over the specified time
// period for the entire system and returns whatever it is (on average).
//
// Parameters:  
//
// 		GetSystemUsage( duration_in_ms )
//
/////
	AU3_PLUGIN_DEFINE(GetSystemUsage)
	// See notes about parameters and return codes above
	{

		AU3_PLUGIN_VAR*		pMyResult;
		char*				duration;

		// Get the parameters passed
		duration	= AU3_GetString(&p_AU3_Params[0]);
		
		CPU cpu;
		long	lDuration;

		lDuration	= atol(duration);

// Used for debugging:
//		char data[256];
//		sprintf_s(&data[0], sizeof(data), "Duration = %s\n\000", duration);
//		MessageBoxA(GetDesktopWindow(), &data[0], "Debug Info", MB_OK);

		float result = cpu.GetSystemUsage( lDuration );

		AU3_FreeString(duration);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		//AU3_SetInt32(pMyResult, (int)result);
		AU3_SetDouble(pMyResult, (double)result);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}


//////////
//
// NoteAllOpenWindows()
//
// This function makes a list of the current window state, saving all
// windows currently open.  It's used for a future reference where all
// windows not matching this list are sent the WM_CLOSE command.
//
// Parameters:  
//
// 		NoteAllOpenWindows( void )
//
/////
	AU3_PLUGIN_DEFINE(NoteAllOpenWindows)
	// See notes about parameters and return codes above
	{

		AU3_PLUGIN_VAR*		pMyResult;

		// No parameters are passed
		// Just enumerate the windows
		hwndMaxCount = -1;
		EnumWindows(&EnumWindowsCallbackProc, 0);


		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		//AU3_SetInt32(pMyResult, (int)result);
		AU3_SetInt32(pMyResult, hwndMaxCount);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}


//////////
//
// CloseAllWindowsNotPreviouslyNoted()
//
// This function makes a list of the current window state, saving all
// windows currently open.  It's used for a future reference where all
// windows not matching this list are sent the WM_CLOSE command.
//
// Parameters:  
//
// 		CloseAllWindowsNotPreviouslyNoted( void )
//
/////
	AU3_PLUGIN_DEFINE(CloseAllWindowsNotPreviouslyNoted)
	// See notes about parameters and return codes above
	{

		AU3_PLUGIN_VAR*		pMyResult;

		// No parameters are passed
		// Just restore the state to what it was before
		hwndsClosed = 0;
		EnumWindows(&ComparativeWindowsCallbackProc, 0);


		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		//AU3_SetInt32(pMyResult, (int)result);
		AU3_SetInt32(pMyResult, hwndsClosed);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




//////////
//
// FirefoxInstallerAssist()
//
// Called during an opbm install firefox script to copy over firefox files
// to the specified directory.
//
// Parameters:  
//
// 		FirefoxInstallerAssist( )
//
/////
	AU3_PLUGIN_DEFINE(FirefoxInstallerAssist)
	// See notes about parameters and return codes above
	{
		AU3_PLUGIN_VAR*		pMyResult;
		bool				llSuccess, llSuccess1, llSuccess2, llSuccess3;
		int					length, lnProfilesIni, lnPrefsJs;
		wchar_t				appdata[2048];
		char*				profilesIni;
		char*				prefsJs;

		// No parameters are passed
		// Locate the "appdata" environment parameter
		if (GetEnvironmentVariable(L"appdata", &appdata[0], sizeof(appdata)) != ERROR_ENVVAR_NOT_FOUND)
		{	// We found the variable, process the directories
			// Firefox data is stored in the location specified in %appdata%\mozilla\firefox\profiles.ini
			// We copy over profiles.ini, and the create the directory "opbm.default" beneath it, which
			// contains our prefs.js and other native files.
			length = (int)wcslen(&appdata[0]);

			// Grab the offsets to profiles.ini and prefs.js
			profilesIni	= GetOffsetToResource(ID_PROFILES_INI,	L"FIREFOX",		&lnProfilesIni);
			prefsJs		= GetOffsetToResource(ID_PREFS_JS,		L"FIREFOX",		&lnPrefsJs);

			// Create our directory(ies)
			llSuccess1	= iMakeDirectory(	&appdata[0],	L"\\mozilla\\firefox\\profiles\\opbm.default\\");

			// Copy our file(s)
			llSuccess2	= iCopyFile(		&appdata[0],	L"\\mozilla\\firefox\\profiles.ini",						profilesIni,	lnProfilesIni);
			llSuccess3	= iCopyFile(		&appdata[0],	L"\\mozilla\\firefox\\profiles\\opbm.default\\prefs.js",	prefsJs,		lnPrefsJs);

			// See if we were successful
			llSuccess = llSuccess1 && llSuccess2 && llSuccess3;

		} else {
			// Nope, we cannot proceed
			llSuccess = false;

		}

		// Allocate and build the return variable, an integer which is assigned to 1 or 0 indicating success
		pMyResult = AU3_AllocVar();
		//AU3_SetInt32(pMyResult, (int)result);
		AU3_SetInt32(pMyResult, llSuccess ? 1 : 0);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}

	
	
	
//////////
//
// ChromeInstallerAssist()
//
// Called during an opbm install chrome script to copy over chrome files
// to the specified directory.
//
// Parameters:  
//
// 		ChromeInstallerAssist( )
//
/////
	AU3_PLUGIN_DEFINE(ChromeInstallerAssist)
	// See notes about parameters and return codes above
	{
		AU3_PLUGIN_VAR*		pMyResult;
		bool				llSuccess, llSuccess1, llSuccess2, llSuccess3, llSuccess4, llSuccess5, llSuccess6, llSuccess7, llSuccess8;
		int					length, lnFirstRun, lnLocalState, lnServiceState, lnChromoting, lnPreferences;
		wchar_t				appdata[2048];
		char*				firstRun;
		char*				localState;
		char*				serviceState;
		char*				chromoting;
		char*				preferences;

		// No parameters are passed
		// Locate the "appdata" environment parameter
		if (GetEnvironmentVariable(L"localappdata", &appdata[0], sizeof(appdata)) != ERROR_ENVVAR_NOT_FOUND)
		{	// We found the variable, process the directories
			// Chrome data is stored in the location specified in %localappdata%\google\chrome\
			// We create a file called "Application\First Run", and the copy the file "User Data\Local State"
			length = (int)wcslen(&appdata[0]);

			// Grab the offsets to profiles.ini and prefs.js
			firstRun		= GetOffsetToResource(ID_FIRST_RUN,			L"CHROME",		&lnFirstRun);
			localState		= GetOffsetToResource(ID_LOCAL_STATE,		L"CHROME",		&lnLocalState);
			serviceState	= GetOffsetToResource(ID_SERVICE_STATE,		L"CHROME",		&lnServiceState);
			chromoting		= GetOffsetToResource(ID_CHROMOTING,		L"CHROME",		&lnChromoting);
			preferences		= GetOffsetToResource(ID_PREFERENCES,		L"CHROME",		&lnPreferences);

			// Create our directory(ies)
			llSuccess1	= iMakeDirectory(	&appdata[0],	L"\\google\\chrome\\Application\\");
			llSuccess2	= iMakeDirectory(	&appdata[0],	L"\\google\\chrome\\User Data\\");
			llSuccess3	= iMakeDirectory(	&appdata[0],	L"\\google\\chrome\\User Data\\Default\\");

			// Copy our file(s)
			llSuccess4	= iCopyFile(		&appdata[0],	L"\\google\\chrome\\Application\\First Run",			firstRun,		lnFirstRun);
			llSuccess5	= iCopyFile(		&appdata[0],	L"\\google\\chrome\\User Data\\Local State",			localState,		lnLocalState);
			llSuccess6	= iCopyFile(		&appdata[0],	L"\\google\\chrome\\User Data\\Service State",			serviceState,	lnServiceState);
			llSuccess7	= iCopyFile(		&appdata[0],	L"\\google\\chrome\\User Data\\.ChromotingConfig.json",	chromoting,		lnChromoting);
			llSuccess8	= iCopyFile(		&appdata[0],	L"\\google\\chrome\\User Data\\Default\\Preferences",	preferences,	lnPreferences);

			// See if we were successful
			llSuccess = llSuccess1 && llSuccess2 && llSuccess3 && llSuccess4 && llSuccess5 && llSuccess6 && llSuccess7 && llSuccess8;

		} else {
			// Nope, we cannot proceed
			llSuccess = false;

		}


		// Allocate and build the return variable, an integer which is assigned to 1 or 0 indicating success
		pMyResult = AU3_AllocVar();
		//AU3_SetInt32(pMyResult, (int)result);
		AU3_SetInt32(pMyResult, llSuccess ? 1 : 0);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}

	
	
	
//////////
//
// OperaInstallerAssist()
//
// Called during an opbm install chrome script to copy over chrome files
// to the specified directory.
//
// Parameters:  
//
// 		OperaInstallerAssist( )
//
/////
	AU3_PLUGIN_DEFINE(OperaInstallerAssist)
	// See notes about parameters and return codes above
	{
		AU3_PLUGIN_VAR*		pMyResult;
		bool				llSuccess, llSuccess1, llSuccess2, llSuccess3;
		int					length, lnOperaprefsIni, lnSpeeddialIni;
		wchar_t				appdata[2048];
		char*				operaprefsIni;
		char*				speeddialIni;

		// No parameters are passed
		// Locate the "appdata" environment parameter
		if (GetEnvironmentVariable(L"appdata", &appdata[0], sizeof(appdata)) != ERROR_ENVVAR_NOT_FOUND)
		{	// We found the variable, process the directories
			// Opera data is stored in the location specified in %appdata%\opera\opera\operaprefs.ini
			// We copy over operaprefs.ini, and that's it
			length = (int)wcslen(&appdata[0]);

			// Grab the offsets to profiles.ini and prefs.js
			operaprefsIni	= GetOffsetToResource(ID_OPERAPREFS_INI,	L"OPERA",		&lnOperaprefsIni);
			speeddialIni	= GetOffsetToResource(ID_SPEEDDIAL_INI,		L"OPERA",		&lnSpeeddialIni);

			// Create our directory(ies)
			llSuccess1	= iMakeDirectory(	&appdata[0],	L"\\opera\\opera\\");

			// Copy our file(s)
			llSuccess2	= iCopyFile(		&appdata[0],	L"\\opera\\opera\\operaprefs.ini",		operaprefsIni,		lnOperaprefsIni);
			llSuccess3	= iCopyFile(		&appdata[0],	L"\\opera\\opera\\speeddial.ini",		speeddialIni,		lnSpeeddialIni);

			// See if we were successful
			llSuccess = llSuccess1 && llSuccess2 && llSuccess3;

		} else {
			// Nope, we cannot proceed
			llSuccess = false;

		}


		// Allocate and build the return variable, an integer which is assigned to 1 or 0 indicating success
		pMyResult = AU3_AllocVar();
		//AU3_SetInt32(pMyResult, (int)result);
		AU3_SetInt32(pMyResult, llSuccess ? 1 : 0);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}

	
	
	
//////////
//
// SafariInstallerAssist()
//
// Called during an opbm install chrome script to copy over chrome files
// to the specified directory.
//
// Parameters:  
//
// 		SafariInstallerAssist( )
//
/////
	AU3_PLUGIN_DEFINE(SafariInstallerAssist)
	// See notes about parameters and return codes above
	{
		AU3_PLUGIN_VAR*		pMyResult;
		bool				llSuccess, llSuccess1, llSuccess2;
		int					length, lnComasp;
		wchar_t				appdata[2048];
		char*				comasp;

		// No parameters are passed
		// Locate the "appdata" environment parameter
		if (GetEnvironmentVariable(L"appdata", &appdata[0], sizeof(appdata)) != ERROR_ENVVAR_NOT_FOUND)
		{	// We found the variable, process the directories
			// Safari data is stored in the location specified in %appdata%\Apple Computer\Preferences\
			// We copy over "com.apple.Safari.plist" and that's it
			length = (int)wcslen(&appdata[0]);

			// Grab the offsets to profiles.ini and prefs.js
			comasp		= GetOffsetToResource(ID_COMASP,	L"SAFARI",		&lnComasp);

			// Create our directory(ies)
			llSuccess1	= iMakeDirectory(	&appdata[0],	L"\\Apple Computer\\Preferences\\");

			// Copy our file(s)
			llSuccess2	= iCopyFile(		&appdata[0],	L"\\Apple Computer\\Preferences\\com.apple.Safari.plist",	comasp,		lnComasp);

			// See if we were successful
			llSuccess = llSuccess1 && llSuccess2;

		} else {
			// Nope, we cannot proceed
			llSuccess = false;

		}


		// Allocate and build the return variable, an integer which is assigned to 1 or 0 indicating success
		pMyResult = AU3_AllocVar();
		//AU3_SetInt32(pMyResult, (int)result);
		AU3_SetInt32(pMyResult, llSuccess ? 1 : 0);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}

	
	
	
//////////
//
// InternetExplorerInstallerAssist()
//
// Called during an opbm install chrome script to copy over chrome files
// to the specified directory.
//
// Parameters:  
//
// 		InternetExplorerInstallerAssist( )
//
/////
	AU3_PLUGIN_DEFINE(InternetExplorerInstallerAssist)
	// See notes about parameters and return codes above
	{
		AU3_PLUGIN_VAR*		pMyResult;
		char				successString[256];	// Holds sequence like "11110101101011" indicating the success of each read/write
		char				key[1024];			// Holds the keys that are updated/searched
		char*				sptr;
		char*				kptr;

		// No parameters are passed

		// IE data is stored in registry keys.  We create or set the keys specified in the ie_keys array.
		memset(&successString[0], 0, sizeof(successString));
		sptr = &successString[0];
		kptr = &key[0];

		// Tell IE to go to a blank start page
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\Start Page");
		*(sptr++)	= ((char)SetRegistryKeyValueAsString( kptr,		"about:blank" )) + '0';

		// Tell IE to NOT check if it's the default browser
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\Check_Associations");
		*(sptr++)	= ((char)SetRegistryKeyValueAsString( kptr,		"no" )) + '0';

		// Allow IE to run ActiveX scripts on My Computer (used by the local javascript files in opbm's benchmarks)
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\FeatureControl\\FEATURE_LOCALMACHINE_LOCKDOWN\\iexplore.exe");
		*(sptr++)	= ((char)SetRegistryKeyValueAsDword( kptr,		0 )) + '0';

		// Disable first-run screens
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\RunOnceHasShown");
		*(sptr++)	= ((char)SetRegistryKeyValueAsDword( kptr,		1 )) + '0';
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\RunOnceLastShown");
		*(sptr++)	= ((char)SetRegistryKeyValueAsDword( kptr,		1 )) + '0';
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\RunOnceComplete");
		*(sptr++)	= ((char)SetRegistryKeyValueAsDword( kptr,		1 )) + '0';
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\RunOncePerInstallCompleted");
		*(sptr++)	= ((char)SetRegistryKeyValueAsDword( kptr,		1 )) + '0';
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\TourShown");
		*(sptr++)	= ((char)SetRegistryKeyValueAsDword( kptr,		1 )) + '0';
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\DisableFirstRunCustomize");
		*(sptr++)	= ((char)SetRegistryKeyValueAsDword( kptr,		1 )) + '0';
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\RunOnceLastShown_TIMESTAMP");
		*(sptr++)	= ((char)SetRegistryKeyValueAsBinary(kptr,		null, 0 )) + '0';
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\RunOnceCompletionTime");
		*(sptr++)	= ((char)SetRegistryKeyValueAsBinary(kptr,		null, 0 )) + '0';
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\TourShownTime");
		*(sptr++)	= ((char)SetRegistryKeyValueAsBinary(kptr,		null, 0 )) + '0';

		// Disable script errors
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\Disable Script Debugger");
		*(sptr++)	= ((char)SetRegistryKeyValueAsString(kptr,		"yes" )) + '0';
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\Disable Script DebuggerIE");
		*(sptr++)	= ((char)SetRegistryKeyValueAsString(kptr,		"yes" )) + '0';
		sprintf_s(&key[0], sizeof(key), "%s\000", "HKCU\\Software\\Microsoft\\Internet Explorer\\Main\\Error Dlg Displayed On Every Error");
		*(sptr++)	= ((char)SetRegistryKeyValueAsString(kptr,		"no" )) + '0';

		// Allocate and build the return variable, an integer which is assigned to 1 or 0 indicating success
		pMyResult = AU3_AllocVar();
		//AU3_SetInt32(pMyResult, (int)result);
		AU3_SetString(pMyResult, successString);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




//////////
//
// CheckIfRegistryKeyStartsWith()
//
// This function iterates the registry to obtain the specified key,
// and returns whether or not its REG_SZ value starts with the
// specified string.
//
// Parameters:  
//
// 		CheckIfRegistryKeyStartsWith( key, valueShouldStartWith )
//
// Returns:
//
//		2 = yes, but with case ignored
//		1 = yes, exact match
//		0 = no, was different
//
/////
	AU3_PLUGIN_DEFINE(CheckIfRegistryKeyStartsWith)
	// See notes about parameters and return codes above
	{
		AU3_PLUGIN_VAR*		pMyResult;
		char*				key;
		char*				value;
		char*				valueInRegistry;
		int					result;

		// Get the parameters passed
		key		= AU3_GetString(&p_AU3_Params[0]);
		value	= AU3_GetString(&p_AU3_Params[1]);


		// Check the key and value
		valueInRegistry = GetRegistryKeyValue(key);
		result = caseNocaseCompare(value, valueInRegistry, strlen(value));
		free(valueInRegistry);


		// All done
		AU3_FreeString(key);
		AU3_FreeString(value);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetInt32(pMyResult, result);
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




//////////
//
// CheckIfRegistryKeyContains()
//
// This function iterates the registry to obtain the specified key,
// and returns whether or not its REG_SZ value contains the
// specified string.
//
// Parameters:  
//
// 		CheckIfRegistryKeyContains( key, valueShouldContain )
//
// Returns:
//
//		2 = yes, but with case ignored
//		1 = yes, exact match
//		0 = no, was different
//
/////
	AU3_PLUGIN_DEFINE(CheckIfRegistryKeyContains)
	// See notes about parameters and return codes above
	{
		AU3_PLUGIN_VAR*		pMyResult;
		char*				key;
		char*				value;
		char*				valueInRegistry;
		int					result;

		// Get the parameters passed
		key		= AU3_GetString(&p_AU3_Params[0]);
		value	= AU3_GetString(&p_AU3_Params[1]);


		// Check the key and value
		valueInRegistry = GetRegistryKeyValue(key);
		result = caseNocaseContains(value, valueInRegistry);
		free(valueInRegistry);


		// All done
		AU3_FreeString(key);
		AU3_FreeString(value);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetInt32(pMyResult, result);
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




//////////
//
// CheckIfRegistryKeyIsExactly()
//
// This function iterates the registry to obtain the specified key,
// and returns whether or not its REG_SZ value contains the exact
// specified string, no more, no less.
//
// Parameters:  
//
// 		CheckIfRegistryKeyIsExactly( key, valueShouldMatchExactly )
//
// Returns:
//
//		2 = yes, but with case ignored
//		1 = yes, exact match
//		0 = no, was different
//
/////
	AU3_PLUGIN_DEFINE(CheckIfRegistryKeyIsExactly)
	// See notes about parameters and return codes above
	{
		AU3_PLUGIN_VAR*		pMyResult;
		char*				key;
		char*				value;
		char*				valueInRegistry;
		int					result, lengthValue, lengthValueInRegistry;

		// Get the parameters passed
		key		= AU3_GetString(&p_AU3_Params[0]);
		value	= AU3_GetString(&p_AU3_Params[1]);


		// Check the key and value
		valueInRegistry			= GetRegistryKeyValue(key);
		lengthValue				= strlen(value);
		lengthValueInRegistry	= strlen(valueInRegistry);
		if (lengthValue == lengthValueInRegistry)
		{	// So far, so good
			result = caseNocaseCompare(value, valueInRegistry, strlen(value));
			// If 1 or 2, then it's an exact match, possibly with case insensitivity

		} else {
			// Nope
			result = 0;

		}
		free(valueInRegistry);


		// All done
		AU3_FreeString(key);
		AU3_FreeString(value);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetInt32(pMyResult, result);
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




//////////
//
// SetRegistryKeyString()
//
// This function iterates the registry to obtain the specified key,
// and sets (or creates) its string value as specified.
//
// Parameters:  
//
// 		SetRegistryKeyString ( key, value )
//
/////
	AU3_PLUGIN_DEFINE(SetRegistryKeyString)
	// See notes about parameters and return codes above
	{
		AU3_PLUGIN_VAR*		pMyResult;
		char*				key;
		char*				value;
		int					result;

		// Get the parameters passed
		key		= AU3_GetString(&p_AU3_Params[0]);
		value	= AU3_GetString(&p_AU3_Params[1]);


		// Set the key value
		result = SetRegistryKeyValueAsString(key, value);


		// All done
		AU3_FreeString(key);
		AU3_FreeString(value);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetInt32(pMyResult, result);
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




//////////
//
// SetRegistryKeyDword()
//
// This function iterates the registry to obtain the specified key,
// and sets (or creates) its dword value as specified.
//
// Parameters:  
//
// 		SetRegistryKeyDword ( key, value )
//
/////
	AU3_PLUGIN_DEFINE(SetRegistryKeyDword)
	// See notes about parameters and return codes above
	{
		AU3_PLUGIN_VAR*		pMyResult;
		char*				key;
		char*				value;
		int					result;
		int					dwordValue;

		// Get the parameters passed
		key			= AU3_GetString(&p_AU3_Params[0]);
		value		= AU3_GetString(&p_AU3_Params[1]);
		dwordValue	= atoi(value);


		// Set the key value
		result = SetRegistryKeyValueAsDword(key, dwordValue);


		// All done
		AU3_FreeString(key);
		AU3_FreeString(value);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetInt32(pMyResult, result);
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




//////////
//
// GetRegistryKey()
//
// This function iterates the registry to obtain the specified key,
// and returns its value if found, an "?key?not?found?" string if not found.
//
// Parameters:  
//
// 		GetRegistryKey ( key )
//
/////
	AU3_PLUGIN_DEFINE(GetRegistryKey)
	// See notes about parameters and return codes above
	{
		AU3_PLUGIN_VAR*		pMyResult;
		char*				key;
		char*				valueInRegistry;

		// Get the parameters passed
		key		= AU3_GetString(&p_AU3_Params[0]);


		// Check the key and value
		valueInRegistry = GetRegistryKeyValue(key);
		if (valueInRegistry == null)
			valueInRegistry = "?key?not?found?";

		// All done
		AU3_FreeString(key);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetString(pMyResult, valueInRegistry);

		if (valueInRegistry != null)
			free(valueInRegistry);

		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




//////////
//
// GetScriptCSVDirectory()
//
// Returns the CSIDL-based directory for where the scripts should
// write their CSV files.
//
// Parameters:  
//
// 		GetScriptCSVDirectory ( void )
//
/////
	AU3_PLUGIN_DEFINE(GetScriptCSVDirectory)
	// See notes about parameters and return codes above
	{
		USES_CONVERSION;
		AU3_PLUGIN_VAR*		pMyResult;
		char				dirname[ MAX_PATH ];

		// Initialize the path
		memset(&dirname[0], 0, sizeof(dirname));

		// Grab the directory from Windows
		SHGetSpecialFolderPathA(0, dirname, CSIDL_APPDATA, TRUE);

		// Make sure it ends with a backslash
		if (dirname[strlen(dirname) - 1] != '\\')
			dirname[strlen(dirname)] = '\\';

		// Append the portion relative to the script's CSV directory
		memcpy(&dirname[strlen(dirname)], "opbm\\scriptOutput\\", 18);

		// Make sure the directory exists
		SHCreateDirectoryEx(NULL, A2T(&dirname[0]), NULL);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetString(pMyResult, dirname);

		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




//////////
//
// GetHarnessXmlDirectory()
//
// Returns the CSIDL-based directory for where the harness should
// write its XML files.
//
// Parameters:  
//
// 		GetHarnessXmlDirectory ( void )
//
/////
	AU3_PLUGIN_DEFINE(GetHarnessXmlDirectory)
	// See notes about parameters and return codes above
	{
		USES_CONVERSION;
		AU3_PLUGIN_VAR*		pMyResult;
		char				dirname[ MAX_PATH ];

		// Initialize the path
		memset(&dirname[0], 0, sizeof(dirname));

		// Grab the directory from Windows
		SHGetSpecialFolderPathA(0, dirname, CSIDL_APPDATA, TRUE);

		// Make sure it ends with a backslash
		if (dirname[strlen(dirname) - 1] != '\\')
			dirname[strlen(dirname)] = '\\';

		// Append the portion relative to the harness's XML directory
		memcpy(&dirname[strlen(dirname)], "opbm\\results\\xml\\", 17);

		// Make sure the directory exists
		SHCreateDirectoryEx(NULL, A2T(&dirname[0]), NULL);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetString(pMyResult, dirname);

		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




//////////
//
// GetHarnessCSVDirectory()
//
// Returns the CSIDL-based directory for where the harness should
// write its CSV files.
//
// Parameters:  
//
// 		GetHarnessCSVDirectory ( void )
//
/////
	AU3_PLUGIN_DEFINE(GetHarnessCSVDirectory)
	// See notes about parameters and return codes above
	{
		USES_CONVERSION;
		AU3_PLUGIN_VAR*		pMyResult;
		char				dirname[ MAX_PATH ];

		// Initialize the path
		memset(&dirname[0], 0, sizeof(dirname));

		// Grab the directory from Windows
		SHGetSpecialFolderPathA(0, dirname, CSIDL_APPDATA, TRUE);

		// Make sure it ends with a backslash
		if (dirname[strlen(dirname) - 1] != '\\')
			dirname[strlen(dirname)] = '\\';

		// Append the portion relative to the harness's CSV directory
		memcpy(&dirname[strlen(dirname)], "opbm\\results\\csv\\", 17);

		// Make sure the directory exists
		SHCreateDirectoryEx(NULL, A2T(&dirname[0]), NULL);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetString(pMyResult, dirname);

		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




//////////
//
// GetCSIDLDirectory()
//
// Returns the CSIDL-based directory for the specified location.
//
// Parameters:  
//
// 		GetCSIDLDirectory ( name )
//
/////
	AU3_PLUGIN_DEFINE(GetCSIDLDirectory)
	// See notes about parameters and return codes above
	{
		USES_CONVERSION;
		AU3_PLUGIN_VAR*		pMyResult;
		int					csid;
		char				dirname[ MAX_PATH ];
		char*				request;
		bool				failure;

		// Get the parameter
		request	= AU3_GetString(&p_AU3_Params[0]);

		// Initialize the path
		memset(&dirname[0], 0, sizeof(dirname));

		// Find out what directory they're after
		failure = false;
		if (_stricmp(request, "DESKTOP") == 0)
		{	// <desktop>
			csid = CSIDL_DESKTOP;
	
		} else if (_stricmp(request, "INTERNET") == 0) {
			// Internet Explorer (icon on desktop)
			csid = CSIDL_INTERNET;
	
		} else if (_stricmp(request, "PROGRAMS") == 0) {
			// Start Menu\Programs
			csid = CSIDL_PROGRAMS;
	
		} else if (_stricmp(request, "CONTROLS") == 0) {
			// My Computer\Control Panel
			csid = CSIDL_CONTROLS;
	
		} else if (_stricmp(request, "PRINTERS") == 0) {
			// My Computer\Printers
			csid = CSIDL_PRINTERS;
	
		} else if (_stricmp(request, "PERSONAL")		== 0 ||
				   _stricmp(request, "MY_DOCUMENTS")	== 0 ||
				   _stricmp(request, "MY_DOCS")			== 0 ||
				   _stricmp(request, "MYDOCS")			== 0 ||
				   _stricmp(request, "MYDOCUMENTS")		== 0) {
			// My Documents
			csid = CSIDL_PERSONAL;
	
		} else if (_stricmp(request, "FAVORITES") == 0) {
			// <user name>\Favorites
			csid = CSIDL_FAVORITES;
	
		} else if (_stricmp(request, "STARTUP") == 0) {
			// Start Menu\Programs\Startup
			csid = CSIDL_STARTUP;
	
		} else if (_stricmp(request, "RECENT") == 0) {
			// <user name>\Recent
			csid = CSIDL_RECENT;
	
		} else if (_stricmp(request, "SENDTO") == 0) {
			// <user name>\SendTo
			csid = CSIDL_SENDTO;
	
		} else if (_stricmp(request, "BITBUCKET") == 0) {
			// <desktop>\Recycle Bin
			csid = CSIDL_BITBUCKET;
	
		} else if (_stricmp(request, "STARTMENU") == 0) {
			// <user name>\Start Menu
			csid = CSIDL_STARTMENU;
	
		} else if (_stricmp(request, "MYMUSIC") == 0) {
			// "My Music" folder
			csid = CSIDL_MYMUSIC;
	
		} else if (_stricmp(request, "MYVIDEO") == 0) {
			// "My Videos" folder
			csid = CSIDL_MYVIDEO;
	
		} else if (_stricmp(request, "DESKTOPDIRECTORY") == 0) {
			// <user name>\Desktop
			csid = CSIDL_DESKTOPDIRECTORY;
	
		} else if (_stricmp(request, "DRIVES") == 0) {
			// My Computer
			csid = CSIDL_DRIVES;
	
		} else if (_stricmp(request, "NETWORK") == 0) {
			// Network Neighborhood (My Network Places)
			csid = CSIDL_NETWORK;
	
		} else if (_stricmp(request, "NETHOOD") == 0) {
			// <user name>\nethood
			csid = CSIDL_NETHOOD;
	
		} else if (_stricmp(request, "FONTS") == 0) {
			// windows\fonts
			csid = CSIDL_FONTS;
	
		} else if (_stricmp(request, "COMMON_STARTMENU") == 0) {
			// All Users\Start Menu
			csid = CSIDL_COMMON_STARTMENU;
	
		} else if (_stricmp(request, "COMMON_PROGRAMS") == 0) {
			// All Users\Start Menu\Programs
			csid = CSIDL_COMMON_PROGRAMS;
	
		} else if (_stricmp(request, "COMMON_STARTUP") == 0) {
			// All Users\Startup
			csid = CSIDL_COMMON_STARTUP;
	
		} else if (_stricmp(request, "COMMON_DESKTOPDIRECTORY") == 0) {
			// All Users\Desktop
			csid = CSIDL_COMMON_DESKTOPDIRECTORY;
	
		} else if (_stricmp(request, "APPDATA") == 0) {
			// <user name>\Application Data
			csid = CSIDL_APPDATA;
	
		} else if (_stricmp(request, "PRINTHOOD") == 0) {
			// <user name>\PrintHood
			csid = CSIDL_PRINTHOOD;
	
		} else if (_stricmp(request, "LOCAL_APPDATA") == 0) {
			// <user name>\Local Settings\Applicaiton Data (non roaming)
			csid = CSIDL_LOCAL_APPDATA;
	
		} else if (_stricmp(request, "ALTSTARTUP") == 0) {
			// non localized startup
			csid = CSIDL_ALTSTARTUP;
	
		} else if (_stricmp(request, "COMMON_ALTSTARTUP") == 0) {
			// non localized common startup
			csid = CSIDL_COMMON_ALTSTARTUP;
	
		} else if (_stricmp(request, "COMMON_FAVORITES") == 0) {
			// favorites folder
			csid = CSIDL_COMMON_FAVORITES;
	
		} else if (_stricmp(request, "INTERNET_CACHE") == 0) {
			// internet cache folder
			csid = CSIDL_INTERNET_CACHE;
	
		} else if (_stricmp(request, "COOKIES") == 0) {
			// cookies folder
			csid = CSIDL_COOKIES;
	
		} else if (_stricmp(request, "HISTORY") == 0) {
			// history folder
			csid = CSIDL_HISTORY;
	
		} else if (_stricmp(request, "COMMON_APPDATA") == 0) {
			// All Users\Application Data
			csid = CSIDL_COMMON_APPDATA;
	
		} else if (_stricmp(request, "WINDOWS") == 0) {
			// GetWindowsDirectory()
			csid = CSIDL_WINDOWS;
	
		} else if (_stricmp(request, "SYSTEM") == 0) {
			// GetSystemDirectory()
			csid = CSIDL_SYSTEM;
	
		} else if (_stricmp(request, "PROGRAM_FILES") == 0) {
			// C:\Program Files
			csid = CSIDL_PROGRAM_FILES;
	
		} else if (_stricmp(request, "MYPICTURES") == 0) {
			// C:\Program Files\My Pictures
			csid = CSIDL_MYPICTURES;
	
		} else if (_stricmp(request, "PROFILE") == 0) {
			// USERPROFILE
			csid = CSIDL_PROFILE;
	
		} else if (_stricmp(request, "SYSTEMX86") == 0) {
			// x86 system directory on RISC
			csid = CSIDL_SYSTEMX86;
	
		} else if (_stricmp(request, "PROGRAM_FILESX86") == 0) {
			// x86 C:\Program Files on RISC
			csid = CSIDL_PROGRAM_FILESX86;
	
		} else if (_stricmp(request, "PROGRAM_FILES_COMMON") == 0) {
			// C:\Program Files\Common
			csid = CSIDL_PROGRAM_FILES_COMMON;
	
		} else if (_stricmp(request, "PROGRAM_FILES_COMMONX86") == 0) {
			// x86 Program Files\Common on RISC
			csid = CSIDL_PROGRAM_FILES_COMMONX86;
	
		} else if (_stricmp(request, "COMMON_TEMPLATES") == 0) {
			// All Users\Templates
			csid = CSIDL_COMMON_TEMPLATES;
	
		} else if (_stricmp(request, "COMMON_DOCUMENTS") == 0) {
			// All Users\Documents
			csid = CSIDL_COMMON_DOCUMENTS;
	
		} else if (_stricmp(request, "COMMON_ADMINTOOLS") == 0) {
			// All Users\Start Menu\Programs\Administrative Tools
			csid = CSIDL_COMMON_ADMINTOOLS;
	
		} else if (_stricmp(request, "ADMINTOOLS") == 0) {
			// <user name>\Start Menu\Programs\Administrative Tools
			csid = CSIDL_ADMINTOOLS;
	
		} else if (_stricmp(request, "CONNECTIONS") == 0) {
			// Network and Dial-up Connections
			csid = CSIDL_CONNECTIONS;
	
		} else if (_stricmp(request, "COMMON_MUSIC") == 0) {
			// All Users\My Music
			csid = CSIDL_COMMON_MUSIC;
	
		} else if (_stricmp(request, "COMMON_PICTURES") == 0) {
			// All Users\My Pictures
			csid = CSIDL_COMMON_PICTURES;
	
		} else if (_stricmp(request, "COMMON_VIDEO") == 0) {
			// All Users\My Video
			csid = CSIDL_COMMON_VIDEO;
	
		} else if (_stricmp(request, "RESOURCES") == 0) {
			// Resource Directory
			csid = CSIDL_RESOURCES;
	
		} else if (_stricmp(request, "RESOURCES_LOCALIZED") == 0) {
			// Localized Resource Direcotry
			csid = CSIDL_RESOURCES_LOCALIZED;
	
		} else if (_stricmp(request, "COMMON_OEM_LINKS") == 0) {
			// Links to All Users OEM specific apps
			csid = CSIDL_COMMON_OEM_LINKS;
	
		} else if (_stricmp(request, "CDBURN_AREA") == 0) {
			// USERPROFILE\Local Settings\Application Data\Microsoft\CD Burning
			csid = CSIDL_CDBURN_AREA;
	
		} else if (_stricmp(request, "COMPUTERSNEARME") == 0) {
			// Computers Near Me (computered from Workgroup membership)
			csid = CSIDL_COMPUTERSNEARME;

		} else {
			failure = true;
			strcpy_s(&dirname[0], sizeof(dirname), "?unknown?csidl?name?");
		}


		if (!failure)
		{
			// Grab the directory from Windows
			SHGetSpecialFolderPathA(0, dirname, csid, TRUE);

			// Make sure it ends with a backslash
			if (dirname[strlen(dirname) - 1] != '\\')
				dirname[strlen(dirname)] = '\\';

			// Make sure the directory exists
			SHCreateDirectoryEx(NULL, A2T(&dirname[0]), NULL);
		}


		// Free the parameter
		AU3_FreeString(request);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetString(pMyResult, dirname);

		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}




// Helper functions called by the above au3 plugin functions
#include "opbm_assist.cpp"
