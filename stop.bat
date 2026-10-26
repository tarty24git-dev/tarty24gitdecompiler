@echo off
echo Stopping CTH Trade Finance System (Windows)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do taskkill /f /pid %%a 2>nul
echo CTH Trade Finance System stopped.
