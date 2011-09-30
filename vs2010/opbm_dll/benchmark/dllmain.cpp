//////////
//
// DllMain.cpp
//
/////
//
// This source file is supportive of the Java Benchmark application.
// It is arranged a little unusually because of the requirements of
// Java communicating with "native" C/C++ functions.
//
// To facilitate simplicity, the source code required to carry out the
// C++ work is included in benchmark.cpp, and this DllMain serves as a
// spawning point for that code.  This file handles all of the peculiarities
// of the Java JNI interface code, and sends raw C++ data types to the
// functions in benchmark.cpp.
//
/////
	#include <SDKDDKVer.h>
	#define WIN32_LEAN_AND_MEAN
	#include "windows.h"
	#include "winbase.h"
	#include "benchmark_jni.h"		// JNI include file derived from netbeans benchmark.jar
	#include <malloc.h>

	// Should be defined for the maximum number of instances of running JVMs that will be encountered
	#define _MAX_CONNECTIONS 32		// 32-cores should be sufficient




//////////
//
// Global variables
//
/////
	// Handles synchronized access in certain operations
	CRITICAL_SECTION*	gCriticalSection = NULL;
	struct SConnections
	{
		char*	uuid;
		char*	name;
		int		handle;
		int		testMax;
		int		testCurrent;
		float	testCompleted;
	};	// Each connection will create an instance
	SConnections	gsConnections[_MAX_CONNECTIONS];
	int				nextHandle = 0;
	HWND			ghWndJBM = NULL;









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
		switch (ul_reason_for_call)
		{
		case DLL_PROCESS_ATTACH:
		case DLL_THREAD_ATTACH:
			if (gCriticalSection == NULL)
			{	// Initialize it
				gCriticalSection = (CRITICAL_SECTION*)malloc(sizeof(CRITICAL_SECTION));
				if (gCriticalSection != NULL)
				{	// We're good
					ZeroMemory(gCriticalSection, sizeof(CRITICAL_SECTION));
					InitializeCriticalSection(gCriticalSection);
				} else {
					// Failure
					return FALSE;
				}
			}
			if (ghWndJBM == NULL)
			{	// Locate the JBM process, which must be running beforehand
				EnterCriticalSection(gCriticalSection);

				ghWndJBM = 

				LeaveCriticalSection(gCriticalSection);
			}
			break;

		case DLL_THREAD_DETACH:
		case DLL_PROCESS_DETACH:
			break;
		}

		return TRUE;
	}




