Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

Const $DIRECTORY_SPREADSHEET		= @ScriptDir & "\Data"
Const $DIRECTORY_EXCEL_I386			= "C:\Program Files (x86)\Microsoft Office\OFFICE14"
Const $DIRECTORY_EXCEL_X64			= "C:\Program Files\Microsoft Office\OFFICE14"
Const $FILENAME_SPREADSHEET			= $DIRECTORY_SPREADSHEET & "\surfaceChart.xlsx"
Const $FILENAME_SPREADSHEET_BACKUP	= $DIRECTORY_SPREADSHEET & "\surfaceChart.backup"
Const $FILENAME_EXCEL_I386			= $DIRECTORY_EXCEL_I386 & "\Excel.exe"
Const $FILENAME_EXCEL_X64			= $DIRECTORY_EXCEL_X64 & "\Excel.exe"
Const $WINDOW_SURF_CHART			= "surfaceChart"
Const $NBR_OF_CALCULATIONS			= 25

Func ExcelGotoSheetAndCell($sheet, $cell)
	opbmWinActivate($WINDOW_SURF_CHART, $WINDOW_SURF_CHART, $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_SURF_CHART & ": Unable to find Window." )
	Send("^g")
	opbmWinWaitActivate("Go To", "", $gTimeout, $ERROR_PREFIX & "WinWait: Go To: Unable to find Window." )
	ControlSend("Go To", "", "[CLASSNN:EDTBX1]", Chr(39) & $sheet & Chr(39) & Chr(33) & $cell, 1)
	Send("{ENTER}")
	opbmWinActivate($WINDOW_SURF_CHART, $WINDOW_SURF_CHART, $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_SURF_CHART & ": Unable to find Window." )
EndFunc

Func ExcelGotoCell($cell)
	opbmWinActivate($WINDOW_SURF_CHART, $WINDOW_SURF_CHART, $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_SURF_CHART & ": Unable to find Window." )
	Send("^g")
	opbmWinWaitActivate("Go To", "", $gTimeout, $ERROR_PREFIX & "WinWait: Go To: Unable to find Window." )
	ControlSend("Go To", "", "[CLASSNN:EDTBX1]", $cell, 1)
	Send("{ENTER}")
	opbmWinActivate($WINDOW_SURF_CHART, $WINDOW_SURF_CHART, $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_SURF_CHART & ": Unable to find Window." )
EndFunc
