#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=operaInstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;
; Usage:	operaInstall
;
;======================================================================================================================================
#include <../../common/opera/operaCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

; Begin at 2 (see operaCommon for definition of 0 and 1)
$gBaselines[2][0] = $LAUNCH_OPERA_1150_INSTALLER
$gBaselines[2][1] = $LAUNCH_OPERA_1150_INSTALLER_SCORE
$gBaselines[3][0] = $INSTALL_OPERA_1150
$gBaselines[3][1] = $INSTALL_OPERA_1150_SCORE

outputDebug( "Starting up Opera 11.50 Installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If isOperaAlreadyInstalled() Then
	outputError( "Opera 11.50 already installed" )
	Exit -1
Endif

outputDebug( "InitializeScript()" )
InitializeOperaScript()
InitializeOperaScriptInstallSpecific()

outputDebug( "LaunchOperaInstaller()" )
LaunchOperaInstaller()

outputDebug( "AcceptAndInstall()" )
AcceptAndInstall()

; Close the auto-launch browser (if it appears)
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()

outputDebug( "SetInitialSettings()" )
SetInitialSettings()

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "operaInstallTimes.csv" )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func InitializeOperaScriptInstallSpecific()
	Opt("MouseCoordMode", 2)		; 0=active window, 1=absolute screen, 2=client area of active window
EndFunc

Func LaunchOperaInstaller()
	outputDebug( "Attempting to launch " & $OPERA_INSTALLER )
	
	; Begin the timer before we launch the installer
	TimerBegin()
	$gPID = Run($OPERA_INSTALLER, "C:\", @SW_SHOW)
	
	; Opera's installer at startup is not always using the CPU heavy, but sometimes file copying, so we wait a few seconds
	Sleep(3000)
	
	; Wait until it is done uncompressing and begins with its dialog
	opbmWinWaitActivate( "Opera 11.50 - Installer", "", $gTimeout, $ERROR_PREFIX & "WinWait: Opera 11.50 - Installer: Unable to find Window.")
	TimerEnd( $LAUNCH_OPERA_1150_INSTALLER )
	
	; Wait for the system to settle down
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc

Func AcceptAndInstall()
	outputDebug( "Accept and install" )
	
	TimerBegin()
	; Sent an enter key, as the default option is "Accept and Install"
	Send("{Enter}")
	; Clicking that button immediately begins the install process
	; Opera's main installer algorithm is not always using the CPU heavy, but sometimes file copying, so we wait a few seconds
	Sleep(3000)
	; There is no termination screen, so we wait for the system to be idle for a long while
	opbmWaitUntilSystemIdle( 10, 1000, 120000 )
	TimerEnd( $INSTALL_OPERA_1150 )
EndFunc

Func SetInitialSettings()
	Sleep(2000)
	; This feature was wrapped into this C++ function in the opbm.dll
	OperaInstallerAssist()
EndFunc
