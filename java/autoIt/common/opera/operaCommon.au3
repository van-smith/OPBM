Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $DATA_DIRECTORY					= @ScriptDir & "\data"
Const $EXE_DIRECTORY					= @ScriptDir & "\exe"

; Default physical installation directory from Firefox 5.0.1 installer
Const $OPERA_DIRECTORY					= "C:\Program Files (x86)\Opera"

; Relative paths to reach startup/main page for benchmarks
Const $SUNSPIDER_URL					= $DATA_DIRECTORY		&	"\driver.html"
Const $KRAKEN_URL						= $DATA_DIRECTORY		&	"\driver.html"
Const $GOOGLEV8_URL						= $DATA_DIRECTORY		&	"\run.html"

; Executables used to install, uninstall, or execute Firefox
Const $OPERA_INSTALLER					= $EXE_DIRECTORY		&	"\Opera_1150_int_Setup.exe"
Const $OPERA_UNINSTALLER				= $OPERA_DIRECTORY		&	"\opera.exe"
Const $OPERA_EXECUTABLE					= $OPERA_DIRECTORY		&	"\opera.exe"
Const $OPERA_EXECUTABLE_TO_LAUNCH		= $OPERA_DIRECTORY		&	"\opera.exe " & $OPBM_SPLASH_HTML
Const $OPERA_UNINSTALL_COMMAND			= $OPERA_UNINSTALLER	&	" --uninstall"

; Constants used throughout for various scripts
Const $LAUNCH_OPERA 					= "Launch Opera 11.50"
Const $CLOSE_OPERA 						= "Close Opera"
Const $OPERA_WINDOW						= " - Opera"
Const $OPEN_FILE_DIALOG_TITLE			= "Open"

Const $TYPE_SUNSPIDER_URL 				= "Type SunSpider URL"
Const $TYPE_GOOGLEV8_URL				= "Type URL to Google V8 benchmark"
Const $TYPE_KRAKEN_URL					= "Type URL to Kraken benchmark"
Const $LAUNCH_OPERA_1150_INSTALLER		= "Launch Opera Installer"
Const $LAUNCH_OPERA_1150_UNINSTALLER	= "Launch Opera Un-installer"
Const $BYPASS_NEXT_BUTTON				= "Bypass Next Button"
Const $INSTALL_OPERA_1150				= "Install Opera 11.50"
Const $UNINSTALL_OPERA_1150				= "Un-install Opera 11.50"
Const $CLOSE_UNINSTALLER				= "Close Un-installer"

Const $RUN_SUNSPIDER 					= "Run SunSpider"
Const $RUN_GOOGLEV8						= "Run Google V8"
Const $RUN_KRAKEN						= "Run Kraken"

; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_OPERA
$gBaselines[0][1] = 1.26748555645132
$gBaselines[1][0] = $CLOSE_OPERA
$gBaselines[1][1] = 0.442227858358096
For $i = 2 to $gBaselineSize - 1
	$gBaselines[ $i ][0]	= "--unused--"
	$gBaselines[ $i ][1]	= 0.0
Next


;============================================================================================================================
;============================================================================================================================


Func isOperaAlreadyInstalled()
	If FileExists( $OPERA_EXECUTABLE ) Then
		return True
	EndIf	
	return False
EndFunc

Func InitializeOperaScript()
	Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
EndFunc

Func LaunchOpera()
	outputDebug( "Attempting to launch " & $OPERA_EXECUTABLE )
	
	TimerBegin()
	$gPID = Run( $OPERA_EXECUTABLE_TO_LAUNCH, "C:\", @SW_SHOWMAXIMIZED )
	opbmWaitUntilProcessIdle( $gPID, 5, 100, 5000 )
	WinActivate( $OPBM_SPLASH_HTML_TITLE )
	TimerEnd( $LAUNCH_OPERA )
	
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc

Func CloseOpera()
	; Start the timer
	TimerBegin()
	
	; Close Opera
	WinActivate( $OPERA_WINDOW )
	WinClose( $OPERA_WINDOW )	
	
	; Wait until the sytem settles down
	opbmWaitUntilSystemIdle( 10, 100, 10000 )
	
	; Take the ending timer
	TimerEnd( $CLOSE_OPERA )
EndFunc
