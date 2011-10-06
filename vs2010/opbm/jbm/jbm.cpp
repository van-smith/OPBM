//////////
//
// jbm.cpp
// Java Benchmark Monitor
//
// -----
// Last Updated:  Sep 30, 2011
//
// by Van Smith
// Cossatot Analytics Laboratories, LLC. (Cana Labs)
//
// (c) Copyright Cana Labs.
// Free software licensed under the GNU GPL2.
//
// version 1.0
//
/////
//
// This source file is the top-level file used to monitor multiple
// Java Benchmark instances reporting using the JBM protocol.
//
// This system was explicitly written for OPBM, and uses AutoIt to
// launch JBM.exe before launching individual JVM instances to
// conduct single-threaded benchmarks, each JVM being assigned an
// affinity to a given core.
//
// The purpose of the JBM is to receive data from all of the
// separate processes and convey it in a visual form to the user.
//
// This app creates a series of windows using the default resource
// bitmaps which provide four data fields:
//
//			1)	Name of instance (process, as in "JVM X on Core Y")
//			2)	Name of test currently being conducted in that instance
//			3)	Percent of completion for the test
//			4)  Percent of completion overall for the benchmark
//
/////

#include "jbm.h"

//////////
//
// A command line parameter MUST be provided:  numberOfProcesses
//
// Example usage:
//
//		jbm 6
//
// The above example will launch JBM and create a HUD for displaying
// six separate running processes and their relative statuses during
// execution, waiting for all six to launch and connect before
// allowing any of them to begin their testing.
//
/////
	int main(int argc, char* argv[])
	{
		verifyParameter(argc, argv);
		determineLayout();
		loadBitmaps();
		createFonts();
		createWindow();
		readEvents();
	}









//////////
//
// Verifies the command-line parameters, reports errors, and terminates
// if there are errors.
//
/////
	void verifyParameter(int argc, char* argv[])
	{
		bool bShowUsageError		= false;
		bool bShowParameterError	= false;

		printf("JBM - Java Benchmark Monitor\n");
		printf("Written for OPBM by Van Smith\n");
		printf("\n");

		// They need only one parameter, no more, no less
		if (argc != 2)
		{	// Correct usage *IS* required
			bShowUsageError = true;

		} else {
			// Check the paramter, which should be a process count
			gnProcessCount = atoi(argv[1]);
			if (gnProcessCount < 1 || gnProcessCount > _JBM_MAX_CONNECTIONS)
			{
				bShowUsageError		= true;		// Show the usage also
				bShowParameterError = true;		// And the parameters must be correct
			}
		}

		// Report any errors
		if (bShowUsageError)
		{
			printf("Error:  Command-line usage is incorrect.\n");
			printf("        |  Usage:  jbm numberOfProcesses\n");
			printf("        |Example:  jbm 6\n");
			printf("        |\n");
			printf("        +This example will launch JBM with a HUD displaying six separate processes running benchmarks.\n");
			printf("\n");
		}
		if (bShowParameterError)
		{	// Process number is not correct
			printf("Error:  Command-line parameter is incorrect.\n");
			printf("        |Number of processes must be between 1 and %u.\n", _JBM_MAX_CONNECTIONS);
			printf("        +If more processes are needed, please contact the developer.\n");
			printf("\n");
		}

		// Exit if error
		if (bShowUsageError || bShowParameterError)
		{	// Exit on error
			exit(-1);
		}
		// If we get here, we're good
	}




