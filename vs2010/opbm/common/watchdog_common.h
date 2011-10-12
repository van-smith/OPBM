//////////
//
// watchdog_common.h
//
/////
//
//


#include "..\common\pipe_common.h"

const wchar_t	_WATCHDOG_Class_Name[]							= L"WatchdogProcessForOPBM";
const wchar_t	_WATCHDOG_Window_Name[]							= L"Watchdog Process";
const wchar_t	_WATCHDOG_Pipe_Name_Harness[]					= L"\\\\.\\pipe\\Watchdog Data Pipe For OPBM Harness";
const wchar_t	_WATCHDOG_Pipe_Name_Script[]					= L"\\\\.\\pipe\\Watchdog Data Pipe For Scripts";

// Special number returned during the "reporting in" messages, to indicate success
#define _WATCHDOG_SUCCESS_RESPONSE								0x12345588

// SPipeData type message values:
#define _WATCHDOG_TYPE_ADD_PROCESS								1
#define _WATCHDOG_TYPE_DELETE_PROCESS							2
#define _WATCHDOG_TYPE_ADD_SUBPROCESS							3
#define _WATCHDOG_TYPE_DELETE_SUBPROCESS						4
#define _WATCHDOG_TYPE_FILE_TO_DELETE_POST_MORTUM				5
#define _WATCHDOG_TYPE_DIRECTORY_TO_CLEAN_POST_MORTUM			6
#define _WATCHDOG_TYPE_DIRECTORY_TO_DELETE_POST_MORTUM			7
#define _WATCHDOG_TYPE_PROCESS_NAME_TO_KILL_POST_MORTUM			8
#define _WATCHDOG_TYPE_PROCESS_ID_TO_KILL_POST_MORTUM			9

// Used by WndProc as MSG, WPARAM and LPARAM have related data
#define _WATCHDOG_HARNESS_REPORTING_IN							WM_USER + 1
#define _WATCHDOG_SCRIPT_REPORTING_IN							WM_USER + 2
#define _WATCHDOG_HARNESS_HAS_PIPE_DATA							WM_USER + 3
#define _WATCHDOG_SCRIPT_HAS_PIPE_DATA							WM_USER + 4
#define _WATCHDOG_SHOULD_SELF_TERMINATE							WM_USER + 5




//////////
// Structures to populate based on which type of packet is being sent
// Note:  The original packet is unchanged in response, with the SResponse field being updated appropriately
/////
	struct SResponse
	{
		int					status;							// The status is always 0-failure, 1-success
		int					handle;							// The handle returned for the new item (if something was added)
		int					error;							// If status is 0, populated with an error code for the requested function
		SPipeDataLongName	notes;							// A word response which explains what happened, such as "Subprocess ID #N does not exist", etc.

		// For SProcessNameToKillPostMortum when partial or repeated search is used
		int					processCount;					// The number of processes found with that partial or repeated name
	};

	struct SAddProcess
	{
		int					id;								// Process ID to monitor
		int					timeout;						// Timeout in seconds until this process is killed
		SPipeDataLongName	alias;							// A user-defined alias name to give this id, can be used in responses to identify (by name) the process, rather than just the id number
	};

	struct SDeleteProcess
	{
		int					handle;							// Previously returned handle to be deleted, so its watchdog is no longer employed
	};

	struct SAddSubprocess
	{
		int					handle;							// Handle to parent Process ID, as returned from previous call to SAddProcess
		int					sid;							// Subprocess ID to monitor
		int					timeout;						// Timeout in seconds until this Subprocess, and its parent and other sibling Subprocesses, are killed
		SPipeDataLongName	alias;							// A user-defined alias name to give this id, can be used in responses to identify (by name) the process, rather than just the id number
	};

	struct SDeleteSubprocess
	{
		int					handle;							// Previously returned handle to be deleted, so its watchdog is no longer employed
	};

	struct SFileToDeletePostMortum
	{
		int					handle;							// The previously returned handle to associate this filename
		SPipeDataFilename	filename;						// Fully qualified path name to the file to delete, as in c:\\full\\path\\and\\filename.ext
	};

	struct SDirectoryToCleanPostMortum
	{
		int					handle;							// The previously returned handle to associate this directory
		SPipeDataDirectory	directory;						// Fully qualified path name to the directory to clean (delete all files and subdirs within), as in c:\\full\\path\\ 
	};

	struct SDirectoryToDeletePostMortum
	{
		int					handle;							// The previously returned handle to associate this directory
		SPipeDataDirectory	directory;						// Fully qualified path name to the directory to delete, as in c:\\full\\path\\ 
	};

	struct SProcessNameToKillPostMortum
	{
		int					handle;							// The previously returned handle to associate this process-to-kill
		int					partialSearch;					// 0-No, 1-Yes, Should a partial search (substr) be conducted on the name?  (Useful for killing all "chrome" processes, such as "chromelaunch.exe" and "chromeupdate.exe", etc.)
		int					repeatedSearch;					// 0-No, 1-Yes, Should a repeated search be conducted on the name?  (Useful for killing all programx.exe processes, for example, when multiple instances are spawned as part of a test)
		SPipeDataLongName	process;						// Name (or partial name) to search for the process
		SPipeDataLongName	alias;							// A user-defined alias name (or group alias name) to give this entry/these entries, can be used in responses to identify (by name) the process, rather than just the id number
	};

	struct SProcessIdToKillPostMortum
	{
		int					handle;							// The previously returned handle to associate this process-to-kill
		int					sid;							// Subprocess ID to kill when the process associated with the handle is killed
		SPipeDataLongName	alias;							// A user-defined alias name to give this id, can be used in responses to identify (by name) the process, rather than just the id number
	};

