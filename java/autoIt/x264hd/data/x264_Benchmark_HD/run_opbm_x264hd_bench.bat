cls
@echo off

del 720p_results*.rtf
cd test

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
set target=opbm-x264hd
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

:clean-up
del *.log
del *.stats
del *.mbtree
del ..\*.mp4
move 720p_results*.rtf ..

:publish-new
echo.
echo All runs complete!
echo.
echo Data written to results-%target%.rtf
echo.

cd ..

:exit