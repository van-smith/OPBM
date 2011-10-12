
// Version 0.2

#include "stdafx.h"
#include <pdh.h>
#include <pdhmsg.h>
#include "PdhUtil.h"


/////////////////////////////////////////////////////////////////////////////

// 1 Megabyte
#define MB1										0x100000

// thread counter types
#define TC_PID									0
#define TC_TID									1
#define TC_STARTADDRESS							2
#define TC_THREADSTATE							3
#define TC_THREADWAITREASON						4

#define NCOUNTERTYPES							5

#define SZTOTAL									"_Total"

/////////////////////////////////////////////////////////////////////////////
// types

typedef struct _TRHEADCOUNTER
{
	PDH_HCOUNTER hCounter;
	TCHAR szCounter[256];
	DWORD dwPID;
	TCHAR szProcName[32];
} THREADCOUNTER, *LPTHREADCOUNTER;

/////////////////////////////////////////////////////////////////////////////
// globals

HQUERY ghQuery;

LPPROCINFO galpProcInfo = NULL;
DWORD gdwProcInfo = 0;

LPTHREADINFO galpThreadInfo = NULL;
DWORD gdwThreadInfo = 0;

CStringList glsThreadCounters;
LPTHREADCOUNTER galpThreadCounters = NULL;
DWORD gdwThreadCounters = 0;

// this is the filter for which process to enumerate
// -1 means all
DWORD gdwCurrentProcessId = 0xffffffff;

// *** order must be the same as the TC_ definitions ***
LPCTSTR gaszCounters[] = {  _T("ID Process"),
							_T("ID Thread"),
							_T("Start Address"),
							_T("Thread State"),
							_T("Thread Wait Reason")  };

/////////////////////////////////////////////////////////////////////////////
// prototypes

void InitGlobals();
DWORD EnumAllProcesses();

UINT GetCounterIndex(LPCTSTR szCounter);
CString GetProcessNameFromCounter(CString sCounter);
BOOL GetThreadCounters(LPCTSTR *lpszCounter, DWORD dwCounters);
void GetCounterName(CString sCounterString, LPTSTR szCounter);
BOOL GetProcInfo(LPTHREADCOUNTER lpThreadCounter, CString sCounterInfo);
BOOL EnumThreadsByCounter(LPCTSTR *lpszCounter, DWORD dwCounters, LPTHREADINFO lpThreadInfo);

/////////////////////////////////////////////////////////////////////////////
// public

__declspec(dllexport) BOOL WINAPI EnumProcessNames(LPPROCINFO lpProcInfo, DWORD cb, LPDWORD cbNeeded)
{
	DWORD dw;




	if ((lpProcInfo == NULL) || (cb == 0) || (cbNeeded == NULL))
	{
		return FALSE;
	}


	InitGlobals();

	dw = EnumAllProcesses();

	if (dw == 0xffffffff)
	{
		InitGlobals();

		return FALSE;
	}

	if (cb < gdwProcInfo)
	{
		*cbNeeded = gdwProcInfo;
		InitGlobals();

		return FALSE;
	}

	// this is impossible, but anyway...
	if (gdwProcInfo == 0)
	{
		*cbNeeded = 0;
		InitGlobals();

		return FALSE;
	}

	// copy them
	memcpy((LPPROCINFO)lpProcInfo, (LPPROCINFO)galpProcInfo, gdwProcInfo * (sizeof(PROCINFO)));
	*cbNeeded = gdwProcInfo;

	InitGlobals();


	return TRUE;

}

__declspec(dllexport) BOOL WINAPI EnumThreads(LPTHREADINFO lpThreadInfo, DWORD cb, LPDWORD cbNeeded)
{
	BOOL bRes = TRUE;





	if ((lpThreadInfo == NULL) || (cb == 0) || (cbNeeded == NULL))
	{
		return FALSE;
	}


	// initialize globals
	InitGlobals();

	// get all unique processes
	EnumAllProcesses();

	// enumerate the threads
	if (!GetThreadCounters(gaszCounters, NCOUNTERTYPES))
	{
		bRes = FALSE;

		goto Leave;
	}

	if (cb < gdwThreadInfo)
	{
		*cbNeeded = gdwThreadInfo;
		bRes = FALSE;

		goto Leave;
	}

	// this is impossible, but anyway...
	if (gdwThreadInfo == 0)
	{
		*cbNeeded = 0;
		bRes = FALSE;

		goto Leave;
	}


	// get all the info
	if (!EnumThreadsByCounter(gaszCounters, NCOUNTERTYPES, lpThreadInfo))
	{
		bRes = FALSE;

		goto Leave;
	}


Leave:
	// *** must do this before initializing the globals!!! ***
	// return the number of thread info items
	*cbNeeded = gdwThreadInfo;

	// release globals
	InitGlobals();
	PdhCloseQuery(ghQuery);



	return bRes;

}

