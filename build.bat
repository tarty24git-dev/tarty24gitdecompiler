@echo off
echo ===================================================
echo Building CTH Trade Finance Maven Application
echo ===================================================
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed!
    exit /b %ERRORLEVEL%
)
echo [SUCCESS] Build completed successfully. Output JAR in target/cth-trade-finance-1.0.0-SNAPSHOT.jar
