#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 9.19.2011

	Description: Atom to open earthquake Access database, run queries and reports

	Usage:	accessEarthquake

#ce ======================================================================================================================================

#include <../../common/office2010/accessCommon.au3>

$gBaselines[0][0] = $ACCESS_COPY_EARTHQUAKE
$gBaselines[0][1] = $ACCESS_COPY_EARTHQUAKE_SCORE
$gBaselines[1][0] = $ACCESS_OPEN_EARTHQUAKE
$gBaselines[1][1] = $ACCESS_OPEN_EARTHQUAKE_SCORE
$gBaselines[2][0] = $ACCESS_EARTHQUAKE_QUERIES
$gBaselines[2][1] = $ACCESS_EARTHQUAKE_QUERIES_SCORE
$gBaselines[3][0] = $ACCESS_EARTHQUAKE_REPORTS
$gBaselines[3][1] = $ACCESS_EARTHQUAKE_REPORTS_SCORE
$gBaselines[4][0] = $ACCESS_COMPACT_EARTHQUAKE
$gBaselines[4][1] = $ACCESS_COMPACT_EARTHQUAKE_SCORE

outputDebug( "initializeAccessScript()" )
initializeAccessScript()
outputDebug( "initializeAccessEarthquakeScript()" )
initializeAccessEarthquakeScript()
outputDebug( "accessOpenEarthquake()" )
accessOpenEarthquake()
outputDebug( "accessEarthquakeQueries()" )
accessEarthquakeQueries()
outputDebug( "accessEarthquakeReports()" )
accessEarthquakeReports()
outputDebug( "compactEarthquake()" )
compactEarthquake()

Exit

Func initializeAccessEarthquakeScript()
	$directoryOutput	= GetScriptTempDirectory()
	$filenameEarthquakeDb	= $directoryOutput & $filenameEarthquakeDb
	; Delete the document in case it wasn't deleted last time
	opbmFileDeleteIfExists( $filenameEarthquakeDb )
	; copy the database file:
	TimerBegin()
	$gErrorTrap = FileCopy($FILENAME_ACCDB, $filenameEarthquakeDb, 1)
	If $gErrorTrap = 0 Then ErrorHandle($ERROR_PREFIX & "FileCopy: " & $FILENAME_ACCDB & ": Unable to copy file.")
	TimerEnd( $ACCESS_COPY_EARTHQUAKE )
EndFunc

Func accessOpenEarthquake()
	local $i
	opbmWinWaitActivate( $MICROSOFT_ACCESS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Access. Unable to find Window." )
	TimerBegin()
	Send( "!fo" )
	opbmWinWaitActivate( $OPEN, $OPEN, $gTimeout, $ERROR_PREFIX & "WinWait: Open: Unable to find Window." )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	ControlSend( $OPEN, $OPEN, "Edit1", $filenameEarthquakeDb, 1)
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	Send( "{ENTER}" )
	opbmWaitUntilProcessIdle( $gPID, $gPercent, $gDurationMS, $gTimeoutMS )
	opbmWinWaitActivate( $MICROSOFT_ACCESS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Access. Unable to find Window." )
	TimerEnd( $ACCESS_OPEN_EARTHQUAKE )
	Sleep( 5000 )
EndFunc

Func accessEarthquakeQueries()
	opbmWinWaitActivate( $MICROSOFT_ACCESS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Access. Unable to find Window." )

	TimerBegin()
	runQuery( "1898-2011 earthquake data Query with Nework Codes" )
	runQuery( "1898-2011 earthquake data with Parsed Dates" )
	runQuery( "1898-2011 eq data recorded by the Alaska Regional Network" )
	runQuery( "All Earthquake Data With Magnitude 6 and above" )
	runQuery( "Earthquake Average, Maximum and Minimum Sizes" )
	runQuery( "Earthquake Data with >2 earthquakes at same lat, long" )
	runQuery( "Significant Earthquakes with non 0 magnitude value" )
	runQuery( "All Earthquakes by Hemisphere with average Magnitude" )
	runQuery( "All Earthquakes by Hemisphere with Magnitudes Larger than 6" )
	TimerEnd( $ACCESS_EARTHQUAKE_QUERIES )
EndFunc

Func accessEarthquakeReports()
	opbmWinWaitActivate( $MICROSOFT_ACCESS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Access. Unable to find Window." )

	TimerBegin()
	runQuery( "r1898-2011 eq data recorded by the Alaska Regional Network" )
	runQuery( "rAll Earthquake Data By Magnitude" )
	runQuery( "rAll Earthquake Data With Magnitude 6 and above" )
	runQuery( "rEarthquake Average, Maximum and Minimum Sizes" )
	runQuery( "rEarthquake Data with >2 earthquakes at same lat, long" )
	TimerEnd( $ACCESS_EARTHQUAKE_REPORTS )
EndFunc

Func compactEarthquake()
	Sleep( 5000 )
	opbmWinWaitActivate( $MICROSOFT_ACCESS, "", $gTimeout, $ERROR_PREFIX & "WinWait: Microsoft Access. Unable to find Window." )

	TimerBegin()
	Send( "!y" )
	Send( "c" )
	opbmWaitUntilSystemIdle( 5, 100, 20000 )
	TimerEnd( $ACCESS_COMPACT_EARTHQUAKE )
EndFunc