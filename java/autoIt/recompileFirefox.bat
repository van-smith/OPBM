@echo off
echo Firefox Script Compile Begins
echo    +-- Firefox Google V8
cd firefox\googlev8
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in firefoxGoogleV8.au3 > ..\..\compile\firefoxGoogleV8.txt
cd ..\..

echo    +-- Firefox Install
cd firefox\install
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in firefoxInstall.au3 > ..\..\compile\firefoxInstall.txt
cd ..\..

echo    +-- Firefox Kraken
cd firefox\kraken
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in firefoxKraken.au3 > ..\..\compile\firefoxKraken.txt
cd ..\..

echo    +-- Firefox Spider
cd firefox\spider
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in firefoxSpider.au3 > ..\..\compile\firefoxSpider.txt
cd ..\..

echo    +-- Firefox Un-install
cd firefox\uninstall
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in firefoxUninstall.au3 > ..\..\compile\firefoxUninstall.txt
cd ..\..
echo        [Finished]