//////////
//
// Determines how the windows will be laid out, and allocates and
// initializes initial resources for the layout.
//
/////
	void determineLayout(void)
	{
		int x, y, top, left, count;
		SProcesses* lsp;

		// Note:  See jbm_common.h and _JBM_MAX_CONNECTIONS, as these entries should sync with that value
		if (gnProcessCount == 1)
		{	// A single process, oh how boring, a single square
			gnHeight	= 1;
			gnWidth		= 1;

		} else if (gnProcessCount == 2) {
			// Two processes, a single line with two entries side-by-side
			gnHeight	= 1;
			gnWidth		= 2;

		} else if (gnProcessCount >= 3 && gnProcessCount <= 4) {
			// 3 or 4 processes, two lines of two entries
			gnHeight	= 2;
			gnWidth		= 2;

		} else if (gnProcessCount >= 5 && gnProcessCount <= 6) {
			// 5 or 6 processes, two lines of three entries
			gnHeight	= 2;
			gnWidth		= 3;

		} else if (gnProcessCount >= 7 && gnProcessCount <= 8) {
			// 7 or 8 processes, two lines of four entries
			gnHeight	= 2;
			gnWidth		= 4;

		} else if (gnProcessCount == 9) {
			// 9 processes, three lines of three entries
			gnHeight	= 3;
			gnWidth		= 3;

		} else if (gnProcessCount >= 10 && gnProcessCount <= 12) {
			// 10 to 12 processes, three lines of four entries
			gnHeight	= 3;
			gnWidth		= 4;

		} else if (gnProcessCount >= 13 && gnProcessCount <= 16) {
			// 13 to 16 processes, four lines of four entries
			gnHeight	= 4;
			gnWidth		= 4;

		} else if (gnProcessCount >= 17 && gnProcessCount <= 20) {
			// 17 to 20 processes, four lines of five entries
			gnHeight	= 4;
			gnWidth		= 5;

		} else if (gnProcessCount >= 21 && gnProcessCount <= 24) {
			// 21 to 24 processes, six lines of four entries
			gnHeight	= 4;
			gnWidth		= 6;

		} else if (gnProcessCount == 25) {
			// 25 processes, five lines of five entries
			gnHeight	= 5;
			gnWidth		= 5;

		} else if (gnProcessCount >= 26 && gnProcessCount <= 30) {
			// 26 to 30 processes, five lines of six entries
			gnHeight	= 5;
			gnWidth		= 6;

		} else if (gnProcessCount >= 31 && gnProcessCount <= 32 /* We only have _JBM_MAX_CONNECTIONS defined as 32 though, so the 33 to 36 are no valid right now */) {
			// 31 to 36 processes, six lines of six entries
			gnHeight	= 6;
			gnWidth		= 6;

		} else {
			// An invalid value, so set it to the maximum, and it will automatically ignore all others
			gnHeight	= 6;
			gnWidth		= 6;

		}

		// Create the appropriate number of slots in our inter-process buffer
		gsProcesses = (SProcesses*)malloc(gnWidth * gnHeight * sizeof(SProcesses));
		if (gsProcesses != NULL)
		{	// Initially populate them with their stride info
			top					= 0;
			left				= 0;
			lsp					= gsProcesses;
			count				= 0;
			for (y = 0; y < gnHeight; y++)
			{
				for (x = 0; x < gnWidth; x++)
				{	// Initialize this entry
					lsp->status				= (count <= gnProcessCount) ? _JBM_EMPTY_SLOT : _JBM_UNUSED_SLOT;
					lsp->pipeHandle			= INVALID_HANDLE_VALUE;
					lsp->firstScore			= NULL;
					lsp->doubleBufferBitmap	= CreateCompatibleBitmap(GetDC(GetDesktopWindow()), _JBM_BACKGROUND_WIDTH, _JBM_BACKGROUND_HEIGHT);

					SetRect(&lsp->rc, left, top, left + _JBM_BACKGROUND_WIDTH, top + _JBM_BACKGROUND_HEIGHT);

					ZeroMemory(&lsp->pipeData,			sizeof(lsp->pipeData));
					ZeroMemory(&lsp->testHistory,		sizeof(lsp->testHistory));
					ZeroMemory(&lsp->testHistoryTimes,	sizeof(lsp->testHistoryTimes));

					// Move right for the next location/position
					left += _JBM_BACKGROUND_WIDTH;

					// Increment our counter for this record
					++count;

					// Move to next record
					++lsp;
				}
				top += _JBM_BACKGROUND_HEIGHT;
			}
		}
		// When we get here, every process has been initialized to its default values
	}




