#region ;
#AutoIt3Wrapper_OutFile=ExcelTest8.exe
#endregion

;====================================================================================
;author: R. Pritchett
;filename: ExcelTest8.au3
;date: 10/25/2011
;description: A revised version of ExcelTest1
;	This program opens a new Excel Worksheet, enters some data,
;	 runs some formulas, creates a couple of charts, and closes.
;====================================================================================
#include <Excel.au3>
#include <file.au3>
#include <..\..\common\office2010\excelCommon.au3>

;Setup Constants in case they don't exist elsewhere
Const $EXCEL_GENERAL_SINCOSTAN = "Run SINCOSTAN test"
Const $EXCEL_GENERAL_SINCOSTAN_SCORE = 29.20
Const $EXCEL_GENERAL_POWERBALL = "Run POWERBALL test"
Const $EXCEL_GENERAL_POWERBALL_SCORE = 41.62
Const $EXCEL_GENERAL_FIBONACCI = "Run FIBONACCI test"
Const $EXCEL_GENERAL_FIBONACCI_SCORE = 82.2
Const $EXCEL_GENERAL_RANKPERCENTILE = "Run RANKPERCENTILE test"
Const $EXCEL_GENERAL_RANKPERCENTILE_SCORE = 63.2
Const $EXCEL_GENERAL_CENSUS = "Run CENSUS test"
Const $EXCEL_GENERAL_CENSUS_SCORE = 3.75

;Setup Baseline scoring
$gBaseLines[0][0] = $EXCEL_GENERAL_SINCOSTAN
$gBaseLines[0][1] = $EXCEL_GENERAL_SINCOSTAN_SCORE
$gBaseLines[1][0] = $EXCEL_GENERAL_POWERBALL
$gBaseLines[1][1] = $EXCEL_GENERAL_POWERBALL_SCORE
$gBaseLines[2][0] = $EXCEL_GENERAL_FIBONACCI
$gBaseLines[2][1] = $EXCEL_GENERAL_FIBONACCI_SCORE
$gBaseLines[3][0] = $EXCEL_GENERAL_RANKPERCENTILE
$gBaseLines[3][1] = $EXCEL_GENERAL_RANKPERCENTILE_SCORE
$gBaseLines[4][0] = $EXCEL_GENERAL_CENSUS
$gBaseLines[4][1] = $EXCEL_GENERAL_CENSUS_SCORE
;
;et. al.
;

;Setup loop
Dim $CurrentLoop
Dim $LoopLimit
Global $ExcelTest1

If $CmdLine[0] > 0 Then
	$LoopLimit = $CmdLine[1]
Else
	$LoopLimit = 1
EndIf

For $CurrentLoop = 1 to $LoopLimit

	outputStatus("InitializeExcelScript()")
	InitializeExcelScript()

	outputStatus("SINCOSTAN()")
	SINCOSTAN()

	outputStatus("PowerBall()")
	PowerBall()

	outputStatus("Fibonacci()")
	Fibonacci()

	outputStatus("RankPercentile()")
	RankPercentile()

	outputStatus("Census()")
	Census()

	opbmWinWaitActivate( $MICROSOFT_EXCEL, "", $gTimeout, $ERROR_PREFIX & "WinWait: Open: Unable to find Window." )
	Send( "!fx" )
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	Send( "n" )


Next

Exit
; That's all folks

;====================================================================================
;=================================== TESTS ==========================================
;====================================================================================

