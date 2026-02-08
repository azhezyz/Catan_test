package catan;

import java.util.List;
import java.util.Set;

public final class Demonstrator {
    public static void main(String[] args) {
        Board board = buildBoard();
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        seedInitialState(board, alice, bob);

        GameEngine engine = new GameEngine(board, List.of(alice, bob), List.of(6, 8, 5, 6, 9, 5));
        SimulationReport report = engine.runSimulation(5);

        for (String line : report.getLogLines()) {
            System.out.println(line);
        }
        System.out.println(report.summarize());
    }

    private static Board buildBoard() {
        Tile tile1 = new Tile(1, ResourceType.WOOD, 6, Set.of(1, 2, 3));
        Tile tile2 = new Tile(2, ResourceType.BRICK, 8, Set.of(3, 4, 5));
        Tile tile3 = new Tile(3, ResourceType.SHEEP, 5, Set.of(5, 6, 1));

        Node node1 = new Node(1, Set.of(1, 3));
        Node node2 = new Node(2, Set.of(1));
        Node node3 = new Node(3, Set.of(1, 2));
        Node node4 = new Node(4, Set.of(2));
        Node node5 = new Node(5, Set.of(2, 3));
        Node node6 = new Node(6, Set.of(3));

        Path path1 = new Path(1, 1, 2);
        Path path2 = new Path(2, 2, 3);
        Path path3 = new Path(3, 3, 4);
        Path path4 = new Path(4, 4, 5);
        Path path5 = new Path(5, 5, 6);
        Path path6 = new Path(6, 6, 1);

        return new Board(List.of(tile1, tile2, tile3), List.of(node1, node2, node3, node4, node5, node6),
                List.of(path1, path2, path3, path4, path5, path6));
    }

    private static void seedInitialState(Board board, Player alice, Player bob) {
        claimSettlement(board, alice, 1);
        claimSettlement(board, bob, 4);
        claimRoad(board, alice, 1);
        claimRoad(board, bob, 4);

        alice.addResource(ResourceType.WOOD, 1);
        alice.addResource(ResourceType.BRICK, 1);
        alice.addResource(ResourceType.SHEEP, 1);
        alice.addResource(ResourceType.WHEAT, 1);

        bob.addResource(ResourceType.WOOD, 1);
        bob.addResource(ResourceType.BRICK, 1);
        bob.addResource(ResourceType.SHEEP, 1);
        bob.addResource(ResourceType.WHEAT, 1);
    }

    private static void claimSettlement(Board board, Player player, int nodeId) {
        Node node = board.getNode(nodeId);
        node.claim(player);
        player.addSettlement(nodeId);
    }

    private static void claimRoad(Board board, Player player, int pathId) {
        Path path = board.getPath(pathId);
        path.claim(player);
        player.addRoad(pathId);
    }
}
