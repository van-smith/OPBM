//////////
//
// Variables used by opbm_common
//
/////
	#define _MAX_HWND_COUNT	16384
	extern HWND		enumeratedWindows[];
	extern int		hwndMaxCount;
	extern int		hwndsClosed;

	// "Run macros?" key:			Security\\AccessVBOM
	// "Disable Warnings?" key:		Security\\VBAWarnings
	extern char gcOffice2010_AccessVBOM_Excel[];
	extern char gcOffice2010_VBAWarnings_Excel[];
	extern char gcOffice2010_WelcomeScreen_Excel[];
	extern char* gcOffice2010_AccessVBOM_ExcelSave;
	extern char* gcOffice2010_VBAWarnings_ExcelSave;
	extern char* gcOffice2010_WelcomeScreen_ExcelSave;

	extern char gcOffice2010_AccessVBOM_PowerPoint[];
	extern char gcOffice2010_VBAWarnings_PowerPoint[];
	extern char* gcOffice2010_AccessVBOM_PowerPointSave;
	extern char* gcOffice2010_VBAWarnings_PowerPointSave;

	extern char gcOffice2010_AccessVBOM_Publisher[];
	extern char gcOffice2010_VBAWarnings_Publisher[];
	extern char* gcOffice2010_AccessVBOM_PublisherSave;
	extern char* gcOffice2010_VBAWarnings_PublisherSave;

	extern char gcOffice2010_AccessVBOM_Access[];
	extern char gcOffice2010_VBAWarnings_Access[];
	extern char* gcOffice2010_AccessVBOM_AccessSave;
	extern char* gcOffice2010_VBAWarnings_AccessSave;

	extern char gcOffice2010_AccessVBOM_Outlook[];
	extern char gcOffice2010_VBAWarnings_Outlook[];
	extern char* gcOffice2010_AccessVBOM_OutlookSave;
	extern char* gcOffice2010_VBAWarnings_OutlookSave;

	extern char gcOffice2010_AccessVBOM_Word[];
	extern char gcOffice2010_VBAWarnings_Word[];
	extern char* gcOffice2010_AccessVBOM_WordSave;
	extern char* gcOffice2010_VBAWarnings_WordSave;


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
	void			GetCompressedPathname			(char* compressedPathname, int compressedPathnameSize, char* uncompressedPathname, int uncompressedPathnameLength);
	char*			GetRegistryKeyValue				(char* key);
	char*			GetRegistryKeyValueWOW64		(char* key);
	char*			GetRegistryKeyValueWOW32		(char* key);
	int				caseNocaseCompare				(char* left, char* right, int length);
	int				caseNocaseContains				(char* needle, char* haystack);
	int				SetRegistryKeyValueAsString		(char* key, char* value);
	int				SetRegistryKeyValueAsDword		(char* key, int value);
	int				SetRegistryKeyValueAsBinary		(char* key, char* value, int length);
	char*			breakoutHkeyComponents			(char* key, HKEY& hk, int& skip);
	BOOL CALLBACK	EnumWindowsCallbackProc			(HWND hwnd, LPARAM lParam);
	BOOL CALLBACK	ComparativeWindowsCallbackProc	(HWND hwnd, LPARAM lParam);

	void			Office2010SaveKeys				();
	void			Office2010InstallKeys			(char* successString);
	void			Office2010RestoreKeys			(char* successString);
