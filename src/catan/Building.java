package catan;

import java.util.Objects;
import java.util.Optional;

public final class Building {
    private final BuildingType type;
    private final Player owner;

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

    public static Building empty() {
        return new Building(BuildingType.NONE, null);
    }

    public static Building settlement(Player owner) {
        return new Building(BuildingType.SETTLEMENT, owner);
    }

    public static Building city(Player owner) {
        return new Building(BuildingType.CITY, owner);
    }

    public BuildingType getType() {
        return type;
    }

    public Optional<Player> getOwner() {
        return Optional.ofNullable(owner);
    }

    public boolean isOccupied() {
        return type != BuildingType.NONE;
    }

    public boolean isOwnedBy(Player player) {
        return owner != null && owner.equals(player);
    }
}
