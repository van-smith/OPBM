========================================================================
                  DLL APPLICATION : EnumThreads
========================================================================
NOTE:			This solution is a stand-alone.
				It is not part of OPBM directly.
				It is used by the watchdog project, but
				its code base is maintained independently.

			The code was taken from the public domain at:
  http://www.codeproject.com/KB/system/ntenumthreads.aspx?display=Print
========================================================================

StdAfx.h, StdAfx.cpp
    These files are used to build a precompiled header (PCH) file
    named EnumThreads.pch and a precompiled types file named StdAfx.obj.

Resource.h
    This is the standard header file, which defines new resource IDs.
    Microsoft Visual C++ reads and updates this file.

PdhUtil.h, PdhUtil.cpp
    There is no way to access thread information within Windows except
	throught the Performance Data Helper libraries (PDH), which allows the
	performance data of each thread to be examined.  As such, the PDH is
	used to enumerate all of the threads for a given process.

	This is sometimes a necessity when trying to find the owner of an HWND.

EnumThreads.h, EnumThreads.cpp
    Contains code for testing the functionality of PdhUtil functions.
	Must be converted to a console app, or invoked externally to run
	PdhTest();

To use this DLL, refer to the ..\common\enumthreads.h header file, which
includes the function LoadEnumThreadsDll(), and handles the allocation of
all of the externally visible functions:

	EnumThreads()
	EnumProcessNames()
	EnumProcessThreads()
	EnumProcessThreadsEx()

========================================================================
