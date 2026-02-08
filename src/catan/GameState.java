package catan;

public final class GameState {
    private final Board board;

    public GameState(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }
}
