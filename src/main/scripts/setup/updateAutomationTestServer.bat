@ECHO off

cd C:\Textura\tools-automationtestcodeservice

git pull origin develop

call mvn compile 
call mvn war:war
xcopy /f "C:\Textura\tools-automationtestcodeservice\target\Automation.war" "C:\apache-tomcat-8.0.12\webapps" /Y
