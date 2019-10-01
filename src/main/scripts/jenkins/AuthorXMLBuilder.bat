@ECHO off
SETLOCAL enabledelayedexpansion

REM This script was created to run AuthorXMLBuilder tool
REM Script takes the project name as a paramater
REM Framework and Application project have to be compiled in order for java command to work

REM Set Application name
SET APPLICATION_NAME=%1
SET APPLICATION_NAME_LOWER_CASE=%APPLICATION_NAME%
CALL :ToLowerCase APPLICATION_NAME_LOWER_CASE

REM Path to Application classes and test classes
SET APPLICATION_CLASS_PATH=C:\Automation\Textura\%APPLICATION_NAME%\target\classes
SET APPLICATION_TEST_CLASS_PATH=C:\Automation\Textura\%APPLICATION_NAME%\target\test-classes

REM Path to Framework classes and test classes
SET FRAMEWORK_CLASS_PATH=C:\Automation\Textura\Framework\target\classes
SET FRAMEWORK_TEST_CLASS_PATH=C:\Automation\Textura\Framework\target\test-classes

REM Path to External libraries
SET EXTERNAL_LIBRARIES_PATH=C:\Automation\Textura\Framework\ConfigBuilder.jar

REM Name of the tool to execute
SET TOOL_NAME="com.textura.%APPLICATION_NAME_LOWER_CASE%.tools.%APPLICATION_NAME%AuthorXMLBuilder"

TITLE Run AuthorXMLBuilder script for %APPLICATION_NAME%


java -cp %APPLICATION_CLASS_PATH%;%APPLICATION_TEST_CLASS_PATH%;%FRAMEWORK_CLASS_PATH%;%FRAMEWORK_TEST_CLASS_PATH%;%EXTERNAL_LIBRARIES_PATH% %TOOL_NAME% %*


:ToLowerCase string -- convert uppercase strings to lowercase strings
FOR %%a IN ("A=a" "B=b" "C=c" "D=d" "E=e" "F=f" "G=g" "H=h" "I=i" "J=j" "K=k" "L=l" "M=m" "N=n" "O=o" "P=p" "Q=q" "R=r" "S=s" "T=t" "U=u" "V=v" "W=w" "X=x" "Y=y" "Z=z") do (
    CALL SET %~1=%%%~1:%%~a%%
)