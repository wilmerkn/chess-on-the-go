package server.model;

import java.io.Serializable;

public class Move implements Serializable {

    private String gameID;
    private String username;

    private int sourceRow;
    private int sourceCol;
    private int targetRow;
    private int targetCol;

    private boolean legalMove;

    public Move(int sourceRow, int sourceCol, int targetRow, int targetCol) {
        this.sourceRow = sourceRow;
        this.sourceCol = sourceCol;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
    }

    private void setLegalMove(boolean bol) {
        this.legalMove = bol;
    }

    private boolean isLegalMove() {
        return legalMove;
    }

    public int getSourceRow() {
        return sourceRow;
    }

    public int getSourceCol() {
        return sourceCol;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public int getTargetCol() {
        return targetCol;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
