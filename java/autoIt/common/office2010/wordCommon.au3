Const $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>
#include <office2010Common.au3>

; Source directories for data
Const $DIRECTORY_ALICE_IN_WONDERLAND_HTML		= @ScriptDir & "\data"

; Directories to access 32-bit and 64-bit versions of the app
Const $DIRECTORY_WORD_I386						= "C:\Program Files (x86)\Microsoft Office\OFFICE14"
Const $DIRECTORY_WORD_X64						= "C:\Program Files\Microsoft Office\OFFICE14"

; Filenames used internally
Const $FILENAME_ALICE_IN_WONDERLAND_HTML		= $DIRECTORY_ALICE_IN_WONDERLAND_HTML & "\AliceInWonderland.html"
Const $FILENAME_ALICE_IN_WONDERLAND_PDF			= $DIRECTORY_ALICE_IN_WONDERLAND_HTML & "\AliceInWonderland.pdf"
Const $FILENAME_WORD_I386						= $DIRECTORY_WORD_I386 & "\WinWord.exe"
Const $FILENAME_WORD_X64						= $DIRECTORY_WORD_X64 & "\WinWord.exe"
Const $WINDOW_ALICE_IN_WONDERLAND_HTML			= "AliceInWonderland"

Const $NBR_OF_PAGE_DOWN							= 25;

Const $LAUNCH_MICROSOFT_WORD					= "Launch Microsoft Word 2010"
Const $CLOSE_MICROSOFT_WORD						= "Close Microsoft Word 2010"
Const $CLOSE_EMPTY_DOCUMENT						= "Close Empty Document"
Const $OPEN_ALICE_IN_WONDERLAND					= "Open AliceInWonderland.html"
Const $TIME_TO_PAGE_DOWN_N_TIMES				= "Time to page down " & $NBR_OF_PAGE_DOWN & "times"
Const $SAVE_AS_PDF								= "Save as PDF"
Const $MICROSOFT_WORD_WINDOW					= "Microsoft Word"
Const $DOCUMENT1								= "Document1"
Const $STATUS_BAR								= "Status Bar"
Const $SAVE_AS									= "Save As"
Const $ADOBE_READER								= "Adobe Reader"
Const $OPEN_DIALOG								= "Open"

; Setup references for timing items
Dim $gBaselineSize
$gBaselineSize = 10
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $LAUNCH_MICROSOFT_WORD
$gBaselines[0][1] = $LAUNCH_MICROSOFT_WORD_SCORE
$gBaselines[1][0] = $CLOSE_MICROSOFT_WORD
$gBaselines[1][1] = $CLOSE_MICROSOFT_WORD_SCORE
