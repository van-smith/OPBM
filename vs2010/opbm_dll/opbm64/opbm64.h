// opbm64.h

//////////
//
// Common include files for dllMain.cpp and opbm64.cpp
//
/////
	#include "windows.h"
	#include "winbase.h"
	#include "..\..\..\java\opbm\jni\opbm64_jni.h"	// Generated by [root]\java\opbm\jni\javah.bat, do not edit manually
	#include "atlconv.h"
	#include "shlobj.h"
	#include "..\common\opbm_common.h"




//////////
//
// Forward declarations for opbm64.cpp functions
//
/////
	void			sendWindowToForeground					(const TCHAR* title);
