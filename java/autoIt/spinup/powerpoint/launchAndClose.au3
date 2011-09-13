#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 9.12.2011

	Description: Atom to launch Microsoft PowerPoint 2010
	
	Usage:	launchAndClose

#ce ======================================================================================================================================

#include <../../common/office2010/powerPointCommon.au3>

outputDebug( "initializePowerPointScript()" )
initializePowerPointScript()

outputDebug( "launchPowerPoint()" )
launchPowerPoint()

outputDebug( "closePowerPoint()" )
closePowerPoint()

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit
