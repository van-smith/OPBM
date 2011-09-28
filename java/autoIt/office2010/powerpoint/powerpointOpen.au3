#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 8.30.2011

	Description: Atom to launch Microsoft PowerPoint 2010
	
	Usage:	powerpointOpen

#ce ======================================================================================================================================

#include <../../common/office2010/powerpointCommon.au3>

outputDebug( "InitializePowerPointScript()" )
initializePowerPointScript()
outputDebug( "LaunchPowerPoint()" )
launchPowerPoint()

FirstRunCheck()

Exit
