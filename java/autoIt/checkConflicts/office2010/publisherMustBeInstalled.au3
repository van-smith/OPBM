#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date:  Sep 26, 2011

	Description: Atom to check if Microsoft Publisher 2010 is installed
	
	Usage:	publisherMustBeInstalled

#ce ======================================================================================================================================

#include <../../common/office2010/publisherCommon.au3>


outputDebug( "Check Conflicts Microsoft Publisher 2010" )
InitializePublisherScript(0)

If isPublisherInstalled() = "not found" Then
	outputConflict( "Microsoft Publisher 2010 is not installed" )
	outputResolution ( "Please manually install Office 2010 with Publisher" )
EndIf

Exit
