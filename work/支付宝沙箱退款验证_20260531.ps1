# 2026-05-31: Call manager refund API with Alipay sandbox original trade number.
param(
    [Parameter(Mandatory = $true)]
    [string]$OrderNo,
    [Parameter(Mandatory = $true)]
    [string]$AlipayTradeNo,
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
    requestNo = "RF-ALIPAY-SANDBOX-" + (Get-Date -Format "yyyyMMddHHmmss")
    orderNo = $OrderNo
    amount = $Amount
    reason = "alipay sandbox refund verify"
    operator = "admin"
    channelType = "ALIPAY"
    originalChannelTradeNo = $AlipayTradeNo
} | ConvertTo-Json

Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/order/refund" -Method Post -Headers $Headers -ContentType "application/json" -Body $Body | ConvertTo-Json -Depth 8
