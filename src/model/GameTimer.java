package model;

public class GameTimer {

    private GameTimerTask threadtask;

    public GameTimer(){

    }

    public void turnOn(){
        new Thread(this.threadtask = new GameTimerTask()).start();
    }

    public void turnOff(){
        threadtask.setStopFlag(true);
    }

}
