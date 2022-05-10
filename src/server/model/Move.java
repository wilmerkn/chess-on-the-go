package server.model;

public class Move {
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
}
