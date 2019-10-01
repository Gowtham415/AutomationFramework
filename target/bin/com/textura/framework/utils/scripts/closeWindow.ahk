#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
#Warn  ; Recommended for catching common errors.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.

SetTitleMatchMode, 2
WinWait, %1%,,%2%
{
    IfWinExist %1%,,JavascriptServer
    {
        WinActivate
        WinClose 
        ExitApp 0
    }
    else
    {     
        ExitApp 1
    }
    
}

