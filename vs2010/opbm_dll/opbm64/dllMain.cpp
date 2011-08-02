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
	#include "atlconv.h"




//////////
//
// Global variables
//
/////
	HMODULE ghModule;




//////////
//
// Forward declarations for opbm64.cpp functions
//
/////
	void			sendWindowToForeground					(const TCHAR* title);




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
	JNIEXPORT void JNICALL Java_opbm_Opbm_sendWindowToForeground_1native(JNIEnv* env, jobject obj, jstring title)
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
