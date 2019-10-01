Local $windowName = $CmdLine[1]
Opt("WinTitleMatchMode", 2) ;enable partial window title matching
WinSetState ( $windowName, "", @SW_MINIMIZE )
WinSetState ( $windowName, "", @SW_MAXIMIZE )
WinActivate ( $windowName ) ;bring window to front
