@echo off
echo Safari Script Compile Begins
echo    +-- Safari Google V8
cd safari\googlev8
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in safariGoogleV8.au3 > ..\..\compile\safariGoogleV8.txt
cd ..\..

echo    +-- Safari Install
cd safari\install
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in safariInstall.au3 > ..\..\compile\safariInstall.txt
cd ..\..

echo    +-- Safari Kraken
cd safari\kraken
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in safariKraken.au3 > ..\..\compile\safariKraken.txt
cd ..\..

echo    +-- Safari Spider
cd safari\spider
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in safariSpider.au3 > ..\..\compile\safariSpider.txt
cd ..\..

echo    +-- Safari Un-install
cd safari\uninstall
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in safariUninstall.au3 > ..\..\compile\safariUninstall.txt
cd ..\..
echo        [Finished]
