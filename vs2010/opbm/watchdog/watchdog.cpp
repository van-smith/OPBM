//////////
//
// watchdog.cpp
//
/////
//
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
// The only commmand line parameter is the path to access DLLs
// (EnumThreads.dll), which for OPBM, should be ..\dll\ relative
// to the location of this exe, as in:
//		....autoIt\common\opbm\exe\watchdog.exe
//		....autoIt\common\opbm\dll\EnumThreads.dll
//
/////
	int APIENTRY WinMain(HINSTANCE	hInstance,
						 HINSTANCE	hPrevInstance,
						 LPSTR		lpCmdLine,
						 int		nCmdShow)
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
		char filename[_MAX_FNAME];

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
		ghWnd	=	CreateWindowEx(	0,
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
			case WM_CREATE:
				ghTimer = SetTimer(hwnd, 1, /* 5 seconds */ 5000, NULL);
				break;

			case WM_TIMER:
				// We've had another 5 seconds go by
				// Check the processes, see if any are due for termination
// REMEMBER
//				if (isValidProcess(pid))
//				{	// It is valid
//				} else {
//					// Invalid
//				}

				break;

			// If the user wants to close the application
			case WM_DESTROY:
			case _WATCHDOG_SHOULD_SELF_TERMINATE:
				// then close it
				PostQuitMessage(WM_QUIT);
				break;

			case _WATCHDOG_HARNESS_REPORTING_IN:
				// The harness is reporting that it has made a connection
				// We acknowledge the request with our secret number _WATCHDOG_SUCCESS_RESPONSE
				return(_WATCHDOG_SUCCESS_RESPONSE);
				break;

			case _WATCHDOG_SCRIPT_REPORTING_IN:
				// A script is reporting that it has made a connection
				// We acknowledge the request with our secret number _WATCHDOG_SUCCESS_RESPONSE
				return(_WATCHDOG_SUCCESS_RESPONSE);
				break;

			case _WATCHDOG_HARNESS_HAS_PIPE_DATA:
				// The harness is sending a packet
				loadAndProcessHarnessPipeData();
				break;

			case _WATCHDOG_SCRIPT_HAS_PIPE_DATA:
				// The script is sending a packet
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
				processHarnessPipeMessage(&harnessPipeData);
			}
		}
	}

	void loadAndProcessScriptPipeData(void)
	{
		SScriptPipeData pipeData;
		DWORD numread;

		if (ghScriptPipeHandle != INVALID_HANDLE_VALUE)
		{	// Load the pipe data
			ReadFile(ghScriptPipeHandle, &pipeData, sizeof(pipeData), &numread, NULL);
			if (numread == sizeof(pipeData))
			{	// A valid message
				memcpy(&scriptPipeData, &pipeData, sizeof(pipeData));
				processScriptPipeMessage(&scriptPipeData);
			}
		}
	}

	// Write the response back for the harness
	bool sendHarnessPipeMessage(SHarnessPipeData* pipeData)
	{
		DWORD numwritten;
		WriteFile(ghHarnessPipeHandle, pipeData, sizeof(SHarnessPipeData), &numwritten, NULL);
		return(numwritten == sizeof(SHarnessPipeData));
	}

	// Write the response back for the script
	bool sendScriptPipeMessage(SScriptPipeData* pipeData)
	{
		DWORD numwritten;
		WriteFile(ghScriptPipeHandle, pipeData, sizeof(SScriptPipeData), &numwritten, NULL);
		return(numwritten == sizeof(SScriptPipeData));
	}

	// Process common and harness-specific messages
	void processHarnessPipeMessage(SHarnessPipeData* hpd)
	{
		switch (hpd->packet.type)
		{
			case _WATCHDOG_TYPE_ADD_PROCESS:
			case _WATCHDOG_TYPE_DELETE_PROCESS:
			case _WATCHDOG_TYPE_ADD_SUBPROCESS:
			case _WATCHDOG_TYPE_DELETE_SUBPROCESS:
			case _WATCHDOG_TYPE_FILE_TO_DELETE_POST_MORTUM:
			case _WATCHDOG_TYPE_DIRECTORY_TO_CLEAN_POST_MORTUM:
			case _WATCHDOG_TYPE_DIRECTORY_TO_DELETE_POST_MORTUM:
			case _WATCHDOG_TYPE_PROCESS_NAME_TO_KILL_POST_MORTUM:
			case _WATCHDOG_TYPE_PROCESS_ID_TO_KILL_POST_MORTUM:
				processCommonPipeMessages(&hpd->packet, &hpd->response);
				// When control returns here, the packet has been process, and the response created
				// Now send it back down the proper pipe
				sendHarnessPipeMessage(hpd);
				break;

			default:
				// Unrecognized response
				break;
		}
	}

	// Process common and script-specific messages
	void processScriptPipeMessage(SScriptPipeData* spd)
	{
		switch (spd->packet.type)
		{
			case _WATCHDOG_TYPE_ADD_PROCESS:
			case _WATCHDOG_TYPE_DELETE_PROCESS:
			case _WATCHDOG_TYPE_ADD_SUBPROCESS:
			case _WATCHDOG_TYPE_DELETE_SUBPROCESS:
			case _WATCHDOG_TYPE_FILE_TO_DELETE_POST_MORTUM:
			case _WATCHDOG_TYPE_DIRECTORY_TO_CLEAN_POST_MORTUM:
			case _WATCHDOG_TYPE_DIRECTORY_TO_DELETE_POST_MORTUM:
			case _WATCHDOG_TYPE_PROCESS_NAME_TO_KILL_POST_MORTUM:
			case _WATCHDOG_TYPE_PROCESS_ID_TO_KILL_POST_MORTUM:
				processCommonPipeMessages(&spd->packet, &spd->response);
				// When control returns here, the packet has been process, and the response created
				// Now send it back down the proper pipe
				sendScriptPipeMessage(spd);
				break;

			default:
				// Unrecognized response
				break;
		}
	}

	// Process the common pipe message as these portions are identical for the harness and scripts
	void processCommonPipeMessages(SPacket* packet, SResponse* response)
	{
		switch (packet->type)
		{
			case _WATCHDOG_TYPE_ADD_PROCESS:
				addProcess(&packet->ap, response);
				break;

			case _WATCHDOG_TYPE_DELETE_PROCESS:
				deleteProcess(&packet->dp, response);
				break;

			case _WATCHDOG_TYPE_ADD_SUBPROCESS:
				addSubprocess(&packet->asp, response);
				break;

			case _WATCHDOG_TYPE_DELETE_SUBPROCESS:
				deleteSubprocess(&packet->dsp, response);
				break;

			case _WATCHDOG_TYPE_FILE_TO_DELETE_POST_MORTUM:
				addFileToDeletePostMortum(&packet->fdpm, response);
				break;

			case _WATCHDOG_TYPE_DIRECTORY_TO_CLEAN_POST_MORTUM:
				addDirectoryToCleanPostMortum(&packet->dcpm, response);
				break;

			case _WATCHDOG_TYPE_DIRECTORY_TO_DELETE_POST_MORTUM:
				addDirectoryToDeletePostMortum(&packet->ddpm, response);
				break;

			case _WATCHDOG_TYPE_PROCESS_NAME_TO_KILL_POST_MORTUM:
				addProcessNameToKillPostMortum(&packet->pnkpm, response);
				break;

			case _WATCHDOG_TYPE_PROCESS_ID_TO_KILL_POST_MORTUM:
				addProcessIdToKillPostMortum(&packet->pikpm, response);
				break;
		}
	}

	// Creates a new process, and populates it with the next available handle
	SProcess* createNewSProcess(void)
	{
		SProcess* sp;

		sp = (SProcess*)malloc(sizeof(SProcess));
		if (sp != NULL)
		{	// Initialize it
			ZeroMemory(sp, sizeof(SProcess));
			sp->handle = gnNextHandle++;
		}
		return(sp);
	}

	// Appends it to the chain
	bool appendNewSProcessToLinkedList(SProcess* sp)
	{
		SProcessLL*		spll;
		SProcessLL*		spllNew;


		spllNew = (SProcessLL*)malloc(sizeof(SProcessLL));
		if (spllNew)
		{	// Initialize it
			ZeroMemory(spllNew, sizeof(SProcessLL));
			memcpy(&spllNew->process, sp, sizeof(SProcess));

			// Add it to the linked list
			spll = gsFirstProcess;
			if (spll == NULL)
			{	// First one
				gsFirstProcess = spllNew;

			} else {
				// Iterate through linked list to find the end, and add it there
				while (spll->next != NULL)
					spll = spll->next;

				spll->next = spllNew;
			}
			return(true);
		}
		return(false);
	}

	// Append it to the end of the subprocess chain for the specified process (by its handle)
	bool appendNewSProcessToLinkedListSubprocess(SProcessLL* spll, SProcess* sp)
	{
		SProcessLL*		spllNew;

		if (spll != NULL)
		{	// Add it here
			spllNew = (SProcessLL*)malloc(sizeof(SProcessLL));
			if (spllNew)
			{	// Initialize it
				ZeroMemory(spllNew, sizeof(SProcessLL));
				memcpy(&spllNew->process, sp, sizeof(SProcess));

				if (spll->firstSubprocess == NULL)
				{	// First one
					spll->firstSubprocess = spllNew;

				} else {
					// Iterate through linked list to find the end, and add it there
					spll = spll->firstSubprocess;
					while (spll->next != NULL)
						spll = spll->next;

					spll->next = spllNew;
				}
				return(true);
			}
		}
		// If we get here, the parent process wasn't valid, or we couldn't allocate memory
		return(false);
	}

	// Delete the specified linked list entry
	// We have to search from the beginning to find which previous entry points to it, since all linked list entries in this tree are one-way-pointers (only to the next entry, no prev entries are permanently stored)
	void deleteLinkedListEntry(SProcessLL* spll)
	{
		SProcessLL** prev;

		if (spll != NULL)
		{	// Find out where it is
			if (spll == gsFirstProcess)
			{	// We are updating the first entry
				gsFirstProcess = spll->next;

			} else {
				// It's a later entry
				prev = findLinkedListEntryBeforeThisOne(gsFirstProcess, spll);
				if (prev != NULL)
				{	// There is a previous entry
					// Update its pointers to skip over this one we're deleting
					*prev = spll->next;
				}
			}

			// Based on what entries are here, delete them
			free(spll);
		}
	}

	// Called recursively for each next and firstSubprocess branch, trying to find the needle in the haystack
	// It could be either part of the main linked list (next entries) or one of its subprocesses (firstSubprocess entries)
	SProcessLL** findLinkedListEntryBeforeThisOne(SProcessLL* haystack, SProcessLL* needle)
	{
		SProcessLL** result;

		if (haystack->firstSubprocess == needle)
			return(&haystack->firstSubprocess);	// It's the first entry in the subprocess list, return this one's firstSubprocess pointer

		if (haystack->next == needle)
			return(&haystack->next);			// It's the next entry after this one, return this one's next pointer

		// Try subprocesses
		if (haystack->firstSubprocess != NULL)
		{	// See if it's down this branch
			result = findLinkedListEntryBeforeThisOne(haystack->firstSubprocess, needle);
			if (result != NULL)
				return(result);	// It was, return wherever it fell
			// If we get here, it wasn't
		}

		// Try processes further down the chain
		if (haystack->next != NULL)
		{	// See if it's down this branch
			result = findLinkedListEntryBeforeThisOne(haystack->next, needle);
			if (result != NULL)
				return(result);	// It was, return wherever it fell
			// If we get here, it wasn't
		}

		// Not found
		return(NULL);
	}

	// They're adding a process for timeout watch
	void addProcess(SAddProcess* ap, SResponse* response)
	{
		boolean		failure				= true;
		int			error				= 0;
		char		notes[sizeof(response->notes)];
		SProcess*	sp;

		// Clear out our response notes area
		ZeroMemory(notes, sizeof(notes));

		// Make sure the timeout data is valid, from 1 second to 5 days
		if (ap->timeout > 0 && ap->timeout < 5*86400)
		{	// For now, we go ahead and add it even if the process is invalid.
			// Once it falls out of scope and is no longer valid, it will be removed from the queue and its watchdog will be terminated
			sp = createNewSProcess();
			if (sp != NULL)
			{	// Populate it
				sp->id					= ap->id;
				sp->timeout				= ap->timeout;
				sp->countdown			= ap->timeout;

				// Copy over optional name and alias
				memcpy(&sp->name,	&ap->name,	min(sizeof(sp->name),	sizeof(ap->name)));
				memcpy(&sp->alias,	&ap->alias,	min(sizeof(sp->alias),	sizeof(ap->alias)));

				// Append it to the linked list
				if (appendNewSProcessToLinkedList(sp))
				{	// Update our response
					failure					= false;
					response->status		= _SUCCESS;
					response->handle		= sp->handle;
					response->error			= 0;
					response->processCount	= 0;
					sprintf_s(notes, sizeof(notes), "Added Process #%u (%s)\000", sp->id, sp->alias);

				} else {
					// Report the memory error
					error = -1;
					sprintf_s(notes, sizeof(notes), "Unable to append new process to the process chain, out of memory\000");
				}

			} else {
				// Report the memory error
				error = -2;
				sprintf_s(notes, sizeof(notes), "Unable to allocate memory for new process\000");
			}

		} else {
			// Report the parameter error
			error = -3;
			sprintf_s(notes, sizeof(notes), "Timeout parameter (duration) must be between 1 and 5*86400\000");
		}

		// If there was a failure, we could not add it
		if (failure)
		{	// Populate the response with the failure
			response->status		= _FAILURE;
			response->handle		= 0;
			response->error			= error;
			response->processCount	= 0;
		}

		// Add in any note that needs added
		ZeroMemory(&response->notes, sizeof(response->notes));
		memcpy(&response->notes, notes, min(strlen(notes), sizeof(response->notes)));
	}

	void deleteProcess(SDeleteProcess* dp, SResponse* response)
	{
		boolean			failure				= true;
		int				error				= 0;
		char			notes[sizeof(response->notes)];
		SProcessLL*		spll;

		// Clear out our response notes area
		ZeroMemory(notes, sizeof(notes));

		// Verify the handle is valid
		spll = isValidHandleAtProcessLevel(dp->handle);
		if (spll != NULL)
		{	// Update our response (we do this first while the relevant data is still in scope)
			failure					= false;
			response->status		= _SUCCESS;
			response->handle		= 0;
			response->error			= 0;
			response->processCount	= 0;
			sprintf_s(notes, sizeof(notes), "Deleted Process #%u (handle #%u, %s)\000", spll->process.id, spll->process.handle, spll->process.alias);

			// Delete it
			deleteLinkedListEntry(spll);

		} else {
			// Invalid handle
			error = -1;
			sprintf_s(notes, sizeof(notes), "Handle parameter is not valid (or no longer valid if process has fallen out of scope)\000");
		}

		// If there was a failure, we could not add it
		if (failure)
		{	// Populate the response with the failure
			response->status		= _FAILURE;
			response->handle		= 0;
			response->error			= error;
			response->processCount	= 0;
		}

		// Add in any note that needs added
		ZeroMemory(&response->notes, sizeof(response->notes));
		memcpy(&response->notes, notes, min(strlen(notes), sizeof(response->notes)));
	}

	void addSubprocess(SAddSubprocess* asp, SResponse* response)
	{
		boolean			failure				= true;
		int				error				= 0;
		char			notes[sizeof(response->notes)];
		SProcess*		sp;
		SProcessLL*		spll;

		// Clear out our response notes area
		ZeroMemory(notes, sizeof(notes));

		// Verify the handle is valid
		spll = isValidHandleAtProcessLevel(asp->handle);
		if (spll != NULL)
		{	// Make sure the timeout data is valid, from 1 second to 5 days
			if (asp->timeout > 0 && asp->timeout < 5*86400)
			{	// For now, we go ahead and add it even if the subprocess is invalid.
				// Once it falls out of scope and is no longer valid, it will be removed from the queue and its watchdog will be terminated
				sp = createNewSProcess();
				if (sp != NULL)
				{	// Populate it
					sp->id					= asp->sid;
					sp->timeout				= asp->timeout;
					sp->countdown			= asp->timeout;

					// Copy over optional name and alias
					memcpy(&sp->name,	&asp->name,		min(sizeof(sp->name),	sizeof(asp->name)));
					memcpy(&sp->alias,	&asp->alias,	min(sizeof(sp->alias),	sizeof(asp->alias)));

					// Append it to the linked list
					if (appendNewSProcessToLinkedListSubprocess(spll, sp))
					{	// Update our response
						failure					= false;
						response->status		= _SUCCESS;
						response->handle		= sp->handle;
						response->error			= 0;
						response->processCount	= 0;
						sprintf_s(notes, sizeof(notes), "Added Subprocess #%u (%s)\000", sp->id, sp->alias);

					} else {
						// Report the memory error
						error = -1;
						sprintf_s(notes, sizeof(notes), "Unable to append new subprocess to the process chain, out of memory\000");
					}

				} else {
					// Report the memory error
					error = -2;
					sprintf_s(notes, sizeof(notes), "Unable to allocate memory for new subprocess\000");
				}

			} else {
				// Report the parameter error
				error = -3;
				sprintf_s(notes, sizeof(notes), "Timeout parameter (duration) must be between 1 and 5*86400\000");
			}

		} else {
			// Invalid handle
			error = -4;
			sprintf_s(notes, sizeof(notes), "Parent handle parameter is not valid (or no longer valid if process has fallen out of scope)\000");
		}

		// If there was a failure, we could not add it
		if (failure)
		{	// Populate the response with the failure
			response->status		= _FAILURE;
			response->handle		= 0;
			response->error			= error;
			response->processCount	= 0;
		}

		// Add in any note that needs added
		ZeroMemory(&response->notes, sizeof(response->notes));
		memcpy(&response->notes, notes, min(strlen(notes), sizeof(response->notes)));
	}

	void deleteSubprocess(SDeleteSubprocess* dsp, SResponse* response)
	{
		boolean			failure				= true;
		int				error				= 0;
		char			notes[sizeof(response->notes)];
		SProcessLL*		spll;

		// Clear out our response notes area
		ZeroMemory(notes, sizeof(notes));

		// Verify the handle is valid
		spll = isValidHandleAtSubprocessLevel(dsp->handle);
		if (spll != NULL)
		{	// Update our response (we do this first while the relevant data is still in scope)
			failure					= false;
			response->status		= _SUCCESS;
			response->handle		= 0;
			response->error			= 0;
			response->processCount	= 0;
			sprintf_s(notes, sizeof(notes), "Deleted Subprocess #%u (handle #%u, %s)\000", spll->process.id, spll->process.handle, spll->process.alias);

			// Delete it
			deleteLinkedListEntry(spll);

		} else {
			// Invalid handle
			error = -1;
			sprintf_s(notes, sizeof(notes), "Handle parameter is not valid (or no longer valid if process or subprocess has fallen out of scope)\000");
		}

		// If there was a failure, we could not add it
		if (failure)
		{	// Populate the response with the failure
			response->status		= _FAILURE;
			response->handle		= 0;
			response->error			= error;
			response->processCount	= 0;
		}

		// Add in any note that needs added
		ZeroMemory(&response->notes, sizeof(response->notes));
		memcpy(&response->notes, notes, min(strlen(notes), sizeof(response->notes)));
	}

	void addFileToDeletePostMortum(SFileToDeletePostMortum* fdpm, SResponse* response)
	{
	}

	void addDirectoryToCleanPostMortum(SDirectoryToCleanPostMortum* dcpm, SResponse* response)
	{
	}

	void addDirectoryToDeletePostMortum(SDirectoryToDeletePostMortum* ddpm, SResponse* response)
	{
	}

	void addProcessNameToKillPostMortum(SProcessNameToKillPostMortum* pnkpm, SResponse* response)
	{
	}

	void addProcessIdToKillPostMortum(SProcessIdToKillPostMortum* pikpm, SResponse* response)
	{
	}

	bool isValidProcess(DWORD pid)
	{
		HANDLE hProcess;
		DWORD exitCode;

		if (pid == 0)
			return true;	// System idle process--always running
		if (pid < 0)
			return false;	// Invalid PID

		hProcess = OpenProcess(PROCESS_QUERY_INFORMATION, FALSE, pid);

		if (hProcess == NULL)
			return false;	// Invalid PID of another sort

		if (GetExitCodeProcess(hProcess, &exitCode))
		{	// If we get here, we're good, and we have its exit code
			CloseHandle(hProcess);
			return (exitCode == STILL_ACTIVE);		// If it's still active, we indicate the same
		}
		// If we get here, an error accessing that hProcess
		CloseHandle(hProcess);
		return true;
	}

	// Check process and subprocess levels
	SProcessLL* isValidHandle(int handle)
	{
		SProcessLL* spll;

		spll = isValidHandleAtProcessLevel(handle);
		if (spll != NULL)
			return(spll);

		spll = isValidHandleAtSubprocessLevel(handle);
		return(spll);
	}

	// Check process level
	SProcessLL* isValidHandleAtProcessLevel(int handle)
	{
		SProcessLL* spll;

		// Find the SProcess with the specified handle
		spll = gsFirstProcess;
		while (spll != NULL && spll->process.handle != handle)
			spll = spll->next;

		return(spll);	// If NULL invalid, otherwise valid
	}

	// Check subprocess level
	SProcessLL* isValidHandleAtSubprocessLevel(int handle)
	{
		SProcessLL* spll;
		SProcessLL*	spllsp;

		// Find the SProcess with the specified handle
		spll = gsFirstProcess;
		while (spll != NULL)
		{	// See if it's down this subprocess branch
			spllsp = spll->firstSubprocess;
			while (spllsp != NULL && spllsp->process.handle != handle)
				spllsp = spllsp->next;

			if (spllsp != NULL)
				return(spllsp);		// We found it at this subprocess level
			// If we get here, it wasn't found

			// Move to next parent
			spll = spll->next;
		}
		return(spll);	// If NULL invalid, otherwise valid
	}
