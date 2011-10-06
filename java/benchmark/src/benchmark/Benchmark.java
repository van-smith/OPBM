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
 *
 * JBM is a Win32 app that resides in memory, creates a status window that
 * handles initial coordination for a simultaneous launch of each instance
 * that's launched (1:1 ratio of JVMs per cores on the executing machine),
 * and then graphically displays the
 *
 * Benchmark is a 32-bit or 64-bit JNI DLL which required by this app to
 * communicate its connection, start coordination, test being run, and test
 * completion status, to the JBM monitor app.
 *
 * -----
 * Last Updated:  Sep 30, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.2.0
 *
 */

package benchmark;
import benchmark.common.Utils;
import benchmark.tests.AesData;
import benchmark.tests.StringTest;
import benchmark.tests.SHA256;
import benchmark.tests.IntegerSort;
import benchmark.tests.AesEncrypt;
import benchmark.tests.AesDecrypt;

public class Benchmark
{
//////////
// NATIVE functions in benchmark64.dll:
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
	public native static boolean	didBenchmarkDllLoadOkayN();											// Called after the DLL is loaded in the static block above to see if it loaded okay, and found the JBM okay
	public native static int		firstConnectN(String uuid, String instanceTitle, int testCount);	// Called to connect to the monitor app, identifying itself
	public native static boolean	okayToBeginN();														// Called after firstConnectN() to see if all other JVMs have launched and reported in yet
	public native static void		reportTestN(int handle, int test, String name);						// Called to indicate a new test has started
	public native static void		reportCompletionN(int handle, float percent);						// Called to update the completion status of the current test
	public native static void		reportExitingN(int handle);											// Tell JBM that we're exiting
	public native static void		streamN(int handle);												// Test from miniBench, written in C++

	/**
	 * Constructor
	 */
	public Benchmark(int handle)
	{
		m_handle	= handle;
		AesData.initialize();
	}

	/**
	 * Main test entry point, executes each test in sequence
	 */
	public void run()
	{
		m_testNumber = 1;

		integerSort();
		string();			// This test Generates the random strings needed by aesEncrypt() and aesDecrypt()
		aesEncrypt();
		aesDecrypt();
		sha256();
		stream();

		// Tell the JBM we're done by setting the current test beyond the max, and reporting 100.0% finished
		reportTestN(m_handle, _TEST_MAX_COUNT + 1, "Finished");					// Set counter beyond max
		reportCompletionN(m_handle, 1.0f);										// Indicate 100% finished
	}

	/**
	 * Performs sort on 128KB of 64-bit integer array
	 * @param test the test number to report to the monitor app through native interface
	 */
	private static final int _MAX_INTEGER_TESTS = 5;
	public void integerSort()
	{
		IntegerSort is;

		// #1 - 8KB test
		reportTestN(m_handle, m_testNumber++, "Integer Sort 8KB");
		is = new IntegerSort(m_handle, 1500, 1024);
		is.run();

		// #2 - 64KB test
		reportTestN(m_handle, m_testNumber++, "Integer Sort 64KB");
		is = new IntegerSort(m_handle, 750, 8192);
		is.run();

		// #3 - 256KB test
		reportTestN(m_handle, m_testNumber++, "Integer Sort 256KB");
		is = new IntegerSort(m_handle, 200, 32768);
		is.run();

		// #4 - 1MB test
		reportTestN(m_handle, m_testNumber++, "Integer Sort 1MB");
		is = new IntegerSort(m_handle, 75, 128000);
		is.run();

		// #5 - 8MB test
		reportTestN(m_handle, m_testNumber++, "Integer Sort 8MB");
		is = new IntegerSort(m_handle, 10, 1024000);
		is.run();
	}

	/**
	 * Performs String builder test, building a 32KB String through a random
	 * selection from a fixed string.
	 * @param test the test number to report to the monitor app through native interface
	 */
	private static final int _MAX_STRING_TESTS = 2;
	public void string()
	{
		StringTest st = new StringTest(m_handle);
		st.run();
	}

