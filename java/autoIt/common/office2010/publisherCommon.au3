#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 8.31.2011

	Description: AutoIT file containing common Microsoft Publisher 2010 data and functions
	
	Usage:	accessCommon is not directly exceutable

#ce ======================================================================================================================================
Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>
#include <office2010Common.au3>

Const $DIRECTORY_DATA					= @ScriptDir & "\Data"
Const $DIRECTORY_EXE_X86				= "C:\Program Files (x86)\Microsoft Office\OFFICE14"
Const $DIRECTORY_EXE_AMD64				= "C:\Program Files\Microsoft Office\OFFICE14"
Const $FILENAME_PUB						= $DIRECTORY_DATA & "\hedgeFlyer.pub"
Const $FILENAME_PUB_BACKUP				= $DIRECTORY_DATA & "\hedgeFlyer.backup"
Const $FILENAME_PUBLISHER_X86			= $DIRECTORY_EXE_X86 & "\MSPUB.EXE"
Const $FILENAME_PUBLISHER_AMD64			= $DIRECTORY_EXE_AMD64 & "\MSPUB.EXE"
Const $LAUNCH_MICROSOFT_PUBLISHER   	= "Launch Microsoft Publisher 2010"
Const $CLOSE_MICROSOFT_PUBLISHER		= "Close Microsoft Publisher 2010"

; accessEarthquake.au3
Const $HEDGE_OPEN_FLYER					= "Open HEDGE flyer"
Const $HEDGE_SAVE_AND_CLOSE_FLYER		= "Save and close HEDGE flyer"
Const $MICROSOFT_PUBLISHER				= "Microsoft Publisher"
Const $OPEN								= "Open"
Const $STATUS_BAR						= "Status Bar"

; Setup references for timing items
Dim $gBaselineSize
$gBaselineSize = 20
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_MICROSOFT_PUBLISHER
$gBaselines[0][1] = $LAUNCH_MICROSOFT_PUBLISHER_SCORE
$gBaselines[1][0] = $CLOSE_MICROSOFT_PUBLISHER
$gBaselines[1][1] = $CLOSE_MICROSOFT_PUBLISHER_SCORE


Func launchPublisher()
	Local $filename
	
	; Find out which version we're running
	If FileExists( $DIRECTORY_EXE_AMD64 ) Then
		$filename = $FILENAME_PUBLISHER_AMD64
		outputDebug( "Running 64-bit Office" )
	ElseIf FileExists( $DIRECTORY_EXE_X86 ) Then
		$filename = $FILENAME_PUBLISHER_X86
		outputDebug( "Running 32-bit Office" )
	Else
		ErrorHandle("Cannot launch application: Publisher 2010 not found at " & $FILENAME_PUBLISHER_AMD64 & " or " & $FILENAME_PUBLISHER_X86 & ".")
	EndIf
	
	; Opbm sets some registry keys at startup
	outputDebug( $SAVING_AND_SETTING_OFFICE_2010_REGISTRY_KEYS )
	;Office2010SaveRegistryKeys()
	;Office2010InstallRegistryKeys()

	; Attempt to launch the application
	outputDebug( "Attempting to launch " & $filename)
	TimerBegin()
	$gPID = Run($filename, "C:\", @SW_MAXIMIZE)
	opbmWinWaitActivate( $MICROSOFT_PUBLISHER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Window." )
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
	TimerEnd( $LAUNCH_MICROSOFT_PUBLISHER )
EndFunc

Func closePublisher()
	opbmWinWaitActivate( $MICROSOFT_PUBLISHER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher. Unable to find Window." )
	TimerBegin()
	Send("!fx")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitClose( $MICROSOFT_PUBLISHER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Publisher: Window did not close." )
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
	TimerEnd( $CLOSE_MICROSOFT_PUBLISHER )
	
	outputDebug( $RESTORING_OFFICE_2010_REGISTRY_KEYS )
	;Office2010RestoreRegistryKeys()
EndFunc

Func initializePublisherScript()
	Opt("WinTitleMatchMode", 2)     ;1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()	
	$gPID = WinGetProcess ( $MICROSOFT_PUBLISHER )		
	outputDebug( "Publisher PID: " & $gPID )
EndFunc
