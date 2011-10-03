#pragma once
#ifndef UNICODE
	#define UNICODE
#endif
#ifndef _UNICODE
	#define _UNICODE
#endif

#pragma  warning( disable:4201) // nonstandard extension used : nameless struct/union
#pragma  warning( disable:4710) // warning C4710: function 'toto' not expanded

#include <crtdbg.h>
#include <windows.h>
#include <windowsx.h>
#include <tchar.h>
#include <iostream>
#include <stdio.h>
#include "psapi.h"
#include <winbase.h>
#include <wchar.h>

#ifndef null
#define null 0
#endif

class CInstance
{
public:
	CInstance() { };

	HANDLE		pid;

	FILETIME	oldTime;
	FILETIME	oldKernel;
	FILETIME	oldUser;
	FILETIME	oldIdle;

	FILETIME	newTime;
	FILETIME	newKernel;
	FILETIME	newUser;
	FILETIME	newIdle;
};


class CPU
{
public:
    CPU( void );
    ~CPU( void );
    
	// Wait until the current or specified process is idle
	float			WaitUntilIdle							( HANDLE hProcess,	float percent, int durationMilliseconds, int timeoutMilliseconds );
	float			WaitUntilSystemIdle						(					float percent, int durationMilliseconds, int timeoutMilliseconds );

	// Used internally, or can be used externally, returns usage of the the current or specified process
	float			GetUsage								( HANDLE hProcess, int durationMilliseconds );
    float			GetSystemUsage							( int durationMilliseconds );

	// Supportive methods
	float			ComputeDelta							( CInstance* p );
	unsigned long	FileTimeDiff							( FILETIME* old, FILETIME* _new );
	unsigned long	QueryPerformanceCounterInMilliseconds	( void );
	unsigned long	QueryPerformanceCounterInMicroseconds	( void );
	void			CopyNewToOld							( CInstance* p );
	void			CopyToNew								( CInstance* p, FILETIME* time, FILETIME* idle, FILETIME* kernel, FILETIME* user );
	void			CopyToOld								( CInstance* p, FILETIME* time, FILETIME* idle, FILETIME* kernel, FILETIME* user );

public:
	CInstance				m_process;
	CInstance				m_system;
	int						m_cpu_count;
	LARGE_INTEGER			m_frequency;
};
