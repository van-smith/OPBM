@echo off
echo Office2010 Script Compile Begins
echo    +-- Access Open
cd office2010\access
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in accessOpen.au3 > ..\..\compile\accessOpen.txt
cd ..\..

echo    +-- Access Close
cd office2010\access
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in accessClose.au3 > ..\..\compile\accessClose.txt
cd ..\..

echo    +-- Access Earthquake
cd office2010\access
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in accessEarthquake.au3 > ..\..\compile\accessEarthquake.txt
cd ..\..

echo    +-- Excel Heat
cd office2010\excel
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in heat.au3 > ..\..\compile\heat.txt
cd ..\..

echo    +-- Excel General
cd office2010\excel
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in excelGeneral.au3 > ..\..\compile\excelGeneral.txt
cd ..\..

echo    +-- PowerPoint Open
cd office2010\powerpoint
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in powerpointOpen.au3 > ..\..\compile\powerpointOpen.txt
cd ..\..

echo    +-- PowerPoint Close
cd office2010\powerpoint
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in powerpointClose.au3 > ..\..\compile\powerpointClose.txt
cd ..\..

echo    +-- PowerPoint War
cd office2010\powerpoint
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in powerpointWar.au3 > ..\..\compile\powerpointWar.txt
cd ..\..

echo    +-- Publisher Open
cd office2010\publisher
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in publisherOpen.au3 > ..\..\compile\publisherOpen.txt
cd ..\..

echo    +-- Publisher Close
cd office2010\publisher
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in publisherClose.au3 > ..\..\compile\publisherClose.txt
cd ..\..

echo    +-- Publisher Hedge
cd office2010\publisher
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in publisherHedge.au3 > ..\..\compile\publisherHedge.txt
cd ..\..

echo    +-- Word Alice in Wonderland
cd office2010\word
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in alice.au3 > ..\..\compile\alice.txt
cd ..\..

echo    +-- Close Word
cd office2010\word
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in closeWord.au3 > ..\..\compile\closeWord.txt
cd ..\..

echo    +-- Word Island
cd office2010\word
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in wordIsland.au3 > ..\..\compile\wordIsland.txt
cd ..\..

echo    +-- Word Open
cd office2010\word
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in wordOpen.au3 > ..\..\compile\wordOpen.txt
cd ..\..
echo        [Finished]