__declspec(dllexport) BOOL WINAPI EnumProcessThreads(LPTHREADINFO lpThreadInfo, DWORD cb, LPDWORD cbNeeded)
{
	return EnumProcessThreadsEx(GetCurrentProcessId(), lpThreadInfo, cb, cbNeeded);
}

__declspec(dllexport) BOOL WINAPI EnumProcessThreadsEx(DWORD dwPid, LPTHREADINFO lpThreadInfo, DWORD cb, LPDWORD cbNeeded)
{
	BOOL bRes = TRUE;
	DWORD i, j;





	if ((lpThreadInfo == NULL) || (cb == 0) || (cbNeeded == NULL))
	{
		return FALSE;
	}


	// initialize globals
	InitGlobals();

	// the caller can specify -1 for current process 
	if (dwPid == 0xffffffff)
	{
		gdwCurrentProcessId = GetCurrentProcessId();
	}

	gdwCurrentProcessId = dwPid;

	// get all unique processes
	EnumAllProcesses();


	// enumerate the threads
	if (!GetThreadCounters(gaszCounters, NCOUNTERTYPES))
	{
		bRes = FALSE;

		goto Leave;
	}

	// this is impossible, but anyway...
	if (gdwThreadInfo == 0)
	{
		*cbNeeded = 0;
		bRes = FALSE;

		goto Leave;
	}


	// get all the info
	if (!EnumThreadsByCounter(gaszCounters, NCOUNTERTYPES, lpThreadInfo))
	{
		bRes = FALSE;

		goto Leave;
	}


	// make a copy
	if (galpThreadInfo != NULL)
	{
		delete[] galpThreadInfo;
	}

	galpThreadInfo = new THREADINFO[gdwThreadInfo];

	if (galpThreadInfo == NULL)
	{
		bRes = FALSE;

		goto Leave;
	}

	memcpy((LPTHREADINFO)galpThreadInfo, (LPTHREADINFO)lpThreadInfo, (gdwThreadInfo * sizeof(THREADINFO)));

	// filter now
	for (i = 0, j = 0; i < gdwThreadInfo; i++)
	{
		if (galpThreadInfo[i].dwPID != gdwCurrentProcessId)
		{
			continue;
		}

		j++;
	}	// end for

	if (cb < j)
	{
		*cbNeeded = j;
		bRes = FALSE;

		goto Leave;
	}

	// *** must do this before initializing the globals!!! ***
	// return the number of thread info items
	*cbNeeded = j;

	// copy them now
	memset(lpThreadInfo, 0, (cb * sizeof(THREADINFO)));

	for (i = 0, j = 0; i < gdwThreadInfo; i++)
	{
		if (galpThreadInfo[i].dwPID == gdwCurrentProcessId)
		{
			memcpy(&lpThreadInfo[j++], &galpThreadInfo[i], sizeof(THREADINFO));
		}
	}	// end for


Leave:
	// release globals
	InitGlobals();
	PdhCloseQuery(ghQuery);


	return bRes;

}

/////////////////////////////////////////////////////////////////////////////
// private

