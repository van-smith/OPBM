#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=chromeInstall.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;
; Usage:	chromeInstall
;
;======================================================================================================================================
#include <../../common/chrome/chromeCommon.au3>

Dim $CurrentLoop
Dim $LoopLimit
Dim $ChromeLocalAppData
Dim $googleUpdateFolder

$gBaselines[2][0] = $INSTALL_CHROME
$gBaselines[2][1] = $INSTALL_CHROME_SCORE

outputDebug( "Starting up Chrome Installer" )

outputDebug( "InitializeGlobalVariables()" )
InitializeGlobalVariables()

If isChromeAlreadyInstalled() Then
	outputError( "Chrome already installed" )
	Exit -1
Endif

outputDebug( "InitializeChromeScript()" )
InitializeChromeScript()

;KillChromeIfRunning();rcp

;+rcp 12/21/2011 To remove old preferences, etc.
outputDebug( "Removing Chrome localappdata")
RemoveChromeLocalAppData()
;rcp

outputDebug( "Install()" )
Install()

;KillChromeIfRunning()	; Will force-close if it "resists" normal close attempts;rcp

; Close any instances of the browser which may have auto-launched
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()

KillChromeIfRunning();rcp

outputDebug( "SetInitialSettings()" )
SetInitialSettings()

; Verify Chrome is actually installed
If not isChromeAlreadyInstalled() Then
	outputError( "Chrome did not install properly" )
	opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
	Exit -1
Endif

;+rcp 12/21/2011 To remove automatic updates
outputDebug( "Removing Google updater" )
RemoveGoogleUpdater()
;rcp

outputDebug( "FinalizeScript()" )
opbmFinalizeScript( "chromeInstallTimes.csv" )

opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

; The Chrome installer works all by itself, just launch and it goes
Func Install()
	Local $error
	outputDebug( "Attempting to launch " & $CHROME_INSTALLER )

	;KillChromeIfRunning();rcp

	TimerBegin()
	$gPID = Run($CHROME_INSTALLER, "C:\", @SW_MAXIMIZE)
	Sleep(8000)
	; The install process is a little quirky on this one, so we wait a full second
	;Added the following for slow installs -rcp
	;The installer takes about 90 sec mostly looking for the Internet
	If(WinExists("Google Chrome Installer")) Then
		WinActivate("Google Chrome Installer")
		WinWaitNotActive("Google Chrome Installer"," ",180000)
	EndIf
	;-rcp
	opbmWaitUntilSystemIdle( $gPercent, 1000, 45000 )
	TimerEnd( $INSTALL_CHROME )

	; Wait for a possible (optional?) post-launch window to arrive
	Sleep(10000)
	;+rcp 12/21/2011
	If(WinExists("Installation")) Then
		WinActivate("Installation")
		Send("{enter}") ;just in case it's required
	EndIf

	If (WinExists("Google Chrome")) Then
		WinActivate("Google Chrome")
		WinClose( "Google Chrome" )
	EndIf
	;rcp

	;KillChromeIfRunning();rcp
EndFunc

Func SetInitialSettings()
	Sleep(2000)
	; This feature was wrapped into this C++ function in the opbm.dll
	ChromeInstallerAssist()
EndFunc

;+rcp 12/21/2011 To remove old preferences, etc.
Func RemoveChromeLocalAppData()
	$ChromeLocalAppData = EnvGet("localappdata")&"\google"
	If FileExists($ChromeLocalAppData) Then
		DirRemove($ChromeLocalAppData,1)
	Else
		outputDebug($ChromeLocalAppData&" does not exist")
	EndIf
EndFunc
;rcp

;+rcp 12/21/2011 To remove automatic updates
Func RemoveGoogleUpdater()
	$googleUpdateFolder = EnvGet("ProgramFiles(x86)")&"\Google\Update"
	If FileExists($googleUpdateFolder) Then
		DirRemove($googleUpdateFolder,1)
	Else
		outputDebug($googleUpdateFolder&" does not exist")
	EndIf
EndFunc
;rcp