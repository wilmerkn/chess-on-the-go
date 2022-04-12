package model;

//model class containing what controller may need
public class GameModel {
    private ChessPieceAbstract[] chessPieces; //use hashmap instead
    private Player[] players;
    private int turn;

    private GameMap map;

    private GameTimer gameTimer;

    private ChessPiece chessPiece;

    public GameModel(){
        gameTimer = new GameTimer();
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

    public ChessPieceAbstract[] getChessPieces() {
        return chessPieces;
    }

    public void setChessPieces(ChessPieceAbstract[] chessPieces) {
        this.chessPieces = chessPieces;
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
