@echo off
echo Java Script Compile Begins
echo    +-- Compute
cd java\compute
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in javaCompute.au3 > ..\..\compile\javaCompute.txt
cd ..\..

echo        [Finished]
