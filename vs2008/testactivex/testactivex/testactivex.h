// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the TESTACTIVEX_EXPORTS
// symbol defined on the command line. this symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// TESTACTIVEX_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.
#ifdef TESTACTIVEX_EXPORTS
#define TESTACTIVEX_API __declspec(dllexport)
#else
#define TESTACTIVEX_API __declspec(dllimport)
#endif

// This class is exported from the testactivex.dll
class TESTACTIVEX_API Ctestactivex {
public:
	Ctestactivex(void);
	// TODO: add your methods here.
};

extern TESTACTIVEX_API int ntestactivex;

TESTACTIVEX_API int fntestactivex(void);