DWORD EnumAllProcesses()
{
	HQUERY hQuery;
	PDH_STATUS pdhRes;
	LPVOID lpv;
	LPTSTR lpsz;
	int n;
	DWORD dw, dwCounters, dw2;
	DWORD dwDetail = PERF_DETAIL_WIZARD;
	PDH_HCOUNTER *ahCounters = NULL;
	CStringList lsProcessCounters;
	POSITION pos;
	CString sbuff, stemp, sKey;
	CString *asbuff = NULL;
    PDH_FMT_COUNTERVALUE pdhFormattedValue;
	CMapStringToString mProcess;
	
	





	dw = 0;
	pdhRes = PdhOpenQuery(NULL, (DWORD_PTR)&dw, &hQuery);

	if (pdhRes != ERROR_SUCCESS)
	{
		return 0xffffffff;
	}

	
	dw = MB1;

	///////////////////////
	// get all processes //
	///////////////////////
	while (1)
	{
		lpv = HeapAlloc(GetProcessHeap(), 0, dw);

		if (lpv == NULL)
		{
			return 0xffffffff;
		}

		lpsz = (LPTSTR)lpv;
		memset(lpsz, 0, dw);

		pdhRes = PdhExpandCounterPath("\\Process(*)\\ID Process", lpsz, &dw);

		if (pdhRes != PDH_CSTATUS_VALID_DATA)
		{
			if (pdhRes == PDH_MORE_DATA)
			{
				HeapFree(GetProcessHeap(), 0, lpv);
				lpv = NULL;
				// *** dw now has the required length ***
			}
		}
		else
		{
			break;
		}
	}	// end while

	TRACE("\nProcesses\n");
	dwCounters = 0;

	// get the counters
	while (1)
	{
		n = lstrlen(lpsz);

		if (n == 0)
		{
			break;
		}

		dwCounters++;
		TRACE("   [%u] : %s\n", dwCounters, lpsz);
		lsProcessCounters.AddTail((LPCTSTR)lpsz);
		lpsz += (n + 1);
	}	// end while


	// allocate
	ahCounters = new PDH_HCOUNTER[dwCounters];
	asbuff = new CString[dwCounters];

	// global storage
	if (galpProcInfo != NULL)
	{
		delete[] galpProcInfo;
	}

	galpProcInfo = new PROCINFO[dwCounters];

	if ((ahCounters == NULL) || (asbuff == NULL) || (galpProcInfo == NULL))
	{
		goto Leave;
	}

	memset(ahCounters, 0, dwCounters);
	memset(galpProcInfo, 0, dwCounters);

	TRACE("\n%u Counters\n", dwCounters);

	for (pos = lsProcessCounters.GetHeadPosition(), dw2 = 0; pos != NULL; dw2++)
	{
		sbuff = lsProcessCounters.GetNext(pos);
		asbuff[dw2] = sbuff;

		if (PdhAddCounter(hQuery, (LPCTSTR)sbuff, 0, &ahCounters[dw2]) != ERROR_SUCCESS)
		{
			TRACE("   Failed to add counter for %s\n", (LPCTSTR)sbuff);

			continue;
		}

		TRACE("   %s : 0x%x\n", (LPCTSTR)sbuff, ahCounters[dw2]);
	}	// end for


	if (PdhCollectQueryData(hQuery) != ERROR_SUCCESS)
	{
		TRACE("\nFailed to collect query data\n");
	}


	// get values
	mProcess.RemoveAll();
	TRACE("\nProcess ID's\n");

	for (dw = 0, gdwProcInfo = 0; dw < dwCounters; dw++)
	{
		if (PdhGetFormattedCounterValue(ahCounters[dw], PDH_FMT_LONG, NULL, &pdhFormattedValue) != ERROR_SUCCESS)
		{
			TRACE("   Failed to get formatted counter value for %s : 0x%x\n", (LPCTSTR)asbuff[dw], ahCounters[dw]);

			continue;
		}

		// I don't know why it returns more counters than there are processes,
		// so I'll have to weed them out.
		// just to be sure
		sbuff = GetProcessNameFromCounter(asbuff[dw]);
		sKey.Format("%s:%u", sbuff, pdhFormattedValue.longValue);

		if (mProcess.Lookup((LPCTSTR)sKey, stemp))
		{
			// exists so skip
			continue;
		}

		// *** ignore the total ***
		if (!sbuff.CompareNoCase(SZTOTAL))
		{
			continue;
		}

		mProcess[sKey] = sKey;

		// store the PID's
		galpProcInfo[gdwProcInfo].dwPID = pdhFormattedValue.longValue;

		if (sbuff.GetLength() < sizeof(galpProcInfo[dw].szProcName))
		{
			lstrcpy(galpProcInfo[gdwProcInfo].szProcName, (LPCTSTR)sbuff);
		}
		else
		{
			// say something
			OutputDebugString("EnumAllProcesses() : PROCINFO.szProcName size too small!\n");
		}

		// increment now!
		gdwProcInfo++;

		TRACE("   %s[0x%x] : 0x%x %u\n", (LPCTSTR)sbuff, ahCounters[dw], pdhFormattedValue.longValue, pdhFormattedValue.longValue);
	}	// end for


Leave:
	delete[] ahCounters;
	delete[] asbuff;

	HeapFree(GetProcessHeap(), 0, lpv);
	PdhCloseQuery(hQuery);


	return gdwProcInfo;

}

