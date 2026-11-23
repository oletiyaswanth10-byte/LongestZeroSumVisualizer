@echo off
REM Build & Run script for Longest Zero-Sum Subarray Visualizer (Windows)

setlocal
set PROJECT_DIR=%~dp0
set BIN_DIR=%PROJECT_DIR%bin

echo ========================================
echo  Longest Zero-Sum Subarray Visualizer
echo  Build Script
echo ========================================

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo.
echo [1/3] Compiling source files...
javac -d "%BIN_DIR%" ^
  "%PROJECT_DIR%src\utils\InputValidator.java" ^
  "%PROJECT_DIR%src\algorithms\LongestZeroSum.java" ^
  "%PROJECT_DIR%src\visualization\ArrayVisualizer.java" ^
  "%PROJECT_DIR%src\gui\ArrayPanel.java" ^
  "%PROJECT_DIR%src\visualization\HashMapVisualizer.java" ^
  "%PROJECT_DIR%src\gui\Dashboard.java" ^
  "%PROJECT_DIR%src\Main.java"

if errorlevel 1 (
    echo Compilation failed. Please check Java installation.
    pause
    exit /b 1
)

echo [2/3] Compiling test suite...
javac -d "%BIN_DIR%" ^
  "%PROJECT_DIR%src\utils\InputValidator.java" ^
  "%PROJECT_DIR%src\algorithms\LongestZeroSum.java" ^
  "%PROJECT_DIR%test\AlgorithmTest.java"

echo [3/3] Running algorithm tests...
java -cp "%BIN_DIR%" test.AlgorithmTest

if errorlevel 1 (
    echo Tests failed. Aborting launch.
    pause
    exit /b 1
)

echo.
echo Build successful! Launching application...
echo.
java -cp "%BIN_DIR%" Main
