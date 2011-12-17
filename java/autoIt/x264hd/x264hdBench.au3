#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 11.29.2011

	Description: Atom to run the x264HD benchmark

	Usage:	x264hdBenchmark

#ce ======================================================================================================================================

Const $ROOT_DIR = @ScriptDir & "\.."

#include <..\common\opbm\opbmCommon.au3>

Dim $directoryOutput
Dim $directoryX264Benchmark = "x264_Benchmark_HD"
Dim $filenameRunX264Benchmark

Const $DIRECTORY_SOURCE_X264				= @ScriptDir & "\data\" & $directoryX264Benchmark
Const $FILENAME_RUN_BENCHMARK				= "run_opbm_x264hd_bench.bat"

Const $COPY_X264_FILES						= "Copy x264hd files"
;Const $COPY_X264_FILES_SCORE				= 0.714107504			;Moved to baselineScores 2011_12_16 -rcp  0.07
Const $RUN_X264HD_BENCHMARK					= "Run x264hd benchmark"
;Const $RUN_X264HD_BENCHMARK_SCORE			= 243.8252516			;Moved to baselineScores 2011_12_16 -rcp  246

Const $CPU_USAGE_THRESHOLD 					= 5 ; percent
Const $CPU_USAGE_THRESHOLD_TIME				= 100 ; milliseconds
Const $CPU_USAGE_THRESHOLD_TIMEOUT			= 5000 ; milliseconds

Dim $gBaselineSize = 20
Dim $gBaselines[ $gBaselineSize ][ 2 ]
$gBaselines[ 0 ][ 0 ] = $COPY_X264_FILES
$gBaselines[ 0 ][ 1 ] = $COPY_X264_FILES_SCORE
$gBaselines[ 1 ][ 0 ] = $RUN_X264HD_BENCHMARK
$gBaselines[ 1 ][ 1 ] = $RUN_X264HD_BENCHMARK_SCORE

outputDebug( "initializeX264hdBenchScript" )
initializeX264hdBenchScript()

outputDebug( "runX264hdBenchmark" )
runX264Benchmark()

Exit

Func initializeX264hdBenchScript()
	Opt("WinTitleMatchMode", 2)     ;1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")

	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()

	$directoryOutput	= GetScriptTempDirectory()
	$directoryX264Benchmark	= $directoryOutput & "\" & $directoryX264Benchmark
	$filenameRunX264Benchmark = $directoryX264Benchmark & "\" & $FILENAME_RUN_BENCHMARK
	; Delete the document in case it wasn't deleted last time
	DirRemove( $directoryX264Benchmark, 1 )
	; copy the database file:
	;TimerBegin()	;Removed due to inconsistent behavior -rcp 11/17/2011
	$gErrorTrap = DirCopy( $DIRECTORY_SOURCE_X264, $directoryX264Benchmark, 1)
	If $gErrorTrap = 0 Then ErrorHandle($ERROR_PREFIX & "DirCopy: " & $DIRECTORY_SOURCE_X264 & ": Unable to copy x264hd source directory.")
	;TimerEnd( $COPY_X264_FILES )	;Removed due to inconsistent behavior -rcp 11/17/2011
EndFunc

Func runX264Benchmark()
	TimerBegin()
	$gErrorTrap = RunWait( $filenameRunX264Benchmark, $directoryX264Benchmark, @SW_MAXIMIZE )
	outputDebug( "RunWait returned " & $gErrorTrap )
	; If $gErrorTrap = 0 Then ErrorHandle($ERROR_PREFIX & $RUN_X264HD_BENCHMARK & ": Run x264hd benchmark returned an error.")
	TimerEnd( $RUN_X264HD_BENCHMARK )
EndFunc