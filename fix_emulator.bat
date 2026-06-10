@echo off
echo ==========================================
echo   CAMPSITE COMMANDER - EMULATOR FIXER
echo ==========================================
echo.
echo 1. Killing stuck emulator processes...
taskkill /F /IM qemu-system-x86_64.exe /T
taskkill /F /IM adb.exe /T
echo.
echo 2. Clearing emulator lock files...
del /s /q "%USERPROFILE%\.android\avd\*.lock"
echo.
echo 3. Restarting ADB server...
adb start-server
echo.
echo Done!
echo.
echo Now, go back to Android Studio:
echo - Look at the top toolbar (near the Play button).
echo - CHANGE 'New Run Config' to 'app'.
echo - Click Play again.
echo.
pause
