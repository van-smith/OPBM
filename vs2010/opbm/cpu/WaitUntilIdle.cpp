/*
 *
 * AutoIt Plugin SDK -	WaitUntilIdle()
 *						WaitUntilSystemIdle()
 *
 * Copyright(c)2011 Cossatot Analytics Laboratories, LLC.
 * Free Software. Released under GPLv2.
 * See:  Free Software Foundation at www.fsf.org.
 *
 * WaitUntilIdle.cpp
 *
 */

#include <stdio.h>
#include <windows.h>

#include "au3\au3plugin.h"
// Note: AU3_GetString() allocates some memory that must be manually freed later using AU3_FreeString()

#include "cpu.h"


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
	AU3_PLUGIN_FUNC g_AU3_Funcs[] = 
	{
		/* Function Name,			Min,	Max
		   -----------------------	----	---- */
		{ "WaitUntilIdle",			4,		4},			/* Waits until a specified process is idle */
		{ "WaitUntilSystemIdle",	3,		3}			/* Waits until the entire system is idle */
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
		*n_AU3_NumFuncs	= sizeof(g_AU3_Funcs)/sizeof(AU3_PLUGIN_FUNC);		// Number of functions
		
		return(AU3_PLUGIN_OK);
	}


//////////
//
// DllMain()
//
// This function is called when the DLL is loaded and unloaded.  Do not 
// modify it unless you understand what it does...
//
/////
	BOOL WINAPI DllMain(HANDLE	hInst,
						ULONG	ul_reason_for_call,
						LPVOID	lpReserved)
	{
//		switch (ul_reason_for_call)
//		{
//			case DLL_PROCESS_ATTACH:
//			case DLL_THREAD_ATTACH:
//			case DLL_THREAD_DETACH:
//			case DLL_PROCESS_DETACH:
//				break;
//		}
		return TRUE;
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
// 		WaitUntilIdle( process, percent, duration, timeout)
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
		bool result = cpu.WaitUntilIdle( (HANDLE)atoi(process),
										 atof(percent), 
										 atof(duration),
										 atof(timeout) );

		AU3_FreeString(process);
		AU3_FreeString(percent);
		AU3_FreeString(duration);
		AU3_FreeString(timeout);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetInt32(pMyResult, result ? 1 : 0);
		

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
// 		WaitUntilSystemIdle( percent, duration, timeout)
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
		percent		= AU3_GetString(&p_AU3_Params[1]);
		duration	= AU3_GetString(&p_AU3_Params[2]);
		timeout		= AU3_GetString(&p_AU3_Params[3]);
		

		CPU cpu;
		bool result = cpu.WaitUntilSystemIdle(	atof(percent), 
												atof(duration),
												atof(timeout) );

		AU3_FreeString(percent);
		AU3_FreeString(duration);
		AU3_FreeString(timeout);

		// Allocate and build the return variable
		pMyResult = AU3_AllocVar();
		AU3_SetInt32(pMyResult, result ? 1 : 0);
		

		/* Pass back the result, error code and extended code.
		 * Note: AutoIt is responsible for freeing the memory used in p_AU3_Result
		 */
		*p_AU3_Result		= pMyResult;
		*n_AU3_ErrorCode	= 0;
		*n_AU3_ExtCode		= 0;

		return( AU3_PLUGIN_OK );
	}
