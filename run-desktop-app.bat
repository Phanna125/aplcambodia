@echo off
title Coffee Ordering System - Desktop App
cd /d "%~dp0desktop-app"
echo Starting Coffee Ordering System Desktop App via Maven...
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
"C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.5\plugins\maven\lib\maven3\bin\mvn.cmd" clean compile exec:java
pause
