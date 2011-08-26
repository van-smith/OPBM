@echo off
echo 7-Zip Script Compile Begins
echo    +-- 7-Zip 9.20 Install
cd 7zip\install
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in 7zipinstall.au3 > ..\..\compile\7zipInstall.txt
cd ..\..

echo    +-- 7-Zip 9.20 Un-install
cd 7zip\uninstall
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in 7zipUninstall.au3 > ..\..\compile\7zipUninstall.txt
cd ..\..
echo        [Finished]
