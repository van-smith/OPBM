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
$gBaselines[2][1] = 2.0
$gBaselines[3][0] = $BYPASS_NEXT_BUTTON
$gBaselines[3][1] = 2.0
$gBaselines[4][0] = $INSTALL_FIREFOX
$gBaselines[4][1] = 2.0

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
	opbmWinWaitActivate( "Mozilla Firefox Setup", "Welcome to", $gTimeout, $ERROR_PREFIX & "WinWait: Mozilla Firefox Setup Welcome: Unable to find Window.")
	TimerEnd( $LAUNCH_FIREFOX_INSTALLER )
EndFunc

Func BypassNextButtons()
	outputDebug( "Bypassing 'Next' buttons..." )
	TimerBegin()
	Send("!n")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	opbmWinWaitActivate( "Mozilla Firefox Setup", "Setup Type", $gTimeout, $ERROR_PREFIX & "WinWait: Mozilla Firefox Setup Type: Unable to find Window.")
	Send("!s")
	Send("!n")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	opbmWinWaitActivate( "Mozilla Firefox Setup", "Summary", $gTimeout, $ERROR_PREFIX & "WinWait: Mozilla Firefox Setup Summary: Unable to find Window.")
	TimerEnd( $BYPASS_NEXT_BUTTON )
EndFunc

Func Install()
	outputDebug( "Installing..." )
	
	TimerBegin()
	Send("!i")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	opbmWinWaitActivate( "Mozilla Firefox Setup", "Completing the", $gTimeout, $ERROR_PREFIX & "WinWait: Mozilla Firefox Setup Completion: Unable to find Window.")
	Send("!l")
	Send("!f")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $INSTALL_FIREFOX )
EndFunc

Func SetInitialSettings()
	; We need to copy our prefs.js to this location:
	; C:\Users\*\AppData\Roaming\Mozilla\Firefox\Profiles\*\prefs.js
EndFunc
