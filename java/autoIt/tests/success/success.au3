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
outputStatus( "Running test()" )
outputTiming( "Simulated test result1,10.0, 100.0" )
outputTiming( "Simulated test result2,9.0, 105.0" )
outputStatus( "TimerFinish: Total Runtime,19.0" )
Exit
