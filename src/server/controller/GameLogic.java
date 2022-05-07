package server.controller;

import client.board.BoardView;
import server.model.*;
import client.GameView;

import javax.swing.*;
import java.util.HashMap;

//functionality: Map generation, ingame timer (count up).

//todo way to get every moveset from chesspieces for valid move checks

public class GameLogic {

    private GameView view;
    private GameModel model;

    public GameLogic() {
        this.model = new GameModel(this);
        this.view = new GameView(this);

        //all game code runs here
        model.setMap(new GameMap(8));
        //initializeMap();
        debugChesspieces();
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
                        chessPiece.setMoveset(new int[][]{{1,1,1},
                                                          {1,0,1},
                                                          {1,1,1}});
                    }
                    if(type.equals(ChessPieceType.QUEEN)){
                        chessPiece.setMoveset(new int[][]{});
                    }
                    if(type.equals(ChessPieceType.KNIGHT)){
                        chessPiece.setMoveset(new int[][]{});
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


    //draws player-one game map
    public void drawMap(){
        int mapDim = model.getMap().getMapDimension();
        HashMap<String, JLabel> notationLbl = model.getBoardView().getNotationToJLMap();
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        BoardView.SquarePanel[][] squarePanel = model.getBoardView().getBoardPanel().getSquares();
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

        gamemap[4][4] = chesspieces.get(9);
        //9 is knight

        for(int r = 0; r < gamemap.length; r++){
            for(int c = 0; c < gamemap[r].length;c++){
                if(gamemap[r][c] != null){
                    ChessPiece chessPiece = (ChessPiece) gamemap[r][c];
                    ChessPieceType type = chessPiece.getChessPieceType();

                    if(type.equals(ChessPieceType.KING)){
                        chessPiece.setMoveset(new int[][]{{1,1,1},
                                                          {1,0,1},
                                                          {1,1,1}});
                    }
                    if(type.equals(ChessPieceType.QUEEN)){
                        chessPiece.setMoveset(new int[][]{{1, 2, 2, 2, 1, 2, 2, 2, 1},
                                                          {2, 1, 2, 2, 1, 2, 2, 1, 2},
                                                          {2, 2, 1, 2, 1, 2, 1, 2, 2},
                                                          {2, 2, 2, 1, 1, 1, 2, 2, 2},
                                                          {1, 1, 1, 1, 0, 1, 1, 1, 1},
                                                          {2, 2, 2, 1, 1, 1, 2, 2, 2},
                                                          {2, 2, 1, 2, 1, 2, 1, 2, 2},
                                                          {2, 1, 2, 2, 1, 2, 2, 1, 2},
                                                          {1, 2, 2, 2, 1, 2, 2, 2, 1}});
                    }
                    if(type.equals(ChessPieceType.KNIGHT)){
                        chessPiece.setMoveset(new int[][]{{2,1,2,1,2},
                                                          {1,2,2,2,1},
                                                          {2,2,0,2,2},
                                                          {1,2,2,2,1},
                                                          {2,1,2,1,2}});
                    }

                }
            }
        }

    }

    //method used to update logical game map with chesspieces new position
    public void update(int y_or, int x_or, int y_tr, int x_tr){ //called when chesspiece is moved
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        BoardView.SquarePanel[][] squarePanel = model.getBoardView().getBoardPanel().getSquares();
        boolean validMove = false;

        ChessPieceAbstract tempChesspiece = null;
        tempChesspiece = gamemap[y_or][x_or];

        //call movecheck here. if all true then make move
        validMove = moveCheck(y_or,y_tr,x_or,x_tr,gamemap,tempChesspiece);

        //if(validMove){
            System.out.println("valid!");
            //inverseMapArray();
            squarePanel[y_tr][x_tr].revalidate(); // new
            squarePanel[y_tr][x_tr].repaint(); // new

            gamemap[y_or][x_or] = null;
            gamemap[y_tr][x_tr] = tempChesspiece;

            //inverseMapArray();

            drawMap();
        //}

    }

    public void highlightMovementPattern(int srcY,int srcX){
        int YCord = srcY;
        int XCord = srcX;

        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        ChessPiece cp = (ChessPiece) gamemap[YCord][XCord];
        int[][] moveset = cp.getMoveset();
        BoardView.SquarePanel[][] squarePanel = model.getBoardView().getBoardPanel().getSquares();

        int YOffset = 0;
        int XOffset = 0;

        try {
            //get offsets from moveset
            for(int row = 0; row < moveset.length; row ++){
                for (int col = 0; col < moveset[row].length; col++){

                    if(moveset[row][col] == 0){
                        YOffset = row;
                        XOffset = col;
                        break;
                    }
                }
            }
            System.out.println((YCord-YOffset) + " " + (XCord-XOffset));

            System.out.println(moveset.length);

        } catch (Exception e) {
            e.printStackTrace();
        }

            for(int y = (YCord - YOffset); y < (YCord-YOffset+moveset.length); y++){
                for(int x = (XCord - XOffset); x < (XCord-XOffset+moveset.length);x++){

                    if(moveset[y-(YCord-YOffset)][x-(XCord-YOffset)] == 1){
                        try{
                            squarePanel[y][x].toggleHighlight();
                        } catch (Exception e) {
                            continue;
                        }
                    }


                }
            }



    }

    public boolean moveCheck(int y_or, int y_tr, int x_or, int x_tr,ChessPieceAbstract[][] gamemap,ChessPieceAbstract chesspiece){
        //call all check methods in here

        boolean samepos = checkSamePos(y_or,y_tr,x_or,x_tr);
        boolean withinMoveset = validFromMoveset();
        //check all booleans are negative
        if(!samepos){
            return false;
        }
        return true;
    }



    public boolean validFromMoveset(){



        return true;
    }


    //debug further
    public boolean checkSamePos(int y_or, int y_tr, int x_or, int x_tr){

        if(y_or == y_tr && x_or == x_tr){
            return true;
        }
        return false;
    }

    /*
    public boolean validMoveSet(int y_or, int y_tr, int x_or, int x_tr,ChessPieceAbstract[][] gamemap,ChessPieceAbstract chesspiece){
        ChessPiece cp = (ChessPiece) chesspiece;
        int[][] moveset = cp.getMoveset();

        for(int row = 0; row < moveset.length; row ++){
            for(int col = 0; col < moveset[row].length; col++){

                if(moveset[row][col] == 1){

                }

            }
        }

        return true;
    }*/

    public void checkObstruction(){

    }

}
