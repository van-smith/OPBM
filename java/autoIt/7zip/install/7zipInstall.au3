#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=7zipInstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written August 2011 by:
; -----------------------
;	Van Smith
;
; Usage:	7zipInstall
;
;======================================================================================================================================
#include <../../common/7zip/7zipCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit
Dim $InstallerWindowTitle
Dim $is386Installer

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

If $is386Installer Then
	outputDebug( "AcceptAndInstall()" )
	AcceptAndInstalli386()
EndIf

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "7ZipInstallTimes.csv" )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func Launch7ZipInstaller()
	Local $installer
	
	If is64BitOS() Then
		$installer				= $SEVENZIP_INSTALLER_X64
		$InstallerWindowTitle	= $SEVENZIP_INSTALLER_TITLE_X64
		$is386Installer			= False
		
	Else
		$installer				= $SEVENZIP_INSTALLER_I386
		$InstallerWindowTitle	= $SEVENZIP_INSTALLER_TITLE_I386
		$is386Installer			= True
		
	EndIf
	outputDebug( "Attempting to launch " & $installer )
	
	; Begin the timer before we launch the installer
	TimerBegin()
	$gPID = Run( $installer, "C:\", @SW_SHOW )
	; Wait for it to load
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
	
	If not $is386Installer Then
		; The 64-bit installer works passively
		If ProcessWaitClose( $gPID, 60 ) <> 1 Then
			ErrorHandle( "Error with 7-Zip 64-bit installer" )
		EndIf
	Else
		; Wait until it is done uncompressing and begins with its dialog
		opbmWinWaitActivate( $InstallerWindowTitle, "Welcome", 30, $ERROR_PREFIX & "WinWait: 7-Zip 9.20 Setup: Unable to find Window.")
	EndIf
	TimerEnd( $LAUNCH_SEVENZIP_INSTALLER )
	
	; Wait for the system to settle down, as the windows flash for a moment here
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc

Func AcceptAndInstalli386()
	outputDebug("Waiting for " & $InstallerWindowTitle & " + Welcome")
	opbmWinWaitActivate( $InstallerWindowTitle, "Choose Install Location", 30, $ERROR_PREFIX & "WinWait: 7-Zip 9.20 Setup: Unable to find Window.")
	
	TimerBegin()
	; Click the "Install" button
	Send("!i")
	Sleep(250)
	outputDebug("Waiting for " & $InstallerWindowTitle & " + Completing")
	opbmWinWaitActivate( $InstallerWindowTitle, "Completing", 60, $ERROR_PREFIX & "WinWait: 7-Zip 9.20 Setup: Unable to find Window.")
	
	; Click the finish button
	Send("{Enter}")
	Sleep(250)
	
	; Wait until everything is completed
	outputDebug("Waiting for hard disk to settle down")
	opbmWaitUntilSystemIdle( 10, 500, $gTimeout )
	
	TimerEnd( $INSTALL_SEVENZIP )
EndFunc
