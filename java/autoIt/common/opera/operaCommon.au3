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

; Executables used to install, uninstall, or execute Opera		;Changed ro Opera from Firefox -rcp 11/11/11
Const $OPERA_INSTALLER					= $EXE_DIRECTORY		&	"\Opera_1152_int_Setup.exe"	;Change to Version 11.52 -rcp 11/11/11
;Const $OPERA_INSTALLER_WINDOW_NAME		= "Opera 11.52 - Installer"	;Eliminate Version references in other scripts -rcp 11/15/11
Const $OPERA_INSTALLER_WINDOW_NAME		= "Opera 1"				;Just in case we're uninstalling a different version -rcp 11/16/11
Const $OPERA_UNINSTALLER				= $OPERA_DIRECTORY		&	"\opera.exe"
Const $OPERA_EXECUTABLE					= $OPERA_DIRECTORY		&	"\opera.exe"
Const $OPERA_EXECUTABLE_TO_LAUNCH		= $OPERA_DIRECTORY		&	"\opera.exe " & $OPBM_SPLASH_HTML
Const $OPERA_UNINSTALL_COMMAND			= $OPERA_UNINSTALLER	&	" /uninstall"	;Change to / from -- -rcp 11/11/11

; Constants used throughout for various scripts
Const $LAUNCH_OPERA 					= "Launch Opera"	;Eliminate Version reference -rcp 11/11/11
Const $CLOSE_OPERA 						= "Close Opera"
Const $OPERA_WINDOW						= " - Opera"
Const $OPEN_FILE_DIALOG_TITLE			= "Open"

Const $TYPE_SUNSPIDER_URL 				= "Type SunSpider URL"
Const $TYPE_GOOGLEV8_URL				= "Type URL to Google V8 benchmark"
Const $TYPE_KRAKEN_URL					= "Type URL to Kraken benchmark"
Const $LAUNCH_OPERA_INSTALLER			= "Launch Opera Installer"	;Eliminate Version reference -rcp 11/11/11
Const $LAUNCH_OPERA_UNINSTALLER			= "Launch Opera Un-installer"	;Eliminate Version reference -rcp 11/11/11
Const $BYPASS_NEXT_BUTTON				= "Bypass Next Button"
Const $INSTALL_OPERA					= "Install Opera"	;Eliminate Version reference -rcp 11/11/11
Const $UNINSTALL_OPERA					= "Un-install Opera"	;Eliminate Version reference -rcp 11/11/11
Const $CLOSE_UNINSTALLER				= "Close Un-installer"

Const $RUN_SUNSPIDER 					= "Run SunSpider"
Const $RUN_GOOGLEV8						= "Run Google V8"
Const $RUN_KRAKEN						= "Run Kraken"

; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_OPERA
$gBaselines[0][1] = $LAUNCH_OPERA_SCORE
$gBaselines[1][0] = $CLOSE_OPERA
$gBaselines[1][1] = $CLOSE_OPERA_SCORE
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
	Local $i
	outputDebug( "Attempting to launch " & $OPERA_EXECUTABLE )

	If Not isOperaAlreadyInstalled() Then
		outputError("Opera is not installed.")
		Exit -1
	EndIf

	TimerBegin()
	$gPID = Run( $OPERA_EXECUTABLE_TO_LAUNCH, "C:\", @SW_SHOWMAXIMIZED )
	opbmWaitUntilProcessIdle( $gPID, 5, 100, 5000 )
	opbmWinWaitActivate( $OPBM_SPLASH_HTML_TITLE, "", 30 )
	TimerEnd( $LAUNCH_OPERA )

	; Wait for it to load completely
	opbmWaitUntilProcessIdle( $gPID, 10, 500, 5000 )

	; There's a featureNotAbug in the Opera install script right now that leaves diretory contents from
	; the prior install in place.  This has been observed on some systems to cause Opera to re-open
	; several previous tabs as each test in Opera is run, causing multiple windows to share the cpu and
	; greatly affect timing.  To combat this temporarily, we send several Ctrl+F4 keystrokes to close
	; the current tab, back to the main speed dial tab.
	; Wait two seconds for Opera to settle down (it was observed during testing that if Opera hasn't
	; been loaded in a long time, it will take extra long the first time)
	Sleep(2000)
	For $i = 1 to 10
		; Ctrl+F4 closes the current tab
		Send("^{f4}")
		; Wait for a tick before the next one
		Sleep(100)
	Next
	; Wait a few seconds for Opera to settle down (for the same reasons as Sleep(2000) above)
	Sleep(2000)
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
