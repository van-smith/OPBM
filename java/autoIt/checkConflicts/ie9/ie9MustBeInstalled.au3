#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date:  Sep 26, 2011

	Description: Atom to check if IE9 is installed
	
	Usage:	ie9MustBeInstalled

#ce ======================================================================================================================================

#include <../../common/ie/ieCommon.au3>


outputDebug( "Check Conflicts Internet Explorer 9" )
InitializeGlobalVariables()
InitializeIEScript()

If Not isIE9Installed() Then
	outputConflict( "Internet Explorer 9 is not installed" )
	outputResolution ( "Please manually install Internet Explorer 9" )
EndIf

Exit
