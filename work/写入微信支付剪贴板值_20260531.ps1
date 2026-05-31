# 2026-05-31: Write WeChat Pay values from clipboard into ignored local secret files.
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("MchId", "AppId", "ApiV3Key", "MerchantSerialNo")]
    [string]$Type,
    [string]$OutputDir = "work/.payment-secrets/wechat-production"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path $PSScriptRoot -Parent
$FullOutputDir = Join-Path $Root $OutputDir
New-Item -ItemType Directory -Force -Path $FullOutputDir | Out-Null

$Value = (Get-Clipboard -Raw).Trim()
if (-not $Value) {
    throw "剪贴板为空，请先在微信支付商户平台复制对应值。"
}

$MinimumLength = switch ($Type) {
    "MchId" { 8 }
    "AppId" { 8 }
    "ApiV3Key" { 32 }
    "MerchantSerialNo" { 20 }
}
if ($Value.Length -lt $MinimumLength) {
    throw "$Type 长度异常：$($Value.Length)，请重新复制正确内容。"
}
if ($Type -eq "ApiV3Key" -and $Value.Length -ne 32) {
    throw "ApiV3Key 必须是 32 个字符，请在微信支付商户平台重新设置或复制。"
}

$FileName = switch ($Type) {
    "MchId" { "mch_id.txt" }
    "AppId" { "app_id.txt" }
    "ApiV3Key" { "api_v3_key.txt" }
    "MerchantSerialNo" { "merchant_serial_no.txt" }
}

$Target = Join-Path $FullOutputDir $FileName
$Value | Set-Content -Path $Target -Encoding UTF8 -NoNewline

Write-Host "$Type 已写入本地密钥目录，长度：$($Value.Length)" -ForegroundColor Green
Write-Host $Target
