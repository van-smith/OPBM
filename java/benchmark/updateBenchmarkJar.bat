@echo off
if EXIST dist\benchmark.jar move /Y dist\benchmark.jar .\benchmark.jar
copy /Y benchmark.jar ..\autoIt\java\compute\exe\benchmark.jar

