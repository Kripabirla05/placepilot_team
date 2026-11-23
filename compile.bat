@echo off
dir /s /b src\*.java > sources.txt
if not exist out mkdir out
javac -d out @sources.txt
if %errorlevel%==0 (echo Compiled successfully.) else (echo Compile failed.)
