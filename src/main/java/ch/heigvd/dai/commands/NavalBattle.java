package ch.heigvd.dai.commands;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NavalBattle implements Runnable {
    public static final String END_OF_LINE = "\n";
    public static int id_game = 1;
    public static final int NBR_BOATS = 3;
    public boolean rematch = false;
    private final int thisIdGame;
    private final Socket socketP1, socketP2;

    /**
     * The class constructor
     */
    public NavalBattle(Socket socketP1, Socket socketP2) {
        this.socketP1 = socketP1;
        this.socketP2 = socketP2;
        this.thisIdGame = id_game++;
    }

    /**
     * Main function
     */
    @Override
    public void run() {
        try (
                BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(socketP1.getOutputStream(), StandardCharsets.UTF_8));
                BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(socketP2.getOutputStream(), StandardCharsets.UTF_8));
                BufferedReader in1  = new BufferedReader(new InputStreamReader(socketP1.getInputStream(), StandardCharsets.UTF_8));
                BufferedReader in2  = new BufferedReader(new InputStreamReader(socketP2.getInputStream(), StandardCharsets.UTF_8))
        ) {
            do {
                broadcastMessage(out1, out2, "GAMEREADY");

                // Ask the players to place their boats
                BoardHandler boardP1 = new BoardHandler();
                BoardHandler boardP2 = new BoardHandler();
                broadcastMessage(out1, out2, "INIT_GRID");
                boardP1.placeShip(in1.readLine().split("=")[1]);
                boardP2.placeShip(in2.readLine().split("=")[1]);
                sendMessage(out1, "URBOARD=" + boardP1.getUserBoardToString());
                sendMessage(out2, "URBOARD=" + boardP2.getUserBoardToString());

                // Start of the game
                System.out.println("[GameSession] Game n°" + thisIdGame + " started!");
                playGame(out1, out2, in1, in2, boardP1, boardP2);


                // Ask the players if they want to play again
                if (in2.readLine().equals("REMATCH_OFFER") && in1.readLine().equals("REMATCH_OFFER")) {
                    rematch = true;
                } else {
                    rematch = false;
                    broadcastMessage(out1, out2, "REMATCH_DENY");
                    broadcastMessage(out1, out2, "END");
                    System.out.println("[GameSession] Game n°" + thisIdGame + " ended.");
                }

            } while(rematch);
        } catch (IOException e) {
            System.out.println("[GameSession] Error: " + e.getMessage());
        } finally {
            closeSockets();
        }
    }

    /**
     * Simulation of the game
     */
    private void playGame(BufferedWriter out1, BufferedWriter out2, BufferedReader in1, BufferedReader in2,
                          BoardHandler boardP1, BoardHandler boardP2) throws IOException {
        BufferedReader attackerIn  = in1;
        BufferedWriter attackerOut = out1;
        BufferedWriter defenderOut = out2;
        boolean isP1Turn = true; // Identifies who's turn this is | player 1 starts first

        int hitsP1 = 0, hitsP2 = 0;

        while (!socketP1.isClosed() && !socketP2.isClosed()) {
            sendMessage(defenderOut, "WAIT");
            char result;
            // Get the attacker input
            while (true) {
                sendMessage(attackerOut, "ATTACK");
                int targetCell = Integer.parseInt(attackerIn.readLine().split("=")[1]);

                result = (isP1Turn ? boardP2 : boardP1).receiveFire(targetCell);
                if (result != 'E') break;

                sendMessage(attackerOut, "ERROR"); // The user already attacked the cell entered
            }

            // Check of the attacker input
            if (result == GridUtils.gridStateChar[GridUtils.GridState.HIT.ordinal()]) {
                if (isP1Turn) hitsP1++; else hitsP2++;
                broadcastMessage(attackerOut, defenderOut, "HIT");
            } else {
                broadcastMessage(attackerOut, defenderOut, "MISS");
            }

            sendBoards(attackerOut, defenderOut, boardP1, boardP2, isP1Turn);

            // Check results
            if (hitsP1 == NBR_BOATS || hitsP2 == NBR_BOATS) {
                sendMessage(attackerOut, "UWON");
                sendMessage(defenderOut, "ULOST");
                break;
            }

            // Inverting the roles
            isP1Turn = !isP1Turn;
            attackerIn = (attackerIn == in1) ? in2 : in1;
            attackerOut = (attackerOut == out1) ? out2 : out1;
            defenderOut = (defenderOut == out2) ? out1 : out2;
        }
    }

    /**
     * Sends the previous attacked board to both players depending on roles
     */
    private void sendBoards(BufferedWriter attackerOut, BufferedWriter defenderOut,
                            BoardHandler boardP1, BoardHandler boardP2, boolean isP1Turn) throws IOException {
        if (isP1Turn) {
            sendMessage(attackerOut, "OPPONENTBOARD=" + boardP2.getOpponentBoardToString());
            sendMessage(defenderOut, "URBOARD=" + boardP2.getUserBoardToString());
        } else {
            sendMessage(attackerOut, "OPPONENTBOARD=" + boardP1.getOpponentBoardToString());
            sendMessage(defenderOut, "URBOARD=" + boardP1.getUserBoardToString());
        }
    }

    /**
     * Sends a message to both players
     */
    private void broadcastMessage(BufferedWriter out1, BufferedWriter out2, String message) throws IOException {
        sendMessage(out1, message);
        sendMessage(out2, message);
    }

    /**
     * Sends a message to one player
     */
    private void sendMessage(BufferedWriter out, String message) throws IOException {
        out.write(message + END_OF_LINE);
        out.flush();
    }

    /**
     * Closes the sockets
     */
    private void closeSockets() {
        try {
            socketP1.close();
            socketP2.close();
        } catch (IOException e) {
            System.out.println("[GameSession] Could not close sockets: " + e.getMessage());
        }
    }
}
