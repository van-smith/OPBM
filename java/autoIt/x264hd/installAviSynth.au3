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

Const $FILENAME_AVISYNTH_INSTALL			= @ScriptDir & "\data\AviSynthInstall.exe"
Const $WINDOW_AVISYNTH_INSTALL_1			= "Installer Language"
Const $WINDOW_AVISYNTH_INSTALL_2			= "AviSynth 2."

Const $LAUNCH_AVISYNTH_INSTALL				= "Launch AviSynth Install"
;Const $LAUNCH_AVISYNTH_INSTALL_SCORE		= 1.268034864			;Moved to baselineScores 2011_12_16 -rcp  1.0
Const $RUN_AVISYNTH_INSTALL					= "Run AviSynth Install"
;Const $RUN_AVISYNTH_INSTALL_SCORE			= 1.250468203			;Moved to baselineScores 2011_12_16 -rcp  1.0

Const $CPU_USAGE_THRESHOLD 					= 5 ; percent
Const $CPU_USAGE_THRESHOLD_TIME				= 100 ; milliseconds
Const $CPU_USAGE_THRESHOLD_TIMEOUT			= 5000 ; milliseconds

Dim $gBaselineSize
$gBaselineSize = 20
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_AVISYNTH_INSTALL
$gBaselines[0][1] = $LAUNCH_AVISYNTH_INSTALL_SCORE
$gBaselines[1][0] = $RUN_AVISYNTH_INSTALL
$gBaselines[1][1] = $RUN_AVISYNTH_INSTALL_SCORE

outputDebug( "initializeAviSynthInstallScript" )
initializeAviSynthInstallScript()
outputDebug( "launchAviSynthInstaller()" )
launchAviSynthInstaller()
; select default languge (English)
ControlClick( $WINDOW_AVISYNTH_INSTALL_1, "", 1, "left", 1 )
opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
sleep( 2000 )
; select default languge (English)
ControlClick( $WINDOW_AVISYNTH_INSTALL_2, "", 1, "left", 1 )
opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
sleep( 2000 )
; select default languge (English)
ControlClick( $WINDOW_AVISYNTH_INSTALL_2, "", 1, "left", 1 )
opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
sleep( 2000 )
; Click the Install button:
TimerBegin()
ControlClick( $WINDOW_AVISYNTH_INSTALL_2, "", 1, "left", 1 )
opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
TimerEnd( $RUN_AVISYNTH_INSTALL )
sleep( 2000 )
; Click Next:
ControlClick( $WINDOW_AVISYNTH_INSTALL_2, "", 1, "left", 1 )
opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
sleep( 2000 )
; Click Finish:
ControlClick( $WINDOW_AVISYNTH_INSTALL_2, "", 1, "left", 1 )
opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
sleep( 2000 )

Exit

Func launchAviSynthInstaller()
	; Attempt to launch the application
	outputDebug( "Attempting to launch " & $FILENAME_AVISYNTH_INSTALL)
	TimerBegin()
	$gPID = Run( $FILENAME_AVISYNTH_INSTALL, "C:\", @SW_SHOWDEFAULT )
	opbmWinWaitActivate( $WINDOW_AVISYNTH_INSTALL_1, "", $gTimeout, $ERROR_PREFIX & "WinWait: AviSynthInstall. Unable to find Window." )
	opbmWaitUntilSystemIdle( $CPU_USAGE_THRESHOLD, $CPU_USAGE_THRESHOLD_TIME, $CPU_USAGE_THRESHOLD_TIMEOUT )
	TimerEnd( $LAUNCH_AVISYNTH_INSTALL )
EndFunc

Func initializeAviSynthInstallScript( $retrieveProcess = 1)
	Opt("WinTitleMatchMode", 2)     ;1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")

	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()

	If $retrieveProcess = 1 Then
		$gPID = WinGetProcess ( $WINDOW_AVISYNTH_INSTALL_1 )
		outputDebug( "AviSynth Installer PID: " & $gPID )
	EndIf
EndFunc