; TEST 1 : SIN/COS/TAN load/calc/plot
Func SINCOSTAN()
	Local $index
	Local $xlXYScatter

	TimerBegin()

	$ExcelTest1 = _ExcelBookNew() ;Create new book, make it visible
	$ExcelTest1.activeworkbook.saved = 1	; To prevent 'yes/no' questions from Excel
	;Ensure all commands will be recognized
	$ExcelTest1.AddIns("Analysis ToolPak").Installed = False
	$ExcelTest1.AddIns("Analysis ToolPak").Installed = True
	;Add a new sheet to work on
	_ExcelSheetAddNew($ExcelTest1, "SINCOSTAN") ; Add new sheet
	_ExcelSheetActivate($ExcelTest1, "SINCOSTAN") ; Make sure it's the active sheet
	;Enter column headers
	_ExcelWriteCell($ExcelTest1, "Degrees", 1, 1) ;Enter a column header
	_ExcelWriteCell($ExcelTest1, "Radians", 1, 2) ;Enter a column header
	_ExcelWriteCell($ExcelTest1, "Sine", 1, 3) ;Enter a column header
	_ExcelWriteCell($ExcelTest1, "Cosine", 1, 4) ;Enter a column header
	_ExcelWriteCell($ExcelTest1, "Tangent", 1, 5) ;Enter a column header
	;Enter baseline formulas
	_ExcelWriteCell($ExcelTest1, "=RANDBETWEEN(-360,360)", 2, 1) ;Enter a column of degrees
	_ExcelWriteCell($ExcelTest1, "=A2+.1", 3, 1) ;Enter a column of degrees formula for copy/paste
	_ExcelWriteCell($ExcelTest1, "= Radians(A2)", 2, 2) ;Convert the 1st column to radians
	_ExcelWriteCell($ExcelTest1, "= SIN(B2)", 2, 3) ;Find the sine of the 2nd column
	_ExcelWriteCell($ExcelTest1, "= COS(B2)", 2, 4) ;Find the cosine of the 2nd column
	_ExcelWriteCell($ExcelTest1, "= TAN(B2)", 2, 5) ;Find the tangent of the 2nd column
	;Copy down degrees column
	$ExcelTest1.Range("A3:A3601").Select
	$ExcelTest1.Selection.FillDown
	;Copy down radians/sin/cos/tan columns
	$ExcelTest1.Range("B2:E3601").Select
	$ExcelTest1.Selection.FillDown
	;Select columns to chart
	$ExcelTest1.Range("C1:E3601").Select
	;Add Chart
	Send("!{F1}")
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	$xlXYScatter = -4169 ;XYScatter Chart type
	With $ExcelTest1
		.ActiveChart.ChartType = $xlXYScatter
		.ActiveChart.Axes($xlValue).MinimumScale = -2
		.ActiveChart.Axes($xlValue).MaximumScale = 2
		.ActiveChart.Axes($xlCategory).MinimumScale = 0
		.ActiveChart.Axes($xlCategory).MaximumScale = 3600
	EndWith

	;Recalculate 50 times
	for $index = 1 to 50
		$ExcelTest1.Calculate
		opbmWaitUntilSystemIdle( 5, 100, 5000 )
	Next

	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	TimerEnd( $EXCEL_GENERAL_SINCOSTAN )
	Sleep( 2000 )
EndFunc
;End of TEST 1

;TEST 2: Simulate 10000 PowerBall Draws
Func PowerBall()
	Local $index
	Local $index1
	Local $draws

	TimerBegin()
	;
	_ExcelSheetAddNew($ExcelTest1, "PowerBall") ; Add new sheet
	_ExcelSheetActivate($ExcelTest1, "PowerBall") ; Make sure it's the active sheet
	;Layout Table
	$draws = 10000
	;Enter column Headers
	For $index = 1 To 5
		_ExcelWriteCell($ExcelTest1, "Ball " & $index, 1, $index) ;Enter a column header
	Next
	_ExcelWriteCell($ExcelTest1, "PowerBall", 1, 6) ;Enter a column header
	_ExcelWriteCell($ExcelTest1, "Ball No.", 1, 7) ;Enter a column header
	_ExcelWriteCell($ExcelTest1, "Ball Count", 1, 8) ;Enter a column header
	_ExcelWriteCell($ExcelTest1, "PwrBall No.", 1, 9) ;Enter a column header
	_ExcelWriteCell($ExcelTest1, "PB Count", 1, 10) ;Enter a column header
	_ExcelWriteCell($ExcelTest1, "Draw Count", 1, 11) ;Enter a column header
	;Enter Ball list
	_ExcelWriteCell($ExcelTest1, 1, 2, 7) ;Enter Ball list
	_ExcelWriteCell($ExcelTest1, "=g2+1", 3, 7) ;Enter Ball list formula for copy/paste
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	$ExcelTest1.Range("G3:G60").Select
	$ExcelTest1.Selection.FillDown	;Copy down
	;Enter PwrBall list
	_ExcelWriteCell($ExcelTest1, 1, 2, 9) ;Enter PwrBall list
	_ExcelWriteCell($ExcelTest1, "=g2+1", 3, 9) ;Enter PwrBall list formula for copy/paste
	$ExcelTest1.Range("I3:I40").Select
	$ExcelTest1.Selection.FillDown	;Copy down
	;Enter Count Balls
	_ExcelWriteCell($ExcelTest1, "=countif($a$2:$e$"&$draws+1&",g2)", 2, 8) ;Enter Count Balls formula
	$ExcelTest1.Range("H2:H60").Select
	$ExcelTest1.Selection.FillDown	;Copy down
	;Enter Count PowerBalls
	_ExcelWriteCell($ExcelTest1, "=countif($f$2:$f$"&$draws+1&",i2)", 2, 10) ;Enter Count PowerBalls formula
	$ExcelTest1.Range("J2:J40").Select
	$ExcelTest1.Selection.FillDown	;Copy down
	;Enter Draw Count
	_ExcelWriteCell($ExcelTest1, "=sum(j2:j40)", 2, 11);Enter Draw Count fornula
	;Load Balls
	For $index1 = 1 To 5
		_ExcelWriteFormula($ExcelTest1, "=1+MOD(INT(RAND()*5900),59)", 2, $index1) ;Input random numbers
	Next
	_ExcelWriteFormula($ExcelTest1, "=1+MOD(INT(RAND()*3900),39)", 2, 6) ;Input random numbers
	$ExcelTest1.Range("A2:F"&$draws+1).Select
	$ExcelTest1.Selection.FillDown	;Copy down
	;Add Chart
	$ExcelTest1.Range("H1:H60,J1:J40").Select
	Send("!{F1}")
	;Recalculate 100 times
	for $index = 1 to 100
		$ExcelTest1.Calculate
		opbmWaitUntilSystemIdle( 5, 100, 5000 )
	Next

	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	TimerEnd( $EXCEL_GENERAL_POWERBALL )
	Sleep( 2000 )
