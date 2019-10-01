@ECHO off
SET AUTOMATION_ARTIFACTS_PATH=C:\Automation_artifacts
SET AUTOMATION_PATH=C:\Automation
SET TEXTURA_PATH=C:\Automation\Textura
SET REPOSITORY_PATH=C:/Automation/repository
SET JFROG_PATH=C:\Automation_artifacts\jfrog
SET JFROG_REPOSITORY=http://dfwin7qaauto53:8081/artifactory/
SET GIT_PATH="C:\Program Files\Git\bin\git"
SET BITBUCKET_PATH=https://bitbucket.texturallc.net/scm/at

IF EXIST %AUTOMATION_PATH% (
ECHO *
ECHO *
ECHO * Automation folder already exists: %AUTOMATION_PATH%
ECHO * Unable to continue
ECHO *
ECHO *
) ELSE (
%JFROG_PATH% rt dl --url %JFROG_REPOSITORY% --user readonly --password QAselenium7 --split-count=0 "tools-maven" %AUTOMATION_PATH%/Maven/
%JFROG_PATH% rt dl --url %JFROG_REPOSITORY% --user readonly --password QAselenium7 --split-count=0 "repository-maven" %REPOSITORY_PATH%/maven/
%JFROG_PATH% rt dl --url %JFROG_REPOSITORY% --user readonly --password QAselenium7 --split-count=0 "repository-resources" %REPOSITORY_PATH%/resources/
%GIT_PATH% clone %BITBUCKET_PATH%/textura.git %TEXTURA_PATH%
rmdir /S /Q %TEXTURA_PATH%\.git
%GIT_PATH% clone %BITBUCKET_PATH%/framework.git %TEXTURA_PATH%\Framework
%GIT_PATH% clone %BITBUCKET_PATH%/bo.git %TEXTURA_PATH%\BO
%GIT_PATH% clone %BITBUCKET_PATH%/bs.git %TEXTURA_PATH%\BS
%GIT_PATH% clone %BITBUCKET_PATH%/CPM.git %TEXTURA_PATH%\CPM
%GIT_PATH% clone %BITBUCKET_PATH%/gdb.git %TEXTURA_PATH%\GDB
%GIT_PATH% clone %BITBUCKET_PATH%/lat.git %TEXTURA_PATH%\LAT
%GIT_PATH% clone %BITBUCKET_PATH%/pe.git %TEXTURA_PATH%\PE
%GIT_PATH% clone %BITBUCKET_PATH%/pqm.git %TEXTURA_PATH%\PQM
%GIT_PATH% clone %BITBUCKET_PATH%/se.git %TEXTURA_PATH%\SE
%GIT_PATH% clone %BITBUCKET_PATH%/tcs.git %TEXTURA_PATH%\TCS
#%GIT_PATH% clone %BITBUCKET_PATH%/tp.git %TEXTURA_PATH%\TP //Bitbucket project is crashing
)
ECHO *
ECHO *
ECHO * Setup complete
ECHO *
ECHO *
PAUSE
