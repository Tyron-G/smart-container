# 2026-05-31: Local pre-deployment verification for smart-container manager/admin.
param(
    [string]$ManagerBaseUrl = "http://localhost:9965/manager",
    [string]$FrontendUrl = "http://127.0.0.1:5173",
    [string]$MysqlHost = "localhost",
    [int]$MysqlPort = 3306,
    [string]$MysqlUser = "root",
    [string]$MysqlPassword = "123456",
    [string]$Database = "smart_container",
    [switch]$SkipBuild,
    [switch]$SkipFrontend
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path $PSScriptRoot -Parent
$FrontendRoot = "D:\smart-container-admin"
$Results = New-Object System.Collections.Generic.List[object]

function Add-Result {
    param([string]$Name, [bool]$Passed, [string]$Message)
    $Results.Add([pscustomobject]@{ Name = $Name; Passed = $Passed; Message = $Message })
}

function Invoke-Step {
    param([string]$Name, [scriptblock]$Action)
    try {
        $Message = & $Action
        Add-Result $Name $true ([string]$Message)
    }
    catch {
        Add-Result $Name $false $_.Exception.Message
    }
}

function Invoke-MysqlScalar {
    param([string]$Sql)
    $Args = @(
        "--default-character-set=utf8mb4",
        "-h$MysqlHost",
        "-P$MysqlPort",
        "-u$MysqlUser",
        "-p$MysqlPassword",
        $Database,
        "-N",
        "-B"
    )
    $Sql | & mysql @Args
}

if (-not $SkipBuild) {
    Invoke-Step "backend package" {
        Push-Location $ProjectRoot
        try {
            $env:MAVEN_OPTS = "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED"
            mvn -pl container-manager -am -DskipTests package | Out-Null
            "ok"
        }
        finally {
            Pop-Location
        }
    }

    if (-not $SkipFrontend) {
        Invoke-Step "frontend build" {
            Push-Location $FrontendRoot
            try {
                npm run build | Out-Null
                "ok"
            }
            finally {
                Pop-Location
            }
        }
    }
}

Invoke-Step "manager jar exists" {
    $Jar = Join-Path $ProjectRoot "container-manager\target\container-manager-1.0-SNAPSHOT.jar"
    if (-not (Test-Path -LiteralPath $Jar)) { throw "missing $Jar" }
    $Jar
}

Invoke-Step "frontend dist exists" {
    if ($SkipFrontend) { return "skipped" }
    $Index = Join-Path $FrontendRoot "dist\index.html"
    if (-not (Test-Path -LiteralPath $Index)) { throw "missing $Index" }
    $Index
}

Invoke-Step "manager port 9965" {
    $Conn = Get-NetTCPConnection -LocalPort 9965 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $Conn) { throw "not listening" }
    "PID $($Conn.OwningProcess)"
}

Invoke-Step "frontend port 5173" {
    if ($SkipFrontend) { return "skipped" }
    $Conn = Get-NetTCPConnection -LocalPort 5173 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $Conn) { throw "not listening" }
    "PID $($Conn.OwningProcess)"
}

Invoke-Step "database required tables" {
    $Sql = @"
select count(1)
from information_schema.tables
where table_schema = '$Database'
  and table_name in (
    'sys_admin_user','sys_role','sys_permission','sys_user_role','sys_role_permission',
    'payment_operation_record','operation_log','order_item','order_refund_record',
    'order_adjust_record','order_exception_record','coupon_issue_record','coupon_use_record'
  );
"@
    $Count = (Invoke-MysqlScalar $Sql).Trim()
    if ($Count -ne "13") { throw "matched $Count/13" }
    "matched $Count/13"
}

Invoke-Step "payment audit columns" {
    $Sql = @"
select count(1)
from information_schema.columns
where table_schema = '$Database'
  and table_name = 'payment_operation_record'
  and column_name in ('error_code','channel_request_payload','channel_response_payload');
"@
    $Count = (Invoke-MysqlScalar $Sql).Trim()
    if ($Count -ne "3") { throw "matched $Count/3" }
    "matched $Count/3"
}

Invoke-Step "admin permission count" {
    $Sql = @"
select count(distinct p.permission_code)
from sys_admin_user u
join sys_user_role ur on u.user_id = ur.user_id
join sys_role_permission rp on ur.role_id = rp.role_id
join sys_permission p on rp.permission_id = p.permission_id
where u.username = 'admin'
  and u.status = 'ACTIVE'
  and p.status = 'ACTIVE';
"@
    $Count = (Invoke-MysqlScalar $Sql).Trim()
    if ([int]$Count -lt 35) { throw "admin permission count $Count < 35" }
    "admin permission count $Count"
}

Invoke-Step "manager login api" {
    $Login = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/auth/login" -Method Post -ContentType "application/json" -Body (@{ username = "admin"; password = "admin123" } | ConvertTo-Json)
    if ($Login.code -ne "000000") { throw $Login.message }
    "token $($Login.data.token)"
}

