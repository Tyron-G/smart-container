# 2026-05-31: Local deployment readiness verification for manager and admin frontend.
param(
    [string]$ManagerBaseUrl = "http://localhost:9965/manager",
    [string]$FrontendUrl = "http://127.0.0.1:5173"
)

$ErrorActionPreference = "Stop"
$Results = New-Object System.Collections.Generic.List[object]

function Add-Result {
    param([string]$Name, [bool]$Passed, [string]$Message)
    $Results.Add([pscustomobject]@{ Name = $Name; Passed = $Passed; Message = $Message })
}

function Test-Http {
    param([string]$Name, [string]$Url)
    try {
        $Response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 10
        Add-Result $Name ($Response.StatusCode -ge 200 -and $Response.StatusCode -lt 500) "HTTP $($Response.StatusCode)"
    }
    catch {
        Add-Result $Name $false $_.Exception.Message
    }
}

$Login = $null
try {
    $Login = Invoke-RestMethod -Uri "$ManagerBaseUrl/api/manager/auth/login" -Method Post -ContentType "application/json" -Body (@{ username = "admin"; password = "admin123" } | ConvertTo-Json)
    Add-Result "manager login" ($Login.code -eq "000000") $Login.code
}
catch {
    Add-Result "manager login" $false $_.Exception.Message
}

$ManagerPort = Get-NetTCPConnection -LocalPort 9965 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
Add-Result "manager port 9965" ([bool]$ManagerPort) ($(if ($ManagerPort) { "PID $($ManagerPort.OwningProcess)" } else { "not listening" }))

$FrontendPort = Get-NetTCPConnection -LocalPort 5173 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
Add-Result "frontend port 5173" ([bool]$FrontendPort) ($(if ($FrontendPort) { "PID $($FrontendPort.OwningProcess)" } else { "not listening" }))

try {
    $Headers = @{ Authorization = "Bearer $($Login.data.token)" }
    $Response = Invoke-WebRequest -Uri "$ManagerBaseUrl/api/manager/auth/permissions" -Headers $Headers -UseBasicParsing -TimeoutSec 10
    Add-Result "manager auth endpoint" ($Response.StatusCode -eq 200) "HTTP $($Response.StatusCode)"
}
catch {
    Add-Result "manager auth endpoint" $false $_.Exception.Message
}
Test-Http "frontend index" "$FrontendUrl/"

$ManagerJar = Join-Path $PSScriptRoot "..\container-manager\target\container-manager-1.0-SNAPSHOT.jar"
Add-Result "manager jar exists" (Test-Path -LiteralPath $ManagerJar) $ManagerJar

$FrontendDist = Join-Path (Split-Path $PSScriptRoot -Parent) "..\smart-container-admin\dist\index.html"
Add-Result "frontend dist exists" (Test-Path -LiteralPath $FrontendDist) $FrontendDist

$DbCheckSql = @"
select count(1) as table_count
from information_schema.tables
where table_schema = 'smart_container'
  and table_name in ('payment_operation_record','operation_log','order_refund_record','order_adjust_record');
"@

try {
    $DbResult = $DbCheckSql | mysql --default-character-set=utf8mb4 -hlocalhost -P3306 -uroot -p123456 -N -B
    Add-Result "database required tables" ($DbResult.Trim() -eq "4") "matched $($DbResult.Trim())/4"
}
catch {
    Add-Result "database required tables" $false $_.Exception.Message
}

$Results | Format-Table -AutoSize
$Failed = $Results | Where-Object { -not $_.Passed }
if ($Failed.Count -gt 0) {
    throw "Deployment verification failed: $($Failed.Count) check(s) failed."
}
