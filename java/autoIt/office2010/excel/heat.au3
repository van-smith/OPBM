#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=heat.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

#include <../../common/office2010/excelCommon.au3>

; Defined in excelCommon.au3, but overridden here for "heat"-specific launch and close entries
;$gBaselines[0][0] = $LAUNCH_MICROSOFT_EXCEL
$gBaselines[0][1] = $HEAT_LAUNCH_MICROSOFT_EXCEL
;$gBaselines[1][0] = $CLOSE_MICROSOFT_EXCEL
$gBaselines[1][1] = $HEAT_CLOSE_MICROSOFT_EXCEL

; Only defined in this AutoIt script
$gBaselines[2][0] = $HEAT_CLOSE_EMPTY_WORKSHEET
$gBaselines[2][1] = $HEAT_CLOSE_EMPTY_WORKSHEET_SCORE
$gBaselines[3][0] = $HEAT_OPEN_WORKSHEET
$gBaselines[3][1] = $HEAT_OPEN_WORKSHEET_SCORE
$gBaselines[4][0] = $HEAT_SAVE_AND_CLOSE_WORKSHEET
$gBaselines[4][1] = $HEAT_SAVE_AND_CLOSE_WORKSHEET_SCORE
$gBaselines[5][0] = $HEAT_TIME_TO_ITERATE_N_TIMES
$gBaselines[5][1] = $HEAT_TIME_TO_ITERATE_N_TIMES_SCORE

Dim $CurrentLoop
Dim $LoopLimit

if $CmdLine[0] > 0 then 
	$LoopLimit = $CmdLine[1] 
Else 
	$LoopLimit = 1
EndIf
For $CurrentLoop = 1 to $LoopLimit
	outputStatus( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	
	outputStatus( "InitializeExcelScript()" )
	InitializeExcelScript()
	
	FirstRunCheck()
	
	outputStatus( "LaunchExcel()" )
	LaunchExcel()
	
	outputStatus( "CloseEmptyWorksheet()" )
	CloseEmptyWorksheet()
	
	outputStatus( "OpenSurfaceChartWorksheet()" )
	OpenSurfaceChartWorksheet()
	
	outputStatus( "IterateCalculations()" )
	IterateCalculations()
	
	outputStatus( "CloseSurfaceChartWorksheet()" )
	CloseSurfaceChartWorksheet()
	
	outputStatus( "CloseExcel()" )
	CloseExcel()
	
	outputStatus( "FinalizeScript()" )
	opbmFinalizeScript( "office2010ExcelHeat.csv" )
	opbmFileDelete( $FILENAME_SPREADSHEET )
Next
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func InitializeExcelScript()
	Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
	
	$gErrorTrap = FileCopy($FILENAME_SPREADSHEET_BACKUP, $FILENAME_SPREADSHEET, 1)
	If $gErrorTrap = 0 Then ErrorHandle($ERROR_PREFIX & "FileCopy: " & $FILENAME_SPREADSHEET_BACKUP & ": Unable to copy file.")
EndFunc

Func LaunchExcel()
	Local $filename
	If FileExists($FILENAME_EXCEL_X64) Then
		$filename = $FILENAME_EXCEL_X64
	ElseIf FileExists( $FILENAME_EXCEL_I386 ) Then
		$filename = $FILENAME_EXCEL_I386
	Else
		ErrorHandle("Launch: Excel 2010 not found in " & $FILENAME_EXCEL_X64 & " or " & $FILENAME_EXCEL_I386 & ", unable to launch.")
	EndIf
	outputDebug( "Attempting to launch " & $filename)
	TimerBegin()
	$gPID = Run($filename, "C:\", @SW_MAXIMIZE)
	opbmWinWaitActivate("Microsoft Excel", "Book1", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Excel. Unable to find Window." )
	TimerEnd("Launch Microsoft Excel 2010")
EndFunc

Func CloseEmptyWorksheet()
	TimerBegin()
	Send("!fc")
	opbmWinWaitActivate("Microsoft Excel", "Status Bar", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Excel. Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd("Close Empty Worksheet")
EndFunc

Func OpenSurfaceChartWorksheet()
	local $i
	TimerBegin()
	Send("!fo")
	opbmWinWaitActivate("Open", "Open", $gTimeout, $ERROR_PREFIX & "WinWait: Open: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	ControlSend("Open", "Open", "Edit1", $FILENAME_SPREADSHEET, 1)
	Send("{ENTER}")
	
	; the following is necessary to properly regain focus
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send("{PGDN}")
	Send("{PGUP}")

	opbmWinWaitActivate($WINDOW_SURF_CHART, $WINDOW_SURF_CHART, $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_SURF_CHART & ": Unable to find Window." )
	; the following is necessary to properly regain focus
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send("{PGDN}")
	Send("{PGUP}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd("Open Worksheet")
EndFunc

Func IterateCalculations()
	Local $i
	TimerBegin()
	for $i = 1 to $NBR_OF_CALCULATIONS
		Send( "{F9}" )
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
		; Used to force a redraw after each iteration
		Send("^{HOME}")
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Next
	TimerEnd( "Time to iterate sheet " & $NBR_OF_CALCULATIONS & " times" )
EndFunc

Func CloseSurfaceChartWorksheet()
	opbmWinWaitActivate( $WINDOW_SURF_CHART, $WINDOW_SURF_CHART, $gTimeout, $ERROR_PREFIX & "WinWait:" & $WINDOW_SURF_CHART & ": Unable to find Window." )
	TimerBegin()
	Send("!fc")
	opbmWinWaitActivate("Microsoft Excel", "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Excel: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	;save changes to the spreadsheet:
	Send("!n")
	opbmWinWaitActivate("Microsoft Excel", "Status Bar", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Excel: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd("Save and Close Worksheet")
EndFunc

Func CloseExcel()
	TimerBegin()
	Send("!fx")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitClose("Microsoft Excel", "Status Bar", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Excel: Window did not close." )
	TimerEnd("Close Microsoft Excel 2010")
EndFunc
