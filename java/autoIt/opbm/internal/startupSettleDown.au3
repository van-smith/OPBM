#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=rebootTime.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written September 2011 by:
; -------------------------
;	Van Smith
;
; Usage:	startupSettleDown
;
;======================================================================================================================================
#include <../../common/opbm/opbmInternal.au3>

outputDebug( "Wait until 5% for 10 seconds" )
InitializeGlobalVariables()
InitializeInternalOpbmScript()

Dim $count
Dim $percent
Dim $threshholdPercent
Dim $totalCount		; Holds the total number of two second intervals being polled, so we can timeout after 360 seconds
Dim $timeout
Dim $pollInterval
Dim $threshhold

; Iterate until we reach a 10 second interval of 5% or less CPU utilization
; Timeout after 6 minutes, poll every 2 seconds, that way our times are never
; more than two seconds off true
$pollInterval		= 2		; seconds
$threshhold			= 10	; seconds
$timeout			= 360	; seconds
$threshholdPercent	= 5		; percent

; Iterate repeatedly until we reach one of those conditions
TimerBegin()
$count = 0
While ($count * $pollInterval) < $threshhold AND ($totalCount * $pollInterval) < $timeout
	$percent = opbmWaitUntilSystemIdle( $threshholdPercent, $pollInterval * 1000, 1000 )
	If $percent <= $threshholdPercent Then
		$count = $count + 1
	Else
		$count = 0
	EndIf
	outputDebug( Int($percent) & "% at " & Int(TimerDiff( $gTimer ) / 1000) & " seconds" )
	
	; Increment our counter
	$totalCount = $totalCount + 1
WEnd
If ($totalCount * $pollInterval) >= $timeout Then
	outputDebug("Did not settle down to " & $threshholdPercent & "% after " & $timeout & " seconds")
EndIf
TimerEnd( $STARTUP_SETTLE_DOWN )
opbmFinalizeScript( "startupSettleDown.csv" )
Exit
