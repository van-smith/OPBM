// cpuload.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "windows.h"

HANDLE	workerThread[32];
ULONG	workerThreadId[32];
int workerThreads;
LARGE_INTEGER frequency;
double gPercent;
unsigned long gMaximum;
bool gTickTock, flagInfo, flagPause;

#define _TICK false
#define _TOCK true

unsigned long QueryPerformanceCounterInMilliseconds( void );
DWORD WINAPI computePi(LPVOID param);

int main(int argc, char* argv[])
{
	int i;
	SYSTEM_INFO info;
	unsigned long appStart, appEnd, appDiff;
	DWORD_PTR affinity_mask;

	if (argc < 3)
	{
//		printf("Usage: cpuload %% duration [-info] [-pause]\n\n       %% = CPU load to task with, as in 20%%\nduration = milliseconds to maintain workload, as in 5000 for 5 seconds\n");
		exit(-1);
	}
	flagInfo	= (argc >= 4 && _memicmp(argv[3], "-info", 5) == 0);
	flagPause	= (argc >= 5 && _memicmp(argv[4], "-pause", 6) == 0);

	gMaximum	= atol(argv[2]);	// Maximum number of milliseconds
	gPercent	= atof(argv[1]);	// Number of ticks out of 100

	// Verify our target is within range
	if (gPercent > 100.0)
		gPercent = 100.0;

	GetSystemInfo(&info);
	workerThreads = info.dwNumberOfProcessors;
	QueryPerformanceFrequency(&frequency);
	if (flagInfo)
	{
//		printf("CPULOAD processing %u CPUs at %.0f%% for %u milliseconds\n", workerThreads, gPercent, gMaximum);
	}

	// Start out computing
	gTickTock	= _TOCK;

	// Create a thread for every CPU (to give a true system workload)
	// And assign affinity to each logically numbered CPU
	// And set the thread priority above normal
	appStart = QueryPerformanceCounterInMilliseconds();
	for (i = 0; i < workerThreads; i++)
	{
		workerThread[i] = CreateThread(0, 0, computePi, 0, 0, &workerThreadId[i]);
		affinity_mask = (DWORD_PTR)(1 << (i - 1));
		SetThreadAffinityMask(workerThread[i], affinity_mask);
//		SetThreadPriority(workerThread[i], THREAD_PRIORITY_ABOVE_NORMAL);
	}

	// Wait for threads to end
	DWORD exitCode;
	do {
		exitCode = !STILL_ACTIVE;
		for (i = 0; i < workerThreads; i++)
		{
			GetExitCodeThread(workerThread[i], &exitCode);
			if (exitCode == STILL_ACTIVE)
				break;	// Still going on at least this one, so continue on
		}

		if (exitCode == STILL_ACTIVE)
			Sleep(10);
		else
			break;

	} while (1);

	appEnd = QueryPerformanceCounterInMilliseconds();
	appDiff = appEnd - appStart;

	if (flagInfo)
	{
//		printf("CPULOAD processed for %u milliseconds\n", appDiff);
		if (flagPause)
		{
//			printf("...pausing 5 seconds before existing");
			Sleep(5000);
//			printf(", done.\n");
		}
	}

	return 0;
}

unsigned long QueryPerformanceCounterInMilliseconds( void )
{
	LARGE_INTEGER now;

	QueryPerformanceCounter(&now);
	return ((unsigned long)(now.QuadPart * 1000 / frequency.QuadPart));
}



DWORD WINAPI computePi(LPVOID param)
{
	long compute, sleep, swaps;
	bool tickTock;
	unsigned long start, now, ilast;
	double iterate;

	// Copy our global variables
	tickTock	= gTickTock;

	// Begin ... NOW!
	start		= QueryPerformanceCounterInMilliseconds();
	ilast		= start;
	compute		= 0;
	sleep		= 0;
	swaps		= 0;
	do {
		now = QueryPerformanceCounterInMilliseconds();
		iterate = (double)(now - ilast);

		// _TICK = sleeping
		// _TOCK = computing
		if (tickTock == _TOCK)
		{	// We are computing
			if (iterate >= gPercent)
			{	// But we've just switched over to sleeping
				compute += (long)iterate;
				++swaps;
				tickTock = _TICK;
				ilast = (unsigned long)now;

			} else {
				// Compute something, one pass through our "ComputePi" function
				// Set the initial values of the polynomials, and the differences
				double dP0 = 5.0;
				double dP1 = 41.0;
				double dP2 = 40.0;
				double dQ0 = 3.0;
				double dQ1 = 102.0;
				double dQ2 = 288.0;
				double dQ3 = 192.0;

				// Perform a few iterations of the formula.
				double dPi = 0.0, d = 1.0;	// d is 4^k
				for (int k = 0; k < 12; k++)
				{
					// Perform the even, addition step.
					dPi = dPi + (dP0 / dQ0 / d);
					dP0 = dP0 + dP1;
					dP1 = dP1 + dP2;
					dQ0 = dQ0 + dQ1;
					dQ1 = dQ1 + dQ2;
					dQ2 = dQ2 + dQ3;
					d = d * 4.0;

					// Perform the odd, subtraction step.
					dPi = dPi - (dP0 / dQ0 / d);
					dP0 = dP0 + dP1;
					dP1 = dP1 + dP2;
					dQ0 = dQ0 + dQ1;
					dQ1 = dQ1 + dQ2;
					dQ2 = dQ2 + dQ3;
					d = d * 4.0;
				}
				// Display the result.
				dPi = 2.0 * dPi;
			}

		} else {
			// We are sleeping
			if (iterate >= 100.0 - gPercent)
			{	// But we've just switched over to computing
				sleep += (long)iterate;
				++swaps;
				tickTock = _TOCK;
				ilast = (unsigned long)now;

			} else {
				Sleep(1);

			}
		}

	} while (now - start < gMaximum);

//	if (flagInfo)
//		printf("%.f%% Thread terminated %u sleeps, %u computes\n", gPercent, sleep / (swaps / 2), compute / (swaps / 2));

	ExitThread(0);
}