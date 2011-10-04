#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date:  Sep 26, 2011

	Description: Atom to check if Microsoft Word 2010 is installed
	
	Usage:	wordMustBeInstalled

#ce ======================================================================================================================================

#include <../../common/office2010/wordCommon.au3>


outputDebug( "Check Conflicts Microsoft Word 2010" )
InitializeGlobalVariables()
InitializeWordScript()

If isWordInstalled() = "not found" Then
	outputConflict( "Microsoft Word 2010 is not installed" )
	outputResolution ( "Please manually install Office 2010 with Word" )
EndIf

Exit
