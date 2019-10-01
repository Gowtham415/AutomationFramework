<#
 # This script does the following:
 #   - kills processes 
 #   - deletes files in TMP folder
 # 
 # To add additional environment to the script:
 #   1. Add environment name which is name of the hub
 #   2. Add environment hub nodes as array, hub should be the first element of the array
 #
 #>


<#
 # Automation Environments:
#>

$AutomationEnvironments = @{

    "DFWIN7QAAUTO10" = @("DFWIN7QAAUTO10","DFWIN7QAAUTO11","DFWIN7QAAUTO12","DFWIN7QAAUTO13","DFWIN7QAAUTO14","DFWIN7QAAUTO15","DFWIN7QAAUTO16")
	
	"DFWIN7QAAUTO80" = @("DFWIN7QAAUTO80","DFWIN7QAAUTO79","DFWIN7QAAUTO81","DFWIN7QAAUTO82","DFWIN7QAAUTO83","DFWIN7QAAUTO84")

	"DFWIN7QAAUTO90" = @("DFWIN7QAAUTO90","DFWIN7QAAUTO85","DFWIN7QAAUTO86","DFWIN7QAAUTO87","DFWIN7QAAUTO88","DFWIN7QAAUTO89")

    "DFWIN7QAAUTO51" = @("DFWIN7QAAUTO17","DFWIN7QAAUTO18","DFWIN7QAAUTO19","DFWIN7QAAUTO30","DFWIN7QAAUTO31",
                         "DFWIN7QAAUTO32","DFWIN7QAAUTO33","DFWIN7QAAUTO34","DFWIN7QAAUTO35","DFWIN7QAAUTO36",
                         "DFWIN7QAAUTO37","DFWIN7QAAUTO38","DFWIN7QAAUTO39","DFWIN7QAAUTO40","DFWIN7QAAUTO41",
                         "DFWIN7QAAUTO42","DFWIN7QAAUTO43","DFWIN7QAAUTO44","DFWIN7QAAUTO45","DFWIN7QAAUTO46",
                         "DFWIN7QAAUTO47","DFWIN7QAAUTO48","DFWIN7QAAUTO49","DFWIN7QAAUTO50","DFWIN7QAAUTO52",
                         "DFWIN7QAAUTO62","DFWIN7QAAUTO63","DFWIN7QAAUTO64","DFWIN7QAAUTO65","DFWIN7QAAUTO66",
                         "DFWIN7QAAUTO67","DFWIN7QAAUTO68","DFWIN7QAAUTO69","DFWIN7QAAUTO71","DFWIN7QAAUTO72",
                         "DFWIN7QAAUTO73","DFWIN7QAAUTO2","DFWIN7QAAUTO4","DFWIN7QAAUTO8","DFWIN7QAAUTO9",
                         "DFWIN7QAAUTO51")

    "DFWIN7QAAUTO20" = @("DFWIN7QAAUTO20", "DFWIN7QAAUTO25","DFWIN7QAAUTO24","DFWIN7QAAUTO22","DFWIN7QAAUTO23","DFWIN7QAAUTO21","DFWIN7QAAUTO74","DFWIN7QAAUTO76","DFWIN7QAAUTO77","DFWIN7QAAUTO78")
	
    "DFWIN7QAAUTO75" = @("DFWIN7QAAUTO75", "DFWIN7QAAUTO25","DFWIN7QAAUTO24","DFWIN7QAAUTO22","DFWIN7QAAUTO23","DFWIN7QAAUTO20","DFWIN7QAAUTO21","DFWIN7QAAUTO74","DFWIN7QAAUTO76","DFWIN7QAAUTO77","DFWIN7QAAUTO78")
    
    "ESTPMWIN50-1" = @("ESTPMWIN50-1","ESTPMWIN60-1","ESTPMWIN60-2","ESTPMWIN60-3","ESTPMWIN60-4","ESTPMWIN60-5","ESTPMWIN60-6","ESTPMWIN60-7","ESTPMWIN60-8",
    				   "ESTPMWIN60-9","ESTPMWIN60-10","ESTPMWIN61-1","ESTPMWIN61-2","ESTPMWIN61-3","ESTPMWIN61-4","ESTPMWIN61-5","ESTPMWIN61-6","ESTPMWIN61-7",
					   "ESTPMWIN61-8","ESTPMWIN61-9","ESTPMWIN61-10","ESTPMWIN62-1","ESTPMWIN62-2","ESTPMWIN62-3","ESTPMWIN62-4","ESTPMWIN62-5","ESTPMWIN62-6",
					   "ESTPMWIN62-7","ESTPMWIN62-8","ESTPMWIN62-9","ESTPMWIN62-10")
					   
	"ESTPMWIN51-1" = @("ESTPMWIN51-1","ESTPMWIN63-1","ESTPMWIN63-2","ESTPMWIN63-3")
	
	"ESTPMWIN51-2" = @("ESTPMWIN51-2","ESTPMWIN63-4","ESTPMWIN63-5","ESTPMWIN63-6")
	
	"ESTPMWIN51-3" = @("ESTPMWIN51-3","ESTPMWIN63-7","ESTPMWIN63-8","ESTPMWIN63-9")
	
	"ESTPMWIN51-4" = @("ESTPMWIN51-4","ESTPMWIN63-10","ESTPMWIN64-1","ESTPMWIN64-2")
	
	"ESTPMWIN51-5" = @("ESTPMWIN51-5","ESTPMWIN64-3","ESTPMWIN64-4","ESTPMWIN64-5")
	
	"ESTPMWIN51-6" = @("ESTPMWIN51-6","ESTPMWIN64-6","ESTPMWIN64-7","ESTPMWIN64-8")

}


