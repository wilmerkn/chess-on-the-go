package server.model;

import client.board.BoardView;
import server.controller.GameLogic;

import java.util.HashMap;

//server.model class containing what server.controller may need
public class GameModel {
    private Player[] players;
    private int turn;

    private GameMap map;

    private GameTimer gameTimer;

    private ChessPiece chessPiece;

    private BoardView boardView;

    private HashMap<Integer,ChessPieceAbstract> chesspieces = new HashMap<Integer,ChessPieceAbstract>();

    public GameModel(GameLogic gameLogic){
        gameTimer = new GameTimer();
        boardView = new BoardView(gameLogic); // model should not have view
        map = new GameMap(8);

        chesspieces.put(1, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.PAWN, "WP")); //white Pawn
        chesspieces.put(2, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.ROOK, "WR")); //white Tower
        chesspieces.put(3, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.KNIGHT, "WN")); //white Runner
        chesspieces.put(4, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.BISHOP, "WB")); //white Bishop
        chesspieces.put(5, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.QUEEN, "WQ")); //white Queen
        chesspieces.put(6, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.KING, "WK")); //white King
        chesspieces.put(7, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.PAWN, "BP")); //black Pawn
        chesspieces.put(8, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.ROOK, "BR")); //black Tower
        chesspieces.put(9, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.KNIGHT, "BN")); //black Runner
        chesspieces.put(10, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.BISHOP, "BB")); //black Bishop
        chesspieces.put(11, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.QUEEN, "BQ")); //black Queen
        chesspieces.put(12, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.KING, "BK")); //black King
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

    public BoardView getBoardView() {
        return boardView;
    }

    public void setBoardView(BoardView boardView) {
        this.boardView = boardView;
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
