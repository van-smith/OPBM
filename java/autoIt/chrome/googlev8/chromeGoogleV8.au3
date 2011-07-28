#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=chromeGoogleV8.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	chromeGoogleV8 [ repeat_count ]
;
;======================================================================================================================================
#include <../../common/chrome/chromeCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

$gBaselines[2][0] = $TYPE_GOOGLEV8_URL
$gBaselines[2][1] = 2.5
$gBaselines[3][0] = $RUN_GOOGLEV8
$gBaselines[3][1] = 24.00828219972

outputDebug( "Starting up Chrome Google V8" )

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
	opbmTypeURL( $GOOGLEV8_URL, $TYPE_GOOGLEV8_URL, $OPEN_FILE_DIALOG_TITLE )
	
	; Unique portions to this benchmark
	outputDebug( "RunGoogleV8()" )
	RunGoogleV8()
	; End
	
	outputDebug( "CloseChrome()" )
	CloseChrome( "V8 Benchmark Suite" )
	outputDebug( "FinalizeScript()" )
	opbmFinalizeScript( "chromeGoogleV8Times.csv" )
Next
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func RunGoogleV8()
	; As we press enter it begins processing
	Send( "{Enter}" )
	
	; Start the timer for the benchmark
	TimerBegin()
	Sleep(5000)
 	
	; Wait up to six minutes for the benchmark to complete:
	opbmWaitUntilSystemIdle( 10, 500, 360000 )
	
	; Record ending time
	TimerEnd( $RUN_GOOGLEV8 )
	
	; Wait for system to settle down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc
