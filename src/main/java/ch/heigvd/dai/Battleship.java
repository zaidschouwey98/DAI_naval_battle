package ch.heigvd.dai;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

public class Battleship {
    final int SIZE = 10;
    boolean TURN = false;
    char[] grid = new char[SIZE];
    char[] target = new char[SIZE];
    private final Random random = new Random();

    private void initialiseGrid(char[] grid) {
        for (int i = 0; i < grid.length; i++) {
            grid[i] = '_';
        }
    }

    public Battleship() {
        initialiseGrid(this.grid);
        initialiseGrid(this.target);
        placeShip(2, 'A');
        placeShip(3, 'B');
    }

    private void placeShip(int size, char marker) {
        boolean placed = false;

        while (!placed) {
            int start = random.nextInt(target.length - size + 1);

            boolean overlap = false;
            for (int i = start; i < start + size; i++) {
                if (target[i] != '_') {
                    overlap = true;
                    break;
                }
            }

            if (!overlap) {
                for (int i = start; i < start + size; i++) {
                    target[i] = marker;
                }
                placed = true;
            }
        }
    }

    char getPosition(char[] grid, int p) {
        return grid[p];
    }

    void setPosition(int p, char c) {
        grid[p] = c;
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

    private boolean checkHit(int position) {
        if (target[position] != '_') {
            char ship = target[position];
            target[position] = '_';
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try (DataInputStream reader = new DataInputStream(System.in)) {
            Battleship game = new Battleship();
            while (true) {
                try {
                    System.out.println("Current grid:");
                    System.out.println(game);

                    System.out.print("Your turn: Please enter a position [0-" + (game.SIZE - 1) + "]: ");
                    int input = Integer.parseInt(reader.readLine());

                    if (input >= 0 && input < game.SIZE) {
                        if (game.getPosition(game.grid, input) == 'X') {
                            System.out.println("Position " + input + " has already been marked. Choose a different position.");
                        } else {
                            game.setPosition(input, 'X');
                            if (game.checkHit(input)) {
                                System.out.println("Hit! You hit a ship at position " + input);
                            } else {
                                System.out.println("Miss! No ship at position " + input);
                                game.setPosition(input, 'O');
                            }
                        }
                    } else {
                        System.out.println("Invalid position. Please enter a number between 0 and " + (game.SIZE - 1));
                    }

                    if (game.isGameOver()) {
                        System.out.println("Game Over! You've sunk all the ships.");
                        break;
                    }

                } catch (IOException e) {
                    System.out.println("An error occurred during input. Please try again.");
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while initializing the input stream.");
        }
    }

    private boolean isGameOver() {
        for (int i = 0; i < SIZE; i++) {
            if (target[i] != '_') {
                return false;
            }
        }
        return true;
    }
}
