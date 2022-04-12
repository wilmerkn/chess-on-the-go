package model;

public class GameMap {
    private ChessPiece[][] map;
    private String[][] mapRep;
    private int mapDimension;


    public GameMap(int mapDimension){
        this.mapDimension = mapDimension;
        this.mapRep = new String[mapDimension][mapDimension];
        this.map = new ChessPiece[mapDimension][mapDimension];
    }

    public void displayMap(){

    }

    public void displayRepMap(){

    }

    public ChessPiece[][] getMap() {
        return map;
    }

    public String[][] getMapRep() {
        return mapRep;
    }

    public int getMapDimension() {
        return mapDimension;
    }

}
