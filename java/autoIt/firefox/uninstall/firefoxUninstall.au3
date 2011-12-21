#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=firefoxUninstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;
; Usage:	firefoxUninstall
;
;======================================================================================================================================
#include <../../common/firefox/firefoxCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

$gBaselines[2][0] = $LAUNCH_FIREFOX_UNINSTALLER
$gBaselines[2][1] = $LAUNCH_FIREFOX_UNINSTALLER_SCORE
$gBaselines[3][0] = $UNINSTALL_FIREFOX
$gBaselines[3][1] = $UNINSTALL_FIREFOX_SCORE
$gBaselines[4][0] = $CLOSE_UNINSTALLER
$gBaselines[4][1] = $CLOSE_UNINSTALLER_SCORE

outputDebug( "Starting up Firefox 5.0.1 Uninstaller" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If not isFirefoxAlreadyInstalled() Then
	outputError( "Firefox 5.0.1 is not installed" )
	Exit -1
Endif

outputDebug( "InitializeScript()" )
InitializeFirefoxScript()

outputDebug( "LaunchUninstaller()" )
LaunchUninstaller()

outputDebug( "Uninstall()" )
Uninstall()

outputDebug( "CloseUninstaller()" )
CloseUninstaller()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "firefoxUninstallTimes.csv" )
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func LaunchUninstaller()
	outputDebug( "Attempting to launch " & $FIREFOX_UNINSTALLER )
	TimerBegin()
	$gPID = Run($FIREFOX_UNINSTALLER, "C:\", @SW_MAXIMIZE)
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	opbmWinWaitActivate( "Mozilla Firefox Uninstall", "Welcome to", $gTimeout, $ERROR_PREFIX & "WinWait: Mozilla Firefox Uninstall Welcome: Unable to find Window.")
	TimerEnd( $LAUNCH_FIREFOX_UNINSTALLER )
EndFunc

Func Uninstall()
	outputDebug( "Bypassing 'Next' buttons..." )
	
	Send("!n")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( "Mozilla Firefox Uninstall", "Uninstall", $gTimeout, $ERROR_PREFIX & "WinWait: Mozilla Firefox Uninstall: Unable to find Window.")
	
	TimerBegin()
	Send("!u")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( "Mozilla Firefox Uninstall", "Completing the", $gTimeout, $ERROR_PREFIX & "WinWait: Mozilla Firefox Uninstall Completion: Unable to find Window.")
	TimerEnd( $UNINSTALL_FIREFOX )
EndFunc

Func CloseUninstaller()
	TimerBegin()
	Send("!f")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $CLOSE_UNINSTALLER )
EndFunc
