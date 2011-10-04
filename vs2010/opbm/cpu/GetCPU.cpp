// GetCPU.cpp : This app is used only as a test app, primarily to test
//              functions in the opbm_common.cpp/.h files, and modifications
//              made to the CPU class.
//
#include "cpu.h"
#include "..\common\opbm_common.h"
#include "..\common\opbm_common_extern.h"
#include "atlconv.h"
#include "shlobj.h"
#include "psapi.h"

void			spawnWorkerThreads(void);
DWORD WINAPI	computePi(LPVOID param);
unsigned long	QueryPerformanceCounterInMilliseconds( void );

char key[] = "HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System\\EnableLUA";

#include "..\common\opbm_common.cpp"

int _tmain(int argc, _TCHAR* argv[])
{

	//CPU cpu;
	//HANDLE hProcess;
	//float system;
	//
	//hProcess = (HANDLE)GetCurrentProcessId();
	//
	//spawnWorkerThreads();
	//
	//for( ; ; )
	//{
	//	system	= cpu.WaitUntilIdle( hProcess, 5, 250, 2000 );
	//	wprintf( L"System: %.3f\n", system );
	//}
	char* tptr = GetRegistryKeyValue(key);
	
	return 0;
}


void spawnWorkerThreads(void)
{
	HANDLE workerThread;
	ULONG workerThreadId;

	workerThread = CreateThread(0, 0, computePi, 0, 0, &workerThreadId);
}


LARGE_INTEGER frequency;
double gPercent;
unsigned long gMaximum;
bool gTickTock, flagInfo, flagPause;

#define _TICK false
#define _TOCK true

DWORD WINAPI computePi(LPVOID param)
{
	long compute, sleep, swaps;
	bool tickTock;
	unsigned long start, now, ilast;
	double iterate;

	// Copy/set our global variables
	tickTock	= gTickTock;
	gPercent	= 30.0;
	QueryPerformanceFrequency(&frequency);

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

	} while (1);
}

unsigned long QueryPerformanceCounterInMilliseconds( void )
{
	LARGE_INTEGER now;

	QueryPerformanceCounter(&now);
	return ((unsigned long)(now.QuadPart * 1000 / frequency.QuadPart));
}
