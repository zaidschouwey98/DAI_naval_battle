package ch.heigvd.dai.commands;

import java.util.concurrent.*;

import ch.heigvd.dai.NavalBattle;
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

    private static final int MAX_THREADS = 10; // Limite des threads

    @Override
    public Integer call() {
        startServer(port);
        return 0;
    }

    static void startServer(int port) {
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Server] Listening on port " + port);

            while (!serverSocket.isClosed()) {
                try {
                    // Accepter la connexion du premier joueur
                    Socket player1Socket = serverSocket.accept();
                    System.out.println("[Server] Player 1 connected from "
                            + player1Socket.getInetAddress().getHostAddress() + ":"
                            + player1Socket.getPort() + END_OF_LINE);

                    // Accepter la connexion du second joueur
                    Socket player2Socket = serverSocket.accept();
                    System.out.println("[Server] Player 2 connected from "
                            + player2Socket.getInetAddress().getHostAddress() + ":"
                            + player2Socket.getPort() + END_OF_LINE);

                    // Confirmer que les deux joueurs sont connectés
                    executorService.submit(new NavalBattle(player1Socket, player2Socket));
                } catch (IOException e) {
                    System.out.println("[Server] IO exception: " + e);
                }
            }
        } catch (IOException e) {
            System.out.println("[Server] Could not start the server: " + e);
        } finally {
            executorService.shutdown();
        }
    }
}
