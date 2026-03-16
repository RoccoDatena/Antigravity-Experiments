@echo off
@setlocal

@REM ─── Requires Java 17+ in PATH  ───────────────────────────────────────────────
@REM     Download: https://adoptium.net/temurin/releases/?version=17
@REM     Set JAVA_HOME if needed: set JAVA_HOME=C:\path\to\jdk-17
set MAVEN_OPTS=-Xms128m -Xmx512m -XX:MaxMetaspaceSize=256m

set MAVEN_VERSION=3.9.6
set WRAPPER_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%-bin
set MAVEN_HOME=%WRAPPER_DIR%\apache-maven-%MAVEN_VERSION%
set MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd
set DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip
set ZIP_FILE=%WRAPPER_DIR%\apache-maven-%MAVEN_VERSION%-bin.zip

@REM ─── Verify Java is available ───────────────────────────────────────────────
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java not found. Please install Java 17+ and add it to PATH.
    exit /b 1
)

@REM ─── Use cached Maven if already downloaded ──────────────────────────────────
if exist "%MAVEN_CMD%" goto runMaven

@REM ─── Download and extract Maven ──────────────────────────────────────────────
echo.
echo [ Maven Wrapper ] Apache Maven %MAVEN_VERSION% not found. Downloading...
echo   From: %DOWNLOAD_URL%
echo   To  : %WRAPPER_DIR%
echo.

if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"

powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%ZIP_FILE%' -UseBasicParsing }"
if %ERRORLEVEL% neq 0 (
    echo ERROR: Download failed. Check your internet connection.
    exit /b 1
)

echo Extracting...
powershell -Command "Expand-Archive -Path '%ZIP_FILE%' -DestinationPath '%WRAPPER_DIR%' -Force"
del /f /q "%ZIP_FILE%"

echo.
echo [ Maven Wrapper ] Maven %MAVEN_VERSION% ready at: %MAVEN_HOME%
echo.

:runMaven
"%MAVEN_CMD%" %*
