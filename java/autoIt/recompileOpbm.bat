@echo off
echo OPBM Compile Begins
echo    +-- Reboot Time
cd opbm\internal
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in rebootTime.au3 > ..\..\compile\rebootTime.txt
cd ..\..

echo    +-- Startup Settle Down
cd opbm\internal
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in startupSettleDown.au3 > ..\..\compile\startupSettleDown.txt
cd ..\..
echo        [Finished]
