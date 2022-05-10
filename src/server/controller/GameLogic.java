package server.controller;

import client.board.BoardView;
import client.gameview.GameView;
import client.gameview.BoardPanel;
import client.gameview.GameView;
import server.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

//todo switch moveset when array inversed,

//todo only initialize playable side with moves

//todo complete all chess peices to fully functioning.
// currently complete: knight,king
// left to fix: pawn(doing), rook, bishop, queen.

//todo check for check mate every draw.

//todo remove left click toggle highlight

public class GameLogic {

    private GameView view;
    private GameModel model;


    public GameLogic(){

        this.view = new GameView(this);
        this.model = new GameModel();

        //all game code runs here
        model.setMap(new GameMap(8));

        initializeMap();
        //debugChesspieces();
        //inverseMapArray();

        drawMap();
    }

    public void initializeMap() {
        int mapDim = model.getMap().getMapDimension();
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        HashMap<Integer, ChessPieceAbstract> chesspieces = model.getChesspieces();
        //load black chesspieces

        gamemap[0][0] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.ROOK, "BR");// 8
        gamemap[0][1] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.KNIGHT, "BN");
        gamemap[0][2] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.BISHOP, "BB");
        gamemap[0][3] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.QUEEN, "BQ");
        gamemap[0][4] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.KING, "BK");
        gamemap[0][5] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.BISHOP, "BB");
        gamemap[0][6] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.KNIGHT, "BN");
        gamemap[0][7] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.ROOK, "BR");

        //loads black Pawns
        int row = 1;
        for(int col = 0; col < mapDim; col++){
            gamemap[row][col] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.PAWN, "BP");
        }

        //load white Pawns
        row = 6;
        for(int col = 0; col < mapDim; col++){
            gamemap[row][col] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.PAWN, "WP");
        }

        //load white chesspieces
        gamemap[7][0] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.ROOK, "WR");
        gamemap[7][1] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.KNIGHT, "WN");
        gamemap[7][2] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.BISHOP, "WB");
        gamemap[7][3] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.QUEEN, "WQ");
        gamemap[7][4] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.KING, "WK");
        gamemap[7][5] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.BISHOP, "WB");
        gamemap[7][6] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.KNIGHT, "WN");
        gamemap[7][7] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.ROOK, "WR");

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
                                    {3,1,3},
                                    {2,0,2},
                            });
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

    //draws up Gui Map from map array
    public void drawMap(){
        int mapDim = model.getMap().getMapDimension();
        HashMap<String, JLabel> notationLbl = view.getBoardPanel().getNotationToJLMap();
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        BoardPanel.SquarePanel[][] squarePanel = view.getBoardPanel().getSquares();
        GameMap map = model.getMap();

        cleanBoard();

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

    /*
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

    }*/

    //method used to update logical game map with chesspieces new position
    public void update(int y_or, int x_or, int y_tr, int x_tr){ //called when chesspiece is moved
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        BoardPanel.SquarePanel[][] squarePanel = view.getBoardPanel().getSquares();

        ChessPiece tempChesspiece = null;
        tempChesspiece = ((ChessPiece) gamemap[y_or][x_or]);

            squarePanel[y_tr][x_tr].revalidate(); // new
            squarePanel[y_tr][x_tr].repaint(); // new

            gamemap[y_tr][x_tr] = tempChesspiece;
            gamemap[y_or][x_or] = null;

            if( tempChesspiece.getChessPieceType() == ChessPieceType.PAWN && tempChesspiece.getMoved() == 0 ){
                tempChesspiece.setMoved(1);
                tempChesspiece.setMoveset(new int[][]{
                        {3,1,3},
                        {2,0,2},
                        {2,2,2}
                });
            }

            inverseMapArray();
            drawMap();
    }

    //todo block highlighting if chesspieces in the way
    //method used to show highlighted movement pattern in GUI
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

                        if(moveset[y-(YCord-YOffset)][x-(XCord-XOffset)] == 3 && cp.getChessPieceType() == ChessPieceType.PAWN && ((ChessPiece)gamemap[y][x]).getColor() != cp.getColor()){
                            squarePanel[y][x].toggleHighlight();
                        }

                        if(moveset[y-(YCord-YOffset)][x-(XCord-XOffset)] == 1 && cp.getColor() != ((ChessPiece)gamemap[y][x]).getColor() && cp.getChessPieceType() != ChessPieceType.PAWN){ //highlights position if chesspiece is not of same color
                            squarePanel[y][x].toggleHighlight();
                        }
                    } catch (Exception e) {
                        continue;
                    }

                }
            }
    }

    //todo things to check. clicks on same spot(done), moves within moveset(done), does tile contain friendly or enemy(done), is checkmate?, is castling, cant move on enemy king only put in chess/checkmate
    //check if move input by user is a valid move in chess
    public boolean moveValid(int sourceRow, int sourceCol, int targetRow, int targetCol){
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        ChessPiece cp = (ChessPiece) gamemap[sourceRow][sourceCol];
        int[][] moveset = cp.getMoveset();
        ChessPieceType chessPieceType = cp.getChessPieceType();

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

        int yTrOffset = (sourceRow-targetRow);
        int xTrOffset = (sourceCol-targetCol);

        boolean samespot = false;
        boolean withinMoveset = false;
        boolean friendlyObstruction = false;
        boolean pawnobstruct = false;
        boolean pawnattacks = false;

        samespot = samecpspot(sourceRow,sourceCol,targetRow,targetCol);
        withinMoveset = moveWithinCPMoveset(sourceRow,sourceCol,targetRow,targetCol,movesetOffsetY,movesetOffsetX,yTrOffset,xTrOffset,moveset,cp,gamemap);
        friendlyObstruction = friendlyCPObstruction(targetRow,targetCol,gamemap,cp);
        pawnattacks = pawnAttack(targetRow,targetCol,movesetOffsetY,movesetOffsetX,yTrOffset,xTrOffset,moveset,cp,gamemap);
        pawnobstruct = pawnObstruct(targetRow,targetCol,gamemap,cp);

        System.out.println("samespot: " + samespot);
        System.out.println("witinmoveset: " + withinMoveset);
        System.out.println("friendlyObstruction: " + friendlyObstruction);
        System.out.println("pawnObstruction: " + pawnobstruct);
        System.out.println("PawnAttack: " + pawnattacks);

        if( (!samespot && !withinMoveset && !friendlyObstruction) && !pawnobstruct || (pawnattacks) ){//if errorchecks are negative make move valid
            return true; //move is valid
        }
        else{
            return false;
        }

    }

    //did user click the same spot
    public boolean samecpspot(int sR, int sC, int tR, int tC){
        if(sR == tR && sC == tC){
            return true;
        }
        else{
            return false;
        }
    }

    //does peice move within moveset
    public boolean moveWithinCPMoveset(int sR, int sC, int tR, int tC, int movesetOffsetY, int movesetOffsetX, int yTrOffset, int xTrOffset, int[][] moveset, ChessPiece cp,ChessPieceAbstract[][] gamemap){
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

    //is pawn obstruct by enemy
    public boolean pawnObstruct(int targetRow, int targetCol, ChessPieceAbstract[][] gamemap, ChessPiece cp){
        if(cp.getChessPieceType() == ChessPieceType.PAWN && gamemap[targetRow][targetCol] != null){
            if( ((ChessPiece)gamemap[targetRow][targetCol]).getColor() != cp.getColor() ){
                return true;
            }
        }
        return false;
    }

    //checks if pawn can attack with attack pattern
    public boolean pawnAttack(int targetRow, int targetCol, int movesetOffsetY, int movesetOffsetX, int yTrOffset, int xTrOffset, int[][] moveset, ChessPiece cp, ChessPieceAbstract[][] gamemap){
        if(cp.getChessPieceType() == ChessPieceType.PAWN && gamemap[targetRow][targetCol] != null && ((ChessPiece)gamemap[targetRow][targetCol]).getColor() != cp.getColor() && moveset[movesetOffsetY-yTrOffset][movesetOffsetX-xTrOffset] == 3){
            return true;
        }
        else{
            return false;
        }
    }

    //cleans board of chesspiece sprites
    public void cleanBoard(){
        BoardPanel.SquarePanel[][] squarePanel = view.getBoardPanel().getSquares();
        for(int row = 0; row < squarePanel.length; row++){
            for(int col = 0; col < squarePanel[row].length; col++){
                squarePanel[row][col].removePiece();
            }
        }
    }

    //notes:
    //todo method used to switch movement pattern on pawns after first move, cleans board slate of chesspieces for redraw(done)
    // .
    // Kolla på hur logik ska skjötas på server medans clienter skickar moves.
    // Castling requirements:
    // Your king has not moved in the game yet.
    // Your king is not in check.
    // The king does not castle through a square which is controlled by an opponent’s piece.
    // The king is not in check after castling.
    // The rook has not been moved in the game yet.

}
