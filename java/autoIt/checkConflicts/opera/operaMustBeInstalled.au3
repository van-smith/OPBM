#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date:  Sep 26, 2011

	Description: Atom to check if Opera is installed
	
	Usage:	operaMustBeInstalled

#ce ======================================================================================================================================

#include <../../common/opera/operaCommon.au3>


outputDebug( "Check Conflicts Opera" )
InitializeGlobalVariables()
InitializeOperaScript()

If Not isOperaAlreadyInstalled() Then
	outputConflict( "Opera is not installed" )
	outputResolution ( "Please manually install Opera 11.50 or later" )
EndIf

Exit
