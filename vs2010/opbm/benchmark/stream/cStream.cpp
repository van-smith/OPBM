#include "cStream.h"
/*
  COSBI: Comprehensive Open Source Benchmarking Initiative
  Copyright (c) 2010 Van Smith

  Adpated for use in OPBM October 2011
  Copyright (c) 2011 Van Smith

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation; either version 2
  of the License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

  The current web address of the GNU General Public License is:
  http://www.gnu.org/licenses/gpl.html

  You can contact the authors of this software at:
  cosbi@vanshardware.com
  See www.vanshardware.com or www.cosbi.org for more contact details.
*/

cStream::cStream( int jbmHandle )
{
  N	= 4000000; // increase to 8MB
  handle = jbmHandle; // Used for OPBM, to report progress during the test
  OFFSET = 0;
  scalar = 3.0;
  freq = 0;
  Verbose = false;
  label[ eCopy  ] = "Copy:      ";
  label[ eScale ] = "Scale:     ";
  label[ eAdd   ] = "Add:       ";
  label[ eTriad ] = "Triad:     ";
  mArraysAreAllocated = false;
#ifdef vs2005
  // obtain clock update frequency:
  QueryPerformanceFrequency((LARGE_INTEGER *)&freq);
#else
  // obtain clock update frequency:
  freq = CLOCKS_PER_SEC;
#endif
  allocateArrays();
}; // void cStream::cStream( void )

cStream::~cStream( void )
{
#ifdef vs2005
  _aligned_free( a );
  _aligned_free( b );
  _aligned_free( c );
#else
  free( a );
  free( b );
  free( c );
#endif
} // void cStream::~cStream( void )

void cStream::initializeVariables()
{
	avgtime[ eCopy ] = avgtime[ eScale ] = avgtime[ eAdd ] = avgtime[ eTriad ] = 0;
	maxtime[ eCopy ] = maxtime[ eScale ] = maxtime[ eAdd ] = maxtime[ eTriad ] = 0;
	mintime[ eCopy ] = mintime[ eScale ] = mintime[ eAdd ] = mintime[ eTriad ] = FLT_MAX;
} // void cStream::initializeVariables()

void cStream::allocateArrays()
{
  if( mArraysAreAllocated )
  {
#ifdef vs2005
    a = (double*)_aligned_realloc( a, N * sizeof(double), 16 );
    b = (double*)_aligned_realloc( b, N * sizeof(double), 16 );
    c = (double*)_aligned_realloc( c, N * sizeof(double), 16 );
#else
    free( a );
    free( b );
    free( c );
  #ifdef windowos
    a = (double*)malloc( N * sizeof(double) );
    b = (double*)malloc( N * sizeof(double) );
    c = (double*)malloc( N * sizeof(double) );
  #else
    a = (double*)malloc( N * sizeof(double) );
    b = (double*)malloc( N * sizeof(double) );
    c = (double*)malloc( N * sizeof(double) );
  #endif
#endif
  } else
  {
#ifdef vs2005
    // allocate memory from heap for arrays
    a = (double*)_aligned_malloc( N * sizeof(double), 16 );
    b = (double*)_aligned_malloc( N * sizeof(double), 16 );
    c = (double*)_aligned_malloc( N * sizeof(double), 16 );
    bytes = (double*)_aligned_malloc( 4 * sizeof(double), 16 );
#else
    // obtain clock update frequency:
    freq = CLOCKS_PER_SEC;
    // allocate memory from heap for arrays
    #ifdef windowsos
     a = (double*)malloc( N * sizeof(double) );
     b = (double*)malloc( N * sizeof(double) );
     c = (double*)malloc( N * sizeof(double) );
     bytes = (double*)malloc( 4 * sizeof(double) );
    #else
     a = (double*)valloc( N * sizeof(double) );
     b = (double*)valloc( N * sizeof(double) );
     c = (double*)valloc( N * sizeof(double) );
     bytes = (double*)valloc( 4 * sizeof(double) );
    #endif
#endif
  }
  bytes[ eCopy  ] = (double)(2 * sizeof(double) * N);
  bytes[ eScale ] = (double)(2 * sizeof(double) * N);
  bytes[ eAdd   ] = (double)(3 * sizeof(double) * N);
  bytes[ eTriad ] = (double)(3 * sizeof(double) * N);
  mArraysAreAllocated = true;
} // void cStream::allocateArrays()

