#include "cpu.h"


//////////
//
// Constructor
//
/////
	CPU::CPU()
	{
		m_process.pid						= (HANDLE)-1;
		m_process.oldTime.dwLowDateTime		= 0;
		m_process.oldTime.dwHighDateTime	= 0;
		m_process.oldKernel.dwLowDateTime	= 0;
		m_process.oldKernel.dwHighDateTime	= 0;
		m_process.oldUser.dwLowDateTime		= 0;
		m_process.oldUser.dwHighDateTime	= 0;
		m_process.newTime.dwLowDateTime		= 0;
		m_process.newTime.dwHighDateTime	= 0;
		m_process.newKernel.dwLowDateTime	= 0;
		m_process.newKernel.dwHighDateTime	= 0;
		m_process.newUser.dwLowDateTime		= 0;
		m_process.newUser.dwHighDateTime	= 0;

		m_system.pid						= (HANDLE)-1;
		m_system.oldTime.dwLowDateTime		= 0;
		m_system.oldTime.dwHighDateTime		= 0;
		m_system.oldKernel.dwLowDateTime	= 0;
		m_system.oldKernel.dwHighDateTime	= 0;
		m_system.oldUser.dwLowDateTime		= 0;
		m_system.oldUser.dwHighDateTime		= 0;
		m_system.newTime.dwLowDateTime		= 0;
		m_system.newTime.dwHighDateTime		= 0;
		m_system.newKernel.dwLowDateTime	= 0;
		m_system.newKernel.dwHighDateTime	= 0;
		m_system.newUser.dwLowDateTime		= 0;
		m_system.newUser.dwHighDateTime		= 0;

		SYSTEM_INFO info;
		GetSystemInfo(&info);
		m_cpu_count = info.dwNumberOfProcessors;

		QueryPerformanceFrequency(&m_frequency);
	}


//////////
//
// Destructor
//
/////
	CPU::~CPU()
	{
	}


//////////
//
// Wait for the process to be at or below the specified percent threshold over the duration,
// with a timeout occuring at the interval specified
//
/////
	float CPU::WaitUntilIdle( HANDLE hProcess, float percent, int durationMilliseconds, int timeoutMilliseconds )
	{
		float sample, totAverage;
		int totSamples;
		LARGE_INTEGER start, now, belowCount;


		// Iterate until our time limit is reached, or until we have a successful number of iterations at or below the threshold
		belowCount.QuadPart		= 0;
		totSamples				= 0;
		totAverage				= 0.0;
		start.QuadPart			= QueryPerformanceCounterInMilliseconds();
		now						= start;
		belowCount				= start;
		while ( (now.QuadPart - start.QuadPart) < timeoutMilliseconds )
		{	// Wait for the specified range in milliseconds
			// Grab the CPU usage for the specified process
			sample = GetUsage( hProcess, durationMilliseconds );
			if (sample >= 0.0 && sample <= percent)
				return( sample );

			// Wasn't in the range, add it to our average in case we need to return a failure
			totAverage += sample;
			++totSamples;

			// Adjust the time to its current quantity
			now.QuadPart = QueryPerformanceCounterInMilliseconds();
		}
		return( totAverage / (float)totSamples);	// We've reached our limit for trying, indicate failure
	}


//////////
//
// Wait for the process to be at or below the specified percent threshold over the duration,
// with a timeout occuring at the interval specified
//
/////
	float CPU::WaitUntilSystemIdle( float percent, int durationMilliseconds, int timeoutMilliseconds )
	{
		float sample, totAverage;
		int totSamples;
		LARGE_INTEGER start, now, belowCount;


		// Iterate until our time limit is reached, or until we have a successful number of iterations at or below the threshold
		belowCount.QuadPart		= 0;
		totSamples				= 0;
		totAverage				= 0.0;
		start.QuadPart			= QueryPerformanceCounterInMilliseconds();
		now						= start;
		belowCount				= start;
		while ( (now.QuadPart - start.QuadPart) < timeoutMilliseconds )
		{	// Wait for the specified range in milliseconds
			// Grab the CPU usage for the specified process
			sample = GetSystemUsage( durationMilliseconds );
			if (sample >= 0.0 && sample <= percent)
				return( sample );

			// Wasn't in the range, add it to our average in case we need to return a failure
			totAverage += sample;
			++totSamples;

			// Adjust the time to its current quantity
			now.QuadPart = QueryPerformanceCounterInMilliseconds();
		}
		return( totAverage / (float)totSamples);	// We've reached our limit for trying, indicate failure
	}


	float CPU::GetUsage(HANDLE hProcess, int durationMilliseconds)
	{
		int i, result;
		float percent = 100.0;
		FILETIME time;
		FILETIME idleS, kernelS, userS;
		FILETIME kernelP, userP;
		FILETIME created, exited;
		CInstance* p;
		HANDLE hProcessToRead;

		hProcessToRead = OpenProcess(PROCESS_ALL_ACCESS, FALSE, (DWORD)hProcess);

		for (i = 0; i < 2; i++)
		{
			int result1 = GetProcessTimes( hProcessToRead, &created, &exited, &kernelP, &userP );

			GetSystemTimeAsFileTime( &time );
			result = GetSystemTimes( &idleS, &kernelS, &userS );

			// We need to compute the time
			p = &m_process;
			if (i == 1)
			{	// Second pass, compute the difference
				CopyNewToOld(p);
				CopyToNew(p, &time, null, &kernelP, &userP);
				percent = ComputeDelta(p) / (float)m_cpu_count;

			} else {
				// First pass, store the time for the next go around
				CopyToNew(p, &time, null, &kernelP, &userP);
				CopyToOld(p, &time, null, &kernelP, &userP);

			}

			if (i == 0)
				Sleep( durationMilliseconds );
		}
		CloseHandle(hProcessToRead);
		return(percent);
	}


	float CPU::GetSystemUsage(int durationMilliseconds)
	{
		int i, result;
		float percent = 100.0;
		FILETIME time;
		FILETIME idleS, kernelS, userS;
		CInstance* p;

		for (i = 0; i < 2; i++)
		{
			GetSystemTimeAsFileTime( &time );
			result = GetSystemTimes( &idleS, &kernelS, &userS );

			p = &m_system;
			if (i == 1)
			{	// There's a previous time here
				// Copy new to old, latest to new
				CopyNewToOld(p);
				CopyToNew(p, &time, &idleS, &kernelS, &userS);
				percent = ComputeDelta(p) / (float)m_cpu_count;

			} else {
				// Store the time for the next go around
				// Copy latest to old and new
				CopyToNew(p, &time, &idleS, &kernelS, &userS);
				CopyToOld(p, &time, &idleS, &kernelS, &userS);

			}

			if (i == 0)
				Sleep( durationMilliseconds );
		}
		return(percent);
	}


