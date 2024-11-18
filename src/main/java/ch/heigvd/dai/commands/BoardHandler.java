package ch.heigvd.dai.commands;

import java.util.Random;

public class BoardHandler {

    final int SIZE = 10;
    boolean TURN = false;
    char[] grid = new char[SIZE];
    char[] target = new char[SIZE];

    private final Random random = new Random();
    public BoardHandler() {
        initialiseGrid(this.grid);
        initialiseGrid(this.target);
        placeShip(2, 'A');
        placeShip(3, 'B');
    }
    private void placeShip(int size, char marker) {
        boolean placed = false;

        while (!placed) {
            int start = random.nextInt(grid.length - size + 1);

            boolean overlap = false;
            for (int i = start; i < start + size; i++) {
                if (grid[i] != '_') {
                    overlap = true;
                    break;
                }
            }

            if (!overlap) {
                for (int i = start; i < start + size; i++) {
                    grid[i] = marker;
                }
                placed = true;
            }
        }
    }

    private void initialiseGrid(char[] grid) {
        for (int i = 0; i < grid.length; i++) {
            grid[i] = '_';
        }
    }

    @Override
    public String toString() {
        StringBuilder gridString = new StringBuilder("[");
        for (int i = 0; i < SIZE; i++) {
            gridString.append(grid[i]);
            if (i < SIZE - 1) {
                gridString.append(",");
            }
        }
        gridString.append("]");
        return gridString.toString();
    }

    public FireResult receiveFire(int index){
        if(index > grid.length){
            throw new IndexOutOfBoundsException();
        }


        FireResult res = switch (grid[index]) {
            case 'B' -> FireResult.H;
            case 'A' -> FireResult.H;
            case '_' -> FireResult.M;
            default -> FireResult.UNKOWN;
        };
        if(this.grid[index] == 'B'){
            this.grid[index] = 'X';
        }
        if(this.grid[index] == '_'){
            this.grid[index] = '_';
        }
        return res;
    }

    public void setOpponentBoardCell(int index, FireResult fireResult){
        if(index > grid.length){
            throw new IndexOutOfBoundsException();
        }
        switch (fireResult){
            case H -> this.target[index] = 'X';
            case M -> this.target[index] = 'O';
        }


    }
    public String getOpponentBoardToString() {
        String str = "";
        str +='[';
        for(char c : target){
            str += c + ",";
        }
        str+=']';
        return str;
    }


}
