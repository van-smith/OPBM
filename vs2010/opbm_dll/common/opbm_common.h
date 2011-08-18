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
	void			GetHarnessTempDirectory			(char* dirname, int dirnameLength);
	void			GetScriptCSVDirectory			(char* dirname, int dirnameLength);
	void			GetScriptTempDirectory			(char* dirname, int dirnameLength);
	void			GetSettingsDirectory			(char* dirname, int dirnameLength);
	void			GetRunningDirectory				(char* dirname, int dirnameLength);
	void			GetCSIDLDirectory				(char* dirname, int dirnameLength, char* csidl_name);
	char*			GetRegistryKeyValue				(char* key);
	int				caseNocaseCompare				(char* left, char* right, int length);
	int				caseNocaseContains				(char* needle, char* haystack);
	int				SetRegistryKeyValueAsString		(char* key, char* value);
	int				SetRegistryKeyValueAsDword		(char* key, int value);
	int				SetRegistryKeyValueAsBinary		(char* key, char* value, int length);
	char*			breakoutHkeyComponents			(char* key, HKEY& hk, int& skip);
	BOOL CALLBACK	EnumWindowsCallbackProc			(HWND hwnd, LPARAM lParam);
	BOOL CALLBACK	ComparativeWindowsCallbackProc	(HWND hwnd, LPARAM lParam);