	/**
	 * Performs AES encrypt test, constructs 2,000 AES encrypted strings
	 * @param test the test number to report to the monitor app through native interface
	 */
	private static final int _MAX_AES_ENCRYPT_TESTS = 1;
	public void aesEncrypt()
	{	// Report the test we're on
		reportTestN(m_handle, m_testNumber++, "AES Encrypt");

		// Try to setup our cipher encryption engine
		AesData.initializeCipherEncryptionEngine();

		// If it's valid, run the test
		if (AesData.m_isValid)
		{	// We're good
			AesEncrypt ae = new AesEncrypt(m_handle);
			ae.run();
		}
	}

	/**
	 * Performs AES decrypt test, decrypts strings previously constructed in
	 * aesEncrypt()
	 * @param test the test number to report to the monitor app through native interface
	 */
	private static final int _MAX_AES_DECRYPT_TESTS = 1;
	public void aesDecrypt()
	{	// Report the test we're on
		reportTestN(m_handle, m_testNumber++, "AES Decrypt");

		// If it's valid, run the test
		if (AesData.m_isValid)
		{	// We're good
			AesDecrypt ad = new AesDecrypt(m_handle);
			ad.run();
		}
	}

	/**
	 * Performs SHA-256 encryption test
	 * @param test the test number to report to the monitor app through native interface
	 */
	private static final int _MAX_SHA_256_TESTS = 1;
	public void sha256()
	{
		reportTestN(m_handle, m_testNumber++, "SHA-256");
		SHA256 sh = new SHA256(m_handle);
		sh.run();
	}

	/**
	 * Performs STREAM test.  STREAM is part of miniBench, and is a C++ app.
	 * It runs in a JNI DLL.
	 * @param test
	 */
	private static final int _MAX_STREAM_TESTS = 1;
	public void stream()
	{
		reportTestN(m_handle, m_testNumber++, "STREAM");
		streamN(m_handle);
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
		int waitCount, handle;
		boolean continueWaiting;
		String title;

		if (args[0] == null)
		{	// No command line parameter for the name of this instance was provided
			System.out.println("Syntax:  benchmark.jar \"Instance Title\"");
			System.exit(-1);
		}

		// Grab our title
		title = args[0];

		// Assign a UUID and report in to the monitor app
		m_uuid		= Utils.getUUID();
		handle		= firstConnectN(m_uuid, title, _TEST_MAX_COUNT);
		if (handle < 0)
		{	// Some error connecting
			System.out.println("Error connecting to monitor app. Please ensure monitor app is running first.");
			System.exit(-2);
		}
		System.out.println("Benchmark given handle \"" + Integer.toString(handle) + "\".");
		Benchmark bm = new Benchmark(handle);

		// Wait until all launched JVM instances report in, allowing the
		// compute portions of the test to be accurate, rather than incurring
		// overhead due to disk loading, JVM initialization, etc.
		// Wait for up to _TIMEOUT_SECONDS before terminating in error.
		continueWaiting		= true;
		waitCount			= 0;
		while (continueWaiting && waitCount < _TIMEOUT_SECONDS * 10)
		{
			if (okayToBeginN())
			{	// We're good, let's go
				break;
			}
			// Pause before asking again
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
			}

			++waitCount;
		}
		if (waitCount >= _TIMEOUT_SECONDS * 10)
		{	// All of the JVMs did not launch within the timeout
			System.out.println("Error waiting for all JVMs to launch. Timeout after " + Integer.toString(_TIMEOUT_SECONDS) + " seconds.");
			System.exit(-3);
		}

		// Create our benchmark instance
		System.out.println("Benchmark \"" + args[0] + "\" starts.");
		bm.run();
		System.out.println("Benchmark \"" + args[0] + "\" ends.");

		// Save the benchmark timing data
// REMEMBER

		// All done
		reportExitingN(handle);
		System.exit(0);
	}


	// Class variables
	private static String		m_uuid;											// UUID assigned at startup, used to identify this instance to the monitor app
	private static int			m_handle;
	public	static int			m_testNumber;

	// Class constants
	private static final int	_TIMEOUT_SECONDS	= 120;
	private static final int	_TEST_MAX_COUNT		= _MAX_INTEGER_TESTS + _MAX_AES_ENCRYPT_TESTS + _MAX_AES_DECRYPT_TESTS + _MAX_SHA_256_TESTS + _MAX_STRING_TESTS + _MAX_STREAM_TESTS;
}
