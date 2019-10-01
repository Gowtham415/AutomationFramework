Local $windowName = $CmdLine[1]
Local $path = $CmdLine[2]
Local $timeout = $CmdLine[3]

While $timeout > 0
	WinActivate($windowName, "")
	If WinActive($windowName) Then
		ExitLoop
	EndIf
	$timeout = $timeout - 1
	Sleep(1000)
WEnd
WinActivate($windowName, "")
If WinActive($windowName) Then
Sleep(100)
Send("!n")
Sleep(100)
Send($path)
Sleep(2500)
Send("!+o")
EndIf

Sleep(1000)

For $a = 0 To 10
	If Not(WinActive($windowName)) Then ExitLoop
	Send("!n")
	Send($path)
	Sleep(2500)
	Send("{ENTER}")
	Sleep(100)
	Send("!+o")
	Sleep(900)
Next
