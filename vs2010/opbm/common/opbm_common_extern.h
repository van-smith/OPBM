// opbm_common_extern.h : Holds all of the variables referenced externally by opbm_common.h
// Include this file only in your top-level DllMain.cpp, or main.cpp program

HWND	enumeratedWindows[_MAX_HWND_COUNT];
int		hwndMaxCount	= 0;
int		hwndsClosed		= 0;