EndFunc
;End of TEST 2

; TEST 3: Prove Fibonacci (Golden Ratio) series
Func Fibonacci()
	Local $index
	Local $index1
	;TimerBegin()
	;
	_ExcelSheetAddNew($ExcelTest1, "Fibonacci") ; Add new sheet
	_ExcelSheetActivate($ExcelTest1, "Fibonacci") ; Make sure it's the active sheet
	;Enter Fibonacci
	_ExcelWriteCell($ExcelTest1, "Fibonacci", 1, 1) ;Enter a column header
	_ExcelWriteCell($ExcelTest1, "= rand()/10000",2 ,1) ;Enter the first seed number
	_ExcelWriteCell($ExcelTest1, "= rand()/10000",3 ,1) ;Enter the second seed number
	_ExcelWriteCell($ExcelTest1, "= A3+A2",4 ,1) ;Enter the Fibonacci formula
	$ExcelTest1.Range("A4:A1003").Select
	$ExcelTest1.Selection.FillDown	;Copy down
	;Enter Golden Ratio
	_ExcelWriteCell($ExcelTest1, "Golden Ratio", 1, 2) ;Enter a column header
	_ExcelWriteCell($ExcelTest1, "= A3/A2",3,2) ;Enter the Golden Ratio formula
	$ExcelTest1.Range("B3:B1003").Select
	$ExcelTest1.Selection.FillDown	;Copy down
	;Add Chart
	$ExcelTest1.Range("B1:B1003").Select
	Send("!{F1}")
	Local $xlLineMarkers = 65 ;XYScatter Chart type
	With $ExcelTest1
		.ActiveChart.ChartType = $xlLineMarkers
		.ActiveChart.Axes($xlValue).MinimumScale = 1.61
		.ActiveChart.Axes($xlValue).MaximumScale = 1.63
	EndWith
	;Recalculate 100 times
	for $index = 1 to 100
		$ExcelTest1.Calculate
		opbmWaitUntilSystemIdle( 5, 100, 5000 )
	Next

	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	TimerEnd( $EXCEL_GENERAL_FIBONACCI )
	Sleep( 2000 )
EndFunc
;End of TEST 3

