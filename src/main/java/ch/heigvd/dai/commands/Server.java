package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@CommandLine.Command(name = "hostGame", description = "Start the server part of the network game.")
public class Server implements Callable<Integer> {

    public static String END_OF_LINE = "\n";

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: ${DEFAULT-VALUE}).",
            defaultValue = "6433")
    protected static int port;

    @Override
    public Integer call() {
        throw new UnsupportedOperationException(
                "Please remove this exception and implement this method.");
    }

    static void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Server] Listening on port " + port);

            while (true) {
                Socket player1Socket = null;
                Socket player2Socket = null;

                try {
                    // Attente du premier joueur
                    player1Socket = serverSocket.accept();
                    System.out.println("[Server] Player 1 connected from "
                            + player1Socket.getInetAddress().getHostAddress() + ":"
                            + player1Socket.getPort() + END_OF_LINE);
                    BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(player1Socket.getOutputStream(),
                            StandardCharsets.UTF_8));

                    out1.write("Waiting for a second player to join..." + END_OF_LINE);
                    out1.flush();

                    // Attente du second joueur
                    player2Socket = serverSocket.accept();
                    System.out.println("[Server] Player 2 connected from "
                            + player2Socket.getInetAddress().getHostAddress() + ":"
                            + player2Socket.getPort() + END_OF_LINE);
                    BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(player2Socket.getOutputStream(),
                            StandardCharsets.UTF_8));

                    // Confirmation de connexion pour les deux joueurs
                    out1.write("Both players are connected. The game can start!" + END_OF_LINE);
                    out1.flush();
                    out2.write("Both players are connected. The game can start!" + END_OF_LINE);
                    out2.flush();

                    // Ici, on pourrait lancer le jeu, mais pour l'instant on s'arrête ici
                    System.out.println("[Server] Both players are now connected. Ready to start the game.");



                    // Fermeture des connexions des joueurs après la fin du jeu
                    player1Socket.close();
                    player2Socket.close();
                    System.out.println("[Server] Both players are now disconnected. End of the game.");

                } catch (IOException e) {
                    System.out.println("[Server] IO exception: " + e);
                    if (player1Socket != null && !player1Socket.isClosed()) {
                        try {
                            player1Socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (player2Socket != null && !player2Socket.isClosed()) {
                        try {
                            player2Socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[Server] IO exception: " + e);
        }
    }
}
