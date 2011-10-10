//////////
//
// jbm.h
// Header file for JBM
//
/////




#include <stdio.h>
#include <Windows.h>
#include <WinBase.h>
#include <tchar.h>
#include "resource.h"
#define _JBM 1
#define _JBM_OWNER 1
#include "..\common\jbm_common.h"



//////////
//
// Constants used in JBM
//
/////
	#define		_JBM_UNUSED_SLOT					0					// Show grayscale
	#define		_JBM_EMPTY_SLOT						1					// Show red
	#define		_JBM_SLOT_FILLED					2					// Show yellow
	#define		_JBM_RUNNING						3					// Show normally
	#define		_JBM_FINISHED						4					// Show yellow
	#define		_JBM_EXITED							5					// Show red

	#define		_JBM_BACKGROUND_WIDTH				146
	#define		_JBM_BACKGROUND_HEIGHT				75

	#define		_HEADER_FOREGROUND					RGB(255,255,255)	// Header
	#define		_HEADER_FOREGROUND_DARK				RGB(0,0,0)
	#define		_HEADER_BACKGROUND					RGB(209,200,172)
	#define		_TEST_FOREGROUND					RGB(0,0,255)		// Currently running test
	#define		_TEST_BACKGROUND					RGB(241,230,198)
	#define		_PERCENT_FOREGROUND					RGB(128,128,255)	// Percent text
	#define		_PERCENT_BACKGROUND					RGB(209,200,172)
	#define		_HISTORY_FOREGROUND					RGB(0,0,0)			// Previous test and times
	#define		_HISTORY_BACKGROUND					RGB(241,230,198)




//////////
// Global variables
/////
	struct SScoringDataLL
	{	// A linked list of scoring data for the process
		SScoringDataLL*		next;
		SScoringData		score;
	};

	struct SProcesses
	{
		int					status;					// _JBM_EMPTY_SLOT, _JBM_SLOT_FILLED or _JBM_RUNNING
		RECT				rc;						// Rectangle in the window where this entry gets written-to/updated
		HBITMAP				doubleBufferBitmap;		// Bitmap used for a double-buffer
		HANDLE				pipeHandle;				// Handle to the pipe used to read this item's pipe data
		SPipeData			pipeData;				// Copy of the most recently read pipe data
		SPipeDataNames		testHistory[4];			// Holds a history of prior tests that were run
		SPipeDataNames		testHistoryTimes[4];	// Holds a history of the time between 
		SScoringDataLL*		firstScore;				// Pointer to first score
	};
	SProcesses*	gsProcesses					= NULL;
	int			gnProcessCount				= 0;
	int			gnWidth						= 0;
	int			gnHeight					= 0;
	bool		gbAppIsRunning				= true;
	HINSTANCE	ghInst						= GetModuleHandle(NULL);
	HWND		ghWnd						= NULL;
	HANDLE		ghOwnerPipeHandle			= INVALID_HANDLE_VALUE;


//////////
// Bitmaps, fonts, graphics elements loaded/needed by this app
/////
	HBITMAP		ghbmpConnectionRunning		= NULL;
	HBITMAP		ghbmpConnectionEmptySlot	= NULL;
	HBITMAP		ghbmpConnectionRed			= NULL;
	HBITMAP		ghbmpConnectionYellow		= NULL;
	HBITMAP		ghbmpConnectionGreen		= NULL;
	HBITMAP		ghbmpStatusbarHighLeft		= NULL;
	HBITMAP		ghbmpStatusbarHighMiddle	= NULL;
	HBITMAP		ghbmpStatusbarHighRight		= NULL;
	HBITMAP		ghbmpStatusbarLowLeft		= NULL;
	HBITMAP		ghbmpStatusbarLowMiddle		= NULL;
	HBITMAP		ghbmpStatusbarLowRight		= NULL;
	HFONT		ghfHeader					= NULL;
	HFONT		ghfPercent					= NULL;
	HFONT		ghfTests					= NULL;

//////////
// Forward declarations
/////
	void				verifyParameter					(LPSTR lpCmdLine);
	void				loadBitmaps						(void);
	void				createFonts						(void);
	void				determineLayout					(void);
	void				createWindow					(void);
	void				readEvents						(void);
	LRESULT CALLBACK	WndProc							(HWND hwnd, UINT m, WPARAM w, LPARAM l);
	void				paintThisProcess				(SProcesses* sp, HDC hdc, HDC hdc2, RECT rcSlot);
	float				verifyPercent					(float percent);
	void				setRectRelativeTo				(RECT* rcOutter, RECT* rcInner, int left, int top, int right, int bottom);
	void				drawStatusBar					(HDC hdc, HDC hdc2, RECT& rc, float percent, char* text);
	void				drawStatusBarSegment			(HDC hdc, HDC hdc2, RECT& rc, HBITMAP left, HBITMAP middle, HBITMAP right, int leftWidth, int middleWidth, int rightWidth, char* text);
	void				connectToThisInstancePipeData	(int slot);
	int					connectToOwnerPipeData			(void);
	void				loadPipeData					(int slot, int newStatus);
	void				loadScoringData					(int slot);
	void				setSlotStatus					(int slot, int newStatus);
	bool				allHaveExited					(void);
	int					getScoringDataItem				(int slot, int scoreNumber);
	int					getMaxScoringDataItem			(int scoreNumber);
