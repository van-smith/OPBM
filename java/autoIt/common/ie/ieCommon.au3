Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $DATA_DIRECTORY					= @ScriptDir & "\data"
Const $EXE_DIRECTORY					= @ScriptDir & "\exe"

; Relative paths to reach startup/main page for benchmarks
Const $SUNSPIDER_URL					= $DATA_DIRECTORY		&	"\driver.html"
Const $KRAKEN_URL						= $DATA_DIRECTORY		&	"\driver.html"
Const $GOOGLEV8_URL						= $DATA_DIRECTORY		&	"\run.html"

; Contains code relative to using IE9, which other scripts use as well (see alice.au3 for an example)
#include <ieCommonIE9Functions.au3>

; Constants used throughout for various scripts
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
