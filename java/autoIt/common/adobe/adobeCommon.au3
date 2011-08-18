Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $EXE_DIRECTORY							= @ScriptDir & "\exe"

; Executables used to install, uninstall, or execute Chrome
#include "adobeVersion.au3"

; Constants used throughout for various scripts
Const $LAUNCH_ACROBAT_READER					= "Launch Adobe Acrobat 10.1"
Const $CLOSE_ACROBAT_READER						= "Close Adobe Acrobat 10.1"
Const $ACROBAT_READER_INSTALLER_WINDOW			= "Adobe Reader X (10.1.0) - Setup"
Const $ACROBAT_READER_WINDOW					= "Adobe Reader"

Const $LAUNCH_ACROBAT_READER_INSTALLER			= "Launch Adobe Acrobat 10.1 Installer"
Const $LAUNCH_ACROBAT_READER_UNINSTALLER		= "Launch Adobe Acrobat 10.1 Un-installer"
Const $INSTALL_ACROBAT_READER					= "Install Adobe Acrobat 10.1"
Const $UNINSTALL_ACROBAT_READER					= "Un-install Adobe Acrobat 10.1"
Const $CLOSE_UNINSTALLER						= "Close Un-installer"
Const $READY_TO_INSTALL							= "Ready to Install Adobe Reader"
Const $SETUP_COMPLETED							= "Setup Completed"
Const $CLICK_NEXT_TO_INSTALL					= "Click next to install"
Const $DESTINATION_FOLDER						= "Destination Folder"
Const $WELCOME_TO_SETUP_FOR_ADOBE_READER_101	= "Welcome to Setup for Adobe Reader 10.1"
Const $PROGRAM_MAINTENANCE						= "Program Maintenance"
Const $REMOVE_THE_PROGRAM						= "Remove the Program"
Const $PREFERENCES								= "Preferences"
Const $ACROBAT_READER_LICENSE_AGREEMENT			= "Adobe Reader X - Distribution License Agreement"
Const $ADOBE_READER_X_MAINTENANCE				= "Adobe Reader X (10.1.0) Maintenance"

; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_ACROBAT_READER
$gBaselines[0][1] = $LAUNCH_ACROBAT_READER_SCORE
$gBaselines[1][0] = $CLOSE_ACROBAT_READER
$gBaselines[1][1] = $CLOSE_ACROBAT_READER_SCORE
For $i = 2 to $gBaselineSize - 1
	$gBaselines[ $i ][0]	= "--unused--"
	$gBaselines[ $i ][1]	= 0.0
Next


;============================================================================================================================
;============================================================================================================================


Func InitializeAdobeScript()
	Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
EndFunc

Func LaunchAcrobatReader()
	outputDebug( "Attempting to launch " & $ACROBAT_READER_EXECUTABLE )
	
	TimerBegin()
	$gPID = Run( $ACROBAT_READER_EXECUTABLE_TO_LAUNCH, "C:\", @SW_SHOWMAXIMIZED )
	opbmWaitUntilProcessIdle( $gPID, 5, 100, 5000 )
	opbmWinWaitActivate( $ACROBAT_READER_WINDOW )
	TimerEnd( $LAUNCH_ACROBAT_READER )
	
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc

Func CloseAcrobatReader( $title )
	; Bring the window foreground (if it's not)
	opbmWinWaitActivate( $title, "", $gTimeout, $ERROR_PREFIX & "WinWait: Adobe Acrobat: Unable to find Window.")
	
	; Start the timer
	TimerBegin()
	
	; Close it
	WinActivate( $title )
	WinClose( $title )
	
	; Wait until the sytem settles down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
	
	; Take the ending timer
	TimerEnd( $CLOSE_ACROBAT_READER )
EndFunc
