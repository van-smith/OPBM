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
$gBaselines[1][0] = $HEDGE_PAGE
$gBaselines[1][1] = $HEDGE_PAGE_SCORE
$gBaselines[2][0] = $HEDGE_ROTATE
$gBaselines[2][1] = $HEDGE_ROTATE_SCORE
$gBaselines[3][0] = $HEDGE_SAVE_XPS
$gBaselines[3][1] = $HEDGE_SAVE_XPS_SCORE
$gBaselines[4][0] = $HEDGE_ZOOM_XPS
$gBaselines[4][1] = $HEDGE_ZOOM_XPS_SCORE
$gBaselines[5][0] = $HEDGE_XPS_EXIT
$gBaselines[5][1] = $HEDGE_XPS_EXIT_SCORE

outputDebug( "initializePublisherScript()" )
initializePublisherScript()
outputDebug( "initializePublisherHedgeScript()" )
InitializePublisherHedgeScript()
outputDebug( "openHedge()" )
openHedge()
outputDebug( "pageHedge()" )
pageHedge()
outputDebug( "rotateHedge()" )
rotateHedge()
outputDebug( "saveAsXps()" )
saveAsXps()
outputDebug( "zoomXps()" )
zoomXps()
outputDebug( "exitXps()" )
exitXps()

Exit

Func InitializePublisherHedgeScript()
	; Delete the document in case it wasn't deleted last time
	$directoryOutput	= GetScriptTempDirectory()
	$filenameHedgeXps	= $directoryOutput & $filenameHedgeXps
	opbmFileDeleteIfExists( $filenameHedgeXps )
EndFunc

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

Func pageHedge()
	opbmWinWaitActivate( $MICROSOFT_PUBLISHER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Window." )

	TimerBegin()
	Send( "{f5}" )
	opbmWinWaitActivate( "Go To Page", "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Go To Page Window." )
	Send( "1" )
	Send( "{ENTER}" )
	opbmWinWaitActivate( $MICROSOFT_PUBLISHER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Window." )

	Local $i
	For $i = 1 To 10
		Send( "^{PGDN}" )
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
		Send( "^{PGUP}" )
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Next
	TimerEnd( $HEDGE_PAGE )
EndFunc

Func rotateHedge()
	opbmWinWaitActivate( $MICROSOFT_PUBLISHER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Window." )

	TimerBegin()
	Send( "{f5}" )
	opbmWinWaitActivate( "Go To Page", "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Go To Page Window." )
	Send( "1" )
	Send( "{ENTER}" )
	opbmWinWaitActivate( $MICROSOFT_PUBLISHER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Window." )

	Send( "^a" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "!h" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "ay" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "{down}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "{up}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "{down}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "{down}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "{down}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "{ALT}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
;	Send( "{ESCAPE}" )
;	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )

	opbmWinWaitActivate( $MICROSOFT_PUBLISHER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Window." )
	TimerEnd( $HEDGE_PAGE )
EndFunc

Func saveAsXps()
	opbmWinWaitActivate( $MICROSOFT_PUBLISHER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Window." )

	TimerBegin()
	Send( "!f" )
	Send( "a" )
	opbmWinWaitActivate( $SAVE_AS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher Save As: Unable to find Window." )

	; Send its full pathname
	Send( $filenameHedgeXps )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "{tab}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "x" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	; Choose "Save" button
	Send("!s")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $XPS_VIEWER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft XPS Viewer. Unable to find Window." )
	TimerEnd( $HEDGE_SAVE_XPS )
EndFunc

Func zoomXps()
	opbmWinWaitActivate( $XPS_VIEWER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft XPS Viewer. Unable to find Window." )

	TimerBegin()
	Dim $i
	For $i = 1 To 10
		Send( "{ALT}" )
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
		Send( "v" )
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
		Send( "i" )
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Next
	For $i = 1 To 10
		Send( "{ALT}" )
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
		Send( "v" )
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
		Send( "o" )
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Next
	Send( "^l" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $XPS_VIEWER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft XPS Viewer. Unable to find Window." )
	TimerEnd( $HEDGE_ZOOM_XPS )
EndFunc

Func exitXps()
	opbmWinWaitActivate( $XPS_VIEWER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft XPS Viewer. Unable to find Window." )
	TimerBegin()
	Send("!fx")
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $MICROSOFT_PUBLISHER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Window." )
	TimerEnd( $HEDGE_XPS_EXIT )
EndFunc