package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Runs deterministic resource-only rounds (no building) to demonstrate production rules.
 */
public final class ResourceSimulationEngine {
    private final Board board;
    private final List<Player> players;
    private final List<Integer> diceSequence;

    public ResourceSimulationEngine(Board board, List<Player> players, List<Integer> diceSequence) {
        this.board = Objects.requireNonNull(board, "board");
        this.players = List.copyOf(Objects.requireNonNull(players, "players"));
        this.diceSequence = List.copyOf(Objects.requireNonNull(diceSequence, "diceSequence"));
        if (this.players.isEmpty()) {
            throw new IllegalArgumentException("At least one player is required.");
        }
        if (this.diceSequence.isEmpty()) {
            throw new IllegalArgumentException("Dice sequence cannot be empty.");
        }
    }

    public List<String> runResourceOnly(int rounds) {
        if (rounds <= 0 || rounds > 8192) {
            throw new IllegalArgumentException("Rounds must be between 1 and 8192.");
        }
        List<String> log = new ArrayList<>();
        int rollIndex = 0;
        for (int round = 1; round <= rounds; round++) {
            int roll = diceSequence.get(rollIndex % diceSequence.size());
            rollIndex++;
            log.add(String.format("[%d] / ALL : Rolled %d", round, roll));
            distributeResources(round, roll, log);
        }
        return log;
    }

    private void distributeResources(int round, int roll, List<String> log) {
        if (roll == 7) {
            return;
        }
        for (Tile tile : board.tilesForRoll(roll)) {
            ResourceType resource = tile.getResourceType();
            if (resource == null) {
                continue; // Desert tile produces nothing
            }
            for (int nodeId : tile.getAdjacentNodeIds()) {
                Node node = board.getNode(nodeId);
                node.getOwner().ifPresent(owner -> {
                    int amount = node.getBuilding().getType() == BuildingType.CITY ? 2 : 1;
                    owner.addResource(resource, amount);
                    log.add(String.format("[%d] / %s : Gained %d %s from Tile %d", round, owner.getName(), amount,
                            resource, tile.getId()));
                });
            }
        }
    }
}