BOOL GetThreadCounters(LPCTSTR *lpszCounter, DWORD dwCounters)
{
	PDH_STATUS pdhRes;
	LPVOID lpv = NULL;
	LPTSTR lpsz;
	int n;
	DWORD i, dw, dw2;
	DWORD dwDetail = PERF_DETAIL_WIZARD;
	POSITION pos;
	CString sbuff;
	BOOL bRes = TRUE;
	TCHAR szCounter[256];
	LPVOID *alpvCounters = NULL;
	
	
	



	
	if ((lpszCounter == NULL) || (dwCounters == 0))
	{
		return FALSE;
	}

	dw = 0;
	pdhRes = PdhOpenQuery(NULL, (DWORD_PTR)&dw, &ghQuery);

	if (pdhRes != ERROR_SUCCESS)
	{
		return FALSE;
	}

	
	/////////////////////
	// get all threads //
	/////////////////////
	alpvCounters = new LPVOID[dwCounters];

	if (alpvCounters == NULL)
	{
		return FALSE;
	}

	for (i = 0; i < dwCounters; i++)
	{
		dw = MB1;

		while (1)
		{
			lpv = HeapAlloc(GetProcessHeap(), 0, dw);

			if (lpv == NULL)
			{
				return FALSE;
			}

			// store the pointer
			((LPDWORD)alpvCounters)[i] = (DWORD)lpv;
			lpsz = (LPTSTR)lpv;
			memset(lpsz, 0, dw);
			sprintf_s(szCounter, sizeof(szCounter), "\\Thread(*/*#*)\\%s", lpszCounter[i]);
			pdhRes = PdhExpandCounterPath((LPCTSTR)szCounter, lpsz, &dw);

			if (pdhRes != PDH_CSTATUS_VALID_DATA)
			{
				if (pdhRes == PDH_MORE_DATA)
				{
					HeapFree(GetProcessHeap(), 0, lpv);
					lpv = NULL;
					// *** dw now has the required length ***
				}
				else
				{
					// *** don't know what else to do ***
					bRes = FALSE;

					goto Leave;
				}
			}
			else
			{
				break;
			}
		}	// end while
	}	// end for


	//TRACE("\nThreads\n");
	gdwThreadCounters = 0;
	glsThreadCounters.RemoveAll();

	for (i = 0, dw = 0; i < dwCounters; i++)
	{
		lpsz = (LPTSTR)alpvCounters[i];
		TRACE("%s\n", lpszCounter[i]);

		while (1)
		{
			n = lstrlen(lpsz);

			if (n == 0)
			{
				break;
			}

			TRACE("   [%u] : %s\n", dw++, lpsz);

			// *** ignore total ***
			sbuff = GetProcessNameFromCounter((CString)lpsz);

			if (!sbuff.CompareNoCase(SZTOTAL))
			{
				TRACE("Ignoring %s\n", lpsz);
			}
			else
			{
				gdwThreadCounters++;
				glsThreadCounters.AddTail((LPCTSTR)lpsz);
			}

			lpsz += (n + 1);
		}	// end while
	}	// end for


	// divide by the number of types of counters
	// if it doesn't even out, then the number of threads
	// changed during the operation therefore buggering
	// this function
	if (gdwThreadCounters % NCOUNTERTYPES)
	{
		// there is remainder
		bRes = FALSE;

		goto Leave;
	}


	// store it globally
	// divide by the number of counters
	gdwThreadInfo = gdwThreadCounters / NCOUNTERTYPES;


	// allocate
	if (galpThreadCounters != NULL)
	{
		delete[] galpThreadCounters;
	}

	galpThreadCounters = new THREADCOUNTER[gdwThreadCounters];

	if (gdwThreadCounters == NULL)
	{
		bRes = FALSE;
		goto Leave;
	}

	memset(galpThreadCounters, 0, gdwThreadCounters);

	if (galpThreadCounters == NULL)
	{
		bRes = FALSE;
		goto Leave;
	}


	TRACE("\n%u Counters\n", gdwThreadCounters);

	for (pos = glsThreadCounters.GetHeadPosition(), dw2 = 0; pos != NULL; dw2++)
	{
		sbuff = glsThreadCounters.GetNext(pos);

		// save the counter name
		GetCounterName(sbuff, galpThreadCounters[dw2].szCounter);

		if (PdhAddCounter(ghQuery, (LPCTSTR)sbuff, 0, &(galpThreadCounters[dw2].hCounter)) != ERROR_SUCCESS)
		{
			TRACE("   Failed to add counter for %s\n", (LPCTSTR)sbuff);

			continue;
		}

		// save info
		if (!GetProcInfo(&galpThreadCounters[dw2], sbuff))
		{
			TRACE("Failed to extract process info for %s\n", (LPCTSTR)sbuff);

			continue;
		}

		//TRACE("   %s : 0x%x\n", (LPCTSTR)sbuff, galpThreadCounters[dw2].hCounter);
	}	// end for


Leave:
	delete[] alpvCounters;

	HeapFree(GetProcessHeap(), 0, lpv);


	return bRes;

}

