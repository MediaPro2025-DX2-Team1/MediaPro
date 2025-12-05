@echo off
REM MediaPro Component Preview Script
REM Usage: preview.bat <ComponentName>
REM        preview.bat list

setlocal

if "%~1"=="" (
    echo Usage: preview.bat ^<ComponentName^>
    echo        preview.bat list
    echo.
    echo Run 'preview.bat list' to see available components.
    exit /b 1
)

cd /d "%~dp0"
gradlew.bat run --args="--preview %~1" -q
