/*
 * OPBM's Java Benchmark
 *
 * This class is the top-level class of the OPBM Java Benchmark test.  It is
 * a single-threaded class that executes a series of workloads:
 *
 *		o Integer sort
 *		o AES encrypt / decrypt
 *		o SHA-256
 *		o 32KB String construction
 *		o STREAM test from minibench
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
			System.out.println("Unable to run this benchmark. Unable to find JBM.");
			System.exit(-1);
		}
	}
	public native static boolean	didBenchmarkDllLoadOkayN();											// Called after the DLL is loaded in the static block above to see if it loaded okay, and found the JBM okay
	public native static int		firstConnectN(String uuid, String instanceTitle, int testCount);	// Called to connect to the monitor app, identifying itself
	public native static boolean	okayToBeginN();														// Called after firstConnectN() to see if all other JVMs have launched and reported in yet
	public native static void		reportTestN(int handle, int test, String name);						// Called to indicate a new test has started
	public native static void		reportCompletionN(int handle, float percent);						// Called to update the completion status of the current test
	public native static void		reportExitingN(int handle);											// Tell JBM that we're exiting
	public native static void		streamN(int handle, int test);										// Test from miniBench, written in C++

	/**
	 * Constructor
	 */
	public Benchmark(int handle)
	{
		m_handle = handle;
	}

	/**
	 * Main test entry point, executes each test in sequence
	 */
	public void run()
	{
		integerSort(_TEST_INTEGER_SORT);		// Test #1
		aesEncrypt(_TEST_AES_ENCRYPT);			// Test #2
		aesDecrypt(_TEST_AES_DECRYPT);			// Test #3
		sha256(_TEST_SHA_256);					// Test #4
		string(_TEST_STRING);					// Test #5
		stream(_TEST_STREAM);					// Test #6

		// Tell the JBM we're done
		reportTestN(m_handle, _TEST_MAX_COUNT + 1, "Finished");					// Set counter beyond max
		reportCompletionN(m_handle, 1.0f);										// Indicate 100% finished
	}

	/**
	 * Performs sort on 128KB of 64-bit integer array
	 * @param test the test number to report to the monitor app through native interface
	 */
	public void integerSort(int test)
	{
		reportTestN(m_handle, test, "Integer Sort");
		IntegerSort is = new IntegerSort(m_handle);
		is.run();
	}

	/**
	 * Performs AES encrypt test, constructs 2,000 AES encrypted strings
	 * @param test the test number to report to the monitor app through native interface
	 */
	public void aesEncrypt(int test)
	{
		reportTestN(m_handle, test, "AES Encrypt");
		AesEncrypt ae = new AesEncrypt(m_handle);
		ae.run();
	}

	/**
	 * Performs AES decrypt test, decrypts strings previously constructed in
	 * aesEncrypt()
	 * @param test the test number to report to the monitor app through native interface
	 */
	public void aesDecrypt(int test)
	{
		reportTestN(m_handle, test, "AES Decrypt");
		AesDecrypt ad = new AesDecrypt(m_handle);
		ad.run();
	}

	/**
	 * Performs SHA-256 encryption test
	 * @param test the test number to report to the monitor app through native interface
	 */
	public void sha256(int test)
	{
		reportTestN(m_handle, test, "SHA-256");
		SHA256 sh = new SHA256(m_handle);
		sh.run();
	}

	/**
	 * Performs String builder test, building a 32KB String through a random
	 * selection from a fixed string.
	 * @param test the test number to report to the monitor app through native interface
	 */
	public void string(int test)
	{
		reportTestN(m_handle, test, "String Builder");
		StringTest st = new StringTest(m_handle);
		st.run();
	}

	/**
	 * Performs STREAM test.  STREAM is part of miniBench, and is a C++ app.
	 * It runs in a JNI DLL.
	 * @param test
	 */
	public void stream(int test)
	{
		reportTestN(m_handle, test, "STREAM");
		streamN(m_handle, _TEST_STREAM);
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
	private int					m_handle;

	// Class constants
	private static final int	_TIMEOUT_SECONDS	= 120;

	// The following values must sync with those in benchmark.h
	private static final int	_TEST_INTEGER_SORT	= 1;
	private static final int	_TEST_AES_ENCRYPT	= 2;
	private static final int	_TEST_AES_DECRYPT	= 3;
	private static final int	_TEST_SHA_256		= 4;
	private static final int	_TEST_STRING		= 5;
	private static final int	_TEST_STREAM		= 6;
	private static final int	_TEST_MAX_COUNT		= 6;
}
