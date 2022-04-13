package model;

//abstract class inherited by chesspieces
public abstract class ChessPiece implements ChessPieceBluePrint{

    private boolean isDead;

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    @Override
    public void attack() {
    //attack enemy player
    }

    @Override
    public void move() {
    //move a piece
    }
}
