#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT_DIR="$REPO_ROOT/out/production/Catan_test"
CONFIG_FILE="$REPO_ROOT/game.config"
STATE_FILE="$REPO_ROOT/visualize/state.json"

cd "$REPO_ROOT"

echo "[Launcher] Compiling Java sources..."
mkdir -p "$OUT_DIR"
javac -encoding UTF-8 -d "$OUT_DIR" src/catan/*.java

echo "[Launcher] Starting game..."
java -cp "$OUT_DIR" catan.HumanGameLauncher "$CONFIG_FILE" "$STATE_FILE"
