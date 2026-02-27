# Catan_test (Test-only Repo)

This repository is for testing/integration only.

## Unused Code Checklist (current scan)

### Classes not referenced by other classes
- `src/catan/GameState.java`
- `src/catan/GameConfig.java`

### Methods not called
- `src/catan/BuildPlanner.java` -> `forcedDecision(Board board, Player player)`

## Run the Game

### Windows launcher
Use:

```powershell
.\start_game.bat
```

### macOS launcher
Use:

```bash
chmod +x ./start_game.sh
./start_game.sh
```

Notes:
- The game asks for player names first.
- After name input, the visualizer process is started automatically.

## Manual Run (without launcher)

Compile:

```powershell
javac -encoding UTF-8 -d out/production/Catan_test src/catan/*.java
```

Run:

```powershell
java -cp out/production/Catan_test catan.HumanGameLauncher game.config visualize/state.json
```

## Human Commands

- `Roll`
- `List`
- `Actions`
- `Build settlement <nodeId>`
- `Build city <nodeId>`
- `Build road <fromNodeId, toNodeId>`
- `Go`

Output format:

```text
[TurnID] / [PlayerID]: [Action]
```
