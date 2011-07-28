#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=chromeKraken.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	chromeKraken [ repeat_count ]
;
;======================================================================================================================================
#include <../../common/chrome/chromeCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

$gBaselines[2][0] = $TYPE_KRAKEN_URL
$gBaselines[2][1] = 2.0
$gBaselines[3][0] = $RUN_KRAKEN
$gBaselines[3][1] = 88.0556364251051

outputDebug( "Starting up Chrome Kraken" )

if $CmdLine[0] > 0 then 
	$LoopLimit = $CmdLine[1] 
Else 
	$LoopLimit = 1
EndIf
For $CurrentLoop = 1 to $LoopLimit
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	outputDebug( "InitializeChromeScript()" )
	InitializeChromeScript()
	outputDebug( "LaunchChrome()" )
	LaunchChrome()
	outputDebug( "TypeKrakenURL()" )
	opbmTypeURL( $KRAKEN_URL, $TYPE_KRAKEN_URL, $OPEN_FILE_DIALOG_TITLE )
	
	; Unique portions to this benchmark
	outputDebug( "RunKraken()" )
	RunKraken()
	; End
	
	outputDebug( "CloseChrome()" )
	CloseChrome( "Benchmark Results" )
	outputDebug( "FinalizeScript()" )
	opbmFinalizeScript( "chromeKrakenTimes.csv" )
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
	
	; This benchmark takes a few seconds to boot up, during which time there's not much CPU use, but lots of disk use.
	; Wait a few seconds before beginning to test the timing
	Sleep(8000)
 	
	; Wait up to six minutes for the benchmark to complete:
	opbmWaitUntilSystemIdle( 10, 250, 480000 )
	
	; Record ending time
	TimerEnd( $RUN_KRAKEN )
	
	; Wait for system to settle down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc
