package server.model;

import java.io.Serializable;

public class Move implements Serializable {

    private String gameID;
    private String username;

    private final int sourceRow;
    private final int sourceCol;
    private final int targetRow;
    private final int targetCol;

    private boolean legalMove;

    public Move(int sourceRow, int sourceCol, int targetRow, int targetCol) {
        this.sourceRow = sourceRow;
        this.sourceCol = sourceCol;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
    }

    public void setLegalMove(boolean bol) {
        this.legalMove = bol;
    }

    public boolean isLegalMove() {
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
