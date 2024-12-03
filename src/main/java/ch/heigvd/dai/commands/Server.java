package ch.heigvd.dai.commands;

import picocli.CommandLine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CommandLine.Command(name = "server", description = "Start the server part of the network game.")
public class Server implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: ${DEFAULT-VALUE}).",
            defaultValue = "6433")

    protected int port;
    private ServerSocket serverSocket;


    @Override
    public Integer call() throws IOException {
        int THREAD_POOL_SIZE = 3;
        try(ServerSocket serverSocket = new ServerSocket(port);
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);) {
            System.out.println("Server started !");
            System.out.println("Waiting for players to connect...");
            while (!serverSocket.isClosed()) {
                Socket socket_p1 = serverSocket.accept();
                Socket socket_p2 = serverSocket.accept();
                executorService.submit(new NavalBattle(socket_p1, socket_p2));
            }
        } catch (IOException e){
            System.err.println("Error starting servor : " + e);
        }
        return 0;
    }
}
