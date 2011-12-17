@echo off
echo x264hd Script Compile Begins
echo    +-- installAviSynth
cd x264hd
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in installAviSynth.au3 > ..\compile\installAviSynth.txt
cd ..

echo    +-- x264hdBench
cd x264hd
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in x264hdBench.au3 > ..\compile\x264hdBench.txt
cd ..

echo    +-- uninstallAviSynth
cd x264hd
"C:\Program Files (x86)\AutoIt3\Aut2Exe\Aut2Exe.exe" /in uninstallAviSynth.au3 > ..\compile\uninstallAviSynth.txt
cd ..
echo        [Finished]
