// spinup.cpp : Defines the entry point for the console application.
//

#include <SDKDDKVer.h>
#include <stdio.h>
#include <tchar.h>
#include <Windows.h>


int _tmain(int argc, _TCHAR* argv[])
{
	printf("Spinning up for 60 seconds...\n");

	// Placeholder pause for now, 60 seconds
	Sleep(60000);
	// In the future, will load multiple common DLLs used by various
	// applications as determined by a depends tree for installed
	// applications in OPBM's benchmark purview.

	// Indicate "success"
	return 0;
}
