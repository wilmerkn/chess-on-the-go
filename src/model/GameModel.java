package model;

public class GameModel {
    private ChessPieceBluePrint[] chessPieces; //use hashmap instead
    private Player[] players;
    private int turn;

    private GameTimer gameTimer;

    public GameModel(){
        gameTimer = new GameTimer();
    }

    public Player[] getPlayers() {
        return players;
    }

    public ChessPieceBluePrint[] getChessPieces() {
        return chessPieces;
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public void setChessPieces(ChessPieceBluePrint[] chessPieces) {
        this.chessPieces = chessPieces;
    }

    public void setGameTimer(GameTimer gameTimer) {
        this.gameTimer = gameTimer;
    }
}
