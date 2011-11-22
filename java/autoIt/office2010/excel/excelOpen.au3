#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 11.17.2011

	Description: Atom to launch Microsoft Excel 2010

	Usage:	accessOpen

#ce ======================================================================================================================================

#include <../../common/office2010/excelCommon.au3>

outputDebug( "initializeExcelScript()" )
initializeExcelScript()
outputDebug( "launchExcel()" )
launchExcel()

;FirstRunCheck()

Exit