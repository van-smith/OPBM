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
#include "..\common\enumthreads_common.h"



//////////
//
// Constants used in watchdog
//
/////
	// None yet



//////////
// Global variables
/////
	struct SProcess
	{	// Internal watchdog structure for each item
		int						handle;									// Handle assigned to the caller by watchdog

		// Process id
		DWORD					id;										// Process ID
		SPipeDataLongName		name;									// Process name (such as "acrord32.exe" or "javaw.exe")
		SPipeDataLongName		alias;									// Internally assigned alias (such as "Acrobat Reader" or "Java Benchmark")

		// For non-watched processes (those tagged as process-to-kill-when-the-parent/child-is-killed), timeout and countdown will both be 0
		int						timeout;								// User-specified timeout period in seconds
		int						countdown;								// When this countdown reaches 0, it's time to terminate the process
	};

	struct SFileToDeleteLL
	{	// A fully qualified pathname of a file to delete when its parent process is killed
		SFileToDeleteLL*		next;
		SPipeDataFilename		filename;
	};

	struct SDirectoryToCleanLL
	{	// A fully qualified pathname of a directory to clean (remove all files and sub-dirs) when its parent process is killed
		SDirectoryToCleanLL*	next;
		SPipeDataDirectory		directory;
	};

	struct SDirectoryToDeleteLL
	{	// A fully qualified pathname of a directory to delete (completely removed) when its parent process is killed
		SDirectoryToDeleteLL*	next;
		SPipeDataDirectory		directory;
	};

	struct SProcessLL
	{	// A linked list of all process data we are watching
		SProcessLL*				next;									// Pointer to the next process in the chain
		SProcessLL*				firstSubprocess;						// Pointer to the first sub-process for this process

		SProcess				process;								// The process data for this process

		SFileToDeleteLL*		firstFileToDelete;						// Pointer to the first file to delete
		SDirectoryToCleanLL*	firstDirToClean;						// Pointer to the first directory to clean (remove all files/sub-dirs)
		SDirectoryToDeleteLL*	firstDirToDelete;						// Pointer to the first directory to delete
	};

	HWND				ghWnd						= NULL;			// Message window
	SProcessLL*			gsFirstProcess				= NULL;			// Root process, if watchdog is not active, then this parameter is NULL
	HINSTANCE			ghInst						= NULL;			// Windows-assigned instance
	HANDLE				ghHarnessPipeHandle			= NULL;			// Handle to the harness named pipe
	HANDLE				ghScriptPipeHandle			= NULL;			// Handle to the script named pipe
	UINT_PTR			ghTimer						= NULL;			// Handle to the timer we create on WM_CREATE
	int					gnNextHandle				= 1;			// Handles assigned to callers, increments after each assignment

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

	bool				sendHarnessPipeMessage						(SHarnessPipeData* pipeData);
	bool				sendScriptPipeMessage						(SScriptPipeData* pipeData);

	void				processHarnessPipeMessage					(SHarnessPipeData* hpd);
	void				processScriptPipeMessage					(SScriptPipeData* spd);
	void				processCommonPipeMessages					(SPacket* packet, SResponse* response);

	SProcess*			createNewSProcess							(void);
	bool				appendNewSProcessToLinkedList				(SProcess* sp);
	bool				appendNewSProcessToLinkedListSubprocess		(SProcessLL* spll, SProcess* sp);
	void				deleteLinkedListEntry						(SProcessLL* spll);
	SProcessLL**		findLinkedListEntryBeforeThisOne			(SProcessLL* haystack, SProcessLL* needle);

	void				addProcess									(SAddProcess* ap, SResponse* response);
	void				deleteProcess								(SDeleteProcess* dp, SResponse* response);
	void				addSubprocess								(SAddSubprocess* asp, SResponse* response);
	void				deleteSubprocess							(SDeleteSubprocess* dsp, SResponse* response);
	void				addFileToDeletePostMortum					(SFileToDeletePostMortum* fdpm, SResponse* response);
	void				addDirectoryToCleanPostMortum				(SDirectoryToCleanPostMortum* dcpm, SResponse* response);
	void				addDirectoryToDeletePostMortum				(SDirectoryToDeletePostMortum* ddpm, SResponse* response);
	void				addProcessNameToKillPostMortum				(SProcessNameToKillPostMortum* pnkpm, SResponse* response);
	void				addProcessIdToKillPostMortum				(SProcessIdToKillPostMortum* pikpm, SResponse* response);

	bool				isValidProcess								(DWORD pid);
	SProcessLL*			isValidHandle								(int handle);
	SProcessLL* 		isValidHandleAtProcessLevel					(int handle);
	SProcessLL*			isValidHandleAtSubprocessLevel				(int handle);
