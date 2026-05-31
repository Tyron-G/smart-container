# 2026-05-31: Check whether local payment channel configuration can enable real refund calls.
param(
    [string]$ManagerBaseUrl = "http://localhost:9965/manager",
    [string]$Username = "admin",
    [string]$Password = "admin123"
)

$ErrorActionPreference = "Stop"

$Login = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/auth/login" -Method Post -ContentType "application/json" -Body (@{
    username = $Username
    password = $Password
} | ConvertTo-Json)

if ($Login.code -ne "000000") {
    throw "登录失败：$($Login.message)"
}

$Headers = @{ Authorization = "Bearer $($Login.data.token)" }
$Status = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/payment/configStatus" -Headers $Headers
if ($Status.code -ne "000000") {
    throw "配置检查失败：$($Status.message)"
}

$Status.data | ConvertTo-Json -Depth 8
