package catan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Board {
    private final Map<Integer, Tile> tilesById;
    private final Map<Integer, Node> nodesById;
    private final Map<Integer, Path> pathsById;

    public Board(List<Tile> tiles, List<Node> nodes, List<Path> paths) {
        this.tilesById = indexById(Objects.requireNonNull(tiles, "tiles"), "tile");
        this.nodesById = indexById(Objects.requireNonNull(nodes, "nodes"), "node");
        this.pathsById = indexById(Objects.requireNonNull(paths, "paths"), "path");
        validateAdjacency();
    }

    private static <T extends Identifiable> Map<Integer, T> indexById(List<T> items, String label) {
        Map<Integer, T> map = new HashMap<>();
        for (T item : items) {
            int id = item.getId();
            if (map.put(id, item) != null) {
                throw new IllegalArgumentException("Duplicate " + label + " id " + id);
            }
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException("Board must include at least one " + label + ".");
        }
        return map;
    }

    private void validateAdjacency() {
        for (Tile tile : tilesById.values()) {
            for (int nodeId : tile.getAdjacentNodeIds()) {
                if (!nodesById.containsKey(nodeId)) {
                    throw new IllegalArgumentException("Tile " + tile.getId() + " references missing node " + nodeId);
                }
            }
        }
        for (Node node : nodesById.values()) {
            for (int tileId : node.getAdjacentTileIds()) {
                if (!tilesById.containsKey(tileId)) {
                    throw new IllegalArgumentException("Node " + node.getId() + " references missing tile " + tileId);
                }
            }
            for (int pathId : node.getAdjacentPathIds()) {
                Path path = pathsById.get(pathId);
                if (path == null || !path.connectsNode(node.getId())) {
                    throw new IllegalArgumentException("Node " + node.getId() + " references missing path " + pathId);
                }
            }
        }
        for (Path path : pathsById.values()) {
            if (!nodesById.containsKey(path.getNodeAId()) || !nodesById.containsKey(path.getNodeBId())) {
                throw new IllegalArgumentException("Path " + path.getId() + " references missing node.");
            }
        }
    }

    public List<Tile> getTiles() {
        return Collections.unmodifiableList(new ArrayList<>(tilesById.values()));
    }

    public List<Node> getNodes() {
        return Collections.unmodifiableList(new ArrayList<>(nodesById.values()));
    }

    public List<Path> getPaths() {
        return Collections.unmodifiableList(new ArrayList<>(pathsById.values()));
    }

    public Node getNode(int nodeId) {
        Node node = nodesById.get(nodeId);
        if (node == null) {
            throw new IllegalArgumentException("Unknown node id: " + nodeId);
        }
        return node;
    }

    public Path getPath(int pathId) {
        Path path = pathsById.get(pathId);
        if (path == null) {
            throw new IllegalArgumentException("Unknown path id: " + pathId);
        }
        return path;
    }

    public List<Tile> tilesForRoll(int roll) {
        List<Tile> matches = new ArrayList<>();
        for (Tile tile : tilesById.values()) {
            if (tile.producesOnRoll(roll)) {
                matches.add(tile);
            }
        }
        return matches;
    }

    public List<Node> getAdjacentNodes(int nodeId) {
        Set<Integer> adjacentIds = new HashSet<>();
        Node node = getNode(nodeId);
        for (int pathId : node.getAdjacentPathIds()) {
            Path path = getPath(pathId);
            int otherId = path.getNodeAId() == nodeId ? path.getNodeBId() : path.getNodeAId();
            adjacentIds.add(otherId);
        }
        List<Node> neighbors = new ArrayList<>();
        for (int id : adjacentIds) {
            neighbors.add(getNode(id));
        }
        return neighbors;
    }

    public boolean hasAdjacentRoadOwnedBy(Player player, int nodeId) {
        for (int pathId : getNode(nodeId).getAdjacentPathIds()) {
            Path path = getPath(pathId);
            if (path.getOwner().isPresent() && path.getOwner().get().equals(player)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRoadConnectedToPlayer(Player player, int nodeAId, int nodeBId) {
        if (isNodeOwnedByPlayer(player, nodeAId) || isNodeOwnedByPlayer(player, nodeBId)) {
            return true;
        }
        return isAdjacentRoadOwnedBy(player, nodeAId) || isAdjacentRoadOwnedBy(player, nodeBId);
    }

    private boolean isAdjacentRoadOwnedBy(Player player, int nodeId) {
        for (int pathId : getNode(nodeId).getAdjacentPathIds()) {
            Path path = getPath(pathId);
            if (path.getOwner().isPresent() && path.getOwner().get().equals(player)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNodeOwnedByPlayer(Player player, int nodeId) {
        return getNode(nodeId).getOwner().map(player::equals).orElse(false);
    }
}
