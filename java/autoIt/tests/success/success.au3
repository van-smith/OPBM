#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=success.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written August 2011 by:
; ----------------------
;	Van Smith
;
; Usage:	success
;
;======================================================================================================================================
#include <../../common/ie/ieCommon.au3>

outputDebug( "Simulating a success" )
Sleep(1000)
outputStatus( "Running test()" )
Sleep(1000)
outputTiming( "Simulated test result,10.0, 100.0" )
outputStatus( "TimerFinish: Total Runtime,10.0" )
Exit
