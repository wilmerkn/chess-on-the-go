package controller;

import model.GameModel;
import view.GameView;

public class GameLogic {

    private GameView view;
    private GameModel model;

    public GameLogic(){
        this.model = new GameModel();
        this.view = new GameView(this);
    }

    public void generateMap(){

    }

}
