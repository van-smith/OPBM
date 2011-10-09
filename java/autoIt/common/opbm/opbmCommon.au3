#include <GUIConstants.au3>
#Include <Date.au3>
AutoItSetOption("MustDeclareVars", 1) 
Opt("SendKeyDelay", 25) 
;AdlibEnable("ErrorDlg", 500)

#include <..\scoring\baselineScores.au3>


Const $ERROR_PREFIX								= @ScriptName & ":" & @ScriptLineNumber & ": "
Const $OPBM_DLL									= $ROOT_DIR & "\common\opbm\dll\opbm.dll"
Const $OPBM_SPLASH_HTML							= chr(34) & $ROOT_DIR & "\common\opbm\html\opbm_splash.html" & chr(34)
Const $CPU_ACTIVITY_THRESHOLD					= 5
Const $TIMER_MAX_INDEX_COUNT					= 100
; When these splash/landing files are opened/launched, the title bar will contain this text:
Const $OPBM_SPLASH_HTML_TITLE					= "OPBM Benchmark Splash"

Global $gIterations
Global $gPID
Global $gTimerPoint
Global $gTimer
Global $gTimerStart
Global $gIndex									= 0
Global $gTimeIndex[ $TIMER_MAX_INDEX_COUNT ]
Global $gScriptBeginTime
Global $gCSVPath
Global $gTimeout								= 30
Global $gLongTimeout							= 300
Global $gMessage
Global $gErrorTrap								= -9999
Global $gOpbmPluginHandle
Global $gcRegistryKeyRestorer					= "none"

; Used for WaitUntilIdle()
Global $gPercent								= 10
Global $gDurationMS								= 100
Global $gTimeoutMS								= 1000

; Parameters used for registry key saving / restoration
Const $RESTORING_OFFICE_2010_REGISTRY_KEYS			= "Restoring Office 2010 registry keys"
Const $SAVING_AND_SETTING_OFFICE_2010_REGISTRY_KEYS	= "Saving and Setting Office 2010 registry keys"

