#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=operaUninstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;
; Usage:	operaUninstall
;
;======================================================================================================================================
#include <../../common/opera/operaCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see operaCommon for definition of 0 and 1)
$gBaselines[2][0] = $LAUNCH_OPERA_UNINSTALLER	;Eliminate Version reference -rcp 11/11/11
$gBaselines[2][1] = $LAUNCH_OPERA_UNINSTALLER_SCORE	;Eliminate Version reference -rcp 11/11/11
$gBaselines[3][0] = $UNINSTALL_OPERA	;Eliminate Version reference -rcp 11/11/11
$gBaselines[3][1] = $UNINSTALL_OPERA_SCORE	;Eliminate Version reference -rcp 11/11/11

outputDebug( "Starting up Opera Uninstaller" )	;Eliminate Version reference -rcp 11/11/11

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If not isOperaAlreadyInstalled() Then
	outputError( "Opera is not installed" )	;Eliminate Version reference -rcp 11/11/11
	Exit -1
Endif

outputDebug( "InitializeScript()" )
InitializeOperaScript()
InitializeOperaScriptUninstallSpecific()

outputDebug( "LaunchUninstaller()" )
LaunchUninstaller()

outputDebug( "Uninstall()" )
Uninstall()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "operaUninstallTimes.csv" )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func InitializeOperaScriptUninstallSpecific()
	Opt("MouseCoordMode", 2)		; 0=active window, 1=absolute screen, 2=client area of active window
EndFunc

Func LaunchUninstaller()
	outputDebug( "Attempting to launch " & $OPERA_UNINSTALL_COMMAND )

	; Begin the timer as the uninstaller loads
	TimerBegin()
	$gPID = Run($OPERA_UNINSTALL_COMMAND, "C:\", @SW_SHOW)
	;opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $OPERA_INSTALLER_WINDOW_NAME, "", $gTimeout, $ERROR_PREFIX & "WinWait: "& $OPERA_INSTALLER_WINDOW_NAME & ": Unable to find Window.")	;Eliminate Version reference-rcp 11/15/11
	TimerEnd( $LAUNCH_OPERA_UNINSTALLER )	;Eliminate Version reference -rcp 11/11/11
EndFunc

Func Uninstall()
	outputDebug( "Clicking uninstall" )
	; Send an enter key as the default option is "Uninstall"
	Send("{Enter}")
	TimerBegin()
	;Added the next two lines in case Opera is open when we tries to uninstall -rcp 11/21/2011
	;The unfortunate side effect is that it may hit the Cancel button
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
	Send("{Enter}")
	;-rcp 11/21/2011
	opbmWaitUntilProcessIdle( $gPID, 10, 500, 120000 )
	TimerEnd( $UNINSTALL_OPERA )	;Eliminate Version reference -rcp 11/11/11
	;opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, 15000 ) ;wait up to 15 seconds to make sure we're done $gTimeoutMS
	Sleep(10000)	;Wait 10 seconds for extraineous windows to appear before moving on -rcp 11/13/2011
EndFunc
