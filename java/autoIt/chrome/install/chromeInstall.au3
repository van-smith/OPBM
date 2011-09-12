#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=chromeInstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;
; Usage:	chromeInstall
;
;======================================================================================================================================
#include <../../common/chrome/chromeCommon.au3>

Dim $CurrentLoop
Dim $LoopLimit

$gBaselines[2][0] = $INSTALL_CHROME
$gBaselines[2][1] = $INSTALL_CHROME_SCORE

outputDebug( "Starting up Chrome Installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If isChromeAlreadyInstalled() Then
	outputError( "Chrome already installed" )
	Exit -1
Endif

outputDebug( "InitializeChromeScript()" )
InitializeChromeScript()

KillChromeIfRunning()

outputDebug( "Install()" )
Install()

KillChromeIfRunning()	; Will force-close if it is "resistant" to normal close attempts

; Close any instances of the browser which may have auto-launched
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()

KillChromeIfRunning()

outputDebug( "SetInitialSettings()" )
SetInitialSettings()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "chromeInstallTimes.csv" )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

; The Chrome installer works all by itself, just launch and it goes
Func Install()
	Local $error
	outputDebug( "Attempting to launch " & $CHROME_INSTALLER )

	KillChromeIfRunning()

	TimerBegin()
	$gPID = Run($CHROME_INSTALLER, "C:\", @SW_MAXIMIZE)
	Sleep(8000)
	; The install process is a little quirky on this one, so we wait a full second
	opbmWaitUntilSystemIdle( $gPercent, 1000, 45000 )
	TimerEnd( $INSTALL_CHROME )
	
	; Wait for a possible (optional?) post-launch window to arrive
	Sleep(10000)
	WinClose( "Google Chrome" )
	
	KillChromeIfRunning()
EndFunc

Func SetInitialSettings()
	Sleep(2000)
	; This feature was wrapped into this C++ function in the opbm.dll
	ChromeInstallerAssist()
EndFunc
