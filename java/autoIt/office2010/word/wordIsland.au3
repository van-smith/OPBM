#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 9.19.2011

	Description: Atom to type excerpt from John Donne's Meditation XVII
	             into a new Microsoft Word 2010 document.

	Usage:	wordIsland

#ce ======================================================================================================================================

#include <../../common/office2010/wordCommon.au3>

$gBaselines[0][0] = $WORD_ISLAND
$gBaselines[0][1] = $WORD_ISLAND_SCORE
$gBaselines[1][0] = $WORD_ISLAND_SAVE
$gBaselines[1][1] = $WORD_ISLAND_SAVE_SCORE

outputDebug( "InitializeGlobalVariables()" )
initializeGlobalVariables()
outputDebug( "initializeWordScript()" )
initializeWordScript()
outputDebug( "initializeWordIslandScript()" )
initializeWordIslandScript()

outputDebug( "typeIslandIntoDocument()" )
typeIslandIntoDocument()
outputDebug( "saveDocument()" )
saveDocument()

Exit

Func InitializeWordIslandScript()
	; Delete the document in case it wasn't deleted last time
	$directoryOutput	= GetScriptTempDirectory()
	$filenameIsland		= $directoryOutput & $filenameIsland
	opbmFileDeleteIfExists( $filenameIsland )
EndFunc

Func typeIslandIntoDocument()
	; Try to activate window
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "{PGDN}" )
	Send( "{PGUP}" )
	Sleep( 1000 )

	outputDebug( $WORD_ISLAND )

	TimerBegin()
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send("!w")
	Sleep( 1000 )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send("q")
	opbmWinWaitActivate( "Zoom", "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find Zoom window." )
	Send("!2")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send("{enter}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )

;	Send("{alt}")
;	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send("!h")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send("ac")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )

	Send("^b")
	Send( "Excerpt from ^iMeditation XVII^i, by John Donne" )
	Send("^b")
	Send("^<")
	Send("{enter}")
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )

	Send( "Who casts not up his eye to the sun when it rises?" )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "But who takes off his eye from a comet when that breaks out?" )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "Who bends not his ear to any bell which upon any occasion rings?" )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "But who can remove it from that bell which is passing a piece of himself out of this world?" )
	Send("{enter}")
	Send("{enter}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )

	Send( "No man is an island," )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "entire of itself;" )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "every man is a piece of the continent," )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "a part of the main." )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "If a clod be washed away by the sea," )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "Europe is the less," )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "as well as if a promontory were," )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "as well as if a manor of thy friend's" )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "or of thine own were." )
	Send("{enter}")
	Send("{enter}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )

	Send( "Any man's death diminishes me," )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "because I am involved in mankind," )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "and therefore never send to know for whom the bells tolls;" )
	Send("{enter}")
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send( "it tolls for thee." )
	Send("{enter}")
	Send("{enter}")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )

	TimerEnd( $WORD_ISLAND )

	Sleep( 1000 );

	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send("^<")
	Send("^<")
	Send("^b")
	Send( "Dedicated to Richard G. Russell" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send("{alt}")
	Send("w")
	Send("j")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	Send("{alt}")
	Send("w")
	Send("1")

	Sleep( 5000 );

EndFunc

Func saveDocument()
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )

	TimerBegin()
	Send( "^s" )
	opbmWinWaitActivate( $SAVE_AS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word Save As: Unable to find Window." )
	; Send its full pathname
	Send( $filenameIsland )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	; Choose "Save" button
	Send("!s")
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $MICROSOFT_WORD_WINDOW, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Word: unable to find window." )
	TimerEnd( $WORD_ISLAND_SAVE )

EndFunc