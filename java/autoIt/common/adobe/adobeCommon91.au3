Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $EXE_DIRECTORY						= @ScriptDir & "\exe"

; Default physical installation directory from Chrome 12.0.742.122 installer
Const $ACROBAT_READER_DIRECTORY				= "C:\Program Files (x86)\Adobe\Reader 9.0\Reader"

; Executables used to install, uninstall, or execute Chrome
Const $ACROBAT_READER_INSTALLER				= $EXE_DIRECTORY				&	"\AdbeRdr910_en_US.exe"
Const $ACROBAT_READER_UNINSTALLER			= $EXE_DIRECTORY				&	"\AdbeRdr910_en_US.exe"
Const $ACROBAT_READER_EXECUTABLE			= $ACROBAT_READER_DIRECTORY		&	"\AcroRd32.exe"
Const $ACROBAT_READER_EXECUTABLE_TO_LAUNCH	= $ACROBAT_READER_DIRECTORY		&	"\AcroRd32.exe"


; Constants used throughout for various scripts
Const $LAUNCH_ACROBAT_READER				= "Launch Adobe Acrobat 9.0"
Const $CLOSE_ACROBAT_READER					= "Close Adobe Acrobat 9.0"
Const $ACROBAT_READER_INSTALLER_WINDOW		= "Adobe Reader 9.1 - Setup"
Const $ACROBAT_READER_WINDOW				= "Adobe Reader"

Const $LAUNCH_ACROBAT_READER_INSTALLER		= "Launch Adobe Acrobat 9.0 Installer"
Const $LAUNCH_ACROBAT_READER_UNINSTALLER	= "Launch Adobe Acrobat 9.0 Un-installer"
Const $INSTALL_ACROBAT_READER				= "Install Adobe Acrobat 9.0"
Const $UNINSTALL_ACROBAT_READER				= "Un-install Adobe Acrobat 9.0"
Const $CLOSE_UNINSTALLER					= "Close Un-installer"
Const $READY_TO_INSTALL_THE_PROGRAM			= "Ready to Install the Program"
Const $SETUP_COMPLETED						= "Setup Completed"
Const $CLICK_NEXT_TO_INSTALL				= "Click next to install"
Const $DESTINATION_FOLDER					= "Destination Folder"
Const $WELCOME_TO_SETUP_FOR_ADOBE_READER_91	= "Welcome to Setup for Adobe Reader 9.1"
Const $PROGRAM_MAINTENANCE					= "Program Maintenance"
Const $REMOVE_THE_PROGRAM					= "Remove the Program"
Const $PREFERENCES							= "Preferences"
Const $ACROBAT_READER_LICENSE_AGREEMENT		= "Adobe Reader - License Agreement"

; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_ACROBAT_READER
$gBaselines[0][1] = 0.665446095369494
$gBaselines[1][0] = $CLOSE_ACROBAT_READER
$gBaselines[1][1] = 0.85075427679012
For $i = 2 to $gBaselineSize - 1
	$gBaselines[ $i ][0]	= "--unused--"
	$gBaselines[ $i ][1]	= 0.0
Next


;============================================================================================================================
;============================================================================================================================


Func isAcrobatReaderAlreadyInstalled()
	If FileExists( $ACROBAT_READER_EXECUTABLE ) Then
		return True
	EndIf	
	return False
EndFunc

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
