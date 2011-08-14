#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_outfile=alice.exe
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

;======================================================================================================================================
; Cossatot Analytics Laboratories, LLC.
;
; Written July 2011 by:
; ---------------------
;	Van Smith
;	Rick C. Hodgin
;
; Usage:	alice [repeat_count]
;
;======================================================================================================================================
#include <../../common/office2010/wordCommon.au3>
#include <../../common/adobe/adobeVersion.au3>
#include <../../common/ie/ieCommonIE9Functions.au3>

; Defined in wordCommon.au3, but overridden here for "alice"-specific launch and close entries
;$gBaselines[0][0] = $LAUNCH_MICROSOFT_WORD
$gBaselines[0][1] = $ALICE_LAUNCH_MICROSOFT_WORD_SCORE
;$gBaselines[1][0] = $CLOSE_MICROSOFT_WORD
$gBaselines[1][1] = $ALICE_CLOSE_MICROSOFT_WORD_SCORE

; Only defined in this AutoIt script
$gBaselines[2][0]	= $CLOSE_EMPTY_DOCUMENT
$gBaselines[2][1]	= $ALICE_CLOSE_EMPTY_DOCUMENT_SCORE
$gBaselines[3][0]	= $OPEN_ALICE_IN_WONDERLAND
$gBaselines[3][1]	= $ALICE_OPEN_ALICE_IN_WONDERLAND_SCORE
$gBaselines[4][0]	= $SAVE_AS_PDF
$gBaselines[4][1]	= $ALICE_SAVE_AS_PDF_SCORE
$gBaselines[5][0]	= $MANIPULATE_IN_ACROBAT_READER
$gBaselines[5][1]	= $ALICE_MANIPULATE_IN_ACROBAT_READER_SCORE
$gBaselines[6][0]	= $ALICE_TIME_TO_PGDN_N_TIMES_NORMALLY
$gBaselines[6][1]	= $ALICE_TIME_TO_PGDN_N_TIMES_NORMALLY_SCORE
$gBaselines[7][0]	= $LAUNCH_IE
$gBaselines[7][1]	= $ALICE_LAUNCH_IE_SCORE
$gBaselines[8][0]	= $CLOSE_IE
$gBaselines[8][1]	= $ALICE_CLOSE_IE_SCORE
$gBaselines[9][0]	= $ALICE_COPY_TO_CLIPBOARD
$gBaselines[9][1]	= $ALICE_COPY_TO_CLIPBOARD_SCORE
$gBaselines[10][0]	= $ALICE_TIME_TO_PGDN_N_TIMES_FONT_FX
$gBaselines[10][1]	= $ALICE_TIME_TO_PGDN_N_TIMES_FONT_FX_SCORE
$gBaselines[11][0]	= $ALICE_SET_FONT_LIGATURES
$gBaselines[11][1]	= $ALICE_SET_FONT_LIGATURES_SCORE
$gBaselines[12][0]	= $ALICE_PASTE_INTO_DOCUMENT
$gBaselines[12][1]	= $ALICE_PASTE_INTO_DOCUMENT_SCORE
$gBaselines[13][0]	= $ALICE_PAGE_DOWN_N_TIMES_IN_IE9
$gBaselines[13][1]	= $ALICE_PAGE_DOWN_N_TIMES_IN_IE9_SCORE

Dim $CurrentLoop
Dim $LoopLimit

outputDebug( "Starting up Microsoft Word Alice In Wonderland" )

if $CmdLine[0] > 0 then 
	$LoopLimit = $CmdLine[1] 
Else 
	$LoopLimit = 1
EndIf
For $CurrentLoop = 1 to $LoopLimit
	outputDebug( "InitializeGlobalVariables()" )
	InitializeGlobalVariables()
	outputDebug( "InitializeAliceScript()" )
	InitializeAliceScript()
	
	If not isAcrobatReaderAlreadyInstalled() Then
		outputError( $ACROBAT_READER_IS_NOT_INSTALLED )
		Exit -1
	EndIf
	
	If not isIE9Installed() Then
		outputError( $IE9_IS_NOT_INSTALLED )
		Exit -1
	EndIf
	
