@echo off
rem Auto-detect JAVA_HOME if not set or path does not exist
if not exist "%JAVA_HOME%\bin\java.exe" (
    set "JAVA_HOME="
    for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk*" "C:\Program Files\Java\jdk*" "C:\Program Files\Amazon Corretto\jdk*") do (
        if exist "%%D\bin\java.exe" set "JAVA_HOME=%%D"
    )
)

if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

rem Auto-detect Maven executable
set "MVN_CMD=mvn"
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    for /d %%I in ("C:\Program Files\JetBrains\IntelliJ IDEA*") do (
        if exist "%%I\plugins\maven\lib\maven3\bin\mvn.cmd" set "MVN_CMD=%%I\plugins\maven\lib\maven3\bin\mvn.cmd"
    )
)

"%MVN_CMD%" %*
