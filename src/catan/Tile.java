package catan;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Tile implements Identifiable {
    private final int id;
    private final TileType type;
    private final Integer numberToken;
    private final Set<Integer> adjacentNodeIds;

    public Tile(int id, TileType type, Integer numberToken, Set<Integer> adjacentNodeIds) {
        if (id < 0) {
            throw new IllegalArgumentException("Tile id must be non-negative.");
        }
        this.id = id;
        this.type = Objects.requireNonNull(type, "type");
        if (type == TileType.DESERT && numberToken != null) {
            throw new IllegalArgumentException("Desert tile cannot have a number token.");
        }
        if (type != TileType.DESERT && (numberToken == null || numberToken < 2 || numberToken > 12)) {
            throw new IllegalArgumentException("Non-desert tiles must have a number token between 2 and 12.");
        }
        this.numberToken = numberToken;
        this.adjacentNodeIds = new HashSet<>(Objects.requireNonNull(adjacentNodeIds, "adjacentNodeIds"));
        if (this.adjacentNodeIds.isEmpty()) {
            throw new IllegalArgumentException("Tile must have adjacent nodes.");
        }
    }

    @Override
    public int getId() {
        return id;
    }

    public TileType getType() {
        return type;
    }

    public Optional<Integer> getNumberToken() {
        return Optional.ofNullable(numberToken);
    }

    public Set<Integer> getAdjacentNodeIds() {
        return Collections.unmodifiableSet(adjacentNodeIds);
    }

    public boolean producesOnRoll(int roll) {
        return numberToken != null && numberToken == roll;
    }

    public Optional<ResourceType> getResourceType() {
        return type.getResourceType();
    }
}
