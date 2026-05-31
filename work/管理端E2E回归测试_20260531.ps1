# 2026-05-31: Local manager API regression checks for happy paths and error paths.
param(
    [string]$BaseUrl = "http://localhost:9965/manager"
)

$ErrorActionPreference = "Stop"
$Results = New-Object System.Collections.Generic.List[object]
$Login = Invoke-RestMethod -Uri "$BaseUrl/api/manager/auth/login" -Method Post -ContentType "application/json" -Body (@{ username = "admin"; password = "admin123" } | ConvertTo-Json)
$Headers = @{ Authorization = "Bearer $($Login.data.token)" }

function Invoke-Check {
    param(
        [string]$Name,
        [scriptblock]$Action,
        [bool]$ExpectSuccess = $true
    )

    try {
        $Response = & $Action
        $Code = [string]$Response.code
        $Passed = if ($ExpectSuccess) { $Code -eq "000000" -or [string]$Response.message -like "*已处理*" } else { $Code -ne "000000" }
        $Results.Add([pscustomobject]@{
            Name = $Name
            Passed = $Passed
            Code = $Code
            Message = [string]$Response.message
        })
    }
    catch {
        $Results.Add([pscustomobject]@{
            Name = $Name
            Passed = $false
            Code = "EXCEPTION"
            Message = $_.Exception.Message
        })
    }
}

function Post-Json {
    param(
        [string]$Path,
        [hashtable]$Body
    )
    Invoke-RestMethod -Uri "$BaseUrl$Path" -Method Post -Headers $Headers -ContentType "application/json" -Body ($Body | ConvertTo-Json -Depth 8)
}

Invoke-Check "order detail with items" { Invoke-RestMethod "$BaseUrl/api/manager/order/detail?orderNo=ORD-20260530-001" -Headers $Headers }
Invoke-Check "order refund" { Post-Json "/api/manager/order/refund" @{ requestNo = "RF-E2E-20260531"; orderNo = "ORD-20260530-001"; amount = 0.11; reason = "e2e refund"; operator = "admin" } }
Invoke-Check "order supplement charge" { Post-Json "/api/manager/order/supplementCharge" @{ requestNo = "ADJ-E2E-20260531"; orderNo = "ORD-20260530-001"; amount = 0.12; reason = "e2e adjust"; operator = "admin" } }
Invoke-Check "order exception handle" { Post-Json "/api/manager/order/handleException" @{ orderNo = "ORD-20260530-003"; result = "e2e handled"; operator = "admin" } }
Invoke-Check "customer agreements" { Invoke-RestMethod "$BaseUrl/api/manager/customer/agreements?customerId=CUST-10001" -Headers $Headers }
Invoke-Check "customer order page" { Invoke-RestMethod "$BaseUrl/api/manager/customer/orders?customerId=CUST-10001&pageNum=1&pageSize=5" -Headers $Headers }
Invoke-Check "device save" { Post-Json "/api/manager/device/save" @{ deviceId = "DEV-E2E-20260531"; deviceSn = "SN-E2E-20260531"; deviceName = "E2E Device"; deviceStatus = "ONLINE"; address = "Local Test"; operator = "admin" } }
Invoke-Check "gate save" { Post-Json "/api/manager/device/saveGate" @{ deviceId = "DEV-E2E-20260531"; gateId = "GATE-E2E-20260531-01"; gateName = "E2E Gate"; gateCode = "01"; sort = 1; gateStatus = "ONLINE"; operator = "admin" } }
Invoke-Check "device events" { Invoke-RestMethod "$BaseUrl/api/manager/device/events?processStatus=CREATED&pageNum=1&pageSize=5" -Headers $Headers }
Invoke-Check "event processed" { Post-Json "/api/manager/device/markEventProcessed" @{ id = 1; remark = "e2e processed"; operator = "admin" } }
Invoke-Check "role save" { Post-Json "/api/manager/auth/saveRole" @{ roleId = "ROLE-E2E-20260531"; roleName = "E2E Role"; roleCode = "E2E_TEST"; description = "regression"; status = "ACTIVE"; operator = "admin" } }
Invoke-Check "role disable" { Post-Json "/api/manager/auth/changeRoleStatus" @{ roleId = "ROLE-E2E-20260531"; status = "DISABLED"; operator = "admin" } }
Invoke-Check "role enable" { Post-Json "/api/manager/auth/changeRoleStatus" @{ roleId = "ROLE-E2E-20260531"; status = "ACTIVE"; operator = "admin" } }
Invoke-Check "permission save" { Post-Json "/api/manager/auth/savePermission" @{ permissionId = "PERM-E2E-20260531"; permissionName = "E2E Permission"; permissionCode = "e2e:test"; permissionType = "ACTION"; menuKey = "system"; description = "regression"; sort = 999; status = "ACTIVE"; operator = "admin" } }
Invoke-Check "permission disable" { Post-Json "/api/manager/auth/changePermissionStatus" @{ permissionId = "PERM-E2E-20260531"; status = "DISABLED"; operator = "admin" } }
Invoke-Check "permission list includes disabled" {
    $Response = Invoke-RestMethod "$BaseUrl/api/manager/auth/permissions" -Headers $Headers
    $Target = $Response.data | Where-Object { $_.permission_id -eq "PERM-E2E-20260531" } | Select-Object -First 1
    if (-not $Target -or $Target.status -ne "DISABLED") {
        return [pscustomobject]@{ code = "999999"; message = "停用权限未出现在权限配置列表" }
    }
    return [pscustomobject]@{ code = "000000"; message = $null }
}
Invoke-Check "permission enable" { Post-Json "/api/manager/auth/changePermissionStatus" @{ permissionId = "PERM-E2E-20260531"; status = "ACTIVE"; operator = "admin" } }
Invoke-Check "coupon issues" { Invoke-RestMethod "$BaseUrl/api/manager/couponFlow/issues?pageNum=1&pageSize=5" -Headers $Headers }
Invoke-Check "coupon uses" { Invoke-RestMethod "$BaseUrl/api/manager/couponFlow/uses?pageNum=1&pageSize=5" -Headers $Headers }
Invoke-Check "audit logs" { Invoke-RestMethod "$BaseUrl/api/manager/audit/logs?pageNum=1&pageSize=5" -Headers $Headers }
Invoke-Check "error unauthorized audit" { Invoke-RestMethod "$BaseUrl/api/manager/audit/logs?pageNum=1&pageSize=1" } $false

Invoke-Check "error refund missing order" { Post-Json "/api/manager/order/refund" @{ orderNo = "ORD-NOT-EXIST"; amount = 1; reason = "invalid"; operator = "admin" } } $false
Invoke-Check "error coupon issue disabled or missing config" { Post-Json "/api/manager/couponFlow/issue" @{ couponConfigId = "CP-NOT-EXIST"; customerId = "CUST-10001"; operator = "admin" } } $false
Invoke-Check "error role save missing code" { Post-Json "/api/manager/auth/saveRole" @{ roleName = "Invalid Role"; operator = "admin" } } $false

$Results | Format-Table -AutoSize
$Failed = $Results | Where-Object { -not $_.Passed }
if ($Failed.Count -gt 0) {
    throw "Regression failed: $($Failed.Count) check(s) failed."
}
