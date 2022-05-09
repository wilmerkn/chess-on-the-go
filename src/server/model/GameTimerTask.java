package server.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

//task inherited by a thread to start a timer
public class GameTimerTask implements Runnable{

    private boolean stopFlag; //stop flag for while loop
    //private GameView view; //access to view to change text in components

    private int hours;
    private int minutes;
    private int seconds;
    private String hour;
    private String minute;
    private String second;
    private static final int N = 60;

    public GameTimerTask(){
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

                NumberFormat formatter = new DecimalFormat("00");
                if (seconds == N) {
                    seconds = 00; //increment minutes and reset if second = 60
                    minutes++;
                }

                if (minutes == N) {
                    minutes = 00; //increment hours and reset if minutes = 60
                    hours++;
                }
                hour = formatter.format(hours);
                minute = formatter.format(minutes); //formats time to 00 unit
                second = formatter.format(seconds);
                String clockTime = String.valueOf(hour + ":" + minute + ":" + second);
                System.out.println(clockTime); //debugging, remove when set in GUI
                seconds++;

                Thread.sleep(1000); //wait 1 sec

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
