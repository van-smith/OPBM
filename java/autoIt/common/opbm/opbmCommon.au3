#include <GUIConstants.au3>
#Include <Date.au3>
AutoItSetOption("MustDeclareVars", 1) 
Opt("SendKeyDelay", 25) 
;AdlibEnable("ErrorDlg", 500)

#include <..\scoring\baselineScores.au3>


Const $ERROR_PREFIX								= @ScriptName & ":" & @ScriptLineNumber & ": "
Const $OPBM_DLL									= $ROOT_DIR & "\common\opbm\dll\opbm.dll"
Const $OPBM_SPLASH_HTML							= $ROOT_DIR & "\common\opbm\html\opbm_splash.html"
Const $OPBM_SPLASH_ZIP							= $ROOT_DIR & "\common\opbm\zip\opbm_test_archive.7z"
Const $CPU_ACTIVITY_THRESHOLD					= 5
Const $TIMER_MAX_INDEX_COUNT					= 100
; When these splash/landing files are opened/launched, the title bar will contain this text:
Const $OPBM_SPLASH_HTML_TITLE					= "OPBM Benchmark Splash"
Const $OPBM_SPLASH_ZIP_TITLE					= "opbm_test_archive.7z"

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
	;		FixupPathnames( $pathname )				; Converts "c:\some\dir\..\path\" to "c:\some\path" (removes "dir\..")
	;		; The following DO include the trailing backslash
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
	$gOpbmPluginHandle = PluginOpen( $OPBM_DLL )
	If $gOpbmPluginHandle <> 0 Then 
		errorHandle( $OPBM_DLL & " did not open" )
	EndIf
	outputDebug( "Plugin " & $OPBM_DLL & " opened properly" )
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
		; Upon exit, we potentially need to restore the registry for the specified operation we're running
		checkRegistryKeysNeedingRestored()
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

; August 12, 2011
; ! Used only by apps that launch Office2010 !
;
; Should be used at startup by calling these two in this order:
;	Office2010SaveRegistryKeys()
;	Office2010InstallRegistryKeys()
;
; and then a clean termination procedure is to call one of these:
;	Office2010RestoreRegistryKeys()
;	checkRegistryKeysNeedingRestored()
;
; Note:  A better solution would be to have the harness handle all registry key saving / restoring automatically.
; Note:  The called functions reside in common\opbm\dlls\opbm.dll
Func Office2010SaveRegistryKeys()
	$gcRegistryKeyRestorer = "Office2010"
	Office2010SaveKeys()
EndFunc
Func Office2010InstallRegistryKeys()
	Office2010InstallKeys()
EndFunc
Func Office2010RestoreRegistryKeys()
	Office2010RestoreKeys()
EndFunc

; If the $gcRegistryKeyRestorer variable contains something recognized, restore those registry key settings
; Note:  There is logic within opbm.dll which prevents improperly called registry keys from being restored,
;        if for example they had never been saved in the first place.
Func checkRegistryKeysNeedingRestored()
	$gcRegistryKeyRestorer = StringLower( $gcRegistryKeyRestorer )
	If $gcRegistryKeyRestorer = "office2010" Then
		; Undo any registry key settings handled by Office2010 startup code
		outputDebug( $RESTORING_OFFICE_2010_REGISTRY_KEYS )
		Office2010RestoreRegistryKeys()
	;ElseIf $registryKeyRestorer = "something else"
	;ElseIf $registryKeyRestorer = "another thing"
	EndIf
EndFunc
