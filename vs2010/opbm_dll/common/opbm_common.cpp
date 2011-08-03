//////////
//
// opbm_common.cpp
//
// Shared functions between opbm.dll, and opbm64.dll (used by Java JNI interface)
//
/////

void GetHarnessCSVDirectory(char* dirname, int dirnameLength)
{
	USES_CONVERSION;
	// Clear out the dirname contents
	memset(dirname, 0, dirnameLength);

	// Grab the directory from Windows
	SHGetSpecialFolderPathA(0, dirname, CSIDL_MYDOCUMENTS, TRUE);

	// Make sure it ends with a backslash
	if (*(dirname +strlen(dirname) - 1) != '\\')
		*(dirname + strlen(dirname)) = '\\';

	// Append the portion relative to the harness's CSV directory
	memcpy(dirname + strlen(dirname), "opbm\\results\\csv\\", 17);

	// Make sure the directory exists
	SHCreateDirectoryEx(NULL, A2T(dirname), NULL);
}


void GetHarnessXMLDirectory(char* dirname, int dirnameLength)
{
	USES_CONVERSION;
	// Initialize the path
	memset(dirname, 0, dirnameLength);

	// Grab the directory from Windows
	SHGetSpecialFolderPathA(0, dirname, CSIDL_MYDOCUMENTS, TRUE);

	// Make sure it ends with a backslash
	if (*(dirname +strlen(dirname) - 1) != '\\')
		*(dirname + strlen(dirname)) = '\\';

	// Append the portion relative to the harness's XML directory
	memcpy(dirname + strlen(dirname), "opbm\\results\\xml\\", 17);

	// Make sure the directory exists
	SHCreateDirectoryEx(NULL, A2T(dirname), NULL);
}



void GetScriptCSVDirectory(char* dirname, int dirnameLength)
{
	USES_CONVERSION;
	// Initialize the path
	memset(dirname, 0, dirnameLength);

	// Grab the directory from Windows
	SHGetSpecialFolderPathA(0, dirname, CSIDL_MYDOCUMENTS, TRUE);

	// Make sure it ends with a backslash
	if (*(dirname +strlen(dirname) - 1) != '\\')
		*(dirname + strlen(dirname)) = '\\';

	// Append the portion relative to the script's CSV directory
	memcpy(&dirname[strlen(dirname)], "opbm\\scriptOutput\\", 18);

	// Make sure the directory exists
	SHCreateDirectoryEx(NULL, A2T(dirname), NULL);
}

