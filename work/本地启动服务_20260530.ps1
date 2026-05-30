param(
    [switch]$SkipNacos,
    [switch]$SkipRocketMQ,
    [switch]$SkipServices
)

$ErrorActionPreference = 'Stop'

$projectRoot = 'D:\smart-container'
$javaHome = 'C:\Program Files\Java\jdk-1.8'
$nacosHome = 'F:\Program Files\Tencent\nacos'
$rocketMqHome = 'D:\rocketmq-all-5.5.0-bin-release'

$env:JAVA_HOME = $javaHome
$env:PATH = "$env:JAVA_HOME\bin;" + ($env:PATH -replace [regex]::Escape('C:\Program Files\Java\jdk-17\bin;'), '')
$env:ROCKETMQ_HOME = $rocketMqHome

function Test-PortOpen {
    param([int]$Port)
    Test-NetConnection -ComputerName 'localhost' -Port $Port -InformationLevel Quiet -ErrorAction SilentlyContinue
}

function Wait-PortOpen {
    param(
        [int]$Port,
        [string]$Name,
        [int]$TimeoutSeconds = 60
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        if (Test-PortOpen -Port $Port) {
            return $true
        }
        Start-Sleep -Seconds 1
    }
    throw "$Name 启动超时，端口 $Port 未就绪。"
}

function Start-CmdProcess {
    param(
        [string]$FilePath,
        [string]$Arguments,
        [string]$WorkingDirectory
    )

    Start-Process -FilePath $FilePath -ArgumentList $Arguments -WorkingDirectory $WorkingDirectory -WindowStyle Hidden | Out-Null
}

function Start-Nacos {
    if (Test-PortOpen -Port 8848) {
        Write-Host 'Nacos 已在运行，跳过启动。' -ForegroundColor Yellow
        return
    }

    $startupCmd = Join-Path $nacosHome 'bin\startup.cmd'
    if (-not (Test-Path $startupCmd)) {
        throw "未找到 Nacos 启动脚本：$startupCmd"
    }

    Write-Host '正在启动 Nacos...' -ForegroundColor Cyan
    Start-CmdProcess -FilePath 'cmd.exe' -Arguments "/c \"$startupCmd -m standalone\"" -WorkingDirectory (Join-Path $nacosHome 'bin')
    Wait-PortOpen -Port 8848 -Name 'Nacos' -TimeoutSeconds 90
    Write-Host 'Nacos 启动成功。' -ForegroundColor Green
}

function Start-RocketMQ {
    if (-not (Test-PortOpen -Port 9876)) {
        $namesrvCmd = Join-Path $rocketMqHome 'bin\mqnamesrv.cmd'
        if (-not (Test-Path $namesrvCmd)) {
            throw "未找到 RocketMQ NameServer 启动脚本：$namesrvCmd"
        }

        Write-Host '正在启动 RocketMQ NameServer...' -ForegroundColor Cyan
        Start-CmdProcess -FilePath 'cmd.exe' -Arguments "/c \"$namesrvCmd\"" -WorkingDirectory (Join-Path $rocketMqHome 'bin')
        Wait-PortOpen -Port 9876 -Name 'RocketMQ NameServer' -TimeoutSeconds 90
        Write-Host 'RocketMQ NameServer 启动成功。' -ForegroundColor Green
    }
    else {
        Write-Host 'RocketMQ NameServer 已在运行，跳过启动。' -ForegroundColor Yellow
    }

    if (-not (Test-PortOpen -Port 10911)) {
        $brokerCmd = Join-Path $rocketMqHome 'bin\mqbroker.cmd'
        $brokerConf = Join-Path $rocketMqHome 'conf\broker.conf'
        if (-not (Test-Path $brokerCmd)) {
            throw "未找到 RocketMQ Broker 启动脚本：$brokerCmd"
        }

        Write-Host '正在启动 RocketMQ Broker...' -ForegroundColor Cyan
        Start-CmdProcess -FilePath 'cmd.exe' -Arguments "/c \"$brokerCmd -n localhost:9876 -c $brokerConf autoCreateTopicEnable=true\"" -WorkingDirectory (Join-Path $rocketMqHome 'bin')
        Wait-PortOpen -Port 10911 -Name 'RocketMQ Broker' -TimeoutSeconds 90
        Write-Host 'RocketMQ Broker 启动成功。' -ForegroundColor Green
    }
    else {
        Write-Host 'RocketMQ Broker 已在运行，跳过启动。' -ForegroundColor Yellow
    }
}

function Start-Microservice {
    param(
        [string]$Name,
        [string]$ModuleDir,
        [string]$MainClass,
        [int]$Port
    )

    if (Test-PortOpen -Port $Port) {
        Write-Host "$Name 已在运行，跳过启动。" -ForegroundColor Yellow
        return
    }

    $modulePath = Join-Path $projectRoot $ModuleDir
    if (-not (Test-Path $modulePath)) {
        throw "未找到模块目录：$modulePath"
    }

    Write-Host "正在启动 $Name ..." -ForegroundColor Cyan
    $command = "mvn spring-boot:run -DskipTests \"-Dspring-boot.run.mainClass=$MainClass\""
    Start-CmdProcess -FilePath 'cmd.exe' -Arguments "/c $command" -WorkingDirectory $modulePath
    Wait-PortOpen -Port $Port -Name $Name -TimeoutSeconds 180
    Write-Host "$Name 启动成功。" -ForegroundColor Green
}

Write-Host ''
Write-Host 'smart-container 一键启动开始' -ForegroundColor Cyan
Write-Host ''

if (-not $SkipNacos) {
    Start-Nacos
}

if (-not $SkipRocketMQ) {
    Start-RocketMQ
}

if (-not $SkipServices) {
    Start-Microservice -Name 'container-order' -ModuleDir 'container-order' -MainClass 'cn.fuguang.order.ContainerOrderApplication' -Port 9960
    Start-Microservice -Name 'container-account' -ModuleDir 'container-account' -MainClass 'cn.fuguang.account.ContainerAccountApplication' -Port 9961
    Start-Microservice -Name 'container-channel' -ModuleDir 'container-channel' -MainClass 'cn.fuguang.channel.ContainerChannelApplication' -Port 9962
    Start-Microservice -Name 'container-device' -ModuleDir 'container-device' -MainClass 'cn.fuguang.device.ContainerDeviceApplication' -Port 9964
    Start-Microservice -Name 'container-manager' -ModuleDir 'container-manager' -MainClass 'cn.fuguang.manager.ContainerManagerApplication' -Port 9965
    Start-Microservice -Name 'container-communicate' -ModuleDir 'container-communicate' -MainClass 'cn.fuguang.communicate.ContainerCommunicateApplication' -Port 9963
}

Write-Host ''
Write-Host 'smart-container 一键启动完成。' -ForegroundColor Green