//////////
//
// firstConnectN(JString uuid, JString instanceTitle, JInt testCount)
//
// Called to initially connect to the JBM monitor app by a JVM that has
// been properly launched, and is desiring to begin running its test.
//
// Returns:
//		-1			- Failure in creating a new entry
//		-2			- The uuid specified already exists, and cannot be re-used
//		1 or above	- A valid handle to reference this instance
//
/////
	// firstConnectN()
	JNIEXPORT jint JNICALL Java_benchmark_Benchmark_firstConnectN(JNIEnv* env, jclass cls, jstring uuid, jstring instanceTitle, jint testCount)
	{
		int i, length, lengthInstanceTitle;
		jint result;
		const char* cuuidptr;
		const char* cinstanceTitleptr;
		jboolean isCopy, isCopyInstanceTitle;

		EnterCriticalSection(gCriticalSection);

		// See if we need to initialize everything
		if (nextHandle == 0)
		{	// Yes, nothing's been initialized yet
			for (i = 0; i < _MAX_CONNECTIONS; i++)
			{	// Initialize each entry to an empty state
				gsConnections[i].uuid			= NULL;
				gsConnections[i].name			= NULL;
				gsConnections[i].handle			= -1;
				gsConnections[i].testMax		= -1;
				gsConnections[i].testCurrent	= -1;
				gsConnections[i].testCompleted	= -1.0f;
			}
			// We're initialized now
			nextHandle = 1;
		}

		// See if this process has already connected
		result	= -1;	// Indicate failure in creating a new one
		length	= env->GetStringLength(uuid);
		if (length <= 128)
		{	// Copy it to our local form
			lengthInstanceTitle		= env->GetStringLength(instanceTitle);
			cuuidptr				= env->GetStringUTFChars(uuid,			&isCopy);
			cinstanceTitleptr		= env->GetStringUTFChars(instanceTitle, &isCopyInstanceTitle);
			for (i = 0; i < _MAX_CONNECTIONS; i++)
			{	// For each entry, we are looking to see if it's already been assigned a handle
				if (gsConnections[i].uuid != NULL)
				{	// We found an entry, see if it's this one
					if (strlen(gsConnections[i].uuid) == length)
					{	// It's a candidate
						if (_memicmp(gsConnections[i].uuid, cuuidptr, length) == 0)
						{	// It's a match, this is an error as there should not be a match
							result = -2;	// Indicate failure in that this uuid already exists
							break;
						}
					}
				}
			}
			// Check our status
			if (result == -1)
			{	// If we get here, it wasn't found, add it
				for (i = 0; i < _MAX_CONNECTIONS; i++)
				{	// Find an empty slot
					if (gsConnections[i].uuid == NULL)
					{	// We found an entry, see if it's this one
						gsConnections[i].uuid			= (char*)malloc(length				+ 1);
						gsConnections[i].name			= (char*)malloc(lengthInstanceTitle	+ 1);
						gsConnections[i].handle			= nextHandle;
						gsConnections[i].testMax		= -1;
						gsConnections[i].testCurrent	= -1;
						gsConnections[i].testCompleted	= -1.0f;

						// Copy the strings
						ZeroMemory(gsConnections[i].uuid, length				+ 1);
						ZeroMemory(gsConnections[i].name, lengthInstanceTitle	+ 1);
						memcpy(gsConnections[i].uuid, cuuidptr,				length);
						memcpy(gsConnections[i].name, cinstanceTitleptr,	lengthInstanceTitle);

						// Grab the handle
						result = gsConnections[i].handle;
						++nextHandle;
						break;
					}
				}
				// When we get here, it's either added or not, but result is set to the correct value
			}

			// Free our pointer (and its potentially copied duplicate of the original string memory)
			env->ReleaseStringUTFChars(uuid,			cuuidptr);
			env->ReleaseStringUTFChars(instanceTitle,	cinstanceTitleptr);
		}

		LeaveCriticalSection(gCriticalSection);
		return(result);
	}




//////////
//
// okayToBegin(JInt handle)
//
// Called to see if all of the JVMs have reported in yet.  If so, true
// is returned and testing can begin.  If no, the caller must pause for
// some time (a second) and then repeat the inquiry.
//
/////
	// okayToBeginN()
	JNIEXPORT jboolean JNICALL Java_benchmark_Benchmark_okayToBeginN(JNIEnv* env, jclass cls, jint handle)
	{
		jboolean result;

		result = false;
		return(result);
	}




//////////
//
// reportTestN(JInt handle, JInt testNumber, JString testName)
//
// Called to indicate what test number we're on and what its name is.
//
/////
	// reportTestN()
	JNIEXPORT void JNICALL Java_benchmark_Benchmark_reportTestN(JNIEnv* env, jclass cls, jint handle, jint testNumber, jstring testName)
	{
	}




//////////
//
// reportCompletionN(JInt handle, JFloat percent)
//
// Called to indicate how far completed the app is on the given test
//
/////
	// reportCompletionN()
	JNIEXPORT void JNICALL Java_benchmark_Benchmark_reportCompletionN(JNIEnv* env, jclass cls, jint handle, jfloat percent)
	{
	}




//////////
//
// streamN(JInt handle, JInt testNumber)
//
// Called to initiate the STREAM test from the minibench app
//
/////
	// streamN()
	JNIEXPORT void JNICALL Java_benchmark_Benchmark_streamN(JNIEnv* env, jclass cls, jint handle, jint testNumber)
	{
	}
