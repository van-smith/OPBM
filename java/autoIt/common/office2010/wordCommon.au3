Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>
#include <office2010Common.au3>


; Relative locations for content
Const $DATA_DIRECTORY					= @ScriptDir & "\data"
Const $EXE_DIRECTORY					= @ScriptDir & "\exe"

; Source directories for data
Const $DIRECTORY_ALICE_IN_WONDERLAND_HTML		= @ScriptDir & "\data"
Dim $DIRECTORY_ALICE_IN_WONDERLAND_PDF			; Used in this script, but must be set after initialization because they use opbm.dll plugin functions

; Directories to access 32-bit and 64-bit versions of the app
Const $DIRECTORY_WORD_I386						= "C:\Program Files (x86)\Microsoft Office\OFFICE14"
Const $DIRECTORY_WORD_X64						= "C:\Program Files\Microsoft Office\OFFICE14"

; Filenames used internally
Const $WINDOW_IE9_ALICE_IN_WONDERLAND			= "Alice in Wonderland"
Const $WINDOW_ALICE_IN_WONDERLAND_HTML			= "AliceInWonderland"
Const $ALICEINWONDERLAND_HTML					= $WINDOW_ALICE_IN_WONDERLAND_HTML & ".html"
Const $ALICEINWONDERLAND_PDF					= $WINDOW_ALICE_IN_WONDERLAND_HTML & ".pdf"
Const $FILENAME_ALICE_IN_WONDERLAND_HTML		= $DIRECTORY_ALICE_IN_WONDERLAND_HTML & "\" & $ALICEINWONDERLAND_HTML
Dim $FILENAME_ALICE_IN_WONDERLAND_PDF			; Used in this script, but must be set after initialization because they use opbm.dll plugin functions
Const $FILENAME_WORD_I386						= $DIRECTORY_WORD_I386 & "\WinWord.exe"
Const $FILENAME_WORD_X64						= $DIRECTORY_WORD_X64 & "\WinWord.exe"


Const $ALICE_NBR_PAGE_DOWNS						= 60

Const $LAUNCH_MICROSOFT_WORD					= "Launch Microsoft Word 2010"
Const $CLOSE_MICROSOFT_WORD						= "Close Microsoft Word 2010"
Const $CLOSE_EMPTY_DOCUMENT						= "Close Empty Document"
Const $OPEN_ALICE_IN_WONDERLAND					= "Open " & $ALICEINWONDERLAND_HTML
Const $ALICE_TIME_TO_PGDN_N_TIMES_NORMALLY		= "Time to page down " & $ALICE_NBR_PAGE_DOWNS & " times normally"
Const $ALICE_TIME_TO_PGDN_N_TIMES_FONT_FX		= "Time to page down " & $ALICE_NBR_PAGE_DOWNS & " times with font effects"
Const $SAVE_AS_PDF								= "Save as PDF"
Const $MANIPULATE_IN_ACROBAT_READER				= "Manipulate in Acrobat Reader"
Const $MICROSOFT_WORD_WINDOW					= "Microsoft Word"
Const $DOCUMENT1								= "Document1"
Const $STATUS_BAR								= "Status Bar"
Const $SAVE_AS									= "Save As"
Const $ADOBE_READER								= "Adobe Reader"
Const $OPEN_DIALOG								= "Open"
Const $ALICE_PAGE_DOWN_N_TIMES_IN_IE9			= "Page down " & $ALICE_NBR_PAGE_DOWNS & " times in IE9"
Const $ALICE_COPY_TO_CLIPBOARD					= "Copy " & $ALICEINWONDERLAND_HTML & " to clipboard"
Const $ALICE_PASTE_INTO_DOCUMENT				= "Paste into document"
Const $ALICE_SET_FONT_LIGATURES					= "Set font ligatures to Standard + Contextual"
Const $WORD_HAS_FINISHED_SEARCHING_THE_DOCUMENT	= "Word has finished searching the document"
Const $FIND_AND_REPLACE							= "Find and Replace"
Const $ALICE_SET_FONT_EFFECTS					= "Setting font effects"
Const $ALICE_UPDATING_IMAGE_TEXT_ALIGNMENT		= "Updating image's text alignment"

; Setup references for timing items
Dim $gBaselineSize
$gBaselineSize = 20
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_MICROSOFT_WORD
$gBaselines[0][1] = $LAUNCH_MICROSOFT_WORD_SCORE
$gBaselines[1][0] = $CLOSE_MICROSOFT_WORD
$gBaselines[1][1] = $CLOSE_MICROSOFT_WORD_SCORE

Func LaunchWord()
	Local $filename
	
	; See which version we're running, either 64-bit (default) or 32-bit (fallback)
	If FileExists($FILENAME_WORD_X64) Then
		$filename = $FILENAME_WORD_X64
	ElseIf FileExists( $FILENAME_WORD_I386 ) Then
		$filename = $FILENAME_WORD_I386
	Else
		ErrorHandle("Word 2010 not found in " & $FILENAME_WORD_X64 & " or " & $FILENAME_WORD_I386 & ", unable to launch.")
	EndIf
	
	; Opbm sets some registry keys at startup
	outputDebug( $SAVING_AND_SETTING_OFFICE_2010_REGISTRY_KEYS )
	Office2010SaveRegistryKeys()
	Office2010InstallRegistryKeys()
	
	; Launch the process
	outputDebug( "Attempting to launch " & $filename)
	TimerBegin()
	$gPID = Run( $filename, "C:\", @SW_MAXIMIZE )
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, $DOCUMENT1, $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word. Unable to find Window." )
	
	; Note the time
	TimerEnd( $LAUNCH_MICROSOFT_WORD )
EndFunc

Func CloseWord()
	; Exit word
	TimerBegin()
	; File -> Exit
	Send("!fx")
	; "Save Changes?" No
	Send("!n")	
	TimerEnd( $CLOSE_MICROSOFT_WORD )
	
	; Restore registry keys
	Office2010RestoreRegistryKeys()
EndFunc
