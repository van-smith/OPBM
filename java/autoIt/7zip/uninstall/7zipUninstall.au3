#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=7zipUninstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written August 2011 by:
; -----------------------
;	Van Smith
;
; Usage:	7zipUninstall
;
;======================================================================================================================================
#include <../../common/7zip/7zipCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit
Dim $InstallerWindowTitle
Dim $is386Installer

; Begin at 2 (see adobeCommon for definition of 0 and 1)
$gBaselines[2][0] = $LAUNCH_SEVENZIP_UNINSTALLER
$gBaselines[2][1] = $LAUNCH_SEVENZIP_UNINSTALLER_SCORE
$gBaselines[3][0] = $UNINSTALL_SEVENZIP
$gBaselines[3][1] = $UNINSTALL_SEVENZIP_SCORE

outputDebug( "Starting up 7zip Un-installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If not is7ZipAlreadyInstalled() Then
	outputError( $SEVENZIP_IS_NOT_INSTALLED )
	Exit -1
Endif

outputDebug( "InitializeScript()" )
Initialize7ZipScript()

outputDebug( "Launch7ZipUninstaller()" )
Launch7ZipUninstaller()

outputDebug( "Uninstall()" )
If not $is386Installer Then
	Uninstallx64()
Else
	Uninstalli386()
EndIf

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "acrobatReaderUninstallTimes.csv" )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func Launch7ZipUninstaller()
	Local $uninstaller
	
	If FileExists( $SEVENZIP_EXECUTABLE_X64 ) Then
		$uninstaller			= $SEVENZIP_UNINSTALLER_X64
		$InstallerWindowTitle	= $SEVENZIP_UNINSTALLER_TITLE_X64
		$is386Installer			= False
		
	ElseIf FileExists( $SEVENZIP_EXECUTABLE_I386 ) Then
		$uninstaller			= $SEVENZIP_UNINSTALLER_I386
		$InstallerWindowTitle	= $SEVENZIP_UNINSTALLER_TITLE_I386
		$is386Installer			= True
		
	EndIf	
	outputDebug( "Attempting to launch 7-Zip uninstaller " & $uninstaller )
	
	; Begin the timer before we launch the installer
	TimerBegin()
	$gPID = Run( $uninstaller, "C:\", @SW_SHOW )
	
	; Wait until it is done uncompressing and begins with its dialog
	If not $is386Installer Then
		opbmWinWaitActivate( $InstallerWindowTitle, "Welcome", 30, $ERROR_PREFIX & "WinWait: 7-Zip 9.20 Setup: Unable to find Window.")
	Else
		opbmWinWaitActivate( $InstallerWindowTitle, "Uninstall 7-Zip", 30, $ERROR_PREFIX & "WinWait: 7-Zip 9.20 Uninstall: Unable to find Window.")
	EndIf
	TimerEnd( $LAUNCH_SEVENZIP_UNINSTALLER )
	
	; Wait for the system to settle down
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc

Func Uninstallx64()
	TimerBegin()
	; Click the next button
	Send( "!n" )
	Sleep(250)
	outputDebug("Waiting for " & $InstallerWindowTitle & " + Modify, repair, or remove")
	opbmWinWaitActivate( $InstallerWindowTitle, "Modify, repair, or remove", 30, $ERROR_PREFIX & "WinWait: 7-Zip 9.20 Setup: Unable to find Window.")
	
	; Click the remove button
	Send( "!r" )
	Sleep(250)
	outputDebug("Waiting for " & $InstallerWindowTitle & " + Remove 7-Zip")
	opbmWinWaitActivate( $InstallerWindowTitle, "Remove 7-Zip", 30, $ERROR_PREFIX & "WinWait: 7-Zip 9.20 Setup: Unable to find Window.")
	
	; Click the second remove button
	Send( "!r" )
	Sleep(250)
	; Clicking this button immediately begins the uninstall process
	outputDebug("Waiting for " & $InstallerWindowTitle & " + Completing")
	opbmWinWaitActivate( $InstallerWindowTitle, "Completing", 60, $ERROR_PREFIX & "WinWait: 7-Zip 9.20 Setup: Unable to find Window.")

	; Click the "Finish" button
	Send( "!f" )
	Sleep(250)

	; Wait for system to be idle
	opbmWaitUntilSystemIdle( 10, 200, 10000 )
	
	TimerEnd( $UNINSTALL_SEVENZIP)
EndFunc

Func Uninstalli386()
	TimerBegin()
	; Click the uninstall button
	Send( "!u" )
	Sleep(250)
	outputDebug("Waiting for " & $InstallerWindowTitle & " + Completing")
	opbmWinWaitActivate( $InstallerWindowTitle, "Completing", 30, $ERROR_PREFIX & "WinWait: 7-Zip 9.20 Setup: Unable to find Window.")
	
	; Click the "Finish" button
	Send( "!f" )
	Sleep(250)

	; Wait for system to be idle
	opbmWaitUntilSystemIdle( 10, 200, 10000 )
	
	TimerEnd( $UNINSTALL_SEVENZIP)
EndFunc
