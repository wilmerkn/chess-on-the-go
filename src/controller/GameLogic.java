package controller;

import model.*;
import view.GameView;

import java.sql.Time;
import java.util.Map;

public class GameLogic {

    private GameView view;
    private GameModel model;

    public GameLogic() {
        this.model = new GameModel();
        this.view = new GameView(this);
    }

    public void loadGameMap(){
        model.initializeMap();
    }

    public void Update(){
        //update GUI elements (update positions of sprites then revaluate and repaint)
    }

}
