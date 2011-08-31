#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 8.31.2011

	Description: Atom to open HEDGE flyer
	
	Usage:	publisherHedge

#ce ======================================================================================================================================

#include <../../common/office2010/publisherCommon.au3>

$gBaselines[0][0] = $HEDGE_OPEN_FLYER
$gBaselines[0][1] = $HEDGE_OPEN_FLYER_SCORE

outputDebug( "initializePublisherScript()" )
initializePublisherScript()
outputDebug( "openHedge()" )
openHedge()

Exit

Func OpenHedge()
	local $i
	opbmWinWaitActivate( $MICROSOFT_PUBLISHER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Window." )
	TimerBegin()
	Send( "!fo" )
	opbmWinWaitActivate( $OPEN, $OPEN, $gTimeout, $ERROR_PREFIX & "WinWait: Open: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	ControlSend( $OPEN, $OPEN, "Edit1", $FILENAME_PUB, 1)
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "{ENTER}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $HEDGE_OPEN_FLYER )
	Sleep( 5000 )
EndFunc
