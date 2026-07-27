@echo off
setlocal enabledelayedexpansion
title Coffee Ordering System - Spring Boot REST API
cd /d "%~dp0backend"

echo Starting Coffee Ordering System Spring Boot REST API and Swagger UI...

rem Check if current JAVA_HOME is valid
if exist "%JAVA_HOME%\bin\java.exe" goto HAVE_JAVA

rem Check common JDK locations
if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
    goto HAVE_JAVA
)

for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
    if exist "%%D\bin\java.exe" set "JAVA_HOME=%%D" & goto HAVE_JAVA
)

for /d %%D in ("C:\Program Files\Java\jdk*") do (
    if exist "%%D\bin\java.exe" set "JAVA_HOME=%%D" & goto HAVE_JAVA
)

for /d %%D in ("C:\Program Files\Amazon Corretto\jdk*") do (
    if exist "%%D\bin\java.exe" set "JAVA_HOME=%%D" & goto HAVE_JAVA
)

:HAVE_JAVA
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"

rem Locate Maven
set "MVN_CMD=mvn"
where mvn >nul 2>&1
if %errorlevel% equ 0 goto RUN_APP

if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.5\plugins\maven\lib\maven3\bin\mvn.cmd" (
    set "MVN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.5\plugins\maven\lib\maven3\bin\mvn.cmd"
    goto RUN_APP
)

for /d %%I in ("C:\Program Files\JetBrains\IntelliJ IDEA*") do (
    if exist "%%I\plugins\maven\lib\maven3\bin\mvn.cmd" (
        set "MVN_CMD=%%I\plugins\maven\lib\maven3\bin\mvn.cmd"
        goto RUN_APP
    )
)

:RUN_APP
"%MVN_CMD%" spring-boot:run
pause
endlocal