; Parsing through Internet Explorer 9
	outputDebug( "LaunchIE9()" )
	LaunchIE()
	OpenAliceInWonderLandHtml()
	PageDownNTimes()
	CopyToClipboard()
	CloseIE( $WINDOW_IE9_ALICE_IN_WONDERLAND )
; End

; Parsing in Word, with Acrobat manipulation
	outputDebug( "LaunchWord()" )
	LaunchWord()
	
	FirstRunCheck()
	
	PasteIntoDocument()
	PageThroughDocument( $ALICE_TIME_TO_PGDN_N_TIMES_NORMALLY )
	SetFontEffects()
	PageThroughDocument( $ALICE_TIME_TO_PGDN_N_TIMES_FONT_FX )
	MakeTextWrapTightAroundPictures()
	
;	outputDebug( "SaveAsAliceInWonderLandPdf()" )
;	SaveAsAliceInWonderLandPdf()
;	
;	outputDebug( "ManipulateInAcrobatReader()" )
;	ManipulateInAcrobatReader()
	
	outputDebug( "CloseWord()" )
	CloseWord()
; End
	
	outputDebug( "FinalizeWordScript()" )
	opbmFinalizeScript( "aliceTimes.csv" )
;	opbmFileDelete( $FILENAME_ALICE_IN_WONDERLAND_PDF )
Next
opbmPauseAndCloseAllWindowsNotPreviouslyNoted()
Exit

;======================================================================================================================================
;======================================================================================================================================

Func InitializeAliceScript()
	Opt("WinTitleMatchMode", -2)		; 1=start, 2=subStr, 3=exact, 4=advanced, -1 to -4=Nocase
	HotKeySet("{ESC}", "Terminate")
	
	; Start script timer
	$gScriptBeginTime = TimerInit()
	opbmWaitUntilSystemIdle( 10, 100, 5000 )
	
	; Delete the PDF in case it wasn't deleted last time
	opbmFileDeleteIfExists( $FILENAME_ALICE_IN_WONDERLAND_PDF )
EndFunc

; Opens in IE9 for later copy-and-paste into Word 2010
Func OpenAliceInWonderLandHtml()
	opbmTypeURL( $FILENAME_ALICE_IN_WONDERLAND_HTML, $OPEN_ALICE_IN_WONDERLAND, "Opening " & $FILENAME_ALICE_IN_WONDERLAND_HTML, $WINDOW_IE9_ALICE_IN_WONDERLAND )
EndFunc

Func PageDownNTimes()
	Local $i
	
	outputDebug( $ALICE_PAGE_DOWN_N_TIMES_IN_IE9 )
	TimerBegin()
	; Page down a few times
	For $i = 1 to $ALICE_NBR_PAGE_DOWNS
		Send("{PGDN}")
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Next
	; Wait until everything related to Windows is settled (because Internet Explorer is "very close to home" and accesses many Windows functions in unique "Microsoft ways")
	opbmWaitUntilSystemIdle( $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $WINDOW_IE9_ALICE_IN_WONDERLAND, "", $gTimeout, $ERROR_PREFIX & "WinWait: Internet Explorer 9: Unable to find Window." )
	TimerEnd( $ALICE_PAGE_DOWN_N_TIMES_IN_IE9 )
EndFunc

Func CopyToClipboard()
	outputDebug( "Copying HTML to clipboard" )
	TimerBegin()
	; Select everything that was loaded in preparation to copy-and-paste into Word
	Send("^a")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, 250, 20000 )
	
	; Copy it to the clipboard
	Send("^c")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, 250, 20000 )
	
	; Pause until Windows settles down for one full second
	opbmWaitUntilSystemIdle( $gPercent, 1000, 20000 )
	TimerEnd( $ALICE_COPY_TO_CLIPBOARD )
EndFunc

