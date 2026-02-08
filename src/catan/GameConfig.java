package catan;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class GameConfig {
    private final int maxRounds;
    private final List<Integer> diceSequence;

    private GameConfig(int maxRounds, List<Integer> diceSequence) {
        if (maxRounds <= 0 || maxRounds > 8192) {
            throw new IllegalArgumentException("Max rounds must be between 1 and 8192.");
        }
        if (diceSequence.isEmpty()) {
            throw new IllegalArgumentException("Dice sequence cannot be empty.");
        }
        this.maxRounds = maxRounds;
        this.diceSequence = List.copyOf(diceSequence);
    }

    public static GameConfig load(Path path) {
        Properties properties = new Properties();
        if (Files.exists(path)) {
            try (InputStream input = Files.newInputStream(path)) {
                properties.load(input);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read config file.", e);
            }
        }
        int rounds = Integer.parseInt(properties.getProperty("maxRounds", "10"));
        String dice = properties.getProperty("diceSequence", "6,8,5,6,9,5,10,4");
        List<Integer> sequence = parseDiceSequence(dice);
        return new GameConfig(rounds, sequence);
    }

    private static List<Integer> parseDiceSequence(String raw) {
        String[] parts = raw.split(",");
        List<Integer> sequence = new ArrayList<>();
        for (String part : parts) {
            int roll = Integer.parseInt(part.trim());
            if (roll < 2 || roll > 12) {
                throw new IllegalArgumentException("Dice roll out of range: " + roll);
            }
            sequence.add(roll);
        }
        return sequence;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public List<Integer> getDiceSequence() {
        return diceSequence;
    }
}
