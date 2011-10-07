/*
 * OPBM's Java Benchmark
 *
 * This class is the top-level class of the OPBM Java Benchmark test.  It is
 * a single-threaded class that executes a series of workloads:
 *
 *		o Integer sort
 *		o String construction
 *		o AES encrypt / decrypt
 *		o SHA-256
 *		o STREAM
 *		o Provide a real-time completion report to an external monitor app
 *
 * To run the benchmark:
 *		(new Benchmark()).run()
 *
 * This benchmark was designed for OPBM and has external requirements for that
 * entity.  Specifically there are two C++ apps that must be present:
 *
 *		1)  C++ JBM, creates jbm.exe (Java Benchmark Monitor)
 *		2)	C++ Benchmark, creates benchmark32.dll and benchmark64.dll
 * Additional support comes from the classes:
 *		3)  RandomData, holds all random data related initialization
 *		4)  Utils, holds UUID logic to assign a unique connection to the JBM
 *
 * JBM is a Win32 app that resides in memory before this benchmark instance is
 * launched.  It creates a status window that handles initial coordination for
 * a simultaneous launch of multiple instances of this benchmark that are
 * launched, one per core.  It then synchronizes their launch, and graphically
 * displays their progress until finished.
 *
 * Benchmark is a 32-bit and 64-bit JNI DLL which is required by this app to
 * communicate with the JBM.  It reports its connection, receives start
 * coordination, indicates which test being run, and test completion status and
 * timing, and tells the JBM when it's exited completely.
 *
 * -----
 * Last Updated:  Oct 6, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.0
 *
 */

package benchmark;

import benchmark.common.NanoTimer;
import benchmark.common.RandomData;
import benchmark.common.Utils;
import benchmark.tests.AesData;
import benchmark.tests.StringTest;
import benchmark.tests.SHA256;
import benchmark.tests.IntegerSort;
import benchmark.tests.AesEncrypt;
import benchmark.tests.AesDecrypt;
import benchmark.tests.Stream;

public class Benchmark
{
//////////
// Requires benchmark32.dll and benchmark64.dll:
////
	static {
		if (System.getProperty("sun.arch.data.model").equals("32"))
		{	// 32-bit JVM
			System.loadLibrary("benchmark32");
			System.out.println("Running 32-bit JVM");
		} else {
			// 64-bit JVM
			System.loadLibrary("benchmark64");
			System.out.println("Running 64-bit JVM");
		}
		if (!didBenchmarkDllLoadOkayN())
		{	// Did the benchmarkNN.dll load okay? (it has to connect with a running JBM to have successfully launched)
			System.out.println("Unable to run this benchmark:  Unable to find JBM. Make sure JBM is running first.");
			System.exit(-1);
		}
	}
//////////
// NATIVE functions in the JNI Benchmark DLL:
////
	public native static boolean	didBenchmarkDllLoadOkayN();											// Called after the DLL is loaded in the static block above to see if it loaded okay, and found the JBM okay
	public native static int		firstConnectN(String uuid, String instanceTitle, int testCount);	// Called to connect to the monitor app, identifying itself
	public native static boolean	okayToBeginN();														// Called after firstConnectN() to see if all other JVMs have launched and reported in yet
	public native static void		reportTestN(int handle, int test, String name);						// Called to indicate a new test has started
	public native static void		reportTestScoreAndTimeN(int handle, String scoreName, double minScore, double maxScore, double avgScore, double geoScore, double cvScore, double minTime, double maxTime, double avgTime, double geoTime, double cvTime);	// Called to report a time for this test
	public native static void		reportCompletionN(int handle, float percent);						// Called to update the completion status of the current test
	public native static void		reportExitingN(int handle);											// Tell JBM that we're exiting



//////////
// Main benchmark code begins here
////
	/**
	 * Constructor
	 */
	public Benchmark()
	{
		RandomData.initialize();
		AesData.initialize();
	}

	/**
	 * Main test entry point, executes each test in sequence
	 */
	public void run()
	{
		integerSort();			// Sort 128KB data set
		string();				// Build 32KB string This test Generates the random strings needed by aesEncrypt() and aesDecrypt()
		aesEncrypt();			// Encrypt 10K strings
		aesDecrypt();			// Decrypt and compare 10K strings
		sha256();				// Compute 20K SHA-256 hashes
		stream();				// STREAM memory bandwidth test

		// Tell the JBM we're done by setting the current test beyond the max, and reporting 100.0% finished
		reportTestN(m_handle, _TEST_MAX_COUNT + 1, "Finished");					// Set counter beyond max
		reportCompletionN(m_handle, 1.0f);										// Indicate 100% finished
	}
	/**
	 * Performs sort on 128KB of 64-bit integer array
	 * @param test the test number to report to the monitor app through native interface
	 */
	public void integerSort()
	{
		reportTestN(m_handle, m_testNumber++, "Integer Sort");
		m_is = new IntegerSort(m_handle, 1000, 16384);
		m_is.run();
	}

	/**
	 * Performs String builder test, building a 32KB String through a random
	 * selection from a fixed string.
	 * @param test the test number to report to the monitor app through native interface
	 */
	public void string()
	{
		Benchmark.reportTestN(m_handle, Benchmark.m_testNumber++, "Build String");
		m_sg = new StringTest(m_handle);
		m_sg.run();
	}

	/**
	 * Performs AES encrypt test, constructs 2,000 AES encrypted strings
	 * @param test the test number to report to the monitor app through native interface
	 */
	public void aesEncrypt()
	{
		// Try to setup our cipher encryption engine
		AesData.initializeCipherEncryptionEngine();
		// If it's valid, run the test
		if (AesData.m_isValid)
		{	// We're good
			// Report the test we're on
			reportTestN(m_handle, m_testNumber++, "AES Encrypt");
			m_ae = new AesEncrypt(m_handle);
			m_ae.run();
		}
	}

