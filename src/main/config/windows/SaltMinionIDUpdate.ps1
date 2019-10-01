#
# Update Salt minion ID during startup of Windows
# This is needed for cloning, set minion ID to hostname + domain
#
# Salt ID example:
# id: estpmautowin200.us.oracle.com
# in C:\salt\conf\minion
#

Start-Sleep -s 240
$EnvironmentName = hostname
$EnvironmentName = "$EnvironmentName".ToLower()
$DomainName = ".us.oracle.com"
$SaltNewID = "id: " + $EnvironmentName + $DomainName

$line = Get-Content C:\salt\conf\minion | Select-String id: | Select-Object -ExpandProperty Line
$content = Get-Content C:\salt\conf\minion

Write-Output $SaltNewID

$content | ForEach-Object {$_ -replace $line, $SaltNewID} | Set-Content C:\salt\conf\minion

# Print output
# Get-Content C:\salt\conf\minion

# Restart salt minion for changes to take effect
Stop-Service -Name salt-minion
Start-Sleep -s 60
Start-Service -Name salt-minion

# Disable schedule task
Start-Sleep -s 3
Disable-ScheduledTask -TaskName SaltMinionIDUpdate
# Enable-ScheduledTask -TaskName SaltMinionIDUpdate

Get-ChildItem "$env:USERPROFILE\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup\SaltMinionIDUpdate.cmd" | ForEach-Object { Remove-Item $_ }
