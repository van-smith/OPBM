//////////
//
// Variables used by opbm_common
//
/////
	#define _MAX_HWND_COUNT	16384
	extern HWND		enumeratedWindows[];
	extern int		hwndMaxCount;
	extern int		hwndsClosed;


//////////
//
// Functions in opbm_common.cpp
//
/////
	void			GetHarnessCSVDirectory			(char* dirname, int dirnameLength);
	void			GetHarnessXMLDirectory			(char* dirname, int dirnameLength);
	void			GetScriptCSVDirectory			(char* dirname, int dirnameLength);
	void			GetCSIDLDirectory				(char* dirname, int dirnameLength, char* csidl_name);
	BOOL CALLBACK	EnumWindowsCallbackProc			(HWND hwnd, LPARAM lParam);
	BOOL CALLBACK	ComparativeWindowsCallbackProc	(HWND hwnd, LPARAM lParam);
