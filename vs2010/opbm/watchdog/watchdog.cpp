//////////
//
// watchdog.cpp
// OPBM Watchdog -- Used to monitor errant apps, and terminate hung
//                  processes after a harness-/script-specified
//                  timeout intervals, including cleanup (file and/or
//                  directory deletion).
//
// -----
// Last Updated:  October 12, 2011
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
// This source file is the top-level file used by watchdog.
// This process has no visible windows, nothing external indicating
// it is running.  It exists as a background process which, periodically
// upon a timer interval, will check to see if the timeout interval
// has been achieved on each process it's been instructed to watch.
// If so, it will terminate that process, any child processes, and
// clean up any files or directories it's been instructed to, recording
// all information for relay back to the OPBM harness through the
// opbm32.dll/opbm64.dll JNI native functions.
//
// This system was explicitly written for OPBM and is designed to
// receive data from the harness itself, as well as AutoIt scripts
// using the opbm32.dll/opbm64.dll JNI native function interface
// within the harness, and opbm.dll plugin interface in AutoIt.
//
// Watchdog is automatically launched by the harness when it starts
// a benchmark run.  And it is automatically terminated by the harness
// when the run ends.
//
// The purpose of the watchdog is to provide an external process
// oversight as to the goings on of the script functionality.  Should
// a script fail and the system be placed in an unexpected state,
// watchdog is there to clean everything up, and report back to the
// harness exactly what happened.
//
/////

#include "watchdog.h"

//////////
//
// The only commmand line parameter is the path to access
// EnumThreads.dll, which for OPBM, should be ..\dll\ relative
// to the location of this exe, as in:
//		....autoIt\common\opbm\exe\watchdog.exe
//		....autoIt\common\opbm\dll\EnumThreads.dll
//
/////
	int APIENTRY WinMain(HINSTANCE hInstance,
						 HINSTANCE hPrevInstance,
						 LPSTR     lpCmdLine,
						 int       nCmdShow)
	{
		ghInst = hInstance;

		// Initialize
		connectToExternalDlls(lpCmdLine);
		createNamedPipes();
		createMessageWindow();

		// Run
		if (ghWnd != NULL && ghHarnessPipeHandle != INVALID_HANDLE_VALUE && ghScriptPipeHandle != INVALID_HANDLE_VALUE)
			readEvents();	// Read Windows event messages until we're finished
		else
			exit(-1);		// If we fail, the harness will identify the failure and notify the user (because watchdog is not found, or non-response to requests, or the return value of -1 is identified)
	}









//////////
//
// EnumThreads is used for enumerating threads for processes,
// to find the owners of HWNDs
//
/////
	void connectToExternalDlls(LPSTR lpCmdLine)
	{
		char	filename[_MAX_FNAME];

		if (lpCmdLine != NULL)
		{	// We have a valid command line
			// Make sure it ends in a backslash
			if (lpCmdLine[strlen(lpCmdLine) - 1] != '\\')
				lpCmdLine[strlen(lpCmdLine)] = '\\';

			// EnumThreads.dll
			sprintf_s(filename, sizeof(filename), "%sEnumThreads.dll\000", lpCmdLine);
			if (!LoadEnumThreadsDll(filename))
				exit(-2);		// Failure to load EnumThreads.dll

			// Other external DLLs can be loaded here

		} else {
			// A command line error
			exit(-1);
		}
		// If we get here, we're good
	}




//////////
//
// Creates the named pipes for data exchange
//
/////
	void createNamedPipes(void)
	{
		// Use this code to connect to the harness and the script pipes in opbm32.dll/opbm64.dll and opbm.dll, or other custom apps
		//ghHarnessPipeHandle	= CreateFile(_WATCHDOG_Pipe_Name_Harness,	GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
		//ghScriptPipeHandle	= CreateFile(_WATCHDOG_Pipe_Name_Script,	GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);

		// Create to the harness pipe
		ghHarnessPipeHandle	= CreateNamedPipe(  _WATCHDOG_Pipe_Name_Harness,
												PIPE_ACCESS_DUPLEX | FILE_FLAG_FIRST_PIPE_INSTANCE,
												PIPE_READMODE_BYTE | PIPE_NOWAIT,
												2,
												sizeof(SHarnessPipeData),
												sizeof(SHarnessPipeData),
												0,
												NULL);

		// Create to the script pipe
		ghScriptPipeHandle	= CreateNamedPipe(  _WATCHDOG_Pipe_Name_Script,
												PIPE_ACCESS_DUPLEX | FILE_FLAG_FIRST_PIPE_INSTANCE,
												PIPE_READMODE_BYTE | PIPE_NOWAIT,
												2,
												sizeof(SScriptPipeData),
												sizeof(SScriptPipeData),
												0,
												NULL);

		// If there is a failure creating the named pipes, the harness will identify it
		// when it tries to verify this process launched and installed correctly.
	}




