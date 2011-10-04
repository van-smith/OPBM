#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=javaCompute.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written October 2011 by:
; ------------------------
;	Van Smith
;
; Usage:	javaCompute
;
;======================================================================================================================================
#include <../../common/java/javaCommon.au3>


Dim $CurrentLoop
Dim $LoopLimit

;$gBaselines[2][0] = $TYPE_SUNSPIDER_URL
;$gBaselines[2][1] = $CHROME_TYPE_SUNSPIDER_URL_SCORE
;$gBaselines[3][0] = $RUN_SUNSPIDER
;$gBaselines[3][1] = $CHROME_RUN_SUNSPIDER_SCORE

outputDebug( "Starting up Java Compute Test" )

if $CmdLine[0] > 0 then 
	$LoopLimit = $CmdLine[1] 
Else 
	$LoopLimit = 1
EndIf
For $CurrentLoop = 1 to $LoopLimit
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	outputDebug( "InitializeJavaScript()" )
	InitializeJavaScript()
	
	outputDebug( "LaunchJBM()" )
	LaunchJBM()
Next
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

Func LaunchJBM()
	Local $i, $lPID, $cores, $java_path
	Local $line1, $line2, $line3
	Local $echo_off
	Local $change_directory
	Local $executable
	Local $runme_dot_bat
	Local $run_silently_vbs
	Local $jbm_executable_to_launch
	Local $java_benchmark_instance_to_launch
	Local $file_handle
	
	; Grab the number of cores on this system
	$cores = GetCoreCount()
	$cores = 20
	If $cores < 1 or $cores > 32 Then
		ErrorHandle( "The core count is not correct, reported " & $cores & " cores." )
	EndIf
	
	; Launch the Java Benchmark Monitor
	$jbm_executable_to_launch	= $JBM_EXECUTABLE & " "	& $cores
	$gPID = Run( $jbm_executable_to_launch, "C:\", @SW_SHOW )
	If $gPID = 0 Then
		ErrorHandle( "Unable to launch the Java Benchmark Monitor using [" & $jbm_executable_to_launch & "]")
	EndIf
	; The JBM is launched
	; The pause below will allow it to instantiate itself
	
	$run_silently_vbs	= GetScriptTempDirectory() & "runSilently.vbs"
	$runme_dot_bat		= GetScriptTempDirectory() & "runme.bat"
	$file_handle = FileOpen( $run_silently_vbs, 2 + 16 )	; Open in write mode, erase previous contents, force binary mode
	If $file_handle == -1 Then
		ErrorHandle( "Unable to create a temporary file to launch a Java Benchmark" )
	EndIf
	$line1	= "Set WshShell = CreateObject(" & chr(34) & "WScript.Shell" & chr(34) & ")"
	$line2	= "WshShell.Run chr(34) & " & chr(34) & $runme_dot_bat & chr(34) & " & Chr(34), 0"
	$line3	= "Set WshShell = Nothing"
	FileWriteLine( $file_handle, $line1		& @CRLF )
	FileWriteLine( $file_handle, $line2		& @CRLF )
	FileWriteLine( $file_handle, $line3		& @CRLF )
	FileClose( $file_handle )
	
	; Create a JVM instance for every core
	FileChangeDir( ".\exe" )
	outputMessage( "In directory " & @WorkingDir )
	;$java_path = "c:\program files\java\jre7\bin\java.exe"
	For $i = 0 to ($cores - 1)
		; Pause to let the idleness wear out
		opbmWaitUntilSystemIdle( 10, 1000, 3000)
		; Create a file that has this information:
		;	@echo off
		;	cd c:\cana\java\autoit\java\compute\exe\
		;	start /AFFINITY N /B "c:\program files\java\jre7\bin\java.exe" -jar benchmark.jar "JVM X of Y"
		;		OR:
		;	start /AFFINITY N /B benchmark.jar "JVM X of Y"
		$echo_off					= "@echo off"
		$change_directory			= "cd " & @WorkingDir
		;$executable				= "start /AFFINITY " & ($i + 1) & " /B " & chr(34) & $java_path & chr(34) & " -jar benchmark.jar " & chr(34) & "JVM " & ($i + 1) & " of " & $cores & chr(34)
		$executable					= "start /AFFINITY " & ($i + 1) & " /B benchmark.jar " & chr(34) & "JVM " & ($i + 1) & " of " & $cores & chr(34)
		
		; Create the runme.bat file
		$file_handle = FileOpen( $runme_dot_bat, 2 + 16 )	; Open in write mode, erase previous contents, force binary mode
		If $file_handle == -1 Then
			ErrorHandle( "Unable to create a temporary file to launch a Java Benchmark" )
		EndIf
		FileWriteLine( $file_handle, $echo_off			& @CRLF )
		FileWriteLine( $file_handle, $change_directory	& @CRLF )
		FileWriteLine( $file_handle, $executable		& @CRLF )
		FileClose( $file_handle )
		
		outputMessage( "Launching [" & $executable & "]")
		$lPID = ShellExecute( $run_silently_vbs, "C:\", @SW_SHOW )
		If $lPID = 0 Then
			ErrorHandle( "Unable to launch a Java Benchmark using [" & $run_silently_vbs & "]")
		EndIf
	Next
	ProcessWaitClose( $gPID )
EndFunc
