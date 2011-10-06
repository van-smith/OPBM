#pragma once
/*
  COSBI: Comprehensive Open Source Benchmarking Initiative
  Copyright (c) 2007 Van Smith

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

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <float.h>
#include <time.h>
#include <iostream>
#include <string>
#include <iomanip>
using namespace std;

#define windowsos
#define vs2005

#ifdef vs2005
  #include <windows.h>
  #define vs2005align16 __declspec(align(16))
  #define gccAlign16
  #define vs2005align64 __declspec(align(64))
  #define gccAlign64
#else
  #define vs2005align16
  #define gccAlign16 __attribute__((__aligned__(16)))
  #define vs2005align64
  #define gccAlign64 __attribute__((__aligned__(64)))
#endif

#define HLINE "--------------------------------------------------------------\n"

#ifndef MIN
  #define MIN(x,y) ((x)<(y)?(x):(y))
#endif

#ifndef MAX
  #define MAX(x,y) ((x)>(y)?(x):(y))
#endif

enum enumBandwidthType { eCopy, eScale, eAdd, eTriad };

class cStream
{
public:
	cStream( int handle);
	~cStream( void );
	int			OFFSET;
	void		runBenchmark();
	void		runBenchmarkTuned();
	void		runBenchmarkNoOutput();
	void		runBenchmarkTunedNoOutput();
	void		setBufferSize( int aNewBufferSizeInBytes );
	double		getCopyBandwidth();
	double		getAddBandwidth();
	double		getScaleBandwidth();
	double		getTriadBandwidth();
	bool		Verbose;

protected:
	void		allocateArrays();
	void		initializeVariables();
	void		calculateBandwidthResults();
	void		runChecks();
	void		runTests();
	void		checkSTREAMresults ();
	void		outputSummary();
	int			checkTick();
	void		runTunedTests();
	void		tuned_STREAM_Copy();
	void		tuned_STREAM_Add();
	void		tuned_STREAM_Scale(double scalar);
	void		tuned_STREAM_Triad(double scalar);

private:
	static const int		NTIMES	= 10;
	static const int		M		= 20;
	int						handle;				// Used in OPBM, handle to communicate with JBM monitor
	__int64					freq;
	__int64					start;
	__int64					stop;
	double					t;
	__int64					times[4][NTIMES];
	int						N;
	bool					mArraysAreAllocated;
	vs2005align16 double*	a gccAlign16;
	vs2005align16 double*	b gccAlign16;
	vs2005align16 double*	c gccAlign16;
	double					avgtime[ 4 ];
	double					maxtime[ 4 ];
	double					mintime[ 4 ];
	string					label[ 4 ];
	vs2005align16 double*	bytes gccAlign16;
	int						quantum;
	int						BytesPerWord;
	double					scalar;
	double					mCopyBandwidth;
	double					mAddBandwidth;
	double					mScaleBandwidth;
	double					mTriadBandwidth;
};
