package server.model;

import java.io.Serializable;

//work in progress timer class to measure the time of a occuring game of chess
public class GameTimer implements Serializable {

    private final GameTimerTask threadtask;

    private final int time;

    public GameTimer(int time){
        this.time = time;
        threadtask = new GameTimerTask(time);
    }

    public void turnOn(){
        new Thread(this.threadtask).start();
    } //starts timer

    public void turnOff(){
        threadtask.setStopFlag(true);
    } //stops timer

    public String getTime() {
        return threadtask.getCurrentTime();
    }
}