//////////
//
// Loads the resource bitmaps
//
/////
	void loadBitmaps(void)
	{
		ghbmpConnectionRunning		= LoadBitmap(ghInst, MAKEINTRESOURCE(IDB_CONNECTION));
		ghbmpConnectionEmptySlot	= LoadBitmap(ghInst, MAKEINTRESOURCE(IDB_CONNECTION_EMPTY_SLOT));
		ghbmpConnectionRed			= LoadBitmap(ghInst, MAKEINTRESOURCE(IDB_CONNECTION_UNUSED_SLOT));
		ghbmpConnectionYellow		= LoadBitmap(ghInst, MAKEINTRESOURCE(IDB_CONNECTION_SLOT_FILLED));
		ghbmpStatusbarHighLeft		= LoadBitmap(ghInst, MAKEINTRESOURCE(IDB_STATUSBAR_HIGH_LEFT));
		ghbmpStatusbarHighMiddle	= LoadBitmap(ghInst, MAKEINTRESOURCE(IDB_STATUSBAR_HIGH_MIDDLE));
		ghbmpStatusbarHighRight		= LoadBitmap(ghInst, MAKEINTRESOURCE(IDB_STATUSBAR_HIGH_RIGHT));
		ghbmpStatusbarLowLeft		= LoadBitmap(ghInst, MAKEINTRESOURCE(IDB_STATUSBAR_LOW_LEFT));
		ghbmpStatusbarLowMiddle		= LoadBitmap(ghInst, MAKEINTRESOURCE(IDB_STATUSBAR_LOW_MIDDLE));
		ghbmpStatusbarLowRight		= LoadBitmap(ghInst, MAKEINTRESOURCE(IDB_STATUSBAR_LOW_RIGHT));
	}




//////////
//
// Creates the desired font resources
//
/////
	void createFonts(void)
	{
		int height;

		height = -MulDiv(11, GetDeviceCaps(GetDC(GetDesktopWindow()), LOGPIXELSY), 72);
		ghfHeader	= CreateFontA(height, 0, 0, 0, FW_NORMAL, 0, 0, 0, DEFAULT_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, FF_DONTCARE, "Calibri");

		height = -MulDiv(8, GetDeviceCaps(GetDC(GetDesktopWindow()), LOGPIXELSY), 72);
		ghfPercent	= CreateFontA(height, 0, 0, 0, FW_BOLD, 0, 0, 0, DEFAULT_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, FF_DONTCARE, "Calibri");

		height = -MulDiv(10, GetDeviceCaps(GetDC(GetDesktopWindow()), LOGPIXELSY), 72);
		ghfTests	= CreateFontA(height, 0, 0, 0, FW_NORMAL, 0, 0, 0, DEFAULT_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, FF_DONTCARE, "Calibri");
	}




