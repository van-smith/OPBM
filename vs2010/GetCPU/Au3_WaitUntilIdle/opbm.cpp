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

#include "..\GetCPU\cpu.h"
#include "au3\au3plugin.h"
// Note: AU3_GetString() allocates some memory that must be manually freed later using AU3_FreeString()

//////////
//
// Used for NoteAllOpenWindows() and CloseAllWindowsNotPreviouslyNoted()
// Allocate enough space for 16K windows, which should be more than plenty.
// Note:  Windows doesn't just have windows 
//
/////
	#define _MAX_HWND_COUNT	16384
	HWND enumeratedWindows[_MAX_HWND_COUNT];
	int hwndMaxCount	= 0;
	int hwndsClosed		= 0;
	BOOL CALLBACK EnumWindowsCallbackProc(HWND hwnd, LPARAM lParam);
	BOOL CALLBACK ComparativeWindowsCallbackProc(HWND hwnd, LPARAM lParam);



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
	int numberOfCustomAU3Functions = 6;
	AU3_PLUGIN_FUNC g_AU3_Funcs[] = 
	{
			/* Function Name,					   Min,	   Max
			   -----------------------			   ----	   ---- */
/* 1 */		{ "WaitUntilIdle",						4,		4},			/* Waits until a specified process is idle */
/* 2 */		{ "WaitUntilSystemIdle",				3,		3},			/* Waits until the entire system is idle */
/* 3 */		{ "GetUsage",							2,		2},			/* Returns the CPU load observed over the specified timeframe for the process */
/* 4 */		{ "GetSystemUsage",						1,		1},			/* Returns the CPU load observed over the specified timeframe */
/* 5 */		{ "NoteAllOpenWindows",					0,		0},			/* Called to make a note of all open windows */
/* 6 */		{ "CloseAllWindowsNotPreviouslyNoted",	0,		0}			/* Called to restore the window state to that which it was before */
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


/*
 *	Callback function, physically appends this HWND to the list of HWNDs already captured
 *
 */
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


/*
 *	Callback function, physically appends this HWND to the list of HWNDs already captured
 *
 */
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
