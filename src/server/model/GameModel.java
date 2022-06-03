package server.model;

import java.util.HashMap;

//server.model class containing what server.controller may need
public class GameModel {
    private Player[] players;
    private int turn;
    private GameMap map;
    private GameTimer gameTimer;
    private ChessPiece chessPiece;
    private GameState gameState;
    private HashMap<Integer,ChessPieceAbstract> chesspieces = new HashMap<Integer,ChessPieceAbstract>();

    public GameModel() {
        gameState = new GameState();
        map = new GameMap(8);
    }

    public Player[] getPlayers() {
        return players;
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
    public void setGameTimer(GameTimer gameTimer) {
        this.gameTimer = gameTimer;
    }
    public HashMap<Integer, ChessPieceAbstract> getChesspieces() {
        return chesspieces;
    }
    public void setChesspieces(HashMap<Integer, ChessPieceAbstract> chesspieces) {
        this.chesspieces = chesspieces;
    }
    public ChessPiece getChessPiece() {
        return chessPiece;
    }
    public GameMap getMap() {
        return map;
    }
    public void setChessPiece(ChessPiece chessPiece) {
        this.chessPiece = chessPiece;
    }
    public void setMap(GameMap map) {
        this.map = map;
    }
    public GameState getGameState() {
        return gameState;
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
