$AutomationArtifactsPath = 'C:\Automation_artifacts'
$AutomationPath = 'C:\Automation'
$RepositoryPath = 'C:\Automation\repository'
$MavenPath = 'C:\Automation\Maven'
$TexturaPath = 'C:\Automation\Textura'
$JfrogMavenPath = 'C:/Automation/Maven'
$JfrogRepositoryPath = 'C:/Automation/repository'
$JfrogPath = 'C:\Automation_artifacts\jfrog'
$JfrogRepositoryUrl = 'http://dfwin7qaauto53.texturallc.net:8081/artifactory/'
$GitPath = 'C:\Program Files\Git\bin\git'
$BitbucketUrlSsh = 'ssh://git@bitbucket.texturallc.net:7999/at'
$BitbucketUrlHttp = 'https://bitbucket.texturallc.net/scm/at'
$AutomationResources = 'C:\Automation\repository\resources'
$AutomationClients = $env:programdata + '\AutomationClients'
$JenkinsClientPath = $AutomationResources + '\jenkins'
$JenkinsClientScriptPath = 'C:\Automation\Textura\Framework\src\main\scripts\jenkins\slave.bat'
$JenkinsClientLnkScriptPath = 'C:\Automation\Textura\Framework\src\main\scripts\jenkins\Jenkins Client - start.lnk'
$SeleniumFilesPath = $AutomationResources + '\selenium'
$SeleniumHubScriptPath = 'C:\Automation\Textura\Framework\src\main\scripts\selenium\hub\SeleniumHub-start.bat'
$SeleniumHubLnkScriptPath = 'C:\Automation\Textura\Framework\src\main\scripts\selenium\hub\Automation Hub - start.lnk'
$SeleniumHubNodeScriptPath = 'C:\Automation\Textura\Framework\src\main\scripts\selenium\node\SeleniumNodeHub-start.bat'
$SeleniumHubNodeLnkScriptPath = 'C:\Automation\Textura\Framework\src\main\scripts\selenium\node\Automation Node Hub - start.lnk'
$SeleniumNodeScriptPath = 'C:\Automation\Textura\Framework\src\main\scripts\selenium\node\SeleniumNode-start.bat'
$SeleniumNodeScriptLnkPath = 'C:\Automation\Textura\Framework\src\main\scripts\selenium\node\Automation Node - start.lnk'
$AutomationProjects = "Framework", "BO", "BS", "CPM", "GDB", "LAT", "PE", "PQM", "SE", "TCS", "TL", "TP"
$EnvHostname = $env:computername
$Command=$args[0]
$Project=$args[1]
$Branch=$args[2]


if(-Not($Project -eq "") -or -Not($Project -eq $null))
{
    $Project = "$Project".ToUpper()
}

if(-Not($Branch -eq "") -or -Not($Branch -eq $null))
{
    if($Branch -like "*qis-*")
    {
        $Branch = "$Branch".ToUpper()
    }
}

if($EnvHostname -like 'df*' -or $EnvHostname -like '*_ws')
{
    $BitbucketUrl = $BitbucketUrlSsh
}
else
{
    $BitbucketUrl = $BitbucketUrlHttp
}

Set-Location $AutomationArtifactsPath

function TerminateProcessByPath($ProcessName, $Path)
{
    $processes = Get-WmiObject Win32_Process -Filter "name = '$ProcessName'"
    
    foreach($proc in $processes)
    {
        if(!([string]::IsNullOrWhiteSpace($proc.CommandLine)))
        {
            if($proc.CommandLine.Contains($Path))
            {
                Write-Host "Stopping proccess $($proc.ProcessId) with $($proc.ThreadCount) threads; $($proc.CommandLine)..." -ErrorAction SilentlyContinue
                Stop-Process -F $proc.ProcessId
            }
        }
    }
}

function TerminateProcess($ProcessName)
{
    $processes = Get-WmiObject Win32_Process -Filter "name = '$ProcessName'"
    foreach($proc in $processes)
    {
        Write-Host "Stopping proccess $($proc.ProcessId) with $($proc.ThreadCount) threads; $($proc.CommandLine)..." -ErrorAction SilentlyContinue
        Stop-Process -F $proc.ProcessId
    }
}

