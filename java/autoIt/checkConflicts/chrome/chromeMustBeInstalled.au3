#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date:  Sep 26, 2011

	Description: Atom to check to see if Chrome is installed
	
	Usage:	chromeMustBeInstalled

#ce ======================================================================================================================================

#include <../../common/chrome/chromeCommon.au3>


outputDebug( "Check Conflicts Chrome" )
InitializeGlobalVariables()
InitializeChromeScript()

If Not isChromeAlreadyInstalled() Then
	outputConflict( "Google Chrome is not installed" )
	outputResolution ( "Please manually install Google Chrome 12.x or later" )
EndIf

Exit
