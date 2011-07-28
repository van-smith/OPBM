#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=acrobatReader91Install.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	acrobatReader91Install
;
;======================================================================================================================================
#include <../../common/adobe/adobeCommon91.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see adobeCommon for definition of 0 and 1)
$gBaselines[2][0] = $LAUNCH_ACROBAT_READER_INSTALLER
$gBaselines[2][1] = 15.0288786416106
$gBaselines[3][0] = $INSTALL_ACROBAT_READER
$gBaselines[3][1] = 56.8640286312424

outputDebug( "Starting up Adobe Acrobat Installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If isAcrobatReaderAlreadyInstalled() Then
	outputError( "Acrobat Reader already installed" )
	Exit
Endif

outputDebug( "InitializeScript()" )
InitializeAdobeScript()

outputDebug( "LaunchAcrobatReaderInstaller()" )
LaunchAcrobatReaderInstaller()

outputDebug( "AcceptAndInstall()" )
AcceptAndInstall()

outputDebug( "LaunchAcrobatReader()" )
LaunchAcrobatReader()

outputDebug( "AcceptLicenseAgreement()" )
AcceptLicenseAgreement()

outputDebug( "SetSecurityUpdatesToManual()" )
SetSecurityUpdatesToManual()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "acrobatReaderInstallTimes.csv" )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func LaunchAcrobatReaderInstaller()
	outputDebug( "Attempting to launch " & $ACROBAT_READER_INSTALLER )
	
	; Begin the timer before we launch the installer
	TimerBegin()
	$gPID = Run( $ACROBAT_READER_INSTALLER, "C:\", @SW_SHOW )
	
	; Acrobat's installer at startup is not always using the CPU heavy, but sometimes file copying, so we wait a few seconds
	Sleep(3000)
	
	; Wait until it is done uncompressing and begins with its dialog
	opbmWinWaitActivate( $ACROBAT_READER_INSTALLER_WINDOW, $CLICK_NEXT_TO_INSTALL, 120, $ERROR_PREFIX & "WinWait: Acrobat Reader 9.1 Setup: Unable to find Window.")
	TimerEnd( $LAUNCH_ACROBAT_READER_INSTALLER )
	
	; Wait for the system to settle down, as the windows flash for a moment here
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc

Func AcceptAndInstall()
	TimerBegin()
	; Sent a left-click to the "Next" button
	WinActivate( $ACROBAT_READER_INSTALLER_WINDOW, $CLICK_NEXT_TO_INSTALL )
	Send("!n")
	opbmWinWaitActivate( $ACROBAT_READER_INSTALLER_WINDOW, $READY_TO_INSTALL_THE_PROGRAM, 10, $ERROR_PREFIX & "WinWait: Acrobat Reader 9.1 Setup: Unable to find Window.")
	
	; Click the "Install" button
	WinActivate( $ACROBAT_READER_INSTALLER_WINDOW, $READY_TO_INSTALL_THE_PROGRAM )
	Send("!i")
	; Clicking that button immediately begins the install process
	
	; Acrobat's main installer algorithm is not always using the CPU heavy, but sometimes file copying
	; It also takes a long time on slow systems, more than two minutes
	opbmWinWaitActivate( $ACROBAT_READER_INSTALLER_WINDOW, $SETUP_COMPLETED, 240, $ERROR_PREFIX & "WinWait: Acrobat Reader 9.1 Setup Completed: Unable to find Window.")
	
	; Click the "Finish" button
	WinActivate( $ACROBAT_READER_INSTALLER_WINDOW, $SETUP_COMPLETED )
	Send("!f")
	
	; The disk hammers away for a long time after the process ends, so wait for it to settle down for 2 seconds
	outputDebug( "Waiting for hard disk to settle down" )
	opbmWaitUntilSystemIdle( 10, 2000, 120000 )
	
	TimerEnd( $INSTALL_ACROBAT_READER )
EndFunc

Func AcceptLicenseAgreement()
	opbmWinWaitActivate( $ACROBAT_READER_LICENSE_AGREEMENT, "", $gTimeout, $ERROR_PREFIX & "WinWait: Acrobat Reader 9.1 License Agreement: Unable to find Window.")
	ControlClick( $ACROBAT_READER_LICENSE_AGREEMENT, "", "[TEXT:Accept]", "left" )
	Sleep(1000)
EndFunc

Func SetSecurityUpdatesToManual()
	opbmWinWaitActivate( $ACROBAT_READER_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Acrobat Reader 9.1: Unable to find Window.")
	
	; Send Alt-E for edit menu, then "n" for Prefere&nces
	Send( "!en" )
	Sleep(500)
	
	opbmWinWaitActivate( $PREFERENCES, "", $gTimeout, $ERROR_PREFIX & "WinWait: Acrobat Reader 9.1 Preferences: Unable to find Window.")
	; We arrive on a list of categories, but we don't know where we might be
	; Move to top of list
	Send( "{home}" )
	; Go down twice to get to "General"
	Send( "{down}{down}")
	; This process takes a long time to load the first time
	Sleep(5000)
	opbmWaitUntilSystemIdle( 10, 1000, $gTimeout )
	
	; Uncheck the "check for updates" button
	; Gain focus on the "Check for updates option"
	Send( "!c" )
	Sleep(100)
	; Send a "-" key, which always un-selects it
	Send( "-" )
	Sleep(100)
	
	; Click the OK button
	ControlClick( $PREFERENCES, "", "[TEXT:OK]", "left")
	Sleep(250)
	
	opbmWinWaitActivate( $ACROBAT_READER_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Acrobat Reader 9.1: Unable to find Window.")
	; Close acrobat
	Send( "!fx" )
EndFunc
