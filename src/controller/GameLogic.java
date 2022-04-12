package controller;

import model.GameModel;
import view.GameView;

import java.sql.Time;

public class GameLogic {

    private GameView view;
    private GameModel model;

    public GameLogic() {
        this.model = new GameModel();
        this.view = new GameView(this);
    }

    public void generateMap(){
        //generates Logic map
    }

    public void generateLogicalMap(){
        //generates String Based map
    }

    public void Update(){
        //update GUI elements
    }

}
