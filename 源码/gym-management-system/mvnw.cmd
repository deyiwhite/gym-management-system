@echo off
setlocal
set "MVNW_HOME=%~dp0"
set "MVNW_VERBOSE=false"
set "MVNW_REPO_URL=https://repo.maven.apache.org/maven2"
set "M2_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9"
set "MAVEN_USER_HOME=%USERPROFILE%\.m2"

if not exist "%MVNW_HOME%.mvn\wrapper\maven-wrapper.jar" (
    echo Maven Wrapper JAR not found. Run: mvn wrapper:wrapper
    goto :end
)

if not exist "%M2_HOME%" (
    echo Downloading Maven distribution...
    mkdir "%M2_HOME%" 2>nul
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip' -OutFile '%TEMP%\maven.zip'; Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath '%M2_HOME%\..'; Move-Item '%M2_HOME%\..\apache-maven-3.9.9\*' '%M2_HOME%'; Remove-Item '%M2_HOME%\..\apache-maven-3.9.9' -Recurse -Force}"
)

set "JAVA_HOME=%JAVA_HOME%"
if not defined JAVA_HOME set "JAVA_HOME=C:\Program Files\Java\jdk-21"

java -classpath "%MVNW_HOME%.mvn\wrapper\maven-wrapper.jar" ^
     "-Dmaven.multiModuleProjectDirectory=%MVNW_HOME%" ^
     org.apache.maven.wrapper.MavenWrapperMain %*

:end
