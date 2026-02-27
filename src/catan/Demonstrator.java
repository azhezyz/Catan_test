package catan;

import java.util.List;

public final class Demonstrator {
    private Demonstrator() {
    }

    public static void main(String[] args) {
        Board board = StandardGameSetup.buildFullBoard();
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        Player charlie = new Player("Charlie");
        Player diana = new Player("Diana");

        StandardGameSetup.seedInitialState(board, alice, bob, charlie, diana);

        GameEngine engine = new GameEngine(board, List.of(alice, bob, charlie, diana));
        SimulationReport report = engine.runSimulation(100);

        for (String line : report.getLogLines()) {
            System.out.println(line);
        }
        System.out.println(report.summarize());
    }
}
