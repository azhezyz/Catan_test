package catan;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Player {
    private final String name;
    private final EnumMap<ResourceType, Integer> resources;
    private final Set<Integer> settlementNodeIds;
    private final Set<Integer> roadPathIds;

    public Player(String name) {
        this.name = requireName(name);
        this.resources = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
        this.settlementNodeIds = new HashSet<>();
        this.roadPathIds = new HashSet<>();
    }

    private static String requireName(String name) {
        String trimmed = Objects.requireNonNull(name, "name").trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be blank.");
        }
        return trimmed;
    }

    public String getName() {
        return name;
    }

    public Map<ResourceType, Integer> getResources() {
        return Collections.unmodifiableMap(resources);
    }

    public int getResourceCount(ResourceType type) {
        return resources.getOrDefault(type, 0);
    }

    public void addResource(ResourceType type, int amount) {
        Objects.requireNonNull(type, "type");
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        resources.put(type, getResourceCount(type) + amount);
    }

    public boolean canAfford(Map<ResourceType, Integer> cost) {
        Objects.requireNonNull(cost, "cost");
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            if (getResourceCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void spend(Map<ResourceType, Integer> cost) {
        if (!canAfford(cost)) {
            throw new IllegalStateException("Insufficient resources to spend.");
        }
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            resources.put(entry.getKey(), getResourceCount(entry.getKey()) - entry.getValue());
        }
    }

    public void addSettlement(int nodeId) {
        if (!settlementNodeIds.add(nodeId)) {
            throw new IllegalStateException("Settlement already recorded for node " + nodeId);
        }
    }

    public void addRoad(int pathId) {
        if (!roadPathIds.add(pathId)) {
            throw new IllegalStateException("Road already recorded for path " + pathId);
        }
    }

    public Set<Integer> getSettlementNodeIds() {
        return Collections.unmodifiableSet(settlementNodeIds);
    }

    public Set<Integer> getRoadPathIds() {
        return Collections.unmodifiableSet(roadPathIds);
    }
}
