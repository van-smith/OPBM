@echo off
echo Chrome Script Compile Begins
echo    +-- Chrome Google V8
cd chrome\googlev8
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in chromeGoogleV8.au3 > ..\..\compile\chromeGoogleV8.txt
cd ..\..

echo    +-- Chrome Install
cd chrome\install
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in chromeInstall.au3 > ..\..\compile\chromeInstall.txt
cd ..\..

echo    +-- Chrome Kraken
cd C:\cana\java\autoIT\chrome\kraken
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in chromeKraken.au3 > ..\..\compile\chromeKraken.txt
cd ..\..

echo    +-- Chrome Spider
cd C:\cana\java\autoIT\chrome\spider
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in chromeSpider.au3 > ..\..\compile\chromeSpider.txt
cd ..\..

echo    +-- Chrome Un-install
cd C:\cana\java\autoIT\chrome\uninstall
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in chromeUninstall.au3 > ..\..\compile\chromeUninstall.txt
cd ..\..
echo        [Finished]
