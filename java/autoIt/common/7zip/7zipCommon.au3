Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $EXE_DIRECTORY							= @ScriptDir & "\exe"

; Executables used to install, uninstall, or execute Chrome
Const $SEVENZIP_DIRECTORY_I386					= "C:\Program Files (x86)\7zip"
Const $SEVENZIP_DIRECTORY_X64					= "C:\Program Files\7zip"
Const $SEVENZIP_INSTALLER						= $EXE_DIRECTORY		&	"\7z.exe"
Const $SEVENZIP_UNINSTALLER						= "MsiExec.exe /I{AC76BA86-7AD7-1033-7B44-AA1000000001}"
Const $SEVENZIP_EXECUTABLE_I386					= $SEVENZIP_DIRECTORY_I386	&	"\7zFM.exe"
Const $SEVENZIP_EXECUTABLE_I386_TO_LAUNCH		= $SEVENZIP_DIRECTORY_I386	&	"\7zFM.exe"
Const $SEVENZIP_EXECUTABLE_X64					= $SEVENZIP_DIRECTORY_X64	&	"\7zFM.exe"
Const $SEVENZIP_EXECUTABLE_X64_TO_LAUNCH		= $SEVENZIP_DIRECTORY_X64	&	"\7zFM.exe"

; Constants used throughout for various scripts
Const $LAUNCH_SEVENZIP							= "Launch 7-Zip"
Const $CLOSE_SEVENZIP							= "Close 7-Zip"
Const $SEVENZIP_INSTALLER_WINDOW				= "7-Zip - Setup"
Const $SEVENZIP_WINDOW							= "7-Zip"

Const $LAUNCH_SEVENZIP_INSTALLER				= "Launch 7-Zip Installer"
Const $LAUNCH_SEVENZIP_UNINSTALLER				= "Launch 7-Zip Un-installer"
Const $INSTALL_SEVENZIP							= "Install 7-Zip"
Const $UNINSTALL_SEVENZIP						= "Un-install 7-Zip"
Const $CLOSE_UNINSTALLER						= "Close Un-installer"
Const $READY_TO_INSTALL							= "Ready to Install 7-Zip"
Const $SETUP_COMPLETED							= "Setup Completed"
Const $SEVENZIP_IS_NOT_INSTALLED				= "7-Zip is not installed"

; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_SEVENZIP
$gBaselines[0][1] = $LAUNCH_SEVENZIP_SCORE
$gBaselines[1][0] = $CLOSE_SEVENZIP
$gBaselines[1][1] = $CLOSE_SEVENZIP_SCORE
For $i = 2 to $gBaselineSize - 1
	$gBaselines[ $i ][0]	= "--unused--"
	$gBaselines[ $i ][1]	= 0.0
Next


;============================================================================================================================
;============================================================================================================================

Func is7ZipAlreadyInstalled()
	If FileExists( $SEVENZIP_EXECUTABLE ) Then
		return True
	EndIf	
	return False
EndFunc

Func Initialize7ZipScript()
	Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
EndFunc

Func Launch7Zip()
	Local $executable, $executable_to_launch
	
	If is64BitOS() Then
		$executable				= $SEVENZIP_EXECUTABLE_X64
		$executable_to_launch	= $SEVENZIP_EXECUTABLE_X64_TO_LAUNCH
	Else
		$executable				= $SEVENZIP_EXECUTABLE_I386
		$executable_to_launch	= $SEVENZIP_EXECUTABLE_I386_TO_LAUNCH
	EndIf
	outputDebug( "Attempting to launch " & $executable )
	
	TimerBegin()
	$gPID = Run( $executable_to_launch, "C:\", @SW_SHOWMAXIMIZED )
	opbmWaitUntilProcessIdle( $gPID, 5, 200, 10000 )
	TimerEnd( $LAUNCH_SEVENZIP )
EndFunc

Func Close7Zip( $title )
	; Bring the window foreground (if it's not)
	opbmWinWaitActivate( $title, "", $gTimeout, $ERROR_PREFIX & "WinWait: 7-Zip: Unable to find Window.")
	
	; Start the timer
	TimerBegin()
	
	; Close it
	WinActivate( $title )
	WinClose( $title )
	
	; Wait until the sytem settles down
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
	
	; Take the ending timer
	TimerEnd( $CLOSE_SEVENZIP )
EndFunc
