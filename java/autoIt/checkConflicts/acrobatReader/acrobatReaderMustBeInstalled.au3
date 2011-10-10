#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date:  October 10, 2011

	Description: Atom to check to see if Acrobat Reader is installed
	
	Usage:	acrobatReaderMustBeInstalled

#ce ======================================================================================================================================

#include <../../common/adobe/adobeCommon.au3>


outputDebug( "Check Conflicts Acrobat Reader" )
InitializeGlobalVariables()
InitializeAdobeScript()

If Not isAcrobatReaderAlreadyInstalled() Then
	outputConflict( "Acrobat Reader is not installed" )
	outputResolution ( "Please manually install Acrobat Reader 10.x or later" )
EndIf

Exit
