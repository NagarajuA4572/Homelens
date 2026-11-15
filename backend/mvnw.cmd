@echo off
setlocal

set SCRIPT_DIR=%~dp0
set MAVEN_HOME=%SCRIPT_DIR%..\tools\apache-maven-3.9.9

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  echo Maven installation not found at %MAVEN_HOME%
  echo Install it once, then rerun this script.
  exit /b 1
)

cd /d "%SCRIPT_DIR%"
call "%MAVEN_HOME%\bin\mvn.cmd" %*
