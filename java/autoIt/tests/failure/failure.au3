#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=failure.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written August 2011 by:
; ----------------------
;	Van Smith
;
; Usage:	failure
;
;======================================================================================================================================
#include <../../common/ie/ieCommon.au3>

outputDebug( "Simulating a failure" )
Sleep(1000)
outputStatus( "Attempting to do something()" )
Sleep(1000)
outputError( "Failure" )
Exit -1
