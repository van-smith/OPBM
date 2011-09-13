@echo off
call recompile7zip.bat
echo .
call recompileFileio.bat
echo .
call recompileAcrobatReader.bat
echo .
call recompileOffice2010.bat
echo .
call recompileChrome.bat
echo .
call recompileFirefox.bat
echo .
call recompileIE.bat
echo .
call recompileOpera.bat
echo .
call recompileSafari.bat
echo .
call recompileSpinup.bat
echo [Recompile All Complete]
echo [See compile\ directory for text of any errors encountered]
