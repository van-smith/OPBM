#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 8.31.2011

	Description: AutoIT file containing common Microsoft Access 2010 data and functions

	Usage:	accessCommon is not directly exceutable

#ce ======================================================================================================================================
Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>
#include <office2010Common.au3>

Const $DIRECTORY_DATA					= @ScriptDir & "\Data"
Const $DIRECTORY_EXE_X86				= "C:\Program Files (x86)\Microsoft Office\OFFICE14"
Const $DIRECTORY_EXE_AMD64				= "C:\Program Files\Microsoft Office\OFFICE14"
Const $FILENAME_ACCDB					= $DIRECTORY_DATA & "\earthquake.accdb"
Const $FILENAME_TXT						= $DIRECTORY_DATA & "\earthquake.txt"
Const $FILENAME_PPTX_BACKUP				= $DIRECTORY_DATA & "\earthquake.backup"
Const $FILENAME_ACCESS_X86				= $DIRECTORY_EXE_X86 & "\MSACCESS.EXE"
Const $FILENAME_ACCESS_AMD64			= $DIRECTORY_EXE_AMD64 & "\MSACCESS.EXE"
Const $LAUNCH_MICROSOFT_ACCESS   		= "Launch Microsoft Access 2010"
Const $CLOSE_MICROSOFT_ACCESS			= "Close Microsoft Access 2010"

; accessEarthquake.au3
Const $ACCESS_COPY_EARTHQUAKE			= "Copy earthqauke database"
Const $ACCESS_OPEN_EARTHQUAKE			= "Open earthqauke database"
Const $ACCESS_EARTHQUAKE_QUERIES		= "Run earthquake queries"
Const $ACCESS_EARTHQUAKE_REPORTS		= "Run earthquake reports"
Const $ACCESS_COMPACT_EARTHQUAKE		= "Compact earthquake database"
Const $MICROSOFT_ACCESS					= "Microsoft Access"
Const $OPEN								= "Open"
Const $STATUS_BAR						= "Status Bar"

Dim $directoryOutput
Dim $filenameEarthquakeDb				= "earthquake.accdb"

; Setup references for timing items
Dim $gBaselineSize
$gBaselineSize = 20
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_MICROSOFT_ACCESS
$gBaselines[0][1] = $LAUNCH_MICROSOFT_ACCESS_SCORE
$gBaselines[1][0] = $CLOSE_MICROSOFT_ACCESS
$gBaselines[1][1] = $CLOSE_MICROSOFT_ACCESS_SCORE


Func initializeAccessScript( $retrieveProcess = 1)
	Opt("WinTitleMatchMode", 2)     ;1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	
	If $retrieveProcess = 1 Then
		$gPID = WinGetProcess ( $MICROSOFT_ACCESS )
		outputDebug( "Access PID: " & $gPID )
	EndIf
EndFunc

Func isAccessInstalled()
	Local $filename
	
	If FileExists( $FILENAME_ACCESS_AMD64 ) Then
		$filename = $FILENAME_ACCESS_AMD64
		outputDebug( "Running 64-bit Office" )
	ElseIf FileExists( $FILENAME_ACCESS_X86 ) Then
		$filename = $FILENAME_ACCESS_X86
		outputDebug( "Running 32-bit Office" )
	Else
		$filename = "not found"
	EndIf
	
	return $filename
EndFunc

Func launchAccess()
	Local $filename

	; Find out which version we're running
	$filename = isAccessInstalled()
	If $filename = "not found" Then
		ErrorHandle("Cannot launch application: Access 2010 not found at " & $FILENAME_ACCESS_AMD64 & " or " & $FILENAME_ACCESS_X86 & ".")
	EndIf

	; Opbm sets some registry keys at startup
	outputDebug( $SAVING_AND_SETTING_OFFICE_2010_REGISTRY_KEYS )
	Office2010SaveRegistryKeys()
	Office2010InstallRegistryKeys()

	; Attempt to launch the application
	outputDebug( "Attempting to launch " & $filename)
	TimerBegin()
	$gPID = Run($filename, "C:\", @SW_MAXIMIZE)
	opbmWinWaitActivate( $MICROSOFT_ACCESS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Access. Unable to find Window." )
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
	TimerEnd( $LAUNCH_MICROSOFT_ACCESS )
EndFunc

Func closeAccess()
	opbmWinWaitActivate( $MICROSOFT_ACCESS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Access. Unable to find Window." )
	TimerBegin()
	Send("!fx")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitClose( $MICROSOFT_ACCESS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Access: Window did not close." )
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
	TimerEnd( $CLOSE_MICROSOFT_ACCESS )

	outputDebug( $RESTORING_OFFICE_2010_REGISTRY_KEYS )
	Office2010RestoreRegistryKeys()
EndFunc

Func runQuery( $queryName )
	; go to search box
	Send( "^f" )
	opbmWaitUntilSystemIdle( 10, 100, 20000 )
	; select all text in search box
	Send( "^a" )
	opbmWaitUntilSystemIdle( 10, 100, 20000 )
	outputDebug( "Access is executing: " & $queryName )
	; enter the query
	Send( $queryName )
	Send( "{ENTER}" )
	opbmWaitUntilSystemIdle( 5, 100, 20000 )
	opbmWinWaitActivate( $MICROSOFT_ACCESS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Access. Unable to find Window." )
	; close the query window (this is necessary because it is not possible to reliably switch focus to the search box).
	Send( "^{F4}" )
EndFunc