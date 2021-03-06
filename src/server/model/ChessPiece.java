package server.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class ChessPiece extends ChessPieceAbstract implements Serializable {
    private ChessPieceType chessPieceType; //enum for type
    private ChessPieceColor color; //color of a chesspiece (white/black)
    private int[][] moveset; //check best way to go about it
    private int[] attackMoveset;
    private boolean alive;
    private int moved;
    private String spriteName;

    //constructor to give chesspiece color
    public ChessPiece(ChessPieceColor color, ChessPieceType chessPieceType, String spriteName){
        this.alive = true;
        this.moved = 0;
        this.chessPieceType = chessPieceType;
        this.color = color;
        this.spriteName = spriteName;
    }

    //getters-setters
    public int[] getAttackMoveset() {
        return attackMoveset;
    }
    public void setAttackMoveset(int[] attackMoveset) {
        this.attackMoveset = attackMoveset;
    }
    public int[][] getMoveset() {
        return moveset;
    }
    public ChessPieceType getChessPieceType() {
        return chessPieceType;
    }
    public void setMoveset(int[][] moveset) {
        this.moveset = moveset;
    }
    public void setChessPieceType(ChessPieceType chessPieceType) {
        this.chessPieceType = chessPieceType;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public String getSpriteName() {
        return spriteName;
    }
    public ChessPieceColor getColor() {
        return color;
    }
    public void setColor(ChessPieceColor color) {
        this.color = color;
    }
    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName;
    }
    public int getMoved() {
        return moved;
    }
    public void setMoved(int moved) {
        this.moved = moved;
    }

    //to-string
    @Override
    public String toString() {
        String outText = String.format("%s" + " color: " + "%s", chessPieceType,color);
        return outText;
    }
}
