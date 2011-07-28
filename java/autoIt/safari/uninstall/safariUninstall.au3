#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=safariUninstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	safariUninstall
;
;======================================================================================================================================
#include <../../common/safari/safariCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see safariCommon for definition of 0 and 1)
$gBaselines[2][0] = $LAUNCH_SAFARI_UNINSTALLER
$gBaselines[2][1] = 2.0
$gBaselines[3][0] = $UNINSTALL_SAFARI
$gBaselines[3][1] = 2.0

outputDebug( "Starting up Safari 5.1.7534.50 Un-installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If not isSafariAlreadyInstalled() Then
	outputError( "Safari 5.1.7534.50 is not installed" )
	Exit
Endif

outputDebug( "InitializeScript()" )
InitializeSafariScript()
InitializeScriptUninstallSpecific()

outputDebug( "LaunchUninstaller()" )
LaunchUninstaller()

outputDebug( "Uninstall()" )
Uninstall()

outputDebug( "CloseUninstaller()" )
CloseUninstaller()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "safariUninstallTimes.csv" )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func InitializeScriptUninstallSpecific()
	Opt("MouseCoordMode", 2)		; 0=active window, 1=absolute screen, 2=client area of active window
EndFunc

Func LaunchUninstaller()
	outputDebug( "Attempting to launch " & $SAFARI_UNINSTALL_COMMAND )
	TimerBegin()
	$gPID = Run($SAFARI_UNINSTALL_COMMAND, "C:\", @SW_SHOW)
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	opbmWinWaitActivate( "Safari", "Change or remove Safari", $gTimeout, $ERROR_PREFIX & "WinWait: Safari Uninstaller: Unable to find Window.")
	TimerEnd( $LAUNCH_SAFARI_UNINSTALLER )
EndFunc

Func Uninstall()
	outputDebug( "Clicking uninstall (remove)" )
	
	; Click on "Remove"
	ControlClick( "Safari", "Change or remove Safari", "[CLASS:Button; INSTANCE:3]", "left" )
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( "Safari", "Do you want", $gTimeout, $ERROR_PREFIX & "WinWait: Safari Uninstall Confirmation: Unable to find Window.")
	
	; Click on "Yes"
	TimerBegin()
	ControlClick( "Safari", "Do you want", "[CLASS:Button; INSTANCE:1]", "left" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( "Safari", "Safari Installer Completed", $gTimeout, $ERROR_PREFIX & "WinWait: Safari Uninstall Confirmation: Unable to find Window.")
	TimerEnd( $UNINSTALL_SAFARI )
EndFunc

Func CloseUninstaller()
	; Click on "Finish"
	ControlClick( "Safari", "Safari Installer Completed", "[CLASS:Button; INSTANCE:3]", "left" )
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc
