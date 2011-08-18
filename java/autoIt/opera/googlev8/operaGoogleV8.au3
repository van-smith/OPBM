#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=operaGoogleV8.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	operaGoogleV8 [ repeat_count ]
;
;======================================================================================================================================
#include <../../common/opera/operaCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see operaCommon for definition of 0 and 1)
$gBaselines[2][0] = $TYPE_GOOGLEV8_URL
$gBaselines[2][1] = $OPERA_TYPE_GOOGLEV8_URL_SCORE
$gBaselines[3][0] = $RUN_GOOGLEV8
$gBaselines[3][1] = $OPERA_RUN_GOOGLEV8_SCORE

outputDebug( "Starting up Opera Google V6" )

if $CmdLine[0] > 0 then 
	$LoopLimit = $CmdLine[1] 
Else 
	$LoopLimit = 1
EndIf
For $CurrentLoop = 1 to $LoopLimit
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	outputDebug( "InitializeOperaScript()" )
	InitializeOperaScript()
	outputDebug( "LaunchOpera()" )
	LaunchOpera()
	outputDebug( "TypeGoogleV8URL()" )
	opbmTypeURL( $GOOGLEV8_URL, $TYPE_GOOGLEV8_URL, $OPEN_FILE_DIALOG_TITLE )

	; Unique portions to this benchmark
	outputDebug( "RunGoogleV8()" )
	RunGoogleV8()
	; End
	
	outputDebug( "CloseOpera()" )
	CloseOpera()
	outputDebug( "FinalizeScript()" )
	opbmFinalizeScript( "operaGoogleV8Times.csv" )
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
