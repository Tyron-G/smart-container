# 2026-05-31: Start container-manager with explicit WeChat local integration mode.
param()

$ErrorActionPreference = "Stop"
$Root = Split-Path $PSScriptRoot -Parent

$env:MANAGER_WECHAT_ENABLED = "true"
$env:MANAGER_WECHAT_ENVIRONMENT = "local-test"
$env:MANAGER_WECHAT_LOCAL_TEST_MODE = "true"
$env:MANAGER_WECHAT_ALLOW_ORDER_NO_AS_OUT_TRADE_NO = "false"
$env:MANAGER_ALIPAY_ENABLED = "false"

$Listener = Get-NetTCPConnection -LocalPort 9965 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($Listener) {
    Stop-Process -Id $Listener.OwningProcess -Force
    Start-Sleep -Seconds 3
}

$Command = 'mvn spring-boot:run -DskipTests "-Dspring-boot.run.mainClass=cn.fuguang.manager.ContainerManagerApplication" > ..\work\manager-wechat-local-run-20260531.log 2>&1'
Start-Process -FilePath "cmd.exe" -ArgumentList "/c $Command" -WorkingDirectory (Join-Path $Root "container-manager") -WindowStyle Hidden -Environment @{
    MANAGER_WECHAT_ENABLED = $env:MANAGER_WECHAT_ENABLED
    MANAGER_WECHAT_ENVIRONMENT = $env:MANAGER_WECHAT_ENVIRONMENT
    MANAGER_WECHAT_LOCAL_TEST_MODE = $env:MANAGER_WECHAT_LOCAL_TEST_MODE
    MANAGER_WECHAT_ALLOW_ORDER_NO_AS_OUT_TRADE_NO = $env:MANAGER_WECHAT_ALLOW_ORDER_NO_AS_OUT_TRADE_NO
    MANAGER_ALIPAY_ENABLED = $env:MANAGER_ALIPAY_ENABLED
}

$Deadline = (Get-Date).AddSeconds(150)
do {
    Start-Sleep -Seconds 2
    $Ready = Test-NetConnection -ComputerName localhost -Port 9965 -InformationLevel Quiet -ErrorAction SilentlyContinue
} while (-not $Ready -and (Get-Date) -lt $Deadline)

if (-not $Ready) {
    throw "container-manager 9965 启动超时"
}

Get-NetTCPConnection -LocalPort 9965 -State Listen | Select-Object LocalPort, State, OwningProcess
