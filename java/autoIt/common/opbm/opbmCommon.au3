#include <GUIConstants.au3>
#Include <Date.au3>
AutoItSetOption("MustDeclareVars", 1) 
Opt("SendKeyDelay", 25) 
;AdlibEnable("ErrorDlg", 500)

#include <..\scoring\baselineScores.au3>


Const $ERROR_PREFIX					= @ScriptName & ":" & @ScriptLineNumber & ": "
Const $OPBM_DLL						= $ROOT_DIR & "\common\opbm\dll\opbm.dll"
Const $OPBM_SPLASH_HTML				= $ROOT_DIR & "\common\opbm\html\opbm_splash.html"
Const $CPU_ACTIVITY_THRESHOLD		= 5
Const $TIMER_MAX_INDEX_COUNT		= 100
Const $OPBM_SPLASH_HTML_TITLE		= "OPBM Benchmark Splash"

Global $gIterations
Global $gPID
Global $gTimerPoint
Global $gTimer
Global $gTimerStart
Global $gIndex						= 0
Global $gTimeIndex[ $TIMER_MAX_INDEX_COUNT ]
Global $gScriptBeginTime
Global $gCSVPath
Global $gTimeout					= 30
Global $gLongTimeout				= 300
Global $gMessage
Global $gErrorTrap					= -9999
Global $gOpbmPluginHandle

; Used for WaitUntilIdle()
Global $gPercent					= 10
Global $gDurationMS					= 100
Global $gTimeoutMS					= 1000

Func InitializeGlobalVariables()
	Local $i
	
	$gIterations					= ""
	$gPID							= ""
	$gTimerPoint					= ""
	$gTimer							= ""
	$gIndex							= 0
	$gScriptBeginTime				= ""
	$gCSVPath						= ""
	$gTimeOut						= 30
	$gLongTimeOut					= 300
	$gMessage						= ""
	$gErrorTrap						= -9999
	
	; Initialize the time indices
	For $i = 0 to $TIMER_MAX_INDEX_COUNT - 1
		$gTimeIndex[$i] = ""
	Next
	
	; Load the opbm.dll plugin, which allows these functions as "native" AutoIt functions:
	;		WaitUntilIdle($aPID, $aCpuUsageThreshold, $aPollPeriodMS, $aTimeoutMS )
	;		WaitUntilSystemIdle( $aCpuUsageThreshold, $aPollPeriodMS, $aTimeoutMS )
	;		GetUsage($aPID, $aPollPeriodMS )
	;		GetSystemUsage( $aPollPeriodMS )
	;		NoteAllOpenWindows()
	;		CloseAllWindowsNotPreviouslyNoted()
	$gOpbmPluginHandle = PluginOpen( $OPBM_DLL )
	If $gOpbmPluginHandle <> 0 Then 
		errorHandle( $OPBM_DLL & " did not open" )
	EndIf
	outputDebug( "Plugin " & $OPBM_DLL & " opened properly" )
	
	; Note all open windows at the present time (can be used to restore the system to its previous state)
	; from opbm.dll
	NoteAllOpenWindows()
EndFunc

Func opbmNoteAllOpenWindows()
	; From opbm.dll
	NoteAllOpenWindows()
EndFunc

Func opbmCloseAllWindowsNotPreviouslyNoted()
	; From opbm.dll
	CloseAllWindowsNotPreviouslyNoted()
EndFunc

Func opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
	; Wait for any lingering windows to appear
	Sleep(2000)
	WaitUntilSystemIdle( 10, 1000, 20000 )
	opbmCloseAllWindowsNotPreviouslyNoted()
	
	; Repeat (in case we were on a dialog that needed closed)
	Sleep(2000)
	WaitUntilSystemIdle( 10, 1000, 20000 )
	opbmCloseAllWindowsNotPreviouslyNoted()
EndFunc

Func Terminate()
	Local $lProcessWait
	
	$lProcessWait = ProcessWaitClose($gPID, $gTimeOut)
	If $lProcessWait = 0 Then
		ProcessClose($gPID)
	EndIf
	
	PluginClose( $gOpbmPluginHandle )
	Exit
EndFunc

Func opbmWaitUntilProcessIdle( $aPID, $aCpuUsageThreshold, $aPollPeriodMS, $aTimeoutMS )
	Local $result
	$result = WaitUntilIdle( $aPID, $aCpuUsageThreshold, $aPollPeriodMS, $aTimeoutMS )
;	ConsoleWrite( "debug,Process CPU utilization:" & $result & ", threshold:" & $aCpuUsageThreshold & ", duration:" & $aPollPeriodMS & ", timeout:" & $aTimeoutMS & @CRLF )
	return $result
EndFunc

Func opbmWaitUntilSystemIdle( $aCpuUsageThreshold, $aPollPeriodMS, $aTimeoutMS )
	Local $result
	$result = WaitUntilSystemIdle( $aCpuUsageThreshold, $aPollPeriodMS, $aTimeoutMS )
