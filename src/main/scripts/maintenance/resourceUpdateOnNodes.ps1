<#
 # This script does the following:
 #   - updates resource folder from Hub to its Nodes
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

    "DFWIN7QAAUTO10" = @("DFWIN7QAAUTO11","DFWIN7QAAUTO12","DFWIN7QAAUTO13","DFWIN7QAAUTO14","DFWIN7QAAUTO15","DFWIN7QAAUTO16")
	
	"DFWIN7QAAUTO80" = @("DFWIN7QAAUTO79","DFWIN7QAAUTO81","DFWIN7QAAUTO82","DFWIN7QAAUTO83","DFWIN7QAAUTO84")

	"DFWIN7QAAUTO90" = @("DFWIN7QAAUTO85","DFWIN7QAAUTO86","DFWIN7QAAUTO87","DFWIN7QAAUTO88","DFWIN7QAAUTO89")

    "DFWIN7QAAUTO51" = @("DFWIN7QAAUTO17","DFWIN7QAAUTO18","DFWIN7QAAUTO19","DFWIN7QAAUTO30","DFWIN7QAAUTO31",
                         "DFWIN7QAAUTO32","DFWIN7QAAUTO33","DFWIN7QAAUTO34","DFWIN7QAAUTO35","DFWIN7QAAUTO36",
                         "DFWIN7QAAUTO37","DFWIN7QAAUTO38","DFWIN7QAAUTO39","DFWIN7QAAUTO40","DFWIN7QAAUTO41",
                         "DFWIN7QAAUTO42","DFWIN7QAAUTO43","DFWIN7QAAUTO44","DFWIN7QAAUTO45","DFWIN7QAAUTO46",
                         "DFWIN7QAAUTO47","DFWIN7QAAUTO48","DFWIN7QAAUTO49","DFWIN7QAAUTO50","DFWIN7QAAUTO52",
                         "DFWIN7QAAUTO62","DFWIN7QAAUTO63","DFWIN7QAAUTO64","DFWIN7QAAUTO65","DFWIN7QAAUTO66",
                         "DFWIN7QAAUTO67","DFWIN7QAAUTO68","DFWIN7QAAUTO69","DFWIN7QAAUTO71","DFWIN7QAAUTO72",
                         "DFWIN7QAAUTO73","DFWIN7QAAUTO2","DFWIN7QAAUTO4","DFWIN7QAAUTO8","DFWIN7QAAUTO9")
	
    "DFWIN7QAAUTO75" = @("DFWIN7QAAUTO25","DFWIN7QAAUTO24","DFWIN7QAAUTO22","DFWIN7QAAUTO23","DFWIN7QAAUTO20","DFWIN7QAAUTO21","DFWIN7QAAUTO74","DFWIN7QAAUTO76","DFWIN7QAAUTO77","DFWIN7QAAUTO78")
}


# Log start of the script
$GetTimeStamp = Get-Date -format G
Write-Output "$GetTimeStamp Start Automation Node resource folder updates"

$EnvironmentName = hostname
$EnvironmentName = "$EnvironmentName".ToUpper()

Write-Host "Received Automation Environment name: " -nonewline; 
Write-Host $EnvironmentName -foregroundcolor red -backgroundcolor DarkMagenta

$AutomationSetup = $AutomationEnvironments[$EnvironmentName]

# Determine which configuration should be processed

if ($AutomationSetup -eq "" -or $AutomationSetup -eq $null) {
	Write-Host $EnvironmentName -foregroundcolor red -nonewline;
	Write-Host " does not have any nodes to update resource folder on."
	break
} else {

    Write-Output "Automation Environment (Hub):" $EnvironmentName
    Write-Output "Automation Environment (Hub / Nodes):" $AutomationSetup
}
foreach($Server in $AutomationSetup) {
Write-Host "Updating resource folder on $Server"
robocopy C:\Automation\Textura\CPM\src\test\resources \\$Server\c$\Automation\Textura\CPM\src\test\resources /E /NFL /NDL /NJH /NJS /nc /ns
}
		
# Log end of the script
$GetTimeStamp = Get-Date -format G
Write-Output "$GetTimeStamp Finished Updates on nodes"