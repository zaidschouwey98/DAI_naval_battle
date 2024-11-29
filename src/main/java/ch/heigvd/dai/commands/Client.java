package ch.heigvd.dai.commands;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import java.util.Scanner;

@CommandLine.Command(name = "client", description = "Start the client part of the network game.")
public class Client implements Callable<Integer> {
    @CommandLine.Option(
            names = {"-H", "--host"},
            description = "Host to connect to.",
            defaultValue = "127.0.0.1",
            required = true)
    protected String host;

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: ${DEFAULT-VALUE}).",
            defaultValue = "6433")
    protected int port;

    static final String END_LINE = "\r\n";
    @Override
    public Integer call() throws IOException {
        try(Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                                                                         StandardCharsets.UTF_8));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
                                                                           StandardCharsets.UTF_8));
            Scanner scanner = new Scanner(System.in)){ // Création de l'objet Scanner
                while (!socket.isClosed()) {
                    String command = in.readLine(); // Bloquant jusqu'à recevoir commande du serveur
                    String[] parsed_command = command.split("=");
                    switch (parsed_command[0]) {
                        case "GAMEREADY":
                            System.out.println("Game is starting...");
                            break;
                        case "INIT_GRID" : // Initialize player board by placing 3 boats
                            System.out.println("Place your 3 boats [1 - 16]");
                            String[] boats = new String[3];
                            for(int i = 0; i < 3; i++){
                                String move;
                                while(true){
                                    System.out.print("Enter boat " + i + ":");
                                    move = scanner.nextLine();
                                    for(String s : boats){
                                        if(move.equals(s)){
                                            System.out.println("Error : the cell is already taken !");
                                        }
                                    }
                                    break;
                                }
                                boats[i] = move;
                            }
                            out.write("INIT_GRID=" + boats[0] + " " + boats[1] + " " + boats[2] + END_LINE);
                            out.flush();
                            break;
                        case "ATTACK":
                            System.out.println("Entrez la case à attaquer :"); // Affiche un message
                            String move = scanner.nextLine();
                            out.write("ATTACK=" + move + END_LINE);
                            out.flush();
                            break;
                        case "YOURBOARD":
                            System.out.println(parsed_command[1]);
                            break;
                        case "OPPONENTBOARD":
                            System.out.println(parsed_command[1]);
                            break;
                        case "HIT":
                            System.out.println("HIT !");
                            break;
                        case null:
                        case "WAIT":
                            System.out.println("Attendez le tour de l'adversaire");
                            break;
                        case "END" :
                            socket.close();
                            break;
                        default: //ERROR
                            String code = parsed_command[1];
                            switch(code){
                                case "1":
                                    System.out.println("You already attacked this cell !");
                                    break;
                                case "2":
                                    System.out.println("Erreur 2");
                                    break;
                                case "3":
                                    System.out.println("Erreur 3");
                                    break;
                                default:
                                    break;
                            }
                    }
                }
        } catch(IOException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Bye Bye");
        return 0;
    }
}