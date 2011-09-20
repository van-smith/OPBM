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
Const $FILENAME_PPTX					= $DIRECTORY_DATA & "\theComingWar.pptx"
Const $FILENAME_PPTX_BACKUP				= $DIRECTORY_DATA & "\theComingWar.backup"
Const $FILENAME_POWERPOINT_X86			= $DIRECTORY_EXE_X86 & "\POWERPNT.EXE"
Const $FILENAME_POWERPOINT_AMD64		= $DIRECTORY_EXE_AMD64 & "\POWERPNT.EXE"
Const $LAUNCH_MICROSOFT_POWERPOINT   	= "Launch Microsoft PowerPoint 2010"
Const $CLOSE_MICROSOFT_POWERPOINT		= "Close Microsoft PowerPoint 2010"

; powerpointWar.au3
Const $WAR_CLOSE_EMPTY_PRESENTATION    	= "Close empty presentation"
Const $WAR_OPEN_PRESENTATION			= "Open War presentation"
Const $WAR_SAVE_AND_CLOSE_PPTX			= "Save and close War presentation"
Const $WAR_CREATE_WMV					= "Create War WMV"
Const $WAR_PLAY_PRESENTATION			= "Time to play War presentation"
Const $MICROSOFT_POWERPOINT				= "Microsoft PowerPoint"
Const $NEW_PRESENTATION					= "Presentation1"
Const $OPEN								= "Open"
Const $SAVE_AS							= "Save As"
Const $STATUS_BAR						= "Status Bar"

Dim $directoryOutput
Dim $filenameWarPptx					= "theComingWar.pptx"
Dim $filenameWarWmv						= "theComingWar.wmv"
; Setup references for timing items
Dim $gBaselineSize
$gBaselineSize = 20
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_MICROSOFT_POWERPOINT
$gBaselines[0][1] = $LAUNCH_MICROSOFT_POWERPOINT_SCORE
$gBaselines[1][0] = $CLOSE_MICROSOFT_POWERPOINT
$gBaselines[1][1] = $CLOSE_MICROSOFT_POWERPOINT_SCORE


Func launchPowerPoint()
	Local $filename

	; Find out which version we're running
	If FileExists( $FILENAME_POWERPOINT_AMD64 ) Then
		$filename = $FILENAME_POWERPOINT_AMD64
		outputDebug( "Running 64-bit Office" )
	ElseIf FileExists( $FILENAME_POWERPOINT_X86 ) Then
		$filename = $FILENAME_POWERPOINT_X86
		outputDebug( "Running 32-bit Office" )
	Else
		ErrorHandle("Cannot launch application: PowerPoint 2010 not found at " & $FILENAME_POWERPOINT_AMD64 & " or " & $FILENAME_POWERPOINT_X86 & ".")
	EndIf

	; Opbm sets some registry keys at startup
	outputDebug( $SAVING_AND_SETTING_OFFICE_2010_REGISTRY_KEYS )
	;Office2010SaveRegistryKeys()
	;Office2010InstallRegistryKeys()

	; Attempt to launch the application
	outputDebug( "Attempting to launch " & $filename)
	TimerBegin()
	$gPID = Run($filename, "C:\", @SW_MAXIMIZE)
	opbmWinWaitActivate( $MICROSOFT_POWERPOINT, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft PowerPoint. Unable to find Window." )
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
	TimerEnd( $LAUNCH_MICROSOFT_POWERPOINT )
EndFunc

Func closePowerPoint()
	opbmWinWaitActivate( $MICROSOFT_POWERPOINT, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft PowerPoint. Unable to find Window." )
	TimerBegin()
	Send("!fx")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitClose( $MICROSOFT_POWERPOINT, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft PowerPoint: Window did not close." )
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
	TimerEnd( $CLOSE_MICROSOFT_POWERPOINT )

	outputDebug( $RESTORING_OFFICE_2010_REGISTRY_KEYS )
	;Office2010RestoreRegistryKeys()
EndFunc

Func initializePowerPointScript()
	Opt("WinTitleMatchMode", 2)     ;1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	$gPID = WinGetProcess ( $MICROSOFT_POWERPOINT )
EndFunc
