package model;

public class GameMap {
    private ChessPieceAbstract[][] map;
    private String[][] mapRep;
    private int mapDimension;


    public GameMap(int mapDimension){
        this.mapDimension = mapDimension;
        this.mapRep = new String[mapDimension][mapDimension];
        this.map = new ChessPieceAbstract[mapDimension][mapDimension];
    }

    public void displayMap(){

    }

    public void displayRepMap(){

    }

    public ChessPieceAbstract[][] getMap() {
        return map;
    }

    public String[][] getMapRep() {
        return mapRep;
    }

    public int getMapDimension() {
        return mapDimension;
    }

}
