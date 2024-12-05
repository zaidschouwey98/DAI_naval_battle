package ch.heigvd.dai.commands;

import java.util.Arrays;

public class BoardHandler {

    static final int SIZE = 16;
    static final int OBJ_PER_LINE = 4;
    private final char[] grid = new char[SIZE];   // The user's grid
    private final char[] target = new char[SIZE]; // The opponent's grid, the boats aren't revealed but keeps tracks of
                                                  // shots fired

    /**
     * Class constuctor
     **/
    public BoardHandler() {
        initializeGrid(grid);
        initializeGrid(target);
    }

    /**
     * Initialize the cells by filling them with "empty_cell" (->GridUtils)
     **/
    private void initializeGrid(char[] grid) {
        Arrays.fill(grid, GridUtils.gridStateChar[GridUtils.GridState.EMPTY_CELL.ordinal()]);
    }

    /**
     * Place a boat on the user's grid
     **/
    public void placeShip(String boats) {
        for (String boat : boats.split(" ")) {
            grid[Integer.parseInt(boat) - 1] = GridUtils.gridStateChar[GridUtils.GridState.BOAT.ordinal()];
        }
    }

    /**
     * Follows an attack move from a player :
     * Updates the grid depending on the cell chosen
     **/
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

    /**
     * Called by receiveFire :
     * Changes the state of a cell
     **/
    private char updateCellState(int index, GridUtils.GridState newState) {
        char newStateChar = GridUtils.gridStateChar[newState.ordinal()];
        grid[index] = newStateChar;
        target[index] = newStateChar;
        return newStateChar;
    }

    /**
     * Returns the grid of the opponent as a String
     **/
    public String getOpponentBoardToString() {
        return boardToString(target);
    }

    /**
     * Returns the user's grid as a String
     **/
    public String getUserBoardToString() {
        return boardToString(grid);
    }

    /**
     * Transforms a grid to a String (ex. "~~XoO~ ...")
     **/
    private String boardToString(char[] board) {
        StringBuilder str = new StringBuilder(SIZE);
        for (char cell : board) {
            str.append(cell);
        }
        return str.toString();
    }
}