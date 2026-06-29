@echo off
setlocal
set "MVNW_HOME=%~dp0"
set "MVNW_PROJECT_DIR=%MVNW_HOME:~0,-1%"
set "MAVEN_VERSION=3.9.9"
set "MAVEN_DIST=%MVNW_HOME%.mvn\wrapper\dists"
set "MAVEN_HOME=%MAVEN_DIST%\apache-maven-%MAVEN_VERSION%"
set "MAVEN_ZIP=%MVNW_HOME%.mvn\wrapper\tmp\apache-maven-%MAVEN_VERSION%-bin.zip"
set "MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"

if not exist "%MVNW_HOME%.mvn\wrapper\maven-wrapper.jar" (
    echo Maven Wrapper JAR not found. Run: mvn wrapper:wrapper
    goto :end
)

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Downloading Maven distribution...
    mkdir "%MVNW_HOME%.mvn\wrapper\tmp" 2>nul
    mkdir "%MAVEN_DIST%" 2>nul
    powershell -NoProfile -ExecutionPolicy Bypass -Command "& {Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ZIP%'; Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_DIST%' -Force}"
)

"%MAVEN_HOME%\bin\mvn.cmd" "-Dmaven.multiModuleProjectDirectory=%MVNW_PROJECT_DIR%" %*

:end
