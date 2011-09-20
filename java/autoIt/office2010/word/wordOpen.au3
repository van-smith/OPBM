#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 9.19.2011

	Description: Atom to launch Microsoft Word 2010

	Usage:	wordOpen

#ce ======================================================================================================================================

#include <../../common/office2010/wordCommon.au3>

outputDebug( "InitializeGlobalVariables()" )
initializeGlobalVariables()
outputDebug( "initializeWordScript()" )
initializeWordScript()
outputDebug( "launchWord()" )
launchWord()

Exit