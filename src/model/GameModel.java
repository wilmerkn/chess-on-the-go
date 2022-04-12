package model;

import java.util.HashMap;

//model class containing what controller may need
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
        chesspieces.put(2, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.TOWER)); //white Tower
        chesspieces.put(3, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.RUNNER)); //white Runner
        chesspieces.put(4, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.BISHOP)); //white Bishop
        chesspieces.put(5, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.QUEEN)); //white Queen
        chesspieces.put(6, new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.KING)); //white King
        chesspieces.put(7, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.PAWN)); //black Pawn
        chesspieces.put(8, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.TOWER)); //black Tower
        chesspieces.put(9, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.RUNNER)); //black Runner
        chesspieces.put(10, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.BISHOP)); //black Bishop
        chesspieces.put(11, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.QUEEN)); //black Queen
        chesspieces.put(12, new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.KING)); //black King
    }

    public void initializeMap(){
        ChessPieceAbstract[][] gamemap = map.getMap();

        //load black chesspieces
        gamemap[0][0] = chesspieces.get(8);
        gamemap[0][1] = chesspieces.get(9);
        gamemap[0][2] = chesspieces.get(10);
        gamemap[0][3] = chesspieces.get(11);
        gamemap[0][4] = chesspieces.get(12);
        gamemap[0][5] = chesspieces.get(10);
        gamemap[0][6] = chesspieces.get(9);
        gamemap[0][7] = chesspieces.get(8);

        //loads black Pawns
        int row = 1;
        for(int col = 0; col < map.getMapDimension(); col++){
            gamemap[row][col] = chesspieces.get(7);
        }

        //load white Pawns
        row = 6;
        for(int col = 0; col < map.getMapDimension(); col++){
            gamemap[row][col] = chesspieces.get(1);
        }

        //load white chesspieces
        gamemap[7][0] = chesspieces.get(2);
        gamemap[7][1] = chesspieces.get(3);
        gamemap[7][2] = chesspieces.get(4);
        gamemap[7][3] = chesspieces.get(5);
        gamemap[7][4] = chesspieces.get(6);
        gamemap[7][5] = chesspieces.get(4);
        gamemap[7][6] = chesspieces.get(3);
        gamemap[7][7] = chesspieces.get(2);
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
