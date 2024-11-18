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
                if(input.contains("BOARD=")){
                    String board = input.substring(6);
                    System.out.println(board);


                } else if(input.contains("YOUR_TURN")){
                    System.out.println("It's your Turn ! ");

                    Scanner myObj = new Scanner(System.in);  // Create a Scanner object
                    System.out.println("Enter your target [0 - 10] : ");
                    String target = myObj.nextLine();  // Read user input

                    out.write("ATTACK "+target+ "\n");
                    out.flush();
                    String res = in.readLine();
                    System.out.println("Shot "+ res);
                    String opponentBoard = in.readLine();
                    System.out.println("Board : "+ opponentBoard);
                } else if(input.equals("GAME_READY")){
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