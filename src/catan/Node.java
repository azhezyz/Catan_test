package catan;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Node {
    private final int id;
    private final Set<Integer> adjacentTileIds;
    private final Set<Integer> adjacentNodeIds;
    private Player owner;
    private Building building;

    public Node(int id, Set<Integer> adjacentTileIds, Set<Integer> adjacentNodeIds) {
        if (id < 0) {
            throw new IllegalArgumentException("Node id must be non-negative.");
        }
        this.id = id;
        this.adjacentTileIds = new HashSet<>(Objects.requireNonNull(adjacentTileIds, "adjacentTileIds"));
        if (this.adjacentTileIds.isEmpty()) {
            throw new IllegalArgumentException("Node must have at least one adjacent tile.");
        }
        this.adjacentNodeIds = new HashSet<>(Objects.requireNonNull(adjacentNodeIds, "adjacentNodeIds"));
        this.building = Building.empty();
    }

    /**
     * 获取所有相邻节点 ID（通过路径直接相连的节点）。
     */
    public Set<Integer> getAdjacentNodeIds() {
        return Collections.unmodifiableSet(adjacentNodeIds);
    }

    /**
     * 判断该节点是否可以升级为城市：节点必须已被该玩家拥有（有定居点）。
     */
    public boolean canUpgradeToCity(Player player) {
        return owner != null && owner.equals(player)
                && building.getType() == BuildingType.SETTLEMENT;
    }

    /**
     * 判断该节点是否可以建造定居点：
     * 1. 节点未被占据
     * 2. 相邻节点没有任何定居点/城市（距离规则）
     * 3. 玩家在相邻路径上有道路
     */
    public boolean canBuildSettlement(Board board, Player player) {
        if (owner != null) {
            return false;
        }
        // 距离规则：所有相邻节点必须为空
        for (int neighborId : adjacentNodeIds) {
            Node neighbor = board.getNode(neighborId);
            if (neighbor.isClaimed()) {
                return false;
            }
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
