#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=7ZipRunTest.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written August 2011 by:
; -----------------------
;	Van Smith
;
; Usage:	7ZipRunTest [ repeat_count ]
;
;======================================================================================================================================
#include <../../common/7zip/7zipCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit
Dim $is386Executable

$gBaselines[2][0] = $SEVENZIP_UNARCHIVE_FIVE_TIMES
$gBaselines[2][1] = $SEVENZIP_UNARCHIVE_FIVE_TIMES_SCORE
$gBaselines[3][0] = $SEVENZIP_ARCHIVE_FIVE_DIRECTORIES
$gBaselines[3][1] = $SEVENZIP_ARCHIVE_FIVE_DIRECTORIES_SCORE
$gBaselines[4][0] = $SEVENZIP_TEST_ARCHIVE_INTEGRITY
$gBaselines[4][1] = $SEVENZIP_TEST_ARCHIVE_INTEGRITY_SCORE

outputDebug( "Starting up 7-Zip Run Test" )

if $CmdLine[0] > 0 then 
	$LoopLimit = $CmdLine[1] 
Else 
	$LoopLimit = 1
EndIf
For $CurrentLoop = 1 to $LoopLimit
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	outputDebug( "Initialize7ZipScript()" )
	Initialize7ZipScript()
	
	outputDebug( "Launch7Zip()" )
	Launch7Zip()
	
	outputDebug( "UnarchiveFiveTimes()" )
	UnarchiveFiveTimes()

	outputDebug( "ArchiveFiveDirectories()" )
	ArchiveFiveDirectories()
	
	outputDebug( "TestArchiveIntegrity()" )
	TestArchiveIntegrity()
	
	outputDebug( "FinalizeScript()" )
	opbmFinalizeScript( "7ZipRunTestTimes.csv" )
Next
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func UnarchiveFiveTimes()
	Local $i
	Local $directory
	
	; Clean up before we begin, delete everything in the 7zipRunTest\ and further down
	DirRemove( GetScriptTempDirectory() & "7zipRunTest\", 1 )
	
	; Start the timer for the benchmark
	TimerBegin()
	
	For $i = 1 to 5
		; Send F5 to bring up "Copy To" (extract) dialog box
		Send( "{F5}" )
		Sleep(250)
		opbmWinWaitActivate( "Copy", "Copy To", 30, $ERROR_PREFIX & "WinWait: 7-Zip Copy Dialog: Unable to find Window.")
		
		; Send the filename
		$directory = GetScriptTempDirectory() & "7zipRunTest\directory" & $i & "\"
		Send( $directory )
		Sleep(250)
		opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
		; Click the "OK" button
		Send( "{Enter}" )
		Sleep(250)
		; Wait until it's done
		opbmWaitUntilProcessIdle( $gPID, 10, 250, 30000 )
	Next
	
	; Close the window
	Send("!{F4}")
	opbmWaitUntilProcessIdle( $gPID, 10, 250, 30000 )
	
	; Record ending time
	TimerEnd( $SEVENZIP_UNARCHIVE_FIVE_TIMES )
EndFunc

Func ArchiveFiveDirectories()
	Local $filename
	Local $directory
	Local $cmd
	
	; Start the timer for the benchmark
	TimerBegin()
	
	; Create a command like this:
	; 7z a -r C:\Users\rick\Documents\opbm\scriptOutput\temp\7zipruntest.7z C:\Users\rick\Documents\opbm\scriptOutput\temp\7zipruntest\*
	$filename	= GetScriptTempDirectory() & "7zipRunTest.7z"
	$directory	= GetScriptTempDirectory() & "7zipRunTest\*"
	If not $is386Executable Then
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_X64 & " a -r " & $filename & " " & $directory
	Else
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_I386 & " a -r " & $filename & " " & $directory
	EndIf
	
	$gPID = Run( $cmd, "C:\", @SW_SHOWMAXIMIZED )
	If ProcessWaitClose( $gPID, 120 ) <> 1 Then
		; An error occurred
		ErrorHandle( $ERROR_TEXT & ": Unable to execute 7z command" )
	EndIf
	
	; Record ending time
	TimerEnd( $SEVENZIP_ARCHIVE_FIVE_DIRECTORIES )
EndFunc

Func TestArchiveIntegrity()
	Local $filename
	Local $directory
	Local $cmd
	
	; Start the timer for the benchmark
	TimerBegin()
	
	; Create a command like this:
	; 7z t C:\Users\rick\Documents\opbm\scriptOutput\temp\7zipruntest.7z
	$filename	= GetScriptTempDirectory() & "7zipRunTest.7z"
	If not $is386Executable Then
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_X64 & " t " & $filename
	Else
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_I386 & " t " & $filename
	EndIf
	
	$gPID = Run( $cmd, "C:\", @SW_SHOWMAXIMIZED )
	If ProcessWaitClose( $gPID, 120 ) <> 1 Then
		; An error occurred
		ErrorHandle( $ERROR_TEXT & ": Unable to execute 7z command" )
	EndIf
	
	; Record ending time
	TimerEnd( $SEVENZIP_TEST_ARCHIVE_INTEGRITY )
EndFunc
