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
Dim $Cores

; This script launches multiple instances of a Java jar file, and each
; instance contains all logic for timing events, percents, etc.  The
; gBaselines[] array is not used.

outputDebug( "Starting up Java Compute Test" )

if $CmdLine[0] > 0 then 
	$LoopLimit = $CmdLine[1] 
Else 
	$LoopLimit = 1
EndIf
For $CurrentLoop = 1 to $LoopLimit
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	outputDebug( "InitializeJava_Script()" )
	InitializeJava_Script()

; Added to test some functionality
;Sleep(12000)	; Give time to connect to debugger
;OpbmWatchdog_ProcessStart(0, 1)
;Exit
	; Grab the number of cores on this system
	$Cores = GetCoreCount(0)	; 0-physical cores, 1-logical cores (including hyperthreaded)
	If $Cores < 1 or $Cores > 32 Then
		ErrorHandle( "The core count is not correct, reported " & $Cores & " cores." )
	EndIf
	outputDebug( "Noted " & $Cores & " cores" )

	outputDebug( "LaunchJBM()" )
	LaunchJBM()

	outputDebug( "LaunchJvmInstances()" )
	LaunchJvmInstances()

	outputDebug( "WaitForJvmsToFinish()" )
	WaitForJvmsToFinish()

	outputDebug( "GatherAndReportJvmScoring()" )
	GatherAndReportJvmScoring()

	outputDebug( "ShutdownJBM()" )
	ShutdownJBM()
Next
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