# Log start of the script
$GetTimeStamp = Get-Date -format G
Write-Output "$GetTimeStamp Start Automation VMs cleanup session"

$EnvironmentName = hostname
$EnvironmentName = "$EnvironmentName".ToUpper()

Write-Host "Received Automation Environment name: " -nonewline; 
Write-Host $EnvironmentName -foregroundcolor red -backgroundcolor DarkMagenta

$AutomationSetup = $AutomationEnvironments[$EnvironmentName]

# Determine which configuration should be processed

if ($AutomationSetup -eq "" -or $AutomationSetup -eq $null) {

	$AutomationSetup = hostname
	Write-Output "Automation Environment (Hub):" $AutomationSetup
    Write-Output "Automation Environment (Hub / Nodes):" $AutomationSetup
} else {

    Write-Output "Automation Environment (Hub):" $EnvironmentName
    Write-Output "Automation Environment (Hub / Nodes):" $AutomationSetup
}

foreach($Server in $AutomationSetup) {
   
    # Kill processes and remove files
	$User = "qaadmin"
    $Password = ConvertTo-SecureString -String "QAselenium7" -AsPlainText -Force
    $Credential = New-Object -TypeName System.Management.Automation.PSCredential -ArgumentList $User, $Password
	
    # Print time stamp
    $GetTimeStamp = Get-Date -format G
    Write-Output "$GetTimeStamp Kill processes on server: $Server"

    # Firefox related, # IE related, # Chrome related, # Other
	
	$process = ([WMICLASS]"\\$Server\ROOT\CIMV2:win32_process").Create("cmd.exe `/c C:\Automation\Textura\Framework\src\main\scripts\maintenance\taskkill.bat") 

    # Print time stamp
    $GetTimeStamp = Get-Date -format G
    Write-Output "$GetTimeStamp Finished killing processes on server: $Server"

    # Print time stamp
    $GetTimeStamp = Get-Date -format G
    Write-Output "$GetTimeStamp Start folder cleanup"
    
    # Delete files
    $GetTimeStamp = Get-Date -format G
    Write-Output "$GetTimeStamp Deleting files"
	 
	$process = ([WMICLASS]"\\$Server\ROOT\CIMV2:win32_process").Create("cmd.exe /c C:\Automation\Textura\Framework\src\main\scripts\maintenance\deleteTempFolder.bat") 
	
    # Print time stamp
    $GetTimeStamp = Get-Date -format G
    Write-Output "$GetTimeStamp Finished folder cleanup on: $Server"

}
	Start-Sleep -s 10 
foreach($Server in $AutomationSetup) {

	# Terminate firefox breakpoint windows
    $GetTimeStamp = Get-Date -format G
    Write-Output "$GetTimeStamp Closing Firefox Breakpoint exception windows"
	 
	$process = ([WMICLASS]"\\$Server\ROOT\CIMV2:win32_process").Create("cmd.exe /c C:\Automation\Textura\Framework\src\main\scripts\maintenance\BreakpointClose.bat") 
	
    # Print time stamp
    $GetTimeStamp = Get-Date -format G
    Write-Output "$GetTimeStamp Finished closing Firefox Breakpoint exception windows on: $Server"
}
	
	#Automation Artifacts folder cleanup 
	Write-Output " Starting deleting folders that are 5 days old or older in c:\automation_artifacts"
	get-childitem "c:\automation_artifacts" |? {$_.psiscontainer -and $_.lastwritetime -le (get-date).adddays(-5)} |% {remove-item c:\automation_artifacts\$_ -recurse -force}
	Write-Output " Finished deleting folders, folders that are 5 days old or older in c:\automation_artifacts"
		
# Log end of the script
$GetTimeStamp = Get-Date -format G
Write-Output "$GetTimeStamp Finished Automation VMs cleanup session"