;	ConsoleWrite( "debug,System CPU utilization:" & $result & ", threshold:" & $aCpuUsageThreshold & ", duration:" & $aPollPeriodMS & ", timeout:" & $aTimeoutMS & @CRLF )
	return $result
EndFunc

;Func TakeTimer($Label)
;	Local $Ratio
;	$gTimerPoint = TimerDiff($gTimer)	
;
;	; Begin
;	; Added for OPBM July, 2011
;	; Compute the ratio in addition to the time, based on the baseline values encoded in the script
;	; Each script should have code like this at the startup before any TakeTimer() functions are called:
;	;	Dim $gBaselines[1][2]
;	;	Dim $gBaselineSize
;	;	$gBaselines[0][0] = "Description"		; The TakeTimer() $Label description goes here"
;	;	$gBaselines[0][1] = 2.0					; The timing value goes here
;	;	$gBaselineSize = 1						; Number of items in gBaselines[] goes here
;	$Ratio = 0
;	For $i = 0 to $gBaselineSize - 1
;		If $gBaselines[$i][0] = $Label Then
;			$Ratio = ( $gBaseLines[$i][1] / $gTimerPoint ) * 100000
;			ExitLoop
;		EndIf
;	Next
;	If $Ratio = 0 Then
;		$gTimeIndex[$gIndex] = $Label & "," & ($gTimerPoint / 1000)
;		;ConsoleWrite( "TakeTimer: " & $gTimeIndex[$gIndex] & @CRLF )
;	Else
;		$gTimeIndex[$gIndex] = $Label & "," & ($gTimerPoint / 1000) & "," & $Ratio
;	EndIf
;	outputTiming( $gTimeIndex[ $gIndex ] )
;EndFunc

Func TimerBegin()
	$gTimer = TimerInit()
EndFunc

Func TimerEnd( $Label )
	Local $lRatio
	Local $lFound
	Local $lsTimingMessage
	
	$gTimerPoint = TimerDiff( $gTimer )	

	;*** Block comment begin
	; Added for OPBM July, 2011
	; Compute the ratio in addition to the time, based on the baseline values encoded in the script
	; Each script should have code like this at the startup before any TakeTimer() functions are called:
	;	Dim $gBaselines[1][2]
	;	Dim $gBaselineSize
	;	$gBaselines[0][0] = "Description"		; The TakeTimer() $Label description goes here"
	;	$gBaselines[0][1] = 2.0					; The timing value goes here
	;	$gBaselineSize = 1						; Number of items in gBaselines[] goes here
	$lRatio = 0
	$lFound = 0
	For $i = 0 to $gBaselineSize - 1
		If $gBaselines[$i][0] = $Label Then
			$lRatio = ( $gBaseLines[$i][1] / $gTimerPoint ) * 100000
			$lFound = 1
			ExitLoop
		EndIf
	Next
	
	If $lFound = 0 Then
		$lsTimingMessage = $Label & ", " & ( $gTimerPoint / 1000 )
	Else
		$lsTimingMessage = $Label & ", " & ( $gTimerPoint / 1000 ) & ", " & $lRatio
	EndIf
	outputTiming( $lsTimingMessage )
	$gTimeIndex[ $gIndex ] = $lsTimingMessage
	$gIndex =  $gIndex + 1
EndFunc

Func outputDebug( $outputString )
	ConsoleWrite( "debug," & $outputString & @CRLF )
EndFunc

Func outputTiming( $outputString )
	ConsoleWrite( "timing," & $outputString & @CRLF )
EndFunc

Func outputStatus( $outputString )
	ConsoleWrite( "status," & $outputString & @CRLF )
EndFunc

Func outputError( $outputString )
	ConsoleWrite( "error," & $outputString & @CRLF )
EndFunc

Func TimerWriteTimesToCSV( $CSVPath )
	Local $lFileTimerCsv
	Local $i
	;Local $liArraySize
	
	$gTimerPoint = TimerDiff( $gScriptBeginTime )	
	$gTimeIndex[ $gIndex ] = "Total Runtime," & ( $gTimerPoint / 1000 )
	outputStatus( "TimerFinish: " & $gTimeIndex[$gIndex] & @CRLF )
	$lFileTimerCsv = FileOpen($CSVPath, 9)
	If 	$lFileTimerCsv = -1 Then
		ErrorHandle($ERROR_PREFIX & "TimerFinish:FileOpen: . Unable to open file.")
	EndIf
	
	;$liArraySize = UBound( $gTimeIndex )
	For $i = 0 To $gIndex
		$gErrorTrap = FileWriteLine($lFileTimerCsv, $gTimeIndex[$i])
		If $gErrorTrap = 0 Then ErrorHandle($ERROR_PREFIX & "TimerFinish:FileWriteLine($lFileTimerCsv, $gTimeIndex[$i]). Unable to write to file.")
		Next
		
	$gErrorTrap = FileWriteLine($lFileTimerCsv, "")
	If $gErrorTrap = 0 Then
		ErrorHandle($ERROR_PREFIX & "TimerFinish:FileWriteLine($lFileTimerCsv, $gTimeIndex[$i]). Unable to write to file.")
	EndIf
	
	$gErrorTrap = FileClose( $lFileTimerCsv )
	If $gErrorTrap = 0 Then
		ErrorHandle($ERROR_PREFIX & "TimerFinish:FileClose: Unable to close file.")
	EndIf
