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
            required = true)
    protected String host;

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: ${DEFAULT-VALUE}).",
            defaultValue = "6433")
    protected int port;

    @Override
    public Integer call() throws IOException {
        int playerId;
        Socket socket = new Socket("127.0.0.1", port);

        try(BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter out =
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))){

            // Create board via CLI
            // Pass board via message
            out.write("START\n");
            out.flush();

            while (!socket.isClosed()) {
                String input = in.readLine();
                if(input.contains("PLAYERBOARD=")){
                    String board = input.substring(6);
                    System.out.println("Your board : " + board);


                } else if(input.contains("YOUR_TURN")){
                    System.out.println("It's your Turn ! ");
                    Scanner myObj = new Scanner(System.in);
                    System.out.println("Enter your target [1 - 10] : ");
                    String target = myObj.nextLine();  // Read user input
                    int val = Integer.parseInt(target);
                    out.write("ATTACK "+(val - 1)+ "\n");
                    out.flush();

                } else if(input.contains("ATTACKRESULT=")){
                    String res = input.substring(13);
                    System.out.println("Shot "+ res);
                }else if(input.contains("OPPONENTBOARD=")){
                    String opponentBoard = input.substring(14);
                    System.out.println("Opponent Board : "+ opponentBoard);
                }else if(input.contains("GAME_READY")){
                    System.out.println("Game starting !");
                }
                else {
                    System.out.println("UNKNOWN WORD : " + input);
                }
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }


        return 0;
    }
}