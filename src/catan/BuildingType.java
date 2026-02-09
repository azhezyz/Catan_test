package catan;

/*
 * An Enum defining the three possible states for a node on the board.
 * - NONE: The node is empty (no buildings).
 * - SETTLEMENT: A basic building worth 1 Victory Point.
 * - CITY: An upgraded building worth 2 Victory Points.
 */
public enum BuildingType {
    NONE,
    SETTLEMENT,
    CITY
}