BOOL EnumThreadsByCounter(LPCTSTR *lpszCounter, DWORD dwCounters, LPTHREADINFO lpThreadInfo)
{
	DWORD i, j;
	CString sbuff;
    PDH_FMT_COUNTERVALUE pdhFormattedValue;
	BOOL bRes = TRUE;
	UINT uCounter;


	



	if ((lpszCounter == NULL) || (dwCounters == 0) || (lpThreadInfo == NULL))
	{
		return FALSE;
	}

	j = 0;
	
	if (PdhCollectQueryData(ghQuery) != ERROR_SUCCESS)
	{
		TRACE("\nFailed to collect query data\n");
	}


	// get values
	// *** all these acrobatics are necessary to be safe. it'll be slower, but it's safer ***
	// first, get all the pids
	for (i = 0, j = 0; i < gdwThreadCounters; i++)
	{
		if (strcmp(galpThreadCounters[i].szCounter, gaszCounters[TC_PID]))
		{
			continue;
		}

		if (PdhGetFormattedCounterValue(galpThreadCounters[i].hCounter, PDH_FMT_LONG, NULL, &pdhFormattedValue) != ERROR_SUCCESS)
		{
			printf("   Failed to get formatted counter value for %s : 0x%x\n", (LPCTSTR)sbuff, galpThreadCounters[j].hCounter);
			j++;

			continue;
		}

		// it's a PID
		lpThreadInfo[j++].dwPID = pdhFormattedValue.longValue;
		TRACE(" lpThreadInfo[%u].dwPID : 0x%x %u\n", (j - 1), lpThreadInfo[j - 1].dwPID, lpThreadInfo[j - 1].dwPID);
	}	// end for

	// make sure the thread counters are enumerated right
	ASSERT(j == gdwThreadInfo);


	for (i = 0, j = 0; i < gdwThreadCounters; i++)
	{
		// they were put in sequentially by thread followed by counter type
		// therefore, we can offset by the number of thread info's (threads)
		if (PdhGetFormattedCounterValue(galpThreadCounters[i].hCounter, PDH_FMT_LONG, NULL, &pdhFormattedValue) != ERROR_SUCCESS)
		{
			printf("   Failed to get formatted counter value for %s : 0x%x\n", (LPCTSTR)sbuff, galpThreadCounters[i].hCounter);

			continue;
		}

		uCounter = GetCounterIndex(galpThreadCounters[i].szCounter);

		if (uCounter == 0xffffffff)
		{
			// something's very wrong with the code
			// actually, this shouldn't happen unless I made a mistake with the code
			return FALSE;
		}

		switch (uCounter)
		{
			case TC_PID :
				// skip pid
				//lpThreadInfo[j].dwPID = pdhFormattedValue.longValue;
				lstrcpy(lpThreadInfo[j].szProcName, galpThreadCounters[j].szProcName);
				TRACE("lpThreadInfo[j].szProcName : %s\n", lpThreadInfo[j].szProcName);
				break;

			case TC_TID :
				lpThreadInfo[j].dwThreadId = pdhFormattedValue.longValue;
				TRACE("lpThreadInfo[j].dwThreadId : 0x%x %u\n", lpThreadInfo[j].dwThreadId, lpThreadInfo[j].dwThreadId);
				break;

			case TC_STARTADDRESS :
				lpThreadInfo[j].lpStartAddress = (LPVOID)pdhFormattedValue.longValue;
				TRACE("lpThreadInfo[j].lpStartAddress : 0x%x\n", lpThreadInfo[j].lpStartAddress);
				break;

			case TC_THREADSTATE :
				lpThreadInfo[j].dwThreadState = pdhFormattedValue.longValue;
				TRACE("lpThreadInfo[j].dwThreadState : 0x%x %u\n", lpThreadInfo[j].dwThreadState, lpThreadInfo[j].dwThreadState);
				break;

			case TC_THREADWAITREASON :
				lpThreadInfo[j].dwThreadWaitReason = pdhFormattedValue.longValue;
				TRACE("lpThreadInfo[j].dwThreadWaitReason : 0x%x %u\n", lpThreadInfo[j].dwThreadWaitReason, lpThreadInfo[j].dwThreadWaitReason);
				break;
		}	// end switch

		// now increment the number of counters processed
		j++;

		if (j >= gdwThreadInfo)
		{
			// move to next node of info
			j = 0;
		}

		//TRACE("   %s[0x%x] : 0x%x %u\n", (LPCTSTR)galpThreadCounters[i].szProcName, galpThreadCounters[i].hCounter, pdhFormattedValue.longValue, pdhFormattedValue.longValue);
	}	// end for


	return bRes;

}

