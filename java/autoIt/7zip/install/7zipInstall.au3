#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=7zipInstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written August 2011 by:
; -----------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	7zipInstall
;
;======================================================================================================================================
#include <../../common/7zip/7zipCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see adobeCommon for definition of 0 and 1)
$gBaselines[2][0] = $LAUNCH_SEVENZIP_INSTALLER
$gBaselines[2][1] = $LAUNCH_SEVENZIP_INSTALLER_SCORE
$gBaselines[3][0] = $INSTALL_SEVENZIP
$gBaselines[3][1] = $INSTALL_SEVENZIP_SCORE

outputDebug( "Starting up 7-Zip Installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If is7ZipAlreadyInstalled() Then
	outputError( "7-zip is already installed" )
	Exit -1
Endif

outputDebug( "InitializeScript()" )
Initialize7ZipScript()

outputDebug( "Launch7ZipInstaller()" )
Launch7ZipInstaller()

outputDebug( "AcceptAndInstall()" )
AcceptAndInstall()

outputDebug( "LaunchAcrobatReader()" )
LaunchAcrobatReader()

outputDebug( "AcceptLicenseAgreement()" )
AcceptLicenseAgreement()

outputDebug( "SetInitialSettings()" )
SetInitialSettings()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "7ZipInstallTimes.csv" )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func Launch7ZipInstaller()
	Local $installer
	
	If is64BitOS() Then
		$installer = $SEVENZIP_INSTALLER_X64
	Else
		$installer = $SEVENZIP_INSTALLER_I386
	EndIf
	outputDebug( "Attempting to launch " & $installer )
	
	; Begin the timer before we launch the installer
	TimerBegin()
	$gPID = Run( $installer, "C:\", @SW_SHOW )
	; Wait for it to load
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
	
	; Wait until it is done uncompressing and begins with its dialog
	opbmWinWaitActivate( $ACROBAT_READER_INSTALLER_WINDOW, $READY_TO_INSTALL, 120, $ERROR_PREFIX & "WinWait: Acrobat Reader Setup: Unable to find Window.")
	TimerEnd( $LAUNCH_ACROBAT_READER_INSTALLER )
	
	; Wait for the system to settle down, as the windows flash for a moment here
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc

Func AcceptAndInstall()
	TimerBegin()
	; Click the "Install" button
	WinActivate( $ACROBAT_READER_INSTALLER_WINDOW, $READY_TO_INSTALL )
	Send("!i")
	; Clicking that button immediately begins the install process
	
	; Acrobat's main installer algorithm is not always using the CPU heavy, but sometimes file copying
	; It also takes a long time on slow systems, more than two minutes
	opbmWinWaitActivate( $ACROBAT_READER_INSTALLER_WINDOW, $SETUP_COMPLETED, 240, $ERROR_PREFIX & "WinWait: Acrobat Reader Setup Completed: Unable to find Window.")
	
	; Click the "Finish" button
	WinActivate( $ACROBAT_READER_INSTALLER_WINDOW, $SETUP_COMPLETED )
	Send("!f")
	
	; The disk hammers away for a long time after the process ends, so wait for it to settle down for 2 seconds
	outputDebug( "Waiting for hard disk to settle down" )
	opbmWaitUntilSystemIdle( 10, 2000, 120000 )
	
	TimerEnd( $INSTALL_ACROBAT_READER )
EndFunc

Func AcceptLicenseAgreement()
	opbmWinWaitActivate( $ACROBAT_READER_LICENSE_AGREEMENT, "", $gTimeout, $ERROR_PREFIX & "WinWait: Acrobat Reader License Agreement: Unable to find Window.")
	ControlClick( $ACROBAT_READER_LICENSE_AGREEMENT, "", "[TEXT:Accept]", "left" )
	Sleep(1000)
EndFunc

Func SetSecurityUpdatesToManual()
	opbmWinWaitActivate( $ACROBAT_READER_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Acrobat Reader: Unable to find Window.")
	
	; Send Alt-E for edit menu, then "n" for Prefere&nces
	Send( "!en" )
	Sleep(500)
	
	opbmWinWaitActivate( $PREFERENCES, "", $gTimeout, $ERROR_PREFIX & "WinWait: Acrobat Reader Preferences: Unable to find Window.")
	; We arrive on a list of categories, but we don't know where we might be
	; Move to top of list
	Send( "{end}" )
	Sleep(100)
	; This process takes a long time to load the first time
	opbmWaitUntilSystemIdle( 10, 1000, $gTimeout )
	
	; Choose the "Do &not download or install updates automatically"
	Send( "!n" )
	Sleep(100)
	
	; Click the OK button
	ControlClick( $PREFERENCES, "", "[TEXT:OK]", "left")
	Sleep(250)
	
	opbmWinWaitActivate( $ACROBAT_READER_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Acrobat Reader: Unable to find Window.")
	; Close acrobat
	Send( "!fx" )
EndFunc