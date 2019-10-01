Local $window1 = $CmdLine[1]
Local $path = $CmdLine[2]
Local $timeout = $CmdLine[3]
Opt("WinTitleMatchMode",2)

While $timeout > 0
	  If (WinActivate($window1) <> 0) Then
		  MouseClick($window1)
		  Action()
		 ExitLoop
	  EndIf
		 $timeout = $timeout - 1
		 Sleep(1000)
WEnd

$timeout = 5000

 Func	 Action()
For $a = 0 To 5

	Send("{BACKSPACE}")
	$currentPath = $path
	Send($currentPath)
	Sleep(2500)
	Send("{ENTER}")
	Sleep(5000)
	 If (WinActivate($window1) <> 1) Then ExitLoop

Next
EndFunc


