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

    public Node(int id, Set<Integer> adjacentTileIds) {
        if (id <= 0) {
            throw new IllegalArgumentException("Node id must be positive.");
        }
        this.id = id;
        this.adjacentTileIds = new HashSet<>(Objects.requireNonNull(adjacentTileIds, "adjacentTileIds"));
        if (this.adjacentTileIds.isEmpty()) {
            throw new IllegalArgumentException("Node must have at least one adjacent tile.");
        }
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
    }
}
