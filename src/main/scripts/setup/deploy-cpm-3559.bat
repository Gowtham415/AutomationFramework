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
%GIT_PATH% clone %BITBUCKET_PATH%/framework-3559.git %TEXTURA_PATH%\Framework
%GIT_PATH% clone %BITBUCKET_PATH%/cpm-3559.git %TEXTURA_PATH%\CPM
)
ECHO *
ECHO *
ECHO * Setup complete
ECHO *
ECHO *
PAUSE
