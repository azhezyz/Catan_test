package catan;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/*
 * Represents a player in the game.
 * It tracks their name, resource hand, buildings, and victory points.
 */
public final class Player {
    private final String name;
    private final EnumMap<ResourceType, Integer> resources;
    private final Set<Integer> settlementNodeIds;
    private final Set<Integer> roadPathIds;
    private boolean hasLongestRoad = false;

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

    // Basic resource management
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

    // Setter for the title
    public void setHasLongestRoad(boolean hasIt) {
        this.hasLongestRoad = hasIt;
    }

    /*
     * Scoring Logic:
     * - 1 Point for every settlement owned.
     * - 2 Points for holding the Longest Road title.
     */
    public int getVictoryPoints() {
        int points = 0;
        // 1 point per Settlement
        points += settlementNodeIds.size();
        // 2 points if holding Longest Road
        if (hasLongestRoad) {
            points += 2;
        }
        return points;
    }

    /*
     * Longest Road Calculation:
     * This uses a "Depth First Search" (DFS). It starts from every road segment 
     * and tries to "walk" as far as possible to find the longest continuous string.
     */
    public int calculateLongestRoad(Board board) {
        if (roadPathIds.isEmpty()) return 0;
        int maxLen = 0;
        for (int pathId : roadPathIds) {
            Path startPath = board.getPath(pathId);
            // Walk from both ends of the starting road segment
            maxLen = Math.max(maxLen, 1 + walkRoad(board, startPath.getNodeAId(), new HashSet<>(Set.of(pathId))));
            maxLen = Math.max(maxLen, 1 + walkRoad(board, startPath.getNodeBId(), new HashSet<>(Set.of(pathId))));
        }
        return maxLen;
    }

    // Recursive helper that "walks" along connected paths
    private int walkRoad(Board board, int nodeId, Set<Integer> visited) {
        Node node = board.getNode(nodeId);
        // Path is broken if an opponent has a settlement here
        if (node.isClaimed() && !node.getOwner().get().equals(this)) {
            return 0;
        }

        int deepest = 0;
        for (Path p : board.getPaths()) {
            if (p.isAdjacentToNode(nodeId) && !visited.contains(p.getId())) {
                if (p.getOwner().isPresent() && p.getOwner().get().equals(this)) {
                    visited.add(p.getId());
                    int nextNode = (p.getNodeAId() == nodeId) ? p.getNodeBId() : p.getNodeAId();
                    deepest = Math.max(deepest, 1 + walkRoad(board, nextNode, visited));
                    visited.remove(p.getId()); // Backtrack
                }
            }
        }
        return deepest;
    }
}
