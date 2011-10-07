//////////
//
// jbm_common.h
//
// Named pipes are used to communicate data from the running
// JVM instances to the JBM.  They follow a naming convention:
//
//		sprintf(dest, "%s%04u\000", _JBM_Pipe_Name_Prefix, instanceNumber)
//
// Data communicated is ONLY between caller as WRITE access, and
// JBM as READ access.  InstanceNumbers are determined by the data
// specified by calls to JBM's WndProc's 
//
//



// Constants used to identify and relate things
#define			_JBM_MAX_CONNECTIONS							32		// 32-cores should be sufficient (for 2011/2012)
																		// Note:  If this value changes, update the determineLayout() function

const wchar_t	_JBM_Class_Name[]								= L"JavaBenchmarkMonitorForOPBM";
const wchar_t	_JBM_Window_Name[]								= L"Java Benchmark Monitor";
const wchar_t	_JBM_Pipe_Name_Prefix[]							= L"\\\\.\\pipe\\JBM Data Pipe For Handle ";
const wchar_t	_JBM_Pipe_wsprintf_string[]						= L"%s%04u\000";
#ifdef _JBM_OWNER
// The following is only used by the app which started JBM, to communicate with the data in this pipe
const wchar_t	_JBM_Owner_Pipe_Name[]							= L"\\\\.\\pipe\\JBM Owner Data Pipe";
#endif

// Used by WndProc as MSG, WPARAM and LPARAM have related data
#define			_JBM_NEW_INSTANCE_REPORTING_IN					WM_USER + 1
#define			_JBM_NEW_INSTANCE_FIRST_DATA					WM_USER + 2
#define			_JBM_HAS_UPDATED_PIPE_DATA						WM_USER + 3
#define			_JBM_ARE_ALL_INSTANCES_LOADED					WM_USER + 4
#define			_JBM_REQUEST_A_NEW_HANDLE						WM_USER + 5
#define			_JBM_THIS_INSTANCE_IS_FINISHED					WM_USER + 6
#define			_JBM_THIS_INSTANCE_HAS_EXITED					WM_USER + 7
#define			_JBM_HAS_SCORING_DATA							WM_USER + 8
#ifdef _JBM_OWNER
// The following are only used by the app which started JBM, to communicate with the JBM to find out its status
#define			_JBM_OWNER_REPORTING_IN							WM_USER + 9
#define			_JBM_OWNER_REQUESTING_IF_ALL_HAVE_EXITED		WM_USER + 10
#define			_JBM_OWNER_REQUESTING_SCORING_DATA				WM_USER + 11
#define			_JBM_OWNER_IS_REQUESTING_THE_JBM_SELF_TERMINATE	WM_USER + 12
#endif


// Data used in the named pipe to communicate with JBM
struct SPipeDataNames
{
	int			length;								// Length of the name stored in name
	char		name[32];							// Text of the name
};

struct SScoringData
{
	SPipeDataNames	name;							// Name of the test with this scoring data

	double			minScore;						// Minimum score observed
	double			maxScore;						// Maximum score observed
	double			avgScore;						// Average
	double			geoScore;						// Geometric mean
	double			CVScore;						// Coefficient of variation

	double			minTime;						// Minimum score observed
	double			maxTime;						// Maximum score observed
	double			avgTime;						// Average
	double			geoTime;						// Geometric mean
	double			CVTime;							// Coefficient of variation
};

struct SPipeData
{
	bool			hasStarted;						// true or false indicating if the process has started running yet
	bool			hasCompleted;					// true or false indicating whether or not the process is finished, and has exited

	float			testPercentCompleted;			// How much of the individual test is completed
	float			overallPercentCompleted;		// How much of the overall test is completed?
	float			lastTestTimeInSeconds;			// How long did that last test take?

	SPipeDataNames	instance;						// Name assigned to the instance (Typically "JVM x on Core y"
	SPipeDataNames	test;							// Name of the current test being run
	SScoringData	score;							// Holds data transferred for scores (see _JBM_HAS_SCORING_DATA message)
};
