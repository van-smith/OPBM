//////////
//
// enumthreads.h
//
// To be included by applications needing to use enumthreads.dll.
// To use, call "LoadEnumThreadsDll" with the path to the dll.
// It returns TRUE or FALSE if it was loaded successfully.
//
/////

#pragma once




//////////
// structs
/////
	typedef struct _PROCINFO
	{
		DWORD	dwPID;
		TCHAR	szProcName[32];
	} PROCINFO, *LPPROCINFO;

	typedef struct _THREADINFO
	{
		DWORD	dwPID;
		TCHAR	szProcName[32];

		DWORD	dwThreadId;
		LPVOID	lpStartAddress;
		DWORD	dwThreadState;
		DWORD	dwThreadWaitReason;
	} THREADINFO, *LPTHREADINFO;




//////////
// Import prototypes from the DLL
/////
	BOOL (WINAPI* EnumProcessNames)(LPPROCINFO lpProcInfo, DWORD cb, LPDWORD cbNeeded);
	BOOL (WINAPI* EnumThreads)(LPTHREADINFO lpThreadInfo, DWORD cb, LPDWORD cbNeeded);
	BOOL (WINAPI* EnumProcessThreads)(LPTHREADINFO lpThreadInfo, DWORD cb, LPDWORD cbNeeded);
	BOOL (WINAPI* EnumProcessThreadsEx)(DWORD dwPid, LPTHREADINFO lpThreadInfo, DWORD cb, LPDWORD cbNeeded);

	HMODULE	ghEnumThreadsDllInstance;
    int		gnEnumThreadsDllFunctionCount = 4;
    void* gsEnumThreadsFunctions[] = {
        (void *)&EnumProcessNames,		(void *)"EnumProcessNames",
        (void *)&EnumThreads,			(void *)"EnumThreads",
        (void *)&EnumProcessThreads,	(void *)"EnumProcessThreads",
        (void *)&EnumProcessThreadsEx,	(void *)"EnumProcessThreadsEx"
    };




//////////
// Loader function, to load the DLL and its functions
/////
	BOOL LoadEnumThreadsDll(char* pathToEnumThreadsDll)
	{
		int i;
		void* lpfunc;
		void* lpname;

		ghEnumThreadsDllInstance = LoadLibraryA(pathToEnumThreadsDll);
		if (ghEnumThreadsDllInstance == NULL)
		{	// It wasn't found, failure
			return(FALSE);
		}

		for (i = 0; i < gnEnumThreadsDllFunctionCount; i++)
		{
			lpfunc	= gsEnumThreadsFunctions[(i * 2)];
			lpname	= gsEnumThreadsFunctions[(i * 2) + 1];
			*(FARPROC *)lpfunc = GetProcAddress(ghEnumThreadsDllInstance, (char *)lpname);
			if (lpfunc == NULL)
			{	// The function wasn't found, which most likely means an incorrect version of the DLL
				return(FALSE);
			}
		}
		// If we get here, we're good
		return(TRUE);
	}
