package client;

import server.controller.GameLogic;

public class GameView {
    private GameLogic gameLogic;

    public GameView(GameLogic gameLogic){
        this.gameLogic = gameLogic;
    }

    public void updateGUI(){
        //gameLogic.update();
    }
}
