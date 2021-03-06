#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=safariSpider.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;
; Usage:	safariSpider [ repeat_count ]
;
;======================================================================================================================================
#include <../../common/safari/safariCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see safariCommon for definition of 0 and 1)
;$gBaselines[2][0] = $TYPE_SUNSPIDER_URL
;$gBaselines[2][1] = $SAFARI_TYPE_SUNSPIDER_URL_SCORE
$gBaselines[2][0] = $RUN_SUNSPIDER
$gBaselines[2][1] = $SAFARI_RUN_SUNSPIDER_SCORE

outputDebug( "Starting up Safari SunSpider" )

if $CmdLine[0] > 0 then
	$LoopLimit = $CmdLine[1]
Else
	$LoopLimit = 1
EndIf
For $CurrentLoop = 1 to $LoopLimit
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	outputDebug( "InitializeSafariScript()" )
	InitializeSafariScript()
	outputDebug( "LaunchSafari()" )
	LaunchSafari()
	outputDebug( "TypeURL()" )
	opbmTypeURLSafari( $SUNSPIDER_URL, $TYPE_SUNSPIDER_URL )

	; Unique portions to this benchmark
	outputDebug( "RunSunSpider()" )
	RunSunSpider()
	; End

	outputDebug( "CloseSafari()" )
	CloseSafari( "Benchmark Results" )
	outputDebug( "FinalizeScript()" )
	opbmFinalizeScript( "safariSpiderTimes.csv" )
Next
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func RunSunSpider()
	; As we press enter it begins processing
	Send( "{Enter}" )

	; Start the timer for the benchmark
	TimerBegin()
	Sleep(5000)

	; Wait up to six minutes for the benchmark to complete:
	opbmWaitUntilSystemIdle( 10, 500, 360000 ) ;Changed from 250 to 500 -rcp 12/12/2011

	; Record ending time
	TimerEnd( $RUN_SUNSPIDER )

	; Wait for system to settle down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc
