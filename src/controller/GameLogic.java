package controller;

import model.*;
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



    public void Update(){
        //update GUI elements (update positions of sprites then revaluate and repaint)
    }

}
