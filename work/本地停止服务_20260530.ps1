param(
    [switch]$IncludeRocketMQ
)

$ErrorActionPreference = 'Stop'

function Stop-JavaProcessByKeyword {
    param(
        [string]$Keyword,
        [string]$DisplayName
    )

    $processes = Get-CimInstance Win32_Process |
        Where-Object {
            $_.Name -eq 'java.exe' -and $_.CommandLine -match [regex]::Escape($Keyword)
        }

    if (-not $processes) {
        [PSCustomObject]@{
            名称 = $DisplayName
            结果 = 'SKIP'
            说明 = '未找到运行中的进程'
        }
        return
    }

    foreach ($process in $processes) {
        try {
            Stop-Process -Id $process.ProcessId -Force -ErrorAction Stop
            [PSCustomObject]@{
                名称 = $DisplayName
                结果 = 'OK'
                说明 = "已停止 PID=$($process.ProcessId)"
            }
        }
        catch {
            [PSCustomObject]@{
                名称 = $DisplayName
                结果 = 'FAIL'
                说明 = "停止 PID=$($process.ProcessId) 失败：$($_.Exception.Message)"
            }
        }
    }
}

$targets = @(
    @{ Keyword = 'D:\smart-container\container-order\target\classes'; Name = 'container-order' }
    @{ Keyword = 'D:\smart-container\container-account\target\classes'; Name = 'container-account' }
    @{ Keyword = 'D:\smart-container\container-channel\target\classes'; Name = 'container-channel' }
    @{ Keyword = 'D:\smart-container\container-communicate\target\classes'; Name = 'container-communicate' }
    @{ Keyword = 'D:\smart-container\container-device\target\classes'; Name = 'container-device' }
    @{ Keyword = 'D:\smart-container\container-manager\target\classes'; Name = 'container-manager' }
)

if ($IncludeRocketMQ) {
    $targets += @(
        @{ Keyword = 'D:\rocketmq-all-5.5.0-bin-release'; Name = 'RocketMQ' }
    )
}

$results = foreach ($target in $targets) {
    Stop-JavaProcessByKeyword -Keyword $target.Keyword -DisplayName $target.Name
}

Write-Host ''
Write-Host 'smart-container 一键停止结果' -ForegroundColor Cyan
Write-Host ''
$results | Format-Table -AutoSize

$failed = $results | Where-Object { $_.结果 -eq 'FAIL' }
Write-Host ''
if ($failed.Count -gt 0) {
    Write-Host "失败项数量: $($failed.Count)" -ForegroundColor Yellow
    exit 1
}
else {
    Write-Host '停止脚本执行完成。' -ForegroundColor Green
    exit 0
}