/////////////////////////////////////////////////////////////////////////////
// helpers

void InitGlobals()
{
	gdwCurrentProcessId = 0xffffffff;

	if (galpProcInfo != NULL)
	{
		delete[] galpProcInfo;
		galpProcInfo = NULL;
	}

	gdwProcInfo = 0;

	if (galpThreadInfo != NULL)
	{
		delete[] galpThreadInfo;
		galpThreadInfo = NULL;
	}

	gdwThreadInfo = 0;

	glsThreadCounters.RemoveAll();

	if (galpThreadCounters != NULL)
	{
		delete[] galpThreadCounters;
		galpThreadCounters = NULL;
	}

	gdwThreadCounters = 0;

}

CString GetProcessNameFromCounter(CString sCounter)
{
	CString s;
	int n1, n2, n3, n4;



	s.Empty();
	n1 = sCounter.Find(_T('('));

	if (n1 < 0)
	{
		return s;
	}

	n2 = sCounter.Find(_T('/'));
	n3 = sCounter.Find(_T(')'));

	if (n2 < 0)
	{
		if (n3 < 0)
		{
			return s;
		}

		n4 = n3;
	}
	else
	{
		n4 = n2;
	}

	s = sCounter.Mid(n1 + 1, n4 - n1 - 1);


	return s;

}

BOOL GetProcInfo(LPTHREADCOUNTER lpThreadCounter, CString sCounterInfo)
{
	CString s;
	int n1, n2;

	
	
	
	if ((lpThreadCounter == NULL) || (!sCounterInfo.GetLength()))
	{
		return FALSE;
	}


	// get the name
	s.Empty();
	n1 = sCounterInfo.Find(_T('('));

	if (n1 < 0)
	{
		return FALSE;
	}

	n2 = sCounterInfo.Find(_T('/'));

	if (n2 < 0)
	{
		return FALSE;
	}

	s = sCounterInfo.Mid(n1 + 1, n2 - n1 - 1);

	if (s.GetLength() > sizeof(lpThreadCounter->szProcName))
	{
		// *** if this happens, increase the size in the struct ***
		// it shouldn't as the names are supposed to be no more than 16 in lenght
		OutputDebugString("lpThreadCounter->szProcName too small!\n");

		return FALSE;
	}

	lstrcpy(lpThreadCounter->szProcName, (LPCTSTR)s);


	// find the pid
	// I know this sucks, but it's small, so I'll live with the overhead
	// of this over a map
	for (DWORD i = 0; i < gdwProcInfo; i++)
	{
		if (!s.CompareNoCase(galpProcInfo[i].szProcName))
		{
			// found it
			lpThreadCounter->dwPID = galpProcInfo[i].dwPID;

			break;
		}
	}	// end for


	return TRUE;

}

void GetCounterName(CString sCounterString, LPTSTR szCounter)
{
	if ((!sCounterString.GetLength()) || (szCounter == NULL))
	{
		return;
	}

	int n = sCounterString.ReverseFind('\\');

	if (n < 0)
	{
		return;
	}

	CString s = sCounterString.Right(sCounterString.GetLength() - n - 1);

	lstrcpy(szCounter, (LPCTSTR)s);

}

UINT GetCounterIndex(LPCTSTR szCounter)
{
	for (UINT i = 0; i < NCOUNTERTYPES; i++)
	{
		if (!lstrcmp(gaszCounters[i], szCounter))
		{
			// found it
			return i;
		}
	}	// end for


	return 0xffffffff;

}
