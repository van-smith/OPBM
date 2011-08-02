Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $DATA_DIRECTORY					= @ScriptDir & "\data"
Const $EXE_DIRECTORY					= @ScriptDir & "\exe"

; Default physical installation directory from Firefox 5.0.1 installer
Const $SAFARI_DIRECTORY					= "C:\Program Files (x86)\Safari"

; Relative paths to reach startup/main page for benchmarks
Const $SUNSPIDER_URL					= $DATA_DIRECTORY		&	"\driver.html"
Const $KRAKEN_URL						= $DATA_DIRECTORY		&	"\driver.html"
Const $GOOGLEV8_URL						= $DATA_DIRECTORY		&	"\run.html"

; Executables used to install, uninstall, or execute Firefox
Const $SAFARI_INSTALLER					= $EXE_DIRECTORY		&	"\SafariSetup.exe"
Const $SAFARI_UNINSTALLER				= $EXE_DIRECTORY		&	"\SafariSetup.exe"
Const $SAFARI_EXECUTABLE				= $SAFARI_DIRECTORY		&	"\safari.exe"
Const $SAFARI_EXECUTABLE_TO_LAUNCH		= $SAFARI_EXECUTABLE	&	" file:\\" & $OPBM_SPLASH_HTML
Const $SAFARI_UNINSTALL_COMMAND			= $SAFARI_UNINSTALLER

; Constants used throughout for various scripts
Const $LAUNCH_SAFARI 					= "Launch Safari 5.1.7534.50"
Const $CLOSE_SAFARI 					= "Close Safari"
Const $SAFARI_WINDOW					= "Untitled"
Const $OPEN_FILE_DIALOG_TITLE			= "Open"

Const $TYPE_SUNSPIDER_URL 				= "Type SunSpider URL"
Const $TYPE_GOOGLEV8_URL				= "Type URL to Google V8 benchmark"
Const $TYPE_KRAKEN_URL					= "Type URL to Kraken benchmark"
Const $LAUNCH_SAFARI_INSTALLER			= "Launch Safari 5.1.7534.50 Installer"
Const $LAUNCH_SAFARI_UNINSTALLER		= "Launch Safari 5.1.7534.50 Un-installer"
Const $BYPASS_NEXT_BUTTON				= "Bypass Next Button"
Const $INSTALL_SAFARI					= "Install Safari 5.1.7534.50"
Const $UNINSTALL_SAFARI					= "Un-install Safari 5.1.7534.50"
Const $CLOSE_UNINSTALLER				= "Close Un-installer"

Const $RUN_SUNSPIDER 					= "Run SunSpider"
Const $RUN_GOOGLEV8						= "Run Google V8"
Const $RUN_KRAKEN						= "Run Kraken"

; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_SAFARI
$gBaselines[0][1] = $LAUNCH_SAFARI_SCORE
$gBaselines[1][0] = $CLOSE_SAFARI
$gBaselines[1][1] = $CLOSE_SAFARI_SCORE
For $i = 2 to $gBaselineSize - 1
	$gBaselines[ $i ][0]	= "--unused--"
	$gBaselines[ $i ][1]	= 0.0
Next


;============================================================================================================================
;============================================================================================================================

Func isSafariAlreadyInstalled()
	If FileExists( $SAFARI_EXECUTABLE ) Then
		return True
	EndIf	
	return False
EndFunc

Func InitializeSafariScript()
	Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
EndFunc

Func LaunchSafari()
	outputDebug( "Attempting to launch " & $SAFARI_EXECUTABLE )
	
	If Not isSafariAlreadyInstalled() Then
		outputError("Safari is not installed.")
		Exit -1
	EndIf
	
	TimerBegin()
	$gPID = Run( $SAFARI_EXECUTABLE_TO_LAUNCH, "C:\", @SW_SHOWMAXIMIZED )
	opbmWaitUntilProcessIdle( $gPID, 5, 100, 5000 )
	opbmWinWaitActivate( $OPBM_SPLASH_HTML_TITLE )
	TimerEnd( $LAUNCH_SAFARI )
	
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc

Func CloseSafari($title)
	; Bring the browser window foreground (if it's not)
	opbmWinWaitActivate( $title, "", $gTimeout, $ERROR_PREFIX & "WinWait: Safari: Unable to find Window.")
	
	; Start the timer
	TimerBegin()
	
	; Close Opera
	WinActivate( $title )
	WinClose( $title )	
	
	; Wait until the sytem settles down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
	
	; Take the ending timer
	TimerEnd( $CLOSE_SAFARI )
EndFunc
