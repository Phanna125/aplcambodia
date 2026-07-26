@echo off
title Coffee Ordering System - Spring Boot REST API
cd /d "%~dp0backend"
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo Starting Coffee Ordering System Spring Boot REST API ^& Swagger UI...
"C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.5\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
pause
