package server.model;

import java.util.HashMap;

//server.model class containing what server.controller may need
public class GameModel {
    private Player[] players;
    private int turn;

    private GameMap map;

    private GameTimer gameTimer;

    private ChessPiece chessPiece;

    private HashMap<Integer,ChessPieceAbstract> chesspieces = new HashMap<Integer,ChessPieceAbstract>();

    public GameModel(){
        gameTimer = new GameTimer();
        map = new GameMap(8);

        chesspieces.put(1, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.PAWN)); //white Pawn
        chesspieces.put(2, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.ROOK)); //white Tower
        chesspieces.put(3, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.KNIGHT)); //white Runner
        chesspieces.put(4, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.BISHOP)); //white Bishop
        chesspieces.put(5, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.QUEEN)); //white Queen
        chesspieces.put(6, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.KING)); //white King
        chesspieces.put(7, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.PAWN)); //black Pawn
        chesspieces.put(8, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.ROOK)); //black Tower
        chesspieces.put(9, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.KNIGHT)); //black Runner
        chesspieces.put(10, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.BISHOP)); //black Bishop
        chesspieces.put(11, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.QUEEN)); //black Queen
        chesspieces.put(12, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.KING)); //black King
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
}