function DeletePath($Path)
{
    if (Test-Path $Path)
    {
        Get-ChildItem -Path $Path -Recurse | Remove-Item -Force -Recurse -Confirm:$false -ErrorAction SilentlyContinue
        Remove-Item $Path -Force -Recurse -Confirm:$false -ErrorAction SilentlyContinue
    }
    else
    {
        Write-Host "$Path does not exist..."
    }
}

function GitStatus($ProjectName)
{
    Set-Location $TexturaPath\$ProjectName
    $Execute = "git status"
    Invoke-Expression $Execute
    Set-Location $AutomationArtifactsPath
}

function CloneProject($ProjectName)
{
    if (-Not(Test-Path "$TexturaPath\$ProjectName"))
    {
        Write-Host "Clone project: $ProjectName"
        GitClone($ProjectName)
    }
    else
    {
        Write-Host "Project already exists: $ProjectName"
    }
}

function GitClone($ProjectName)
{
    $Execute = "git clone -q $BitbucketUrl/$ProjectName $TexturaPath\" + "$ProjectName"
    Invoke-Expression $Execute
}

function CloneFramework
{
    if (-Not(Test-Path $TexturaPath))
    {
        Write-Host "Create Textura folder"
        $Execute = "git clone -q $BitbucketUrl/textura $TexturaPath"
        Invoke-Expression $Execute
        Get-ChildItem -Path $TexturaPath\.git -Recurse | Remove-Item -Force -Recurse
        Remove-Item $TexturaPath\.git -Force
    }
    if (-Not(Test-Path $TexturaPath\Framework))
    {
        Write-Host "Clone project: Framework"
        $Execute = "git clone -q $BitbucketUrl/framework $TexturaPath\Framework"
        Invoke-Expression $Execute
    }
    else
    {
        Write-Host "Project already exists: Framework"
    }
}

function UpdateProject($ProjectName)
{
    if (Test-Path "$TexturaPath\$ProjectName")
    {
        Write-Host "Update project: $ProjectName"
        GitUpdate $ProjectName
    }
    else
    {
        Write-Host "Project does not exist: $ProjectName"
    }
}

function GitUpdate($ProjectName)
{
    Set-Location $TexturaPath\$ProjectName
    $Execute = "git fetch --prune -v"
    Invoke-Expression $Execute
    $Execute = "git reset --hard"
    Invoke-Expression $Execute
    $Execute = "git clean -df"
    Invoke-Expression $Execute
    $Execute = "git pull"
    Invoke-Expression $Execute
    GitBranchInfo $ProjectName
    Set-Location $AutomationArtifactsPath
}

function GitCheckout($ProjectName, $BranchName)
{
    Set-Location $TexturaPath\$ProjectName
    $Execute = "git remote update"
    Invoke-Expression $Execute
    $Execute = "git checkout --force $Branch"
    Invoke-Expression $Execute
    $Execute = "git pull"
    Invoke-Expression $Execute
    GitBranchInfo $ProjectName
    Set-Location $AutomationArtifactsPath
}

function CheckoutBranch($ProjectName, $BranchName)
{
    CheckIfBranchExists $ProjectName $BranchName

    if (Test-Path "$TexturaPath\$ProjectName")
    {
        Write-Host "Checkout Branch: $ProjectName"
        GitCheckout $ProjectName $BranchName
    }
    else
    {
        Write-Host "Project does not exist: $ProjectName"
    }
}

function GitBranchInfo($ProjectName)
{
    Set-Location $TexturaPath\$ProjectName
    $Execute = "git branch -vvv"
    Invoke-Expression $Execute
    $Execute = "git status -v"
    Invoke-Expression $Execute
    Set-Location $AutomationArtifactsPath
}

function CheckIfBranchExists($ProjectName, $BranchName)
{
    Set-Location $TexturaPath\$ProjectName
    $Execute = "git ls-remote --heads --exit-code"
    $Output = Invoke-Expression $Execute

    if ($Output -like "*$BranchName")
    {
        Write-Host "Automation branch exists: $BranchName"
    }
    else
    {
        Write-Host "Automation branch DOES NOT exist: $BranchName"
        exit -1
    }
}

