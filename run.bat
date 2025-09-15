@echo off
echo Starting PharmaPoint Application...
echo.

cd /d "D:\PharmaPoint"

REM Compile the Java files
echo Compiling Java files...
javac -cp "src\lib\*" -d "out" src\**\*.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.

REM Run the application with native access enabled
echo Running PharmaPoint...
java --enable-native-access=ALL-UNNAMED -cp "out;src\lib\*" Main

pause
