package server.controller;

import client.board.BoardView;
import client.gameview.GameView;
import client.gameview.BoardPanel;
import client.gameview.GameView;
import server.model.*;

import javax.swing.*;
import java.util.HashMap;

//functionality: Map generation, ingame timer (count up).

//todo way to get every moveset from chesspieces for valid move checks

public class GameLogic {


    private GameModel model;
    private GameView view;

    public GameLogic() {
        this.view = new GameView(this);
        this.model = new GameModel();


        //all game code runs here
        model.setMap(new GameMap(8));

        initializeMap();
        //debugChesspieces();
        //inverseMapArray();

        drawMap();
    }

    public void initializeMap(){
        int mapDim = model.getMap().getMapDimension();
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        HashMap<Integer, ChessPieceAbstract> chesspieces = model.getChesspieces();
        //load black chesspieces
        gamemap[0][0] = chesspieces.get(8);
        gamemap[0][1] = chesspieces.get(9);
        gamemap[0][2] = chesspieces.get(10);
        gamemap[0][3] = chesspieces.get(11);
        gamemap[0][4] = chesspieces.get(12);
        gamemap[0][5] = chesspieces.get(10);
        gamemap[0][6] = chesspieces.get(9);
        gamemap[0][7] = chesspieces.get(8);

        //loads black Pawns
        int row = 1;
        for(int col = 0; col < mapDim; col++){
            gamemap[row][col] = chesspieces.get(7);
        }

        //load white Pawns
        row = 6;
        for(int col = 0; col < mapDim; col++){
            gamemap[row][col] = chesspieces.get(1);
        }

        //load white chesspieces
        gamemap[7][0] = chesspieces.get(2);
        gamemap[7][1] = chesspieces.get(3);
        gamemap[7][2] = chesspieces.get(4);
        gamemap[7][3] = chesspieces.get(5);
        gamemap[7][4] = chesspieces.get(6);
        gamemap[7][5] = chesspieces.get(4);
        gamemap[7][6] = chesspieces.get(3);
        gamemap[7][7] = chesspieces.get(2);

        //todo iterate all chess pieces and add movesets

        for(int r = 0; r < gamemap.length; r++){
            for(int c = 0; c < gamemap[r].length;c++){
                if(gamemap[r][c] != null){
                    ChessPiece chessPiece = (ChessPiece) gamemap[r][c];
                    ChessPieceType type = chessPiece.getChessPieceType();

                    if(type.equals(ChessPieceType.KING)){
                        chessPiece.setMoveset(new int[][]{
                                {1,1,1},
                                {1,0,1},
                                {1,1,1}});
                    }
                    else if(type.equals(ChessPieceType.QUEEN)){
                        chessPiece.setMoveset(new int[][]{
                                {1,2,2,2,2,2,2,1,2,2,2,2,2,2,1},
                                {2,1,2,2,2,2,2,1,2,2,2,2,2,1,2},
                                {2,2,1,2,2,2,2,1,2,2,2,2,1,2,2},
                                {2,2,2,1,2,2,2,1,2,2,2,1,2,2,2},
                                {2,2,2,2,1,2,2,1,2,2,1,2,2,2,2},
                                {2,2,2,2,2,1,2,1,2,1,2,2,2,2,2},
                                {2,2,2,2,2,2,1,1,1,2,2,2,2,2,2},
                                {1,1,1,1,1,1,1,0,1,1,1,1,1,1,1},
                                {2,2,2,2,2,2,1,1,1,2,2,2,2,2,2},
                                {2,2,2,2,2,1,2,1,2,1,2,2,2,2,2},
                                {2,2,2,2,1,2,2,1,2,2,1,2,2,2,2},
                                {2,2,2,1,2,2,2,1,2,2,2,1,2,2,2},
                                {2,2,1,2,2,2,2,1,2,2,2,2,1,2,2},
                                {2,1,2,2,2,2,2,1,2,2,2,2,2,1,2},
                                {1,2,2,2,2,2,2,1,2,2,2,2,2,2,1}});
                    }
                    else if(type.equals(ChessPieceType.KNIGHT)){
                        chessPiece.setMoveset(new int[][]{
                                {2,1,2,1,2},
                                {1,2,2,2,1},
                                {2,2,0,2,2},
                                {1,2,2,2,1},
                                {2,1,2,1,2}});
                    }
                    else if(type.equals(ChessPieceType.BISHOP)){
                        chessPiece.setMoveset(new int[][]{
                                {1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
                                {2,1,2,2,2,2,2,2,2,2,2,2,2,1,2},
                                {2,2,1,2,2,2,2,2,2,2,2,2,1,2,2},
                                {2,2,2,1,2,2,2,2,2,2,2,1,2,2,2},
                                {2,2,2,2,1,2,2,2,2,2,1,2,2,2,2},
                                {2,2,2,2,2,1,2,2,2,1,2,2,2,2,2},
                                {2,2,2,2,2,2,1,2,1,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,0,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,1,2,1,2,2,2,2,2,2},
                                {2,2,2,2,2,1,2,2,2,1,2,2,2,2,2},
                                {2,2,2,2,1,2,2,2,2,2,1,2,2,2,2},
                                {2,2,2,1,2,2,2,2,2,2,2,1,2,2,2},
                                {2,2,1,2,2,2,2,2,2,2,2,2,1,2,2},
                                {2,1,2,2,2,2,2,2,2,2,2,2,2,1,2},
                                {1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
                        });
                    }
                    else if(type.equals(ChessPieceType.ROOK)){
                        chessPiece.setMoveset(new int[][]{
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {1,1,1,1,1,1,1,0,1,1,1,1,1,1,1},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                        });
                    }
                    else if(type.equals(ChessPieceType.PAWN)){
                        if(chessPiece.getColor() == ChessPieceColor.WHITE){
                            chessPiece.setMoveset(new int[][]{
                                    {2,1,2},
                                    {2,0,2},
                            });
                        }
                        else{
                            chessPiece.setMoveset(new int[][]{
                                    {2,0,2},
                                    {2,1,2},
                            });
                        }

                    }

                }
            }
        }
    }

    //draws player two map upside down
    /*
    public void drawPlayerTwoMap(){
        int mapDim = model.getMap().getMapDimension();
        HashMap<String, JLabel> notationLbl = model.getBoardView().getNotationToJLMap();
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        BoardView.SquarePanel[][] squarePanel = model.getBoardView().getBoardPanel().getSquares();
        GameMap map = model.getMap();

        for(int row = 0; row < mapDim; row++){
            for(int col = 0; col < mapDim; col++){
                if(gamemap[row][col] != null){
                    ChessPiece chessPiece = (ChessPiece) gamemap[row][col];
                    squarePanel[(mapDim-1)-row][(mapDim-1)-col].placePiece(notationLbl.get(chessPiece.getSpriteName()));
                }
            }
        }
        map.displayMap();
    }*/

    //inverses map array
    public void inverseMapArray(){
        int mapDim = model.getMap().getMapDimension();
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        ChessPieceAbstract[][] tempArray = new ChessPieceAbstract[mapDim][mapDim];

        for (int row = mapDim - 1; row >= 0; row--) {
            for (int col = mapDim - 1; col >= 0; col--)
                tempArray[(mapDim-1)-row][(mapDim-1)-col] = gamemap[row][col];
        }
        model.getMap().setMap(tempArray);
    }


    //todo change so it doesnt draw picture on picture
    //draws player-one game map
    public void drawMap(){
        int mapDim = model.getMap().getMapDimension();
        HashMap<String, JLabel> notationLbl = view.getBoardPanel().getNotationToJLMap(); //todo get board panel
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        BoardPanel.SquarePanel[][] squarePanel = view.getBoardPanel().getSquares();
        GameMap map = model.getMap();

        for(int row = 0; row < mapDim; row++){
            for(int col = 0; col < mapDim; col++){
                if(gamemap[row][col] != null){
                    ChessPiece chessPiece = (ChessPiece) gamemap[row][col];
                    squarePanel[row][col].placePiece(notationLbl.get(chessPiece.getSpriteName()));
                }
            }
        }
        map.displayMap();
    }

    public void debugChesspieces(){
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        HashMap<Integer, ChessPieceAbstract> chesspieces = model.getChesspieces();

        gamemap[4][4] = chesspieces.get(7);
        //9 is knight

        for(int r = 0; r < gamemap.length; r++){
            for(int c = 0; c < gamemap[r].length;c++){
                if(gamemap[r][c] != null){
                    ChessPiece chessPiece = (ChessPiece) gamemap[r][c];
                    ChessPieceType type = chessPiece.getChessPieceType();

                    if(type.equals(ChessPieceType.KING)){
                        chessPiece.setMoveset(new int[][]{
                                {1,1,1},
                                {1,0,1},
                                {1,1,1}});
                    }
                    else if(type.equals(ChessPieceType.QUEEN)){
                        chessPiece.setMoveset(new int[][]{
                                {1,2,2,2,2,2,2,1,2,2,2,2,2,2,1},
                                {2,1,2,2,2,2,2,1,2,2,2,2,2,1,2},
                                {2,2,1,2,2,2,2,1,2,2,2,2,1,2,2},
                                {2,2,2,1,2,2,2,1,2,2,2,1,2,2,2},
                                {2,2,2,2,1,2,2,1,2,2,1,2,2,2,2},
                                {2,2,2,2,2,1,2,1,2,1,2,2,2,2,2},
                                {2,2,2,2,2,2,1,1,1,2,2,2,2,2,2},
                                {1,1,1,1,1,1,1,0,1,1,1,1,1,1,1},
                                {2,2,2,2,2,2,1,1,1,2,2,2,2,2,2},
                                {2,2,2,2,2,1,2,1,2,1,2,2,2,2,2},
                                {2,2,2,2,1,2,2,1,2,2,1,2,2,2,2},
                                {2,2,2,1,2,2,2,1,2,2,2,1,2,2,2},
                                {2,2,1,2,2,2,2,1,2,2,2,2,1,2,2},
                                {2,1,2,2,2,2,2,1,2,2,2,2,2,1,2},
                                {1,2,2,2,2,2,2,1,2,2,2,2,2,2,1}});
                    }
                    else if(type.equals(ChessPieceType.KNIGHT)){
                        chessPiece.setMoveset(new int[][]{
                                {2,1,2,1,2},
                                {1,2,2,2,1},
                                {2,2,0,2,2},
                                {1,2,2,2,1},
                                {2,1,2,1,2}});
                    }
                    else if(type.equals(ChessPieceType.BISHOP)){
                        chessPiece.setMoveset(new int[][]{
                                {1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
                                {2,1,2,2,2,2,2,2,2,2,2,2,2,1,2},
                                {2,2,1,2,2,2,2,2,2,2,2,2,1,2,2},
                                {2,2,2,1,2,2,2,2,2,2,2,1,2,2,2},
                                {2,2,2,2,1,2,2,2,2,2,1,2,2,2,2},
                                {2,2,2,2,2,1,2,2,2,1,2,2,2,2,2},
                                {2,2,2,2,2,2,1,2,1,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,0,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,1,2,1,2,2,2,2,2,2},
                                {2,2,2,2,2,1,2,2,2,1,2,2,2,2,2},
                                {2,2,2,2,1,2,2,2,2,2,1,2,2,2,2},
                                {2,2,2,1,2,2,2,2,2,2,2,1,2,2,2},
                                {2,2,1,2,2,2,2,2,2,2,2,2,1,2,2},
                                {2,1,2,2,2,2,2,2,2,2,2,2,2,1,2},
                                {1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
                        });
                    }
                    else if(type.equals(ChessPieceType.ROOK)){
                        chessPiece.setMoveset(new int[][]{
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {1,1,1,1,1,1,1,0,1,1,1,1,1,1,1},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                        });
                    }
                    else if(type.equals(ChessPieceType.PAWN)){
                        chessPiece.setMoveset(new int[][]{
                                {2,1,2},
                                {2,0,2},
                        });
                    }

                }
            }
        }

    }

    //method used to update logical game map with chesspieces new position
    public void update(int y_or, int x_or, int y_tr, int x_tr){ //called when chesspiece is moved
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        BoardPanel.SquarePanel[][] squarePanel = view.getBoardPanel().getSquares();

        ChessPieceAbstract tempChesspiece = null;
        tempChesspiece = gamemap[y_or][x_or];

            //inverseMapArray();
            squarePanel[y_tr][x_tr].revalidate(); // new
            squarePanel[y_tr][x_tr].repaint(); // new

            gamemap[y_or][x_or] = null;
            gamemap[y_tr][x_tr] = tempChesspiece;

            //inverseMapArray();

            drawMap();


    }

    //todo block highlighting if chesspieces in the way
    public void highlightMovementPattern(int srcY,int srcX){

        int YCord = srcY; //get y/x cordinate of piece
        int XCord = srcX;

        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        ChessPiece cp = (ChessPiece) gamemap[YCord][XCord]; //get piece at cordinate
        int[][] moveset = cp.getMoveset(); //get moveset of piece
        BoardPanel.SquarePanel[][] squarePanel = view.getBoardPanel().getSquares(); //get GUI panel

        int YOffset = 0; //initialize offsets
        int XOffset = 0;

        try {
            //get offsets from moveset
            for(int row = 0; row < moveset.length; row ++){
                for (int col = 0; col < moveset[row].length; col++){
                    if(moveset[row][col] == 0){ //find number 0 in moveset array to get offsets
                        YOffset = row;
                        XOffset = col;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

            //y/x = cordinate - offset.
            for(int y = (YCord - YOffset); y < (YCord-YOffset+moveset.length); y++){
                for(int x = (XCord - XOffset); x < (XCord-XOffset+moveset.length);x++){

                    try{
                        if(moveset[y-(YCord-YOffset)][x-(XCord-XOffset)] == 1 && gamemap[y][x] == null){ //if there is a 1 in moveset then highlight tile
                            squarePanel[y][x].toggleHighlight();
                        }
                        else if(moveset[y-(YCord-YOffset)][x-(XCord-XOffset)] == 1 && ((ChessPiece) gamemap[YCord][XCord]).getColor() != ((ChessPiece)gamemap[y][x]).getColor()){ //highlights position if chesspiece is not of same color
                            squarePanel[y][x].toggleHighlight();
                        }
                    } catch (Exception e) {
                        continue;
                    }

                }
            }
    }

    //todo things to check. clicks on same spot(done), moves within moveset(done), does tile contain friendly or enemy(done), is checkmate?, is castling
    public boolean moveValid(int sourceRow, int sourceCol, int targetRow, int targetCol){
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        ChessPiece cp = (ChessPiece) gamemap[sourceRow][sourceCol];
        int[][] moveset = cp.getMoveset();

        boolean samespot = false;
        boolean withinMoveset = false;
        boolean friendlyObstruction = false;

        samespot = samecpspot(sourceRow,sourceCol,targetRow,targetCol);
        withinMoveset = moveWithinCPMoveset(sourceRow,sourceCol,targetRow,targetCol,moveset);
        friendlyObstruction = friendlyCPObstruction(targetRow,targetCol,gamemap,cp);

        if(!samespot && !withinMoveset && !friendlyObstruction){ //if errorchecks are negative make move valid
            return true; //move is valid
        }
        else{
            return false;
        }

    }

    public boolean samecpspot(int sR, int sC, int tR, int tC){
        if(sR == tR && sC == tC){
            //System.out.println("same position!"); debugging
            return true;
        }
        else{
            return false;
        }
    }

    public boolean moveWithinCPMoveset(int sR, int sC, int tR, int tC, int[][] moveset){

        int movesetOffsetY = -1;
        int movesetOffsetX = -1;

        try {
            //get offsets from moveset
            for(int row = 0; row < moveset.length; row ++){
                for (int col = 0; col < moveset[row].length; col++){
                    if(moveset[row][col] == 0){ //find number 0 in moveset array to get offsets
                        movesetOffsetY = row;
                        movesetOffsetX = col;
                        break;
                    }
                }
            }

        } catch (Exception e) {

        }

        int yTrOffset = sR-tR;
        int xTrOffset = sC-tC;

        try{
            if(moveset[movesetOffsetY-yTrOffset][movesetOffsetX-xTrOffset] != 1){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {

        }
        return true;
    }

    //checks if obstruction is of same chesspiece color
    public boolean friendlyCPObstruction(int targetRow, int targetCol, ChessPieceAbstract[][] gamemap,ChessPiece cp){
        if(gamemap[targetRow][targetCol] != null){
            ChessPieceColor obstructionColor = ((ChessPiece)gamemap[targetRow][targetCol]).getColor();
            if(cp.getColor() == obstructionColor){
                return true;
            }
        }
        return false;
    }

    //notes:
    //todo method used to switch movement pattern on pawns after first move, cleans board slate of chesspieces for redraw
    // .
    // Castling requirements:
    // Your king has not moved in the game yet.
    // Your king is not in check.
    // The king does not castle through a square which is controlled by an opponent’s piece.
    // The king is not in check after castling.
    // The rook has not been moved in the game yet.

}
