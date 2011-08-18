@echo off
echo Office2010 Script Compile Begins
echo    +-- Excel Heat
cd office2010\excel
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in heat.au3 > ..\..\compile\heat.txt
cd ..\..

echo    +-- Word Alice in Wonderland
cd office2010\word
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in alice.au3 > ..\..\compile\alice.txt
cd ..\..
echo        [Finished]
