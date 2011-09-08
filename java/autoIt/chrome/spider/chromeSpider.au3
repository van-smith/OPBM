#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=chromeSpider.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;
; Usage:	chromeSpider [ repeat_count ]
;
;======================================================================================================================================
#include <../../common/chrome/chromeCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

$gBaselines[2][0] = $TYPE_SUNSPIDER_URL
$gBaselines[2][1] = $CHROME_TYPE_SUNSPIDER_URL_SCORE
$gBaselines[3][0] = $RUN_SUNSPIDER
$gBaselines[3][1] = $CHROME_RUN_SUNSPIDER_SCORE

outputDebug( "Starting up Chrome Spider" )

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
	outputDebug( "TypeURL()" )
	opbmTypeURL( $SUNSPIDER_URL, $TYPE_SUNSPIDER_URL, $OPEN_FILE_DIALOG_TITLE )

	; Unique portions to this benchmark
	outputDebug( "RunSunSpider()" )
	RunSunSpider()
	; End
	
	outputDebug( "CloseChrome()" )
	CloseChrome( "Benchmark Results" )
	outputDebug( "FinalizeScript()" )
	opbmFinalizeScript( "chromeSpiderTimes.csv" )
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
