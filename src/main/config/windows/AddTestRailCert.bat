@ECHO off
REM *
REM Add or delete certificate for Oracle TestRail server
REM https://testrail.us.oracle.com/index.php?
REM *

ECHO *
ECHO * Go to JDK / JRE folder
ECHO *
cd %JAVA_HOME%
cd ..\jre*
cd lib\security

ECHO *
ECHO * Add oracle_testrail certificate
ECHO *
keytool -noprompt -importcert -alias testrail -keystore cacerts -file "C:\Automation\repository\resources\testrail\testrail.us.oracle.com.crt" -storepass changeit

REM *
REM * Delete oracle_testrail certificate
REM *
REM keytool -noprompt -delete -alias testrail -keystore cacerts -storepass changeit

TIMEOUT /T 5 /NOBREAK