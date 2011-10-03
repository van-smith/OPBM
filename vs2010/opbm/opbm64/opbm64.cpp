//////////
//
// opbm64.cpp
//
/////
//
// This source file is supportive of the Opbm Java application.
// It is arranged a little unusually because of the requirements of
// Java communicating with "native" C/C++ functions.
//
// To facilitate simplicity, the source code required to carry out the C++
// work is included here in opbm64.cpp, and the DllMain.cpp file serves as a
// spawning point for that code.  DllMain.cpp handles all of the peculiarities
// of the Java JNI interface code, and sends raw C++ data types to the
// functions in this file.
//
/////
	#include "opbm64.h"



//////////
//
// Variables declared in DllMain, not here
//
/////
	extern HMODULE ghModule;




//////////
//
// Global variables declared and used only in opbm64.cpp
//
/////
	// none so far




//////////
//
// sendWindowToForeground(title)
//
// Called to send the specifically-named window to the foreground if it's found.
// If the window is minimized on the taskbar, it will be restored.  Focus is also
// given to the newly-made foreground window.
//
/////
	void sendWindowToForeground(const TCHAR* title)
	{
		HWND hwnd;

		// See if we can find the window
		hwnd = FindWindow(NULL, title);
		if (hwnd != NULL)
		{	// Tell the window to come to the front
			// This process is rather difficult and cumbersome, and who knows why?

			// Attach to the foreground window's process temporarily
			AttachThreadInput(GetWindowThreadProcessId(::GetForegroundWindow(), NULL), GetCurrentThreadId(), TRUE);

			// From the authority given to that foreground window's process, tell it to restore the window
			ShowWindow(hwnd, SW_RESTORE);	// In case it's minimized
			SetForegroundWindow(hwnd);		// Bring it forward
			SetFocus(hwnd);					// Tell Windows to set focus there

			// And detach
			AttachThreadInput(GetWindowThreadProcessId(::GetForegroundWindow(),NULL), GetCurrentThreadId(), FALSE);
		}
		return;
	}
