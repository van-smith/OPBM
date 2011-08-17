//////////
//
// DllMain.cpp
//
/////
//
// This source file is supportive of the Opbm Java application.
// It is arranged a little unusually because of the requirements of
// Java communicating with "native" C/C++ functions.
//
// To facilitate simplicity, the source code required to carry out the
// C++ work is included in opbm64.cpp, and this DllMain serves as a
// spawning point for that code.  This file handles all of the peculiarities
// of the Java JNI interface code, and sends raw C++ data types to the
// functions in opbm64.cpp.
//
/////
	#include "windows.h"
	#include "winbase.h"
	#include "opbm64.h"




//////////
//
// Global variables
//
/////
	HMODULE ghModule;

	// Used for snapshotProcesses() and stopProcesses()
	DWORD	gProcIDs[2048];
	DWORD	gProcIDsSize = 0;
	HWND	enumeratedWindows[_MAX_HWND_COUNT];
	int		hwndMaxCount	= 0;
	int		hwndsClosed		= 0;




//////////
//
// Main app entry point
//
/////
	BOOL APIENTRY DllMain( HMODULE hModule,
						   DWORD  ul_reason_for_call,
						   LPVOID lpReserved
						 )
	{
		ghModule = hModule;

		switch (ul_reason_for_call)
		{
			case DLL_PROCESS_ATTACH:
			case DLL_THREAD_ATTACH:
			case DLL_THREAD_DETACH:
				break;
			case DLL_PROCESS_DETACH:
				break;
		}
		return TRUE;
	}


// Handls common functions between opbm.dll and opbm64.dll, such as CSIDL directory locations
#include "..\common\opbm_common.cpp"



//////////
//
// sendWindowToForeground(JString title)
//
// Called to bring the specified window (by name) to the foreground.  Used primarily
// to ensure a single instance of the application is running, by sending the already-
// launched instance to the foreground before then immediately terminating.  But, can
// be used for any purpose to send any window to the foreground.
//
// If the window is minimized on the taskbar, it will be restored.
//
/////
	// sendWindowToForeground()
	JNIEXPORT void JNICALL Java_opbm_Opbm_sendWindowToForeground(JNIEnv* env, jclass cls, jstring title)
	{
		USES_CONVERSION;
		const char* wtptr;	// Window title pointer
		jboolean isCopy;

		// Allocate the variable
		wtptr = env->GetStringUTFChars(title, &isCopy);

		sendWindowToForeground(A2T(wtptr));

		// Release the memory
		env->ReleaseStringUTFChars(title, wtptr);

		return;
	}




//////////
//
// dir = GetHarnessCSVDirectory()
//
// Called to return the CSIDL location of the harness's CSV directory.
//
/////
	// GetHarnessCSVDirectory()
	JNIEXPORT jstring JNICALL Java_opbm_Opbm_getHarnessCSVDirectory(JNIEnv* env, jclass cls)
	{
		char		dirname[ MAX_PATH ];
		jstring		directory;

		// Allocate the variable
		GetHarnessCSVDirectory(dirname, sizeof(dirname));

		// Create the return variable
		directory = env->NewStringUTF( dirname );

		return(directory);
	}




//////////
//
// dir = GetHarnessXMLDirectory()
//
// Called to return the CSIDL location of the harness's XML directory.
//
/////
	// GetHarnessXMLDirectory()
	JNIEXPORT jstring JNICALL Java_opbm_Opbm_getHarnessXMLDirectory(JNIEnv* env, jclass cls)
	{
		char		dirname[ MAX_PATH ];
		jstring		directory;

		// Allocate the variable
		GetHarnessXMLDirectory(dirname, sizeof(dirname));

		// Create the return variable
		directory = env->NewStringUTF( dirname );
		return(directory);
	}




//////////
//
// dir = GetHarnessTempDirectory()
//
// Called to return the CSIDL location of the harness's temporary directory.
//
/////
	// GetHarnessTempDirectory()
	JNIEXPORT jstring JNICALL Java_opbm_Opbm_getHarnessTempDirectory(JNIEnv* env, jclass cls)
	{
		char		dirname[ MAX_PATH ];
		jstring		directory;

		// Allocate the variable
		GetHarnessTempDirectory(dirname, sizeof(dirname));

		// Create the return variable
		directory = env->NewStringUTF( dirname );
		return(directory);
	}




//////////
//
// dir = GetScriptCSVDirectory()
//
// Called to return the CSIDL location of the script's CSV directory.
//
/////
	// GetScriptCSVDirectory()
	JNIEXPORT jstring JNICALL Java_opbm_Opbm_getScriptCSVDirectory(JNIEnv* env, jclass cls)
	{
		char		dirname[ MAX_PATH ];
		jstring		directory;

		// Allocate the variable
		GetScriptCSVDirectory(dirname, sizeof(dirname));

		// Create the return variable
		directory = env->NewStringUTF( dirname );
		return(directory);
	}




