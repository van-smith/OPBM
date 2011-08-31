#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 8.30.2011

	Description: Atom to launch Microsoft Access 2010
	
	Usage:	accessOpen

#ce ======================================================================================================================================

#include <../../common/office2010/accessCommon.au3>

outputDebug( "initializeAccessScript()" )
initializeAccessScript()
outputDebug( "launchAccess()" )
launchAccess()

Exit