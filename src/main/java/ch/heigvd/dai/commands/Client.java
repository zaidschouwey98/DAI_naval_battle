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
             BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream())) {
            System.out.println("[Client] Connected to " + host + ":" + port);
            while(socket.isConnected()) {
                //Garder le buffer d'entrée et sortie actif
                socket.close();
            }
             } catch (IOException e) {
            System.out.println("[Client] Could not connect to " + host + ":" + port);
        }
        throw new UnsupportedOperationException(
                "Please remove this exception and implement this method.");
    }
}