Func InitializeGlobalVariables()
	Local $i
	
	$gIterations			= ""
	$gPID					= ""
	$gTimerPoint			= ""
	$gTimer					= ""
	$gIndex					= 0
	$gScriptBeginTime		= ""
	$gCSVPath				= ""
	$gTimeOut				= 30
	$gLongTimeOut			= 300
	$gMessage				= ""
	$gErrorTrap				= -9999
	
	; Initialize the time indices
	For $i = 1 to $TIMER_MAX_INDEX_COUNT
		$gTimeIndex[$i - 1] = ""
	Next
	
	; Load the opbm.dll plugin:
	; It allows these functions to appear as "native" AutoIt functions, usable anywhere:
	;		WaitUntilIdle($aPID, $aCpuUsageThreshold, $aPollPeriodMS, $aTimeoutMS )
	;		WaitUntilSystemIdle( $aCpuUsageThreshold, $aPollPeriodMS, $aTimeoutMS )
	;			Example:  WaitUntilIdle( $gPID, 10, 100, 1000 )  ; Wait up to 1 second for the process CPU usage to drop below 10% for 100ms
	;			Example:  WaitUntilSystemIdle( 10, 200, 2000 )  ; Wait up to 2 seconds for the entire system's CPU usage to drop below 10% for 200ms
	;
	;	Additional functions in opbm.dll:
	;		GetUsage($aPID, $aPollPeriodMS )		; Immediately return the process usage sampled over the poll period in milliseconds
	;		GetSystemUsage( $aPollPeriodMS )		; Immediatley return the system usage sampled over the poll period in milliseconds
	;		NoteAllOpenWindows()					; Use at start of script
	;		CloseAllWindowsNotPreviouslyNoted()		; Use anywhere during script to close all windows that weren't already open when noted
	;		FirefoxInstallerAssist()				; Sets Firefox to a reasonable state after initial install (no prompting, scripts run, etc.)
	;		ChromeInstallerAssist()					; Sets Chrome to a reasonable state after initial install (no prompting, scripts run, etc.)
	;		OperaInstallerAssist()					; Sets Opera to a reasonable state after initial install (no prompting, scripts run, etc.)
	;		SafariInstallerAssist()					; Sets Safari to a reasonable state after initial install (no prompting, scripts run, etc.)
	;		InternetExplorerInstallerAssist()		; Sets IE keys to a reasonable state, to disable initial promptings
	;		Office2010SaveKeys()					; Saves existing registry keys overwritten/used by Office 2010 scripts for this instance
	;		Office2010InstallKeys()					; Installs Opbm registry keys for Office 2010
	;		Office2010RestoreKeys()					; Retores previously saved registry keys (from Office2010SaveKeys() call during this instance of the script)
	;		CheckIfRegistryKeyStartsWith( $key, $valueShouldStartWith )
	;		CheckIfRegistryKeyContains( $key, $stringItShouldContain )
	;		CheckIfRegistryKeyIsExactly( $key, $value )
	;		SetRegistryKeyString( $key, $stringValue )
	;		SetRegistryKeyDword( $key, $dwordValue )
	;		GetRegistryKey( $key )
	;;;;;;;;FixupPathnames( $pathname )				; (future function) Converts "c:\some\dir\..\path\" to "c:\some\path" (removes "dir\..")
	;;;;;;;;The following functions ALWAYS return/include the trailing backslash:
	;		GetScriptCSVDirectory()					; Returns c:\users\user\documents\opbm\scriptOutput\
	;		GetScriptTempDirectory()				; Returns c:\users\user\documents\opbm\scriptOutput\temp\
	;		GetHarnessXmlDirectory()				; Returns c:\users\user\documents\opbm\results\xml\
	;		GetHarnessCSVDirectory()				; Returns c:\users\user\documents\opbm\results\csv\
	;		GetHarnessTempDirectory()				; Returns c:\users\user\documents\opbm\temp\
	;		GetCSIDLDirectory( $CSIDL_nameWithoutLeadingCSIDL_)
	;			Example:  GetCSIDLDirectory( "MYDOCUMENTS" )
	;			Returns:  c:\users\user\documents\
	;		is32BitOS()		; Is the OS installed a 32-bit OS?
	;		is64BitOS()		; Is the OS installed a 64-bit OS?
	;		GetCoreCount()	; Returns the number of cores on the system
	;;;;;;;;Added in support of JBM and the Java Benchmark test, which runs instances for every core:
	;		JbmOwnerReportingIn()							; At startup, makes the connection to the running JBM instance
	;		JbmOwnerHaveAllInstancesExited()				; Polled once per second, to find out when all of the JVMs are done
	;		JbmOwnerRequestsScoringData(jvm, subtest)		; Requests the scoring data from all JVMs that tested
	;		JbmOwnerRequestsSubtestMaxScoringData(subtest)	; Request the highest score for the specified subtest
	;		JbmOwnerRequestsSubtestName(subtest)			; Requests the name of the subtest
	;		JbmOwnerRequestsSubtestAvgTiming(subtest)		; Requests the average timing observed for the subtest
	;		JbmOwnerRequestsSubtestMinTiming(subtest)		; Requests the minimum timing observed for the subtest
	;		JbmOwnerRequestsSubtestMaxTiming(subtest)		; Requests the minimum timing observed for the subtest
	;		JbmOwnerRequestsSubtestGeoTiming(subtest)		; Requests the geometric mean timing observed for the subtest
	;		JbmOwnerRequestsSubtestCVTiming(subtest)		; Requests the cv timing observed for the subtest
	;		JbmOwnerRequestsSubtestAvgScoring(subtest)		; Requests the average scoring observed for the subtest
	;		JbmOwnerRequestsSubtestMinScoring(subtest)		; Requests the maximum scoring observed for the subtest
	;		JbmOwnerRequestsSubtestMaxScoring(subtest)		; Requests the maximum scoring observed for the subtest
	;		JbmOwnerRequestsSubtestGeoScoring(subtest)		; Requests the geoemtric mean scoring observed for the subtest
	;		JbmOwnerRequestsSubtestCVScoring(subtest)		; Requests the cv scoring observed for the subtest
	;		JbmOwnerRequestsTheJbmSelfTerminate()			; Tells the JBM that the owner is done with it, and to self-terminate (shut down/exit)
	$gOpbmPluginHandle = PluginOpen( $OPBM_DLL )
	If $gOpbmPluginHandle <> 0 Then 
		errorHandle( $OPBM_DLL & " did not open" )
	EndIf
	;outputDebug( "Plugin " & $OPBM_DLL & " opened properly" )
	outputDebug( "Plugins loaded okay" )
	If is32BitOS() Then
		outputDebug( "Detected Windows 32-bit Operating System" )
	Else
		outputDebug( "Detected Windows 64-bit Operating System" )
	EndIf
	
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
	; Wait for any lingering windows to appear (up to five seconds, minimum of one)
	WaitUntilSystemIdle( 10, 1000, 5000 )
	opbmCloseAllWindowsNotPreviouslyNoted()
	
	; Repeat (in case we were on a dialog that needed closed, up to five seconds, minimum of one)
	WaitUntilSystemIdle( 10, 1000, 5000 )
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

