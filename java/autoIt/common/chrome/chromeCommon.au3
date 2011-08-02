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
Const $CHROME_UNINSTALLER				= $CHROME_DIRECTORY		&	"\12.0.742.122\Installer\setup.exe"
Const $CHROME_UNINSTALL_COMMAND			= $CHROME_UNINSTALLER	&	" --uninstall"
Const $CHROME_EXECUTABLE				= $CHROME_DIRECTORY		&	"\chrome.exe"
Const $CHROME_EXECUTABLE_TO_LAUNCH		= $CHROME_DIRECTORY		&	"\chrome.exe " & $OPBM_SPLASH_HTML


; Constants used throughout for various scripts
Const $LAUNCH_CHROME 					= "Launch Chrome 12.0.742.122"
Const $CLOSE_CHROME						= "Close Chome 12.0.742.122"
Const $CHROME_WINDOW					= "Untitled"
Const $OPEN_FILE_DIALOG_TITLE			= "Open"

Const $TYPE_SUNSPIDER_URL 				= "Type SunSpider URL"
Const $TYPE_GOOGLEV8_URL				= "Type URL to Google V8 benchmark"
Const $TYPE_KRAKEN_URL					= "Type URL to Kraken benchmark"
Const $LAUNCH_CHROME_INSTALLER			= "Launch Chrome Installer"
Const $LAUNCH_CHROME_UNINSTALLER		= "Launch Chrome Un-installer"
Const $BYPASS_NEXT_BUTTON				= "Bypass Next Button"
Const $BYPASS_UNINSTALL_BUTTON			= "Bypass Uninstall Button"
Const $INSTALL_CHROME					= "Install Chrome 12.0.742.122"
Const $UNINSTALL_CHROME					= "Un-install Chrome 12.0.742.122"
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
	return False
EndFunc

Func InitializeChromeScript()
	Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
EndFunc

Func LaunchChrome()
	outputDebug( "Attempting to launch " & $CHROME_EXECUTABLE )
	
	If Not isChromeAlreadyInstalled() Then
		outputError("Chrome is not installed.")
		Exit
	EndIf
	
	TimerBegin()
	$gPID = Run( $CHROME_EXECUTABLE_TO_LAUNCH, "C:\", @SW_SHOWMAXIMIZED )
	opbmWaitUntilProcessIdle( $gPID, 5, 100, 5000 )
	opbmWinWaitActivate( $OPBM_SPLASH_HTML_TITLE )
	TimerEnd( $LAUNCH_CHROME )
	
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc

Func CloseChrome( $title )
	; Bring the browser window foreground (if it's not)
	opbmWinWaitActivate( $title, "", $gTimeout, $ERROR_PREFIX & "WinWait: Chrome: Unable to find Window.")
	
	; Start the timer
	TimerBegin()
	
	; Close Chrome
	WinActivate( $title )
	WinClose( $title )
	
	; Wait until the sytem settles down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
	
	; Take the ending timer
	TimerEnd( $CLOSE_CHROME )
EndFunc
