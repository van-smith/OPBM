#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=chromeUninstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
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

outputDebug( "Starting up Chrome Uninstaller" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If not isChromeAlreadyInstalled() Then
	outputError( "Chrome is not installed" )
	Exit -1
Endif

outputDebug( "InitializeScript()" )
InitializeChromeScript()

KillChromeIfRunning()

outputDebug( "LaunchUninstaller()" )
LaunchUninstaller()

outputDebug( "Uninstall()" )
Uninstall()

KillChromeIfRunning()
CloseCallbackWindow()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "chromeUninstallTimes.csv" )
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func LaunchUninstaller()
	Dim $filename1
	Dim $filename2
	Dim $filename3
	
	KillChromeIfRunning()
	
	$filename1	= GetCSIDLDirectory( "COMMON_PROGRAMS" ) & "Google Chrome\Uninstall Google Chrome.lnk"
	$filename2	= GetCSIDLDirectory( "PROGRAMS" ) & "Google Chrome\Uninstall Google Chrome.lnk"
	$filename3	= $CHROME_UNINSTALL_COMMAND
	
	If FileExists( $filename1 ) Then
		TimerBegin()
		outputDebug( "Attempting ShellExecute " & $filename1 )
		$gPID = ShellExecute( $filename1, "C:\", @SW_SHOWMAXIMIZED )
	Else
		$gPID = 0
	EndIf
	
	If $gPID = 0 Then
		If FileExists( $filename2 ) Then
			TimerBegin()
			outputDebug( "Attempting ShellExecute " & $filename2 )
			$gPID = ShellExecute( $filename2, "C:\", @SW_SHOWMAXIMIZED )
		Else
			$gPID = 0
		EndIf
		
		If $gPID = 0 Then
			TimerBegin()
			outputDebug( "Attempting to launch " & $filename3 )
			$gPID = Run( $filename3, "C:\", @SW_SHOWMAXIMIZED )
			If $gPID = 0 Then
				ErrorHandle( "Unable to launch Chrome uninstaller " & $filename1 & " or " & $filename2 & " or " & $filename3 )
			EndIf
		EndIf
	EndIf
	If $gPID = 0 Then
		$gPID = Run( 'uninstallChrome.bat', @ScriptDir, @SW_HIDE )
	EndIf

	;opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
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
	
	TimerEnd( $UNINSTALL_CHROME )
EndFunc

Func CloseCallbackWindow()
	; Give it time to launch
	Sleep(2000)
	
	; Close it if/when it launches
	outputDebug( "CloseCallbackWindow()" )
	opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
EndFunc
