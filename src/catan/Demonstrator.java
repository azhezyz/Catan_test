package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Demonstrator {
    public static void main(String[] args) {
        GameConfig config = GameConfig.load(resolveConfigPath(args));
        Board board = buildBoard();
        List<Player> players = List.of(new Player(1), new Player(2), new Player(3), new Player(4));
        List<Agent> agents = buildAgents(players.size());
        seedInitialState(board, players);

        GameEngine engine = new GameEngine(board, players, agents, config.getDiceSequence());
        List<String> log = engine.runSimulation(config.getMaxRounds());
        for (String line : log) {
            System.out.println(line);
        }
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
        Tile tile1 = new Tile(1, TileType.FOREST, 6, Set.of(1, 2, 3, 4, 5, 6));
        Tile tile2 = new Tile(2, TileType.HILLS, 8, Set.of(4, 7, 8, 9, 10, 5));
        Tile tile3 = new Tile(3, TileType.PASTURE, 5, Set.of(6, 5, 10, 11, 12, 1));
        Tile tile4 = new Tile(4, TileType.FIELDS, 9, Set.of(2, 13, 14, 7, 4, 3));
        Tile tile5 = new Tile(5, TileType.MOUNTAINS, 4, Set.of(8, 14, 15, 11, 10, 9));
        Tile tile6 = new Tile(6, TileType.FOREST, 10, Set.of(12, 11, 15, 16, 17, 1));
        Tile tile7 = new Tile(7, TileType.DESERT, null, Set.of(16, 15, 14, 13, 2, 1));

        List<Path> paths = buildPaths();
        List<Node> nodes = buildNodes(paths);
        return new Board(List.of(tile1, tile2, tile3, tile4, tile5, tile6, tile7), nodes, paths);
    }

    private static List<Path> buildPaths() {
        List<Path> paths = new ArrayList<>();
        paths.add(new Path(1, 1, 2));
        paths.add(new Path(2, 2, 3));
        paths.add(new Path(3, 3, 4));
        paths.add(new Path(4, 4, 5));
        paths.add(new Path(5, 5, 6));
        paths.add(new Path(6, 6, 1));
        paths.add(new Path(7, 4, 7));
        paths.add(new Path(8, 7, 8));
        paths.add(new Path(9, 8, 9));
        paths.add(new Path(10, 9, 10));
        paths.add(new Path(11, 10, 5));
        paths.add(new Path(12, 6, 12));
        paths.add(new Path(13, 12, 11));
        paths.add(new Path(14, 11, 10));
        paths.add(new Path(15, 2, 13));
        paths.add(new Path(16, 13, 14));
        paths.add(new Path(17, 14, 7));
        paths.add(new Path(18, 8, 14));
        paths.add(new Path(19, 14, 15));
        paths.add(new Path(20, 15, 11));
        paths.add(new Path(21, 11, 16));
        paths.add(new Path(22, 16, 17));
        paths.add(new Path(23, 17, 1));
        paths.add(new Path(24, 15, 16));
        return paths;
    }

    private static List<Node> buildNodes(List<Path> paths) {
        List<Set<Integer>> adjacency = new ArrayList<>();
        for (int i = 0; i <= 17; i++) {
            adjacency.add(new java.util.HashSet<>());
        }
        for (Path path : paths) {
            adjacency.get(path.getNodeAId()).add(path.getId());
            adjacency.get(path.getNodeBId()).add(path.getId());
        }
        return List.of(
                new Node(1, Set.of(1, 3, 6, 7), adjacency.get(1)),
                new Node(2, Set.of(1, 4, 7), adjacency.get(2)),
                new Node(3, Set.of(1, 4), adjacency.get(3)),
                new Node(4, Set.of(1, 2, 4), adjacency.get(4)),
                new Node(5, Set.of(1, 2, 3), adjacency.get(5)),
                new Node(6, Set.of(1, 3), adjacency.get(6)),
                new Node(7, Set.of(2, 4), adjacency.get(7)),
                new Node(8, Set.of(2, 5), adjacency.get(8)),
                new Node(9, Set.of(2, 5), adjacency.get(9)),
                new Node(10, Set.of(2, 3, 5), adjacency.get(10)),
                new Node(11, Set.of(3, 5, 6), adjacency.get(11)),
                new Node(12, Set.of(3, 6), adjacency.get(12)),
                new Node(13, Set.of(4, 7), adjacency.get(13)),
                new Node(14, Set.of(4, 5, 7), adjacency.get(14)),
                new Node(15, Set.of(5, 6, 7), adjacency.get(15)),
                new Node(16, Set.of(6, 7), adjacency.get(16)),
                new Node(17, Set.of(6, 7), adjacency.get(17))
        );
    }

    private static void seedInitialState(Board board, List<Player> players) {
        int[] startingNodes = {1, 4, 8, 12};
        int[] startingPaths = {1, 3, 9, 13};
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Node node = board.getNode(startingNodes[i]);
            node.placeInitialSettlement(board, player);
            Path path = board.getPath(startingPaths[i]);
            path.buildRoad(board, player);
        }
    }
}
