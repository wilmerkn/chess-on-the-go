package server.controller;

import client.board.BoardView;
import server.model.*;
import client.GameView;

import javax.swing.*;
import java.util.HashMap;

//functionality: Map generation, ingame timer (count up).

public class GameLogic {

    private GameView view;
    private GameModel model;

    public GameLogic() {
        this.model = new GameModel(this);
        this.view = new GameView(this);

        //all game code runs here
        model.setMap(new GameMap(8));
        initializeMap();
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
    }

    //draws player two map upside down
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
    }

    //todo not sure how to fix

    public void inverseMapArray(){
        int mapDim = model.getMap().getMapDimension();
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();


        for(int row = 0; row < mapDim/2; row++){
            for(int col = 0; col < mapDim; col++){
                if(gamemap[row][col] != null){

                    ChessPiece temp = (ChessPiece) gamemap[row][col];
                    gamemap[row][col] = gamemap[(mapDim-1)-row][(mapDim-1)-col];
                    gamemap[(mapDim-1)-row][(mapDim-1)-col] = temp;

                }
            }
        }
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

    //method used to update logical game map with chesspieces new position
    public void update(int y_or, int x_or, int y_tr, int x_tr){ //called when chesspiece is moved
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        GameMap map = model.getMap();

        ChessPieceAbstract tempChesspiece = null;
        tempChesspiece = gamemap[y_or][x_or];

        gamemap[y_or][x_or] = null;
        gamemap[y_tr][x_tr] = tempChesspiece;
        map.displayMap();
    }

}
