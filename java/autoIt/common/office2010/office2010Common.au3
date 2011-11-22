; office2010Common.au3

Const $WINDOW_OFFICE2010_USERNAME = "User Name"
Const $WINDOW_OFFICE2010_WELCOME = "Welcome to Microsoft Office 2010"
Const $TIMEOUT = 5


Func FirstRunCheck()
	If DismissUserNameWindow() Then
		outputStatus( "Microsoft Office 2010 user name screen found and dismissed." )
	EndIf

	If DismissOffice2010WelcomeWindow() Then
		outputStatus( "Microsoft Office 2010 welcome screen found and dismissed." )
	;Else
	; Not found, which is typical behavior
	EndIf
EndFunc

Func DismissUserNameWindow()
	Dim $lReturnCode
	; Wait until the CPU is below 10% for 1 second
	outputDebug( "Waiting for Microsoft Office 2010 User Name screen" )
	opbmWaitUntilSystemIdle( 10, 1000, 5000 )

	; See if there's an Office 2010 User Name screen
	$lReturnCode = WinWait( $WINDOW_OFFICE2010_USERNAME, "", 5 )
	If $lReturnCode = False Then
		; Nope, Office 2010 user name screen not found, which is typical behavior
		Return False
	Else
		; It was found, meaning this is a first-run, so dismiss it
		Do
			WinActivate( $WINDOW_OFFICE2010_USERNAME )
			Sleep(250)

			Send( "{Enter}" )
			Sleep(250)
			$lReturnCode = WinWait( $WINDOW_OFFICE2010_USERNAME, "", 1 )
		Until $lReturnCode = 0
	EndIf
	; Window found and dismissed
	Return True
EndFunc

Func DismissOffice2010WelcomeWindow()
	Dim $lReturnCode
	; Wait until the CPU is below 10% for 1 second
	outputDebug( "Waiting for Microsoft Office 2010 welcome screen" )
	opbmWaitUntilSystemIdle( 10, 1000, 5000 )

	; See if there's an Office 2010 welcome screen
	$lReturnCode = WinWait( $WINDOW_OFFICE2010_WELCOME, "", 5 )
	If $lReturnCode = False Then
		; Nope, Office 2010 welcome screen not found, which is typical behavior
		Return False
	Else
		; It was found, meaning this is a first-run, so dismiss it
		Do
			WinActivate( $WINDOW_OFFICE2010_WELCOME )
			Sleep(250)
			; Select "Don’t make changes"
			Send("!d")
			Sleep(250)
			Send( "{Enter}" )
			Sleep(250)
			$lReturnCode = WinWait( $WINDOW_OFFICE2010_WELCOME, "", 1 )
		Until $lReturnCode = 0
	EndIf
	; Window found and dismissed
	Return True
EndFunc
