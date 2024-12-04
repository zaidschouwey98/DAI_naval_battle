package ch.heigvd.dai.commands;

import java.util.Arrays;

public class BoardHandler {

    static final int SIZE = 16;
    static final int OBJ_PER_LINE = 4;
    private final char[] grid = new char[SIZE];
    private final char[] target = new char[SIZE];

    public BoardHandler() {
        initializeGrid(grid);
        initializeGrid(target);
    }

    private void initializeGrid(char[] grid) {
        Arrays.fill(grid, GridUtils.gridStateChar[GridUtils.GridState.EMPTY_CELL.ordinal()]);
    }

    public void placeShip(String boats) {
        for (String boat : boats.split(" ")) {
            grid[Integer.parseInt(boat) - 1] = GridUtils.gridStateChar[GridUtils.GridState.BOAT.ordinal()];
        }
    }

    public char receiveFire(int index) {
        index -= 1; // Ajuste l'index car l'utilisateur entre [1-16]
        char currentState = grid[index];

        if (currentState == GridUtils.gridStateChar[GridUtils.GridState.BOAT.ordinal()]) {
            return updateCellState(index, GridUtils.GridState.HIT);
        } else if (currentState == GridUtils.gridStateChar[GridUtils.GridState.EMPTY_CELL.ordinal()]) {
            return updateCellState(index, GridUtils.GridState.MISS);
        }

        return 'E'; // La case est invalide ou déjà attaquée
    }

    private char updateCellState(int index, GridUtils.GridState newState) {
        char newStateChar = GridUtils.gridStateChar[newState.ordinal()];
        grid[index] = newStateChar;
        target[index] = newStateChar;
        return newStateChar;
    }

    public String getOpponentBoardToString() {
        return boardToString(target);
    }

    public String getUserBoardToString() {
        return boardToString(grid);
    }

    private String boardToString(char[] board) {
        StringBuilder str = new StringBuilder(SIZE);
        for (char cell : board) {
            str.append(cell);
        }
        return str.toString();
    }
}