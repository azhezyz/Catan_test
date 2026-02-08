package catan;

import java.util.Objects;
import java.util.Optional;

public final class Path implements Identifiable {
    private final int id;
    private final int nodeAId;
    private final int nodeBId;
    private Player owner;

    public Path(int id, int nodeAId, int nodeBId) {
        if (id <= 0) {
            throw new IllegalArgumentException("Path id must be positive.");
        }
        if (nodeAId <= 0 || nodeBId <= 0 || nodeAId == nodeBId) {
            throw new IllegalArgumentException("Path must connect two distinct node ids.");
        }
        this.id = id;
        this.nodeAId = nodeAId;
        this.nodeBId = nodeBId;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getNodeAId() {
        return nodeAId;
    }

    public int getNodeBId() {
        return nodeBId;
    }

    public Optional<Player> getOwner() {
        return Optional.ofNullable(owner);
    }

    public boolean isClaimed() {
        return owner != null;
    }

    public boolean canBuildRoad(Board board, Player player) {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(player, "player");
        if (isClaimed()) {
            return false;
        }
        return board.isRoadConnectedToPlayer(player, nodeAId, nodeBId);
    }

    public void buildRoad(Board board, Player player) {
        if (!canBuildRoad(board, player)) {
            throw new IllegalStateException("Cannot build road on path " + id);
        }
        owner = player;
        player.addRoad(id);
    }

    public boolean connectsNode(int nodeId) {
        return nodeAId == nodeId || nodeBId == nodeId;
    }
}
