package catan;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Tile {
    private final int id;
    private final ResourceType resourceType;   // null for DESERT
    private final int numberToken;              // 0 for DESERT (no production)
    private final Set<Integer> adjacentNodeIds;

    public Tile(int id, ResourceType resourceType, int numberToken, Set<Integer> adjacentNodeIds) {
        if (id < 0) {
            throw new IllegalArgumentException("Tile id must be non-negative.");
        }
        if (resourceType != null && (numberToken < 2 || numberToken > 12)) {
            throw new IllegalArgumentException("Number token must be between 2 and 12 for resource tiles.");
        }
        if (resourceType == null && numberToken != 0) {
            throw new IllegalArgumentException("Desert tile must have number token 0.");
        }
        this.id = id;
        this.resourceType = resourceType;
        this.numberToken = numberToken;
        this.adjacentNodeIds = new HashSet<>(Objects.requireNonNull(adjacentNodeIds, "adjacentNodeIds"));
        if (this.adjacentNodeIds.isEmpty()) {
            throw new IllegalArgumentException("Tile must have adjacent nodes.");
        }
    }

    public int getId() {
        return id;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Optional<ResourceType> getResourceTypeOpt() {
        return Optional.ofNullable(resourceType);
    }

    public int getNumberToken() {
        return numberToken;
    }

    public Set<Integer> getAdjacentNodeIds() {
        return Collections.unmodifiableSet(adjacentNodeIds);
    }
}
