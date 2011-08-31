#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 8.30.2011

	Description: Atom to launch Microsoft PowerPoint 2010
	
	Usage:	powerpointOpen


#ce ======================================================================================================================================

#include <../../common/office2010/powerpointCommon.au3>

$gBaselines[0][0] = $WAR_OPEN_PRESENTATION
$gBaselines[0][1] = $WAR_OPEN_PRESENTATION_SCORE
$gBaselines[1][0] = $WAR_PLAY_PRESENTATION
$gBaselines[1][1] = $WAR_PLAY_PRESENTATION_SCORE

outputDebug( "InitializePowerPointScript()" )
initializePowerPointScript()
outputDebug( "openWar()" )
openWar()
outputDebug( "playPresentation()" )
playPresentation()

Exit

Func OpenWar()
	local $i
	opbmWinWaitActivate( $MICROSOFT_POWERPOINT, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft PowerPoint. Unable to find Window." )
	TimerBegin()
	Send( "!fo" )
	opbmWinWaitActivate( $OPEN, $OPEN, $gTimeout, $ERROR_PREFIX & "WinWait: Open: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	ControlSend( $OPEN, $OPEN, "Edit1", $FILENAME_PPTX, 1)
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "{ENTER}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $WAR_OPEN_PRESENTATION )
EndFunc

Func playPresentation()
	TimerBegin()
	Send( "{alt}" )
	Send( "s" )
	Send( "b" )
	Sleep( 100 * 1000 )
	opbmWaitUntilProcessIdle( $gPID, 5, $gDurationMS, $gTimeoutMS )
	TimerEnd( $WAR_PLAY_PRESENTATION )
	WinActivate( $MICROSOFT_POWERPOINT )
	;opbmWinWaitActivate( $MICROSOFT_POWERPOINT, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft PowerPoint. Unable to find Window." )
	;Send( "{esc}" )	
EndFunc