//////////
// Pipe data contained by harness and script communications
/////
	struct SPipeData
	{
		int			type;
		// Based on the type, the following structures will be populated and are employed for the task:
		union		data
		{	// The following messages appear in the data union, as only one is ever used at a time
			SAddProcess						ap;				// When type is _WATCHDOG_TYPE_ADD_PROCESS
			SDeleteProcess					dp;				// When type is _WATCHDOG_TYPE_DELETE_PROCESS
			SAddSubprocess					asp;			// When type is _WATCHDOG_TYPE_ADD_SUBPROCESS
			SDeleteSubprocess				dsp;			// When type is _WATCHDOG_TYPE_DELETE_SUBPROCESS
			SFileToDeletePostMortum			fdpm;			// When type is _WATCHDOG_TYPE_FILE_TO_DELETE_POST_MORTUM
			SDirectoryToCleanPostMortum		dcpm;			// When type is _WATCHDOG_TYPE_DIRECTORY_TO_CLEAN_POST_MORTUM
			SDirectoryToDeletePostMortum	ddpm;			// When type is _WATCHDOG_TYPE_DIRECTORY_TO_DELETE_POST_MORTUM
			SProcessNameToKillPostMortum	pnkpm;			// When type is _WATCHDOG_TYPE_PROCESS_NAME_TO_KILL_POST_MORTUM
			SProcessIdToKillPostMortum		pikpm;			// When type is _WATCHDOG_TYPE_PROCESS_ID_TO_KILL_POST_MORTUM
		};
	};

// Message structures contained within SHarnessPipeData and SScriptPipeData
#ifdef _WATCHDOG_HARNESS
	// This is the structure physically transferred back and forth from harness
	// to watchdog, and back from watchdog to harness, in the harness named pipe.
	struct SHarnessPipeData
	{
		SPipeData		packet;								// Data packet populated by harness through opbm32.dll/opbm64.dll
		SResponse		response;							// Assigned by watchdog after the type+data is processed above
// REMEMBER - Additional items will appear here for communication to the harness of killed processes
	};
#endif

#ifdef _WATCHDOG_SCRIPTS
	// This is the structure physically transferred back and forth from script
	// to watchdog, and back from watchdog to script, in the script named pipe.
	struct SScriptPipeData
	{
		SPipeData		packet;								// Data packet populated by script through opbm.dll
		SResponse		response;							// Assigned by watchdog after the type+data is processed above
	};
#endif