//////////
//
// Creates a window for the process
//
/////
	void createWindow(void)
	{
		int width, height, left, top, style;
		WNDCLASSEX wce;
		RECT rc, rcDesktop;

		// Register our class
		wce.cbSize			= sizeof(WNDCLASSEX);
		wce.style			= CS_HREDRAW | CS_VREDRAW;
		wce.lpfnWndProc		= WndProc;
		wce.cbClsExtra		= 0;
		wce.cbWndExtra		= 0;
		wce.hIcon			= LoadIcon(NULL, IDI_APPLICATION);
		wce.hCursor			= LoadCursor(NULL, IDC_ARROW);
		wce.hbrBackground	= CreateSolidBrush(_HEADER_BACKGROUND);	// Should be same color as the connections.bmp background
		wce.lpszMenuName	= NULL;
		wce.lpszClassName	= _JBM_Class_Name;
		wce.hInstance		= ghInst;
		wce.hIconSm			= LoadIcon(NULL, IDI_APPLICATION);

		// Register the application
		RegisterClassEx(&wce);

		// Size our window
		// Grab the desktop size
		GetWindowRect(GetDesktopWindow(), &rcDesktop);
		// Compute the pixel width
		width	= gnWidth	* _JBM_BACKGROUND_WIDTH;
		height	= gnHeight	* _JBM_BACKGROUND_HEIGHT;
		// Create a temporary holder for the size, so it can be adjusted by the window decoration widths (borders, title bar, etc.)
		style	= WS_CAPTION;
		SetRect(&rc, 0, 0, width, height);
		AdjustWindowRect(&rc, style, FALSE);

		// Re-compute the width based on the desktop width and the width with window border decorations
		width	= rc.right	- rc.left;
		height	= rc.bottom	- rc.top;
		left	= ((rcDesktop.right		- rcDesktop.left)	- width)	/ 2;
		top		= ((rcDesktop.bottom	- rcDesktop.top)	- height)	/ 2;

		// Create our window
		ghWnd	=	CreateWindowEx(0,
								   _JBM_Class_Name,
								   _JBM_Window_Name,
								   style,
								   left, top, width, height,
								   NULL, NULL, ghInst, NULL);

		if (ghWnd != NULL)
			ShowWindow(ghWnd, SW_SHOW);
		else
			MessageBox(NULL, L"Unable to create monitor window", L"Failure", MB_OK | MB_ICONEXCLAMATION);
	}




