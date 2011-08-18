; ieCommonIE9Functions.au3

; Default physical installation directory from Internet Explorer
Const $IE_DIRECTORY						= "C:\Program Files (x86)\Internet Explorer"

; Executables used to install, uninstall, or execute Internet Explorer
Const $IE_EXECUTABLE					= $IE_DIRECTORY			&	"\iexplore.exe"
Const $IE_EXECUTABLE_TO_LAUNCH			= $IE_DIRECTORY			&	"\iexplore.exe " & $OPBM_SPLASH_HTML
Const $IE9_IS_NOT_INSTALLED				= "Internet Explorer 9 not found"
Const $LAUNCH_IE 						= "Launch Internet Explorer"
Const $CLOSE_IE 						= "Close Internet Explorer"
Const $IE_WINDOW						= " - Internet Explorer"

Func isIE9Installed()
	Local $result
	If FileExists( $IE_EXECUTABLE ) Then
		; Internet Explorer exists, but is it version 9?
		; Returns 1 if exact match, 2 if case-insensitive match, 0 if error
		If CheckIfRegistryKeyStartsWith( "HKLM\Software\Microsoft\Internet Explorer\Version", "9." ) >= 1 Then
			; Set our default values for IE9
			$result = InternetExplorerInstallerAssist()
			If StringInStr( $result, "0" ) Then
				; There was at least an error, the presence of every 0 indicates an error, 1 indicates success:
				outputDebug( "Unable to set some IE Registry Keys: " & $result )
			EndIf
			return True
		EndIf
	EndIf	
	; Internet Explorer does not exist... weird
	return False
EndFunc

Func VerifyIE9Installed()
	If not isIE9Installed() Then
		outputError( "Requires IE Version 9.0" )
		opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
		Exit -1
	EndIf
EndFunc

Func LaunchIE()
	outputDebug( "Attempting to launch " & $IE_EXECUTABLE )
	; Make sure we're not running IE8, IE7 or (gasp!) IE6
	VerifyIE9Installed()
	
	TimerBegin()
	$gPID = Run( $IE_EXECUTABLE_TO_LAUNCH, "C:\", @SW_SHOWMAXIMIZED )
	opbmWaitUntilProcessIdle( $gPID, 5, 100, 5000 )
	opbmWinWaitActivate( $OPBM_SPLASH_HTML_TITLE )
	TimerEnd( $LAUNCH_IE )
	
	opbmWaitUntilProcessIdle( $gPID, 10, 100, 5000 )
EndFunc

Func CloseIE( $windowTitle = $IE_WINDOW )
	; Start the timer
	TimerBegin()
	
	; Close Opera
	WinActivate( $windowTitle )
	WinClose( $windowTitle )	
	
	; Wait until the sytem settles down
	opbmWaitUntilSystemIdle( 10, 100, 10000 )
	
	; Take the ending timer
	TimerEnd( $CLOSE_IE )
EndFunc
