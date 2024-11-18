package ch.heigvd.dai.commands;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import picocli.CommandLine;

@CommandLine.Command(name = "server", description = "Start the server part of the network game.")
public class Server implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: ${DEFAULT-VALUE}).",
            defaultValue = "6433")

    protected int port;
    private ServerSocket serverSocket;
    private final int THREAD_POOL_SIZE = 10;


    @Override
    public Integer call() throws IOException {
        LinkedList<Game> games = new LinkedList<>();
        games.push(new Game());

        serverSocket = new ServerSocket(port);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        System.out.println("Server started on port:" + port);
        int playerId = 0;
        while(!serverSocket.isClosed()){
            Socket clientSocket = serverSocket.accept();
            playerId = ++playerId;
            for(Game game : games){
                if(game.getPlayer() < 2){
                    executor.submit(new ClientHandler(clientSocket,game,playerId));
                    break;
                } else {
                    games.push(new Game());
                }
            }
        }
        return 0;
    }
}

class ClientHandler implements Runnable {

    private final Socket socket;
    private final Game game;
    private final int  playerId;
    public ClientHandler(Socket socket,Game game, int playerId) {
        this.game = game;
        this.socket = socket;
        this.playerId = playerId;
    }

    @Override
    public void run() {
        try (socket;
             BufferedReader in =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out =
                     new BufferedWriter(
                             new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
            System.out.println(
                    "[Server "
                            + "] new client connected from "
                            + socket.getInetAddress().getHostAddress()
                            + ":"
                            + socket.getPort());

            game.addPlayer(playerId,new BoardHandler());


            System.out.println("Generating board...");
            // Generate board
            in.readLine().equals("START");



            System.out.println("Sending board...");
            // SEND BOARD
            String boardtoS = "BOARD=" + this.game.getBoard(playerId).toString() +"\n";
            out.write(boardtoS);
            out.flush();

            synchronized (game.getLock()) {
                while (game.getPlayer() < 2) {
                    System.out.println("Waiting for player...");
                    game.getLock().wait();
                }
                game.getLock().notifyAll();
            }

            System.out.println("Game Starting ...");
            out.write("GAME_READY\n");
            out.flush();


            while (!game.gameOver()) {
                synchronized (game.getLock()) {
                    Thread.sleep(1000);
                    if (game.getPlayerTurn() == playerId) {
                        out.write("YOUR_TURN\n");
                        out.flush();
                        String clientMessage = in.readLine();
                        if(clientMessage.contains("ATTACK")) {
                            System.out.println("Attacking...");
                            int targetIndex = Integer.parseInt(clientMessage.substring(7));
                            FireResult res = game.shoot(targetIndex);
                            game.getBoard(playerId).setOpponentBoardCase(targetIndex,res);

                            if(res == FireResult.H){
                                out.write("HIT\n");
                                out.flush();
                                System.out.println("HIT");
                            } else if(res == FireResult.M){
                                out.write("MISS\n");
                                out.flush();
                                System.out.println("MISS");
                            } else {
                                out.write("UNKNOWN\n");
                                out.flush();
                            }
                            out.write(game.getBoard(playerId).getOpponentBoardToString() + '\n');
                            out.flush();
                        } else {
                            out.write("UNVALID\n");
                            out.flush();
                        }


                        game.getLock().notifyAll();
                    } else {
                        game.getLock().wait();
                    }
                }
            }




            System.out.println("[Server " +  "] closing connection");
        } catch (IOException e) {
            System.out.println("[Server " +  "] exception: " + e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
