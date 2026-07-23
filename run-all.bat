@echo off
title Launch Coffee Ordering System
echo Starting Backend API...
start cmd /c "run-backend.bat"

echo Waiting for backend to initialize (10 seconds)...
timeout /t 10 /nobreak >nul

echo Starting Desktop App...
start cmd /c "run-desktop-app.bat"

echo Both applications launched in separate windows!
pause
