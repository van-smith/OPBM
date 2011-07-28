#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=acrobatReader91Uninstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	acrobatReader91Uninstall
;
;======================================================================================================================================
#include <../../common/adobe/adobeCommon91.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see adobeCommon for definition of 0 and 1)
$gBaselines[2][0] = $LAUNCH_ACROBAT_READER_UNINSTALLER
$gBaselines[2][1] = 15.9717848536923
$gBaselines[3][0] = $UNINSTALL_ACROBAT_READER
$gBaselines[3][1] = 47.7901606281408

outputDebug( "Starting up Adobe Acrobat Installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If not isAcrobatReaderAlreadyInstalled() Then
	outputError( "Acrobat Reader is not installed" )
	Exit
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
	opbmWinWaitActivate( $ACROBAT_READER_INSTALLER_WINDOW, $WELCOME_TO_SETUP_FOR_ADOBE_READER_91, 120, $ERROR_PREFIX & "WinWait: Acrobat Reader 9.1 Setup: Unable to find Window.")
	TimerEnd( $LAUNCH_ACROBAT_READER_UNINSTALLER )
	
	; Wait for the system to settle down
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc

Func Uninstall()
	TimerBegin()
	; Click shift-tab to get the "Next" button, then press it
	WinActivate( $ACROBAT_READER_INSTALLER_WINDOW, $WELCOME_TO_SETUP_FOR_ADOBE_READER_91 )
	Send( "!n" )
	Sleep(250)
	opbmWinWaitActivate( $ACROBAT_READER_INSTALLER_WINDOW, $PROGRAM_MAINTENANCE, 10, $ERROR_PREFIX & "WinWait: Acrobat Reader 9.1 Setup: Unable to find Window.")
	
	; Click the down arrow to get to the "Remove" option, then tab tab to get to the Next button, then press it
	WinActivate( $ACROBAT_READER_INSTALLER_WINDOW, $PROGRAM_MAINTENANCE )
	Send( "!r" )
	Sleep(100)
	Send( "!n" )
	Sleep(250)
	opbmWinWaitActivate( $ACROBAT_READER_INSTALLER_WINDOW, $REMOVE_THE_PROGRAM, 10, $ERROR_PREFIX & "WinWait: Acrobat Reader 9.1 Setup: Unable to find Window.")
	
	; Click the "Remove" button
	WinActivate( $ACROBAT_READER_INSTALLER_WINDOW, $REMOVE_THE_PROGRAM )
	Send( "!r" )
	Sleep(250)
	
	; Clicking that button immediately begins the uninstall process
	; Acrobat's main installer algorithm is not always using the CPU heavy, but sometimes file copying
	; It also takes a long time on slow systems, more than two minutes
	opbmWinWaitActivate( $ACROBAT_READER_INSTALLER_WINDOW, $SETUP_COMPLETED, 240, $ERROR_PREFIX & "WinWait: Acrobat Reader 9.1 Setup Completed: Unable to find Window.")
	
	; Click the "Finish" button
	WinActivate( $ACROBAT_READER_INSTALLER_WINDOW, $SETUP_COMPLETED)
	Send( "!f" )
	
	; The disk hammers away for a long time after the process ends, so wait for it to settle down for 2 seconds
	outputDebug( "Waiting for hard disk to settle down" )
	opbmWaitUntilSystemIdle( 10, 2000, 120000 )
	
	TimerEnd( $UNINSTALL_ACROBAT_READER )
EndFunc
