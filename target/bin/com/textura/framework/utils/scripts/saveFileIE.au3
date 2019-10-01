Local $window1 = $CmdLine[1]
Local $window2 = $CmdLine[2]
Local $window3 = $CmdLine[3]
Local $path = $CmdLine[4]
Local $timeout = $CmdLine[5]

WinWaitActive($window1, "", $timeout)
If WinActive($window1) Then

Send("{LEFT}")
Send("{ENTER}")

WinWaitActive($window2, "", $timeout)
If WinActive($window2) Then
Send($path)
Sleep(2500)
Send("{ENTER}")
WinWaitActive($window3, "", $timeout)
Send("{ENTER}")
EndIf

EndIf

Sleep(3000)
