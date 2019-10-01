@echo off
cd %USERPROFILE%\AppData\Local\Temp
SET var=%cd%
if %var%==%USERPROFILE%\AppData\Local\Temp (
attrib +r +s *.bat > nul
for /d %%D in (*) do rd /s /q "%%D"
del /q *
attrib -r -s *.bat > nul
)