EndFunc

Func WordGoToLine($Line)
	$gErrorTrap = WinActivate(" - Microsoft Word", "Status Bar")
	If $gErrorTrap = 0 Then ErrorHandle(" - Microsoft Word. Window was not found or could not be activated.")
	Send("^g")
	$gErrorTrap = WinWait("Find and Replace", "", $gTimeout)
	If $gErrorTrap = 0 Then ErrorHandle("WinWait: Find and Replace. Unable to find Window.")
	$gErrorTrap = WinActivate("Find and Replace", "")
	If $gErrorTrap = 0 Then ErrorHandle("WinActivate: Find and Replace. Window was not found or could not be activated.")
	Send("!o")
	Send("l")
	ControlSend("Find and Replace", "", "RichEdit20W4", $line)
	Send("{ENTER}")
	$gErrorTrap = WinClose("Find and Replace", "")
	If $gErrorTrap = 0 Then ErrorHandle("WinClose: Find and Replace. Unable to close window.")
	;WaitUntilProcessIdle($gPID, -1, 250, 2, 5)
	$gErrorTrap = WinActivate(" - Microsoft Word", "Status Bar")
	If $gErrorTrap = 0 Then ErrorHandle("WinActivate:  - Microsoft Word. Window was not found or could not be activated.")
EndFunc

Func ErrorHandle( $Text, $ShowMsgBox = True, $aExit = True )
	Local $lErrorFile
	outputError("Error was handled: " & $Text & @CRLF)
	$lErrorFile = FileOpen(@ScriptDir & "\" &@ScriptName & "-Error.txt", 2)
	FileWriteLine($lErrorFile, "**************************************")
	FileWriteLine($lErrorFile, @MON & "/" & @MDAY & "/" & @YEAR & "  " & @HOUR & ":" & @MIN & ":" & @SEC)
	FileWriteLine($lErrorFile, $Text)
	FileClose($lErrorFile)
	;ProcessClose($gPID)
	If $ShowMsgBox = True Then
		MsgBox(16,"Script Error!", $Text)
	EndIf
	If $aExit Then
		opbmCloseAllWindowsNotPreviouslyNoted()
		Exit
	EndIf
EndFunc

Func ErrorDlg()
	If WinActive("Error", "") Then ErrorHandle("An Error Dialog Box appeared")
EndFunc

Func opbmWinWaitActivate( $title, $text = "", $timeout = 0, $errorText = "" )
	Dim $lReturnCode = 1
	; Attempt to find the window
	WinWait( $title, $text, $timeout )
	
	; See if it's already active, if not, activate it
	If Not WinActive($title, $text) Then
		WinActivate($title, $text)
		; Wait for it to be active
		If WinWaitActive($title, $text, $timeout) = 0 Then
			ErrorHandle( $errorText )
		EndIf
	EndIf
	; If we get here, we're good
	Return $lReturnCode
EndFunc

Func opbmWinActivate( $title, $text = "", $timeout = 0, $errorText = "" )
	Dim $lReturnCode = 1
	; See if it's already active, if not, activate it
	If Not WinActive($title, $text) Then
		WinActivate($title, $text)
		; Wait for it to be active
		If WinWaitActive($title, $text, $timeout) = 0 Then
			ErrorHandle( $errorText )
		EndIf
	EndIf
	; If we get here, we're good
	Return $lReturnCode
EndFunc

Func opbmWinWaitClose( $title, $text = "", $timeout = 0, $errorText = "" )
	; Try to close it
	If WinWaitClose( $title, $text, $gTimeout ) = 0 Then
		; If we get here, it didnt' close
		ErrorHandle( $errorText )
	EndIf
EndFunc

Func opbmFileDelete( $filename )
	$gErrorTrap = FileDelete( $filename )
	If $gErrorTrap = 0 Then
		ErrorHandle( $ERROR_PREFIX & "FileDelete: Unable to delete file: " & $filename)
	EndIf
EndFunc

Func opbmFileDeleteIfExists( $filename )
	If FileExists( $filename ) Then
		opbmFileDelete( $filename )
	EndIf
EndFunc

Func opbmFinalizeScript($name)
	; Save the captures time events to the CSV
	TimerWriteTimesToCSV( @ScriptDir & "\Results\" & $name )
	
	; Wait for the system to settle down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc


Func opbmTypeURL( $url, $timerText, $open )
	Local $i
	outputDebug( "Attempting to obtain Open File dialog window" )
	
	; Begin the timer before we press Ctrl+L for the address bar
	TimerBegin()
	Send( "^l" )
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
	
	; Convert any "\" character to a "/" character
	; Send the URL and capture the end timer
	Send( "file://" & StringReplace( $url, "\", "/" ) )
	TimerEnd( $timerText )
	; Don't sleep afterward because it sets up the next keystroke
EndFunc
