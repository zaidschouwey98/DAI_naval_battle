package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import java.io.*;
import java.net.*;

@CommandLine.Command(name = "joinGame", description = "Start the client part of the network game.")
public class Client implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-H", "--host"},
            description = "IP adress of the host to connect to.",
            defaultValue = "127.0.0.1",
            required = true)
    protected String host;

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: ${DEFAULT-VALUE}).",
            defaultValue = "6433")
    protected int port;

    @Override
    public Integer call() {
        try (Socket socket = new Socket(host, port);
             BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
             BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("[Client] Connected to " + host + ":" + port);

            byte[] buffer = new byte[1024];
            while (!socket.isClosed()) {
                // Lire les messages du serveur
                if (bin.available() > 0) {
                    int bytesRead = bin.read(buffer);
                    String serverMessage = new String(buffer, 0, bytesRead);
                    System.out.println("[Server] " + serverMessage);

                    // Vérifier si le serveur envoie un message de fin de jeu
                    if ("END".equals(serverMessage.trim())) {
                        System.out.println("[Client] Game over.");
                        socket.close();
                        break;
                    }
                }

                // Envoyer des messages au serveur
                if (reader.ready()) {
                    System.out.print("Your move: ");
                    String clientMessage = reader.readLine();
                    bout.write(clientMessage.getBytes());
                    bout.flush();

                    // Quitter si le client envoie une commande de sortie
                    if ("quit".equalsIgnoreCase(clientMessage.trim())) {
                        System.out.println("[Client] Disconnecting...");
                        socket.close();
                        break;
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("[Client] Could not connect to " + host + ":" + port);
            return -1;
        }
        return 0;
    }
}