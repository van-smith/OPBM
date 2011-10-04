#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date:  Sep 26, 2011

	Description: Atom to check if Microsoft Access 2010 is installed
	
	Usage:	accessMustBeInstalled

#ce ======================================================================================================================================

#include <../../common/office2010/accessCommon.au3>


outputDebug( "Check Conflicts Microsoft Access 2010" )
InitializeAccessScript(0)

If isAccessInstalled() = "not found" Then
	outputConflict( "Microsoft Access 2010 is not installed" )
	outputResolution ( "Please manually install Office 2010 with Access" )
EndIf

Exit
