package catan;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Player implements Identifiable {
    private final int id;
    private final EnumMap<ResourceType, Integer> resources;
    private final Set<Integer> roadPathIds;
    private final Set<Integer> settlementNodeIds;
    private final Set<Integer> cityNodeIds;
    private int victoryPoints;

    public Player(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Player id must be positive.");
        }
        this.id = id;
        this.resources = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
        this.roadPathIds = new HashSet<>();
        this.settlementNodeIds = new HashSet<>();
        this.cityNodeIds = new HashSet<>();
        this.victoryPoints = 0;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getLabel() {
        return "P" + id;
    }

    public Map<ResourceType, Integer> getResources() {
        return Collections.unmodifiableMap(resources);
    }

    public int getResourceCount() {
        int count = 0;
        for (int value : resources.values()) {
            count += value;
        }
        return count;
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

    public void addRoad(int pathId) {
        if (!roadPathIds.add(pathId)) {
            throw new IllegalStateException("Road already recorded for path " + pathId);
        }
    }

    public void addSettlement(int nodeId) {
        if (!settlementNodeIds.add(nodeId)) {
            throw new IllegalStateException("Settlement already recorded for node " + nodeId);
        }
    }

    public void removeSettlement(int nodeId) {
        if (!settlementNodeIds.remove(nodeId)) {
            throw new IllegalStateException("Settlement missing for node " + nodeId);
        }
    }

    public void addCity(int nodeId) {
        if (!cityNodeIds.add(nodeId)) {
            throw new IllegalStateException("City already recorded for node " + nodeId);
        }
    }

    public Set<Integer> getRoadPathIds() {
        return Collections.unmodifiableSet(roadPathIds);
    }

    public Set<Integer> getSettlementNodeIds() {
        return Collections.unmodifiableSet(settlementNodeIds);
    }

    public Set<Integer> getCityNodeIds() {
        return Collections.unmodifiableSet(cityNodeIds);
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void addVictoryPoints(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Victory point increment must be positive.");
        }
        victoryPoints += amount;
    }
}
