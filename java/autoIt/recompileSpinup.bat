@echo off
echo Spinup Script Compile Begins
echo    +-- Spinup Access
cd spinup\access
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in launchAndClose.au3 > ..\..\compile\accessLaunchAndClose.txt
cd ..\..

echo    +-- Spinup Excel
cd spinup\excel
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in launchAndClose.au3 > ..\..\compile\accessLaunchAndClose.txt
cd ..\..

echo    +-- Spinup Powerpoint
cd spinup\powerpoint
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in launchAndClose.au3 > ..\..\compile\accessLaunchAndClose.txt
cd ..\..

echo    +-- Spinup Publisher
cd spinup\publisher
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in launchAndClose.au3 > ..\..\compile\accessLaunchAndClose.txt
cd ..\..

echo    +-- Spinup Word
cd spinup\word
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in launchAndClose.au3 > ..\..\compile\accessLaunchAndClose.txt
cd ..\..
echo        [Finished]