	/**
	 * Performs AES decrypt test, decrypts strings previously constructed in
	 * aesEncrypt()
	 * @param test the test number to report to the monitor app through native interface
	 */
	public void aesDecrypt()
	{
		// If it's valid, run the test
		if (AesData.m_isValid)
		{	// We're good
			// Report the test we're on
			reportTestN(m_handle, m_testNumber++, "AES Decrypt");
			m_ad = new AesDecrypt(m_handle);
			m_ad.run();
		}
	}

	/**
	 * Performs SHA-256 encryption test
	 * @param test the test number to report to the monitor app through native interface
	 */
	public void sha256()
	{
		reportTestN(m_handle, m_testNumber++, "SHA-256");
		m_sh = new SHA256(m_handle);
		m_sh.run();
	}

	/**
	 * Performs STREAM test.  STREAM is part of miniBench, and is a C++ app.
	 * It runs in a JNI DLL.
	 * @param test
	 */
	public void stream()
	{
		reportTestN(m_handle, m_testNumber++, "STREAM");
		m_sm = new Stream(m_handle);
		m_sm.run();
	}

	/**
	 * Main app entry point, initiates the test after pausing several seconds.
	 * Command line syntax is:  "benchmark.jar 1" (where 1 is the instance,
	 * must be a value >= 1 and <= the maximum value passed to JBM.exe as its
	 * parameter)
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException
	{
		int waitCount;

		if (args[0] == null)
		{	// No command line parameter for the name of this instance was provided
			System.out.println("Syntax:  benchmark.jar \"Instance Title\"");
			System.out.println("Syntax:  c:\\path\\to\\java.exe -jar benchmark.jar \"Instance Title\"");
			System.exit(-1);
		}

		// Initialize everything up front, before we check in
		Benchmark bm = new Benchmark();

		// Assign a UUID, initialize our test number at the start, and report in to the monitor app
		m_uuid			= Utils.getUUID();
		m_testNumber	= 1;													// An increment used to help JBM determine how far through the overall testing we are
		m_handle		= firstConnectN(m_uuid, args[0]/*title*/, _TEST_MAX_COUNT);
		if (m_handle < 0)
		{	// Some error connecting to JBM
			System.out.println("Error connecting to the JBM. Please ensure the JBM is running first.");
			System.exit(-2);
		}
		System.out.println("This benchmark instnace was assigned handle \"" + Integer.toString(m_handle) + "\".");

//////////
//
// Now, wait until all launched JVM instances report in, allowing the
// compute portions of the test to be accurate, rather than incurring
// overhead due to disk access, loading, required synchronization for
// multiple JVM instantiation, causing notable pauses in computation, etc.
//
//////////
		// Only wait for up to _TIMEOUT_SECONDS before terminating in error.
		waitCount = 0;
		while (waitCount < _TIMEOUT_SECONDS * _STARTUP_POLLS_PER_SECOND)
		{
			if (okayToBeginN())
			{	// We're good, let's go
				break;
			}
			// Pause before asking again
			try {
				Thread.sleep(1000 / _STARTUP_POLLS_PER_SECOND);
			} catch (InterruptedException ex) {
			}

			++waitCount;
		}
		if (waitCount >= _TIMEOUT_SECONDS * _STARTUP_POLLS_PER_SECOND)
		{	// All of the JVMs did not launch within the timeout
			System.out.println("Error waiting for all JVMs to launch. Timeout after " + Integer.toString(_TIMEOUT_SECONDS) + " seconds.");
			System.exit(-3);
		}

		// Run the benchmark
		System.out.println("Benchmark \"" + args[0] + "\" begins.");
		bm.run();
		System.out.println("Benchmark \"" + args[0] + "\" ends.");

		// All done
		reportExitingN(m_handle);
		System.exit(0);
	}

	// Class variables
	private static String		m_uuid;											// UUID assigned at startup, used to identify this instance to the monitor app
	private static int			m_handle;										// Handle assigned by JBM
	public	static int			m_testNumber;									// Current test being executed, starts at 1 and runs to _TEST_MAX_COUNT

	// Tests
	private	IntegerSort			m_is;											// Instantiated in integer()
	private StringTest			m_sg;											// Instantiated in string()
	private AesEncrypt			m_ae;											// Instantiated in aesEncrypt()
	private AesDecrypt			m_ad;											// Instantiated in aesDecrypt()
	private SHA256				m_sh;											// Instantiated in sha256()
	private Stream				m_sm;											// Instantiated in stream()

	// Class constants
	private static final int	_TIMEOUT_SECONDS			= 120;
	private static final int	_STARTUP_POLLS_PER_SECOND	= 10;
	private static final int	_MAX_INTEGER_TESTS			= 1;
	private static final int	_MAX_STRING_TESTS			= 1;
	private static final int	_MAX_AES_ENCRYPT_TESTS		= 1;
	private static final int	_MAX_AES_DECRYPT_TESTS		= 1;
	private static final int	_MAX_SHA_256_TESTS			= 1;
	private static final int	_MAX_STREAM_TESTS			= 1;
	private static final int	_TEST_MAX_COUNT				= _MAX_INTEGER_TESTS + _MAX_AES_ENCRYPT_TESTS + _MAX_AES_DECRYPT_TESTS + _MAX_SHA_256_TESTS + _MAX_STRING_TESTS + _MAX_STREAM_TESTS;
}
