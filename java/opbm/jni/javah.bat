@echo off
set include=C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\INCLUDE;C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\ATLMFC\INCLUDE;C:\Program Files (x86)\Microsoft SDKs\Windows\v7.0A\include;c:\mingw64\inclucelib=C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\LIB;C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\ATLMFC\LIB;C:\Program Files (x86)\Microsoft SDKs\Windows\v7.0A\lib;c:\mingw64\libLIBPATH=C:\Windows\Microsoft.NET\Framework\v4.0.30319;C:\Windows\Microsoft.NET\Framework\v3.5;C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\LIB;C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\ATLMFC\LIB;C:\Program Files\Java\jdk1.6.0_25\include\;C:\Program Files\Java\jdk1.6.0_25\include\win32\;
set path=%path%;C:\Program Files\Java\jdk1.6.0_25\bin\;
cd c:\cana\java\opbm\build\classes\
javah -jni -o ../../jni/opbm64.h opbm.Opbm
cd c:\cana\java\opbm\jni\
pause