Func TimerBegin()
	$gTimer = TimerInit()
EndFunc

Func TimerEnd( $Label )
	$gTimerPoint = TimerDiff( $gTimer )	
	TimerRecord( $Label, $gTimerPoint )
EndFunc

Func TimerRecord( $Label, $time )
	Local $lRatio
	Local $lFound
	Local $lsTimingMessage

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
			$lRatio = ( $gBaseLines[$i][1] / $time ) * 100000
			$lFound = 1
			ExitLoop
		EndIf
	Next
	
	; The format of lsTimingMessage cannot be changed without breaking the harness.
	; It looks to the exact format "Description, time, ratio" to obtain its numbers.
	If $lFound = 0 Then
		$lsTimingMessage = $Label & ", " & ( $time / 1000 )
	Else
		$lsTimingMessage = $Label & ", " & ( $time / 1000 ) & ", " & $lRatio
	EndIf
	outputTiming( $lsTimingMessage )
	$gTimeIndex[ $gIndex ] = $lsTimingMessage
	$gIndex =  $gIndex + 1
EndFunc

Func outputDebug( $outputString )
	ConsoleWrite( "debug," & $outputString & @CRLF )
EndFunc

Func outputTiming( $outputString )
;; There is a dependency on this format in the HUD.properlyFormat() method:
;; Begin
	ConsoleWrite( "timing," & $outputString & @CRLF )
;; Please do not change
;; Line must be passed to OPBM as: "timing,Run Google V8, 25.5744077447851, 53.8921816837743"
;; End
EndFunc

Func outputStatus( $outputString )
	ConsoleWrite( "status," & $outputString & @CRLF )
EndFunc

Func outputError( $outputString )
	ConsoleWrite( "error," & $outputString & @CRLF )
EndFunc

Func outputConflict( $outputString )
	ConsoleWrite( "conflict," & $outputString & @CRLF )
EndFunc

Func outputResolution( $outputString )
	ConsoleWrite( "resolution," & $outputString & @CRLF )
EndFunc

Func outputInternalCommand( $outputString )
	ConsoleWrite( "command," & $outputString & @CRLF )
EndFunc

; Added to allow a raw message to be passed, so that additional
; information can be logged to the output capture without it being
; processed by the HUD's visual components.
Func outputMessage( $outputString )
	ConsoleWrite( $outputString & @CRLF )
EndFunc

Func TimerWriteTimesToCSV( $CSVPath )
	Local $lFileTimerCsv
	Local $i
	
	$gTimerPoint = TimerDiff( $gScriptBeginTime )
;; There is a dependency on this format in the HUD.properlyFormat() method:
;; Begin
	$gTimeIndex[ $gIndex ] = "Total Runtime," & ( $gTimerPoint / 1000 )
	outputStatus( "TimerFinish: " & $gTimeIndex[$gIndex] & @CRLF )
