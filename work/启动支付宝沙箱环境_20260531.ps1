# 2026-05-31: Start container-manager with local Alipay sandbox configuration.
param(
    [string]$SecretDir = "work/.payment-secrets/alipay-sandbox"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path $PSScriptRoot -Parent
$FullSecretDir = Join-Path $Root $SecretDir

$AppIdPath = Join-Path $FullSecretDir "app_id.txt"
$PrivateKeyPath = Join-Path $FullSecretDir "merchant_private_key_single_line.txt"
$AlipayPublicKeyPath = Join-Path $FullSecretDir "alipay_public_key.txt"

foreach ($Path in @($AppIdPath, $PrivateKeyPath, $AlipayPublicKeyPath)) {
    if (-not (Test-Path -LiteralPath $Path)) {
        throw "缺少支付宝沙箱配置文件：$Path"
    }
}

$env:MANAGER_ALIPAY_ENABLED = "true"
$env:MANAGER_ALIPAY_ENVIRONMENT = "sandbox"
$env:MANAGER_ALIPAY_SERVER_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do"
$env:MANAGER_ALIPAY_APP_ID = (Get-Content -Raw -Path $AppIdPath).Trim()
$env:MANAGER_ALIPAY_MERCHANT_PRIVATE_KEY = (Get-Content -Raw -Path $PrivateKeyPath).Trim()
$env:MANAGER_ALIPAY_PUBLIC_KEY = (Get-Content -Raw -Path $AlipayPublicKeyPath).Trim()
$env:MANAGER_ALIPAY_SIGN_TYPE = "RSA2"
$env:MANAGER_ALIPAY_CHARSET = "UTF-8"
$env:MANAGER_ALIPAY_FORMAT = "json"
$env:MANAGER_ALIPAY_ALLOW_ORDER_NO_AS_OUT_TRADE_NO = "false"
$env:MAVEN_OPTS = "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED"

$Listener = Get-NetTCPConnection -LocalPort 9965 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($Listener) {
    Stop-Process -Id $Listener.OwningProcess -Force
    Start-Sleep -Seconds 3
}

$Command = 'mvn spring-boot:run -DskipTests "-Dspring-boot.run.mainClass=cn.fuguang.manager.ContainerManagerApplication" > ..\work\manager-run-20260531.log 2>&1'
Start-Process -FilePath "cmd.exe" -ArgumentList "/c $Command" -WorkingDirectory (Join-Path $Root "container-manager") -WindowStyle Hidden -Environment @{
    MANAGER_ALIPAY_ENABLED = $env:MANAGER_ALIPAY_ENABLED
    MANAGER_ALIPAY_ENVIRONMENT = $env:MANAGER_ALIPAY_ENVIRONMENT
    MANAGER_ALIPAY_SERVER_URL = $env:MANAGER_ALIPAY_SERVER_URL
    MANAGER_ALIPAY_APP_ID = $env:MANAGER_ALIPAY_APP_ID
    MANAGER_ALIPAY_MERCHANT_PRIVATE_KEY = $env:MANAGER_ALIPAY_MERCHANT_PRIVATE_KEY
    MANAGER_ALIPAY_PUBLIC_KEY = $env:MANAGER_ALIPAY_PUBLIC_KEY
    MANAGER_ALIPAY_SIGN_TYPE = $env:MANAGER_ALIPAY_SIGN_TYPE
    MANAGER_ALIPAY_CHARSET = $env:MANAGER_ALIPAY_CHARSET
    MANAGER_ALIPAY_FORMAT = $env:MANAGER_ALIPAY_FORMAT
    MANAGER_ALIPAY_ALLOW_ORDER_NO_AS_OUT_TRADE_NO = $env:MANAGER_ALIPAY_ALLOW_ORDER_NO_AS_OUT_TRADE_NO
    MAVEN_OPTS = $env:MAVEN_OPTS
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
