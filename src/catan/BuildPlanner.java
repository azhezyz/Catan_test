package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * BuildPlanner is a utility class that helps the GameEngine and Players 
 * decide what can be built based on resources and board rules.
 */
public final class BuildPlanner {
    // Standard Catan resource costs for each building type
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
    private static final Map<ResourceType, Integer> CITY_COST = Map.of(
            ResourceType.WHEAT, 2,
            ResourceType.ORE, 3
    );

    // Private constructor to prevent creating instances of this utility class
    private BuildPlanner() {
    }

    /*
     * Scans the entire board to find every legal move a player can afford.
     * It checks for city upgrades, then settlements, then roads.
     */
    public static List<ActionDecision> availableActions(Board board, Player player) {
        List<ActionDecision> actions = new ArrayList<>();
        
        // Check every node to see if a city can be built (upgraded)
        for (Node node : board.getNodes()) {
            if (node.canUpgradeToCity(player) && player.canAfford(CITY_COST)) {
                actions.add(ActionDecision.city(node.getId()));
            }
        }
        
        // Check every node to see if a new settlement can be placed
        for (Node node : board.getNodes()) {
            if (node.canBuildSettlement(board, player) && player.canAfford(SETTLEMENT_COST)) {
                actions.add(ActionDecision.settlement(node.getId()));
            }
        }
        
        // Check every path to see if a road can be placed
        for (Path path : board.getPaths()) {
            if (path.canBuildRoad(board, player) && player.canAfford(ROAD_COST)) {
                actions.add(ActionDecision.road(path.getId()));
            }
        }
        return actions;
    }

    /*
     * If multiple actions are possible, this returns the first one found.
     * If nothing can be built, it returns a "NONE" decision.
     */
    public static ActionDecision forcedDecision(Board board, Player player) {
        List<ActionDecision> actions = availableActions(board, player);
        if (actions.isEmpty()) {
            return ActionDecision.none();
        }
        return actions.get(0);
    }

    // Standard getters to share resource costs with other classes
    public static Map<ResourceType, Integer> settlementCost() {
        return SETTLEMENT_COST;
    }

    public static Map<ResourceType, Integer> roadCost() {
        return ROAD_COST;
    }

    public static Map<ResourceType, Integer> cityCost() {
        return CITY_COST;
    }
}