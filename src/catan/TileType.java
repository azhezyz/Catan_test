package catan;

import java.util.Optional;

public enum TileType {
    HILLS(ResourceType.BRICK),
    FOREST(ResourceType.WOOD),
    MOUNTAINS(ResourceType.ORE),
    FIELDS(ResourceType.WHEAT),
    PASTURE(ResourceType.SHEEP),
    DESERT(null);

    private final ResourceType resourceType;

    TileType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public Optional<ResourceType> getResourceType() {
        return Optional.ofNullable(resourceType);
    }
}
