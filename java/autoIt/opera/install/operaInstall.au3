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
$gBaselines[2][0] = $LAUNCH_OPERA_INSTALLER	;Eliminate Version reference -rcp 11/11/11
$gBaselines[2][1] = $LAUNCH_OPERA_INSTALLER_SCORE	;Eliminate Version reference -rcp 11/11/11
$gBaselines[3][0] = $INSTALL_OPERA	;Eliminate Version reference -rcp 11/11/11
$gBaselines[3][1] = $INSTALL_OPERA_SCORE	;Eliminate Version reference -rcp 11/11/11

outputDebug( "Starting up Opera Installer" )	;Eliminate Version reference -rcp 11/11/11

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If isOperaAlreadyInstalled() Then
	outputError( "Opera already installed" )	;Eliminate Version reference -rcp 11/11/11
	Exit -1
Endif

outputDebug( "InitializeScript()" )
InitializeOperaScript()
InitializeOperaScriptInstallSpecific()

outputDebug( "LaunchOperaInstaller()" )
LaunchOperaInstaller()

;Added to resolve "pin to taksbar" problem -rcp 12/6/2011
outputDebug( "ChooseOptions()" )
ChooseOptions()

outputDebug( "AcceptAndInstall()" )
AcceptAndInstall()

; Close the auto-launch browser (if it appears)
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()

outputDebug( "SetInitialSettings()" )
SetInitialSettings()

If not isOperaAlreadyInstalled() Then
	outputError( "Opera did not install properly" )	;Eliminate Version reference -rcp 11/11/11
	opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
	Exit -1
Endif

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
	opbmWinWaitActivate( $OPERA_INSTALLER_WINDOW_NAME, "", $gTimeout, $ERROR_PREFIX & "WinWait: " & $OPERA_INSTALLER_WINDOW_NAME & ": Unable to find Window.")	;Eliminate Version reference -rcp 11/11/11
	TimerEnd( $LAUNCH_OPERA_INSTALLER )	;Eliminate Version reference -rcp 11/11/11

	; Wait for the system to settle down
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc

; Added to resolve "pin to task bar" problem -rcp 12/6/2011
Func ChooseOptions()
	outputDebug( "Choose Options")
	opbmWinWaitActivate( $OPERA_INSTALLER_WINDOW_NAME, "", $gTimeout)
	SendKeepActive($OPERA_INSTALLER_WINDOW_NAME)
	Send("+{TAB}") ;To get to Options button
	Sleep(1000) ;So I can see it happen
	Send("{Enter}") ; To get to Options
	Sleep(1000) ;So I can see it happen
	Send("+{TAB 2}") ;To get to the first shortcut
	Sleep(1000) ;So I can see it happen
	Send("{SPACE}") ;To uncheck Opera as default browser
	Sleep(1000) ;So I can see it happen
	Send("+{TAB}") ;To get to the second shortcut
	Sleep(1000) ;So I can see it happen
	Send("{SPACE}") ;To uncheck pin to taskbar
	Sleep(1000) ;So I can see it happen
	; Wait for the system to settle down
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
EndFunc
; -rcp

Func AcceptAndInstall()
	outputDebug( "Accept and install" )

	TimerBegin()
	; Sent an enter key, as the default option is "Accept and Install"
	Send("{Enter}")
	; Clicking that button immediately begins the install process
	; Opera's main installer algorithm is not always using the CPU heavy, but sometimes file copying, so we wait a few seconds
	;Sleep(3000)
	; There is no termination screen, so we wait for the system to be idle for a long while
	opbmWaitUntilSystemIdle( 10, 1000, 120000 )
	TimerEnd( $INSTALL_OPERA )	;Eliminate Version reference -rcp 11/11/11
EndFunc

Func SetInitialSettings()
	Sleep(2000)
	; This feature was wrapped into this C++ function in the opbm.dll
	OperaInstallerAssist()
EndFunc
