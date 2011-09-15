Dim $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

; Relative locations for content
Const $EXE_DIRECTORY							= @ScriptDir & "\exe"

; Executables used to install, uninstall, or execute Chrome
Const $SEVENZIP_DIRECTORY_I386					= "C:\Program Files (x86)\7-zip"
Const $SEVENZIP_DIRECTORY_X64					= "C:\Program Files\7-zip"
Const $SEVENZIP_INSTALLER_I386					= $EXE_DIRECTORY						&	"\7z920-i386.exe"
Const $SEVENZIP_INSTALLER_X64					= "MsiExec.exe /I " & $EXE_DIRECTORY	&	"\7z920-x64.msi /passive"
Const $SEVENZIP_UNINSTALLER_I386				= $SEVENZIP_DIRECTORY_I386				&	"\Uninstall.exe"
Const $SEVENZIP_UNINSTALLER_X64					= "MsiExec.exe /X{23170F69-40C1-2702-0920-000001000000} /passive"
Const $SEVENZIP_EXECUTABLE_I386					= $SEVENZIP_DIRECTORY_I386	&	"\7zFM.exe"
Const $SEVENZIP_EXECUTABLE_X64					= $SEVENZIP_DIRECTORY_X64	&	"\7zFM.exe"
Const $SEVENZIP_EXECUTABLE_I386_TO_LAUNCH		= $SEVENZIP_DIRECTORY_I386	&	"\7zFM.exe "
Const $SEVENZIP_EXECUTABLE_X64_TO_LAUNCH		= $SEVENZIP_DIRECTORY_X64	&	"\7zFM.exe "
Const $SEVENZIP_CMD_LINE_EXECUTABLE_I386		= $SEVENZIP_DIRECTORY_I386	&	"\7z.exe"
Const $SEVENZIP_CMD_LINE_EXECUTABLE_X64			= $SEVENZIP_DIRECTORY_X64	&	"\7z.exe"

; Constants used throughout for various scripts
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
Const $SEVENZIP_CREATE_7Z_ARCHIVE				= "Create .7z archive"
Const $SEVENZIP_CREATE_ZIP_ARCHIVE				= "Create .zip archive"
Const $SEVENZIP_7Z_UNARCHIVE_FIVE_TIMES			= "Unarchive .7z five times"
Const $SEVENZIP_ZIP_UNARCHIVE_FIVE_TIMES		= "Unarchive .zip five times"
Const $SEVENZIP_TEST_7Z_ARCHIVE_INTEGRITY		= "Test .7z archive integrity"
Const $SEVENZIP_TEST_ZIP_ARCHIVE_INTEGRITY		= "Test .zip archive integrity"


; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
For $i = 0 to $gBaselineSize - 1
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

Func Report7ZipNotFoundAndTerminate()
	ErrorHandle("7-Zip not found in " & $SEVENZIP_CMD_LINE_EXECUTABLE_I386 & " or " & $SEVENZIP_CMD_LINE_EXECUTABLE_X64 & ", unable to launch")
EndFunc

Func Cleanup7ZipDirectories()
	; Clean up before we begin, delete everything in the 7zipRunTest\ and further down
	DirRemove( GetScriptTempDirectory() & "7zipRunTest\", 1 )
EndFunc