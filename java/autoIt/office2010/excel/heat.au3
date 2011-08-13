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
	
	outputStatus( "LaunchExcel()" )
	LaunchExcel()
	
	FirstRunCheck()
	
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
	outputStatus( "Removing temporary file" )
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

Func CloseEmptyWorksheet()
	TimerBegin()
	Send("!fc")
	opbmWinWaitActivate( $MICROSOFT_EXCEL, $STATUS_BAR, $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Excel. Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $HEAT_CLOSE_EMPTY_WORKSHEET )
EndFunc

Func OpenSurfaceChartWorksheet()
	local $i
	TimerBegin()
	Send("!fo")
	opbmWinWaitActivate( $OPEN, $OPEN, $gTimeout, $ERROR_PREFIX & "WinWait: Open: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	ControlSend( $OPEN, $OPEN, "Edit1", $FILENAME_SPREADSHEET, 1)
	Send("{ENTER}")
	
	; The following is necessary to properly regain focus
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send("{PGDN}")
	Send("{PGUP}")

	opbmWinWaitActivate($WINDOW_SURF_CHART, $WINDOW_SURF_CHART, $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_SURF_CHART & ": Unable to find Window." )
	; The following is necessary to properly regain focus
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send("{PGDN}")
	Send("{PGUP}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $HEAT_OPEN_WORKSHEET )
EndFunc

; These iterations might take a while on some graphics cards,
; so we wait up to 20 seconds for each operation to complete
Func IterateCalculations()
	Local $i
	Local $filename
	
	opbmWinWaitActivate($WINDOW_SURF_CHART, $WINDOW_SURF_CHART, $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_SURF_CHART & ": Unable to find Window." )
	TimerBegin()
	for $i = 1 to $NBR_OF_CALCULATIONS
		Send( "{F9}" )
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, 20000 )
		
		; Ctrl+Home is used to force a redraw after each iteration by moving to cell A1
		Send("^{HOME}")
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, 20000 )
		
		; Every 4th iteration, write a file
		If Mod($i, 4) = 1 Then
			; Create the filename for this iteration
			$filename = GetScriptTempDirectory() & "heatChartFrame_" & StringFormat("%03u", $i) & ".png"
			outputDebug("Exporting temporary chart image " & $filename)
			ClipPut( $filename )
			; Put the full path name from the Windows' clipboard into cell A1 (that cell is used by the excel macro)
			Send("^v")
			Sleep(100)
			; Execute the "export" macro, which writes to the file using the export function, as
			; well as appending a copied image to the spreadsheet
			Send("^+E")
			opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, 20000 )
		EndIf
		
		; Depending on where we are in the computation, change the graph type
		If $i = 5 Then
			; Switch to a different perspective, send macro command Ctrl+Shift+A
			Send("^+A")
			opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, 20000 )
		ElseIf $i = 10 Then
			; Switch to a different graph type, send macro command Ctrl+Shift+B
			; Requires "n" to answer "no" to popup dialog which says "This will display faster if you ... blah blah blah"
			Send("^+B")
			opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, 20000 )
		ElseIf $i = 15 Then
			; Switch to a different perspective, send macro command Ctrl+Shift+C
			Send("^+C")
			opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, 20000 )
		ElseIf $i = 20 Then
			; Switch to a different graph type, send macro command Ctrl+Shift+D
			Send("^+D")
			opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, 20000 )
		EndIf
	Next
	
	TimerEnd( $HEAT_TIME_TO_ITERATE_N_TIMES )
EndFunc

Func CloseSurfaceChartWorksheet()
	opbmWinWaitActivate( $WINDOW_SURF_CHART, $WINDOW_SURF_CHART, $gTimeout, $ERROR_PREFIX & "WinWait:" & $WINDOW_SURF_CHART & ": Unable to find Window." )
	TimerBegin()
	Send("!fc")
	opbmWinWaitActivate( $MICROSOFT_EXCEL, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Excel: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	;save changes to the spreadsheet:
	Send("!n")
	opbmWinWaitActivate( $MICROSOFT_EXCEL, $STATUS_BAR, $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Excel: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $HEAT_SAVE_AND_CLOSE_WORKSHEET )
EndFunc
