package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Demonstrator {
    public static void main(String[] args) {
        GameConfig config = GameConfig.load(resolveConfigPath(args));
        Board board = buildBoard();

        System.out.println("=== Resource-Only Simulation ===");
        List<Player> resourcePlayers = createPlayers();
        seedInitialSettlements(board, resourcePlayers);
        ResourceSimulationEngine resourceEngine = new ResourceSimulationEngine(board, resourcePlayers, config.getDiceSequence());
        printLog(resourceEngine.runResourceOnly(5));

        System.out.println("=== Build Simulation (Abundant Resources) ===");
        Board buildBoard = buildBoard();
        List<Player> buildPlayers = createPlayers();
        seedInitialSettlementsAndRoads(buildBoard, buildPlayers);
        grantAbundantResources(buildPlayers);
        GameEngine engine = new GameEngine(buildBoard, buildPlayers, buildAgents(buildPlayers.size()), config.getDiceSequence());
        printLog(engine.runSimulation(5));
    }

    private static java.nio.file.Path resolveConfigPath(String[] args) {
        if (args != null && args.length > 0) {
            return java.nio.file.Path.of(args[0]);
        }
        return java.nio.file.Path.of("config.properties");
    }

    private static List<Agent> buildAgents(int count) {
        List<Agent> agents = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            agents.add(new DeterministicAgent(100L + i));
        }
        return agents;
    }

    private static Board buildBoard() {
        Map<Integer, Set<Integer>> nodeTiles = buildNodeAdjacency();
        List<Tile> tiles = buildTiles(nodeTiles);
        List<Path> paths = buildPaths();
        List<Node> nodes = buildNodes(paths, nodeTiles);
        return new Board(tiles, nodes, paths);
    }

    private static List<Path> buildPaths() {
        List<Path> paths = new ArrayList<>();
        // Simple ring of 54 nodes (0..53). Each path connects node i to i+1 (mod 54).
        for (int i = 0; i < 54; i++) {
            int next = (i + 1) % 54;
            paths.add(new Path(i, i, next));
        }
        return paths;
    }

    private static List<Node> buildNodes(List<Path> paths, Map<Integer, Set<Integer>> nodeTiles) {
        List<Set<Integer>> adjacency = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            adjacency.add(new java.util.HashSet<>());
        }
        for (Path path : paths) {
            adjacency.get(path.getNodeAId()).add(path.getId());
            adjacency.get(path.getNodeBId()).add(path.getId());
        }
        List<Node> nodes = new ArrayList<>();
        for (int nodeId = 0; nodeId < 54; nodeId++) {
            nodes.add(new Node(nodeId, nodeTiles.get(nodeId), adjacency.get(nodeId)));
        }
        return nodes;
    }

    private static List<Player> createPlayers() {
        return List.of(new Player(1), new Player(2), new Player(3), new Player(4));
    }

    private static void seedInitialSettlements(Board board, List<Player> players) {
        // Two starting settlements per player, spaced to satisfy the distance rule.
        int[][] startingNodes = {
                {0, 6},
                {12, 18},
                {24, 30},
                {36, 42}
        };
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            for (int nodeId : startingNodes[i]) {
                board.getNode(nodeId).placeInitialSettlement(board, player);
            }
        }
    }

    private static void seedInitialSettlementsAndRoads(Board board, List<Player> players) {
        seedInitialSettlements(board, players);
        int[] startingPaths = {0, 12, 24, 36};
        for (int i = 0; i < players.size(); i++) {
            Path path = board.getPath(startingPaths[i]);
            path.buildRoad(board, players.get(i));
        }
    }

    private static void grantAbundantResources(List<Player> players) {
        for (Player player : players) {
            player.addResource(ResourceType.WOOD, 10);
            player.addResource(ResourceType.BRICK, 10);
            player.addResource(ResourceType.SHEEP, 10);
            player.addResource(ResourceType.WHEAT, 10);
            player.addResource(ResourceType.ORE, 10);
        }
    }

    private static void printLog(List<String> log) {
        for (String line : log) {
            System.out.println(line);
        }
    }

    private static List<Tile> buildTiles(Map<Integer, Set<Integer>> nodeTiles) {
        // Tile IDs: 0..18 with the required types and number tokens.
        Map<Integer, TileType> types = Map.ofEntries(
                Map.entry(0, TileType.MOUNTAINS),
                Map.entry(1, TileType.FIELDS),
                Map.entry(2, TileType.HILLS),
                Map.entry(3, TileType.MOUNTAINS),
                Map.entry(4, TileType.PASTURE),
                Map.entry(5, TileType.PASTURE),
                Map.entry(6, TileType.PASTURE),
                Map.entry(7, TileType.FIELDS),
                Map.entry(8, TileType.MOUNTAINS),
                Map.entry(9, TileType.FOREST),
                Map.entry(10, TileType.MOUNTAINS),
                Map.entry(11, TileType.FIELDS),
                Map.entry(12, TileType.FOREST),
                Map.entry(13, TileType.HILLS),
                Map.entry(14, TileType.HILLS),
                Map.entry(15, TileType.FIELDS),
                Map.entry(16, TileType.DESERT),
                Map.entry(17, TileType.FOREST),
                Map.entry(18, TileType.FOREST)
        );
        Map<Integer, Integer> numbers = Map.ofEntries(
                Map.entry(0, 3),
                Map.entry(1, 11),
                Map.entry(2, 8),
                Map.entry(3, 3),
                Map.entry(4, 11),
                Map.entry(5, 5),
                Map.entry(6, 12),
                Map.entry(7, 3),
                Map.entry(8, 6),
                Map.entry(9, 4),
                Map.entry(10, 6),
                Map.entry(11, 9),
                Map.entry(12, 5),
                Map.entry(13, 9),
                Map.entry(14, 8),
                Map.entry(15, 4),
                Map.entry(17, 2),
                Map.entry(18, 10)
        );
        List<Tile> tiles = new ArrayList<>();
        for (int tileId = 0; tileId <= 18; tileId++) {
            Set<Integer> adjacentNodes = collectNodesForTile(nodeTiles, tileId);
            Integer number = numbers.get(tileId);
            tiles.add(new Tile(tileId, types.get(tileId), number, adjacentNodes));
        }
        return tiles;
    }

    private static Set<Integer> collectNodesForTile(Map<Integer, Set<Integer>> nodeTiles, int tileId) {
        Set<Integer> nodes = new java.util.HashSet<>();
        for (Map.Entry<Integer, Set<Integer>> entry : nodeTiles.entrySet()) {
            if (entry.getValue().contains(tileId)) {
                nodes.add(entry.getKey());
            }
        }
        return nodes;
    }

    private static Map<Integer, Set<Integer>> buildNodeAdjacency() {
        // Node IDs: 0..53 with the provided adjacent tile IDs.
        return Map.ofEntries(
                Map.entry(0, Set.of(13)),
                Map.entry(1, Set.of(13, 14)),
                Map.entry(2, Set.of(14)),
                Map.entry(3, Set.of(14, 15)),
                Map.entry(4, Set.of(15)),
                Map.entry(5, Set.of(15, 16)),
                Map.entry(6, Set.of(16)),
                Map.entry(7, Set.of(16, 17)),
                Map.entry(8, Set.of(17)),
                Map.entry(9, Set.of(17, 18)),
                Map.entry(10, Set.of(18)),
                Map.entry(11, Set.of(12, 13)),
                Map.entry(12, Set.of(12)),
                Map.entry(13, Set.of(11, 12, 13)),
                Map.entry(14, Set.of(11)),
                Map.entry(15, Set.of(11, 14)),
                Map.entry(16, Set.of(14)),
                Map.entry(17, Set.of(14, 15)),
                Map.entry(18, Set.of(15)),
                Map.entry(19, Set.of(15, 16)),
                Map.entry(20, Set.of(16)),
                Map.entry(21, Set.of(16, 17)),
                Map.entry(22, Set.of(17)),
                Map.entry(23, Set.of(17, 18)),
                Map.entry(24, Set.of(18)),
                // Center tile 0 is adjacent to the inner-ring nodes for this demo layout.
                Map.entry(25, Set.of(0, 10, 11)),
                Map.entry(26, Set.of(0, 10)),
                Map.entry(27, Set.of(9, 10, 11)),
                Map.entry(28, Set.of(9)),
                Map.entry(29, Set.of(0, 9, 12)),
                Map.entry(30, Set.of(12)),
                Map.entry(31, Set.of(0, 12, 13)),
                Map.entry(32, Set.of(13)),
                Map.entry(33, Set.of(0, 13, 8)),
                Map.entry(34, Set.of(8)),
                Map.entry(35, Set.of(0, 8, 14)),
                Map.entry(36, Set.of(14)),
                Map.entry(37, Set.of(14, 5)),
                Map.entry(38, Set.of(5)),
                Map.entry(39, Set.of(5, 15)),
                Map.entry(40, Set.of(15)),
                Map.entry(41, Set.of(15, 6)),
                Map.entry(42, Set.of(6)),
                Map.entry(43, Set.of(6, 17)),
                Map.entry(44, Set.of(17)),
                Map.entry(45, Set.of(17, 7)),
                Map.entry(46, Set.of(7)),
                Map.entry(47, Set.of(7, 18)),
                Map.entry(48, Set.of(18)),
                Map.entry(49, Set.of(18, 16)),
                Map.entry(50, Set.of(16)),
                Map.entry(51, Set.of(16, 1)),
                Map.entry(52, Set.of(1)),
                Map.entry(53, Set.of(1, 2))
        );
    }
}
