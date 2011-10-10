@echo off
echo Check Conflicts Script Compile Begins
echo    +-- Check Conflict Chrome
cd checkConflicts\chrome
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in chromeMustBeInstalled.au3 > ..\..\compile\chromeMustBeInstalled.txt
cd ..\..
 
echo    +-- Check Conflict Acrobat Reader
cd checkConflicts\acrobatReader
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in acrobatReaderMustBeInstalled.au3 > ..\..\compile\acrobatReaderMustBeInstalled.txt
cd ..\..
 
echo    +-- Check Conflict IE9
cd checkConflicts\ie9
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in ie9MustBeInstalled.au3 > ..\..\compile\ie9MustBeInstalled.txt
cd ..\..
 
echo    +-- Check Conflict Opera
cd checkConflicts\opera
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in operaMustBeInstalled.au3 > ..\..\compile\operaMustBeInstalled.txt
cd ..\..
 
echo    +-- Check Conflict Office2010 Access
cd checkConflicts\office2010
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in accessMustBeInstalled.au3 > ..\..\compile\accessMustBeInstalled.txt
cd ..\..
 
echo    +-- Check Conflict Office2010 Excel
cd checkConflicts\office2010
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in excelMustBeInstalled.au3 > ..\..\compile\excelMustBeInstalled.txt
cd ..\..
 
echo    +-- Check Conflict Office2010 PowerPoint
cd checkConflicts\office2010
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in powerpointMustBeInstalled.au3 > ..\..\compile\powerpointMustBeInstalled.txt
cd ..\..
 
echo    +-- Check Conflict Office2010 Publisher
cd checkConflicts\office2010
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in publisherMustBeInstalled.au3 > ..\..\compile\publisherMustBeInstalled.txt
cd ..\..
 
echo    +-- Check Conflict Office2010 Word
cd checkConflicts\office2010
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in wordMustBeInstalled.au3 > ..\..\compile\wordMustBeInstalled.txt
cd ..\..
 
echo        [Finished]
