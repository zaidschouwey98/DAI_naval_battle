package ch.heigvd.dai.commands;

import java.util.Random;

public class BoardHandler {
    private Board[] board;
    private Board[] opponentBoard;

    public BoardHandler() {
        this.board = new Board[10];
        this.opponentBoard = new Board[10];
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            this.board[i] = rand.nextInt(2) == 0 ? Board.O : Board.B;
        }
        for (int i = 0; i < 10; i++) {
            this.opponentBoard[i] = Board.V;
        }
    }
    public BoardHandler(Board[] board) {
        this.board = board;
    }

    public String toString(){
        String str = "";
        str +='[';
        for(Board c : board){
            str += c + ",";
        }
        str+=']';
        return str;
    }

    public FireResult receiveFire(int index){
        if(index > board.length){
            throw new IndexOutOfBoundsException();
        }


        FireResult res = switch (board[index]) {
            case B -> FireResult.H;
            case O -> FireResult.M;
            default -> FireResult.UNKOWN;
        };
        if(this.board[index] == Board.B){
            this.board[index] = Board.X;
        }
        if(this.board[index] == Board.O){
            this.board[index] = Board.R;
        }
        return res;
    }

    public void setOpponentBoardCase(int index, FireResult result){
        if(index > board.length){
            throw new IndexOutOfBoundsException();
        }
        switch (result){
            case H -> this.opponentBoard[index] = Board.O;
            case M -> this.opponentBoard[index] = Board.X;
        }


    }
    public String getOpponentBoardToString() {
        String str = "";
        str +='[';
        for(Board c : opponentBoard){
            str += c + ",";
        }
        str+=']';
        return str;
    }


}
