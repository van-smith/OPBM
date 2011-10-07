//////////
//
// opbm.h
//
/////








#define _JBM_OWNER 1
#include "jbm_common.h"

extern HMODULE ghModule;




//////////
//
// Variables used for the JbmOwner functions
//
/////
	bool			hasJbmOwnerCheckedIn			= false;
	HANDLE			ghOwnerPipeHandle				= INVALID_HANDLE_VALUE;
	HWND			ghWndJBM						= NULL;
	SScoringData	gsScoreData;




//////////
//
// Used for NoteAllOpenWindows() and CloseAllWindowsNotPreviouslyNoted()
// Allocate enough space for 16K windows, which should be more than plenty.
// Note:  Windows doesn't just have windows 
//
/////
	// opbm_assist.cpp functions:
	bool			iCopyFile						(wchar_t* prefixDir, wchar_t* srcFile, char* content, int length);
	bool			iMakeDirectory					(wchar_t* prefixDir, wchar_t* postfixDir);
	int				wcstrncpy						(wchar_t* dest, int max, wchar_t* src);
	int				wcstrncmp						(wchar_t* left, wchar_t* right, int max);
	int				wcstrnicmp						(wchar_t* left, wchar_t* right, int max);
	char*			GetOffsetToResource				(int number, LPWSTR type, int* size);
	BOOL			isRunningUnderWOW64				(void);