Func LaunchJBM()
	Local $jbm_executable_to_launch
	
	; Launch the Java Benchmark Monitor
	$jbm_executable_to_launch	= $JBM_EXECUTABLE & " "	& $cores
	$gPID = Run( $jbm_executable_to_launch, "C:\", @SW_SHOW )
	If $gPID = 0 Then
		ErrorHandle( "Unable to launch the Java Benchmark Monitor using [" & $jbm_executable_to_launch & "]" )
	EndIf
	; The JBM is launched
	
	; Let it instantiate itself
	opbmWaitUntilSystemIdle( 10, 1000, 3000)
	
	; Try to connect to it
	If JbmOwnerReportingIn() <> 1 Then
		ShutdownJBM()
		opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
		ErrorHandle( "The Java Benchmark Monitor was launched, but this script was unable to connect to it." )
	EndIf
	; When we get here, the JBM is launched, and we've made a connection to it
EndFunc

Func LaunchJvmInstances()
	Local $i, $lPID
	Local $line1, $line2, $line3
	Local $echo_off
	Local $change_directory
	Local $executable
	Local $runme_dot_bat
	Local $run_silently_vbs
	Local $jbm_executable_to_launch
	Local $java_benchmark_instance_to_launch
	Local $file_handle

	; The following code launches each JVM, and assigns affinity to separate cores, beginning at 1 and continuing to the core count
	$run_silently_vbs	= GetScriptTempDirectory() & "runSilently.vbs"
	$runme_dot_bat		= GetScriptTempDirectory() & "runme.bat"
	
	; Create a file that will launch the runme.bat file silently:
	;		Set WshShell = CreateObject("WScript.Shell")
	;		WshShell.Run chr(34) & "c:\users\user name\documents\opbm\scriptOutput\temp\runme.bat" & chr(34), 0
	;		Set WshShell = Nothing
	; Begin
		$file_handle = FileOpen( $run_silently_vbs, 2 + 16 )	; Open in write mode, erase previous contents, force binary mode
		If $file_handle == -1 Then
			ShutdownJBM()
			opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
			ErrorHandle( "Unable to create a temporary file to launch a Java Benchmark" )
		EndIf
		$line1	= "Set WshShell = CreateObject(" & chr(34) & "WScript.Shell" & chr(34) & ")"
		$line2	= "WshShell.Run chr(34) & " & chr(34) & $runme_dot_bat & chr(34) & " & Chr(34), 0"
		$line3	= "Set WshShell = Nothing"
		FileWriteLine( $file_handle, $line1		& @CRLF )
		FileWriteLine( $file_handle, $line2		& @CRLF )
		FileWriteLine( $file_handle, $line3		& @CRLF )
		FileClose( $file_handle )
			
		outputDebug( "Populated to runSilently.vbs:" )
		outputDebug( $line1 )
		outputDebug( $line2 )
		outputDebug( $line3 )
	; End
	
	; Create a JVM instance for every core
;	Local $before = @WorkingDir
	FileChangeDir( ".\exe" )
;	If $before = @WorkingDir Then
;		; The directory was not changed (does not exist), which means we probably didn't start up in the correct directory, or the OPBM installation is damaged
;		; We should start up in autoIt\java\compute\, which should have an exe\ directory below it which contains benchmark.jar, benchmark32.dll and benchmark64.dll
;		
;		; Remember, we may need to put a message here for this error
;		; The error condition will be captured below when the start command fails
;		; so for now we just leave it alone
;	EndIf
	outputMessage( "In directory " & @WorkingDir )
	For $i = 0 to ($Cores - 1)
		; Pause to let the system idle down
		opbmWaitUntilSystemIdle( 10, 1000, 10000)
		
		; Create the batch file which has this type of information (adjusted to dynamic conditions of system):
		;		@echo off
		;		cd c:\cana\java\autoit\java\compute\exe\
		;		start /AFFINITY N /B benchmark.jar "JVM X of Y"
		; Begin
			$echo_off					= "@echo off"
			$change_directory			= "cd " & chr(34) & @WorkingDir & chr(34)
			$executable					= "start /AFFINITY " & StripLeadingZeros(Hex($i + 1)) & " /B benchmark.jar " & chr(34) & "JVM " & ($i + 1) & " of " & $Cores & chr(34)
			; Create the runme.bat file
			$file_handle = FileOpen( $runme_dot_bat, 2 + 16 )	; Open in write mode, erase previous contents, force binary mode
			If $file_handle == -1 Then
				ShutdownJBM()
				opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
				ErrorHandle( "Unable to create a temporary file to launch a Java Benchmark" )
			EndIf
			FileWriteLine( $file_handle, $echo_off			& @CRLF )
			FileWriteLine( $file_handle, $change_directory	& @CRLF )
			FileWriteLine( $file_handle, $executable		& @CRLF )
			FileClose( $file_handle )
		
			outputDebug( "Populated to runme.bat:" )
			outputDebug( $change_directory )
			outputDebug( $executable )
		; End
		
		outputMessage( "Launching [" & $executable & "] via [" & $run_silently_vbs & "]")
		$lPID = ShellExecute( $run_silently_vbs, "C:\", @SW_SHOW )
		If $lPID = 0 Then
			ShutdownJBM()
			opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
			ErrorHandle( "Unable to launch a Java Benchmark using [" & $run_silently_vbs & "]")
		EndIf
	Next
	; When we get here, every JVM has been launched
EndFunc

; This test should not take more than 4 minutes on the slowest machines
; So we add a timeout after 8 minutes
Func WaitForJvmsToFinish()
	Local $seconds
	Local $timeout
	Local $finished
	
	$timeout = 12 * 60
	outputDebug( "Will timeout in 12 minutes" )
	
	$finished = 0
	While $finished = 0 and $seconds < $timeout
		; Pause
		Sleep( 5000 )
		$seconds = $seconds + 5
		
		; See if they've all exited yet
		$finished = JbmOwnerHaveAllInstancesExited()
	WEnd
	
	If $seconds >= $timeout Then
		; Failure, the test did not end within the timeout period
		ShutdownJBM()
		opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
		outputError( "Failure to complete the test within the timeout period of 8 minutes." )
		Exit -1
	EndIf
	; If we get here, then all JVMs have exited
EndFunc

Func GatherAndReportJvmScoring()
	Local $subtest
	Local $status, $name, $timing, $scoring
	Local $maxtests
	
	; Gather all of the scores and report them
	$subtest = 1
	While $subtest > 0
		$status	= JbmOwnerRequestsSubtestMaxScoringData( $subtest )
		If $status = "failure" Then
			; Will be returned when we're reached the end of tests
			ExitLoop
		EndIf
		$name		= JbmOwnerRequestsName()
		$timing		= JbmOwnerRequestsAvgTiming()
		$scoring	= JbmOwnerRequestsAvgScoring() * $Cores
		outputTiming( $name & "," & $timing & "," & $scoring )
		$subtest = $subtest + 1
	WEnd
EndFunc

;Func GatherAndReportIndividualJvmFullScoring()
;	Local $jvm, $subtest
;	Local $status, $name, $timing, $scoring
;	Local $maxtests
;	
;	; Gather all of the scores for all JVMs and report everything about them
;	For $jvm = 0 to ($Cores - 1)
;		$subtest = 1
;		While $subtest > 0
;			$status	= JbmOwnerRequestsSubtestScoringData( $jvm, $subtest )
;			If $status = "failure" Then
;				; Will be returned when we're reached the end of tests
;				ExitLoop
;			EndIf
;			; Report the line as "JVM_N_TestName,timing,score"
;			$name		= "JVM_" & ($jvm + 1) & "_" & JbmOwnerRequestsName()
;			$timing		= JbmOwnerRequestsAvgTiming()
;			$scoring	= JbmOwnerRequestsAvgScoring() * $Cores
;			outputTiming( $name & "," & $timing & "," & $scoring )
;
;			; Add debug information for timing min, max, avg, geo and cv
;			outputDebug( "Full timing data for JVM " & ($jvm + 1) )
;			outputDebug( "  Min: " & JbmOwnerRequestsMinTiming() )
;			outputDebug( "  Max: " & JbmOwnerRequestsMaxTiming() )
;			outputDebug( "  Avg: " & JbmOwnerRequestsAvgTiming() )
;			outputDebug( "  Geo: " & JbmOwnerRequestsGeoTiming() )
;			outputDebug( "   CV: " & JbmOwnerRequestsCVTiming() )
;			
;			; Add debug information for scoring min, max, avg, geo and cv
;			outputDebug( "Full scoring data for JVM " & ($jvm + 1) )
;			outputDebug( "  Min: " & JbmOwnerRequestsMinScoring() )
;			outputDebug( "  Max: " & JbmOwnerRequestsMaxScoring() )
;			outputDebug( "  Avg: " & JbmOwnerRequestsAvgScoring() )
;			outputDebug( "  Geo: " & JbmOwnerRequestsGeoScoring() )
;			outputDebug( "   CV: " & JbmOwnerRequestsCVScoring() )
;			
;			$subtest = $subtest + 1
;		WEnd
;	Next
;EndFunc

Func ShutdownJBM()
	JbmOwnerRequestsTheJbmSelfTerminate()
EndFunc
