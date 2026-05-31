# 2026-05-31: Build a local delivery bundle for smart-container manager handoff.
param(
    [string]$FrontendRoot = "D:\smart-container-admin",
    [string]$OutputDir = "work/交付包_20260531"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path $PSScriptRoot -Parent
$FullOutputDir = Join-Path $Root $OutputDir
$BackendJar = Join-Path $Root "container-manager\target\container-manager-1.0-SNAPSHOT.jar"
$FrontendDist = Join-Path $FrontendRoot "dist"

if (-not (Test-Path -LiteralPath $BackendJar)) {
    throw "缺少后端 JAR：$BackendJar"
}
if (-not (Test-Path -LiteralPath (Join-Path $FrontendDist "index.html"))) {
    throw "缺少前端 dist：$FrontendDist"
}

New-Item -ItemType Directory -Force -Path $FullOutputDir | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $FullOutputDir "backend") | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $FullOutputDir "frontend") | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $FullOutputDir "sql") | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $FullOutputDir "scripts") | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $FullOutputDir "docs") | Out-Null

Copy-Item -LiteralPath $BackendJar -Destination (Join-Path $FullOutputDir "backend\container-manager-1.0-SNAPSHOT.jar") -Force

$FrontendZip = Join-Path $FullOutputDir "frontend\smart-container-admin-dist_20260531.zip"
if (Test-Path -LiteralPath $FrontendZip) {
    Remove-Item -LiteralPath $FrontendZip -Force
}
Compress-Archive -Path (Join-Path $FrontendDist "*") -DestinationPath $FrontendZip -Force

$SqlFiles = @(
    "建表与测试数据_20260530.sql",
    "管理端完整功能补齐_20260531.sql",
    "管理端账号权限初始化_20260531.sql",
    "权限按钮与支付通道补齐_20260531.sql",
    "真实支付通道审计字段_20260531.sql"
)
foreach ($File in $SqlFiles) {
    $Path = Join-Path $Root "work\$File"
    if (Test-Path -LiteralPath $Path) {
        Copy-Item -LiteralPath $Path -Destination (Join-Path $FullOutputDir "sql\$File") -Force
    }
}

$ScriptFiles = @(
    "部署前完整验证_20260531.ps1",
    "管理端E2E回归测试_20260531.ps1",
    "真实支付通道配置检查_20260531.ps1",
    "启动微信本地联调环境_20260531.ps1",
    "微信本地退款验证_20260531.ps1",
    "启动支付宝沙箱环境_20260531.ps1",
    "支付宝沙箱预下单_20260531.ps1",
    "支付宝沙箱交易查询_20260531.ps1",
    "支付宝沙箱退款验证_20260531.ps1",
    "真实支付通道生产环境变量模板_20260531.ps1"
)
foreach ($File in $ScriptFiles) {
    $Path = Join-Path $Root "work\$File"
    if (Test-Path -LiteralPath $Path) {
        Copy-Item -LiteralPath $Path -Destination (Join-Path $FullOutputDir "scripts\$File") -Force
    }
}

$DocFiles = @(
    "本地启动与接口健康检查说明_20260530.md",
    "数据库表与业务对象对应说明_20260530.md",
    "接口归属说明_20260530.md",
    "真实支付通道接入说明_20260531.md",
    "交付清单_20260531.md",
    "部署验证报告_20260531.md"
)
foreach ($File in $DocFiles) {
    $Path = Join-Path $Root "docs\$File"
    if (Test-Path -LiteralPath $Path) {
        Copy-Item -LiteralPath $Path -Destination (Join-Path $FullOutputDir "docs\$File") -Force
    }
}

$ManifestPath = Join-Path $FullOutputDir "交付包清单_20260531.txt"
$Lines = @(
    "smart-container 管理端交付包",
    "生成时间：$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')",
    "后端 JAR：backend/container-manager-1.0-SNAPSHOT.jar",
    "前端 dist：frontend/smart-container-admin-dist_20260531.zip",
    "SQL：sql/",
    "验证脚本：scripts/",
    "文档：docs/",
    "说明：未包含 work/.payment-secrets、日志文件、node_modules、target 目录和任何真实密钥。"
)
$Lines | Set-Content -Path $ManifestPath -Encoding UTF8

Get-ChildItem -Path $FullOutputDir -Recurse -File | Select-Object FullName, Length
