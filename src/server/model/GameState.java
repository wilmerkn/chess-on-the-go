package server.model;

public class GameState {

    private int playerTurn;
    boolean checkMate;

    public GameState(){
        playerTurn = 1;
        checkMate = false;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setCheckMate(boolean checkMate) {
        this.checkMate = checkMate;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }
}