function UpdateRootPomFile()
{
    $ExistingProjects += "	<modules>"
    $Dirs = Get-ChildItem -dir $TexturaPath

    if (Test-Path "$TexturaPath\pom.xml")
    {
        (Get-Content $TexturaPath\pom.xml) | Where-Object { $_ -notmatch '^		<module>.+$' } | Set-Content $TexturaPath\pom.xml

        foreach($Dir in $Dirs)
        {
            if($AutomationProjects -contains $Dir)
            {
                if(-Not($Dir -like 'TL'))
                {
                    Write-Host "Regenerating project configuration: $Dir"
                    $ExistingProjects += "`r`n		<module>$Dir</module>"
                }
            }
        }
        (Get-Content $TexturaPath\pom.xml) | Foreach-Object { $_ -replace '	<modules>', $ExistingProjects ` } | Set-Content $TexturaPath\pom.xml
    }
    else
    {
        Write-Host "Could not find configuration file..."              
    }
}

function DeployMaven
{
    Write-Host "Deploy Maven: $JfrogMavenPath/"
    $Execute = "$JfrogPath rt dl --url $JfrogRepositoryUrl --user readonly --password QAselenium7 --split-count=0 tools-maven $JfrogMavenPath/"
    Invoke-Expression $Execute
}

function DeployRepository
{
    Write-Host "Deploy repository: $JfrogRepositoryPath/maven/"
    $Execute = "$JfrogPath rt dl --url $JfrogRepositoryUrl --user readonly --password QAselenium7 --split-count=0 repository-maven $JfrogRepositoryPath/maven/"
    Invoke-Expression $Execute

    Write-Host "Deploy repository: $JfrogRepositoryPath/resources/"
    $Execute = "$JfrogPath rt dl --url $JfrogRepositoryUrl --user readonly --password QAselenium7 --split-count=0 repository-resources $JfrogRepositoryPath/resources/"
    Invoke-Expression $Execute
}

function ClientsInstallNode
{
    if(-Not(Test-Path $TexturaPath\Framework))
    {
        Write-Host "Deploy Framework first: $TexturaPath\Framework does not exist"
        break
    }

    if(-Not(Test-Path $RepositoryPath))
    {
        Write-Host "Deploy repository first: $RepositoryPath does not exist"
        break
    }

    Write-Host "Install Selenium Node on VM, update shortcuts"

    ClientsStop
 
    if(Test-Path $AutomationClients)
    {
        Write-Host "Deleting $AutomationClients..."
        Get-ChildItem -Path $AutomationClients -Recurse | Remove-Item -Force -Recurse
        Remove-Item $AutomationClients -Force -Recurse
        Remove-Item "C:\Users\$env:username\Desktop\Jenkins Client - start.lnk" -Force -ErrorAction SilentlyContinue
        Remove-Item "C:\Users\$env:username\Desktop\Automation Hub - start.lnk" -Force -ErrorAction SilentlyContinue
        Remove-Item "C:\Users\$env:username\Desktop\Automation Node Hub - start.lnk" -Force -ErrorAction SilentlyContinue
        Remove-Item "C:\Users\$env:username\Desktop\Automation Node - start.lnk" -Force -ErrorAction SilentlyContinue
    }

    Write-Host "Create $AutomationClients..."
    New-Item -Path $AutomationClients -ItemType Directory -Force | Out-Null

    Write-Host "Copy Selenium files..."
    Copy-Item $SeleniumFilesPath $AutomationClients -Force

    Write-Host "Copy Selenium shortcuts..."
    Copy-Item  $SeleniumNodeScriptLnkPath "C:\Users\$env:username\Desktop\Automation Node - start.lnk" -Force
}

function ClientsInstallHub
{
    if(-Not(Test-Path $TexturaPath\Framework))
    {
        Write-Host "Deploy Framework first: $TexturaPath\Framework does not exist"
        break
    }
    
    if(-Not(Test-Path $RepositoryPath))
    {
        Write-Host "Deploy repository first: $RepositoryPath does not exist"
        break
    }

    Write-Host "Install Selenium Node, Hub and Jenkins client on VM, update shortcuts"

    ClientsStop
 
    if(Test-Path $AutomationClients)
    {
        Write-Host "Deleting $AutomationClients..."
        Get-ChildItem -Path $AutomationClients -Recurse | Remove-Item -Force -Recurse
        Remove-Item $AutomationClients -Force -Recurse
        Remove-Item "C:\Users\$env:username\Desktop\Jenkins Client - start.lnk" -Force -ErrorAction SilentlyContinue
        Remove-Item "C:\Users\$env:username\Desktop\Automation Hub - start.lnk" -Force -ErrorAction SilentlyContinue
        Remove-Item "C:\Users\$env:username\Desktop\Automation Node Hub - start.lnk" -Force -ErrorAction SilentlyContinue
        Remove-Item "C:\Users\$env:username\Desktop\Automation Node - start.lnk" -Force -ErrorAction SilentlyContinue
    }

    Write-Host "Create $AutomationClients..."
    New-Item -Path $AutomationClients -ItemType Directory -Force | Out-Null

    Write-Host "Copy Selenium files..."
    Copy-Item $SeleniumFilesPath $AutomationClients -Force

    Write-Host "Copy Jenkins client files..."
    Copy-Item $JenkinsClientPath $AutomationClients -Recurse

    Write-Host "Copy Selenium and Jenkins shortcuts..."
    Copy-Item $JenkinsClientLnkScriptPath "C:\Users\$env:username\Desktop\Jenkins Client - start.lnk" -Force
    Copy-Item $SeleniumHubLnkScriptPath "C:\Users\$env:username\Desktop\Automation Hub - start.lnk" -Force
    Copy-Item $SeleniumHubNodeLnkScriptPath "C:\Users\$env:username\Desktop\Automation Node Hub - start.lnk" -Force
}

function ClientsStop
{
    Write-Host "Stop Jenkins and Selenium Hub / Node on VM"

    TerminateProcessByPath 'java.exe' 'selenium'
    TerminateProcessByPath 'java.exe' 'jenkins'
    TerminateProcess 'cmd.exe'
    TerminateProcess 'chromedriver.exe'
}

function ClientsStart
{
    Write-Host "Start clients on VM"

    if(Test-Path "C:\Users\$env:username\Desktop\Jenkins Client - start.lnk")
    {
        Write-Host "Start Jenkins, Selenium Hub and Hub-Node"
        Start-Process cmd.exe -ArgumentList "/c $JenkinsClientScriptPath"
        Start-Process cmd.exe -ArgumentList "/c $SeleniumHubScriptPath"
        Start-Process cmd.exe -ArgumentList "/c $SeleniumHubNodeScriptPath"
    }
    else
    {
        Write-Host "Start Selenium Node"
        Start-Process cmd.exe -ArgumentList "/c $SeleniumNodeScriptPath"
    }
}

Function ShowUsage
{
    Write-Host ""
    Write-Host "USAGE: deploy-auto command [project] [branch]"
    Write-Host ""
    Write-Host "  clone [project]           clone Framework and a speicified [project] where [project] options are:" $AutomationProjects[1,2,3,4,5,6,7,8,9,10,11]
    Write-Host "  clone all                 deploy reposiotry, Maven, clone Framework and all automation projects"
    Write-Host "  clone Framework           deploy reposiotry, Maven and clone Framework"
    Write-Host "  update [project]          update Framework and a specified [project] to the latest revision where [project] options are:" $AutomationProjects[1,2,3,4,5,6,7,8,9,10,11]
    Write-Host "  update all                update reposiotry, Maven, Framework and all automation projects to the latest revision"
    Write-Host "  update Framework          update reposiotry, Maven and Framework to the latest revision"
    Write-Host "  switch [project] [branch] switch specified [project] to a different branch [branch], where [project] options are:" $AutomationProjects[0,1,2,3,4,5,6,7,8,9,10,11]
    Write-Host "  branch-info [project]     display information about project's checked out branch, where [project] options are:" $AutomationProjects[1,2,3,4,5,6,7,8,9,10,11]
    Write-Host "  repo                      deploy / update repository, Maven only"
    Write-Host "  clean-all                 delete $AutomationPath folder"
    Write-Host "  clean-repo                delete $RepositoryPath and $MavenPath folders"
    Write-Host "  clean-projects            delete $TexturaPath folder"
    Write-Host "  clients-restart           restart Selenium Node (Hub and Jenkins client if present)"
    Write-Host "  clients-stop              stop Selenium Node (Hub and Jenkins client if present)"
    Write-Host "  clients-start             start Selenium Node (Hub and Jenkins client if present)"
    Write-Host "  clients-install-hub       install Jenkins client, Selenium Hub and Hub-Node and Desktop shortcuts"
    Write-Host "  clients-install-node      install Selenium Node and Desktop shortcut"
    Write-Host ""
    Write-Host "EXAMPLE:"
    Write-Host ""
    Write-Host "  deploy-auto clone cpm           - clone Framework and clone CPM project"
    Write-Host "  deploy-auto switch cpm QIS-9999 - switch CPM project to QIS-9999 branch"
    Write-Host ""
    Write-Host ""
}

# Print time stamp
$GetTimeStamp = Get-Date -format G
Write-Host "Start deploy-auto: $GetTimeStamp"

if($Command -eq 'repo')
{
    Write-Host "Deploy / update Automation repository only..."
    DeployMaven
    DeployRepository
}
elseif($Command -eq 'Framework')
{
    Write-Host "Deploy Framework, Maven and repository..."
    DeployMaven
    DeployRepository
    CloneFramework
    UpdateRootPomFile
}
elseif($Command -eq 'clone')
{
    if($Project -eq $AutomationProjects[1] -or $Project -eq $AutomationProjects[2] -or 
       $Project -eq $AutomationProjects[3] -or $Project -eq $AutomationProjects[4] -or 
       $Project -eq $AutomationProjects[5] -or $Project -eq $AutomationProjects[6] -or 
       $Project -eq $AutomationProjects[7] -or $Project -eq $AutomationProjects[8] -or 
       $Project -eq $AutomationProjects[9] -or $Project -eq $AutomationProjects[10] -or
       $Project -eq $AutomationProjects[11])
    {
        Write-Host "Deploying: $Project"
        CloneFramework
        CloneProject $Project
        UpdateRootPomFile
    }
    elseif($Command -eq 'Framework')
    {
        Write-Host "Deploy Framework, Maven and repository..."
        CloneFramework
        UpdateRootPomFile
    }
    elseif($Project -eq 'all')
    {
        Write-Host "Deploy ALL automation projects..."
        DeployMaven
        DeployRepository
        CloneFramework
        CloneProject $AutomationProjects[1]
        CloneProject $AutomationProjects[2]
        CloneProject $AutomationProjects[3]
        CloneProject $AutomationProjects[4]
        CloneProject $AutomationProjects[5]
        CloneProject $AutomationProjects[6]
        CloneProject $AutomationProjects[7]
        CloneProject $AutomationProjects[8]
        CloneProject $AutomationProjects[9]
        CloneProject $AutomationProjects[10]
        CloneProject $AutomationProjects[11]
        UpdateRootPomFile
    }
    else
    {
        ShowUsage
    }
}
elseif($Command -eq 'update')
{
    if($Project -eq $AutomationProjects[1] -or $Project -eq $AutomationProjects[2] -or 
       $Project -eq $AutomationProjects[3] -or $Project -eq $AutomationProjects[4] -or 
       $Project -eq $AutomationProjects[5] -or $Project -eq $AutomationProjects[6] -or 
       $Project -eq $AutomationProjects[7] -or $Project -eq $AutomationProjects[8] -or 
       $Project -eq $AutomationProjects[9] -or $Project -eq $AutomationProjects[10] -or
       $Project -eq $AutomationProjects[11])
    {
        Write-Host "Update: $Project"
        UpdateProject $AutomationProjects[0]
        UpdateProject $Project
        UpdateRootPomFile
    }
    elseif($Project -eq 'Framework')
    {
        Write-Host "Update Framework, Maven and repository..."
        DeployMaven
        DeployRepository
        UpdateProject $AutomationProjects[0]
        UpdateRootPomFile
    }
    elseif($Project -eq 'all')
    {
        Write-Host "Update ALL automation projects..."
        DeployMaven
        DeployRepository
        UpdateProject $AutomationProjects[0]
        UpdateProject $AutomationProjects[1]
        UpdateProject $AutomationProjects[2]
        UpdateProject $AutomationProjects[3]
        UpdateProject $AutomationProjects[4]
        UpdateProject $AutomationProjects[5]
        UpdateProject $AutomationProjects[6]
        UpdateProject $AutomationProjects[7]
        UpdateProject $AutomationProjects[8]
        UpdateProject $AutomationProjects[9]
        UpdateProject $AutomationProjects[10]
        UpdateProject $AutomationProjects[11]
        UpdateRootPomFile
    }
    else
    {
        ShowUsage
    }
}
elseif($Command -eq 'switch')
{
    if($Project -eq $AutomationProjects[1] -or $Project -eq $AutomationProjects[2] -or 
       $Project -eq $AutomationProjects[3] -or $Project -eq $AutomationProjects[4] -or 
       $Project -eq $AutomationProjects[5] -or $Project -eq $AutomationProjects[6] -or 
       $Project -eq $AutomationProjects[7] -or $Project -eq $AutomationProjects[8] -or 
       $Project -eq $AutomationProjects[9] -or $Project -eq $AutomationProjects[10] -or
       $Project -eq $AutomationProjects[11])
    {
        Write-Host "Project: $Project, Branch: $Branch"
        UpdateProject $Project
        CheckoutBranch $Project $Branch
        UpdateRootPomFile
    }
    elseif($Project -eq 'Framework')
    {
        Write-Host "Project: Framework, Branch: $Branch"
        UpdateProject $Project
        CheckoutBranch $AutomationProjects[0] $Branch
        UpdateRootPomFile
    }
    else
    {
        ShowUsage
    }
}
elseif($Command -like 'clean-*')
{
    Write-Host "Run clean command..."

    $Path = $AutomationPath

    if($Command -eq 'clean-all')
    {
        $Path = $AutomationPath
        TerminateProcess 'eclipse.exe'  
        ClientsStop
        Write-Host "Deleting $AutomationPath..."
        DeletePath "$AutomationPath"
    }
    elseif($Command -eq 'clean-projects')
    {
        $Path = $TexturaPath
        TerminateProcess 'eclipse.exe'  
        ClientsStop
        Write-Host "Deleting $TexturaPath..."
        DeletePath "$TexturaPath"
    }
    elseif($Command -eq 'clean-repo')
    {
        $Path = $RepositoryPath
        TerminateProcess 'eclipse.exe'  
        ClientsStop
        Write-Host "Deleting $RepositoryPath..."
        DeletePath "$RepositoryPath"
        DeletePath "$MavenPath"
    }
    else
    {
        Write-Host ""
        Write-Host "Invalid clean command"
        ShowUsage
    }
}
elseif($Command -eq 'clients-install-hub')
{
    ClientsInstallHub
    ClientsStart
}
elseif($Command -eq 'clients-install-node')
{
    ClientsInstallNode
    ClientsStart
}
elseif($Command -eq 'clients-start')
{
    ClientsStart
}
elseif($Command -eq 'clients-stop')
{
    ClientsStop
}
elseif($Command -eq 'clients-restart')
{
    ClientsStop
    Start-Sleep -s 3
    ClientsStart
}
elseif($Command -eq 'branch-info')
{
    GitBranchInfo $Project
}
elseif($Command -eq 'branch-check')
{
    CheckIfBranchExists $Project $Branch
}
else
{
    ShowUsage
}

# Print time stamp
$GetTimeStamp = Get-Date -format G
Write-Host "Finished: $GetTimeStamp"