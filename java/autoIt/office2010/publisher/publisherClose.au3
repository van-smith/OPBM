#cs ======================================================================================================================================

	Office Productivity Benchmark (OPBM)
	Copyright 2011 Cossatot Analytics Laboratories, LLC.

	Written by:	Van Smith (van@canalabs.com)
	Initial creation date: 8.31.2011

	Description: Atom to close Microsoft Publisher 2010
	
	Usage:	publisherClose

#ce ======================================================================================================================================

#include <../../common/office2010/publisherCommon.au3>

outputDebug( "initializePublisherScript()" )
initializePublisherScript()
outputDebug( "closePublisher()" )
closePublisher()

Exit