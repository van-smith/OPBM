#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date:  Sep 26, 2011

	Description: Atom to check if Microsoft Publisher 2010 is installed
	
	Usage:	publisherMustBeInstalled

#ce ======================================================================================================================================

#include <../../common/office2010/powerPointCommon.au3>


outputDebug( "Check Conflicts Microsoft Publisher 2010" )
initializePowerPointScript(0)

If isPowerPointInstalled() = "not found" Then
	outputConflict( "Microsoft PowerPoint 2010 is not installed" )
	outputResolution ( "Please manually install Office 2010 with PowerPoint" )
EndIf

Exit
