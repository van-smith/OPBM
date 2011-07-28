========================================================================
    DYNAMIC LINK LIBRARY : Au3_WaitUntilIdle Project Overview
========================================================================

AppWizard has created this Au3_WaitUntilIdle DLL for you.

This file contains a summary of what you will find in each of the files that
make up your Au3_WaitUntilIdle application.


Au3_WaitUntilIdle.vcxproj
    This is the main project file for VC++ projects generated using an Application Wizard.
    It contains information about the version of Visual C++ that generated the file, and
    information about the platforms, configurations, and project features selected with the
    Application Wizard.

Au3_WaitUntilIdle.vcxproj.filters
    This is the filters file for VC++ projects generated using an Application Wizard. 
    It contains information about the association between the files in your project 
    and the filters. This association is used in the IDE to show grouping of files with
    similar extensions under a specific node (for e.g. ".cpp" files are associated with the
    "Source Files" filter).

Au3_WaitUntilIdle.cpp
    This is the main DLL source file.

	When created, this DLL does not export any symbols. As a result, it
	will not produce a .lib file when it is built. If you wish this project
	to be a project dependency of some other project, you will either need to
	add code to export some symbols from the DLL so that an export library
	will be produced, or you can set the Ignore Input Library property to Yes
	on the General propert page of the Linker folder in the project's Property
	Pages dialog box.

/////////////////////////////////////////////////////////////////////////////
Other standard files:

StdAfx.h, StdAfx.cpp
    These files are used to build a precompiled header (PCH) file
    named Au3_WaitUntilIdle.pch and a precompiled types file named StdAfx.obj.

/////////////////////////////////////////////////////////////////////////////
Other notes:

AppWizard uses "TODO:" comments to indicate parts of the source code you
should add to or customize.

/////////////////////////////////////////////////////////////////////////////


//		TKLong sTime;
//		int sLastCpu;
//		int sLastCpuProcess;
//		TKTime sLastUpTime;
//
//		// lock
//		{ 
//			SmartLock lock( &m_lock );
//
//			sTime           = s_time;
//			sLastCpu        = s_lastCpu;
//			sLastCpuProcess = s_lastCpuProcess;
//			sLastUpTime     = s_lastUpTime;
//		}
//
//		if( s_delay.MSec() < 200 )
//		{
//			if( pSystemUsage != NULL )
//				*pSystemUsage = sLastCpu;
//
//			if( pUpTime != NULL )
//				*pUpTime = sLastUpTime;
//
//			return sLastCpuProcess;
//		}
//
//		TKLong time;
//
//		TKLong idleTime;
//		TKLong kernelTime;
//		TKLong userTime;
//		TKLong kernelTimeProcess;
//		TKLong userTimeProcess;
//
//		GetSystemTimeAsFileTime( (LPFILETIME)&time );
//
//		if( sTime == 0 )
//		{
//			// for the system
//			if( s_pfnGetSystemTimes != NULL )
//			{
//				/*BOOL res = */s_pfnGetSystemTimes( (LPFILETIME)&idleTime, (LPFILETIME)&kernelTime, (LPFILETIME)&userTime );
//			}
//			else
//			{
//				idleTime    = 0;
//				kernelTime  = 0;
//				userTime    = 0;
//			}
//
//			// for this process
//			{
//				FILETIME createTime;
//				FILETIME exitTime;
//				GetProcessTimes( hProcess, &createTime, &exitTime, 
//								 (LPFILETIME)&kernelTimeProcess, 
//								 (LPFILETIME)&userTimeProcess );
//			}
//
//			// LOCK
//			{
//				SmartLock lock( &m_lock );
//
//				s_time              = time;
//
//				s_idleTime          = idleTime;
//				s_kernelTime        = kernelTime;
//				s_userTime          = userTime;
//
//				s_kernelTimeProcess = kernelTimeProcess;
//				s_userTimeProcess   = userTimeProcess;
//
//				s_lastCpu           = 0;
//				s_lastCpuProcess    = 0;
//
//				s_lastUpTime        = kernelTime + userTime;
//
//				sLastCpu        = s_lastCpu;
//				sLastCpuProcess = s_lastCpuProcess;
//				sLastUpTime     = s_lastUpTime;
//			}
//
//			if( pSystemUsage != NULL )
//				*pSystemUsage = sLastCpu;
//
//			if( pUpTime != NULL )
//				*pUpTime = sLastUpTime;
//
//			s_delay.Mark();
//			return sLastCpuProcess;
//		}
//		/////////////////////////////////////////////////////
//		// sTime != 0
//
//		TKLong div = ( time - sTime );
//
//		// for the system
//		if( s_pfnGetSystemTimes != NULL )
//		{
//			/*BOOL res = */s_pfnGetSystemTimes( (LPFILETIME)&idleTime, (LPFILETIME)&kernelTime, (LPFILETIME)&userTime );
//		}
//		else
//		{
//			idleTime    = 0;
//			kernelTime  = 0;
//			userTime    = 0;
//		}
//
//		// for this process
//		{
//			FILETIME createTime;
//			FILETIME exitTime;
//			GetProcessTimes( GetCurrentProcess(), &createTime, &exitTime, 
//							 (LPFILETIME)&kernelTimeProcess, 
//							 (LPFILETIME)&userTimeProcess );
//		}
//
//		int cpu;
//		int cpuProcess;
//		// LOCK
//		{
//			SmartLock lock( &m_lock );
//
//			TKLong usr = userTime   - s_userTime;
//			TKLong ker = kernelTime - s_kernelTime;
//			TKLong idl = idleTime   - s_idleTime;
//
//			TKLong sys = (usr + ker);
//
//			if( sys == 0 )
//				cpu = 0;
//			else
//				cpu = int( (sys - idl) *100 / sys ); // System Idle take 100 % of cpu :-((
//       
//			cpuProcess = int( ( ( ( userTimeProcess - s_userTimeProcess ) + ( kernelTimeProcess - s_kernelTimeProcess ) ) *100 ) / div );
//       
//			s_time              = time;
//
//			s_idleTime          = idleTime;
//			s_kernelTime        = kernelTime;
//			s_userTime          = userTime;
//
//			s_kernelTimeProcess = kernelTimeProcess;
//			s_userTimeProcess   = userTimeProcess;
//       
//			s_cpu[(s_index++) %5] = cpu;
//			s_cpuProcess[(s_index++) %5] = cpuProcess;
//			s_count ++;
//			if( s_count > 5 ) 
//				s_count = 5;
//       
//			int i;
//			cpu = 0;
//			for( i = 0; i < s_count; i++ )
//				cpu += s_cpu[i];
//       
//			cpuProcess = 0;
//			for( i = 0; i < s_count; i++ )
//				cpuProcess += s_cpuProcess[i];
//
//			cpu         /= s_count;
//			cpuProcess  /= s_count;
//       
//			s_lastCpu        = cpu;
//			s_lastCpuProcess = cpuProcess;
//
//			s_lastUpTime     = kernelTime + userTime;
//
//			sLastCpu        = s_lastCpu;
//			sLastCpuProcess = s_lastCpuProcess;
//			sLastUpTime     = s_lastUpTime;
//		}
//
//		//DBGOUT( _T("CPU:%d  sys:%d div %d"), cpuProcess, cpu, div );
//   
//		if( pSystemUsage != NULL )
//			*pSystemUsage = sLastCpu;
//
//		if( pUpTime != NULL )
//			*pUpTime = sLastUpTime;
//
//		s_delay.Mark();
//		return sLastCpuProcess;
