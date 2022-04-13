package model;

import javax.swing.*;

public class ChessPiece extends ChessPieceAbstract {
    private Icon icon; //used to store the chesspiece sprite
    private ChessPieceType chessPieceType; //enum for type
    private ChessPieceColor color; //color of a chesspiece (white/black)
    private int[] moveset; //check best way to go about it
    private int[] attackMoveset;
    private boolean alive;

    //constructor color will be null
    public ChessPiece(){
        this.alive = true;
        this.chessPieceType = ChessPieceType.BISHOP;
        this.color = ChessPieceColor.WHITE;
    }

    //constructor to give chesspiece color
    public ChessPiece(ChessPieceColor color, ChessPieceType chessPieceType){
        this.alive = true;
        this.chessPieceType = chessPieceType;
        this.color = color;
    }

    //getters-setters
    public int[] getAttackMoveset() {
        return attackMoveset;
    }

    public void setAttackMoveset(int[] attackMoveset) {
        this.attackMoveset = attackMoveset;
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

    //placeholdermethods
    @Override
    public void attack() {

    }

    @Override
    public void move() {

    }

    //to-string
    @Override
    public String toString() {
        String outText = String.format("%s" + " color: " + "%s", chessPieceType,color);
        return outText;
    }
}