;; Please do not change
;; Line must be passed to OPBM as: "status,TimerFinish: Total Runtime,28.3679337510449"
;; End
	$lFileTimerCsv = FileOpen($CSVPath, 9)
	If 	$lFileTimerCsv = -1 Then
		ErrorHandle($ERROR_PREFIX & "TimerFinish:FileOpen: . Unable to open file.")
	EndIf
	
	For $i = 0 To $gIndex
		$gErrorTrap = FileWriteLine($lFileTimerCsv, $gTimeIndex[$i])
		If $gErrorTrap = 0 Then
			ErrorHandle($ERROR_PREFIX & "TimerFinish:FileWriteLine($lFileTimerCsv, $gTimeIndex[$i]). Unable to write to file.")
		EndIf
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
	If $gErrorTrap = 0 Then
		ErrorHandle(" - Microsoft Word. Window was not found or could not be activated.")
	EndIf
	Send("^g")
	$gErrorTrap = WinWait("Find and Replace", "", $gTimeout)
	If $gErrorTrap = 0 Then
		ErrorHandle("WinWait: Find and Replace. Unable to find Window.")
	EndIf
	$gErrorTrap = WinActivate("Find and Replace", "")
	If $gErrorTrap = 0 Then
		ErrorHandle("WinActivate: Find and Replace. Window was not found or could not be activated.")
	EndIf
	Send("!o")
	Send("l")
	ControlSend("Find and Replace", "", "RichEdit20W4", $line)
	Send("{ENTER}")
	$gErrorTrap = WinClose("Find and Replace", "")
	If $gErrorTrap = 0 Then
		ErrorHandle("WinClose: Find and Replace. Unable to close window.")
	EndIf
	$gErrorTrap = WinActivate(" - Microsoft Word", "Status Bar")
	If $gErrorTrap = 0 Then
		ErrorHandle("WinActivate:  - Microsoft Word. Window was not found or could not be activated.")
	EndIf
EndFunc

