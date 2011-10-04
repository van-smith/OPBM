Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <opbmCommon.au3>

Const $STARTUP_SETTLE_DOWN					= "Startup settle down"
Const $REBOOT_TIME							= "Time to reboot"


; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $STARTUP_SETTLE_DOWN
$gBaselines[0][1] = $STARTUP_SETTLE_DOWN_SCORE
; The reboot time is recorded as an internal function of the harness
; It is scored there also.  See the HUD's stream gobbler's commands.
; And specifically see BenchmarkParams' getRebootTimeScore() method.
;$gBaselines[1][0] = $REBOOT_TIME
;$gBaselines[1][1] = $REBOOT_TIME_SCORE
For $i = 1 to $gBaselineSize - 1
	$gBaselines[ $i ][0]	= "--unused--"
	$gBaselines[ $i ][1]	= 0.0
Next


Func InitializeInternalOpbmScript()
	$gScriptBeginTime = TimerInit()
EndFunc