//////////
//
// Read and process Windows' events until we're closed externally, or
// the gbAppIsRunning flag is lowered (by the user)
//
/////
	void readEvents(void)
	{
		MSG msg;

		while (gbAppIsRunning && GetMessage(&msg, NULL, 0, 0))
		{
			TranslateMessage (&msg);
			DispatchMessage (&msg);
		}
	}



	LRESULT CALLBACK WndProc(HWND hwnd, UINT m, WPARAM w, LPARAM l)
	{
		int				i, x, y, left, top, count;
		SProcesses*		lsp;
		HDC				lhdcDoubleBuffer, lhdc1, lhdc2;
		PAINTSTRUCT		ps;
		RECT			rcSlot, rcZero;

		switch (m)
		{
			// If the user wants to close the application
			case WM_DESTROY:
				// then close it
				PostQuitMessage(WM_QUIT);
				break;

			case WM_PAINT:
				lhdcDoubleBuffer = BeginPaint(hwnd, &ps);
				lhdc1	= CreateCompatibleDC(lhdcDoubleBuffer);
				lhdc2	= CreateCompatibleDC(lhdcDoubleBuffer);
				SetRect(&rcZero, 0, 0, _JBM_BACKGROUND_WIDTH, _JBM_BACKGROUND_HEIGHT);

				if (gsProcesses != NULL)
				{	// We only draw the processes if everything was setup correctly
					lsp		= gsProcesses;
					left	= 0;
					top		= 0;
					count	= 0;
					for (y = 0; y < gnHeight; y++)
					{
						for (x = 0; x < gnWidth; x++)
						{	// Position the instance/slot rectangle
							SetRect(&rcSlot, left, top, left + _JBM_BACKGROUND_WIDTH, top + _JBM_BACKGROUND_HEIGHT);

							if (count >= gnProcessCount)
								lsp->status = _JBM_UNUSED_SLOT;

							// Paint it
							SelectObject(lhdc1, (HGDIOBJ)lsp->doubleBufferBitmap);
							paintThisProcess(lsp, lhdc1, lhdc2, rcZero);
							CopyRect(&lsp->rc, &rcSlot);

							// Update the double-buffer DC
							BitBlt(lhdcDoubleBuffer, rcSlot.left, rcSlot.top, rcSlot.left + _JBM_BACKGROUND_WIDTH, rcSlot.top + _JBM_BACKGROUND_HEIGHT, lhdc1, 0, 0, SRCCOPY);

							// Move to next process
							++lsp;
							++count;

							// Move horizontally for next position
							left += _JBM_BACKGROUND_WIDTH;
						}
						// Move vertically down to next row, and back to left-side
						left	= 0;
						top		+= _JBM_BACKGROUND_HEIGHT;
					}
				}

				DeleteDC(lhdc2);
				DeleteDC(lhdc1);
				EndPaint(hwnd, &ps);
				break;

			case _JBM_NEW_INSTANCE_REPORTING_IN:
				// A new instance has reported in, connect to its pipe
				connectToThisInstancePipeData((int)w);
				break;

			case _JBM_NEW_INSTANCE_FIRST_DATA:
				// A new instance has reported its first data set
				loadPipeData((int)w, _JBM_SLOT_FILLED);
				break;

			case _JBM_HAS_UPDATED_PIPE_DATA:
				// An existing instance has reported in, load it
				loadPipeData((int)w, _JBM_RUNNING);
				break;

			case _JBM_ARE_ALL_INSTANCES_LOADED:
				// They want to know if all slots are loaded yet
				if (gsProcesses != NULL)
				{
					lsp		= gsProcesses;
					count	= 0;
					for (i = 0; i < gnProcessCount; i++)
					{	// See how many have been filled
						count += (lsp->status >= _JBM_SLOT_FILLED) ? 1 : 0;
						++lsp;
					}
					if (count == gnProcessCount)
						return(1);
					//else return 0 below
				}
				//else return 0 below
				break;

			case _JBM_REQUEST_A_NEW_HANDLE:
				// They want us to assign a handle to them
				for (i = 0; i < gnProcessCount; i++)
				{	// Search for a slot that's 
					if (gsProcesses[i].status == _JBM_EMPTY_SLOT)
					{	// Return this slot
						return(i);
					}
				}
				// If we get here, no slots available
				return(-1);

			case _JBM_THIS_INSTANCE_IS_FINISHED:
				loadPipeData((int)w, _JBM_FINISHED);
				break;

			case _JBM_THIS_INSTANCE_HAS_EXITED:
				setSlotStatus((int)w, _JBM_EXITED);
				checkIfAllHaveExited();
				break;

			case _JBM_HAS_SCORING_DATA:
				loadScoringData((int)w);
				break;

			default:
				// Process the left-over messages
				return DefWindowProc(hwnd, m, w, l);
		}
		// If something was not done, let it go
		return 0;
	}

	void paintThisProcess(SProcesses* sp, HDC hdc, HDC hdc2, RECT rcSlot)
	{
		bool lbIsRunning;
		RECT rc;
		char text[256];

		// Copy over the background
		lbIsRunning = false;
		switch (sp->status)
		{
			case _JBM_EXITED:
			case _JBM_EMPTY_SLOT:
				SelectObject(hdc2, (HGDIOBJ)ghbmpConnectionEmptySlot);
				break;

			case _JBM_UNUSED_SLOT:
				SelectObject(hdc2, (HGDIOBJ)ghbmpConnectionRed);
				break;

			case _JBM_FINISHED:
			case _JBM_SLOT_FILLED:
				SelectObject(hdc2, (HGDIOBJ)ghbmpConnectionYellow);
				break;

			case _JBM_RUNNING:
				lbIsRunning = true;
				SelectObject(hdc2, (HGDIOBJ)ghbmpConnectionRunning);
				break;
		}
		BitBlt(hdc, rcSlot.left, rcSlot.top, rcSlot.left + _JBM_BACKGROUND_WIDTH, rcSlot.top + _JBM_BACKGROUND_HEIGHT, hdc2, 0, 0, SRCCOPY);

		if (sp->status > _JBM_UNUSED_SLOT)
		{
			// Update the header
			SelectObject(hdc, (HGDIOBJ)ghfHeader);
			SetTextColor(hdc, _HEADER_FOREGROUND);
			SetBkColor(hdc, _HEADER_BACKGROUND);
			SetBkMode(hdc, TRANSPARENT);
			setRectRelativeTo(&rcSlot, &rc, 3,3,143,19);
			if (sp->pipeData.instance.name[0] != 0)
				DrawTextA(hdc, sp->pipeData.instance.name, sp->pipeData.instance.length, &rc, DT_CENTER | DT_VCENTER | DT_SINGLELINE);
			else
				DrawTextA(hdc, "Waiting...", 10, &rc, DT_CENTER | DT_VCENTER | DT_SINGLELINE);

			// When the process is running, update additional information
			if (sp->status >= _JBM_RUNNING)
			{	// Update the test currently running
				SelectObject(hdc, (HGDIOBJ)ghfTests);
				SetTextColor(hdc, _TEST_FOREGROUND);
				SetBkColor(hdc, _TEST_BACKGROUND);
				SetBkMode(hdc, OPAQUE);
				setRectRelativeTo(&rcSlot, &rc, 3,25,143,42);
				DrawTextA(hdc, sp->pipeData.test.name, sp->pipeData.test.length, &rc, DT_LEFT | DT_VCENTER | DT_SINGLELINE);

				// Update the status messages
				SetTextColor(hdc, _HISTORY_FOREGROUND);
				SetBkColor(hdc, _HISTORY_BACKGROUND);
				SetBkMode(hdc, OPAQUE);
				// First
				setRectRelativeTo(&rcSlot, &rc, 3,75,143,91);
				DrawTextA(hdc, sp->testHistory[0].name, sp->testHistory[0].length, &rc, DT_LEFT | DT_VCENTER | DT_SINGLELINE);
				SetRect(&rc, 100,75,143,91);
				DrawTextA(hdc, sp->testHistoryTimes[0].name, sp->testHistoryTimes[0].length, &rc, DT_RIGHT | DT_VCENTER | DT_SINGLELINE);
				// Second
				setRectRelativeTo(&rcSlot, &rc, 3,75,143,91);
				DrawTextA(hdc, sp->testHistory[1].name, sp->testHistory[1].length, &rc, DT_LEFT | DT_VCENTER | DT_SINGLELINE);
				setRectRelativeTo(&rcSlot, &rc, 100,75,143,91);
				DrawTextA(hdc, sp->testHistoryTimes[1].name, sp->testHistoryTimes[1].length, &rc, DT_RIGHT | DT_VCENTER | DT_SINGLELINE);
				// Third
				setRectRelativeTo(&rcSlot, &rc, 3,75,143,91);
				DrawTextA(hdc, sp->testHistory[2].name, sp->testHistory[2].length, &rc, DT_LEFT | DT_VCENTER | DT_SINGLELINE);
				setRectRelativeTo(&rcSlot, &rc, 100,75,143,91);
				DrawTextA(hdc, sp->testHistoryTimes[2].name, sp->testHistoryTimes[2].length, &rc, DT_RIGHT | DT_VCENTER | DT_SINGLELINE);
				// Fourth
				setRectRelativeTo(&rcSlot, &rc, 3,75,143,91);
				DrawTextA(hdc, sp->testHistory[3].name, sp->testHistory[3].length, &rc, DT_LEFT | DT_VCENTER | DT_SINGLELINE);
				setRectRelativeTo(&rcSlot, &rc, 100,75,143,91);
				DrawTextA(hdc, sp->testHistoryTimes[3].name, sp->testHistoryTimes[3].length, &rc, DT_RIGHT | DT_VCENTER | DT_SINGLELINE);

				// Update the status bar for the current test
				setRectRelativeTo(&rcSlot, &rc, 2,46,144,58);
				sprintf_s(text, sizeof(text), "Test: %3.0f%%\000", verifyPercent(sp->pipeData.testPercentCompleted) * 100.0f);
				drawStatusBar(hdc, hdc2, rc, verifyPercent(sp->pipeData.testPercentCompleted), text);

				// Update the status bar for the overall completion
				setRectRelativeTo(&rcSlot, &rc, 2,59,144,72);
				sprintf_s(text, sizeof(text), "Overall: %3.0f%%\000", verifyPercent(sp->pipeData.overallPercentCompleted) * 100.0f);
				drawStatusBar(hdc, hdc2, rc, verifyPercent(sp->pipeData.overallPercentCompleted), text);
			}
		}
	}

	float verifyPercent(float percent)
	{
		// Verify the percent value is in range
		if (percent > 1.0f && percent <= 100.0f)
		{	// It's in the range 0..100, so make it in the range 0..1
			percent = percent / 100.0f;
		}
		if (percent < 0.0f)
			percent = 0.0f;
		if (percent > 1.0f)
			percent = 1.0f;

		return(percent);
	}

	void setRectRelativeTo(RECT* rcOutter, RECT* rcInner, int left, int top, int right, int bottom)
	{
		SetRect(rcInner, rcOutter->left + left, rcOutter->top + top, rcOutter->left + right, rcOutter->top + bottom);
	}

	void drawStatusBar(HDC hdc, HDC hdc2, RECT& rc, float percent, char* text)
	{
		float width;
		RECT lrc;

		// Grab the overall width
		width = (float)(rc.right - rc.left);

		// Do the highlighted portion
		lrc.left	= rc.left;
		lrc.top		= rc.top;
		lrc.right	= (int)((float)lrc.left + (width * percent));
		lrc.bottom	= rc.bottom;
		drawStatusBarSegment(hdc, hdc2, lrc, ghbmpStatusbarHighLeft, ghbmpStatusbarHighMiddle, ghbmpStatusbarHighRight, 2, 1, 2, text);

		// Do the lowlighted portion
		lrc.left	= lrc.right + 1;
		lrc.right	= rc.right;
		drawStatusBarSegment(hdc, hdc2, lrc, ghbmpStatusbarLowLeft, ghbmpStatusbarLowMiddle, ghbmpStatusbarLowRight, 2, 1, 2, text);

		// Overlay the text
		SelectObject(hdc, (HGDIOBJ)ghfPercent);
		SetTextColor(hdc, _PERCENT_FOREGROUND);
		SetBkColor(hdc, _PERCENT_BACKGROUND);
		SetBkMode(hdc, TRANSPARENT);
		DrawTextA(hdc, text, strlen(text), &rc, DT_CENTER | DT_TOP | DT_SINGLELINE);
	}

	void drawStatusBarSegment(HDC hdc, HDC hdc2, RECT& rc, HBITMAP left, HBITMAP middle, HBITMAP right, int leftWidth, int middleWidth, int rightWidth, char* text)
	{
		int x, height, width;

		// Grab the height, width
		height	= rc.bottom	- rc.top;
		width	= rc.right	- rc.left;

		// Paint the left side
		SelectObject(hdc2, (HGDIOBJ)left);
		BitBlt(hdc, rc.left, rc.top, leftWidth, height, hdc2, 0, 0, SRCCOPY);

		// Paint the middle
		SelectObject(hdc2, (HGDIOBJ)middle);
		for (x = rc.left + leftWidth; x < rc.right - rightWidth; x++)
		{	// Repeat the middle portion until we fill in the whole area
			BitBlt(hdc, x, rc.top, middleWidth, height, hdc2, 0, 0, SRCCOPY);
		}

		// Paint the right side
		SelectObject(hdc2, (HGDIOBJ)right);
		BitBlt(hdc, rc.right - rightWidth, rc.top, rightWidth, height, hdc2, 0, 0, SRCCOPY);
	}

	void connectToThisInstancePipeData(int slot)
	{
		SProcesses* sp;
		wchar_t pipeName[256];

		if (slot >= 0 && slot < _JBM_MAX_CONNECTIONS)
		{	// We have a valid slot, see what we need to do

			// Grab this instance
			sp = gsProcesses + slot;

			// Connect to the pipe for this one
			wsprintf(pipeName, _JBM_Pipe_wsprintf_string, _JBM_Pipe_Name_Prefix, slot);
			sp->pipeHandle = CreateFile(pipeName, GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
			if (sp->pipeHandle != INVALID_HANDLE_VALUE)
			{	// The connection has been made
				sp->status = _JBM_SLOT_FILLED;
				// Repaint this item
				InvalidateRect(ghWnd, &sp->rc, FALSE);
			}
		}
	}

	void loadPipeData(int slot, int newStatus)
	{
		SProcesses* sp;
		SPipeData pipeData;
		DWORD numread;

		if (slot >= 0 && slot < _JBM_MAX_CONNECTIONS)
		{	// We have a valid slot, see what we need to do
			// Grab this instance
			sp = gsProcesses + slot;

			if (sp->pipeHandle != INVALID_HANDLE_VALUE)
			{	// Load the pipe data
				ReadFile(sp->pipeHandle, &pipeData, sizeof(pipeData), &numread, NULL);
				if (numread == sizeof(pipeData))
				{	// A valid message
					memcpy(&sp->pipeData, &pipeData, sizeof(pipeData));
					sp->status = newStatus;
					// Repaint this item
					InvalidateRect(ghWnd, &sp->rc, FALSE);
				}
			}
		}
	}

	void loadScoringData(int slot)
	{
		SProcesses* sp;
		SPipeData pipeData;
		SScoringDataLL* sd;
		SScoringDataLL** addHere;
		DWORD numread;

		if (slot >= 0 && slot < _JBM_MAX_CONNECTIONS)
		{	// We have a valid slot, see what we need to do
			// Grab this instance
			sp = gsProcesses + slot;

			if (sp->pipeHandle != INVALID_HANDLE_VALUE)
			{	// Load the pipe data
				ReadFile(sp->pipeHandle, &pipeData, sizeof(pipeData), &numread, NULL);
				if (numread == sizeof(pipeData))
				{	// A valid message
					// Append the scoring data from the pipeData
					sd = sp->firstScore;
					if (sd == NULL)
					{	// This is the first score
						addHere = &sp->firstScore;
					} else {
						// append to the end of the linked list
						while (sd->next != NULL)
							sd = sd->next;
						// Add to the end
						addHere = &sd->next;
					}
					*addHere = (SScoringDataLL*)malloc(sizeof(SScoringData));
					if (*addHere != NULL)
					{	// Copy the user data to our list
						memcpy(&(*addHere)->score, &pipeData.score, sizeof(SScoringData));
						(*addHere)->next = NULL;
					}
					//else an error allocating memory, which means the system's in an unstable state
				}
			}
		}
	}

	void setSlotStatus(int slot, int newStatus)
	{
		SProcesses* sp;

		if (slot >= 0 && slot < _JBM_MAX_CONNECTIONS)
		{	// We have a valid slot, see what we need to do
			// Grab this instance
			sp = gsProcesses + slot;
			// Set the status
			sp->status = newStatus;
			// Repaint the item
			InvalidateRect(ghWnd, &sp->rc, FALSE);
		}
	}

	void checkIfAllHaveExited(void)
	{
		SProcesses* lsp;
		int i, count;

		count	= 0;
		lsp		= gsProcesses;
		for (i = 0; i < gnProcessCount; i++)
		{
			count += (lsp->status == _JBM_EXITED) ? 1 : 0;
			++lsp;
		}
		if (count == gnProcessCount)
		{	// All are finished
			exit(0);
		}
	}
