Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $EXE_DIRECTORY							= @ScriptDir & "\exe"

; Executables used to install, uninstall, or execute Chrome
Const $SEVENZIP_DIRECTORY_I386					= "C:\Program Files (x86)\7-zip"
Const $SEVENZIP_DIRECTORY_X64					= "C:\Program Files\7-zip"
Const $SEVENZIP_INSTALLER_I386					= $EXE_DIRECTORY						&	"\7z920-i386.exe"
Const $SEVENZIP_INSTALLER_X64					= "MsiExec.exe /I " & $EXE_DIRECTORY	&	"\7z920-x64.msi"
Const $SEVENZIP_UNINSTALLER_I386				= $SEVENZIP_DIRECTORY_I386				&	"\Uninstall.exe"
Const $SEVENZIP_UNINSTALLER_X64					= "MsiExec.exe /I{23170F69-40C1-2702-0920-000001000000}"
Const $SEVENZIP_EXECUTABLE_I386					= $SEVENZIP_DIRECTORY_I386	&	"\7zFM.exe"
Const $SEVENZIP_EXECUTABLE_X64					= $SEVENZIP_DIRECTORY_X64	&	"\7zFM.exe"
Const $SEVENZIP_EXECUTABLE_I386_TO_LAUNCH		= $SEVENZIP_DIRECTORY_I386	&	"\7zFM.exe " & $OPBM_SPLASH_ZIP
Const $SEVENZIP_EXECUTABLE_X64_TO_LAUNCH		= $SEVENZIP_DIRECTORY_X64	&	"\7zFM.exe " & $OPBM_SPLASH_ZIP
Const $SEVENZIP_CMD_LINE_EXECUTABLE_I386		= $SEVENZIP_DIRECTORY_I386	&	"\7z.exe"
Const $SEVENZIP_CMD_LINE_EXECUTABLE_X64			= $SEVENZIP_DIRECTORY_X64	&	"\7z.exe"

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
Const $SEVENZIP_INSTALLER_TITLE_X64				= "7-Zip 9.20 (x64 edition) Setup"
Const $SEVENZIP_UNINSTALLER_TITLE_X64			= "7-Zip 9.20 (x64 edition) Setup"
Const $SEVENZIP_INSTALLER_TITLE_I386			= "7-Zip 9.20 Setup"
Const $SEVENZIP_UNINSTALLER_TITLE_I386			= "7-Zip 9.20 Uninstall"
Const $SEVENZIP_UNARCHIVE_FIVE_TIMES			= "Unarchive to five separate directories"
Const $SEVENZIP_ARCHIVE_FIVE_DIRECTORIES		= "Archive five directories to new archive"
Const $SEVENZIP_TEST_ARCHIVE_INTEGRITY			= "Test archive integrity"


; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_SEVENZIP
$gBaselines[0][1] = $LAUNCH_SEVENZIP_SCORE
For $i = 1 to $gBaselineSize - 1
	$gBaselines[ $i ][0]	= "--unused--"
	$gBaselines[ $i ][1]	= 0.0
Next


;============================================================================================================================
;============================================================================================================================

Func is7ZipAlreadyInstalled()
	If FileExists( $SEVENZIP_EXECUTABLE_X64 ) Then
		return True
	ElseIf FileExists( $SEVENZIP_EXECUTABLE_I386 ) Then
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
	
	If FileExists( $SEVENZIP_EXECUTABLE_X64 ) Then
		$executable				= $SEVENZIP_EXECUTABLE_X64
		$executable_to_launch	= $SEVENZIP_EXECUTABLE_X64_TO_LAUNCH
		$is386Executable		= False
		
	ElseIf FileExists( $SEVENZIP_EXECUTABLE_I386 ) Then
		$executable				= $SEVENZIP_EXECUTABLE_I386
		$executable_to_launch	= $SEVENZIP_EXECUTABLE_I386_TO_LAUNCH
		$is386Executable		= True
		
	Else
		ErrorHandle("7-Zip not found in " & $SEVENZIP_EXECUTABLE_X64_TO_LAUNCH & " or " & $SEVENZIP_EXECUTABLE_I386_TO_LAUNCH & ", unable to launch.")
	EndIf
	outputDebug( "Attempting to launch " & $executable )
	
	TimerBegin()
	$gPID = Run( $executable_to_launch, "C:\", @SW_SHOWMAXIMIZED )
	opbmWaitUntilProcessIdle( $gPID, 5, 200, 10000 )
	opbmWinWaitActivate( $OPBM_SPLASH_ZIP_TITLE, "", 30 )
	TimerEnd( $LAUNCH_SEVENZIP )
EndFunc
