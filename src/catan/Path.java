package catan;

import java.util.Objects;
import java.util.Optional;

public final class Path {
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

    public void claim(Player player) {
        Objects.requireNonNull(player, "player");
        if (owner != null) {
            throw new IllegalStateException("Path already claimed.");
        }
        owner = player;
    }

    public boolean isAdjacentToNode(int nodeId) {
        return nodeAId == nodeId || nodeBId == nodeId;
    }
}
