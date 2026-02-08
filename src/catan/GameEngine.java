package catan;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GameEngine {
    private static final Map<ResourceType, Integer> SETTLEMENT_COST = Map.of(
            ResourceType.WOOD, 1,
            ResourceType.BRICK, 1,
            ResourceType.SHEEP, 1,
            ResourceType.WHEAT, 1
    );
    private static final Map<ResourceType, Integer> ROAD_COST = Map.of(
            ResourceType.WOOD, 1,
            ResourceType.BRICK, 1
    );

    private final Board board;
    private final List<Player> players;
    private final List<Integer> diceSequence;

    public GameEngine(Board board, List<Player> players, List<Integer> diceSequence) {
        this.board = Objects.requireNonNull(board, "board");
        this.players = List.copyOf(Objects.requireNonNull(players, "players"));
        if (this.players.isEmpty()) {
            throw new IllegalArgumentException("At least one player is required.");
        }
        this.diceSequence = List.copyOf(Objects.requireNonNull(diceSequence, "diceSequence"));
        if (this.diceSequence.isEmpty()) {
            throw new IllegalArgumentException("Dice sequence cannot be empty.");
        }
        for (int roll : this.diceSequence) {
            if (roll < 2 || roll > 12) {
                throw new IllegalArgumentException("Dice roll out of range: " + roll);
            }
        }
    }

    public SimulationReport runSimulation(int rounds) {
        if (rounds <= 0) {
            throw new IllegalArgumentException("Rounds must be positive.");
        }
        List<String> log = new ArrayList<>();
        int rollIndex = 0;
        for (int round = 1; round <= rounds; round++) {
            int roll = diceSequence.get(rollIndex % diceSequence.size());
            rollIndex++;
            log.add(String.format("[Round %d] roll=%d", round, roll));
            distributeResources(roll, log);
            for (Player player : players) {
                buildSettlementIfPossible(player, log);
                buildRoadIfPossible(player, log);
            }
            log.add("-- End of round --");
        }
        return new SimulationReport(log, players);
    }

    private void distributeResources(int roll, List<String> log) {
        for (Tile tile : board.tilesForRoll(roll)) {
            for (int nodeId : tile.getAdjacentNodeIds()) {
                Node node = board.getNode(nodeId);
                node.getOwner().ifPresent(owner -> {
                    owner.addResource(tile.getResourceType(), 1);
                    log.add(String.format("  %s gains 1 %s from tile %d", owner.getName(), tile.getResourceType(), tile.getId()));
                });
            }
        }
    }

    private void buildSettlementIfPossible(Player player, List<String> log) {
        if (!player.canAfford(SETTLEMENT_COST)) {
            return;
        }
        for (Node node : board.getNodes()) {
            if (!node.isClaimed()) {
                node.claim(player);
                player.spend(SETTLEMENT_COST);
                player.addSettlement(node.getId());
                log.add(String.format("  %s builds settlement on node %d", player.getName(), node.getId()));
                return;
            }
        }
    }

    private void buildRoadIfPossible(Player player, List<String> log) {
        if (!player.canAfford(ROAD_COST)) {
            return;
        }
        for (Path path : board.getPaths()) {
            if (path.isClaimed()) {
                continue;
            }
            if (canClaimPath(player, path)) {
                path.claim(player);
                player.spend(ROAD_COST);
                player.addRoad(path.getId());
                log.add(String.format("  %s builds road on path %d", player.getName(), path.getId()));
                return;
            }
        }
    }

    private boolean canClaimPath(Player player, Path path) {
        for (int nodeId : player.getSettlementNodeIds()) {
            if (path.isAdjacentToNode(nodeId)) {
                return true;
            }
        }
        return false;
    }

    public static Map<ResourceType, Integer> initialResources(int wood, int brick, int sheep, int wheat, int ore) {
        EnumMap<ResourceType, Integer> resources = new EnumMap<>(ResourceType.class);
        resources.put(ResourceType.WOOD, wood);
        resources.put(ResourceType.BRICK, brick);
        resources.put(ResourceType.SHEEP, sheep);
        resources.put(ResourceType.WHEAT, wheat);
        resources.put(ResourceType.ORE, ore);
        return resources;
    }

    public static List<ActionDecision> availableActions(Board board, Player player) {
        List<ActionDecision> actions = new ArrayList<>();
        for (Node node : board.getNodes()) {
            if (!node.isClaimed() && player.canAfford(SETTLEMENT_COST)) {
                actions.add(ActionDecision.settlement(node.getId()));
            }
        }
        for (Path path : board.getPaths()) {
            if (!path.isClaimed() && isAdjacentToPlayerSettlement(path, player) && player.canAfford(ROAD_COST)) {
                actions.add(ActionDecision.road(path.getId()));
            }
        }
        return actions;
    }

    private static boolean isAdjacentToPlayerSettlement(Path path, Player player) {
        for (int nodeId : player.getSettlementNodeIds()) {
            if (path.isAdjacentToNode(nodeId)) {
                return true;
            }
        }
        return false;
    }
}
