package server.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

//task inherited by a thread to start a timer
public class GameTimerTask implements Runnable, Serializable {

    private boolean stopFlag; //stop flag for while loop
    //private GameView view; //access to view to change text in components

    private int hours;
    private int minutes;
    private int seconds;
    private String hour;
    private String minute;
    private String second;
    private static final int N = 00;
    private String currentTime;

    private int timeControl;

    public GameTimerTask(int timeControl){
        this.minutes = timeControl;
        this.currentTime = "0" + timeControl + ":00";
        //this.view = null;
        stopFlag = false;
    }

    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    @Override
    public void run() {

        while(!stopFlag){
            try{
                Thread.sleep(1000);
                NumberFormat formatter = new DecimalFormat("00");
                if (seconds == N) {
                    seconds = 59; //increment minutes and reset if second = 60
                    minutes--;
                }
                //hour = formatter.format(hours);
                minute = formatter.format(minutes); //formats time to 00 unit
                second = formatter.format(seconds);
                String clockTime = String.valueOf(minute + ":" + second);
                currentTime = clockTime;
                if(currentTime.equals("00:00")) {
                    stopFlag = true;
                }
                seconds--;
                 //wait 1 sec

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getCurrentTime() {
        return currentTime;
    }
}