void cStream::runChecks()
{
  register int j;

  /* --- SETUP --- determine precision and check timing --- */
  cout << HLINE
       << "STREAM version $Revision: 5.8 $"
       << endl
       << HLINE ;
  BytesPerWord = sizeof(double);
  cout << "This system uses "
       << BytesPerWord
       << " bytes per DOUBLE PRECISION word."
       << endl
       << HLINE
       << "Array size = "
       << N
       << ", Offset = "
       << OFFSET
       << endl
       << "Total memory required = "
       << ( 3.0 * BytesPerWord ) * ( ( double )N / 1048576.0 )
       << " MB."
       << endl
       << "Each test is run "
       << NTIMES
       << " times, but only"
       << endl
       << "the *best* time for each is used."
       << endl
       << HLINE;

    /* Get initial value for system clock. */
  for (j=0; j<N; j++)
  {
	  a[j] = 1.0;
	  b[j] = 2.0;
	  c[j] = 0.0;
	}

  cout << HLINE;

#ifdef vs2005
  cout << "Timer frequency (Hz): " << freq << endl;
  quantum = checkTick();
#else
  cout << "Timer frequency (Hz): " << CLOCKS_PER_SEC << endl;
  quantum = checkTick();
#endif

  if( quantum >= 1 )
  {
	  cout << "Your clock granularity/precision appears to be "
         << quantum
         << " microseconds."
         << endl;
  }
  else
  {
	  cout << "Your clock granularity appears to be "
	       << "less than one microsecond."
         << endl;
	  quantum = 1;
  }

#ifdef vs2005
  QueryPerformanceCounter( ( LARGE_INTEGER *)&start );
#else
  start = clock();
#endif
  for (j = 0; j < N; j++)
  {
    a[j] = 2.0E0 * a[j];
  }
#ifdef vs2005
  QueryPerformanceCounter( ( LARGE_INTEGER *)&stop );
#else
  stop = clock();
#endif
  t = 1.0E6 * ( stop - start ) / freq;

  cout << "Each test below will take on the order of "
       << ( int )t
       << " microseconds."
       << endl
       << "   (= "
       << ( int )( t / quantum )
       << " clock ticks)"
       << endl
       << "Increase the size of the arrays if this shows that"
       << endl
       << "you are not getting at least 20 clock ticks per test."
       << endl
       << HLINE
       << "WARNING -- The above is only a rough guideline."
       << endl
       << "For best results, please be sure you know the"
       << endl
       << "precision of your system timer."
       << endl
       << HLINE;
} // void cStream::runChecks()

extern void reportCompletionN(int handle, float percent); // For OPBM, to report back its progress
void cStream::runTests()
{
  register int j, k;
  float completed, increment, next;
  /*  --- MAIN LOOP --- repeat test cases NTIMES times --- */

  completed = 0.0f;
  increment = 1.0f / (float)NTIMES;
  next = 0.01f;
  for (k=0; k<NTIMES; k++)
  {
    // For OPBM, to report back its progress
    if (completed >= next)
	{
      reportCompletionN(handle, completed);
	  next += 0.01f;
	}
	completed += increment;

#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&start );
#else
    start = clock();
#endif
	  for (j=0; j<N; j++)
	    c[j] = a[j];
#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&stop );
#else
    stop = clock();
#endif
    times[ eCopy ][k] = stop - start;

#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&start );
#else
    start = clock();
#endif
	  for (j=0; j<N; j++)
	    b[j] = scalar * c[j];
#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&stop );
#else
    stop = clock();
#endif
    times[ eScale ][k] = stop - start;

#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&start );
#else
    start = clock();
#endif
	  for (j=0; j<N; j++)
	    c[j] = a[j]+b[j];
#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&stop );
#else
    stop = clock();
#endif
		times[ eAdd ][k] = stop - start;

#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&start );
#else
    start = clock();
#endif
	  for (j=0; j<N; j++)
	    a[j] = b[j]+scalar*c[j];
#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&stop );
#else
    stop = clock();
#endif
	  times[ eTriad ][k] = stop - start;
	} // for
} // void cStream::runTests();

