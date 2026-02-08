package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BuildPlanner {
    private static final Map<ResourceType, Integer> SETTLEMENT_COST = Map.of(
            ResourceType.LUMBER, 1,
            ResourceType.BRICK, 1,
            ResourceType.WOOL, 1,
            ResourceType.GRAIN, 1
    );
    private static final Map<ResourceType, Integer> ROAD_COST = Map.of(
            ResourceType.LUMBER, 1,
            ResourceType.BRICK, 1
    );
    private static final Map<ResourceType, Integer> CITY_COST = Map.of(
            ResourceType.GRAIN, 2,
            ResourceType.ORE, 3
    );

    private BuildPlanner() {
    }

    public static List<ActionDecision> availableActions(Board board, Player player) {
        List<ActionDecision> actions = new ArrayList<>();
        for (Node node : board.getNodes()) {
            if (node.canUpgradeToCity(player) && player.canAfford(CITY_COST)) {
                actions.add(ActionDecision.city(node.getId()));
            }
        }
        for (Node node : board.getNodes()) {
            if (node.canBuildSettlement(board, player) && player.canAfford(SETTLEMENT_COST)) {
                actions.add(ActionDecision.settlement(node.getId()));
            }
        }
        for (Path path : board.getPaths()) {
            if (path.canBuildRoad(board, player) && player.canAfford(ROAD_COST)) {
                actions.add(ActionDecision.road(path.getId()));
            }
        }
        return actions;
    }

    public static ActionDecision forcedDecision(Board board, Player player) {
        List<ActionDecision> actions = availableActions(board, player);
        if (actions.isEmpty()) {
            return ActionDecision.none();
        }
        return actions.get(0);
    }

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
