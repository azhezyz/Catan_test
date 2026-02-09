package catan;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
 * SimulationReport packages all the results of the game into a readable format.
 * It contains the round-by-round logs and a final summary.
 */
public final class SimulationReport {
    private final List<String> logLines;
    private final List<Player> players;

    public SimulationReport(List<String> logLines, List<Player> players) {
        this.logLines = List.copyOf(Objects.requireNonNull(logLines, "logLines"));
        this.players = List.copyOf(Objects.requireNonNull(players, "players"));
    }

    public List<String> getLogLines() {
        return logLines;
    }

    public List<Player> getPlayers() {
        return players;
    }

    // Creates the scoreboard printed at the very end.
    public String summarize() {
        StringBuilder summary = new StringBuilder();
        summary.append("Final Summary\n");
        for (Player player : players) {
            summary.append("- ").append(player.getName()).append(" scores ").append(player.getVictoryPoints()).append(", resources: ");
            for (Map.Entry<ResourceType, Integer> entry : player.getResources().entrySet()) {
                summary.append(entry.getKey()).append("=").append(entry.getValue()).append(" ");
            }
            summary.append("settlements=").append(player.getSettlementNodeIds().size());
            summary.append(" roads=").append(player.getRoadPathIds().size());
            summary.append("\n");
        }
        return summary.toString();
    }
}