;TEST 4: Load an external file (107KB) then do Rank & Percentile Analysis
Func RankPercentile()
	Local $index
	Local $index1
	TimerBegin()
	;
	_ExcelSheetAddNew($ExcelTest1, "GDP") ; Add new sheet
	_ExcelSheetActivate($ExcelTest1, "GDP") ; Make sure it's the active sheet
	Local $gdpFilePath1 = @ScriptDir & "\data\WorldBankGDP3.csv"
	Local $GDPFile = _ExcelBookOpen($gdpFilePath1)
	$GDPFile.Cells.Select
	$GDPFile.Selection.Copy
	_ExcelSheetActivate($ExcelTest1, "GDP")
	$ExcelTest1.Range("A1").Select
	$ExcelTest1.ActiveSheet.Paste
	$ExcelTest1.Columns("A:D").EntireColumn.AutoFit
	$ExcelTest1.Rows("1:1").Select
	$ExcelTest1.Selection.Font.Bold = True
	$ExcelTest1.Selection.HorizontalAlignment = $xlCenter
	$ExcelTest1.Range("A1").Select
	opbmWinWaitActivate( "WorldBankGDP3", "WorldBankGDP3", $gTimeout, $ERROR_PREFIX & "WinWait: Open: Unable to find Window." )
	Send( "!fx" )
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	Send( "n" )
	opbmWaitUntilSystemIdle( 5, 100, 5000 )

	_ExcelSheetAddNew($ExcelTest1, "GPD Rank") ; Add new sheet
	_ExcelSheetActivate($ExcelTest1, "GPD Rank") ; Make sure it's the active sheet
	;Setup Headers
	_ExcelWriteCell($ExcelTest1, "=VLOOKUP(GDP!$A1,GDP!$A$1:$BC$217,2)",1,1)

	local $test2IndexTemp = 3
	For $index = 2 to 153 Step 3
		_ExcelWriteCell($ExcelTest1, "=VLOOKUP(GDP!$A1,GDP!$A$1:$BC$217,"&$test2IndexTemp&")",1,$index)
		_ExcelWriteCell($ExcelTest1, "Rank", 1, $index+1)
		_ExcelWriteCell($ExcelTest1, "Percentile", 1, $index+2)
		$test2IndexTemp += 1
	Next
	$ExcelTest1.Rows("1:1").Select
	$ExcelTest1.Selection.Font.Bold = True
	$ExcelTest1.Range("A1").Select
	;Insert Country names
	$ExcelTest1.Range("A1:A217").Select
	$ExcelTest1.Selection.FillDown
	;Insert GDP, Rank, & Percentile formulas
	_ExcelWriteCell($ExcelTest1, "=HLOOKUP(B$1,GDP!$B$1:$BC$217,GDP!$A2+1",2,2)
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	_ExcelWriteCell($ExcelTest1, "=RANK(HLOOKUP(B$1,GDP!$B$1:$BC$217,GDP!$A2+1),B$2:B$217)", 2, 3)
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	_ExcelWriteCell($ExcelTest1, "=PERCENTRANK(B$2:B$217,HLOOKUP(B$1,GDP!$B$1:$BC$217,GDP!$A2+1))", 2, 4)
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	; Format Percent column
	$ExcelTest1.Range("D2").Select
	$ExcelTest1.Selection.NumberFormat = "0.0%"
	; Fill in the rest of the sheet
	$ExcelTest1.Range("B2:D2").Select
	$ExcelTest1.Selection.Copy
	$ExcelTest1.Range("B2:EX217").Select
	$ExcelTest1.ActiveSheet.Paste
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	; Pretty it up
	$ExcelTest1.Range("A1").Select
	$ExcelTest1.Columns("A:EX").EntireColumn.AutoFit
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	;Recalculate 100 times
	for $index = 1 to 100
		$ExcelTest1.Calculate
		opbmWaitUntilSystemIdle( 5, 100, 5000 )
	Next

	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	TimerEnd($EXCEL_GENERAL_RANKPERCENTILE)
	Sleep( 2000 )
EndFunc
;End of TEST 4

;TEST 5: Load an external file (720KB)
Func Census()
	TimerBegin()

	_ExcelSheetAddNew($ExcelTest1, "Census") ; Add new sheet
	_ExcelSheetActivate($ExcelTest1, "Census") ; Make sure it's the active sheet
	Local $censusFilePath1 = @ScriptDir & "\data\2000_Census.csv"
	Local $censusFile = _ExcelBookOpen($censusFilePath1)
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	$censusFile.Cells.Select
	$censusFile.Selection.Copy
	_ExcelSheetActivate($ExcelTest1, "Census")
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	$ExcelTest1.Range("A1").Select
	$ExcelTest1.ActiveSheet.Paste
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	$ExcelTest1.Columns("A:A").EntireColumn.AutoFit
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	$ExcelTest1.Rows("1:2").Select
	$ExcelTest1.Selection.Font.Bold = True

	opbmWinWaitActivate( "2000_Census", "2000_Census", $gTimeout, $ERROR_PREFIX & "WinWait: Open: Unable to find Window." )
	Send( "!fx" )
	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	Send( "n" )

	opbmWaitUntilSystemIdle( 5, 100, 5000 )
	TimerEnd( $EXCEL_GENERAL_CENSUS )
	Sleep( 2000 )
EndFunc
;End of TEST 5