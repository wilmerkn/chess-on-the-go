package model;

public class GameMap {
    private ChessPiece[][] map;
    private String[][] mapRep;
    private int mapDimension;


    public GameMap(ChessPiece[][] map, String[][] mapRep, int mapDimension){
        this.mapDimension = mapDimension;
        this.mapRep = mapRep;
        this.map = map;
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
