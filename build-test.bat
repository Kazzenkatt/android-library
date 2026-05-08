@echo off
echo ========================================
echo Project02 Baseline Build Test
echo ========================================
echo.

cd C:\dev\AndroidStudioProjects\Project02

set JAVA_HOME=C:\Program Files\Java\jdk-11.0.30+7
set PATH=%JAVA_HOME%\bin;%PATH%

echo Java Version:
java -version
echo.

echo Starting build...
gradlew.bat clean build

echo.
echo ========================================
echo Build Complete
echo ========================================
