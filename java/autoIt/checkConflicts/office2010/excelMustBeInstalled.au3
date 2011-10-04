#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date:  Sep 26, 2011

	Description: Atom to check if Microsoft Excel 2010 is installed
	
	Usage:	excelMustBeInstalled

#ce ======================================================================================================================================

#include <../../common/office2010/excelCommon.au3>


outputDebug( "Check Conflicts Microsoft Excel 2010" )
InitializeGlobalVariables()
InitializeExcelScript()

If isExcelInstalled() = "not found" Then
	outputConflict( "Microsoft Excel 2010 is not installed" )
	outputResolution ( "Please manually install Office 2010 with Excel" )
EndIf

Exit
