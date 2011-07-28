// testactivex.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"
#include "testactivex.h"
#include "excel8.h"


#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// The one and only application object

CWinApp theApp;

using namespace std;

int _tmain(int argc, TCHAR* argv[], TCHAR* envp[])
{
	int nRetCode = 0;

	// initialize MFC and print and error on failure
	if (!AfxWinInit(::GetModuleHandle(NULL), NULL, ::GetCommandLine(), 0))
	{
		// TODO: change error code to suit your needs
		_tprintf(_T("Fatal Error: MFC initialization failed\n"));
		nRetCode = 1;
	}
	else
	{
		OleVariant covTrue((short)TRUE), covFalse((short)FALSE), covOptional((long)DISP_E_PARAMNOTFOUND, VT_ERROR);
		 
		_Application excelApp;
		 
		if(!excelApp.CreateDispatch("Excel.Application"))
		{
			// error handling
		}
		 
		excelApp.GetWorkbooks().Add(covOptional);
		excelApp.GetRange(COleVariant("A1"),COleVariant("C6")).Select();
		excelApp.GetActiveCell().SetFormula("Hello World!");
		excelApp.SetVisible(TRUE);
	}

	return nRetCode;
}
