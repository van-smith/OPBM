#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=ieKraken.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	ieKraken [ repeat_count ]
;
;======================================================================================================================================
#include <../../common/ie/ieCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

$gBaselines[1][0] = $TYPE_KRAKEN_URL
$gBaselines[1][1] = 1.9521766837074
$gBaselines[2][0] = $RUN_KRAKEN
$gBaselines[2][1] = 207.873023287008

outputDebug( "Starting up IE Kraken" )

if $CmdLine[0] > 0 then 
	$LoopLimit = $CmdLine[1] 
Else 
	$LoopLimit = 1
EndIf
For $CurrentLoop = 1 to $LoopLimit
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	outputDebug( "InitializeIEScript()" )
	InitializeIEScript()
	outputDebug( "LaunchIE()" )
	LaunchIE()
	outputDebug( "TypeKrakenURL()" )
	opbmTypeURL( $KRAKEN_URL, $TYPE_KRAKEN_URL, $OPEN_FILE_DIALOG_TITLE )
	
	; Unique portions to this benchmark
	outputDebug( "RunKraken()" )
	RunKraken()
	; End
	
	outputDebug( "CloseIE()" )
	CloseIE()
	outputDebug( "FinalizeScript()" )
	opbmFinalizeScript( "ieKrakenTimes.csv" )
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
 	
	; Wait up to eight minutes for the benchmark to complete:
	opbmWaitUntilSystemIdle( 10, 250, 480000 )
	
	; Record ending time
	TimerEnd( $RUN_KRAKEN )
	
	; Wait for system to settle down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc
