Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $DATA_DIRECTORY					= @ScriptDir & "\data"
Const $EXE_DIRECTORY					= @ScriptDir & "\exe"

; Default physical installation directory from Internet Explorer
Const $IE_DIRECTORY						= "C:\Program Files (x86)\Internet Explorer"

; Relative paths to reach startup/main page for benchmarks
Const $SUNSPIDER_URL					= $DATA_DIRECTORY		&	"\driver.html"
Const $KRAKEN_URL						= $DATA_DIRECTORY		&	"\driver.html"
Const $GOOGLEV8_URL						= $DATA_DIRECTORY		&	"\run.html"

; Executables used to install, uninstall, or execute Firefox
Const $IE_EXECUTABLE					= $IE_DIRECTORY			&	"\iexplore.exe"
Const $IE_EXECUTABLE_TO_LAUNCH			= $IE_DIRECTORY			&	"\iexplore.exe " & $OPBM_SPLASH_HTML

; Constants used throughout for various scripts
Const $LAUNCH_IE 						= "Launch Internet Explorer"
Const $CLOSE_IE 						= "Close Internet Explorer"
Const $IE_WINDOW						= " - Internet Explorer"
Const $OPEN_FILE_DIALOG_TITLE			= "Open"

Const $TYPE_SUNSPIDER_URL 				= "Type SunSpider URL"
Const $TYPE_GOOGLEV8_URL				= "Type URL to Google V8 benchmark"
Const $TYPE_KRAKEN_URL					= "Type URL to Kraken benchmark"

Const $RUN_SUNSPIDER 					= "Run SunSpider"
Const $RUN_GOOGLEV8						= "Run Google V8"
Const $RUN_KRAKEN						= "Run Kraken"

; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_IE
$gBaselines[0][1] = $LAUNCH_IE_SCORE
$gBaselines[1][0] = $CLOSE_IE
$gBaselines[1][1] = $CLOSE_IE_SCORE
For $i = 2 to $gBaselineSize - 1
	$gBaselines[ $i ][0]	= "--unused--"
	$gBaselines[ $i ][1]	= 0.0
Next


;============================================================================================================================
;============================================================================================================================


Func InitializeIEScript()
	Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
EndFunc

Func VerifyIE9Installed()
	If not isIE9Installed() Then
		outputError( "Requires IE Version 9.0" )
		opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
		Exit -1
	EndIf
EndFunc

Func isIE9Installed()
	Local $result
	If FileExists( $IE_EXECUTABLE ) Then
		; Internet Explorer exists, but is it version 9?
		; Returns 1 if exact match, 2 if case-insensitive match, 0 if error
		If CheckIfRegistryKeyStartsWith( "HKLM\Software\Microsoft\Internet Explorer\Version", "9." ) >= 1 Then
			; Set our default values for IE9
			$result = InternetExplorerInstallerAssist()
			If StringInStr( $result, "0" ) Then
				; There was at least an error, the presence of every 0 indicates an error, 1 indicates success:
				outputDebug( "Unable to set some IE Registry Keys: " & $result )
			EndIf
			return True
		EndIf
	EndIf	
	; Internet Explorer does not exist... weird
	return False
EndFunc

Func LaunchIE()
	outputDebug( "Attempting to launch " & $IE_EXECUTABLE )
	; Make sure we're not running IE8, IE7 or (gasp!) IE6
	VerifyIE9Installed()
	
	TimerBegin()
	$gPID = Run( $IE_EXECUTABLE_TO_LAUNCH, "C:\", @SW_SHOWMAXIMIZED )
	opbmWaitUntilProcessIdle( $gPID, 5, 100, 5000 )
	opbmWinWaitActivate( $OPBM_SPLASH_HTML_TITLE )
	TimerEnd( $LAUNCH_IE )
	
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc

Func CloseIE()
	; Start the timer
	TimerBegin()
	
	; Close Opera
	WinActivate( $IE_WINDOW )
	WinClose( $IE_WINDOW )	
	
	; Wait until the sytem settles down
	opbmWaitUntilSystemIdle( 10, 100, 10000 )
	
	; Take the ending timer
	TimerEnd( $CLOSE_IE )
EndFunc
