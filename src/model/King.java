package model;

import javax.swing.*;

public class King extends ChessPiece{

    private Icon icon; //used to store the chesspiece sprite
    private ChessPieceType chessPieceType; //enum for type
    private int[] moveset; //check best way to go about it
    private boolean alive;

    public King(){
        this.alive = true;
        this.chessPieceType = ChessPieceType.KING;
    }

    public int[] getMoveset() {
        return moveset;
    }

    public Icon getIcon() {
        return icon;
    }

    public ChessPieceType getChessPieceType() {
        return chessPieceType;
    }

    public void setMoveset(int[] moveset) {
        this.moveset = moveset;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public void setChessPieceType(ChessPieceType chessPieceType) {
        this.chessPieceType = chessPieceType;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public void attack() {

    }

    @Override
    public void move() {

    }

    @Override
    public String toString() {
        String outText = String.format("PieceType: " + "%s" + ", piece status: " + "%s", chessPieceType,alive);
        return outText;
    }
}