package catan;

/*
 * An Enum that lists the four possible construction actions in the game.
 * - ROAD: Building a road segment on a path.
 * - SETTLEMENT: Building a new settlement on a node.
 * - CITY: Upgrading an existing settlement to a city.
 * - NONE: Skipping the build phase or taking no action.
 */
public enum BuildAction {
    ROAD,
    SETTLEMENT,
    CITY,
    NONE
}
