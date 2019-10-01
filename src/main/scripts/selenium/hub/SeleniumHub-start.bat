@ECHO off
SET SELENIUM_HUB_NAME=%COMPUTERNAME%
SET SELENIUM_HUB_CONFIG_PATH=C:\Automation\Textura\Framework\src\main\scripts\selenium\hub
SET SELENIUM_HUB_CONFIG_FILE=SeleniumHubConfig.json
SET SELENIUM_FILE_PATH=C:\Automation\repository\resources\selenium\2.53.1
SET SELENIUM_FILE_NAME=selenium-server-standalone-2.53.1.jar
TITLE Hub: %SELENIUM_HUB_NAME%

ECHO *
ECHO *
ECHO * Automation Hub: %SELENIUM_HUB_NAME%
ECHO * Configuration:  %SELENIUM_HUB_CONFIG_FILE%
ECHO *
ECHO *
java -jar %SELENIUM_FILE_PATH%\%SELENIUM_FILE_NAME% -role hub -hubConfig %SELENIUM_HUB_CONFIG_PATH%\%SELENIUM_HUB_CONFIG_FILE%
PAUSE
