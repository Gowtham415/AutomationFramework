<#
 # This script connects to VMs and does the following:
 #
 #   - Retrieves current Java Update registry settings
 #   - Disables Java Automatic Update functionality
 #
 #>

#Automation environments:
#"dfhubwin7qa1","dftlinkqa1","dftlinkqa2"
#"dfwin7qaauto1","dfwin7qaauto10","dfwin7qaauto11","dfwin7qaauto12","dfwin7qaauto13","dfwin7qaauto14","dfwin7qaauto15","dfwin7qaauto16","dfwin7qaauto17","dfwin7qaauto18","dfwin7qaauto19"
#"dfwin7qaauto2","dfwin7qaauto20","dfwin7qaauto21","dfwin7qaauto22","dfwin7qaauto23","dfwin7qaauto24","dfwin7qaauto25"
#"dfwin7qaauto3","dfwin7qaauto30","dfwin7qaauto31","dfwin7qaauto32","dfwin7qaauto33","dfwin7qaauto34","dfwin7qaauto35","dfwin7qaauto36","dfwin7qaauto37","dfwin7qaauto38","dfwin7qaauto39"
#"dfwin7qaauto4","dfwin7qaauto40","dfwin7qaauto41","dfwin7qaauto42","dfwin7qaauto43","dfwin7qaauto44","dfwin7qaauto45","dfwin7qaauto46","dfwin7qaauto47","dfwin7qaauto48","dfwin7qaauto49"
#"dfwin7qaauto5","dfwin7qaauto50","dfwin7qaauto51","dfwin7qaauto52","dfwin7qaauto53","dfwin7qaauto54"
#"dfwin7qaauto6","dfwin7qaauto60","dfwin7qaauto61","dfwin7qaauto62","dfwin7qaauto63","dfwin7qaauto64","dfwin7qaauto65","dfwin7qaauto66","dfwin7qaauto67","dfwin7qaauto68","dfwin7qaauto69"
#"dfwin7qaauto7","dfwin7qaauto70","dfwin7qaauto71","dfwin7qaauto72","dfwin7qaauto73","dfwin7qaauto74","dfwin7qaauto75","dfwin7qaauto76","dfwin7qaauto77","dfwin7qaauto78","dfwin7qaauto79"
#"dfwin7qaauto8","dfwin7qaauto80","dfwin7qaauto81","dfwin7qaauto82","dfwin7qaauto83","dfwin7qaauto84","dfwin7qaauto85","dfwin7qaauto9"


$AutomationEnvsGroup1 = @("dfwin7qaauto4","dfwin7qaauto40","dfwin7qaauto41","dfwin7qaauto42","dfwin7qaauto43","dfwin7qaauto44","dfwin7qaauto45","dfwin7qaauto46","dfwin7qaauto47","dfwin7qaauto48","dfwin7qaauto49")
Write-Host "Print Environment: " $AutomationEnvsGroup1

$UpdateJavaVariables = 
{
    # Print time stamp
    $GetTimeStamp = Get-Date -format G
    Write-Output "$GetTimeStamp Start updating VM remotely"

    Write-Host "Get EnableJavaUpdate"
    Get-ItemProperty -Path "HKLM:\SOFTWARE\Wow6432Node\JavaSoft\Java Update\Policy" -Name EnableJavaUpdate
    Write-Host "Set EnableJavaUpdate"
    Set-ItemProperty -Path "HKLM:\SOFTWARE\Wow6432Node\JavaSoft\Java Update\Policy" -Name EnableJavaUpdate -Value 0
    Write-Host "After Set EnableJavaUpdate"
    Get-ItemProperty -Path "HKLM:\SOFTWARE\Wow6432Node\JavaSoft\Java Update\Policy" -Name EnableJavaUpdate

    Write-Host "Get NotifyDownload"
    Get-ItemProperty -Path "HKLM:\SOFTWARE\Wow6432Node\JavaSoft\Java Update\Policy\jucheck" -Name NotifyDownload
    Write-Host "Set NotifyDownload"
    Set-ItemProperty -Path "HKLM:\SOFTWARE\Wow6432Node\JavaSoft\Java Update\Policy\jucheck" -Name NotifyDownload -Value 0
    Write-Host "After Set NotifyDownload"
    Get-ItemProperty -Path "HKLM:\SOFTWARE\Wow6432Node\JavaSoft\Java Update\Policy\jucheck" -Name NotifyDownload

    Write-Host "Get SunJavaUpdateSched"
    Get-ItemProperty -Path "HKLM:\SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Run" -Name SunJavaUpdateSched
    Write-Host "Delete SunJavaUpdateSched"
    Remove-ItemProperty -Path "HKLM:\SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Run" -Name SunJavaUpdateSched

}

foreach($Server in $AutomationEnvsGroup1) {
   
    # Set user on VM
    $User = "$Server\qaadmin"
    $Password = ConvertTo-SecureString -String "QAselenium7" -AsPlainText -Force
    $Credential = New-Object -TypeName System.Management.Automation.PSCredential -ArgumentList $User, $Password

    $GetTimeStamp = Get-Date -format G
    Write-Output "$GetTimeStamp Connect to remote server: $Server"
    Set-Item WSMan:\localhost\Client\TrustedHosts -Value $Server -Force

    # Print time stamp
    $GetTimeStamp = Get-Date -format G
    Write-Output "$GetTimeStamp Run script on remote server: $Server"
    Invoke-Command -ComputerName $Server -ScriptBlock $UpdateJavaVariables -credential $Credential -Debug
    
    # Print time stamp
    $GetTimeStamp = Get-Date -format G
    Write-Output "$GetTimeStamp Finished updating $Server"
}
