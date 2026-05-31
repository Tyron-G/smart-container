# 2026-05-31: Write Alipay sandbox values from clipboard into ignored local secret files.
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("AppId", "MerchantPrivateKey", "AlipayPublicKey")]
    [string]$Type,
    [string]$OutputDir = "work/.payment-secrets/alipay-sandbox"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path $PSScriptRoot -Parent
$FullOutputDir = Join-Path $Root $OutputDir
New-Item -ItemType Directory -Force -Path $FullOutputDir | Out-Null

$Value = (Get-Clipboard -Raw).Trim()
if (-not $Value) {
    throw "剪贴板为空，请先在支付宝沙箱页面复制对应值。"
}

$MinimumLength = switch ($Type) {
    "AppId" { 8 }
    "MerchantPrivateKey" { 1000 }
    "AlipayPublicKey" { 300 }
}
if ($Value.Length -lt $MinimumLength) {
    throw "$Type 长度异常：$($Value.Length)，请重新复制正确内容。"
}

$FileName = switch ($Type) {
    "AppId" { "app_id.txt" }
    "MerchantPrivateKey" { "merchant_private_key_single_line.txt" }
    "AlipayPublicKey" { "alipay_public_key.txt" }
}

$Target = Join-Path $FullOutputDir $FileName
$Value | Set-Content -Path $Target -Encoding UTF8 -NoNewline

Write-Host "$Type 已写入本地密钥目录，长度：$($Value.Length)" -ForegroundColor Green
Write-Host $Target
