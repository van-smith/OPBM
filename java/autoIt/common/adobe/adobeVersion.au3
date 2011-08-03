; adobeVersion.au3

; Default physical installation directory from Chrome 12.0.742.122 installer
Const $ACROBAT_READER_DIRECTORY					= "C:\Program Files (x86)\Adobe\Reader 10.0\Reader"
Const $ACROBAT_READER_INSTALLER					= $EXE_DIRECTORY				&	"\AdbeRdr1010_en_US.exe"
Const $ACROBAT_READER_UNINSTALLER				= "MsiExec.exe /I{AC76BA86-7AD7-1033-7B44-AA1000000001}"
Const $ACROBAT_READER_EXECUTABLE				= $ACROBAT_READER_DIRECTORY		&	"\AcroRd32.exe"
Const $ACROBAT_READER_EXECUTABLE_TO_LAUNCH		= $ACROBAT_READER_DIRECTORY		&	"\AcroRd32.exe"

; Error messages used related to the isAcrobatReaderAlreadyInstalled() function
Const $ACROBAT_READER_IS_NOT_INSTALLED			= "Acrobat Reader is not installed"

Func isAcrobatReaderAlreadyInstalled()
	If FileExists( $ACROBAT_READER_EXECUTABLE ) Then
		return True
	EndIf	
	return False
EndFunc
