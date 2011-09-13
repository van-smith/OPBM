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
	// Let the operating system do its thing
	Sleep(60000);

	// Indicate "success"
	return 0;
}