void cStream::runTunedTests()
{
  register int k;
  /*  --- MAIN LOOP --- repeat test cases NTIMES times --- */
  for (k=0; k<NTIMES; k++)
	{
	  //times[0][k] = timeGetTime();
#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&start );
#else
    start = clock();
#endif
    tuned_STREAM_Copy();
#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&stop );
#else
    stop = clock();
#endif
    times[0][k] = stop - start;

#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&start );
#else
    start = clock();
#endif
    tuned_STREAM_Scale(scalar);
#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&stop );
#else
    stop = clock();
#endif
    times[1][k] = stop - start;

#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&start );
#else
    start = clock();
#endif
    tuned_STREAM_Add();
#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&stop );
#else
    stop = clock();
#endif
		times[2][k] = stop - start;

#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&start );
#else
    start = clock();
#endif
    tuned_STREAM_Triad(scalar);
#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&stop );
#else
    stop = clock();
#endif
	  times[3][k] = stop - start;
	} // for
} // void cStream::runTunedTests();

void cStream::calculateBandwidthResults()
{
  register int j, k;
  /* --- SUMMARY --- */
  for (k=1; k<NTIMES; k++) /* note -- skip first iteration */
	{
	  for ( j = eCopy; j <= eTriad; j++)
	    {
	      avgtime[j] = avgtime[j] + times[j][k];
	      mintime[j] = MIN(mintime[j], times[j][k]);
	      maxtime[j] = MAX(maxtime[j], times[j][k]);
	    } // for
	} // for

  mCopyBandwidth  = ( bytes[ eCopy  ] ) / ( mintime[ eCopy  ] / freq );
  mScaleBandwidth = ( bytes[ eScale ] ) / ( mintime[ eScale ] / freq );
  mAddBandwidth   = ( bytes[ eAdd   ] ) / ( mintime[ eAdd   ] / freq );
  mTriadBandwidth = ( bytes[ eTriad ] ) / ( mintime[ eTriad ] / freq );
} //void calculateBandwidthResults()

void cStream::outputSummary()
{

  register int j;
  cout << "Function      Rate (MB/s)   Avg time     Min time     Max time"
       << endl;
  for ( j = eCopy; j <= eTriad; j++)
  {
	  avgtime[j] = avgtime[j]/(double)(NTIMES-1);

    cout << label[j]
         << fixed
         << setw( 14 )
         << setprecision( 3 )
         << ( 1.0E-06 * bytes[j] ) / ( mintime[j] / freq )
         << setw( 11 )
	       << avgtime[j] / freq
         << setw( 13 )
	       << mintime[j] / freq
         << setw( 13 )
	       << maxtime[j] / freq
         << endl;
    }
    cout << HLINE;

    /* --- Check Results --- */
    checkSTREAMresults();
    cout << HLINE;
}; // void cStream::outputSummary()

void cStream::runBenchmark()
{
  initializeVariables();
  runChecks();
  runTests();
  checkSTREAMresults();
  calculateBandwidthResults();
  outputSummary();
}; // void cStream::runBenchmark()

void cStream::runBenchmarkTuned()
{
  initializeVariables();
  runChecks();
  runTunedTests();
  checkSTREAMresults();
  calculateBandwidthResults();
  outputSummary();
}; // void cStream::runBenchmarkTuned()

void cStream::runBenchmarkNoOutput()
{
  initializeVariables();
  runTests();
  calculateBandwidthResults();
}; // void cStream::runBenchmarkNoOutput()

void cStream::runBenchmarkTunedNoOutput()
{
  initializeVariables();
  runTests();
  calculateBandwidthResults();
}; // void cStream::runBenchmarkTunedNoOutput()

int cStream::checkTick()
{
  int i, minDelta, Delta;
  __int64	t1, t2;
  __int64 timesfound[ M ];

/*  Collect a sequence of M unique time values from the system. */
  for (i = 0; i < M; i++)
  {
#ifdef vs2005
    QueryPerformanceCounter( ( LARGE_INTEGER *)&t1 );
#else
    t1 = clock();
#endif
    do
    {
#ifdef vs2005
      QueryPerformanceCounter( ( LARGE_INTEGER *)&t2 );
#else
      t2 = clock();
#endif
    } while( (t2 - t1) < 1 );
	  timesfound[i] = t2;
	}
/*
 * Determine the minimum difference between these M values.
 * This result will be our estimate (in milliseconds) for the
 * clock granularity.
 */
    minDelta = 1000000;
    for (i = 1; i < M; i++)
    {
	    Delta = (int)( 1.0E6 * ( timesfound[ i ] - timesfound[ i - 1 ] ) / freq );
	    minDelta = MIN(minDelta, MAX(Delta,0));
  	}
   return( minDelta );
} // int cStream::checkTick()

