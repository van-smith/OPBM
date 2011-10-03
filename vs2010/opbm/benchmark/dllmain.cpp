//////////
//
// DllMain.cpp
// JNI DLL 32-/64-bit Support for Java Benchmark
//
// -----
// Last Updated:  Sep 30, 2011
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
// This source file provides support for instances of the Java
// Benchmark, and their separate-process needs to communicate
// with a running untility called JBM (Java Benchmark Monitor)
// which provides a visual HUD on the overall status of the
// multi-process benchmark execution.
//
/////
	#include <SDKDDKVer.h>
	#define WIN32_LEAN_AND_MEAN
	#include "windows.h"
	#include "winbase.h"
	#include "benchmark_jni.h"			// JNI include file derived from netbeans benchmark.jar
	#include "..\common\jbm_common.h"	// JBM common variables
	#include <malloc.h>




//////////
//
// Global variables
//
/////
	// Handles synchronized access in certain operations
	CRITICAL_SECTION*	gCriticalSection = NULL;

	// Refer to jbm_common.h for info on pipe protocols
	struct SConnections
	{
		char*		uuid;							// Caller's UUID assigned this handle
		char*		name;							// Name of the current test being run by caller
		int			handle;							// Handle of this entry
		int			testMax;						// How many tests maximum
		int			testCurrent;					// What number is the current test (base-1)
		float		testCompleted;					// What % of the test has been completed

		// Used for communication with JBM (see jbm_common.h)
		bool		wasLastPipeWriteSuccessful;		// Was the last pipe write successful?
		HANDLE		pipeHandle;						// Handle to the named pipe (created here, opened in JBM)
		SPipeData	pipeData;						// The pipe data to communicate to JBM (see jbm_common.h)
	};	// Each connection will create an instance
	SConnections	gsConnections[_JBM_MAX_CONNECTIONS];

	int				nextHandle = 0;
	HWND			ghWndJBM = NULL;

	// Forward declarations
	void writePipeDataToJBM(SConnections* sc);









//////////
//
// Main app entry point
//
/////
	BOOL APIENTRY DllMain( HMODULE	hModule,
						   DWORD	ul_reason_for_call,
						   LPVOID	lpReserved )
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
					MessageBoxA(NULL, "Unable to allocate necessary memory.", "Fatal Error", MB_OK | MB_ICONERROR);
					return FALSE;
				}
			}

			if (ghWndJBM == NULL)
			{	// Locate the JBM process, which must be running beforehand
				EnterCriticalSection(gCriticalSection);

				ghWndJBM = FindWindow( _JBM_Class_Name, _JBM_Window_Name);
				if (ghWndJBM == NULL)
				{	// Error, JBM is not yet running
					LeaveCriticalSection(gCriticalSection);
					MessageBoxA(NULL, "Java Benchmark Monitor is not running.", "Fatal Error", MB_OK | MB_ICONERROR);
					return FALSE;
				}
				// We're good
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
//		-3			- Unable to communicate with JBM due to data pipe failure
//		1 or above	- A valid handle to reference this instance
//
/////
	// firstConnectN()
	JNIEXPORT jint JNICALL Java_benchmark_Benchmark_firstConnectN(JNIEnv* env, jclass cls, jstring uuid, jstring instanceTitle, jint testCount)
	{
		int				i, length, lengthInstanceTitle;
		jint			result;
		const char*		cuuidptr;
		const char*		cinstanceTitleptr;
		jboolean		isCopy, isCopyInstanceTitle;
		wchar_t			pipeName[sizeof(_JBM_Pipe_Name_Prefix) + 4 + 1];
		SConnections*	sc;

		EnterCriticalSection(gCriticalSection);

		// See if we need to initialize everything
		if (nextHandle == 0)
		{	// Yes, nothing's been initialized yet
			for (i = 0; i < _JBM_MAX_CONNECTIONS; i++)
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
			for (i = 0; i < _JBM_MAX_CONNECTIONS; i++)
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
				for (i = 0; i < _JBM_MAX_CONNECTIONS; i++)
				{	// Find an empty slot
					if (gsConnections[i].uuid == NULL)
					{	// We found an entry, see if it's this one
						sc = &gsConnections[i];
						sc->uuid			= (char*)malloc(length				+ 1);
						sc->name			= (char*)malloc(lengthInstanceTitle	+ 1);
						sc->handle			= nextHandle;
						sc->testMax			= -1;
						sc->testCurrent		= -1;
						sc->testCompleted	= -1.0f;
						//gsConnections[i].namedPipe	= (assigned below)

						// Copy the strings
						ZeroMemory(sc->uuid, length					+ 1);
						ZeroMemory(sc->name, lengthInstanceTitle	+ 1);
						memcpy(sc->uuid, cuuidptr,			length);
						memcpy(sc->name, cinstanceTitleptr,	lengthInstanceTitle);

						// Update the handle for the next call
						++nextHandle;

						// Create the named pipe to use between this instance and the JBM
						wsprintf(pipeName, _JBM_Pipe_wsprintf_string, _JBM_Pipe_Name_Prefix, gsConnections[i].handle);
						sc->pipeHandle	= CreateNamedPipe(pipeName,
														  PIPE_ACCESS_DUPLEX | FILE_FLAG_FIRST_PIPE_INSTANCE,
														  PIPE_READMODE_BYTE | PIPE_NOWAIT,
														  2,
														  sizeof(SPipeData),
														  sizeof(SPipeData),
														  0,
														  NULL);

						if (sc->pipeHandle != INVALID_HANDLE_VALUE)
						{	// We're good

							// Tell the JBM process we have another entry added to the mix
							SendMessage(ghWndJBM, _JBM_NEW_INSTANCE_REPORTING_IN, sc->handle, NULL);

							// Write our first entry, indicating we have not started yet
							sc->pipeData.hasStarted					= false;
							sc->pipeData.hasCompleted				= false;
							sc->pipeData.overallPercentCompleted	= 0.0;
							sc->pipeData.testPercentCompleted		= 0.0;
							sc->pipeData.lastTestTimeInSeconds		= 0.0;
							ZeroMemory(sc->pipeData.instance.name,	sizeof(sc->pipeData.instance.name));
							ZeroMemory(sc->pipeData.test.name,		sizeof(sc->pipeData.test.name));
							memcpy(sc->pipeData.instance.name, sc->name, min(lengthInstanceTitle, sizeof(sc->pipeData.instance.name)));

							// Write the data for the initial read
							writePipeDataToJBM(sc);

							// Tell the JBM process we have another entry added to the mix
							SendMessage(ghWndJBM, _JBM_NEW_INSTANCE_FIRST_DATA, sc->handle, NULL);

							// All done
							// Grab our return result
							result = sc->handle;

						} else {
							// Invalid handle returned, which means the pipe is not valid
							result = -3;
						}
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

	// Write data to the named pipe, and set its success flag
	void writePipeDataToJBM(SConnections* sc)
	{
		DWORD numwritten;

		sc->pipeData.instance.length = strlen(sc->pipeData.instance.name);
		sc->pipeData.test.length = strlen(sc->pipeData.test.name);

		WriteFile(sc->pipeHandle, &sc->pipeData, sizeof(sc->pipeData), &numwritten, NULL);
		sc->wasLastPipeWriteSuccessful = (numwritten == sizeof(sc->pipeData));
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
