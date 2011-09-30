Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $DATA_DIRECTORY					= @ScriptDir & "\data"
Const $EXE_DIRECTORY					= @ScriptDir & "\exe"

; Default physical installation directory from Chrome 12.0.742.122 installer
Const $CHROME_DIRECTORY					= EnvGet( "userprofile" ) & "\AppData\Local\Google\Chrome\Application"

; Relative paths to reach startup/main page for benchmarks
Const $SUNSPIDER_URL					= $DATA_DIRECTORY		&	"\driver.html"
Const $KRAKEN_URL						= $DATA_DIRECTORY		&	"\driver.html"
Const $GOOGLEV8_URL						= $DATA_DIRECTORY		&	"\run.html"

; Executables used to install, uninstall, or execute Chrome
Const $CHROME_INSTALLER					= $EXE_DIRECTORY		&	"\ChromeStandaloneSetup.exe"
Const $CHROME_UNINSTALLER				= chr(34) & $CHROME_DIRECTORY				&	"\12.0.742.122\Installer\setup.exe" & chr(34)
;Const $CHROME_UNINSTALLER				= $EXE_DIRECTORY		&	"\ChromeStandaloneSetup.exe"
Const $CHROME_UNINSTALL_COMMAND			= chr(34) & $CHROME_UNINSTALLER				& chr(34) &	" --uninstall"
Const $CHROME_EXECUTABLE				= chr(34) & $CHROME_DIRECTORY				&	"\chrome.exe" & chr(34)
Const $CHROME_EXECUTABLE_TO_LAUNCH		= chr(34) & $CHROME_DIRECTORY				&	"\chrome.exe" & chr(34) & " " & $OPBM_SPLASH_HTML


; Constants used throughout for various scripts
Const $LAUNCH_CHROME 					= "Launch Chrome"
Const $CLOSE_CHROME						= "Close Chome"
Const $CHROME_WINDOW					= "Untitled"
Const $OPEN_FILE_DIALOG_TITLE			= "Open"

Const $TYPE_SUNSPIDER_URL 				= "Type SunSpider URL"
Const $TYPE_GOOGLEV8_URL				= "Type URL to Google V8 benchmark"
Const $TYPE_KRAKEN_URL					= "Type URL to Kraken benchmark"
Const $LAUNCH_CHROME_INSTALLER			= "Launch Chrome Installer"
Const $LAUNCH_CHROME_UNINSTALLER		= "Launch Chrome Un-installer"
Const $BYPASS_NEXT_BUTTON				= "Bypass Next Button"
Const $BYPASS_UNINSTALL_BUTTON			= "Bypass Uninstall Button"
Const $INSTALL_CHROME					= "Install Chrome"
Const $UNINSTALL_CHROME					= "Un-install Chrome"
Const $CLOSE_UNINSTALLER				= "Close Un-installer"

Const $RUN_SUNSPIDER 					= "Run SunSpider"
Const $RUN_GOOGLEV8						= "Run Google V8"
Const $RUN_KRAKEN						= "Run Kraken"

; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_CHROME
$gBaselines[0][1] = $LAUNCH_CHROME_SCORE
$gBaselines[1][0] = $CLOSE_CHROME
$gBaselines[1][1] = $CLOSE_CHROME_SCORE
For $i = 2 to $gBaselineSize - 1
	$gBaselines[ $i ][0]	= "--unused--"
	$gBaselines[ $i ][1]	= 0.0
Next


;============================================================================================================================
;============================================================================================================================


Func isChromeAlreadyInstalled()
	If FileExists( $CHROME_EXECUTABLE ) Then
		return True
	EndIf
	If FileExists( StringReplace( $CHROME_EXECUTABLE, chr(34), "" ) ) Then
		return True
	EndIf
	return False
EndFunc

Func InitializeChromeScript()
	Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	KillChromeIfRunning()
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
EndFunc

Func LaunchChrome()
	Local $filename1
	Local $filename2
	Local $filename3
	
	If Not isChromeAlreadyInstalled() Then
		outputError("Chrome is not installed.")
		Exit -1
	EndIf

	KillChromeIfRunning()

;	$filename1	= GetCSIDLDirectory( "COMMON_PROGRAMS" ) & "Google Chrome\Google Chrome.lnk"
;	$filename2	= GetCSIDLDirectory( "PROGRAMS" ) & "Google Chrome\Google Chrome.lnk"
	$filename3	= $CHROME_EXECUTABLE_TO_LAUNCH
	
;	If FileExists( $filename1 ) Then
;		TimerBegin()
;		outputDebug( "Attempting ShellExecute " & $filename1 )
;		$gPID = ShellExecute( $filename1 & $OPBM_SPLASH_HTML , "C:\", @SW_SHOWMAXIMIZED )
;	Else
;		$gPID = 0
;	EndIf
;	
;	If $gPID = 0 Then
;		If FileExists( $filename2 ) Then
;			TimerBegin()
;			outputDebug( "Attempting ShellExecute " & $filename2 )
;			$gPID = ShellExecute( $filename2 & $OPBM_SPLASH_HTML , "C:\", @SW_SHOWMAXIMIZED )
;		Else
;			$gPID = 0
;		EndIf
;		
;		If $gPID = 0 Then
			TimerBegin()
			outputDebug( "Attempting to launch " & $filename3 )
			$gPID = Run( $filename3, "C:\", @SW_SHOWMAXIMIZED )
			If $gPID = 0 Then
;				ErrorHandle( "Unable to launch Chrome via " & $filename1 & " or " & $filename2 & " or " & $filename3 )
				ErrorHandle( "Unable to launch Chrome via " & $filename3 )
			EndIf
;		EndIf
;	EndIf
	opbmWaitUntilProcessIdle( $gPID, 5, 100, 5000 )
	opbmWinWaitActivate( $OPBM_SPLASH_HTML_TITLE, "", 30 )
	TimerEnd( $LAUNCH_CHROME )
EndFunc

Func CloseChrome( $title )
	; Bring the browser window foreground (if it's not)
	opbmWinWaitActivate( $title, "", $gTimeout, $ERROR_PREFIX & "WinWait: Chrome: Unable to find Window.")
	
	; Start the timer
	TimerBegin()
	
	; Close Chrome
	WinActivate( $title )
	WinClose( $title )

	KillChromeIfRunning()

	; Wait until the sytem settles down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
	
	; Take the ending timer
	TimerEnd( $CLOSE_CHROME )
EndFunc

Func KillChromeIfRunning()
	KillProcessByName( "chrome.exe" )
EndFunc
