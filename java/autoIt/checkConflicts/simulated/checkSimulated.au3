#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date:  Sep 15, 2011

	Description: Atom to simulate a check for conflicts, and note resolutions
	
	Usage:	checkSimulated

#ce ======================================================================================================================================

#include <../../common/checkConflicts/checkConflictsCommon.au3>

outputDebug( "initializeCheckConflictsScript()" )
initializeCheckConflictsScript()

outputConflict( "This is an example conflict. Version 1.23 of Program XYZ is installed" )
outputResolution ( "Please uninstall Version 1.23 of Program XYZ" )

outputConflict( "Another sample.  A virus has been detected." )
outputResolution ( "Please run anti-virus software." )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit
