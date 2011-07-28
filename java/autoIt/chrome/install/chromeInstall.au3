#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=chromeInstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	chromeInstall
;
;======================================================================================================================================
#include <../../common/chrome/chromeCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

$gBaselines[2][0] = $INSTALL_CHROME
$gBaselines[2][1] = 20.0961445413738

outputDebug( "Starting up Chrome 12.0.742.122 Installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If isChromeAlreadyInstalled() Then
	outputError( "Chrome 12.0.742.122 already installed" )
	Exit
Endif

outputDebug( "InitializeChromeScript()" )
InitializeChromeScript()

outputDebug( "Install()" )
Install()

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
	
	TimerBegin()
	$gPID = Run($CHROME_INSTALLER, "C:\", @SW_MAXIMIZE)
	Sleep(8000)
	; The install process is a little quirky on this one, so we wait a full second
	opbmWaitUntilSystemIdle( $gPercent, 1000, 45000 )
	TimerEnd( $INSTALL_CHROME )
	
	; Wait for a possible (optional?) post-launch window to arrive
	Sleep(10000)
	WinClose( "Google Chrome" )
EndFunc
