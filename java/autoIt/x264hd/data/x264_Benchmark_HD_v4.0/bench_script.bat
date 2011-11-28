cls
@echo off
cd test

:avisynth_check32
if exist "%PROGRAMFILES%\AviSynth 2.5" goto start

:avisynth_check64
if exist "%PROGRAMFILES(x86)%\AviSynth 2.5" goto start
goto error

:error
echo Attention: Avisynth 2.5.8 not found on this machine
echo You need to download and install avisynth 2.5.8 for this benchmark to run
echo Now feeding your browser the link to Avisynth 2.5.8
echo.
echo Please try again once you have installed AviSynth
"Link to download Avisynth.url"
set /p target=hit ENTER to close this window
goto exit

:start
echo.
echo ,@@@@+``````'@@@@@@@@@@@@@@@@@@@@@@@@ .........................................
echo `@@@@@``````@@@@@@@@@@@@@@@@@@@@@@@@@
echo ``@@@@@````@@@@@```````````````````.@           x264 HD Benchmark v4.0
echo ``.@@@@'``+@@@@````````````````````.@                 by graysky
echo ```#@@@@``@@@@,````````````````````.@           updated by Adrian Wong
echo ````@@@@@@@@@@`#@@@````@@@````:@'``.@  	       and Dashken of Tech ARP
echo `````@@@@@@@@``@:;@@``@@`.````@@+``.@      
echo `````+@@@@@@``````@@`'@`+,```@`@'``.@      2-Pass Encode of an HD MPEG2 source
echo ``````@@@@@+`````.@;`@@#@@'`@:`@+``.@      1280x720 @ 3,963 kbps; Qf = 0.18
echo ``````@@@@@@````,@+``@@``@@`@@@@@,`.@      
echo `````@@@@@@@@``+@:```:@``@#`...@+``.@      Quality settings via Sharktooth's
echo ````+@@@@@@@@;`@@@@@``@@@@`````@'``.@      SA-Blu-ray MeGUI profile
echo ````@@@@`#@@@@`````````````````````.@      
echo ```@@@@#``@@@@@````````````````````.@      Subpixel Refinement = 6 (RDO)
echo ``#@@@@```,@@@@'```````````````````.@      M.E. Algorithm = multi-hexagon
echo `,@@@@.````@@@@@```````````````````.@
echo `@@@@@``````@@@@@@@@@@@@@@@@@@@@@@@@@ .........................................
echo.
echo Please enter the name you'd like to give the results text file.
echo For example, you might want to use your current CPU settings as the file name
set /p target=e.g. the CPU multiplier and FSB (like 9x333 for example):
echo.
echo The results will be written to 720p_results-%target%.rtf
echo.
echo This new version uses the latest x264 r1913 32-bit encoder so it will run much
echo better on multi-core processors. It will also better reflect the performance of
echo the latest x264 encoders.
echo.
echo The benchmark will now encode a 720p (1280x720) HD sample.
echo.
echo There will be 4 runs with 2 passes per run = a total of 8 passes
echo. 
echo It may take a long time with slower processors, so PLEASE BE PATIENT!
echo.
echo For accurate results :
echo    - CPU priority for x264.exe has been set to HIGH
echo    - Do NOT use your PC while the benchmark is running!
echo.

:run1-32
echo.
echo    --- Now Starting 32-bit Run 1 : Pass 1 of 2 ---
echo.
start /high /b x264-32 --quiet --pass 1 --bitrate 3959 --stats "1.stats" --level 4.1 --keyint 24 --min-keyint 1 --bframes 3 --direct auto --subme 1 --analyse none --ipratio 1.1 --pbratio 1.1 --vbv-bufsize 30000 --vbv-maxrate 38000 --qcomp 0.5 --me dia --threads auto --thread-input --sar 1:1  --output NUL "test-720p.avs" --mvrange 511 --aud 2>&1 | tee 32run1pass1.log
echo.
echo    --- Now Starting 32-bit Run 1 : Pass 2 of 2 ---
echo.
start /high /b x264-32 --quiet --pass 2 --bitrate 3959 --stats "1.stats" --level 4.1 --keyint 24 --min-keyint 1 --ref 3 --mixed-refs --bframes 3 --weightb --direct auto --subme 6 --analyse p8x8,b8x8,i4x4,i8x8 --8x8dct --ipratio 1.1 --pbratio 1.1 --vbv-bufsize 30000 --vbv-maxrate 38000 --qcomp 0.5 --me umh --threads auto --thread-input --sar 1:1 --output "../run1-720p.mp4" "test-720p.avs" --mvrange 511 --aud 2>&1 | tee 32run1pass2.log
echo.

:run2-32
echo    --- Now Starting 32-bit Run 2 : Pass 1 of 2 ---
echo.
start /high /b x264-32 --quiet --pass 1 --bitrate 3959 --stats "2.stats" --level 4.1 --keyint 24 --min-keyint 1 --bframes 3 --direct auto --subme 1 --analyse none --ipratio 1.1 --pbratio 1.1 --vbv-bufsize 30000 --vbv-maxrate 38000 --qcomp 0.5 --me dia --threads auto --thread-input --sar 1:1 --output NUL "test-720p.avs" --mvrange 511 --aud 2>&1 | tee 32run2pass1.log
echo.
echo    --- Now Starting 32-bit Run 2 : Pass 2 of 2 ---
echo.
start /high /b x264-32 --quiet --pass 2 --bitrate 3959 --stats "2.stats" --level 4.1 --keyint 24 --min-keyint 1 --ref 3 --mixed-refs --bframes 3 --weightb --direct auto --subme 6 --analyse p8x8,b8x8,i4x4,i8x8 --8x8dct --ipratio 1.1 --pbratio 1.1 --vbv-bufsize 30000 --vbv-maxrate 38000 --qcomp 0.5 --me umh --threads auto --thread-input --sar 1:1 --output "../run2-720p.mp4" "test-720p.avs" --mvrange 511 --aud 2>&1 | tee 32run2pass2.log
echo.

:run3-32
echo    --- Now Starting 32-bit Run 3 : Pass 1 of 2 ---
echo.
start /high /b x264-32 --quiet --pass 1 --bitrate 3959 --stats "3.stats" --level 4.1 --keyint 24 --min-keyint 1 --bframes 3 --direct auto --subme 1 --analyse none --ipratio 1.1 --pbratio 1.1 --vbv-bufsize 30000 --vbv-maxrate 38000 --qcomp 0.5 --me dia --threads auto --thread-input --sar 1:1 --output NUL "test-720p.avs" --mvrange 511 --aud 2>&1 | tee 32run3pass1.log
echo.
echo    --- Now Starting 32-bit Run 3 : Pass 2 of 2 ---
echo.
start /high /b x264-32 --quiet --pass 2 --bitrate 3959 --stats "3.stats" --level 4.1 --keyint 24 --min-keyint 1 --ref 3 --mixed-refs --bframes 3 --weightb --direct auto --subme 6 --analyse p8x8,b8x8,i4x4,i8x8 --8x8dct --ipratio 1.1 --pbratio 1.1 --vbv-bufsize 30000 --vbv-maxrate 38000 --qcomp 0.5 --me umh --threads auto --thread-input --sar 1:1 --output "../run3-720p.mp4" "test-720p.avs" --mvrange 511 --aud 2>&1 | tee 32run3pass2.log
echo.

:run4-32
echo    --- Now Starting 32-bit Run 4 : Pass 1 of 2 ---
echo.
start /high /b x264-32 --quiet --pass 1 --bitrate 3959 --stats "4.stats" --level 4.1 --keyint 24 --min-keyint 1 --bframes 3 --direct auto --subme 1 --analyse none --ipratio 1.1 --pbratio 1.1 --vbv-bufsize 30000 --vbv-maxrate 38000 --qcomp 0.5 --me dia --threads auto --thread-input --sar 1:1 --output NUL "test-720p.avs" --mvrange 511 --aud 2>&1 | tee 32run4pass1.log
echo.
echo    --- Now Starting 32-bit Run 4 : Pass 2 of 2 ---
echo.
start /high /b x264-32 --quiet --pass 2 --bitrate 3959 --stats "4.stats" --level 4.1 --keyint 24 --min-keyint 1 --ref 3 --mixed-refs --bframes 3 --weightb --direct auto --subme 6 --analyse p8x8,b8x8,i4x4,i8x8 --8x8dct --ipratio 1.1 --pbratio 1.1 --vbv-bufsize 30000 --vbv-maxrate 38000 --qcomp 0.5 --me umh --threads auto --thread-input --sar 1:1 --output "../run4-720p.mp4" "test-720p.avs" --mvrange 511 --aud 2>&1 | tee 32run4pass2.log
echo.

REM Load CPU-Z while SpeedStep is off to accurately report the CPU multiplier
start /low /b cpuz -txt=system_details

:analyze-new
echo x264 HD BENCHMARK 4.0 RESULTS >> 720p_results-"%target%.rtf
echo. >> 720p_results-"%target%.rtf
echo Please do NOT compare it with older versions of the benchmark! >> 720p_results-"%target%.rtf
echo Please copy/paste everything below the line to to report your data >> 720p_results-"%target%.rtf
echo to http://forums.techarp.com/reviews-articles/26363-x264-hd-benchmark-4-0-a.html >> 720p_results-"%target%.rtf
echo. >> 720p_results-"%target%.rtf
echo ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ >> 720p_results-"%target%.rtf
echo. >> 720p_results-"%target%.rtf
echo Results for x264.exe r1913 >> 720p_results-"%target%.rtf
echo ========================== >> 720p_results-"%target%.rtf
echo. >> 720p_results-"%target%.rtf
echo Pass 1 >> 720p_results-"%target%.rtf
echo ------ >> 720p_results-"%target%.rtf
grep -U "encoded 1442" 32run1pass1.log >> 720p_results-"%target%.rtf
grep -U "encoded 1442" 32run2pass1.log >> 720p_results-"%target%.rtf
grep -U "encoded 1442" 32run3pass1.log >> 720p_results-"%target%.rtf
grep -U "encoded 1442" 32run4pass1.log >> 720p_results-"%target%.rtf
echo. >> 720p_results-"%target%.rtf
echo Pass 2 >> 720p_results-"%target%.rtf
echo ------ >> 720p_results-"%target%.rtf
grep -U "encoded 1442" 32run1pass2.log >> 720p_results-"%target%.rtf
grep -U "encoded 1442" 32run2pass2.log >> 720p_results-"%target%.rtf
grep -U "encoded 1442" 32run3pass2.log >> 720p_results-"%target%.rtf
grep -U "encoded 1442" 32run4pass2.log >> 720p_results-"%target%.rtf
echo. >> 720p_results-"%target%.rtf

:end
REM Insert system info into report

echo. >> 720p_results-"%target%.rtf
echo System Details >> 720p_results-"%target%.rtf
echo -------------- >> 720p_results-"%target%.rtf
grep -U "Name" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Codename" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Specification" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Core Stepping" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Technology" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Stock frequency" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Core Speed" system_details.txt >> 720p_results-"%target%.rtf
grep -U "FID range" system_details.txt >> 720p_results-"%target%.rtf
echo. >> 720p_results-"%target%.rtf
grep -U "Northbridge" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Southbridge" system_details.txt >> 720p_results-"%target%.rtf
echo. >> 720p_results-"%target%.rtf
grep -U "CAS#" system_details.txt >> 720p_results-"%target%.rtf
grep -U "RAS# Precharge" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Cycle Time (tRAS)" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Command Rate" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Memory Frequency" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Memory Type" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Memory Size" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Channels" system_details.txt >> 720p_results-"%target%.rtf
echo. >> 720p_results-"%target%.rtf
grep -U "Windows Version" system_details.txt >> 720p_results-"%target%.rtf
echo. >> 720p_results-"%target%.rtf
grep -U "max VID" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Voltage sensor 0" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Number of processors" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Number of threads" system_details.txt >> 720p_results-"%target%.rtf
grep -U "L2 cache" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Instructions sets" system_details.txt >> 720p_results-"%target%.rtf
grep -U "Package" system_details.txt >> 720p_results-"%target%.rtf
echo. >> 720p_results-"%target%.rtf
grep -U "Temperature" system_details.txt >> 720p_results-"%target%.rtf

:clean-up
del *.log
del *.stats
del *.mbtree
del ..\*.mp4
del system_details.txt
move 720p_results*.rtf ..

:publish-new
echo.
echo All runs complete!
echo.
echo Data written to results-%target%.rtf
echo.
set /p target=Hit the ENTER key to close this window and open your report!
cd..
start /b wordpad "720p_results-%target%.rtf"

:exit