# 2026-05-31: Verify manager WeChat refund workflow in local integration mode.
param(
    [string]$OrderNo = "ORD-20260530-001",
    [decimal]$Amount = 0.01,
    [string]$ManagerBaseUrl = "http://localhost:9965/manager"
)

$ErrorActionPreference = "Stop"
$Login = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/auth/login" -Method Post -ContentType "application/json" -Body (@{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json)
if ($Login.code -ne "000000") {
    throw "登录失败：$($Login.message)"
}

$Headers = @{ Authorization = "Bearer $($Login.data.token)" }
$Body = @{
    requestNo = "RF-WECHAT-LOCAL-" + (Get-Date -Format "yyyyMMddHHmmss")
    orderNo = $OrderNo
    amount = $Amount
    reason = "wechat local refund verify"
    operator = "admin"
    channelType = "WECHAT"
    originalChannelTradeNo = "4200000000000000000000000000000000"
} | ConvertTo-Json

Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/order/refund" -Method Post -Headers $Headers -ContentType "application/json" -Body $Body | ConvertTo-Json -Depth 8
