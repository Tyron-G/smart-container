# 2026-05-31: Start container-manager with real WeChat Pay configuration from ignored local files.
param(
    [string]$SecretDir = "work/.payment-secrets/wechat-production",
    [string]$PrivateKeyFileName = "apiclient_key.pem"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path $PSScriptRoot -Parent
$FullSecretDir = Join-Path $Root $SecretDir

$MchIdPath = Join-Path $FullSecretDir "mch_id.txt"
$AppIdPath = Join-Path $FullSecretDir "app_id.txt"
$ApiV3KeyPath = Join-Path $FullSecretDir "api_v3_key.txt"
$MerchantSerialNoPath = Join-Path $FullSecretDir "merchant_serial_no.txt"
$PrivateKeyPath = Join-Path $FullSecretDir $PrivateKeyFileName

foreach ($Path in @($MchIdPath, $ApiV3KeyPath, $MerchantSerialNoPath, $PrivateKeyPath)) {
    if (-not (Test-Path -LiteralPath $Path)) {
        throw "缺少微信支付配置文件：$Path"
    }
}

$env:MANAGER_WECHAT_ENABLED = "true"
$env:MANAGER_WECHAT_ENVIRONMENT = "production"
$env:MANAGER_WECHAT_MCH_ID = (Get-Content -Raw -Path $MchIdPath).Trim()
$env:MANAGER_WECHAT_APP_ID = if (Test-Path -LiteralPath $AppIdPath) { (Get-Content -Raw -Path $AppIdPath).Trim() } else { "" }
$env:MANAGER_WECHAT_API_V3_KEY = (Get-Content -Raw -Path $ApiV3KeyPath).Trim()
$env:MANAGER_WECHAT_MERCHANT_SERIAL_NO = (Get-Content -Raw -Path $MerchantSerialNoPath).Trim()
$env:MANAGER_WECHAT_PRIVATE_KEY_PATH = $PrivateKeyPath
$env:MANAGER_WECHAT_LOCAL_TEST_MODE = "false"
$env:MANAGER_WECHAT_ALLOW_ORDER_NO_AS_OUT_TRADE_NO = "false"
$env:MANAGER_ALIPAY_ENABLED = "false"

$Listener = Get-NetTCPConnection -LocalPort 9965 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($Listener) {
    Stop-Process -Id $Listener.OwningProcess -Force
    Start-Sleep -Seconds 3
}

$Command = 'mvn spring-boot:run -DskipTests "-Dspring-boot.run.mainClass=cn.fuguang.manager.ContainerManagerApplication" > ..\work\manager-wechat-real-run-20260531.log 2>&1'
Start-Process -FilePath "cmd.exe" -ArgumentList "/c $Command" -WorkingDirectory (Join-Path $Root "container-manager") -WindowStyle Hidden -Environment @{
    MANAGER_WECHAT_ENABLED = $env:MANAGER_WECHAT_ENABLED
    MANAGER_WECHAT_ENVIRONMENT = $env:MANAGER_WECHAT_ENVIRONMENT
    MANAGER_WECHAT_MCH_ID = $env:MANAGER_WECHAT_MCH_ID
    MANAGER_WECHAT_APP_ID = $env:MANAGER_WECHAT_APP_ID
    MANAGER_WECHAT_API_V3_KEY = $env:MANAGER_WECHAT_API_V3_KEY
    MANAGER_WECHAT_MERCHANT_SERIAL_NO = $env:MANAGER_WECHAT_MERCHANT_SERIAL_NO
    MANAGER_WECHAT_PRIVATE_KEY_PATH = $env:MANAGER_WECHAT_PRIVATE_KEY_PATH
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