//////////
//
// Formula is:
//
//     percent = (100 * (kernel + user - idle)) / time
//
/////
	float CPU::ComputeDelta( CInstance* p )
	{
		unsigned long kernel, user, idle, time;

		time	= FileTimeDiff(&p->oldTime,		&p->newTime);
		kernel	= FileTimeDiff(&p->oldKernel,	&p->newKernel);
		user	= FileTimeDiff(&p->oldUser,		&p->newUser);
		idle	= FileTimeDiff(&p->oldIdle,		&p->newIdle);

		return((float)((double)100.0 * (double)(kernel + user - idle) / (double)time));
	}


	unsigned long CPU::FileTimeDiff(FILETIME* _old, FILETIME* _new)
	{
		ULARGE_INTEGER o, n;

		o.HighPart	= _old->dwHighDateTime;
		o.LowPart	= _old->dwLowDateTime;

		n.HighPart	= _new->dwHighDateTime;
		n.LowPart	= _new->dwLowDateTime;

		return((unsigned long)(n.QuadPart - o.QuadPart));
	}

	unsigned long CPU::QueryPerformanceCounterInMilliseconds( void )
	{
		LARGE_INTEGER now;

		QueryPerformanceCounter(&now);
		return ((unsigned long)(now.QuadPart * 1000 / m_frequency.QuadPart));
	}

	unsigned long CPU::QueryPerformanceCounterInMicroseconds( void )
	{
		LARGE_INTEGER now;

		QueryPerformanceCounter(&now);
		return ((unsigned long)(now.QuadPart * 1000000 / m_frequency.QuadPart));
	}

	void CPU::CopyNewToOld( CInstance* p )
	{
		p->oldTime.dwLowDateTime		= p->newTime.dwLowDateTime;
		p->oldTime.dwHighDateTime		= p->newTime.dwHighDateTime;
		p->oldIdle.dwLowDateTime		= p->newIdle.dwLowDateTime;
		p->oldIdle.dwHighDateTime		= p->newIdle.dwHighDateTime;
		p->oldKernel.dwLowDateTime		= p->newKernel.dwLowDateTime;
		p->oldKernel.dwHighDateTime		= p->newKernel.dwHighDateTime;
		p->oldUser.dwLowDateTime		= p->newUser.dwLowDateTime;
		p->oldUser.dwHighDateTime		= p->newUser.dwHighDateTime;
	}

	void CPU::CopyToNew( CInstance* p, FILETIME* time, FILETIME* idle, FILETIME* kernel, FILETIME* user)
	{
		if (time)
		{
			p->newTime.dwLowDateTime		= time->dwLowDateTime;
			p->newTime.dwHighDateTime		= time->dwHighDateTime;
		}
		if (idle)
		{
			p->newIdle.dwLowDateTime		= idle->dwLowDateTime;
			p->newIdle.dwHighDateTime		= idle->dwHighDateTime;
		}
		if (kernel)
		{
			p->newKernel.dwLowDateTime		= kernel->dwLowDateTime;
			p->newKernel.dwHighDateTime		= kernel->dwHighDateTime;
		}
		if (user)
		{
			p->newUser.dwLowDateTime		= user->dwLowDateTime;
			p->newUser.dwHighDateTime		= user->dwHighDateTime;
		}
	}

	void CPU::CopyToOld( CInstance* p, FILETIME* time, FILETIME* idle, FILETIME* kernel, FILETIME* user)
	{
		if (time)
		{
			p->oldTime.dwLowDateTime		= time->dwLowDateTime;
			p->oldTime.dwHighDateTime		= time->dwHighDateTime;
		}
		if (idle)
		{
			p->oldIdle.dwLowDateTime		= idle->dwLowDateTime;
			p->oldIdle.dwHighDateTime		= idle->dwHighDateTime;
		}
		if (kernel)
		{
			p->oldKernel.dwLowDateTime		= kernel->dwLowDateTime;
			p->oldKernel.dwHighDateTime		= kernel->dwHighDateTime;
		}
		if (user)
		{
			p->oldUser.dwLowDateTime		= user->dwLowDateTime;
			p->oldUser.dwHighDateTime		= user->dwHighDateTime;
		}
	}
