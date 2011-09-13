#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 9.12.2011

	Description: Atom to launch and close Microsoft Word 2010
	
	Usage:	launchAndClose

#ce ======================================================================================================================================

#include <../../common/office2010/wordCommon.au3>

InitializeGlobalVariables()

outputDebug( "initializeWordScript()" )
initializeWordScript()

outputDebug( "launchWord()" )
launchWord()

outputDebug( "closeWord()" )
closeWord()

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit
