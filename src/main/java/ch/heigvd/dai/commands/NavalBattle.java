package ch.heigvd.dai.commands;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;



public class NavalBattle implements Runnable {
    public static String END_OF_LINE = "\n";

    private final Socket socket_p1, socket_p2;
    NavalBattle(Socket socket_p1, Socket socket_p2){
        this.socket_p1 = socket_p1;
        this.socket_p2 = socket_p2;
    }

    @Override
    public void run() {
        try (BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(socket_p1.getOutputStream(), StandardCharsets.UTF_8));
             BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(socket_p2.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader in1 = new BufferedReader(new InputStreamReader(socket_p1.getInputStream(), StandardCharsets.UTF_8));
             BufferedReader in2 = new BufferedReader(new InputStreamReader(socket_p2.getInputStream(), StandardCharsets.UTF_8));) {

            // Messages initiaux aux joueurs
            out1.write("GAMEREADY" + END_OF_LINE);
            out1.flush();
            out2.write("GAMEREADY" + END_OF_LINE);
            out2.flush();
            BufferedWriter currentOutBuffer = out1;
            BufferedReader currentInBuffer = in1;
            System.out.println("[GameSession] Game started between Player 1 and Player 2.");

            // Simuler le déroulement du jeu
            while (!this.socket_p2.isClosed() || !this.socket_p1.isClosed()) {
                currentOutBuffer.write("ATTACK");
                currentOutBuffer.flush();
                String command = currentInBuffer.readLine(); // Bloquant jusqu'à recevoir commande du serveur
                String[] parsed_command = command.split("=");
                switch (parsed_command[0]) {
                    case "INIT_GRID": // Initialize player board by placing 3 boats
                        break;
                    case "ATTACK":

                        break;
                    case "BOARD":

                        break;
                    case "WAIT":

                        break;
                    case "END":

                        break;
                }

                if(currentInBuffer == in1){
                    currentInBuffer = in2;
                } else {
                    currentInBuffer = in1;
                }
                if(currentOutBuffer == out1){
                    currentOutBuffer = out2;
                } else {
                    currentOutBuffer = out1;
                }
            }
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
