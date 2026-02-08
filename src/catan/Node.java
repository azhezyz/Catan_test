package catan;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Node implements Identifiable {
    private final int id;
    private final Set<Integer> adjacentTileIds;
    private final Set<Integer> adjacentPathIds;
    private Building building;

    public Node(int id, Set<Integer> adjacentTileIds, Set<Integer> adjacentPathIds) {
        if (id < 0) {
            throw new IllegalArgumentException("Node id must be non-negative.");
        }
        this.id = id;
        this.adjacentTileIds = new HashSet<>(Objects.requireNonNull(adjacentTileIds, "adjacentTileIds"));
        this.adjacentPathIds = new HashSet<>(Objects.requireNonNull(adjacentPathIds, "adjacentPathIds"));
        if (this.adjacentTileIds.isEmpty()) {
            throw new IllegalArgumentException("Node must have at least one adjacent tile.");
        }
        if (this.adjacentPathIds.isEmpty()) {
            throw new IllegalArgumentException("Node must have at least one adjacent path.");
        }
        this.building = Building.empty();
    }

    @Override
    public int getId() {
        return id;
    }

    public Set<Integer> getAdjacentTileIds() {
        return Collections.unmodifiableSet(adjacentTileIds);
    }

    public Set<Integer> getAdjacentPathIds() {
        return Collections.unmodifiableSet(adjacentPathIds);
    }

    public Building getBuilding() {
        return building;
    }

    public boolean isOccupied() {
        return building.isOccupied();
    }

    public Optional<Player> getOwner() {
        return building.getOwner();
    }

    public boolean canBuildSettlement(Board board, Player player) {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(player, "player");
        if (!canPlaceSettlement(board)) {
            return false;
        }
        return board.hasAdjacentRoadOwnedBy(player, id);
    }

    public boolean canPlaceInitialSettlement(Board board) {
        Objects.requireNonNull(board, "board");
        return canPlaceSettlement(board);
    }

    private boolean canPlaceSettlement(Board board) {
        if (isOccupied()) {
            return false;
        }
        for (Node neighbor : board.getAdjacentNodes(id)) {
            if (neighbor.isOccupied()) {
                return false;
            }
        }
        return true;
    }

    public void placeInitialSettlement(Board board, Player player) {
        if (!canPlaceInitialSettlement(board)) {
            throw new IllegalStateException("Cannot place initial settlement on node " + id);
        }
        building = Building.settlement(player);
        player.addSettlement(id);
        player.addVictoryPoints(1);
    }

    public void buildSettlement(Board board, Player player) {
        if (!canBuildSettlement(board, player)) {
            throw new IllegalStateException("Cannot build settlement on node " + id);
        }
        building = Building.settlement(player);
        player.addSettlement(id);
        player.addVictoryPoints(1);
    }

    public boolean canUpgradeToCity(Player player) {
        Objects.requireNonNull(player, "player");
        return building.getType() == BuildingType.SETTLEMENT && building.isOwnedBy(player);
    }

    public void buildCity(Player player) {
        if (!canUpgradeToCity(player)) {
            throw new IllegalStateException("Cannot build city on node " + id);
        }
        building = Building.city(player);
        player.removeSettlement(id);
        player.addCity(id);
        player.addVictoryPoints(1);
    }
}