//////////
//
// dir = GetScriptTempDirectory()
//
// Called to return the CSIDL location of the script's temporary directory.
//
/////
	// GetScriptTempDirectory()
	JNIEXPORT jstring JNICALL Java_opbm_Opbm_getScriptTempDirectory(JNIEnv* env, jclass cls)
	{
		char		dirname[ MAX_PATH ];
		jstring		directory;

		// Allocate the variable
		GetScriptTempDirectory(dirname, sizeof(dirname));

		// Create the return variable
		directory = env->NewStringUTF( dirname );
		return(directory);
	}




//////////
//
// dir = GetSettingsDirectory()
//
// Called to return the CSIDL location of the harness's settings directory.
//
/////
	// GetSettingsDirectory()
	JNIEXPORT jstring JNICALL Java_opbm_Opbm_getSettingsDirectory(JNIEnv* env, jclass cls)
	{
		char		dirname[ MAX_PATH ];
		jstring		directory;

		// Allocate the variable
		GetCSIDLDirectory(dirname, sizeof(dirname), "MYDOCUMENTS");
		memcpy(&dirname[0] + strlen(dirname), "opbm\\settings\\", 14);

		// Create the return variable
		directory = env->NewStringUTF( dirname );
		return(directory);
	}




//////////
//
// dir = getCSIDLDirectory()
//
// Called to return the CSIDL location of the specified directory.
//
/////
	// getCSIDLDirectory()
	JNIEXPORT jstring JNICALL Java_opbm_Opbm_getCSIDLDirectory(JNIEnv* env, jclass cls, jstring str)
	{
		char		dirname[ MAX_PATH ];
		const char* ccptr;
		char*		cptr;
		jboolean	isCopy;
		jstring		directory;
		int			length;

		// Initialize the variable
		memset(dirname, 0, sizeof(dirname));

		// Allocate the variable
		length	= env->GetStringLength(str);
		if (length != 0)
		{
			ccptr = env->GetStringUTFChars(str, &isCopy);		// returns const char*
			if (ccptr != NULL)
			{
				cptr = (char*)malloc(length);					// returns char*
				if (cptr != NULL)
				{
					memcpy(cptr, ccptr, length);

					// Allocate the variable
					GetCSIDLDirectory(dirname, sizeof(dirname), cptr);

					// Release the non-const memory
					free(cptr);
				}
				env->ReleaseStringUTFChars(str, ccptr);
			}
		}

		// Create the return variable
		directory = env->NewStringUTF( dirname );
		return(directory);
	}




//////////
//
// snapshotProcesses()
//
// Called to take a snapshot of all running processes as of the time of this call.
//
/////
	// snapshotProcesses()
	JNIEXPORT void JNICALL Java_opbm_Opbm_snapshotProcesses(JNIEnv* env, jclass cls)
	{
		// Note all windows
		hwndMaxCount = -1;
		EnumWindows(&EnumWindowsCallbackProc, 0);

		// Note all processes
		if (EnumProcesses(&gProcIDs[0], sizeof(gProcIDs), &gProcIDsSize))
		{	// Convert byte count to dword count
			gProcIDsSize /= 4;
			// It stored everything that exists up to 2048 processes (a way high number)
		}
	}




//////////
//
// stopProcesses()
//
// Called to stop all processes that weren't running when the last snapshot was taken.
//
/////
	// stopProcesses()
	JNIEXPORT void JNICALL Java_opbm_Opbm_stopProcesses(JNIEnv* env, jclass cls)
	{
		int result;
		DWORD i, j;
		DWORD lProcIDs[2048];
		DWORD lProcIDsSize;
		HANDLE handle;

		// Close windows
		hwndsClosed = 0;
		EnumWindows(&ComparativeWindowsCallbackProc, 0);

		// Wait three seconds (for windo
		Sleep(3000);

		// Close processes
		if (gProcIDsSize != 0)
		{
			// Get a list of current processes, and kill every one that's not in our original list
			if (EnumProcesses(&lProcIDs[0], sizeof(lProcIDs), &lProcIDsSize))
			{
				// Convert byte count to dword count
				lProcIDsSize /= 4;

				// Compare against everything that existed previously
				// Iterate through the current ones, see if they're found in the old ones
				for (i = 0; i < lProcIDsSize; i++)	// current ones
				{
					for (j = 0; j < gProcIDsSize; j++)	// old ones
					{
						// If we find a match, it's a process to keep
						if (lProcIDs[i] == gProcIDs[j])
						{
							// We found a match, keep it
							break;
						}
						// If we get here, it wasn't found, keep checking
					}
					// If we get here, we either left the loop early by finding a match, or not
					if (j >= gProcIDsSize)
					{
						// If we get here, we did not find this one
						// So, we are desiring to terminate this process with extreme prejudice (ie, immediately)
						handle = OpenProcess(PROCESS_TERMINATE, FALSE, lProcIDs[i]);
						if (handle != NULL)
						{
							// Terminate the process (or try to, Windows may not allow us to)
							result = TerminateProcess(handle, -1);
						}
						// If we get here, we've done our earnest best... nothing else to do
					}
				}
			}
		}
	}
