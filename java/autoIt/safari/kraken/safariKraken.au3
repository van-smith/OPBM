#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=safariKraken.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;
; Usage:	safariKraken [ repeat_count ]
;
;======================================================================================================================================
#include <../../common/safari/safariCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see safariCommon for definition of 0 and 1)
;$gBaselines[2][0] = $TYPE_KRAKEN_URL
;$gBaselines[2][1] = $SAFARI_TYPE_KRAKEN_URL_SCORE
$gBaselines[2][0] = $RUN_KRAKEN
$gBaselines[2][1] = $SAFARI_RUN_KRAKEN_SCORE

outputDebug( "Starting up Safari Kraken" )

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
	opbmTypeURLSafari( $KRAKEN_URL, $TYPE_KRAKEN_URL, $OPEN_FILE_DIALOG_TITLE )
	
	; Unique portions to this benchmark
	outputDebug( "RunKraken()" )
	RunKraken()
	; End
	
	outputDebug( "CloseSafari()" )
	CloseSafari( "Benchmark Results" )
	outputDebug( "FinalizeScript()" )
	opbmFinalizeScript( "safariKrakenTimes.csv" )
Next
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func RunKraken()
	; As we press enter it begins processing
	Send( "{Enter}" )
	
	; Start the timer for the benchmark
	TimerBegin()
	Sleep(5000)
 	
	; Wait up to sixteen minutes for the benchmark to complete:
	opbmWaitUntilSystemIdle( 10, 500, 960000 )
	
	; Record ending time
	TimerEnd( $RUN_KRAKEN )
	
	; Wait for system to settle down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc
