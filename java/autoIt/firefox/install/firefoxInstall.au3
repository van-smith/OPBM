#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=firefoxInstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	firefoxInstall
;
;======================================================================================================================================
#include <../../common/firefox/firefoxCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

$gBaselines[2][0] = $LAUNCH_FIREFOX_INSTALLER
$gBaselines[2][1] = $LAUNCH_FIREFOX_INSTALLER_SCORE
$gBaselines[3][0] = $BYPASS_NEXT_BUTTON
$gBaselines[3][1] = $BYPASS_NEXT_BUTTON_SCORE
$gBaselines[4][0] = $INSTALL_FIREFOX
$gBaselines[4][1] = $INSTALL_FIREFOX_SCORE

outputDebug( "Starting up Firefox 5.0.1 Installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If isFirefoxAlreadyInstalled() Then
	outputError( "Firefox 5.0.1 already installed" )
	Exit
Endif

outputDebug( "InitializeFirefoxScript()" )
InitializeFirefoxScript()

outputDebug( "LaunchFirefoxInstaller()" )
LaunchFirefoxInstaller()

outputDebug( "BypassNextButtons()" )
BypassNextButtons()

outputDebug( "Install()" )
Install()

outputDebug( "SetInitialSettings()" )
SetInitialSettings()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "firefoxInstallTimes.csv" )
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func LaunchFirefoxInstaller()
	outputDebug( "Attempting to launch " & $FIREFOX_INSTALLER )
	TimerBegin()
	$gPID = Run($FIREFOX_INSTALLER, "C:\", @SW_SHOW)
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $MOZILLA_FIREFOX_SETUP, "Welcome to", $gTimeout, $ERROR_PREFIX & "WinWait: Mozilla Firefox Setup Welcome: Unable to find Window.")
	TimerEnd( $LAUNCH_FIREFOX_INSTALLER )
EndFunc

Func BypassNextButtons()
	outputDebug( "Bypassing 'Next' buttons..." )
	TimerBegin()
	Send("!n")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	opbmWinWaitActivate( $MOZILLA_FIREFOX_SETUP, "Setup Type", $gTimeout, $ERROR_PREFIX & "WinWait: Mozilla Firefox Setup Type: Unable to find Window.")
	; Select "Standard" install
	Send("!s")
	; Select "Next" button
	Send("!n")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	opbmWinWaitActivate( $MOZILLA_FIREFOX_SETUP, "Summary", $gTimeout, $ERROR_PREFIX & "WinWait: Mozilla Firefox Setup Summary: Unable to find Window.")
	TimerEnd( $BYPASS_NEXT_BUTTON )
EndFunc

Func Install()
	outputDebug( "Installing..." )
	
	TimerBegin()
	; Turn off "Use Firefox as the default browser"
	Send("!s")
	; Select "Install"
	Send("!i")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	opbmWinWaitActivate( "Mozilla Firefox Setup", "Completing the", $gTimeout, $ERROR_PREFIX & "WinWait: Mozilla Firefox Setup Completion: Unable to find Window.")
	; Turn off the option to launch Firefox after install
	Send("!l")
	; Selected "finished"
	Send("!f")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $INSTALL_FIREFOX )
EndFunc

Func SetInitialSettings()
	; This feature was wrapped into this C++ function in the opbm.dll
	FirefoxInstallerAssist()
EndFunc
