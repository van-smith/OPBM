#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 8.30.2011
	Modified: 11.23.2011 -rcp

	Description: Atom to close Microsoft Excel 2010

	Usage:	excelClose


#ce ======================================================================================================================================

#include <../../common/office2010/excelCommon.au3>

outputDebug( "initializeExcelScript()" )
InitializeExcelScript()
outputDebug( "closeExcel()" )
CloseExcel()

Exit