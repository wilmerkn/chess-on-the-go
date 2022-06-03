package server.model;

public class GameMap {
    private ChessPieceAbstract[][] map;
    private final String[][] mapRep;
    private final int mapDimension;

    public GameMap(int mapDimension){
        this.mapDimension = mapDimension;
        this.mapRep = new String[mapDimension][mapDimension];
        this.map = new ChessPiece[mapDimension][mapDimension];
    }

    //displays string based map and showing the objects stored on the positions (Updates when run)
    public void displayMap(){
        System.out.println("-----------------------------------------------------------");
        for(int row = 0; row < mapDimension; row++){
            for(int col = 0; col < mapDimension; col++){
                System.out.print("[" +map[row][col]+ "]" + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------");
    }

    //displays string based map and showing chosen strings on positions (Updates when run)
    public void displayLogicalMap(){
        System.out.println("-----------------------------------------------------------");
        for(int row = 0; row < mapDimension; row++){
            for(int col = 0; col < mapDimension; col++){
                System.out.print("[" +mapRep[row][col]+ "]" + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------");
    }

    public ChessPieceAbstract[][] getMap() {
        return map;
    }

    public String[][] getMapRep() {
        return mapRep;
    }

    public void setMap(ChessPieceAbstract[][] map) {
        this.map = map;
    }

    public int getMapDimension() {
        return mapDimension;
    }

}
