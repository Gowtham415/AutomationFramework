Local $window1 = $CmdLine[1]
Local $window2 = $CmdLine[2]
Local $path = $CmdLine[3]
Local $timeout = $CmdLine[4]
Opt("WinTitleMatchMode",2)

While $timeout > 0
	  If (WinActivate($window1) <> 0) Then
		  MouseClick($window1)
		 ExitLoop
	  EndIf
		 $timeout = $timeout - 1
		 Sleep(1000)
WEnd

$timeout = $CmdLine[4]

	While $timeout > 0
	  If WinActivate($window1) Then
		 ControlClick($window1, "", "left")
		 Sleep(2000)
		 Send("!s")
		 Send("{ENTER}")
		 Sleep(2000)
			Action()
		If WinActivate($window2) Then
		   Sleep(1000)
		   WinClose($window2)
		EndIf
		If WinActivate($window1) Then
		   Sleep(1000)
		   WinClose($window1)
		EndIf
		 ExitLoop
	  Else
    EndIf
	$timeout = $timeout - 1
	Sleep(1000)
 WEnd

 Func	 Action()
For $a = 0 To 5
	If Not(WinActive($window2)) Then ExitLoop
	Send("!n")
	Send("{BACKSPACE}")
	$currentPath = $path
	Send($currentPath)
	Sleep(2500)
	Send("{ENTER}")
	Sleep(1000)
	Send("{Tab}")
	Sleep(1000)
	Send("{Tab}")
	Sleep(2000)
Next
EndFunc


