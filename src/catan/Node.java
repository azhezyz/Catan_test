package catan;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Node {
    private final int id;
    private final Set<Integer> adjacentTileIds;
    private Player owner;
    private Building building;

    public Node(int id, Set<Integer> adjacentTileIds) {
        if (id <= 0) {
            throw new IllegalArgumentException("Node id must be positive.");
        }
        this.id = id;
        this.adjacentTileIds = new HashSet<>(Objects.requireNonNull(adjacentTileIds, "adjacentTileIds"));
        if (this.adjacentTileIds.isEmpty()) {
            throw new IllegalArgumentException("Node must have at least one adjacent tile.");
        }
        this.building = Building.empty();
    }

    /**
     * 判断该节点是否可以升级为城市：节点必须已被该玩家拥有（有定居点）。
     */
    public boolean canUpgradeToCity(Player player) {
        return owner != null && owner.equals(player);
    }

    /**
     * 判断该节点是否可以建造定居点：节点未被占据，且玩家在相邻路径上有道路。
     */
    public boolean canBuildSettlement(Board board, Player player) {
        if (owner != null) {
            return false;
        }
        // 玩家必须有一条道路连接到该节点
        for (Path path : board.getPaths()) {
            if (path.isAdjacentToNode(id) && path.isClaimed()
                    && path.getOwner().isPresent() && path.getOwner().get().equals(player)) {
                return true;
            }
        }
        return false;
    }



    public int getId() {
        return id;
    }

    public Set<Integer> getAdjacentTileIds() {
        return Collections.unmodifiableSet(adjacentTileIds);
    }

    public Optional<Player> getOwner() {
        return Optional.ofNullable(owner);
    }

    public boolean isClaimed() {
        return owner != null;
    }

    public void claim(Player player) {
        Objects.requireNonNull(player, "player");
        if (owner != null) {
            throw new IllegalStateException("Node already claimed.");
        }
        owner = player;
        building = Building.settlement(player);
    }

    public Building getBuilding() {
        return building;
    }

    public void upgradeToCity(Player player) {
        Objects.requireNonNull(player, "player");
        if (owner == null || !owner.equals(player)) {
            throw new IllegalStateException("Node is not owned by the player.");
        }
        if (building.getType() == BuildingType.CITY) {
            throw new IllegalStateException("Node is already a city.");
        }
        building = Building.city(player);
    }
}
