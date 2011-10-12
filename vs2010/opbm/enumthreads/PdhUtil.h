
#pragma once

/////////////////////////////////////////////////////////////////////////////
// types

typedef struct _PROCINFO
{
	DWORD dwPID;
	TCHAR szProcName[32];
} PROCINFO, *LPPROCINFO;

typedef struct _THREADINFO
{
	DWORD dwPID;
	TCHAR szProcName[32];

	DWORD dwThreadId;
	LPVOID lpStartAddress;
	DWORD dwThreadState;
	DWORD dwThreadWaitReason;
} THREADINFO, *LPTHREADINFO;

/////////////////////////////////////////////////////////////////////////////
// exports

__declspec(dllexport) BOOL WINAPI EnumProcessNames(LPPROCINFO lpProcInfo, DWORD cb, LPDWORD cbNeeded);
__declspec(dllexport) BOOL WINAPI EnumThreads(LPTHREADINFO lpThreadInfo, DWORD cb, LPDWORD cbNeeded);
__declspec(dllexport) BOOL WINAPI EnumProcessThreads(LPTHREADINFO lpThreadInfo, DWORD cb, LPDWORD cbNeeded);
__declspec(dllexport) BOOL WINAPI EnumProcessThreadsEx(DWORD dwPid, LPTHREADINFO lpThreadInfo, DWORD cb, LPDWORD cbNeeded);

/////////////////////////////////////////////////////////////////////////////
