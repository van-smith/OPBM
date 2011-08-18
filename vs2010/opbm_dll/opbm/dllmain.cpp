// dllmain.cpp : Defines the entry point for the DLL application.
#include "stdafx.h"
#include "..\common\opbm_common.h"


HMODULE ghModule;
HWND	enumeratedWindows[_MAX_HWND_COUNT];
int		hwndMaxCount	= 0;
int		hwndsClosed		= 0;


BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 )
{
	ghModule = hModule;
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		break;
	}
	return TRUE;
}
