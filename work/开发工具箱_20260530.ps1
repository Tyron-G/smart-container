param(
    [Parameter(Mandatory = $true, Position = 0)]
    [ValidateSet('start', 'check', 'stop')]
    [string]$Action,

    [switch]$IncludeNettyPorts,
    [switch]$IncludeRocketMQ,
    [switch]$SkipNacos,
    [switch]$SkipRocketMQ,
    [switch]$SkipServices
)

$ErrorActionPreference = 'Stop'
$workDir = Split-Path -Parent $MyInvocation.MyCommand.Path

$startScript = Join-Path $workDir '本地启动服务_20260530.ps1'
$checkScript = Join-Path $workDir '本地健康检查_20260530.ps1'
$stopScript  = Join-Path $workDir '本地停止服务_20260530.ps1'

function Invoke-LocalScript {
    param(
        [string]$ScriptPath,
        [string[]]$Arguments
    )

    if (-not (Test-Path $ScriptPath)) {
        throw "脚本不存在：$ScriptPath"
    }

    & pwsh -File $ScriptPath @Arguments
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}

switch ($Action) {
    'start' {
        $args = @()
        if ($SkipNacos) { $args += '-SkipNacos' }
        if ($SkipRocketMQ) { $args += '-SkipRocketMQ' }
        if ($SkipServices) { $args += '-SkipServices' }

        Invoke-LocalScript -ScriptPath $startScript -Arguments $args
    }

    'check' {
        $args = @()
        if ($IncludeNettyPorts) { $args += '-IncludeNettyPorts' }

        Invoke-LocalScript -ScriptPath $checkScript -Arguments $args
    }

    'stop' {
        $args = @()
        if ($IncludeRocketMQ) { $args += '-IncludeRocketMQ' }

        Invoke-LocalScript -ScriptPath $stopScript -Arguments $args
    }
}
