package ch.heigvd.dai.commands;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "client", description = "Start the client part of the network game.")
public class Client implements Callable<Integer> {
    @CommandLine.Option(
            names = {"-H", "--host"},
            description = "Host to connect to.",
            defaultValue = "127.0.0.1",
            required = true)
    protected String host;

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: ${DEFAULT-VALUE}).",
            defaultValue = "6433")
    protected int port;

    private static final int NBR_BOATS = 3;
    private static final String END_LINE = "\r\n";

    /**
     * Returns true / false if the cells entered by the user is on the board
     */
    private static boolean isNotOnBoard(String input) {
        try {
            int number = Integer.parseInt(input);
            return number < 1 || number > BoardHandler.SIZE;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    /**
     * The main behavior of the client part for the game
     */
    @Override
    public Integer call() {
        // Trying to connect to the server
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Waiting for the game to start...");

            // Beginning of the game
            while (!socket.isClosed()) {
                String command = in.readLine(); // Blocking until server responds

                if (command == null) {
                    System.out.println("Server closed connection.");
                    break;
                }

                processCommand(command, out, scanner, socket);
                System.out.println("-----------------------------------");
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
        // End of the game
        System.out.println("Bye Bye !");
        return 0;
    }

    /**
     * Decodes a command received by the server
     */
    private void processCommand(String command, BufferedWriter out, Scanner scanner, Socket socket) throws IOException {
        String[] parsedCommand = command.split("=", 2);
        String action = parsedCommand[0];
        String payload = parsedCommand.length > 1 ? parsedCommand[1] : "";

        switch (action) {
            case "GAMEREADY":
                System.out.println("Game is starting !");
                break;

            case "INIT_GRID":
                handleInitGrid(out, scanner);
                break;

            case "ATTACK":
                handleAttack(out, scanner);
                break;

            case "URBOARD":
                displayBoard("Your board:", payload);
                break;

            case "OPPONENTBOARD":
                displayBoard("Opponent's board:", payload);
                break;

            case "HIT":
                System.out.println("HIT!");
                break;

            case "MISS":
                System.out.println("Missed...");
                break;

            case "WAIT":
                System.out.println("Waiting for opponent's move...");
                break;

            case "UWON":
                System.out.println("YOU WIN!");
                this.handleEndOfGame(out, scanner);
                break;

            case "ULOST":
                System.out.println("YOU LOST!");
                this.handleEndOfGame(out, scanner);
                break;

            case "END":
                socket.close();
                break;

            case "ERROR":
                System.out.println("You already attacked this cell!");
                break;

            case "REMATCH_DENY":
                System.out.println("Rematch denied !");
                break;

            default:
                System.out.println("Unexpected command from server...");
                break;
        }
    }

    /**
     * Makes the player choose 3 cells to place his boats
     */
    private void handleInitGrid(BufferedWriter out, Scanner scanner) throws IOException {
        System.out.println("Place your " + NBR_BOATS + " boats [1 - 16]");
        String[] boats = new String[NBR_BOATS];

        for (int i = 0; i < NBR_BOATS; i++) {
            boats[i] = getValidInput(scanner, "Enter boat n°" + (i + 1) + ":", boats);
        }

        out.write("INIT_GRID=" + String.join(" ", boats) + END_LINE);
        out.flush();
        System.out.println("You've placed your boats. Wait for opponent.");
    }

    /**
     * Makes the player choose a cell to attack
     */
    private void handleAttack(BufferedWriter out, Scanner scanner) throws IOException {
        String move = getValidInput(scanner, "Enter the cell to attack:", null);
        out.write("ATTACK=" + move + END_LINE);
        out.flush();
    }

    /**
     * Asks the player if he wants to replay a game or not
     */
    private void handleEndOfGame(BufferedWriter out, Scanner scanner) throws IOException {
        System.out.println("You can type 'Rematch' to ask for a rematch and 'Exit' to quit.");
        String input = scanner.nextLine();
        while(!input.equalsIgnoreCase("rematch") && !input.equalsIgnoreCase("exit")) {
            System.out.println("You can type 'Rematch' to ask for a rematch and 'Exit' to quit.");
            input = scanner.nextLine();
        }
        if(input.equalsIgnoreCase("rematch")) {
            out.write("REMATCH_OFFER" + END_LINE);
            System.out.println("Waiting for oppenent...");
        } else {
            out.write("QUIT" + END_LINE);
        }
        out.flush();

    }

    /**
     * Makes the user enter a number then checks if its valid
     */
    private String getValidInput(Scanner scanner, String prompt, String[] existing) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine();

            if (isNotOnBoard(input)) {
                System.out.println("Error: wrong entry !");
                continue;
            }

            if (existing != null && contains(existing, input)) {
                System.out.println("Error: you've already entered this cell !");
                continue;
            }

            break;
        }
        return input;
    }

    /**
     * displays a board
     */
    private void displayBoard(String title, String payload) {
        System.out.println(title);
        int size = (int) Math.sqrt(BoardHandler.SIZE); // The size of a line

        String centerize = "      "; // To center the board
        System.out.print(centerize + "    ");
        for (int col = 1; col <= size; col++) {
            System.out.printf("%2d  ", col);
        }
        System.out.println();

        for (int row = 0; row < size; row++) {
            System.out.print(centerize + "   ");
            for (int col = 0; col < size; col++) {
                System.out.print("+---");
            }
            System.out.println("+");

            System.out.printf(centerize + "%2d ", row + 1);
            for (int col = 0; col < size; col++) {
                System.out.printf("| %c ", payload.charAt(row * size + col));
            }
            System.out.println("|");
        }

        System.out.print(centerize + "   ");
        for (int col = 0; col < size; col++) {
            System.out.print("+---");
        }
        System.out.println("+");
    }

    /**
     * Called by getValidInputs :
     * Checks if a value isn't already in a array
     */
    private boolean contains(String[] array, String value) {
        for (String s : array) {
            if (value.equals(s)) {
                return true;
            }
        }
        return false;
    }
}