void cStream::checkSTREAMresults ()
{
	double aj,bj,cj,scalar;
	double asum,bsum,csum;
	double epsilon;
	int	j,k;

    /* reproduce initialization */
	aj = 1.0;
	bj = 2.0;
	cj = 0.0;
    /* a[] is modified during timing check */
	aj = 2.0E0 * aj;
    /* now execute timing loop */
	scalar = 3.0;
	for (k=0; k<NTIMES; k++)
        {
            cj = aj;
            bj = scalar*cj;
            cj = aj+bj;
            aj = bj+scalar*cj;
        }
	aj = aj * (double) (N);
	bj = bj * (double) (N);
	cj = cj * (double) (N);

	asum = 0.0;
	bsum = 0.0;
	csum = 0.0;
	for (j=0; j<N; j++) {
		asum += a[j];
		bsum += b[j];
		csum += c[j];
	}
 if( Verbose )
 {
	  cout << "Results Comparison: "
         << endl
	       << "        Expected  : "
         << aj
         << ", "
         << bj
         << ", "
         << cj
         << endl
	       << "        Observed  : "
         << asum
         << ", "
         << bsum
         << ", "
         << csum
         << endl;
 } // if

#ifndef abs
#define abs(a) ((a) >= 0 ? (a) : -(a))
#endif
	epsilon = 1.e-8;

	if (abs(aj-asum)/asum > epsilon)
  {
		cout << "Failed Validation on array a[]"
         << endl
		     << "        Expected  : "
         << aj
         << endl
         << "        Observed  : "
         << asum
         << endl;
	}
	else if (abs(bj-bsum)/bsum > epsilon)
  {
		cout << "Failed Validation on array b[]"
         << endl
		     << "        Expected  : "
         << bj
         << endl
		     << "        Observed  : "
         << bsum
         << endl;
	}
	else if (abs(cj-csum)/csum > epsilon)
  {
		cout << "Failed Validation on array c[]"
         << endl
		     << "        Expected  : "
         << cj
         << endl
		     << "        Observed  : "
         << csum
         << endl;
	}
	else {
		cout << "Solution Validates"
         << endl;
	}
}

void cStream::tuned_STREAM_Copy()
{
	int j;
  for (j=0; j<N; j++)
  {
    c[j] = a[j];
  }
}

void cStream::tuned_STREAM_Scale(double scalar)
{
	int j;
	for (j=0; j<N; j++)
  {
	  b[j] = scalar*c[j];
  }
}

void cStream::tuned_STREAM_Add()
{
  int j;
  for (j=0; j<N; j++)
  {
    c[ j ] = a[ j ] + b[ j ];
  }
} // void cStream::tuned_STREAM_Add()

void cStream::tuned_STREAM_Triad(double scalar)
{
	int j;
	for ( j=0; j<N; j += 10 )
  {
	  a[ j ] = b[ j ] + scalar*c[ j ];
	  a[ j + 1 ] = b[ j + 1 ] + scalar*c[ j + 1 ];
	  a[ j + 2 ] = b[ j + 2 ] + scalar*c[ j + 2 ];
	  a[ j + 3 ] = b[ j + 3 ] + scalar*c[ j + 3 ];
	  a[ j + 4 ] = b[ j + 4 ] + scalar*c[ j + 4 ];
	  a[ j + 5 ] = b[ j + 5 ] + scalar*c[ j + 5 ];
	  a[ j + 6 ] = b[ j + 6 ] + scalar*c[ j + 6 ];
	  a[ j + 7 ] = b[ j + 7 ] + scalar*c[ j + 7 ];
	  a[ j + 8 ] = b[ j + 8 ] + scalar*c[ j + 8 ];
	  a[ j + 9 ] = b[ j + 9 ] + scalar*c[ j + 9 ];
  }; // for
}

double cStream::getCopyBandwidth()
{
  return mCopyBandwidth;
}// double cStream::getCopyBandwidth()

double cStream::getAddBandwidth()
{
  return mAddBandwidth;
} // double cStream::getAddBandwidth()

double cStream::getScaleBandwidth()
{
  return mScaleBandwidth;
} // double cStream::getScaleBandwidth()

double cStream::getTriadBandwidth()
{
  return mTriadBandwidth;
} // double cStream::getTriadBandwidth()

void cStream::setBufferSize( int aNewBufferSizeInBytes )
{
  N = ( int )ceil( (double)(aNewBufferSizeInBytes / sizeof( double )) );
  allocateArrays();
} // void cStream::setBufferSize( int aNewBufferSize )
