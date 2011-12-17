#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 11.23.2011

	Description: installAviSynth installs the AviSynth video encoder.  Note that the AviSynth installation program
				 has been renamed "AviSynthInstall.exe".

	Usage:	Launching installAviSynth.au3 will install the AviSynth video encoder and return performance
	        metrics in OPBM format.

#ce ======================================================================================================================================
Const $ROOT_DIR = @ScriptDir & "\.."

#include <..\common\opbm\opbmCommon.au3>

Const $FILENAME_AVISYNTH_UNINSTALL			= "C:\Program Files (x86)\AviSynth 2.5\Uninstall.exe"
Const $WINDOW_AVISYNTH_UNINSTALL_1			= "Installer Language"
Const $WINDOW_AVISYNTH_UNINSTALL_2			= "AviSynth Uninstall"

Const $LAUNCH_AVISYNTH_UNINSTALL			= "Launch AviSynth Uninstall"
;Const $LAUNCH_AVISYNTH_UNINSTALL_SCORE		= 0.922777684			;Moved to baselineScores 2011_12_16 -rcp  1.0
Const $RUN_AVISYNTH_UNINSTALL				= "Run AviSynth Uninstall"
;Const $RUN_AVISYNTH_UNINSTALL_SCORE		= 0.570758179			;Moved to baselineScores 2011_12_16 -rcp  1.0

Const $CPU_USAGE_THRESHOLD 					= 5 ; percent
Const $CPU_USAGE_THRESHOLD_TIME				= 100 ; milliseconds
Const $CPU_USAGE_THRESHOLD_TIMEOUT			= 5000 ; milliseconds

Dim $gBaselineSize
$gBaselineSize = 20
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_AVISYNTH_UNINSTALL
$gBaselines[0][1] = $LAUNCH_AVISYNTH_UNINSTALL_SCORE
$gBaselines[1][0] = $RUN_AVISYNTH_UNINSTALL
$gBaselines[1][1] = $RUN_AVISYNTH_UNINSTALL_SCORE

; -rcp Added 2011_12_08
if not isAviSynthAlreadyInstalled() Then
	outputError( "AviSynth is not installed" )
	Exit -1
Endif
; -rcp
outputDebug( "initializeAviSynthUninstallScript" )
initializeAviSynthUninstallScript()
outputDebug( "launchAviSynthUninstaller()" )
launchAviSynthUninstaller()
; select default languge (English)
ControlClick( $WINDOW_AVISYNTH_UNINSTALL_1, "", 1, "left", 1 )
opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
sleep( 2000 )
; Click the Uninstall button:
TimerBegin()
ControlClick( $WINDOW_AVISYNTH_UNINSTALL_2, "", 1, "left", 1 )
opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
TimerEnd( $RUN_AVISYNTH_UNINSTALL )
; Click "Yes":
Sleep( 2000 )
opbmWinActivate($WINDOW_AVISYNTH_UNINSTALL_2, "", $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_AVISYNTH_UNINSTALL_2 & ": Unable to find Window." )
Send( "!y" )
opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
; Click "Close":
Sleep( 2000 )
opbmWinActivate($WINDOW_AVISYNTH_UNINSTALL_2, "", $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_AVISYNTH_UNINSTALL_2 & ": Unable to find Window." )
Send( "!c" )
opbmWinActivate($WINDOW_AVISYNTH_UNINSTALL_2, "", $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_AVISYNTH_UNINSTALL_2 & ": Unable to find Window." )
; Click "OK":
Sleep( 2000 )
opbmWinActivate($WINDOW_AVISYNTH_UNINSTALL_2, "", $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_AVISYNTH_UNINSTALL_2 & ": Unable to find Window." )
Send( "{enter}" )
opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
Sleep( 2000 )

; Check to make sure uninstaller is closed

Exit
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; -rcp Added 2011_12_08
Func isAviSynthAlreadyInstalled()
	If FileExists( $FILENAME_AVISYNTH_UNINSTALL ) Then
		return True
	EndIf
	If FileExists( StringReplace( $FILENAME_AVISYNTH_UNINSTALL, chr(34), "" ) ) Then
		return True
	EndIf
	return False
EndFunc
; -rcp
Func launchAviSynthUninstaller()
	; Attempt to launch the application
	outputDebug( "Attempting to launch " & $FILENAME_AVISYNTH_UNINSTALL)
	TimerBegin()
	$gPID = Run( $FILENAME_AVISYNTH_UNINSTALL, "C:\", @SW_SHOWDEFAULT )
	opbmWinWaitActivate( $WINDOW_AVISYNTH_UNINSTALL_1, "", $gTimeout, $ERROR_PREFIX & "WinWait: AviSynthInstall. Unable to find Window." )
	opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
	TimerEnd( $LAUNCH_AVISYNTH_UNINSTALL )
EndFunc

Func initializeAviSynthUninstallScript( $retrieveProcess = 1)
	Opt("WinTitleMatchMode", 2)     ;1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")

	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()

	If $retrieveProcess = 1 Then
		$gPID = WinGetProcess ( $WINDOW_AVISYNTH_UNINSTALL_1 )
		outputDebug( "AviSynth Uninstaller PID: " & $gPID )
	EndIf
EndFunc

