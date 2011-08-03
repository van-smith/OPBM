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
	JNIEXPORT void JNICALL Java_opbm_Opbm_sendWindowToForeground(JNIEnv* env, jobject obj, jstring title)
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
	JNIEXPORT jstring JNICALL Java_opbm_Opbm_getHarnessCSVDirectory(JNIEnv* env, jobject obj)
	{
		USES_CONVERSION;
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
	JNIEXPORT jstring JNICALL Java_opbm_Opbm_getHarnessXMLDirectory(JNIEnv* env, jobject obj)
	{
		USES_CONVERSION;
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
// dir = GetScriptCSVDirectory()
//
// Called to return the CSIDL location of the script's CSV directory.
//
/////
	// GetScriptCSVDirectory()
	JNIEXPORT jstring JNICALL Java_opbm_Opbm_getScriptCSVDirectory(JNIEnv* env, jobject obj)
	{
		USES_CONVERSION;
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
// dir = GetSettingsDirectory()
//
// Called to return the CSIDL location of the harness's settings directory.
//
/////
	// GetSettingsDirectory()
	JNIEXPORT jstring JNICALL Java_opbm_Opbm_getSettingsDirectory(JNIEnv* env, jobject obj)
	{
		USES_CONVERSION;
		char		dirname[ MAX_PATH ];
		jstring		directory;

		// Allocate the variable
		GetCSIDLDirectory(dirname, sizeof(dirname), "MYDOCUMENTS");
		memcpy(&dirname[0] + strlen(dirname), "opbm\\settings\\", 14);

		// Create the return variable
		directory = env->NewStringUTF( dirname );
		return(directory);
	}
