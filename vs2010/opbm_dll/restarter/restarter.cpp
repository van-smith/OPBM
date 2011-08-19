//////////
//
// restarter.cpp
//
//////////
//
// OPBM - Restarter application, allows continuation to restart the JVM
//        after a reboot.
//
// Accepts three command line parameters:
//
//		1) path to start in (c:\cana\java\obpm\)
//		2) pathname of JVM (c:\program files\java\jdk1.7.0\jre\bin\java.exe)
//		3) jar (opbm.jar)
//
// This application assembles those pieces into an executable command
// line like this, after first changing to the specified directory:
//
//		c:\program files\java\jdk1.7.0\jre\bin\java.exe -jar opbm.jar
//
//




#include <SDKDDKVer.h>
#include <stdio.h>
#include <tchar.h>
#include <Windows.h>
#include <WinBase.h>
#include <atlconv.h>

char jarPrefix[] = " -jar ";
void usage(int returnCode);


int _tmain(int argc, _TCHAR* argv[])
{
	USES_CONVERSION;
	char command[ MAX_PATH ];
	char params[ MAX_PATH ];
	TCHAR tcommand[ MAX_PATH * 2 ];
	TCHAR tparams[ MAX_PATH * 2 ];
	TCHAR tdir[ MAX_PATH * 2 ];
	STARTUPINFO si;
	PROCESS_INFORMATION pi;
	BOOL result;

	if (argc != 4)
	{	// Invalid number of parameters
		printf("OPBM is unable to restart due to incorrect command line syntax.\n");
		printf("\n");
		usage(-1);
	}

	// Identify ourselves to the world
	printf("OPBM Restarter\n");
	printf("Preparing to restart...\n");
	printf("\n");

	// Initialize our memory
	ZeroMemory(command, MAX_PATH);
	ZeroMemory(params, MAX_PATH);
	ZeroMemory(tcommand, MAX_PATH * 2);
	ZeroMemory(tparams, MAX_PATH * 2);

	// Assemble the command
	memcpy(command, T2A(argv[2]), strlen(T2A(argv[2])));
	// Right now, the command looks like "c:\program files\java\jdk1.7.0\jre\bin\java.exe" (with the double-quotes)

	// Assemble the params
	memcpy(params, jarPrefix, strlen(jarPrefix));								// append -jar
	memcpy(params + strlen(params), T2A(argv[3]), strlen(T2A(argv[3])));		// append opbm.jar
	// Right now, the parameters looks like " -jar opbm.jar"

	// Change to the specified directory
	if (!SetCurrentDirectory(argv[1]))
	{	// Unable to change to the specified directory
		printf("error\n");
		printf("\n");
		printf("OPBM is unable to restart due to incorrect command line parameter:  startup directory\n");
		printf("Tried to change directory to \"%s\", and failed.\n", T2A(argv[1]));
		printf("\n");
		usage(-2);
	}
	// We're in the startup directory (c:\cana\java\opbm\)
	GetCurrentDirectory(sizeof(tdir), tdir);
	printf("Directory: %s\n", T2A(tdir));
	
	// Create buffers for the command and parameters
	_tcscpy_s(tcommand, MAX_PATH * 2, A2T(command));
	_tcscpy_s(tparams, MAX_PATH * 2, A2T(params)); // _T(" -jar opbm.jar opbm.Opbm"));

	// Tell the world what we're doing
	printf("Executing: %s %s\n", T2A(tcommand), T2A(tparams));
	printf("\n");

	// Create the new process and we're done!
	memset(&si, 0, sizeof(si));
	memset(&pi, 0, sizeof(pi));
    si.cb = sizeof(si);
	result = CreateProcess(	tcommand,									/* app name */
							tparams,									/* command line parameters */
							NULL,										/* process attributes */
							NULL,										/* thread attributes */
							FALSE,										/* inherit handles? */
							CREATE_NO_WINDOW | NORMAL_PRIORITY_CLASS,	/* creation flags */
							NULL,										/* environment */
							NULL,										/* alternate startup directory */
							&si,										/* startup info */
							&pi);										/* process info */
	if (result)
	{	// Success
		printf("\n\nSuccess.\n");
		return(0);

	} else {
		printf("Error %u\n", GetLastError());
		printf("\n");
		printf("OPBM is unable to restart due to a failure to create a new process\n");
		printf("Tried to change directory to \"%s\", and succeeded.\n", T2A(argv[1]));
		printf("Tried to execute the following command, and failed:\n");
		printf("%s %s\n", command, params);
		usage(-3);
	}
}


void usage(int returnCode)
{
	int i;

	printf("-----\n");
	printf("Usage:\n");
	printf("    restarter c:\\cana\\java\\opbm\\ \"c:\\program files\\java\\jre7\\bin\\java.exe\" opbm.jar\n");
	printf("\n");
	printf("\n");
	printf("-----\n");
	printf("Please copy-and-paste or hit PrtScrn key (captures screen image to clipboard)\n\n");
#define MAX_SECONDS 60
	for (i = 0; i < MAX_SECONDS; i++)
	{	// Keep the message up for 30 seconds
		printf("\r%u seconds before exit", MAX_SECONDS - i - 1);
		Sleep(1000);
	}
	exit(returnCode);
}