@echo off
echo Opera Script Compile Begins
echo    +-- Opera Google V8
cd opera\googlev8
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in operaGoogleV8.au3 > ..\..\compile\operaGoogleV8.txt
cd ..\..

echo    +-- Opera Install
cd opera\install
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in operaInstall.au3 > ..\..\compile\operaInstall.txt
cd ..\..

echo    +-- Opera Kraken
cd opera\kraken
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in operaKraken.au3 > ..\..\compile\operaKraken.txt
cd ..\..

echo    +-- Opera Spider
cd opera\spider
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in operaSpider.au3 > ..\..\compile\operaSpider.txt
cd ..\..

echo    +-- Opera Un-install
cd opera\uninstall
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in operaUninstall.au3 > ..\..\compile\operaUninstall.txt
cd ..\..
echo        [Finished]
