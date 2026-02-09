package catan;

import java.util.Objects;
import java.util.Optional;

/*
 * Represents a "Path" (an edge) between two nodes.
 * Paths are where roads are built.
 */
public final class Path {
    private final int id;
    private final int nodeAId; // First end of the road
    private final int nodeBId; // Second end of the road
    private Player owner;

    public Path(int id, int nodeAId, int nodeBId) {
        if (id < 0) {
            throw new IllegalArgumentException("Path id must be positive.");
        }
        if (nodeAId < 0 || nodeBId < 0 || nodeAId == nodeBId) {
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

    // Checks if this path touches a specific node ID.
    public boolean isAdjacentToNode(int nodeId) {
        return nodeAId == nodeId || nodeBId == nodeId;
    }

    /*
     * Road Building Rules:
     * 1. The path must be empty.
     * 2. It must touch a building owned by the player OR another road owned by the player.
     */
    public boolean canBuildRoad(Board board, Player player) {
        if (owner != null) {
            return false;
        }
        // Touch building check
        Node nodeA = board.getNode(nodeAId);
        Node nodeB = board.getNode(nodeBId);
        if ((nodeA.getOwner().isPresent() && nodeA.getOwner().get().equals(player))
                || (nodeB.getOwner().isPresent() && nodeB.getOwner().get().equals(player))) {
            return true;
        }
        // Touch existing road check
        for (Path other : board.getPaths()) {
            if (other == this || !other.isClaimed()) {
                continue;
            }
            if (other.getOwner().isPresent() && other.getOwner().get().equals(player)) {
                if (other.isAdjacentToNode(nodeAId) || other.isAdjacentToNode(nodeBId)) {
                    return true;
                }
            }
        }
        return false;
    }

}
