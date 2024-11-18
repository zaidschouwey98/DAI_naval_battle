package ch.heigvd.dai.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class Game {
    private LinkedList<Integer> players;
    private int playerTurn;
    private Map<Integer,BoardHandler> boards = new HashMap<Integer,BoardHandler>();
    private final Object lock = new Object(); // Objet de synchronisation pour la gestion des tours

    private boolean gameOver = false;
    public Game() {
        players = new LinkedList<>();
    }

    public int addPlayer(int playerId, BoardHandler boardHandler) {
        if(players.size()>=2){
            return -1;
        }
        this.players.push(playerId);

        this.boards.put(playerId, boardHandler);

        if(players.size() == 2){
            Random rand = new Random();
            this.playerTurn = players.get(rand.nextInt(2));
        }
        return 0;
    }

    public FireResult shoot(int target){

        int opponentId = -1;
        if(playerTurn == players.get(0)) {
            opponentId = players.get(1);
        } else if(playerTurn == players.get(1)) {
            opponentId = players.get(0);
        }
        playerTurn = opponentId;

        return boards.get(opponentId).receiveFire(target);
    }

    public int getPlayer(){
        return players.size();
    }

    public boolean gameOver(){
        return gameOver;
    }

    public int getPlayerTurn(){
        return playerTurn;
    }
    public BoardHandler getBoard(int playerId){
        return boards.get(playerId);
    }

    public Object getLock() {
        return lock; // Retourne l'objet de synchronisation
    }

}
