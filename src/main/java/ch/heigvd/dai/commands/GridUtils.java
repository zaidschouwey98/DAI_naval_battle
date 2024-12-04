package ch.heigvd.dai.commands;

public class GridUtils {
    // Enum pour représenter les états de la grille
    public static enum GridState {
        EMPTY_CELL,
        BOAT,
        HIT,
        MISS
    }

    // Tableau de chaînes associé aux états de la grille
    public static final char[] gridStateChar = {'~', 'O', 'X' , 'o'};
}