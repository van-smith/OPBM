#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=operaUninstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	operaUninstall
;
;======================================================================================================================================
#include <../../common/opera/operaCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see operaCommon for definition of 0 and 1)
$gBaselines[2][0] = $LAUNCH_OPERA_1150_UNINSTALLER
$gBaselines[2][1] = $LAUNCH_OPERA_1150_UNINSTALLER_SCORE
$gBaselines[3][0] = $UNINSTALL_OPERA_1150
$gBaselines[3][1] = $UNINSTALL_OPERA_1150_SCORE

outputDebug( "Starting up Opera 11.50 Un-installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If not isOperaAlreadyInstalled() Then
	outputError( "Opera 11.50 is not installed" )
	Exit
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
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( "Opera 11.50 - Installer", "", $gTimeout, $ERROR_PREFIX & "WinWait: Opera 11.50 Uninstaller: Unable to find Window.")
	TimerEnd( $LAUNCH_OPERA_1150_UNINSTALLER )
EndFunc

Func Uninstall()
	outputDebug( "Clicking uninstall" )
	; Send an enter key as the default option is "Uninstall"
	Send("{Enter}")
	TimerBegin()
	opbmWaitUntilProcessIdle( $gPID, 10, 500, 120000 )
	TimerEnd( $UNINSTALL_OPERA_1150 )
EndFunc
