#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=safariInstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;
; Usage:	safariInstall
;
;======================================================================================================================================
#include <../../common/safari/safariCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see safariCommon for definition of 0 and 1)
$gBaselines[2][0] = $LAUNCH_SAFARI_INSTALLER
$gBaselines[2][1] = $LAUNCH_SAFARI_INSTALLER_SCORE
$gBaselines[3][0] = $INSTALL_SAFARI
$gBaselines[3][1] = $INSTALL_SAFARI_SCORE

outputDebug( "Starting up Safari 5.1.7534.50 Installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If isSafariAlreadyInstalled() Then
	outputError( "Safari 5.1.7534.50 already installed" )
	Exit -1
Endif

outputDebug( "InitializeScript()" )
InitializeSafariScript()
InitializeScriptInstallSpecific()

outputDebug( "LaunchSafariInstaller()" )
LaunchSafariInstaller()

outputDebug( "AcceptAndInstall()" )
AcceptAndInstall()

outputDebug( "DismissCongratulations()" )
DismissCongratulations()

outputDebug( "SetInitialSettings()" )
SetInitialSettings()

; Close any instances that may have launched
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "safariInstallTimes.csv" )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func InitializeScriptInstallSpecific()
	Opt("MouseCoordMode", 2)		; 0=active window, 1=absolute screen, 2=client area of active window
EndFunc

Func LaunchSafariInstaller()
	outputDebug( "Attempting to launch " & $SAFARI_INSTALLER )
	
	; Begin the timer before we launch the installer
	TimerBegin()
	$gPID = Run($SAFARI_INSTALLER, "C:\", @SW_SHOW)
	
	; Safari's installer at startup is not always using the CPU heavy, but sometimes file copying, so we wait a few seconds
	Sleep(3000)
	
	; Wait until it is done uncompressing and begins with its dialog
	opbmWinWaitActivate( "Safari", "Welcome to", $gTimeout, $ERROR_PREFIX & "WinWait: Safari Welcome: Unable to find Window.")
	TimerEnd( $LAUNCH_SAFARI_INSTALLER )
	
	; Wait for the system to settle down
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc

Func AcceptAndInstall()
	outputDebug( "Accepting and installing" )
	
	; Click "Next"
	ControlClick( "Safari", "Welcome to", "[CLASS:Button; INSTANCE:2]", "left" )
	Sleep(500)
	
	; Click "I accept the terms..."
	opbmWinWaitActivate( "Safari", "License Agreement", $gTimeout, $ERROR_PREFIX & "WinWait: Safari License Agreement: Unable to find Window.")
	ControlClick( "Safari", "License Agreement", "[CLASS:Button; INSTANCE:3]", "left" )
	Sleep(100)
	; Click "Next"
	ControlClick( "Safari", "License Agreement", "[CLASS:Button; INSTANCE:5]", "left" )
	Sleep(100)
	
	; Un-check "Install Safari Desktop Shortcuts"
	opbmWinWaitActivate( "Safari", "Installation Options", $gTimeout, $ERROR_PREFIX & "WinWait: Safari Installation Options: Unable to find Window.")
	ControlClick( "Safari", "Installation Options", "[CLASS:Button; INSTANCE:1]", "left" )
	Sleep(100)
	; Un-check "Make Safari the default browser for all users"
	ControlClick( "Safari", "Installation Options", "[CLASS:Button; INSTANCE:2]", "left" )
	Sleep(100)
	; Un-check "Install Bonjour for Windows"
	ControlClick( "Safari", "Installation Options", "[CLASS:Button; INSTANCE:3]", "left" )
	Sleep(100)
	; Un-check "Automatically update Safari"
	ControlClick( "Safari", "Installation Options", "[CLASS:Button; INSTANCE:4]", "left" )
	Sleep(100)
	; Click "Next"
	ControlClick( "Safari", "Installation Options", "[CLASS:Button; INSTANCE:5]", "left" )
	Sleep(500)
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( "Safari", "Install Destination", $gTimeout, $ERROR_PREFIX & "WinWait: Safari Installation Options: Unable to find Window.")
	
	; Click "Install"
	TimerBegin()
	ControlClick( "Safari", "Install Destination", "[CLASS:Button; INSTANCE:1]", "left" )
	; This install is slow, so wait a bit for it to get going
	Sleep(5000)
	; And wait for it to be well finished for up to 6 minutes
	opbmWaitUntilSystemIdle( 10, 1000, 360000 )
	opbmWinWaitActivate( "Safari", "Congratulations", 30, $ERROR_PREFIX & "WinWait: Safari Congratulations Window: Unable to find Window.")
	TimerEnd( $INSTALL_SAFARI )
EndFunc

Func DismissCongratulations()
	; Un-check "Open Safari after the installer exits"
	ControlClick( "Safari", "Congratulations", "[CLASS:Button; INSTANCE:1]", "left" )
	Sleep(100)
	
	; Click "Finish"
	ControlClick( "Safari", "Congratulations", "[CLASS:Button; INSTANCE:3]", "left" )
	Sleep(500)
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc

Func SetInitialSettings()
	; This feature was wrapped into this C++ function in the opbm.dll
	SafariInstallerAssist()
EndFunc
