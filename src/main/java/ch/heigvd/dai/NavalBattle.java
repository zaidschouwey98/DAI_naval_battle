package ch.heigvd.dai;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;



public class NavalBattle implements Runnable {
    public static String END_OF_LINE = "\n";

    private final Socket socket_p1, socket_p2;

    public
    NavalBattle(Socket socket_p1, Socket socket_p2){
        this.socket_p1 = socket_p1;
        this.socket_p2 = socket_p2;
    }

    @Override
    public void run() {
        try (BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(socket_p1.getOutputStream(), StandardCharsets.UTF_8));
             BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(socket_p2.getOutputStream(), StandardCharsets.UTF_8))) {

            // Messages initiaux aux joueurs
            out1.write("Both players are connected. The game can start!" + END_OF_LINE);
            out1.flush();
            out2.write("Both players are connected. The game can start!" + END_OF_LINE);
            out2.flush();

            System.out.println("[GameSession] Game started between Player 1 and Player 2.");

            // Simuler le déroulement du jeu
            Thread.sleep(5000); // Remplacer par une vraie logique de jeu

            // Envoyer le message de fin de jeu
            out1.write("END" + END_OF_LINE);
            out1.flush();
            out2.write("END" + END_OF_LINE);
            out2.flush();

            System.out.println("[GameSession] Game ended between Player 1 and Player 2.");

        } catch (IOException | InterruptedException e) {
            System.out.println("[GameSession] Error: " + e);
        } finally {
            try {
                socket_p1.close();
                socket_p2.close();
            } catch (IOException e) {
                System.out.println("[GameSession] Could not close sockets: " + e);
            }
        }
    }
}
