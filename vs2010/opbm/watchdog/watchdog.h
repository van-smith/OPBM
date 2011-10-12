//////////
//
// watchdog.h
// Header file for OPBM's Watchdog Application
//
/////




#include <stdio.h>
#include <windows.h>
#include <winbase.h>
#include <tchar.h>
#define _WATCHDOG 1
#define _WATCHDOG_HARNESS 1
#define _WATCHDOG_SCRIPTS 1
#include "..\common\watchdog_common.h"
#include "..\common\enumthreads.h"



//////////
//
// Constants used in watchdog
//
/////
	// None yet



//////////
// Global variables
/////
	struct SProcessData
	{
		int					handle;									// Handle assigned to the caller by watchdog

		DWORD				id;										// Process ID
		int					timeout;								// User-specified timeout period in seconds
		int					countdown;								// When this countdown reaches 0, it's time to terminate the process

		SPipeDataLongName	name;									// Process name (such as "acrord32.exe"
		SPipeDataLongName	alias;									// Internally assigned alias (such as "Acrobat Reader" rather than "acrord32.exe")
	};

	struct SProcessLL
	{	// A linked list of scoring data for the process
		SProcessLL*			next;									// Pointer to the next process in the chain
		SProcessLL*			firstChild;								// Pointer to the first sub-process for this process
		SProcessData		process;								// The process data for this process
	};

	HWND				ghWnd						= NULL;			// Message window
	SProcessLL*			firstProcess				= NULL;			// Root process, if watchdog is not active, then this parameter is NULL
	HINSTANCE			ghInst						= NULL;			// Windows-assigned instance
	HANDLE				ghHarnessPipeHandle			= NULL;			// Handle to the harness named pipe
	HANDLE				ghScriptPipeHandle			= NULL;			// Handle to the script named pipe

	SHarnessPipeData	harnessPipeData;
	SScriptPipeData		scriptPipeData;


//////////
// Forward declarations
/////
	void				connectToExternalDlls						(LPSTR lpCmdLine);
	void				createNamedPipes							(void);
	void				createMessageWindow							(void);
	void				readEvents									(void);
	LRESULT CALLBACK	WndProc										(HWND hwnd, UINT m, WPARAM w, LPARAM l);
	void				loadAndProcessHarnessPipeData				(void);
	void				loadAndProcessScriptPipeData				(void);
	void				processHarnessPipeMessage					(void);
	void				processScriptPipeMessage					(void);
