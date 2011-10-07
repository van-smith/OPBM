Dim $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $EXE_DIRECTORY							= @ScriptDir & "\exe"

; Executables used to install, uninstall, or execute Chrome
Const $JBM_EXECUTABLE							= chr(34) & $ROOT_DIR	&	"\common\opbm\exe\jbm.exe" & chr(34)


; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
For $i = 0 to $gBaselineSize - 1
	$gBaselines[ $i ][0]	= "--unused--"
	$gBaselines[ $i ][1]	= 0.0
Next


;============================================================================================================================
;============================================================================================================================

Func isJBMInstalled()
	If FileExists( $JBM_EXECUTABLE ) Then
		return True
	EndIf
	If FileExists( StringReplace( $JBM_EXECUTABLE, chr(34), "" ) ) Then
		return True
	EndIf
	return False
EndFunc

; This is not "Javascript" but is Java's script
Func InitializeJava_Script()
	;Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
EndFunc
