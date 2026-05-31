# 2026-05-31: Generate local RSA2 keys for Alipay sandbox.
param(
    [string]$OutputDir = "work/.payment-secrets/alipay-sandbox"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path $PSScriptRoot -Parent
$FullOutputDir = Join-Path $Root $OutputDir
$JavaHome = $env:JAVA_HOME
if (-not $JavaHome -or -not (Test-Path -LiteralPath (Join-Path $JavaHome "bin\java.exe"))) {
    $JavaHome = "C:\Program Files\Java\jdk-1.8"
}
$JavaExe = Join-Path $JavaHome "bin\java.exe"
$JavacExe = Join-Path $JavaHome "bin\javac.exe"
if (-not (Test-Path -LiteralPath $JavaExe) -or -not (Test-Path -LiteralPath $JavacExe)) {
    throw "未找到可用 JDK：$JavaHome"
}
New-Item -ItemType Directory -Force -Path $FullOutputDir | Out-Null

$Source = @"
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class GenerateAlipaySandboxKey {
    private static String pem(String title, byte[] content) {
        String body = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(content);
        return "-----BEGIN " + title + "-----\n" + body + "\n-----END " + title + "-----\n";
    }

    public static void main(String[] args) throws Exception {
        String outputDir = args[0];
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        Files.write(Paths.get(outputDir, "merchant_private_key_pkcs8.pem"), pem("PRIVATE KEY", privateKey.getEncoded()).getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(outputDir, "merchant_private_key_single_line.txt"), Base64.getEncoder().encodeToString(privateKey.getEncoded()).getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(outputDir, "app_public_key.pem"), pem("PUBLIC KEY", publicKey.getEncoded()).getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(outputDir, "app_public_key_for_alipay_console.txt"), Base64.getEncoder().encodeToString(publicKey.getEncoded()).getBytes(StandardCharsets.UTF_8));
    }
}
"@

$JavaFile = Join-Path $FullOutputDir "GenerateAlipaySandboxKey.java"
$Source | Set-Content -Path $JavaFile -Encoding UTF8
& $JavacExe "-source" "8" "-target" "8" $JavaFile
& $JavaExe -cp $FullOutputDir GenerateAlipaySandboxKey $FullOutputDir
Remove-Item -LiteralPath $JavaFile -Force
Remove-Item -LiteralPath (Join-Path $FullOutputDir "GenerateAlipaySandboxKey.class") -Force

Write-Host "应用公钥（复制到支付宝沙箱后台）：" -ForegroundColor Cyan
Get-Content -Raw -Path (Join-Path $FullOutputDir "app_public_key_for_alipay_console.txt")
Write-Host ""
Write-Host "本地私钥已生成：" -ForegroundColor Green
Write-Host (Join-Path $FullOutputDir "merchant_private_key_single_line.txt")
