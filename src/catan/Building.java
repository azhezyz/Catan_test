package catan;

import java.util.Objects;
import java.util.Optional;

/*
 * Represents a physical structure built on the board (Settlement or City).
 * This class is "immutable," meaning once a Building object is created, 
 * its type and owner cannot be changed.
 */
public final class Building {
    private final BuildingType type;
    private final Player owner;

    /*
     * Private constructor. 
     * We use logic here to ensure the building is valid:
     * - A NONE type building cannot have an owner.
     * - A SETTLEMENT or CITY must have an owner.
     */
    private Building(BuildingType type, Player owner) {
        this.type = Objects.requireNonNull(type, "type");
        this.owner = owner;
        if (type == BuildingType.NONE && owner != null) {
            throw new IllegalArgumentException("Empty building cannot have an owner.");
        }
        if (type != BuildingType.NONE && owner == null) {
            throw new IllegalArgumentException("Occupied building must have an owner.");
        }
    }

    // Creates an empty spot where nothing is built yet
    public static Building empty() {
        return new Building(BuildingType.NONE, null);
    }

    // Creates a new Settlement belonging to a player
    public static Building settlement(Player owner) {
        return new Building(BuildingType.SETTLEMENT, owner);
    }

    // Creates a new City belonging to a player
    public static Building city(Player owner) {
        return new Building(BuildingType.CITY, owner);
    }

    // Returns the type (NONE, SETTLEMENT, or CITY)
    public BuildingType getType() {
        return type;
    }

    // Returns the owner wrapped in an Optional (since a spot might be empty)
    public Optional<Player> getOwner() {
        return Optional.ofNullable(owner);
    }

    // Quick check to see if the spot is taken
    public boolean isOccupied() {
        return type != BuildingType.NONE;
    }

    // Checks if a specific player is the owner of this building
    public boolean isOwnedBy(Player player) {
        return owner != null && owner.equals(player);
    }
}