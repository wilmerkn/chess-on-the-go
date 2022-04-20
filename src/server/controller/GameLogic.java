package server.controller;

import server.model.*;
import client.GameView;

import java.util.HashMap;

//functionality: Map generation, ingame timer (count up).

public class GameLogic {

    private GameView view;
    private GameModel model;

    public GameLogic() {
        this.model = new GameModel();
        this.view = new GameView(this);

        //all game code runs here

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

    public void drawPlayer2Map(){
        //draw player 1 map but upside down
    }

    public void Update(){
        //update GUI elements (update positions of sprites then revaluate and repaint)
        
        
    }

}
