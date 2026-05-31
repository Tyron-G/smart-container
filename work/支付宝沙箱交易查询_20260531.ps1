# 2026-05-31: Query Alipay sandbox trade by out_trade_no and print trade_no.
param(
    [Parameter(Mandatory = $true)]
    [string]$OutTradeNo,
    [string]$SecretDir = "work/.payment-secrets/alipay-sandbox"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path $PSScriptRoot -Parent
$FullSecretDir = Join-Path $Root $SecretDir
$AppId = (Get-Content -Raw -Path (Join-Path $FullSecretDir "app_id.txt")).Trim()
$PrivateKey = (Get-Content -Raw -Path (Join-Path $FullSecretDir "merchant_private_key_single_line.txt")).Trim()
$AlipayPublicKey = (Get-Content -Raw -Path (Join-Path $FullSecretDir "alipay_public_key.txt")).Trim()

$JavaHome = $env:JAVA_HOME
if (-not $JavaHome -or -not (Test-Path -LiteralPath (Join-Path $JavaHome "bin\java.exe"))) {
    $JavaHome = "C:\Program Files\Java\jdk-1.8"
}
$JavaExe = Join-Path $JavaHome "bin\java.exe"
$JavacExe = Join-Path $JavaHome "bin\javac.exe"

$WorkDir = Join-Path $Root "work/.payment-secrets/alipay-sandbox-tools"
$DepDir = Join-Path $WorkDir "deps"
if (-not (Test-Path -LiteralPath $DepDir)) {
    New-Item -ItemType Directory -Force -Path $DepDir | Out-Null
    Push-Location (Join-Path $Root "container-manager")
    try {
        mvn -q dependency:copy-dependencies "-DincludeScope=runtime" "-DoutputDirectory=$DepDir" | Out-Null
    }
    finally {
        Pop-Location
    }
}
$Classpath = (($DepDir, $WorkDir) + (Get-ChildItem -Path $DepDir -Filter "*.jar" | Select-Object -ExpandProperty FullName)) -join ";"

$Source = @"
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;

public class AlipaySandboxTradeQuery {
    public static void main(String[] args) throws Exception {
        AlipayConfig config = new AlipayConfig();
        config.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do");
        config.setAppId(args[0]);
        config.setPrivateKey(args[1]);
        config.setAlipayPublicKey(args[2]);
        config.setFormat("json");
        config.setCharset("UTF-8");
        config.setSignType("RSA2");
        AlipayClient client = new DefaultAlipayClient(config);

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{\"out_trade_no\":\"" + args[3] + "\"}");
        AlipayTradeQueryResponse response = client.execute(request);
        System.out.println("success=" + response.isSuccess());
        System.out.println("outTradeNo=" + response.getOutTradeNo());
        System.out.println("tradeNo=" + response.getTradeNo());
        System.out.println("tradeStatus=" + response.getTradeStatus());
        System.out.println("code=" + response.getCode());
        System.out.println("msg=" + response.getMsg());
        System.out.println("subCode=" + response.getSubCode());
        System.out.println("subMsg=" + response.getSubMsg());
        System.out.println("body=" + response.getBody());
    }
}
"@

$JavaFile = Join-Path $WorkDir "AlipaySandboxTradeQuery.java"
$Source | Set-Content -Path $JavaFile -Encoding UTF8
& $JavacExe "-encoding" "UTF-8" "-source" "8" "-target" "8" "-proc:none" "-cp" $Classpath $JavaFile
& $JavaExe -cp $Classpath AlipaySandboxTradeQuery $AppId $PrivateKey $AlipayPublicKey $OutTradeNo
