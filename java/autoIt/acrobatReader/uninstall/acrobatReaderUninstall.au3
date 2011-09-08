#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=acrobatReaderUninstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;
; Usage:	acrobatReaderUninstall
;
;======================================================================================================================================
#include <../../common/adobe/adobeCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see adobeCommon for definition of 0 and 1)
$gBaselines[2][0] = $LAUNCH_ACROBAT_READER_UNINSTALLER
$gBaselines[2][1] = $LAUNCH_ACROBAT_READER_UNINSTALLER_SCORE
$gBaselines[3][0] = $UNINSTALL_ACROBAT_READER
$gBaselines[3][1] = $UNINSTALL_ACROBAT_READER_SCORE

outputDebug( "Starting up Adobe Acrobat Un-installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If not isAcrobatReaderAlreadyInstalled() Then
	outputError( $ACROBAT_READER_IS_NOT_INSTALLED )
	Exit -1
Endif

outputDebug( "InitializeScript()" )
InitializeAdobeScript()

outputDebug( "LaunchAcrobatReaderUninstaller()" )
LaunchAcrobatReaderUninstaller()

outputDebug( "Uninstall()" )
Uninstall()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "acrobatReaderUninstallTimes.csv" )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func LaunchAcrobatReaderUninstaller()
	outputDebug( "Attempting to launch " & $ACROBAT_READER_UNINSTALLER )
	
	; Begin the timer before we launch the installer
	TimerBegin()
	$gPID = Run( $ACROBAT_READER_UNINSTALLER, "C:\", @SW_SHOW )
	
	; Adobe's installer at startup is not always using the CPU heavy, but sometimes file copying, so we wait a few seconds
	Sleep(3000)
	
	; Wait until it is done uncompressing and begins with its dialog
	opbmWinWaitActivate( $ACROBAT_READER_INSTALLER_WINDOW, $ADOBE_READER_X_MAINTENANCE , 120, $ERROR_PREFIX & "WinWait: Acrobat Reader Setup: Unable to find Window.")
	TimerEnd( $LAUNCH_ACROBAT_READER_UNINSTALLER )
	
	; Wait for the system to settle down
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc

Func Uninstall()
	TimerBegin()
	; Click shift-tab to get the "Next" button, then press it
	WinActivate( $ACROBAT_READER_INSTALLER_WINDOW, $ADOBE_READER_X_MAINTENANCE )
	Send( "{Down}!n" )
	Sleep(250)
	
	; Clicking that button immediately begins the uninstall process
	; Acrobat's main installer algorithm is not always using the CPU heavy, but sometimes file copying
	; It also takes a long time on slow systems, more than two minutes
	opbmWinWaitActivate( $ACROBAT_READER_INSTALLER_WINDOW, $SETUP_COMPLETED, 240, $ERROR_PREFIX & "WinWait: Acrobat Reader Setup Completed: Unable to find Window.")
	
	; Click the "Finish" button
	WinActivate( $ACROBAT_READER_INSTALLER_WINDOW, $SETUP_COMPLETED)
	Send( "!f" )
	
	; The disk hammers away for a long time after the process ends, so wait for it to settle down for 2 seconds
	outputDebug( "Waiting for hard disk to settle down" )
	opbmWaitUntilSystemIdle( 10, 2000, 120000 )
	
	TimerEnd( $UNINSTALL_ACROBAT_READER )
EndFunc
