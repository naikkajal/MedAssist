@echo off
echo Setting up environment variables...

:: Set JAVA_HOME to the Android Studio JDK we found
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"

:: Add Android SDK platform-tools to PATH (for adb)
set "PATH=%PATH%;C:\Users\naikk\AppData\Local\Android\Sdk\platform-tools"

echo JAVA_HOME set to: %JAVA_HOME%
echo Checking ADB connection...
adb devices

echo.
echo Building and installing debug APK...
call .\gradlew.bat installDebug

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build or Install Failed! Check the output above.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Launching LoginActivity...
adb shell am start -n com.example.login1/.LoginActivity

echo.
echo Done!
pause