Invoke-Step "manager protected api" {
    $Login = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/auth/login" -Method Post -ContentType "application/json" -Body (@{ username = "admin"; password = "admin123" } | ConvertTo-Json)
    $Headers = @{ Authorization = "Bearer $($Login.data.token)" }
    $Resp = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/auth/permissions" -Headers $Headers
    if ($Resp.code -ne "000000") { throw $Resp.message }
    "permissions $($Resp.data.Count)"
}

Invoke-Step "payment local idempotency" {
    $Login = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/auth/login" -Method Post -ContentType "application/json" -Body (@{ username = "admin"; password = "admin123" } | ConvertTo-Json)
    $Headers = @{ Authorization = "Bearer $($Login.data.token)" }
    $RequestNo = "RF-VERIFY-20260531"
    $Body = @{ requestNo = $RequestNo; orderNo = "ORD-20260530-001"; amount = 0.01; reason = "deploy verify"; operator = "admin"; channelType = "LOCAL_SIMULATED" } | ConvertTo-Json
    $First = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/order/refund" -Method Post -Headers $Headers -ContentType "application/json" -Body $Body
    $Second = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/order/refund" -Method Post -Headers $Headers -ContentType "application/json" -Body $Body
    if ($First.code -ne "000000" -or $Second.code -ne "000000") { throw "refund verify failed" }
    if (-not $Second.data.idempotent) { throw "second request is not idempotent" }
    "idempotent ok"
}

Invoke-Step "payment alipay disabled safe" {
    $Login = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/auth/login" -Method Post -ContentType "application/json" -Body (@{ username = "admin"; password = "admin123" } | ConvertTo-Json)
    $Headers = @{ Authorization = "Bearer $($Login.data.token)" }
    $RequestNo = "RF-ALIPAY-SAFE-20260531"
    $Body = @{ requestNo = $RequestNo; orderNo = "ORD-20260530-001"; amount = 0.01; reason = "alipay safe verify"; operator = "admin"; channelType = "ALIPAY" } | ConvertTo-Json
    $Resp = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/order/refund" -Method Post -Headers $Headers -ContentType "application/json" -Body $Body
    if ($Resp.code -ne "000000") { throw $Resp.message }
    if ($Resp.data.status -ne "PENDING_CONFIG" -and $Resp.data.status -ne "CONFIG_ERROR") { throw "unexpected alipay status $($Resp.data.status)" }
    if ($Resp.data.errorCode -notin @("ALIPAY_DISABLED", "ALIPAY_CONFIG_ERROR", "ALIPAY_ORIGINAL_TRADE_NO_MISSING")) { throw "unexpected error code $($Resp.data.errorCode)" }
    "$($Resp.data.status) $($Resp.data.errorCode)"
}

Invoke-Step "payment wechat disabled safe" {
    $Login = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/auth/login" -Method Post -ContentType "application/json" -Body (@{ username = "admin"; password = "admin123" } | ConvertTo-Json)
    $Headers = @{ Authorization = "Bearer $($Login.data.token)" }
    $Config = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/payment/configStatus" -Headers $Headers
    if ($Config.code -eq "000000" -and $Config.data.wechat.readyForLocalWechatRefund) {
        # 2026-05-31: 微信本地联调模式应跑通 WECHAT 业务链路，但不能触发真实资金。
        $RequestNo = "RF-WECHAT-LOCAL-VERIFY-20260531"
        $Body = @{ requestNo = $RequestNo; orderNo = "ORD-20260530-001"; amount = 0.01; reason = "wechat local verify"; operator = "admin"; channelType = "WECHAT"; originalChannelTradeNo = "4200000000000000000000000000000000" } | ConvertTo-Json
        $Resp = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/order/refund" -Method Post -Headers $Headers -ContentType "application/json" -Body $Body
        if ($Resp.code -ne "000000") { throw $Resp.message }
        if ($Resp.data.status -ne "SUCCESS") { throw "unexpected wechat local status $($Resp.data.status)" }
        if ([string]$Resp.data.channelMessage -notlike "*未触发微信真实资金划转*") { throw "wechat local mode message missing safe marker" }
        return "$($Resp.data.status) local-test"
    }
    $RequestNo = "RF-WECHAT-SAFE-20260531"
    $Body = @{ requestNo = $RequestNo; orderNo = "ORD-20260530-001"; amount = 0.01; reason = "wechat safe verify"; operator = "admin"; channelType = "WECHAT" } | ConvertTo-Json
    $Resp = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/order/refund" -Method Post -Headers $Headers -ContentType "application/json" -Body $Body
    if ($Resp.code -ne "000000") { throw $Resp.message }
    if ($Resp.data.status -ne "PENDING_CONFIG" -and $Resp.data.status -ne "CONFIG_ERROR") { throw "unexpected wechat status $($Resp.data.status)" }
    if ($Resp.data.errorCode -notin @("WECHAT_DISABLED", "WECHAT_CONFIG_ERROR", "WECHAT_ORIGINAL_TRADE_NO_MISSING")) { throw "unexpected error code $($Resp.data.errorCode)" }
    "$($Resp.data.status) $($Resp.data.errorCode)"
}

$Results | Format-Table -AutoSize
$Failed = $Results | Where-Object { -not $_.Passed }
if ($Failed.Count -gt 0) {
    throw "Pre-deployment verification failed: $($Failed.Count) check(s) failed."
}
