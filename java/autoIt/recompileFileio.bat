@echo off
echo File I/O Script Compile Begins
echo    +--  Copy Test
cd fileio\copy
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in fileioTest.au3 > ..\..\compile\fileioTest.txt
cd ..\..
echo        [Finished]
