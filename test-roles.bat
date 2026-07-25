@echo off
title Launch Coffee Shop - 3 Roles

echo ==============================================
echo 1) Starting Backend API...
echo ==============================================
start cmd /c "run-backend.bat"

echo Waiting 8 seconds for backend to boot up...
timeout /t 8 /nobreak >nul

cd /d "%~dp0desktop-app"
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
set "MAVEN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.5\plugins\maven\lib\maven3\bin\mvn.cmd"

echo ==============================================
echo 2) Compiling Desktop App...
echo ==============================================
call "%MAVEN_CMD%" clean compile

echo ==============================================
echo 3) Launching 3 Desktop Windows...
echo ==============================================
echo Starting Window 1 (For Customer)
start cmd /c ""%MAVEN_CMD%" exec:java"
timeout /t 2 >nul

echo Starting Window 2 (For Cashier)
start cmd /c ""%MAVEN_CMD%" exec:java"
timeout /t 2 >nul

echo Starting Window 3 (For Barista)
start cmd /c ""%MAVEN_CMD%" exec:java"

echo All systems launched successfully!
pause
