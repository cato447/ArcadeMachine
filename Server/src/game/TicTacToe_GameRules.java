package game;

import ai.MinMax;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class TicTacToe_GameRules {

    private String gameState;
    private Integer[][] board;
    private MinMax ai;
    Point startWin, endWin;

    public TicTacToe_GameRules(){
        gameState = "---------";
        ai = new MinMax();
    }

    public void resetGameState() {
        this.gameState = "---------";
    }

    public String getGameState() {
        return gameState;
    }

    private void makeMoveOnBoard(int index, int player){
        byte[] gameStateBytes = gameState.getBytes();
        if (player == 0){
            gameStateBytes[index] = (byte) 'x';
        } else if (player == 1) {
            gameStateBytes[index] = (byte) 'o';
        }
        gameState = new String(gameStateBytes, StandardCharsets.UTF_8);
        board = this.convertToNumbers(gameState);
    }

    public boolean makeClientMove(String input, int id) {
        int column = 0;
        int row = 0;
        if (input.contains(",")){
            column = Integer.valueOf(input.split("\\|")[0].split(",")[0]) / 300;
            row = Integer.valueOf(input.split("\\|")[1].split(",")[0]) / 300;
        } else {
            column = (int) Double.valueOf(input.split("\\|")[0]).doubleValue() / 300;
            row = (int) Double.valueOf(input.split("\\|")[1]).doubleValue() / 300;
        }
        int index = column * 3 + row;

        if (isLegalMove(column, row)) {
            makeMoveOnBoard(index, id);
            return true;
        } else {
            return false;
        }
    }

    public void makeComputerMove(){
        int index = ai.findBestMove(ai.convertBoard(this.getGameState()));
        makeMoveOnBoard(index, 1);
    }

    private LinkedList<Integer> getEmptySpaces(){
        LinkedList<Integer> emptySpaces = new LinkedList<>();
        for (int column = 0; column < 3; column++){
            for (int row = 0; row < 3; row++){
                if (isLegalMove(column, row)){
                    emptySpaces.add(column * 3 + row);
                }
            }
        }
        return emptySpaces;
    }

    private boolean isLegalMove(int column, int row){
        int index = column * 3 + row;
        return gameState.charAt(index) == '-';
    }

    private Integer[][] convertToNumbers(String gameState){
        Integer[][] board = new Integer[3][3];
        for (int i = 0; i < 3; i++){
            for (int k = 0; k < 3; k++){
                char currentChar = gameState.charAt(i*3+k);
                if (currentChar == 'x'){
                    board[i][k] = 1;
                } else if (currentChar == 'o'){
                    board[i][k] = -1;
                } else if (currentChar == '-'){
                    board[i][k] = 0;
                }
            }
        }
        return board;
    }

    private boolean diagonalWin(){
        int sumLeftUp = 0;
        int sumLeftDown = 0;
        for (int i = 0; i < 3; i++){
            sumLeftUp += board[i][i];
            sumLeftDown += board[i][2-i];
        }
        if (sumLeftDown == 3 || sumLeftDown == -3){
            startWin = new Point(2,0);
            endWin = new Point(0,2);
            return true;
        } else if (sumLeftUp == 3 || sumLeftUp == -3){
            startWin = new Point(0,0);
            endWin = new Point(2,2);
            return true;
        } else {
            return false;
        }
    }

    private boolean horizontalWin(){
        for (int row = 0; row < 3; row++){
            int sum = 0;
            for (int column = 0; column < 3; column++){
                sum += board[row][column];
            }
            if (sum == 3 || sum == -3){
                startWin = new Point(row, 0);
                endWin = new Point(row, 0);
                return true;
            }
        }
        return false;
    }

    private boolean verticalWin(){
        for (int column = 0; column < 3; column++){
            int sum = 0;
            for (int row = 0; row < 3; row++){
                sum += board[row][column];
            }
            if (sum == 3 || sum == -3){
                startWin = new Point(0, column);
                endWin = new Point(2, column);
                return true;                        //187
            } 
        }
        return false;
    }

    public boolean gameEnded(){
        return horizontalWin() || verticalWin() || diagonalWin() || this.gameState.matches("([xo]){9}");
    }

    public Point[] getWinCoordinates(){
        return new Point[]{startWin, endWin};
    }

}
