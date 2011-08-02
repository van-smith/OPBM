@echo off
echo IE Script Compile Begins
echo    +-- IE Google V8
cd ie\googlev8
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in ieGoogleV8.au3 > ..\..\compile\ieGoogleV8.txt
cd ..\..

echo    +-- IE Kraken
cd ie\kraken
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in ieKraken.au3 > ..\..\compile\ieKraken.txt
cd ..\..

echo    +-- IE Spider
cd ie\spider
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in ieSpider.au3 > ..\..\compile\ieSpider.txt
cd ..\..
echo        [Finished]
