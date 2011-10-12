========================================================================
             WIN32 APPLICATION : watchdog Project Overview
========================================================================
NOTE:  watchdog is a stand-alone executable with no direct interaction
       and no visible windows to the user.  It runs as a background
	   process, designed for one purpose, and one purpose only:  to be
	   instructed by OPBM and OPBM's scripts what to watch out for.
	   It implements a timer to periodically examine the timeout
	   intervals of those processes its been instructed to watch, and
	   if the timeout interval is reached, it begins termination
	   protocols, killing every process that remains active, and storing
	   information about its operations to reveal to the OPBM harness
	   through opbm32.dll and opbm64.dll JNI native functions.
========================================================================

watchdog.vcxproj
    This is the main project file for VC++ projects generated using an Application Wizard.
    It contains information about the version of Visual C++ that generated the file, and
    information about the platforms, configurations, and project features selected with the
    Application Wizard.

watchdog.vcxproj.filters
    This is the filters file for VC++ projects generated using an Application Wizard. 
    It contains information about the association between the files in your project 
    and the filters. This association is used in the IDE to show grouping of files with
    similar extensions under a specific node (for e.g. ".cpp" files are associated with the
    "Source Files" filter).

watchdog.h, watchdog.cpp
    This is the main application source file.

watchdog_common.h
    Any external app desiring to use the watchdog functionality and protocols needs to
	include this file, as it contains the necessary structures and template information
	to allow for named pipe data exchange between processes.