Func ErrorHandle( $Text, $ShowMsgBox = False, $aExit = True )
	Local $lErrorFile
	outputError("Error was handled: " & $Text & @CRLF)
	$lErrorFile = FileOpen(@ScriptDir & "\" &@ScriptName & "-Error.txt", 2)
	FileWriteLine($lErrorFile, "**************************************")
	FileWriteLine($lErrorFile, @MON & "/" & @MDAY & "/" & @YEAR & "  " & @HOUR & ":" & @MIN & ":" & @SEC)
	FileWriteLine($lErrorFile, $Text)
	FileClose($lErrorFile)
; Disabled modal message box because error is now logged to the harness
;	If $ShowMsgBox Then
;		MsgBox(16,"Script Error!", $Text)
;	EndIf
	If $aExit Then
		opbmCloseAllWindowsNotPreviouslyNoted()
		Exit -1
	EndIf
EndFunc

Func ErrorDlg()
	If WinActive("Error", "") Then
		ErrorHandle( "An Error Dialog Box appeared" )
	EndIf
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

Func opbmWinWaitForEitherOfTwoWindows( $window1title, $window1text, $window2title, $window2text, $timeout )
	Local $lReturnCode
	Local $count
	
	$count = 0
	While (true)
		; Attempt to find the window
		If WinExists ( $window1title, $window1text ) Then
			; The first window was found
			$lReturnCode = 1
			ExitLoop
		EndIf
		If WinExists ( $window2title, $window2text ) Then
			; The second window was found
			$lReturnCode = 2
			ExitLoop
		EndIf
		; Wait for a second
		Sleep( 1000 )
		$count = $count + 1
		If $count > $timeout Then
			lReturnCode = 0
		EndIf
	WEnd
	return $lReturnCode
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
		ErrorHandle( $ERROR_PREFIX & "FileDelete: Unable to delete file: " & $filename )
	EndIf
EndFunc

Func opbmFileDeleteIfExists( $filename )
	If FileExists( $filename ) Then
		opbmFileDelete( $filename )
	EndIf
EndFunc

Func opbmFinalizeScript($name)
	; Save the captures time events to the CSV
	TimerWriteTimesToCSV( GetScriptCSVDirectory() & $name )
	
	; Wait for the system to settle down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc

Func opbmTypeURL( $url, $timerText, $open = "Attempting to open URL", $waitForWindow = " ", $waitForText = "" )
	outputDebug( $open )
	
	; Begin the timer before we press Ctrl+L for the address bar
	TimerBegin()
	Send( "^l" )
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
	
	; Convert any "\" character to a "/" character
	; Send the URL and capture the end timer
	Send( "file://" & StringReplace( $url, "\", "/" ) & "{Enter}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )

	; See if we have to wait for a window to appear
	If not StringIsSpace( $waitForWindow ) Then
		; We have to wait for the window to appear
		opbmWinWaitActivate( $waitForWindow, $waitForText, 30, "Unable to find " & $waitForWindow & " " & $waitForText )
		; If we get here, then the window was found
	EndIf
	; Store our timing for this part
	TimerEnd( $timerText )
	; Don't sleep afterward because the caller will handle all of that
EndFunc

Func opbmTypeURLSafari( $url, $timerText, $open = "Attempting to open URL", $waitForWindow = " ", $waitForText = "" )
	outputDebug( $open )
	
	; Begin the timer before we press Ctrl+L for the address bar
	TimerBegin()
	Send( "^l" )
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
	
	; Convert any "\" character to a "/" character
	; Send the URL and capture the end timer
	Send( "file:///" & StringReplace( $url, "\", "/" ) & "{Enter}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )

	; See if we have to wait for a window to appear
	If not StringIsSpace( $waitForWindow ) Then
		; We have to wait for the window to appear
		opbmWinWaitActivate( $waitForWindow, $waitForText, 30, "Unable to find " & $waitForWindow & " " & $waitForText )
		; If we get here, then the window was found
	EndIf
	; Store our timing for this part
	TimerEnd( $timerText )
	; Don't sleep afterward because the caller will handle all of that
EndFunc

Func KillProcessByName( $name )
	Local $list
	Local $i
	Local $pid
	Local $pname
	Local $result
	
	; Attempt to close the process (or processes if there are multiple instances)
	$list = ProcessList( $name )
	For $i = 0 to $list[0][0]
		$pname	= $list[$i][0]
		$pid	= $list[$i][1]
		If StringInStr( $pname, ".exe" ) Then
			$result	= ProcessClose( $pid )
			If $result <> 1 Then
				; An error occurred trying to close the process naturally, so use a tool to do it
				TaskKillProcessByID( $pid, $pname )
				; If we get here, then PsKill successfully closed the app
			EndIf
		EndIf
	Next
EndFunc

Func TaskKillProcessByID( $pid, $nameToDisplay )
	Local $key
	Local $cmd
	Local $pskillid
	Local $exists
	
	If ProcessExists( $pid ) Then
		$cmd		= $ROOT_DIR & "\common\opbm\exe\taskkill.exe /f /t /pid " & $pid
		outputDebug( "Attempting " & $cmd & " " & $nameToDisplay )
		$pskillid	= Run( $cmd, $ROOT_DIR, @SW_SHOWNORMAL)
		If ProcessWait( $pskillid, 30 ) <> $pskillid Then
			; An error occurred while processing the command
			If ProcessExists( $pid ) Then
				; The process should have been killed, but was not
				ErrorHandle( "Unable to forcibly terminate " & $nameToDisplay & ", pid " & $pid )
			;Else
			;We're good, the process is no longer in existence, even though pskill.exe failed
			EndIf
		;Else
		; We're good, it was a success ($pid was killed properly)
		EndIf
	;Else
	; We're good, it already doesn't exist
	EndIf
EndFunc

Func Office2010SaveRegistryKeys()
; no longer used, moved to OPBM Java app
EndFunc
Func Office2010InstallRegistryKeys()
; no longer used, moved to OPBM Java app
EndFunc
Func Office2010RestoreRegistryKeys()
; no longer used, moved to OPBM Java app
EndFunc
