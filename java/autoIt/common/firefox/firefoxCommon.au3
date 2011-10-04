Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $DATA_DIRECTORY					= @ScriptDir & "\data"
Const $EXE_DIRECTORY					= @ScriptDir & "\exe"

; Default physical installation directory from Firefox 5.0.1 installer
Const $FIREFOX_DIRECTORY				= "C:\Program Files (x86)\Mozilla Firefox"

; Relative paths to reach startup/main page for benchmarks
Const $SUNSPIDER_URL					= $DATA_DIRECTORY		&	"\driver.html"
Const $KRAKEN_URL						= $DATA_DIRECTORY		&	"\driver.html"
Const $GOOGLEV8_URL						= $DATA_DIRECTORY		&	"\run.html"

; Executables used to install, uninstall, or execute Firefox
Const $FIREFOX_INSTALLER				= chr(34) & $EXE_DIRECTORY		&	"\Firefox Setup 5.0.1.exe" & chr(34)
Const $FIREFOX_UNINSTALLER				= chr(34) & $FIREFOX_DIRECTORY	&	"\uninstall\helper.exe" & chr(34)
Const $FIREFOX_EXECUTABLE				= chr(34) & $FIREFOX_DIRECTORY	&	"\firefox.exe" & chr(34)
Const $FIREFOX_EXECUTABLE_TO_LAUNCH		= chr(34) & $FIREFOX_DIRECTORY	&	"\firefox.exe" & chr(34) & " " & $OPBM_SPLASH_HTML

; Constants used throughout for various scripts
Const $LAUNCH_FIREFOX 					= "Launch Firefox 5.0.1"
Const $CLOSE_FIREFOX					= "Close Firefox"
Const $FIREFOX_WINDOW					= "Mozilla Firefox"
Const $OPEN_FILE_DIALOG_TITLE			= "Open"

Const $TYPE_SUNSPIDER_URL 				= "Type SunSpider URL"
Const $TYPE_GOOGLEV8_URL				= "Type URL to Google V8 benchmark"
Const $TYPE_KRAKEN_URL					= "Type URL to Kraken benchmark"
Const $LAUNCH_FIREFOX_INSTALLER			= "Launch Firefox Installer"
Const $LAUNCH_FIREFOX_UNINSTALLER		= "Launch Firefox Un-installer"
Const $BYPASS_NEXT_BUTTON				= "Bypass Next Button"
Const $INSTALL_FIREFOX					= "Install Firefox 5.0.1"
Const $UNINSTALL_FIREFOX				= "Un-install Firefox 5.0.1"
Const $CLOSE_UNINSTALLER				= "Close Un-installer"
Const $MOZILLA_FIREFOX_SETUP			= "Mozilla Firefox Setup"
Const $WELCOME_TO_FIREFOX				= "Welcome to Firefox"
Const $IMPORT_WIZARD					= "Import Wizard"

Const $RUN_SUNSPIDER 					= "Run SunSpider"
Const $RUN_GOOGLEV8						= "Run Google V8"
Const $RUN_KRAKEN						= "Run Kraken"

; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_FIREFOX
$gBaselines[0][1] = $LAUNCH_FIREFOX_SCORE
$gBaselines[1][0] = $CLOSE_FIREFOX
$gBaselines[1][1] = $CLOSE_FIREFOX_SCORE
For $i = 2 to $gBaselineSize - 1
	$gBaselines[ $i ][0]	= "--unused--"
	$gBaselines[ $i ][1]	= 0.0
Next


;============================================================================================================================
;============================================================================================================================


Func isFirefoxAlreadyInstalled()
	If FileExists( $FIREFOX_EXECUTABLE ) Then
		return True
	EndIf	
	If FileExists( StringReplace( $FIREFOX_EXECUTABLE, chr(34), "" ) ) Then
		return True
	EndIf	
	return False
EndFunc

Func InitializeFirefoxScript()
	Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
EndFunc

Func LaunchFirefox()
	outputDebug( "Attempting to launch " & $FIREFOX_EXECUTABLE )
	
	If Not isFirefoxAlreadyInstalled() Then
		outputError("Firefox is not installed.")
		Exit -1
	EndIf
	
	TimerBegin()
	$gPID = Run( $FIREFOX_EXECUTABLE_TO_LAUNCH, "C:\", @SW_SHOWMAXIMIZED )
	opbmWaitUntilProcessIdle( $gPID, 5, 100, 5000 )
	opbmWinWaitActivate( $OPBM_SPLASH_HTML_TITLE, "", 30 )
	TimerEnd( $LAUNCH_FIREFOX )
EndFunc

Func CloseFirefox()
	; Start the timer
	TimerBegin()
	
	; Close Firefox
	WinActivate( $FIREFOX_WINDOW )
	WinClose( $FIREFOX_WINDOW )	
	
	; Wait until the sytem settles down
	opbmWaitUntilSystemIdle( 10, 100, 10000 )
	
	; Take the ending timer
	TimerEnd( $CLOSE_FIREFOX )
EndFunc
