package catan;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class DeterministicAgent implements Agent {
    private final Random random;

    public DeterministicAgent(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public ActionDecision decideBuild(GameState state, Player player) {
        List<ActionDecision> candidates = BuildPlanner.availableActions(state.getBoard(), player);
        if (candidates.isEmpty()) {
            return ActionDecision.none();
        }
        Collections.shuffle(candidates, random);
        return candidates.get(0);
    }
}
