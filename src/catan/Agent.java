package catan;

public interface Agent {
    ActionDecision decideBuild(GameState state, Player player);
}
