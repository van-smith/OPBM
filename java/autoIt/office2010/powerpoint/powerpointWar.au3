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
$gBaselines[1][0] = $WAR_CREATE_WMV
$gBaselines[1][1] = $WAR_CREATE_WMV_SCORE
$gBaselines[2][0] = $WAR_PLAY_PRESENTATION
$gBaselines[2][1] = $WAR_PLAY_PRESENTATION_SCORE

outputDebug( "InitializePowerPointScript()" )
initializePowerPointScript()
outputDebug( "InitializePowerPointWarScript()" )
initializePowerPointWarScript()
outputDebug( "openWar()" )
openWar()
outputDebug( "saveWarAsWmv()" )
saveWarAsWmv()
outputDebug( "playPresentation()" )
playPresentation()

Exit

Func initializePowerPointWarScript()
	$directoryOutput	= GetScriptTempDirectory()
	$filenameWarPptx	= $directoryOutput & $filenameWarPptx
	$filenameWarWmv		= $directoryOutput & $filenameWarWmv
	; Delete the document in case it wasn't deleted last time
	opbmFileDeleteIfExists( $filenameWarPptx )
	opbmFileDeleteIfExists( $filenameWarWmv )
	; copy the working file:
	$gErrorTrap = FileCopy($FILENAME_PPTX, $filenameWarPptx, 1)
	If $gErrorTrap = 0 Then ErrorHandle($ERROR_PREFIX & "FileCopy: " & $FILENAME_PPTX & ": Unable to copy file.")
EndFunc

Func OpenWar()
	local $i
	opbmWinWaitActivate( $MICROSOFT_POWERPOINT, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft PowerPoint. Unable to find Window." )
	TimerBegin()
	Send( "!fo" )
	opbmWinWaitActivate( $OPEN, $OPEN, $gTimeout, $ERROR_PREFIX & "WinWait: Open: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
; for Debugging
outputMessage( $filenameWarPptx )
	ControlSend( $OPEN, $OPEN, "Edit1", $filenameWarPptx, 1)
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "{ENTER}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWaitUntilSystemIdle( 5, 100, 1200000 )
	TimerEnd( $WAR_OPEN_PRESENTATION )
EndFunc

Func saveWarAsWmv()
	opbmWinWaitActivate( $MICROSOFT_POWERPOINT, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft PowerPoint. Unable to find Window." )
	Sleep( 1000 )

	Send( "!fa" )
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
	opbmWinWaitActivate( $SAVE_AS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft PowerPoint Save As: Unable to find Window." )
	Sleep( 1000 )
	; Send its full pathname
	Send( $filenameWarWmv )
	opbmWaitUntilSystemIdle( 10, 100, 5000 )

	; Save as WMV
	; Alt+t to choose type, w to choose "WMV"
	Send("!tw")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Sleep( 1000 )
	; Choose "Save" button
	TimerBegin()
	Send("!s")
	Sleep( 5000 )
	opbmWaitUntilSystemIdle( 5, 100, 1200000 )
	opbmWinWaitActivate( $MICROSOFT_POWERPOINT, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft PowerPoint. Unable to find Window." )
	TimerEnd( $WAR_CREATE_WMV )
EndFunc

Func playPresentation()
	TimerBegin()
	Send( "{alt}" )
	Send( "s" )
	Send( "b" )
	dim $lSearchColor = "00FF0000"
	dim $lColor
	dim $i
	for $i = 1 to 2000
		$lColor = PixelGetColor( 600, 20 )
		;outputDebug( "Pixel color " & Hex( $lColor ) )
		if Hex( $lColor ) = $lSearchColor then
			ExitLoop
		EndIf
		Sleep( 100 )
	Next
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	TimerEnd( $WAR_PLAY_PRESENTATION )
	Sleep( 2000 )
	MouseClick( "left", 600, 20 )
	Sleep( 2000 )
	;Send( "{ESC}" )
	;opbmWaitUntilSystemIdle( 5, 100, 5000 )
	;WinActivate( $MICROSOFT_POWERPOINT )
	;opbmWinWaitActivate( $MICROSOFT_POWERPOINT, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft PowerPoint. Unable to find Window." )
EndFunc