//////////
//
// Creates a message window for the process to communicate with
//
/////
	void createMessageWindow(void)
	{
		WNDCLASSEX wce;

		// Register our class
		wce.cbSize			= sizeof(WNDCLASSEX);
		wce.style			= 0;
		wce.lpfnWndProc		= WndProc;
		wce.cbClsExtra		= 0;
		wce.cbWndExtra		= 0;
		wce.hIcon			= NULL;
		wce.hCursor			= NULL;
		wce.hbrBackground	= NULL;
		wce.lpszMenuName	= NULL;
		wce.lpszClassName	= _WATCHDOG_Class_Name;
		wce.hInstance		= ghInst;
		wce.hIconSm			= NULL;

		// Register the application class
		RegisterClassEx(&wce);

		// Create our window
		ghWnd	=	CreateWindowEx(0,
								   _WATCHDOG_Class_Name,
								   _WATCHDOG_Window_Name,
								   0, 0, 0, 0, 0,
								   HWND_MESSAGE, NULL, ghInst, NULL);

		// If there is a failure creating the window, the harness will identify it
		// when it tries to verify this process launched and installed correctly.
	}




//////////
//
// Read and process Windows' events until we're closed externally, or
// the gbAppIsRunning flag is lowered (by the user)
//
/////
	void readEvents(void)
	{
		MSG msg;

		while (GetMessage(&msg, NULL, 0, 0))
		{
			TranslateMessage (&msg);
			DispatchMessage (&msg);
		}
	}

	LRESULT CALLBACK WndProc(HWND hwnd, UINT m, WPARAM w, LPARAM l)
	{
		switch (m)
		{
			// If the user wants to close the application
			case WM_DESTROY:
			case _WATCHDOG_SHOULD_SELF_TERMINATE:
				// then close it
				PostQuitMessage(WM_QUIT);
				break;

			case _WATCHDOG_HARNESS_REPORTING_IN:
				// The harness is reporting that it has made a connection
				// We acknowledge the request with 0x12345678
				return(_WATCHDOG_SUCCESS_RESPONSE);
				break;

			case _WATCHDOG_SCRIPT_REPORTING_IN:
				// A script is reporting that it has made a connection
				return(_WATCHDOG_SUCCESS_RESPONSE);
				break;

			case _WATCHDOG_HARNESS_HAS_PIPE_DATA:
				loadAndProcessHarnessPipeData();
				break;

			case _WATCHDOG_SCRIPT_HAS_PIPE_DATA:
				loadAndProcessScriptPipeData();
				break;

			default:
				// Process the left-over messages
				return DefWindowProc(hwnd, m, w, l);
		}
		// If something was not done, let it go
		return 0;
	}

	void loadAndProcessHarnessPipeData(void)
	{
		SHarnessPipeData pipeData;
		DWORD numread;

		if (ghHarnessPipeHandle != INVALID_HANDLE_VALUE)
		{	// Load the pipe data
			ReadFile(ghHarnessPipeHandle, &pipeData, sizeof(pipeData), &numread, NULL);
			if (numread == sizeof(pipeData))
			{	// A valid message
				memcpy(&harnessPipeData, &pipeData, sizeof(pipeData));
				processHarnessPipeMessage();
			}
		}
	}

	void loadAndProcessScriptPipeData(void)
	{
		SHarnessPipeData pipeData;
		DWORD numread;

		if (ghScriptPipeHandle != INVALID_HANDLE_VALUE)
		{	// Load the pipe data
			ReadFile(ghScriptPipeHandle, &pipeData, sizeof(pipeData), &numread, NULL);
			if (numread == sizeof(pipeData))
			{	// A valid message
				memcpy(&harnessPipeData, &pipeData, sizeof(pipeData));
				processScriptPipeMessage();
			}
		}
	}

	void processHarnessPipeMessage(void)
	{
	}

	void processScriptPipeMessage(void)
	{
	}
