#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=ieSpider.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	ieSpider [ repeat_count ]
;
;======================================================================================================================================
#include <../../common/ie/ieCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

$gBaselines[1][0] = $TYPE_SUNSPIDER_URL
$gBaselines[1][1] = 1.9663037460281
$gBaselines[2][0] = $RUN_SUNSPIDER
$gBaselines[2][1] = 18.2511285168844

outputDebug( "Starting up Internet Explorer SunSpider" )

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
	outputDebug( "TypeURL()" )
	opbmTypeURL( $SUNSPIDER_URL, $TYPE_SUNSPIDER_URL, $OPEN_FILE_DIALOG_TITLE )

	; Unique portions to this benchmark
	outputDebug( "RunSunSpider()" )
	RunSunSpider()
	; End
	
	outputDebug( "CloseIE()" )
	CloseIE()
	outputDebug( "FinalizeScript()" )
	opbmFinalizeScript( "ieSpiderTimes.csv" )
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
	opbmWaitUntilSystemIdle( 10, 500, 360000 )
	
	; Record ending time
	TimerEnd( $RUN_SUNSPIDER )
	
	; Wait for system to settle down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc
