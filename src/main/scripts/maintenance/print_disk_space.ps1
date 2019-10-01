
$User = $args[0]+"\qaadmin"
$PWord = ConvertTo-SecureString -String "QAselenium7" -AsPlainText -Force
$Cred = New-Object -TypeName System.Management.Automation.PSCredential -ArgumentList $User, $PWord

$disk = Get-WmiObject Win32_LogicalDisk -ComputerName $args[0] -Credential $Cred -Filter "DeviceID='C:'" | Select-Object Size,FreeSpace

Write-Host "Computer name = "$args[0] 
Write-Host "total disk size (GB): "($disk.Size/1GB)
Write-Host "free disk size (GB): ["($disk.FreeSpace/1GB)"]"