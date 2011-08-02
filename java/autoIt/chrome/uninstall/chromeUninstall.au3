#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=chromeUninstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	chromeUninstall
;
;======================================================================================================================================
#include <../../common/chrome/chromeCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

$gBaselines[2][0] = $LAUNCH_CHROME_UNINSTALLER
$gBaselines[2][1] = $LAUNCH_CHROME_UNINSTALLER_SCORE
$gBaselines[3][0] = $UNINSTALL_CHROME
$gBaselines[3][1] = $UNINSTALL_CHROME_SCORE

outputDebug( "Starting up Chrome 12.0.742.122 Un-installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If not isChromeAlreadyInstalled() Then
	outputError( "Chrome 12.0.742.122 is not installed" )
	Exit
Endif

outputDebug( "InitializeScript()" )
InitializeChromeScript()

outputDebug( "LaunchUninstaller()" )
LaunchUninstaller()

outputDebug( "Uninstall()" )
Uninstall()

outputDebug( "CloseCallbackWindow()" )
CloseCallbackWindow()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "chromeUninstallTimes.csv" )
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func LaunchUninstaller()
	outputDebug( "Attempting to launch " & $CHROME_UNINSTALL_COMMAND)
	
	TimerBegin()
	$gPID = Run($CHROME_UNINSTALL_COMMAND, "C:\", @SW_MAXIMIZE)
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( "Uninstall Google Chrome", "", $gTimeout, $ERROR_PREFIX & "WinWait: Uninstall Google Chrome: Unable to find Window.")
	
	TimerEnd( $LAUNCH_CHROME_UNINSTALLER )
EndFunc

Func Uninstall()
	outputDebug( "Bypassing 'Uninstall' buttons..." )
	
	; Click on the "Uninstall" button
	TimerBegin()
	ControlClick( "Uninstall Google Chrome", "", "[CLASS:Button; INSTANCE:1]", "left" )
	; The uninstaller auto-launches a browser instance with the google.com "Why did you uninstall our wonderful browser?" link
	opbmWaitUntilSystemIdle( 10, 500, 60000 )
	
	TimerEnd("Un-install Chrome 12.0.742.122")
EndFunc

Func CloseCallbackWindow()
	; Give it time to launch
	Sleep(10000)
	; Close it if/when it launches
	WinClose( "google.com" )
EndFunc
