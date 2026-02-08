package catan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Board {
    private final Map<Integer, Tile> tilesById;
    private final Map<Integer, Node> nodesById;
    private final Map<Integer, Path> pathsById;

    public Board(List<Tile> tiles, List<Node> nodes, List<Path> paths) {
        Objects.requireNonNull(tiles, "tiles");
        Objects.requireNonNull(nodes, "nodes");
        Objects.requireNonNull(paths, "paths");
        this.tilesById = indexById(tiles, "tile");
        this.nodesById = indexById(nodes, "node");
        this.pathsById = indexById(paths, "path");
        validateAdjacency();
    }

    private static <T> Map<Integer, T> indexById(List<T> items, String label) {
        Map<Integer, T> map = new HashMap<>();
        for (T item : items) {
            int id;
            if (item instanceof Tile tile) {
                id = tile.getId();
            } else if (item instanceof Node node) {
                id = node.getId();
            } else if (item instanceof Path path) {
                id = path.getId();
            } else {
                throw new IllegalArgumentException("Unsupported " + label + " type: " + item.getClass());
            }
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
            if (tile.getNumberToken() == roll) {
                matches.add(tile);
            }
        }
        return matches;
    }
}
