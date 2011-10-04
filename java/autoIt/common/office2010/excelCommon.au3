#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 6.05.2011

	Description: AutoIT file containing common Microsoft Excel 2010 data and functions

	Usage:	excelCommon is not directly exceutable

#ce ======================================================================================================================================
Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>
#include <office2010Common.au3>

Const $DIRECTORY_SPREADSHEET			= @ScriptDir & "\Data"
Const $DIRECTORY_EXCEL_I386				= "C:\Program Files (x86)\Microsoft Office\OFFICE14"
Const $DIRECTORY_EXCEL_X64				= "C:\Program Files\Microsoft Office\OFFICE14"
;Const $FILENAME_SPREADSHEET			= $DIRECTORY_SPREADSHEET & "\surfaceChart.xlsx"
;Const $FILENAME_SPREADSHEET_BACKUP		= $DIRECTORY_SPREADSHEET & "\surfaceChart.backup"
Const $FILENAME_SPREADSHEET				= $DIRECTORY_SPREADSHEET & "\surfaceChartWithMacros.xlsm"
Const $FILENAME_SPREADSHEET_BACKUP		= $DIRECTORY_SPREADSHEET & "\surfaceChartWithMacros.backup"
Const $FILENAME_EXCEL_I386				= $DIRECTORY_EXCEL_I386 & "\Excel.exe"
Const $FILENAME_EXCEL_X64				= $DIRECTORY_EXCEL_X64 & "\Excel.exe"
Const $WINDOW_SURF_CHART				= "surfaceChart"
Const $NBR_OF_CALCULATIONS				= 25
Const $LAUNCH_MICROSOFT_EXCEL			= "Launch Microsoft Excel 2010"
Const $CLOSE_MICROSOFT_EXCEL			= "Close Microsoft Excel 2010"

; heat.au3
Const $HEAT_CLOSE_EMPTY_WORKSHEET		= "Close Empty Worksheet" 
Const $HEAT_OPEN_WORKSHEET				= "Open Worksheet"
Const $HEAT_SAVE_AND_CLOSE_WORKSHEET	= "Save and Close Worksheet"
Const $HEAT_TIME_TO_ITERATE_N_TIMES		= "Time to iterate sheet " & $NBR_OF_CALCULATIONS & " times"
Const $MICROSOFT_EXCEL					= "Microsoft Excel"
Const $OPEN								= "Open"
Const $BOOK1							= "Book1"
Const $STATUS_BAR						= "Status Bar"


; Setup references for timing items
Dim $gBaselineSize
$gBaselineSize = 20
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_MICROSOFT_EXCEL
$gBaselines[0][1] = $LAUNCH_MICROSOFT_EXCEL_SCORE
$gBaselines[1][0] = $CLOSE_MICROSOFT_EXCEL
$gBaselines[1][1] = $CLOSE_MICROSOFT_EXCEL_SCORE


; Functions used by Excel
Func InitializeExcelScript()
	Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
EndFunc

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

Func isExcelInstalled()
	Local $filename
	
	If FileExists($FILENAME_EXCEL_X64) Then
		$filename = $FILENAME_EXCEL_X64
		outputDebug( "Running 64-bit Office" )
	ElseIf FileExists( $FILENAME_EXCEL_I386 ) Then
		$filename = $FILENAME_EXCEL_I386
		outputDebug( "Running 32-bit Office" )
	Else
		$filename = "not found"
	EndIf
	
	return $filename
EndFunc

Func LaunchExcel()
	Local $filename
	
	; Find out which version we're running
	$filename = isExcelInstalled()
	If $filename = "not found" Then
		ErrorHandle("Launch: Excel 2010 not found in " & $FILENAME_EXCEL_X64 & " or " & $FILENAME_EXCEL_I386 & ", unable to launch.")
	EndIf
	
	; Opbm sets some registry keys at startup
	outputDebug( $SAVING_AND_SETTING_OFFICE_2010_REGISTRY_KEYS )
	Office2010SaveRegistryKeys()
	Office2010InstallRegistryKeys()

	; Attempt to launch the application
	outputDebug( "Attempting to launch " & $filename)
	TimerBegin()
	$gPID = Run($filename, "C:\", @SW_MAXIMIZE)
	opbmWinWaitActivate( $MICROSOFT_EXCEL, $BOOK1, $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Excel. Unable to find Window." )
	TimerEnd( $LAUNCH_MICROSOFT_EXCEL )
EndFunc

Func CloseExcel()
	TimerBegin()
	Send("!fx")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitClose( $MICROSOFT_EXCEL, $STATUS_BAR, $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Excel: Window did not close." )
	TimerEnd( $CLOSE_MICROSOFT_EXCEL )
	
	outputDebug( $RESTORING_OFFICE_2010_REGISTRY_KEYS )
	Office2010RestoreRegistryKeys()
EndFunc
