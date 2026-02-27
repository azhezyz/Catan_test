$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$outDir = Join-Path $repoRoot "out\production\Catan_test"
$srcGlob = Join-Path $repoRoot "src\catan\*.java"
$configFile = Join-Path $repoRoot "game.config"
$statePathArg = Join-Path $repoRoot "visualize\state.json"

Push-Location $repoRoot
try {
    Write-Host "[Launcher] Compiling Java sources..."
    & javac -encoding UTF-8 -d $outDir $srcGlob
    if ($LASTEXITCODE -ne 0) {
        throw "javac failed."
    }

    Write-Host "[Launcher] Starting game..."
    & java -cp $outDir catan.HumanGameLauncher $configFile $statePathArg
} finally {
    Pop-Location
}
