#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=fileioTest.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written September 2011 by:
; --------------------------
;	Van Smith
;
; Usage:	fileioTest [ repeat_count ]
;
;======================================================================================================================================
#include <../../common/fileio/fileioCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

outputDebug( "Starting File I/O Create and Copy Test" )

if $CmdLine[0] > 0 then 
	$LoopLimit = $CmdLine[1] 
Else 
	$LoopLimit = 1
EndIf
For $CurrentLoop = 1 to $LoopLimit
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	; Start script timer
	$gScriptBeginTime = TimerInit()
	
	RunTest()

	outputDebug( "FinalizeScript()" )
	opbmFinalizeScript( "copyTestTimes.csv" )
Next
Exit

;======================================================================================================================================
;======================================================================================================================================

Func RunTest()
	Local $i
	Local $j
	Local $start
	Local $diff
	Local $createDiff
	Local $createTime
	Local $copyTime
	Local $directorySrc
	Local $directoryDst
	Local $fileSrc
	Local $fileDst
	Local $handle
	
	$createTime = 0
	$copyTime = 0
	For $i = 0 to 9
		; Create one hundred 10MB files
		outputDebug( "Iteration " & ($i + 1) & " of 10" )
		$start	= TimerInit()
		For $j = 0 to 99
			$directorySrc	= GetScriptTempDirectory() & "src" & $i & "\"
			$fileSrc		= $directorySrc & "fileSrc" & StringFormat( "%02u", $j ) & ".bin"
			$handle = FileOpen( $fileSrc, 2 + 8 + 16 )		; Create in (16)binary (2)write mode, (8)create directory structure
			If $handle = -1 Then
				; Clean up what we've done so far
				CleanUp( $i )
				ErrorHandle( "Unable to create " & $fileSrc )
			EndIf
			FileSetPos( $handle, 10239999, 2 )				; Seek to 10MB - 1
			; Use this lesser value for testing
			; FileSetPos( $handle, 1023, 2 ) 				; Seek to 1KB - 1
			FileWrite( $handle, " " )						; Write a single byte
			FileClose( $handle )
		Next
		$diff		= TimerDiff( $start )
		$createTime	+= $diff
		$createDiff	= $diff
		
		; Copy newly created one hundred 10MB files to another location
		$start	= TimerInit()
		For $j = 0 to 99
			$directorySrc	= GetScriptTempDirectory() & "src" & $i & "\"
			$directoryDst	= GetScriptTempDirectory() & "dst" & $i & "\"
			$fileSrc		= $directorySrc & "fileSrc" & StringFormat( "%02u", $j ) & ".bin"
			$fileDst		= $directoryDst& "fileDst" & StringFormat( "%02u", $j ) & ".bin"
			If FileCopy( $fileSrc, $fileDst, 1 + 8 ) = 0 Then
				; Failure copying
				CleanUp( $i )
				ErrorHandle( "Unable to copy " & $fileSrc & " to " & $fileDst )
			EndIF
		Next
		$diff	= TimerDiff( $start )
		$copyTime += $diff
		outputDebug( "Create time " & $createDiff / 1000 & ", Copy time " & $diff / 1000 )
	Next
	TimerRecord( $FILEIO_CREATE_FILES, $createTime )
	TimerRecord( $FILEIO_COPY_FILES, $copyTime )
	CleanUp( $i )
EndFunc


Func CleanUp( $max )
	Local $i
	Local $directorySrc
	Local $directoryDst
	
	outputDebug( "Cleaning up temporary files" )
	For $i = 0 to $max
		$directorySrc = GetScriptTempDirectory() & "src" & $i & "\"
		$directoryDst = GetScriptTempDirectory() & "dst" & $i & "\"
		DirRemove( $directorySrc, 1 )
		DirRemove( $directoryDst, 1 )
	Next
EndFunc
