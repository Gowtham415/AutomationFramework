@ECHO off
SET SELENIUM_HUB_NAME=%COMPUTERNAME%
SET SELENIUM_NODE_CONFIG_PATH=C:\Automation\Textura\Framework\src\main\scripts\selenium\node
SET SELENIUM_NODE_CONFIG_FILE=SeleniumNodeConfig.json
SET SELENIUM_FILE_PATH=C:\Automation\repository\resources\selenium\2.53.1
SET CHROMEDRIVER=C:\Automation\repository\resources\chromedriver\2.41\chromedriver.exe
SET IEDRIVER=C:\Automation\repository\resources\iedriver\2.35.3.0\IEDriverServer.exe
SET SELENIUM_FILE_NAME=selenium-server-standalone-2.53.1.jar
TITLE Node: %COMPUTERNAME% Hub: %SELENIUM_HUB_NAME% (N)

ECHO *
ECHO *
ECHO * Automation Node: %COMPUTERNAME%
ECHO * Automation Hub:  %SELENIUM_HUB_NAME%
ECHO * Configuration:   %SELENIUM_NODE_CONFIG_FILE%
ECHO *
ECHO *
sleep 3
java -jar %SELENIUM_FILE_PATH%\%SELENIUM_FILE_NAME% -Dwebdriver.ie.driver=%IEDRIVER% -Dwebdriver.chrome.driver=%CHROMEDRIVER% -role node -hub http://%SELENIUM_HUB_NAME%:4444/grid/register -nodeConfig %SELENIUM_NODE_CONFIG_PATH%\%SELENIUM_NODE_CONFIG_FILE%
PAUSE
