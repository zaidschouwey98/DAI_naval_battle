package ch.heigvd.dai.commands;

public class GridUtils {
    // Enum to represent the states of a cell
    public static enum GridState {
        EMPTY_CELL,
        BOAT,
        HIT,
        MISS
    }

    // Array associated with the enum of the states
    public static final char[] gridStateChar = {
            '~', // EMPTY_CELL
            'O', // BOAT
            'X', // HIT
            '*'  // MISS
    };
}