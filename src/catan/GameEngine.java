package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GameEngine {
    private final Board board;
    private final List<Player> players;
    private final List<Agent> agents;
    private final List<Integer> diceSequence;

    public GameEngine(Board board, List<Player> players, List<Agent> agents, List<Integer> diceSequence) {
        this.board = Objects.requireNonNull(board, "board");
        this.players = List.copyOf(Objects.requireNonNull(players, "players"));
        this.agents = List.copyOf(Objects.requireNonNull(agents, "agents"));
        if (this.players.size() != this.agents.size()) {
            throw new IllegalArgumentException("Each player must have an agent.");
        }
        if (this.players.isEmpty()) {
            throw new IllegalArgumentException("At least one player is required.");
        }
        this.diceSequence = List.copyOf(Objects.requireNonNull(diceSequence, "diceSequence"));
        if (this.diceSequence.isEmpty()) {
            throw new IllegalArgumentException("Dice sequence cannot be empty.");
        }
    }

    public List<String> runSimulation(int rounds) {
        if (rounds <= 0 || rounds > 8192) {
            throw new IllegalArgumentException("Rounds must be between 1 and 8192.");
        }
        List<String> log = new ArrayList<>();
        int rollIndex = 0;
        boolean victoryReached = false;
        for (int round = 1; round <= rounds && !victoryReached; round++) {
            for (int index = 0; index < players.size() && !victoryReached; index++) {
                Player player = players.get(index);
                int roll = diceSequence.get(rollIndex % diceSequence.size());
                rollIndex++;
                log.add(String.format("[%d] / %s : Rolled %d", round, player.getLabel(), roll));
                distributeResources(roll);
                GameState state = new GameState(board);
                ActionDecision decision = agents.get(index).decideBuild(state, player);
                if (player.getResourceCount() > 7 && decision.getAction() == BuildAction.NONE) {
                    decision = BuildPlanner.forcedDecision(board, player);
                }
                logBuildAction(round, player, decision, log);
                victoryReached = player.getVictoryPoints() >= 10;
            }
            log.add(formatVictoryPoints(round));
        }
        return log;
    }

    private void distributeResources(int roll) {
        if (roll == 7) {
            return;
        }
        for (Tile tile : board.tilesForRoll(roll)) {
            tile.getResourceType().ifPresent(resource -> {
                for (int nodeId : tile.getAdjacentNodeIds()) {
                    Node node = board.getNode(nodeId);
                    node.getOwner().ifPresent(owner -> {
                        int amount = node.getBuilding().getType() == BuildingType.CITY ? 2 : 1;
                        owner.addResource(resource, amount);
                    });
                }
            });
        }
    }

    private void logBuildAction(int round, Player player, ActionDecision decision, List<String> log) {
        switch (decision.getAction()) {
            case ROAD -> buildRoad(round, player, decision.getTargetId(), log);
            case SETTLEMENT -> buildSettlement(round, player, decision.getTargetId(), log);
            case CITY -> buildCity(round, player, decision.getTargetId(), log);
            case NONE -> log.add(String.format("[%d] / %s : No build action", round, player.getLabel()));
        }
    }

    private void buildRoad(int round, Player player, int pathId, List<String> log) {
        Path path = board.getPath(pathId);
        if (!player.canAfford(BuildPlanner.roadCost()) || !path.canBuildRoad(board, player)) {
            log.add(String.format("[%d] / %s : No build action", round, player.getLabel()));
            return;
        }
        player.spend(BuildPlanner.roadCost());
        path.buildRoad(board, player);
        log.add(String.format("[%d] / %s : Built road between Node %d and Node %d", round, player.getLabel(), path.getNodeAId(), path.getNodeBId()));
    }

    private void buildSettlement(int round, Player player, int nodeId, List<String> log) {
        Node node = board.getNode(nodeId);
        if (!player.canAfford(BuildPlanner.settlementCost()) || !node.canBuildSettlement(board, player)) {
            log.add(String.format("[%d] / %s : No build action", round, player.getLabel()));
            return;
        }
        player.spend(BuildPlanner.settlementCost());
        node.buildSettlement(board, player);
        log.add(String.format("[%d] / %s : Built settlement at Node %d", round, player.getLabel(), node.getId()));
    }

    private void buildCity(int round, Player player, int nodeId, List<String> log) {
        Node node = board.getNode(nodeId);
        if (!player.canAfford(BuildPlanner.cityCost()) || !node.canUpgradeToCity(player)) {
            log.add(String.format("[%d] / %s : No build action", round, player.getLabel()));
            return;
        }
        player.spend(BuildPlanner.cityCost());
        node.buildCity(player);
        log.add(String.format("[%d] / %s : Built city at Node %d", round, player.getLabel(), node.getId()));
    }

    private String formatVictoryPoints(int round) {
        StringBuilder builder = new StringBuilder();
        builder.append("End of round ").append(round).append(" : ");
        for (int index = 0; index < players.size(); index++) {
            Player player = players.get(index);
            if (index > 0) {
                builder.append(" ");
            }
            builder.append(player.getLabel()).append("=").append(player.getVictoryPoints());
        }
        return builder.toString();
    }
}