void GetCSIDLDirectory(char* dirname, int dirnameLength, char* csidl_name)
{
	USES_CONVERSION;
	int csid;
	bool failure;

	// Initialize the path
	memset(dirname, 0, dirnameLength);

	// Find out what directory they're after
	failure = false;
	if (_stricmp(csidl_name, "DESKTOP") == 0)
	{	// <desktop>
		csid = CSIDL_DESKTOP;
	
	} else if (_stricmp(csidl_name, "INTERNET") == 0) {
		// Internet Explorer (icon on desktop)
		csid = CSIDL_INTERNET;
	
	} else if (_stricmp(csidl_name, "PROGRAMS") == 0) {
		// Start Menu\Programs
		csid = CSIDL_PROGRAMS;
	
	} else if (_stricmp(csidl_name, "CONTROLS") == 0) {
		// My Computer\Control Panel
		csid = CSIDL_CONTROLS;
	
	} else if (_stricmp(csidl_name, "PRINTERS") == 0) {
		// My Computer\Printers
		csid = CSIDL_PRINTERS;
	
	} else if (_stricmp(csidl_name, "PERSONAL")		== 0 ||
				_stricmp(csidl_name, "MY_DOCUMENTS")	== 0 ||
				_stricmp(csidl_name, "MY_DOCS")			== 0 ||
				_stricmp(csidl_name, "MYDOCS")			== 0 ||
				_stricmp(csidl_name, "MYDOCUMENTS")		== 0) {
		// My Documents
		csid = CSIDL_PERSONAL;
	
	} else if (_stricmp(csidl_name, "FAVORITES") == 0) {
		// <user name>\Favorites
		csid = CSIDL_FAVORITES;
	
	} else if (_stricmp(csidl_name, "STARTUP") == 0) {
		// Start Menu\Programs\Startup
		csid = CSIDL_STARTUP;
	
	} else if (_stricmp(csidl_name, "RECENT") == 0) {
		// <user name>\Recent
		csid = CSIDL_RECENT;
	
	} else if (_stricmp(csidl_name, "SENDTO") == 0) {
		// <user name>\SendTo
		csid = CSIDL_SENDTO;
	
	} else if (_stricmp(csidl_name, "BITBUCKET") == 0) {
		// <desktop>\Recycle Bin
		csid = CSIDL_BITBUCKET;
	
	} else if (_stricmp(csidl_name, "STARTMENU") == 0) {
		// <user name>\Start Menu
		csid = CSIDL_STARTMENU;
	
	} else if (_stricmp(csidl_name, "MYMUSIC") == 0) {
		// "My Music" folder
		csid = CSIDL_MYMUSIC;
	
	} else if (_stricmp(csidl_name, "MYVIDEO") == 0) {
		// "My Videos" folder
		csid = CSIDL_MYVIDEO;
	
	} else if (_stricmp(csidl_name, "DESKTOPDIRECTORY") == 0) {
		// <user name>\Desktop
		csid = CSIDL_DESKTOPDIRECTORY;
	
	} else if (_stricmp(csidl_name, "DRIVES") == 0) {
		// My Computer
		csid = CSIDL_DRIVES;
	
	} else if (_stricmp(csidl_name, "NETWORK") == 0) {
		// Network Neighborhood (My Network Places)
		csid = CSIDL_NETWORK;
	
	} else if (_stricmp(csidl_name, "NETHOOD") == 0) {
		// <user name>\nethood
		csid = CSIDL_NETHOOD;
	
	} else if (_stricmp(csidl_name, "FONTS") == 0) {
		// windows\fonts
		csid = CSIDL_FONTS;
	
	} else if (_stricmp(csidl_name, "COMMON_STARTMENU") == 0) {
		// All Users\Start Menu
		csid = CSIDL_COMMON_STARTMENU;
	
	} else if (_stricmp(csidl_name, "COMMON_PROGRAMS") == 0) {
		// All Users\Start Menu\Programs
		csid = CSIDL_COMMON_PROGRAMS;
	
	} else if (_stricmp(csidl_name, "COMMON_STARTUP") == 0) {
		// All Users\Startup
		csid = CSIDL_COMMON_STARTUP;
	
	} else if (_stricmp(csidl_name, "COMMON_DESKTOPDIRECTORY") == 0) {
		// All Users\Desktop
		csid = CSIDL_COMMON_DESKTOPDIRECTORY;
	
	} else if (_stricmp(csidl_name, "APPDATA") == 0) {
		// <user name>\Application Data
		csid = CSIDL_APPDATA;
	
	} else if (_stricmp(csidl_name, "PRINTHOOD") == 0) {
		// <user name>\PrintHood
		csid = CSIDL_PRINTHOOD;
	
	} else if (_stricmp(csidl_name, "LOCAL_APPDATA") == 0) {
		// <user name>\Local Settings\Applicaiton Data (non roaming)
		csid = CSIDL_LOCAL_APPDATA;
	
	} else if (_stricmp(csidl_name, "ALTSTARTUP") == 0) {
		// non localized startup
		csid = CSIDL_ALTSTARTUP;
	
	} else if (_stricmp(csidl_name, "COMMON_ALTSTARTUP") == 0) {
		// non localized common startup
		csid = CSIDL_COMMON_ALTSTARTUP;
	
	} else if (_stricmp(csidl_name, "COMMON_FAVORITES") == 0) {
		// favorites folder
		csid = CSIDL_COMMON_FAVORITES;
	
	} else if (_stricmp(csidl_name, "INTERNET_CACHE") == 0) {
		// internet cache folder
		csid = CSIDL_INTERNET_CACHE;
	
	} else if (_stricmp(csidl_name, "COOKIES") == 0) {
		// cookies folder
		csid = CSIDL_COOKIES;
	
	} else if (_stricmp(csidl_name, "HISTORY") == 0) {
		// history folder
		csid = CSIDL_HISTORY;
	
	} else if (_stricmp(csidl_name, "COMMON_APPDATA") == 0) {
		// All Users\Application Data
		csid = CSIDL_COMMON_APPDATA;
	
	} else if (_stricmp(csidl_name, "WINDOWS") == 0) {
		// GetWindowsDirectory()
		csid = CSIDL_WINDOWS;
	
	} else if (_stricmp(csidl_name, "SYSTEM") == 0) {
		// GetSystemDirectory()
		csid = CSIDL_SYSTEM;
	
	} else if (_stricmp(csidl_name, "PROGRAM_FILES") == 0) {
		// C:\Program Files
		csid = CSIDL_PROGRAM_FILES;
	
	} else if (_stricmp(csidl_name, "MYPICTURES") == 0) {
		// C:\Program Files\My Pictures
		csid = CSIDL_MYPICTURES;
	
	} else if (_stricmp(csidl_name, "PROFILE") == 0) {
		// USERPROFILE
		csid = CSIDL_PROFILE;
	
	} else if (_stricmp(csidl_name, "SYSTEMX86") == 0) {
		// x86 system directory on RISC
		csid = CSIDL_SYSTEMX86;
	
	} else if (_stricmp(csidl_name, "PROGRAM_FILESX86") == 0) {
		// x86 C:\Program Files on RISC
		csid = CSIDL_PROGRAM_FILESX86;
	
	} else if (_stricmp(csidl_name, "PROGRAM_FILES_COMMON") == 0) {
		// C:\Program Files\Common
		csid = CSIDL_PROGRAM_FILES_COMMON;
	
	} else if (_stricmp(csidl_name, "PROGRAM_FILES_COMMONX86") == 0) {
		// x86 Program Files\Common on RISC
		csid = CSIDL_PROGRAM_FILES_COMMONX86;
	
	} else if (_stricmp(csidl_name, "COMMON_TEMPLATES") == 0) {
		// All Users\Templates
		csid = CSIDL_COMMON_TEMPLATES;
	
	} else if (_stricmp(csidl_name, "COMMON_DOCUMENTS") == 0) {
		// All Users\Documents
		csid = CSIDL_COMMON_DOCUMENTS;
	
	} else if (_stricmp(csidl_name, "COMMON_ADMINTOOLS") == 0) {
		// All Users\Start Menu\Programs\Administrative Tools
		csid = CSIDL_COMMON_ADMINTOOLS;
	
	} else if (_stricmp(csidl_name, "ADMINTOOLS") == 0) {
		// <user name>\Start Menu\Programs\Administrative Tools
		csid = CSIDL_ADMINTOOLS;
	
	} else if (_stricmp(csidl_name, "CONNECTIONS") == 0) {
		// Network and Dial-up Connections
		csid = CSIDL_CONNECTIONS;
	
	} else if (_stricmp(csidl_name, "COMMON_MUSIC") == 0) {
		// All Users\My Music
		csid = CSIDL_COMMON_MUSIC;
	
	} else if (_stricmp(csidl_name, "COMMON_PICTURES") == 0) {
		// All Users\My Pictures
		csid = CSIDL_COMMON_PICTURES;
	
	} else if (_stricmp(csidl_name, "COMMON_VIDEO") == 0) {
		// All Users\My Video
		csid = CSIDL_COMMON_VIDEO;
	
	} else if (_stricmp(csidl_name, "RESOURCES") == 0) {
		// Resource Directory
		csid = CSIDL_RESOURCES;
	
	} else if (_stricmp(csidl_name, "RESOURCES_LOCALIZED") == 0) {
		// Localized Resource Direcotry
		csid = CSIDL_RESOURCES_LOCALIZED;
	
	} else if (_stricmp(csidl_name, "COMMON_OEM_LINKS") == 0) {
		// Links to All Users OEM specific apps
		csid = CSIDL_COMMON_OEM_LINKS;
	
	} else if (_stricmp(csidl_name, "CDBURN_AREA") == 0) {
		// USERPROFILE\Local Settings\Application Data\Microsoft\CD Burning
		csid = CSIDL_CDBURN_AREA;
	
	} else if (_stricmp(csidl_name, "COMPUTERSNEARME") == 0) {
		// Computers Near Me (computered from Workgroup membership)
		csid = CSIDL_COMPUTERSNEARME;

	} else {
		failure = true;
		memcpy(dirname, "?unknown?csidl?name?", 20);
	}


	if (!failure)
	{
		// Grab the directory from Windows
		SHGetSpecialFolderPathA(0, dirname, csid, TRUE);

		// Make sure it ends with a backslash
		if (*(dirname +strlen(dirname) - 1) != '\\')
			*(dirname + strlen(dirname)) = '\\';

		// Make sure the directory exists
		SHCreateDirectoryEx(NULL, A2T(dirname), NULL);
	}
}
