param(
    [switch]$IncludeNettyPorts
)

$ErrorActionPreference = 'Stop'

function Test-PortStatus {
    param(
        [string]$Name,
        [int]$Port
    )

    $ok = Test-NetConnection -ComputerName 'localhost' -Port $Port -InformationLevel Quiet -ErrorAction SilentlyContinue
    [PSCustomObject]@{
        类型   = '端口'
        名称   = $Name
        目标   = "localhost:$Port"
        结果   = if ($ok) { 'UP' } else { 'DOWN' }
        说明   = if ($ok) { '端口可访问' } else { '端口不可访问' }
    }
}

function Test-HttpStatus {
    param(
        [string]$Name,
        [string]$Url
    )

    try {
        $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 8
        [PSCustomObject]@{
            类型   = 'HTTP'
            名称   = $Name
            目标   = $Url
            结果   = 'OK'
            说明   = "HTTP $($response.StatusCode)"
        }
    }
    catch {
        $statusCode = $null
        if ($_.Exception.Response) {
            try {
                $statusCode = [int]$_.Exception.Response.StatusCode
            }
            catch {
                $statusCode = $null
            }
        }

        [PSCustomObject]@{
            类型   = 'HTTP'
            名称   = $Name
            目标   = $Url
            结果   = 'FAIL'
            说明   = if ($statusCode) { "HTTP $statusCode" } else { $_.Exception.Message }
        }
    }
}

$portChecks = @(
    @{ Name = 'Nacos'; Port = 8848 }
    @{ Name = 'Redis'; Port = 6379 }
    @{ Name = 'RocketMQ NameServer'; Port = 9876 }
    @{ Name = 'RocketMQ Broker'; Port = 10911 }
    @{ Name = 'container-order'; Port = 9960 }
    @{ Name = 'container-account'; Port = 9961 }
    @{ Name = 'container-channel'; Port = 9962 }
    @{ Name = 'container-communicate'; Port = 9963 }
    @{ Name = 'container-device'; Port = 9964 }
    @{ Name = 'container-manager'; Port = 9965 }
)

if ($IncludeNettyPorts) {
    $portChecks += @(
        @{ Name = 'communicate-keNai-netty'; Port = 7000 }
        @{ Name = 'communicate-yiNuo-netty'; Port = 7001 }
    )
}

$httpChecks = @(
    @{ Name = 'order-health'; Url = 'http://localhost:9960/order/actuator/health' }
    @{ Name = 'account-health'; Url = 'http://localhost:9961/account/actuator/health' }
    @{ Name = 'channel-health'; Url = 'http://localhost:9962/channel/actuator/health' }
    @{ Name = 'communicate-health'; Url = 'http://localhost:9963/communicate/actuator/health' }
    @{ Name = 'device-health'; Url = 'http://localhost:9964/device/actuator/health' }
    @{ Name = 'manager-health'; Url = 'http://localhost:9965/manager/actuator/health' }
)

$results = @()

foreach ($item in $portChecks) {
    $results += Test-PortStatus -Name $item.Name -Port $item.Port
}

foreach ($item in $httpChecks) {
    $results += Test-HttpStatus -Name $item.Name -Url $item.Url
}

Write-Host ''
Write-Host 'smart-container 本地健康检查结果' -ForegroundColor Cyan
Write-Host ''
$results | Format-Table -AutoSize

$failed = $results | Where-Object { $_.结果 -in @('DOWN', 'FAIL') }
Write-Host ''
if ($failed.Count -gt 0) {
    Write-Host "失败项数量: $($failed.Count)" -ForegroundColor Yellow
    exit 1
}
else {
    Write-Host '所有检查项通过。' -ForegroundColor Green
    exit 0
}
