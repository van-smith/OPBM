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

$gBaselines[2][0] = $SEVENZIP_CREATE_7Z_ARCHIVE
$gBaselines[2][1] = $SEVENZIP_CREATE_7Z_ARCHIVE_SCORE
$gBaselines[3][0] = $SEVENZIP_7Z_UNARCHIVE_FIVE_TIMES
$gBaselines[3][1] = $SEVENZIP_7Z_UNARCHIVE_FIVE_TIMES_SCORE
$gBaselines[4][0] = $SEVENZIP_TEST_7Z_ARCHIVE_INTEGRITY
$gBaselines[4][1] = $SEVENZIP_TEST_7Z_ARCHIVE_INTEGRITY_SCORE
$gBaselines[5][0] = $SEVENZIP_ZIP_UNARCHIVE_FIVE_TIMES
$gBaselines[5][1] = $SEVENZIP_ZIP_UNARCHIVE_FIVE_TIMES_SCORE
$gBaselines[6][0] = $SEVENZIP_TEST_ZIP_ARCHIVE_INTEGRITY
$gBaselines[6][1] = $SEVENZIP_TEST_ZIP_ARCHIVE_INTEGRITY_SCORE
$gBaselines[7][0] = $SEVENZIP_CREATE_ZIP_ARCHIVE
$gBaselines[7][1] = $SEVENZIP_CREATE_ZIP_ARCHIVE_SCORE

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

	If not is7ZipAlreadyInstalled() Then
		opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
		Report7ZipNotFoundAndTerminate()
	EndIf

	Cleanup7ZipDirectories()

	outputDebug( "Create7zArchive()" )
	create7zArchive()

	outputDebug( "Test7zArchiveIntegrity()" )
	Test7zArchiveIntegrity()

	outputDebug( "CreateZipArchive()" )
	createZipArchive()

	outputDebug( "TestZipArchiveIntegrity()" )
	TestZipArchiveIntegrity()

	outputDebug( "Unarchive7zFiveTimes()" )
	Unarchive7zFiveTimes()

	outputDebug( "UnarchiveZipFiveTimes()" )
	UnarchiveZipFiveTimes()

	outputDebug( "FinalizeScript()" )
	opbmFinalizeScript( "7ZipRunTestTimes.csv" )
