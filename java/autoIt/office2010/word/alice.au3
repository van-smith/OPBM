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
; Usage:	alice
;
;======================================================================================================================================
#include <../../common/office2010/wordCommon.au3>
#include <../../common/adobe/adobeVersion.au3>


; Begin at 2, refer to wordCommon.au3 to see 0 and 1
$gBaselines[2][0] = $CLOSE_EMPTY_DOCUMENT
$gBaselines[2][1] = $ALICE_CLOSE_EMPTY_DOCUMENT_SCORE
$gBaselines[3][0] = $OPEN_ALICE_IN_WONDERLAND
$gBaselines[3][1] = $ALICE_OPEN_ALICE_IN_WONDERLAND_SCORE
$gBaselines[4][0] = $TIME_TO_PAGE_DOWN_N_TIMES
$gBaselines[4][1] = $ALICE_TIME_TO_PAGE_DOWN_N_TIMES_SCORE
$gBaselines[5][0] = $SAVE_AS_PDF
$gBaselines[5][1] = $ALICE_SAVE_AS_PDF_SCORE = 3.81

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
	
	outputDebug( "LaunchWord()" )
	LaunchWord()
	
	FirstRunCheck()
	
	outputDebug( "CloseEmptyDocument()" )
	CloseEmptyDocument()
	
	outputDebug( "OpenAliceInWonderLandHtml()" )
	OpenAliceInWonderLandHtml()
	
	outputDebug( "PageThroughDocument()" )
	PageThroughDocument()
	
	outputDebug( "SaveAsAliceInWonderLandPdf()" )
	SaveAsAliceInWonderLandPdf()
	
	outputDebug( "CloseWord()" )
	CloseWord()
	
	outputDebug( "FinalizeWordScript()" )
	opbmFinalizeScript( "aliceTimes.csv" )
	opbmFileDelete( $FILENAME_ALICE_IN_WONDERLAND_PDF )
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

Func LaunchWord()
	Local $filename
	
	; See which version we're running, either 64-bit (default) or 32-bit (fallback)
	If FileExists($FILENAME_WORD_X64) Then
		$filename = $FILENAME_WORD_X64
	ElseIf FileExists( $FILENAME_WORD_I386 ) Then
		$filename = $FILENAME_WORD_I386
	Else
		ErrorHandle("Launch: Word 2010 not found in " & $FILENAME_WORD_X64 & " or " & $FILENAME_WORD_I386 & ", unable to launch.")
	EndIf
	outputDebug( "Attempting to launch " & $filename)
	
	; Launch the process
	TimerBegin()
	$gPID = Run($filename, "C:\", @SW_MAXIMIZE)
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, $DOCUMENT1, $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word. Unable to find Window." )
	
	; Note the time
	TimerEnd( $LAUNCH_MICROSOFT_WORD )
EndFunc

Func CloseEmptyDocument()
	outputDebug( "Closing empty document... " )
	
	TimerBegin()
	Send("!fc")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, $STATUS_BAR, $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word. Unable to find Window." )
	TimerEnd("Close Empty Document")
EndFunc

Func OpenAliceInWonderLandHtml()
	outputDebug( "Opening " & $FILENAME_ALICE_IN_WONDERLAND_HTML )
	
	TimerBegin()
	; Open file dialog
	Send("!fo")
	opbmWinWaitActivate( $OPEN_DIALOG, $OPEN_DIALOG, $gTimeout, $ERROR_PREFIX & "WinWait: Open: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	; Specify the AliceInWonderland.html filename
	ControlSend( $OPEN_DIALOG, $OPEN_DIALOG, "Edit1", $FILENAME_ALICE_IN_WONDERLAND_HTML, 1)
	Send("{ENTER}")
	
	; Wait for it to load
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	; Page down a couple times
	Send("{PGDN}")
	Send("{PGUP}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $WINDOW_ALICE_IN_WONDERLAND_HTML, $WINDOW_ALICE_IN_WONDERLAND_HTML, $gTimeout, $ERROR_PREFIX & "WinWait: " & $WINDOW_ALICE_IN_WONDERLAND_HTML & ": Unable to find Window." )
	
	TimerEnd( $OPEN_ALICE_IN_WONDERLAND )
EndFunc

Func PageThroughDocument()
	Local $i
	
	outputDebug( "Paging through document " & $NBR_OF_PAGE_DOWN & " times." )
	
	TimerBegin()
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: Unable to find Window." )
	for $i = 1 to $NBR_OF_PAGE_DOWN
		Send("{PGDN}")
		opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Next
	
	TimerEnd( $TIME_TO_PAGE_DOWN_N_TIMES )
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
	
	opbmWinWaitActivate( $ADOBE_READER, "", $gTimeout, $ERROR_PREFIX & "WinWait: Adobe Reader: Unable to find Window." )
	; Exit Adobe Reader
	Send("!fx")
	
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	
	TimerEnd( $SAVE_AS_PDF )
EndFunc

Func CloseWord()
	; Exit word
	TimerBegin()
	; File -> Exit
	Send("!fx")
	; "Save Changes?" No
	Send("!n")	
	
	TimerEnd( $CLOSE_MICROSOFT_WORD )
EndFunc
