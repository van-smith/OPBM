// EnumThreads.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "EnumThreads.h"
#include "PdhUtil.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// The one and only application object

CWinApp theApp;

using namespace std;

/////////////////////////////////////////////////////////////////////////////

void PdhTest()
{
	THREADINFO aThreadInfo[512];
	DWORD dw, i;
	FILE *fd;




	DeleteFile("PdhTest.txt");
	fopen_s(&fd, "PdhTest.txt", "a+");

	if (!EnumThreads(&aThreadInfo[0], 512, &dw))
	{
		printf("Failed to enumerate all threads\n");
	}
	else
	{
		// succeeded
		printf("\nAll Threads\n");
		fprintf(fd, "\nAll Threads\n");

		for (i = 0; i < dw; i++)
		{
			printf("aThreadInfo[%u].dwPid : 0x%x %u\n", i, aThreadInfo[i].dwPID, aThreadInfo[i].dwPID);
			printf("aThreadInfo[%u].szProcName : %s\n", i, aThreadInfo[i].szProcName);
			printf("aThreadInfo[%u].dwThreadId : 0x%x %u\n", i, aThreadInfo[i].dwThreadId, aThreadInfo[i].dwThreadId);
			printf("aThreadInfo[%u].lpStartAddress : 0x%x %u\n", i, (DWORD)aThreadInfo[i].lpStartAddress, (DWORD)aThreadInfo[i].lpStartAddress);
			printf("aThreadInfo[%u].dwThreadState : 0x%x %u\n", i, aThreadInfo[i].dwThreadState, aThreadInfo[i].dwThreadState);
			printf("aThreadInfo[%u].dwThreadWaitReason : 0x%x %u\n\n", i, aThreadInfo[i].dwThreadWaitReason, aThreadInfo[i].dwThreadWaitReason);

			if (fd != NULL)
			{
				fprintf(fd, "aThreadInfo[%u].dwPid : 0x%x %u\n", i, aThreadInfo[i].dwPID, aThreadInfo[i].dwPID);
				fprintf(fd, "aThreadInfo[%u].szProcName : %s\n", i, aThreadInfo[i].szProcName);
				fprintf(fd, "aThreadInfo[%u].dwThreadId : 0x%x %u\n", i, aThreadInfo[i].dwThreadId, aThreadInfo[i].dwThreadId);
				fprintf(fd, "aThreadInfo[%u].lpStartAddress : 0x%x %u\n", i, (DWORD)aThreadInfo[i].lpStartAddress, (DWORD)aThreadInfo[i].lpStartAddress);
				fprintf(fd, "aThreadInfo[%u].dwThreadState : 0x%x %u\n", i, aThreadInfo[i].dwThreadState, aThreadInfo[i].dwThreadState);
				fprintf(fd, "aThreadInfo[%u].dwThreadWaitReason : 0x%x %u\n\n", i, aThreadInfo[i].dwThreadWaitReason, aThreadInfo[i].dwThreadWaitReason);
			}
		}
	}



	PROCINFO ProcInfo[256];

	if (!EnumProcessNames(&ProcInfo[0], 256, &dw))
	{
		printf("Failed to enumerate process names\n");
	}
	else
	{
		// succeeded
		printf("\nProcess Names\n");
		fprintf(fd, "\nProcess Names\n");

		for (i = 0; i < dw; i++)
		{
			printf("ProcInfo[%u].dwPID : 0x%x %u\n", i, ProcInfo[i].dwPID, ProcInfo[i].dwPID);
			printf("ProcInfo[%u].szProcName : %s\n", i, ProcInfo[i].szProcName);

			if (fd != NULL)
			{
				fprintf(fd, "ProcInfo[%u].dwPID : 0x%x %u\n", i, ProcInfo[i].dwPID, ProcInfo[i].dwPID);
				fprintf(fd, "ProcInfo[%u].szProcName : %s\n", i, ProcInfo[i].szProcName);
			}
		}
	}


	if (!EnumProcessThreads(&aThreadInfo[0], 512, &dw))
	{
		printf("Failed to enumerate process threads\n");
	}
	else
	{
		// succeeded
		printf("\nThis Process's Treads\n");
		fprintf(fd, "\nThis Process's Treads\n");

		for (i = 0; i < dw; i++)
		{
			printf("aThreadInfo[%u].dwPid : 0x%x %u\n", i, aThreadInfo[i].dwPID, aThreadInfo[i].dwPID);
			printf("aThreadInfo[%u].szProcName : %s\n", i, aThreadInfo[i].szProcName);
			printf("aThreadInfo[%u].dwThreadId : 0x%x %u\n", i, aThreadInfo[i].dwThreadId, aThreadInfo[i].dwThreadId);
			printf("aThreadInfo[%u].lpStartAddress : 0x%x %u\n", i, (DWORD)aThreadInfo[i].lpStartAddress, (DWORD)aThreadInfo[i].lpStartAddress);
			printf("aThreadInfo[%u].dwThreadState : 0x%x %u\n", i, aThreadInfo[i].dwThreadState, aThreadInfo[i].dwThreadState);
			printf("aThreadInfo[%u].dwThreadWaitReason : 0x%x %u\n\n", i, aThreadInfo[i].dwThreadWaitReason, aThreadInfo[i].dwThreadWaitReason);

			if (fd != NULL)
			{
				fprintf(fd, "aThreadInfo[%u].dwPid : 0x%x %u\n", i, aThreadInfo[i].dwPID, aThreadInfo[i].dwPID);
				fprintf(fd, "aThreadInfo[%u].szProcName : %s\n", i, aThreadInfo[i].szProcName);
				fprintf(fd, "aThreadInfo[%u].dwThreadId : 0x%x %u\n", i, aThreadInfo[i].dwThreadId, aThreadInfo[i].dwThreadId);
				fprintf(fd, "aThreadInfo[%u].lpStartAddress : 0x%x %u\n", i, (DWORD)aThreadInfo[i].lpStartAddress, (DWORD)aThreadInfo[i].lpStartAddress);
				fprintf(fd, "aThreadInfo[%u].dwThreadState : 0x%x %u\n", i, aThreadInfo[i].dwThreadState, aThreadInfo[i].dwThreadState);
				fprintf(fd, "aThreadInfo[%u].dwThreadWaitReason : 0x%x %u\n\n", i, aThreadInfo[i].dwThreadWaitReason, aThreadInfo[i].dwThreadWaitReason);
			}
		}
	}


	// on WinNT 4.0, the System pid is 2
	// on Win2000, the System ppid is 8
	if (!EnumProcessThreadsEx(2, &aThreadInfo[0], 512, &dw))
	{
		printf("Failed to enumerate process threads\n");
	}
	else
	{
		// succeeded
		printf("\nSystem Process's Threads\n");
		fprintf(fd, "\nSystem Process's Threads\n");

		for (i = 0; i < dw; i++)
		{
			printf("aThreadInfo[%u].dwPid : 0x%x %u\n", i, aThreadInfo[i].dwPID, aThreadInfo[i].dwPID);
			printf("aThreadInfo[%u].szProcName : %s\n", i, aThreadInfo[i].szProcName);
			printf("aThreadInfo[%u].dwThreadId : 0x%x %u\n", i, aThreadInfo[i].dwThreadId, aThreadInfo[i].dwThreadId);
			printf("aThreadInfo[%u].lpStartAddress : 0x%x %u\n", i, (DWORD)aThreadInfo[i].lpStartAddress, (DWORD)aThreadInfo[i].lpStartAddress);
			printf("aThreadInfo[%u].dwThreadState : 0x%x %u\n", i, aThreadInfo[i].dwThreadState, aThreadInfo[i].dwThreadState);
			printf("aThreadInfo[%u].dwThreadWaitReason : 0x%x %u\n\n", i, aThreadInfo[i].dwThreadWaitReason, aThreadInfo[i].dwThreadWaitReason);

			if (fd != NULL)
			{
				fprintf(fd, "aThreadInfo[%u].dwPid : 0x%x %u\n", i, aThreadInfo[i].dwPID, aThreadInfo[i].dwPID);
				fprintf(fd, "aThreadInfo[%u].szProcName : %s\n", i, aThreadInfo[i].szProcName);
				fprintf(fd, "aThreadInfo[%u].dwThreadId : 0x%x %u\n", i, aThreadInfo[i].dwThreadId, aThreadInfo[i].dwThreadId);
				fprintf(fd, "aThreadInfo[%u].lpStartAddress : 0x%x %u\n", i, (DWORD)aThreadInfo[i].lpStartAddress, (DWORD)aThreadInfo[i].lpStartAddress);
				fprintf(fd, "aThreadInfo[%u].dwThreadState : 0x%x %u\n", i, aThreadInfo[i].dwThreadState, aThreadInfo[i].dwThreadState);
				fprintf(fd, "aThreadInfo[%u].dwThreadWaitReason : 0x%x %u\n\n", i, aThreadInfo[i].dwThreadWaitReason, aThreadInfo[i].dwThreadWaitReason);
			}
		}
	}

	fclose(fd);

}

/////////////////////////////////////////////////////////////////////////////

int _tmain(int argc, TCHAR* argv[], TCHAR* envp[])
{
	// initialize MFC and print and error on failure
	if (!AfxWinInit(::GetModuleHandle(NULL), NULL, ::GetCommandLine(), 0))
	{
		// TODO: change error code to suit your needs
		cerr << _T("Fatal Error: MFC initialization failed") << endl;
		return 1;
	}

	PdhTest();


	return 0;

}