Next
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Cleanup7ZipDirectories()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func create7ZArchive()
	Local $filename
	Local $inputFiles
	Local $cmd

	; Start the timer for the benchmark
	TimerBegin()

	; Create a command like this:
	; 7z a C:\Users\rick\Documents\opbm\scriptOutput\temp\7zipruntest.7z [files]
	#cs	; Removed to incorporate new dataset -rcp 2011_12_09
	$filename	= chr(34) & GetScriptTempDirectory() & "7zipRunTest\7zipRunTest.7z" & chr(34) & " "

	$inputFiles	= chr(34) & $ROOT_DIR & "\7zip\install\exe\7z920-i386.exe" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\7zip\install\exe\7z920-x64.msi" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\acrobatReader\install\exe\AdbeRdr1010_en_US.exe" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\chrome\install\exe\ChromeStandaloneSetup.exe" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\firefox\install\exe\Firefox Setup 5.0.1.exe" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\opera\install\exe\Opera_1150_int_Setup.exe" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\safari\install\exe\SafariSetup.exe" & chr(34) & " "
	#ce	;-rcp 2011_12_09

	$filename	= chr(34) & GetScriptTempDirectory() & "7zipRunTest\7zipRunTest.7z" & chr(34) & " "	;-rcp 2011_12_09

	$inputFiles	= chr(34) & @ScriptDir & "\Data" & chr(34)	;-rcp 2011_12_09

	If not $is386Executable Then
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_X64 & " a -mx9 " & $filename & $inputFiles
	Else
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_I386 & " a -mx9 " & $filename & $inputFiles
	EndIf
	outputDebug( "Attempting to run " & $cmd )
	$gPID = Run( $cmd, "C:\", @SW_SHOWMAXIMIZED )
	If ProcessWaitClose( $gPID, 600) <> 1 Then	; Changed from 240 to 600 -rcp 12/11/2011
		; An error occurred, or the process didn't complete after 4 minutes ; 10 min -rcp 12/11/2011
		ErrorHandle( $ERROR_PREFIX & ": Error executing 7z command [" & $cmd & "]" ) ;Changed from $ERROR_TEXT to $ERROR_PREFIX -rcp 12/11/2011
	EndIf

	; Record ending time
	TimerEnd( $SEVENZIP_CREATE_7Z_ARCHIVE )
EndFunc

Func Test7zArchiveIntegrity()
	Local $filename
	Local $directory
	Local $cmd

	; Start the timer for the benchmark
	TimerBegin()

	; Create a command like this:
	; 7z t C:\Users\rick\Documents\opbm\scriptOutput\temp\7zipruntest.7z
	$filename	= chr(34) & GetScriptTempDirectory() & "7zipRunTest\7zipRunTest.7z" & chr(34) & " "
	If not $is386Executable Then
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_X64 & " t " & $filename
	Else
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_I386 & " t " & $filename
	EndIf

	$gPID = Run( $cmd, "C:\", @SW_SHOWMAXIMIZED )
	If ProcessWaitClose( $gPID, 60 ) <> 1 Then
		; An error occurred
		ErrorHandle( $ERROR_PREFIX & ": Unable to execute 7z command" ) ;Changed from $ERROR_TEXT to $ERROR_PREFIX -rcp 12/11/2011
	EndIf

	; Record ending time
	TimerEnd( $SEVENZIP_TEST_7Z_ARCHIVE_INTEGRITY )
EndFunc

Func createZipArchive()
	Local $filename
	Local $inputFiles
	Local $cmd

	; Start the timer for the benchmark
	TimerBegin()

	; Create a command like this:
	; 7z a C:\Users\rick\Documents\opbm\scriptOutput\temp\7zipruntest.zip [files]
	#cs	; Removed to incorporate new dataset -rcp 2011_12_09
	$filename	= chr(34) & GetScriptTempDirectory() & "7zipRunTest\7zipRunTest.zip" & chr(34) & " "

	$inputFiles	= chr(34) & $ROOT_DIR & "\7zip\install\exe\7z920-i386.exe" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\7zip\install\exe\7z920-x64.msi" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\acrobatReader\install\exe\AdbeRdr1010_en_US.exe" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\chrome\install\exe\ChromeStandaloneSetup.exe" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\firefox\install\exe\Firefox Setup 5.0.1.exe" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\opera\install\exe\Opera_1150_int_Setup.exe" & chr(34) & " "
	$inputFiles	= $inputFiles & chr(34) & $ROOT_DIR & "\safari\install\exe\SafariSetup.exe" & chr(34) & " "
	#ce	;-rcp 2011_12_09

	$filename	= chr(34) & GetScriptTempDirectory() & "7zipRunTest\7zipRunTest.zip" & chr(34) & " "	;-rcp 2011_12_09

	$inputFiles	= chr(34) & @ScriptDir & "\Data" & chr(34)	;-rcp 2011_12_09

	If not $is386Executable Then
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_X64 & " a -mx9 " & $filename & $inputFiles
	Else
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_I386 & " a -mx9 " & $filename & $inputFiles
	EndIf
	outputDebug( "Attempting to run " & $cmd )
	$gPID = Run( $cmd, "C:\", @SW_SHOWMAXIMIZED )
	If ProcessWaitClose( $gPID, 600 ) <> 1 Then	; Changed from 240 to 600 -rcp 12/11/2011
		; An error occurred, or the process didn't complete after 4 minutes ; 10 minutes -rcp 12/11/2011
		ErrorHandle( $ERROR_PREFIX & ": Error executing 7z command [" & $cmd & "]" ) ;Changed from $ERROR_TEXT to $ERROR_PREFIX -rcp 12/11/2011
	EndIf

	; Record ending time
	TimerEnd( $SEVENZIP_CREATE_ZIP_ARCHIVE )
EndFunc

Func TestZipArchiveIntegrity()
	Local $filename
	Local $directory
	Local $cmd

	; Start the timer for the benchmark
	TimerBegin()

	; Create a command like this:
	; 7z t C:\Users\rick\Documents\opbm\scriptOutput\temp\7zipruntest.7z
	$filename	= chr(34) & GetScriptTempDirectory() & "7zipRunTest\7zipRunTest.zip" & chr(34) & " "
	If not $is386Executable Then
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_X64 & " t " & $filename
	Else
		$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_I386 & " t " & $filename
	EndIf

	$gPID = Run( $cmd, "C:\", @SW_SHOWMAXIMIZED )
	If ProcessWaitClose( $gPID, 60 ) <> 1 Then
		; An error occurred
		ErrorHandle( $ERROR_PREFIX & ": Unable to execute 7z command" ) ;Changed from $ERROR_TEXT to $ERROR_PREFIX -rcp 12/11/2011
	EndIf

	; Record ending time
	TimerEnd( $SEVENZIP_TEST_ZIP_ARCHIVE_INTEGRITY )
EndFunc

Func Unarchive7zFiveTimes()
	Local $i
	Local $filename
	Local $destination
	Local $cmd

	; Start the timer for the benchmark
	TimerBegin()

	For $i = 1 to 5
		; Create a command like this:
		; 7z t C:\Users\rick\Documents\opbm\scriptOutput\temp\7zipruntest.7z
		$filename		= chr(34) & GetScriptTempDirectory() & "7zipRunTest\7zipRunTest.7z" & chr(34) & " "
		$destination	= chr(34) & GetScriptTempDirectory() & "7zipRunTest\extract7z_" & $i & "\" & chr(34)
		If not $is386Executable Then
			$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_X64 & " e " & $filename & " -o" & chr(34) & $destination & chr(34)
		Else
			$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_I386 & " e " & $filename & " -o" & chr(34) & $destination & chr(34)
		EndIf

		$gPID = Run( $cmd, "C:\", @SW_SHOWMAXIMIZED )
		If ProcessWaitClose( $gPID, 60 ) <> 1 Then
			; An error occurred
			ErrorHandle( $ERROR_PREFIX & ": Unable to execute 7z command" ) ;Changed from $ERROR_TEXT to $ERROR_PREFIX -rcp 12/11/2011
		EndIf
	Next

	; Record ending time
	TimerEnd( $SEVENZIP_7Z_UNARCHIVE_FIVE_TIMES )
EndFunc

Func UnarchiveZipFiveTimes()
	Local $i
	Local $filename
	Local $destination
	Local $cmd

	; Start the timer for the benchmark
	TimerBegin()

	For $i = 1 to 5
		; Create a command like this:
		; 7z t C:\Users\rick\Documents\opbm\scriptOutput\temp\7zipruntest.7z
		$filename		= chr(34) & GetScriptTempDirectory() & "7zipRunTest\7zipRunTest.zip" & chr(34) & " "
		$destination	= chr(34) & GetScriptTempDirectory() & "7zipRunTest\extractzip_" & $i & "\" & chr(34)
		If not $is386Executable Then
			$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_X64 & " e " & $filename & " -o" & chr(34) & $destination & chr(34)
		Else
			$cmd = $SEVENZIP_CMD_LINE_EXECUTABLE_I386 & " e " & $filename & " -o" & chr(34) & $destination & chr(34)
		EndIf

		$gPID = Run( $cmd, "C:\", @SW_SHOWMAXIMIZED )
		If ProcessWaitClose( $gPID, 60 ) <> 1 Then
			; An error occurred
			ErrorHandle( $ERROR_PREFIX & ": Unable to execute 7z command" ) ;Changed from $ERROR_TEXT to $ERROR_PREFIX -rcp 12/11/2011
		EndIf
	Next

	; Record ending time
	TimerEnd( $SEVENZIP_ZIP_UNARCHIVE_FIVE_TIMES )
EndFunc
