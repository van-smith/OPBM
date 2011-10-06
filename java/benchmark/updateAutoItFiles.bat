@echo off
if EXIST dist\benchmark.jar move /Y dist\benchmark.jar .\benchmark.jar
copy /Y benchmark.jar ..\autoIt\java\compute\exe\benchmark.jar
copy /Y benchmark32.dll ..\autoIt\java\compute\exe\benchmark32.dll
copy /Y benchmark64.dll ..\autoIt\java\compute\exe\benchmark64.dll
