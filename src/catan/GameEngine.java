package catan;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
 * GameEngine is the central controller for the Catan simulation.
 * It manages the dice rolls, resource distribution, and building phases.
 */
public final class GameEngine {
    // Define the resource costs for Settlements and Roads.
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
    private final DiceSet diceSet;

    // Achievement tracking for the Longest Road bonus (+2 Victory Points).
    private Player longestRoadHolder = null;
    private int currentMaxRoadLength = 4; // Standard rules: must reach 5 to claim.

    /*
     * Constructor sets up the engine with a board and a list of players.
     */
    public GameEngine(Board board, List<Player> players) {
        this.board = Objects.requireNonNull(board, "board");
        this.players = List.copyOf(Objects.requireNonNull(players, "players"));
        if (this.players.isEmpty()) {
            throw new IllegalArgumentException("At least one player is required.");
        }
        this.diceSet = new DiceSet();
    }

    // Helper method to roll the dice.
    public int rollDice() {
        return diceSet.nextRoll();
    }

    /*
     * The main simulation loop.
     * For each round:
     * 1. Roll dice and give resources.
     * 2. Let each player build if they can afford it.
     * 3. Check if anyone has reached 10 Victory Points to win.
     */
    public SimulationReport runSimulation(int rounds) {
        if (rounds <= 0) {
            throw new IllegalArgumentException("Rounds must be positive.");
        }
        List<String> log = new ArrayList<>();
        for (int round = 1; round <= rounds; round++) {
            int roll = rollDice();
            log.add(String.format("[Round %d] roll=%d", round, roll));
            distributeResources(roll, log);

            for (Player player : players) {
                buildSettlementIfPossible(player, log);
                buildRoadIfPossible(player, log);

                // End game immediately if someone reaches 10 points.
                if (player.getVictoryPoints() >= 10) {
                    log.add(String.format("!!! %s wins with %d VPs in round %d !!!", 
                            player.getName(), player.getVictoryPoints(), round));
                    return new SimulationReport(log, players);
                }
            }
            log.add("-- End of round --");
        }
        return new SimulationReport(log, players);
    }

    /*
     * Resource Distribution:
     * Finds every tile matching the dice roll and gives resources 
     * to players with buildings adjacent to those tiles.
     */
    private void distributeResources(int roll, List<String> log) {
        for (Tile tile : board.tilesForRoll(roll)) {
            if (tile.getResourceType() == null) {
                continue; // Desert tile produces nothing.
            }
            for (int nodeId : tile.getAdjacentNodeIds()) {
                Node node = board.getNode(nodeId);
                node.getOwner().ifPresent(owner -> {
                    owner.addResource(tile.getResourceType(), 1);
                    log.add(String.format("  %s gains 1 %s from tile %d", owner.getName(), tile.getResourceType(), tile.getId()));
                });
            }
        }
    }

    /*
     * Settlement logic:
     * Tries to find an empty node that is far enough from other buildings 
     * and connected to the player's road network.
     */
    private void buildSettlementIfPossible(Player player, List<String> log) {
        if (!player.canAfford(SETTLEMENT_COST)) {
            return;
        }
        for (Node node : board.getNodes()) {
            if (!node.isClaimed() && !hasAdjacentSettlement(node) && canClaimNode(player, node)) {
                node.claim(player);
                player.spend(SETTLEMENT_COST);
                player.addSettlement(node.getId());
                log.add(String.format("  %s builds settlement on node %d", player.getName(), node.getId()));
                return;
            }
        }
    }

    // Distance Rule: No building can be placed directly next to another building.
    private boolean hasAdjacentSettlement(Node node) {
        for (int neighborId : node.getAdjacentNodeIds()) {
            if (board.getNode(neighborId).isClaimed()) {
                return true;
            }
        }
        return false;
    }

    // Road Rule: A settlement must touch one of your roads.
    private boolean canClaimNode(Player player, Node node) {
        for (int nodeId : player.getSettlementNodeIds()) {
            for (Path path : board.getPaths()) {
                if (path.isAdjacentToNode(node.getId()) && path.isAdjacentToNode(nodeId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Road logic:
     * Finds an empty path that touches an existing building or road owned by the player.
     * After building, it triggers the Longest Road calculation.
     */
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
                // Achievement Logic:
                int newLen = player.calculateLongestRoad(board);
                if (newLen > currentMaxRoadLength) {
                    // If someone else had the title, they lose the 2 VP bonus.
                    if (longestRoadHolder != null && !longestRoadHolder.equals(player)) {
                        longestRoadHolder.setHasLongestRoad(false);
                        log.add(String.format("  %s takes Longest Road from %s!", player.getName(), longestRoadHolder.getName()));
                    }
                    // Current player gets the title and 2 VP bonus.
                    player.setHasLongestRoad(true);
                    longestRoadHolder = player;
                    currentMaxRoadLength = newLen;
                    log.add(String.format("  %s now has Longest Road (Length: %d)", player.getName(), newLen));
                }
                return;
            }
        }
    }

    private boolean canClaimPath(Player player, Path path) {
        return path.canBuildRoad(board, player);
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
            if (!path.isClaimed() && path.canBuildRoad(board, player) && player.canAfford(ROAD_COST)) {
                actions.add(ActionDecision.road(path.getId()));
            }
        }
        return actions;
    }


}