Func PasteIntoDocument()
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: Unable to find Window." )
	outputDebug( $ALICE_PASTE_INTO_DOCUMENT )
	
	TimerBegin()
	; Paste into the document from the clipboard
	Send("^v")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send("^{Home}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	; Pause until Windows settles down for two full seconds
	opbmWaitUntilSystemIdle( $gPercent, 2000, 30000 )
	
	TimerEnd( $ALICE_PASTE_INTO_DOCUMENT )
EndFunc

Func PageThroughDocument( $description )
	Local $i
	
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: Unable to find Window." )
	outputDebug( $description )
	
	TimerBegin()
	for $i = 1 to $ALICE_NBR_PAGE_DOWNS
		Send("{PGDN}")
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, 20000 )
	Next
		
	; Go back to the beginning
	Send("^{Home}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $description )
EndFunc

; Word 2010 has special font effects that can make for some beautiful typography.
; This test enables those effects
Func SetFontEffects()
	TimerBegin()
	; Select everything
	Send("^a")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	; Ctrl+D brings up the font dialog
	Send("^d")
	Sleep(250)
	opbmWinWaitActivate( "Font", "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word Font Dialog: Unable to find Window." )
	
	; Alt+V selects the "advanced" tab
	Send("!v")
	Sleep(250)
	; Alt+L selects the "Ligatures" combobox
	Send("!l")
	Sleep(250)
	; Home, down, down selects "Standard and Contextual" ligatures, then enter accepts that combobox entry
	Send("{home}{down}{down}{enter}")
	Sleep(100)
	
	; This second enter key closes the dialog and accepts the options ("Okay" is the default option)
	Send("{enter}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	; Un-select everything
	Send("{home}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $ALICE_SET_FONT_LIGATURES )
EndFunc

Func MakeTextWrapTightAroundPictures()
	; Cannot find keystrokes to make this work globally
EndFunc

Func SaveAsAliceInWonderLandPdf()
	; Save as...
	TimerBegin()
	Send("!fa")
	opbmWinWaitActivate( $SAVE_AS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word Save As: Unable to find Window." )
	
	; as pdf
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	; Alt+t to choose type, p, p to choose "PDF"
	Send("!tpp")
	Sleep(250)
	; Alt+e to choose "Open file after publishing"
	Send("!e")
	Sleep(250)
	Send("+=")
	Sleep(250)
	; Choose "Save" button
	Send("!s")
	
	; Wait for Adobe Reader to appear
	opbmWinWaitActivate( $ADOBE_READER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Adobe Reader: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $SAVE_AS_PDF )
EndFunc

Func ManipulateInAcrobatReader()
	Local $i
	
	TimerBegin()
	; Turn on navigation pane
	Send("{F4}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	For $i = 1 to 4
		; Fit actual size
		Send("^1")
		; Fit to page width
		Send("^2")
		; Fit to page height
		Send("^0")
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Next
	
	; Move from front to back and rotate at each step
	For $i = 1 to 8
		; Last page
		Send("{End}")
		If $i <= 4 then
			; Rotate clockwise
			Send("^+{+}")
		Else
			; Rotate counter-clockwise
			Send("^+{-}")
		EndIf
		
		; First page
		Send("{Home}")
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Next
	
	; Move forward N pages, a page at a time
	for $i = 1 to $ALICE_NBR_PAGE_DOWNS
		Send("{PGDN}")
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Next
	
	; Turn off navigation
	Send("{F4}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	; Goto page 25
	Send("+^n25{Enter}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	; Goto page 1
	Send("+^n1{Enter}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	; Goto page 10
	Send("+^n10{Enter}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	; Goto page 30
	Send("+^n30{Enter}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	; Goto page 3
	Send("+^n3{Enter}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	; Goto page 1
	Send("{Home}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	; Finished manipulating, exit Acrobat Reader
	Send("!fx")
	
	; Wait for the Word window to have focus again
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: Unable to find Window." )
	; and for the system to settle down
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	TimerEnd( $MANIPULATE_IN_ACROBAT_READER )
EndFunc
