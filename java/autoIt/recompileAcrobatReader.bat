@echo off
echo Adobe Acrobat Script Compile Begins
echo    +-- Adobe Acrobat 10.1 Install
cd acrobatReader\install
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in acrobatReaderInstall.au3 > ..\..\compile\acrobatReaderInstall.txt
cd ..\..

echo    +-- Adobe Acrobat 10.1 Un-install
cd acrobatReader\uninstall
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in acrobatReaderUninstall.au3 > ..\..\compile\acrobatReaderUninstall.txt
cd ..\..
echo        [Finished]
