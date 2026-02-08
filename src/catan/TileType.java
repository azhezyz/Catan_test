package catan;

import java.util.Optional;

public enum TileType {
    HILLS(ResourceType.BRICK),
    FOREST(ResourceType.LUMBER),
    MOUNTAINS(ResourceType.ORE),
    FIELDS(ResourceType.GRAIN),
    PASTURE(ResourceType.WOOL),
    DESERT(null);

    private final ResourceType resourceType;

    TileType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public Optional<ResourceType> getResourceType() {
        return Optional.ofNullable(resourceType);
    }
}
